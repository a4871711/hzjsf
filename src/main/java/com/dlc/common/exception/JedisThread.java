/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: JedisThread
 * Author:   Administrator
 * Date:     2019/4/27 13:28
 * Description: 消费者线程类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.dlc.common.exception;

import com.dlc.common.constant.Constants;
import com.dlc.common.utils.ConsumerRedis;
import com.dlc.common.utils.JedisUtil;
import com.dlc.modules.api.dao.IncomePayDetailMapper;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.dao.UserWalletMapper;
import com.dlc.modules.api.dao.WalletDetailDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import redis.clients.jedis.Jedis;

/**
 * @author chenyuexin
 * @date 2019-04-27 13:28
 * @version 1.0
 */
public class JedisThread implements Runnable{
    final Logger logger = LoggerFactory.getLogger(JedisUtil.class);
    @Override
    public void run() {
        //JedisUtil.consumerMessage("withdraw");
        Jedis jedis = null;

        try {
            WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
            UserWalletMapper userWalletMapper = wac.getBean(UserWalletMapper.class);
            WalletDetailDao walletDetailDao = wac.getBean(WalletDetailDao.class);
            UserInfoMapper userInfoMapper = wac.getBean(UserInfoMapper.class);
            IncomePayDetailMapper incomePayDetailMapper = wac.getBean(IncomePayDetailMapper.class);
            jedis = JedisUtil.getJedis();
            ConsumerRedis consumerRedis = new ConsumerRedis(userWalletMapper, walletDetailDao, userInfoMapper, incomePayDetailMapper);
            jedis.subscribe(consumerRedis, Constants.CHANNEL);
        }catch (Exception e){
            logger.info("出队列时.......报错---->",e);
        }finally {
            JedisUtil.close(jedis);
        }
    }
}
