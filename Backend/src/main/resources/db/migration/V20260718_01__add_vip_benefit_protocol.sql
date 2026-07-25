-- 协议管理新增「权益会员协议」(type=7)。
-- protocol.pId 自增,各环境值不同,故按 type 定位;NOT EXISTS 保证重复执行幂等。
-- 内容留空,由后台「协议管理」页面编辑填写。
INSERT INTO protocol (type, selected, protocols)
SELECT 7, 0, ''
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM protocol WHERE type = 7);

-- 同步 type 字段注释,补充新类型说明。
ALTER TABLE protocol
MODIFY COLUMN `type` int DEFAULT NULL COMMENT '类型0：用户协议 1：隐私协议 2：矢历连续包月协议 3: 矢历连续会员协议 4矢历连续次卡会员协议 5管理端服务协议 6管理端隐私协议 7权益会员协议';
