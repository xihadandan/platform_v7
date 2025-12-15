create table API_COMMAND
(
  uuid                VARCHAR2(64) not null,
  create_time         TIMESTAMP(6),
  creator             VARCHAR2(255),
  modify_time         TIMESTAMP(6),
  modifier            VARCHAR2(255),
  rec_ver             NUMBER(10) not null,
  system_unit_id      VARCHAR2(32),
  serial_number       VARCHAR2(64),
  idempotent_key      VARCHAR2(300) not null,
  retry_num           NUMBER(10) default 0,
  next_retry_time     TIMESTAMP(6),
  biz_correlation_key VARCHAR2(120),
  business_type       VARCHAR2(120),
  status              NUMBER(1),
  out_system_code     VARCHAR2(64),
  service_code        VARCHAR2(64),
  response_time       TIMESTAMP(6),
  api_mode            VARCHAR2(6)
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
comment on table API_COMMAND
  is 'api指令';
-- Add comments to the columns 
comment on column API_COMMAND.serial_number
  is '请求流水号';
comment on column API_COMMAND.idempotent_key
  is '幂等值';
comment on column API_COMMAND.retry_num
  is '重试次数';
comment on column API_COMMAND.next_retry_time
  is '下一次重试时间';
comment on column API_COMMAND.biz_correlation_key
  is '业务关联key';
comment on column API_COMMAND.business_type
  is '业务类型描述';
comment on column API_COMMAND.status
  is '状态';
comment on column API_COMMAND.out_system_code
  is '外部系统编码';
comment on column API_COMMAND.service_code
  is '服务编码';
comment on column API_COMMAND.response_time
  is '回馈时间';
comment on column API_COMMAND.api_mode
  is 'in或者out';
-- Create/Recreate indexes 
create index IDX_API_RETRY_TIME on API_COMMAND (NEXT_RETRY_TIME)
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
create index IDX_API_STATUS on API_COMMAND (STATUS)
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
create unique index UQ_IDEMP_KEY on API_COMMAND (IDEMPOTENT_KEY)
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
create table API_COMMAND_DETAIL
(
  uuid            VARCHAR2(255) not null,
  create_time     TIMESTAMP(6),
  creator         VARCHAR2(255),
  modify_time     TIMESTAMP(6),
  modifier        VARCHAR2(255),
  rec_ver         NUMBER(10) not null,
  system_unit_id  VARCHAR2(32),
  request_body    CLOB,
  response_body   CLOB,
  command_uuid    VARCHAR2(64),
  adapter_request BLOB
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
comment on table API_COMMAND_DETAIL
  is 'api指令报文明细';
-- Add comments to the columns 
comment on column API_COMMAND_DETAIL.request_body
  is '请求报文';
comment on column API_COMMAND_DETAIL.response_body
  is '响应报文';
comment on column API_COMMAND_DETAIL.command_uuid
  is '指令UUID';
comment on column API_COMMAND_DETAIL.adapter_request
  is '请求对象';

  
  
-- Create table
create table API_AUTHORIZE_ITEM
(
  uuid           VARCHAR2(64) not null,
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(255),
  modify_time    TIMESTAMP(6),
  modifier       VARCHAR2(255),
  rec_ver        NUMBER(10) not null,
  system_unit_id VARCHAR2(32),
  pattern        VARCHAR2(120) not null,
  is_authorized  NUMBER(1) not null,
  system_uuid    VARCHAR2(64)
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
comment on table API_AUTHORIZE_ITEM
  is '内部服务接口授权信息';
-- Add comments to the columns 
comment on column API_AUTHORIZE_ITEM.pattern
  is '匹配模式';
comment on column API_AUTHORIZE_ITEM.is_authorized
  is '是否授权';
-- Create/Recreate primary, unique and foreign key constraints 
alter table API_AUTHORIZE_ITEM
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
create table API_OUT_SYSTEM_CONFIG
(
  uuid           VARCHAR2(64) not null,
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(255),
  modify_time    TIMESTAMP(6),
  modifier       VARCHAR2(255),
  rec_ver        NUMBER(10) not null,
  system_unit_id VARCHAR2(32),
  system_code    VARCHAR2(64) not null,
  system_name    VARCHAR2(120 CHAR) not null,
  token          VARCHAR2(1000)
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
comment on table API_OUT_SYSTEM_CONFIG
  is 'api对接外部系统配置';
-- Add comments to the columns 
comment on column API_OUT_SYSTEM_CONFIG.system_code
  is '系统编码';
comment on column API_OUT_SYSTEM_CONFIG.system_name
  is '系统名称';
comment on column API_OUT_SYSTEM_CONFIG.token
  is 'token';
-- Create/Recreate indexes 
create unique index IDX_A_O_S_C_CODE on API_OUT_SYSTEM_CONFIG (SYSTEM_CODE)
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
create table API_OUT_SYSTEM_SERVICE_CONFIG
(
  uuid            VARCHAR2(64) not null,
  create_time     TIMESTAMP(6),
  creator         VARCHAR2(255),
  modify_time     TIMESTAMP(6),
  modifier        VARCHAR2(255),
  rec_ver         NUMBER(10) not null,
  system_unit_id  VARCHAR2(32),
  system_uuid     VARCHAR2(64),
  service_code    VARCHAR2(64),
  service_name    VARCHAR2(120 CHAR),
  service_url     VARCHAR2(300),
  overtime_limit  NUMBER(10),
  service_adapter VARCHAR2(300)
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
comment on table API_OUT_SYSTEM_SERVICE_CONFIG
  is 'api对接系统服务配置';
-- Add comments to the columns 
comment on column API_OUT_SYSTEM_SERVICE_CONFIG.service_code
  is '服务编码';
comment on column API_OUT_SYSTEM_SERVICE_CONFIG.service_name
  is '服务名称';
comment on column API_OUT_SYSTEM_SERVICE_CONFIG.service_url
  is '服务调用地址';
comment on column API_OUT_SYSTEM_SERVICE_CONFIG.overtime_limit
  is '超时时限';
comment on column API_OUT_SYSTEM_SERVICE_CONFIG.service_adapter
  is '服务适配器';
