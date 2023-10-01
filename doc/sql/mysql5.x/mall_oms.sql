/*
 Navicat Premium Data Transfer

 Source Server         : local-dev
 Source Server Type    : MySQL
 Source Server Version : 50737
 Source Host           : 192.168.11.42:3306
 Source Schema         : mall_oms

 Target Server Type    : MySQL
 Target Server Version : 50737
 File Encoding         : 65001

 Date: 03/11/2022 16:55:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for oms_order
-- ----------------------------
DROP TABLE IF EXISTS `oms_order`;
CREATE TABLE `oms_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_sn` varchar(64) NOT NULL DEFAULT '' COMMENT '订单号',
  `total_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '订单总额（分）',
  `total_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '商品总数',
  `source_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '订单来源[0->PC订单；1->APP订单]',
  `status` int(11) NOT NULL DEFAULT '101' COMMENT '订单状态：\r\n101->待付款；\r\n102->用户取消；\r\n103->系统取消；\r\n201->已付款；\r\n202->申请退款；\r\n203->已退款；\r\n301->待发货；\r\n401->已发货；\r\n501->用户收货；\r\n502->系统收货；\r\n901->已完成；',
  `remark` varchar(500) NOT NULL DEFAULT '' COMMENT '订单备注',
  `member_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '会员id',
  `freight_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '运费金额（分）',
  `pay_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '应付总额（分）',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `pay_type` tinyint(4) DEFAULT NULL COMMENT '支付方式【1->微信jsapi；2->支付宝；3->余额； 4->微信app；】',
  `out_trade_no` varchar(32) DEFAULT NULL COMMENT '微信支付等第三方支付平台的商户订单号',
  `transaction_id` varchar(32) DEFAULT NULL COMMENT '微信支付订单号',
  `out_refund_no` varchar(32) DEFAULT NULL COMMENT '商户退款单号',
  `refund_id` varchar(32) DEFAULT NULL COMMENT '微信退款单号',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '确认收货时间',
  `comment_time` datetime DEFAULT NULL COMMENT '评价时间',
  `reason` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT ' 取消原因',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除【0->正常；1->已删除】',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `index_order_sn` (`order_sn`) USING BTREE COMMENT '订单号唯一索引',
  UNIQUE KEY `index_otn` (`out_trade_no`) USING BTREE COMMENT '商户订单号唯一索引',
  UNIQUE KEY `index_ti` (`transaction_id`) USING BTREE COMMENT '商户支付单号唯一索引',
  UNIQUE KEY `index_orn` (`out_refund_no`) USING BTREE COMMENT '商户退款单号唯一索引',
  UNIQUE KEY `index_ri` (`refund_id`) USING BTREE COMMENT '退款单号唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=387 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单详情表';

-- ----------------------------
-- Table structure for oms_order_delivery
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_delivery`;
CREATE TABLE `oms_order_delivery` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) NOT NULL COMMENT '订单id',
  `delivery_company` varchar(64) NOT NULL DEFAULT '' COMMENT '物流公司(配送方式)',
  `delivery_sn` varchar(64) NOT NULL DEFAULT '' COMMENT '物流单号',
  `receiver_name` varchar(100) NOT NULL DEFAULT '' COMMENT '收货人姓名',
  `receiver_phone` varchar(32) NOT NULL DEFAULT '' COMMENT '收货人电话',
  `receiver_post_code` varchar(32) NOT NULL DEFAULT '' COMMENT '收货人邮编',
  `receiver_province` varchar(32) NOT NULL DEFAULT '' COMMENT '省份/直辖市',
  `receiver_city` varchar(32) NOT NULL DEFAULT '' COMMENT '城市',
  `receiver_region` varchar(32) NOT NULL DEFAULT '' COMMENT '区',
  `receiver_detail_address` varchar(500) NOT NULL DEFAULT '' COMMENT '详细地址',
  `remark` varchar(500) NOT NULL DEFAULT '' COMMENT '备注',
  `delivery_status` tinyint(4) DEFAULT '0' COMMENT '物流状态【0->运输中；1->已收货】',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '确认收货时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除【0->正常；1->已删除】',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单物流记录表';

-- ----------------------------
-- Table structure for oms_order_item
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_item`;
CREATE TABLE `oms_order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `spu_name` varchar(128) DEFAULT NULL COMMENT '商品名称',
  `type` tinyint(1) DEFAULT '0' COMMENT '0-NFT 1-盲盒',
  `sku_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '商品ID',
  `spu_id` bigint(20) DEFAULT NULL,
  `product_id` varchar(64) DEFAULT NULL COMMENT '苹果商品ID',
  `sku_sn` varchar(64) NOT NULL DEFAULT '' COMMENT '商品编码',
  `sku_name` varchar(128) NOT NULL DEFAULT '' COMMENT '规格名称',
  `pic_url` varchar(255) NOT NULL DEFAULT '' COMMENT '商品图片',
  `price` bigint(20) NOT NULL DEFAULT '0' COMMENT '商品单价(单位：分)',
  `count` int(11) NOT NULL DEFAULT '0' COMMENT '商品数量',
  `total_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '商品总价(单位：分)',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识(1:已删除；0:正常)',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_order_id` (`order_id`) USING BTREE COMMENT '订单id索引'
) ENGINE=InnoDB AUTO_INCREMENT=387 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单商品信息表';

-- ----------------------------
-- Table structure for oms_order_log
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_log`;
CREATE TABLE `oms_order_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) NOT NULL COMMENT '订单id',
  `user` varchar(100) DEFAULT NULL COMMENT '操作人[用户；系统；后台管理员]',
  `detail` varchar(255) NOT NULL DEFAULT '' COMMENT '操作详情',
  `order_status` int(11) DEFAULT NULL COMMENT '操作时订单状态',
  `remark` varchar(500) NOT NULL DEFAULT '' COMMENT '备注',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除【0->正常；1->已删除】',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单操作历史记录';

-- ----------------------------
-- Table structure for oms_order_pay
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_pay`;
CREATE TABLE `oms_order_pay` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` bigint(20) NOT NULL COMMENT '订单id',
  `pay_sn` varchar(128) NOT NULL DEFAULT '' COMMENT '支付流水号',
  `pay_amount` bigint(20) NOT NULL DEFAULT '0' COMMENT '应付总额(分)',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `pay_type` tinyint(4) DEFAULT NULL COMMENT '支付方式【1->支付宝；2->微信；3->银联； 4->货到付款；】',
  `pay_status` tinyint(4) DEFAULT NULL COMMENT '支付状态',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `callback_content` text NOT NULL COMMENT '回调内容',
  `callback_time` datetime DEFAULT NULL COMMENT '回调时间',
  `pay_subject` varchar(200) NOT NULL DEFAULT '' COMMENT '交易内容',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除【0->正常；1->已删除】',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=308 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='支付信息表';

-- ----------------------------
-- Table structure for oms_order_setting
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_setting`;
CREATE TABLE `oms_order_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `flash_order_overtime` int(11) DEFAULT NULL COMMENT '秒杀订单超时关闭时间(分)',
  `normal_order_overtime` int(11) DEFAULT NULL COMMENT '正常订单超时时间(分)',
  `confirm_overtime` int(11) DEFAULT NULL COMMENT '发货后自动确认收货时间（天）',
  `finish_overtime` int(11) DEFAULT NULL COMMENT '自动完成交易时间，不能申请退货（天）',
  `comment_overtime` int(11) DEFAULT NULL COMMENT '订单完成后自动好评时间（天）',
  `member_level` tinyint(4) DEFAULT NULL COMMENT '会员等级【0-不限会员等级，全部通用；其他-对应的其他会员等级】',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除【0->正常；1->已删除】',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单配置信息';

-- ----------------------------
-- Table structure for oms_pay_channel
-- ----------------------------
DROP TABLE IF EXISTS `oms_pay_channel`;
CREATE TABLE `oms_pay_channel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(20) DEFAULT NULL,
  `pay_type` tinyint(4) DEFAULT NULL COMMENT '支付方式',
  `status` tinyint(1) DEFAULT '0',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `index_pay_type` (`pay_type`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='支付信息表';

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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
