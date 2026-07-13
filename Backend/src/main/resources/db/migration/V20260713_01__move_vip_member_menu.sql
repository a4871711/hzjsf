-- 将「权益会员」菜单从「会员管理」移动到「VIP权益」分组下(排在末尾, order_num=7)。
-- 用 name/url 定位并采用 UPDATE...JOIN 形式:若「VIP权益」父菜单不存在则匹配 0 行,
-- 不会把 parent_id 误设为 NULL;重复执行结果一致,天然幂等。
-- 菜单 id 各环境自增值不同,故不硬编码 id,与 V20260712_01 建菜单脚本保持同一定位方式。
UPDATE sys_menu m
JOIN (
    SELECT menu_id FROM sys_menu WHERE name = 'VIP权益' AND parent_id = 0 ORDER BY menu_id DESC LIMIT 1
) AS p
SET m.parent_id = p.menu_id,
    m.order_num = 7
WHERE m.url = 'modules/sys/vipMember.html';
