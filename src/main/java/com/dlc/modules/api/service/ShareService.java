package com.dlc.modules.api.service;

import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/10/12/012
 */
public interface ShareService {

    Map<String,Object> share(Long shareType, Long userId);
}
