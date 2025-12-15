-- Create table
create table EXEC_SQL_DEFINITION
(
  uuid           VARCHAR2(64) not null,
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(255 CHAR),
  modify_time    TIMESTAMP(6),
  modifier       VARCHAR2(255 CHAR),
  rec_ver        NUMBER(10),
  name           VARCHAR2(255 CHAR) not null,
  id             VARCHAR2(255 CHAR) not null,
  sql            CLOB,
  system_unit_id VARCHAR2(12) not null
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
-- Add comments to the table 
comment on table EXEC_SQL_DEFINITION
  is '可执行SQL配置';
-- Add comments to the columns 
comment on column EXEC_SQL_DEFINITION.sql
  is 'sql语句';
-- Create/Recreate primary, unique and foreign key constraints 
alter table EXEC_SQL_DEFINITION
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
