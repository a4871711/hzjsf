package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.PtProductApiDao;
import com.dlc.modules.api.entity.PtProduct;
import com.dlc.modules.api.service.PtProductApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 私教商品会员端只读浏览 Service 实现
 *
 * @author claude
 */
@Service("ptProductApiService")
public class PtProductApiServiceImpl implements PtProductApiService {

    @Autowired
    private PtProductApiDao ptProductApiDao;

    @Override
    public PtProduct queryObject(Long id) {
        return ptProductApiDao.queryObject(id);
    }

    @Override
    public List<PtProduct> queryList(Query query) {
        return ptProductApiDao.queryList(query);
    }

    @Override
    public int queryTotal(Query query) {
        return ptProductApiDao.queryTotal(query);
    }
}
