package com.dlc.modules.api.service;

import com.dlc.modules.api.vo.UserInfoVo;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/19/019
 */
public interface PrivateClassService {
    List<Map<String,Object>> courseList(Long coachId);

    Map<String,Object> courseInfo(Map<String, Object> params);

    Map<String,Object> createCourseOrder(Map<String, Object> params,UserInfoVo user);

    int updatePrivateClassOrder(String orderNo, BigDecimal wallet, String transaction_id, int payType) throws ParseException;

    int queryIsOrBuyTyk(Long userId, Long privateClassId);
}
