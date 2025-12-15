-- Create table
create table mt_tenant_upgrade_batch
(
  uuid            varchar(255) not null,
  create_time     timestamp not null,
  creator         varchar(255) not null,
  modify_time     timestamp not null,
  modifier        varchar(255) not null,
  rec_ver         number(10) not null,
  name            varchar(255) not null,
  repo_file_names varchar(2000) not null,
  repo_file_names Varchar(2000) not null
)
;
-- Add comments to the columns 
comment on column mt_tenant_upgrade_batch.uuid
  is 'uuid，系统字段，同时作为mogodb的文件夹ID';
comment on column mt_tenant_upgrade_batch.create_time
  is '创建时间';
comment on column mt_tenant_upgrade_batch.creator
  is '创建人';
comment on column mt_tenant_upgrade_batch.modify_time
  is '修改时间';
comment on column mt_tenant_upgrade_batch.modifier
  is '修改人';
comment on column mt_tenant_upgrade_batch.rec_ver
  is '版本号';
comment on column mt_tenant_upgrade_batch.name
  is '批次号，由 年月日时分秒 组成';
comment on column mt_tenant_upgrade_batch.repo_file_names
  is '文件名';
comment on column mt_tenant_upgrade_batch.repo_file_names
  is '文件UUID';
-- Create/Recreate primary, unique and foreign key constraints 
alter table mt_tenant_upgrade_batch
  add constraint mt_tenant_upgrade_batch_uuid primary key (UUID);
