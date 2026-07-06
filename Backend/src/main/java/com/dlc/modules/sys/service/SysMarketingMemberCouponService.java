package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.MkMemberCouponEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员领券记录 Service（mk_member_coupon，只读）。记录由发券/领券生成，后台不手改。
 *
 * @author claude
 */
public interface SysMarketingMemberCouponService {

    MkMemberCouponEntity queryObject(Long id);

    List<MkMemberCouponEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);
}
