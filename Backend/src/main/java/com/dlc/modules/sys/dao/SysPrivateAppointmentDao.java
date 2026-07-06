package com.dlc.modules.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 私教预约记录后台 Dao(pt_private_appointment 只读查询,第15步)。
 * finish/cancel/coachBook 写操作不在本 Dao:委托 api 侧 PrivateAppointmentService 三态机。
 * 对应 mapper/sys/SysPrivateAppointmentDao.xml(sys 目录热刷新)。
 *
 * @author claude
 */
@Mapper
@Repository
public interface SysPrivateAppointmentDao {

    /** 分页列表:联会员/教练/商品/门店/权益编号;门店隔离 storeIds 空=超管不过滤 */
    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    /** 顶部统计卡:待上课/已完成/已取消/爽约数量(沿用除 appointmentStatus 外的筛选) */
    Map<String, Object> queryStat(Map<String, Object> params);

    /** 详情(含会员/教练/权益编号);storeIds 过滤在 SQL 内收口,越权/不存在返回 null → 404 */
    Map<String, Object> queryDetail(@Param("id") Long id, @Param("storeIds") String storeIds);

    /** 门店范围内预约判存(finish/cancel 前越权校验):0=不存在或不在管辖门店 → 404 */
    int countInScope(@Param("id") Long id, @Param("storeIds") String storeIds);

    /** 教练是否在管辖门店范围内(coachBook 前越权校验):storeIds 空=超管放行 */
    int countCoachInScope(@Param("coachId") Long coachId, @Param("storeIds") String storeIds);
}
