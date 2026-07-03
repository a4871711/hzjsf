package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysMemberBlacklistDao;
import com.dlc.modules.sys.entity.MemberBlacklistEntity;
import com.dlc.modules.sys.service.SysMemberBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 会员黑名单后台管理服务实现。
 *
 * @date 2026-07-03
 */
@Service("sysMemberBlacklistService")
public class SysMemberBlacklistServiceImpl implements SysMemberBlacklistService {

    @Autowired
    private SysMemberBlacklistDao sysMemberBlacklistDao;

    @Override
    public List<Map<String, Object>> queryList(Map<String, Object> params) {
        return sysMemberBlacklistDao.queryList(params);
    }

    @Override
    public int queryTotal(Map<String, Object> params) {
        return sysMemberBlacklistDao.queryTotal(params);
    }

    @Override
    public String blacklist(String phone, String reason, String operator) {
        Long userId = sysMemberBlacklistDao.findUserIdByPhone(phone.trim());
        if (userId == null) {
            return "该手机号未找到会员";
        }
        if (sysMemberBlacklistDao.countActiveByUserId(userId) > 0) {
            return "该会员已在黑名单中";
        }
        MemberBlacklistEntity entity = new MemberBlacklistEntity();
        entity.setUserId(userId);
        entity.setReason(reason);
        entity.setOperator(operator);
        entity.setStatus(1);
        entity.setCreatedDate(new Date());
        sysMemberBlacklistDao.save(entity);
        return null;
    }

    @Override
    public void release(Long id) {
        sysMemberBlacklistDao.release(id);
    }
}
