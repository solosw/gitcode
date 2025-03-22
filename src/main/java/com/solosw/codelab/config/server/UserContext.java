package com.solosw.codelab.config.server;

import com.solosw.codelab.entity.po.Users;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class UserContext {
    private Users currentUser;

    public void setUser(Users user) {
        this.currentUser = user;
    }

    public Users getUser() {
        return currentUser;
    }
}
