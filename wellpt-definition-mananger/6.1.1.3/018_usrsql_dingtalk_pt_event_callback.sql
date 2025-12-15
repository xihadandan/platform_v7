-- Add/modify columns 
alter table PT_T_EVENT_CALL_BACK add event_name VARCHAR2(255);
-- Add comments to the columns 
comment on column PT_T_EVENT_CALL_BACK.event_name
  is '事件名称';