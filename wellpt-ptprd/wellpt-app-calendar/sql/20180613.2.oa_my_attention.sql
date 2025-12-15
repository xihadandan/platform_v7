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

Date: 2018-07-09 10:52:47
*/


-- ----------------------------
-- Table structure for OA_MY_ATTENTION
-- ----------------------------
CREATE TABLE "OA_MY_ATTENTION" (
"UUID" VARCHAR2(255 CHAR) NOT NULL ,
"CREATOR" VARCHAR2(255 CHAR) NULL ,
"CREATE_TIME" TIMESTAMP(6)  NULL ,
"MODIFIER" VARCHAR2(255 CHAR) NULL ,
"MODIFY_TIME" TIMESTAMP(6)  NULL ,
"REC_VER" NUMBER(10) NULL ,
"ATTENTION_OBJ_ID" VARCHAR2(255 CHAR) NULL ,
"ATTENTION_OBJ_TYPE" VARCHAR2(3 BYTE) NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;
COMMENT ON COLUMN "OA_MY_ATTENTION"."ATTENTION_OBJ_ID" IS '被关注的对象ID';
COMMENT ON COLUMN "OA_MY_ATTENTION"."ATTENTION_OBJ_TYPE" IS '被关注的对象类型, U:用户，G：群组';

-- ----------------------------
-- Indexes structure for table OA_MY_ATTENTION
-- ----------------------------
CREATE INDEX "idx_attention_creator"
ON "OA_MY_ATTENTION" ("CREATOR" ASC, "ATTENTION_OBJ_TYPE" ASC)
LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table OA_MY_ATTENTION
-- ----------------------------
ALTER TABLE "OA_MY_ATTENTION" ADD CHECK ("UUID" IS NOT NULL);
ALTER TABLE "OA_MY_ATTENTION" ADD CHECK ("UUID" IS NOT NULL);
ALTER TABLE "OA_MY_ATTENTION" ADD CHECK ("UUID" IS NOT NULL);
ALTER TABLE "OA_MY_ATTENTION" ADD CHECK ("UUID" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table OA_MY_ATTENTION
-- ----------------------------
ALTER TABLE "OA_MY_ATTENTION" ADD PRIMARY KEY ("UUID");
