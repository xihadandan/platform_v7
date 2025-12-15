-- Add/modify columns 
alter table DMS_TAG add color VARCHAR2(16 CHAR);
-- Add comments to the columns 
comment on column DMS_TAG.color
  is '颜色';
-- Add/modify columns 
alter table DMS_TAG add owner_id VARCHAR2(16 CHAR);
-- Add comments to the columns 
comment on column DMS_TAG.owner_id
  is '所有者ID';

  
  
CREATE TABLE "DMS_TAG_DATA" 
   (	"UUID" VARCHAR2(255 CHAR) NOT NULL ENABLE, 
	"CREATE_TIME" TIMESTAMP (6), 
	"CREATOR" VARCHAR2(255 CHAR), 
	"MODIFIER" VARCHAR2(255 CHAR), 
	"MODIFY_TIME" TIMESTAMP (6), 
	"REC_VER" NUMBER(10,0), 
	"TAG_UUID" VARCHAR2(255 CHAR), 
	"DATA_UUID" VARCHAR2(255 CHAR), 
	 PRIMARY KEY ("UUID")
   ) ;
 
   COMMENT ON COLUMN "DMS_TAG_DATA"."UUID" IS 'UUID，系统字段';
 
   COMMENT ON COLUMN "DMS_TAG_DATA"."CREATE_TIME" IS '创建时间';
 
   COMMENT ON COLUMN "DMS_TAG_DATA"."CREATOR" IS '创建人';
 
   COMMENT ON COLUMN "DMS_TAG_DATA"."MODIFIER" IS '修改人';
 
   COMMENT ON COLUMN "DMS_TAG_DATA"."MODIFY_TIME" IS '修改时间';
 
   COMMENT ON COLUMN "DMS_TAG_DATA"."REC_VER" IS '版本号';
 
   COMMENT ON COLUMN "DMS_TAG_DATA"."TAG_UUID" IS '标签UUID';
 
   COMMENT ON COLUMN "DMS_TAG_DATA"."DATA_UUID" IS '数据UUID';
   