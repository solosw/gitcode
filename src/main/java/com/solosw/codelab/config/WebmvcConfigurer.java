package com.solosw.codelab.config;

import com.solosw.codelab.Inspectors.JwtInterceptor;
import com.solosw.codelab.config.server.CustomAuthenticationProvider;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebmvcConfigurer implements WebMvcConfigurer {

    @Value("${local.file.dir}")
    private String localFileDir;

    @Value("${local.file.path}")
    private String localFilePath;
    @Autowired
    JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**") // 拦截所有以 /api 开头的请求
                .excludePathPatterns("/back/user/login"); // 排除登录接口
    }

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {

        File file = new File(localFileDir);
        if (file.exists() && file.isFile()) {
            throw new RuntimeException("本地路径已被占用：" + localFileDir);
        }

        if (!file.exists()) {
            file.mkdirs();
        }

        registry.addResourceHandler(localFilePath + "/**").addResourceLocations("file:" + localFileDir);
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 当所有未匹配的GET请求发生时，重定向到index.html
        registry.addViewController("/**/{path:[^\\.]*}").setViewName("forward:/");
    }



}
