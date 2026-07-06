package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtExceptionWarningRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 异常预警记录 Dao（pt_exception_warning_record，第22步·运营域）。
 * sys 侧只做后台只读列表 + 顶部统计卡片；写入在 api 侧 ExceptionWarningScanDao（§18.4 不做处理态流转）。
 * 门店隔离：storeIds 为逗号分隔串，空=超管看全部。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtExceptionWarningRecordDao extends BaseDao<PtExceptionWarningRecordEntity> {

    /** 顶部统计卡片：todayNew/frequentCancel/lessonAbnormal/enabledRuleCount（门店隔离随 map） */
    Map<String, Object> queryStat(Map<String, Object> params);
}
