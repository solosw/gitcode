package com.solosw.codelab;

import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.shell.ShellFactory;

public class EchoShellFactory implements ShellFactory {
    public static final EchoShellFactory INSTANCE = new EchoShellFactory();

    public EchoShellFactory() {
        super();
    }

    @Override
    public Command createShell(ChannelSession channel) {
        return new EchoShell();
    }
}
