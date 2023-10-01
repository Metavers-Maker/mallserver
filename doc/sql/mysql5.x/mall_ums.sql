/*
 Navicat Premium Data Transfer

 Source Server         : local-dev
 Source Server Type    : MySQL
 Source Server Version : 50737
 Source Host           : 192.168.11.42:3306
 Source Schema         : mall_ums

 Target Server Type    : MySQL
 Target Server Version : 50737
 File Encoding         : 65001

 Date: 03/11/2022 16:56:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ums_ad_level_config
-- ----------------------------
DROP TABLE IF EXISTS `ums_ad_level_config`;
CREATE TABLE `ums_ad_level_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `level` int(20) DEFAULT NULL COMMENT '等级',
  `team_num` int(20) DEFAULT '0',
  `next_level` int(20) DEFAULT '0' COMMENT '下一级',
  `invite_num` int(20) DEFAULT '0',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_member_id` (`level`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for ums_address
-- ----------------------------
DROP TABLE IF EXISTS `ums_address`;
CREATE TABLE `ums_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) DEFAULT NULL COMMENT '会员ID',
  `consignee_name` varchar(64) DEFAULT NULL COMMENT '收货人姓名',
  `consignee_mobile` varchar(20) DEFAULT NULL COMMENT '收货人联系方式',
  `province` varchar(64) DEFAULT NULL COMMENT '省',
  `city` varchar(64) DEFAULT NULL COMMENT '市',
  `area` varchar(64) DEFAULT NULL COMMENT '区',
  `detail_address` varchar(255) DEFAULT NULL COMMENT '详细地址',
  `zip_code` char(6) DEFAULT NULL COMMENT '邮编',
  `defaulted` tinyint(4) DEFAULT NULL COMMENT '是否默认地址',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for ums_follow
-- ----------------------------
DROP TABLE IF EXISTS `ums_follow`;
CREATE TABLE `ums_follow` (
  `id` bigint(50) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(50) DEFAULT NULL COMMENT '用户 id',
  `follow_id` bigint(50) DEFAULT NULL COMMENT '国家',
  `status` tinyint(1) DEFAULT '0',
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for ums_invite_level_config
-- ----------------------------
DROP TABLE IF EXISTS `ums_invite_level_config`;
CREATE TABLE `ums_invite_level_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `level` int(20) DEFAULT NULL COMMENT '等级',
  `invite_num` int(20) DEFAULT '0',
  `coin_type` int(2) DEFAULT '0' COMMENT '指定币种',
  `coin_value` decimal(20,4) DEFAULT '0.0000' COMMENT '值',
  `min_value` decimal(20,4) DEFAULT '0.0000' COMMENT '最小值',
  `max_value` decimal(20,4) DEFAULT '0.0000' COMMENT '最大值',
  `fee` double(20,4) DEFAULT '0.0000' COMMENT '手续费',
  `claim_coin_type` int(2) DEFAULT '0' COMMENT '指定币种',
  `claim_coin_value` decimal(20,4) DEFAULT NULL COMMENT '值',
  `next_level` int(20) DEFAULT NULL COMMENT '下一级',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_member_id` (`level`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for ums_member
-- ----------------------------
DROP TABLE IF EXISTS `ums_member`;
CREATE TABLE `ums_member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `openid` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `trade_password` varchar(255) DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '邮箱地址',
  `mobile` varchar(255) DEFAULT NULL,
  `uid` varchar(20) DEFAULT NULL COMMENT '展示ID',
  `nick_name` varchar(50) DEFAULT NULL,
  `gender` tinyint(1) DEFAULT '1',
  `avatar_url` text,
  `status` tinyint(1) DEFAULT '1',
  `auth_status` tinyint(1) DEFAULT '0',
  `birthday` date DEFAULT NULL,
  `secret` varchar(255) DEFAULT NULL,
  `is_bind_google` tinyint(1) DEFAULT '0',
  `chain_address` varchar(255) DEFAULT NULL COMMENT '链地址',
  `invite_code` varchar(20) DEFAULT NULL,
  `safe_code` varchar(20) DEFAULT NULL COMMENT '安全码',
  `salt` varchar(10) DEFAULT NULL,
  `ext` text CHARACTER SET utf8mb4,
  `last_login_type` varchar(255) DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL,
  `last_login_ip` varchar(255) DEFAULT NULL,
  `device_id` text,
  `deleted` tinyint(1) DEFAULT '0',
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `index_unique_mobile` (`mobile`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=175 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for ums_member_auth
-- ----------------------------
DROP TABLE IF EXISTS `ums_member_auth`;
CREATE TABLE `ums_member_auth` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) DEFAULT NULL,
  `mobile` varchar(20) DEFAULT NULL,
  `real_name` varchar(255) DEFAULT NULL,
  `id_card_type` tinyint(1) DEFAULT '0' COMMENT '邮箱地址',
  `id_card` varchar(255) DEFAULT NULL,
  `status` tinyint(1) DEFAULT '0',
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_id_card` (`id_card`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for ums_member_auth_log
-- ----------------------------
DROP TABLE IF EXISTS `ums_member_auth_log`;
CREATE TABLE `ums_member_auth_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) DEFAULT NULL,
  `request` text,
  `response` text COMMENT '邮箱地址',
  `seq_no` varchar(255) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for ums_member_invite
-- ----------------------------
DROP TABLE IF EXISTS `ums_member_invite`;
CREATE TABLE `ums_member_invite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `nick_name` varchar(50) DEFAULT NULL,
  `invite_code` varchar(20) DEFAULT NULL,
  `invite_level` int(20) DEFAULT '0',
  `invite_member_id` bigint(20) DEFAULT NULL,
  `invite_nick_name` varchar(50) DEFAULT NULL,
  `invite_mobile` varchar(20) DEFAULT NULL,
  `invite_num` int(20) DEFAULT '0',
  `invite_total` int(20) DEFAULT '0',
  `auth_num` int(20) DEFAULT '0',
  `auth_status` tinyint(1) DEFAULT '0',
  `depth_path` text,
  `depth` int(20) DEFAULT '0',
  `active_value` decimal(20,4) DEFAULT '0.0000',
  `active_a_value` decimal(20,4) DEFAULT '0.0000',
  `active_b_value` decimal(20,4) DEFAULT '0.0000',
  `team_level` int(20) DEFAULT '0',
  `team_active_value` decimal(20,4) DEFAULT '0.0000',
  `team_active_a_value` decimal(20,4) DEFAULT '0.0000',
  `team_active_b_value` decimal(20,4) DEFAULT '0.0000',
  `team_active_value_ext` decimal(20,4) DEFAULT '0.0000',
  `team_big_active_value` decimal(20,4) DEFAULT '0.0000',
  `team_small_active_value` decimal(20,4) DEFAULT '0.0000',
  `ext` text,
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `ad_level` int(20) DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_member_id` (`member_id`) USING HASH,
  KEY `index_invite_code` (`invite_code`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=175 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for ums_member_log
-- ----------------------------
DROP TABLE IF EXISTS `ums_member_log`;
CREATE TABLE `ums_member_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` tinyint(1) DEFAULT '0',
  `member_id` bigint(20) DEFAULT NULL,
  `client_id` varchar(20) DEFAULT NULL,
  `grant_type` varchar(20) DEFAULT NULL,
  `ip` varchar(20) DEFAULT NULL,
  `device_id` varchar(255) CHARACTER SET utf8mb4 DEFAULT '',
  `device_name` varchar(50) DEFAULT NULL,
  `device_version` varchar(20) DEFAULT NULL,
  `user_agent` text,
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_member_id` (`member_id`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=153 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for ums_member_third
-- ----------------------------
DROP TABLE IF EXISTS `ums_member_third`;
CREATE TABLE `ums_member_third` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `type` tinyint(1) DEFAULT '0' COMMENT '登录类型',
  `salt` varchar(255) DEFAULT NULL COMMENT '盐',
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for ums_relation
-- ----------------------------
DROP TABLE IF EXISTS `ums_relation`;
CREATE TABLE `ums_relation` (
  `id` bigint(50) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(50) DEFAULT NULL COMMENT '用户 id',
  `follow_id` bigint(50) DEFAULT NULL COMMENT '国家',
  `status` tinyint(1) DEFAULT '0',
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for ums_star_level_config
-- ----------------------------
DROP TABLE IF EXISTS `ums_star_level_config`;
CREATE TABLE `ums_star_level_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `level` int(20) DEFAULT NULL COMMENT '等级',
  `team_active_value` decimal(20,4) DEFAULT '0.0000',
  `team_small_active_value` decimal(20,4) DEFAULT '0.0000' COMMENT '最小值',
  `invite_num` int(20) DEFAULT '0',
  `coin_type` int(2) DEFAULT '0' COMMENT '指定币种',
  `coin_value` decimal(20,4) DEFAULT '0.0000' COMMENT '值',
  `rate` decimal(20,4) DEFAULT '0.0000' COMMENT '手续费',
  `next_level` int(20) DEFAULT '0' COMMENT '下一级',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_member_id` (`level`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

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
