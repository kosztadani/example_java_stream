package dev.kosztadani.examples.stream.merge;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility class for merging streams together.
 */
public class StreamMerge {

    private StreamMerge() {
    }

    /**
     * Merges streams together, using the natural ordering of the elements.
     *
     * <p>
     * This method assumes that each individual stream is already ordered.
     * If this does not hold true, the behavior is undefined.
     *
     * <p>
     * The streams passed to this method must not contain null elements.
     *
     * @param <T>     The type of elements in the streams.
     * @param streams The streams to be merged.
     * @return A merged stream.
     */
    @SafeVarargs
    public static <T extends Comparable<? super T>> Stream<T> mergeStreams(Stream<? extends T>... streams) {
        return mergeStreams(Comparator.naturalOrder(), streams);
    }

    /**
     * Merges streams together, using supplied Comparator.
     *
     * <p>
     * This method assumes that each individual stream is already ordered
     * according to the comparator. If this does not hold true, the behavior is
     * undefined.
     *
     * <p>
     * The streams passed to this method may contain null elements if the
     * comparator handles them correctly.
     *
     * @param <T>        The type of elements in the streams.
     * @param comparator The comparator that defines to order of elements.
     * @param streams    The streams to be merged.
     * @return A merged stream.
     */
    @SafeVarargs
    public static <T> Stream<T> mergeStreams(Comparator<? super T> comparator, Stream<? extends T>... streams) {
        List<Stream<? extends T>> streamList = Arrays.asList(streams);
        OrderedMultiStreamSpliteratorFactory<T> factory = new OrderedMultiStreamSpliteratorFactory<>(comparator, streamList);
        Stream<T> mergedStream = StreamSupport.stream(factory, 0, false);
        return mergedStream.onClose(factory::close);
    }
}
