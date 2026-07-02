package com.dlc.modules.api.dao;

import org.apache.ibatis.annotations.Param;

/**
 * 会员黑名单(member_blacklist)Mapper(移动端只读)
 * 转让前置校验用:实时查转让人/受让人是否在生效黑名单中。
 * 后台拉黑/解除的写入在第14步 sys 侧实现,本接口只读。
 */
public interface MemberBlacklistMapper {

    /** 统计某会员生效中(status=1)的黑名单记录数,>0 即在黑名单 */
    int countActiveByUserId(@Param("userId") Long userId);
}
