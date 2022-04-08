package com.marco.bilibili.api;

import com.marco.bilibili.api.support.UserSupport;
import com.marco.bilibili.domain.JsonResponse;
import com.marco.bilibili.domain.UserMoment;
import com.marco.bilibili.domain.annotation.ApiLimitedRole;
import com.marco.bilibili.domain.annotation.DataLimited;
import com.marco.bilibili.domain.constant.AuthRoleConstant;
import com.marco.bilibili.service.UserMomentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserMomentsApi {

    @Autowired
    private UserMomentsService userMomentsService;

    @Autowired
    private UserSupport userSupport;

    //发布动态
    @ApiLimitedRole(limitedRoleCodeList = {AuthRoleConstant.ROLE_LV0}) //接口控制
    @DataLimited//数据控制
    @PostMapping("/user-moments")
    public JsonResponse<String> addUserMoments(@RequestBody UserMoment userMoment) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        userMoment.setUserId(userId);
        userMomentsService.addUserMoments(userMoment);
        return JsonResponse.success();
    }

    //获得用户订阅人的动态
    @GetMapping("user-subscribed-moments")
    public JsonResponse<List<UserMoment>> getUserSubscribedMoments(){
        Long userId = userSupport.getCurrentUserId();
        List<UserMoment> list = userMomentsService.getUserSubscribedMoments(userId);
        return new JsonResponse<>(list);
    }
}
