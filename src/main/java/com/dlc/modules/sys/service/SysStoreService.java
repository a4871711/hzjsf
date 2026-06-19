package com.dlc.modules.sys.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.SysStoreEntity;
import com.dlc.modules.sys.vo.OpenRecordVo;

import java.util.List;
import java.util.Map;

/**
 * lingkangming
 */
public interface SysStoreService {
	
	SysStoreEntity queryObject(Long id);
	
	List<SysStoreEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	int save(SysStoreEntity store);
	
	void update(SysStoreEntity store);
	
	int delete(Long id);
	
	void deleteBatch(Long[] ids);

	/**
	 * 根据门店电话判断是否已存在
	 */
	List<Map<String,Object>> queryStoreByCondition(Map<String, Object> map);

	void updateStoreAddId(Long storeId);
	//根据门店id查询门店下所有私教课
    PageUtils queryClassList(Query query);

	R confirmClass(Long arrangeClassId);

    PageUtils queryCoachList(Query query);

    //R updateGrade(Long cpsId, Integer grade);

	R updateGrade(Map<String, Object> params);
	//门店地址id
    Long queryStoreAddrId(Long storeId);
	//查团体课列表
    PageUtils queryTeamClassList(Query query);
	//更新团体课状态
    R updateClass(Long[] stuTeamClassId);

    int updateStoreAddIdByMap(Map<String, Object> upMap);

	PageUtils queryStuInfo(Map<String, Object> params);

	int insertStore(Map<String,Object> map);

    int updateStoretatus(Long id, int status);

    R signInTeamClass(Long[] stuTeamClassIds);

    List<Map<String,Object>> queryfaceRecordList(Query query);

	int queryfaceRecordTotal(Query query);

	List<OpenRecordVo> queryOpenDoorRecordList(Query query);

	int queryOpenDoorRecordTotal(Query query);

	//统计门店预估在店总人数
	int getStorePeopleTotal(String storeId);

	//获取指定人&时间后的第一次开门记录
	Map getNearOpenLog(Long userId, String startTime);

	//获取指定人&ID前的最后一次开门记录
	Map getLastOpenLog(Long userId, String id);

}
