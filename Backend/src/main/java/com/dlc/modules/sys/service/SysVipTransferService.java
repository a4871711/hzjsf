package com.dlc.modules.sys.service;

import java.util.List;
import java.util.Map;

/**
 * VIP 权益转让审核工作台后台服务(只读列表)。
 * 审核动作(通过/驳回+退费+推送)不在此,直接复用 api 的 VipTransferService.audit(单事务)。
 *
 * @date 2026-07-02
 */
public interface SysVipTransferService {

    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);
}
