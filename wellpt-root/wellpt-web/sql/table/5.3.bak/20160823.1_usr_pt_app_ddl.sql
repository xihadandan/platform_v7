-- Alter table 
alter table APP_PRODUCT_INTEGRATION
  storage
  (
    next 8
  )
;
-- Add/modify columns 
alter table APP_PRODUCT_INTEGRATION add data_path VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column APP_PRODUCT_INTEGRATION.data_path
  is '数据集成信息路径——/系统/模块|子模块/应用等';
