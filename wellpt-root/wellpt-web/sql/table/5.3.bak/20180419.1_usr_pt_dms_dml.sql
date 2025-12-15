-- Add/modify columns 
alter table DMS_FOLDER add system_unit_id VARCHAR2(12);
-- Add comments to the columns 
comment on column DMS_FOLDER.system_unit_id
  is '归属系统单位ID';
  

-- Add/modify columns 
alter table DMS_ROLE add system_unit_id VARCHAR2(12);
-- Add comments to the columns 
comment on column DMS_ROLE.system_unit_id
  is '归属系统单位ID';
  