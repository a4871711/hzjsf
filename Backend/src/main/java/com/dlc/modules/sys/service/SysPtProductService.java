package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.PtProductEntity;
import com.dlc.modules.sys.entity.TeamClassEntity;

import java.util.List;
import java.util.Map;

/**
 * 私教商品 Service。一次事务内维护主表 + 适用门店/指定教练关联 + 分期规则 + 附赠团课权益规则。
 *
 * @author claude
 */
public interface SysPtProductService {

    PtProductEntity queryObject(Long id);

    List<PtProductEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(PtProductEntity entity);

    void update(PtProductEntity entity);

    void deleteBatch(Long[] ids);

    /** 上架：先执行 checkCanList 全部校验通过才置 listing_status=1 */
    void onCard(Long[] ids);

    void offCard(Long[] ids);

    /** 深拷贝主表 + 5 子表，新 product_no、listing_status=0、sold_count=0、名称加「-副本」 */
    void copy(Long id);

    /** 供商品表单选择的团课商品下拉（最佳可用实现：读现有 team_class） */
    List<TeamClassEntity> groupClassOptions();
}
