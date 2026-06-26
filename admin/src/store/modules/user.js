export default {
  namespaced: true,
  state: {
    id: 0,
    name: '超级管理员'
  },
  mutations: {
    updateId (state, id) {
      state.id = id
    },
    updateName (state, name) {
      state.name = name
    }
  },
	actions:{
		setName({ commit },data){
			commit('updateName',data)
		},
	}
}
