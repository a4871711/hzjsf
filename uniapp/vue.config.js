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
  chainWebpack(config) {
    config.module.rule('js').exclude.clear().add((filePath) => {
      const normalizedPath = filePath.replace(/\\/g, '/')
      if (!normalizedPath.includes('/node_modules/')) {
        return false
      }

      return !/\/node_modules\/(@dcloudio\/(uni-|vue-cli-plugin-uni\/packages\/(uni-app|uni-cloud|uni-stat|uni-push))|uview-ui\/)/.test(normalizedPath)
    })
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
