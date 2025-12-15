-- Add/modify columns 
alter table WF_TASK_TIMER add auto_update_limit_time NUMBER(1);
-- Add comments to the columns 
comment on column WF_TASK_TIMER.auto_update_limit_time
  is '自动更新时限';
