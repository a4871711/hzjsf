package com.dlc.modules.api.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dlc.common.exception.RRException;
import com.dlc.common.utils.Constant;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.UserAddressMapper;
import com.dlc.modules.api.entity.UserAddress;
import com.dlc.modules.api.service.AddressService;
import com.dlc.modules.qd.utils.PhoneCodeVer;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-05-22 11:19
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private UserAddressMapper addressMapper;

    /*@Override
    public Remind queryObject(Long id) {
        return null;
    }*/

    @Override
    public List<UserAddress> queryList(Map<String, Object> map) {
        return addressMapper.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return 0;
    }

    @Override
    public void delete(Long id) {
        addressMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void deleteBatch(Long[] ids) {

    }

    @Override
    public R saveOrUpdate(UserAddress address, Long userId) {
        if(address == null){
            return R.reError("参数缺失");
        }
        try {
            if (address.getUserAddressId() != null) {
                //更新地址
                addressMapper.updateByPrimaryKeySelective(address);

            } else {
                //新增地址
                if (StringUtils.isBlank(address.getUserName()) || address.getUserName().length() > 50) {
                    return R.reError("请输入正确的姓名！");
                }
                if (StringUtils.isBlank(address.getPhone()) || !PhoneCodeVer.isPhoneNum(address.getPhone())) {
                    return R.reError("请输入正确的手机号");
                }
                if (StringUtils.isBlank(address.getProvince())) {
                    return R.reError("请输入省");
                }
                if (StringUtils.isBlank(address.getCity())) {
                    return R.reError("请输入市");
                }
                if (StringUtils.isBlank(address.getZone())) {
                    return R.reError("请输入区");
                }
                if (StringUtils.isBlank(address.getAddr()) || address.getAddr().length() > 50) {
                    return R.reError("请输入正确的详细地址，不超过50字");
                }
                //address.setCreateTime(new Date());
                address.setUserId(userId);
                address.setStatus((byte) Constant.AddressStatus.ADDRESS_USABLE.getValue());
                addressMapper.insertSelective(address);
                JSONObject object = new JSONObject();
                object.put("userId",userId);
                return R.reOk(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }

        return R.reOk();
    }


    @Override
    public void updateStatus(Long userAddressId) {
        UserAddress address = new UserAddress();
        address.setUserAddressId(userAddressId);
        address.setStatus((byte) Constant.AddressStatus.ADDRESS_UNUSABLE.getValue());
        addressMapper.updateByPrimaryKeySelective(address);
    }

    @Override
    public int updateIsdefault(Long userAddressId, Long userId) {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("userAddressId", userAddressId);
        queryMap.put("userId", userId);
        int res = addressMapper.updateIsdefaultId(queryMap);
        return res == 0? 0 : 1;
    }

    @Override
    public int deleteAddr(Long userAddressId) {
        int res = 0;
        try {
            //物理删除
            res = addressMapper.deleteByPrimaryKey(userAddressId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
        return res;
    }
}
