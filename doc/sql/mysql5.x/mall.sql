/*
 Navicat Premium Data Transfer

 Source Server         : local-dev
 Source Server Type    : MySQL
 Source Server Version : 50737
 Source Host           : 192.168.11.42:3306
 Source Schema         : mall

 Target Server Type    : MySQL
 Target Server Version : 50737
 File Encoding         : 65001

 Date: 03/11/2022 16:54:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) DEFAULT '' COMMENT '部门名称',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父节点id',
  `tree_path` varchar(255) DEFAULT '' COMMENT '父节点id路径',
  `sort` int(11) DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：1-正常 0-禁用',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除状态：1-删除 0-未删除',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='部门表';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
BEGIN;
INSERT INTO `sys_dept` VALUES (1, 'Hiyuan团队', 0, '0', 1, 1, 0, NULL, '2022-09-30 12:12:59');
INSERT INTO `sys_dept` VALUES (2, '开发部', 1, '0,1', 1, 1, 0, NULL, '2022-09-30 12:14:12');
INSERT INTO `sys_dept` VALUES (3, '测试部', 1, '0,1', 2, 1, 0, NULL, '2022-09-30 12:14:19');
INSERT INTO `sys_dept` VALUES (8, '运营部', 1, '0,1', 0, 1, 0, '2022-09-30 12:13:28', '2022-09-30 12:14:27');
INSERT INTO `sys_dept` VALUES (10, '产品部', 1, '0,1', 0, 1, 0, '2022-09-30 12:13:56', '2022-09-30 12:14:23');
INSERT INTO `sys_dept` VALUES (11, '商务部', 1, '0,1', 0, 1, 0, '2022-09-30 12:14:44', '2022-09-30 12:14:44');
INSERT INTO `sys_dept` VALUES (12, '设计部', 1, '0,1', 0, 1, 0, '2022-09-30 12:15:15', '2022-09-30 12:15:15');
COMMIT;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键 ',
  `name` varchar(50) DEFAULT '' COMMENT '类型名称',
  `code` varchar(50) DEFAULT '' COMMENT '类型编码',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态（0-正常 ,1-停用）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `type_code` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1000018 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='字典类型表';

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
BEGIN;
INSERT INTO `sys_dict` VALUES (2, 'Authorization way', 'grant_type', 1, NULL, '2020-10-17 16:09:50', '2021-01-31 17:48:24');
INSERT INTO `sys_dict` VALUES (3, 'admin微服务', 'admin_saas', 1, NULL, '2021-06-17 08:13:43', '2022-10-01 19:14:32');
INSERT INTO `sys_dict` VALUES (4, 'Requeset type', 'request_method', 1, NULL, '2021-06-17 08:18:07', '2021-06-17 08:18:07');
INSERT INTO `sys_dict` VALUES (1000013, '广场类型', 'ground_type', 1, NULL, '2022-10-01 19:15:09', '2022-10-01 19:15:09');
INSERT INTO `sys_dict` VALUES (1000014, 'hiyuan_oss', 'hiyuan', 1, NULL, '2022-10-01 19:15:57', '2022-10-01 19:15:57');
INSERT INTO `sys_dict` VALUES (1000015, '钱包类型', 'wallet_type', 1, NULL, '2022-10-01 19:26:10', '2022-10-01 19:26:10');
INSERT INTO `sys_dict` VALUES (1000016, '商品类型', 'goods_type', 1, NULL, '2022-10-01 23:09:16', '2022-10-01 23:09:16');
INSERT INTO `sys_dict` VALUES (1000017, '苹果价格配表', 'apple_price', 1, NULL, '2022-10-03 17:37:05', '2022-10-03 17:37:21');
COMMIT;

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) DEFAULT '' COMMENT '字典项名称',
  `value` varchar(50) DEFAULT '' COMMENT '字典项值',
  `dict_code` varchar(50) DEFAULT '' COMMENT '字典编码',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态（0 停用 1正常）',
  `defaulted` tinyint(1) DEFAULT '0' COMMENT '是否默认（0否 1是）',
  `remark` varchar(255) DEFAULT '' COMMENT '备注',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='字典数据表';

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
BEGIN;
INSERT INTO `sys_dict_item` VALUES (6, 'password', 'password', 'grant_type', 1, 1, 0, NULL, '2020-10-17 17:11:52', '2021-01-31 17:48:18');
INSERT INTO `sys_dict_item` VALUES (7, 'authorization', 'authorization_code', 'grant_type', 1, 1, 0, NULL, '2020-10-17 17:12:15', '2020-12-14 18:11:00');
INSERT INTO `sys_dict_item` VALUES (8, 'client', 'client_credentials', 'grant_type', 1, 1, 0, NULL, '2020-10-17 17:12:36', '2020-12-14 18:11:00');
INSERT INTO `sys_dict_item` VALUES (9, 'refresh', 'refresh_token', 'grant_type', 1, 1, 0, NULL, '2020-10-17 17:12:57', '2021-01-09 01:33:12');
INSERT INTO `sys_dict_item` VALUES (10, 'inplicit', 'implicit', 'grant_type', 1, 1, 0, NULL, '2020-10-17 17:13:23', '2020-12-14 18:11:00');
INSERT INTO `sys_dict_item` VALUES (16, 'unlimited', '*', 'request_method', 1, 1, 0, NULL, '2021-06-17 08:18:34', '2021-06-17 08:18:34');
INSERT INTO `sys_dict_item` VALUES (17, 'GET', 'GET', 'request_method', 2, 1, 0, NULL, '2021-06-17 00:18:55', '2021-06-17 00:18:55');
INSERT INTO `sys_dict_item` VALUES (18, 'POST', 'POST', 'request_method', 3, 1, 0, NULL, '2021-06-17 00:19:06', '2021-06-17 00:19:06');
INSERT INTO `sys_dict_item` VALUES (19, 'PUT', 'PUT', 'request_method', 4, 1, 0, NULL, '2021-06-17 00:19:17', '2021-06-17 00:19:17');
INSERT INTO `sys_dict_item` VALUES (20, 'DELETE', 'DELETE', 'request_method', 5, 1, 0, NULL, '2021-06-17 00:19:30', '2021-06-17 00:19:30');
INSERT INTO `sys_dict_item` VALUES (21, 'PATCH', 'PATCH', 'request_method', 6, 1, 0, NULL, '2021-06-17 00:19:42', '2021-06-17 00:19:42');
INSERT INTO `sys_dict_item` VALUES (52, 'NFT广场', '0', 'ground_type', 1, 1, 0, '', '2022-10-01 19:16:10', '2022-10-03 16:51:29');
INSERT INTO `sys_dict_item` VALUES (53, '全局服务', 'global', 'admin_saas', 1, 1, 0, '', '2022-10-01 19:17:57', '2022-10-01 19:17:57');
INSERT INTO `sys_dict_item` VALUES (54, '商品服务', 'mall-pms', 'admin_saas', 1, 1, 0, '', '2022-10-01 19:18:45', '2022-10-01 19:18:45');
INSERT INTO `sys_dict_item` VALUES (55, '用户服务', 'mall-ums', 'admin_saas', 1, 1, 0, '', '2022-10-01 19:19:07', '2022-10-02 21:37:23');
INSERT INTO `sys_dict_item` VALUES (56, '系统服务', 'admin', 'admin_saas', 1, 1, 0, '', '2022-10-01 19:19:31', '2022-10-01 19:19:31');
INSERT INTO `sys_dict_item` VALUES (57, '钱包服务', 'mall-wms', 'admin_saas', 1, 1, 0, '', '2022-10-01 19:19:57', '2022-10-01 19:19:57');
INSERT INTO `sys_dict_item` VALUES (58, '认证服务', 'auth', 'admin_saas', 1, 1, 0, '', '2022-10-01 19:21:19', '2022-10-01 19:21:19');
INSERT INTO `sys_dict_item` VALUES (59, '盲盒广场', '1', 'ground_type', 1, 1, 0, '', '2022-10-01 19:22:38', '2022-10-03 16:51:51');
INSERT INTO `sys_dict_item` VALUES (60, '热推广场', '2', 'ground_type', 1, 1, 0, '', '2022-10-01 19:22:45', '2022-10-03 16:51:57');
INSERT INTO `sys_dict_item` VALUES (61, '普通商品广场', '3', 'ground_type', 1, 1, 0, '', '2022-10-01 19:22:56', '2022-10-01 19:22:56');
INSERT INTO `sys_dict_item` VALUES (62, '测试广场', '4', 'ground_type', 1, 1, 0, '', '2022-10-01 19:23:09', '2022-10-03 16:52:08');
INSERT INTO `sys_dict_item` VALUES (63, '全部', '0', 'hiyuan', 1, 1, 0, '', '2022-10-01 19:24:34', '2022-10-01 19:24:34');
INSERT INTO `sys_dict_item` VALUES (64, 'banner', '1', 'hiyuan', 1, 1, 0, '', '2022-10-01 19:24:48', '2022-10-01 19:24:48');
INSERT INTO `sys_dict_item` VALUES (65, 'base', '2', 'hiyuan', 1, 1, 0, '', '2022-10-01 19:24:55', '2022-10-01 19:24:55');
INSERT INTO `sys_dict_item` VALUES (66, 'ent', '3', 'hiyuan', 1, 1, 0, '', '2022-10-01 19:25:00', '2022-10-01 19:25:00');
INSERT INTO `sys_dict_item` VALUES (67, 'hot', '4', 'hiyuan', 1, 1, 0, '', '2022-10-01 19:25:06', '2022-10-01 19:25:06');
INSERT INTO `sys_dict_item` VALUES (68, 'other', '5', 'hiyuan', 1, 1, 0, '', '2022-10-01 19:25:13', '2022-10-01 19:25:13');
INSERT INTO `sys_dict_item` VALUES (69, 'package', '6', 'hiyuan', 1, 1, 0, '', '2022-10-01 19:25:20', '2022-10-01 19:25:20');
INSERT INTO `sys_dict_item` VALUES (70, 'xmeta', '7', 'hiyuan', 1, 1, 0, '', '2022-10-01 19:25:26', '2022-10-01 19:25:26');
INSERT INTO `sys_dict_item` VALUES (71, '建设值', '0', 'wallet_type', 1, 1, 0, '', '2022-10-01 19:26:33', '2022-10-01 19:26:33');
INSERT INTO `sys_dict_item` VALUES (72, '荣誉值', '1', 'wallet_type', 1, 1, 0, '', '2022-10-01 19:26:41', '2022-10-01 19:26:41');
INSERT INTO `sys_dict_item` VALUES (73, '贡献值', '2', 'wallet_type', 1, 1, 0, '', '2022-10-01 19:26:51', '2022-10-03 16:49:29');
INSERT INTO `sys_dict_item` VALUES (74, 'NFT', '0', 'goods_type', 1, 1, 0, '', '2022-10-01 23:10:00', '2022-10-01 23:10:00');
INSERT INTO `sys_dict_item` VALUES (75, '盲盒', '1', 'goods_type', 1, 1, 0, '', '2022-10-01 23:10:09', '2022-10-01 23:10:09');
INSERT INTO `sys_dict_item` VALUES (76, '普通商品', '2', 'goods_type', 1, 1, 0, '', '2022-10-01 23:10:17', '2022-10-01 23:10:17');
INSERT INTO `sys_dict_item` VALUES (78, '1', '1', '1', 0, 0, 0, '', '2022-10-02 14:58:57', '2022-10-02 14:59:24');
INSERT INTO `sys_dict_item` VALUES (79, 'RMB', '4', 'wallet_type', 1, 1, 0, '', '2022-10-03 16:50:09', '2022-10-06 17:04:55');
INSERT INTO `sys_dict_item` VALUES (80, '兑换广场', '5', 'ground_type', 1, 1, 0, '', '2022-10-03 16:52:17', '2022-10-03 16:52:17');
INSERT INTO `sys_dict_item` VALUES (81, 'apple1', 'com.ywzm1', 'apple_price', 1, 1, 0, '', '2022-10-03 17:37:53', '2022-10-03 17:38:20');
INSERT INTO `sys_dict_item` VALUES (82, 'apple3', 'com.ywzm3', 'apple_price', 1, 1, 0, '', '2022-10-03 17:38:14', '2022-10-03 17:38:14');
INSERT INTO `sys_dict_item` VALUES (83, '工作包', '3', 'goods_type', 1, 1, 0, '', '2022-10-04 19:15:47', '2022-10-04 19:15:47');
INSERT INTO `sys_dict_item` VALUES (84, '物品服务', 'mall-bms', 'admin_saas', 1, 1, 0, '', '2022-10-04 20:54:10', '2022-10-04 20:54:10');
INSERT INTO `sys_dict_item` VALUES (85, '农场服务', 'mall-farm', 'admin_saas', 1, 1, 0, '', '2022-10-04 20:54:43', '2022-10-04 20:54:43');
INSERT INTO `sys_dict_item` VALUES (87, '基础工作包广场', '6', 'ground_type', 1, 1, 0, '', '2022-10-05 19:32:33', '2022-10-05 19:32:33');
INSERT INTO `sys_dict_item` VALUES (88, '赠送工作包广场', '7', 'ground_type', 1, 1, 0, '', '2022-10-05 19:32:46', '2022-10-05 19:32:46');
INSERT INTO `sys_dict_item` VALUES (89, '星元值', '3', 'wallet_type', 1, 1, 0, '', '2022-10-07 17:16:06', '2022-10-07 23:54:26');
INSERT INTO `sys_dict_item` VALUES (90, '体验工作包【权益币】', '100', 'wallet_type', 1, 1, 0, '', '2022-10-08 13:11:06', '2022-10-08 13:11:06');
INSERT INTO `sys_dict_item` VALUES (91, '实习工作包【权益币】', '101', 'wallet_type', 1, 1, 0, '', '2022-10-08 13:11:16', '2022-10-08 13:11:16');
INSERT INTO `sys_dict_item` VALUES (92, '初级工作包【权益币】', '102', 'wallet_type', 1, 1, 0, '', '2022-10-08 13:11:27', '2022-10-08 13:11:27');
INSERT INTO `sys_dict_item` VALUES (93, '中级工作包【权益币】', '103', 'wallet_type', 1, 1, 0, '', '2022-10-08 13:11:37', '2022-10-08 13:11:37');
INSERT INTO `sys_dict_item` VALUES (94, '高级工作包【权益币】', '104', 'wallet_type', 1, 1, 0, '', '2022-10-08 13:11:46', '2022-10-08 13:11:46');
INSERT INTO `sys_dict_item` VALUES (95, '专家工作包【权益币】', '105', 'wallet_type', 1, 1, 0, '', '2022-10-08 13:11:57', '2022-10-08 13:11:57');
INSERT INTO `sys_dict_item` VALUES (96, '资深工作包【权益币】', '106', 'wallet_type', 1, 1, 0, '', '2022-10-08 13:12:07', '2022-10-08 13:12:07');
INSERT INTO `sys_dict_item` VALUES (97, '元碎', '9999', 'wallet_type', 1, 1, 0, '', '2022-10-10 12:57:38', '2022-10-10 12:57:38');
INSERT INTO `sys_dict_item` VALUES (98, '学院资源广场', '100', 'ground_type', 1, 1, 0, '', '2022-10-22 19:46:32', '2022-10-22 19:46:32');
COMMIT;

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `log_type` int(2) DEFAULT NULL COMMENT '日志类型（1登录日志，2操作日志）',
  `log_content` varchar(1000) DEFAULT NULL COMMENT '日志内容',
  `operate_type` varchar(50) DEFAULT NULL COMMENT '操作类型',
  `userid` bigint(20) DEFAULT NULL COMMENT '操作用户账号',
  `username` varchar(100) DEFAULT NULL COMMENT '操作用户名称',
  `ip` varchar(100) DEFAULT NULL COMMENT 'IP',
  `method` varchar(500) DEFAULT NULL COMMENT '请求java方法',
  `request_url` varchar(255) DEFAULT NULL COMMENT '请求路径',
  `request_param` longtext COMMENT '请求参数',
  `request_type` varchar(10) DEFAULT NULL COMMENT '请求类型',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL,
  `cost_time` bigint(20) DEFAULT NULL COMMENT '耗时',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_table_userid` (`userid`) USING BTREE,
  KEY `index_logt_ype` (`log_type`) USING BTREE,
  KEY `index_operate_type` (`operate_type`) USING BTREE,
  KEY `index_log_type` (`log_type`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='系统日志表';

-- ----------------------------
-- Records of sys_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT '' COMMENT '菜单名称',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父菜单ID',
  `path` varchar(128) DEFAULT '' COMMENT '路由路径',
  `component` varchar(128) DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(64) DEFAULT '' COMMENT '菜单图标',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `visible` tinyint(1) DEFAULT '1' COMMENT '状态：0-禁用 1-开启',
  `redirect` varchar(128) DEFAULT '' COMMENT '跳转路径',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='菜单管理';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_oauth_client
-- ----------------------------
DROP TABLE IF EXISTS `sys_oauth_client`;
CREATE TABLE `sys_oauth_client` (
  `client_id` varchar(100) NOT NULL,
  `resource_ids` varchar(256) DEFAULT NULL,
  `client_secret` varchar(256) DEFAULT NULL,
  `scope` varchar(256) DEFAULT NULL,
  `authorized_grant_types` varchar(256) DEFAULT NULL,
  `web_server_redirect_uri` varchar(256) DEFAULT NULL,
  `authorities` varchar(256) DEFAULT NULL,
  `access_token_validity` int(11) DEFAULT NULL,
  `refresh_token_validity` int(11) DEFAULT NULL,
  `additional_information` varchar(4096) DEFAULT NULL,
  `autoapprove` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`client_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of sys_oauth_client
-- ----------------------------
BEGIN;
INSERT INTO `sys_oauth_client` VALUES ('client', '', '123456', 'all', 'password,refresh_token', NULL, NULL, 360000, 7200, NULL, 'true');
INSERT INTO `sys_oauth_client` VALUES ('mall-admin', '', '123456', 'all', 'password,client_credentials,refresh_token,authorization_code', NULL, '', 360000, 7200, NULL, 'true');
INSERT INTO `sys_oauth_client` VALUES ('mall-admin-web', '', '123456', 'all', 'password,sms_code,refresh_token,captcha', NULL, '', 360000, 7200, NULL, 'true');
INSERT INTO `sys_oauth_client` VALUES ('mall-app', '', '123456', 'all', 'username,password,sms_code,refresh_token', NULL, NULL, 360000, 7200, NULL, 'true');
INSERT INTO `sys_oauth_client` VALUES ('mall-weapp', '', '123456', 'all', 'wechat,refresh_token', NULL, NULL, 360000, 7200, NULL, 'true');
INSERT INTO `sys_oauth_client` VALUES ('mall-web', '', '123456', 'all', 'username,sms_code,authorization_code,password,refresh_token,implicit', NULL, NULL, 360000, 7200, NULL, 'true');
COMMIT;

-- ----------------------------
-- Table structure for sys_path_rule
-- ----------------------------
DROP TABLE IF EXISTS `sys_path_rule`;
CREATE TABLE `sys_path_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `path` text COMMENT '路径',
  `type` bigint(1) NOT NULL COMMENT '放行类型',
  `value` text COMMENT '值',
  `remark` varchar(255) DEFAULT NULL,
  `created` datetime DEFAULT NULL COMMENT '更新时间',
  `updated` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`,`type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='路径限制访问规则';

-- ----------------------------
-- Records of sys_path_rule
-- ----------------------------
BEGIN;
INSERT INTO `sys_path_rule` VALUES (1, '注册', '/mall-ums/app-api/v1/account/register', 0, '1', '鄙', '2022-09-27 21:50:34', '2022-10-03 12:01:43');
INSERT INTO `sys_path_rule` VALUES (2, '开启任务', '/mall-farm/app-api/v1/farm/open', 2, '6:00:00_23:59:59', '', '2022-10-13 11:36:21', '2022-10-15 11:15:43');
INSERT INTO `sys_path_rule` VALUES (3, '收获奖励', '/mall-farm/app-api/v1/farm/claim', 2, '6:00:00_23:59:59', '', '2022-10-13 11:36:52', '2022-10-13 13:21:09');
COMMIT;

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) DEFAULT NULL COMMENT '权限名称',
  `menu_id` int(11) DEFAULT NULL COMMENT '菜单模块ID\r\n',
  `url_perm` varchar(128) DEFAULT NULL COMMENT 'URL权限标识',
  `btn_perm` varchar(64) DEFAULT NULL COMMENT '按钮权限标识',
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `id` (`id`,`name`) USING BTREE,
  KEY `id_2` (`id`,`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='权限表';

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '角色名称',
  `code` varchar(32) DEFAULT NULL COMMENT '角色编码',
  `sort` int(11) DEFAULT NULL COMMENT '显示顺序',
  `status` tinyint(1) DEFAULT '1' COMMENT '角色状态：0-正常；1-停用',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识：0-未删除；1-已删除',
  `created` datetime DEFAULT NULL COMMENT '更新时间',
  `updated` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` VALUES (1, '管理员', 'Admin', NULL, 1, 0, '2022-10-01 14:11:46', '2022-10-01 14:11:46');
INSERT INTO `sys_role` VALUES (2, '运维', 'Op', NULL, 1, 0, '2022-10-01 14:11:57', '2022-10-01 14:11:57');
INSERT INTO `sys_role` VALUES (3, '客人', 'Guest', NULL, 1, 0, '2022-10-01 14:12:08', '2022-10-01 14:12:08');
INSERT INTO `sys_role` VALUES (4, '开发', 'Develop', NULL, 1, 0, '2022-10-01 14:12:19', '2022-10-01 14:12:19');
INSERT INTO `sys_role` VALUES (5, '商务', 'Business', NULL, 1, 0, '2022-10-01 14:12:35', '2022-10-01 14:12:35');
COMMIT;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色和菜单关联表';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `role_id` int(11) NOT NULL COMMENT '角色id',
  `permission_id` int(11) NOT NULL COMMENT '资源id',
  PRIMARY KEY (`role_id`,`permission_id`),
  KEY `role_id` (`role_id`) USING BTREE,
  KEY `permission_id` (`permission_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色权限表';

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL COMMENT '用户名',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `gender` tinyint(1) DEFAULT '1' COMMENT '性别：1-男 2-女',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `avatar` varchar(255) DEFAULT '' COMMENT '用户头像',
  `mobile` varchar(20) DEFAULT NULL COMMENT '联系方式',
  `status` tinyint(1) DEFAULT '1' COMMENT '用户状态：1-正常 0-禁用',
  `email` varchar(128) DEFAULT NULL COMMENT '用户邮箱',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识：0-未删除；1-已删除',
  `created` datetime DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `login_name` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户信息表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` VALUES (1, 'root', 'System manager', 0, '$2a$10$ETQoS1AUlbAx0MkU4e0Zv.47PuDmuB1NOVbbjaldxohQc1JqGVuEO', 1, '', '', 1, '', 0, NULL, '2022-01-10 05:01:30');
INSERT INTO `sys_user` VALUES (2, 'admin', 'System manager', 1, '$2a$10$ETQoS1AUlbAx0MkU4e0Zv.47PuDmuB1NOVbbjaldxohQc1JqGVuEO', 2, 'https://gitee.com/haoxr/image/raw/master/20210605215800.png', '', 1, 'mulingtech@163.com', 0, '2019-10-11 05:41:22', '2021-06-07 15:41:35');
INSERT INTO `sys_user` VALUES (3, 'dev01', 'dev01', 1, '$2a$10$x1LjxGDjkgfgz2BWHTeUduJT9vUGx1AnI0mij3q5uYAZU3mp0zmQG', 2, '', '', 1, '', 0, '2022-10-01 14:15:00', '2022-10-01 14:15:00');
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `role_id` int(11) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户和角色关联表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
