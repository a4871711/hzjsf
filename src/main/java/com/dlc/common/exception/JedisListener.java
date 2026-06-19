/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: JedisListener
 * Author:   Administrator
 * Date:     2019/4/27 11:29
 * Description: 监听器
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.dlc.common.exception;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author chenyuexin
 * @date 2019-04-27 11:29
 * @version 1.0
 */
public class JedisListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //启动线程
        Thread jedisThread = new Thread(new JedisThread());
        jedisThread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
