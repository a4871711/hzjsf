package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.ApiFlashSaleDao;
import com.dlc.modules.api.dao.DeviceMapper;
import com.dlc.modules.api.dao.StoreMapper;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.dao.VipBenefitMapper;
import com.dlc.modules.api.entity.Device;
import com.dlc.modules.api.entity.VipBenefit;
import com.dlc.modules.api.entity.VipBenefitCard;
import com.dlc.modules.api.service.IncomePayDetailService;
import com.dlc.modules.api.service.PayService;
import com.dlc.modules.api.service.VipBenefitService;
import com.dlc.modules.api.service.VipCardService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private StoreMapper storeMapper;
    @Autowired
    private ApiFlashSaleDao apiFlashSaleDao;
    @Autowired
    private com.dlc.modules.api.service.ApiFlashSaleService apiFlashSaleService;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Map<String, Object> buy(UserInfoVo user, Long vipCardId, Long storeId, Long storeAddrId, Long flashSaleActivityId) {
        Long userId = user.getUserId();
        // 持有未过期有效权益(status=0且未过期)时禁止重复购买
        if (vipBenefitMapper.countValidByUser(userId) > 0) {
            throw new RRException(CodeAndMsg.ERROR_VIP_BENEFIT_ALREADY_HELD);
        }
        // 复用第6步:查上架权益卡 + 后端实时算动态价;不存在/已下架抛 ERROR_VIP_CARD_OFF_SHELF
        VipBenefitCard card = vipCardService.queryVipCardDetail(vipCardId);
        // 应付金额一律后端按当前 sold_count 重算,不信前端传值
        BigDecimal price = card.getCurrentPrice();

        // ===== 限时秒杀：命中活动+商品且当前可抢则用秒杀价成交(暂停动态涨价)，秒杀限购与"一人一卡"叠加 =====
        Long flashActivityId = null;
        if (flashSaleActivityId != null) {
            // 校验活动+商品当前可抢(时段/库存)，不可抢由 service 抛业务错
            Map<String, Object> ap = apiFlashSaleService.checkBuyable(flashSaleActivityId, vipCardId);
            if (toInt(ap.get("bizType")) != 3) {
                throw new RRException("该秒杀活动不是权益卡");
            }
            Object plimit = ap.get("purchaseLimit");
            if (plimit != null && String.valueOf(plimit).trim().length() > 0) {
                int bought = apiFlashSaleDao.countMemberBenefitFlashOrders(userId, flashSaleActivityId, vipCardId);
                if (bought >= Integer.parseInt(String.valueOf(plimit))) {
                    throw new RRException("已达该秒杀每人限购上限");
                }
            }
            price = new BigDecimal(String.valueOf(ap.get("flashSalePrice")));
            flashActivityId = flashSaleActivityId;
        }

        // 订单号末位拼后缀 6(权益卡购买),作回调激活的幂等键
        String orderNo = OrderNoGenerator.getOrderIdByTime() + ConfigConstant.VIP_CARD_BUY_TYPE;

        // 先落待支付权益占位(status=9),支付成功回调才生效
        VipBenefit vb = new VipBenefit();
        vb.setUserId(userId);
        vb.setOriginUserId(userId);
        vb.setVipCardId(vipCardId);
        vb.setSourceOrderNo(orderNo);
        // 门店归属:优先前端随单传的"购买时所在门店"(小程序首页/详情页的当前定位门店),但须校验
        // 该 store_id 真实存在——它是后台"权益卡购买记录"按门店做数据权限过滤的字段
        // (SysVipCardOrderDao.Filter: AND b.store_id IN (#{storeIds})),不可无脑信任客户端传值,
        // 否则恶意请求可传任意/不存在的门店 id 把购买记录伪装成别的门店、或让记录从所有门店视图消失。
        // 校验不通过或未传:回退老口径(会员卡归属门店,无会员卡则 NULL)
        Long verifiedStoreId = (storeId != null && storeMapper.selectByPrimaryKey(storeId) != null)
                ? storeId : null;
        vb.setStoreId(verifiedStoreId != null ? verifiedStoreId : userInfoMapper.queryStoreIdByUserId(userId));
        if (storeAddrId != null) {
            vb.setStoreAddrId(storeAddrId);
        } else {
            int nowStoreId = user.getNowStoreId();
            vb.setStoreAddrId(nowStoreId > 0 ? (long) nowStoreId : null);
        }
        vb.setOriginPrice(price);
        // 下单即快照会员卡剩余整天数(此刻会员卡未被本单续期),激活据此顺延生效/到期;无有效期为0=立即生效
        vb.setDeferDays(remainDaysOfMembership(userId));
        vb.setStatus(9);
        vb.setTransferCount(0);
        vb.setTransferable(1);
        vb.setFlashSaleActivityId(flashActivityId);
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
        // 支付回调时间可能带任意时分秒，先归一为服务端所在时区的购买日零点，后续只按自然日计算。
        Date purchaseDay = startOfDay(new Date());
        int days = card.getValidityDays() == null ? 0 : card.getValidityDays();
        // 权益有效期只按自然日计算：无旧卡时购买日生效；有旧卡时从旧卡到期日次日生效。
        // 到期日包含在有效期内，例如360天权益的到期日=start+359天，落库为当天23:59:59。
        int defer = deferDaysByOrderNo(orderNo);
        // start/expire 最终写入 vip_benefit.start_time、expire_time；defer 是下单时保存的旧会员卡占用自然日数快照。
        Date start = addDays(purchaseDay, defer);
        Date expire = days <= 0 ? start : endOfDay(addDays(start, days - 1));

        // 幂等核心:仅 status=9 待支付时才激活;重复回调/并发命中 0 行直接返回,不再记账/计数
        int rows = vipBenefitMapper.activate(orderNo, start, expire);
        if (rows == 0) {
            return 0;
        }
        // 首次激活成功:同一事务内记账(用途自动=6) + 真实购买人数 +1
        // 记账放在激活判定之后,确保重复回调不会重复写流水
        incomePayDetailService.saveIncomePayDetail(orderNo, transactionNumber, money, payType);
        vipBenefitMapper.incrSoldCount(card.getVipCardId());
        // 限时秒杀权益卡：激活成功后 CAS 扣减秒杀库存(幂等由上面 activate 仅 status=9 命中保证,只执行一次)
        VipBenefit boughtVb = vipBenefitMapper.selectByOrderNo(orderNo);
        if (boughtVb != null && boughtVb.getFlashSaleActivityId() != null) {
            int dec = apiFlashSaleService.increaseSold(boughtVb.getFlashSaleActivityId(), boughtVb.getVipCardId(), boughtVb.getCreatedDate());
            if (dec <= 0) {
                log.warn("权益卡秒杀库存扣减失败(已售罄/活动失效) orderNo={}, activityId={}", orderNo, boughtVb.getFlashSaleActivityId());
            }
        }
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
    public boolean hasValidBenefit(Long userId) {
        // 收口到 latestValidBenefit 同一 SQL 出口,避免"有效权益"判定口径分叉
        return latestValidBenefit(userId) != null;
    }

    @Override
    public boolean hasValidBenefitForFitCard(Long userId, Long fitCardId) {
        if (userId == null || fitCardId == null) {
            return false;
        }
        // 权益类型会员卡只能由明确绑定它的有效权益解锁，不能用其它权益或空配置兜底。
        return vipBenefitMapper.countValidForFitCard(userId, fitCardId) > 0;
    }

    @Override
    public VipBenefit latestValidBenefit(Long userId) {
        return userId == null ? null : vipBenefitMapper.selectLatestValidByUser(userId);
    }

    /** 宽松取整：null/非数字返回 0（用于秒杀活动 map 字段解析） */
    private int toInt(Object v) {
        if (v == null) {
            return 0;
        }
        try {
            return Integer.parseInt(String.valueOf(v).trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** 在 date 基础上加 days 天 */
    private Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }

    /**
     * 归一到服务端默认时区所在自然日的 0 点。
     * 项目数据库和旧逻辑都使用 {@link Date}，这里沿用服务端时区，避免支付回调时分秒影响权益生效日。
     */
    private Date startOfDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /** 归一到自然日 23:59:59.999，使 expire_time 所在当天完整计入权益有效期。 */
    private Date endOfDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    /** 按订单号取下单时快照的顺延天数(null/无记录记 0) */
    private int deferDaysByOrderNo(String orderNo) {
        VipBenefit vb = vipBenefitMapper.selectByOrderNo(orderNo);
        if (vb == null || vb.getDeferDays() == null) {
            return 0;
        }
        return vb.getDeferDays() < 0 ? 0 : vb.getDeferDays();
    }

    /**
     * 计算当前会员卡仍占用的自然日数，并在购买权益时保存为 defer_days 快照。
     *
     * <p>无有效会员卡或会员卡已过期时返回 0，权益从购买日生效；否则首尾日期都计入。
     * 例如今天是 8 月 19 日，会员卡 8 月 20 日到期，占用 19、20 两个自然日，返回 2，
     * 新权益从 8 月 21 日开始，避免与旧会员卡有效期重叠。</p>
     */
    private int remainDaysOfMembership(Long userId) {
        Device dev = deviceMapper.selectUserValidity(userId);
        if (dev == null || dev.getValidityDate() == null) {
            return 0;
        }
        long todayMs = truncateToDay(new Date());
        long expireMs = truncateToDay(dev.getValidityDate());
        long diffDays = (expireMs - todayMs) / (24L * 60 * 60 * 1000);
        // diffDays 是两个零点之间的间隔；加 1 才能把今天这个仍可使用的自然日包含进来。
        return diffDays >= 0 ? (int) diffDays + 1 : 0;
    }

    /** 截断到当天 0 点的毫秒值(忽略时分秒) */
    private long truncateToDay(Date date) {
        return startOfDay(date).getTime();
    }
}
