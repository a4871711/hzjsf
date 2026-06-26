-- 健身卡：新人专享价（APP 新人购卡使用，与老用户单价 cardPrice 区分）
ALTER TABLE `fit_card`
  ADD COLUMN `newUserPrice` DECIMAL(10,2) NULL DEFAULT NULL COMMENT '新人专享价' AFTER `cardPrice`;

-- 可选：将历史「单价」复制为新人价，便于平滑上线（按需执行）
-- UPDATE `fit_card` SET `newUserPrice` = `cardPrice` WHERE `newUserPrice` IS NULL;
