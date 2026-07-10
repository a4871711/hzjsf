<template>
	<view class="card">
		<view class="top">
			<image src="/static/image/card_bg.png" alt="" class="imgbg" />
			<!-- https://shilijsf.shilisports.com/h5/bg_img/card_bg2.png -->
			<view class="text-box">
				<view class="flex">
					<image src="/static/image/renewal_i.png" alt="" class="img-icon" />
					<view>矢历会员</view>
				</view>
				<view class="name">{{myStore.storeName}}</view>
				<view class="flex_s">
					<view class="km">{{myStore.distance}}km</view>
					<!-- <view class="top-btn" @click="config.path('/pagesA/select_store/select_store?type=2')">切换门店</view> -->
				</view>
			</view>
		</view>
		<view class="icon-box" style="background-color: #ffefd5;">
			<view class="icon-title">
				<image src="/static/image/renewal_5.png" alt="" class="renewal_5" />
			</view>
			<view class="item-box">
				<view class="item-icon" v-for="(item,index) in iconList" :key="index">
					<image :src="item.icon" alt="" class="icon" />
					<view>{{item.title}}</view>
				</view>
			</view>
		</view>

		<!-- vip -->
		<view class="vip-box">
			<view class="index-title flex_s">
				<view>选择会员套餐</view>
				<view class="grey" @click="switchTab">
					<text>全国{{total}}家门店适用</text>
					<image src="/static/image/index_more.png" alt="" class="qh" />
				</view>
			</view>
			<!-- 会员卡 -->
			<view class="vip-card flex_s">
				<scroll-view class="scroll-view" scroll-x="true">
					<view class="item flex_col_center" :class="fitCardId == item.fitCardId ? 'select-border':''"
						v-for="(item, index) in cardList" :key="index" @click="handleSelect(item,index)">
						<!-- <view class="item-title">{{item.autoPay != 0 ?'连续包':''}}{{item.ctName}}</view> -->
						<view class="item-title">{{item.cardName||'--'}}</view>
						<view class="price">
							<text class="text">¥</text>{{item.isNewUser==1?(item.newUserPrice || item.cardPrice):item.cardPrice }}
						</view>

						<view class="btn flex_1" v-if="item.cardType != 10">{{item.validity}}天</view>
						<view class="btn flex_1" v-if="item.cardType === 10">{{item.useCount}}次</view>
					</view>
				</scroll-view>
			</view>

			<!-- 可用优惠券 -->
			<view class="discount flex_s" @click="jumpPageCoupons()">
				<view class="left">
					<image src="/static/image/renewal_coupons.png" alt="" class="icon_coupons" />
					<view class="title">优惠券管理</view>
					<!-- <view class="ky">0元可用</view> -->
				</view>
				<view class="right flex">
					<view v-if="couponPrice">
						<view>-¥{{couponPrice}}</view>
					</view>
					<view v-else>
						<view>请选择</view>
					</view>

					<image src="/static/image/my_icon-dr.png" alt="" class="icon-arrow" />
				</view>
			</view>

			<view class="renewal-bottom">
				<view class="tips-txt">
					<u-checkbox-group activeColor="#DD541A">
						<u-checkbox :checked="checked" shape="circle" activeColor="#DD541A" size="15" labelSize="13"
							label="我已阅读并同意" @change="getchecked"></u-checkbox><text class="text_red"
							@click="config.path('/pagesA/agreement/agreement?type=0')">《用户协议》</text>、<text
							class="text_red"
							@click="config.path('/pagesA/agreement/agreement?type=2')">《矢历连续包月协议》</text>
					</u-checkbox-group>
				</view>
				<view class="bottom-btn" v-if="price.toFixed(2) <= 0" style="opacity: 0.3;">
					<view class="btn-left">支付{{price.toFixed(2)}}元 续费会员</view>
					<view class="btn-right">立即购买</view>
				</view>
				<view class="bottom-btn" v-else>
					<view class="btn-left">支付{{price.toFixed(2)}}元 续费会员</view>
					<view class="btn-right" @click="hanleBuy">立即购买</view>
				</view>
			</view>
		</view>
		<view style="height: 300rpx;"></view>
		<!-- 立即购买弹窗 -->
		<package-details :show="show" @handleClose="handleClose" :couponId="couponId" :price="noCouponPrice"
			:storeAddrId="myStore.storeAddrId" :fitCardId="fitCardId"></package-details>

		<!-- 切换门店 -->
		<area-list :show="storeShow" :storeAreaList="storeAreaList" @hanleClose="hanleClose"></area-list>

		<!-- 提示弹窗 -->
		<u-modal :show="tipsShow" title="提示" :content='tipsContent' @confirm="tipsShow = false"></u-modal>

	</view>
</template>

<script>
	import packageDetails from '@/components/package-details.vue';
	import areaList from '@/components/area-list.vue';
	import {
		getMyStore,
		getfitCardList,
		getFitCardInfo,
		getStoreInfo,
	} from '@/api/index'
	import {
		getOpenDoorQR,
		createOrder,
		Wxpay
	} from '@/api/my.js'
	export default {
		data() {
			return {
				iconList: [ // 图标列表
					{
						title: '免费用器械',
						icon: '/static/image/renewal_01.png'
					},
					{
						title: '超多门店通用',
						icon: '/static/image/renewal_02.png'
					},
					{
						title: '24小时营业',
						icon: '/static/image/renewal_03.png'
					},
					{
						title: '1v1入门指导',
						icon: '/static/image/renewal_04.png'
					},
					{
						title: '免费测量',
						icon: '/static/image/renewal_05.png'
					},
				],
				show: false,
				checked: false,
				storeId: null,
				myStore: {}, //我的门店
				latitude: '',
				longitude: '',
				total: this.$store.state.total, //门店总数
				package: 0, //当前选中套餐
				cardList: [], //会员套餐
				price: 0, //购买价格 用于页面底部显示
				noCouponPrice: 0, // 未选优惠券的价格,传给后端
				fitCardId: null, //卡片id
				couponId: '', //优惠券id
				fitCardRow: {}, // 选中套餐
				couponPrice: null, // 优惠券面值
				couponInfo: null, // 优惠券信息		
				tipsContent: '', //弹窗提示信息
				tipsShow: false, //是否显示提示弹窗
				userVipData: {}, //用户会员卡数据
				openVip: '', //判断点击开通会员和详情点进来的情况，有的情况下就是开通点进来的否者就是详情
				itemFitCardId: null, //用于判断外部套餐列表点击第几个
				single: false, //true=只展示 itemFitCardId 这一张卡(权益卡详情页"可购买会员卡"跳转专用),不展示该门店其它套餐

			};
		},
		components: {
			packageDetails,
			areaList,
		},
		onLoad(options) {
			if (options) {
				this.openVip = options.openVip
			}
			if (options.fitCardId) {
				this.itemFitCardId = options.fitCardId
				// console.log(options.fitCardId, 'this.fitCardRow = item;')
			}
			this.single = options.single == '1'
			console.log(this.itemFitCardId, 'onLoad')
			this.storeId = options.id;
			// 用完了删除掉
			uni.setStorageSync('coupon_info', null);
		},
		onShow(options) {
			console.log(this.itemFitCardId, 'show')
			this.package = 0;
			this.getMyStore();
			this.getStore(); //获取门店详情信息		
			this.couponInfo = uni.getStorageSync('coupon_info'); // 获取选中优惠券
			console.log(this.couponInfo, 'lll')
			this.checkCoupon();
			this.getUserVipInfo();
		},
		methods: {
			// 跳转优惠券
			jumpPageCoupons() {
				console.log(this.myStore, 'llllllll')
				// this.config.path('/pagesA/coupon/coupon')
				// uni.navigateTo({
				// 	url: '/pagesA/coupon/coupon' +
				// 		`?storeAddrId=${this.myStore.storeAddrId}&fitCardId=${this.fitCardId}&paySum=${this.price}&storeId=${this.storeId}`,
				// })
				uni.navigateTo({
					url: '/pagesA/coupon/coupon' +
						`?storeAddrId=${this.myStore.storeAddrId}&fitCardId=${this.fitCardId}&paySum=${this.noCouponPrice}&storeId=${this.storeId}`,
				})
			},
			// 首页门店
			getMyStore() {
				// console.log(this.$store.state.latilongi.latitude,this.$store.state.latilongi.longitude,'金纬度')
				let that = this;
				let data = {
					offset: 0,
					limit: 1,
					userLng: that.$store.state.latilongi.longitude,
					userLat: that.$store.state.latilongi.latitude,
				}
				getMyStore(data).then((res) => {
					let store = res.data[0];
					// store.storeImgUrl = store.storeImgUrl.split(',').map(url => url.trim());
					store.distance = parseFloat((store.distance / 1000).toFixed(2));
					if (that.openVip) {
						that.myStore = store
						that.getfitCardList(store.storeAddrId);
					}
				});
			},
			// 门店详情
			getStore() {
				let that = this;
				let data = {
					storeId: that.storeId,
					userLng: that.$store.state.latilongi.longitude,
					userLat: that.$store.state.latilongi.latitude,
				}
				getStoreInfo(data).then((res) => {
					let store = res.info;
					store.distance = parseFloat((store.distance / 1000).toFixed(2));
					that.myStore = store;
					that.getfitCardList(store.storeAddrId);
				});
			},
			// 验证是否使用优惠券， 如果使用就扣减对应金额
			checkCoupon() {
				if (this.couponInfo) {
					this.couponId = this.couponInfo.couponId;
					// this.couponPrice = parseInt(this.couponInfo.couponPrice.replace(/0*$/, ''), 10);
					this.couponPrice = Number(this.couponInfo.couponPrice);
					console.log(this.couponInfo, 'this.couponInfo')
					this.price = (this.price - this.couponPrice <= 0) ? 0 : this.price - this.couponPrice;
					console.log(this.price, 'this.price')
				} else {
					this.couponId = '';
				}
			},
			// 立即购买
			hanleBuy() {
				const that = this
				if (this.checked) {
					console.log('this.fitCardRow===', this.fitCardRow)
					// 选中次卡判断
					if (this.isVip && this.fitCardRow.cardType == 10 && this.currentType != 10) {
						this.tipsShow = true;
						this.tipsContent = '尊敬的 [年 / 月 / 季] 卡会员，您的卡还未用完，为保权益精准，暂不支持购次卡，感谢理解！'
						return;
					}
					// 选中月卡，年卡等判断
					if (this.isVip && this.fitCardRow.cardType != 10 && this.currentType == 10 &&
						(this.userVipData.useCount - this.userVipData.usedCount) > 0) {
						this.tipsShow = true;
						this.tipsContent = '尊敬的次卡会员，您尚有未用完的次卡次数，待次卡用完再考虑升级吧，感谢理解！'
						return;
					}

					if (this.fitCardRow.autoPay > 0) {
						this.show = true;
					} else {
						let data = {
							paySum: this.noCouponPrice,
							fitCardId: this.fitCardId,
							couponId: this.couponId,
							storeAddressId: this.myStore.storeAddrId
						};
						createOrder(data).then((res) => {
							if (res.code == 1) {
								Wxpay({
									orderNo: res.data.orderNo,
									// paySum:0.01,
									paySum: res.data.paySum
								}).then((r) => {
									if (r.code == 1) {
										uni.requestPayment({
											appId: r.params.appId,
											nonceStr: r.params.nonceStr,
											package: r.params.package,
											paySign: r.params.paySign,
											signType: r.params.signType,
											timeStamp: r.params.timeStamp,
											success: e => {
												console.log(e)
												this.config.Toast('微信支付成功');
												setTimeout(() => {

													uni.redirectTo({
														url: '/pagesA/card_record/card_record'
													});
												}, 1000)
											},
											fail: err => {
												console.log('微信支付失败')
											}
										})
									} else {
										that.config.Toast(r.msg)
									}

								})
							}
						}).catch((e) => {
							// -38 须先购买权益卡等业务码:createOrder 封装对 code!=1 一律 reject,必须接住提示
							that.config.Toast((e && e.message) || '下单失败');
						});
					}

				} else {
					this.config.Toast('请先同意协议！')
				}

			},
			// 切换选择套餐
			handleSelect(item, index) {
				console.log(item.newUserPrice, item.cardPrice, '11')
				this.couponId = '';
				this.couponInfo = null;
				this.couponPrice = null;
				this.noCouponPrice = item.isNewUser == 1 ? (item.newUserPrice || item.cardPrice) : item.cardPrice;
				this.fitCardRow = item;
				this.package = index;
				this.price = item.isNewUser == 1 ? (item.newUserPrice || item.cardPrice) : item.cardPrice;
				this.fitCardId = item.fitCardId;
				this.cardType = item.cardType; //当前卡片类型
				//这是防止选优惠券后重置进来的选择
				this.itemFitCardId = item.fitCardId
				console.log(item, '健身房')
			},
			getchecked(val) {
				this.checked = val
			},
			handleClose() {
				this.show = false
			},
			// 健身卡列表
			getfitCardList(storeAddrId) {
				this.cardList = []
				console.log('this.fitCardId', this.fitCardId);
				// this.fitCardId = '';
				let data = {
					storeId: storeAddrId
				}
				getfitCardList(data).then((res) => {
					let list = (res.data || []).map((item) => {
						return {
							...item,
							cardPrice: item.cardPrice
						}
					});
					if (this.single && this.itemFitCardId) {
						// 权益卡详情页"可购买会员卡"跳转专用:只展示这一张卡,不展示该门店其它套餐
						list = list.filter((item) => item.fitCardId == this.itemFitCardId);
						if (!list.length) {
							// 绑定卡未挂在当前门店的套餐列表(storeAddrIds/showStoreAddrIds 过滤)时,
							// 按 fitCardId 兜底拉单卡,避免空列表下 fitCardRow=undefined 直接报错
							getFitCardInfo({
								id: this.itemFitCardId
							}).then((r) => {
								this.cardList = r.data ? [r.data] : [];
								this.applyCardSelection();
							}).catch(() => {
								this.applyCardSelection();
							});
							return;
						}
					} else {
						// 普通购卡入口不展示权益类型会员卡(cardNature=1 只能从权益卡详情页购买),
						// 口径与首页/门店页一致,避免选中后下单才被后端 -38 拒绝
						list = list.filter((item) => Number(item.cardNature) !== 1);
					}
					this.cardList = list;
					this.applyCardSelection();
				});
			},
			// 套餐列表就绪后统一做默认选卡+价格初始化(getfitCardList 主路径与 single 兜底路径共用)
			applyCardSelection() {
				let row = this.cardList[0];
				console.log('row==', row)
				if (this.fitCardId) {
					row = this.cardList.find((item) => {
						return item.fitCardId === this.fitCardId;
					});
				}

				if (!row) {
					row = this.cardList[0];
				}
				if (!row) {
					// 列表为空且单卡兜底也失败:不再初始化价格,避免 undefined 解引用
					this.config.Toast('该会员卡当前不可购买');
					return;
				}
				// 外部点击逃禅进入参数复制
				if (this.itemFitCardId) {
					this.fitCardId = this.itemFitCardId
					// 找不到指定卡时退回默认卡,防 undefined 解引用
					this.fitCardRow = this.cardList.find((item) => {
						return item.fitCardId == this.fitCardId;
					}) || row;
					this.noCouponPrice = this.fitCardRow.isNewUser == 1 ? (this.fitCardRow.newUserPrice || this
						.fitCardRow.cardPrice) : this.fitCardRow.cardPrice
					this.price = this.fitCardRow.isNewUser == 1 ? (this.fitCardRow.newUserPrice || this
						.fitCardRow.cardPrice) : this.fitCardRow.cardPrice;
					console.log(this.fitCardRow, 'this.fitCardRow')
				} else {

					// 页面初始化价格
					this.price = row.isNewUser == 1 ? (row.newUserPrice || row.cardPrice) : row.cardPrice;
					this.fitCardId = row.fitCardId;
					this.noCouponPrice = row.isNewUser == 1 ? (row.newUserPrice || row.cardPrice) : row
						.cardPrice
					this.fitCardRow = row

				}

				// 查询优惠券
				this.checkCoupon();
			},
			// 跳门店
			switchTab() {
				uni.switchTab({
					url: '/pages/shop/shop'
				});
			},
			// 获取会员信息
			getUserVipInfo() {
				getOpenDoorQR({
					userLng: this.$store.state.latilongi.longitude,
					userLat: this.$store.state.latilongi.latitude,
				}).then((r) => {
					this.userVipData = r.data;
					if (r.code == 1) {
						this.isVip = true;
						this.currentType = r.data.type; //已买卡的类型
					} else {
						this.isVip = false;
					}
				});
			},
		}
	}
</script>

<style lang="scss">
	.card {
		text-align: center;

		.top {
			height: 150rpx;
			background: #141414;
			position: relative;

			.km {
				font-size: 24rpx;
			}

			.top-btn {
				width: 164rpx;
				height: 60rpx;
				background: linear-gradient(90deg, #7E4F0C, #5B3803);
				border-radius: 30rpx;
				border: 1px solid #7C4E0C;
				font-weight: 500;
				font-size: 27rpx;
				color: #DDD4C7;
				line-height: 50rpx;
				text-align: center;
				line-height: 60rpx;
			}

			.imgbg {
				height: 315rpx;
				position: absolute;
				top: 0;
				left: 50%;
				width: 83%;
				transform: translate(-50%);
			}

			.text-box {
				color: #5A2809;
				position: absolute;
				z-index: 2;
				width: 75%;
				top: 70%;
				left: 50%;
				transform: translate(-50%);
				text-align: left;

				// background-image: url();
				.img-icon {
					width: 23rpx;
					height: 36rpx;
					margin-right: 10rpx;
				}

				.name {
					font-size: 40rpx;
					color: #5A2809;
					font-weight: 500;
					margin: 10rpx 0 20rpx 0;
				}
			}
		}

		.icon-box {
			padding-bottom: 75rpx;

			.renewal_5 {
				width: 317rpx;
				height: 29rpx;
				padding-top: 200rpx;
				margin-bottom: 36rpx;
			}

			.item-box {
				display: flex;
				justify-content: space-around;
				width: 670rpx;
				margin: auto;

				.item-icon {
					display: flex;
					flex-direction: column;
					align-items: center;
					font-size: 23rpx;
					color: #A0A0A0;

					.icon {
						width: 85rpx;
						height: 85rpx;
						margin-top: 14rpx;
						margin-bottom: 10rpx;
					}
				}
			}
		}

		.index-title {
			font-size: 30.32rpx;
			color: #000;
			font-weight: 800;
			margin-bottom: 38.41rpx;

			.qh {
				width: 21.23rpx;
				height: 18.19rpx;
				margin-left: 19.2rpx;
			}

			.grey {
				color: #999999;
				font-size: 24.26rpx;
				font-weight: 500;
			}
		}

		.vip-box {
			background-color: #fff;
			border-radius: 30rpx 30rpx 0 0;
			padding: 30rpx 40rpx;
			box-sizing: border-box;
			margin-top: -20rpx;

			.vip-card {
				.scroll-view {
					white-space: nowrap;
					width: 100%;
					padding-bottom: 30px;
					text-align: left; //.card 根容器是 text-align:center,卡片是 inline-block 会继承居中,单张卡时居中;这里强制靠左

					.select-border {
						border: 2px solid #a6937a;
					}

					.item {
						display: inline-block;
						text-align: center; //外层为让单卡靠左设了 text-align:left,卡内文字要恢复居中
						width: 174rpx;
						height: 250rpx;
						background: #fff7e3;
						border-radius: 20rpx;
						box-sizing: border-box;
						margin-right: 18rpx;

						.item-title {
							font-size: 30rpx;
							color: #67451C;
							font-weight: 500;
							padding-top: 34rpx;
							box-sizing: border-box;
						}

						.price {
							font-weight: 800;
							font-size: 50.54rpx;
							color: #67451C;
							line-height: 34rpx;
							margin-top: 38rpx;
							margin-bottom: 49rpx;

							.text {
								font-size: 30.32rpx;
							}
						}

						.btn {
							font-size: 20rpx;
							color: #75532A;
						}
					}
				}
			}
		}

		.discount {
			height: 106rpx;
			background: #f2f2f2;
			border-radius: 20rpx;
			padding: 36rpx;
			box-sizing: border-box;

			.left {
				display: flex;
				align-items: center;

				.icon_coupons {
					width: 33rpx;
					height: 30rpx;
				}

				.title {
					font-weight: bold;
					font-size: 32rpx;
					color: #333333;
					margin: 0 17rpx 0 13rpx;
				}

				.ky {
					font-size: 24rpx;
					color: #333333;
				}
			}

			.right {
				color: #FF6E20;
				font-weight: bold;

				.icon-arrow {
					width: 10rpx;
					height: 17rpx;
					margin-left: 13rpx;
				}
			}
		}

		.tips-txt {
			margin-top: 60rpx;
			font-size: 24rpx !important;
			color: #999999;

			.u-checkbox-group--row {
				justify-content: center;
			}

			.text_red {
				color: #DD541A;
			}
		}

		.renewal-bottom {
			margin-top: 50rpx;
			position: fixed;
			bottom: 30rpx;
			width: 90%;

			.bottom-btn {
				height: 92rpx;
				background-repeat: no-repeat;
				background-image: url('https://shilijsf.shilisports.com/h5/bg_img/renewal_btnbg.png');
				background-size: 100% 100%;
				display: flex;
				align-content: center;
				align-items: center;
				margin-top: 38rpx;

				.btn-left {
					width: 65%;
					color: #FFFFFF;
					font-size: 30rpx;
				}

				.btn-right {
					width: 35%;
					font-size: 36rpx;
					color: #5E3A04;
					font-weight: bold;
				}
			}
		}

	}
</style>