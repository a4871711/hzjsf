package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.MkCouponEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 优惠券模板 mk_coupon 的 api 侧 Dao(第18步:领券列表/领券/抵扣算法用)。
 * 与 sys.dao.MkCouponDao 同表不同包不同 XML(mapper/api/MkCouponApiDao.xml,改后须重启 Tomcat)。
 *
 * @author claude
 */
@Mapper
@Repository
public interface MkCouponApiDao {

    /** 按 id 取券模板(领券/抵扣算法用);仅取模板字段,查不到返回 null */
    MkCouponEntity selectById(@Param("couponId") Long couponId);

    /**
     * 可领券列表:status=1 上架 + deleted=0。门店匹配(该门店在 store_rel,或券未配任何门店=不限门店)。
     * newUser=false 时排除新人券(is_new_user_coupon=1),避免非新人看到领不了/用不了的新人券。
     * storeId 为空则不做门店过滤(全部上架券)。
     */
    List<MkCouponEntity> selectCanReceiveList(@Param("storeId") Long storeId,
                                              @Param("newUser") boolean newUser);

    /** 券是否可用于指定门店:券未配任何门店=不限门店(返回>0);配了则需该门店在 rel */
    int countStoreApplicable(@Param("couponId") Long couponId, @Param("storeId") Long storeId);

    /** 券是否适用指定商品(scope_type=2 指定商品时用):命中 product_rel 返回>0 */
    int countProductApplicable(@Param("couponId") Long couponId, @Param("productId") Long productId);
}
