package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtRenewalWarningRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 续费预警记录 Dao（pt_renewal_warning_record，第22步·运营域）。
 * sys 侧只做后台只读列表 + 跟进/续费/忽略处理；扫描写入在 api 侧 RenewalWarningScanDao。
 * 门店隔离：storeIds 为逗号分隔串，空=超管看全部。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtRenewalWarningRecordDao extends BaseDao<PtRenewalWarningRecordEntity> {

    /** 跟进：置 follow_status=1 + 记跟进备注；门店隔离在 SQL 内收口，越权不生效 */
    int follow(@Param("recordId") Long recordId, @Param("followRemark") String followRemark,
               @Param("storeIds") String storeIds);

    /** 标记状态：followStatus 2已续费/3已忽略；写 closed_at=now 关闭记录；门店隔离收口 */
    int markStatus(@Param("recordId") Long recordId, @Param("followStatus") Integer followStatus,
                   @Param("storeIds") String storeIds);
}
