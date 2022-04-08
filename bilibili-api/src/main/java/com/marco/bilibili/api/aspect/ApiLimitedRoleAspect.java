package com.marco.bilibili.api.aspect;

import com.marco.bilibili.api.support.UserSupport;
import com.marco.bilibili.domain.annotation.ApiLimitedRole;
import com.marco.bilibili.domain.auth.UserRole;
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
public class ApiLimitedRoleAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.marco.bilibili.domain.annotation.ApiLimitedRole)")
    public void check(){

    }

    @Before("check() && @annotation(apiLimitedRole)")
    public void deBefore(JoinPoint joinPoint, ApiLimitedRole apiLimitedRole){
        Long userId = userSupport.getCurrentUserId();
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        String[] limitedRoleCodeList = apiLimitedRole.limitedRoleCodeList();
        Set<String> limitedRoleCodeSet = Arrays.stream(limitedRoleCodeList).collect(Collectors.toSet());
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        roleCodeSet.retainAll(limitedRoleCodeSet);
        //只要有一个权限不足就不能访问
        if(roleCodeSet.size() > 0){
            throw new ConditionException("权限不足！");
        }
    }

}
