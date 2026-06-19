package com.dlc.modules.api.schedule;

import com.dlc.modules.api.service.PointsService;
import com.dlc.modules.api.service.SportRecordService;
import com.dlc.modules.sys.entity.SysDataMapEntity;
import com.dlc.modules.sys.service.SysSetService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 能量转换为积分
 */
public class EnergyToIntegralTask {
    private org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private SportRecordService sportRecordService;
    @Autowired
    private SysSetService sysSetService;
    @Autowired
    private PointsService pointsService;

    public void energyToIntegral() {
        //log.info("能量转换积分task start...");
        /*List<Map<String, Object>> classEnergy = sportRecordService.queryEnergy();
        List<SysDataMapEntity> tempList = sysSetService.queryCreditsetList(null);
        SysDataMapEntity tempEntity = tempList.get(0);
        BigDecimal nengliang = new BigDecimal(tempEntity.getVal());
        BigDecimal jifen = new BigDecimal(tempEntity.getNextLabel());
        BigDecimal bili = nengliang.divide(jifen, 2, BigDecimal.ROUND_HALF_DOWN);
        log.info("nengliang/jifen=bili-->" + bili);
        for (Map<String, Object> e : classEnergy) {
            double d=Double.parseDouble(e.get("kcal").toString()) / (bili.intValue());
            e.put("jifen",(int)d);
            //log.info("jifen" + e.get("kcal") + "userId->" + e.get("userId") + "jifen->" + e.get("jifen"));
            int res = pointsService.updatePointCountTask(e);
            //log.info("能量转换积分task end...result:" + res);
        }*/
    }
}
