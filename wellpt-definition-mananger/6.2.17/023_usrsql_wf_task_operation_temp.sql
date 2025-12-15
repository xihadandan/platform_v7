

-- Create table
create table WF_TASK_OPERATION_TEMP
(
  UUID              VARCHAR2(36) not null,
  CREATE_TIME       TIMESTAMP(6),
  CREATOR           VARCHAR2(255),
  MODIFIER          VARCHAR2(255),
  MODIFY_TIME       TIMESTAMP(6),
  REC_VER           NUMBER(10),
  OPINION_TEXT    VARCHAR2(4000),
  ASSIGNEE    VARCHAR2(36),
  ASSIGNEE_NAME    VARCHAR2(255),
  FLOW_INST_UUID    VARCHAR2(36),
  TASK_INST_UUID    VARCHAR2(36),
  TASK_IDENTITY_UUID    VARCHAR2(36),
  TASK_ID    VARCHAR2(36),
  TASK_NAME    VARCHAR2(255),
  constraint PK_WF_TASK_OPERATION_TEMP primary key (UUID)
);


-- Add comments to the table
comment on table WF_TASK_OPERATION_TEMP is '环节保存操作意见';
-- Add comments to the columns
comment on column WF_TASK_OPERATION_TEMP.UUID is '主键uuid';
comment on column WF_TASK_OPERATION_TEMP.CREATE_TIME is '创建时间';
comment on column WF_TASK_OPERATION_TEMP.CREATOR is '创建人';
comment on column WF_TASK_OPERATION_TEMP.MODIFIER is '更新人';
comment on column WF_TASK_OPERATION_TEMP.MODIFY_TIME is '更新时间';

comment on column WF_TASK_OPERATION_TEMP.OPINION_TEXT is '办理意见内容';
comment on column WF_TASK_OPERATION_TEMP.ASSIGNEE is '操作人ID';
comment on column WF_TASK_OPERATION_TEMP.ASSIGNEE_NAME is '操作人名称';
comment on column WF_TASK_OPERATION_TEMP.FLOW_INST_UUID is '所在流程实例';
comment on column WF_TASK_OPERATION_TEMP.TASK_INST_UUID is '所在任务实例';
comment on column WF_TASK_OPERATION_TEMP.TASK_IDENTITY_UUID is '所在待办实体UUID';
comment on column WF_TASK_OPERATION_TEMP.TASK_ID is '所在任务ID';
comment on column WF_TASK_OPERATION_TEMP.TASK_NAME is '所在任务名称';