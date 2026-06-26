export default {
	data() {
		return {
			baseUrl:process.env.VUE_APP_IMG
		}
	},
	methods: {
		elFormVisible(tit,elform) {
		  if(!elform){
        if(this.$refs.elForm.$refs.ruleForm){
          this.$refs.elForm.$refs.ruleForm.resetFields()
          this.$data.formData = this.$options.data.call(this).formData;
        }
        this.$refs.elForm.dialogFormVisible = !this.$refs.elForm.dialogFormVisible
        this.$refs.elForm.dialogFormTitle = tit?tit:this.formData.id?'编辑':'新增'

        if(this.$refs.elForm.dialogFormVisible){
          return new Promise((resolve, reject) => {
            setTimeout(()=>{
              resolve();
            },2000)
          })
        }
      }else{
        if(this.$refs[elform].$refs.ruleForm){
          this.$refs[elform].$refs.ruleForm.resetFields()
          if(this.formData){
            this.$data.formData = this.$options.data.call(this).formData;
          }
          if(this.formData2){
            this.$data.formData2 = this.$options.data.call(this).formData2;
          }

        }
        this.$refs[elform].dialogFormVisible = !this.$refs[elform].dialogFormVisible
        if(this.formData){
          this.$refs[elform].dialogFormTitle = tit?tit:this.formData.id?'编辑':'新增'
        }
        if(this.formData2){
          this.$refs[elform].dialogFormTitle = tit?tit:this.formData2.id?'编辑':'新增'
        }


        if(this.$refs[elform].dialogFormVisible){
          return new Promise((resolve, reject) => {
            setTimeout(()=>{
              resolve();
            },2000)
          })
        }
      }


		},
		labIndex(arr,idx){
			return arr.map(item => item.label).indexOf(idx)
		},
    labIndex2(arr,idx){
      return arr.map(item => item.prop).indexOf(idx);
    },
		searchIndex(arr,idx){
			return arr.map(item => item.placeholder).indexOf(idx)
		},
		dataFromReset(ref) {
			this.formData.id = ''
		  this.$refs[ref].$refs.ruleForm.resetFields()
		},
		searchFromReset() {
		  if(this.$data.searchData){
			  Object.assign(this.$data.searchData,this.$options.data.call(this).searchData)
				Object.assign(this.$data.pagination,this.$options.data.call(this).pagination)
				this.init()
		  }
		},
		valIndex(arr,idx){
			return arr.map(item => item.value).indexOf(idx)
		},
		filter(arr,idx){
			return arr[arr.map(item => item.value).indexOf(idx)].label
		},
		checkDate(date){
			if(date == "") {
					return true;
			}
			var r = date.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
			if (r==null) {
					return false;
			}
			else {
					return true;
			}
		},
		TableDel(url,data,type){
			this.$confirm(`删除当前这条数据`, '提示', {
			  confirmButtonText: '确定',
			  cancelButtonText: '取消',
			  type: 'warning'
			}).then(() => {
				if(type=="del"){
					this.$http.del(url,data).then(res=>{
						if(res.code==0){
							this.$message({
								type: 'success',
								message: '删除成功!'
							});
							this.init()
						}
					})
				}else{
					this.$http.post(url,data).then(res=>{
						if(res.code==0){
							this.$message({
								type: 'success',
								message: '删除成功!'
							});
							this.init()
						}
					})
				}
			})
		},
		/*
		 * 导出Excel功能
		 * 直接调用方法this.handleDownload();标签的label用‘导出Excel’才有loading的样式
		 * 需要先安装npm i --save file-saver xlsx ;  npm i --save-dev script-loader
		 * 同级文件里的Export2Excel.js;引用于花裤衩的ue-element-admin
		 * 使用的时候需要在页面注入formatJson方法,下面有一个模板
		 * formatterExcel方法也可以根据需求做稍微调整
		 *
		 */
		handleDownload() {
			if(this.tableData==undefined || this.tableData.length==0) return this.$message.error('请先生成数据再导出Excel');
			if(this.formatJson){
				let idx = this.searchHandle.map(item => item.label).indexOf('导出Excel')
				if(idx!=-1) this.searchHandle[idx].loading = true
				import('@/mixins/Export2Excel').then(excel => {
				  const tHeader = this.formatterExcel({tHeader:this.tableCols},'label')
				  const filterVal = this.formatterExcel({filterVal:this.tableCols},'prop')
				  const list = this.tableData
				  const data = this.formatJson(filterVal, list)
				  excel.export_json_to_excel({
				    header: tHeader,
				    data,
				    filename: this.$route.meta.title,
				    autoWidth: true,
				    bookType: 'xlsx'
				  })
					if(idx!=-1) this.searchHandle[idx].loading = false
				})
			}else{
				this.$message.error('请先在页面注入formatJson方法');
			}
		},
		formatterExcel({tHeader='',filterVal=''}={},type){
			if(type=='prop'){
				let arr = []
				for(var i=0 ; i<filterVal.length ; i++) {
				    if(!filterVal[i].prop || filterVal[i].prop==='qrcode') { continue; }
				    arr.push(filterVal[i].prop)
				}
				return arr
			}else if(type=='label'){
				let arr = []
				for(var i=0 ; i<tHeader.length ; i++) {
				    if( tHeader[i].label==='操作' | tHeader[i].label==='设备二维码') { continue; }
				    arr.push(tHeader[i].label)
				}
				return arr
			}
		},
		// formatJson(filterVal, jsonData) {
		//   return jsonData.map(v => filterVal.map(j => {
		//     if (j === 'status') {
		//       return v[j] == 1 ? "在线" : "离线"
		//     } else {
		//       return v[j]
		//     }
		//   }))
		// },
		getYMD(t, format = '-') {
		  t = t.toString().length === 13 ? t : t * 1000
		  let date = new Date(t),
		      Y = date.getFullYear(),
		      M = date.getMonth() + 1,
		      D = date.getDate();
		  M = M < 10 ? '0' + M : M
		  D = D < 10 ? '0' + D : D
		  return [Y, M, D].join(format)
		},
		getHMS(t, format = ':',unit) {
		  t = t.toString().length === 13 ? t : t * 1000
		  let date = new Date(t),
		      h = date.getHours(),
		      m = date.getMinutes(),
		      s = date.getSeconds();
		  h = h < 10 ? '0' + h : h
		  m = m < 10 ? '0' + m : m
		  s = s < 10 ? '0' + s : s
		  return unit===true?[h, m, s].join(format):[h, m].join(format)
		},
		getDeta(time, format = '-,:') {
		  if (!time) {
		    return '-'
		  }
		  time = time.toString().length === 13 ? time : time * 1000
		  let sep = format.split(',')
		  return `${this.getYMD(time, sep[0])} ${this.getHMS(time,sep[1],true)}`
		}
	}
}
