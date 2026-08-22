package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.modules.api.dao.PtPrivateOrderDao;
import com.dlc.modules.api.entity.PtPrivateOrderEntity;
import com.dlc.modules.api.entity.PtProduct;
import com.dlc.modules.api.service.IncomePayDetailService;
import com.dlc.modules.api.service.MemberPrivateBenefitService;
import com.dlc.modules.api.service.PrivateOrderService;
import com.dlc.modules.sys.dao.PtCoachMonthlyCommissionRuleDao;
import com.dlc.modules.sys.dao.SysPrivateOrderDao;
import com.dlc.modules.sys.entity.PtCoachFeeRuleEntity;
import com.dlc.modules.sys.entity.SysCoachTradeDetailEntity;
import com.dlc.modules.sys.service.SysCoachFeeRuleService;
import com.dlc.modules.sys.service.SysCoachTradeDetailService;
import com.dlc.modules.sys.service.SysPrivateOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 私教购买记录后台 Service 实现。
 * createManual/deleteCascade 显式声明事务，保证订单、权益、流水和计数要么全部成功，要么全部回滚。
 * refund 继续委托 api 侧 PrivateOrderService.refund 并加入同一事务。
 */
@Service("sysPrivateOrderService")
public class SysPrivateOrderServiceImpl implements SysPrivateOrderService {

    private static final Logger log = LoggerFactory.getLogger(SysPrivateOrderServiceImpl.class);
    private static final BigDecimal MAX_MANUAL_AMOUNT = new BigDecimal("99999999.99");

    @Autowired
    private SysPrivateOrderDao sysPrivateOrderDao;
    @Autowired
    private PrivateOrderService privateOrderService;
    @Autowired
    private PtPrivateOrderDao ptPrivateOrderDao;
    @Autowired
    private MemberPrivateBenefitService memberPrivateBenefitService;
    @Autowired
    private IncomePayDetailService incomePayDetailService;
    @Autowired
    private SysCoachFeeRuleService sysCoachFeeRuleService;
    @Autowired
    private SysCoachTradeDetailService sysCoachTradeDetailService;
    @Autowired
    private PtCoachMonthlyCommissionRuleDao ptCoachMonthlyCommissionRuleDao;

    @Override
    public List<Map<String, Object>> queryList(Map<String, Object> params) {
        return sysPrivateOrderDao.queryList(params);
    }

    @Override
    public int queryTotal(Map<String, Object> params) {
        return sysPrivateOrderDao.queryTotal(params);
    }

    @Override
    public Map<String, Object> queryDetail(Long id, String storeIds) {
        Map<String, Object> detail = sysPrivateOrderDao.queryDetail(id, storeIds);
        if (detail != null) {
            detail.put("couponRel", sysPrivateOrderDao.queryCouponRel(id));
        }
        return detail;
    }

    @Override
    public boolean existsInScope(Long id, String storeIds) {
        return sysPrivateOrderDao.countInScope(id, storeIds) > 0;
    }

    @Override
    public List<Map<String, Object>> queryMemberOptions(String keyword, String storeIds) {
        return sysPrivateOrderDao.queryMemberOptions(keyword, storeIds);
    }

    @Override
    public List<Map<String, Object>> queryProductOptions(String keyword, Long storeId, String storeIds) {
        if (storeId == null) {
            throw new RRException("请选择购买门店");
        }
        return sysPrivateOrderDao.queryProductOptions(keyword, storeId, storeIds);
    }

    @Override
    public List<Map<String, Object>> queryCoachOptions(String keyword, Long productId, Long storeId,
                                                       String storeIds) {
        if (productId == null || storeId == null) {
            throw new RRException("请先选择购买门店和商品");
        }
        return sysPrivateOrderDao.queryCoachOptions(keyword, productId, storeId, storeIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createManual(Map<String, Object> params, Long operatorId, String storeIds) {
        Long memberId = requiredLong(params.get("memberId"), "会员");
        Long productId = requiredLong(params.get("productId"), "私教商品");
        Long storeId = requiredLong(params.get("storeId"), "购买门店");
        Long coachId = optionalLong(params.get("coachId"), "销售教练");
        BigDecimal paidAmount = requiredAmount(params.get("paidAmount"));
        String remark = buildManualRemark(params.get("remark"));

        Map<String, Object> member = sysPrivateOrderDao.queryMemberSnapshot(memberId, storeIds);
        if (member == null) {
            throw new RRException("会员不存在或不在当前门店权限范围内");
        }
        PtProduct product = sysPrivateOrderDao.queryProductSnapshot(productId, storeId, storeIds);
        if (product == null) {
            throw new RRException("商品不存在、未上架或不适用于所选门店");
        }
        if (product.getServiceType() == null || product.getLessonCount() == null
                || product.getLessonCount() <= 0 || product.getDurationMinutes() == null
                || product.getDurationMinutes() <= 0 || product.getValidityDays() == null
                || (product.getValidityDays() != -1 && product.getValidityDays() <= 0)) {
            throw new RRException("商品课时、时长或有效期配置不完整，无法新增购买记录");
        }
        if (coachId == null) {
            coachId = sysPrivateOrderDao.querySingleCoachId(productId, storeId);
        } else if (sysPrivateOrderDao.countAvailableCoach(coachId, productId, storeId) == 0) {
            throw new RRException("所选教练当前无法在该门店服务此商品");
        }

        PtPrivateOrderEntity order = buildManualOrder(member, product, memberId, storeId,
                coachId, paidAmount, remark, operatorId);
        order.setOrderNo(genOrderNo());
        try {
            sysPrivateOrderDao.saveManualOrder(order);
        } catch (DuplicateKeyException e) {
            // 全局订单号理论上唯一；极小概率撞号时仅重试一次，其他数据库错误继续抛出。
            order.setOrderNo(genOrderNo());
            sysPrivateOrderDao.saveManualOrder(order);
        }

        if (ptPrivateOrderDao.increaseProductSoldCount(productId) == 0) {
            throw new RRException("商品库存不足，无法新增购买记录");
        }
        memberPrivateBenefitService.activate(order.getId(), memberId, productId, storeId, coachId,
                order.getLessonCount(), order.getValidityDays());
        grantGroupBenefit(order);
        if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            incomePayDetailService.saveIncomePayDetail(order.getOrderNo(),
                    "MANUAL_PT_" + order.getId(), paidAmount, 9);
            settleWholeOrderCommission(order, paidAmount);
        }
        log.info("后台手工新增私教购买记录 orderId={},orderNo={},operatorId={}",
                order.getId(), order.getOrderNo(), operatorId);
        return order.getOrderNo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCascade(Long orderId, String confirmOrderNo, Long operatorId, String storeIds) {
        if (orderId == null || confirmOrderNo == null || confirmOrderNo.trim().isEmpty()) {
            throw new RRException("缺少参数:orderId/confirmOrderNo");
        }
        PtPrivateOrderEntity order = sysPrivateOrderDao.queryOrderForDelete(orderId, storeIds);
        if (order == null) {
            throw new RRException("订单不存在或不在当前门店权限范围内");
        }
        if (!order.getOrderNo().equals(confirmOrderNo.trim())) {
            throw new RRException("确认订单号不一致，已取消删除");
        }

        // 来源订单的赠课子单先锁定并删除。父订单同时删除时不把课时归还到即将删除的父权益。
        List<PtPrivateOrderEntity> giftOrders = sysPrivateOrderDao.queryGiftOrdersForDelete(orderId);
        for (PtPrivateOrderEntity giftOrder : giftOrders) {
            deleteOneOrder(giftOrder, false);
        }
        deleteOneOrder(order, Integer.valueOf(1).equals(order.getOrderSource()));

        log.warn("后台永久删除私教购买记录 orderId={},orderNo={},operatorId={},giftOrderCount={}",
                orderId, order.getOrderNo(), operatorId, giftOrders.size());
    }

    @Override
    public void refund(Long orderId, BigDecimal refundAmount, Integer refundLessons, String remark, Long operatorId) {
        privateOrderService.refund(orderId, refundAmount, refundLessons, remark, operatorId);
    }

    private PtPrivateOrderEntity buildManualOrder(Map<String, Object> member, PtProduct product,
                                                   Long memberId, Long storeId, Long coachId,
                                                   BigDecimal paidAmount, String remark, Long operatorId) {
        PtPrivateOrderEntity order = new PtPrivateOrderEntity();
        order.setMemberId(memberId);
        order.setMemberName(text(member.get("name")));
        order.setMemberMobile(text(member.get("mobile")));
        order.setProductId(product.getId());
        order.setProductName(product.getProductName());
        order.setProductTypeId(product.getProductTypeId());
        order.setProductTypeName(product.getTypeName());
        order.setServiceType(product.getServiceType());
        order.setStoreId(storeId);
        order.setCoachId(coachId);
        order.setLessonCount(product.getLessonCount());
        order.setDurationMinutes(product.getDurationMinutes());
        order.setValidityDays(product.getValidityDays());
        order.setOriginalAmount(paidAmount);
        order.setPayableAmount(paidAmount);
        order.setPaidAmount(paidAmount);
        order.setRefundAmount(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setMarketingType(0);
        order.setCouponDiscountAmount(BigDecimal.ZERO);
        order.setPayMethod(9);
        order.setPayStatus(2);
        order.setOrderStatus(2);
        order.setOrderSource(0);
        order.setRemark(remark);
        order.setCreatedBy(operatorId);
        order.setUpdatedBy(operatorId);
        return order;
    }

    private void deleteOneOrder(PtPrivateOrderEntity order, boolean restoreGiftLessons) {
        if (restoreGiftLessons && order.getSourceBenefitId() != null
                && order.getLessonCount() != null && order.getLessonCount() > 0) {
            if (sysPrivateOrderDao.restoreSourceBenefitLessons(
                    order.getSourceBenefitId(), order.getLessonCount()) == 0) {
                throw new RRException("赠送来源权益不存在，无法安全删除赠送订单");
            }
        }
        sysPrivateOrderDao.deleteAppointments(order.getId());
        sysPrivateOrderDao.deleteCoachTradeDetails(order.getOrderNo());
        sysPrivateOrderDao.deleteMemberGroupBenefitFlows(order.getId());
        sysPrivateOrderDao.deleteMemberGroupBenefits(order.getId());
        sysPrivateOrderDao.restoreMemberCoupon(order.getId());
        sysPrivateOrderDao.deleteCouponRel(order.getId());
        sysPrivateOrderDao.deleteIncomeDetails(order.getId(), order.getOrderNo());
        sysPrivateOrderDao.deleteInstallmentBills(order.getId());
        sysPrivateOrderDao.deleteInstallmentPlan(order.getId());
        sysPrivateOrderDao.deletePrivateBenefit(order.getId());

        if (isCountedSale(order)) {
            sysPrivateOrderDao.decreaseProductSoldCount(order.getProductId());
            if (Integer.valueOf(1).equals(order.getMarketingType()) && order.getMarketingActivityId() != null) {
                sysPrivateOrderDao.decreaseGroupBuySoldCount(order.getMarketingActivityId());
            } else if (Integer.valueOf(2).equals(order.getMarketingType())
                    && order.getMarketingActivityId() != null) {
                sysPrivateOrderDao.decreaseFlashSaleSoldCount(order.getMarketingActivityId());
            }
        }
        if (sysPrivateOrderDao.deleteOrder(order.getId()) != 1) {
            throw new RRException("订单删除失败，请刷新后重试");
        }
    }

    private boolean isCountedSale(PtPrivateOrderEntity order) {
        if (Integer.valueOf(1).equals(order.getOrderSource())) {
            return false;
        }
        Integer status = order.getOrderStatus();
        return Integer.valueOf(1).equals(status)
                || Integer.valueOf(2).equals(status)
                || Integer.valueOf(4).equals(status);
    }

    private void grantGroupBenefit(PtPrivateOrderEntity order) {
        Map<String, Object> rule = ptPrivateOrderDao.selectGroupBenefitRule(order.getProductId());
        if (rule == null || ptPrivateOrderDao.countMemberGroupBenefitByOrder(order.getId()) > 0) {
            return;
        }
        int giftCount = number(rule.get("giftCount"));
        int validityDays = number(rule.get("validityDays"));
        if (giftCount <= 0 || validityDays <= 0) {
            log.warn("后台建单附赠团课规则不完整，跳过发放 productId={}", order.getProductId());
            return;
        }
        ptPrivateOrderDao.insertMemberGroupBenefit(order.getMemberId(), order.getId(),
                order.getProductId(), giftCount, validityDays);
        Long benefitId = ptPrivateOrderDao.selectMemberGroupBenefitIdByOrder(order.getId());
        ptPrivateOrderDao.insertMemberGroupBenefitFlow(
                benefitId, order.getMemberId(), giftCount, order.getId());
    }

    private void settleWholeOrderCommission(PtPrivateOrderEntity order, BigDecimal paidAmount) {
        if (order.getCoachId() == null
                || ptCoachMonthlyCommissionRuleDao.countMonthlyProduct(order.getProductId()) > 0) {
            return;
        }
        PtCoachFeeRuleEntity rule = sysCoachFeeRuleService.matchFeeRule(
                order.getCoachId(), order.getProductId(), order.getStoreId(), 2);
        if (rule == null || rule.getCommissionRate() == null) {
            return;
        }
        BigDecimal commission = paidAmount.multiply(rule.getCommissionRate())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        SysCoachTradeDetailEntity detail = new SysCoachTradeDetailEntity();
        detail.setCoachId(order.getCoachId());
        detail.setTradeType(1);
        detail.setMoney(commission);
        detail.setOrigMoney(paidAmount);
        detail.setPercent(rule.getCommissionRate().doubleValue());
        detail.setTransactionNumber("PT_SALE_" + order.getId());
        detail.setOrderNo(order.getOrderNo());
        detail.setStatus(1);
        detail.setTransactionTime(new Date());
        detail.setCreateTime(new Date());
        sysCoachTradeDetailService.save(detail);
    }

    private Long requiredLong(Object value, String label) {
        Long result = optionalLong(value, label);
        if (result == null) {
            throw new RRException("请选择" + label);
        }
        return result;
    }

    private Long optionalLong(Object value, String label) {
        if (value == null || value.toString().trim().isEmpty()) {
            return null;
        }
        try {
            long result = Long.parseLong(value.toString().trim());
            if (result <= 0) {
                throw new NumberFormatException();
            }
            return result;
        } catch (NumberFormatException e) {
            throw new RRException(label + "参数不正确");
        }
    }

    private BigDecimal requiredAmount(Object value) {
        if (value == null || value.toString().trim().isEmpty()) {
            throw new RRException("请输入实付金额");
        }
        try {
            BigDecimal amount = new BigDecimal(value.toString().trim());
            if (amount.scale() > 2 || amount.compareTo(BigDecimal.ZERO) < 0
                    || amount.compareTo(MAX_MANUAL_AMOUNT) > 0) {
                throw new RRException("实付金额须为0至99999999.99，且最多两位小数");
            }
            return amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (NumberFormatException e) {
            throw new RRException("实付金额格式不正确");
        }
    }

    private String buildManualRemark(Object value) {
        String suffix = value == null ? "" : value.toString().trim();
        String remark = suffix.isEmpty() ? "后台手工新增" : "后台手工新增：" + suffix;
        if (remark.length() > 255) {
            throw new RRException("备注不能超过247个字符");
        }
        return remark;
    }

    private String genOrderNo() {
        return "PT" + OrderNoGenerator.getOrderIdByTime() + ConfigConstant.PT_PRIVATE_ORDER_TYPE;
    }

    private String text(Object value) {
        return value == null ? null : value.toString();
    }

    private int number(Object value) {
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }
}
