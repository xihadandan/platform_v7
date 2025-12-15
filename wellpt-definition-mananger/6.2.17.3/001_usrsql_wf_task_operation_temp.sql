
--  Add/modify columns
ALTER TABLE WF_TASK_OPERATION_TEMP ADD OPINION_LABEL VARCHAR2(255);
-- Add comments to the columns
comment on column WF_TASK_OPERATION_TEMP.OPINION_LABEL is '办理意见立场文本';

--  Add/modify columns
ALTER TABLE WF_TASK_OPERATION_TEMP ADD OPINION_VALUE VARCHAR2(255);
-- Add comments to the columns
comment on column WF_TASK_OPERATION_TEMP.OPINION_VALUE is '办理意见立场';

