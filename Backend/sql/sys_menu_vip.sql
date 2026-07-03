-- =====================================================================
-- VIP 权益后台菜单 + 按钮权限(对应 admin 前端 views/modules/sys/*.vue)
-- 依据《VIP会员权益转让_Web后台界面设计文档.md》/《详细技术设计.md》第 10 章
-- 说明:超级管理员(sys_user.user_id=1)自动可见全部菜单、放行全部 @RequiresPermissions,
--      无需 sys_role_menu 关联;非超管角色需另在 sys_role_menu 给对应 menu_id 授权。
-- 本脚本可重复执行前先自查是否已插入,避免菜单重复。
-- =====================================================================

-- 1) 「VIP权益」顶级目录(type=0,无 url/perms)。NOT EXISTS 保证重复执行不插重复目录
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT 0, 'VIP权益', NULL, NULL, 0, 'fa fa-star', 10 FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.name='VIP权益' AND m.parent_id=0);

-- 2) 「转让费用规则」菜单(type=1,挂在上面的「VIP权益」目录下)
--    url 形如 modules/sys/vipFeeRule.html → 前端动态路由加载 views/modules/sys/vipFeeRule.vue
--    perms 逗号分隔列出该页 5 个按钮权限(沿用本项目菜单记录直接平铺 perms 的写法)
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '转让费用规则', 'modules/sys/vipFeeRule.html',
       'sys:vipFeeRule:list,sys:vipFeeRule:info,sys:vipFeeRule:save,sys:vipFeeRule:update,sys:vipFeeRule:delete',
       1, 'fa fa-money', 0
FROM (SELECT menu_id FROM sys_menu WHERE name='VIP权益' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/vipFeeRule.html');

-- 3) 「权益卡管理」菜单(type=1,挂在「VIP权益」目录下)
--    url modules/sys/vipCard.html → 前端动态路由加载 views/modules/sys/vipCard.vue
--    perms 平铺该页 5 个按钮权限(onCard/offCard 复用 sys:vipcard:update,无需单列)
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '权益卡管理', 'modules/sys/vipCard.html',
       'sys:vipcard:list,sys:vipcard:info,sys:vipcard:save,sys:vipcard:update,sys:vipcard:delete',
       1, 'fa fa-id-card', 1
FROM (SELECT menu_id FROM sys_menu WHERE name='VIP权益' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/vipCard.html');

-- 4) 「转让审核」菜单(type=1,挂「VIP权益」目录下)→ views/modules/sys/vipTransfer.vue
--    perms 平铺该页 2 个按钮权限(list 列表 / audit 审核)
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '转让审核', 'modules/sys/vipTransfer.html',
       'sys:vipTransfer:list,sys:vipTransfer:audit',
       1, 'fa fa-exchange', 2
FROM (SELECT menu_id FROM sys_menu WHERE name='VIP权益' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/vipTransfer.html');

-- 5) 「停卡记录」菜单(type=1,挂「VIP权益」目录下)→ views/modules/sys/cardPause.vue
--    后台只读,仅 list 一个按钮权限
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '停卡记录', 'modules/sys/cardPause.html',
       'sys:cardPause:list',
       1, 'fa fa-pause-circle', 3
FROM (SELECT menu_id FROM sys_menu WHERE name='VIP权益' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/cardPause.html');

-- 6) 「会员黑名单」菜单(type=1,挂「VIP权益」目录下)→ views/modules/sys/memberBlacklist.vue
--    perms 平铺该页 3 个按钮权限(list 列表 / save 拉黑 / remove 解除)
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '会员黑名单', 'modules/sys/memberBlacklist.html',
       'sys:memberBlacklist:list,sys:memberBlacklist:save,sys:memberBlacklist:remove',
       1, 'fa fa-ban', 4
FROM (SELECT menu_id FROM sys_menu WHERE name='VIP权益' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/memberBlacklist.html');

-- 7) 「权益卡购买记录」菜单(type=1,挂「VIP权益」目录下)→ views/modules/sys/vipCardOrder.vue
--    后台只读,仅 list 一个按钮权限;数据源 vip_benefit(一行=一张被买下的权益卡实例)
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '权益卡购买记录', 'modules/sys/vipCardOrder.html',
       'sys:vipCardOrder:list',
       1, 'fa fa-shopping-cart', 5
FROM (SELECT menu_id FROM sys_menu WHERE name='VIP权益' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/vipCardOrder.html');
