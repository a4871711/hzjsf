package com.dlc.modules.sys.service;

import java.util.List;
import java.util.Map;

/**
 * 会员黑名单后台管理服务。
 *
 * @date 2026-07-03
 */
public interface SysMemberBlacklistService {

    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    /**
     * 按手机号拉黑会员。
     *
     * @return null 表示成功,否则返回中文错误提示
     */
    String blacklist(String phone, String reason, String operator);

    /** 解除黑名单(软删) */
    void release(Long id);
}
