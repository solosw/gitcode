package com.solosw.codelab.utils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solosw.codelab.entity.po.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;



public class JwtUtil {

    // 密钥
    private static final String SECRET_STRING = "GPxXgWCdPqGe0AUg3bKsMM7ZLp9UfGBIKpSZl_UMnAo_asd__Sdsad_PYRVF";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    // 过期时间（单位：毫秒）
    private static final long EXPIRATION_TIME = 86400000; // 24小时

    // Jackson ObjectMapper 用于序列化和反序列化


    // 生成JWT
    public static String generateToken(Users user) {

            // 将User对象序列化为JSON字符串
            String userJson = JSON.toJSONString(user);
            return Jwts.builder()
                    .claim("user", userJson) // 将用户信息存储在JWT的自定义claim中
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(SECRET_KEY)
                    .compact();

    }

    // 解析JWT
    public static Users parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 从自定义claim中获取用户信息并反序列化为User对象
            String userJson = claims.get("user", String.class);
            return JSON.parseObject(userJson, Users.class);
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing JWT token", e);
        }
    }

    // 验证JWT是否有效
    public static boolean validateToken(String token) {
        try {
            parseToken(token); // 如果解析成功，则返回true
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
