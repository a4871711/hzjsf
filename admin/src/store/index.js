import Vue from 'vue'
import Vuex from 'vuex'
import cloneDeep from 'lodash/cloneDeep'
import common from './modules/common'
import user from './modules/user'

Vue.use(Vuex)

export default new Vuex.Store({
  state:{
    role:null,
    buttons:{
      1:true
    }
  },
  modules: {
    common,
    user
  },
  mutations: {
    // 重置vuex本地储存状态
    resetStore (state) {
      Object.keys(state).forEach((key) => {
        state[key] = cloneDeep(window.SITE_CONFIG['storeState'][key])
      })
    },
    setRole(state,role){
      state.role=role;
    },
    setButtons(state,arr){
      state.buttons=arr;
    }
  },
  strict: process.env.NODE_ENV !== 'production'
})
