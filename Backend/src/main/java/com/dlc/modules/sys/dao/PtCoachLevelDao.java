package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtCoachLevelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 私教教练等级 Dao
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtCoachLevelDao extends BaseDao<PtCoachLevelEntity> {

    /** 设置默认等级前，先清空其余默认标记 */
    int clearAllDefault();

    /** 名称在未删数据内是否重复（excludeId 为空表示新增） */
    int countByName(@Param("levelName") String levelName, @Param("excludeId") Long excludeId);

    /** 启用状态等级下拉项（用于教练表单） */
    List<PtCoachLevelEntity> queryOptions();
}
