import {RouterMount,createRouter} from 'uni-simple-router';

//初始化
const router = new createRouter({
  platform: process.env.VUE_APP_PLATFORM, //平台
  routes: [...ROUTES]
});

//全局路由前置守卫
router.beforeEach(async (to, from, next) => {
	// console.log(to);
  if (to.meta.isLogin) {
    await checkLogin();
    console.log('需要登录权限');
    return;
  }else next()
});

/**
 * 验证登录
 */
function checkLogin() {
  const token = uni.getStorageSync('token');
  if (!token) {
    uni.showToast({
      title: '请先登录',
      icon : 'none'
    });

    // TODO: 跳转登录页等处理
    setTimeout(() => {
      uni.navigateTo({
        url: '/pagesA/login/login'
      });
    }, 1500);

    return false;
  }

  return token;
}

export {
  router,
  RouterMount
}
