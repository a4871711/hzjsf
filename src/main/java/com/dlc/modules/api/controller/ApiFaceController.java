package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.FaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author
 * @date
 * 人脸识别
 */
@RestController
@RequestMapping("/api/face")
public class ApiFaceController extends BaseController {
    @Autowired
    private FaceService faceService;

    //final static AipFace aipFace = new AipFace("14434925", "TuD8NXmlcuWFv497Z3yeCxid", "Fbfw72jvFIZ5AiKAx9LlUhcoelT30Ses");
    /**
     * 人脸注册接口
     *
     * @param image - 图片信息(**总数据大小应小于10M**)，图片上传方式根据image_type来判断
     * @param imageType - 图片类型 **BASE64**:图片的base64值，base64编码后的图片数据，需urlencode，编码后的图片大小不超过2M；**URL**:图片的 URL地址( 可能由于网络等原因导致下载图片时间过长)**；FACE_TOKEN**: 人脸图片的唯一标识，调用人脸检测接口时，会为每个人脸图片赋予一个唯一的FACE_TOKEN，同一张图片多次检测得到的FACE_TOKEN是同一个
     * param *groupId - 用户组id（由数字、字母、下划线组成），长度限制128B
     * param *userId - 用户id（由数字、字母、下划线组成），长度限制128B
     * param *options - 可选参数对象，key: value都为string类型
     * options - options列表:
     *   user_info 用户资料，长度限制256B
     *   quality_control 图片质量控制  **NONE**: 不进行控制 **LOW**:较低的质量要求 **NORMAL**: 一般的质量要求 **HIGH**: 较高的质量要求 **默认 NONE**
     *   liveness_control 活体检测控制  **NONE**: 不进行控制 **LOW**:较低的活体要求(高通过率 低攻击拒绝率) **NORMAL**: 一般的活体要求(平衡的攻击拒绝率, 通过率) **HIGH**: 较高的活体要求(高攻击拒绝率 低通过率) **默认NONE**
     * @return JSONObject
     */
    @RequestMapping("/register")
    public R addUser(String image, String imageType, HttpServletRequest request) {
        // String groupId, String userId, HashMap<String, String> options, 这几个参数先不传了
        return faceService.addUser(image, imageType,  getUserId(request));
    }
    /**
     * 人脸搜索接口
     *
     * @param image - 图片信息(**总数据大小应小于10M**)，图片上传方式根据image_type来判断
     * @param imageType - 图片类型 **BASE64**:图片的base64值，base64编码后的图片数据，需urlencode，编码后的图片大小不超过2M；**URL**:图片的 URL地址( 可能由于网络等原因导致下载图片时间过长)**；FACE_TOKEN**: 人脸图片的唯一标识，调用人脸检测接口时，会为每个人脸图片赋予一个唯一的FACE_TOKEN，同一张图片多次检测得到的FACE_TOKEN是同一个
     * param *groupIdList - 从指定的group中进行查找 用逗号分隔，**上限20个**
     * param *options - 可选参数对象，key: value都为string类型
     * options - options列表:
     *   quality_control 图片质量控制  **NONE**: 不进行控制 **LOW**:较低的质量要求 **NORMAL**: 一般的质量要求 **HIGH**: 较高的质量要求 **默认 NONE**
     *   liveness_control 活体检测控制  **NONE**: 不进行控制 **LOW**:较低的活体要求(高通过率 低攻击拒绝率) **NORMAL**: 一般的活体要求(平衡的攻击拒绝率, 通过率) **HIGH**: 较高的活体要求(高攻击拒绝率 低通过率) **默认NONE**
     *   user_id 当需要对特定用户进行比对时，指定user_id进行比对。即人脸认证功能。
     *   max_user_num 查找后返回的用户数量。返回相似度最高的几个用户，默认为1，最多返回20个。
     * @return JSONObject
     */
    @RequestMapping("/search")
    public R search(String image, String imageType, String deviceNo){
        //JSONObject JJ = aipFace.search(image, imageType, groupIdList, options);
        return faceService.search(image, imageType, deviceNo);
    }

}
