-- =====================================================================
-- 私教 · 营销域后台菜单(优惠券/拼团/秒杀/领券/会员卡权益)
-- 依据《失历健身私教管理后台_详细实现文档》总则 §0.5 菜单与按钮权限总表
-- 仿 sys_menu_vip.sql：父目录 NOT EXISTS 只建一次；菜单按 url 防重；perms 平铺该页全部按钮权限
-- 铁律：① url 必须指向真实 vue 路径(modules/sys/xxx.html)否则前端动态路由不注册
--       ② perms 串须与各 Controller @RequiresPermissions 逐字一致(注意大小写)
--       ③ 超管 user_id=1 自动放行；非超管需在 sys_role_menu 另行授权
--       ④ 执行后清 localStorage 重新登录，前端动态路由方才注册
-- 本脚本可重复执行(NOT EXISTS 防重)。请在开发库执行，勿用于生产未评审。
-- =====================================================================

-- 1) 顶级目录「卡券管理」(type=0)
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT 0, '卡券管理', NULL, NULL, 0, 'fa fa-ticket', 30 FROM dual
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.name='卡券管理' AND m.parent_id=0);

-- 2) 「优惠券」菜单 → 前端 views/modules/sys/marketingCoupon.vue；perms 平铺 6 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '优惠券', 'modules/sys/marketingCoupon.html',
       'sys:mkCoupon:list,sys:mkCoupon:info,sys:mkCoupon:save,sys:mkCoupon:update,sys:mkCoupon:delete,sys:mkCoupon:grant',
       1, 'fa fa-ticket', 1
FROM (SELECT menu_id FROM sys_menu WHERE name='卡券管理' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/marketingCoupon.html');

-- 3) 「拼团」菜单 → 前端 views/modules/sys/marketingGroupBuy.vue；perms 平铺 5 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '拼团', 'modules/sys/marketingGroupBuy.html',
       'sys:mkGroupBuy:list,sys:mkGroupBuy:info,sys:mkGroupBuy:save,sys:mkGroupBuy:update,sys:mkGroupBuy:delete',
       1, 'fa fa-users', 2
FROM (SELECT menu_id FROM sys_menu WHERE name='卡券管理' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/marketingGroupBuy.html');

-- 4) 「限时秒杀」菜单 → 前端 views/modules/sys/marketingFlashSale.vue；perms 平铺 5 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '限时秒杀', 'modules/sys/marketingFlashSale.html',
       'sys:mkFlashSale:list,sys:mkFlashSale:info,sys:mkFlashSale:save,sys:mkFlashSale:update,sys:mkFlashSale:delete',
       1, 'fa fa-bolt', 3
FROM (SELECT menu_id FROM sys_menu WHERE name='卡券管理' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/marketingFlashSale.html');

-- 5) 「会员领券记录」菜单 → 前端 views/modules/sys/marketingMemberCoupon.vue；perms 平铺 2 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '会员领券记录', 'modules/sys/marketingMemberCoupon.html',
       'sys:mkMemberCoupon:list,sys:mkMemberCoupon:info',
       1, 'fa fa-inbox', 4
FROM (SELECT menu_id FROM sys_menu WHERE name='卡券管理' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/marketingMemberCoupon.html');

-- 6) 「会员卡权益」菜单 → 前端 views/modules/sys/cardBenefit.vue；perms 平铺 1 个按钮
INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '会员卡权益', 'modules/sys/cardBenefit.html',
       'sys:cardBenefit:list',
       1, 'fa fa-id-card-o', 5
FROM (SELECT menu_id FROM sys_menu WHERE name='卡券管理' AND parent_id=0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url='modules/sys/cardBenefit.html');

