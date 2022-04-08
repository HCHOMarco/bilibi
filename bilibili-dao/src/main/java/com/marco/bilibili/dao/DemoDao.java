package com.marco.bilibili.dao;

import com.sun.javafx.collections.MappingChange;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Mapper
public interface DemoDao {

    Long query(Long id);
}
