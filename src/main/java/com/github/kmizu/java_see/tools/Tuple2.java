package com.github.kmizu.java_see.tools;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Tuple2<A1, A2> {
    public final A1 _1;
    public final A2 _2;
}
