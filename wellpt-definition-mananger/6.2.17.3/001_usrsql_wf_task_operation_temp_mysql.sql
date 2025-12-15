--  Add/modify columns
alter table wf_task_operation_temp add column opinion_label varchar(255) comment '办理意见立场文本';
alter table wf_task_operation_temp add column opinion_value varchar(255) comment '办理意见立场';


