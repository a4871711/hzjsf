package com.dlc.modules.sys.service;

import java.util.List;
import java.util.Map;

/**
 * 停卡记录后台服务(只读列表)。
 * 停卡的写入(申请/恢复/取消)在移动端 api 侧,此处只提供后台查询。
 *
 * @date 2026-07-03
 */
public interface SysCardPauseService {

    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);
}
