-- 先删除角色授权，避免 sys_role_menu 残留无效 menu_id。
DELETE FROM sys_role_menu
WHERE menu_id IN (
    SELECT menu_id
    FROM sys_menu
    WHERE url = 'modules/sys/groupToPrivate.html'
);

-- 删除团课转私教菜单；父级“私教运营”及其他子菜单保留。
DELETE FROM sys_menu
WHERE url = 'modules/sys/groupToPrivate.html';

-- 该功能已整体下线，按业务依赖顺序删除跟进、名单和规则数据。
DROP TABLE IF EXISTS pt_group_to_private_follow;
DROP TABLE IF EXISTS pt_group_to_private_lead;
DROP TABLE IF EXISTS pt_group_to_private_rule;
