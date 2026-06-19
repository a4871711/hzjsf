package com.dlc.modules.sys.service;

import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.SysDataMapEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public interface SysSetService {
    //查询积分兑换规则
    List<SysDataMapEntity> queryCreditsetList(Map<String, Object> map);

    //新增积分
    int saveCreditset(SysDataMapEntity dataMapEntity);

    //修改积分
    int updateCreditset(SysDataMapEntity dataMapEntity);

    //积分总数
    int queryCreditsetTotal(Map<String, Object> map);

    //查询某个积分
    SysDataMapEntity queryCreditsetObject(Long dataMapId);

    //查询所有手环
    List<SysDataMapEntity> queryBraceletsetList(Map<String, Object> map);

    //修改手环
    int updateBraceletset(SysDataMapEntity dataMapEntity);

    //手环总数
    int queryBraceletsetTotal(Map<String, Object> map);

    //查询某个手环
    SysDataMapEntity queryBraceletsetObject(Long dataMapId);

    //查询所有会员设置
    List<SysDataMapEntity> queryMembersetList(Map<String, Object> map);

    //修改会员设置
    int updateMemberset(SysDataMapEntity dataMapEntity);

    //会员设置总数
    int queryMembersetTotal(Map<String, Object> map);

    //查询某个会员设置
    SysDataMapEntity queryMembersetObject(Long dataMapId);
    List<SysDataMapEntity> queryFeileiList(Map<String, Object> map);
    List<SysDataMapEntity> queryMoney(Map<String, Object> map);

    List<SysDataMapEntity> queryMembersetRuleList(Query query);

    int queryMembersetRuleTotal(Query query);

    int updateMemberRule(SysDataMapEntity dataMapEntity);

    List<SysDataMapEntity> queryClassGradeList(Query query);

    int queryClassGradeListTotal(Query query);

    int updateClassGradeUpdate(SysDataMapEntity dataMapEntity);

    int saveClassGrade(SysDataMapEntity dataMapEntity);


    int classGradeDelete(Long ids);

    List<SysDataMapEntity> queryClassClassifyList(Query query);

    int queryClassClassifyListTotal(Query query);

    int saveClassClassify(SysDataMapEntity dataMapEntity);

    int updateClassClassify(SysDataMapEntity dataMapEntity);

    Integer querySameGrade(BigDecimal price);
    //查询最大举报类型+1
    Long queryMaxLableNext();


    int deleteBatch(Long[] ids);


}
