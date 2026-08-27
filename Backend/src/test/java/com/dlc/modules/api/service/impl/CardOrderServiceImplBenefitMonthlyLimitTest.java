package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.modules.api.entity.VipBenefit;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * 权益性质月卡购买边界回归测试。
 * 重点覆盖单次 12 张上限，以及本次月卡周期起始日是否超出权益到期日。
 */
public class CardOrderServiceImplBenefitMonthlyLimitTest {

    /** 第 12 张月卡的周期仍落在权益期内时允许下单。 */
    @Test
    public void shouldAllowTwelfthMonthlyCardWithinBenefit() throws Exception {
        validate("2027-08-19 23:59:59", "2027-08-19", 30, 12);
    }

    /** 单次购买 13 张时，无论日期是否合规都必须命中数量上限。 */
    @Test
    public void shouldRejectThirteenthMonthlyCard() throws Exception {
        assertLimit("2027-08-19 23:59:59", "2027-09-18", 30, 13);
    }

    /** 权益最后一个月仍允许购买 1 张，只要该张月卡的周期起始日没有晚于权益到期日。 */
    @Test
    public void shouldAllowOneMonthlyCardInLastBenefitMonth() throws Exception {
        validate("2027-08-19 23:59:59", "2027-09-09", 30, 1);
    }

    /** 权益最后一个月继续购买第 2 张会让新周期从权益到期日之后开始，因此拒绝下单。 */
    @Test
    public void shouldRejectSecondMonthlyCardAfterLastBenefitMonth() throws Exception {
        assertLimit("2027-08-19 23:59:59", "2027-10-09", 30, 2);
    }

    /** 统一断言越界场景返回固定业务码，避免以后被改成模糊的参数错误。 */
    private void assertLimit(String benefitExpire, String nextValidityDate,
                             int validityDays, int buyCount) throws Exception {
        try {
            validate(benefitExpire, nextValidityDate, validityDays, buyCount);
            fail("超过权益月卡上限时应明确拒绝下单");
        } catch (RRException e) {
            assertEquals(CodeAndMsg.ERROR_FIT_CARD_MONTH_LIMIT.getCode().intValue(), e.getCode());
            assertEquals(CodeAndMsg.ERROR_FIT_CARD_MONTH_LIMIT.getMsg(), e.getMessage());
        }
    }

    /** 按生产方法所需字段构造最小测试数据，不依赖 Spring 容器和数据库。 */
    private void validate(String benefitExpire, String nextValidityDate,
                          int validityDays, int buyCount) throws Exception {
        VipBenefit benefit = new VipBenefit();
        benefit.setExpireTime(parse(benefitExpire, "yyyy-MM-dd HH:mm:ss"));
        Date validityDate = parse(nextValidityDate, "yyyy-MM-dd");
        CardOrderServiceImpl.validateBenefitMonthlyCardLimit(
                benefit, validityDate, validityDays, buyCount);
    }

    /** 测试日期显式指定格式，保证用例中的自然日边界一眼可见。 */
    private Date parse(String value, String pattern) throws Exception {
        return new SimpleDateFormat(pattern).parse(value);
    }
}
