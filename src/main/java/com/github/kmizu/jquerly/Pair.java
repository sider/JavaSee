package com.github.kmizu.jquerly;

import java.util.Objects;

public class Pair<A1, A2> {
    public final A1 _1;
    public final A2 _2;

    public Pair(A1 _1, A2 _2) {
        this._1 = _1;
        this._2 = _2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return _1.equals(pair._1) &&
                _2.equals(pair._2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "_1=" + _1 +
                ", _2=" + _2 +
                '}';
    }
}
