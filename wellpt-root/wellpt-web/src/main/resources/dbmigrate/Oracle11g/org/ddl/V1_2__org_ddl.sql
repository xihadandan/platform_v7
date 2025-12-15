-- Create table
create table ORG_CORPORATION
(
  UUID               VARCHAR(64) not null,
  CREATE_TIME        TIMESTAMP,
  CREATOR            VARCHAR(64),
  MODIFY_TIME        TIMESTAMP,
  MODIFIER           VARCHAR(64),
  REC_VER            NUMBER(10),
  NAME               VARCHAR(64),
  SHORT_NAME         VARCHAR(64),
  ID                 VARCHAR(16),
  CODE               VARCHAR(64),
  PARENT_UUID        VARCHAR(64),
  PARENT_NAME        VARCHAR(64),
  TENANT_ID          VARCHAR(64),
  REGISTER_DATE      VARCHAR(64),
  REGISTERED_CAPITAL NUMBER(10,6),
  REPRESENTATIVE     VARCHAR(64),
  EMAIL              VARCHAR(64),
  FAX                VARCHAR(64),
  TELEPHONE          VARCHAR(64),
  ADDRESS            VARCHAR(64),
  REMARK             VARCHAR(64)
)
;
-- Add comments to the columns 
comment on column ORG_CORPORATION.UUID
  is 'UUID，系统字段';
comment on column ORG_CORPORATION.CREATE_TIME
  is '创建时间';
comment on column ORG_CORPORATION.CREATOR
  is '创建人';
comment on column ORG_CORPORATION.MODIFY_TIME
  is '修改时间';
comment on column ORG_CORPORATION.MODIFIER
  is '修改人';
comment on column ORG_CORPORATION.REC_VER
  is '版本号';
comment on column ORG_CORPORATION.NAME
  is '机构名称';
comment on column ORG_CORPORATION.SHORT_NAME
  is '机构简称';
comment on column ORG_CORPORATION.ID
  is 'ID, 由C+10位递增数字组成';
comment on column ORG_CORPORATION.CODE
  is '机构编码，营业执照代码';
comment on column ORG_CORPORATION.PARENT_UUID
  is '上级机构UUID';
comment on column ORG_CORPORATION.PARENT_NAME
  is '上级机构名称，冗余字段';
comment on column ORG_CORPORATION.TENANT_ID
  is '机构所在的租户ID';
comment on column ORG_CORPORATION.REGISTER_DATE
  is '成立日期，格式(YYYY-MM-DD)';
comment on column ORG_CORPORATION.REGISTERED_CAPITAL
  is '注册资本，以万为单位';
comment on column ORG_CORPORATION.REPRESENTATIVE
  is '法人代表';
comment on column ORG_CORPORATION.EMAIL
  is '机构邮件';
comment on column ORG_CORPORATION.FAX
  is '机构传真';
comment on column ORG_CORPORATION.TELEPHONE
  is '联系电话';
comment on column ORG_CORPORATION.ADDRESS
  is '联系地址';
comment on column ORG_CORPORATION.REMARK
  is '备注';
-- Create/Recreate primary, unique and foreign key constraints 
alter table ORG_CORPORATION
  add constraint ORG_CORPORATION_UUID primary key (UUID);
  
  
  
  
  
  
  
-- Create table
create table CD_FIELD_EXT_DEFINITION
(
  UUID             VARCHAR(64) not null,
  CREATE_TIME      TIMESTAMP,
  CREATOR          VARCHAR(64),
  MODIFY_TIME      TIMESTAMP,
  MODIFIER         VARCHAR(64),
  REC_VER          NUMBER(10),
  NAME             VARCHAR(64),
  FIELD_NAME       VARCHAR(64),
  DEFAULT_VALUE    VARCHAR(64),
  SORT_ORDER       NUMBER(10),
  ENABLED          NUMBER(1),
  GROUP_CODE       VARCHAR(64),
  INPUT_TYPE       VARCHAR(32),
  CFG_KEY          VARCHAR(64),
  CFG_KEY_NAME     VARCHAR(64),
  DATE_FORMAT      VARCHAR(32),
  VALIDATION_RULE  VARCHAR(64),
  CONSTRAINT_VALUE VARCHAR(64)
)
;
-- Add comments to the columns 
comment on column CD_FIELD_EXT_DEFINITION.UUID
  is 'UUID，系统字段';
comment on column CD_FIELD_EXT_DEFINITION.CREATE_TIME
  is '创建时间';
comment on column CD_FIELD_EXT_DEFINITION.CREATOR
  is '创建人';
comment on column CD_FIELD_EXT_DEFINITION.MODIFY_TIME
  is '修改时间';
comment on column CD_FIELD_EXT_DEFINITION.MODIFIER
  is '修改人';
comment on column CD_FIELD_EXT_DEFINITION.REC_VER
  is '版本号';
comment on column CD_FIELD_EXT_DEFINITION.NAME
  is '显示名称';
comment on column CD_FIELD_EXT_DEFINITION.FIELD_NAME
  is '字段名，由字母、数字、下划线组成';
comment on column CD_FIELD_EXT_DEFINITION.DEFAULT_VALUE
  is '默认值';
comment on column CD_FIELD_EXT_DEFINITION.SORT_ORDER
  is '排序号，字段解析展示的顺序';
comment on column CD_FIELD_EXT_DEFINITION.ENABLED
  is '是否启用，TRUE启用、FALSE禁用';
comment on column CD_FIELD_EXT_DEFINITION.GROUP_CODE
  is '级别代码';
comment on column CD_FIELD_EXT_DEFINITION.INPUT_TYPE
  is 'INPUT控件类型，包含单行文本、多行文本、单选框、复选框、下拉框、日期';
comment on column CD_FIELD_EXT_DEFINITION.CFG_KEY
  is '配置的字典类型';
comment on column CD_FIELD_EXT_DEFINITION.CFG_KEY_NAME
  is '配置的字典名称，冗余字段';
comment on column CD_FIELD_EXT_DEFINITION.DATE_FORMAT
  is '日期格式';
comment on column CD_FIELD_EXT_DEFINITION.VALIDATION_RULE
  is '验证规则';
comment on column CD_FIELD_EXT_DEFINITION.CONSTRAINT_VALUE
  is '验证规则的约束值';
-- Create/Recreate primary, unique and foreign key constraints 
alter table CD_FIELD_EXT_DEFINITION
  add constraint CD_FIELD_EXT_DEFINITION_UUID primary key (UUID);
  
  
  
  
  
  
  
  
-- Create table
create table CD_FIELD_EXT_VALUE
(
  UUID               VARCHAR(64) not null ,
  CREATE_TIME        TIMESTAMP ,
  CREATOR            VARCHAR(64) ,
  MODIFY_TIME        TIMESTAMP ,
  MODIFIER           VARCHAR(64) ,
  REC_VER            NUMBER(10) ,
  FIELD_EXT_DEF_UUID VARCHAR(64) ,
  GROUP_CODE         VARCHAR(64) ,
  DATA_UUID          VARCHAR(64) ,
  FIELD_EXT_VALUE_1  VARCHAR(64) ,
  FIELD_EXT_VALUE_2  VARCHAR(64) ,
  FIELD_EXT_VALUE_3  VARCHAR(64) ,
  FIELD_EXT_VALUE_4  VARCHAR(64) ,
  FIELD_EXT_VALUE_5  VARCHAR(64) ,
  FIELD_EXT_VALUE_6  VARCHAR(64) ,
  FIELD_EXT_VALUE_7  VARCHAR(64) ,
  FIELD_EXT_VALUE_8  VARCHAR(64) ,
  FIELD_EXT_VALUE_9  VARCHAR(64) ,
  FIELD_EXT_VALUE_10 VARCHAR(64) ,
  FIELD_EXT_VALUE_11 VARCHAR(64) ,
  FIELD_EXT_VALUE_12 VARCHAR(64) ,
  FIELD_EXT_VALUE_13 VARCHAR(64) ,
  FIELD_EXT_VALUE_14 VARCHAR(64) ,
  FIELD_EXT_VALUE_15 VARCHAR(64) ,
  FIELD_EXT_VALUE_16 VARCHAR(64) ,
  FIELD_EXT_VALUE_17 VARCHAR(64) ,
  FIELD_EXT_VALUE_18 VARCHAR(64) ,
  FIELD_EXT_VALUE_19 VARCHAR(64) ,
  FIELD_EXT_VALUE_20 VARCHAR(64) ,
  FIELD_EXT_VALUE_21 VARCHAR(64) ,
  FIELD_EXT_VALUE_22 VARCHAR(64) ,
  FIELD_EXT_VALUE_23 VARCHAR(64) ,
  FIELD_EXT_VALUE_24 VARCHAR(64) ,
  FIELD_EXT_VALUE_25 VARCHAR(64) ,
  FIELD_EXT_VALUE_26 VARCHAR(64) ,
  FIELD_EXT_VALUE_27 VARCHAR(64) ,
  FIELD_EXT_VALUE_28 VARCHAR(64) ,
  FIELD_EXT_VALUE_29 VARCHAR(64) ,
  FIELD_EXT_VALUE_30 VARCHAR(64) 
)
;
-- Add comments to the columns 
comment on column CD_FIELD_EXT_VALUE.UUID
  is 'UUID，系统字段';
comment on column CD_FIELD_EXT_VALUE.CREATE_TIME
  is '创建时间';
comment on column CD_FIELD_EXT_VALUE.CREATOR
  is '创建人';
comment on column CD_FIELD_EXT_VALUE.MODIFY_TIME
  is '修改时间';
comment on column CD_FIELD_EXT_VALUE.MODIFIER
  is '修改人';
comment on column CD_FIELD_EXT_VALUE.REC_VER
  is '版本号';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_DEF_UUID
  is '字段扩展定义UUID';
comment on column CD_FIELD_EXT_VALUE.GROUP_CODE
  is '组别代码，冗余字段';
comment on column CD_FIELD_EXT_VALUE.DATA_UUID
  is '数据UUID';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_1
  is '扩展字段1的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_2
  is '扩展字段2的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_3
  is '扩展字段3的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_4
  is '扩展字段4的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_5
  is '扩展字段5的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_6
  is '扩展字段6的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_7
  is '扩展字段7的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_8
  is '扩展字段8的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_9
  is '扩展字段9的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_10
  is '扩展字段10的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_11
  is '扩展字段11的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_12
  is '扩展字段12的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_13
  is '扩展字段13的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_14
  is '扩展字段14的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_15
  is '扩展字段15的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_16
  is '扩展字段16的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_17
  is '扩展字段17的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_18
  is '扩展字段18的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_19
  is '扩展字段19的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_20
  is '扩展字段20的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_21
  is '扩展字段21的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_22
  is '扩展字段22的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_23
  is '扩展字段23的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_24
  is '扩展字段24的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_25
  is '扩展字段25的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_26
  is '扩展字段26的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_27
  is '扩展字段27的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_28
  is '扩展字段28的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_29
  is '扩展字段29的值';
comment on column CD_FIELD_EXT_VALUE.FIELD_EXT_VALUE_30
  is '扩展字段30的值';
-- Create/Recreate primary, unique and foreign key constraints 
alter table CD_FIELD_EXT_VALUE
  add constraint CD_FIELD_EXT_VALUE_UUID primary key (UUID);