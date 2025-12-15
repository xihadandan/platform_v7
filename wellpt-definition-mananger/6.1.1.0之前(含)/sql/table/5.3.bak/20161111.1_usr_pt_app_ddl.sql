-- Alter table 
alter table APP_PAGE_DEFINITION
  storage
  (
    next 8
  )
;
-- Add/modify columns 
alter table APP_PAGE_DEFINITION add title VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column APP_PAGE_DEFINITION.title
  is '标题';


-- Alter table 
alter table APP_WIDGET_DEFINITION
  storage
  (
    next 8
  )
;
-- Add/modify columns 
alter table APP_WIDGET_DEFINITION add title VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column APP_WIDGET_DEFINITION.title
  is '标题';



update app_page_definition t set t.title = t.name where t.title is null;
update app_widget_definition t set t.title = t.name where t.title is null;
