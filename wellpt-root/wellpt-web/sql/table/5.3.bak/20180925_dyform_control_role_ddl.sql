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

Date: 2018-09-25 15:24:56
*/


-- ----------------------------
-- Table structure for DYFORM_CONTROL_ROLE
-- ----------------------------
DROP TABLE "DYFORM_CONTROL_ROLE";
CREATE TABLE "DYFORM_CONTROL_ROLE" (
"UUID" VARCHAR2(255 CHAR) NULL ,
"CREATE_TIME" TIMESTAMP(6)  NULL ,
"CREATOR" VARCHAR2(255 CHAR) NULL ,
"MODIFIER" VARCHAR2(255 CHAR) NULL ,
"MODIFY_TIME" TIMESTAMP(6)  NULL ,
"REC_VER" NUMBER(10) NULL ,
"DYFORM_ID" VARCHAR2(255 BYTE) NULL ,
"ROLE_UUID" VARCHAR2(255 BYTE) DEFAULT 0  NULL ,
"CONTROL_NAME" VARCHAR2(255 BYTE) NULL 
)
NOLOGGING
NOCOMPRESS
NOCACHE

;
COMMENT ON TABLE "DYFORM_CONTROL_ROLE" IS '表单控件关联的角色关系表';
COMMENT ON COLUMN "DYFORM_CONTROL_ROLE"."DYFORM_ID" IS '表单ID';
COMMENT ON COLUMN "DYFORM_CONTROL_ROLE"."ROLE_UUID" IS '对应的角色UUID';
COMMENT ON COLUMN "DYFORM_CONTROL_ROLE"."CONTROL_NAME" IS '表单中的控件名称';

-- ----------------------------
-- Indexes structure for table DYFORM_CONTROL_ROLE
-- ----------------------------
CREATE INDEX "idx_elementRole_eleUuid_copy"
ON "DYFORM_CONTROL_ROLE" ("DYFORM_ID" ASC)
LOGGING
VISIBLE;
CREATE INDEX "idx_elementRole_roleUuid_copy"
ON "DYFORM_CONTROL_ROLE" ("ROLE_UUID" ASC)
LOGGING
VISIBLE;
