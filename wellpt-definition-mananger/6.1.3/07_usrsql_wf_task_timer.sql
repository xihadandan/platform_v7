-- Add/modify columns 
alter table WF_TASK_TIMER add ref_ids VARCHAR2(500 CHAR);
-- Add comments to the columns 
comment on column WF_TASK_TIMER.ref_ids
  is '引用的计时器ID';