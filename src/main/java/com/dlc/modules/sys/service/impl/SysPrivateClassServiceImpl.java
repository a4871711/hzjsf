package com.dlc.modules.sys.service.impl;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.modules.sys.dao.SysPrivateClassDao;
import com.dlc.modules.sys.dao.SysSetDao;
import com.dlc.modules.sys.entity.SysDataMapEntity;
import com.dlc.modules.sys.entity.SysPrivateClassEntity;
import com.dlc.modules.sys.service.SysPrivateClassService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SysPrivateClassServiceImpl implements SysPrivateClassService {
    @Autowired
    private SysPrivateClassDao sysPrivateClassDao;
    @Autowired
    private SysSetDao sysSetDao;

    @Override
    public SysPrivateClassEntity queryObject(Long privateClassId) {
        return sysPrivateClassDao.queryObject(privateClassId);
    }

    @Override
    public List<SysPrivateClassEntity> queryList(Map<String, Object> map) {
        return sysPrivateClassDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysPrivateClassDao.queryTotal(map);
    }

    @Override
    public void save(SysPrivateClassEntity sysPrivateClassEntity) {
        String prf = ConfigConstant.PRO_VER_URL;

        //私教图加前缀
        String imgUrl = sysPrivateClassEntity.getImgUrl();
        String firstImgUrl = sysPrivateClassEntity.getFirstImgUrl();
        if(StringUtils.isNotBlank(firstImgUrl)){
            if(firstImgUrl.indexOf(prf)==-1){ //没有前缀
                firstImgUrl= prf + firstImgUrl;
                sysPrivateClassEntity.setFirstImgUrl(firstImgUrl);
            }
        }
        if(StringUtils.isNotBlank(imgUrl)){
            String[] urlList = imgUrl.split(",");
            String newUrl = "";
            for(int i=0; i<urlList.length; i++){
                if(i == urlList.length-1){
                    newUrl += urlList[i].indexOf(prf) == -1? prf+urlList[i]+"":urlList[i]+"";
                }else{
                    newUrl += urlList[i].indexOf(prf) == -1? prf+urlList[i]+",": urlList[i] + ",";
                }
            }
            sysPrivateClassEntity.setImgUrl(newUrl);
        }
        sysPrivateClassDao.save(sysPrivateClassEntity);
    }

    @Override
    public void update(SysPrivateClassEntity sysPrivateClassEntity) {
        //私教图加前缀
        String prf = ConfigConstant.PRO_VER_URL;
        String imgUrl = sysPrivateClassEntity.getImgUrl();
        String firstImgUrl = sysPrivateClassEntity.getFirstImgUrl();
        if(StringUtils.isNotBlank(firstImgUrl)){
            if(firstImgUrl.indexOf(prf)==-1){ //没有前缀
                firstImgUrl= prf + firstImgUrl;
                sysPrivateClassEntity.setFirstImgUrl(firstImgUrl);
            }
        }

        if(StringUtils.isNotBlank(imgUrl)){
            String[] urlList = imgUrl.split(",");
            String newUrl = "";
            for(int i=0; i<urlList.length; i++){
                    if(i == urlList.length-1){
                        newUrl += urlList[i].indexOf(prf) == -1? prf+urlList[i]+"":urlList[i]+"";
                    }else{
                        newUrl += urlList[i].indexOf(prf) == -1? prf+urlList[i]+",": urlList[i] + ",";
                    }

            }
            sysPrivateClassEntity.setImgUrl(newUrl);
        }
        sysPrivateClassDao.update(sysPrivateClassEntity);
    }

    @Override
    public void delete(Long privateClassId) {
        sysPrivateClassDao.delete(privateClassId);
    }

    @Override
    public int deleteBatch(Long[] privateClassIds) {
        int res = 0;
        //先删除教练课程关系表
        sysPrivateClassDao.deleteBatchCcShip(privateClassIds);
        //再删除等级价格关系表
        sysPrivateClassDao.deleteBatchGpShip(privateClassIds);
        res = sysPrivateClassDao.deleteBatch(privateClassIds);
        return res;
    }

    @Override
    public SysPrivateClassEntity queryLast(SysPrivateClassEntity entity) {
        return sysPrivateClassDao.queryLast(entity);
    }

    @Override
    public List<SysDataMapEntity> queryClassType() {
        return sysSetDao.queryClassClassifyL();
    }
}
