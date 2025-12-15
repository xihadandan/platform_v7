-- Create table
create table WF_TASK_SUB_FLOW_DISPATCH
(
  uuid                   VARCHAR2(50 CHAR) primary key,
  create_time            TIMESTAMP(6),
  creator                VARCHAR2(50 CHAR),
  modifier               VARCHAR2(50 CHAR),
  modify_time            TIMESTAMP(6),
  rec_ver                NUMBER(10),
  parent_task_inst_uuid  VARCHAR2(50 CHAR),
  parent_flow_inst_uuid  VARCHAR2(50 CHAR),
  flow_inst_uuid         VARCHAR2(50 CHAR),
  task_sub_flow_uuid     VARCHAR2(50 CHAR),
  new_flow_id            VARCHAR2(50 CHAR),
  task_users             VARCHAR2(4000 CHAR),
  log_add_subflow        NUMBER(1),
  completion_state       NUMBER(10) default 0,
  result_msg             VARCHAR2(500 CHAR)
);
-- Add comments to the table 
comment on table WF_TASK_SUB_FLOW_DISPATCH
  is '子流程分发';
-- Add comments to the columns 
comment on column WF_TASK_SUB_FLOW_DISPATCH.uuid
  is 'UUID，系统字段';
comment on column WF_TASK_SUB_FLOW_DISPATCH.create_time
  is '创建时间';
comment on column WF_TASK_SUB_FLOW_DISPATCH.creator
  is '创建人';
comment on column WF_TASK_SUB_FLOW_DISPATCH.modifier
  is '修改人';
comment on column WF_TASK_SUB_FLOW_DISPATCH.modify_time
  is '修改时间';
comment on column WF_TASK_SUB_FLOW_DISPATCH.rec_ver
  is '版本号';
comment on column WF_TASK_SUB_FLOW_DISPATCH.parent_task_inst_uuid
  is '父环节实例UUID';
comment on column WF_TASK_SUB_FLOW_DISPATCH.parent_flow_inst_uuid
  is '父流程实例UUID';
comment on column WF_TASK_SUB_FLOW_DISPATCH.flow_inst_uuid
  is '子流程实例UUID';
comment on column WF_TASK_SUB_FLOW_DISPATCH.task_sub_flow_uuid
  is '子流程UUID';
comment on column WF_TASK_SUB_FLOW_DISPATCH.new_flow_id
  is '子流程ID';
comment on column WF_TASK_SUB_FLOW_DISPATCH.task_users
  is '办理人';
comment on column WF_TASK_SUB_FLOW_DISPATCH.log_add_subflow
  is '记录添加子流程日志';
comment on column WF_TASK_SUB_FLOW_DISPATCH.completion_state
  is '分发状态 0分发中、1分发成功、2分发失败';
comment on column WF_TASK_SUB_FLOW_DISPATCH.result_msg
  is '分发结果信息';
