<template>
	<el-table
			style="width: 100%;margin-bottom: 20px;"
			row-key="id"
			:data="tableData"
			border
			:header-cell-style="{backgroundColor: '#f5f7fa',fontWeight: 'bold',color: '#909399'}">
			<el-table-column
				v-for="(item,idx) in tableCols"
				:key="idx"
				:prop="item.prop"
				:label="item.label"
				:width="item.width"
				:align="item.align || 'left'"
			>
				<template slot-scope="scope">
					<!-- tag -->
					<el-tag
						v-if="item.type==='tag'"
						:type="item.theme && item.theme(scope.row)"
					>{{(item.formatter && item.formatter(scope.row)) || scope.row[item.prop]}}</el-tag>
					<!-- 按钮 -->
					<span v-if="item.type==='button'">
						<el-button
							v-for="(btn,idx) in item.btnList"
							:key="idx"
							:disabled="btn.disabled && btn.disabled(scope.row)"
							v-if="btn.isShow ? btn.isShow(scope.row):true"
							:type="btn.type"
							:size="btn.size || size"
							:icon="btn.icon"
							@click="btn.handle(scope.row,scope.$index)"
						>{{btn.label}}</el-button>
						<el-dropdown v-if="item.dropList" @command="item.handleCommand">
							<el-button icon="el-icon-setting" :size="item.dropSize || size" type="info" />
							<el-dropdown-menu slot="dropdown">
								<el-dropdown-item
									v-for="(drop,idx) in item.dropList"
									:key="idx"
									:command="drop"
								>{{drop}}</el-dropdown-item>
							</el-dropdown-menu>
						</el-dropdown>
					</span>
					<!-- 默认 -->
					<span
						v-if="!item.type"
						:style="item.itemStyle && item.itemStyle(scope.row)"
						:class="item.itemClass && item.item.itemClass(scope.row)"
					>{{(item.formatter && item.formatter(scope.row)) || scope.row[item.prop]}}</span>
				</template>
			</el-table-column>
		</el-table>
</template>
 
<script>
export default {
  name: "TreeTable",
  data() {
    return {
      
    };
  },
  props: {
		// 表格型号：mini,medium,small
		size: {
		  type: String,
		  default: "medium",
		},
    // 表格数据
    tableData: {
      type: Array,
      default: () => [],
    },
    // 表格列配置
    tableCols: {
      type: Array,
      default: () => [],
    },

  },
  computed: {

  },
  methods: {

  },
  mounted() {

  }
};
</script>
<style scoped>

</style>
