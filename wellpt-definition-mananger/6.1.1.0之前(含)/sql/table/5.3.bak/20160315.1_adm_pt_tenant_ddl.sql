-- Create table
create table MT_TENANT_UPGRADE_PROCESS_LOG
(
  UUID               VARCHAR2(255) not null,
  CREATE_TIME        TIMESTAMP(6),
  CREATOR            VARCHAR2(255),
  MODIFY_TIME        TIMESTAMP(6),
  MODIFIER           VARCHAR2(255),
  REC_VER            NUMBER(10) not null,
  NAME               VARCHAR2(255),
  TENANT_UUID        VARCHAR2(255),
  UPGRADE_BATCH_UUID VARCHAR2(255),
  FILE_NAME           VARCHAR2(2000),
  DATA_TYPE          VARCHAR2(255),
  SORT_ORDER         NUMBER(10),
  STATUS             NUMBER(10)
)
tablespace OA_DATA
  pctfree 10
  initrans 1
  maxtrans 255;
-- Add comments to the columns 
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.UUID
  is 'uuid，系统字段';
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.CREATE_TIME
  is '创建时间';
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.CREATOR
  is '创建人';
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.MODIFY_TIME
  is '修改时间';
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.MODIFIER
  is '修改人';
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.REC_VER
  is '版本号';
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.NAME
  is '过程步骤名称';
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.TENANT_UUID
  is '租户UUID';
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.UPGRADE_BATCH_UUID
  is '升级批次UUID';
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.FILE_NAME
  is '升级的文件名称';
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.DATA_TYPE
  is '升级文件的数据类型，dml、dml、dmp、def等';
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.SORT_ORDER
  is '升级的排序号，从1开始递增';
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.STATUS
  is '状态，0失败，1成功';
