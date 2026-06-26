import Vue from 'vue'

/**
 * require.context参数：
 * 1.要搜索的文件夹目录
 * 2.是否还应该搜索它的子目录
 * 3.以及一个匹配文件的正则表达式
 */
const componentsContext = require.context('./', true, /index\.vue$/);

componentsContext.keys().forEach(component => {
  const componentConfig = componentsContext(component);
  /**
   * 兼容 import export 和 require module.export 两种规范
   */
  const ctrl = componentConfig.default || componentConfig;
  Vue.component(`r-${ctrl.name.substr(0,1).toLocaleLowerCase()+ctrl.name.substr(1)}`, ctrl)
});
