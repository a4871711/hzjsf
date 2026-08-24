<template>
	<view class="course-page">
		<view class="store-row" @click="selectStore">
			<u-icon name="map-fill" color="#FF541A" size="19"></u-icon>
			<text class="store-name">{{ currentStore.storeName || '选择门店' }}</text>
			<u-icon name="arrow-down" color="#9B9CA5" size="14"></u-icon>
		</view>

		<view class="search-box">
			<u-icon name="search" color="#A8A9B2" size="21"></u-icon>
			<input v-model="keyword" confirm-type="search" placeholder="搜索课程" placeholder-class="search-placeholder"
				@confirm="applySearch" />
			<view class="clear-search" v-if="keyword" @click="clearSearch">清除</view>
		</view>

		<scroll-view class="kind-tabs" scroll-x>
			<view class="kind-tab" :class="{ active: courseKind === item.value }" v-for="item in courseKinds"
				:key="item.value" @click="changeCourseKind(item.value)">
				<text>{{ item.label }}</text>
				<view class="active-line"></view>
			</view>
		</scroll-view>

		<view class="filter-row">
			<picker class="filter-picker" :range="categoryOptions" range-key="label" :value="categoryIndex"
				@change="onCategoryChange">
				<view class="filter-pill">
					<text class="filter-label">{{ categoryOptions[categoryIndex].label }}</text>
					<u-icon class="filter-arrow" name="arrow-down" color="#A2A3AB" size="12"></u-icon>
				</view>
			</picker>
			<picker class="filter-picker" :range="serviceOptions" range-key="label" :value="serviceIndex"
				@change="onServiceChange">
				<view class="filter-pill">
					<text class="filter-label">{{ serviceOptions[serviceIndex].label }}</text>
					<u-icon class="filter-arrow" name="arrow-down" color="#A2A3AB" size="12"></u-icon>
				</view>
			</picker>
			<picker class="filter-picker" :range="priceOptions" range-key="label" :value="priceIndex"
				@change="onPriceChange">
				<view class="filter-pill">
					<text class="filter-label">{{ priceOptions[priceIndex].label }}</text>
					<u-icon class="filter-arrow" name="arrow-down" color="#A2A3AB" size="12"></u-icon>
				</view>
			</picker>
			<picker class="filter-picker" :range="sortOptions" range-key="label" :value="sortIndex"
				@change="onSortChange">
				<view class="filter-pill">
					<text class="filter-label">{{ sortOptions[sortIndex].label }}</text>
					<u-icon class="filter-arrow" name="arrow-down" color="#A2A3AB" size="12"></u-icon>
				</view>
			</picker>
		</view>

		<view class="course-list" v-if="list.length">
			<view class="course-card" v-for="item in list" :key="item.id" @click="openDetail(item)">
				<image class="course-cover" :src="item.coverUrl || fallbackCover" mode="aspectFill"></image>
				<view class="course-content">
					<view class="course-name ellipsis">{{ item.productName }}</view>
					<view class="tag-row">
						<text class="tag primary">{{ courseKindText(item) }}</text>
						<text class="tag">{{ serviceTypeText(item.serviceType) }}</text>
						<text class="tag" v-if="item.categoryName">{{ item.categoryName }}</text>
					</view>
					<view class="course-meta">
						<text v-if="Number(item.lessonCountVisible) !== 0">{{ item.lessonCount || 0 }} 课时 · </text>单次 {{ item.durationMinutes || 0 }} 分钟 · {{ validityText(item.validityDays) }}
					</view>
					<view class="store-text ellipsis">适用门店：{{ item.storeNames || '进入详情查看' }}</view>
					<view class="price-row">
						<text class="price-label">课程价</text>
						<text class="currency">¥</text>
						<text class="price">{{ priceText(item.salePrice) }}</text>
						<text class="origin-price" v-if="showOrigin(item)">¥{{ priceText(item.originalPrice) }}</text>
						<view class="buy-btn" :class="{ disabled: !canBuy(item) }" @click.stop="buyOrView(item)">
							{{ buyButtonText(item) }}
						</view>
					</view>
				</view>
			</view>
		</view>

		<view class="empty" v-else-if="loaded">
			<u-icon name="file-text" color="#C5C6CC" size="48"></u-icon>
			<text>{{ emptyText }}</text>
			<view class="reset-btn" v-if="hasFilter" @click="resetFilters">清空筛选</view>
		</view>

		<view class="loading-text" v-if="loading">课程加载中...</view>
		<view class="loading-text" v-else-if="list.length">{{ noMore ? '已经到底了' : '上拉加载更多' }}</view>
	</view>
</template>

<script>
	import { getPrivateProductList, getPrivateProductCategories } from '@/api/private-training.js'
	import { getMyStore } from '@/api/index.js'

	export default {
		data() {
			return {
				courseKinds: [
					{ label: '全部', value: '' },
					{ label: '私教课程', value: 'private' },
					{ label: '小班课', value: 'group' },
					{ label: '体验课', value: 'experience' }
				],
				courseKind: '',
				keyword: '',
				categoryOptions: [{ label: '商品分类', value: '' }],
				categoryIndex: 0,
				serviceOptions: [
					{ label: '类型', value: '' },
					{ label: '一对一', value: 1 },
					{ label: '一对多', value: 2 }
				],
				serviceIndex: 0,
				priceOptions: [
					{ label: '价格', min: '', max: '' },
					{ label: '¥0-200', min: 0, max: 200 },
					{ label: '¥200-500', min: 200, max: 500 },
					{ label: '¥500-1000', min: 500, max: 1000 },
					{ label: '¥1000以上', min: 1000, max: '' }
				],
				priceIndex: 0,
				sortOptions: [
					{ label: '排序', value: '' },
					{ label: '价格升序', value: 'priceAsc' },
					{ label: '价格降序', value: 'priceDesc' },
					{ label: '最新上架', value: 'newest' }
				],
				sortIndex: 0,
				currentStore: {},
				list: [],
				page: 1,
				limit: 10,
				total: 0,
				loading: false,
				loaded: false,
				noMore: false,
				initialized: false,
				requestSequence: 0,
				fallbackCover: '/static/image/card_bg.png'
			}
		},
		computed: {
			emptyText() {
				const current = this.courseKinds.find((item) => item.value === this.courseKind);
				return current && current.value ? '当前门店暂无' + current.label : '当前门店暂无可购买课程';
			},
			hasFilter() {
				return !!(this.courseKind || this.keyword || this.categoryIndex || this.serviceIndex || this.priceIndex || this.sortIndex);
			}
		},
		onLoad() {
			this.loadCategories();
		},
		onShow() {
			// tabBar 页面会被缓存，每次切回时都重新拉取，避免首次请求失败后一直保留空列表。
			this.loadCurrentStore(true);
		},
		onPullDownRefresh() {
			this.loadCurrentStore(true, () => uni.stopPullDownRefresh());
		},
		onReachBottom() {
			if (!this.noMore) this.loadList(false);
		},
		methods: {
			loadCategories() {
				getPrivateProductCategories().then((res) => {
					const rows = res.data || [];
					this.categoryOptions = [{ label: '商品分类', value: '' }].concat(rows.map((item) => ({
						label: item.categoryName,
						value: item.id
					})));
				}).catch(() => {
					this.categoryOptions = [{ label: '商品分类', value: '' }];
					this.categoryIndex = 0;
				});
			},
			loadCurrentStore(force, done) {
				const location = this.$store.state.latilongi || {};
				getMyStore({
					offset: 0,
					limit: 1,
					userLng: location.longitude,
					userLat: location.latitude
				}).then((res) => {
					const store = ((res.data || [])[0]) || {};
					const changed = String(store.storeAddrId || '') !== String(this.currentStore.storeAddrId || '');
					this.currentStore = store;
					if (!this.initialized || changed || force) {
						this.initialized = true;
						this.loadList(true, done);
					} else {
						done && done();
					}
				}).catch(() => {
					if (!this.initialized || force) {
						this.initialized = true;
						this.currentStore = {};
						this.loadList(true, done);
					} else {
						done && done();
					}
				});
			},
			buildParams() {
				const category = this.categoryOptions[this.categoryIndex] || this.categoryOptions[0];
				const service = this.serviceOptions[this.serviceIndex];
				const price = this.priceOptions[this.priceIndex];
				const sort = this.sortOptions[this.sortIndex];
				const params = { page: this.page, limit: this.limit };
				if (this.currentStore.storeAddrId) params.storeId = this.currentStore.storeAddrId;
				if (this.courseKind) params.courseKind = this.courseKind;
				if (this.keyword.trim()) params.keyword = this.keyword.trim();
				if (category && category.value) params.categoryId = category.value;
				if (service && service.value) params.serviceType = service.value;
				if (price && price.min !== '') params.minPrice = price.min;
				if (price && price.max !== '') params.maxPrice = price.max;
				if (sort && sort.value) params.sort = sort.value;
				return params;
			},
			loadList(reset, done) {
				if (!reset && this.loading) {
					done && done();
					return;
				}
				if (reset) {
					this.page = 1;
					this.noMore = false;
				}
				const sequence = ++this.requestSequence;
				this.loading = true;
				getPrivateProductList(this.buildParams()).then((res) => {
					if (sequence !== this.requestSequence) return;
					const data = res.data || {};
					const rows = data.list || [];
					this.total = Number(data.totalCount || 0);
					this.list = reset ? rows : this.list.concat(rows);
					this.noMore = this.list.length >= this.total;
					if (!this.noMore) this.page += 1;
					this.loaded = true;
					this.loading = false;
					done && done();
				}).catch((e) => {
					if (sequence !== this.requestSequence) return;
					this.loading = false;
					this.loaded = true;
					this.config.Toast((e && e.message) || '课程加载失败');
					done && done();
				});
			},
			selectStore() {
				uni.navigateTo({ url: '/pagesA/select_store/select_store?type=1' });
			},
			applySearch() {
				this.loadList(true);
			},
			clearSearch() {
				this.keyword = '';
				this.loadList(true);
			},
			changeCourseKind(value) {
				if (this.courseKind === value) return;
				this.courseKind = value;
				this.loadList(true);
			},
			onCategoryChange(e) {
				this.categoryIndex = Number(e.detail.value || 0);
				this.loadList(true);
			},
			onServiceChange(e) {
				this.serviceIndex = Number(e.detail.value || 0);
				this.loadList(true);
			},
			onPriceChange(e) {
				this.priceIndex = Number(e.detail.value || 0);
				this.loadList(true);
			},
			onSortChange(e) {
				this.sortIndex = Number(e.detail.value || 0);
				this.loadList(true);
			},
			resetFilters() {
				this.courseKind = '';
				this.keyword = '';
				this.categoryIndex = 0;
				this.serviceIndex = 0;
				this.priceIndex = 0;
				this.sortIndex = 0;
				this.loadList(true);
			},
			openDetail(item) {
				let url = '/pagesA/private_course_detail/private_course_detail?id=' + item.id;
				if (this.currentStore.storeAddrId) url += '&storeId=' + this.currentStore.storeAddrId;
				uni.navigateTo({ url });
			},
			buyOrView(item) {
				if (this.isUpcoming(item)) {
					this.config.Toast('课程尚未开售');
					return;
				}
				if (this.isSoldOut(item)) {
					this.config.Toast('当前课程已售罄');
					return;
				}
				this.openDetail(item);
			},
			courseKindText(item) {
				if (item.typeName === '团课' || item.typeName === '小班课') return '小班课';
				if (item.typeName === '体验服务') return '体验课';
				return '私教课程';
			},
			serviceTypeText(value) {
				return Number(value) === 2 ? '一对多' : '一对一';
			},
			validityText(value) {
				return Number(value) === -1 ? '长期有效' : '有效期 ' + Number(value || 0) + ' 天';
			},
			priceText(value) {
				const n = Number(value);
				return isNaN(n) ? '0' : (Number.isInteger(n) ? String(n) : n.toFixed(2));
			},
			showOrigin(item) {
				return Number(item.originalPrice) > Number(item.salePrice);
			},
			isSoldOut(item) {
				const stock = item.saleStock;
				// 后端 sale_stock 为 NULL 时表示不限量，响应序列化后可能是空字符串。
				if (stock === null || stock === undefined || String(stock).trim() === '') return false;
				return Number(item.soldCount || 0) >= Number(stock);
			},
			isUpcoming(item) {
				if (!item.listingAt) return false;
				const value = typeof item.listingAt === 'string' ? item.listingAt.replace(/-/g, '/') : item.listingAt;
				const time = new Date(value).getTime();
				return !isNaN(time) && time > Date.now();
			},
			canBuy(item) {
				return !this.isSoldOut(item) && !this.isUpcoming(item);
			},
			buyButtonText(item) {
				if (this.isSoldOut(item)) return '已售罄';
				if (this.isUpcoming(item)) return '待开售';
				return '去购买';
			}
		}
	}
</script>

<style lang="scss" scoped>
	page { background: #F5F5F7; }
	.course-page { min-height: 100vh; padding-bottom: 32rpx; background: #F5F5F7; box-sizing: border-box; }
	.store-row { display: flex; height: 86rpx; padding: 0 30rpx; align-items: center; background: #FFF; }
	.store-name { max-width: 520rpx; margin: 0 12rpx; color: #22232A; font-size: 29rpx; font-weight: 700; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
	.search-box { display: flex; height: 78rpx; margin: 4rpx 30rpx 20rpx; padding: 0 26rpx; align-items: center; border-radius: 39rpx; background: #F1F1F3; box-sizing: border-box; }
	.search-box input { flex: 1; height: 78rpx; margin-left: 18rpx; color: #33343A; font-size: 27rpx; }
	.search-placeholder { color: #A8A9B2; }
	.clear-search { padding-left: 18rpx; color: #FF541A; font-size: 23rpx; }
	.kind-tabs { width: 100%; height: 88rpx; white-space: nowrap; background: #FFF; border-bottom: 1rpx solid #EEEFF2; }
	.kind-tab { position: relative; display: inline-flex; width: 25%; height: 88rpx; align-items: center; justify-content: center; color: #85868F; font-size: 28rpx; font-weight: 600; box-sizing: border-box; }
	.kind-tab.active { color: #202127; font-weight: 800; }
	.active-line { position: absolute; bottom: 9rpx; left: 50%; width: 0; height: 6rpx; border-radius: 3rpx; background: #FF541A; transform: translateX(-50%); transition: width .2s; }
	.kind-tab.active .active-line { width: 42rpx; }
	.filter-row { display: flex; width: 100%; padding: 20rpx 24rpx; background: #FFF; border-bottom: 1rpx solid #E9E9EC; box-sizing: border-box; }
	.filter-picker { width: 0; min-width: 0; flex: 1; margin-right: 12rpx; overflow: hidden; }
	.filter-picker:last-child { margin-right: 0; }
	.filter-pill { position: relative; display: flex; width: 100%; height: 62rpx; padding: 0 32rpx; align-items: center; justify-content: center; border-radius: 31rpx; background: #F2F2F4; color: #777984; font-size: 23rpx; white-space: nowrap; box-sizing: border-box; overflow: hidden; }
	.filter-label { display: block; width: 100%; min-width: 0; overflow: hidden; text-align: center; text-overflow: ellipsis; white-space: nowrap; }
	.filter-arrow { position: absolute; top: 50%; right: 15rpx; transform: translateY(-50%); }
	.course-list { padding: 22rpx 22rpx 0; }
	.course-card { display: flex; min-height: 250rpx; margin-bottom: 20rpx; padding: 22rpx; border-radius: 24rpx; background: #FFF; box-shadow: 0 8rpx 28rpx rgba(35, 36, 42, .05); box-sizing: border-box; }
	.course-cover { width: 210rpx; height: 210rpx; flex-shrink: 0; border-radius: 20rpx; background: #F0F0F0; }
	.course-content { flex: 1; min-width: 0; margin-left: 22rpx; }
	.ellipsis { overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
	.course-name { color: #22232A; font-size: 30rpx; line-height: 42rpx; font-weight: 800; }
	.tag-row { display: flex; margin-top: 12rpx; overflow: hidden; }
	.tag { flex-shrink: 0; margin-right: 8rpx; padding: 5rpx 12rpx; border-radius: 8rpx; background: #F2F2F4; color: #777984; font-size: 20rpx; line-height: 26rpx; }
	.tag.primary { background: #FFF0E7; color: #FF541A; }
	.course-meta { margin-top: 12rpx; color: #84858E; font-size: 22rpx; line-height: 32rpx; white-space: nowrap; }
	.store-text { margin-top: 5rpx; color: #A0A1A9; font-size: 21rpx; line-height: 30rpx; }
	.price-row { display: flex; height: 58rpx; margin-top: 7rpx; align-items: center; }
	.price-label { margin-right: 8rpx; padding: 4rpx 8rpx; border-radius: 7rpx; background: #FF541A; color: #FFF; font-size: 18rpx; }
	.currency { margin-right: 4rpx; color: #F54718; font-size: 22rpx; font-weight: 800; }
	.price { color: #F54718; font-size: 38rpx; font-weight: 900; }
	.origin-price { margin-left: 8rpx; color: #B1B2B9; font-size: 20rpx; text-decoration: line-through; }
	.buy-btn { display: flex; min-width: 118rpx; height: 58rpx; margin-left: auto; padding: 0 20rpx; align-items: center; justify-content: center; border-radius: 29rpx; background: #FF541A; color: #FFF; font-size: 23rpx; font-weight: 700; box-sizing: border-box; }
	.buy-btn.disabled { background: #C9CAD0; }
	.empty { display: flex; min-height: 560rpx; flex-direction: column; align-items: center; justify-content: center; color: #999AA2; font-size: 26rpx; }
	.empty > text { margin-top: 24rpx; }
	.reset-btn { margin-top: 28rpx; padding: 14rpx 34rpx; border: 1rpx solid #FF541A; border-radius: 32rpx; color: #FF541A; }
	.loading-text { padding: 18rpx 0 34rpx; color: #A5A6AD; text-align: center; font-size: 22rpx; }
</style>
