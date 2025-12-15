-- Add/modify columns 
alter table WF_TASK_SUB_FLOW add copy_bot_rule_id VARCHAR2(255 CHAR);
alter table WF_TASK_SUB_FLOW add sync_bot_rule_id VARCHAR2(255 CHAR);
alter table WF_TASK_SUB_FLOW add return_with_over NUMBER(1);
alter table WF_TASK_SUB_FLOW add return_with_direction NUMBER(1);
alter table WF_TASK_SUB_FLOW add return_direction_id VARCHAR2(255 CHAR);
alter table WF_TASK_SUB_FLOW add return_bot_rule_id VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column WF_TASK_SUB_FLOW.copy_bot_rule_id
  is '拷贝信息';
comment on column WF_TASK_SUB_FLOW.sync_bot_rule_id
  is '实时同步';
comment on column WF_TASK_SUB_FLOW.return_with_over
  is '办结时反馈';
comment on column WF_TASK_SUB_FLOW.return_with_direction
  is '流向反馈';
comment on column WF_TASK_SUB_FLOW.return_direction_id
  is '反馈流向';
comment on column WF_TASK_SUB_FLOW.return_bot_rule_id
  is '反馈信息';
