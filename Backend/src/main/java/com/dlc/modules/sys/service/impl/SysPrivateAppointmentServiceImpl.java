package com.dlc.modules.sys.service.impl;

import com.dlc.modules.api.service.PrivateAppointmentService;
import com.dlc.modules.sys.dao.SysPrivateAppointmentDao;
import com.dlc.modules.sys.service.SysPrivateAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 私教预约记录后台 Service 实现。
 * 落在 sys.service.impl 命中事务切面(REQUIRED);finish/cancel/coachBook 委托 api 侧
 * PrivateAppointmentService(第14步),后者加入同一事务,预约状态机与课时账本联动同事务收口。
 * 注意:委托链路涉及 api 包代码,api 侧改动须重启;本页 sys XML 热刷新免重启。
 */
@Service("sysPrivateAppointmentService")
public class SysPrivateAppointmentServiceImpl implements SysPrivateAppointmentService {

    @Autowired
    private SysPrivateAppointmentDao sysPrivateAppointmentDao;
    @Autowired
    private PrivateAppointmentService privateAppointmentService;

    @Override
    public List<Map<String, Object>> queryList(Map<String, Object> params) {
        return sysPrivateAppointmentDao.queryList(params);
    }

    @Override
    public int queryTotal(Map<String, Object> params) {
        return sysPrivateAppointmentDao.queryTotal(params);
    }

    @Override
    public Map<String, Object> queryStat(Map<String, Object> params) {
        return sysPrivateAppointmentDao.queryStat(params);
    }

    @Override
    public Map<String, Object> queryDetail(Long id, String storeIds) {
        return sysPrivateAppointmentDao.queryDetail(id, storeIds);
    }

    @Override
    public boolean existsInScope(Long id, String storeIds) {
        return sysPrivateAppointmentDao.countInScope(id, storeIds) > 0;
    }

    @Override
    public boolean coachInScope(Long coachId, String storeIds) {
        return sysPrivateAppointmentDao.countCoachInScope(coachId, storeIds) > 0;
    }

    @Override
    public void finish(Long appointmentId, Long operatorId) {
        privateAppointmentService.finishAppointment(appointmentId, operatorId);
    }

    @Override
    public void cancel(Long appointmentId, String cancelReason, Long operatorId) {
        privateAppointmentService.cancelByAdmin(appointmentId, cancelReason, operatorId);
    }

    @Override
    public Map<String, Object> coachBook(Long coachId, Long memberId, Long productId,
                                         String appointmentDate, String startTime, String endTime, Long operatorId) {
        return privateAppointmentService.coachBook(coachId, memberId, productId,
                appointmentDate, startTime, endTime, operatorId);
    }
}
