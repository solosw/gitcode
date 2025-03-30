package com.solosw.codelab.annations.aspects;

import com.solosw.codelab.annations.PermissionCheck;
import com.solosw.codelab.controller.base.BaseController;
import com.solosw.codelab.entity.po.Users;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.h2.util.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class PermissionCheckAspect extends BaseController {

    @Around("@annotation(permissionCheck)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, PermissionCheck permissionCheck) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取方法参数名和值
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        String houseName = permissionCheck.houseParam();
        // 获取注解中指定的参数名
        String paramName = permissionCheck.userParam();
        HttpServletRequest httpServletRequest=null;
        // 查找对应参数值
        Object userParam = null;
        Object houseParam = null;
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(paramName)) {
                if(args[i] instanceof Long||args[i] instanceof Integer){
                    userParam = args[i];
                }

            }
            if (parameterNames[i].equals(houseName)) {
                if(args[i] instanceof Long||args[i] instanceof Integer){
                    houseParam = args[i];
                }

            }
        }

        for (Object arg : args) {
            if (arg instanceof HttpServletRequest) {
                httpServletRequest = (HttpServletRequest) arg;
            }
        }
        if(httpServletRequest==null||userParam==null){
            throw new SecurityException("No Permission");
        }
        Users users=getCurrentUser(httpServletRequest);
        if(!users.getId().equals((Long) userParam)){
            throw new SecurityException("No Permission");
        }

        // 权限检查通过，继续执行原方法
        return joinPoint.proceed();
    }

    // 模拟根据类和参数值查询权限
    private String queryPermission(Class<?> clazz, Object paramValue) {
        // 实际项目中，这里可以根据类和参数值从数据库或其他服务中查询权限
        System.out.println("查询权限: 类=" + clazz.getName() + ", 参数=" + paramValue);
        return "USER_READ"; // 假设返回固定权限
    }

    // 模拟获取当前用户的权限列表
    private String[] getCurrentUserPermissions() {
        return new String[]{"USER_READ", "USER_WRITE"};
    }

    // 检查用户是否拥有需要的权限
    private boolean hasPermission(String[] userPermissions, String requiredPermission) {
        for (String permission : userPermissions) {
            if (permission.equals(requiredPermission)) {
                return true;
            }
        }
        return false;
    }
}
