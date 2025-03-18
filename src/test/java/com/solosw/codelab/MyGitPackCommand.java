package com.solosw.codelab;

import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.MapEntryUtils;
import org.apache.sshd.common.util.ValidateUtils;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.git.AbstractGitCommand;
import org.apache.sshd.git.GitLocationResolver;
import org.apache.sshd.server.Environment;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.UploadPack;
import org.eclipse.jgit.util.FS;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MyGitPackCommand extends AbstractGitCommand {
    public MyGitPackCommand(GitLocationResolver rootDirResolver, String command, CloseableExecutorService executorService) {
        super(rootDirResolver, command, executorService);
    }

    public void run() {
        String command = this.getCommand();

        try {
            List<String> strs = parseDelimitedString(command, " ", true);
            String[] args = (String[])strs.toArray(new String[strs.size()]);

            for(int i = 0; i < args.length; ++i) {
                String argVal = args[i];
                if (argVal.startsWith("'") && argVal.endsWith("'")) {
                    args[i] = argVal.substring(1, argVal.length() - 1);
                    argVal = args[i];
                }

                if (argVal.startsWith("\"") && argVal.endsWith("\"")) {
                    args[i] = argVal.substring(1, argVal.length() - 1);
                }
            }

            if (args.length != 2) {
                throw new IllegalArgumentException("Invalid git command line (no arguments): " + command);
            }

            Path rootDir = this.resolveRootDirectory(command, args);
            RepositoryCache.FileKey key = RepositoryCache.FileKey.lenient(rootDir.toFile(), FS.DETECTED);
            Repository db = key.open(true);
            String subCommand = args[0];
            if ("git-upload-pack".equals(subCommand)) {
                UploadPack uploadPack = new UploadPack(db);
                Environment environment = this.getEnvironment();
                Map<String, String> envVars = environment.getEnv();
                String protocol = MapEntryUtils.isEmpty(envVars) ? null : (String)envVars.get("GIT_PROTOCOL");
                if (GenericUtils.isNotBlank(protocol)) {
                    //uploadPack.setExtraParameters(Collections.singleton(protocol));
                }

                uploadPack.upload(this.getInputStream(), this.getOutputStream(), this.getErrorStream());
            } else {
                if (!"git-receive-pack".equals(subCommand)) {
                    throw new IllegalArgumentException("Unknown git command: " + command);
                }

                (new ReceivePack(db)).receive(this.getInputStream(), this.getOutputStream(), this.getErrorStream());
            }

            this.onExit(0);
        } catch (Throwable var12) {
            this.onExit(-1, var12.getClass().getSimpleName());
        }

    }

    protected Path resolveRootDirectory(String command, String[] args) throws IOException {
        GitLocationResolver resolver = this.getGitLocationResolver();
        Path rootDir = resolver.resolveRootDirectory(command, args, this.getServerSession(), this.getFileSystem());

        return rootDir;
    }
}
