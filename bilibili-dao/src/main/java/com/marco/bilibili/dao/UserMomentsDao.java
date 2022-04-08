package com.marco.bilibili.dao;

import com.marco.bilibili.domain.UserMoment;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserMomentsDao {

    Integer addUserMoments(UserMoment userMoment);

}
