-- Add/modify columns 
alter table WF_FLOW_DEFINITION add multi_job_flow_type varchar2(32);
alter table WF_FLOW_DEFINITION add job_field varchar2(32);
-- Add comments to the columns 
comment on column WF_FLOW_DEFINITION.multi_job_flow_type
  is '多职流转设置';
comment on column WF_FLOW_DEFINITION.job_field
  is '职位选择字段';

-- Add/modify columns 
alter table WF_FLOW_INSTANCE add start_job_id varchar2(64);
-- Add comments to the columns 
comment on column WF_FLOW_INSTANCE.start_job_id
  is '发起人职位ID';
