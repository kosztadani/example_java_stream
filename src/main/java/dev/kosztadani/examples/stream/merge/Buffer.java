package dev.kosztadani.examples.stream.merge;

import java.util.NoSuchElementException;

class Buffer<T> {

    private T object;

    private boolean isEmpty = true;

    void set(T object) {
        this.object = object;
        isEmpty = false;
    }

    T get() {
        if (isEmpty) {
            throw new NoSuchElementException();
        } else {
            return object;
        }
    }

    void clear() {
        object = null;
        isEmpty = true;
    }

    boolean isPresent() {
        return !isEmpty;
    }
}
