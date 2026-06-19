package com.dlc.modules.sys.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.UserCouponEntity;
import com.dlc.modules.sys.entity.SysStoreMemberExcel;
import com.dlc.modules.sys.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 系统用户
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年9月18日 上午9:34:11
 */
@Mapper
@Repository
public interface SysUserDao extends BaseDao<SysUserEntity> {
	
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
	SysUserEntity queryByMobile(String mobile);
	
	/**
	 * 修改密码
	 */
	int updatePassword(Map<String, Object> map);
	//app用户列表
    List<Map<String,Object>> queryAppList(Query query);
    List<Map<String,Object>> queryDelList(Query query);
	//app user 总数
	int queryAppTotal(Query query);
	int queryDelTotal(Query query);
	//app用户列表
    List<Map<String,Object>> queryUserCouponList(Query query);
	//app user 总数
	int queryUserCouponTotal(Query query);

    List<Map<String,Object>> queryUserCouponInfo(Long userId);

    int insertCouponInfo(Map<String, Object> updateMap);

    List<Map<String,Object>> queryStoreMemberList(Query query);

	int queryStoreMemberTotal(Query query);

    int queryUserByPhone(String phone);

    int updateUFaceSta(Long userId);

    List<UserCouponEntity> queryCouponList(Query query);

	int queryCouponTotal(Query query);

	int selectTranMemberListTotal(Query query);

    List<Map<String,Object>> selectTranMemberList(Query query);

    int isAnyCardByUserId(Long userId);

    Long getStoreAddressIdBySId(Long storeId);

    Map<String,Object> getTaUserInfoByUid(Long userId);

    int updateUserWristByUid(Long userId);

    List<String> queryDeviceTokens(Long[] deviceId);

	List<String> queryAllDeviceTokens();

    Long getProxyIdByDeviceId(Long deviceId);

	List<String> queryDeviceTokensByUid(Long[] userId);

    List<SysStoreMemberExcel> queryExportStoreMemberList(Map<String, Object> params);

	/**
	 * 更新用户信息
	 * @param map
	 * @return
	 */
	int updateUserInfo(Map<String, Object> map);

	int batchDeleteUsers(@Param("userIds") List userIds, @Param("sysUserId") long sysUserId, @Param("delTime") Date delTime);

	int deleteCouponByUserId(@Param("userId") Long userId);

	int deleteCardOrderByUserId(@Param("userId") Long userId);

	int deleteIncomePayDetailByUserId(@Param("userId") Long userId);

	int deleteOpenDoorRecordByUserId(@Param("userId") Long userId);

	int deleteDeviceChangeByProxyId(@Param("userId") Long userId);

	int deleteUserUpdateRecordByUserId(@Param("userId") Long userId);

	int deleteFaceIdentyRecordByUserId(@Param("userId") Long userId);

	int deleteUserTokenLoginLogsByUserId(@Param("userId") Long userId);

	int deleteUserSportDeviceRecordByUserId(@Param("userId") Long userId);

	int deleteDeviceByProxyId(@Param("userId") Long userId);

	int resetUserMemberFlags(@Param("userId") Long userId);

	/** 删除同客户身份下的历史注销账号（isDel=1） */
	int deleteDelUserRecordByUserId(@Param("userId") Long userId);
}
