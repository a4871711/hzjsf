package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.Share;
import com.dlc.modules.api.service.ShareService;
import com.dlc.modules.api.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Auther:YD
 * @Date: Creat in  2018/9/15/015
 * 门店
 */
@RestController
@RequestMapping("/api/store")//store
public class StoreController extends BaseController{

    @Autowired
    private StoreService storeService;
    @Autowired
    private ShareService shareService;

    /**
     *  @Auther:YD
     *  @parameters: userLng，userLat,门店id
     *  门店详情
     */
    @RequestMapping("/info")
    public R queryStoreInfo(@RequestParam Map<String,Object> params){
        return R.reOk(storeService.queryStoreInfo(params));
    }
    /**
     *  @Auther:YD
     *  @parameters:
     *  门店推荐(点首页门店)
     */
    @RequestMapping("/recommendedStores")
    public R recommendedStores(@RequestParam Map<String,Object> params){
        return R.reOk(storeService.recommendedStores(params));
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  分享
     */
    @RequestMapping("/share")
    public R share(Long shareType, HttpServletRequest request){
        Map<String, Object> share = shareService.share(shareType, getUserId(request));
        if (share == null){
            share = new HashMap<>();
        }
        return R.reOk(share);
    }
}
