package com.dlc.modules.sys.service;

import java.util.List;
import java.util.Map;

/**
 * VIP 权益卡购买记录后台服务(只读列表)。
 * 购买/激活的写入在移动端 api 侧(VipBenefitService),此处只提供后台查询。
 *
 * @date 2026-07-03
 */
public interface SysVipCardOrderService {

    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    /** 导出用:与列表同条件但不分页返回全部(字段已转导出文本) */
    List<Map<String, Object>> queryExportList(Map<String, Object> params);
}
