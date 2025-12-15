/*
Navicat Oracle Data Transfer
Oracle Client Version : 10.2.0.5.0

Source Server         : OA_6.0_USR_DEV
Source Server Version : 110200
Source Host           : 192.168.0.123:1521
Source Schema         : OA_USR_6_0_DEV

Target Server Type    : ORACLE
Target Server Version : 110200
File Encoding         : 65001

Date: 2018-07-11 14:38:49
*/


-- ----------------------------
-- Table structure for MULTI_ORG_USER_WORK_INFO
-- ----------------------------

CREATE TABLE "MULTI_ORG_USER_WORK_INFO" (
"UUID" VARCHAR2(255 CHAR) NOT NULL ,
"CREATE_TIME" TIMESTAMP(6)  NULL ,
"CREATOR" VARCHAR2(255 CHAR) NULL ,
"MODIFIER" VARCHAR2(255 CHAR) NULL ,
"MODIFY_TIME" TIMESTAMP(6)  NULL ,
"REC_VER" NUMBER(10) NULL ,
"USER_ID" VARCHAR2(255 BYTE) NULL ,
"JOB_IDS" VARCHAR2(4000 BYTE) NULL ,
"DEPT_IDS" VARCHAR2(4000 BYTE) NULL ,
"ELE_ID_PATHS" VARCHAR2(4000 BYTE) NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;
COMMENT ON COLUMN "MULTI_ORG_USER_WORK_INFO"."JOB_IDS" IS '职位id多个用;分隔';
COMMENT ON COLUMN "MULTI_ORG_USER_WORK_INFO"."DEPT_IDS" IS '部门id多个用;分隔';
COMMENT ON COLUMN "MULTI_ORG_USER_WORK_INFO"."ELE_ID_PATHS" IS '节点全路径多个用;分隔';

-- ----------------------------
-- Indexes structure for table MULTI_ORG_USER_WORK_INFO
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table MULTI_ORG_USER_WORK_INFO
-- ----------------------------
ALTER TABLE "MULTI_ORG_USER_WORK_INFO" ADD PRIMARY KEY ("UUID");


ALTER TABLE "MULTI_ORG_USER_WORK_INFO"
ADD ( "SYSTEM_UNIT_ID" VARCHAR2(255) NULL  ) ;

COMMENT ON COLUMN "MULTI_ORG_USER_WORK_INFO"."SYSTEM_UNIT_ID" IS '归属单位ID';


