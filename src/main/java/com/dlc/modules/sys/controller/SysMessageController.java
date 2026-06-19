package com.dlc.modules.sys.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.MessageEntity;
import com.dlc.modules.sys.service.SysCoachService;
import com.dlc.modules.sys.service.SysWalletDetailCountService;
import com.dlc.modules.sys.service.SysWalletDetailService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/sys/message")
public class SysMessageController {
    @Autowired
    private SysWalletDetailCountService sysWalletDetailCountService;
    @Autowired
    private SysCoachService sysCoachService;

    @RequestMapping("/list")
    public R message() {
        List<MessageEntity> list = new ArrayList<>();
        List<MessageEntity> walletList = sysWalletDetailCountService.selectMsg();
        List<MessageEntity> coachList = sysCoachService.newMsg();
        list.addAll(walletList);
        list.addAll(coachList);
        Collections.sort(list, new Comparator<MessageEntity>() {
            @Override
            public int compare(MessageEntity o1, MessageEntity o2) {
                return o2.getCreateTime().compareTo(o1.getCreateTime());
            }
        });
        return R.ok().put("page", list);
    }

    @RequestMapping("/getCurrUserRole")
    public R getCurrUserRole() {
        return StringUtils.isBlank(ShiroUtils.getUserEntity().getStoreIds()) ? R.ok() : R.error();
    }
}
