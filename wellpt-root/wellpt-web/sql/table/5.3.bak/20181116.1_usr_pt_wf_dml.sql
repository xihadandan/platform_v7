-- Add/modify columns 
alter table WF_TASK_TIMER add task_init_due_time TIMESTAMP(6);
-- Add comments to the columns 
comment on column WF_TASK_TIMER.task_init_due_time
  is '流程计算后初始化的办理时限日期，当时限类型为指定日期或表单日期字段时有效';
