package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.Advertising;
import com.dlc.modules.api.service.AdvertisingService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author LXK
 * @date 2018/9/11 10:56
 */
@RestController
@RequestMapping("/api/advertising")//advertising
public class ApiAdvertisingController extends BaseController{
    @Autowired
    private AdvertisingService advertisingService;

    /**
     * S商城广告展示
     * */
    @RequestMapping("/advertisingList")
    public R advertisingList(Advertising advertising){
        List<Map<String,Object>> list = advertisingService.advertisingList(advertising);
        return R.reOk(list);
    }

    /**
     * 广告详情
     * */
    @RequestMapping("/advertisingDetails")
    public R advertisingDetails(Long advId){
        Map<String,Object> map= advertisingService.advertisingDetails(advId);
        return R.reOk(map);
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  定制广告
     */
    @RequestMapping("/customizationAdv")
    public R selectCustomizationAdv(HttpServletRequest request){
        UserInfoVo userVo = getUserVo(request);
        Long userId = null;
        if (userVo != null) {
            userId = userVo.getUserId();
        }
        return R.reOk(advertisingService.customizationAdv(userId));
    }
}
