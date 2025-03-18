package com.solosw.codelab;

import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.common.util.threads.NoCloseExecutor;
import org.apache.sshd.git.GitLocationResolver;
import org.apache.sshd.git.pack.GitPackCommand;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.command.CommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.UnknownCommand;
import org.h2.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

public class GitCommandFactory implements CommandFactory {

    @Override
    public Command createCommand(ChannelSession channelSession, String command) throws IOException {
        if(StringUtils.isNullOrEmpty(command)) return new UnknownCommand(command);
        command=command.trim();
        if (command.startsWith("git-upload-pack") || command.startsWith("git-receive-pack")) {

            return new MyGitPackCommand(new CoreTestSupportUtils.MyGitLocationResolver(), command,
                    new NoCloseExecutor(Executors.newFixedThreadPool(10))); // 自定义Git命令处理逻辑
        } else {
            return new UnknownCommand(command); // 或者返回一个新的拒绝访问的命令
        }
    }




}
