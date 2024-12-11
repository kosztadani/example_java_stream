package dev.kosztadani.examples.stream.merge;

import java.util.Iterator;

class BufferedIterator<T> implements Iterator<T> {

    private final Iterator<? extends T> delegate;

    private final Buffer<T> buffer = new Buffer<>();

    BufferedIterator(Iterator<? extends T> iterator) {
        delegate = iterator;
    }

    @Override
    public boolean hasNext() {
        return buffer.isPresent() || delegate.hasNext();
    }

    @Override
    public T next() {
        if (buffer.isPresent()) {
            T object = buffer.get();
            buffer.clear();
            return object;
        } else {
            return delegate.next();
        }
    }

    T peek() {
        if (!buffer.isPresent()) {
            buffer.set(delegate.next());
        }
        return buffer.get();
    }
}
