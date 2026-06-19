package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysCoachDao;
import com.dlc.modules.sys.dao.SysSetDao;
import com.dlc.modules.sys.entity.MessageEntity;
import com.dlc.modules.sys.entity.SysCoachEntity;
import com.dlc.modules.sys.entity.SysDataMapEntity;
import com.dlc.modules.sys.service.SysCoachService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("sysCoachService")
public class SysCoachServiceImpl implements SysCoachService {
	@Autowired
	private SysCoachDao sysCoachDao;
    @Autowired
    private SysSetDao sysSetDao;

	@Override
	public SysCoachEntity queryObject(Long id) {
		return sysCoachDao.queryObject(id);
	}

	@Override
	public List<SysCoachEntity> queryList(Map<String, Object> map) {
		return sysCoachDao.queryList(map);
	}

	@Override
	public int queryTotal(Map<String, Object> map) {
		return sysCoachDao.queryTotal(map);
	}

	@Override
	public void updateFailure(SysCoachEntity coach) {
		sysCoachDao.updateFailure(coach);
	}

	@Override
	public void updateSuccess(SysCoachEntity coach) {
		sysCoachDao.updateSuccess(coach);
		//新增教练门店场所关系表
        String storeIds = coach.getStoreId();
        Long coachId = coach.getCoachId();
        if(StringUtils.isNotBlank(storeIds)){
            List<Map<String, Object>> shipList = new ArrayList<Map<String, Object>>();
            String[] storeId = storeIds.split(",");
            for(String sId : storeId){
                Map<String, Object> shipMap = new HashMap<String, Object>();
                shipMap.put("coachId",coachId);
                long stoId = Long.parseLong(sId);
                //查询门店是否正常营业
                int statusNum = sysCoachDao.queryStoreStatus(stoId);
                if(statusNum <= 0){  //未查到跳过
                    break;
                }
                shipMap.put("storeId",stoId);
                shipList.add(shipMap);
            }
            sysCoachDao.batchShipPlace(shipList);
        }
		//初始化教练钱包表
        sysCoachDao.insertCoachWallt(coachId);
	}

	@Override
	public void delete(Long id) {
		sysCoachDao.delete(id);
	}

	@Override
	public void deleteBatch(Long[] ids) {
		sysCoachDao.deleteBatch(ids);
	}

	@Override
	public List<MessageEntity> newMsg() {
		return sysCoachDao.newMsg();
	}

	@Override
	public List<SysDataMapEntity> getCoachGradeList() {
		return sysSetDao.getCoachList();
	}

	@Override
	public int coachUpdate(SysCoachEntity coach) {
		return sysCoachDao.coachUpdateGrade(coach);
	}
}
