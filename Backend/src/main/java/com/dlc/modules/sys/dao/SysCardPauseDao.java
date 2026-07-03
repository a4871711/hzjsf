package com.dlc.modules.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 停卡记录(card_pause_record)后台只读查询。
 * 移动端停卡(api CardPauseController/Service)已完成写入,后台只提供列表给 admin 的 cardPause.vue 用。
 * 列表联表带出会员昵称手机号、卡名,并附本卡本年度停卡次数(yearCount)。
 * 对应 mapper/sys/SysCardPauseDao.xml(sys XML 热刷新,无需重启)。
 *
 * @date 2026-07-03
 */
@Mapper
@Repository
public interface SysCardPauseDao {

    /** 停卡记录分页列表(联表) */
    List<Map<String, Object>> queryList(Map<String, Object> params);

    /** 与 queryList 同条件的总数 */
    int queryTotal(Map<String, Object> params);
}
