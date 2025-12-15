-- Alter table 
alter table APP_PRODUCT_INTEGRATION
  storage
  (
    next 1
  )
;
-- Add/modify columns 
alter table APP_PRODUCT_INTEGRATION add app_system_uuid VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column APP_PRODUCT_INTEGRATION.app_system_uuid
  is '系统UUID';



-- Alter table 
alter table APP_PRODUCT_INTEGRATION
  storage
  (
    next 8
  )
;
-- Add/modify columns 
alter table APP_PRODUCT_INTEGRATION add app_page_uuid VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column APP_PRODUCT_INTEGRATION.app_page_uuid
  is '页面UUID';
