package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.FitCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface FitCardMapper {
    int deleteByPrimaryKey(Long fitCardId);

    int insert(FitCard record);

    int insertSelective(FitCard record);

    FitCard selectByPrimaryKey(Long fitCardId);

    int updateByPrimaryKeySelective(FitCard record);

    int updateByPrimaryKey(FitCard record);
    /**
     *  @Auther:YD
     *  查询健身卡列表
     */
    List<Map<String,Object>> queryFitCardList(FitCard fitCard);

    Map<String,Object> queryFitCardInfo(Long id);

	FitCard getFitCardInfo(Long valueOf);

	/** 按 fitCardId 列表查上架(status=1)会员卡详情;权益卡详情页"可购买会员卡"区块用 */
	List<FitCard> selectOnShelfByIds(@Param("ids") List<Long> ids);
}