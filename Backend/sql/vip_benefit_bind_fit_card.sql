-- =====================================================================
-- VIP 权益卡「可绑定会员卡」— 数据库变更脚本
-- ① fit_card 新增"卡种性质"字段，标记普通卡/权益卡性质(全新维度，与 cardType 月卡/季卡/半年卡/年卡/次卡无关)
-- ② vip_benefit_card 新增"可绑定的会员卡"配置(候选须为①中权益卡性质的记录)
--
-- 部署顺序：本脚本必须先于新代码部署执行。FitCardDao.xml / VipBenefitCardDao.xml
-- 的 queryList/save/update 均为手写显式列清单(非 SELECT *)，列不存在时会直接报
-- SQLSyntaxErrorException: Unknown column 'xxx' in 'field list'，导致列表接口整体报错。
-- =====================================================================

-- 1) fit_card：卡种性质。默认0，不改变任何存量数据的现有行为；
--    存量数据要成为"权益卡性质"需管理员在 fitcard.vue 编辑页手动勾选。
ALTER TABLE `fit_card`
  ADD COLUMN `cardNature` TINYINT NOT NULL DEFAULT 0
  COMMENT '卡种性质:0普通卡 1权益卡性质(可作为VIP权益卡"可绑定会员卡"候选)'
  AFTER `cardType`;

-- 2) vip_benefit_card：可绑定的会员卡(逗号分隔 fit_card.fitCardId)。
--    为空/NULL = 不限制，任何会员均可购买；向后兼容全部存量权益卡配置。
ALTER TABLE `vip_benefit_card`
  ADD COLUMN `bind_fit_card_ids` VARCHAR(512) DEFAULT NULL
  COMMENT '可绑定的会员卡ID列表(逗号分隔,取自fit_card.fitCardId且须为cardNature=1);为空=不限制'
  AFTER `store_addr_ids`;
