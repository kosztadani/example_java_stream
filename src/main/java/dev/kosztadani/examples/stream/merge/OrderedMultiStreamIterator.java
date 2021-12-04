package dev.kosztadani.examples.stream.merge;

import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings({"rawtypes", "unchecked"})
public class OrderedMultiStreamIterator implements Iterator, AutoCloseable {

    private final Map<Iterator, Stream> map = new IdentityHashMap<>();

    private final Map<Iterator, Integer> originalOrder = new IdentityHashMap<>();

    private final List<Iterator> iterators = new ArrayList<>();

    private final Comparator comparator;

    OrderedMultiStreamIterator(Comparator comparator, Stream... streams) {
        this.comparator = new IndexedComparator(comparator, originalOrder);
        addStreams(streams);
        sort();
    }

    private void addStreams(Stream... streams) {
        for (int i = 0; i < streams.length; i++) {
            addStream(i, streams[i]);
        }
    }

    private void addStream(int index, Stream stream) {
        Iterator streamIterator = stream.iterator();
        if (streamIterator.hasNext()) {
            Iterator bufferedIterator = new BufferedIterator(streamIterator);
            map.put(bufferedIterator, stream);
            iterators.add(bufferedIterator);
            originalOrder.put(bufferedIterator, index);
        } else {
            stream.close();
        }
    }

    @Override
    public boolean hasNext() {
        return iterators.size() > 0 && iterators.get(0).hasNext();
    }

    @Override
    public Object next() {
        Iterator iterator = iterators.get(0);
        Object object = iterator.next();
        if (!iterator.hasNext()) {
            remove(iterator);
        }
        sort();
        return object;
    }

    private void sort() {
        iterators.sort(comparator);
    }

    private synchronized void remove(Iterator iterator) {
        Stream stream = map.get(iterator);
        map.remove(iterator);
        iterators.remove(iterator);
        originalOrder.remove(iterator);
        stream.close();
    }

    @Override
    public synchronized void close() {
        for (Stream stream : map.values()) {
            stream.close();
        }
    }
}
