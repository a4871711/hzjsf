package com.dlc.modules.api.service;

import com.dlc.common.utils.R;

public interface FaceService {
    /*人脸认证*/
    R addUser(String image, String imageType, Long userId);
    /*人脸匹配搜索*/
    R search(String image, String imageType, String deviceNo);
    /*人脸删除*/
    int deleteUserFace(Long userId);
}
