-- Add/modify columns 
alter table WF_DEF_FORMAT add system_unit_id VARCHAR2(12);
-- Add comments to the columns 
comment on column WF_DEF_FORMAT.system_unit_id
  is '归属系统单位';

  
  
-- Add/modify columns 
alter table CD_PRINT_TEMPLATE add system_unit_id VARCHAR2(12);
-- Add comments to the columns 
comment on column CD_PRINT_TEMPLATE.system_unit_id
  is '归属系统单位';

  
  
-- Add/modify columns 
alter table CD_SERIAL_NUMBER add system_unit_id VARCHAR2(12);
-- Add comments to the columns 
comment on column CD_SERIAL_NUMBER.system_unit_id
  is '归属系统单位';

  
  
-- Add/modify columns 
alter table CD_SYSTEM_TABLE_ENTITY add system_unit_id VARCHAR2(12);
-- Add comments to the columns 
comment on column CD_SYSTEM_TABLE_ENTITY.system_unit_id
  is '归属系统单位';
  
  

-- Add/modify columns 
alter table CD_EXCEL_IMPORT_RULE add system_unit_id VARCHAR2(12);
-- Add comments to the columns 
comment on column CD_EXCEL_IMPORT_RULE.system_unit_id
  is '归属系统单位';
  
  
  
-- Add/modify columns 
alter table EXCEL_EXPORT_DEFINITION add system_unit_id VARCHAR2(12);
-- Add comments to the columns 
comment on column EXCEL_EXPORT_DEFINITION.system_unit_id
  is '归属系统单位';
 
  
  
-- Add/modify columns 
alter table DATA_SOURCE_PROFILE add system_unit_id VARCHAR2(12);
-- Add comments to the columns 
comment on column DATA_SOURCE_PROFILE.system_unit_id
  is '归属系统单位';



-- Add/modify columns 
alter table CD_FIELD_EXT_DEFINITION add system_unit_id VARCHAR2(12);
-- Add comments to the columns 
comment on column CD_FIELD_EXT_DEFINITION.system_unit_id
  is '归属系统单位';
 
  
  
-- Add/modify columns 
alter table CD_DATA_MAPPER add system_unit_id VARCHAR2(12);
-- Add comments to the columns 
comment on column CD_DATA_MAPPER.system_unit_id
  is '归属系统单位';
 
  
  
-- Add/modify columns 
alter table LOG_USER_OPERATION add system_unit_id VARCHAR2(12);
-- Add comments to the columns 
comment on column LOG_USER_OPERATION.system_unit_id
  is '归属系统单位';
  
  

-- Add/modify columns 
alter table TASK_JOB_DETAILS add system_unit_id VARCHAR2(12);
-- Add comments to the columns 
comment on column TASK_JOB_DETAILS.system_unit_id
  is '归属系统单位';
  
  