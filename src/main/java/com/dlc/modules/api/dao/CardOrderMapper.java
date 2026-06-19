package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.CardOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CardOrderMapper {
    int deleteByPrimaryKey(Long cardOrderId);

    int insert(CardOrder record);

    int insertSelective(CardOrder record);

    CardOrder selectByPrimaryKey(Long cardOrderId);

    int updateByPrimaryKeySelective(CardOrder record);

    int updateByPrimaryKey(CardOrder record);

    /**
     * 查询个人卡信息
     * @param userId
     * @return
     */
    Map<String, Object> queryCardDetailByUserId(Long userId);

    CardOrder selectByOrderNo(String orderNo);

    Map<String,Object> selectCardOrderByUSerId(Long userId);

    List<CardOrder> selectAllCardOrderByStatus();

    Long queryStoreIdByAddress(Long storeAddrId);

    CardOrder selectPapCardOrder(Long userId);

    int updateOrderStatus(@Param("orderNo") String orderNo, @Param("status") int status);

	List<Map<String, Object>> queryList(Query query);

	int queryTotal(Query query);

}