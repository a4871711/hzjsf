// 本工程源码在项目根目录(而非标准的 src/),以保留 HBuilderX 兼容。
// 源码输入目录通过 package.json scripts 里的 UNI_INPUT_DIR=./ 指定——必须在进程启动时
// 就设好(@vue/cli-service 构造时即加载 vue-cli-plugin-uni 并读取该变量),vue.config.js
// 加载时机太晚,故此处不再设置。
const TransformPages = require('uni-read-pages')
const {webpack} = new TransformPages()

module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'https://fmapi-test.bgzyedu.com',
      },
	  // '/': {
	  //   target: 'https://fmxp-test.bgzyedu.com',
	  // },

    },
  },
  configureWebpack: {
    plugins: [
      new webpack.DefinePlugin({
        ROUTES: webpack.DefinePlugin.runtimeValue(() => {
          const tfPages = new TransformPages({
            includes: ['path', 'name', 'meta','aliasPath']
          });
          return JSON.stringify(tfPages.routes)
        }, true )
      })
    ]
  }
}
