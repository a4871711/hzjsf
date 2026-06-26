package com.dlc.modules.api.controller;

import com.dlc.common.utils.Constant;
import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.UserAddress;
import com.dlc.modules.api.service.AddressService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-05-22 11:02
 */
@RestController
@RequestMapping("/api/address")
public class AddressController extends BaseController {

    @Autowired
    private AddressService addressService;

    /**
     * 收货地址列表
     *
     * @param request
     * @return
     */
    @RequestMapping("/list")
    public R list(Long userAddressId, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", getUserId(request));
        //用户可用的收货地址
        if(userAddressId != null){
            map.put("userAddressId", userAddressId);
        }
        map.put("status", Constant.AddressStatus.ADDRESS_USABLE.getValue());
        List<UserAddress> addresses = addressService.queryList(map);
        return R.reOk(addresses);
    }

    /**
     * 添加/修改收货地址
     *
     * @param request
     * @param address
     * @return
     */
    @RequestMapping("/saveOrUpdate")
    public R saveOrUpdate(HttpServletRequest request, UserAddress address) {
        return addressService.saveOrUpdate(address, getUserId(request));
    }



    /**
     * 删除收货地址
     * @param userAddressId 收货地址ID
     * @return
     */
    @RequestMapping("/delete")
    public R deleteAddress(Long userAddressId) {
        if (userAddressId == null) {
           return R.reError("请添加参数");
        }
        //物理删除
        //addressService.updateStatus(userAddressId);
        int res = addressService.deleteAddr(userAddressId);
        if(res <= 0){ return R.reError("删除失败");}
        return R.reOk();
    }

    /**
     * 地址详细
     *
     * @param userAddressId
     * @return
     */
    @RequestMapping("/info")
    public R info(HttpServletRequest request, Long userAddressId) {
        if (userAddressId == null) {
            return R.reError("请添加参数");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("userId", getUserId(request));
        map.put("userAddressId", userAddressId);
        map.put("status", 1);    //默认查有效地址
        List<UserAddress> addresses = addressService.queryList(map);
        return R.reOk(addresses);
    }

    /**
     * 设置默认地址
     * @param request
     * @return
     */
    @RequestMapping("/updateIsdefault")
    public R updateIsdefault(Long userAddressId, HttpServletRequest request) {
        int res = addressService.updateIsdefault(userAddressId, getUserId(request));
        return res == 0? R.reError("设置失败") : R.reOk();
    }

    private boolean validate(UserAddress address) {
        return StringUtils.isBlank(address.getUserName()) || StringUtils.isBlank(address.getAddr()) || StringUtils.isBlank(address.getPhone()) || StringUtils.isBlank(address.getProvince());
    }
}
