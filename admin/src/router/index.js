/**
 * 全站路由配置
 *
 * 建议:
 * 1. 代码中路由统一使用name属性跳转(不使用path属性)
 */
import {constantRoutes} from './constantRoutes';
import Vue from 'vue'
import Router from 'vue-router'
import http from '@/utils/httpRequest'
import {
  isURL
} from '@/utils/validate'
import {
  clearLoginInfo
} from '@/utils'
import store from '@/store'
Vue.use(Router)


// 开发环境不使用懒加载, 因为懒加载页面太多的话会造成webpack热更新太慢, 所以只有生产环境使用懒加载
const _import = require('./import-' + process.env.NODE_ENV)

// 全局路由(无需嵌套上左右整体布局)
const globalRoutes = [{
    path: '/404',
    component: _import('common/404'),
    name: '404',
    meta: {
      title: '404未找到'
    }
  },
  {
    path: '/login',
    component: _import('common/login'),
    name: 'login',
    meta: {
      title: '登录'
    }
  },
]

// 主入口路由(需嵌套上左右整体布局)
const mainRoutes = {
  path: '/',
  component: _import('main'),
  name: 'main',
  redirect: {
    name: 'login'
  },
  meta: {
    title: '主入口整体布局'
  },
  children: [
    // 通过meta对象设置路由展示方式
    // 1. isTab: 是否通过tab展示内容, true: 是, false: 否
    // 2. iframeUrl: 是否通过iframe嵌套展示内容, '以http[s]://开头': 是, '': 否
    // 提示: 如需要通过iframe嵌套展示内容, 但不通过tab打开, 请自行创建组件使用iframe处理!
    {
      path: '/home',
      component: _import('common/home'),
      name: 'home',
      meta: {
        title: '首页'
      }
    },


  ],

  beforeEnter(to, from, next) {
    let token = Vue.cookie.get('token')
    //TODO
    // if (!token || !/\S/.test(token)) {
    //   clearLoginInfo()
    //   next({ name: 'login' })
    // }
    next()
  }

}

const router = new Router({
  mode: 'hash',
  scrollBehavior: () => ({
    y: 0
  }),
  isAddDynamicMenuRoutes: false, // 是否已经添加动态(菜单)路由
  routes: globalRoutes.concat(mainRoutes)
})



router.beforeEach((to, from, next) => {
  // 添加动态(菜单)路由
  // 1. 已经添加 or 全局路由, 直接访问
  // 2. 获取菜单列表, 添加并保存本地存储

  if (router.options.isAddDynamicMenuRoutes || fnCurrentRouteType(to, globalRoutes) === 'global') {
    next()
  } else {
    // var menuList_=constantRoutes;
    //
    //
    // menuList_.sort((a,b)=>{
    //   return a.sort-b.sort;
    // })
    //
    // for(var i=0;i<menuList_.length;i++){
    //   menuList_[i].menuId=i;
    //   for(var j=0;j<menuList_[i].list.length;j++){
    //     menuList_[i].list[j].menuId=i+'-'+j;
    //     menuList_[i].list[j].parentId=i;
    //   }
    // }
    //
    // fnAddDynamicMenuRoutes(menuList_)
    // router.options.isAddDynamicMenuRoutes = true
    // localStorage.setItem('menuList', JSON.stringify(menuList_ || '[]'))
    // next({ ...to, replace: true })
    //
    // return

    if (localStorage.role) {

      var btns = [];
      var menuList=[];
	  window.btns_arr=JSON.parse(localStorage.getItem('btns_' + localStorage.role) || '[]')

      http({
        url: http.adornUrl('/sys/menu/nav'),
        method: 'get',
      }).then((data) => {
        console.log(data);
        if (data && data.code === 0) {
          var menuList_ = data.menuList || [];
          // return
          for (var i = 0; i < menuList_.length; i++) {
            var menu={
              'menuId': i,
              'parentId': 0,
              'parentName': null,
              'name': menuList_[i].name,
              'url': menuList_[i].list && menuList_[i].list.length > 0 ? '' : menuList_[i].url.replace(/\.html$/ig, '').replace(/^modules\//ig, ''),
              'perms': null,
              'type': menuList_[i].list && menuList_[i].list.length > 0 ? 0 : 1,
              'icon': menuList_[i].icon || 'street',
              'orderNum': 0,
              'open': null,
              'sort':2,
              'list':menuList_[i].list && menuList_[i].list.length > 0 ? [] : null
            }

			if(menuList_[i].list && menuList_[i].list.length > 0){
            for(var j=0;j<menuList_[i].list.length;j++){
              //if(!menuList_[i].list[j].hidden){
                var children={
                  'menuId': i+'_'+j,
                  'parentId': i,
                  'parentName': null,
                  'name': menuList_[i].list[j].name,
                  'url': menuList_[i].list[j].url.replace(/\.html$/ig, '').replace(/^modules\//ig, ''),
                  'perms': null,
                  'type': 1,
                  'icon': menuList_[i].list[j].icon || 'street',
                  'orderNum': 1,
                  'open': null,
                  'list': null,
                }
                menu['list'].push(children);
              //}
            }
			}


            menuList.push(menu);

            // menuList_[i].menuId = i;
            // if (!menuList_[i].list) {
            //   menuList_[i].list = [];
            // }
            // for (var j = 0; j < menuList_[i].list.length; j++) {
            //   menuList_[i].list[j].menuId = i + '-' + j;
            //   menuList_[i].list[j].parentId = i;
            //
            //   menuList_[i].list[j].children_list = menuList_[i].list[j].list;
            //   menuList_[i].list[j].list = [];
            //
            //   for (var k = 0; k < menuList_[i].list[j].children_list.length; k++) {
            //     btns.push(menuList_[i].list[j].children_list[k].buttonCode)
            //   }
            // }
          }

		  http({
				url: http.adornUrl('/sys/menu/myList'),
				method: 'get',
			  }).then((data) => {
			  		console.log(data)
					data.menuList.map(res => {
						if(res.type == 2)btns.push(res.perms.toLowerCase())
					})
					window.btns_arr = btns;
			  		localStorage.setItem('btns_' + localStorage.role, JSON.stringify(btns || '[]'))
					if (window.btns_arr_call) {
						window.btns_arr_call(btns);
					}
		  	  });

          // return
          

          fnAddDynamicMenuRoutes(menuList)
          router.options.isAddDynamicMenuRoutes = true
          localStorage.setItem('menuList', JSON.stringify(menuList || '[]'))
          // localStorage.setItem('permissions', JSON.stringify(data.permissions || '[]'))

          var fag = false;
          var path_list = [];
          if (to.path == '/') {
            menuList.map(res => {
              // console.log(res);
			  res.list.map(res2 => {
			    var url = res2.url.replace(/\//g, '-');
			    path_list.push('/' + url);
			  });
            });
          } else {
            fag = true;
          }

          if (fag) {
            next({
              ...to,
              replace: true
            })
          } else {
            next({
              path: path_list[0]
            })
          }

        } else {
          localStorage.setItem('menuList', '[]')
          // localStorage.setItem('permissions', '[]')
          next()
        }
      }).catch((e) => {
        console.log(`%c${e} 请求菜单列表和权限失败，跳转至登录页！！`, 'color:blue')
        router.push({
          name: 'login'
        })
      })
    }else{
		router.push({
          name: 'login'
        })
	}


  }
})

/**
 * 判断当前路由类型, global: 全局路由, main: 主入口路由
 * @param {*} route 当前路由
 */
function fnCurrentRouteType(route, globalRoutes = []) {
  var temp = []
  for (var i = 0; i < globalRoutes.length; i++) {
    if (route.path === globalRoutes[i].path) {
      return 'global'
    } else if (globalRoutes[i].children && globalRoutes[i].children.length >= 1) {
      temp = temp.concat(globalRoutes[i].children)
    }
  }
  return temp.length >= 1 ? fnCurrentRouteType(route, temp) : 'main'
}


/**
 * 添加动态(菜单)路由
 * @param {*} menuList 菜单列表
 * @param {*} routes 递归创建的动态(菜单)路由
 */
function fnAddDynamicMenuRoutes(menuList = [], routes = []) {
  var temp = []
  for (var i = 0; i < menuList.length; i++) {
    if (menuList[i].list && menuList[i].list.length >= 1) {
      temp = temp.concat(menuList[i].list)
    } else if (menuList[i].url && /\S/.test(menuList[i].url)) {
      menuList[i].url = menuList[i].url.replace(/^\//, '')
      var route = {
        path: menuList[i].url.replace(/\//g, '-'),
        component: null,
        name: menuList[i].url.replace(/\//g, '-'),
        meta: {
          menuId: menuList[i].menuId,
          title: menuList[i].name,
          isDynamic: true,
          isTab: true,
          iframeUrl: ''
        }
      }
      // url以http[s]://开头, 通过iframe展示
      if (isURL(menuList[i].url)) {
        route['path'] = `i-${menuList[i].menuId}`
        route['name'] = `i-${menuList[i].menuId}`
        route['meta']['iframeUrl'] = menuList[i].url
      } else {
        try {
          route['component'] = _import(`modules/${menuList[i].url.replace(/\.html$/ig, '').replace(/^modules\//ig, '')}`) || null
        } catch (e) {}
      }
      routes.push(route)
    }
  }
  if (temp.length >= 1) {
    fnAddDynamicMenuRoutes(temp, routes)
  } else {
    mainRoutes.name = 'main-dynamic'
    mainRoutes.children = routes
    router.addRoutes([
      mainRoutes,
      {
        path: '*',
        redirect: {
          name: '404'
        }
      }
    ])

    localStorage.setItem('dynamicMenuRoutes', JSON.stringify(mainRoutes.children || '[]'))
    // console.log('\n')
    // console.log('%c!<-------------------- 动态(菜单)路由 s -------------------->', 'color:blue')
    // console.log(mainRoutes)
    // console.log('%c!<-------------------- 动态(菜单)路由 e -------------------->', 'color:blue')
  }
}

export default router
