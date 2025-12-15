-- Add/modify columns 
alter table APP_PAGE_DEFINITION add user_id VARCHAR2(50 CHAR);
alter table APP_PAGE_DEFINITION add is_default NUMBER(1);
alter table APP_PAGE_DEFINITION add correlative_page_uuid VARCHAR2(50 CHAR);
alter table APP_PAGE_DEFINITION add theme VARCHAR2(50 CHAR);
alter table APP_PAGE_DEFINITION modify shared default 1;
alter table APP_PAGE_DEFINITION modify is_default default 0;
-- Add comments to the columns 
comment on column APP_PAGE_DEFINITION.user_id
  is '页面归属用户ID';
comment on column APP_PAGE_DEFINITION.is_default
  is '是否默认页面';
comment on column APP_PAGE_DEFINITION.correlative_page_uuid
  is '归属用户页面关联的页面UUID';
comment on column APP_PAGE_DEFINITION.theme
  is '页面主题';

