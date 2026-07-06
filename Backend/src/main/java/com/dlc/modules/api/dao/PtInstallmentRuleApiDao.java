package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.PtInstallmentRuleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 私教商品分期规则只读 Dao(pt_installment_rule,第20步资金域【只读】)。
 * <p>建表/CRUD 归商品域(第9步 sys 侧 PtInstallmentRuleDao 负责写);本 Dao 仅下单生成计划时按商品ID读规则。
 * 类名带 Api 后缀,与 sys 包 PtInstallmentRuleDao 区分。对应 mapper/api/PtInstallmentRuleApiDao.xml(api XML 改动须重启)。</p>
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtInstallmentRuleApiDao {

    /** 按商品ID查分期规则(唯一键 uk_product_id),null=未配置 */
    PtInstallmentRuleEntity selectByProductId(@Param("productId") Long productId);
}
