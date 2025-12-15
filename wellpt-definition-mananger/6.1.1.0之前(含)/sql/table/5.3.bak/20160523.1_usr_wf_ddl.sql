-- Alter table 
alter table WF_TASK_FORM_OPINION_LOG
  storage
  (
    next 1
  )
;
-- Add/modify columns 
alter table WF_TASK_FORM_OPINION_LOG add status NUMBER(10);
-- Add comments to the columns 
comment on column WF_TASK_FORM_OPINION_LOG.status
  is '状态0正常、1、挂起、2删除';
