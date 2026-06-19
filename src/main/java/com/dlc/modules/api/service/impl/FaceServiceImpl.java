package com.dlc.modules.api.service.impl;

import com.dlc.common.face.aip.face.AipFace;
import com.dlc.common.face.aip.face.FaceVerifyRequest;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.DeviceMapper;
import com.dlc.modules.api.dao.FaceIdentyMapper;
import com.dlc.modules.api.dao.FaceIdentyRecordMapper;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.entity.FaceIdenty;
import com.dlc.modules.api.entity.FaceIdentyRecord;
import com.dlc.modules.api.service.FaceService;
import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Component
public class FaceServiceImpl implements FaceService {
    private Logger log = LoggerFactory.getLogger(getClass());
    /**百度人脸识别APPID*/
    @Value("${baidu_appId}")
    public String BAIDU_APPID;
    /*人脸识别appkey*/
    @Value("${baidu_apiKey}")
    public String BAIDU_APIKEY;
    /*人脸识别secretkey*/
    @Value("${baidu_secretKey}")
    public String BAIDU_SECRETKEY;
    /*人脸照片分组id*/
    @Value("${baidu_groupId}")
    public String BAIDU_GROUPID;

    private AipFace aipFace;

    @Autowired
    private FaceIdentyMapper faceIdentyMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private FaceIdentyRecordMapper faceIdentyRecordMapper;

    @Override
    public R addUser(String image, String imageType, Long userId) {
        int res = 0;
        try {
            aipFace = new AipFace(BAIDU_APPID,BAIDU_APIKEY, BAIDU_SECRETKEY);
            try {
                //活体检测
                List<FaceVerifyRequest> input = new ArrayList<>();
                FaceVerifyRequest faceVerifyRequest = new FaceVerifyRequest(image, imageType);
                input.add(faceVerifyRequest);
                JSONObject ja = aipFace.faceverify(input);    //参考：https://ai.baidu.com/docs#/Face-Liveness-V3/top
                int jaCode = ja.optInt("error_code");
                if(jaCode == 0){  //成功
                    JSONObject jaResult = ja.optJSONObject("result");
                    if( null != jaResult ){
                        double face_liveness = jaResult.optDouble("face_liveness");
                        log.info("adduser活体比率face_liveness="+face_liveness);
                        if( face_liveness < 0.393241){   //低于推荐此数值，则可判断为非活体 推荐值0.393241 、0.649192
                            return R.reError("活体检测失败.请退出重试");
                        }
                    }
                }else{ //失败
                    return R.reError("活体检测失败.请退出重试");
                }
            } catch (Exception e) {
                log.info("adduser_活体检测失败...");
            }
            JSONObject jj = aipFace.addUser(image,imageType,BAIDU_GROUPID,userId.toString(),null);
            Integer error_code = jj.optInt("error_code");    //0 成功 其他失败
            String error_msg = jj.optString("error_msg");      //SUCCESS 成功
            JSONObject result = jj.optJSONObject("result");
            if(result == null){
                return R.reError("失败!");
            }
            String face_token = result.optString("face_token");
            String location = result.optString("location");
            //保存认证成功结果
            if(error_code == 0 && "SUCCESS".equals(error_msg)){
                FaceIdenty faceIdenty = new FaceIdenty();
                faceIdenty.setFaceToken(face_token);
                faceIdenty.setLocation(location);
                faceIdenty.setUserId(userId);
                faceIdentyMapper.insertSelective(faceIdenty);
                //更新用户认证状态为1：已完成认证
                res = userInfoMapper.updateFaceStatus(userId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(res <= 0){
            return R.reError("失败");
        }
        return R.reOk();
    }

    @Override
    public R search(String image, String imageType, String deviceNo) {
        List<Long> userIds = new ArrayList<>();
        try {
            aipFace = new AipFace(BAIDU_APPID,BAIDU_APIKEY, BAIDU_SECRETKEY);
            try {
                //活体检测
                List<FaceVerifyRequest> input = new ArrayList<>();
                FaceVerifyRequest faceVerifyRequest = new FaceVerifyRequest(image, imageType);
                input.add(faceVerifyRequest);
                JSONObject ja = aipFace.faceverify(input);    //参考：https://ai.baidu.com/docs#/Face-Liveness-V3/top
                int jaCode = ja.optInt("error_code");
                if(jaCode == 0){  //成功
                    JSONObject jaResult = ja.optJSONObject("result");
                    if( null != jaResult ){
                        double face_liveness = jaResult.optDouble("face_liveness");
                        log.info("活体比率face_liveness="+face_liveness);
                        if( face_liveness < 0.393241){   //低于推荐此数值，则可判断为非活体 推荐值0.393241 、0.649192
                            return R.reError("活体检测失败");
                        }
                    }
                }else{ //失败
                    return R.reError("活体检测失败");
                }
            } catch (Exception e) {
                log.info("search_活体检测失败...");
            }
            JSONObject jj = aipFace.search(image, imageType, BAIDU_GROUPID, null);
            int error_code = jj.optInt("error_code");    //0 成功 其他失败
            String error_msg = jj.optString("error_msg");      //SUCCESS 成功
            JSONObject result = jj.optJSONObject("result");
            if(result == null){
                return R.reError("失败");
            }
            //请求成功
            if(error_code == 0 && "SUCCESS".equals(error_msg)){
                JSONArray user_list = result.optJSONArray("user_list");
                for (Object obj : user_list) {
                    JSONObject jsonObject = (JSONObject)obj;
                    //人脸相似度大于80% 识别成功
                    if(jsonObject.optDouble("score") > 80){  //只要有一个相似度大于80%的即可
                        //userId = Long.valueOf(jsonObject.optString("user_id"));
                        userIds.add(Long.valueOf(jsonObject.optString("user_id")));
                        //break;
                    }
                }
            }
        } catch (Exception e) {
            log.info("***刷脸异常："+e);
        }

        //记录进门次数
        if(CollectionUtils.isNotEmpty(userIds)){
            //先查看该用户会员是否已经禁用/已转卡，如果已禁用返回0
                /*int forbidden = deviceMapper.queryIsForbidden(userId);
                if(forbidden == 0){return R.reError("无权限");}*/
            List<Long> userIdList = deviceMapper.queryIsForbiddenByIds(userIds);
            if(CollectionUtils.isEmpty(userIdList)){ //说明没有满足条件的用户
                return R.reError("无权限");
            }
            Long userId = userIdList.get(0);   //成功后默认去一个用户id进行记录
            try {
                //1.查询该用户有没有刷脸的记录
                int count = faceIdentyRecordMapper.selectRecordCount(userId);
                if(count > 0){
                    //有刷过，做删除操作
                    faceIdentyRecordMapper.deleteRecode(userId);
                }else{
                    //没有记录插入一条数据
                    FaceIdentyRecord faceIdentyRecord = new FaceIdentyRecord();
                    faceIdentyRecord.setDeviceNo(deviceNo);
                    faceIdentyRecord.setUserId(userId);
                    faceIdentyRecordMapper.insertSelective(faceIdentyRecord);
                    //更新累计刷脸次数（进和出只算一次）
                    deviceMapper.updateInOutNumByUId(userId, deviceNo);
                }
            } catch (Exception e) {
                log.info("***刷脸更新数据异常："+e);
            }
            return R.reOk();
        }

        return R.reError("失败");
    }

    @Override
    public int deleteUserFace(Long userId) {
        try {
            aipFace = new AipFace(BAIDU_APPID,BAIDU_APIKEY, BAIDU_SECRETKEY);
            JSONObject jr = aipFace.deleteUser(BAIDU_GROUPID, String.valueOf(userId), null);
            Integer error_code = jr.optInt("error_code");    //0 成功 其他失败
            String error_msg = jr.optString("error_msg");      //SUCCESS 成功
            if(error_code == 0 && "SUCCESS".equals(error_msg)){
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
