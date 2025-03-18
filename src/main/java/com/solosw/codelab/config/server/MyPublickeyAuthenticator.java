package com.solosw.codelab.config.server;

import com.solosw.codelab.entity.po.Users;
import org.apache.sshd.common.config.keys.KeyUtils;
import org.apache.sshd.common.config.keys.PublicKeyEntry;
import org.apache.sshd.common.config.keys.PublicKeyEntryDecoder;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.h2.util.StringUtils;

import java.security.PublicKey;
import java.util.Base64;
import java.util.List;

public class MyPublickeyAuthenticator implements PublickeyAuthenticator {

    GitPersmionHelper gitPersmionHelper;

    public MyPublickeyAuthenticator(GitPersmionHelper gitPersmionHelper) {
        this.gitPersmionHelper = gitPersmionHelper;
    }

    @Override
    public boolean authenticate(String s, PublicKey publicKey, ServerSession serverSession) throws AsyncAuthException {

        String keu=  PublicKeyEntry.toString(publicKey);
        String result = keu.replaceAll("[\\s\\n\\r]", "");

        Users users=gitPersmionHelper.getUserByName(s);
        if(users==null) return false;
        List<com.solosw.codelab.entity.po.PublicKey> publicKeyList=gitPersmionHelper.getPublicKeyByUser(users.getId());
        if(publicKeyList==null) return false;
        boolean find=false;
        for(int i=0;i<publicKeyList.size();i++){
            if(publicKeyList.get(i).getPublicKey().startsWith(result)){
                find=true;
                break;
            }
        }
        if(!find) return false;
        serverSession.getProperties().put("users",users);
        return true;
    }
}
