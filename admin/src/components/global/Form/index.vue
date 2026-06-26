<!-- 搜索表单 -->
<template>
	<div class="ces-form">
		<el-dialog :title="dialogFormTitle" :visible.sync="dialogFormVisible" @close="reset()" @opened="onDialogOpened" :close-on-click-modal="false" :width="dialogWidth" :top="dialogTop" :custom-class="'elDialog'" v-if="dialogFormVisible">
			<el-form :size="size" :inline="inline" :label-width="labelWidth" :rules="formRules" :model="formData" ref="ruleForm">
				<el-form-item v-for="item in formCols" :label="item.label" :key="item.label" :prop="item.prop" v-if="item.isShow ? item.isShow(item) : true">
					<!-- 输入框 -->
					<el-input
						v-if="item.type === 'input'"
						:id="item.id || ''"
						v-model="formData[item.prop]"
						:size="size || item.size"
						:style="{ width: item.width + 'px' }"
						@change="item.change && item.change(formData[item.prop])"
						:placeholder="item.placeholder || '请输入'"
						:disabled="item.isDisabled && item.isDisabled(formData)"
						:type="item.inputType ? item.inputType : 'text'"
					></el-input>
					<!-- 只读
          <el-input
            v-if="item.type==='readonly'"
            :id="item.id||''"
            v-model="formData[item.prop]"
            :size="size || item.size"
            :style="{width:item.width+'px'}"
            :placeholder="item.placeholder || '请输入'"
            readonly="value"
          ></el-input> -->
					<!-- 按钮 -->
					<el-button v-if="item.type === 'button'" type="primary" :size="size || item.size" :style="{ width: item.width + 'px' }" @click="item.handle">
						{{ formData[item.prop] }}
					</el-button>
					<!-- 文本域 -->
					<el-input
						v-if="item.type === 'textarea'"
						type="textarea"
						:rows="4"
						:size="size || item.size"
						:placeholder="item.placeholder || '请输入'"
						:style="{ width: item.width + 'px' }"
						:disabled="item.isDisabled && item.isDisabled(formData)"
						@change="item.change && item.change(formData[item.prop])"
						v-model="formData[item.prop]"
					></el-input>
					<!-- 下拉框 -->
					<el-select
						clearable
						v-if="item.type === 'select'"
						v-model="formData[item.prop]"
						filterable
						:multiple="item.multiple"
						:size="size || item.size"
						:style="{ width: item.width + 'px' }"
						:ref="item.ref || ''"
						:filter-method="item.handleSearch"
						@change="item.change && item.change(formData[item.prop], item)"		
						@visible-change="item.focus && item.focus($event, formData[item.prop])"
						:disabled="item.isDisabled && item.isDisabled(formData[item.prop])"
					>
						<el-option v-for="op in item.options" :label="op.name ? op.name : op.label" :value="op.value" :key="op.value"></el-option>
						
						<el-pagination
							v-if="item.pageSize"
							@size-change="item.handleSizeChange"
							@current-change="item.handleCurrentChange"
							:current-page="item.currentPage"
							:page-sizes="[10, 20, 30, 40]"
							:page-size="item.pageSize"
							layout="prev, pager, next"
							:total="item.total">
						  </el-pagination>
	  
					</el-select>
					<!-- 级联省市区 -->
					<el-cascader
						v-if="item.type === 'cascader'"
						:style="{ width: item.width + 'px' }"
						:placeholder="item.placeholder || '请选择'"
						:options="cascaderOptions"
						v-model="formData[item.prop]"
						@focus="item.focus && item.focus(formData[item.prop])"
						filterable
						clearable
						:changeOnSelect="true"
						:show-all-levels="item.showAll && item.showAll(formData[item.prop])"
						:props="{ multiple: false, expandTrigger: 'hover', checkStrictly: item.checkStrictly && item.checkStrictly(formData[item.prop]) }"
						@change="item.change && item.change(formData[item.prop])"
					></el-cascader>
					<!-- 单选 -->
					<el-radio-group
						v-if="item.type === 'radio'"
						v-model="formData[item.prop]"
						:style="{ width: item.width + 'px' }"
						@change="item.change && item.change(formData[item.prop])"
						:size="size || item.size"
						:disabled="item.isDisabled && item.isDisabled(formData[item.prop])"
					>
						<el-radio v-for="ra in item.radios" :label="ra.value" :key="ra.value">{{ ra.label }}</el-radio>
					</el-radio-group>
					<!-- 复选框 -->
					<el-checkbox-group
						v-if="item.type === 'checkbox'"
						v-model="formData[item.prop]"
						:style="{ width: item.width + 'px' }"
						@change="item.change && item.change(formData[item.prop])"
						:size="size || item.size"
						:disabled="item.isDisabled && item.isDisabled(formData[item.prop])"
					>
						<el-checkbox v-for="ch in item.checkboxs" :label="ch.value" :key="ch.value">{{ ch.label }}</el-checkbox>
					</el-checkbox-group>
					<!-- 日期 -->
					<el-date-picker
						v-if="item.type === 'date'"
						v-model="formData[item.prop]"
						:style="{ width: item.width + 'px' }"
						format="yyyy-MM-dd"
						value-format="yyyy-MM-dd"
						:picker-options="expireTimeOption"
						@change="item.change && item.change(formData[item.prop])"
						:size="size || item.size"
						:disabled="item.isDisabled && item.isDisabled(formData[item.prop])"
					></el-date-picker>
					<el-date-picker
						v-if="item.type === 'datet'"
						v-model="formData[item.prop]"
						:style="{ width: item.width + 'px' }"
						format="yyyy-MM-dd"
						value-format="yyyy-MM-dd HH:mm:ss"
						:picker-options="expireTimeOption"
						@change="item.change && item.change(formData[item.prop])"
						:size="size || item.size"
						:disabled="item.isDisabled && item.isDisabled(formData[item.prop])"
					></el-date-picker>
					<!-- 开始-结束日期 -->
					<el-date-picker
						format="yyyy-MM-dd"
						value-format="yyyy-MM-dd"
						v-if="item.type === 'daterange'"
						type="daterange"
						v-model="formData[item.prop]"
						:style="{ width: item.width + 'px' }"
						range-separator="至"
						start-placeholder="开始日期"
						end-placeholder="结束日期"
						:picker-options="expireTimeOption"
						@change="item.change && item.change(formData[item.prop])"
						:size="size || item.size"
						:disabled="item.isDisabled && item.isDisabled(formData[item.prop])"
					></el-date-picker>
					<!-- 时间 -->
					<el-time-select
						v-if="item.type === 'time'"
						v-model="formData[item.prop]"
						format="HH:mm:ss"
						value-format="HH:mm:ss"
						:style="{ width: item.width + 'px' }"
						@change="item.change && item.change(formData[item.prop])"
						:size="size || item.size"
						:disabled="item.isDisabled && item.isDisabled(formData[item.prop])"
					></el-time-select>
					<!-- 开始-结束时间 -->
					<el-time-picker
						v-if="item.type === 'timePicker'"
						v-model="formData[item.prop]"
						is-range
						format="HH:mm"
						value-format="HH:mm"
						range-separator="至"
						start-placeholder="开始时间"
						end-placeholder="结束时间"
						placeholder="选择时间范围"
						:style="{ width: item.width + 'px' }"
						@change="item.change && item.change(formData[item.prop])"
						:size="size || item.size"
						:disabled="item.isDisabled && item.isDisabled(formData[item.prop])"
						:picker-options="{
							selectableRange: ['00:00:00 - 23:59:59']
						}"
					></el-time-picker>
					<!-- 日期时间 -->
					<el-date-picker
						v-if="item.type === 'dateTime'"
						type="datetime"
						format="yyyy-MM-dd HH:mm:ss"
						value-format="yyyy-MM-dd HH:mm:ss"
						:style="{ width: item.width + 'px' }"
						v-model="formData[item.prop]"
						@change="item.change && item.change(formData[item.prop])"
						:size="size || item.size"
						:disabled="item.isDisabled && item.isDisabled(formData[item.prop])"
					></el-date-picker>
					<!-- 计数器 -->
					<el-input-number
						v-if="item.type === 'number'"
						v-model="formData[item.prop]"
						controls-position="right"
						:style="{ width: item.width + 'px' }"
						@change="item.change && item.change(formData[item.prop])"
						:min="0"
						:max="100"
					/>
					<!-- 滑块 -->
					<!-- <el-slider v-if="item.type==='Slider'" v-model="formData[item.prop]"></el-slider> -->
					<!-- 开关 -->
					<el-switch
						v-if="item.type === 'switch'"
						v-model="formData[item.prop]"
						:size="size || item.size"
						:active-value="item.values && item.values[0]"
						:inactive-value="item.values && item.values[1]"
						@change="item.change && item.change(formData[item.prop])"
						:style="{ width: item.width + 'px' }"
						:disabled="item.isDisabled && item.isDisabled(formData[item.prop])"
					></el-switch>
					<!-- 树形菜单弹出框 -->
					<el-popover v-if="item.type === 'popover'" placement="bottom-start" width="200" ref="deptListPopover" trigger="click">
						<el-tree
							:data="item.options"
							accordion
							node-key="id"
							ref="popoverTree"
							:props="defaultProps"
							:highlight-current="true"
							:expand-on-click-node="false"
							default-expand-all
							@node-click="item.handleNodeClick"
						/>
						<el-input
							slot="reference"
							v-model="formData[item.prop]"
							:readonly="true"
							:style="{ width: item.width + 'px' }"
							:placeholder="item.placeholder || '请选择'"
						></el-input>
					</el-popover>
					<!-- 树形控件 -->
					<el-tree
						v-if="item.type === 'tree'"
						:data="item.options"
						show-checkbox
						node-key="id"
						ref="tree"
						:props="defaultProps"
						:check-strictly="true"
						:default-checked-keys="item.default_checked_keys"
						@check="item.check2"
						@check-change="item.check"
					/>
					<!-- 图片上传 -->
					<section v-if="item.type === 'upload'" :style="{ width: item.width + 'px' }">
						<el-upload
							v-loading="loading"
							class="avatar-uploader"
							:action="upload_url + '/sys/uploads'"
							:show-file-list="false"
							:on-success="item.success"
							:on-error="handleAvatarError"
							:on-progress="handleAvatarProgress"
							:before-upload="beforeAvatarUpload"
							:headers="headers"
							name="file"
						>
							<video
								v-if="formData[item.prop].search(/.mp4/i) != -1"
								style="text-align:center;width:200px;height:150px;"
								:src="formData[item.prop] | formatImg"
								controls
								autoplay
							></video>
							<img v-if="formData[item.prop] && formData[item.prop].search(/.mp4/i) == -1" :src="formData[item.prop] | formatImg" class="avatar" />
							<i v-else class="el-icon-plus avatar-uploader-icon"></i>
							
						</el-upload>
						<div v-if="item.placeholder">
						  {{item.placeholder}}
						</div>
					</section>
					<!-- 多图片上传 -->
					<section v-if="item.type === 'uploadList'" :style="{ width: item.width + 'px' }">
						<el-upload
							v-loading="loading"
							:action="upload_url + '/sys/uploads'"
							list-type="picture-card"
							:multiple="true"
							:limit="9"
							:fileList="fileList"
							:on-success="handleAvatarSuccess"
							:on-error="handleAvatarError"
							:on-progress="handleAvatarProgress"
							:before-upload="beforeAvatarUpload"
							:on-remove="handleRemove"
							:on-exceed="handleExceed"
							:on-preview="handlePictureCardPreview"
							:headers="{ token: $cookie.get('token') }"
							name="file"
						>
							<i class="el-icon-plus"></i>
						</el-upload>
						<el-dialog :visible.sync="dialogVisible" append-to-body title="预览"><img width="100%" :src="dialogImageUrl" alt /></el-dialog>
						<div v-if="item.placeholder">
						  {{item.placeholder}}
						</div>
					</section>
					<!-- 地图 -->
					<div v-if="item.type === 'map'" id="container" style="width:450px;height: 300px;"></div>
					<!-- QQ地图 -->
					<div v-if="item.type === 'qqmap'" class="qqmap-picker">
						<p class="qqmap-tip">点击地图拾取坐标</p>
						<p class="qqmap-coord">
							原经度：{{ formatCoord(oldLongitude) }} 纬度：{{ formatCoord(oldLatitude) }}
						</p>
						<div v-if="hasNewPosition" class="qqmap-new-block">
							<div class="qqmap-new-row">
								<p class="qqmap-coord">
									新经度：{{ formatCoord(newLongitude) }} 纬度：{{ formatCoord(newLatitude) }}
								</p>
								<el-button
									class="qqmap-cancel-btn"
									size="mini"
									@click="cancelNewPosition"
								>取消</el-button>
							</div>
							<p class="qqmap-address">新地址：{{ newAddress }}</p>
						</div>
						<div id="l-map">
							<iframe style="width:450px;height: 500px;" frameborder="0" :src="qqmapsrc"></iframe>
						</div>
					</div>

					<!-- 默认 -->
					<span
						v-if="!item.type"
						:style="{ width: item.width + 'px', display: 'inline-block' }"
						:size="size"
						:class="item.itemClass && item.itemClass(formData[item.prop])"
					>
						{{ (item.formatter && item.formatter(formData[item.prop])) || formData[item.prop] }}
					</span>
					<!-- 图片显示 -->
					<el-image v-if="item.type === 'img'" :src="formData[item.prop] | formatImg" :style="{ width: item.width + 'px', height: item.width + 'px' }"></el-image>
					<!-- 富文本  -->
					<r-tinymce v-if="item.type === 'tinymce'" v-model="formData[item.prop]" :height="300" :width="600"></r-tinymce>
					<i v-if="item.remark && !item.rmblock">{{ item.remark }}</i>
					<div v-if="item.remark && item.rmblock">{{ item.remark }}</div>
					<slot v-if="item.type === 'slot'" :name="item.name"></slot>
				</el-form-item>
				<slot></slot>
			</el-form>
			<el-form inline v-if="isHandle" :style="btnStyle || 'justify-content: flex-end;display: flex;margin-top:20px;'">
				<el-form-item v-for="item in formHandle" :key="item.label" style="margin-bottom: 0;">
					<el-button v-if="item.theme === undefined" :type="item.type" :size="item.size || size" :icon="item.icon" @click="item.handle">{{ item.label }}</el-button>
					<div v-if="item.theme === 'upload'">
						<input v-show="false" ref="excel-upload-input" class="excel-upload-input" type="file" accept=".xlsx, .xls" @change="handleClick" />
						<el-button :loading="item.loading" :size="item.size || size" type="success" icon="el-icon-upload2" @click="handleUpload">{{ item.label }}</el-button>
					</div>
				</el-form-item>
			</el-form>
		</el-dialog>
	</div>
</template>

<script>
import area from '@/utils/area2'
import * as plugin from '@/utils/Tmap'
export default {
	props: {
		isHandle: {
			type: Boolean,
			default: true
		},
		labelWidth: {
			type: String,
			default: '100px'
		},
		dialogWidth: {
			type: String,
			default: '1000px'
		},
		dialogTop: {
			type: String,
			default: '5vh'
		},
		inline: {
			type: Boolean,
			default: true
		},
		// 搜索框型号：mini,medium,small
		size: {
			type: String,
			default: 'small'
		},
		formCols: {
			type: Array,
			default: () => []
		},
		formHandle: {
			type: Array,
			default: () => []
		},
		formData: {
			type: Object,
			default: () => {}
		},
		formRules: {
			type: Object,
			default: () => {}
		},
		btnStyle: {
			type: String,
			default: ''
		},
		fileList: {
			type: Array,
			default: () => []
		},
		defaultProps: {
			type: Object,
			default: () => ({ children: 'children', label: 'text' })
		},
		destroy: {
			type: Boolean,
			default: true
		}
	},
	name: 'Form',
	data() {
		return {
			dialogFormVisible: false,
			dialogFormTitle: '新增',
			loading: false,
			imageUrl: '',
			upload_url: process.env.NODE_ENV !== 'production'?'proxyApi/':process.env.VUE_APP_URL,
			imgurl: process.env.VUE_APP_IMG,
			cascaderOptions: area,
			map: null,
			expireTimeOption: {
				disabledDate(date) {
					return date.getTime() < Date.now() - 8.64e7
				}
			},
			dialogVisible: false,
			dialogImageUrl: '',
			mapKey: '45TBZ-6SDCC-VZ625-AVOWK-BVLPV-6FFO4',
			oldLongitude: '',
			oldLatitude: '',
			newLongitude: '',
			newLatitude: '',
			oldAddress: '',
			newAddress: '',
			qqmapsrc: ''
		}
	},
	computed: {
		headers() {
			return {
				// Authorization:localStorage.token
			}
		},
		hasNewPosition() {
			if (this.newLongitude === '' || this.newLatitude === '') return false
			return String(this.newLongitude) !== String(this.oldLongitude) ||
				String(this.newLatitude) !== String(this.oldLatitude)
		}
	},
	created() {},
	mounted() {
		this.$nextTick(() => {
			//this.plugin()
			this.listen()
		})
	},
	methods: {
		onDialogOpened() {
			if (this.formCols.some(item => item.type === 'qqmap')) {
				this.initMapCoords()
				this.refreshQqMap()
			}
		},
		initMapCoords() {
			this.oldLongitude = this.formData.longitude || ''
			this.oldLatitude = this.formData.latitude || ''
			this.oldAddress = this.getStoreAddress()
			this.newLongitude = ''
			this.newLatitude = ''
			this.newAddress = ''
		},
		getStoreAddress() {
			const detail = this.formData.storeAddrDetail || ''
			const region = [this.formData.province, this.formData.city, this.formData.zone].filter(Boolean).join('')
			return detail || region || ''
		},
		formatCoord(val) {
			return val === '' || val === null || val === undefined ? '未设置' : val
		},
		refreshQqMap() {
			let url = `https://apis.map.qq.com/tools/locpicker?search=1&type=1&key=${this.mapKey}&referer=myapp`
			const lat = this.newLatitude || this.oldLatitude
			const lng = this.newLongitude || this.oldLongitude
			if (lat && lng) {
				url += `&coord=${lat},${lng}`
			}
			this.qqmapsrc = url + `&t=${Date.now()}`
		},
		syncFormDataPosition(lng, lat) {
			this.formData.longitude = lng
			this.formData.latitude = lat
			this.$emit('position', { lat, lng })
		},
		cancelNewPosition() {
			this.newLongitude = ''
			this.newLatitude = ''
			this.newAddress = ''
			this.syncFormDataPosition(this.oldLongitude, this.oldLatitude)
			this.refreshQqMap()
		},
		async plugin(point = { lat: '', lng: '' }) {
			var qq = await plugin.Tmap()
			if (point.lat == '' || point.lat == 0) {
				point = await this.getLocation()
			}
			this.map = await plugin.center(point.lat, point.lng)
			qq.maps.event.addListener(this.map.marker, 'dragend', e => {
				this.map.map.setCenter(e.latLng)
				this.$emit('position', {
					lat: e.latLng.lat,
					lng: e.latLng.lng
				})
			})
		},
		getLocation() {
			//腾讯api
			return new Promise((resolve, reject) => {
				this.$jsonp('https://apis.map.qq.com/ws/location/v1/ip', {
					key: 'N6RBZ-AJN35-AACI2-Q2ICF-HYV6O-JRBBZ',
					output: 'jsonp'
				}).then(res => {
					resolve({
						lat: res.result.location.lat,
						lng: res.result.location.lng
					})
				})
			})
		},	  
		listen() {
			let that = this
            window.addEventListener('message', function(event) {
                var loc = event.data;
                if (loc && loc.module == 'locationPicker') {
					that.newLongitude = loc.latlng.lng
					that.newLatitude = loc.latlng.lat
					that.newAddress = loc.poiaddress || loc.poiname || ''
					that.syncFormDataPosition(loc.latlng.lng, loc.latlng.lat)
                }
                return false
            }, false);
        },
		handleAvatarSuccess(res, file) {
			this.loading = false
			this.imageUrl = URL.createObjectURL(file.raw)
			if (res.code == 0) {
				this.$message({
					message: '上传成功',
					type: 'success'
				})
				this.$emit('imgs', {
					type: 'success',
					url: res.path[0]
				})
			} else {
				this.$message({
					message: res.msg,
					type: 'error'
				})
			}
		},
		reset() {
			this.oldLongitude = ''
			this.oldLatitude = ''
			this.newLongitude = ''
			this.newLatitude = ''
			this.oldAddress = ''
			this.newAddress = ''
			this.qqmapsrc = ''
			this.$refs.ruleForm.resetFields()
			this.$parent.$data.formData = this.$parent.$options.data.call(this).formData
			if (this.$parent.$data.formTable) {
				this.$parent.$data.formTable.data = this.$parent.$options.data.call(this).formTable.data
			}
		},
		handleAvatarError() {
			this.loading = false
			this.$message.error('上传失败')
		},
		beforeAvatarUpload(file) {
			// const isJPG = file.type === "image/jpeg" || "image/png";
			const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'video/mp4'
			const isLt5M = file.size / 1024 / 1024 < 5000
			if (!isJPG) {
				// this.$message.error("上传格式只能是图片格式!");
				this.$message.error('上传文件只能是 JPG/png/mp4 格式!')
			}
			if (!isLt5M) {
				this.$message.error('上传头像图片大小不能超过 5000MB!')
			}
			return isJPG && isLt5M
		},
		handleAvatarProgress(res, file) {
			this.loading = true
		},
		handleRemove(file, fileList) {
			let url = file.response ? file.response.path[0] : file.url
			this.$emit('imgs', {
				type: 'remove',
				url: url
			})
		},
		handleExceed() {
			this.$message({
				message: '最多上传9张图片',
				type: 'warning'
			})
		},
		handlePictureCardPreview(file) {
			this.dialogImageUrl = file.url
			this.dialogVisible = true
		},
		handleClick(e) {
			const files = e.target.files
			const rawFile = files[0] // only use files[0]
			if (!rawFile) return
			this.upload(rawFile)
		},
		handleUpload() {
			this.$refs['excel-upload-input'][0].click()
		},
		upload(rawFile) {
			this.$refs['excel-upload-input'][0].value = null // fix can't select the same excel
			const before = this.beforeUpload(rawFile)
			if (before) {
				let formData = new FormData()
				formData.append('file', rawFile)
				this.$http.upload('/sys/icInfo/importExcelIc', formData).then(res => {
					if (res.code == 0) {
						this.$message({
							type: 'success',
							message: '上传成功'
						})
					}
				})
			}
		},
		beforeUpload(file) {
			const isLt1M = file.size / 1024 / 1024 < 1
			if (isLt1M) {
				return true
			}
			this.$message({
				message: '小于1兆',
				type: 'warning'
			})
			return false
		}
	}
}
</script>
<style scoped lang="scss">
.avatar-uploader,
.el-upload {
	border: 1px dashed #ccc;
	border-radius: 6px;
	cursor: pointer;
	position: relative;
	overflow: hidden;
	width: 150px;
	height: 150px;
	display: block;
}

.avatar-uploader:hover,
.el-upload:hover {
	border-color: #409eff;
}

.avatar-uploader-icon {
	font-size: 28px;
	color: #8c939d;
	width: 150px;
	height: 150px;
	line-height: 150px;
	text-align: center;
}

.avatar {
	width: 150px;
	height: 150px;
	display: block;
}

.qqmap-picker {
	width: 450px;

	.qqmap-tip {
		margin: 0 0 10px;
		color: #606266;
		font-size: 14px;
		line-height: 1.5;
	}

	.qqmap-coord {
		margin: 0 0 8px;
		color: #303133;
		font-size: 14px;
		line-height: 1.5;
	}

	.qqmap-new-block {
		margin-bottom: 8px;
	}

	.qqmap-new-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-bottom: 8px;

		.qqmap-coord {
			margin: 0;
			flex: 1;
			padding-right: 10px;
		}
	}

	.qqmap-address {
		margin: 0 0 12px;
		color: #303133;
		font-size: 14px;
		line-height: 1.5;
	}

	.qqmap-cancel-btn {
		color: #f56c6c;
		border-color: #f56c6c;
		background: #fff;
		padding: 5px 15px;
		flex-shrink: 0;
	}
}
</style>
<style lang="scss">
.elDialog {
	min-width:500px;
}
</style>
