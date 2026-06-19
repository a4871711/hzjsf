package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.MessageEntity;
import com.dlc.modules.sys.entity.SysCoachEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SysCoachDao extends BaseDao<SysCoachEntity> {
    void updateFailure(SysCoachEntity coach);

    void updateSuccess(SysCoachEntity coach);

    int batchShipPlace(List<Map<String, Object>> shipList);

    int insertCoachWallt(Long coachId);

    List<MessageEntity> newMsg();
    //更新教练上课次数+1
    int updateClassCount(Long coachId);

    int coachUpdateGrade(SysCoachEntity coach);

    int queryStoreStatus(Long storeId);
}
