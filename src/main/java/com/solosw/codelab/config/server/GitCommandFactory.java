package com.solosw.codelab.config.server;

import org.apache.sshd.common.util.threads.NoCloseExecutor;
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
import java.util.concurrent.Executors;

public class GitCommandFactory implements CommandFactory {

    int maxThread;
    String rootPath;

    GitPersmionHelper gitPersmionHelper;
    @Override
    public Command createCommand(ChannelSession channelSession, String command) throws IOException {

        if(StringUtils.isNullOrEmpty(command)) return new UnknownCommand(command);
        command=command.trim();
        System.out.println("----------------------------------------------");
        System.out.println(command);
        System.out.println("----------------------------------------------");
        if (command.startsWith("git-upload-pack") || command.startsWith("git-receive-pack")) {

            return new MyGitPackCommand(new CoreTestSupportUtils.MyGitLocationResolver(rootPath), command,
                    new NoCloseExecutor(Executors.newFixedThreadPool(maxThread)),rootPath,gitPersmionHelper,channelSession.getServerSession()); // 自定义Git命令处理逻辑
        } else {
            return new UnknownCommand(command); // 或者返回一个新的拒绝访问的命令
        }
    }

    public GitCommandFactory(int maxThread,String rootPath,GitPersmionHelper persmionHelper){
        this.maxThread=maxThread;
        this.rootPath=rootPath;
        gitPersmionHelper=persmionHelper;
    }

    static class GitCommand implements Command {
        private final String gitCommand;

        public GitCommand(String gitCommand) {
            this.gitCommand = gitCommand;
        }


       @Override
       public void setExitCallback(ExitCallback exitCallback) {

       }

       @Override
       public void setErrorStream(OutputStream outputStream) {

       }

       @Override
       public void setInputStream(InputStream inputStream) {

       }

       @Override
       public void setOutputStream(OutputStream outputStream) {

       }

       @Override
       public void start(ChannelSession channelSession, Environment environment) throws IOException {

       }

       @Override
       public void destroy(ChannelSession channelSession) throws Exception {

       }
   }
}
