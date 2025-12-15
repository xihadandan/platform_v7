 -- Add/modify columns 
alter table LOG_BUSINESS_OPERATION add operation2 VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column LOG_BUSINESS_OPERATION.operation2
  is '源操作类型';
