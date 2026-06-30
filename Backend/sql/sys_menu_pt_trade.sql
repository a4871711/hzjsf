-- =====================================================================
-- 私教 · 交易域后台菜单(购买/权益/预约)
-- 依据《失历健身私教管理后台_详细实现文档》总则 §0.5 菜单与按钮权限总表
-- 仿 sys_menu_vip.sql：父目录 NOT EXISTS 只建一次；菜单按 url 防重；perms 平铺该页全部按钮权限
-- 铁律：① url 必须指向真实 vue 路径(modules/sys/xxx.html)否则前端动态路由不注册
--       ② perms 串须与各 Controller @RequiresPermissions 逐字一致(注意大小写)
--       ③ 超管 user_id=1 自动放行；非超管需在 sys_role_menu 另行授权
--       ④ 执行后清 localStorage 重新登录，前端动态路由方才注册
-- 本脚本可重复执行(NOT EXISTS 防重)。请在开发库执行，勿用于生产未评审。
-- =====================================================================

-- 1) 顶级目录「私教管理」(type=0)
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT 0, '私教管理', NULL, NULL, 0, 'fa fa-user-circle-o', 20 FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.name='私教管理' AND m.parent_id=0);

-- 2) 「购买记录」菜单 → 前端 views/modules/sys/purchase.vue；perms 平铺 3 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '购买记录', 'modules/sys/purchase.html',
       'sys:privateOrder:list,sys:privateOrder:info,sys:privateOrder:refund',
       1, 'fa fa-list-alt', 8
FROM (SELECT menu_id FROM sys_menu WHERE name='私教管理' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/purchase.html');

-- 3) 「会员私教权益」菜单 → 前端 views/modules/sys/memberBenefit.vue；perms 平铺 2 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '会员私教权益', 'modules/sys/memberBenefit.html',
       'sys:memberBenefit:list,sys:memberBenefit:info',
       1, 'fa fa-id-badge', 9
FROM (SELECT menu_id FROM sys_menu WHERE name='私教管理' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/memberBenefit.html');

-- 4) 「预约记录」菜单 → 前端 views/modules/sys/appointment.vue；perms 平铺 5 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '预约记录', 'modules/sys/appointment.html',
       'sys:privateAppointment:list,sys:privateAppointment:info,sys:privateAppointment:finish,sys:privateAppointment:cancel,sys:privateAppointment:coachBook',
       1, 'fa fa-calendar-check-o', 10
FROM (SELECT menu_id FROM sys_menu WHERE name='私教管理' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/appointment.html');

