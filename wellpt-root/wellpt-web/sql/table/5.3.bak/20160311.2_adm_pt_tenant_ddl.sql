  
-- Create table
create table MT_TENANT_UPGRADE_LOG
(
  UUID               varchar2(64) not null,
  CREATE_TIME        TIMESTAMP ,
  CREATOR            varchar2(64),
  MODIFY_TIME        TIMESTAMP,
  MODIFIER           varchar2(64) ,
  REC_VER            number(10),
  TENANT_UUID        varchar2(64),
  UPGRADE_BATCH_UUID varchar2(64),
  RESULT             number(4) ,
  REMARK             varchar2(2048)
)
;
-- Add comments to the columns 
comment on column MT_TENANT_UPGRADE_LOG.UUID
  is 'UUID，系统字段';
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
-- Create/Recreate primary, unique and foreign key constraints 
alter table MT_TENANT_UPGRADE_LOG
  add constraint MT_TENANT_UPGRADE_LOG_UUID primary key (UUID);
  
  



-- Create table
create table MT_TENANT_UPGRADE_PROCESS_LOG
(
  UUID               varchar2(64) not null,
  CREATE_TIME        TIMESTAMP,
  CREATOR            varchar2(64),
  MODIFY_TIME        TIMESTAMP,
  MODIFIER           varchar2(64),
  REC_VER            number(10),
  NAME               varchar2(255),
  TENANT_UUID        varchar2(64),
  UPGRADE_BATCH_UUID varchar2(64),
  FILENAME           varchar2(255),
  DATA_TYPE          varchar2(64),
  SORT_ORDER         number(8),
  STATUS             number(4)
)
;
-- Add comments to the columns 
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.UUID
  is 'UUID，系统字段';
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
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.FILENAME
  is '升级的文件名称';
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.DATA_TYPE
  is '升级文件的数据类型，DML、DML、DMP、DEF等';
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.SORT_ORDER
  is '升级的排序号，从1开始递增';
comment on column MT_TENANT_UPGRADE_PROCESS_LOG.STATUS
  is '状态，0失败，1成功';
-- Create/Recreate primary, unique and foreign key constraints 
alter table MT_TENANT_UPGRADE_PROCESS_LOG
  add constraint MT_T_U_P_LOG_UUID primary key (UUID);
  
  
  
  
-- Create table
create table MT_TENANT_CREATE_PROCESS_LOG
(
  UUID        varchar2(64) not null,
  CREATE_TIME TIMESTAMP,
  CREATOR     varchar2(64),
  MODIFY_TIME TIMESTAMP,
  MODIFIER    varchar2(64),
  REC_VER     number(10),
  NAME        varchar2(255),
  TENANT_UUID varchar2(64),
  PARENT_UUID varchar2(64),
  SORT_ORDER  number(10),
  STATUS      number(4),
  CONTENT     clob,
  BATCH_NO    number(10)
)
;
-- Add comments to the columns 
comment on column MT_TENANT_CREATE_PROCESS_LOG.UUID
  is 'UUID，系统字段';
comment on column MT_TENANT_CREATE_PROCESS_LOG.CREATE_TIME
  is '创建时间';
comment on column MT_TENANT_CREATE_PROCESS_LOG.CREATOR
  is '创建人';
comment on column MT_TENANT_CREATE_PROCESS_LOG.MODIFY_TIME
  is '修改时间';
comment on column MT_TENANT_CREATE_PROCESS_LOG.MODIFIER
  is '修改人';
comment on column MT_TENANT_CREATE_PROCESS_LOG.REC_VER
  is '版本号';
comment on column MT_TENANT_CREATE_PROCESS_LOG.NAME
  is '过程步骤名称';
comment on column MT_TENANT_CREATE_PROCESS_LOG.TENANT_UUID
  is '租户ID';
comment on column MT_TENANT_CREATE_PROCESS_LOG.SORT_ORDER
  is '排序号，从1开始递增';
comment on column MT_TENANT_CREATE_PROCESS_LOG.STATUS
  is '0失败，1成功';
comment on column MT_TENANT_CREATE_PROCESS_LOG.CONTENT
  is '日志内容';
comment on column MT_TENANT_CREATE_PROCESS_LOG.BATCH_NO
  is '创建批次号，第一次创建为1，以后从错误中创建的依次递增1';
-- Create/Recreate primary, unique and foreign key constraints 
alter table MT_TENANT_CREATE_PROCESS_LOG
  add constraint MT_T_C_PROCESS_LOG primary key (UUID);