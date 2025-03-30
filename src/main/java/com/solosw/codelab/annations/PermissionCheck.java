package com.solosw.codelab.annations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD}) // 可以作用于方法或类
@Retention(RetentionPolicy.RUNTIME) // 运行时保留注解信息
public @interface PermissionCheck {

    String userParam();
    String houseParam();


}
