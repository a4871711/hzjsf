-- =====================================================================
-- 私教 · 运营域后台菜单(评价/续费预警/团课转私教/跨店/异常/报表)
-- 依据《失历健身私教管理后台_详细实现文档》总则 §0.5 菜单与按钮权限总表
-- 仿 sys_menu_vip.sql：父目录 NOT EXISTS 只建一次；菜单按 url 防重；perms 平铺该页全部按钮权限
-- 铁律：① url 必须指向真实 vue 路径(modules/sys/xxx.html)否则前端动态路由不注册
--       ② perms 串须与各 Controller @RequiresPermissions 逐字一致(注意大小写)
--       ③ 超管 user_id=1 自动放行；非超管需在 sys_role_menu 另行授权
--       ④ 执行后清 localStorage 重新登录，前端动态路由方才注册
-- 本脚本可重复执行(NOT EXISTS 防重)。请在开发库执行，勿用于生产未评审。
-- =====================================================================

-- 1) 顶级目录「私教运营」(type=0)
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT 0, '私教运营', NULL, NULL, 0, 'fa fa-line-chart', 40 FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.name='私教运营' AND m.parent_id=0);

-- 2) 「评价管理」菜单 → 前端 views/modules/sys/coachComment.vue；perms 平铺 5 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '评价管理', 'modules/sys/coachComment.html',
       'sys:coachComment:list,sys:coachComment:info,sys:coachComment:reply,sys:coachComment:handle,sys:coachComment:delete',
       1, 'fa fa-comments', 1
FROM (SELECT menu_id FROM sys_menu WHERE name='私教运营' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/coachComment.html');

-- 3) 「续费预警」菜单 → 前端 views/modules/sys/renewalWarning.vue；perms 平铺 6 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '续费预警', 'modules/sys/renewalWarning.html',
       'sys:renewalWarning:list,sys:renewalWarning:info,sys:renewalWarning:save,sys:renewalWarning:update,sys:renewalWarning:delete,sys:renewalWarning:follow',
       1, 'fa fa-bell', 2
FROM (SELECT menu_id FROM sys_menu WHERE name='私教运营' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/renewalWarning.html');

-- 4) 「团课转私教」菜单 → 前端 views/modules/sys/groupToPrivate.vue；perms 平铺 8 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '团课转私教', 'modules/sys/groupToPrivate.html',
       'sys:groupToPrivate:list,sys:groupToPrivate:info,sys:groupToPrivate:save,sys:groupToPrivate:update,sys:groupToPrivate:delete,sys:groupToPrivate:sendCoupon,sys:groupToPrivate:follow,sys:groupToPrivate:markConverted',
       1, 'fa fa-exchange', 3
FROM (SELECT menu_id FROM sys_menu WHERE name='私教运营' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/groupToPrivate.html');

-- 5) 「跨店结算规则」菜单 → 前端 views/modules/sys/crossStoreSettle.vue；perms 平铺 2 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '跨店结算规则', 'modules/sys/crossStoreSettle.html',
       'sys:crossStoreSettle:info,sys:crossStoreSettle:save',
       1, 'fa fa-balance-scale', 4
FROM (SELECT menu_id FROM sys_menu WHERE name='私教运营' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/crossStoreSettle.html');

-- 6) 「异常预警中心」菜单 → 前端 views/modules/sys/exceptionWarning.vue；perms 平铺 5 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '异常预警中心', 'modules/sys/exceptionWarning.html',
       'sys:exceptionWarning:list,sys:exceptionWarning:info,sys:exceptionWarning:save,sys:exceptionWarning:update,sys:exceptionWarning:delete',
       1, 'fa fa-exclamation-triangle', 5
FROM (SELECT menu_id FROM sys_menu WHERE name='私教运营' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/exceptionWarning.html');

-- 7) 「私教收入报表」菜单 → 前端 views/modules/sys/privateReport.vue；perms 平铺 1 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '私教收入报表', 'modules/sys/privateReport.html',
       'sys:privateReport:list',
       1, 'fa fa-bar-chart', 6
FROM (SELECT menu_id FROM sys_menu WHERE name='私教运营' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/privateReport.html');

-- 注：原规划的「课时费规则」菜单已按总则 0.11 勘误 #1 并入「教练分成规则」(sys:commission:*)，
--     收入报表成本口径只读引用同一张 pt_coach_fee_rule(扩展版)，此处不单建菜单/权限。
