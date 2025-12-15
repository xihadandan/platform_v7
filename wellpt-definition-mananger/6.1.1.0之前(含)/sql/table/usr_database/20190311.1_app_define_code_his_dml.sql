-- Create table
create table APP_DEFINE_CODE_HIS
(
  uuid            VARCHAR2(64) not null,
  create_time     TIMESTAMP(6),
  creator         VARCHAR2(255),
  modify_time     TIMESTAMP(6),
  modifier        VARCHAR2(255),
  rec_ver         NUMBER(10) not null,
  remark          VARCHAR2(1000 CHAR),
  author          VARCHAR2(120),
  system_unit_id  VARCHAR2(32),
  rela_busiz_uuid VARCHAR2(120),
  script_type     VARCHAR2(120),
  script          CLOB
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
-- Create/Recreate primary, unique and foreign key constraints 
alter table APP_DEFINE_CODE_HIS
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
