-- Money Flow 数据库初始化脚本
-- 生成时间: 2026-02-04

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希',
  `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `status` TINYINT(1) DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `del_flag` TINYINT(1) DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `username` VARCHAR(50) DEFAULT '' COMMENT '用户账号',
  `ipaddr` VARCHAR(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` VARCHAR(255) DEFAULT '' COMMENT '登录地点',
  `browser` VARCHAR(50) DEFAULT '' COMMENT '浏览器类型',
  `os` VARCHAR(50) DEFAULT '' COMMENT '操作系统',
  `client_type` VARCHAR(20) DEFAULT '' COMMENT '客户端类型（web, android, ios, pc）',
  `device_id` VARCHAR(100) DEFAULT '' COMMENT '设备唯一标识',
  `status` TINYINT(1) DEFAULT 0 COMMENT '登录状态（0成功 1失败）',
  `msg` VARCHAR(255) DEFAULT '' COMMENT '提示消息',
  `login_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统访问记录';

-- ----------------------------
-- Table structure for sys_user_device
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_device`;
CREATE TABLE `sys_user_device` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '设备记录ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `client_type` VARCHAR(20) NOT NULL COMMENT '客户端类型(web, android, ios, pc)',
  `device_id` VARCHAR(100) NOT NULL COMMENT '设备唯一标识',
  `device_name` VARCHAR(100) DEFAULT NULL COMMENT '设备名称',
  `device_token` VARCHAR(255) DEFAULT NULL COMMENT '设备推送Token',
  `last_login_ip` VARCHAR(128) DEFAULT NULL COMMENT '最后登录IP',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `status` TINYINT(1) DEFAULT 0 COMMENT '状态（0正常 1禁用）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '首次绑定时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_device` (`user_id`, `client_type`, `device_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设备信息表';

-- ----------------------------
-- Table structure for bus_account
-- ----------------------------
DROP TABLE IF EXISTS `bus_account`;
CREATE TABLE `bus_account` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '所属用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '账户名称',
  `type` VARCHAR(20) NOT NULL COMMENT '账户类型(cash, card, alipay, wechat, other)',
  `icon` VARCHAR(50) DEFAULT 'Wallet' COMMENT '图标名称',
  `initial_balance` DECIMAL(15, 2) NOT NULL DEFAULT 0.00 COMMENT '初始余额',
  `sort_order` INT DEFAULT 0 COMMENT '排序权重',
  `del_flag` TINYINT(1) DEFAULT 0 COMMENT '删除标志',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产账户表';

-- ----------------------------
-- Table structure for bus_category
-- ----------------------------
DROP TABLE IF EXISTS `bus_category`;
CREATE TABLE `bus_category` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `user_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '用户ID(系统分类为NULL)',
  `parent_id` BIGINT UNSIGNED DEFAULT 0 COMMENT '父分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `type` ENUM('expense', 'income') NOT NULL COMMENT '类型',
  `icon` VARCHAR(50) DEFAULT 'HelpCircle' COMMENT '图标',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `del_flag` TINYINT(1) DEFAULT 0 COMMENT '删除标志',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收支分类表';

-- ----------------------------
-- Table structure for bus_transaction
-- ----------------------------
DROP TABLE IF EXISTS `bus_transaction`;
CREATE TABLE `bus_transaction` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '交易ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `type` ENUM('expense', 'income', 'transfer') NOT NULL COMMENT '类型',
  `amount` DECIMAL(15, 2) NOT NULL COMMENT '本位币金额',
  `currency` VARCHAR(10) DEFAULT 'CNY' COMMENT '原币种',
  `orig_amount` DECIMAL(15, 2) DEFAULT NULL COMMENT '原币种金额',
  `date` DATETIME NOT NULL COMMENT '发生时间',
  `category_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '分类ID',
  `account_id` BIGINT UNSIGNED NOT NULL COMMENT '主账户ID(支出/转出)',
  `target_account_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '目标账户ID(转账专用)',
  `note` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `del_flag` TINYINT(1) DEFAULT 0 COMMENT '删除标志',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`, `date`),
  KEY `idx_category` (`category_id`),
  KEY `idx_account` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易明细表';

-- ----------------------------
-- Table structure for bus_tag
-- ----------------------------
DROP TABLE IF EXISTS `bus_tag`;
CREATE TABLE `bus_tag` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名',
  `del_flag` TINYINT(1) DEFAULT 0 COMMENT '删除标志',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- ----------------------------
-- Table structure for bus_transaction_tag
-- ----------------------------
DROP TABLE IF EXISTS `bus_transaction_tag`;
CREATE TABLE `bus_transaction_tag` (
  `transaction_id` BIGINT UNSIGNED NOT NULL COMMENT '交易ID',
  `tag_id` BIGINT UNSIGNED NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`transaction_id`, `tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易标签关联表';

-- ----------------------------
-- Table structure for bus_budget
-- ----------------------------
DROP TABLE IF EXISTS `bus_budget`;
CREATE TABLE `bus_budget` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '预算ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `month` VARCHAR(7) NOT NULL COMMENT '月份(YYYY-MM)',
  `total_amount` DECIMAL(15, 2) NOT NULL DEFAULT 0.00 COMMENT '总预算金额',
  `del_flag` TINYINT(1) DEFAULT 0 COMMENT '删除标志',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_month` (`user_id`, `month`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='月度预算表';

-- ----------------------------
-- Table structure for bus_budget_item
-- ----------------------------
DROP TABLE IF EXISTS `bus_budget_item`;
CREATE TABLE `bus_budget_item` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `budget_id` BIGINT UNSIGNED NOT NULL COMMENT '主预算ID',
  `category_id` BIGINT UNSIGNED NOT NULL COMMENT '分类ID',
  `amount` DECIMAL(15, 2) NOT NULL COMMENT '分类预算金额',
  `del_flag` TINYINT(1) DEFAULT 0 COMMENT '删除标志',
  PRIMARY KEY (`id`),
  KEY `idx_budget_id` (`budget_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类预算明细表';

-- ----------------------------
-- Table structure for bus_recurring_rule
-- ----------------------------
DROP TABLE IF EXISTS `bus_recurring_rule`;
CREATE TABLE `bus_recurring_rule` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `type` ENUM('expense', 'income') NOT NULL COMMENT '类型',
  `amount` DECIMAL(15, 2) NOT NULL COMMENT '金额',
  `category_id` BIGINT UNSIGNED NOT NULL COMMENT '分类ID',
  `account_id` BIGINT UNSIGNED NOT NULL COMMENT '账户ID',
  `frequency` VARCHAR(20) NOT NULL COMMENT '频率(daily, weekly, monthly, yearly)',
  `start_date` DATE NOT NULL COMMENT '开始日期',
  `next_execution_date` DATE NOT NULL COMMENT '下次执行日期',
  `status` TINYINT(1) DEFAULT 0 COMMENT '状态(0启用 1停用)',
  `del_flag` TINYINT(1) DEFAULT 0 COMMENT '删除标志',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='周期性账单规则表';

-- ----------------------------
-- Table structure for bus_goal
-- ----------------------------
DROP TABLE IF EXISTS `bus_goal`;
CREATE TABLE `bus_goal` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '目标ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `name` VARCHAR(100) NOT NULL COMMENT '目标名称',
  `target_amount` DECIMAL(15, 2) NOT NULL COMMENT '目标金额',
  `current_amount` DECIMAL(15, 2) DEFAULT 0.00 COMMENT '当前筹集金额',
  `deadline` DATE DEFAULT NULL COMMENT '截止日期',
  `icon` VARCHAR(50) DEFAULT 'Target' COMMENT '图标',
  `color` VARCHAR(50) DEFAULT NULL COMMENT '颜色',
  `status` ENUM('ongoing', 'completed', 'archived') DEFAULT 'ongoing' COMMENT '状态',
  `del_flag` TINYINT(1) DEFAULT 0 COMMENT '删除标志',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='储蓄目标表';

-- ----------------------------
-- Table structure for bus_goal_record
-- ----------------------------
DROP TABLE IF EXISTS `bus_goal_record`;
CREATE TABLE `bus_goal_record` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `goal_id` BIGINT UNSIGNED NOT NULL COMMENT '目标ID',
  `amount` DECIMAL(15, 2) NOT NULL COMMENT '操作金额',
  `operate_date` DATE NOT NULL COMMENT '日期',
  `create_by` BIGINT DEFAULT NULL COMMENT '操作人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  PRIMARY KEY (`id`),
  KEY `idx_goal_id` (`goal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='目标存取记录流水';

SET FOREIGN_KEY_CHECKS = 1;
