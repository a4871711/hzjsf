package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.MkGroupBuyActivityEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 拼团活动 Dao（mk_group_buy_activity）。sold_count 后台只读，update 语句不写该列。
 *
 * @author claude
 */
@Mapper
@Repository
public interface MkGroupBuyActivityDao extends BaseDao<MkGroupBuyActivityEntity> {

    /** 上下架切换；map: id/status/updatedBy */
    int changeStatus(Map<String, Object> map);
}
