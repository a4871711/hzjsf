package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.SysGradePriceShipEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Mapper
@Repository
public interface SysGradePriceShipDao extends BaseDao<SysGradePriceShipEntity>{
    void savap(@Param("entityId") Long entityId,@Param("dengji")Long dengji,@Param("price")BigDecimal price);
}
