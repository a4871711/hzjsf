-- 权益会员后台管理页(admin: modules/sys/vipMember):
-- 1) vip_benefit 加后台备注列;2) 「会员管理」下插「权益会员」菜单(门店会员之后, order_num=1)
-- status 新增终态 4=已注销(后台注销操作专用, api 侧有效性判定为 status=0 故天然排除)

-- 幂等保护:本脚本在部分环境(如开发库)已用手动 SQL 提前生效,列可能已存在;
-- Flyway 首次针对该库执行迁移时若直接 ADD COLUMN 会报 Duplicate column name 导致启动失败
SET @col_exists = (SELECT COUNT(*) FROM information_schema.columns
                    WHERE table_schema = DATABASE() AND table_name = 'vip_benefit' AND column_name = 'remark');
SET @ddl = IF(@col_exists = 0,
    'ALTER TABLE vip_benefit ADD COLUMN remark VARCHAR(255) DEFAULT NULL COMMENT ''后台备注(权益会员页维护)''',
    'SELECT 1');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '权益会员', 'modules/sys/vipMember.html',
       'sys:vipMember:list,sys:vipMember:update',
       1, 'fa fa-star-o', 1
FROM (SELECT menu_id FROM sys_menu WHERE name = '会员管理' AND parent_id = 0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url = 'modules/sys/vipMember.html');
