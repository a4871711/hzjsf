package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.CoachTradeDetailEntity;
import com.dlc.modules.api.entity.PointsExchange;
import com.dlc.modules.api.entity.SystemMsgEntity;
import com.dlc.modules.api.service.ClassStatusService;
import com.dlc.modules.sys.dao.SysCoachDao;
import com.dlc.modules.sys.dao.SysDeviceDao;
import com.dlc.modules.sys.dao.SysStoreDao;
import com.dlc.modules.sys.dao.SysUserDao;
import com.dlc.modules.sys.entity.SysDeviceEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ClassStatusServiceImpl implements ClassStatusService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private SysDeviceDao sysDeviceDao;

    @Override
    public int updateMemberStatus() {
        //查询已过期的会员
        List<SysDeviceEntity> dList = sysDeviceDao.selectOverdueMember();
        if( CollectionUtils.isNotEmpty(dList) ){
            List<Long> deviceIds = new ArrayList<>();
            List<Long> proxyIds = new ArrayList<>();
            for(SysDeviceEntity deviceEntity : dList){
                deviceIds.add(deviceEntity.getDeviceId());
                proxyIds.add(deviceEntity.getProxyId());
            }
            //批量更新状态已过期4
            sysDeviceDao.updateBatchDeviceOverTime(deviceIds);
            //批量更新用户手环id为null
            int res = sysDeviceDao.updateBatchUserWristByUid(proxyIds);
            return res;
        }
        return 0;
    }
}
