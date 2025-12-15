-- Add/modify columns
alter table DMS_DOC_EXCHANGE_CONFIG add DEFAULT_ENCRYPTION_LEVEL varchar2(64);
alter table DMS_DOC_EXCHANGE_CONFIG add DEFAULT_URGE_LEVEL varchar2(64);
-- Add comments to the columns
comment on column DMS_DOC_EXCHANGE_CONFIG.DEFAULT_ENCRYPTION_LEVEL
  is '默认文档密级';
comment on column DMS_DOC_EXCHANGE_CONFIG.DEFAULT_URGE_LEVEL
  is '默认文档缓急程度';
