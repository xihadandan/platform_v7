-- Add/modify columns 
alter table TASK_JOB_DETAILS add auto_scheduling NUMBER(1);
-- Add comments to the columns 
comment on column TASK_JOB_DETAILS.auto_scheduling
  is '自动调度';
