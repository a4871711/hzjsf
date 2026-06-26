<template>
  <el-submenu
    v-if="menu.list && menu.list.length >= 1"
    :index="menu.menuId + ''"
    :popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'">
    <template slot="title">
	  <i v-if="menu.icon && menu.icon.indexOf('fa')!=-1" :class="menu.icon"></i>
      <icon-svg v-if="menu.icon.indexOf('fa')==-1" :name="menu.icon || 'menu'" class="site-sidebar__menu-icon"></icon-svg>
      <span>{{ menu.name }}</span>
    </template>
    <sub-menu
      v-for="item in menu.list"
      :key="item.menuId"
      :menu="item"
      :dynamicMenuRoutes="dynamicMenuRoutes">
    </sub-menu>
  </el-submenu>
  <el-menu-item v-else-if="hasOneShowingChild(menu)" :index="menu.menuId + ''" @click="gotoRouteHandle(menu)">
	<i v-if="menu.icon && menu.icon.indexOf('fa')!=-1" :class="menu.icon"></i>
    <icon-svg v-if="menu.icon.indexOf('fa')==-1" :name="menu.icon || 'system'" class="site-sidebar__menu-icon"></icon-svg>
    <span>{{ menu.name }}</span>
  </el-menu-item>
</template>

<script>
  import SubMenu from './main-sidebar-sub-menu'
  export default {
    name: 'sub-menu',
    props: {
      menu: {
        type: Object,
        required: true
      },
      dynamicMenuRoutes: {
        type: Array,
        required: true
      }
    },
    components: {
      SubMenu
    },
    computed: {
      sidebarLayoutSkin: {
        get () { return this.$store.state.common.sidebarLayoutSkin }
      }
    },
		data(){
			return {
				onlyOneChild:null
			}
		},
		created(){
		},
    methods: {
			hasOneShowingChild(children){
				if(children.hidden){
					return false
				}else{
					return true
				}
			},
      // 通过menuId与动态(菜单)路由进行匹配跳转至指定路由
      gotoRouteHandle (menu) {
        var route = this.dynamicMenuRoutes.filter(item => item.meta.menuId === menu.menuId)
        if (route.length >= 1) {
          this.$router.push({ name: route[0].name })
        }
      }
    }
  }
</script>
