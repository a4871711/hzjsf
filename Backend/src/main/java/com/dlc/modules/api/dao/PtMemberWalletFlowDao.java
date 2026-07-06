package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.PtMemberWalletFlowEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 会员储值流水 Dao(pt_member_wallet_flow,第19步)。只增不改:无 update/delete。
 * uk_out_order_no 唯一键 = 充值回调/重复扣款/重复退款的 DB 级幂等兜底。
 * 对应 mapper/api/PtMemberWalletFlowDao.xml(api XML 改动须重启 Tomcat)。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtMemberWalletFlowDao {

    /** 写流水(回填自增 id);out_order_no 撞唯一键抛 DuplicateKeyException=并发重复,由事务回滚兜底 */
    int save(PtMemberWalletFlowEntity flow);

    /**
     * 按外部单号查流水:充值回调幂等闸1(先查已处理直接返回)+
     * IncomePayDetailServiceImpl 后缀8记账反查会员的锚点。
     */
    PtMemberWalletFlowEntity selectByOutOrderNo(@Param("outOrderNo") String outOrderNo);

    /** 会员端我的流水分页(member_id=当前登录会员,倒序),params 含 memberId/flowType/offset/limit */
    List<PtMemberWalletFlowEntity> queryMyFlows(Map<String, Object> params);

    /** 与 queryMyFlows 同条件总数 */
    int countMyFlows(Map<String, Object> params);
}
