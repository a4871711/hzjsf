package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.CardType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CardTypeMapper {
    int deleteByPrimaryKey(Long ctId);

    int insert(CardType record);

    int insertSelective(CardType record);

    CardType selectByPrimaryKey(Long ctId);

    int updateByPrimaryKeySelective(CardType record);

    int updateByPrimaryKey(CardType record);

    List<CardType> selectAllCardType();

    int queryIfExistCardType(String ctName);

    Integer selectLastCardType();

    int queryIfExistCardTypeByCtId(Long ctId);
}