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

Date: 2018-07-09 10:52:41
*/


-- ----------------------------
-- Table structure for OA_CALENDAR_GROUP
-- ----------------------------
CREATE TABLE "OA_CALENDAR_GROUP" (
"UUID" VARCHAR2(255 CHAR) NOT NULL ,
"CREATOR" VARCHAR2(255 CHAR) NULL ,
"CREATE_TIME" TIMESTAMP(6)  NULL ,
"MODIFIER" VARCHAR2(255 CHAR) NULL ,
"MODIFY_TIME" TIMESTAMP(6)  NULL ,
"REC_VER" NUMBER(10) NULL ,
"GROUP_NAME" VARCHAR2(255 CHAR) NULL ,
"GROUP_MEMBERS" CLOB NULL ,
"GROUP_MEMBERS_NAME" CLOB NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;
COMMENT ON COLUMN "OA_CALENDAR_GROUP"."GROUP_NAME" IS '组名';
COMMENT ON COLUMN "OA_CALENDAR_GROUP"."GROUP_MEMBERS" IS '组成员列表';
COMMENT ON COLUMN "OA_CALENDAR_GROUP"."GROUP_MEMBERS_NAME" IS '组成员名称';

-- ----------------------------
-- Indexes structure for table OA_CALENDAR_GROUP
-- ----------------------------
CREATE UNIQUE INDEX "idx_calendar_goup"
ON "OA_CALENDAR_GROUP" ("CREATOR" ASC, "GROUP_NAME" ASC)
LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table OA_CALENDAR_GROUP
-- ----------------------------
ALTER TABLE "OA_CALENDAR_GROUP" ADD CHECK ("UUID" IS NOT NULL);
ALTER TABLE "OA_CALENDAR_GROUP" ADD CHECK ("UUID" IS NOT NULL);
ALTER TABLE "OA_CALENDAR_GROUP" ADD CHECK ("UUID" IS NOT NULL);
ALTER TABLE "OA_CALENDAR_GROUP" ADD CHECK ("UUID" IS NOT NULL);
ALTER TABLE "OA_CALENDAR_GROUP" ADD CHECK ("UUID" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table OA_CALENDAR_GROUP
-- ----------------------------
ALTER TABLE "OA_CALENDAR_GROUP" ADD PRIMARY KEY ("UUID");
