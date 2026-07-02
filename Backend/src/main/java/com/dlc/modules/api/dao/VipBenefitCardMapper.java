package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.VipBenefitCard;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * VIP 权益卡商品 Mapper(移动端只读查询)
 */
@Mapper
@Repository
public interface VipBenefitCardMapper {

    /** 分页查上架(status=1)权益卡列表 */
    List<VipBenefitCard> queryList(Map<String, Object> params);

    /** 上架权益卡总数(与 queryList 同条件) */
    int queryTotal(Map<String, Object> params);

    /** 按主键查单条上架权益卡,不存在/已下架返回 null */
    VipBenefitCard queryObject(Long vipCardId);

    /** 按主键查权益卡(不带状态过滤),供转让校验判断是否已下架、取适用门店/费用规则 */
    VipBenefitCard selectByIdIgnoreStatus(Long vipCardId);
}
