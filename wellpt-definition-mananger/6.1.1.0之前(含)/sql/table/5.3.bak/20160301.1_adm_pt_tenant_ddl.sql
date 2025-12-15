-- Create table
create table MT_TENANT_TEMPLATE
(
  UUID           VARCHAR2(255) not null,
  CREATE_TIME    TIMESTAMP(6),
  CREATOR        VARCHAR2(255),
  MODIFY_TIME    TIMESTAMP(6) ,
  MODIFIER       VARCHAR2(255),
  REC_VER        NUMBER(10),
  NAME           VARCHAR2(255),
  DDL_FILE_NAMES VARCHAR2(4000),
  DDL_FILE_UUIDS VARCHAR2(4000),
  DML_FILE_NAMES VARCHAR2(4000),
  DML_FILE_UUIDS VARCHAR2(4000),
  REMARK         VARCHAR2(255) 
)
tablespace OA_DATA
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Add comments to the columns 
comment on column MT_TENANT_TEMPLATE.UUID
  is 'uuid，系统字段';
comment on column MT_TENANT_TEMPLATE.CREATE_TIME
  is '创建时间';
comment on column MT_TENANT_TEMPLATE.CREATOR
  is '创建人';
comment on column MT_TENANT_TEMPLATE.MODIFY_TIME
  is '修改时间';
comment on column MT_TENANT_TEMPLATE.MODIFIER
  is '修改人';
comment on column MT_TENANT_TEMPLATE.REC_VER
  is '版本号';
comment on column MT_TENANT_TEMPLATE.NAME
  is '名称';
comment on column MT_TENANT_TEMPLATE.DDL_FILE_NAMES
  is 'DDL文件名列表，多个以分号隔开';
comment on column MT_TENANT_TEMPLATE.DDL_FILE_UUIDS
  is 'DDL文件id列表，多个以分号隔开';
comment on column MT_TENANT_TEMPLATE.DML_FILE_NAMES
  is 'DML文件名列表，多个以分号隔开';
comment on column MT_TENANT_TEMPLATE.DML_FILE_UUIDS
  is 'DML文件id列表，多个以分号隔开';
comment on column MT_TENANT_TEMPLATE.REMARK
  is '备注';
-- Create/Recreate primary, unique and foreign key constraints 
alter table MT_TENANT_TEMPLATE
  add constraint MT_TENANT_TEMPLATE_UUID primary key (UUID)
  using index 
  tablespace OA_DATA
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
