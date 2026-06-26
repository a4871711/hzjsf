<template>
  <nav class="site-navbar" :class="'site-navbar--' + navbarLayoutType">
    <div class="site-navbar__header">
      <h1 class="site-navbar__brand" @click="$router.push({ name: 'home' })">
        <a class="site-navbar__brand-lg" href="javascript:;">矢历运动管理系统</a>
        <a class="site-navbar__brand-mini" href="javascript:;">矢历</a>
      </h1>
    </div>
    <div class="site-navbar__body clearfix">
      <el-menu
        class="site-navbar__menu"
        mode="horizontal">
        <el-menu-item class="site-navbar__switch" index="0" @click="sidebarFold = !sidebarFold">
          <icon-svg name="zhedie"></icon-svg>
        </el-menu-item>
      </el-menu>
      <el-menu
        class="site-navbar__menu site-navbar__menu--right"
        mode="horizontal">
        
        <el-menu-item class="site-navbar__avatar" index="3">
          <el-dropdown :show-timeout="0" placement="bottom">
            <span class="el-dropdown-link">
              <img src="~@/assets/img/avatar.png" :alt="user.username">
              {{user.username }}
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="updatePasswordHandle()">修改密码</el-dropdown-item>
              <el-dropdown-item @click.native="logoutHandle()">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-menu-item>

				<!--<el-menu-item index="4" @click.native="updatePasswordHandle()">
					<span>修改密码</span>
				</el-menu-item>
				
				<el-menu-item index="5" @click.native="logoutHandle()">
					<span>退出登录</span>
				  <el-badge value="hot">
				    <span>退出登录</span>
				  </el-badge> 
				</el-menu-item>-->

      </el-menu>
    </div>
    <!-- 弹窗, 修改密码 -->
    <update-password v-if="updatePassowrdVisible" ref="updatePassowrd"></update-password>
  </nav>
</template>

<script>
  import UpdatePassword from './main-navbar-update-password'
  import { clearLoginInfo } from '@/utils'
  export default {
    data () {
      return {
        updatePassowrdVisible: false,

        programName:'',

        user:{}
      }
    },
    components: {
      UpdatePassword
    },
    computed: {
      navbarLayoutType: {
        get () { return this.$store.state.common.navbarLayoutType }
      },
      sidebarFold: {
        get () { return this.$store.state.common.sidebarFold },
        set (val) { this.$store.commit('common/updateSidebarFold', val) }
      },
      mainTabs: {
        get () { return this.$store.state.common.mainTabs },
        set (val) { this.$store.commit('common/updateMainTabs', val) }
      }
    },
    mounted(){
      this.getData();
    },
    methods: {
      async getData(){
        var res=await this.apis.getInfo({});
        var data=res.user||{};
        this.user=data;
      },
      // 修改密码
      updatePasswordHandle () {
        this.updatePassowrdVisible = true
        this.$nextTick(() => {
          this.$refs.updatePassowrd.init()
        })
      },
      // 退出
      logoutHandle () {
        this.$confirm(`确定进行[退出]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
					// console.warn('main-navbar.vue重新加入代码')
          // this.$http.get("/logout").then(res => {
          //   if (res.code === 200) {
          //     clearLoginInfo()
          //     this.$router.push({ name: 'login' })
          //   }
          // })


          localStorage.role='';
          clearLoginInfo();
          localStorage.mainTabs_text='';
          this.$store.commit('common/updateMainTabs', [])
          this.$router.push({
            path:'/login'
          });

        }).catch(() => {})
      }
    }
  }
</script>
