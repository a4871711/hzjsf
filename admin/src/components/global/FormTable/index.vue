<!-- 搜索表单 -->
<template>
	<div class="ces-form">
		<section class="header" v-if="Handle">
			<el-button type="primary"  size="mini" @click="addTable">新增</el-button>
			<el-button type="danger"  size="mini"  @click="del">删除</el-button>
		</section>
		<el-form :rules="formTable.rules" :model="formTable" ref="ruleForm">
			<el-table 
			:data="formTable.data" 
			border 
			@select="select"
			@select-all="selectAll"
			:header-cell-class-name="headRuleStyle" 
			:header-cell-style="{backgroundColor: '#f5f7fa',fontWeight: 'bold',color: '#909399'}">
				<el-table-column v-if="isSelection" type="selection" align="center"></el-table-column>
				<el-table-column 
				v-for="item in formTable.cols" 
				:size="size"
				:key="item.id" 
				:prop="item.prop" 
				:label="item.label" 
				:width="item.width"
				:align="item.align||'center'" 
				:fixed="item.fixed"
				:show-overflow-tooltip="item.overflow">
					<template slot-scope="scope">
						<el-form-item :prop="`data.${scope.$index}.${[item.prop]}`" :rules="formTable.rules[item.prop]" >
							<!-- 默认 -->
							<span
							v-if="!item.type"
							:style="item.itemStyle && item.itemStyle(scope.row)"
							:class="item.itemClass && item.item.itemClass(scope.row)"
							>{{(item.formatter && item.formatter(scope.row)) || scope.row[item.prop] || '-'}}</span>
							<!-- 输入框 -->
							<el-input v-if="item.type==='input'" v-model="scope.row[item.prop]" :size="item.size || size"></el-input>
							<!-- 下拉框 -->
							<el-select 
							v-if="item.type==='select'"
							v-model="scope.row[item.prop]"
							:size="item.size || size"
							filterable 
							:multiple="item.multiple"
							:placeholder="item.placeholder || '请选择'"
							@change="item.change && item.change(scope.row[item.prop])" 
							@focus="item.focus && item.focus(scope.row[item.prop])"
							:disabled="item.isDisabled && item.isDisabled(scope.row[item.prop])" 
							@remove-tag="item.remove && item.remove(scope.row[item.prop])">
								<el-option v-for="op in item.options" :label="op.label" :value="op.value" :key="op.value"></el-option>
							</el-select>
							<!-- 级联省市区 -->
							<el-cascader 
							v-if="item.type==='cascader'" 
							:placeholder="item.placeholder || '请选择'"
							:options="item.options || []" 
							v-model="scope.row[item.prop]" 
							:size="item.size || size"
							@focus="item.focus && item.focus(scope.row[item.prop])"
							filterable 
							clearable 
							:show-all-levels="item.showAll && item.showAll(scope.row[item.prop])" 
							:props="{ expandTrigger: 'hover',checkStrictly: item.checkStrictly && item.checkStrictly(scope.row[item.prop]),value:item.value?item.value:'vaule' }"
							@change="item.change && item.change(scope.row[item.prop])" 
							ref="cascaderAddr">
							</el-cascader>
							<span v-if="item.type==='button'">
							  <el-button
							    v-for="(btn,idx) in item.btnList"
							    :key="idx"
									:size="item.size || size"
							    :disabled="btn.disabled && btn.disabled(scope.row)"
							    :type="btn.type"
							    :icon="btn.icon"
							    @click="btn.handle(scope.row,scope.$index)"
							  >{{btn.label}}</el-button>
							</span>
						</el-form-item>
					</template>
				</el-table-column>
			</el-table>
		</el-form>
	</div>
</template>

<script>
	export default {
		props: {
			// 表格型号：mini,medium,small
			size: {
			  type: String,
			  default: "mini",
			},
			formTable: {
				type: Object,
				default: () => {}
			},
			isSelection: {
			  type: Boolean,
			  default: true,
			},
			Handle: {
			  type: Boolean,
			  default: true,
			},
			addTable:Function 
		},
		name: 'FormTable',
		data() {
			return {
				checkBox:[]
			};
		},
		created() {

		},
		methods: {
			select(rows, row) {
			  this.$emit("select", rows, row);
				this.checkBox = rows
			},
			// 全选
			selectAll(rows) {
			  this.$emit("select", rows);
				this.checkBox = rows
			},
			del(){
				if(this.checkBox&&this.checkBox.length>0){
					let arr = this.formTable.data.filter(item => !this.checkBox.some(ele => ele === item))
					this.checkBox = []
					this.formTable.data = arr
				}else{
					this.$message({
						message: '请先选择好需要删除的表格',
						type: 'warning'
					});
				}
			},
			headRuleStyle(row, rowIndex){
				if(row.column.property){
					let idx = row.row.findIndex(item=>{
						return item.property == Object.keys(this.formTable.rules)[0]
					})
					// console.log(row.column.property)
					// console.log(Object.keys(this.formTable.rules)[row.columnIndex-idx])
					if(Object.keys(this.formTable.rules)[row.columnIndex-idx] == row.column.property && row.column.property && Object.keys(this.formTable.rules).length!=1){
						return 'rules'; 
					}else if(Object.keys(this.formTable.rules).toString() == row.column.property){
						return 'rules'; 
					}
				}
			}
		}
	}
</script>
<style scoped>
	.header{
		background-color: #f5f7fa;
		font-weight:bold ;
		padding: 10px 10px;
		border: 1px solid #EBEEF5;
		border-bottom: none;
	}
	.el-form >>> .rules .cell:before{
		content: '*';
		color: #f00;
		margin-right: 5px;
		display: inline-block;
	}
/* 	.el-form >>>.el-form-item {margin-bottom: 0;} */
</style>
