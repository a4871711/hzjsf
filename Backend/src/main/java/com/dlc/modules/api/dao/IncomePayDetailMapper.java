package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.IncomePayDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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

    /**
     * VIP转让过户生效时补记对方(受让人)userId(详细技术设计 附录B.2/B.6)。
     * 服务费已在 payFeeCallback 记账一次,过户不重复插入流水,仅按 orderNo 补 anotherId,天然幂等。
     */
    int updateAnotherId(@Param("orderNo") String orderNo, @Param("anotherId") Long anotherId);

}