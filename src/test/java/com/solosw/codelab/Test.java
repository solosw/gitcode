package com.solosw.codelab;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.http.server.GitServlet;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.UploadPack;


import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

public class Test {

   public SshServer sshd;
    public Test(int port,String host){
        sshd= CoreTestSupportUtils.setupTestServer(getClass());
        sshd.setPort(port);
        sshd.setHost(host);
        SimpleGeneratorHostKeyProvider hostKeyProvider = new SimpleGeneratorHostKeyProvider();
        hostKeyProvider.setPath(new java.io.File("hostkey.pem").toPath());
        sshd.setKeyPairProvider(hostKeyProvider);

    }
    public void start() throws IOException {
        sshd.start();
    }


    public static void main(String[] args) throws Exception {


    }
}
