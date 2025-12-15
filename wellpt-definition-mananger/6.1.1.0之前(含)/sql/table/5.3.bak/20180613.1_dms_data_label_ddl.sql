-- Create table
create table DMS_DATA_LABEL
(
  uuid           VARCHAR2(64) not null,
  label_name     VARCHAR2(64) not null,
  label_color    VARCHAR2(12) not null,
  user_id        VARCHAR2(64) not null,
  module_id      VARCHAR2(64) not null,
  system_unit_id VARCHAR2(64) not null,
  create_time    TIMESTAMP(6) not null,
  creator        VARCHAR2(64) not null,
  modify_time    TIMESTAMP(6),
  modifier       VARCHAR2(64),
  rec_ver        NUMBER(10)
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
-- Create/Recreate indexes 
create index SYS_C00198097 on DMS_DATA_LABEL (USER_ID, MODULE_ID)
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
-- Create/Recreate primary, unique and foreign key constraints 
alter table DMS_DATA_LABEL
  add primary key (UUID)
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

  

-- Create table
create table DMS_DATA_LABEL_RELA
(
  uuid           VARCHAR2(64) not null,
  label_uuid     VARCHAR2(64) not null,
  data_uuid      VARCHAR2(64) not null,
  system_unit_id VARCHAR2(64) not null,
  create_time    TIMESTAMP(6) not null,
  creator        VARCHAR2(64) not null,
  modify_time    TIMESTAMP(6),
  modifier       VARCHAR2(64),
  rec_ver        NUMBER(10)
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
-- Create/Recreate indexes 
create index SYS_C00198177 on DMS_DATA_LABEL_RELA (LABEL_UUID, DATA_UUID)
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
-- Create/Recreate primary, unique and foreign key constraints 
alter table DMS_DATA_LABEL_RELA
  add primary key (UUID)
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
