package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.MkFlashSaleActivityEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 限时秒杀活动 Dao（mk_flash_sale_activity）。sold_count 后台只读，update 语句不写该列。
 *
 * @author claude
 */
@Mapper
@Repository
public interface MkFlashSaleActivityDao extends BaseDao<MkFlashSaleActivityEntity> {

    /** 上下架切换；map: id/status/updatedBy */
    int changeStatus(Map<String, Object> map);
}
