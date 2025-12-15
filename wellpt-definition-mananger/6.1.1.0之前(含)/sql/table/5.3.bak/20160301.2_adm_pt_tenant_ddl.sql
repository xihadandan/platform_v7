-- Create table
create table MT_TENANT_TEMPLATE_MODULE
(
  UUID            VARCHAR2(255) not null,
  CREATE_TIME     TIMESTAMP(6),
  CREATOR         VARCHAR2(255),
  MODIFY_TIME     TIMESTAMP(6),
  MODIFIER        VARCHAR2(255),
  REC_VER         NUMBER(10),
  NAME            VARCHAR2(255),
  SORT_ORDER      NUMBER(10),
  REPO_FILE_NAMES VARCHAR2(4000),
  REPO_FILE_UUIDS VARCHAR2(4000),
  REMARK          VARCHAR2(4000),
  TEMPLATE_UUID   VARCHAR2(255)
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
comment on column MT_TENANT_TEMPLATE_MODULE.UUID
  is 'uuid，系统字段';
comment on column MT_TENANT_TEMPLATE_MODULE.CREATE_TIME
  is '创建时间';
comment on column MT_TENANT_TEMPLATE_MODULE.CREATOR
  is '创建人';
comment on column MT_TENANT_TEMPLATE_MODULE.MODIFY_TIME
  is '修改时间';
comment on column MT_TENANT_TEMPLATE_MODULE.MODIFIER
  is '修改人';
comment on column MT_TENANT_TEMPLATE_MODULE.REC_VER
  is '版本号';
comment on column MT_TENANT_TEMPLATE_MODULE.NAME
  is '名称';
comment on column MT_TENANT_TEMPLATE_MODULE.SORT_ORDER
  is '排序号，从1开始递增，初始化模块数据按排序号递增初处理';
comment on column MT_TENANT_TEMPLATE_MODULE.REPO_FILE_NAMES
  is 'Mongodb文件名称列表，多个以分号隔开';
comment on column MT_TENANT_TEMPLATE_MODULE.REPO_FILE_UUIDS
  is 'Mongodb文件id列表，多个以分号隔开';
comment on column MT_TENANT_TEMPLATE_MODULE.TEMPLATE_UUID
  is '租户模板UUID';
-- Create/Recreate primary, unique and foreign key constraints 
alter table MT_TENANT_TEMPLATE_MODULE
  add constraint MT_TENANT_TEMPLATE_MODULE_UUID primary key (UUID)
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
