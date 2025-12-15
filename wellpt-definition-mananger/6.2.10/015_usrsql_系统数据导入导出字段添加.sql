-- Add/modify columns
alter table DATA_EXPORT_RECORD add PROCESS_LOG varchar2(4000);
-- Add comments to the columns
comment on column DATA_EXPORT_RECORD.PROCESS_LOG
  is '过程日志';


-- Add/modify columns
alter table DATA_IMPORT_RECORD add PROCESS_LOG varchar2(4000);
-- Add comments to the columns
comment on column DATA_IMPORT_RECORD.PROCESS_LOG
  is '过程日志';
