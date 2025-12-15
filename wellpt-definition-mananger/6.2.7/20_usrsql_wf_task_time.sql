-- Add/modify columns 
alter table WF_TASK_TIMER add work_time_plan_id VARCHAR2(50 CHAR);
comment on column WF_TASK_TIMER.work_time_plan_id
  is '工作时间方案ID';