/*
Navicat MySQL Data Transfer

Source Server         : localhost_mysql
Source Server Version : 50725
Source Host           : localhost:3306
Source Database       : oauth2

Target Server Type    : MYSQL
Target Server Version : 50725
File Encoding         : 65001

Date: 2019-10-19 13:34:53
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for batch_data_import_details_his
-- ----------------------------
DROP TABLE IF EXISTS `batch_data_import_details_his`;
CREATE TABLE `batch_data_import_details_his` (
  `uuid` decimal(20,0) NOT NULL,
  `row_index` int(10) DEFAULT NULL COMMENT '导入行',
  `status` int(1) DEFAULT NULL,
  `batch_data_import_uuid` decimal(20,0) NOT NULL,
  `import_data` text,
  `error_msg` text,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_by` varchar(64) DEFAULT NULL,
  `modify_by` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of batch_data_import_details_his
-- ----------------------------

-- ----------------------------
-- Table structure for batch_data_import_his
-- ----------------------------
DROP TABLE IF EXISTS `batch_data_import_his`;
CREATE TABLE `batch_data_import_his` (
  `uuid` decimal(20,0) NOT NULL,
  `data_import_type` int(1) DEFAULT NULL COMMENT '导入数据类型',
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_by` varchar(64) DEFAULT NULL,
  `modify_by` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of batch_data_import_his
-- ----------------------------

-- ----------------------------
-- Table structure for oauth_access_token
-- ----------------------------
DROP TABLE IF EXISTS `oauth_access_token`;
CREATE TABLE `oauth_access_token` (
  `token_id` varchar(256) DEFAULT NULL,
  `token` blob,
  `authentication_id` varchar(256) NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `client_id` varchar(256) DEFAULT NULL,
  `authentication` blob,
  `refresh_token` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`authentication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- ----------------------------
-- Table structure for oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details` (
  `uuid` decimal(20,0) NOT NULL,
  `client_id` varchar(256) NOT NULL,
  `client_name` varchar(256) DEFAULT NULL,
  `resource_ids` varchar(256) DEFAULT NULL,
  `client_secret` varchar(256) DEFAULT NULL,
  `scope` varchar(256) DEFAULT 'read,write',
  `authorized_grant_types` varchar(256) DEFAULT NULL,
  `web_server_redirect_uri` varchar(256) DEFAULT NULL,
  `authorities` varchar(256) DEFAULT NULL,
  `access_token_validity` int(11) DEFAULT NULL,
  `refresh_token_validity` int(11) DEFAULT NULL,
  `additional_information` varchar(4096) DEFAULT NULL,
  `autoapprove` varchar(256) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_by` varchar(64) DEFAULT NULL,
  `modify_by` varchar(64) DEFAULT NULL,
  `login_page` varchar(256),
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of oauth_client_details
-- ----------------------------

-- ----------------------------
-- Table structure for oauth_client_token
-- ----------------------------
DROP TABLE IF EXISTS `oauth_client_token`;
CREATE TABLE `oauth_client_token` (
  `token_id` varchar(256) DEFAULT NULL,
  `token` blob,
  `authentication_id` varchar(256) NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `client_id` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`authentication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of oauth_client_token
-- ----------------------------

-- ----------------------------
-- Table structure for oauth_code
-- ----------------------------
DROP TABLE IF EXISTS `oauth_code`;
CREATE TABLE `oauth_code` (
  `code` varchar(256) DEFAULT NULL,
  `authentication` blob
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of oauth_code
-- ----------------------------

-- ----------------------------
-- Table structure for oauth_refresh_token
-- ----------------------------
DROP TABLE IF EXISTS `oauth_refresh_token`;
CREATE TABLE `oauth_refresh_token` (
  `token_id` varchar(256) DEFAULT NULL,
  `token` blob,
  `authentication` blob
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of oauth_refresh_token
-- ----------------------------

-- ----------------------------
-- Table structure for user_account
-- ----------------------------
DROP TABLE IF EXISTS `user_account`;
CREATE TABLE `user_account` (
  `uuid` decimal(20,0) NOT NULL,
  `account_number` varchar(64) NOT NULL COMMENT '账号',
  `password` varchar(120) DEFAULT NULL COMMENT '密码',
  `enabled` int(1) DEFAULT '1' COMMENT '是否启用',
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_by` varchar(64) DEFAULT NULL,
  `modify_by` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `idx_unique_actnbr` (`account_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_account
-- ----------------------------
INSERT INTO `user_account` VALUES ('1136554904711593984', 'admin', '$2a$10$7xxw2wHkjG64lI8PocGDh.2gol9eQfjNsc1Alfe4nn0aLvJpL8p1y', '1', '2019-10-19 13:33:37', '2019-10-19 13:33:37', 'anonymousUser', 'anonymousUser');

-- ----------------------------
-- Table structure for user_authorities
-- ----------------------------
DROP TABLE IF EXISTS `user_authorities`;
CREATE TABLE `user_authorities` (
  `uuid` decimal(20,0) NOT NULL,
  `authority` varchar(50) NOT NULL,
  `account_number` varchar(64) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_by` varchar(64) DEFAULT NULL,
  `modify_by` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `IX_AUTH_USERNAME` (`account_number`,`authority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_authorities
-- ----------------------------
INSERT INTO `user_authorities` VALUES ('0', 'ROLE_USER', 'admin', null, null, null, null);
INSERT INTO `user_authorities` VALUES ('1', 'ROLE_ADMIN', 'admin', null, null, null, null);

-- ----------------------------
-- Table structure for user_ext_account
-- ----------------------------
DROP TABLE IF EXISTS `user_ext_account`;
CREATE TABLE `user_ext_account` (
  `uuid` decimal(20,0) NOT NULL,
  `account_uuid` decimal(20,0) NOT NULL,
  `source` varchar(120) NOT NULL,
  `ext_account_number` varchar(120) NOT NULL COMMENT '密码',
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_by` varchar(64) DEFAULT NULL,
  `modify_by` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_ext_account
-- ----------------------------

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `uuid` decimal(20,0) NOT NULL,
  `account_number` varchar(64) NOT NULL,
  `user_name` varchar(120) NOT NULL,
  `identified_code` varchar(64) DEFAULT NULL,
  `gender` int(1) DEFAULT NULL,
  `birth_date` datetime DEFAULT NULL,
  `cellphone_number` varchar(15) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `create_by` varchar(255) DEFAULT NULL,
  `modify_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES ('1136554904711593985', 'admin', '系统管理员', null, null, null, '', '2019-10-19 13:33:37', '2019-10-19 13:33:37', 'anonymousUser', 'anonymousUser');
