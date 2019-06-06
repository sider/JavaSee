package com.github.sider.java_see.tools;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Tuple3<A1, A2, A3> {
    public final A1 _1;
    public final A2 _2;
    public final A3 _3;
}
