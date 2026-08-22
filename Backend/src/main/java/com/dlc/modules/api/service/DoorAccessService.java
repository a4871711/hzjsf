package com.dlc.modules.api.service;

import com.dlc.modules.api.vo.UserInfoVo;

import java.util.Map;

/** 会员卡优先、预约兜底的二维码开门决策服务。 */
public interface DoorAccessService {

    Map<String, Object> qrcode(UserInfoVo user, Map<String, Object> params);

    /** 返回 null 表示预约二维码通过，否则返回应记录的拒绝原因。 */
    String validateAppointmentQr(Long userId, Long appointmentId, Long storeAddrId, String rand);
}
