package com.dlc.modules.api.service;

import com.dlc.modules.api.entity.UserInfo;

public interface RongCloudService {

    void getRongCloudToken(UserInfo account);

    String getToken(String id);
}
