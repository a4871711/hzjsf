package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.MessageEntity;
import com.dlc.modules.sys.entity.SysWalletDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 钱包明细表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-11 15:33:55
 */
@Mapper
@Repository
public interface SysWalletDetailCountDao extends BaseDao<SysWalletDetailEntity> {
    List<MessageEntity> selectMsg();
}
