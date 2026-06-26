package com.dlc.modules.api.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.R;

import java.util.List;
import java.util.Map;

/**
 * 个人中心
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-10 21:04:23
 */
public interface UserCentreService {
	/**
	 * 查詢個人中心信息
	 * @return
	 */
	Map<String, Object> queryUserInfo(Long userId, Long myId);

	/**
	 * 個人等級查詢
	 * @param userId
	 * @return
	 */
	Map<String, Object> queryUserLevel(Long userId);

    /**
     * 查詢系統消息
     * @param userId
     * @return
     */
	R querySystemMsg(Long userId);

    /**
     * 课程中心（团教课）查询
     * @param parms
     * @return
     */
	PageUtils queryTeamClass(Map<String, Object> parms);

	/**
	 * 课程中心(私教课)查询
	 * @param parms
	 * @return
	 */
	PageUtils queryMyPrivateClass(Map<String, Object> parms);

	/**
	 * 查询我的约课
	 * @return
	 */
	PageUtils queryAppointClass(Map<String, Object> params);

    /**
     * 查询我的订单订单列表   类型（ 0：商城订单 1：团教课订单 2：私教课订单） 默认0
	 * 订单状态（0 ：全部 1：待付款 2：待发货 2：待收货）
     * @return
     */
    PageUtils queryMyOrders(Map<String, Object> parms);

	/**
	 * 查询我的订单订单详情    类型（ 0：商城订单 1：团教课订单 2：私教课订单） 默认0
	 * @param userId
	 * @param orderType
	 * @return
	 */
	Map<String, Object> queryMyOrderDetail(Long userId, String orderNo, int orderType);

	/**
	 * 查询我的健身卡
	 * @param userId
	 * @return
	 */
	Map<String, Object> queryMyCard(Long userId);

	/**
	 * 查询我的设备
	 * @param userId
	 * @return
	 */
    List<Map<String, Object>> queryMyDevice(Long userId);

	/**
	 * 查询兴趣标签
	 * @return
	 */
    List<String> queryHobbies();
	//查未读
    Map<String, Object> querySystemMsgCount(Long userId);
	//取消订单
    R orderCancel(Long userId, String orderNo, int orderType);
	//物理删除商城订单
    R orderDel(Long userId, String orderNo);
	//确认订单
    R orderConfirm(Long userId, String orderNo);
}
