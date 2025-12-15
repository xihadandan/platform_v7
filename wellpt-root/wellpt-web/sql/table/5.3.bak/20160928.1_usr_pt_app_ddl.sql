-- Alter table 
alter table APP_WIDGET_DEFINITION
  storage
  (
    next 1
  )
;
-- Add/modify columns 
alter table APP_WIDGET_DEFINITION add html clob;
-- Add comments to the columns 
comment on column APP_WIDGET_DEFINITION.html
  is '定义HTML信息';
