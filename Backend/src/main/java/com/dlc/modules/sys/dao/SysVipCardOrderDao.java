package com.dlc.modules.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * VIP 权益卡购买记录(vip_benefit)后台只读查询。
 * 移动端购买/激活(api VipBenefitService)已完成写入,后台只提供列表给 admin 的 vipCardOrder.vue 用。
 * 列表联表带出购买人(origin_user_id)与当前持有人(user_id)的昵称手机号、权益卡名。
 * 对应 mapper/sys/SysVipCardOrderDao.xml(sys XML 热刷新,无需重启)。
 *
 * @date 2026-07-03
 */
@Mapper
@Repository
public interface SysVipCardOrderDao {

    /** 购买记录分页列表(联表) */
    List<Map<String, Object>> queryList(Map<String, Object> params);

    /** 与 queryList 同条件的总数 */
    int queryTotal(Map<String, Object> params);
}
