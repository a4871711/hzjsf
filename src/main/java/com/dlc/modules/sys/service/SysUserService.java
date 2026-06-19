package com.dlc.modules.sys.service;



import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.*;


/**
 * 系统用户
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年9月18日 上午9:43:39
 */
public interface SysUserService {

	/**
	 * 查询用户的所有权限
	 * @param userId  用户ID
	 */
	List<String> queryAllPerms(Long userId);
	
	/**
	 * 查询用户的所有菜单ID
	 */
	List<Long> queryAllMenuId(Long userId);

	/**
	 * 根据用户名，查询系统用户
	 */
	SysUserEntity queryByUserName(String username);
	
	/**
	 * 根据用户ID，查询用户
	 * @param userId
	 * @return
	 */
	SysUserEntity queryObject(Long userId);
	
	/**
	 * 查询用户列表
	 */
	List<SysUserEntity> queryList(Map<String, Object> map);
	
	/**
	 * 查询总数
	 */
	int queryTotal(Map<String, Object> map);
	
	/**
	 * 保存用户
	 */
	void save(SysUserEntity user);
	
	/**
	 * 修改用户
	 */
	void update(SysUserEntity user);
	
	/**
	 * 删除用户
	 */
	void deleteBatch(Long[] userIds);
	
	/**
	 * 修改密码
	 * @param userId       用户ID
	 * @param password     原密码
	 * @param newPassword  新密码
	 */
	int updatePassword(Long userId, String password, String newPassword);
	//查询APP端用户
    PageUtils queryAppList(Map<String, Object> params);
    PageUtils queryDelList(Map<String, Object> params);
	//查询APP端用户优惠券列表
    PageUtils queryUserCouponList(Map<String, Object> params);
	//查询个人优惠券详情列表
    List<Map<String,Object>> queryUserCouponInfo(Long userId);

	R updateUserCoupon(Long[] userIds, Double couponMoney, Integer validDay, BigDecimal limitPrice, String couponTitle, String storeAddrIds);
	R updateUserCoupon(Long[] userIds, Long sysCouponId);

    PageUtils queryStoreMemberList(Map<String, Object> params);

    boolean queryUserByPhone(String phone);

    int updateFaceStatusR(Long deviceId);

    PageUtils queryCouponList(Map<String, Object> params);

    int updateMemberStatus(Long[] deviceIds, int i);

    PageUtils getTranMemberList(Map<String, Object> params);

    int transferCard(Long taUserId, Long trUserId, Long storeId);
	//查询门店会员
    SysDeviceEntity queryStoreMemberById(Long userId, Long storeId);

    PageUtils getMessageTmpList(Map<String, Object> params);

    int saveMsgTemp(MessageTemplate messageTemplate);

	int updateMsgTemp(MessageTemplate messageTemplate);

    int deleteMsgTemp(Long mtId);

	int pushMsgToUser(Long[] deviceIds, Long mtId);

    int queryOtherCard(Long taUserId, Long storeId);

	int pushMsgToUserByUId(Long[] userIds, Long mtId);

    int updateMemberValiditys(NewMember newMember);

	SysDeviceEntity canUpdateValidity(Long deviceId);

    int updateMemberStatusStart(Long[] deviceIds);

	int updateOverMemberValiditys(NewMember newMember, String deviceNo);

    PageUtils getValidityRecordList(Map<String, Object> params);

    List<SysStoreMemberExcel> queryExportStoreMemberList(Map<String, Object> params);

    R delAutoPayByDeviceId(Long deviceId);

    int updateUserInfo(Map data);

	List getUserIdsByDeviceId(Long[] deviceIds);

	/**
	 * 删除注销账户
	 * @param userIds
	 * @return
	 */
	int batchDeleteUsers(List userIds, long sysUserId, Date delTime);

	//风控异常账户列表
	List queryRiskList(Query query);

	int queryRiskTotal(Query query);

	int checkRisk(int id, int check);

	R clearAppUserData(Long userId, String password, List<String> clearTypes);

}
