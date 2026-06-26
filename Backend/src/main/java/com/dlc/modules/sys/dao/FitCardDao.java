package com.dlc.modules.sys.dao;


import com.dlc.modules.sys.entity.FitCardEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 健身卡(会员卡)
 *
 * @author daibenting
 * @email
 * @date 2018-09-10 10:24:28
 */
@Mapper
@Repository
public interface FitCardDao extends BaseDao<FitCardEntity> {
    Integer fitCardCount(long cardId);

    int updateOnOffCard(@Param("fitCardId") Long fitCardId,
                        @Param("status") Integer status);
}
