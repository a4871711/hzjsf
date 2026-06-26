package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.DateUtils;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.ArrangeClassMapper;
import com.dlc.modules.api.dao.CoachAppointmentMapper;
import com.dlc.modules.api.dao.StuPrivateclassShipMapper;
import com.dlc.modules.api.entity.ArrangeClass;
import com.dlc.modules.api.entity.StuPrivateclassShip;
import com.dlc.modules.api.service.AppointClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class AppointClassServiceImpl implements AppointClassService {

    @Autowired
    private ArrangeClassMapper arrangeClassMapper;

    @Autowired
    private CoachAppointmentMapper coachAppointmentMapper;

    @Autowired
    private StuPrivateclassShipMapper stuPrivateclassShipMapper;
    @Override
    public R appointClass(Map<String, Object> params, Long userId) {
        int res = 0;
        try {
            Long spsId = Long.parseLong((String) params.get("spsId"));
            //根据关系id查询课程学员信息
            StuPrivateclassShip spc = stuPrivateclassShipMapper.selectByKey(spsId);
            if(spc.getAskNumber() == 0){
                return R.reError("您购买的该课程已全部约完!");
            }
            Long coachId = Long.parseLong((String) params.get("coachId"));
            String dataJsonArray = (String) params.get("dataJsonArray");
            //获取十位的日期2018-10-11
            String dataTime = (String) params.get("dataTime");
            if(!StringUtils.isEmpty(dataTime)){
                if(dataTime.length() > 10){
                    dataTime = params.get("dataTime").toString().substring(0, 10);
                }
            }
            ArrangeClass arrangeClass = new ArrangeClass();
            arrangeClass.setSpsId(spsId);
            if(null != spc){
                arrangeClass.setCoachClassId(spc.getClassId());
                arrangeClass.setCoachId(spc.getCoachId());
                arrangeClass.setClassName(spc.getClassName());  //新加课程名称
                arrangeClass.setStudentId(spc.getStudentId());            //学员id
            }
            arrangeClass.setClassPlaceId(Long.parseLong((String) params.get("storeAddrId")));
            arrangeClass.setClassPlace((String) params.get("storeName"));                 //上课门店名称
            arrangeClass.setClassTime((String) params.get("dataTime"));
            arrangeClass.setClassDate(dataJsonArray);
            arrangeClass.setDuration(spc.getDuration());
            arrangeClass.setPrice(spc.getPrice());
            arrangeClass.setValidityDate(spc.getValidityDate());
            res = arrangeClassMapper.insertSelective(arrangeClass);
            if(res > 0){
                //成功后去更新 学员-私教课程关系表（可约节数-1）
                stuPrivateclassShipMapper.updateAskNumber(params);
            }
            //将教练约课时间替换掉（更新）
            Map<String,Object> map = new HashMap<>();
            map.put("coachId",coachId);
            map.put("appTime",dataTime);
            map.put("appDate",dataJsonArray);
            coachAppointmentMapper.updateCoachAppointment(map);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
        if(res <= 0){
            return R.reError("约课失败");
        }
        return R.reOk();
    }

    @Override
    public R appointDel(Long spsId) {
        //根据spsId删除约课
        int res = 0;
        try {
            res = arrangeClassMapper.deleteBySpsId(spsId);
            if(res > 0){
                stuPrivateclassShipMapper.deleteByPrimaryKey(spsId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
        if(res <= 0){
            R.reError("删除失败请重试");
        }
        return R.reOk();
    }
    //约课取消接口
    @Override
    public R appointCancel(Long arrangeClassId) {
        int res = 0;
        try {
            //先更新约课安排表的状态
            res = arrangeClassMapper.updateClassStatus(arrangeClassId, 2);
            //再退课程
            if(res > 0){
                stuPrivateclassShipMapper.updateAskNumberByAcId(arrangeClassId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
        if(res <= 0){
            return R.reError("取消约课失败");
        }
        return R.reOk();
    }
}
