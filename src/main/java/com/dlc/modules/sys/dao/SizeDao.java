package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.SizeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author LINGKANGMING
 * @createTime 2018 - 09 - 20 15:02
 * @description 尺寸dao
 */
@Mapper
@Repository
public interface SizeDao extends BaseDao<SizeEntity>{
    List<SizeEntity> selectSizeDetail();

    /**
     * 查询是否已经存在该尺寸
     * @param map
     * @return
     */
    List<Map<String,Object>> getExistsSize(Map<String, Object> map);
}
