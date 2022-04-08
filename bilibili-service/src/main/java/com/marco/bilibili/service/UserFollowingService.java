package com.marco.bilibili.service;

import com.marco.bilibili.dao.UserFollowingDao;
import com.marco.bilibili.domain.*;
import com.marco.bilibili.domain.constant.UserConstant;
import com.marco.bilibili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.dnd.DropTarget;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserFollowingService {

    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;

    @Transactional
    public void addUserFollowings(UserFollowing userFollowing){
        /*添加用户关注的关系*/

        Long groupId = userFollowing.getGroupId();
        //判断取得的分组是否正常
        if(groupId == null){
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setGroupId(followingGroup.getId());
        }else{
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if(followingGroup == null){
                throw new ConditionException("该分组不存在");
            }
        }
        Long followingId = userFollowing.getFollowingId();
        User followingUser = userService.getUserById(followingId);

        //判断关注的用户是否正常
        if(followingUser == null){
            throw new ConditionException("关注的用户不存在");
        }

        //删除原来该用户与关注对象间的关系
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(), followingId);

        userFollowing.setCreateTime(new Date());

        //添加用户与关注对象的关系（有删除和添加需要事务操作，也就是上面的注解）
        userFollowingDao.addUserFollowing(userFollowing);
    }

    /**
     * 获取用户的所有关注对象
     * 获取用户的所有组别分类
     * 获取所有关注对象的基本信息
     * 总而言之就是获取所有关注对象的基本信息和分组
     * 第一步：获取用户关注的对象列表
     * 第二步：根据关注用户的id查询关注用户的基本资料
     * 第三步：将用户关注的对象按关注的组别进行分类
     * */
    public List<FollowingGroup> getUserFollowings(Long userId){
        //获取该用户的所有关注对象的关系
        List<UserFollowing> list = userFollowingDao.getUserFollowings(userId);

        //获取该用户的所有关注对象的ID
        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        if(followingIdSet.size() > 0){
            //获取该用户的所有关注对象的基本信息
            userInfoList = userService.getUserInfoByUserIds(followingIdSet);
        }

//        List<FollowingGroup> res = new ArrayList<>();
//        return res;


        //给该用户的所有关注对象的关系赋值（赋userInfo）
        for(UserFollowing userFollowing : list){
            for(UserInfo userInfo : userInfoList){
                if(userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }

        //获取该用户的创建的组别和系统默认生成的组别（重点关注、悄悄关注等等）
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);

        //获取该用户的所有关注
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);
        allGroup.setCreateTime(new Date());

        //结果
        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);

        //给每个组别都赋值
        for(FollowingGroup followingGroup : groupList){
            List<UserInfo> infoList = new ArrayList<>();
            for(UserFollowing userFollowing : list){
                if(followingGroup.getId().equals(userFollowing.getGroupId())){
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            followingGroup.setCreateTime(new Date());
            followingGroup.setFollowingUserInfoList(infoList);
            result.add(followingGroup);
        }
        return result;
    }

    //一、获取当前用户的粉丝列表
    //二、根据粉丝的用户id查询基本信息
    //三、查询当前用户是否已经关注该粉丝
    public List<UserFollowing> getUserFans(Long userId){
        //获取粉丝列表以及id
        List<UserFollowing> fanList = userFollowingDao.getUserFans(userId);
        Set<Long> fanIdSet = fanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        //获取粉丝信息
        List<UserInfo> fanInfoList = new ArrayList<>();
        if(fanIdSet.size() > 0){
            fanInfoList =  userService.getUserInfoByUserIds(fanIdSet);
        }
        //获取该用户关注对象的关系
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);

        for(UserFollowing fan : fanList){
            for(UserInfo userInfo : fanInfoList){
                if(fan.getUserId().equals(userInfo.getUserId())){
                    userInfo.setFollowed(false);//初始化该用户是否与粉丝互粉
                    fan.setUserInfo(userInfo);//注入粉丝个人的基本信息
                }
            }

            for(UserFollowing following : followingList){
                if(fan.getUserId().equals(following.getFollowingId())){
                    fan.getUserInfo().setFollowed(true);//该粉丝和该用户互粉
                }
            }
        }
        return fanList;
    }

    public Long addUserFollowingGroups(FollowingGroup followingGroup) {
        followingGroup.setCreateTime(new Date());
        followingGroup.setType(UserConstant.USER_FOLLOWING_GROUP_TYPE_USER);
        followingGroupService.addUserFollowingGroups(followingGroup);
        return followingGroup.getId();
    }

    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupService.getUserFollowingGroups(userId);
    }

    public List<UserInfo> checkFollowingStatus(List<UserInfo> userInfoList, Long userId) {
        List<UserFollowing> userFollowingList = userFollowingDao.getUserFollowings(userId);
        for(UserInfo userInfo : userInfoList){
            userInfo.setFollowed(false);
            for(UserFollowing userFollowing : userFollowingList){
                if(userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userInfo.setFollowed(true);
                }
            }
        }
        return userInfoList;
    }
}
