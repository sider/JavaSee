package com.github.sider.java_see.ast;

import java.util.Objects;

/**
 * @author Kota Mizushima
 * Location in AST
 */
public class Location {
    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }

    public final int line;
    public final int column;

    public Location(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return line == location.line &&
                column == location.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, column);
    }

    @Override
    public String toString() {
        return "Location{" +
                "line=" + line +
                ", column=" + column +
                '}';
    }
}
