/**
 * 请求配置文件
 * @param {string} env
 */
const ENV_BASE_URL = {
  development: 'http://127.0.0.1:8080/renren-security/api',
  production: 'https://shilijsf.shilisports.com/api',
  // development: 'https://fmapi-test.bgzyedu.com/api',
  // production: 'https://fmapi-test.bgzyedu.com/api'
}

const CURRENT_ENV = process.env.NODE_ENV || 'development';

export const BASE_URL = ENV_BASE_URL[CURRENT_ENV] || ENV_BASE_URL.development; //后台根域名
export const API_URL = ENV_BASE_URL[CURRENT_ENV] || ENV_BASE_URL.development; //后台接口域名
export const IMG_URL = ''; //图片域名
