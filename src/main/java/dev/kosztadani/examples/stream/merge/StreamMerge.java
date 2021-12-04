package dev.kosztadani.examples.stream.merge;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility class for merging streams together.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class StreamMerge {

    private StreamMerge() {}

    /**
     * Merges streams together, using the natural ordering of the elements.
     *
     * <p>This method assumes that each individual stream is already ordered.
     * If this does not hold true, the behavior is undefined.</p>
     *
     * <p>The streams passed to this method must not contain null elements.</p>
     *
     * @param streams The streams to be merged
     * @param <T> The type of the stream elements.
     * @return A merged stream.
     */
    @SafeVarargs
    public static <T extends Comparable> Stream<T> mergeStreams(Stream<T>... streams) {
        return mergeStreamsUnchecked(streams);
    }

    /**
     * Merges streams together, using supplied Comparator.
     *
     * <p>This method assumes that each individual stream is already ordered
     * according to the comparator. If this does not hold true, the behavior is
     * undefined.</p>
     *
     * <p>The streams passed to this method may contain null elements if the
     * comparator handles them correctly.</p>
     *
     * @param comparator The comparator that defines to order of elements.
     * @param streams The streams to be merged.
     * @param <T> The type of the stream elements.
     * @return A merged stream.
     */
    @SafeVarargs
    public static <T> Stream<T> mergeStreams(Comparator<T> comparator, Stream<T>... streams) {
        return mergeStreamsUnchecked(comparator, streams);
    }

    /**
     * Merges streams together, using the natural ordering of the elements.
     *
     * <p>This method assumes that each individual stream is already ordered.
     * If this does not hold true, the behavior is undefined.</p>
     *
     * <p>The streams passed to this method must not contain null elements.</p>
     *
     * <p>This is a raw version of {@link #mergeStreams(Stream[])} and can be
     * used with Streams of different types of elements, provided that the
     * natural ordering between them is well-defined.</p>
     *
     * @param streams The streams to be merged.
     * @return A merged stream.
     */
    public static Stream mergeStreamsUnchecked(Stream... streams) {
        return mergeStreamsUnchecked(Comparator.naturalOrder(), streams);
    }

    /**
     * Merges streams together, using supplied Comparator.
     *
     * <p>This method assumes that each individual stream is already ordered
     * according to the comparator. If this does not hold true, the behavior is
     * undefined.</p>
     *
     * <p>The streams passed to this method may contain null elements if the
     * comparator handles them correctly.</p>
     *
     * <p>This is a raw version of {@link #mergeStreams(Comparator, Stream[])}
     * and can be  used with Streams of different types of elements, provided
     * that the comparator supports this.</p>
     *
     * @param comparator The comparator that defines to order of elements.
     * @param streams The streams to be merged.
     * @return A merged stream.
     */
    public static Stream mergeStreamsUnchecked(Comparator comparator, Stream... streams) {
        OrderedMultiStreamIterator iterator = new OrderedMultiStreamIterator(comparator, streams);
        Spliterator spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
        return (Stream) StreamSupport.stream(spliterator, false).onClose(iterator::close);
    }
}
