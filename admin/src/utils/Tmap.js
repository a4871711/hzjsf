/*
 * 调用示例
 * Tmap 和 center初始化地图用的两个Promise
 * marker 和 mapListen前者用于获取回调点击设备点返回信息，后者用于监听点击地图和拖动地图
 * marker的 map为必传参数，需要在center的resolve里获取
 * async plugin(){
	 var qq = await plugin.Tmap()
	 const map = await plugin.center(local.lat,local.lng)
	 plugin.marker('',map,function(res){
		 
	 });
   }
 * 
 */

export const Tmap = (key='45TBZ-6SDCC-VZ625-AVOWK-BVLPV-6FFO4') => {//初始化调用
    return new Promise(function (resolve, reject) {
		window.init = function () {
		    resolve(qq)
		}
        var url=["https://map.qq.com/api/js?v=2.exp&callback=init&key="+key]
        for(var i in url){
        	var script = document.createElement("script");
	        script.type = "text/javascript";
	        script.src = url[i];
	        script.onerror = reject;
	        document.head.appendChild(script);
        }
    })
}

export const center = (lat=22.986708,lng=113.726580) => {
    return new Promise(function (resolve, reject) {
		var center = new qq.maps.LatLng(lat,lng);
		var map = new qq.maps.Map(document.getElementById('container'),{
			center: center,
			zoom: 13,
			disableDefaultUI: true,
		});
		var marker = new qq.maps.Marker({
			position: center,
			map: map,
			zIndex:20,
			Draggable:true,
			animation:qq.maps.MarkerAnimation.DOWN,
		});
		resolve({marker:marker,map:map})
    })
}

export let testPoint =  [{'lng':'113.72627934063','lat':'22.986694277283','macno':'1','address':'广东','name':'格子柜1'},{'lng':'113.72624912717','lat':'22.98658378132','macno':'2','address':'东莞','name':'格子柜2'},{'lng':'113.72659917941','lat':'22.986385617983','macno':'3','address':'高盛','name':'格子柜3'}];

import ic_mark from '@/assets/img/ic_bike.png';

export const marker = (point,map,cb=undefined) => { 
		for(var i in point){
			var logo1 = new qq.maps.LatLng(point[i].lat,point[i].lng);
			var size = new qq.maps.Size(35, 37);//根据你导出的UI图的实际尺寸 
			var logo = new qq.maps.Marker({
			    position: logo1,
			    map: map,
				animation:qq.maps.MarkerAnimation.DOWN,
				icon: new qq.maps.MarkerImage(ic_mark,size)
			});
			var label = new qq.maps.Label({
				position: logo1,
				map: map,
				content: point[i].name,
				offset: new qq.maps.Size(15, -30),
			});
			 //设置属性
			logo.name=point[i].name;
			logo.macno=point[i].macno;	
			logo.address=point[i].address;
			logo.lng=point[i].lng;
			logo.lat=point[i].lat;
			//获取标记的点击事件
			qq.maps.event.addListener(logo, 'click', function(e) {
				//console.log(e.target.lat+','+e.target.lng)//获取属性
				if(cb){
					cb(e.target)
				}
			});
		}	
}

export const mapListen = (map,cb=undefined,cb1=undefined) => {
	qq.maps.event.addListener(map, 'center_changed', function(e) {//监听拖动地图事件
	 if(cb1){
	 	cb1(e)
	 }
	})
	qq.maps.event.addListener(map, 'click', function(e) {//监听点击地图事件
	  //console.log(e)
	  if(cb){
	  	cb(e)
	  }
	})
}
