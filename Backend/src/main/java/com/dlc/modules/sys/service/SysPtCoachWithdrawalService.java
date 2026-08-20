package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.PtCoachWithdrawalReviewForm;

import java.util.List;
import java.util.Map;

/** 私教教练提现后台审核。 */
public interface SysPtCoachWithdrawalService {
    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    Map<String, Object> queryObject(Long id, String storeIds);

    void review(PtCoachWithdrawalReviewForm form, Long operatorId, String storeIds);
}
