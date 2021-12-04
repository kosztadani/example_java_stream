package dev.kosztadani.examples.stream.merge;


import java.util.Iterator;

@SuppressWarnings({"rawtypes"})
class BufferedIterator implements Iterator {

    private final Iterator delegate;

    private final Buffer buffer = new Buffer();

    BufferedIterator(Iterator iterator) {
        delegate = iterator;
    }

    @Override
    public boolean hasNext() {
        return buffer.isPresent() || delegate.hasNext();
    }

    @Override
    public Object next() {
        if (buffer.isPresent()) {
            Object object = buffer.get();
            buffer.clear();
            return object;
        } else {
            return delegate.next();
        }
    }

    public Object peek() {
        if (!buffer.isPresent()) {
            buffer.set(delegate.next());
        }
        return buffer.get();
    }
}
