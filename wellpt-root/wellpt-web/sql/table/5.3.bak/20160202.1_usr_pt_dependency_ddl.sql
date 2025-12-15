-- Create table
create table cd_data_dependency
(
  uuid               varchar2(255) not null,
  create_time        timestamp,
  creator            varchar2(255),
  modify_time        timestamp,
  modifier           varchar2(255),
  rec_ver            number(10) not null,
  data_uuid          varchar2(255),
  data_name          varchar2(255),
  data_type          varchar2(255),
  data_rec_ver       number(10),
  dependency_uuid    varchar2(255),
  dependency_name    varchar2(255),
  dependency_type    varchar2(255),
  dependency_rec_ver number(10)
)
;
-- Add comments to the columns 
comment on column cd_data_dependency.uuid
  is 'uuid，系统字段';
comment on column cd_data_dependency.create_time
  is '创建时间';
comment on column cd_data_dependency.creator
  is '创建人';
comment on column cd_data_dependency.modify_time
  is '修改时间';
comment on column cd_data_dependency.modifier
  is '修改人';
comment on column cd_data_dependency.rec_ver
  is '版本号';
comment on column cd_data_dependency.data_uuid
  is '数据UUID';
comment on column cd_data_dependency.data_name
  is '数据名称';
comment on column cd_data_dependency.data_type
  is '数据导出类型';
comment on column cd_data_dependency.data_rec_ver
  is '数据版本号';
comment on column cd_data_dependency.dependency_uuid
  is '依赖数据UUID';
comment on column cd_data_dependency.dependency_name
  is '依赖数据名称';
comment on column cd_data_dependency.dependency_type
  is '依赖数据导出类型';
comment on column cd_data_dependency.dependency_rec_ver
  is '依赖数据版本号';
-- Create/Recreate primary, unique and foreign key constraints 
alter table cd_data_dependency
  add constraint cd_data_dependency_uuid primary key (UUID);

-- Create/Recreate indexes 
create index CD_DD_TYPE_UUID_IDX on CD_DATA_DEPENDENCY (data_type, data_uuid);
create index CD_DD_DATA_UUID_IDX on CD_DATA_DEPENDENCY (data_uuid, dependency_uuid);
