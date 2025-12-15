-- Add/modify columns 
alter table WF_TASK_OPERATION add is_mobile_app NUMBER(1);
-- Add comments to the columns 
comment on column WF_TASK_OPERATION.is_mobile_app
  is '是否移动端应用的操作';
