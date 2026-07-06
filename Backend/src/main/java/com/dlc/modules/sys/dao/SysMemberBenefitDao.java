package com.dlc.modules.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 会员私教权益后台 Dao(pt_member_private_benefit 纯只读,第15步)。
 * 课时变更只能由下单/预约/退款链路驱动,本 Dao 禁止出现任何写语句。
 * 对应 mapper/sys/SysMemberBenefitDao.xml(sys 目录热刷新)。
 *
 * @author claude
 */
@Mapper
@Repository
public interface SysMemberBenefitDao {

    /** 分页列表:联会员/商品/门店/来源订单;门店隔离 storeIds 空=超管不过滤 */
    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    /**
     * 顶部统计卡(SQL 聚合一把出):生效中/已用完/已过期/已退款各数量 + 剩余/冻结课时合计。
     * 口径:沿用列表除 status 外的全部筛选(状态维度由 CASE 分桶,不吃 status 过滤)。
     */
    Map<String, Object> queryStat(Map<String, Object> params);

    /** 详情(权益+来源订单号+课时四态);storeIds 过滤在 SQL 内收口,越权/不存在返回 null → 404 */
    Map<String, Object> queryDetail(@Param("id") Long id, @Param("storeIds") String storeIds);
}
