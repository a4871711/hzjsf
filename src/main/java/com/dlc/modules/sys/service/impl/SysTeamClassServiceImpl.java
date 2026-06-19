package com.dlc.modules.sys.service.impl;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.DateUtils;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.qd.UPushUntils.UMengPush;
import com.dlc.modules.sys.dao.SysTeamClassDao;
import com.dlc.modules.sys.dao.SysUserDao;
import com.dlc.modules.sys.entity.SysStoreEntity;
import com.dlc.modules.sys.entity.TeamClassEntity;
import com.dlc.modules.sys.service.SysTeamClassService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;


@Service("sysTeamClassService")
public class SysTeamClassServiceImpl implements SysTeamClassService {
	@Autowired
	private SysTeamClassDao sysTeamClassDao;
    @Autowired
    private SysUserDao sysUserDao;
	@Override
	public TeamClassEntity queryObject(Long id) {
		return sysTeamClassDao.queryObject(id);
	}

	@Override
	public List<TeamClassEntity> queryList(Map<String, Object> map) {
		return sysTeamClassDao.queryList(map);
	}

	@Override
	public int queryTotal(Map<String, Object> map) {
		return sysTeamClassDao.queryTotal(map);
	}

	@Override
	public void save(TeamClassEntity teamClass) {
		sysTeamClassDao.save(teamClass);
	}

	@Override
	public void update(TeamClassEntity teamClass) {
		sysTeamClassDao.update(teamClass);
	}

	@Override
	public void delete(Long id) {
		sysTeamClassDao.delete(id);
	}

	@Override
	public void deleteBatch(Long[] ids) {
		sysTeamClassDao.deleteBatch(ids);
	}

	@Override
	public List<SysStoreEntity> selectStoreName(String storeIds) {
		return sysTeamClassDao.selectStoreName(storeIds);
	}
}
