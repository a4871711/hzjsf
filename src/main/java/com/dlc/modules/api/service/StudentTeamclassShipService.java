package com.dlc.modules.api.service;

import com.dlc.modules.api.entity.StudentTeamclassShip;

import java.util.List;

/**
 * @Auther:YD
 * @Date: Creat in  2018/10/15/015
 */
public interface StudentTeamclassShipService {

    List<StudentTeamclassShip> selectByStoreId(Long storeId);

    int selectCountById(Long teamClassId, Long userId);

    int selectIfCardUserById(Long userId);
}
