package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.ColorEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author LINGKANGMING
 * @createTime 2018 - 09 - 20 15:02
 * @description 颜色dao
 */
@Mapper
@Repository
public interface ColorDao extends BaseDao<ColorEntity>{
    List<ColorEntity> selectColorDetail();

    /**
     * 查询是否已经存在该颜色
     * @param map
     * @return
     */
    List<Map<String,Object>> getExistsColor(Map<String, Object> map);
}
