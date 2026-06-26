package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.AboutUs;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface AboutUsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AboutUs record);

    int insertSelective(AboutUs record);

    AboutUs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AboutUs record);

    int updateByPrimaryKey(AboutUs record);

    List<AboutUs> queryList();
}