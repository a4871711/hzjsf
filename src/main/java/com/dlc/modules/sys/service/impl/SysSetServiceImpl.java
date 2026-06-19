package com.dlc.modules.sys.service.impl;

import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.dao.SysSetDao;
import com.dlc.modules.sys.entity.SysDataMapEntity;
import com.dlc.modules.sys.service.SysSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class SysSetServiceImpl implements SysSetService {

    @Autowired
    private SysSetDao sysSetDao;

    @Override
    public List<SysDataMapEntity> queryCreditsetList(Map<String, Object> map) {
        return sysSetDao.queryCreditsetList(map);
    }

    @Override
    public int saveCreditset(SysDataMapEntity dataMapEntity) {
        return sysSetDao.saveCreditset(dataMapEntity);
    }

    @Override
    public int updateCreditset(SysDataMapEntity dataMapEntity) {
        return sysSetDao.updateCreditset(dataMapEntity);
    }

    @Override
    public int queryCreditsetTotal(Map<String, Object> map) {
        return sysSetDao.queryCreditsetTotal(map);
    }

    @Override
    public SysDataMapEntity queryCreditsetObject(Long dataMapId) {
        return sysSetDao.queryCreditsetObject(dataMapId);
    }

    @Override
    public List<SysDataMapEntity> queryBraceletsetList(Map<String, Object> map) {
        return sysSetDao.queryBraceletsetList(map);
    }

    @Override
    public int updateBraceletset(SysDataMapEntity dataMapEntity) {
        return sysSetDao.updateBraceletset(dataMapEntity);
    }

    @Override
    public int queryBraceletsetTotal(Map<String, Object> map) {
        return sysSetDao.queryBraceletsetTotal(map);
    }

    @Override
    public SysDataMapEntity queryBraceletsetObject(Long dataMapId) {
        return sysSetDao.queryBraceletsetObject(dataMapId);
    }

    @Override
    public List<SysDataMapEntity> queryMembersetList(Map<String, Object> map) {
        return sysSetDao.queryMembersetList(map);
    }

    @Override
    public int updateMemberset(SysDataMapEntity dataMapEntity) {
        return sysSetDao.updateMemberset(dataMapEntity);
    }

    @Override
    public int queryMembersetTotal(Map<String, Object> map) {
        return sysSetDao.queryMembersetTotal(map);
    }

    @Override
    public SysDataMapEntity queryMembersetObject(Long dataMapId) {
        return sysSetDao.queryMembersetObject(dataMapId);
    }

    @Override
    public List<SysDataMapEntity> queryFeileiList(Map<String, Object> map) {
        return sysSetDao.queryFeileiList(map);
    }

    @Override
    public List<SysDataMapEntity> queryMoney(Map<String, Object> map) {
        return sysSetDao.queryMoney(map);
    }

    @Override
    public List<SysDataMapEntity> queryMembersetRuleList(Query query) {
        return sysSetDao.queryMembersetRuleList(query);
    }

    @Override
    public int queryMembersetRuleTotal(Query query) {
        return sysSetDao.queryMembersetRuleTotal(query);
    }

    @Override
    public int updateMemberRule(SysDataMapEntity dataMapEntity) {
        return sysSetDao.updateMemberRule(dataMapEntity);
    }

    @Override
    public List<SysDataMapEntity> queryClassGradeList(Query query) {
        return sysSetDao.queryClassGradeList(query);
    }

    @Override
    public int queryClassGradeListTotal(Query query) {
        return sysSetDao.queryClassGradeListTotal(query);
    }

    @Override
    public int updateClassGradeUpdate(SysDataMapEntity dataMapEntity) {
        return sysSetDao.updateClassGradeUpdate(dataMapEntity);
    }

    @Override
    public int saveClassGrade(SysDataMapEntity dataMapEntity) {
        int res = 0;
        if(null != dataMapEntity){
            dataMapEntity.setIdxLabel(2L);
            dataMapEntity.setNextLabel(3L);
            res=sysSetDao.saveClassGrade(dataMapEntity);
        }
        return res;
    }

    @Override
    public int classGradeDelete(Long ids) {
        return sysSetDao.deleteGrade(ids);
    }

    @Override
    public List<SysDataMapEntity> queryClassClassifyList(Query query) {
        return sysSetDao.queryClassClassifyList(query);
    }

    @Override
    public int queryClassClassifyListTotal(Query query) {
        return sysSetDao.queryClassClassifyListTotal(query);
    }

    @Override
    public int saveClassClassify(SysDataMapEntity dataMapEntity) {
        int res = 0;
        if(null != dataMapEntity) {
            //查询当前最大编号
            Integer numb = sysSetDao.queryMaxPrice();
            if(numb == null){
                numb = 1;
            }else{
                numb = numb + 1;//往下编号
            }
            dataMapEntity.setIdxLabel(2L);
            dataMapEntity.setNextLabel(4L);
            dataMapEntity.setPrice(BigDecimal.valueOf(numb));
            res = sysSetDao.saveClassClassify(dataMapEntity);
        }
        return res;
    }

    @Override
    public int updateClassClassify(SysDataMapEntity dataMapEntity) {
        return sysSetDao.updateClassClassify(dataMapEntity);
    }

    @Override
    public Integer querySameGrade(BigDecimal price) {
        return sysSetDao.querySameGrade(price);
    }

    @Override
    public Long queryMaxLableNext() {
        return sysSetDao.queryMaxLableNext();
    }


    @Override
    public int deleteBatch(Long[] ids) {
        return sysSetDao.deleteBatch(ids);
    }


}
