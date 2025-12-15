-- Create table
create table DMS_DATA_PERMISSION_DEFINITION
(
  uuid                 VARCHAR2(255 CHAR) not null,
  create_time          TIMESTAMP(6),
  creator              VARCHAR2(255 CHAR),
  modify_time          TIMESTAMP(6),
  modifier             VARCHAR2(255 CHAR),
  rec_ver              NUMBER(10),
  system_unit_id     VARCHAR2(12 CHAR) not null,
  name                 VARCHAR2(150 CHAR) not null,
  id                   VARCHAR2(50 CHAR) not null,
  code                 VARCHAR2(50 CHAR),
  type                 VARCHAR2(2 CHAR) not null,
  data_name            VARCHAR2(50 CHAR),
  rule_definition      CLOB,
  range_definition     CLOB,
  remark               VARCHAR2(255 CHAR),
  PRIMARY KEY ("UUID")
);

-- Add comments to the table 
comment on table DMS_DATA_PERMISSION_DEFINITION
  is '数据权限定义';
-- Add comments to the columns 
comment on column DMS_DATA_PERMISSION_DEFINITION.uuid
  is 'UUID';
comment on column DMS_DATA_PERMISSION_DEFINITION.create_time
  is '创建时间';
comment on column DMS_DATA_PERMISSION_DEFINITION.creator
  is '创建人';
comment on column DMS_DATA_PERMISSION_DEFINITION.modify_time
  is '修改时间';
comment on column DMS_DATA_PERMISSION_DEFINITION.modifier
  is '修改人';
comment on column DMS_DATA_PERMISSION_DEFINITION.rec_ver
  is '版本号';
comment on column DMS_DATA_PERMISSION_DEFINITION.system_unit_id
  is '系统单位ID';
comment on column DMS_DATA_PERMISSION_DEFINITION.name
  is '数据权限名称';
comment on column DMS_DATA_PERMISSION_DEFINITION.id
  is 'ID';
comment on column DMS_DATA_PERMISSION_DEFINITION.code
  is '编号';
comment on column DMS_DATA_PERMISSION_DEFINITION.type
  is '数据类型，1数据库表，2数据库视图';
comment on column DMS_DATA_PERMISSION_DEFINITION.data_name
  is '数据名称，数据库表名或数据库视图名';
comment on column DMS_DATA_PERMISSION_DEFINITION.rule_definition
  is '数据规则定义JSON';
comment on column DMS_DATA_PERMISSION_DEFINITION.range_definition
  is '数据范围定义JSON';
comment on column DMS_DATA_PERMISSION_DEFINITION.remark
  is '备注';
