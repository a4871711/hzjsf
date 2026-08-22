-- 购买记录增加后台手工建单与永久删除权限。仅更新菜单权限数据，不改表结构。
-- CONCAT_WS 会忽略 NULL；FIND_IN_SET 防止脚本被手工执行后再由 Flyway 重复追加。
UPDATE sys_menu
SET perms = CONCAT_WS(',',
    NULLIF(TRIM(BOTH ',' FROM perms), ''),
    IF(IFNULL(FIND_IN_SET('sys:privateOrder:save', perms), 0) = 0,
       'sys:privateOrder:save', NULL),
    IF(IFNULL(FIND_IN_SET('sys:privateOrder:delete', perms), 0) = 0,
       'sys:privateOrder:delete', NULL)
)
WHERE url = 'modules/sys/purchase.html';
