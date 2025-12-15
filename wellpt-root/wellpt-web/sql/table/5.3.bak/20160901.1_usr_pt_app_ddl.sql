-- Alter table 
alter table APP_FUNCTION
  storage
  (
    next 8
  )
;
-- Add/modify columns 
alter table APP_FUNCTION add definition_json VARCHAR2(2000 CHAR);
-- Add comments to the columns 
comment on column APP_FUNCTION.definition_json
  is '功能定义的JSON详细信息';
