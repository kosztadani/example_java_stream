package dev.kosztadani.examples.stream.merge;

import java.util.*;
import java.util.stream.Stream;

class OrderedMultiStreamIterator<T> implements Iterator<T>, AutoCloseable {

    private final Map<Iterator<T>, Stream<? extends T>> map = new IdentityHashMap<>();

    private final Map<Iterator<?>, Integer> originalOrder = new IdentityHashMap<>();

    private final List<BufferedIterator<T>> iterators = new ArrayList<>();

    private final Comparator<BufferedIterator<T>> comparator;

    private final List<Exception> closeExceptions = new ArrayList<>();

    OrderedMultiStreamIterator(Comparator<? super T> comparator, List<Stream<? extends T>> streams) {
        this.comparator = new IndexedComparator<>(comparator, originalOrder);
        addStreams(streams);
        sort();
    }

    private void addStreams(List<Stream<? extends T>> streams) {
        for (int i = 0; i < streams.size(); i++) {
            addStream(i, streams.get(i));
        }
    }

    private void addStream(int index, Stream<? extends T> stream) {
        Iterator<? extends T> streamIterator = stream.iterator();
        if (streamIterator.hasNext()) {
            BufferedIterator<T> bufferedIterator = new BufferedIterator<>(streamIterator);
            map.put(bufferedIterator, stream);
            iterators.add(bufferedIterator);
            originalOrder.put(bufferedIterator, index);
        } else {
            closeStream(stream);
        }
    }

    @Override
    public boolean hasNext() {
        return !iterators.isEmpty() && iterators.get(0).hasNext();
    }

    @Override
    public T next() {
        BufferedIterator<T> iterator = iterators.get(0);
        T object = iterator.next();
        if (!iterator.hasNext()) {
            remove(iterator);
        }
        sort();
        return object;
    }

    private void sort() {
        iterators.sort(comparator);
    }

    private synchronized void remove(BufferedIterator<T> iterator) {
        Stream<? extends T> stream = map.get(iterator);
        map.remove(iterator);
        iterators.remove(iterator);
        originalOrder.remove(iterator);
        closeStream(stream);
    }

    private void closeStream(Stream<?> stream) {
        try {
            stream.close();
        } catch (Exception e) {
            closeExceptions.add(new RuntimeException(e));
        }
    }

    @Override
    public synchronized void close() {
        for (Stream<?> stream : map.values()) {
            closeStream(stream);
        }
        if (!closeExceptions.isEmpty()) {
            throwCloseExceptions();
        }
    }

    private void throwCloseExceptions() {
        Exception first = closeExceptions.get(0);
        for (int i = 1; i < closeExceptions.size(); i++) {
            Exception next = closeExceptions.get(i);
            if (first != next) {
                first.addSuppressed(closeExceptions.get(i));
            }
        }
        if (first instanceof RuntimeException) {
            throw (RuntimeException) first;
        } else {
            throw new RuntimeException(first);
        }
    }
}
