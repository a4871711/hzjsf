package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtCoachEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 私教教练主表 Dao
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtCoachDao extends BaseDao<PtCoachEntity> {

    /** 手机号唯一校验（excludeId 为空表示新增；只统计未删） */
    int countByMobile(@Param("mobile") String mobile, @Param("excludeId") Long excludeId);

    /** 会员账号绑定唯一校验 */
    int countByUserId(@Param("userId") Long userId, @Param("excludeId") Long excludeId);

    /** 教练是否属于当前管理员门店数据范围；storeIds 空表示超管 */
    int countCoachInScope(@Param("coachId") Long coachId, @Param("storeIds") String storeIds);

    /** 会员是否存在且属于当前管理员门店数据范围 */
    int countMemberInScope(@Param("userId") Long userId, @Param("storeIds") String storeIds);

    /** 按会员ID、昵称、手机号远程搜索绑定候选 */
    List<Map<String, Object>> queryMemberOptions(@Param("keyword") String keyword,
                                                  @Param("storeIds") String storeIds);

    /** 绑定/解绑手机端会员账号，userId 可为 null */
    int updateMemberBinding(@Param("coachId") Long coachId, @Param("userId") Long userId,
                            @Param("updatedBy") Long updatedBy);

    /** 统计某编号前缀已有的教练数（含已删，用于当天序号生成） */
    int countByNoPrefix(@Param("prefix") String prefix);

    /** 某等级名称被多少未删教练引用（等级删除/改名护栏） */
    int countByLevelName(@Param("levelName") String levelName);

    /** 等级改名时同步教练冗余等级名 */
    int updateLevelNameRef(@Param("oldName") String oldName, @Param("newName") String newName);
}
