package com.marco.bilibili.api.aspect;

import com.marco.bilibili.api.support.UserSupport;
import com.marco.bilibili.domain.UserMoment;
import com.marco.bilibili.domain.annotation.ApiLimitedRole;
import com.marco.bilibili.domain.auth.UserRole;
import com.marco.bilibili.domain.constant.AuthRoleConstant;
import com.marco.bilibili.domain.exception.ConditionException;
import com.marco.bilibili.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Order(1)
@Component
@Aspect
public class DataLimitedAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.marco.bilibili.domain.annotation.DataLimited)")
    public void check(){

    }

    @Before("check()")
    public void deBefore(JoinPoint joinPoint){
        Long userId = userSupport.getCurrentUserId();
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        Object[] args = joinPoint.getArgs();
        for(Object arg : args){
            if(arg instanceof UserMoment){
                UserMoment userMoment = (UserMoment)arg;
                String type = userMoment.getType();
                //业务逻辑
                if(roleCodeSet.contains(AuthRoleConstant.ROLE_LV1) && !"0".equals(type)){
                    throw new ConditionException("参数异常！");
                }
            }
        }
    }

}
