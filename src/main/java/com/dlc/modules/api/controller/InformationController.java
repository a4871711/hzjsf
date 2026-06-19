package com.dlc.modules.api.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.service.InformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/10/10/010
 * 资讯
 */
@RequestMapping("api/information")
@RestController
public class InformationController extends BaseController {

    @Autowired
    private InformationService informationService;
    /**
     *  @Auther:YD
     *  @parameters:
     *  资讯详情
     */
    @RequestMapping("/list")
    public R queryInformationList(@RequestParam  Map<String,Object> params){
        Query query = new Query(params);
        //登录后为了处理活动天数，这里传一个userId
        List<Map<String,Object>> list = informationService.queryInformationList(query);
        int total = informationService.queryTotal(query);
        PageUtils page = new PageUtils(list,total,query.getLimit(),query.getPage());
        return R.reOk().put("page",page);
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  资讯详情
     */
    @RequestMapping("/info")
    public R queryInformationInfo(Long id){
        return R.reOk(informationService.queryInformationInfo(id));
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  S-live 咨询说
     */
    @RequestMapping("/sLiveList")
    public R querySliveList(){
        return R.reOk(informationService.querySliveList());
    }
    /**
     *  @Auther:YD
     *  @parameters:
     *  轮播图
     */
    @RequestMapping("/sowingMap")
    public R selectSowingMap(Integer infType){
        return R.reOk(informationService.selectSowingMap(infType));
    }
}
