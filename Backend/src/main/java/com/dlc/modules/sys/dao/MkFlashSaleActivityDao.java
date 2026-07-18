package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.MkFlashSaleActivityEntity;
import com.dlc.modules.sys.entity.MkFlashSaleProductEntity;
import com.dlc.modules.sys.entity.MkFlashSaleTimeSlotEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 限时秒杀活动 Dao（多商品版）。活动级 CRUD 走 BaseDao；商品/时段子表与 queryProductBrief 见下。
 *
 * @author claude
 */
@Mapper
@Repository
public interface MkFlashSaleActivityDao extends BaseDao<MkFlashSaleActivityEntity> {

    /** 上下架切换；map: id/status/updatedBy */
    int changeStatus(Map<String, Object> map);

    /** 按 bizType(1私教/2会员卡/3权益卡)取单个关联商品简况；map: bizType/productId；返回 name/salePrice/listing */
    Map<String, Object> queryProductBrief(Map<String, Object> map);

    /* ===== 商品子表 ===== */
    /** 回显：查活动下商品(含 productName/originPrice 按 bizType 联表)；map: activityId/bizType */
    List<MkFlashSaleProductEntity> queryProductsByActivity(Map<String, Object> map);

    /** 批量插入活动商品(sold_count 置 0) */
    int insertProducts(List<MkFlashSaleProductEntity> products);

    /** 硬删活动下全部商品(编辑时先删后建) */
    int deleteProductsByActivity(@Param("activityId") Long activityId);

    /* ===== 每日时段子表 ===== */
    List<MkFlashSaleTimeSlotEntity> queryTimeSlotsByActivity(@Param("activityId") Long activityId);

    int insertTimeSlots(List<MkFlashSaleTimeSlotEntity> timeSlots);

    int deleteTimeSlotsByActivity(@Param("activityId") Long activityId);
}
