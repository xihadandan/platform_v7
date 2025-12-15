-- Alter table 
alter table APP_PRODUCT_INTEGRATION
  storage
  (
    next 8
  )
;
-- Add/modify columns 
alter table APP_PRODUCT_INTEGRATION add app_page_reference number(1);
-- Add comments to the columns 
comment on column APP_PRODUCT_INTEGRATION.app_page_reference
  is '是否页面引用';
