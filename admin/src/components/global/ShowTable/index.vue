<template>
	<section>
		<p class="ft16 mtb20" v-if="title">
			<i class="el-icon-s-flag"></i>
			<span class="ml10">{{ title }}</span>
		</p>
		<p class="ft16" v-if="subtitle">{{subtitle}}</p>
		<table>
			<tbody>
				<tr v-for="(item, trIndex) of listColsShow" :key="trIndex">
					<template v-for="op of item">
						<th v-show="op.thShow != undefined ? op.thShow(ListData) : true"
							:style="{ width: thWidth + 'px' }">{{op.label}}</th>
						<td v-show="op.isShow != undefined ? op.isShow(ListData) : true"
							:style="{ width: tdWidth + 'px' }" :align="op.align||'center'" :rowspan="op.rowspan||1"
							:colspan="op.colspan||1">
							<el-popover v-if="op.type === 'image'" :placement="op.placement || 'left'" trigger="click">
								<div>
									<el-image :lazy="true" style="width: 300px; height: auto" :src="ListData[op.prop] | formatImg"
										fit="fill"></el-image>
								</div>
								<el-image :lazy="true" :style="op.style || 'width: 80px;height: 80px;'"
									:src="ListData[op.prop] | formatImg" fit="contain" slot="reference"></el-image>
							</el-popover>
							<!-- 多图像 -->
							<div v-if="op.type === 'images'">
								<el-popover v-if="imgList(ListData[op.prop]).length > 0"
									:placement="op.placement || 'left'" trigger="click">
									<div>
										<el-carousel trigger="click" height="500px" style="width: 500px; height: 500px"
											arrow="always" indicator-position="none" :autoplay="false">
											<el-carousel-item v-for="(url, idx) in imgList(ListData[op.prop])"
												:key="idx">
												<el-image :src="url | formatImg" alt style="width: 500px; height: 500px"
													fit="contain" />
											</el-carousel-item>
										</el-carousel>
									</div>
									<div slot="reference">
										<!-- 缩略图前3张可见 -->
										<el-image :style="op.style || 'width: 80px;height: 80px;'"
											v-for="(url, uidx) in imgList(ListData[op.prop])" v-show="uidx < 3"
											:key="uidx" :src="url" fit="contain" slot="reference"></el-image>
									</div>
								</el-popover>
								<span v-else>-</span>
							</div>

							<span class="plr10" v-else-if="op.type == 'html'" v-html="op.html && op.html(ListData)"
								@click="op.handleClick && op.handleClick(ListData)"></span>
							<span class="plr10" v-else-if="!op.type" :style="{ color: op.color }">{{
							  (op.formatter && op.formatter(ListData)) ||
							  ListData[op.prop] ||
							  "-"
							}}</span>
						</td>
					</template>
				</tr>
			</tbody>
		</table>
	</section>
</template>

<script>
	export default {
		name: "ShowTable",
		props: {
			title: {
				type: String,
				default: "",
			},
			subtitle:{
				type: String,
				default: "",
			},
			thWidth: {
				type: [Number, String],
				default: 175
			},
			tdWidth: {
				type: [Number, String],
				default: 175
			},
			col: {
				type: [Number, String],
				default: 2
			},
			ListCols: {
				type: Array,
				default: () => [],
			},
			ListData: {
				type: Object,
				default: () => ({}),
			},
		},
		data() {
			return {

			};
		},
		computed: {
			listColsShow() {
				let arr = [],
					ListCols = this.ListCols;
				let col = Number(this.col);
				let count = 0;
				for (var i = 0; i < ListCols.length; i++) {
					if (!arr[count]) arr[count] = [];
					let rowspanList = arr[count].filter(item => {
						return item.rowspan
					});
					let colspanList = arr[count].filter(item => {
						return item.colspan
					});

					if ((arr[count].length >= col + rowspanList.length) || (colspanList.length>0 && arr[count].length >= col*2/ListCols[i].colspan)) {
						count++;
						if (!arr[count]) arr[count] = [];
					}
					arr[count].push(ListCols[i]);
				}
				return arr;
			}
		},
		created() {
			
		},
		methods: {
			imgList(img) {
				if (img) {
					let arr = img.split(",").map(item => {
						return item.startsWith('http') ? item : process.env.VUE_APP_IMG + item;
					})
					return arr;
				}
				return [];
			}
		}
	}
</script>

<style scoped lang="scss">
	table,
	table tr th,
	table tr td {
		border: 1px solid #e4e4e4;
	}

	table {
		min-height: 45px;
		line-height: 45px;
		text-align: center;
		border-collapse: collapse;
		padding: 2px;
	}

	table tr th {
		background-color: #f9fafc;
	}
</style>
