package com.solosw.codelab;

import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.security.PublicKey;
import java.util.Base64;

public class MyPublickeyAuthenticator implements PublickeyAuthenticator {
    @Override
    public boolean authenticate(String s, PublicKey publicKey, ServerSession serverSession) throws AsyncAuthException {

        String encodedPublicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        System.out.println("Public Key (Base64): " + encodedPublicKeyBase64);

        return true;
    }
}
