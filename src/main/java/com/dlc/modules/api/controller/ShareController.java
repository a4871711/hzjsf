package com.dlc.modules.api.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/10/12/012
 *  分享
 */
@RestController
@RequestMapping("/api/share")
public class ShareController {

    @Autowired
    private StoreService storeService;
    @Autowired
    private CoachService coachService;
    @Autowired
    private TeamClassService teamClassService;
    @Autowired
    private SportRecordService sportRecordService;
    @Autowired
    private AboutUsService aboutUsService;
    /**
     *  @Auther:YD
     *  @parameters: userLng 经度，userLat 纬度，storeId门店id

     *  门店详情
     */
    @RequestMapping("/storeInfo")
    public R queryStoreInfo(@RequestParam Map<String,Object> params){
        return R.reOk(storeService.queryStoreInfo(params));
    }

    /**
     *  @Auther:YD
     *  @parameters: storeId 门店id
     *  推荐教练
     */
    @RequestMapping("/recommendCoach")
    public R recommendCoach(Long storeId){
        //List<Map<String,Object>> list = coachService.recommendCoach(storeId);
        List<Map<String,Object>> list = coachService.recommendStoreCoach(storeId);
        return R.reOk(list);
    }

    /**
     *  @Auther:YD
     *  @parameters: storeId，page，limit
     *  门店活动列表
     */
    @RequestMapping("/storeTeamClassList")
    public R selectStoreTeamClassList(@RequestParam Map<String,Object> params) throws ParseException {
        Query query = new Query(params);
        List<Map<String,Object>> list = teamClassService.selectStoreTeamClassList(query);
        int total = teamClassService.queryTotal(query);
        PageUtils page = new PageUtils(list,total,query.getLimit(),query.getPage());
        if (list.size() == 0){
            return R.reError("该门店暂时没有活动！");
        }else {
            return R.reOk().put("page",page);
        }
    }


    /**
     * 运动分类列表
     * */
    @RequestMapping("/sportTypeList")
    public R sportTypeList(Integer sportType,Long userId){
        Map<String,Object> map = this.sportRecordService.querySportListByType(sportType,userId);
        return R.reOk(map);
    }

    /**
     * 运动分类列表
     * */
    @RequestMapping("/getSysFlag")
    public R getSysFlag(){
        return R.reOk(sportRecordService.getSysFlag());
    }
    /**
     * h5下载APP链接查询
     * */
    @RequestMapping("/getDownloadUrl")
    public R getDownloadUrl(){
        return R.reOk(aboutUsService.selectDownloadUrl());
    }
    /**
     * 协议查询
     * */
    @RequestMapping("/queryProtocol")
    public R queryProtocol(int type){
        return R.reOk(aboutUsService.queryProtocolByType(type));
    }
    /**
     * 协议查询
     * */
    @RequestMapping("/listProtocol")
    public R listProtocol(){
        return R.reOk(aboutUsService.listProtocol());
    }
}
