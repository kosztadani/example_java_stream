package dev.kosztadani.examples.stream.merge;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
class IndexedComparator implements Comparator<BufferedIterator> {

    private final Comparator primaryComparator;

    private final Map<Iterator, Integer> secondaryOrder;

    IndexedComparator(Comparator primaryComparator, Map<Iterator, Integer> secondaryOrder) {
        this.primaryComparator = primaryComparator;
        this.secondaryOrder = secondaryOrder;
    }

    @Override
    public int compare(BufferedIterator i1, BufferedIterator i2) {
        int primaryComparison = primaryComparator.compare(i1.peek(), i2.peek());
        if (primaryComparison != 0) {
            return primaryComparison;
        } else {
            return secondaryOrder.get(i1).compareTo(secondaryOrder.get(i2));
        }
    }
}
