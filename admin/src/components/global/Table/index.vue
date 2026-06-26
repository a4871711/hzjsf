<!--表格组件 -->
<template>
	<section class="ces-table-pagee flex_column flex1">
		<!-- 表格操作按钮 -->
		<section class="ces-handle flex" v-if="isHandle" style="margin-bottom: 20px;">
			<span v-for="item in tableHandles" class="mr10">
				<el-button
					v-if="item.isShow && item.type != 'dropdown' && item.type != 'switch' ? item.isShow() : item.type != 'dropdown' && item.type != 'switch' ? true : false"
					:key="item.label"
					:size="size || item.size"
					:type="item.type"
					:icon="item.icon"
					@click="item.handle"
				>
					{{ item.label }}
				</el-button>

				<el-dropdown v-if="item.isShow && item.type == 'dropdown' ? item.isShow() : item.type == 'dropdown' ? true : false" @command="item.handleCommand" trigger="click">
					<el-button icon="" :size="item.dropSize || size" type="primary">{{ item.label }}</el-button>
					<el-dropdown-menu slot="dropdown">
						<el-dropdown-item
							v-for="(drop, idx) in item.dropList"
							:key="idx"
							:command="{ btn: typeof drop == 'object' ? drop.label : drop, row: item }"
							v-if="drop.isShow && typeof drop == 'object' ? drop.isShow(item) : true"
						>
							{{ typeof drop == 'object' ? drop.label : drop }}
						</el-dropdown-item>
					</el-dropdown-menu>
				</el-dropdown>

				<!-- 开关 -->
				<el-switch
					style="position: relative;top:10px;"
					v-if="item.type === 'switch'"
					v-model="item.data"
					:inactive-text="item.inactive_text"
					:size="size || btn.size"
					:active-value="item.values && item.values[0]"
					:inactive-value="item.values && item.values[1]"
					:disabled="item.isDisabled && item.isDisabled(scope.row)"
					@change="item.change && item.change(item)"
				></el-switch>
			</span>
		</section>
		<!-- 数据表格 -->
		<section class="ces-table flex1" ref="tableBody">
			<el-table
				:data="tableData"
				:size="size"
				:border="isBorder"
				@select="select"
				@select-all="selectAll"
				@current-change="CurrentChange"
				:highlightCurrentRow="highlightCurrentRow"
				:row-class-name="tableRowClassName"
				v-loading="loading"
				:defaultSelections="defaultSelections"
				ref="cesTable"
				:row-key="rowKey"
				default-expand-all
				:height="height"
				:header-cell-style="{ backgroundColor: '#f5f7fa', fontWeight: 'bold', color: '#909399' }"
				:tree-props="treeProps"
			>
				<el-table-column v-if="isSelection" type="selection" align="center"></el-table-column>
				<el-table-column v-if="isIndex" type="index" :label="indexLabel" align="center" width="50" :index="indexMethod"></el-table-column>
				<!-- 数据栏 -->
				<el-table-column
					v-for="(item, idx) in tableCols"
					v-if="item.isShow ? item.isShow() : true"
					:key="idx"
					:prop="item.prop"
					:label="item.btnLabel || item.label"
					:width="item.width"
					:align="item.align || 'center'"
					:fixed="item.fixed"
					:render-header="item.require ? renderHeader : null"
					:show-overflow-tooltip="item.overflow"
				>
					<template slot-scope="scope">
						<!-- html -->
						<span v-if="item.type === 'html'" v-html="item.html(scope.row)"></span>
						<!-- tag -->
						<el-tag v-if="item.type === 'tag'" :type="item.theme && item.theme(scope.row)">
							{{ (item.formatter && item.formatter(scope.row)) || scope.row[item.prop] }}
						</el-tag>
						<!-- 按钮 -->
						<span v-if="item.type === 'button'">
							<el-button
								v-for="(btn, idx) in item.btnList"
								:key="idx"
								:disabled="btn.disabled && btn.disabled(scope.row)"
								v-if="btn.isShow ? btn.isShow(scope.row) : true"
								:type="typeof btn.type == 'function' ? btn.type(scope.row) : btn.type"
								:size="btn.size || size"
								:icon="btn.icon"
								@click="btn.handle(scope.row, scope.$index)"
							>
								{{ btn.label ? btn.label : scope.row[btn.des] }}
							</el-button>
							<el-dropdown v-if="item.dropListisShow ? item.dropListisShow(scope.row) : item.dropList ? true : false" @command="item.handleCommand" trigger="click">
								<el-button icon="el-icon-setting" :size="item.dropSize || size" type="info" />
								<el-dropdown-menu slot="dropdown">
									<el-dropdown-item
										v-for="(drop, idx) in item.dropList"
										:key="idx"
										:command="{ btn: typeof drop == 'object' ? drop.label : drop, row: scope.row }"
										v-if="drop.isShow && typeof drop == 'object' ? drop.isShow(scope.row) : true"
									>
										{{ typeof drop == 'object' ? drop.label : drop }}
									</el-dropdown-item>
								</el-dropdown-menu>
							</el-dropdown>
						</span>
						<!-- 输入框 -->
						<el-input
							v-if="item.type === 'input'"
							v-model="scope.row[item.prop]"
							:size="size || btn.size"
							:disabled="item.isDisabled && item.isDisabled(scope.row)"
							:placeholder="item.placeholder || '请输入'"
							@focus="item.focus && item.focus(scope.row)"
							@input="item.input && item.input(scope.row, scope.$index)"
							@change="item.change && item.change(scope.row, scope.$index)"
						></el-input>
						<!-- 下拉框 -->
						<el-select
							v-if="item.type === 'select'"
							v-model="scope.row[item.prop]"
							:size="size || btn.size"
							:disabled="item.isDisabled && item.isDisabled(scope.row)"
							@change="item.change && item.change(scope.row)"
						>
							<el-option v-for="(op, idx) in item.options" :label="op.label" :value="op.value" :key="idx"></el-option>
						</el-select>
						<!-- 级联省市区 -->
						<el-cascader
							v-if="item.type === 'cascader'"
							:style="{ width: item.width + 'px' }"
							:placeholder="item.placeholder || '请选择'"
							:options="item.options || []"
							v-model="scope.row[item.prop]"
							@focus="item.focus && item.focus(scope.row)"
							filterable
							clearable
							:props="{ expandTrigger: 'hover', checkStrictly: true }"
							@change="item.change && item.change(scope.row)"
						></el-cascader>
						<!-- 单选 -->
						<el-radio-group
							v-if="item.type === 'radio'"
							v-model="scope.row[item.prop]"
							:disabled="item.isDisabled && item.isDisabled(scope.row)"
							:size="size || btn.size"
							@change="item.change && item.change(scope.row)"
						>
							<el-radio v-for="(ra, idx) in item.radios" :label="ra.value" :key="idx">{{ ra.label }}</el-radio>
						</el-radio-group>
						<!-- 复选框 -->
						<el-checkbox-group
							v-if="item.type === 'checkbox'"
							v-model="scope.row[item.prop]"
							:disabled="item.isDisabled && item.isDisabled(scope.row)"
							:size="size || btn.size"
							@change="item.change && item.change(scope.row)"
						>
							<el-checkbox v-for="(ra, idx) in item.checkboxs" :label="ra.value" :key="idx">{{ ra.label }}</el-checkbox>
						</el-checkbox-group>
						<!-- 评价 -->
						<el-rate
							v-if="item.type === 'rate'"
							:value="scope.row[item.prop] * 1"
							:allow-half="true"
							:disabled="item.isDisabled && item.isDisabled(scope.row)"
							:size="size || btn.size"
							@change="item.change && item.change(scope.row)"
						></el-rate>
						<!-- 开关 -->
						<el-switch
							v-if="item.type === 'switch'"
							v-model="scope.row[item.prop]"
							:size="size || btn.size"
							:active-value="item.values && item.values[0]"
							:inactive-value="item.values && item.values[1]"
							:disabled="item.isDisabled && item.isDisabled(scope.row)"
							@change="item.change && item.change(scope.row)"
						></el-switch>
						<!-- 二维码 -->
						<el-popover v-if="item.type === 'qrCode'" :placement="item.placement || 'left'" trigger="click" id="qrcode">
							<div>
								<vue-qr
									:logo-src="item.logoSrc"
									:size="300"
									:margin="10"
									:auto-color="true"
									:dot-scale="1"
									:text="(item.formatter && item.formatter(scope.row)) || (scope.row[item.prop] === 0 ? 0 : scope.row[item.prop] ? scope.row[item.prop] : '-')"
								/>
							</div>
							<vue-qr
								slot="reference"
								:logo-src="item.logoSrc"
								:size="100"
								:margin="10"
								:auto-color="true"
								:dot-scale="1"
								:text="(item.formatter && item.formatter(scope.row)) || (scope.row[item.prop] === 0 ? 0 : scope.row[item.prop] ? scope.row[item.prop] : '-')"
							/>
						</el-popover>
						<!-- 单图像 -->
						<!-- <img v-if="item.type==='image'"
            :src="scope.row[item.prop]"
            @click="item.handle && item.handle(scope.row)"/> -->
						<el-popover v-if="item.type === 'image' && scope.row[item.prop] && scope.row[item.prop].search(/.mp4/i) == -1" :placement="item.placement || 'right'" trigger="click">
							<div><el-image style="width: 300px;height: auto;" :src="scope.row[item.prop] | formatImg" fit="fill"></el-image></div>
							<el-image :style="item.style || 'width: 80px;height: 80px;'" :src="scope.row[item.prop] | formatImg" fit="contain" slot="reference"></el-image>
						</el-popover>

						<div v-if="item.type === 'image' && scope.row[item.prop] && scope.row[item.prop].search(/.mp4/i) != -1">
					
							<video controls style="width:200px;height:100px;" :src="scope.row[item.prop] | formatImg"></video>
						</div>

						<!-- 多图像 -->
						<el-popover v-if="item.type === 'images' && scope.row[item.prop]" :placement="item.placement || 'left'" trigger="click">
							<div>
								<el-carousel trigger="click" style="width: 500px;" arrow="always" indicator-position="none" :autoplay="false">
									<el-carousel-item style="overflow: scroll;text-align: center;" v-for="(url, idx) in imgList(scope.row[item.prop])" :key="idx">
										<img :src="url | formatImg" alt style="height:100%;" />
									</el-carousel-item>
								</el-carousel>
							</div>
							<el-button type="text" slot="reference">查看</el-button>
						</el-popover>
						<!-- 滑块 -->
						<el-slider
							v-if="item.type === 'slider'"
							v-model="scope.row[item.prop]"
							:disabled="item.isDisabled && item.isDisabled(scope.row)"
							:size="size || btn.size"
							@change="item.change && item.change(scope.row)"
						></el-slider>
						<!-- 默认 -->
						<span v-if="!item.type && !item.hidden" :style="item.itemStyle && item.itemStyle(scope.row)" :class="item.itemClass && item.itemClass(scope.row)">
							{{ item.formatter ? item.formatter(scope.row) : scope.row[item.prop] === 0 ? 0 : scope.row[item.prop] ? scope.row[item.prop] : '-' }}
						</span>
					</template>
				</el-table-column>
				<div slot="empty">
					<p><img src="~@/assets/img/original.png" width="163" height="100" /></p>
					<p><span>当前暂无数据，请添加数据</span></p>
				</div>
			</el-table>
			<section class="bottomText" v-if="bottomText" :style="{ textAlign: bottomText.textAlign }">
				<span>
					<i :style="{ color: bottomText.titleColor || bottomText.color, fontSize: bottomText.titleFontSize || bottomText.fontSize }">{{ bottomText.title }}</i>
					<i class="ml10" :style="{ color: bottomText.color, fontSize: bottomText.fontSize }">{{ bottomText.content }}</i>
				</span>
			</section>
		</section>

		<!-- 分页 -->
		<section class="ces-pagination" v-if="isPagination">
			<el-pagination
				style="display: flex;justify-content: star;align-items: center;"
				@current-change="handleCurrentChange"
				@size-change="handleSizeChange"
				:page-sizes="[10, 20, 50, 100]"
				layout="total,sizes ,prev, pager, next,jumper"
				:page-size="tablePage.pageSize"
				:current-page="tablePage.pageNum"
				:total="tablePage.total"
			></el-pagination>
		</section>
	</section>
</template>

<script>
import VueQr from 'vue-qr'
export default {
	name: 'Table',
	components: { VueQr },
	props: {
		// 表格型号：mini,medium,small
		size: {
			type: String,
			default: 'medium'
		},
		// height: {
		//   type: Number,
		//   default: 0,
		// },
		rowKey: {
			type: String,
			default: 'id'
		},
		isBorder: {
			type: Boolean,
			default: false
		},
		loading: {
			type: Boolean,
			default: false
		},
		// 表格操作
		isHandle: {
			type: Boolean,
			default: false
		},
		tableHandles: {
			type: Array,
			default: () => []
		},
		// 表格数据
		tableData: {
			type: Array,
			default: () => []
		},
		// 表格列配置
		tableCols: {
			type: Array,
			default: () => []
		},
		// 是否显示表格复选框
		isSelection: {
			type: Boolean,
			default: false
		},
		defaultSelections: {
			type: [Array, Object],
			default: () => null
		},
		// 是否显示表格索引
		isIndex: {
			type: Boolean,
			default: false
		},
		indexLabel: {
			type: String,
			default: '序号'
		},
		// 是否显示分页
		isPagination: {
			type: Boolean,
			default: false
		},
		// 分页数据
		tablePage: {
			type: Object,
			default: () => ({
				limit: 10,
				page: 1,
				total: 0
			})
		},
		highlightCurrentRow: {
			type: Boolean,
			default: false
		},
		treeProps: {
			type: Object,
			default: () => ({ children: 'children', hasChildren: 'hasChildren' })
		},
		bottomText: {
			type: Object,
			default: () => {}
		}
	},
	data() {
		return {
			height: 0,
			imgurl: process.env.VUE_APP_IMG
			//imgurl:'http://s302t98620.zicp.vip:44353/car/img/'
		}
	},
	watch: {
		defaultSelections(val) {
			this.$nextTick(function() {
				if (Array.isArray(val)) {
					val.forEach(row => {
						this.$refs.cesTable.toggleRowSelection(row)
					})
				} else {
					this.$refs.cesTable.toggleRowSelection(val)
				}
			})
		}
	},
	mounted() {
		this.$nextTick(_ => {
			if (this.height > 0) return
			this.$parent.$el.className = 'flex_column'
			this.$parent.$el.style.height = '100%'
			this.height = this.$refs.tableBody.clientHeight || 600
		})
	},
	methods: {
		clearSelection() {
			this.$refs.cesTable.clearSelection();
		},
		indexMethod(index) {
			return (this.tablePage.page - 1) * this.tablePage.limit + index + 1
		},
		// 表格勾选
		select(rows, row) {
			this.$emit('select', rows, row)
		},
		// 全选
		selectAll(rows) {
			this.$emit('select', rows)
		},
		//
		handleCurrentChange(val) {
			this.tablePage.page = val
			this.tablePage.offset = val
			this.$emit('refresh', {
				page: this.tablePage.page,
				limit: this.tablePage.limit
			})
		},
		handleSizeChange(val) {
			this.tablePage.limit = val
			this.$emit('refresh', {
				page: this.tablePage.page,
				limit: this.tablePage.limit
			})
		},
		CurrentChange(val) {
			this.$emit('radio', val)
		},
		imgList(img) {
			if (img) {
				let arr = img.split(',').map(item => {
					return item.startsWith('http') ? item : process.env.VUE_APP_IMG + item
				})
				return arr
			}
			return []
		},
		tableRowClassName({ row, rowIndex }) {
			//获取当前点击行下标
			row.index = rowIndex
		},
		renderHeader(h, obj) {
			return h(
				'span',
				{
					class: 'ces-table-require'
				},
				obj.column.label
			)
		}
	}
}
</script>
<style scoped lang="scss">
.ces-table-require::before {
	content: '*';
	color: red;
}

.el-button + .el-dropdown {
	margin-left: 10px;
}

.bottomText {
	padding: 10px 20px;
	background: #f5f7fa;
}
/* .ces-table-page{
	margin-top: 20px;
	padding: 0 30px;
} */
</style>
