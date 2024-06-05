package com.sky.aop;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.sky.anno.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;

@Component
@Aspect
public class AutoFillAspect {

    // 同时使用excution和annotation是为了提高效率
    @Pointcut("execution (* com.sky.mapper.*.*(..)) && @annotation(com.sky.anno.AutoFill)")
    public void pointcut() {
    }

    // Before方法不需要使用ProceedingJoinPoint，因为不需要aop切面执行方法
    @Before("pointcut()")
    public void autofill(JoinPoint joinPoint) {

        // getSignature获得的方法签名提供的信息较少，需要转型为MethodSignature
        MethodSignature Signatue = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = Signatue.getMethod().getAnnotation(AutoFill.class);
        OperationType type = autoFill.value();

        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object object = args[0];

        LocalDateTime now = LocalDateTime.now();
        // 从ThreadLocal中获取当前id
        Long currentid = BaseContext.getCurrentId();
        if (type == OperationType.INSERT) {
            try {
                Method setCreatTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,
                        LocalDateTime.class);
                Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,
                        LocalDateTime.class);
                Method setCreatUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,
                        Long.class);
                Method setUpdateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,
                        Long.class);

                setCreatTime.invoke(object, now);
                setUpdateTime.invoke(object, now);
                setCreatUser.invoke(object, currentid);
                setUpdateUser.invoke(object, currentid);
            } catch (Exception e) {

                e.printStackTrace();
            }
        } else if (type == OperationType.UPDATE) {
            try {
                Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,
                        LocalDateTime.class);
                Method setUpdateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,
                        Long.class);

                setUpdateTime.invoke(object, now);
                setUpdateUser.invoke(object, currentid);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

}
