-- Add/modify columns 
alter table BUSINESS_CATEGORY_ORG add id VARCHAR2(64);
-- Add comments to the columns 
comment on column BUSINESS_CATEGORY_ORG.id
  is 'ID';
