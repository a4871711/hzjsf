package com.dlc.modules.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 后台 · 会员储值账户/流水只读查询 Dao(第19步资金域,/sys/wallet)。
 * <p>账户/流水的写入(充值/冻结/扣款)一律走 api 侧 PtMemberWalletService.changeBalance,本 Dao 只做后台查询;
 * 冻结/解冻/后台充值动作在 controller 直接委托 api service,不在本 Dao 出写语句。</p>
 * <p>联表带会员昵称/手机,故列表统一返回 Map(不改动 api 的 PtMemberWallet* 实体,避免重塑数据层)。</p>
 * <p>对应 mapper/sys/SysPtWalletDao.xml(sys 目录 XML 可热刷新,免重启)。</p>
 * <p>门店隔离:账户/流水表无 store_id,经会员 user_info.now_store_id 过滤(storeIds 逗号串,空=超管看全部)。</p>
 *
 * @author claude
 */
@Mapper
@Repository
public interface SysPtWalletDao {

    /** 账户分页(联 user_info 昵称/手机);params 含 keyword/status/startTime/endTime/storeIds/offset/limit */
    List<Map<String, Object>> queryList(Map<String, Object> params);

    /** 与 queryList 同条件总数 */
    int queryTotal(Map<String, Object> params);

    /** 统计卡:同筛选条件下 总余额/总充值/总消费(返回 totalBalance/totalRecharge/totalConsume) */
    Map<String, Object> queryStat(Map<String, Object> params);

    /** 单账户详情(联会员昵称/手机) */
    Map<String, Object> queryObject(@Param("id") Long id);

    /** 流水分页(联会员昵称/手机);params 含 memberId/walletId/flowType/startTime/endTime/storeIds/offset/limit */
    List<Map<String, Object>> queryFlowList(Map<String, Object> params);

    /** 与 queryFlowList 同条件总数 */
    int queryFlowTotal(Map<String, Object> params);
}
