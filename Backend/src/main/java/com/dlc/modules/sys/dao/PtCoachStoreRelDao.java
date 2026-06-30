package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtCoachStoreRelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 私教教练-门店关联 Dao
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtCoachStoreRelDao extends BaseDao<PtCoachStoreRelEntity> {

    /** 删除某教练全部门店关联（全删全插用） */
    int deleteByCoachId(Long coachId);

    /** 查某教练的门店ID集合 */
    List<Long> queryStoreIds(Long coachId);

    /** 查某教练的门店名称（逗号拼接，列表展示用） */
    String queryStoreNames(Long coachId);
}
