package com.dlc.modules.sys.service;

import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.SysIncomePayDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 收支明细表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-11 15:33:19
 */
public interface SysIncomePayDetailService {

    SysIncomePayDetailEntity queryObject(Long id);

    List<SysIncomePayDetailEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(SysIncomePayDetailEntity sysIncomePayDetailEntity);

    void update(SysIncomePayDetailEntity sysIncomePayDetailEntity);

    void deleteBatch(Long[] ids);

    String countMoney(Map<String, Object> params);

    int saveIncomePayDetail(Map<String, Object> params);

    int queryStoreTotal(Query query);

    List<SysIncomePayDetailEntity> queryStoredList(Map<String, Object> query);
    String storedCountMoney(Map<String, Object> params);

    List<Map<String, Object>> orderInfo(Long id);

    List<SysIncomePayDetailEntity> queryAllStoredList(Query query);

    int queryAllStoreTotal(Query query);

    List<Map<String,Object>> queryTransList(Query query);

    int queryTransTotal(Query query);

    int updateTradeStatus(Long incomePayDetailId, int tradeStatus);

    /** 是否存在有效购卡记录（与 sys-incomepaydetail 列表口径一致） */
    boolean hasValidCardPurchase(Long userId);

}

