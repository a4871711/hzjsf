package com.dlc.modules.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 私教购买记录后台 Dao(pt_private_order 只读查询,第15步)。
 * 退款写操作不在本 Dao:委托 api 侧 PrivateOrderService.refund(见 PtPrivateOrderDao)。
 * 对应 mapper/sys/SysPrivateOrderDao.xml(sys 目录热刷新,改 XML 免重启)。
 *
 * @author claude
 */
@Mapper
@Repository
public interface SysPrivateOrderDao {

    /** 分页列表:联 store 取门店名、联权益取课时四态/到期时间;门店隔离 storeIds 空=超管不过滤 */
    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    /** 详情(含权益课时/门店名);storeIds 过滤在 SQL 内收口,越权/不存在返回 null → 404 */
    Map<String, Object> queryDetail(@Param("id") Long id, @Param("storeIds") String storeIds);

    /** 券明细(pt_private_order_coupon_rel),一单一券 */
    Map<String, Object> queryCouponRel(@Param("orderId") Long orderId);

    /** 门店范围内订单判存(退款前越权校验):0=不存在或不在管辖门店 → 404 */
    int countInScope(@Param("id") Long id, @Param("storeIds") String storeIds);
}
