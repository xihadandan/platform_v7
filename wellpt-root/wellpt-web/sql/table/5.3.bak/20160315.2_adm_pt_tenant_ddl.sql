-- Create table
create table MT_TENANT_UPGRADE_LOG
(
  UUID               VARCHAR2(255) not null,
  CREATE_TIME        TIMESTAMP(6) not null,
  CREATOR            VARCHAR2(255) not null,
  MODIFY_TIME        TIMESTAMP(6) not null,
  MODIFIER           VARCHAR2(255) not null,
  REC_VER            NUMBER(10) not null,
  TENANT_UUID        VARCHAR2(255) not null,
  UPGRADE_BATCH_UUID VARCHAR2(255) not null,
  RESULT             NUMBER(10) not null,
  REMARK             VARCHAR2(255) not null
)
tablespace OA_DATA
  pctfree 10
  initrans 1
  maxtrans 255;
-- Add comments to the columns 
comment on column MT_TENANT_UPGRADE_LOG.UUID
  is 'uuid，系统字段';
comment on column MT_TENANT_UPGRADE_LOG.CREATE_TIME
  is '创建时间';
comment on column MT_TENANT_UPGRADE_LOG.CREATOR
  is '创建人';
comment on column MT_TENANT_UPGRADE_LOG.MODIFY_TIME
  is '修改时间';
comment on column MT_TENANT_UPGRADE_LOG.MODIFIER
  is '修改人';
comment on column MT_TENANT_UPGRADE_LOG.REC_VER
  is '版本号';
comment on column MT_TENANT_UPGRADE_LOG.TENANT_UUID
  is '租户UUID';
comment on column MT_TENANT_UPGRADE_LOG.UPGRADE_BATCH_UUID
  is '升级批次UUID';
comment on column MT_TENANT_UPGRADE_LOG.RESULT
  is '升级结果，0失败，1成功';
comment on column MT_TENANT_UPGRADE_LOG.REMARK
  is '升级结果描述';
