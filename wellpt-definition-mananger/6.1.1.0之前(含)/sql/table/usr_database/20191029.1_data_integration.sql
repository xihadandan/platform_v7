-- Create table
create table DI_CONFIG
(
  uuid                    VARCHAR2(64) primary key,
  create_time             TIMESTAMP(6) not null,
  creator                 VARCHAR2(64) not null,
  modify_time             TIMESTAMP(6),
  modifier                VARCHAR2(64),
  rec_ver                 NUMBER(10),
  system_unit_id          VARCHAR2(64) not null,
  name                    VARCHAR2(64 CHAR) not null,
  id                      VARCHAR2(64 CHAR) not null,
  is_enable               NUMBER(1) default 0,
  time_interval           NUMBER(12),
  job_uuid                VARCHAR2(64),
  redelivery_maximum      NUMBER(3),
  redelivery_interval     NUMBER(12),
  redelivery_rule_pattern VARCHAR2(120 CHAR),
  enable_log              NUMBER(1),
  exception               CLOB
) ;
-- Add comments to the table
comment on table DI_CONFIG
  is '数据交换配置-基本信息';
-- Add comments to the columns
comment on column DI_CONFIG.name
  is '配置名称';
comment on column DI_CONFIG.id
  is '配置ID';
comment on column DI_CONFIG.is_enable
  is '是否启用';
comment on column DI_CONFIG.time_interval
  is '间隔执行时间';
comment on column DI_CONFIG.job_uuid
  is '定时任务UUID（平台的定时任务配置）';
comment on column DI_CONFIG.redelivery_maximum
  is '最大重试次数';
comment on column DI_CONFIG.redelivery_interval
  is '重试间隔时间';
comment on column DI_CONFIG.redelivery_rule_pattern
  is '重试规则';
comment on column DI_CONFIG.enable_log
  is '是否记录过程日志';
comment on column DI_CONFIG.exception
  is '异常';



-- Create table
create table DI_CALLBACK_REQUEST
(
  uuid            VARCHAR2(64) primary key,
  create_time     TIMESTAMP(6) not null,
  creator         VARCHAR2(64) not null,
  modify_time     TIMESTAMP(6),
  modifier        VARCHAR2(64),
  rec_ver         NUMBER(10),
  callback_class  VARCHAR2(120) not null,
  callback_status NUMBER(1) not null,
  response_body   BLOB,
  time_consuming  NUMBER(12),
  request_id      VARCHAR2(120 CHAR),
  request_body    CLOB
) ;


-- Create table
create table DI_CALLBACK_REQUEST_HIS
(
  uuid            VARCHAR2(64) primary  key,
  create_time     TIMESTAMP(6) not null,
  creator         VARCHAR2(64) not null,
  modify_time     TIMESTAMP(6),
  modifier        VARCHAR2(64),
  rec_ver         NUMBER(10),
  callback_class  VARCHAR2(120) not null,
  callback_status NUMBER(1) not null,
  response_body   BLOB,
  time_consuming  NUMBER(12),
  request_id      VARCHAR2(120 CHAR),
  request_body    CLOB
) ;


-- Create table
create table DI_DATA_CONSUMER_ENDPOINT
(
  uuid           VARCHAR2(64) primary key,
  create_time    TIMESTAMP(6) not null,
  creator        VARCHAR2(64) not null,
  modify_time    TIMESTAMP(6),
  modifier       VARCHAR2(64),
  rec_ver        NUMBER(10),
  edp_type       VARCHAR2(120 CHAR) not null,
  definition     VARCHAR2(3000 CHAR) not null,
  di_config_uuid VARCHAR2(64) not null
) ;
-- Add comments to the table
comment on table DI_DATA_CONSUMER_ENDPOINT
  is '数据交换配置-数据消费源点定义';
-- Add comments to the columns
comment on column DI_DATA_CONSUMER_ENDPOINT.edp_type
  is '来源类型';
comment on column DI_DATA_CONSUMER_ENDPOINT.definition
  is '定义数据(json)';
comment on column DI_DATA_CONSUMER_ENDPOINT.di_config_uuid
  is '数据交换配置UUID';


-- Create table
create table DI_DATA_INTERATION_LOG
(
  uuid            VARCHAR2(64) primary key,
  create_time     TIMESTAMP(6) not null,
  creator         VARCHAR2(64) not null,
  modify_time     TIMESTAMP(6),
  modifier        VARCHAR2(64),
  rec_ver         NUMBER(10),
  di_config_uuid  VARCHAR2(64) not null,
  status          NUMBER(1),
  exchange_id     VARCHAR2(120),
  data_begin_time TIMESTAMP(6),
  is_latest       NUMBER(1),
  exception       CLOB,
  page_index      NUMBER(10) default 1,
  total_page      NUMBER(10) default 1,
  page_limit      NUMBER(10),
  data_end_time   TIMESTAMP(6)
) ;
-- Add comments to the table
comment on table DI_DATA_INTERATION_LOG
  is '数据交换过程日志';
-- Add comments to the columns
comment on column DI_DATA_INTERATION_LOG.di_config_uuid
  is '交换配置';
comment on column DI_DATA_INTERATION_LOG.status
  is '状态';
comment on column DI_DATA_INTERATION_LOG.exchange_id
  is '交换ID';
comment on column DI_DATA_INTERATION_LOG.data_begin_time
  is '开始时间';
comment on column DI_DATA_INTERATION_LOG.is_latest
  is '是否是最近的一次数据交换';
comment on column DI_DATA_INTERATION_LOG.exception
  is '异常';
comment on column DI_DATA_INTERATION_LOG.page_index
  is '页码';
comment on column DI_DATA_INTERATION_LOG.total_page
  is '总页数';
comment on column DI_DATA_INTERATION_LOG.page_limit
  is '每页数据量';
comment on column DI_DATA_INTERATION_LOG.data_end_time
  is '结束时间';


-- Create table
create table DI_DATA_PROCESSOR
(
  uuid                VARCHAR2(64) primary key,
  create_time         TIMESTAMP(6) not null,
  creator             VARCHAR2(64) not null,
  modify_time         TIMESTAMP(6),
  modifier            VARCHAR2(64),
  rec_ver             NUMBER(10),
  di_config_uuid      VARCHAR2(64) not null,
  type                NUMBER(1) not null,
  seq                 NUMBER(2) default 1,
  processor_class     VARCHAR2(255) not null,
  processor_parameter VARCHAR2(3000 CHAR),
  is_async            NUMBER(1) default 0 not null
) ;
-- Add comments to the table
comment on table DI_DATA_PROCESSOR
  is '数据交换配置-数据处理过程定义';
-- Add comments to the columns
comment on column DI_DATA_PROCESSOR.di_config_uuid
  is '数据交换配置UUID';
comment on column DI_DATA_PROCESSOR.type
  is '数据交换配置类型';
comment on column DI_DATA_PROCESSOR.seq
  is '数据交换处理器顺序';
comment on column DI_DATA_PROCESSOR.processor_class
  is '处理器类';
comment on column DI_DATA_PROCESSOR.processor_parameter
  is '处理器参数(json)';
comment on column DI_DATA_PROCESSOR.is_async
  is '是否异步执行';


-- Create table
create table DI_DATA_PROCESSOR_LOG
(
  uuid              VARCHAR2(64) primary key,
  create_time       TIMESTAMP(6) not null,
  creator           VARCHAR2(64) not null,
  modify_time       TIMESTAMP(6),
  modifier          VARCHAR2(64),
  rec_ver           NUMBER(10),
  di_processor_uuid VARCHAR2(64),
  di_config_uuid    VARCHAR2(64) not null,
  time_consuming    NUMBER(12),
  exchange_id       VARCHAR2(120),
  processor_name    VARCHAR2(120 CHAR),
  in_message        CLOB,
  out_message       CLOB
) ;
-- Add comments to the table
comment on table DI_DATA_PROCESSOR_LOG
  is '数据交换配置-处理器处理日志';
-- Add comments to the columns
comment on column DI_DATA_PROCESSOR_LOG.di_processor_uuid
  is '处理器UUID';
comment on column DI_DATA_PROCESSOR_LOG.di_config_uuid
  is '数据交换配置UUID';
comment on column DI_DATA_PROCESSOR_LOG.time_consuming
  is '处理时间耗时';
comment on column DI_DATA_PROCESSOR_LOG.exchange_id
  is '交换ID';
comment on column DI_DATA_PROCESSOR_LOG.in_message
  is '消息处理前';
comment on column DI_DATA_PROCESSOR_LOG.out_message
  is '消息处理后';
-- Create/Recreate indexes
create index IDX_LOG_DI_CONFIG_UUID on DI_DATA_PROCESSOR_LOG (DI_CONFIG_UUID) ;


-- Create table
create table DI_DATA_PRODUCER_ENDPOINT
(
  uuid              VARCHAR2(64) primary key,
  create_time       TIMESTAMP(6) not null,
  creator           VARCHAR2(64) not null,
  modify_time       TIMESTAMP(6),
  modifier          VARCHAR2(64),
  rec_ver           NUMBER(10),
  di_config_uuid    VARCHAR2(64) not null,
  edp_type          VARCHAR2(64) not null,
  definition        VARCHAR2(3000 CHAR) not null,
  callback_class    VARCHAR2(255),
  is_async_callback NUMBER(1) default 0
) ;
-- Add comments to the table
comment on table DI_DATA_PRODUCER_ENDPOINT
  is '数据交换配置-数据生产源点定义';
-- Add comments to the columns
comment on column DI_DATA_PRODUCER_ENDPOINT.di_config_uuid
  is '数据交换配置UUID';
comment on column DI_DATA_PRODUCER_ENDPOINT.edp_type
  is '类型';
comment on column DI_DATA_PRODUCER_ENDPOINT.definition
  is '定义参数';
comment on column DI_DATA_PRODUCER_ENDPOINT.callback_class
  is '回调类';
comment on column DI_DATA_PRODUCER_ENDPOINT.is_async_callback
  is '是否异步回调';


-- Create table
create table DI_TABLE_COLUMN_DATA_CHANGE
(
  uuid             VARCHAR2(64) not null,
  create_time      TIMESTAMP(6) not null,
  column_name      VARCHAR2(64) not null,
  data_type        VARCHAR2(64) not null,
  data_basic_value VARCHAR2(64),
  data_text_value  VARCHAR2(4000),
  data_clob_value  CLOB,
  data_blob_value  BLOB
) ;
-- Create/Recreate primary, unique and foreign key constraints
alter table DI_TABLE_COLUMN_DATA_CHANGE
  add primary key (UUID, COLUMN_NAME) ;




-- Create table
create table DI_TABLE_DATA_CHANGE
(
  uuid           VARCHAR2(64) primary key ,
  create_time    TIMESTAMP(6) not null,
  table_name     VARCHAR2(64) not null,
  pk_uuid        VARCHAR2(200) not null,
  pk_col_name    VARCHAR2(200),
  action         VARCHAR2(12),
  status         NUMBER(1) default 0,
  sync_time      TIMESTAMP(6),
  sync_direction NUMBER(1)
) ;
-- Add comments to the columns
comment on column DI_TABLE_DATA_CHANGE.action
  is '动作';
comment on column DI_TABLE_DATA_CHANGE.status
  is '同步状态';
comment on column DI_TABLE_DATA_CHANGE.sync_time
  is '同步时间';
comment on column DI_TABLE_DATA_CHANGE.sync_direction
  is '同步方向';
