-- Add/modify columns 
alter table WF_TASK_SUB_FLOW add completion_state NUMBER(10) default 0;
-- Add comments to the columns 
comment on column WF_TASK_SUB_FLOW.completion_state
  is '完成状态 0运行中、1正常结束、2终止、3撤销、4退回';

update WF_TASK_SUB_FLOW t set t.completion_state = 1 where t.completed = 1;
commit;
  