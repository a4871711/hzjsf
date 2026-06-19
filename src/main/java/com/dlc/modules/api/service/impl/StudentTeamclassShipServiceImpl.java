package com.dlc.modules.api.service.impl;

import com.dlc.modules.api.dao.DeviceMapper;
import com.dlc.modules.api.dao.StudentTeamclassShipMapper;
import com.dlc.modules.api.entity.StudentTeamclassShip;
import com.dlc.modules.api.service.StudentTeamclassShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Auther:YD
 * @Date: Creat in  2018/10/15/015
 */
@Service
@Transactional
public class StudentTeamclassShipServiceImpl implements StudentTeamclassShipService {

    @Autowired
    private StudentTeamclassShipMapper studentTeamclassShipMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Override
    public List<StudentTeamclassShip> selectByStoreId(Long storeId) {
        return studentTeamclassShipMapper.selectByStoreId(storeId);
    }

    @Override
    public int selectCountById(Long teamClassId, Long userId) {
        return studentTeamclassShipMapper.selectCountById(teamClassId, userId);
    }

    @Override
    public int selectIfCardUserById(Long userId) {
        return deviceMapper.queryIsCardUserById(userId);
    }
}
