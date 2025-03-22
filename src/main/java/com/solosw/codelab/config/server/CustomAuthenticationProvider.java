package com.solosw.codelab.config.server;

import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    UsersService usersService;
    @Autowired
    UserContext userContext;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        Users u=usersService.getUserByName(username);
        if(u==null)  throw new BadCredentialsException("认证失败");
        if(username==null) throw new BadCredentialsException("认证失败");
        if(password==null) throw new BadCredentialsException("认证失败");
        userContext.setUser(new Users().setName(username).setPassword(password));
        // 1. 自定义认证逻辑（示例：直接验证用户名和密码）
        if (!password.equals(u.getPassword())) {
            throw new BadCredentialsException("认证失败");
        }

        // 2. 返回认证成功的 Token
        return new UsernamePasswordAuthenticationToken(
                username,
                password,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }


}
