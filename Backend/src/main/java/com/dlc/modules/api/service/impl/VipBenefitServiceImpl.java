package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.dao.VipBenefitMapper;
import com.dlc.modules.api.entity.VipBenefit;
import com.dlc.modules.api.entity.VipBenefitCard;
import com.dlc.modules.api.service.IncomePayDetailService;
import com.dlc.modules.api.service.PayService;
import com.dlc.modules.api.service.VipBenefitService;
import com.dlc.modules.api.service.VipCardService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VIP 权益卡购买/持有 Service 实现(移动端)
 * 落在 api.service.impl,命中事务切面(默认 REQUIRED)。
 */
@Service("vipBenefitService")
public class VipBenefitServiceImpl implements VipBenefitService {

    @Autowired
    private VipBenefitMapper vipBenefitMapper;
    @Autowired
    private VipCardService vipCardService;
    @Autowired
    private PayService payService;
    @Autowired
    private IncomePayDetailService incomePayDetailService;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public Map<String, Object> buy(UserInfoVo user, Long vipCardId) {
        Long userId = user.getUserId();
        // 复用第6步:查上架权益卡 + 后端实时算动态价;不存在/已下架抛 ERROR_VIP_CARD_OFF_SHELF
        VipBenefitCard card = vipCardService.queryVipCardDetail(vipCardId);
        // 应付金额一律后端按当前 sold_count 重算,不信前端传值
        BigDecimal price = card.getCurrentPrice();

        // 订单号末位拼后缀 6(权益卡购买),作回调激活的幂等键
        String orderNo = OrderNoGenerator.getOrderIdByTime() + ConfigConstant.VIP_CARD_BUY_TYPE;

        // 先落待支付权益占位(status=9),支付成功回调才生效
        VipBenefit vb = new VipBenefit();
        vb.setUserId(userId);
        vb.setOriginUserId(userId);
        vb.setVipCardId(vipCardId);
        vb.setSourceOrderNo(orderNo);
        vb.setStoreId(userInfoMapper.queryStoreIdByUserId(userId));
        int nowStoreId = user.getNowStoreId();
        vb.setStoreAddrId(nowStoreId > 0 ? (long) nowStoreId : null);
        vb.setOriginPrice(price);
        vb.setStatus(9);
        vb.setTransferCount(0);
        vb.setTransferable(1);
        vipBenefitMapper.insertSelective(vb);

        // 返回订单号 + 应付金额,前端据此调小程序统一支付(/wx/proPay)调起微信
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("orderNo", orderNo);
        result.put("paySum", price);
        return result;
    }

    @Override
    public int activateByOrderNo(String orderNo, BigDecimal money, String transactionNumber, Integer payType) {
        // 经 vip_benefit 关联取来源权益卡的有效天数
        VipBenefitCard card = vipBenefitMapper.selectCardByOrderNo(orderNo);
        if (card == null) {
            // 找不到对应权益单/卡,异常订单,幂等返回不报错
            return 0;
        }
        Date now = new Date();
        int days = card.getValidityDays() == null ? 0 : card.getValidityDays();
        Date expire = addDays(now, days);

        // 幂等核心:仅 status=9 待支付时才激活;重复回调/并发命中 0 行直接返回,不再记账/计数
        int rows = vipBenefitMapper.activate(orderNo, now, expire);
        if (rows == 0) {
            return 0;
        }
        // 首次激活成功:同一事务内记账(用途自动=6) + 真实购买人数 +1
        // 记账放在激活判定之后,确保重复回调不会重复写流水
        incomePayDetailService.saveIncomePayDetail(orderNo, transactionNumber, money, payType);
        vipBenefitMapper.incrSoldCount(card.getVipCardId());
        return 1;
    }

    @Override
    public PageUtils myBenefits(Map<String, Object> params) {
        Query query = new Query(params);
        List<VipBenefit> list = vipBenefitMapper.selectMyBenefits(query);
        int total = vipBenefitMapper.countMyBenefits(query);
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }

    @Override
    public int activateAttached(String orderNo) {
        //按订单号取加购权益卡的有效天数;普通购卡单无占位,幂等跳过
        VipBenefitCard card = vipBenefitMapper.selectCardByOrderNo(orderNo);
        if (card == null) {
            return 0;
        }
        Date now = new Date();
        int days = card.getValidityDays() == null ? 0 : card.getValidityDays();
        //幂等激活:仅 status=9 待支付命中;重复回调命中0行直接返回,不重复计数
        int rows = vipBenefitMapper.activate(orderNo, now, addDays(now, days));
        if (rows == 0) {
            return 0;
        }
        vipBenefitMapper.incrSoldCount(card.getVipCardId());
        return 1;
    }

    /** 在 date 基础上加 days 天 */
    private Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }
}
