-- Alter table 
alter table WF_TASK_TIMER
  storage
  (
    next 8
  )
;
-- Add/modify columns 
alter table WF_TASK_TIMER add ignore_empty_limit_time NUMBER(1);
-- Add comments to the columns 
comment on column WF_TASK_TIMER.ignore_empty_limit_time
  is '时限为空时不计时';