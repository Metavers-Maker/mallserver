/*
 Navicat Premium Data Transfer

 Source Server         : local-dev
 Source Server Type    : MySQL
 Source Server Version : 50737
 Source Host           : 192.168.11.42:3306
 Source Schema         : mall_wms

 Target Server Type    : MySQL
 Target Server Version : 50737
 File Encoding         : 65001

 Date: 03/11/2022 16:56:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
  `branch_id` bigint(20) NOT NULL COMMENT 'branch transaction id',
  `xid` varchar(100) NOT NULL COMMENT 'global transaction id',
  `context` varchar(128) NOT NULL COMMENT 'undo_log context,such as serialization',
  `rollback_info` longblob NOT NULL COMMENT 'rollback info',
  `log_status` int(11) NOT NULL COMMENT '0:normal status,1:defense status',
  `log_created` datetime(6) NOT NULL COMMENT 'create datetime',
  `log_modified` datetime(6) NOT NULL COMMENT 'modify datetime',
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='AT transaction mode undo table';

-- ----------------------------
-- Table structure for wms_transfer_config
-- ----------------------------
DROP TABLE IF EXISTS `wms_transfer_config`;
CREATE TABLE `wms_transfer_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `coin_type` tinyint(4) DEFAULT '0' COMMENT '货币类型',
  `min_value` decimal(20,2) DEFAULT NULL COMMENT '会员ID',
  `max_value` decimal(20,2) DEFAULT NULL,
  `fee_type` tinyint(1) DEFAULT '0' COMMENT '费用类型 0-直扣 1-百分比',
  `min_fee` decimal(20,2) DEFAULT '0.00',
  `fee` double(20,6) DEFAULT NULL COMMENT '费用类型的值',
  `status` tinyint(1) DEFAULT '0' COMMENT '0-可用 1-不可用',
  `remark` varchar(255) DEFAULT NULL,
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `index_coin_type` (`coin_type`) USING BTREE COMMENT '币种唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='钱包转赠配置表';

-- ----------------------------
-- Table structure for wms_transfer_log
-- ----------------------------
DROP TABLE IF EXISTS `wms_transfer_log`;
CREATE TABLE `wms_transfer_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `source_id` bigint(20) DEFAULT NULL,
  `target_id` bigint(20) DEFAULT NULL,
  `target_uid` varchar(255) DEFAULT NULL,
  `coin_type` tinyint(1) DEFAULT '0',
  `balance` decimal(20,2) DEFAULT NULL,
  `fee` decimal(20,2) DEFAULT '0.00' COMMENT '余额',
  `remark` varchar(255) DEFAULT NULL,
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='钱包转赠日志表';

-- ----------------------------
-- Table structure for wms_wallet
-- ----------------------------
DROP TABLE IF EXISTS `wms_wallet`;
CREATE TABLE `wms_wallet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID',
  `coin_type` tinyint(4) DEFAULT '0' COMMENT '货币类型',
  `balance` decimal(20,4) DEFAULT '0.0000' COMMENT '余额',
  `status` tinyint(1) DEFAULT '0' COMMENT '0-可用 1-不可用',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='钱包表';

-- ----------------------------
-- Table structure for wms_wallet_log
-- ----------------------------
DROP TABLE IF EXISTS `wms_wallet_log`;
CREATE TABLE `wms_wallet_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID',
  `coin_type` tinyint(4) DEFAULT '0' COMMENT '货币类型',
  `in_balance` decimal(20,4) DEFAULT '0.0000',
  `old_balance` decimal(20,4) DEFAULT '0.0000',
  `balance` decimal(20,4) DEFAULT '0.0000' COMMENT '余额',
  `fee` decimal(20,4) DEFAULT '0.0000' COMMENT '费用',
  `op_type` tinyint(1) DEFAULT '0' COMMENT '操作类型',
  `remark` text,
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_member_id` (`member_id`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=388 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='钱包日志表';

SET FOREIGN_KEY_CHECKS = 1;
