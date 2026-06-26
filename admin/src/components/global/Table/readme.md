#table的使用示例与说明

##子组件模板在子组件作用域内编译，如果要用父组件的必须用props传递；子组件里的that参数是为了获取父组件的this
##子组件标签的数据，使用父组件内的data数据


`表格操作按钮(tableHandles)`
> 一般表头都有新增删除导入导出的按钮
- tableHandles:[
	{label:'新增',type:'primary',handle:that=>this.$router.push({name:'addList'})}
	{label:'删除选中',type:'primary',handle:that=>''}],


`数据表格头和尾都固定为"多选"和"操作"`
> 一般表头都有新增删除导入导出的按钮
- 多选可设置Boolean   :isSelection='true'
- 如果没有操作列则不在tableCols里添加{label:'操作',,type:'button'}


`tableCols有许多插槽根据需求自行添加type`
- type:html			可以对这一列进行html模板设置
- {label:'html模板',prop:'html',type:'html',html:row=>`<h1>${row.html}</h1>`}
---------------------------------
- type:button			操作按钮
- {label:'操作',type:'button',btnList:[
		{
			type:'primary',
			label:'编辑',
			handle:(that,row)=>this.$router.push({name:'addList',query:{id:row.id}}),
			disabled:row=>row.status?true:false,
			isShow:row=>row.status?true:false
		},
		{type:'danger',label:'删除',handle:(that,row)=>this.del(this,row)}
	]}
---------------------------------
- type:input			
- {label:'输入框',prop:'input',type:'input',placeholder:'请输入姓名'},
---------------------------------
- type:select     一般在编辑表里面比较常见
- {label:'下拉框',prop:'select',type:'select',
			options:[
				{id: '选项1',label: '黄金糕'}, {id: '选项2',label: '双皮奶'},
			],
			change:(that,row)=>console.log(row)
		},