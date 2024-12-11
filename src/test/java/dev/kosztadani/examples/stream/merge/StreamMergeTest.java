package dev.kosztadani.examples.stream.merge;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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

        Stream<Integer> merged = StreamMerge.mergeStreams(
            Comparator.reverseOrder(),
            first,
            second
        );

        assertEquals(List.of(6, 5, 4, 3, 2, 1), merged.collect(Collectors.toList()));
    }

    @Test
    void testDifferentTypes() {
        Stream<Integer> integers = Stream.of(1, 3, 5);
        Stream<Float> floats = Stream.of(2.0F, 4.0F);

        Stream<Number> merged = StreamMerge.mergeStreams(
            Comparator.comparing(Number::doubleValue),
            integers,
            floats);

        assertEquals(List.of(1, 2.0F, 3, 4.0F, 5), merged.collect(Collectors.toList()));
    }

    @Test
    void testDifferentTypesUnchecked() {
        Stream<Integer> integers = Stream.of(1, 3, 5);
        Stream<Float> floats = Stream.of(2.0F, 4.0F);

        Stream<Number> merged = StreamMerge.mergeStreams(
            Comparator.comparing(Number::doubleValue),
            integers,
            floats
        );

        assertEquals(List.of(1, 2.0F, 3, 4.0F, 5), merged.collect(Collectors.toList()));
    }

    @Test
    void testWithComparatorThatOrdersNulls() {
        Stream<Integer> first = Stream.of(1, 3, 5);
        Stream<Integer> second = Stream.of(2, null, 6);

        Stream<Integer> merged = StreamMerge.mergeStreams(
            // special comparator that considers nulls as if it was a 4
            Comparator.comparing(num -> Optional.ofNullable(num).orElse(4)),
            first,
            second
        );

        List<Integer> list = merged.toList();
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
        assertNull(list.get(3));
        assertEquals(5, list.get(4));
        assertEquals(6, list.get(5));
    }

    @Test
    void testSecondaryOrderIsOrderOfMergedArguments() {
        IntegerWrapper one = new IntegerWrapper(1);
        IntegerWrapper firstThree = new IntegerWrapper(3);
        Stream<IntegerWrapper> first = Stream.of(one, firstThree);

        IntegerWrapper two = new IntegerWrapper(2);
        IntegerWrapper secondThree = new IntegerWrapper(3);
        Stream<IntegerWrapper> second = Stream.of(two, secondThree);

        List<IntegerWrapper> merged = StreamMerge.mergeStreams(first, second).toList();

        assertSame(one, merged.get(0));
        assertSame(two, merged.get(1));
        assertSame(firstThree, merged.get(2));
        assertSame(secondThree, merged.get(3));
    }

    @Test
    void testSecondaryOrderIsOrderOfMergedArgumentsWithReversedArguments() {
        IntegerWrapper one = new IntegerWrapper(1);
        IntegerWrapper firstThree = new IntegerWrapper(3);
        Stream<IntegerWrapper> first = Stream.of(one, firstThree);

        IntegerWrapper two = new IntegerWrapper(2);
        IntegerWrapper secondThree = new IntegerWrapper(3);
        Stream<IntegerWrapper> second = Stream.of(two, secondThree);

        List<IntegerWrapper> merged = StreamMerge.mergeStreams(second, first).toList();

        assertSame(one, merged.get(0));
        assertSame(two, merged.get(1));
        assertSame(secondThree, merged.get(2));
        assertSame(firstThree, merged.get(3));
    }


    @Test
    void testCloseTryWithResources() {
        AtomicBoolean streamClosed = new AtomicBoolean(false);
        Stream<Integer> integers = Stream.of(1, 3, 5).onClose(
            () -> streamClosed.set(true)
        );
        Stream<Integer> merged = StreamMerge.mergeStreams(integers);

        try (merged) {
            merged.forEach(i -> {
                // do nothing
            });
        }

        assertTrue(streamClosed.get());
    }

    @Test
    void testCloseWithoutTerminalOperation() {
        AtomicBoolean streamClosed = new AtomicBoolean(false);
        Stream<Integer> integers = Stream.of(1, 3, 5).onClose(
            () -> streamClosed.set(true)
        );
        Stream<Integer> merged = StreamMerge.mergeStreams(integers);

        merged.close();

        assertTrue(streamClosed.get());
    }

}
