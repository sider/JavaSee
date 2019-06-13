package com.github.sider.java_see.command;

import com.github.sider.java_see.Main;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
public abstract class CLICommand {
    public final Main.Options options;

    public abstract boolean start();
}
