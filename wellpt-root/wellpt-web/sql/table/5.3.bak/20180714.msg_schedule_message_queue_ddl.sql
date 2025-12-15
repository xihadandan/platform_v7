-- Add/modify columns 
alter table MSG_MESSAGE_TEMPLATE add schedule_time VARCHAR2(64 CHAR);
-- Add comments to the columns 
comment on column MSG_MESSAGE_TEMPLATE.schedule_time
  is '定时时间';

  
  
  -- Create/Recreate indexes 
create index MSG_MESSAGE_TIME on MSG_MESSAGE_QUEUE (sent_time, create_time);




-- Create table
create table MSG_SCHEDULE_MESSAGE_QUEUE
(
  uuid           VARCHAR2(64) not null,
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(64),
  modifier       VARCHAR2(64),
  modify_time    TIMESTAMP(6),
  rec_ver        NUMBER(10),
  message        BLOB,
  template_id    VARCHAR2(120),
  send_time      TIMESTAMP(6),
  system_unit_id VARCHAR2(64),
  business_id    VARCHAR2(64),
  name           VARCHAR2(120 CHAR)
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
comment on table MSG_SCHEDULE_MESSAGE_QUEUE
  is '定时消息队列';
-- Add comments to the columns 
comment on column MSG_SCHEDULE_MESSAGE_QUEUE.message
  is '消息体';
comment on column MSG_SCHEDULE_MESSAGE_QUEUE.template_id
  is '消息模板id';
comment on column MSG_SCHEDULE_MESSAGE_QUEUE.send_time
  is '发送时间';
comment on column MSG_SCHEDULE_MESSAGE_QUEUE.business_id
  is '业务ID';
-- Create/Recreate primary, unique and foreign key constraints 
alter table MSG_SCHEDULE_MESSAGE_QUEUE
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
create table MSG_SCHEDULE_MESSAGE_QUEUE_HIS
(
  uuid           VARCHAR2(64) not null,
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(64),
  modifier       VARCHAR2(64),
  modify_time    TIMESTAMP(6),
  rec_ver        NUMBER(10),
  message        BLOB,
  template_id    VARCHAR2(120),
  send_time      TIMESTAMP(6),
  system_unit_id VARCHAR2(64),
  name           VARCHAR2(120 CHAR)
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
alter table MSG_SCHEDULE_MESSAGE_QUEUE_HIS
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
