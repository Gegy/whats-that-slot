package dev.gegy.whats_that_slot.collection;

import com.google.common.collect.Iterators;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

public final class ConcatList<T> extends AbstractList<T> {
    private final List<T> a;
    private final List<T> b;

    private ConcatList(List<T> a, List<T> b) {
        this.a = a;
        this.b = b;
    }

    public static <T> ConcatList<T> of(List<T> a, List<T> b) {
        return new ConcatList<>(a, b);
    }

    @Override
    public T get(int index) {
        if (index < this.a.size()) {
            return this.a.get(index);
        } else {
            return this.b.get(index - this.a.size());
        }
    }

    @Override
    public int size() {
        return this.a.size() + this.b.size();
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.concat(this.a.iterator(), this.b.iterator());
    }
}
