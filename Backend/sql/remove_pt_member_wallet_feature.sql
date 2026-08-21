-- 下线“会员储值”后台菜单。
-- 仅删除菜单及角色授权，保留 pt_member_wallet / pt_member_wallet_flow 历史资金数据。
-- 执行后需退出后台并重新登录，以刷新动态菜单。

START TRANSACTION;

DELETE role_menu
FROM sys_role_menu role_menu
INNER JOIN sys_menu menu ON menu.menu_id = role_menu.menu_id
WHERE menu.url = 'modules/sys/wallet.html';

DELETE FROM sys_menu
WHERE url = 'modules/sys/wallet.html';

COMMIT;
