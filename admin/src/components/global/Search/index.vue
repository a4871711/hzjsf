<!-- 搜索表单 -->
<template>
  <div class="ces-search">
    <el-form :size="size" :inline="inline" :label-width="labelWidth" ref="ruleForm" :rules="searchRules" :model="searchData" :label-position="labelPosition">
      <el-form-item v-for="item in searchForm" :label="item.label" :key="item.label" :prop="item.prop">
        <!-- 输入框 -->
        <el-input
          clearable
          v-if="item.type==='input'"
          v-show="item.isShow?item.isShow():true"
          v-model="searchData[item.prop]"
          :size="size || item.size"
          :style="{width:item.width+'px'}"
          @change="item.change && item.change(searchData[item.prop])"
          :placeholder="`请输入${item.placeholder||''}`"
          :disabled="item.isDisabled && item.isDisabled(searchData[item.prop])"
        ></el-input>
				<!-- 文本域 -->
				<el-input
				  v-if="item.type==='textarea'"
				  type="textarea"
				  :rows="4"
				  :size="size || item.size"
				  :style="{width:item.width+'px'}"
				  :disabled="item.isDisabled && item.isDisabled(searchData[item.prop])"
				  @change="item.change && item.change(searchData[item.prop])"
				  v-model="searchData[item.prop]"
				></el-input>
        <!-- 下拉框 -->
        <el-select
          clearable
          v-if="item.type==='select'"
          v-show="item.isShow?item.isShow():true"
          v-model="searchData[item.prop]"
          filterable
          :size="size || item.size"
          :style="{width:item.width+'px'}"
          :multiple="item.multiple"
          :placeholder="`请选择${item.placeholder||''}`"
          @change="item.change && item.change(searchData[item.prop], item)"
          @visible-change="item.focus && item.focus($event,searchData[item.prop])"
          :disabled="item.isDisabled && item.isDisabled(searchData[item.prop])"
        >
          <el-option v-for="op in item.options" :label="op.label" :value="op.value" :key="op.value"></el-option>
        </el-select>
        <!-- 级联省市区 -->
        <el-cascader
          v-if="item.type==='cascader'"
          :style="{width:item.width+'px'}"
          :placeholder="item.placeholder || '请选择'"
          :options="item.options || []"
          v-model="searchData[item.prop]"
          @focus="item.focus && item.focus(searchData[item.prop])"
          filterable
          clearable
          :show-all-levels="item.showAll && item.showAll(searchData[item.prop])"
          :props="{ expandTrigger: 'hover',checkStrictly: item.checkStrictly && item.checkStrictly(searchData[item.prop]) }"
          @change="item.change && item.change(searchData[item.prop])"
        ></el-cascader>
        <!-- 单选 -->
        <el-radio-group
          v-if="item.type==='radio'"
          v-model="searchData[item.prop]"
          :style="{width:item.width+'px'}"
          @change="item.change && item.change(searchData[item.prop])"
          :size="size || item.size"
          :disabled="item.isDisabled && item.isDisabled(searchData[item.prop])"
        >
          <el-radio v-for="ra in item.radios" :label="ra.value" :key="ra.value">{{ra.label}}</el-radio>
        </el-radio-group>
        <!-- 单选按钮 -->
        <el-radio-group
          v-if="item.type==='radioButton'"
          v-model="searchData[item.prop]"
          :style="{width:item.width+'px'}"
          @change="item.change && item.change(searchData[item.prop])"
          :size="size || item.size"
          :disabled="item.isDisabled && item.isDisabled(searchData[item.prop])"
        >
          <el-radio-button v-for="ra in item.radios" :label="ra.value" :key="ra.value">{{ra.label}}</el-radio-button>
        </el-radio-group>
        <!-- 复选框 -->
        <el-checkbox-group
          v-if="item.type==='checkbox'"
          v-model="searchData[item.prop]"
          :style="{width:item.width+'px'}"
          @change="item.change && item.change(searchData[item.prop])"
          :size="size || item.size"
          :disabled="item.isDisabled && item.isDisabled(searchData[item.prop])"
        >
          <el-checkbox v-for="ch in item.checkboxs" :label="ch.value" :key="ch.value">{{ch.label}}</el-checkbox>
        </el-checkbox-group>
        <!-- 日期月份 -->
         <el-date-picker
         v-if="item.type==='dateMonth'"
          v-model="searchData[item.prop]"
          :style="{width:item.width+'px'}"
          format="yyyy-MM"
					value-format="yyyy-MM"
          type="month"
          @change="item.change && item.change(searchData[item.prop])"
          :size="size || item.size"
          :disabled="item.isDisabled && item.isDisabled(searchData[item.prop])"
          :placeholder="`请选择${item.placeholder||''}`"
          >
        </el-date-picker>
        <!-- 日期 -->
        <el-date-picker
          v-if="item.type==='date'"
          v-model="searchData[item.prop]"
          :style="{width:item.width+'px'}"
					format="yyyy-MM-dd"
					value-format="yyyy-MM-dd"
          :placeholder="`请选择${item.placeholder||''}`"
          @change="item.change && item.change(searchData[item.prop])"
          :size="size || item.size"
          :disabled="item.isDisabled && item.isDisabled(searchData[item.prop])"
        ></el-date-picker>
        <!-- 开始-结束日期 -->
        <el-date-picker
          v-if="item.type==='daterange'"
          type="daterange"
					format="yyyy-MM-dd"
					:value-format="item.value_format?item.value_format:'yyyy-MM-dd'"
          v-model="searchData[item.prop]"
          :style="{width:item.width+'px'}"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="item.change && item.change(searchData[item.prop])"
          :size="size || item.size"
          :disabled="item.isDisabled && item.isDisabled(searchData[item.prop])"
        ></el-date-picker>
        <!-- 时间 -->
        <el-time-select
          v-if="item.type==='time'"
          v-model="searchData[item.prop]"
          format="HH:mm:ss"
          value-format="HH:mm:ss"
          :style="{width:item.width+'px'}"
          @change="item.change && item.change(searchData[item.prop])"
          :size="size || item.size"
					:placeholder="`请选择${item.placeholder||''}`"
          :disabled="item.isDisabled && item.isDisabled(searchData[item.prop])"
          :picker-options="{
            start: '00:00',
            step: '00:15',
            end: '23:59'
           }">
        ></el-time-select>

        <!-- 日期时间 -->
        <el-date-picker
          v-if="item.type==='dateTime'"
          type="datetime"
          :style="{width:item.width+'px'}"
          v-model="searchData[item.prop]"
          @change="item.change && item.change(searchData[item.prop])"
          :size="size || item.size"
					:placeholder="`请选择${item.placeholder||''}`"
          :disabled="item.isDisabled && item.isDisabled(searchData[item.prop])"
        ></el-date-picker>
        <!-- 滑块 -->
        <!-- <el-slider v-if="item.type==='Slider'" v-model="searchData[item.prop]"></el-slider> -->
        <!-- 开关 -->
        <el-switch
          v-if="item.type==='switch'"
          v-model="searchData[item.prop]"
          :size="size || item.size"
					:active-value="item.values&&item.values[0]"
					:inactive-value="item.values&&item.values[1]"
          @change="item.change && item.change(searchData[item.prop])"
          :style="{width:item.width+'px'}"
          :disabled="item.isDisabled && item.isDisabled(searchData[item.prop])"
        ></el-switch>
        <!-- 图片上传 -->
        <section v-if="item.type==='upload'" :style="{width:item.width+'px'}">
          <el-upload
            v-loading="loading"
            class="avatar-uploader"
            :action="upload_url+'/common/upload'"
            :show-file-list="false"
            :on-success="item.success"
            :on-error="handleAvatarError"
            :on-progress="handleAvatarProgress"
            :before-upload="beforeAvatarUpload"
            name="file"

          >
            <img v-if="searchData[item.prop]" :src="imgurl+searchData[item.prop]" class="avatar" />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </section>
        <!-- 多图片上传 -->
        <section v-if="item.type==='uploadList'" :style="{width:item.width+'px'}">
          <el-upload
            v-loading="loading"
            :action="upload_url+'/file/upload'"
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

						name="file"
          >
            <i class="el-icon-plus"></i>
          </el-upload>
          <el-dialog :visible.sync="dialogVisible"  title="预览" width="600px">
            <img width="100%" :src="dialogImageUrl" alt />
          </el-dialog>
        </section>
				<!-- 富文本 -->
				<r-tinymce
				  v-if="item.type==='tinymce'"
				  v-model="searchData[item.prop]"
				  :height="300"
				  :width="600"
				></r-tinymce>

        <i v-if="item.remark">{{item.remark}}</i>

        <slot v-if="item.type==='slot'" :name="item.name"></slot>

      </el-form-item>
      <el-form-item v-for="item in searchHandle" :key="item.label">
        <el-button
					v-if="item.theme===undefined&&item.isShow?item.isShow():true"
          :type="item.type"
          :size="item.size || size"
					:loading="item.loading"
					:icon="item.icon"
          @click="item.handle"
					:style="{width:item.width+'px'}">{{item.label}}</el-button>

					<div v-if="item.theme==='upload'">
					  <input ref="excel-upload-input" class="excel-upload-input" type="file" accept=".xlsx, .xls" @change="handleClick">
						<el-button :loading="item.loading" :size="item.size || size" type="success" icon="el-icon-upload2" @click="handleUpload">
							{{item.label}}
						</el-button>
					</div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: "Search",
  props: {
    isHandle: {
      type: Boolean,
      default: true,
    },
    inline: {
      type: Boolean,
      default: true,
    },
    labelPosition:{
      type: String,
      default: "left",
    },
    labelWidth: {
      type: String,
      default: "100px",
    },
    // 搜索框型号：mini,medium,small
    size: {
      type: String,
      default: "medium",
    },
    searchForm: {
      type: Array,
      default: () => [],
    },
    searchHandle: {
      type: Array,
      default: () => [],
    },
    searchData: {
      type: Object,
      default: () => {},
    },
		searchRules: {
      type: Object,
      default: () => {},
    },
		fileList: {
		  type: Array,
		  default: () => [],
		},
  },
  computed:{
    token(){
      return localStorage.token
    }
  },
  data() {
    return {
      loading: false,
      imageUrl: "",
      upload_url: process.env.VUE_APP_URL,
      imgurl: process.env.VUE_APP_IMG,
			dialogVisible: false,
			dialogImageUrl:''
    };
  },
  methods: {
    handleAvatarSuccess(res, file) {
      this.loading = false;
      this.imageUrl = URL.createObjectURL(file.raw);
      this.$message({
        message: "上传成功",
        type: "success",
      });
      this.$emit("imgs", {
        type: "text",
        url: `${res.data.value}`,
      });
    },
		handleUpload() {
		  this.$refs['excel-upload-input'][0].click()
		},
		handleClick(e){
			const files = e.target.files
			const rawFile = files[0] // only use files[0]
			if (!rawFile) return
			this.upload(rawFile)
		},
		upload(rawFile) {
		  this.$refs['excel-upload-input'][0].value = null // fix can't select the same excel
		  const before = this.beforeUpload(rawFile)
		  if (before) {
		    let formData = new FormData();
		    formData.append('file',rawFile);
		    this.$http.upload('/file/upload',formData).then(res=>{
					if(res.code == 0){
						this.$message({
						  type: "success",
						  message:  "上传成功",
						});
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
		},
    handleAvatarError() {
      this.loading = false;
      this.$message.error("上传失败");
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === "image/jpeg" || "image/png";
      const isLt5M = file.size / 1024 / 1024 < 5;
      if (!isJPG) {
        this.$message.error("上传格式只能是图片格式!");
      }
      if (!isLt5M) {
        this.$message.error("上传头像图片大小不能超过 5MB!");
      }
      return isJPG && isLt5M;
    },
    handleAvatarProgress(res, file) {
      // this.loading = true;
    },
    handleRemove(file, fileList) {
      let list = fileList.map((item) => {
				console.log(item)
				if(item.response){
					return item.response.data.value
				}else{
					if(item.url.indexOf('http')!=-1){
						return item.url.split(process.env.VUE_APP_IMG)[1]
					}else{
						return item.url
					}
				}
			});
      this.$emit("imgs", {
        type: "pictureCard",
        url: list,
      });
    },
    handleExceed() {
      this.$message({
        message: "最多上传9张图片",
        type: "warning",
      });
    },
		handlePictureCardPreview(file){
			this.dialogImageUrl = file.url;
			this.dialogVisible = true;
		}
  },
};
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

.excel-upload-input{
  display: none;
  z-index: -9999;
}
</style>
