-- Alter table 
alter table APP_FUNCTION
  storage
  (
    next 8
  )
;
-- Add/modify columns 
alter table APP_FUNCTION add export_type VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column APP_FUNCTION.export_type
  is '功能导出类型';
