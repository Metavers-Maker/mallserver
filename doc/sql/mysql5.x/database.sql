/*
* 沐灵商城数据库
* MySQL5.x版本
*/
-- ----------------------------
-- 系统管理数据库
-- ----------------------------
CREATE DATABASE IF	NOT EXISTS mall DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
-- ----------------------------
-- 商城会员数据库
-- ----------------------------
CREATE DATABASE IF	NOT EXISTS mall_ums DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
-- ----------------------------
-- 商城商品数据库
-- ----------------------------
CREATE DATABASE IF	NOT EXISTS mall_pms DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
-- ----------------------------
-- 商城订单数据库
-- ----------------------------
CREATE DATABASE IF	NOT EXISTS mall_oms DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
-- ----------------------------
-- 商城营销数据库
-- ----------------------------
CREATE DATABASE IF	NOT EXISTS mall_im DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

CREATE DATABASE IF	NOT EXISTS mall_card DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

CREATE DATABASE IF	NOT EXISTS mall_seata DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

CREATE DATABASE IF	NOT EXISTS mall_xxl_job DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;