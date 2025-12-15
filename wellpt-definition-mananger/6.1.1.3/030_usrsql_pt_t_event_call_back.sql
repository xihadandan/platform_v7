-- Add/modify columns 
alter table PT_T_EVENT_CALL_BACK add EVENT_NAME VARCHAR2(255);
-- Add comments to the columns 
comment on column PT_T_EVENT_CALL_BACK.EVENT_NAME
  is '事件名称';