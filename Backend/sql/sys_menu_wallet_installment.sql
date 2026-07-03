-- =====================================================================
-- 私教 · 资金域后台菜单(会员储值/分期管理)
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

-- 2) 「会员储值」菜单 → 前端 views/modules/sys/wallet.vue；perms 平铺 6 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '会员储值', 'modules/sys/wallet.html',
       'sys:wallet:list,sys:wallet:info,sys:wallet:recharge,sys:wallet:freeze,sys:wallet:unfreeze,sys:wallet:flowlist',
       1, 'fa fa-credit-card', 11
FROM (SELECT menu_id FROM sys_menu WHERE name='私教管理' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/wallet.html');

-- 3) 「分期管理」菜单 → 前端 views/modules/sys/installment.vue；perms 平铺 3 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '分期管理', 'modules/sys/installment.html',
       'sys:installment:list,sys:installment:info,sys:installment:remind',
       1, 'fa fa-calendar-minus-o', 12
FROM (SELECT menu_id FROM sys_menu WHERE name='私教管理' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/installment.html');

