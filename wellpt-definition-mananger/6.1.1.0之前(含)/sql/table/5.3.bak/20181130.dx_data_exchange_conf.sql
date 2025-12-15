-------------------------------------------------
-- Export file for user USR_PT_6_0_DEV@123_DB4 --
-- Created by Leo on 2018/11/29, 10:44:39 -------
-------------------------------------------------

create table DX_DATA_IMP_EXP_BATCH
(
  uuid              VARCHAR2(64) not null,
  create_time       TIMESTAMP(6),
  creator           VARCHAR2(255),
  modify_time       TIMESTAMP(6),
  modifier          VARCHAR2(255),
  rec_ver           NUMBER(10) not null,
  conf_uuid         VARCHAR2(64),
  execute_no        VARCHAR2(64),
  batch_no          VARCHAR2(120),
  success_num       NUMBER(10),
  total_num         NUMBER(10),
  export_locations  VARCHAR2(2000),
  system_unit_id    VARCHAR2(32),
  batch_type        NUMBER(1),
  feedback_location VARCHAR2(320)
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
comment on table DX_DATA_IMP_EXP_BATCH
  is '数据交换导入导出批次';
comment on column DX_DATA_IMP_EXP_BATCH.batch_type
  is '0 导入 1 导出 ';
alter table DX_DATA_IMP_EXP_BATCH
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


create table DX_DATA_IMP_EXP_CONF
(
  uuid                 VARCHAR2(64) not null,
  create_time          TIMESTAMP(6),
  creator              VARCHAR2(255),
  modify_time          TIMESTAMP(6),
  modifier             VARCHAR2(255),
  rec_ver              NUMBER(10) not null,
  id                   VARCHAR2(64),
  conf_name            VARCHAR2(120),
  action               NUMBER(1),
  template_file_id     VARCHAR2(64),
  template_file_name   VARCHAR2(120),
  is_need_feedback     NUMBER(1),
  is_schedule          NUMBER(1),
  is_enable            NUMBER(1),
  last_exchange_time   TIMESTAMP(6),
  import_done_listener VARCHAR2(120),
  system_unit_id       VARCHAR2(32),
  delay_second         NUMBER(12)
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
comment on table DX_DATA_IMP_EXP_CONF
  is '数据交换导入导出配置';
comment on column DX_DATA_IMP_EXP_CONF.action
  is '0 导入 1 导出';
comment on column DX_DATA_IMP_EXP_CONF.template_file_id
  is '模板文件id';
comment on column DX_DATA_IMP_EXP_CONF.template_file_name
  is '模板文件名称';
comment on column DX_DATA_IMP_EXP_CONF.is_need_feedback
  is '是否需要反馈';
comment on column DX_DATA_IMP_EXP_CONF.is_schedule
  is '启用定时器';
comment on column DX_DATA_IMP_EXP_CONF.is_enable
  is '启用';
comment on column DX_DATA_IMP_EXP_CONF.last_exchange_time
  is '最后一次交换时间';
comment on column DX_DATA_IMP_EXP_CONF.import_done_listener
  is '导入数据处理侦听';
comment on column DX_DATA_IMP_EXP_CONF.delay_second
  is '执行间隔';
alter table DX_DATA_IMP_EXP_CONF
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


create table DX_DATA_IMP_EXP_QRTZ
(
  uuid              VARCHAR2(64) not null,
  create_time       TIMESTAMP(6),
  creator           VARCHAR2(255),
  modify_time       TIMESTAMP(6),
  modifier          VARCHAR2(255),
  rec_ver           NUMBER(10) not null,
  job_uuid          VARCHAR2(64),
  conf_uuid         VARCHAR2(64),
  system_unit_id    VARCHAR2(32),
  last_execute_time TIMESTAMP(6)
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
comment on table DX_DATA_IMP_EXP_QRTZ
  is '数据交换使用的定时器';
alter table DX_DATA_IMP_EXP_QRTZ
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


create table DX_EXPORT_RELA_DATASTORE
(
  uuid                VARCHAR2(64) not null,
  create_time         TIMESTAMP(6),
  creator             VARCHAR2(255),
  modify_time         TIMESTAMP(6),
  modifier            VARCHAR2(255),
  rec_ver             NUMBER(10) not null,
  conf_uuid           VARCHAR2(64),
  datastore_id        VARCHAR2(64),
  last_export_time    TIMESTAMP(6),
  system_unit_id      VARCHAR2(32),
  default_condition   VARCHAR2(255),
  time_column         VARCHAR2(32),
  rich_txt_column     VARCHAR2(32),
  primary_column      VARCHAR2(32),
  attachment_column   VARCHAR2(32),
  data_display_column VARCHAR2(32)
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
comment on table DX_EXPORT_RELA_DATASTORE
  is '数据交换导出关联的数据仓库';
comment on column DX_EXPORT_RELA_DATASTORE.datastore_id
  is '数据仓库';
comment on column DX_EXPORT_RELA_DATASTORE.last_export_time
  is '数据仓库上一次数据的导出时间';
comment on column DX_EXPORT_RELA_DATASTORE.time_column
  is '时间字段，导出数据的时间范围使用';
comment on column DX_EXPORT_RELA_DATASTORE.rich_txt_column
  is '富文本字段';
comment on column DX_EXPORT_RELA_DATASTORE.primary_column
  is '唯一值字段';
comment on column DX_EXPORT_RELA_DATASTORE.attachment_column
  is '附件字段';
comment on column DX_EXPORT_RELA_DATASTORE.data_display_column
  is '数据展示字段';
alter table DX_EXPORT_RELA_DATASTORE
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


create table DX_EXP_DATASTORE_BATCH_DETAIL
(
  uuid              VARCHAR2(64) not null,
  create_time       TIMESTAMP(6),
  creator           VARCHAR2(255),
  modify_time       TIMESTAMP(6),
  modifier          VARCHAR2(255),
  rec_ver           NUMBER(10) not null,
  batch_uuid        VARCHAR2(64),
  datastore_name    VARCHAR2(120),
  start_export_time TIMESTAMP(6),
  success_num       NUMBER(10),
  total_num         NUMBER(10),
  system_unit_id    VARCHAR2(32),
  data_range        VARCHAR2(512)
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
alter table DX_EXP_DATASTORE_BATCH_DETAIL
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


create table DX_IMP_EXP_DATA_DETAIL
(
  uuid           VARCHAR2(64) not null,
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(255),
  modify_time    TIMESTAMP(6),
  modifier       VARCHAR2(255),
  rec_ver        NUMBER(10) not null,
  batch_uuid     VARCHAR2(64),
  is_success     NUMBER(1),
  data_display   VARCHAR2(512),
  system_unit_id VARCHAR2(32)
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
alter table DX_IMP_EXP_DATA_DETAIL
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


create table DX_IMP_EXP_ENDPOINT
(
  uuid           VARCHAR2(64) not null,
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(255),
  modify_time    TIMESTAMP(6),
  modifier       VARCHAR2(255),
  rec_ver        NUMBER(10) not null,
  conf_uuid      VARCHAR2(64),
  endpoint       VARCHAR2(512),
  type           NUMBER(1),
  is_compress    NUMBER(1),
  system_unit_id VARCHAR2(32),
  is_formate     NUMBER(1)
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
comment on table DX_IMP_EXP_ENDPOINT
  is '数据交换端点配置';
comment on column DX_IMP_EXP_ENDPOINT.endpoint
  is '端点地址';
comment on column DX_IMP_EXP_ENDPOINT.type
  is '0 导入端点 1 导出端点 3反馈端点 4备份端点';
comment on column DX_IMP_EXP_ENDPOINT.is_compress
  is '是否压缩报文与附件';
comment on column DX_IMP_EXP_ENDPOINT.is_formate
  is '是否格式化报文';
alter table DX_IMP_EXP_ENDPOINT
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

