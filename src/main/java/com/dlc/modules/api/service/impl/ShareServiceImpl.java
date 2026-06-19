package com.dlc.modules.api.service.impl;

import com.dlc.modules.api.dao.ShareMapper;
import com.dlc.modules.api.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/10/12/012
 */
@Service
@Transactional
public class ShareServiceImpl implements ShareService {

    @Autowired
    private ShareMapper shareMapper;

    @Override
    public Map<String, Object> share(Long shareType, Long userId) {
        return shareMapper.share(shareType, userId);
    }
}
