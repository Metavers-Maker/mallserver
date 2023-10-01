/*
 Navicat Premium Data Transfer

 Source Server         : local-dev
 Source Server Type    : MySQL
 Source Server Version : 50737
 Source Host           : 192.168.11.42:3306
 Source Schema         : mall_farm

 Target Server Type    : MySQL
 Target Server Version : 50737
 File Encoding         : 65001

 Date: 03/11/2022 16:54:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for farm_bag_config
-- ----------------------------
DROP TABLE IF EXISTS `farm_bag_config`;
CREATE TABLE `farm_bag_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `farm_id` bigint(20) DEFAULT NULL,
  `name` varchar(20) DEFAULT NULL,
  `spu_id` bigint(20) DEFAULT NULL,
  `min_days` int(2) DEFAULT '0',
  `max_days` int(4) DEFAULT '0',
  `period` int(4) DEFAULT NULL COMMENT '周期',
  `step` int(4) DEFAULT '0' COMMENT '步长',
  `activate_coin_value` decimal(20,4) DEFAULT '0.0000' COMMENT '激活消耗',
  `claim_coin_value` decimal(20,4) DEFAULT '0.0000' COMMENT '奖励',
  `claim_coin_value_ext` decimal(20,4) DEFAULT '0.0000' COMMENT '奖励',
  `active_value` decimal(20,4) DEFAULT '0.0000' COMMENT '活跃度',
  `active_value_ext` decimal(20,4) DEFAULT '0.0000' COMMENT '30返活跃度',
  `rake_back_coin_value` decimal(20,4) DEFAULT '0.0000' COMMENT '返佣',
  `rake_back_active_value` decimal(20,4) DEFAULT '0.0000' COMMENT '返佣活跃度',
  `visible` tinyint(1) DEFAULT '0' COMMENT '0-可用1-不可用',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单详情表';

-- ----------------------------
-- Table structure for farm_config
-- ----------------------------
DROP TABLE IF EXISTS `farm_config`;
CREATE TABLE `farm_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(20) DEFAULT NULL,
  `limit_hour` int(4) DEFAULT '0',
  `activate_coin_type` int(2) DEFAULT '0',
  `claim_coin_type` int(2) DEFAULT '0',
  `claim_coin_type_ext` int(2) DEFAULT '0',
  `rake_back_coin_type` int(2) DEFAULT '0' COMMENT '返佣币种',
  `visible` tinyint(1) DEFAULT '0' COMMENT '0-可用1-不可用',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单详情表';

-- ----------------------------
-- Table structure for farm_member
-- ----------------------------
DROP TABLE IF EXISTS `farm_member`;
CREATE TABLE `farm_member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `farm_id` bigint(20) DEFAULT NULL,
  `member_id` bigint(20) DEFAULT NULL,
  `claim_coin_type` int(4) DEFAULT NULL,
  `claim_coin_value` decimal(20,4) DEFAULT NULL,
  `active_value_ext` decimal(20,4) DEFAULT '0.0000' COMMENT '每天返活跃度',
  `claimed_active_value_ext` decimal(20,4) DEFAULT '0.0000' COMMENT '领取返活跃度',
  `rake_back_coin_type` int(2) DEFAULT '0' COMMENT '返佣币种',
  `rake_back_coin_value` decimal(20,4) DEFAULT '0.0000' COMMENT '返佣',
  `allow_claimed` datetime DEFAULT NULL COMMENT '允许领取时间',
  `claimed` datetime DEFAULT NULL COMMENT '领取时间',
  `status` tinyint(1) DEFAULT '0' COMMENT '0-开启 1-关闭',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18145 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单详情表';

-- ----------------------------
-- Table structure for farm_member_item
-- ----------------------------
DROP TABLE IF EXISTS `farm_member_item`;
CREATE TABLE `farm_member_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `farm_id` bigint(20) DEFAULT NULL,
  `item_id` bigint(20) DEFAULT NULL,
  `member_id` bigint(20) DEFAULT NULL,
  `name` varchar(20) DEFAULT NULL,
  `spu_id` bigint(20) DEFAULT NULL,
  `claim_coin_type` int(4) DEFAULT NULL,
  `claim_coin_value` decimal(20,4) DEFAULT '0.0000',
  `active_value_ext` decimal(20,4) DEFAULT '0.0000' COMMENT '每天返活跃度',
  `claimed_active_value_ext` decimal(20,4) DEFAULT '0.0000' COMMENT '领取返活跃度',
  `rake_back_coin_type` int(2) DEFAULT '0' COMMENT '返佣币种',
  `rake_back_coin_value` decimal(20,4) DEFAULT '0.0000' COMMENT '返佣',
  `curr_period` int(4) DEFAULT '0',
  `closed` datetime DEFAULT NULL COMMENT '关闭时间',
  `status` tinyint(1) DEFAULT '0' COMMENT '0-开启 1-关闭',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单详情表';

-- ----------------------------
-- Table structure for farm_member_log
-- ----------------------------
DROP TABLE IF EXISTS `farm_member_log`;
CREATE TABLE `farm_member_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `farm_id` bigint(20) DEFAULT NULL,
  `item_id` bigint(20) DEFAULT NULL,
  `member_id` bigint(20) DEFAULT NULL,
  `name` varchar(20) DEFAULT NULL,
  `spu_id` bigint(20) DEFAULT NULL,
  `type` int(4) DEFAULT NULL,
  `claim_coin_type` int(4) DEFAULT NULL,
  `claim_coin_value` decimal(20,4) DEFAULT NULL,
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=176 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单详情表';

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

SET FOREIGN_KEY_CHECKS = 1;
