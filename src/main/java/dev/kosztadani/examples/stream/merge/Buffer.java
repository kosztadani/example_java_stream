package dev.kosztadani.examples.stream.merge;

import java.util.NoSuchElementException;

class Buffer {

    private Object object;

    private boolean isEmpty = true;

    public void set(Object object) {
        this.object = object;
        isEmpty = false;
    }

    public Object get() {
        if (isEmpty) {
            throw new NoSuchElementException();
        } else {
            return object;
        }
    }

    public void clear() {
        object = null;
        isEmpty = true;
    }

    public boolean isPresent() {
        return !isEmpty;
    }
}
