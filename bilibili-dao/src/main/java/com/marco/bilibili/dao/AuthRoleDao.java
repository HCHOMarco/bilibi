package com.marco.bilibili.dao;

import com.marco.bilibili.domain.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthRoleDao {

    AuthRole getRoleByCode(String code);
}
