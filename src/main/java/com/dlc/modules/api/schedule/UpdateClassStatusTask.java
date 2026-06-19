package com.dlc.modules.api.schedule;

import com.dlc.modules.api.service.ClassStatusService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 课程状态处理
 */
public class UpdateClassStatusTask {
    private org.slf4j.Logger log =  LoggerFactory.getLogger(getClass());

    @Autowired
    private ClassStatusService classStatusService;

    public void updateMemberStatus(){
        log.info("会员过期检测start...");
        int res = classStatusService.updateMemberStatus();
        log.info("会员过期检测end..."+res);
    }
}
