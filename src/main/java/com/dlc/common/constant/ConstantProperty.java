/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ConstantsCommon
 * Author:   Administrator
 * Date:     2018/9/5 14:49
 * Description: 公共配置文件读取属性
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.dlc.common.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-09-05 14:49
 */
@Component
public class ConstantProperty {
    /**
     * 项目地址
     */
    @Value("${project_url}")
    public String PROJECT_URL;

    /**
     * 融云APP_KEY
     */
    @Value("${rong_cloud_app_key}")
    public String RONG_CLOUD_APP_KEY;

    /**
     * 融云APP_SECRET
     */
    @Value("${rong_cloud_app_secret}")
    public String RONG_CLOUD_APP_SECRET;

    /**
     * 助通用户名
     */
    @Value("${zt_username}")
    public String ZT_USERNAME;

    /**
     * 助通密码
     */
    @Value("${zt_password}")
    public String ZT_PASSWORD;



}
