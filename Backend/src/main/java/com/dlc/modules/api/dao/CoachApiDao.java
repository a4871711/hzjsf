package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.PtCoachOption;
import com.dlc.modules.api.vo.PtScheduleWindowVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 私教教练会员端只读浏览 Dao（可约教练计算 + 排班读取）
 *
 * @author claude
 */
@Mapper
@Repository
public interface CoachApiDao {

    /**
     * 需求 8.3 交集：商品适用门店 ∩ 教练所属门店 ∩ 教练正常状态 ∩ 教练有排班 ∩ 商品指定教练范围(为空则不限)。
     */
    List<PtCoachOption> queryBookableCoaches(@Param("productId") Long productId,
                                             @Param("storeId") Long storeId);

    /** 下单时校验所选教练确实能在所选门店服务该商品 */
    int countBookableCoach(@Param("productId") Long productId,
                           @Param("storeId") Long storeId,
                           @Param("coachId") Long coachId);

    /** 教练在某门店某星期的启用排班窗口 */
    List<PtScheduleWindowVo> queryEnabledSchedule(@Param("coachId") Long coachId, @Param("storeId") Long storeId,
                                                  @Param("weekday") Integer weekday);

    /** 商品适用门店ID集合，用于校验前端传入 storeId 合法性 */
    List<Long> queryProductStoreIds(Long productId);

    /** 教练所属门店ID集合，用于校验前端传入 storeId 合法性 */
    List<Long> queryCoachStoreIds(Long coachId);

    /** 教练端排班表单可选门店（ID 统一为 store_address.storeAddrId） */
    List<Map<String, Object>> queryCoachStores(Long coachId);

    /** 根据登录会员账号查询已绑定且状态正常的私教身份 */
    Map<String, Object> queryBoundCoachByUserId(@Param("userId") Long userId);

    /** 教练首页三项统计：本月已上课时、未来待上课时、本月已完成课程产生的预计课时费 */
    Map<String, Object> queryCoachHomeStats(@Param("coachId") Long coachId);

    /** 普通私教负责的会员，按会员权益 coach_id 归属并按会员聚合。 */
    List<Map<String, Object>> queryCoachMembers(@Param("coachId") Long coachId,
                                                @Param("keyword") String keyword,
                                                @Param("offset") Integer offset,
                                                @Param("limit") Integer limit);

    int countCoachMembers(@Param("coachId") Long coachId,
                          @Param("keyword") String keyword);

    /** 批量查询本页会员在当前教练名下的全部权益，避免逐会员查询。 */
    List<Map<String, Object>> queryCoachMemberBenefits(@Param("coachId") Long coachId,
                                                        @Param("memberIds") List<Long> memberIds);

    /** 私教收入汇总；仅统计 PT_LESSON_ / PT_SALE_ 流水，避免混入旧 coach 域同 ID 数据 */
    Map<String, Object> queryCoachIncomeSummary(@Param("coachId") Long coachId,
                                                 @Param("monthStart") String monthStart,
                                                 @Param("nextMonthStart") String nextMonthStart);

    /** 私教收入明细分页；type=lesson/sale/null，按月份范围过滤。 */
    List<Map<String, Object>> queryCoachIncomeList(@Param("coachId") Long coachId,
                                                   @Param("type") String type,
                                                   @Param("monthStart") String monthStart,
                                                   @Param("nextMonthStart") String nextMonthStart,
                                                   @Param("offset") Integer offset,
                                                   @Param("limit") Integer limit);

    int countCoachIncome(@Param("coachId") Long coachId,
                         @Param("type") String type,
                         @Param("monthStart") String monthStart,
                         @Param("nextMonthStart") String nextMonthStart);
}
