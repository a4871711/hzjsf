package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtGroupToPrivateFollowEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 团课转私教跟进记录 Dao（pt_group_to_private_follow，第22步·运营域）。
 * 名单的跟进流水（lead 1:N follow）。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtGroupToPrivateFollowDao extends BaseDao<PtGroupToPrivateFollowEntity> {

    /** 某名单的跟进流水（倒序） */
    List<PtGroupToPrivateFollowEntity> listByLeadId(Long leadId);
}
