package com.solosw.codelab;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

import java.io.IOException;

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
}
