package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtCoachEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

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

    /** 统计某编号前缀已有的教练数（含已删，用于当天序号生成） */
    int countByNoPrefix(@Param("prefix") String prefix);

    /** 某等级名称被多少未删教练引用（等级删除/改名护栏） */
    int countByLevelName(@Param("levelName") String levelName);

    /** 等级改名时同步教练冗余等级名 */
    int updateLevelNameRef(@Param("oldName") String oldName, @Param("newName") String newName);
}
