-- Add/modify columns 
alter table WF_TASK_TIMER add timer_id VARCHAR2(50 CHAR);
alter table WF_TASK_TIMER add timer_uuid VARCHAR2(50 CHAR);
-- Add comments to the columns 
comment on column WF_TASK_TIMER.timer_id
  is '计时器配置ID';
comment on column WF_TASK_TIMER.timer_uuid
  is '计时器UUID';


-- Create table
create table TS_TIMER_CONFIG
(
  uuid                     VARCHAR2(255 CHAR) not null,
  create_time              TIMESTAMP(6),
  creator                  VARCHAR2(255 CHAR),
  modifier                 VARCHAR2(255 CHAR),
  modify_time              TIMESTAMP(6),
  rec_ver                  NUMBER(10),
  system_unit_id           VARCHAR2(20 CHAR),
  name                     VARCHAR2(150 CHAR),
  id                       VARCHAR2(50 CHAR),
  code                     VARCHAR2(50 CHAR),
  timing_mode              VARCHAR2(6 CHAR),
  time_limit_type          VARCHAR2(6 CHAR),
  time_limit               VARCHAR2(50 CHAR),
  working_time_id          VARCHAR2(50 CHAR),
  include_start_time_point NUMBER(1),
  auto_delay 			   NUMBER(1),
  listener                 VARCHAR2(150 CHAR),
  remark          		   VARCHAR2(200 CHAR)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table ts_timer_config
  add constraint ts_timer_config_uuid primary key (UUID);
-- Add comments to the table 
comment on table TS_TIMER_CONFIG
  is '计时配置';
-- Add comments to the columns 
comment on column TS_TIMER_CONFIG.uuid
  is 'UUID，系统字段';
comment on column TS_TIMER_CONFIG.create_time
  is '创建时间';
comment on column TS_TIMER_CONFIG.creator
  is '创建人';
comment on column TS_TIMER_CONFIG.modifier
  is '修改人';
comment on column TS_TIMER_CONFIG.modify_time
  is '修改时间';
comment on column TS_TIMER_CONFIG.rec_ver
  is '版本号';
comment on column TS_TIMER_CONFIG.system_unit_id
  is '系统单位ID';
comment on column TS_TIMER_CONFIG.name
  is '名称';
comment on column TS_TIMER_CONFIG.id
  is 'ID';
comment on column TS_TIMER_CONFIG.code
  is '编号';
comment on column TS_TIMER_CONFIG.timing_mode
  is '计时方式';
comment on column TS_TIMER_CONFIG.time_limit_type
  is '时限类型';
comment on column TS_TIMER_CONFIG.time_limit
  is '时限';
comment on column TS_TIMER_CONFIG.working_time_id
  is '工作时间配置ID';
comment on column TS_TIMER_CONFIG.include_start_time_point
  is '计时包含启动时间点，1是0否，默认0';
comment on column TS_TIMER_CONFIG.auto_delay
  is '自动推迟到下一工作时间起始点前，1是0否，默认0';
comment on column TS_TIMER_CONFIG.listener
  is '计时监听';
comment on column TS_TIMER_CONFIG.remark
  is '备注';


-- Create table
create table TS_TIMER
(
  uuid                     VARCHAR2(255 CHAR) not null,
  create_time              TIMESTAMP(6),
  creator                  VARCHAR2(255 CHAR),
  modifier                 VARCHAR2(255 CHAR),
  modify_time              TIMESTAMP(6),
  rec_ver                  NUMBER(10),
  config_uuid              VARCHAR2(50 CHAR),
  init_time_limit          NUMBER(10,1),
  init_due_time            TIMESTAMP(6),
  start_time               TIMESTAMP(6),
  last_start_time          TIMESTAMP(6),
  timing_mode              VARCHAR2(6 CHAR),
  time_limit_type          VARCHAR2(6 CHAR),
  time_limit			   NUMBER(10,1),
  due_time 			       TIMESTAMP(6),
  status                   NUMBER(10),
  timing_state             NUMBER(10),
  due_doing_done           NUMBER(1),
  over_due_doing_done      NUMBER(1),
  listener                 VARCHAR2(150 CHAR)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table ts_timer
  add constraint ts_timer_uuid primary key (UUID);
-- Add comments to the table 
comment on table TS_TIMER
  is '计时实例';
-- Add comments to the columns 
comment on column TS_TIMER.uuid
  is 'UUID，系统字段';
comment on column TS_TIMER.create_time
  is '创建时间';
comment on column TS_TIMER.creator
  is '创建人';
comment on column TS_TIMER.modifier
  is '修改人';
comment on column TS_TIMER.modify_time
  is '修改时间';
comment on column TS_TIMER.rec_ver
  is '版本号';
comment on column TS_TIMER.config_uuid
  is '计时配置UUID';
comment on column TS_TIMER.init_time_limit
  is '计算后初始化的办理时限数字';
comment on column TS_TIMER.init_due_time
  is '计算后初始化的办理时限日期';
comment on column TS_TIMER.start_time
  is '开始计时时间';
comment on column TS_TIMER.last_start_time
  is '最新开始时间';
comment on column TS_TIMER.timing_mode
  is '计时方式';
comment on column TS_TIMER.time_limit_type
  is '时限类型';
comment on column TS_TIMER.time_limit
  is '最新的办理时限';
comment on column TS_TIMER.due_time
  is '到期时间';
comment on column TS_TIMER.status
  is '计时器是运行状态(0未启动、1已启动、2暂停、3结束)';
comment on column TS_TIMER.timing_state
  is '计时状态(0正常、1预警、2到期、3逾期)';
comment on column TS_TIMER.due_doing_done
  is '到期处理是否已经处理过';
comment on column TS_TIMER.over_due_doing_done
  is '逾期处理是否已经处理过';
comment on column TS_TIMER.listener
  is '计时监听';


-- Create table
create table TS_TIMER_LOG
(
  uuid                     VARCHAR2(255 CHAR) not null,
  create_time              TIMESTAMP(6),
  creator                  VARCHAR2(255 CHAR),
  modifier                 VARCHAR2(255 CHAR),
  modify_time              TIMESTAMP(6),
  rec_ver                  NUMBER(10),
  timer_uuid               VARCHAR2(50 CHAR),
  log_time                 TIMESTAMP(6),
  type                     VARCHAR2(50 CHAR),
  time_limit			   VARCHAR2(50 CHAR),
  due_time 			       TIMESTAMP(6),
  remark                   VARCHAR2(2000 CHAR)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table ts_timer_log
  add constraint ts_timer_log_uuid primary key (UUID);
-- Add comments to the table 
comment on table TS_TIMER_LOG
  is '计时实例';
-- Add comments to the columns 
comment on column TS_TIMER_LOG.uuid
  is 'UUID，系统字段';
comment on column TS_TIMER_LOG.create_time
  is '创建时间';
comment on column TS_TIMER_LOG.creator
  is '创建人';
comment on column TS_TIMER_LOG.modifier
  is '修改人';
comment on column TS_TIMER_LOG.modify_time
  is '修改时间';
comment on column TS_TIMER_LOG.rec_ver
  is '版本号';
comment on column TS_TIMER_LOG.timer_uuid
  is '计时器UUID';
comment on column TS_TIMER_LOG.log_time
  is '记录时间';
comment on column TS_TIMER_LOG.type
  is '记录类型启动START、暂停PAUSE、重启RESUME、结束END、到期DUE_DOING、逾期OVER_DUE、强制终止到期处理FORCE_STOP_DUE_DOING、信息INFO、错误ERROR';
comment on column TS_TIMER_LOG.time_limit
  is '办理时限数字或日期';
comment on column TS_TIMER_LOG.due_time
  is '到期时间';
comment on column TS_TIMER_LOG.remark
  is '备注';
  