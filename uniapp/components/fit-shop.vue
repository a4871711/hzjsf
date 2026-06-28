<template>
	<u-popup :show="show" mode="top" @close="close" @open="open">
		<view class="fit-wrap">
			<view v-for="(item, index) in addressList">
				<view class="city"><text>{{item.title}}</text><u-icon name="arrow-down-fill" color="#909399"
						size="8"></u-icon></view>
				<view class="area-box">
					<view class="left">
						<view class="item" v-for="(iitem, iindex) in item.child" @click="handleCity(index, iindex)">
							{{iitem.title}}</view>
					</view>
					<view class="right">
						<view class="item" v-for="(val, key) in areaList" @click="handleArea(key)">{{val.title}}</view>
					</view>
				</view>
			</view>
		</view>
	</u-popup>
</template>

<script>
	import {
		getAreaList
	} from '@/api/shop.js'
	export default {
		name: "fit-shop",
		data() {
			return {
				addressList: [],
				areaList: [],
				addressKey: []
			};
		},
		props: {
			show: {
				type: Boolean,
				default: false
			},
			address: Object,
			default: {}
		},
		watch: {
			show(v) {
				if (v) {
					this.getArea();
				}
			}
		},
		methods: {
			// 获取当前选中中文
			getChangeAddress() {
				let object = {
					province: '',
					city: '',
					zone: ''
				};

				if (this.addressKey[0] || this.addressKey[0] == 0) {
					object.province = this.addressList[this.addressKey[0]].title;
				}

				if (this.addressKey[1] || this.addressKey[1] == 0) {
					object.city = this.addressList[this.addressKey[0]].child[this.addressKey[1]].title;
				}

				if ((this.addressKey[2] || this.addressKey[2] == 0) && this.addressKey[2] - 1 >= 0) {
					object.zone = this.addressList[this.addressKey[0]].child[this.addressKey[1]].child[this.addressKey[2] -
						1].title;
				}

				this.$emit('update:address', object)
				this.$emit('hanleClose', false);
			},
			// 选中区
			handleArea(k) {
				this.addressKey[2] = k;
				this.getChangeAddress();
			},
			// 根据市显示区
			handleCity(parentIndex, index) {
				this.areaList = JSON.parse(JSON.stringify(this.addressList[parentIndex].child[index].child))
				this.areaList.unshift({
					title: `全${this.addressList[parentIndex].child[index].title}`
				})
				this.addressKey = [parentIndex, index];
				// this.getChangeAddress();
			},
			// 获取地址信息
			getArea() {
				getAreaList().then((r) => {
					this.addressList = r.list
					// 初始化数据
					this.handleCity(0, 0);
				});
			},
			open() {
				console.log('open');
			},
			close() {
				this.$emit('hanleClose')
			}
		}
	}
</script>

<style lang="scss" scoped>
	.fit-wrap {
		font-size: 28rpx;

		.city {
			color: #E15B00;
			width: 25%;
			text-align: center;
			line-height: 80rpx;
			display: flex;
			justify-content: center;

			text {
				margin-right: 5rpx;
			}
		}

		.area-box {
			display: flex;

			.left {
				width: 25%;
				height: 100%;
				background: #f2f2f2;
				display: flex;
				flex-direction: column;

				.item {
					color: #666666;
					line-height: 70rpx;
					text-align: center;
				}
			}

			.right {
				width: 75%;
				color: #666666;
				background-color: #fff;
				display: flex;
				flex-direction: column;

				.item {
					display: flex;
					padding-left: 30rpx;
					line-height: 75rpx;
				}
			}
		}

	}
</style>