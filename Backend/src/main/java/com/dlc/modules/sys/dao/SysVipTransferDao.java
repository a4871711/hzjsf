package com.dlc.modules.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * VIP 权益转让审核工作台(vip_benefit_transfer)后台只读查询。
 * 列表联表带出转让人/受让人昵称手机号、权益卡名、已被转让次数;审核流转复用 api 的 VipTransferService。
 * 对应 mapper/sys/SysVipTransferDao.xml(sys XML 热刷新,无需重启)。
 *
 * @date 2026-07-02
 */
@Mapper
@Repository
public interface SysVipTransferDao {

    /** 审核工作台分页列表(联表,带门店数据权限 storeIds 过滤) */
    List<Map<String, Object>> queryList(Map<String, Object> params);

    /** 与 queryList 同条件的总数 */
    int queryTotal(Map<String, Object> params);
}
