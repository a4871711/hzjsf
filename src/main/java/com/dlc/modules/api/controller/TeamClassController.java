package com.dlc.modules.api.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.service.TeamClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/15/015
 */
@RestController
@RequestMapping("/api/teamClass")//teamClass
public class TeamClassController {

    @Autowired
    private TeamClassService teamClassService;

    /**
     *  @Auther:YD
     *  @parameters:
     *  团体课详情
     */
    @RequestMapping("/info")
    public R selectTeamClassInfo(Long id){
        return R.reOk(teamClassService.selectTeamClassInfoMap(id));
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  门店活动列表
     */
    @RequestMapping("/storeTeamClassList")
    public R selectStoreTeamClassList(@RequestParam Map<String,Object> params) throws ParseException {
        if (params.get("page")!=null&&Integer.valueOf((String) params.get("page"))<1){
            params.put("page",1);
        }
        Query query = new Query(params);
        List<Map<String,Object>> list = teamClassService.selectStoreTeamClassList(query);
        int total = teamClassService.queryTotal(query);
        PageUtils page = new PageUtils(list,total,query.getLimit(),query.getPage());

        return R.reOk().put("page",page);

    }
}
