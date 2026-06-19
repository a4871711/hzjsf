package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.UserAttention;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserAttentionMapper {
    int insert(UserAttention record);

    int insertSelective(UserAttention record);

    /**
     * 查询我的关注动态
     * @param params
     * @return
     */
    List<Map<String, Object>> queryMyAttention(Map<String, Object> params);

    int queryMyAttentionCount(Map<String, Object> params);

    /**
     * 添加关注
     * @param attentedId
     * @param userId
     * @return
     */
    int insertAttention(@Param("userId") Long userId, @Param("attentedId") Long attentedId);

    /**
     * 取消关注
     * @param userId
     * @param attentedId
     * @return
     */
    int updateAttentionStatus(@Param("userId") Long userId, @Param("attentedId") Long attentedId);

    int deleteAttention(@Param("userId") Long userId, @Param("attentedId") Long attentedId);

    /**
     * 举报
     * @param userId
     * @param attentedId
     * @param reportType
     * @return
     */
    int updateReportType(@Param("userId") Long userId, @Param("attentedId") Long attentedId,
                         @Param("reportType") String reportType);

    /**
     * 查询粉丝数
     * @param params
     * @return
     */
    List<Map<String,Object>> queryFansByUId(Map<String, Object> params);
    //查分粉丝页数量
    int queryFansCount(Query query);
    /**
     * 查询关注成员数
     * @param params
     * @return
     */
    List<Map<String,Object>> queryAttendByUId(Map<String, Object> params);
    //查关注分页数量
    int queryAttendByCount(Query query);
}