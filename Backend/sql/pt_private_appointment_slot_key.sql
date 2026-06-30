-- =====================================================================
-- 私教 · 预约一对一占位唯一键(可选·路线B)
-- 容量并发护栏主路线为应用层行锁(见交易域§5.2);本脚本为一对一加DB唯一键兜底,按需执行
-- 依据《私教需求文档》第21节 DDL + 《失历健身私教管理后台_详细实现文档》总则0.4/各域§2
-- 字符集 utf8mb4；标 -- 【补充】 的列/索引为需求DDL未给、实现所需(评审项)
-- 可在开发库执行(需确认非生产)
-- =====================================================================

-- 路线B(可选加固,仅一对一防双约):为 pt_private_appointment 增占位键+唯一键
-- 仅服务类型=1(一对一)写入 slot_key= coach_id:appointment_date:start_time; 一对多不写(NULL,MySQL唯一键允许多个NULL)
ALTER TABLE `pt_private_appointment`
  ADD COLUMN `slot_key` VARCHAR(64) DEFAULT NULL COMMENT '一对一占位键 coachId:date:startTime,仅一对一写入' AFTER `end_time`,
  ADD UNIQUE KEY `uk_pt_appt_slot` (`slot_key`);
