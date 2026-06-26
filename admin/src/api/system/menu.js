import request from '@/utils/request'

// 查询菜单列表
export function listMenu(query) {
  return request({
    url: '/sys/menu/list',
    method: 'get',
    params: query
  })
}

// 查询菜单详细
export function getMenu(menuId) {
  return request({
    url: '/sys/menu/info/' + menuId,
    method: 'get'
  })
}

// 查询菜单下拉树结构
export function treeselect(data) {
  return request({
    url: '/sys/menu/treeselect',
    method: 'get',
    params: data
  })
}

// 根据角色ID查询菜单下拉树结构
export function roleMenuTreeselect(roleId) {
  return request({
    url: '/sys/menu/roleMenuTreeselect/' + roleId,
    method: 'get'
  })
}

// 新增菜单
export function addMenu(data) {
  return request({
    url: '/sys/menu/save',
    method: 'post',
	contentType: 'json',
    data: data
  })
}

// 修改菜单
export function updateMenu(data) {
  return request({
    url: '/sys/menu/update',
    method: 'post',
	contentType: 'json',
    data: data
  })
}

// 删除菜单
export function delMenu(menuId) {
  return request({
    url: '/sys/menu/delete',
    method: 'get',
	contentType: 'form',
    params: {menuId: menuId}
  })
}
