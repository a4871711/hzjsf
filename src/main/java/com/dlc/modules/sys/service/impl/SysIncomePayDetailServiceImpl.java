package com.dlc.modules.sys.service.impl;

import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.IncomePayDetailMapper;
import com.dlc.modules.api.entity.IncomePayDetail;
import com.dlc.modules.sys.service.SysIncomePayDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.dlc.modules.sys.dao.SysIncomePayDetailDao;
import com.dlc.modules.sys.entity.SysIncomePayDetailEntity;
import java.util.List;

@Service("SysIncomePayDetailService")
public class SysIncomePayDetailServiceImpl  implements SysIncomePayDetailService {

    @Autowired
    private SysIncomePayDetailDao sysIncomePayDetailDao;
    @Autowired
    private IncomePayDetailMapper incomePayDetailMapper;

    @Override
    public SysIncomePayDetailEntity queryObject(Long id) {
        return sysIncomePayDetailDao.queryObject(id);
    }

    @Override
    public List<SysIncomePayDetailEntity> queryList(Map<String, Object> map) {
        return sysIncomePayDetailDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysIncomePayDetailDao.queryTotal(map);
    }

    @Override
    public void save(SysIncomePayDetailEntity sysIncomePayDetailEntity) {
        sysIncomePayDetailDao.save(sysIncomePayDetailEntity);
    }

    @Override
    public void update(SysIncomePayDetailEntity sysIncomePayDetailEntity) {
        sysIncomePayDetailDao.update(sysIncomePayDetailEntity);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        sysIncomePayDetailDao.deleteBatch(ids);
    }

    @Override
    public String countMoney(Map<String, Object> params) {
        return sysIncomePayDetailDao.countMoney();
    }

    @Override
    public int saveIncomePayDetail(Map<String, Object> params) {
        //params
        //sysIncomePayDetailDao.save()
        IncomePayDetail incomePayDetail = new IncomePayDetail();
        incomePayDetail.setUserId(Long.parseLong(params.get("userId").toString()));
        incomePayDetail.setStoreId(Long.parseLong(params.get("storeId").toString()));
        incomePayDetail.setOrderNo(OrderNoGenerator.getOrderIdByTime());
        incomePayDetail.setMoney(new BigDecimal(params.get("totalMoney").toString()));
        //支付用途（1 提现 2.会员卡购买 3.商城购买 4.私教课购买 5.团体课购买 6.充值 7.门店消费 8.积分兑换 9.商城退款 10.其他 11.提现失败退款）',
        incomePayDetail.setPayType(7);
        incomePayDetail.setTradeDate(new Date());
        incomePayDetail.setTradeStatus(3);
        int i = incomePayDetailMapper.insertSelective(incomePayDetail);
        return i;
    }

    @Override
    public int queryStoreTotal(Query query) {
        return sysIncomePayDetailDao.queryStoredTotal(query);
    }

    @Override
    public List<SysIncomePayDetailEntity> queryStoredList(Map<String, Object> query) {
        return sysIncomePayDetailDao.queryStoredList(query);
    }

    @Override
    public String storedCountMoney(Map<String, Object> params) {
        return sysIncomePayDetailDao.storedCountMoney(params);
    }

    @Override
    public List<Map<String, Object>> orderInfo(Long id) {
        return sysIncomePayDetailDao.queryOrderInfo(id);
    }

    @Override
    public List<SysIncomePayDetailEntity> queryAllStoredList(Query query) {
        return sysIncomePayDetailDao.queryAllStoredList(query);
    }

    @Override
    public int queryAllStoreTotal(Query query) {
        return sysIncomePayDetailDao.queryAllStoredTotal(query);
    }

    @Override
    public List<Map<String,Object>> queryTransList(Query query) {
        return sysIncomePayDetailDao.selectTransList(query);
    }

    @Override
    public int queryTransTotal(Query query) {
        return sysIncomePayDetailDao.selectTransListTotal(query);
    }

    @Override
    public int updateTradeStatus(Long incomePayDetailId, int tradeStatus){
        return sysIncomePayDetailDao.updateTradeStatus(incomePayDetailId, tradeStatus);
    }

    @Override
    public boolean hasValidCardPurchase(Long userId) {
        return userId != null && sysIncomePayDetailDao.countValidCardPurchaseByUserId(userId) > 0;
    }

}
