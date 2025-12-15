-- Create table
create table SERVER_REGISTER_CENTER
(
  uuid        VARCHAR2(64 CHAR) not null,
  create_time TIMESTAMP(6),
  creator     VARCHAR2(255 CHAR),
  modifier    VARCHAR2(255 CHAR),
  modify_time TIMESTAMP(6),
  rec_ver     NUMBER(10),
  ip          VARCHAR2(32 CHAR) not null,
  port        NUMBER(10) not null,
  name        VARCHAR2(120 CHAR),
  remark      VARCHAR2(512 CHAR),
  machine     VARCHAR2(120 CHAR)
);

comment on table SERVER_REGISTER_CENTER
  is '应用服务注册中心';
-- Add comments to the columns
comment on column SERVER_REGISTER_CENTER.ip
  is '应用IP';
comment on column SERVER_REGISTER_CENTER.port
  is '应用端口';
comment on column SERVER_REGISTER_CENTER.name
  is '应用名称';
comment on column SERVER_REGISTER_CENTER.remark
  is '描述信息';
comment on column SERVER_REGISTER_CENTER.machine
  is '机器名';
-- Create/Recreate primary, unique and foreign key constraints
alter table SERVER_REGISTER_CENTER
  add primary key (UUID)
  using index
  tablespace OA_DATA
  pctfree 10
  initrans 2
  maxtrans 255;
