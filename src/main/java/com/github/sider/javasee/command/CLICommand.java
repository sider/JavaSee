package com.github.sider.javasee.command;

import com.github.sider.javasee.Main;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class CLICommand {
    public final Main.Options options;

    public abstract boolean start();
}
