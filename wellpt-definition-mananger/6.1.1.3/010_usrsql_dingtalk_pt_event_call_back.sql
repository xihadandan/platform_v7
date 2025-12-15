-- Create table
create table PT_T_EVENT_CALL_BACK
(
  uuid            VARCHAR2(255) not null,
  create_time     TIMESTAMP(6),
  creator         VARCHAR2(255),
  modifier        VARCHAR2(255),
  modify_time     TIMESTAMP(6),
  rec_ver         NUMBER(10,2),
  event_type      VARCHAR2(255),
  ding_time_stamp VARCHAR2(255),
  ding_user_id    VARCHAR2(255),
  ding_corp_id    VARCHAR2(255),
  status          NUMBER,
  opt_time        TIMESTAMP(6),
  ding_dept_id    VARCHAR2(255),
  remark          VARCHAR2(255)
);
-- Add comments to the table 
comment on table PT_T_EVENT_CALL_BACK
  is '通讯录事件回调数据表';
-- Add comments to the columns 
comment on column PT_T_EVENT_CALL_BACK.uuid
  is '唯一主键';
comment on column PT_T_EVENT_CALL_BACK.create_time
  is '创建时间';
comment on column PT_T_EVENT_CALL_BACK.creator
  is '创建人';
comment on column PT_T_EVENT_CALL_BACK.modifier
  is '更新人';
comment on column PT_T_EVENT_CALL_BACK.modify_time
  is '更新时间';
comment on column PT_T_EVENT_CALL_BACK.rec_ver
  is '数据版本';
comment on column PT_T_EVENT_CALL_BACK.ding_user_id
  is '用户发生变更的userid列表';
comment on column PT_T_EVENT_CALL_BACK.ding_corp_id
  is '发生通讯录变更的企业';
comment on column PT_T_EVENT_CALL_BACK.status
  is '0：未处理 1：已处理 2：处理失败
';
comment on column PT_T_EVENT_CALL_BACK.opt_time
  is '待办事项的标题';
comment on column PT_T_EVENT_CALL_BACK.ding_dept_id
  is '部门发生变更的deptId列表';
comment on column PT_T_EVENT_CALL_BACK.remark
  is '备注';
-- Create/Recreate primary, unique and foreign key constraints 
alter table PT_T_EVENT_CALL_BACK
  add constraint PK_PT_T_EVENT_CALL_BACK primary key (UUID)
  using index ;
