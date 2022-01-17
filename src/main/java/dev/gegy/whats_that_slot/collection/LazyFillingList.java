package dev.gegy.whats_that_slot.collection;

import com.google.common.collect.AbstractIterator;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class LazyFillingList<T> extends AbstractList<T> {
    private final Iterator<T> source;
    private final int size;

    private final List<T> backing = new ArrayList<>();

    private LazyFillingList(Iterator<T> source, int size) {
        this.source = source;
        this.size = size;
    }

    public static <T> LazyFillingList<T> ofIterator(Iterator<T> source, int size) {
        return new LazyFillingList<>(source, size);
    }

    @Override
    public Iterator<T> iterator() {
        if (this.isFilled()) {
            return this.backing.iterator();
        } else {
            return new FillingIterator();
        }
    }

    @Override
    public T get(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + index);
        }

        List<T> list = this.fillUpTo(index);
        return list.get(index);
    }

    private List<T> fillUpTo(int index) {
        Iterator<T> source = this.source;
        List<T> backing = this.backing;
        while (backing.size() <= index) {
            if (!source.hasNext()) {
                throw new IllegalStateException("Could not fill list up to index=" + index);
            }
            backing.add(source.next());
        }

        return backing;
    }

    @Override
    public int size() {
        return this.size;
    }

    private boolean isFilled() {
        return this.backing.size() >= this.size;
    }

    private final class FillingIterator extends AbstractIterator<T> {
        private final List<T> backingList = LazyFillingList.this.backing;
        private final Iterator<T> backingIterator = this.backingList.iterator();
        private final Iterator<T> sourceIterator = LazyFillingList.this.source;

        @Override
        protected T computeNext() {
            Iterator<T> backingIterator = this.backingIterator;
            if (backingIterator.hasNext()) {
                return backingIterator.next();
            }

            Iterator<T> sourceIterator = this.sourceIterator;
            if (sourceIterator.hasNext()) {
                T next = sourceIterator.next();
                this.backingList.add(next);
                return next;
            }

            return this.endOfData();
        }
    }
}
