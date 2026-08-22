package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.DateUtils;
import com.dlc.common.utils.RedisUtils;
import com.dlc.modules.api.dao.CardPauseRecordMapper;
import com.dlc.modules.api.dao.DeviceMapper;
import com.dlc.modules.api.dao.PtPrivateAppointmentDao;
import com.dlc.modules.api.entity.Device;
import com.dlc.modules.api.entity.PtPrivateAppointmentEntity;
import com.dlc.modules.api.service.AboutUsService;
import com.dlc.modules.api.service.DoorAccessService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service("doorAccessService")
public class DoorAccessServiceImpl implements DoorAccessService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private CardPauseRecordMapper cardPauseRecordMapper;
    @Autowired
    private PtPrivateAppointmentDao ptPrivateAppointmentDao;
    @Autowired
    private AboutUsService aboutUsService;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Map<String, Object> qrcode(UserInfoVo user, Map<String, Object> params) {
        Device device = deviceMapper.selectUserValidityForEntry(user.getUserId());
        if (device != null) {
            return membershipDecision(user, params, device);
        }
        if (cardPauseRecordMapper.countActivePauseByUser(user.getUserId()) > 0) {
            return denied("CARD_PAUSED", "会员卡停卡中，暂不可使用");
        }

        Map<String, Object> config = aboutUsService.queryOpenDoor();
        if (!appointmentQrEnabled(config)) {
            return denied("MEMBERSHIP_REQUIRED", "未购买会员或会员卡已过期");
        }

        PtPrivateAppointmentEntity current = ptPrivateAppointmentDao.queryCurrentDoorAppointment(user.getUserId());
        if (current != null) {
            return appointmentDecision(user, params, current, qrcodeValidSeconds(config));
        }
        PtPrivateAppointmentEntity next = ptPrivateAppointmentDao.queryNextDoorAppointment(user.getUserId());
        if (next != null) {
            Map<String, Object> result = denied("APPOINTMENT_TOO_EARLY", "尚未到预约开门时间");
            result.put("nextAccessTime", format(DoorAccessTimePolicy.accessStart(classStart(next))));
            result.put("appointment", appointmentData(next));
            return result;
        }
        PtPrivateAppointmentEntity expired = ptPrivateAppointmentDao.queryExpiredTodayDoorAppointment(user.getUserId());
        if (expired != null) {
            Map<String, Object> result = denied("APPOINTMENT_WINDOW_EXPIRED", "本次预约开门时间已结束");
            result.put("appointment", appointmentData(expired));
            return result;
        }
        return denied("APPOINTMENT_REQUIRED", "请先预约课程");
    }

    @Override
    public String validateAppointmentQr(Long userId, Long appointmentId, Long storeAddrId, String rand) {
        Map<String, Object> config = aboutUsService.queryOpenDoor();
        if (!appointmentQrEnabled(config)) {
            return "预约开门规则已关闭";
        }
        if (cardPauseRecordMapper.countActivePauseByUser(userId) > 0) {
            return "会员卡停卡中";
        }
        if (deviceMapper.selectUserValidityForEntry(userId) != null) {
            return "已有有效会员卡，请刷新开门码";
        }
        PtPrivateAppointmentEntity appointment = ptPrivateAppointmentDao.queryDoorAppointmentById(appointmentId, userId);
        if (appointment == null) {
            return "预约不存在";
        }
        if (!Integer.valueOf(1).equals(appointment.getAppointmentStatus())
                && !Integer.valueOf(3).equals(appointment.getAppointmentStatus())) {
            return "预约状态不可开门";
        }
        if (appointment.getStoreId() == null || !appointment.getStoreId().equals(storeAddrId)) {
            return "预约门店与扫码门店不一致";
        }
        if (!DoorAccessTimePolicy.isAvailable(LocalDateTime.now(), classStart(appointment))) {
            return "不在预约开门时间内";
        }
        String cached = redisUtils.get(appointmentRandKey(appointmentId, userId));
        if (StringUtils.isBlank(cached) || !cached.equals(rand)) {
            return "二维码已失效";
        }
        return null;
    }

    private Map<String, Object> membershipDecision(UserInfoVo user, Map<String, Object> params, Device device) {
        int rand = new Random().nextInt(1000000);
        String qrCode = "hzjsf_" + user.getUserId() + "-" + System.currentTimeMillis()
                + "-" + String.valueOf(params.get("userLat")) + "-" + String.valueOf(params.get("userLng"))
                + "-" + rand;
        redisUtils.set(ConfigConstant.DEVICE_RAND + device.getDeviceId(), rand, 5);

        String validityDate = calculateValidityDate(device);
        String lasttime = redisUtils.get(ConfigConstant.DEVICE + device.getDeviceId());
        if (StringUtils.isBlank(lasttime)) {
            if (Integer.valueOf(10).equals(device.getType()) && device.getUseCount() <= device.getUsedCount()) {
                return denied("MEMBERSHIP_REQUIRED", "会员卡次数已用完");
            }
            Date newtime = DateUtils.addMin(new Date(), 120);
            lasttime = DateUtils.formatFull(newtime);
            redisUtils.set(ConfigConstant.DEVICE + device.getDeviceId(), lasttime, 120 * 60);
        }
        String createtime = DateUtils.formatFull(DateUtils.addMin(DateUtils.toDateFull(lasttime), -120));
        Map<String, Object> result = granted("MEMBERSHIP", device, qrCode);
        result.put("wtState", user.getWtState());
        result.put("nextValidityDate", validityDate);
        result.put("lasttime", lasttime);
        result.put("createtime", createtime);
        return result;
    }

    private Map<String, Object> appointmentDecision(UserInfoVo user, Map<String, Object> params,
                                                     PtPrivateAppointmentEntity appointment, int ttlSeconds) {
        int rand = new Random().nextInt(1000000);
        String qrCode = "hzjsf_" + user.getUserId() + "-" + System.currentTimeMillis()
                + "-" + String.valueOf(params.get("userLat")) + "-" + String.valueOf(params.get("userLng"))
                + "-" + rand + "-A" + appointment.getId();
        redisUtils.set(appointmentRandKey(appointment.getId(), user.getUserId()), rand, ttlSeconds);
        Map<String, Object> data = appointmentData(appointment);
        Map<String, Object> result = granted("APPOINTMENT", data, qrCode);
        result.put("appointment", data);
        return result;
    }

    private Map<String, Object> appointmentData(PtPrivateAppointmentEntity appointment) {
        LocalDateTime start = classStart(appointment);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("id", appointment.getId());
        data.put("appointmentNo", appointment.getAppointmentNo());
        data.put("productName", appointment.getProductName());
        data.put("coachName", appointment.getCoachName());
        data.put("storeName", appointment.getStoreName());
        data.put("storeId", appointment.getStoreId());
        data.put("date", appointment.getAppointmentDate());
        data.put("startTime", appointment.getStartTime());
        data.put("endTime", appointment.getEndTime());
        data.put("accessStart", format(DoorAccessTimePolicy.accessStart(start)));
        data.put("accessEnd", format(DoorAccessTimePolicy.accessEnd(start)));
        return data;
    }

    private boolean appointmentQrEnabled(Map<String, Object> config) {
        Object value = config == null ? null : config.get("appointment_qr_enabled");
        if (value == null) {
            throw new RRException("预约开门配置缺失");
        }
        String normalized = String.valueOf(value).trim();
        if ("1".equals(normalized)) {
            return true;
        }
        if ("0".equals(normalized)) {
            return false;
        }
        throw new RRException("预约开门配置非法，只允许0或1");
    }

    private int qrcodeValidSeconds(Map<String, Object> config) {
        Object value = config == null ? null : config.get("qrcode_valid");
        try {
            int seconds = Integer.parseInt(String.valueOf(value));
            if (seconds > 0) {
                return seconds;
            }
        } catch (Exception ignored) {
            // 统一在下方抛出明确配置错误。
        }
        throw new RRException("二维码有效时间配置非法");
    }

    private String calculateValidityDate(Device device) {
        try {
            Date now = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String endTime = df.format(now);
            String currentValidity = df.format(device.getValidityDate());
            if (df.parse(endTime).before(df.parse(currentValidity))) {
                endTime = currentValidity;
            }
            Long days = Long.valueOf(device.getValidity());
            return df.format(df.parse(endTime).getTime() + (days - 1) * 24L * 60L * 60L * 1000L);
        } catch (Exception e) {
            // 与旧 getOpenDoorQR 一致：续费提示日期计算失败不阻断有效会员卡开门。
            return null;
        }
    }

    private LocalDateTime classStart(PtPrivateAppointmentEntity appointment) {
        return LocalDateTime.of(LocalDate.parse(appointment.getAppointmentDate(), DATE_FMT),
                LocalTime.parse(appointment.getStartTime(), TIME_FMT));
    }

    private String format(LocalDateTime value) {
        return value.format(DATE_TIME_FMT);
    }

    private String appointmentRandKey(Long appointmentId, Long userId) {
        return ConfigConstant.APPOINTMENT_QR_RAND + appointmentId + ":" + userId;
    }

    private Map<String, Object> granted(String mode, Object data, String qrCode) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("granted", true);
        result.put("accessMode", mode);
        result.put("data", data);
        result.put("qrCode", qrCode);
        return result;
    }

    private Map<String, Object> denied(String reason, String message) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("granted", false);
        result.put("reason", reason);
        result.put("message", message);
        return result;
    }
}
