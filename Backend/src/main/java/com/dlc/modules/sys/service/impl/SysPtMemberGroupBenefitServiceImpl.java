package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.PtMemberGroupBenefitDao;
import com.dlc.modules.sys.dao.PtMemberGroupBenefitFlowDao;
import com.dlc.modules.sys.entity.PtMemberGroupBenefitEntity;
import com.dlc.modules.sys.entity.PtMemberGroupBenefitFlowEntity;
import com.dlc.modules.sys.service.SysPtMemberGroupBenefitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 会员附赠团课权益 Service 实现（本域只读）。
 *
 * @author claude
 */
@Service("sysPtMemberGroupBenefitService")
public class SysPtMemberGroupBenefitServiceImpl implements SysPtMemberGroupBenefitService {

    @Autowired
    private PtMemberGroupBenefitDao ptMemberGroupBenefitDao;
    @Autowired
    private PtMemberGroupBenefitFlowDao ptMemberGroupBenefitFlowDao;

    @Override
    public PtMemberGroupBenefitEntity queryObject(Long id) {
        return ptMemberGroupBenefitDao.queryObject(id);
    }

    @Override
    public List<PtMemberGroupBenefitEntity> queryList(Map<String, Object> map) {
        return ptMemberGroupBenefitDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return ptMemberGroupBenefitDao.queryTotal(map);
    }

    @Override
    public List<PtMemberGroupBenefitFlowEntity> queryFlow(Long benefitId) {
        return ptMemberGroupBenefitFlowDao.queryByBenefitId(benefitId);
    }
}
