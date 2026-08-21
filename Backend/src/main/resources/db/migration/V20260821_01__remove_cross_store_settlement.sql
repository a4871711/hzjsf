-- 删除跨店结算菜单的角色授权，避免 sys_role_menu 残留无效 menu_id。
DELETE FROM sys_role_menu
WHERE menu_id IN (
    SELECT menu_id
    FROM sys_menu
    WHERE url = 'modules/sys/crossStoreSettle.html'
);

-- 删除跨店结算菜单本身；父级“私教运营”及其他子菜单保留。
DELETE FROM sys_menu
WHERE url = 'modules/sys/crossStoreSettle.html';

-- 删除已废弃的全局跨店结算规则表。
DROP TABLE IF EXISTS pt_cross_store_settlement_rule;
