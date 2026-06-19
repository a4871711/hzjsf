package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.IncomePayDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface IncomePayDetailMapper {
    int deleteByPrimaryKey(Long incomePayDetailId);

    int insert(IncomePayDetail record);

    int insertSelective(IncomePayDetail record);

    IncomePayDetail selectByPrimaryKey(Long incomePayDetailId);

    int updateByPrimaryKeySelective(IncomePayDetail record);

    int updateTradeStatus(IncomePayDetail record);

    int updateByPrimaryKey(IncomePayDetail record);

    void AddInPayDetail(Map<String, Object> map);

    List<Map<String,Object>> queryTradeDetailByUserId(Map<String, Object> params);

    int queryTradeDetailCount(Map<String, Object> params);

}