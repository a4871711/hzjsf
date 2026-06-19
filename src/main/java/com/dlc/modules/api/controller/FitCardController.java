package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.FitCard;
import com.dlc.modules.api.service.FitCardService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/11/011
 */
@RestController
@RequestMapping("/api/fitCard")//fitCard
public class FitCardController extends BaseController{

    @Autowired
    private FitCardService fitCardService;

    /**
     *  @Auther:YD
     *  @parameters:
     *  查询健身卡列表
     */
    @RequestMapping("/list")
    public R queryFitCardList(FitCard fitCard, HttpServletRequest request){
        Long userId = null;
        UserInfoVo user = getUserVoIgnore(request);
        if(user != null) {
            userId = user.getUserId();
        }
        return R.reOk(fitCardService.queryFitCardList(fitCard, userId));
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  健身卡详情
     */
    @RequestMapping("/info")
    public R queryFitCardInfo(Long id){
        return R.reOk(fitCardService.queryFitCardInfo(id));
    }

}
