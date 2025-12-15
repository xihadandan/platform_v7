CREATE TABLE "CD_DOCUMENT_LINK" 
   (
	"UUID" VARCHAR2(64 CHAR) PRIMARY KEY, 
	"CREATOR" VARCHAR2(64 CHAR), 
	"CREATE_TIME" TIMESTAMP (6), 
	"MODIFIER" VARCHAR2(64 CHAR), 
	"MODIFY_TIME" TIMESTAMP (6), 
	"REC_VER" NUMBER(10,0),
	"BUSINESS_TYPE" VARCHAR2(100 CHAR) NOT NULL, 
	"ACCESS_STRATEGY" VARCHAR2(2 CHAR), 
	"SOURCE_DATA_UUID" VARCHAR2(64 CHAR) NOT NULL, 
	"SOURCE_DATA_CHECKER" VARCHAR2(100 CHAR), 
	"TARGET_DATA_UUID" VARCHAR2(64 CHAR) NOT NULL, 
	"TARGET_DATA_CHECKER" VARCHAR2(100 CHAR), 
	"TARGET_URL" VARCHAR2(500 CHAR) 
   );
   
-- Add comments to the table 
comment on table CD_DOCUMENT_LINK
  is '文档链接关系表';
  
-- Add comments to the columns 
comment on column CD_DOCUMENT_LINK.uuid
  is 'UUID，系统字段';
comment on column CD_DOCUMENT_LINK.create_time
  is '创建时间';
comment on column CD_DOCUMENT_LINK.creator
  is '创建人';
comment on column CD_DOCUMENT_LINK.modifier
  is '修改人';
comment on column CD_DOCUMENT_LINK.modify_time
  is '修改时间';
comment on column CD_DOCUMENT_LINK.rec_ver
  is '版本号';
comment on column CD_DOCUMENT_LINK.business_type
  is '业务类型';
comment on column CD_DOCUMENT_LINK.access_strategy
  is '访问策略, 0不检验，1检验源数据，2检验目标数据，3任意数据，4全部';
comment on column CD_DOCUMENT_LINK.source_data_uuid
  is '源数据UUID';
comment on column CD_DOCUMENT_LINK.source_data_checker
  is '源数据检验器';
comment on column CD_DOCUMENT_LINK.target_data_uuid
  is '目标数据UUID';
comment on column CD_DOCUMENT_LINK.target_data_checker
  is '目标数据检验器';
comment on column CD_DOCUMENT_LINK.target_url
  is '目标地址';
  