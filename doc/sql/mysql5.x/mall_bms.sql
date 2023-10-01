/*
 Navicat Premium Data Transfer

 Source Server         : local-dev
 Source Server Type    : MySQL
 Source Server Version : 50737
 Source Host           : 192.168.11.42:3306
 Source Schema         : mall_bms

 Target Server Type    : MySQL
 Target Server Version : 50737
 File Encoding         : 65001

 Date: 03/11/2022 16:54:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for oms_compound_config
-- ----------------------------
DROP TABLE IF EXISTS `oms_compound_config`;
CREATE TABLE `oms_compound_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data` text COMMENT '规则',
  `spu_id` bigint(20) DEFAULT NULL,
  `type` tinyint(1) DEFAULT '0' COMMENT '虚拟币类型0-元葱',
  `type_value` decimal(20,2) DEFAULT NULL COMMENT '数量',
  `status` tinyint(1) DEFAULT '0' COMMENT '0-可用1-不可用',
  `remark` varchar(255) DEFAULT NULL,
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单详情表';

-- ----------------------------
-- Table structure for oms_exchange_config
-- ----------------------------
DROP TABLE IF EXISTS `oms_exchange_config`;
CREATE TABLE `oms_exchange_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `exchange_type` tinyint(2) DEFAULT '0' COMMENT '0-币物兑换 1-物币兑换',
  `coin_type` tinyint(1) DEFAULT '0' COMMENT '虚拟币类型0-元葱',
  `coin_value` decimal(20,2) DEFAULT NULL COMMENT '数量',
  `spu_id` bigint(20) DEFAULT NULL,
  `period_type` tinyint(1) DEFAULT '0' COMMENT '过期周期 0-天 1-永久',
  `period_value` int(20) DEFAULT '0' COMMENT '周期内允许兑换数量',
  `max_limit` int(20) DEFAULT '0' COMMENT '最大兑换数量',
  `status` tinyint(1) DEFAULT '0' COMMENT '0-可用1-不可用',
  `visible` tinyint(1) DEFAULT '0' COMMENT '0-隐藏 1-可见',
  `remark` varchar(255) DEFAULT NULL,
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_spu_id` (`spu_id`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单详情表';

-- ----------------------------
-- Table structure for oms_exchange_log
-- ----------------------------
DROP TABLE IF EXISTS `oms_exchange_log`;
CREATE TABLE `oms_exchange_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `exchange_type` tinyint(2) DEFAULT '0',
  `member_id` bigint(20) DEFAULT NULL COMMENT '虚拟币类型0-元葱',
  `spu_id` bigint(20) DEFAULT NULL,
  `coin_type` tinyint(1) DEFAULT '0' COMMENT '0-可用1-不可用',
  `coin_value` decimal(20,2) DEFAULT NULL COMMENT '数量',
  `item_name` varchar(255) DEFAULT NULL,
  `item_no` varchar(255) DEFAULT NULL,
  `pic_url` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_spu_id` (`spu_id`) USING HASH,
  KEY `index _member_id` (`member_id`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=188 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单详情表';

-- ----------------------------
-- Table structure for oms_farm_claim
-- ----------------------------
DROP TABLE IF EXISTS `oms_farm_claim`;
CREATE TABLE `oms_farm_claim` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `pool_id` bigint(20) DEFAULT NULL,
  `member_id` bigint(20) DEFAULT NULL,
  `current_days` int(20) DEFAULT '0',
  `coin_type` tinyint(2) DEFAULT '0',
  `reward_amount` decimal(20,2) DEFAULT NULL,
  `status` tinyint(1) DEFAULT '0',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='市场表';

-- ----------------------------
-- Table structure for oms_farm_log
-- ----------------------------
DROP TABLE IF EXISTS `oms_farm_log`;
CREATE TABLE `oms_farm_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `pool_id` bigint(20) DEFAULT NULL,
  `member_id` bigint(20) DEFAULT NULL,
  `spu_id` bigint(20) NOT NULL,
  `item_id` bigint(20) DEFAULT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `item_no` int(20) DEFAULT NULL,
  `pic_url` varchar(255) DEFAULT NULL,
  `days` int(20) NOT NULL DEFAULT '0',
  `current_days` int(20) DEFAULT '0',
  `alloc_point` double(20,1) NOT NULL DEFAULT '0.0',
  `log_type` tinyint(1) DEFAULT '0',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='市场表';

-- ----------------------------
-- Table structure for oms_farm_pool
-- ----------------------------
DROP TABLE IF EXISTS `oms_farm_pool`;
CREATE TABLE `oms_farm_pool` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) DEFAULT NULL,
  `data` text,
  `spu_id` bigint(20) NOT NULL,
  `balance` bigint(20) NOT NULL DEFAULT '0',
  `total_alloc_point` double(20,1) NOT NULL DEFAULT '0.0',
  `days` int(20) DEFAULT '0',
  `day_amount` decimal(20,2) DEFAULT '0.00',
  `current_days` int(20) DEFAULT '0',
  `remark` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `status` tinyint(1) DEFAULT '0',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='市场表';

-- ----------------------------
-- Table structure for oms_farm_stake
-- ----------------------------
DROP TABLE IF EXISTS `oms_farm_stake`;
CREATE TABLE `oms_farm_stake` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `member_id` bigint(20) DEFAULT NULL,
  `pool_id` bigint(20) DEFAULT NULL,
  `spu_id` bigint(20) NOT NULL,
  `alloc_point` double(20,1) NOT NULL DEFAULT '0.0',
  `total` int(20) DEFAULT '0',
  `current_days` int(20) DEFAULT '0',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='市场表';

-- ----------------------------
-- Table structure for oms_farm_stake_item
-- ----------------------------
DROP TABLE IF EXISTS `oms_farm_stake_item`;
CREATE TABLE `oms_farm_stake_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `stake_member_id` bigint(20) DEFAULT NULL,
  `pool_id` bigint(20) DEFAULT NULL,
  `member_id` bigint(20) DEFAULT NULL,
  `spu_id` bigint(20) NOT NULL,
  `item_id` bigint(20) DEFAULT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `item_no` int(20) DEFAULT NULL,
  `pic_url` varchar(255) DEFAULT NULL,
  `days` int(20) NOT NULL DEFAULT '0',
  `current_days` int(20) DEFAULT '0',
  `alloc_point` double(20,1) NOT NULL DEFAULT '0.0',
  `remark` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='市场表';

-- ----------------------------
-- Table structure for oms_item_log
-- ----------------------------
DROP TABLE IF EXISTS `oms_item_log`;
CREATE TABLE `oms_item_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` tinyint(4) DEFAULT NULL,
  `item_name` varchar(200) DEFAULT NULL,
  `item_no` bigint(20) DEFAULT NULL,
  `member_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `member_from` bigint(20) DEFAULT NULL,
  `member_to` bigint(20) DEFAULT NULL,
  `spu_id` bigint(20) DEFAULT NULL,
  `pic_url` varchar(255) DEFAULT NULL,
  `reason` text,
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_member_id` (`member_id`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=188 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单详情表';

-- ----------------------------
-- Table structure for oms_market
-- ----------------------------
DROP TABLE IF EXISTS `oms_market`;
CREATE TABLE `oms_market` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `member_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '会员id',
  `item_id` bigint(20) NOT NULL,
  `item_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '物品类型',
  `item_no` varchar(20) NOT NULL DEFAULT '0' COMMENT '物品NO',
  `spu_id` bigint(20) DEFAULT NULL COMMENT 'spu',
  `name` varchar(32) DEFAULT NULL COMMENT '名称',
  `pic_url` varchar(255) DEFAULT NULL COMMENT '图片链接',
  `coin_type` tinyint(1) DEFAULT '0' COMMENT '币种',
  `coin_num` decimal(20,2) DEFAULT NULL COMMENT '币数量',
  `fee` bigint(20) DEFAULT '0' COMMENT '手续费',
  `buyer_id` bigint(20) DEFAULT NULL COMMENT '买家ID',
  `buyer_name` varchar(255) DEFAULT NULL,
  `buy_timed` datetime DEFAULT NULL COMMENT '购买时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '备注',
  `status` tinyint(1) DEFAULT '0',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='市场表';

-- ----------------------------
-- Table structure for oms_market_config
-- ----------------------------
DROP TABLE IF EXISTS `oms_market_config`;
CREATE TABLE `oms_market_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(32) DEFAULT NULL COMMENT '名称',
  `spu_id` bigint(20) DEFAULT NULL COMMENT 'spu',
  `coin_type` tinyint(1) DEFAULT '0' COMMENT '币种',
  `fee` decimal(20,2) DEFAULT '0.00',
  `status` tinyint(1) DEFAULT '0',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='市场表';

-- ----------------------------
-- Table structure for oms_member_item
-- ----------------------------
DROP TABLE IF EXISTS `oms_member_item`;
CREATE TABLE `oms_member_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `member_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `type` tinyint(1) DEFAULT '0' COMMENT '0-NFT 1-盲盒',
  `spu_id` bigint(20) DEFAULT NULL,
  `hex_id` varchar(255) DEFAULT NULL,
  `name` varchar(128) NOT NULL DEFAULT '' COMMENT '名称',
  `contract` varchar(255) DEFAULT NULL COMMENT '合约地址',
  `pic_url` varchar(255) NOT NULL DEFAULT '' COMMENT '商品图片',
  `hash` varchar(255) DEFAULT NULL,
  `item_no` varchar(20) DEFAULT NULL,
  `bind` tinyint(1) NOT NULL DEFAULT '0' COMMENT '绑定状态:( 0:未绑定 1:绑定)',
  `inside` tinyint(1) DEFAULT '0' COMMENT '转赠状态:0-内部 1-外部',
  `transfer` tinyint(1) DEFAULT '0' COMMENT '转移状态 0未转移 1已转移',
  `status` tinyint(1) DEFAULT '0' COMMENT '铸造状态 0未铸造 1铸造中 2已铸造',
  `freeze_type` tinyint(1) DEFAULT '0' COMMENT '冻结类型:0-默认 1-市场 2-挖矿',
  `freeze` tinyint(1) DEFAULT '0' COMMENT '冻结状态:0- 未冻结 1-已冻结',
  `started` datetime DEFAULT NULL COMMENT '开始时间',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_member_id` (`member_id`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=188 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单详情表';

-- ----------------------------
-- Table structure for oms_member_mission
-- ----------------------------
DROP TABLE IF EXISTS `oms_member_mission`;
CREATE TABLE `oms_member_mission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '任务名称',
  `content` json DEFAULT NULL COMMENT '用户上传的信息',
  `item_id` bigint(20) DEFAULT NULL COMMENT '物品id',
  `status` tinyint(1) DEFAULT '0',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for oms_member_mission_group
-- ----------------------------
DROP TABLE IF EXISTS `oms_member_mission_group`;
CREATE TABLE `oms_member_mission_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `mission_group_id` bigint(20) DEFAULT NULL COMMENT '任务包ID',
  `percent` int(8) DEFAULT NULL COMMENT '任务进度0-100',
  `status` tinyint(1) DEFAULT '0' COMMENT '任务状态0进行中，1完成',
  `reward_status` tinyint(1) DEFAULT NULL COMMENT '奖励状态0未奖励，1完成',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for oms_mission_config
-- ----------------------------
DROP TABLE IF EXISTS `oms_mission_config`;
CREATE TABLE `oms_mission_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(20) DEFAULT NULL COMMENT '任务名称',
  `claim_coin_type` tinyint(2) DEFAULT NULL COMMENT '奖励积分类型',
  `claim_coin_value` decimal(20,4) DEFAULT '0.0000' COMMENT '奖励积分数量',
  `cost_coin_type` tinyint(2) DEFAULT NULL COMMENT '领取消耗积分类型',
  `cost_coin_value` decimal(20,4) DEFAULT '0.0000' COMMENT '领取消耗积分数量',
  `cost` tinyint(1) DEFAULT '1' COMMENT '0-返还1-销毁',
  `visible` tinyint(1) DEFAULT '0' COMMENT '0-可用1-不可用',
  `re_num` int(11) DEFAULT '0' COMMENT '剩余总量',
  `mint_num` int(11) DEFAULT '0' COMMENT '释放总量',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单详情表';

-- ----------------------------
-- Table structure for oms_mission_group_config
-- ----------------------------
DROP TABLE IF EXISTS `oms_mission_group_config`;
CREATE TABLE `oms_mission_group_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(20) DEFAULT NULL COMMENT '任务包名称',
  `group_ids` json DEFAULT NULL COMMENT '任务包ids',
  `reward_coin_type` tinyint(2) DEFAULT NULL COMMENT '奖励类型',
  `reward_coin_value` decimal(20,4) DEFAULT '0.0000' COMMENT '奖励数量',
  `visible` tinyint(1) DEFAULT '0' COMMENT '0-可用1-不可用',
  `ext` json DEFAULT NULL,
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单详情表';

-- ----------------------------
-- Table structure for oms_transfer_config
-- ----------------------------
DROP TABLE IF EXISTS `oms_transfer_config`;
CREATE TABLE `oms_transfer_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` tinyint(1) DEFAULT '0' COMMENT '虚拟币类型0-元葱',
  `spu_id` bigint(20) DEFAULT NULL,
  `type_value` decimal(20,2) DEFAULT NULL COMMENT '数量',
  `icd` bigint(20) DEFAULT '0' COMMENT '内网转赠秒冷却',
  `ocd` bigint(20) DEFAULT '0' COMMENT '外网转赠秒冷却',
  `remark` varchar(255) DEFAULT NULL,
  `status` tinyint(1) DEFAULT '0' COMMENT '0-可用1-不可用',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `index_unique_sku_id` (`spu_id`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=1041 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单详情表';

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
