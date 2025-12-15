-- Add/modify columns 
alter table DMS_ROLE add category VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column DMS_ROLE.category
  is '分类';
