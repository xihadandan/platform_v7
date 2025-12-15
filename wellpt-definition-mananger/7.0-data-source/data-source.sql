-- ADD/MODIFY COLUMNS 
ALTER TABLE CD_DATA_STORE_DEFINITION ADD DB_SOURCE_TYPE NUMBER(1) DEFAULT 0;
ALTER TABLE CD_DATA_STORE_DEFINITION ADD DB_LINK_CONF_UUID NUMBER(19);
-- ADD COMMENTS TO THE COLUMNS 
COMMENT ON COLUMN CD_DATA_STORE_DEFINITION.DB_SOURCE_TYPE
  IS '数据库来源类型: 0 内部数据源 1 自定义数据源';



-- Create table
create table DB_LINK_CONFIG
(
  uuid            NUMBER(19) primary key,
  name            VARCHAR2(120 CHAR) not null,
  link_type       VARCHAR2(32) default 'host' not null,
  host            VARCHAR2(120),
  port            NUMBER(8),
  url             VARCHAR2(120),
  user_name       VARCHAR2(120),
  password        VARCHAR2(120),
  user_role       VARCHAR2(120),
  db_type         VARCHAR2(120) not null,
  sname           VARCHAR2(120),
  sid             VARCHAR2(120),
  connect_stype   VARCHAR2(120) default 'sname' not null,
  param           CLOB,
  driver_class    VARCHAR2(300),
  driver_jar_file VARCHAR2(64),
  remark          VARCHAR2(300 CHAR),
  create_time     TIMESTAMP(6),
  creator         VARCHAR2(64),
  modifier        VARCHAR2(64),
  modify_time     TIMESTAMP(6),
  system          VARCHAR2(64),
  tenant          VARCHAR2(64),
  rec_ver         FLOAT(3) default 1
); 
-- Add comments to the table 
comment on table DB_LINK_CONFIG
  is '外部数据库连接配置';
-- Add comments to the columns 
comment on column DB_LINK_CONFIG.name
  is '名称';
comment on column DB_LINK_CONFIG.link_type
  is '连接方式: host url';
comment on column DB_LINK_CONFIG.host
  is '主机地址';
comment on column DB_LINK_CONFIG.port
  is '端口';
comment on column DB_LINK_CONFIG.user_name
  is '用户名';
comment on column DB_LINK_CONFIG.password
  is '密码';
comment on column DB_LINK_CONFIG.user_role
  is '用户角色';
comment on column DB_LINK_CONFIG.db_type
  is '数据库类型:';
comment on column DB_LINK_CONFIG.sname
  is '服务名';
comment on column DB_LINK_CONFIG.sid
  is '服务ID';
comment on column DB_LINK_CONFIG.connect_stype
  is '连接服务使用方式: sname sid';
comment on column DB_LINK_CONFIG.param
  is '其他参数';
comment on column DB_LINK_CONFIG.driver_class
  is '驱动程序类';
comment on column DB_LINK_CONFIG.driver_jar_file
  is '驱动程序包';
comment on column DB_LINK_CONFIG.remark
  is 'REMARK'; 