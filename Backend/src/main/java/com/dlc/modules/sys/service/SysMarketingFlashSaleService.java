package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.MkFlashSaleActivityEntity;

import java.util.List;
import java.util.Map;

/**
 * 限时秒杀活动 Service（mk_flash_sale_activity）
 *
 * @author claude
 */
public interface SysMarketingFlashSaleService {

    MkFlashSaleActivityEntity queryObject(Long id);

    List<MkFlashSaleActivityEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(MkFlashSaleActivityEntity entity);

    void update(MkFlashSaleActivityEntity entity);

    /** 逻辑删除；进行中活动（时间窗内且 status=1）禁删，需先下架 */
    void deleteBatch(Long[] ids);

    /** 上下架切换：1上架/0下架。下架后不可继续购买 */
    void changeStatus(Long id, Integer status, Long userId);
}
