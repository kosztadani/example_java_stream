package dev.kosztadani.examples.stream.merge;

/**
 * Wrapper around an integer.
 *
 * <p>
 * This allows to create integer-like objects that are guaranteed to be
 * distinct objects, without having to use the deprecated constructors of
 * {@link Integer}.
 */
class IntegerWrapper implements Comparable<IntegerWrapper> {

    private final Integer delegate;

    IntegerWrapper(int i) {
        delegate = i;
    }

    @Override
    public int compareTo(IntegerWrapper o) {
        return delegate.compareTo(o.delegate);
    }
}
