-- Create table
create table PT_T_WORK_RECORD
(
  uuid           VARCHAR2(255) not null,
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(255),
  modifier       VARCHAR2(255),
  modify_time    TIMESTAMP(6),
  rec_ver        NUMBER(10,2),
  biz_id         VARCHAR2(255),
  ding_record_id VARCHAR2(255),
  ding_user_id   VARCHAR2(255),
  operate_time   TIMESTAMP(6),
  record_status  NUMBER,
  title          VARCHAR2(1024),
  user_name      VARCHAR2(255),
  err_code       VARCHAR2(255),
  err_msg        VARCHAR2(1024),
  form_item_list VARCHAR2(2048),
  url            VARCHAR2(2048)
);
-- Add comments to the table 
comment on table PT_T_WORK_RECORD
  is '钉钉待办任务表';
-- Add comments to the columns 
comment on column PT_T_WORK_RECORD.uuid
  is '唯一主键';
comment on column PT_T_WORK_RECORD.create_time
  is '创建时间';
comment on column PT_T_WORK_RECORD.creator
  is '创建人';
comment on column PT_T_WORK_RECORD.modifier
  is '更新人';
comment on column PT_T_WORK_RECORD.modify_time
  is '更新时间';
comment on column PT_T_WORK_RECORD.rec_ver
  is '数据版本';
comment on column PT_T_WORK_RECORD.record_status
  is '0：未更新；1：已更新；2：平台调取失败
';
comment on column PT_T_WORK_RECORD.title
  is '待办事项的标题';
comment on column PT_T_WORK_RECORD.user_name
  is '待办人名称';
comment on column PT_T_WORK_RECORD.err_code
  is '钉钉错误码';
comment on column PT_T_WORK_RECORD.err_msg
  is '钉钉错误信息';
comment on column PT_T_WORK_RECORD.form_item_list
  is '推送内容';
comment on column PT_T_WORK_RECORD.url
  is '待办事项的跳转链接';
-- Create/Recreate primary, unique and foreign key constraints 
alter table PT_T_WORK_RECORD
  add constraint PK_PT_T_WORK_RECORD primary key (UUID)
  using index 
;
