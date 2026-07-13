package com.dlc.modules.sys.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 权益会员(vip_benefit)后台管理 Dao:列表 + 停用/启用、备注、更新有效期、更换门店、注销。
 * SQL 见 mapper/sys/SysVipMemberDao.xml。
 *
 * @date 2026-07-12
 */
@Mapper
@Repository
public interface SysVipMemberDao {

    List<Map<String, Object>> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    /** 导出用:不分页取全部符合筛选的权益会员,字段已在 XML 里转成导出可读文本 */
    List<Map<String, Object>> queryExportList(Map<String, Object> map);

    /** 状态流转带前置状态校验(乐观):命中 0 行说明当前状态不允许该操作 */
    int updateStatus(@Param("vipBenefitId") Long vipBenefitId,
                     @Param("fromStatus") List<Integer> fromStatus,
                     @Param("toStatus") Integer toStatus);

    int updateRemark(@Param("vipBenefitId") Long vipBenefitId, @Param("remark") String remark);

    /** 改到期时间;若原为 3已过期 且新时间在未来则自动回 0正常 */
    int updateValidity(@Param("vipBenefitId") Long vipBenefitId, @Param("expireTime") String expireTime);

    /** 更换开通门店:store_addr_id 与 store_id 联动改(storeId 由 store_address 反查) */
    int updateStore(@Param("vipBenefitId") Long vipBenefitId, @Param("storeAddrId") Long storeAddrId);

    /** 该权益在途转让单数(20待审核/40待受让人确认);>0 时不允许注销,避免服务费占押+幽灵单 */
    int countActiveTransfer(@Param("vipBenefitId") Long vipBenefitId);
}
