-- =====================================================================
-- 私教 · 教练域后台菜单(教练/等级/分成/排班)
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

-- 2) 「教练管理」菜单 → 前端 views/modules/sys/coach.vue；perms 平铺 7 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '教练管理', 'modules/sys/coach.html',
       'sys:coach:list,sys:coach:info,sys:coach:save,sys:coach:update,sys:coach:delete,sys:coach:changeStatus,sys:coach:appointments',
       1, 'fa fa-user', 1
FROM (SELECT menu_id FROM sys_menu WHERE name='私教管理' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/coach.html');

-- 3) 「教练等级管理」菜单 → 前端 views/modules/sys/coachLevel.vue；perms 平铺 6 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '教练等级管理', 'modules/sys/coachLevel.html',
       'sys:coachLevel:list,sys:coachLevel:info,sys:coachLevel:save,sys:coachLevel:update,sys:coachLevel:delete,sys:coachLevel:changeStatus',
       1, 'fa fa-star-o', 2
FROM (SELECT menu_id FROM sys_menu WHERE name='私教管理' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/coachLevel.html');

-- 4) 「教练分成规则」菜单 → 前端 views/modules/sys/commission.vue；perms 平铺 6 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '教练分成规则', 'modules/sys/commission.html',
       'sys:commission:list,sys:commission:info,sys:commission:save,sys:commission:update,sys:commission:delete,sys:commission:changeStatus',
       1, 'fa fa-percent', 3
FROM (SELECT menu_id FROM sys_menu WHERE name='私教管理' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/commission.html');

-- 5) 「教练排班」菜单 → 前端 views/modules/sys/schedule.vue；perms 平铺 6 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '教练排班', 'modules/sys/schedule.html',
       'sys:schedule:list,sys:schedule:save,sys:schedule:update,sys:schedule:delete,sys:schedule:changeEnabled,sys:schedule:coachList',
       1, 'fa fa-calendar', 4
FROM (SELECT menu_id FROM sys_menu WHERE name='私教管理' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/schedule.html');

