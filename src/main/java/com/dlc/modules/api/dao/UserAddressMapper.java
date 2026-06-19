package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserAddressMapper {
    int deleteByPrimaryKey(Long userAddressId);

    int insert(UserAddress record);

    int insertSelective(UserAddress record);

    UserAddress selectByPrimaryKey(Long userAddressId);

    int updateByPrimaryKeySelective(UserAddress record);

    int updateByPrimaryKey(UserAddress record);

    List<UserAddress> queryList(Map<String, Object> map);

    int updateIsdefaultId( Map<String, Object> queryMap);

    Map<String,Object>  queryDefaultAddressByUserId(Long userId);
}