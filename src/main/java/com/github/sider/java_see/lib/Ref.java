package com.github.sider.java_see.lib;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class Ref<T> {
    private T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public static <T> Ref<T> of(T initialValue) {
        return new Ref<>(initialValue);
    }
}
