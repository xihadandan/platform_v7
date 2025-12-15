-- Alter table 
alter table DMS_DATA_VERSION
  storage
  (
    next 1
  )
;
-- Add/modify columns 
alter table DMS_DATA_VERSION add title VARCHAR2(255 CHAR);
alter table DMS_DATA_VERSION add remark VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column DMS_DATA_VERSION.title
  is '当前版本标题';
comment on column DMS_DATA_VERSION.remark
  is '当前版本备注';
