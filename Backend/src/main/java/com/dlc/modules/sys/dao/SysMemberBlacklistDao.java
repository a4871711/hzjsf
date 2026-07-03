package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.MemberBlacklistEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 会员黑名单后台管理(member_blacklist)。
 * 列表联表带出会员昵称/手机号;拉黑按手机号定位会员并去重,解除为软删(status=0)。
 * 对应 mapper/sys/SysMemberBlacklistDao.xml(sys XML 热刷新,无需重启)。
 *
 * @date 2026-07-03
 */
@Mapper
@Repository
public interface SysMemberBlacklistDao {

    /** 黑名单分页列表(联表 user_info 带昵称/手机号) */
    List<Map<String, Object>> queryList(Map<String, Object> params);

    /** 与 queryList 同条件的总数 */
    int queryTotal(Map<String, Object> params);

    /** 按手机号定位会员ID(不存在返回 null) */
    Long findUserIdByPhone(@Param("phone") String phone);

    /** 统计该会员当前生效的黑名单记录数(去重用) */
    int countActiveByUserId(@Param("userId") Long userId);

    /** 新增黑名单记录 */
    int save(MemberBlacklistEntity entity);

    /** 解除黑名单(软删,status 置 0) */
    int release(@Param("id") Long id);
}
