package dev.kosztadani.examples.stream.merge;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StreamMergeTest {
    @Test
    void testNoArguments() {
        Stream<Integer> stream = Stream.empty();

        Stream<Integer> merged = StreamMerge.mergeStreams(stream);

        assertEquals(List.of(), merged.collect(Collectors.toList()));
    }

    @Test
    void testSingleStream() {
        Stream<Integer> stream = Stream.of(1, 2, 3);

        Stream<Integer> merged = StreamMerge.mergeStreams(stream);

        assertEquals(List.of(1, 2, 3), merged.collect(Collectors.toList()));
    }

    @Test
    void testSameLength() {
        Stream<Integer> first = Stream.of(1, 3, 5);
        Stream<Integer> second = Stream.of(2, 4, 6);

        Stream<Integer> merged = StreamMerge.mergeStreams(first, second);

        assertEquals(List.of(1, 2, 3, 4, 5, 6), merged.collect(Collectors.toList()));
    }

    @Test
    void testDifferentLength() {
        Stream<Integer> first = Stream.of(1, 3, 5);
        Stream<Integer> second = Stream.of(2, 4);

        Stream<Integer> merged = StreamMerge.mergeStreams(first, second);

        assertEquals(List.of(1, 2, 3, 4, 5), merged.collect(Collectors.toList()));
    }

    @Test
    void testRepeatingElement() {
        Stream<Integer> first = Stream.of(1, 1, 3, 5);
        Stream<Integer> second = Stream.of(1, 4);

        Stream<Integer> merged = StreamMerge.mergeStreams(first, second);

        assertEquals(List.of(1, 1, 1, 3, 4, 5), merged.collect(Collectors.toList()));

    }

    @Test
    void testFirstEmpty() {
        Stream<Integer> first = Stream.empty();
        Stream<Integer> second = Stream.of(1, 2, 3);

        Stream<Integer> merged = StreamMerge.mergeStreams(first, second);

        assertEquals(List.of(1, 2, 3), merged.collect(Collectors.toList()));

    }
    @Test
    void testSecondEmpty() {
        Stream<Integer> first = Stream.of(1, 2, 3);
        Stream<Integer> second = Stream.empty();

        Stream<Integer> merged = StreamMerge.mergeStreams(first, second);

        assertEquals(List.of(1, 2, 3), merged.collect(Collectors.toList()));

    }

    @Test
    void testMultipleStreams() {
        Stream<Integer> first = Stream.of(1, 4, 7);
        Stream<Integer> second = Stream.of(2, 5, 8);
        Stream<Integer> third = Stream.of(3, 6, 9);

        Stream<Integer> merged = StreamMerge.mergeStreams(first, second, third);

        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), merged.collect(Collectors.toList()));
    }

    @Test
    void testReverseSortCast() {
        Stream<Integer> first = Stream.of(5, 3, 1);
        Stream<Integer> second = Stream.of(6, 4, 2);

        @SuppressWarnings({"rawtypes", "unchecked"})
        Stream<Integer> merged = StreamMerge.mergeStreams(
            (Comparator) Comparator.naturalOrder().reversed(),
            first,
            second
        );

        assertEquals(List.of(6, 5, 4, 3, 2, 1), merged.collect(Collectors.toList()));
    }

    @Test
    void testReverseSortCastUnchecked() {
        Stream<Integer> first = Stream.of(5, 3, 1);
        Stream<Integer> second = Stream.of(6, 4, 2);

        @SuppressWarnings("unchecked")
        Stream<Integer> merged = StreamMerge.mergeStreamsUnchecked(
            Comparator.naturalOrder().reversed(),
            first,
            second
        );

        assertEquals(List.of(6, 5, 4, 3, 2, 1), merged.collect(Collectors.toList()));
    }

    @Test
    void testDifferentTypes() {
        Stream<Integer> integers = Stream.of(1, 3, 5);
        Stream<Float> floats = Stream.of(2.0F, 4.0F);

        @SuppressWarnings({"rawtypes", "unchecked"})
        Stream<Number> merged = StreamMerge.mergeStreams(
            Comparator.comparing(Number::doubleValue),
            (Stream) integers,
            (Stream) floats);

        assertEquals(List.of(1, 2.0F, 3, 4.0F, 5), merged.collect(Collectors.toList()));
    }

    @Test
    void testDifferentTypesUnchecked() {
        Stream<Integer> integers = Stream.of(1, 3, 5);
        Stream<Float> floats = Stream.of(2.0F, 4.0F);

        @SuppressWarnings("unchecked")
        Stream<Number> merged = StreamMerge.mergeStreamsUnchecked(
            Comparator.comparing(Number::doubleValue),
            integers,
            floats
        );

        assertEquals(List.of(1, 2.0F, 3, 4.0F, 5), merged.collect(Collectors.toList()));
    }
}
