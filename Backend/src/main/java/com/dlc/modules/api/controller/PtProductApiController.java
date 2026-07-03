package com.dlc.modules.api.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.PtProduct;
import com.dlc.modules.api.service.PtProductApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 私教商品会员端只读浏览（可匿名访问，无需登录）。
 *
 * @author claude
 */
@RestController
@RequestMapping("/api/ptProduct")
public class PtProductApiController extends BaseController {

    @Autowired
    private PtProductApiService ptProductApiService;

    /**
     * 商品列表。仅返回已上架、未删除、未到下架时间的商品；可按商品类型/服务类型/门店过滤。
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return R.reError("分页信息不能为空");
        }
        Query query = new Query(params);
        List<PtProduct> list = ptProductApiService.queryList(query);
        int total = ptProductApiService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.reOk(pageUtil);
    }

    /**
     * 商品详情。下架/不存在返回失败提示。
     */
    @RequestMapping("/detail")
    public R detail(Long id) {
        if (id == null) {
            return R.reError("商品ID不能为空");
        }
        PtProduct product = ptProductApiService.queryObject(id);
        if (product == null) {
            return R.reError("商品不存在或已下架");
        }
        return R.reOk(product);
    }
}
