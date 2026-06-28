-- =====================================================================
-- VIP 会员权益转让 — 数据库变更脚本（一次性迁移）
-- 依据《VIP会员权益转让_详细技术设计.md》第 2 章 + 附录 C/D
-- 引擎 InnoDB / utf8mb4 / MySQL 8。一次性执行；表已存在请勿重复跑。
-- =====================================================================

-- 1) 转让费用规则（分档，tiers_json 存档位；后台保存时由 service 校验合法性）
CREATE TABLE IF NOT EXISTS vip_fee_rule (
  fee_rule_id   BIGINT        NOT NULL AUTO_INCREMENT COMMENT '转让费用规则ID',
  rule_name     VARCHAR(64)   NOT NULL COMMENT '规则名称',
  tiers_json    VARCHAR(1024) NOT NULL COMMENT '分档JSON 如 [{"fromCount":1,"fee":0},{"fromCount":2,"fee":50},{"fromCount":3,"fee":100}]',
  status        TINYINT       NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
  created_date  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (fee_rule_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='VIP转让费用规则(分档)';

-- 2) VIP 权益卡商品（可单独购买、可上下架；动态定价字段；sold_count 系统维护、后台只读）
CREATE TABLE IF NOT EXISTS vip_benefit_card (
  vip_card_id    BIGINT        NOT NULL AUTO_INCREMENT COMMENT '权益卡商品ID',
  card_name      VARCHAR(64)   NOT NULL COMMENT '权益卡名称',
  benefit_desc   VARCHAR(1024) DEFAULT NULL COMMENT '权益内容描述',
  price          DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '基础售价(购买价)',
  validity_days  INT           NOT NULL DEFAULT 365 COMMENT '有效天数',
  store_addr_ids VARCHAR(512)  DEFAULT NULL COMMENT '适用门店ID(逗号分隔)',
  fee_rule_id    BIGINT        DEFAULT NULL COMMENT '关联转让费用规则',
  show_buy_count TINYINT       NOT NULL DEFAULT 1 COMMENT '是否展示实时购买人数 1是0否',
  base_buy_count INT           NOT NULL DEFAULT 0 COMMENT '起涨基数:已购<=此值不涨价',
  step_num       INT           NOT NULL DEFAULT 0 COMMENT '每多少人涨一档(0=不动态涨价)',
  step_add_price DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '每档加价',
  price_cap      DECIMAL(10,2) DEFAULT NULL COMMENT '封顶价(NULL=不封顶)',
  sold_count     INT           NOT NULL DEFAULT 0 COMMENT '真实累计购买人数(系统维护,后台只读)',
  status         TINYINT       NOT NULL DEFAULT 1 COMMENT '1上架 2下架',
  created_date   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (vip_card_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='VIP权益卡商品';

-- 3) 用户持有的权益卡实例（被转让的对象）。source_order_no 唯一，防同单激活两次(附录C.2)
CREATE TABLE IF NOT EXISTS vip_benefit (
  vip_benefit_id BIGINT        NOT NULL AUTO_INCREMENT,
  user_id        BIGINT        NOT NULL COMMENT '当前持有人',
  origin_user_id BIGINT        NOT NULL COMMENT '原始购买人(留痕,永不变)',
  vip_card_id    BIGINT        NOT NULL COMMENT '来源权益卡商品',
  source_order_no VARCHAR(48)  DEFAULT NULL COMMENT '购买订单号',
  store_id       BIGINT        DEFAULT NULL COMMENT '购买/归属门店',
  store_addr_id  BIGINT        DEFAULT NULL,
  origin_price   DECIMAL(10,2) DEFAULT NULL COMMENT '购买时售价(留痕,不参与转让费用计算)',
  start_time     DATETIME      DEFAULT NULL COMMENT '生效',
  expire_time    DATETIME      DEFAULT NULL COMMENT '到期',
  status         TINYINT       NOT NULL DEFAULT 9 COMMENT '9待支付 0正常 1已转出失效 2已冻结 3已过期',
  transfer_count INT           NOT NULL DEFAULT 0 COMMENT '已被转让次数',
  transferable   TINYINT       NOT NULL DEFAULT 1 COMMENT '是否可转 1是0否',
  created_date   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (vip_benefit_id),
  UNIQUE KEY uk_source_order (source_order_no),
  KEY idx_user (user_id),
  KEY idx_card (vip_card_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户持有的权益卡实例(被转让对象)';

-- 4) 权益转让申请/记录（核心状态机）。service_fee_order_no 唯一，回调幂等
CREATE TABLE IF NOT EXISTS vip_benefit_transfer (
  transfer_id          BIGINT        NOT NULL AUTO_INCREMENT,
  vip_benefit_id       BIGINT        NOT NULL,
  from_user_id         BIGINT        NOT NULL COMMENT '转让人',
  to_user_id           BIGINT        NOT NULL COMMENT '受让人',
  from_store_id        BIGINT        DEFAULT NULL,
  to_store_id          BIGINT        DEFAULT NULL,
  service_fee          DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '转让费用(转让人付)',
  service_fee_order_no VARCHAR(48)   DEFAULT NULL COMMENT '转让费用微信支付单号',
  transaction_number   VARCHAR(64)   DEFAULT NULL COMMENT '微信交易号',
  status               TINYINT       NOT NULL COMMENT '10待付费 20待审核 31已驳回 40待受让人确认 51已拒绝 52已超时 60已撤回 70已生效',
  audit_user_id        BIGINT        DEFAULT NULL,
  audit_time           DATETIME      DEFAULT NULL,
  audit_remark         VARCHAR(512)  DEFAULT NULL,
  confirm_deadline     DATETIME      DEFAULT NULL COMMENT '受让人确认截止时间',
  refund_status        TINYINT       NOT NULL DEFAULT 0 COMMENT '0未退 1已退',
  created_date         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  effect_time          DATETIME      DEFAULT NULL COMMENT '生效时间',
  PRIMARY KEY (transfer_id),
  UNIQUE KEY uk_fee_order (service_fee_order_no),
  KEY idx_benefit (vip_benefit_id),
  KEY idx_from (from_user_id),
  KEY idx_to (to_user_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权益转让申请/记录(状态机)';

-- 5) 停卡记录。uk_card_month 在库层挡住"同卡每月第2次"(附录D.2)
CREATE TABLE IF NOT EXISTS card_pause_record (
  pause_id      BIGINT   NOT NULL AUTO_INCREMENT,
  user_id       BIGINT   NOT NULL,
  card_order_id BIGINT   NOT NULL COMMENT '被停的卡(card_order)',
  pause_month   CHAR(7)  DEFAULT NULL COMMENT '所属自然月 yyyy-MM(限制每月1次/全年12次)',
  pause_days    INT      DEFAULT NULL COMMENT '实际停卡天数(恢复时回填,用于有效期顺延)',
  start_time    DATETIME NOT NULL,
  end_time      DATETIME DEFAULT NULL COMMENT '恢复时间(NULL=停卡中)',
  status        TINYINT  NOT NULL DEFAULT 0 COMMENT '0停卡中 1已恢复 2已取消',
  created_date  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (pause_id),
  UNIQUE KEY uk_card_month (card_order_id, pause_month),
  KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='停卡记录';

-- 6) 会员黑名单
CREATE TABLE IF NOT EXISTS member_blacklist (
  id           BIGINT       NOT NULL AUTO_INCREMENT,
  user_id      BIGINT       NOT NULL,
  reason       VARCHAR(512) DEFAULT NULL,
  operator     VARCHAR(64)  DEFAULT NULL COMMENT '操作管理员',
  status       TINYINT      NOT NULL DEFAULT 1 COMMENT '1生效 0已解除',
  created_date DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员黑名单';
