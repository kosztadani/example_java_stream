package dev.kosztadani.examples.stream.merge;

import java.util.Comparator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;

class OrderedMultiStreamSpliteratorFactory<T> implements Supplier<Spliterator<T>>, AutoCloseable {

    private final Comparator<? super T> comparator;

    private final List<Stream<? extends T>> streams;

    private OrderedMultiStreamIterator<T> iterator;

    OrderedMultiStreamSpliteratorFactory(Comparator<? super T> comparator, List<Stream<? extends T>> streams) {
        this.comparator = comparator;
        this.streams = streams;
    }

    @Override
    synchronized public Spliterator<T> get() {
        iterator = new OrderedMultiStreamIterator<>(comparator, streams);
        return Spliterators.spliteratorUnknownSize(iterator, 0);
    }

    @Override
    synchronized public void close() {
        if (iterator == null) {
            for (Stream<?> stream : streams) {
                stream.close();
            }
        } else {
            iterator.close();
        }
    }
}
