package com.marco.bilibili.dao;

import com.marco.bilibili.domain.File;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface FileDao {

    Integer addFile(File file);

    File getFileMD5(String md5);

}
