<template>
  <div>
    <r-search ref="search" :searchData="searchData" :searchForm="searchForm" :searchHandle="searchHandle" />
    <r-table
      :isSelection="false"
      :isHandle="true"
      :isPagination="true"
      :tableData="tableData"
      :tableCols="tableCols"
      :tablePage="pagination"
      :loading="tableLoading"
      :tableHandles="tableHandles"
      @refresh="page()" />
    <r-form labelWidth="150px" :isHandle="true" :formRules="formRules" :formCols="formCols" :formHandle="formHandle" :formData="formData" :fileList="fileList" @imgs="imgs" @position="position" ref="elForm" :inline="false" dialogWidth="700px" />

  </div>
</template>

<script>
export default {
  components: {
    
  },
  data() {
    var checkPhone = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('手机号不能为空'));
      } else {
        const reg = /^1[3|4|5|7|8][0-9]\d{8}$/
        if (reg.test(value)) {
          callback();
        } else {
          return callback(new Error('请输入正确的手机号'));
        }
      }
    };
    return {
      tableLoading: false,
      searchData: {
        storeName:'',
        storePhone:'',
		goodsIdStoreId:'',
		douyinPoiId:'',
        status:''
      },
      searchForm: [
        { type: "input", placeholder: "门店名称", prop: "storeName", width: 200 },
        { type: "input", placeholder: "门店电话", prop: "storePhone", width: 200 },
        { type: "input", placeholder: "美团门店ID", prop: "goodsIdStoreId", width: 200 },
        { type: "input", placeholder: "抖音门店ID", prop: "douyinPoiId", width: 200 },
        { type: "select", placeholder: "营业状态", prop: "status", width: 200, options:[
			{value: 1, label: '正常'},
			{value: 0, label: '暂停'}
		] },
      ],
      searchHandle: [
        {
          label: "搜索",
          type: "primary",
          handle: e => this.getData()
        },
      ],
      tableHandles: [
        {
          label: "添加门店",
          type: "primary",
          handle: e => {this.fileList = [];this.elFormVisible()} ,
		  isShow: e => this.checkBtn('sys:store:update')
        }
      ],
      tableData: [],
      tableCols: [
        { label: "ID", prop: "storeId" },
        { label: "门店图片", prop: "storeImgUrl", type: 'images' },
        { label: "门店名称", prop: "storeName" },
        { label: "门店电话", prop: "storePhone" },
		{ label: "美团门店ID", prop: "goodsIdStoreId"},
		{ label: "抖音门店ID", prop: "douyinPoiId"},
        { label: "门店地址", prop: "storeAddr" },
        { label: "营业时间", prop: "hours" },
        //{ label: "营业状态", prop: "status", formatter: e => { return e.status == 1 ? '正常' : '暂停' } },
        //{ label: "合伙人", prop: "agent5Name" },
		//{ label: "门店管理员", prop: "agent6Name" },
		{
			label: '营业状态',
			type: "switch",
			prop: 'status',
			values: [1, 0],
			change: (row) => this.changeStatus(row),
			isDisabled: e => !this.checkBtn('sys:store:status')
		},
        { label: "创建时间", prop: 'createdDate', formatter: e => { return this.parseTime(e.createdDate) } },		
        {
          label: "操作",
          type: "button",
          width: 260,
          btnList: [
            {
              label: "编辑",
              type: "success",
              size: "mini",
              icon: "el-icon-edit",
              handle: (row) => this.elFormDetail(row),
			  isShow: e => this.checkBtn('sys:store:save')
            },
            {
              label: "删除",
              type: "danger",
              size: "mini",
              icon: "el-icon-delete",
              handle: (row) => this.del(row),
			  isShow: e => this.checkBtn('sys:store:delete')
            }
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      formData: {
	    storeId: '',
        storeName: '',
        storePhone: "",
		selectArea: [],
		goodsIdStoreId:'',
		douyinPoiId:'',
		province: '',
		city: '',
		zone: '',
		storeAddrDetail: '',
		hours:'',
		status: 1,
		selectLngLat: [],
		longitude: '',
		latitude: '',
		storeImgUrl: '',
		agent5Ids: [],
		agent6Ids: [],
      },
	  fileList: [],
      formCols: [
        { type: "input", label: "门店名称", width: 350, prop: "storeName" },
        { type: "input", label: "门店电话", width: 350, prop: "storePhone" },
        { type: "input", label: "美团门店ID", width: 350, prop: "goodsIdStoreId", placeholder: "美团核销 storeId" },
        { type: "input", label: "抖音门店ID", width: 350, prop: "douyinPoiId", placeholder: "抖音核销 poi_id" },
        { type: "cascader", label: "所在地区", width: 350, prop: "selectArea", change: e=> {
			console.log(e)
			this.formData.province = this.formData.city = this.formData.zone = ''
			if(e && e.length > 0){
				this.formData.province = e[0]
			}
			if(e && e.length > 1){
				this.formData.city = e[1]
			}
			if(e && e.length > 2){
				this.formData.zone = e[2]
			}
		} },	
		{ type: "input", label: "详细地址", width: 350, prop: "storeAddrDetail" },
		{ type: "input", label: "营业时间", width: 350, prop: "hours" },
		{ type: "uploadList", label: "门店图片", placeholder:'宽高：750*413，格式：png, jpg, jpeg', width: 350, prop: "storeImgUrl"},
		//{ type: "select", label: "选择合伙人", width: 350, prop: "agent5Ids", multiple: true, options: [] },
		//{ type: "select", label: "选择门店管理员", width: 350, prop: "agent6Ids", multiple: true, options: [] },
        { type: "radio", label: "营业状态", width: 350, prop: "status", radios: [
			{value: 1, label: '正常'},
			{value: 0, label: '暂停'}
		] },
		{ type: "qqmap", label: "选择位置", width: 350, prop: "selectLngLat" },
      ],
      formRules: {
        storeName: [
          { required: true, message: '请输入', trigger: 'blur' },
        ],
        storePhone: [
          { required: false, message: '请输入', trigger: 'blur' },
        ],
        storeAddrDetail: [
          { required: true, message: '请输入', trigger: 'blur' },
        ],
        storeImgUrl: [
          { required: true, message: '请输入', trigger: 'blur' },
        ],
      },
      formHandle: [
        {
          label: "确认",
          type: "primary",
          icon: "el-icon-circle-plus-outline",
          handle: e => this.elFormSubmit(),
        },
        {
          label: "取消",
          icon: "el-icon-circle-close",
          handle: e => this.elFormVisible(),
        },
      ],
    };
  },
  created() { },
  mounted() {
    this.getData();
	this.getAgent(5)
	this.getAgent(6)
  },
  methods: {
    del(row) {
      this.$confirm('确定要删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        var res = await this.apis.store_delete(
          row.storeId
        );
        this.$message({
          type: 'success',
          message: '删除成功!'
        });
        this.getData();
      }).catch(() => {

      });
    },
	async getAgent(type){
		var res = await this.apis.agent_list({type: type, deleteStatus:0, page: 1, limit: 999});
		  var list = res.page.list || [];
		  list.map(res => {
			res.value = res.id;
			res.label = res.name;
		  });

      	this.formCols[this.labIndex(this.formCols, type == 5 ? '选择合伙人' : '选择门店管理员')].options = list;
	},
    async getData() {
      var res = await this.apis.store_list(Object.assign({
        page: this.pagination.offset,
        limit: this.pagination.limit
        
      }, this.searchData));
      var list = res.page.list || [];
	  list.map(res => {
	  	var temp = []
		if(res.sysUser5 && res.sysUser5.length > 0){
			res.sysUser5.map(rres => {
				temp.push(rres.username)
			})
		}
		res.agent5Name = temp.join(', ');
		
		var temp = []
		if(res.sysUser6 && res.sysUser6.length > 0){
			res.sysUser6.map(rres => {
				temp.push(rres.username)
			})
		}
		res.agent6Name = temp.join(', ');
		
		res.selectArea = [res.province, res.city, res.zone]
	  })
      this.tableData = list;
      this.pagination.total = res.page.totalCount;
    },
    page() {
      this.getData();
    },
    createdAcc() {
      this.elFormVisible();
    },
	position(e){
		console.log(e)
		this.formData.longitude = e.lng
		this.formData.latitude = e.lat
	},
	imgs(e){
		console.log(e)
		let temp = []
		let fileList = this.fileList
		if(e.type == 'success'){
			temp = fileList.filter(res => {
				return res.url == e.url
			})
			if(temp.length > 0)return
			fileList.push({
				name: e.url,
				url: e.url
			})
		}else if(e.type == 'remove'){
			temp = fileList.filter(res => {
				return res.url != e.url
			})
			fileList = temp
		}
		temp = []
		fileList.map(res => {
			temp.push(res.url)
		})
		console.log(fileList)
		this.fileList = fileList;
		this.formData.storeImgUrl = temp.join(',')
	},
    async elFormDetail(row) {

      Object.keys(this.formData).forEach((key) => {
		this.formData[key] = row[key];
	  });
	  this.formData.agent5Ids = this.formData.agent5Ids ? this.formData.agent5Ids.split(',') : []	  
	  this.formData.agent6Ids = this.formData.agent6Ids ? this.formData.agent6Ids.split(',') : []
	  this.formData.agent5Ids = this.formData.agent5Ids.map(res => { return res * 1 })
	  this.formData.agent6Ids = this.formData.agent6Ids.map(res => { return res * 1 })
	  let temp = row.storeImgUrl ? row.storeImgUrl.split(',') : []
	  let fileList = []
	  temp.map(res => {
	  	fileList.push({
			name: res,
			url: res
		})
	  })
	  this.fileList = fileList

      this.elFormVisible('编辑')
    },
    elFormSubmit() {
      this.$refs.elForm.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.submit();
        }
      })
    },
    async submit() {
	  console.log(this.formData)
	  let formData = Object.assign({}, this.formData)
	  formData.agent5Ids = formData.agent5Ids ? formData.agent5Ids.join(',') : ''
	  formData.agent6Ids = formData.agent6Ids ? formData.agent6Ids.join(',') : ''
	  let data = Object.assign(formData, {groupName: '', groupImg: '', selectArea: null, selectLngLat: null, agent5: null, agent6: null, stats: null})
      if (!data.storeId) {
        var res = await this.apis.store_save(data);
      } else {
        var res = await this.apis.store_update(data);
      }
      this.elFormVisible();	  

      this.getData();

    },
	async changeStatus(row) {
		var res = await this.apis.store_update(Object.assign(row, {groupName: '', groupImg: '', selectArea: null, selectLngLat: null, agent5: null, agent6: null, stats: null}))
		//this.getData();
	}
  },
};
</script>

<style scoped lang="scss">
</style>
