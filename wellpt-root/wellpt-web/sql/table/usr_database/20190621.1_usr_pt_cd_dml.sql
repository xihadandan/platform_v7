-- Add/modify columns 
alter table CD_DATA_IMPORT_LOG add import_ids clob;
-- Add comments to the columns 
comment on column CD_DATA_IMPORT_LOG.import_ids
  is '导入数据ID';
