package com.dlc.modules.sys.controller;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.GoodsEntity;
import com.dlc.modules.sys.service.GoodsService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 商品控制类
 */
@RestController("SysGoodsController")
@RequestMapping("/sys/goods")
public class SysGoodsController {
	@Autowired
	private GoodsService goodsService;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:goods:list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);

		List<GoodsEntity> goodsList = goodsService.queryList(query);
		int total = goodsService.queryTotal(query);
		PageUtils pageUtil = new PageUtils(goodsList, total, query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("sys:goods:info")
	public R info(@PathVariable("id") Long id){
		GoodsEntity goods = goodsService.queryObject(id);
		return R.ok().put("Goods", goods);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("sys:goods:save")
	public R save(@RequestBody GoodsEntity goods){
		String imgZhu = goods.getImgUrl();
		if(imgZhu == ""){
			return R.error("请选择商品主图");
		}
		String tu = goods.getCarouselImgUrl();
		if(tu == ""){
			return R.error("商品轮播图至少选择一张");
		}
		String temp = goods.getImgUrl();
		String s = temp;
		goods.setImgUrl(s);
		//拼接域名carouselImgUrl
		String img= goods.getCarouselImgUrl();
		String [] array = img.split(",");
		String image = "";
		String con = "";
		for(int i = 0 ; i<array.length;i++){
			image = array[i];
			con += image+",";
		}
		String images = con.substring(0, con.length()-1);
		goods.setCarouselImgUrl(images);

		//拼接域名remarkImgUrl
		/*String img1 = goods.getRemarkImgUrl();
		String [] array1 = img1.split(",");
		String image1 = "";
		String con1 = "";
		for(int i = 0 ; i<array1.length;i++){
			image1 = array1[i];
			con1 += (ConfigConstant.PRO_VER_URL+image1)+",";
		}
		String images1 = con1.substring(0, con1.length()-1);
		goods.setRemarkImgUrl(images1);*/
		goods.setDeleteStatus(0);
		goods.setCreatedDate(new Date());
		int sum = goodsService.save(goods);
		if(sum>0){
			//GoodsEntity good = goodsService.queryObject(goods.getId());
			goodsService.updateGoodsId(goods.getId());
		}
		return R.ok();
	}


	@RequestMapping("/insertGoods")
	/*@RequiresPermissions("sys:goods:insertGoods")*/
	public R insertGoods(@RequestBody GoodsEntity goods){
		String img = goods.getImgUrl();
		if(img == ""){
			return R.error("请选择商品主图");
		}
		String temp = goods.getImgUrl();
		if(temp.indexOf(ConfigConstant.PRO_VER_URL)==-1){
			String s = temp;
			goods.setImgUrl(s);
		}
		goods.setStatus(1);
		goods.setDeleteStatus(0);
		goods.setCreatedDate(new Date());
		goodsService.insertGoods(goods);
		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("sys:goods:update")
	public R update(@RequestBody GoodsEntity goods){
		/*String imgZhu = goods.getImgUrl();
		if(imgZhu == ""){
			return R.error("请选择商品主图");
		}*/
		String tu = goods.getCarouselImgUrl();
		if(tu == ""){
			return R.error("商品轮播图至少选择一张");
		}
		String temp = goods.getImgUrl();
		if(temp.indexOf(ConfigConstant.PRO_VER_URL)==-1){
			String s = temp;
			goods.setImgUrl(s);
		}

		String img= goods.getCarouselImgUrl();
		String [] array = img.split(",");
		String images = "";
		String image = "";
		String con = "";
		for(int i = 0 ; i<array.length;i++){
			image = array[i];
			if(image.indexOf(ConfigConstant.PRO_VER_URL)==-1){
				con += (ConfigConstant.PRO_VER_URL+image)+",";
			}else{
				con+=image+",";
			}
		}
		images = con.substring(0, con.length()-1);
		goods.setCarouselImgUrl(images);
		goods.setCreatedDate(new Date());
		goodsService.update(goods);
		
		return R.ok();
	}
	/**
	 * 修改
	 */
	@RequestMapping("/updateDetail")
	public R updateDetail(@RequestBody GoodsEntity goods){
		String imgZhu = goods.getImgUrl();
		if(imgZhu == ""){
			return R.error("请选择商品主图");
		}

		goods.setCreatedDate(new Date());
		goodsService.updateDetail(goods);

		return R.ok();
	}
	
	/**
	 * 删除(全部)
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:goods:delete")
	public R delete(@RequestBody Long[] ids){
		Long[] goodsIds=goodsService.queryGoodsId(ids);
		//goodsService.deleteBatch(goodsIds);
		if (goodsIds != null) {
			for (Long goodsId : goodsIds) {
				goodsService.updateDeleteByGid(goodsId);
			}
		}
		return R.ok();
	}
	/**
	 * 删除（部分）
	 */
	@RequestMapping("/delete2")
	@RequiresPermissions("sys:goods:delete")
	public R delete2(@RequestBody Long[] ids){
		/*Long[] goodsIds=goodsService.queryGoodsId(ids);
		goodsService.deleteBatch(goodsIds);*/
        if (ids != null) {
            for (Long id : ids) {
                goodsService.updateDeleteById(id);
            }
        }
		return R.ok();
	}
	/**
	 * 列表
	 */
	@RequestMapping("/listDetail")
	public R listDetail(@RequestParam Map<String, Object> params){
		Query query = new Query(params);
		List<Map<String,Object>> goodsList = goodsService.selectGoodsDetailList(query);
		int total = goodsService.queryTotal(query);
		PageUtils pageUtil = new PageUtils(goodsList, total, query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}
	
}
