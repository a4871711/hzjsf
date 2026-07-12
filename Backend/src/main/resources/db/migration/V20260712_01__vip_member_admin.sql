-- 权益会员后台管理页(admin: modules/sys/vipMember):
-- 1) vip_benefit 加后台备注列;2) 「会员管理」下插「权益会员」菜单(门店会员之后, order_num=1)
-- status 新增终态 4=已注销(后台注销操作专用, api 侧有效性判定为 status=0 故天然排除)

ALTER TABLE vip_benefit
    ADD COLUMN remark VARCHAR(255) DEFAULT NULL COMMENT '后台备注(权益会员页维护)';

INSERT INTO sys_menu (parent_id, name, url, perms, type, icon, order_num)
SELECT t.menu_id, '权益会员', 'modules/sys/vipMember.html',
       'sys:vipMember:list,sys:vipMember:update',
       1, 'fa fa-star-o', 1
FROM (SELECT menu_id FROM sys_menu WHERE name = '会员管理' AND parent_id = 0 ORDER BY menu_id DESC LIMIT 1) AS t
WHERE NOT EXISTS (SELECT 1 FROM sys_menu m WHERE m.url = 'modules/sys/vipMember.html');
