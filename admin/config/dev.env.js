'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  OPEN_PROXY: true, // 是否开启代理, 重置后需重启vue-cli
  VUE_APP_URL: '"https://shilijsf.shilisports.com"',
  VUE_APP_IMG: '"https://shilijsf.shilisports.com"',
})
