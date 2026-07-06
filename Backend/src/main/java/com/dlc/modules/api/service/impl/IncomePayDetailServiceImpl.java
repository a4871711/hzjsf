package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.CardOrder;
import com.dlc.modules.api.entity.IncomePayDetail;
import com.dlc.modules.api.entity.PtMemberWalletFlowEntity;
import com.dlc.modules.api.entity.PtPrivateOrderEntity;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.entity.VipBenefit;
import com.dlc.modules.api.entity.VipBenefitTransfer;
import com.dlc.modules.api.service.CardOrderService;
import com.dlc.modules.api.service.IncomePayDetailService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/18/018
 */
@Service
@Transactional
public class IncomePayDetailServiceImpl implements IncomePayDetailService {

    @Autowired
    private IncomePayDetailMapper incomePayDetailMapper;
    @Autowired
    private CardOrderMapper cardOrderMapper;
    @Autowired
    private PrivateClassOrderMapper privateClassOrderMapper;
    @Autowired
    private TeamClassOrderMapper teamClassOrderMapper;
    @Autowired
    private GoodsOrderMapper goodsOrderMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private VipBenefitMapper vipBenefitMapper;
    @Autowired
    private VipBenefitTransferMapper vipBenefitTransferMapper;
    @Autowired
    private PtPrivateOrderDao ptPrivateOrderDao;
    @Autowired
    private PtMemberWalletFlowDao ptMemberWalletFlowDao;
    @Autowired
    private com.dlc.modules.api.dao.PtOrderInstallmentBillDao ptOrderInstallmentBillDao;
    /**
     *  @Auther:YD
     *  @parameters:
     *  支付后添加收支明细
     */
    @Override
    public int saveIncomePayDetail(String orderNo, String transaction_id, BigDecimal wallet, Integer payType) {


        IncomePayDetail ipd = new IncomePayDetail();
        //支付用途
        String suffix = orderNo.substring(orderNo.length()-1);
        if (suffix.equals(ConfigConstant.PT_PRIVATE_ORDER_TYPE)
                || suffix.equals(ConfigConstant.INSTALLMENT_DOWN_TYPE)
                || suffix.equals(ConfigConstant.INSTALLMENT_BILL_TYPE)) {
            //私教商品购买/分期首付/分期后续期:同属私教商品收款,固定取14(constant表cId=1需配ckey=14)。
            //后缀"b"/"a"非数字无法沿用 Integer.valueOf;后缀"9"虽为数字但9在本表语义为退款,故一并归14避免误分类。
            ipd.setPayType(ConfigConstant.PT_PRIVATE_PAY_TYPE);
        } else {
            ipd.setPayType(Integer.valueOf(suffix));
        }
        //用户id
        if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.CARD_ORDER_TYPE)){
            CardOrder cardOrder = cardOrderMapper.selectByOrderNo(orderNo);
            //健身卡订单
            ipd.setUserId(cardOrder.getUserId());
            //查询当前用户所属门店id
            Long storeId = cardOrderMapper.queryStoreIdByAddress(cardOrder.getStoreAddressId());
            ipd.setStoreId(storeId);
            if (cardOrder.getAutoPay() > 0) {//自动续费
                //支付用途
                //ipd.setPayType(13);
            }
        }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.PRIVATE_CLASS_ORDER_TYPE)){
            //私教课订单
            ipd.setUserId(privateClassOrderMapper.selectPrivateClassOrderByOrder(orderNo).getUserId());
            //查询当前用户所属门店id
            Long storeId = userInfoMapper.queryStoreIdByUserId(ipd.getUserId());
            ipd.setStoreId(storeId);
        }else if(orderNo.substring(orderNo.length()-1).equals(ConfigConstant.TEAM_CLASS_ORDER_TYPE)){
            //团体订单
            ipd.setUserId(teamClassOrderMapper.selectTeamClassOrderByOrderNo(orderNo).getUserId());
            //查询当前用户所属门店id
            Long storeId = userInfoMapper.queryStoreIdByUserId(ipd.getUserId());
            ipd.setStoreId(storeId);
        }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.GOODS_ORDER_TYPE)){
            //商品订单
            ipd.setUserId(goodsOrderMapper.queryCouponMoneyByOrderNo(orderNo).getUserId());
            //查询当前用户所属门店id
            Long storeId = userInfoMapper.queryStoreIdByUserId(ipd.getUserId());
            ipd.setStoreId(storeId);
        }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.VIP_CARD_BUY_TYPE)){
            //VIP权益卡购买订单:按订单号反查 vip_benefit 取 userId,再查所属门店
            VipBenefit vipBenefit = vipBenefitMapper.selectByOrderNo(orderNo);
            if (vipBenefit != null) {
                ipd.setUserId(vipBenefit.getUserId());
                Long storeId = userInfoMapper.queryStoreIdByUserId(vipBenefit.getUserId());
                ipd.setStoreId(storeId);
            }
        }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.VIP_TRANSFER_FEE_TYPE)){
            //VIP转让服务费订单:按服务费订单号反查 vip_benefit_transfer,取转让人 userId/storeId(付费人=转让人)
            VipBenefitTransfer transfer = vipBenefitTransferMapper.selectByFeeOrderNo(orderNo);
            if (transfer != null) {
                ipd.setUserId(transfer.getFromUserId());
                Long storeId = transfer.getFromStoreId() != null
                        ? transfer.getFromStoreId()
                        : userInfoMapper.queryStoreIdByUserId(transfer.getFromUserId());
                ipd.setStoreId(storeId);
            }
        }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.PT_PRIVATE_ORDER_TYPE)){
            //私教商品购买订单(后缀b):按订单号反查 pt_private_order 取 memberId/storeId(购买门店快照,仿VIP后缀6/7写法)
            PtPrivateOrderEntity ptOrder = ptPrivateOrderDao.selectByOrderNo(orderNo);
            if (ptOrder != null) {
                ipd.setUserId(ptOrder.getMemberId());
                ipd.setStoreId(ptOrder.getStoreId());
            }
        }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.WALLET_RECHARGE_TYPE)){
            //储值充值订单(后缀8,第19步):按充值单号反查 pt_member_wallet_flow 取 memberId(充值回调已先写流水),
            //storeId 取会员当前门店(充值账户无门店维度,与其它会员维度收款同锚点)
            //注:payType 由上方按末位"8"自动置为 8;8 在 income_pay_detail 语义为"积分兑换/支出侧",
            //   储值充值属真实收款却会被现有收支列表(payType in 2,3,4,5,6,9,10,11,13)过滤掉、且被 countMoney 计入支出侧,
            //   此为跨步(报表口径)问题,本步不擅改收入分类,保留 8 的自动映射并上抛决策(见交付说明)。
            PtMemberWalletFlowEntity rechargeFlow = ptMemberWalletFlowDao.selectByOutOrderNo(orderNo);
            if (rechargeFlow != null) {
                ipd.setUserId(rechargeFlow.getMemberId());
                ipd.setStoreId(userInfoMapper.queryStoreIdByUserId(rechargeFlow.getMemberId()));
            }
        }else if (orderNo.substring(orderNo.length()-1).equals(ConfigConstant.INSTALLMENT_DOWN_TYPE)
                || orderNo.substring(orderNo.length()-1).equals(ConfigConstant.INSTALLMENT_BILL_TYPE)){
            //分期首付(后缀9)/分期后续期(后缀a)订单(第20步):按本期支付单号反查账单→计划取 member_id、
            //关联订单取 store_id(购买门店快照);记账在分期回调内先于本调用行锁定位账单,故此处必能反查到。
            //注:首付走主单(后缀b)时不进本分支(记账在私教购买后缀b分支);仅独立首付单(后缀9)/后续期(后缀a)进此。
            java.util.Map<String, Object> ms = ptOrderInstallmentBillDao.selectMemberStoreByPayOrderNo(orderNo);
            if (ms != null) {
                if (ms.get("memberId") != null) {
                    ipd.setUserId(((Number) ms.get("memberId")).longValue());
                }
                if (ms.get("storeId") != null) {
                    ipd.setStoreId(((Number) ms.get("storeId")).longValue());
                }
            }
        }
        //订单号
        ipd.setOrderNo(orderNo);
        //流动号
        ipd.setTransactionNumber(transaction_id);
        //金额
        ipd.setMoney(wallet);
        //收支时间
        Date date = new Date();
        ipd.setTradeDate(date);
        //收支方式
        ipd.setTradeType(payType);
        //状态
        ipd.setTradeStatus(3);
        //审核通过时间
        //交易完成时间
        ipd.setTransactionTime(date);
        //创建时间
        ipd.setCreatedDate(date);

        int result = incomePayDetailMapper.insertSelective(ipd);
        return result;
    }

    @Override
    public int updateAnotherId(String orderNo, Long anotherId) {
        return incomePayDetailMapper.updateAnotherId(orderNo, anotherId);
    }

    @Override
    public int saveTransferRefund(String orderNo, BigDecimal money, Long userId, Long anotherId) {
        IncomePayDetail ipd = new IncomePayDetail();
        ipd.setOrderNo(orderNo);
        // 用途=9退款(与商城退款一致;前台 moneyType CASE 把 9 记'+',退款回款计收入)
        ipd.setPayType(9);
        // 退款金额存正数,正负号由前台展示层决定
        ipd.setMoney(money);
        // 本功能只接微信,收支方式=微信
        ipd.setTradeType(ConfigConstant.WXPAY);
        ipd.setUserId(userId);
        ipd.setStoreId(userInfoMapper.queryStoreIdByUserId(userId));
        // 对方=受让人(留痕)
        ipd.setAnotherId(anotherId);
        ipd.setTradeStatus(3);
        Date now = new Date();
        ipd.setTradeDate(now);
        ipd.setTransactionTime(now);
        ipd.setCreatedDate(now);
        return incomePayDetailMapper.insertSelective(ipd);
    }

    @Override
    public int savePtOrderRefund(String orderNo, BigDecimal money, Long memberId, Long storeId, Integer tradeType) {
        IncomePayDetail ipd = new IncomePayDetail();
        ipd.setOrderNo(orderNo);
        // 用途=9退款(与商城/VIP转让退款一致;前台 moneyType CASE 把 9 记'+',正负号由展示层决定)
        ipd.setPayType(9);
        // 退款金额存正数(仓库负向记账惯例:payType=9 标记退款,不存负数)
        ipd.setMoney(money);
        // 收支方式=原支付渠道(本期仅微信,第19/20步储值/分期接入后由调用方传对应值)
        ipd.setTradeType(tradeType);
        ipd.setUserId(memberId);
        // 门店取订单购买门店快照,与购买流水(后缀b分支)同锚点,报表借贷两侧对得上
        ipd.setStoreId(storeId);
        ipd.setTradeStatus(3);
        Date now = new Date();
        ipd.setTradeDate(now);
        ipd.setTransactionTime(now);
        ipd.setCreatedDate(now);
        return incomePayDetailMapper.insertSelective(ipd);
    }
}
