-- Alter table 
alter table APP_FUNCTION
  storage
  (
    next 8
  )
;
-- Add/modify columns 
alter table APP_FUNCTION add exportable NUMBER(1);
-- Add comments to the columns 
comment on column APP_FUNCTION.exportable
  is '功能信息是否可导出';
