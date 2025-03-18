package com.solosw.codelab;

import org.apache.sshd.server.auth.keyboard.DefaultKeyboardInteractiveAuthenticator;
import org.apache.sshd.server.auth.keyboard.InteractiveChallenge;
import org.apache.sshd.server.auth.keyboard.KeyboardInteractiveAuthenticator;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.util.List;

public class MyKeyboardInteractiveAuthenticator extends DefaultKeyboardInteractiveAuthenticator {


    @Override
    public boolean authenticate(ServerSession serverSession, String s, List<String> list) throws Exception {
        return true;
    }
    @Override
    public InteractiveChallenge generateChallenge(ServerSession session, String username, String lang, String subMethods) throws Exception {
            PasswordAuthenticator auth = session.getPasswordAuthenticator();
            InteractiveChallenge challenge = new InteractiveChallenge();
            challenge.setInteractionName(this.getInteractionName(session));
            challenge.setInteractionInstruction(this.getInteractionInstruction(session));
            challenge.setLanguageTag(this.getInteractionLanguage(session));
            challenge.addPrompt(this.getInteractionPrompt(session), this.isInteractionPromptEchoEnabled(session));
            return challenge;

    }
}
