package com.dlc.modules.api.dao;


import com.dlc.modules.api.entity.UserRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 用户记录表
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-15 10:55:18
 */
@Mapper
@Repository
public interface UserRecordDao {

	int save(UserRecordEntity userRecordEntity);

    int updateByUserId(Long userId);

    UserRecordEntity queryUserRecordByUid(Long userId);
}
