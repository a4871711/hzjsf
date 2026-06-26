<template>
	<section>
		<p class="ft16 mtb20" v-if="title">
			<i class="el-icon-s-flag"></i>
			<span class="ml10">{{ title }}</span>
		</p>
		<table v-if="ListCols.length&&ListType=='table'">
			<tbody v-for="(item,index) in ListCols" :key="`${index}`">
				<tr>
					<th 
					  v-for="(op,idx) in item" :key="`th${idx}`" 
					  :style="{minWidth:item.width||width+'px'}">
					  {{op.label||'-'}}
					</th>
				</tr>
				<tr>
					<td 
					　v-for="(op,idx) in item" 
					　v-if="op.isShow ? op.isShow(idx):true" 
					　:key="`td${idx}`" 
					　:style="{minWidth:item.width||width+'px',color:op.color}">
						<p v-if="!op.type||op.showText">
							{{op.formatter && op.formatter(ListData) || ListData[op.prop]||'-'}}
						</p>
						<div v-if="op.type=='img'">
							<div class="imglist" v-if="op.type=='img'&&ListData[op.prop].length">
								<img 
								　v-if="typeof(ListData[op.prop])=='object'" 
								　v-for="(url,idx) in ListData[op.prop]" 
								　:key="idx" 
								　:src="url|formatImg" 
								　alt 
								　style="width: 50px;height: 50px;" 
								/>
								<img 
								　v-if="typeof(ListData[op.prop])=='string'" 
								　:src="ListData[op.prop]|formatImg" 
								　alt 
								　style="width: 50px;height: 50px;" 
								/>
							</div>
							<el-popover 
							:placement="item.placement || 'left'" 
							trigger="click"
							>
								<div>
									<el-carousel 
									　trigger="click" 
									　style="width: 500px;height: auto;" 
									　arrow="always" 
									　indicator-position="none" 
									　:autoplay="false"
									>
										<el-carousel-item 
										　v-for="(url,idx) in ListData[op.prop]" 
										　:key="idx"
										>
											<img 
											　:src="url|formatImg" 
											　alt 
											　style="width: 500px;height: 300px;" 
											/>
										</el-carousel-item>
									</el-carousel>
								</div>
								<el-button 
								　v-if="op.imglook" 
								　type="text" 
								　slot="reference"
								>
								查看
								</el-button>
							</el-popover>
						</div>
						<div v-if="op.type=='button'">
							<el-button 
							　v-for="(btn,idx) in op.btnList" 
							　:key="idx" 
							　:disabled="btn.disabled && btn.disabled(idx)" 
							　v-if="btn.isShow ? btn.isShow(idx):true" 
							　:type="btn.type" 
							　:icon="btn.icon" 
							　@click="btn.handle(idx)"
							>
								{{btn.label}}
							</el-button>
						</div>
						<div v-if="op.type=='input'">
							<el-input 
							　v-model="ListData[op.prop]" 
							　:style="{width:op.width+'px'}" 
							　@change="op.change && op.change(ListData[op.prop])" 
							　:placeholder="`请输入${op.placeholder||''}`" 
							　:disabled="op.isDisabled && op.isDisabled(ListData[op.prop])"
							>
							</el-input>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
		<table v-if="ListRows.length&&ListType=='table'">
			<tbody v-for="(item,index) in ListRows" :key="`${index}`">
				<tr 
				　v-for="(op,idx) in item" 
				　:key="`th${idx}`" 
				　v-if="op.isShow ? op.isShow(idx):true"
				>
					<th :style="{minWidth:item.width||width+'px'}">{{op.label||'-'}}</th>
					<td 
					　v-if="!op.type" 
					　:key="`td${idx}`" 
					　:style="{
					　　minWidth:item.width||width+'px',
					　　textAlign:'left',
					　　paddingLeft:'10px',
					　　color:op.color,
					　　minWidth:'500px'
					}"
					>
						{{op.formatter && op.formatter(ListData) || ListData[op.prop]||'-'}}
					</td>
					<td v-if="op.type=='img'&&ListData[op.prop]">
						<div class="imglist">
							<img 
							　v-if="typeof(ListData[op.prop])=='object'" 
							　v-for="(url,idx) in ListData[op.prop]" 
							　:key="idx" 
							　:src="url|formatImg" 
							　alt 
							　style="width: 50px;height: 50px;" 
							/>
							<img 
							　v-if="typeof(ListData[op.prop])=='string'" 
							　:src="ListData[op.prop]|formatImg" 
							　alt 
							　style="width: 50px;height: 50px;" 
							/>
						</div>
						<el-popover :placement="item.placement || 'left'" trigger="click">
							<div>
								<el-carousel 
								　trigger="click" 
								　style="width: 500px;height: auto;" 
								　arrow="always" 
								　indicator-position="none" 
								　:autoplay="false"
								>
									<el-carousel-item 
									　v-for="(url,idx) in ListData[op.prop]" 
									　:key="idx"
									>
										<img 
										　:src="url|formatImg" 
										　alt 
										　style="width: 500px;height: 300px;" 
										/>
									</el-carousel-item>
								</el-carousel>
							</div>
							<el-button 
							　v-if="op.imglook" 
							　type="text" 
							　slot="reference"
							>
							查看
							</el-button>
						</el-popover>
					</td>
					<td v-if="op.type=='button'">
						<p 
						　v-if="!op.type||op.showText" 
						　:key="`td${idx}`" 
						　:style="{
							minWidth:item.width||width+'px',
							textAlign:'left',
							paddingLeft:'10px',
							color:op.color,minWidth:'500px',
							display:'inline-block'
							}"
						>
							{{op.formatter && op.formatter(ListData) || ListData[op.prop]||'-'}}
						</p>
						<el-button 
						　v-for="(btn,idx) in op.btnList" 
						　:key="idx" 
						　:disabled="btn.disabled && btn.disabled(idx)" 
						　v-if="btn.isShow ? btn.isShow(idx):true" 
						　:type="btn.type" 
						　:icon="btn.icon" 
						　@click="btn.handle(idx)"
						>
							{{btn.label||'-'}}
						</el-button>
					</td>
					<td v-if="op.type=='input'">
						<el-input 
						　v-model="ListData[op.prop]" 
						　:style="{width:op.width+'px'}" 
						　@change="op.change && op.change(ListData[op.prop])" 
						　:placeholder="`请输入${op.placeholder||''}`" 
						　:disabled="op.isDisabled && op.isDisabled(ListData[op.prop])">
						</el-input>

						<el-upload 
						　style="display: inline-block" 
						　v-if="op.upload" class="upload-demo" 
						　:action="op.upload.action" 
						　name="files" 
						　:limit="1" 
						　:on-success="op.upload.success" 
						　:on-exceed="op.upload.handleExceed">
							<el-button 
							size="mini" 
							type="primary"
							>点击上传
							</el-button>
						</el-upload>

					</td>
				</tr>
			</tbody>
		</table>
		<table v-if="ListCols.length&&ListType=='Info'">
			<tbody 
			v-for="(item,index) in ListCols" 
			:key="`${index}`">
				<tr 
				v-for="(it) in Math.ceil((item.length)/ColsNum)" 
				:key="it">
					<td 
					　v-for="(op,idx) in item" 
					　:key="`th${idx}`" 
					　v-if="idx<it*ColsNum&&
					　idx>=(it-1)*ColsNum&&
					　(op.isShow ? op.isShow(idx):true)">
						<p class="bgf9" 
						v-if="!op.hiddenHead" 
						:style="{minWidth:item.width||width+'px'}">
						{{op.label||'-'}}
						</p>
						<div v-if="op.hiddenHead" class="headimg">
							<img 
							　:src="ListData[op.prop]|formatImg" 
							　alt 
							　style="width: 100%;height:auto" />
						</div>
						<div 
						　class="Item" 
						　v-if="!op.hiddenHead" 
						　:style="{minWidth:item.width||width+'px',color:op.color}">
							<p v-if="!op.type||op.showText">
							{{(op.formatter && op.formatter(ListData)) || (ListData[op.prop]===0?0:ListData[op.prop]?ListData[op.prop]:'-')}}
							</p>
							<!-- <p v-if="!op.type||op.showText">{{op.formatter && op.formatter(ListData) || ListData[op.prop]||'-'}}</p> -->
							<div 
							　class="imgitem" 
							　v-if="op.type=='img'&&ListData[op.prop]!=null">
								<div class="imglist">
									<img 
									　v-if="typeof(ListData[op.prop])=='object'" 
									　v-for="(url,idx) in ListData[op.prop]" 
									　:key="idx" 
									　:src="url|formatImg" 
									　alt 
									　style="width: 50px;height: 50px;" />
									<img 
									　v-if="typeof(ListData[op.prop])=='string'" 
									　:src="ListData[op.prop]|formatImg" 
									　alt 
									　style="width: 50px;height: 50px;" />
								</div>
								<el-popover 
								:placement="item.placement || 'left'" 
								trigger="click">
									<div>
										<el-carousel 
										　trigger="click" 
										　style="width: 500px;height: auto;" 
										　arrow="always" 
										　indicator-position="none" 
										　:autoplay="false">
											<el-carousel-item 
											　v-if="typeof(ListData[op.prop])=='object'" 
											　v-for="(url,idx) in ListData[op.prop]" 
											　:key="idx">
												<img 
												　:src="url|formatImg" 
												　style="width: 500px;height: 300px;" />
											</el-carousel-item>
                      						<el-carousel-item 
											  v-if="typeof(ListData[op.prop])=='string'" >
                      						  <img 
												:src="ListData[op.prop]|formatImg" 
												style="width: 500px;height: 300px;" />
                      						</el-carousel-item>

										</el-carousel>
									</div>
									<el-button 
									　v-if="op.imglook" 
									　type="text" 
									　slot="reference">
									<i>查看</i> 
									</el-button>
								</el-popover>
							</div>
							<div 
							v-if="op.type=='img'&&
							ListData[op.prop]==null">
							暂无
							</div>
							<div v-if="op.btnisShow ? op.btnisShow(idx):op.type=='button'?true:false">
								<el-button 
								　v-for="(btn,idx) in op.btnList" 
								　:key="idx" 
								　:disabled="btn.disabled && btn.disabled(idx)" 
								　:type="btn.type" 
								　:icon="btn.icon" 
								　@click="btn.handle" 
								　v-if=" op.btnisShow ? op.btnisShow(idx):true">
									{{btn.label}}
								</el-button>
							</div>
							<div v-if="op.type=='input'">
								<el-input 
								　v-model="ListData[op.prop]" 
								　:style="{width:op.width+'px'}" 
								　@change="op.change && op.change(ListData[op.prop])" 
								　:placeholder="`请输入${op.placeholder||''}`" 
								　:disabled="op.isDisabled && op.isDisabled(ListData[op.prop])">
								</el-input>
							</div>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
	</section>
</template>

<script>
export default {
	name: "List",
	props: {
		title: {
			type: String,
			default: ""
		},
		width: {
			type: Number,
			default: 175
		},
		ColsNum: {
			type: [Number, String],
			default: ""
		},
		ListCols: {
			type: Array,
			default: () => []
		},
		ListRows: {
			type: Array,
			default: () => []
		},
		ListData: {
			type: Object,
			default: () => ({})
		},
		ListType: {
			type: String,
			default: "table"
		}
	},
	data() {
		return {};
	}
};
</script>

<style scoped lang="scss">
table,
table tr th,
table tr td {
  border: 1px solid #e4e4e4;
}
table {
  width: 100%;
  min-height: 45px;
  line-height: 45px;
  text-align: center;
  border-collapse: collapse;
  padding: 2px;
}
table tr th {
  background-color: #f9fafc;
}
.bgf9 {
  background-color: #f9fafc;
}
.imglist {
  margin: 2px 0 5px;
  display: flex;
  flex-wrap: wrap;
  min-width: 180px;
  max-height: 100%;
  img {
    margin: 5px 5px 0;
  }
}
.Item {
  text-align: center;
  padding: 10px 0;
  height: 70px;
  display: flex;
  justify-content: space-around;
  align-items: center;
  border-top: 1px solid #e4e4e4;
}
.imgitem {
  display: flex;
  width: 100%;
  align-items: center;
}
.headimg {
  margin: auto;
  //   display: flex;
  //   justify-content: center;
  //   align-items: center;
  width: 140px;
  text-align: center;
  img {
    margin: 5px 5px 0;
  }
}
</style>
