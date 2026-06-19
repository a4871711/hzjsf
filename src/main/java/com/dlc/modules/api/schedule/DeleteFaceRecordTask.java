package com.dlc.modules.api.schedule;

import com.dlc.modules.api.service.StoreService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/*删除七天之前的刷脸数据*/
public class DeleteFaceRecordTask {
    private org.slf4j.Logger log =  LoggerFactory.getLogger(getClass());

    @Autowired
    private StoreService storeService;

    public void deleteFaceRecord(){
        log.info("删除一天之前的刷脸数据task start...");
        int res = storeService.deleteFaceRecord();
        log.info("删除一天之前的刷脸数据task end...result:"+res);
    }
}
