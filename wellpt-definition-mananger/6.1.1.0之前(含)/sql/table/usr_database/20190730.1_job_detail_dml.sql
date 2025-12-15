-- Add/modify columns 
alter table TASK_JOB_DETAILS add assign_ip VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column TASK_JOB_DETAILS.assign_ip
  is '指定ip执行';

alter table TASK_JOB_DETAILS add last_execute_instance VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column TASK_JOB_DETAILS.last_execute_instance
  is '最近执行实例';




