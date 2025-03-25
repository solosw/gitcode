package com.solosw.codelab.controller.base;

import com.solosw.codelab.entity.po.Users;
import com.solosw.codelab.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BaseController {

    protected Users getCurrentUser(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        return JwtUtil.parseToken(token);

    }

}
