package com.dlc.modules.sys.service.impl;

import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.DeviceMapper;
import com.dlc.modules.api.dao.IncomePayDetailMapper;
import com.dlc.modules.api.dao.UserWalletMapper;
import com.dlc.modules.api.entity.IncomePayDetail;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.sys.dao.StoreGoodsOrderMapper;
import com.dlc.modules.sys.dao.SysWalletDetailDao;
import com.dlc.modules.sys.entity.WalletDetailEntity;
import com.dlc.modules.sys.service.SysStoreGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.dlc.modules.sys.dao.SysStoreGoodsDao;
import com.dlc.modules.sys.entity.SysStoreGoodsEntity;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service("storeGoodsService")
public class SysStoreGoodsServiceImpl  implements SysStoreGoodsService {

    @Autowired
    private SysStoreGoodsDao sysStoreGoodsDao;
    @Autowired
    private SysWalletDetailDao sysWalletDetailDao;
    @Autowired
    private IncomePayDetailMapper incomePayDetailMapper;
    @Autowired
    private UserWalletMapper userWalletMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private StoreGoodsOrderMapper storeGoodsOrderMapper;

    @Override
    public SysStoreGoodsEntity queryObject(Long id) {
        return sysStoreGoodsDao.queryObject(id);
    }

    @Override
    public List<SysStoreGoodsEntity> queryList(Map<String, Object> map) {
        return sysStoreGoodsDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysStoreGoodsDao.queryTotal(map);
    }

    @Override
    public void save(SysStoreGoodsEntity sysStoreGoodsEntity) {
        sysStoreGoodsDao.save(sysStoreGoodsEntity);
    }

    @Override
    public void update(SysStoreGoodsEntity sysStoreGoodsEntity) {
        sysStoreGoodsDao.update(sysStoreGoodsEntity);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        sysStoreGoodsDao.deleteBatch(ids);
    }

    @Override
    public Integer queryCountByBarCode(Map<String, Object> params) {
        return sysStoreGoodsDao.queryCountByBarCode(params);
    }

    @Override
    public SysStoreGoodsEntity queryGoodsByBarCode(String barCode) {
        return sysStoreGoodsDao.queryGoodsByBarCode(barCode);
    }

    @Override
    public R goodsAccounts(Map<String, Object> params, UserInfo userInfo, BigDecimal userWallet) {
        try {
            String orderNo = OrderNoGenerator.getOrderIdByTime();
            //保存钱包明细记录
            WalletDetailEntity walletDetailEntity = new WalletDetailEntity();
            walletDetailEntity.setUserId(Long.parseLong(params.get("userId").toString()));
            //明细类型（1.(用户) 提现 2.（教练）提现 3.(用户）充值）4.（用户）门店商品消费',
            walletDetailEntity.setType(4);
            walletDetailEntity.setMoney(Double.valueOf(params.get("totalMoney").toString()));
            walletDetailEntity.setTransactionTime(new Date());
            walletDetailEntity.setCreateTime(new Date());
            walletDetailEntity.setStatus(3);
            walletDetailEntity.setOrderNo(orderNo);
            int res = sysWalletDetailDao.save(walletDetailEntity);
            int res2 = 0;
            if(res > 0){
                //保存收支明细记录
                IncomePayDetail incomePayDetail = new IncomePayDetail();
                incomePayDetail.setUserId(Long.parseLong(params.get("userId").toString()));
                incomePayDetail.setStoreId(Long.parseLong(params.get("storeId").toString()));
                incomePayDetail.setOrderNo(orderNo);
                incomePayDetail.setMoney(new BigDecimal(params.get("totalMoney").toString()));
                //支付用途（1 提现 2.会员卡购买 3.商城购买 4.私教课购买 5.团体课购买 6.充值 7.门店消费 8.积分兑换 9.商城退款 10.其他 11.提现失败退款）',
                incomePayDetail.setPayType(7);
                incomePayDetail.setTradeDate(new Date());
                incomePayDetail.setTradeType(1); //余额支付
                incomePayDetail.setTradeStatus(3);
                res2 = incomePayDetailMapper.insertSelective(incomePayDetail);
            }
            int res3 = 0;
            if(res2 > 0){
                //扣减用户钱包
                BigDecimal newUserWallet = userWallet.subtract(new BigDecimal(params.get("totalMoney").toString()));
                //更新用户钱包
                res3 = userWalletMapper.updateUserWallet(newUserWallet,userInfo.getUserId());
            }
            //保存订单数据，更新商品信息
            updateGoodsOrder(params, userInfo.getUserId(), orderNo);
            if (res3 > 0) {
                return R.ok();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return R.error("结算失败");
    }

    @Override
    public int queryIsForbiddenStatus(Long userId, String wristId) {
        return deviceMapper.queryDeviceIsForbidden(userId, wristId);
    }

    private void updateGoodsOrder(Map<String, Object> params, Long userId, String orderNo) {
        try {
            List<Map<String, Object>> goodsOrderList = (List<Map<String, Object>>) params.get("orderList");
            if(CollectionUtils.isEmpty(goodsOrderList)){
                return;
            }
            for(Map<String, Object> mm : goodsOrderList){
                 mm.put("userId", userId);
                 mm.put("orderNo", orderNo);
                 mm.put("storeId", params.get("storeId"));
            }
            storeGoodsOrderMapper.batchInsertGoodsOrder(goodsOrderList);
            sysStoreGoodsDao.batchUpdateStoreGoodsNum(goodsOrderList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
