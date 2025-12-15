-- Alter table 
alter table APP_PAGE_DEFINITION
  storage
  (
    next 1
  )
;
-- Add/modify columns 
alter table APP_PAGE_DEFINITION add wtype VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column APP_PAGE_DEFINITION.wtype
  is '页面容器类型';
