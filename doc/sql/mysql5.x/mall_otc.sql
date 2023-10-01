/*
 Navicat Premium Data Transfer

 Source Server         : local-dev
 Source Server Type    : MySQL
 Source Server Version : 50737
 Source Host           : 192.168.11.42:3306
 Source Schema         : mall_otc

 Target Server Type    : MySQL
 Target Server Version : 50737
 File Encoding         : 65001

 Date: 03/11/2022 16:55:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for otc_pay_info
-- ----------------------------
DROP TABLE IF EXISTS `otc_pay_info`;
CREATE TABLE `otc_pay_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `member_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '会员id',
  `pay_type` tinyint(1) DEFAULT '0',
  `name` varchar(32) DEFAULT NULL COMMENT '名称',
  `qr_code` varchar(255) DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '备注',
  `status` tinyint(1) DEFAULT '0',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='支付信息表';

SET FOREIGN_KEY_CHECKS = 1;
