<template>
	<u-popup :show="show" mode="top" @close="close" @open="open">
		<view class="area-wrap">
			<view class="flex">
				<view class="city flex_s font_size_28" @click="changeCityTab">
					<text>{{changeAddress.city || '请选择市'}}</text>
					<u-icon name="arrow-down" color="#666" size="16" v-if="isShowCity"></u-icon>
					<u-icon name="arrow-up" color="#666" size="16" v-else></u-icon>
				</view>
				<view class="city flex_s font_size_28" @click="changeAreaTab">
					<text>{{changeAddress.zone || '请选择区'}}</text>
					<u-icon name="arrow-down" color="#666" size="16" v-if="isShowArea"></u-icon>
					<u-icon name="arrow-up" color="#666" size="16" v-else></u-icon>
				</view>
			</view>
			
			<view v-if="isShowCity">
				<view class="flex font_size_26 clr_h">定位城市</view>
				<view class="flex curr-city font_size_26">
					<u-icon name="map" color="#666" size="14"></u-icon>
					<text class="orange_col">东莞市</text>
				</view>			
				<view class="flex font_size_26 clr_h" style="margin-bottom: 10rpx;">当前城市</view>
			</view>
		
			<view class="item-box" v-if="isShowCity">
				<view class="item" :class="index === changeAddressKey[1] ? 'active':''" v-for="(item,index) in cityList" @click="handleCity(index)">
					{{item.title}}
				</view>
			</view>
			
			<view class="item-box" v-if="isShowArea">
				<view class="item" :class="-1 === changeAddressKey[2] ? 'active':''" @click="handleArea(-1)">全{{changeAddress.city}}</view>
				<view class="item" :class="index === changeAddressKey[2] ? 'active':''" v-for="(item,index) in areaList" @click="handleArea(index)">
					{{item.title}}
				</view>
			</view>
		
		</view>
	</u-popup>
</template>

<script>	
	import {getAreaList} from '@/api/shop.js'
	export default {
		name:"area-list",
		data() {
			return {
				addressList: [],
				cityList: [],
				areaList: [],
				changeAddress: {
					province: '广东省',
					city: '',
					zone: ''
				},
				changeAddressKey: [],
				isShowCity: true, // 显示市
				isShowArea: false, // 显示区
			};
		},
		props:{
			show: {
				type: Boolean,
				default: false
			},
		},
		watch: {
			show(v){
				if(v){
					// 初始化数据
					this.changeAddress = {
						province: '广东省',
						city: '',
						zone: ''
					}
					this.isShowCity = true;
					this.isShowArea = false;
					this.getStoreAreaList();
				}
			}
		},
		methods:{
			// 选中区
			handleArea(index){
				this.changeAddressKey[2] = index;
				this.getChangeAddress();
			},
			// 选中市
			handleCity(index){
				this.changeAddressKey = [0, index];
				this.areaList = this.cityList[index].child;
				this.getChangeAddress();
				this.changeAreaTab();
			},
			// 切换市选择
			changeCityTab(){
				this.isShowCity = true;
				this.isShowArea = false;
			},
			// 切换区选择
			changeAreaTab(){
				if(!this.changeAddress.city){
					return;
				}
				this.isShowCity = false;
				this.isShowArea = true;
			},
			// 获取当前选中中文
			getChangeAddress(){
				let object = {
					province: '',
					city: '',
					zone: ''
				};
								
				if(this.changeAddressKey[0] || this.changeAddressKey[0] == 0){
					object.province = this.addressList[this.changeAddressKey[0]].title;
				}
				
				if(this.changeAddressKey[1] || this.changeAddressKey[1] == 0){
					object.city = this.addressList[this.changeAddressKey[0]].child[this.changeAddressKey[1]].title;
				}
				
				if(this.changeAddressKey[2] || this.changeAddressKey[2] == 0){
					if(this.changeAddressKey[2] == -1){
						this.$emit('update:address', JSON.parse(JSON.stringify(object)))
						object.zone = "全" + (this.addressList[this.changeAddressKey[0]].child[this.changeAddressKey[1]].title)
						this.$emit('hanleClose', false);
					}else{
						object.zone = this.addressList[this.changeAddressKey[0]].child[this.changeAddressKey[1]].child[this.changeAddressKey[2]].title;
						this.$emit('update:address', object)
						this.$emit('hanleClose', false);
					}
				}
				
				this.changeAddress = object;
			},
			// 获取门店地址
			getStoreAreaList(){
				getAreaList().then((r) => {
					this.addressList = r.list
					let province = r.list.find((item, index) => {
						return item.title == this.changeAddress.province
					});
					this.cityList =  province.child;
					this.changeAddressKey = [0];
				});
			},
			open() {
				console.log('open');
			},
			close() {
				// this.show = false
				this.$emit('hanleClose')
			}
		}
	}
</script>

<style lang="scss" scoped>
.area-wrap{
	padding: 40rpx;
	box-sizing: border-box;
}
.city{
	margin-right: 15rpx;
	margin-bottom: 45rpx;
}
.city text{
	margin-right: 10rpx;
}
.orange_col{
	color: #E15B00;
	margin-left: 10rpx;
}
.curr-city{
	margin-top: 43rpx;
	margin-bottom: 47rpx;
}
.item-box{	
	// margin-top: 43rpx;
	display: grid;
	grid-template-columns: 1fr 1fr 1fr 1fr;
	grid-template-rows: 1fr 1fr 1fr 1fr;
	grid-row-gap: 20rpx;
	grid-column-gap: 20rpx;	
}
.item-box .item{
	width: 155rpx;
	height: 65rpx;
	background: #f2f2f2;
	border-radius: 5rpx;
	color: #333333;
	font-size: 26rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}
.item-box .active{
	color: #ffffff;
	background: #E15B00;
}
</style>