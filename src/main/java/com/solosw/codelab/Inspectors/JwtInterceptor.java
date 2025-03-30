package com.solosw.codelab.Inspectors;

import com.solosw.codelab.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Value("${local.file.path}")
    private String localFilePath;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的token
        if(request.getRequestURI().startsWith("/back/"+localFilePath)) return true;

        String token = request.getHeader("Authorization");

        // 检查token是否存在
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid token");
            return false;
        }

        // 验证token是否有效
        if (!JwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return false;
        }

        // 如果token有效，继续处理请求
        return true;
    }
}
