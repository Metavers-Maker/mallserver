/*
 Navicat Premium Data Transfer

 Source Server         : local-dev
 Source Server Type    : MySQL
 Source Server Version : 50737
 Source Host           : 192.168.11.42:3306
 Source Schema         : mall_pms

 Target Server Type    : MySQL
 Target Server Version : 50737
 File Encoding         : 65001

 Date: 03/11/2022 16:55:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for pms_banner
-- ----------------------------
DROP TABLE IF EXISTS `pms_banner`;
CREATE TABLE `pms_banner` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL COMMENT '商品名称',
  `link_type` tinyint(1) DEFAULT '0' COMMENT '链接类型:0 外链 1 内链',
  `link` varchar(255) DEFAULT '0' COMMENT '链接',
  `source` varchar(255) DEFAULT NULL COMMENT 'url',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `visible` tinyint(1) DEFAULT '0' COMMENT '0-隐藏 1-可见',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='轮播表';

-- ----------------------------
-- Table structure for pms_brand
-- ----------------------------
DROP TABLE IF EXISTS `pms_brand`;
CREATE TABLE `pms_brand` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `name` varchar(64) NOT NULL COMMENT '品牌名称',
  `logo_url` varchar(255) DEFAULT NULL COMMENT 'LOGO图片',
  `images` json DEFAULT NULL COMMENT '图集',
  `icons` json DEFAULT NULL,
  `ext` json DEFAULT NULL COMMENT '扩展字段',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `visible` tinyint(1) DEFAULT '0' COMMENT '0-隐藏 1-可见',
  `status` tinyint(1) DEFAULT '0' COMMENT '0-下架 1-上架',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品品牌表';

-- ----------------------------
-- Table structure for pms_ground
-- ----------------------------
DROP TABLE IF EXISTS `pms_ground`;
CREATE TABLE `pms_ground` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
  `type` tinyint(1) DEFAULT '0' COMMENT '0-NFT 1-盲盒',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `visible` tinyint(1) DEFAULT '0' COMMENT '显示状态:( 0:隐藏 1:显示)',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品广场表';

-- ----------------------------
-- Table structure for pms_hot
-- ----------------------------
DROP TABLE IF EXISTS `pms_hot`;
CREATE TABLE `pms_hot` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL COMMENT '商品名称',
  `ext` json DEFAULT NULL COMMENT '扩展字段',
  `content_type` tinyint(1) DEFAULT '0' COMMENT '内容类型:0 主题 1 产品',
  `content_id` bigint(20) DEFAULT '0' COMMENT '内容ID',
  `visible` tinyint(1) DEFAULT '0' COMMENT '0-隐藏 1-可见',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品热度表';

-- ----------------------------
-- Table structure for pms_rnd
-- ----------------------------
DROP TABLE IF EXISTS `pms_rnd`;
CREATE TABLE `pms_rnd` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL COMMENT '名称',
  `sku_id` bigint(20) DEFAULT '0' COMMENT 'skuId',
  `spu_id` bigint(20) DEFAULT NULL COMMENT 'spuId',
  `data` text COMMENT '规则内容',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品热度表';

-- ----------------------------
-- Table structure for pms_sku
-- ----------------------------
DROP TABLE IF EXISTS `pms_sku`;
CREATE TABLE `pms_sku` (
  `id` bigint(20) NOT NULL,
  `spu_id` bigint(20) NOT NULL COMMENT 'SPU ID',
  `name` varchar(128) DEFAULT NULL COMMENT '商品名称',
  `price` bigint(20) DEFAULT NULL COMMENT '商品价格(单位：分)',
  `pic_url` varchar(255) DEFAULT NULL COMMENT '商品主图',
  `stock_num` int(11) DEFAULT '0' COMMENT '库存数量',
  `locked_stock_num` int(11) DEFAULT '0' COMMENT '锁定库存数量',
  `mint_num` int(11) DEFAULT '0' COMMENT '铸造数量',
  `visible` tinyint(1) NOT NULL DEFAULT '0' COMMENT '显示状态:( 0:隐藏 1:显示)',
  `closed` tinyint(1) DEFAULT '0',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `fk_pms_sku_pms_spu` (`spu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品库存表';

-- ----------------------------
-- Table structure for pms_spu
-- ----------------------------
DROP TABLE IF EXISTS `pms_spu`;
CREATE TABLE `pms_spu` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `name` varchar(64) NOT NULL COMMENT '商品名称',
  `subject_id` bigint(20) NOT NULL COMMENT '商品主题ID',
  `brand_id` bigint(20) DEFAULT NULL COMMENT '商品品牌ID',
  `product_id` varchar(64) DEFAULT NULL COMMENT '苹果支付产品ID',
  `contract` varchar(255) DEFAULT NULL COMMENT '合约地址',
  `source_type` tinyint(1) DEFAULT '0' COMMENT '源类型：0-图片 1-视频 2-3D模型 3-音频',
  `type` tinyint(1) DEFAULT '0' COMMENT ' 商品类型：0-NFT 1-盲盒',
  `rule_id` bigint(20) DEFAULT NULL COMMENT '规则ID用于盲盒',
  `price` bigint(20) NOT NULL COMMENT '现价【起】',
  `total` int(11) DEFAULT '0' COMMENT '发行量',
  `sales` int(11) DEFAULT '0' COMMENT '销量',
  `pic_url` varchar(255) DEFAULT NULL COMMENT '商品主图',
  `album` json DEFAULT NULL COMMENT '商品图册',
  `images` json DEFAULT NULL COMMENT '图集',
  `icons` json DEFAULT NULL COMMENT '图标地址集',
  `ext` json DEFAULT NULL COMMENT '扩展字段',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `bind` tinyint(1) NOT NULL DEFAULT '0' COMMENT '绑定状态:( 0:未绑定 1:绑定)',
  `visible` tinyint(1) NOT NULL DEFAULT '0' COMMENT '显示状态:( 0:隐藏 1:显示)',
  `status` tinyint(1) DEFAULT '0' COMMENT '商品状态：0-下架 1-上架',
  `dev` tinyint(1) DEFAULT '0' COMMENT '环境：0-正式 1-开发',
  `started` datetime DEFAULT NULL COMMENT '开售时间',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `fk_pms_spu_pms_brand` (`brand_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品表';

-- ----------------------------
-- Table structure for pms_subject
-- ----------------------------
DROP TABLE IF EXISTS `pms_subject`;
CREATE TABLE `pms_subject` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `name` varchar(64) NOT NULL COMMENT '商品主题名称',
  `brand_id` bigint(20) NOT NULL COMMENT '品牌ID',
  `icon_url` varchar(255) DEFAULT NULL COMMENT '缩略图地址',
  `images` json DEFAULT NULL COMMENT '图集',
  `icons` json DEFAULT NULL,
  `ext` json DEFAULT NULL COMMENT '扩展字段',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `visible` tinyint(1) DEFAULT '0' COMMENT '显示状态:( 0:隐藏 1:显示)',
  `status` tinyint(1) DEFAULT '0' COMMENT '0-下架 1-上架',
  `started` datetime DEFAULT NULL COMMENT '开始时间',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品主题表';

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
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL,
  `detail` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `age` int(3) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
