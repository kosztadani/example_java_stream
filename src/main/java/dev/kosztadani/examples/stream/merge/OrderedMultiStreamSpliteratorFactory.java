package dev.kosztadani.examples.stream.merge;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("rawtypes")
class OrderedMultiStreamSpliteratorFactory implements Supplier<Spliterator>, AutoCloseable {

    private final Comparator comparator;

    private final Stream[] streams;

    private OrderedMultiStreamIterator iterator;

    OrderedMultiStreamSpliteratorFactory(Comparator comparator, Stream... streams) {
        this.comparator = comparator;
        this.streams = streams;
    }


    @Override
    synchronized public Spliterator get() {
        iterator = new OrderedMultiStreamIterator(comparator, streams);
        return Spliterators.spliteratorUnknownSize(iterator, 0);
    }

    @Override
    synchronized public void close() {
        if (iterator != null) {
            iterator.close();
        }
    }
}
