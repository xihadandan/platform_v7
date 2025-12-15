CREATE TABLE "CD_USER_PREFERENCES" 
   (
	"UUID" VARCHAR2(255 CHAR) PRIMARY KEY, 
	"CREATOR" VARCHAR2(255 CHAR), 
	"CREATE_TIME" TIMESTAMP (6), 
	"MODIFIER" VARCHAR2(255 CHAR), 
	"MODIFY_TIME" TIMESTAMP (6), 
	"REC_VER" NUMBER(10,0),
	"MODULE_ID" VARCHAR2(100 CHAR) NOT NULL, 
	"FUNCTION_ID" VARCHAR2(100 CHAR), 
	"USER_ID" VARCHAR2(64 CHAR) NOT NULL, 
	"DATA_KEY" VARCHAR2(100 CHAR) NOT NULL, 
	"DATA_VALUE" CLOB,
	"REMARK" VARCHAR2(200 CHAR) NOT NULL
   );
   
-- Add comments to the table 
comment on table CD_USER_PREFERENCES
  is '用户偏好设置表';
  
-- Add comments to the columns 
comment on column CD_USER_PREFERENCES.uuid
  is 'UUID，系统字段';
comment on column CD_USER_PREFERENCES.create_time
  is '创建时间';
comment on column CD_USER_PREFERENCES.creator
  is '创建人';
comment on column CD_USER_PREFERENCES.modifier
  is '修改人';
comment on column CD_USER_PREFERENCES.modify_time
  is '修改时间';
comment on column CD_USER_PREFERENCES.rec_ver
  is '版本号';
comment on column CD_USER_PREFERENCES.module_id
  is '模块ID';
comment on column CD_USER_PREFERENCES.function_id
  is '功能ID';
comment on column CD_USER_PREFERENCES.user_id
  is '用户ID';
comment on column CD_USER_PREFERENCES.data_key
  is '数据键';
comment on column CD_USER_PREFERENCES.data_value
  is '数据值';
comment on column CD_USER_PREFERENCES.remark
  is '备注';