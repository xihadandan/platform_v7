-- Create table
create table msg_his_message_queue
(
  uuid             varchar(255) not null,
  create_time      timestamp,
  creator          varchar(255),
  modify_time      timestamp,
  modifier         varchar(255),
  rec_ver          number(10),
  name             varchar(255),
  template_id      varchar(255),
  code             varchar(255),
  sent_time        timestamp,
  correlation_uuid varchar(255),
  message          BLOB
)
;
-- Add comments to the columns 
comment on column msg_his_message_queue.uuid
  is 'uuid，系统字段';
comment on column msg_his_message_queue.create_time
  is '创建时间';
comment on column msg_his_message_queue.creator
  is '创建人';
comment on column msg_his_message_queue.modify_time
  is '修改时间';
comment on column msg_his_message_queue.modifier
  is '修改人';
comment on column msg_his_message_queue.rec_ver
  is '版本号';
comment on column msg_his_message_queue.name
  is '消息模板名称';
comment on column msg_his_message_queue.template_id
  is '消息模板ID';
comment on column msg_his_message_queue.code
  is '编号';
comment on column msg_his_message_queue.sent_time
  is '发送时间';
comment on column msg_his_message_queue.correlation_uuid
  is '消息回复时的相关联消息UUID';
comment on column msg_his_message_queue.message
  is 'Message的对象字节流';
-- Create/Recreate primary, unique and foreign key constraints 
alter table msg_his_message_queue
  add constraint msg_his_message_queue_uuid primary key (UUID);
