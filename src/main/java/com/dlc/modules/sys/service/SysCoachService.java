package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.MessageEntity;
import com.dlc.modules.sys.entity.SysCoachEntity;
import com.dlc.modules.sys.entity.SysDataMapEntity;

import java.util.List;
import java.util.Map;

public interface SysCoachService {
	
	SysCoachEntity queryObject(Long id);
	
	List<SysCoachEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void updateFailure(SysCoachEntity coach);
	
	void updateSuccess(SysCoachEntity coach);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);

	List<MessageEntity> newMsg();

	List<SysDataMapEntity> getCoachGradeList();

    int coachUpdate(SysCoachEntity coach);
}
