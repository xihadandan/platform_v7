-- Add/modify columns
alter table DATA_EXPORT_TASK add REEXPORT_TIME timestamp;
-- Add comments to the columns
comment on column DATA_EXPORT_TASK.REEXPORT_TIME
  is '重新导出时间';


-- Add/modify columns
alter table DATA_IMPORT_TASK add REIMPORT_TIME timestamp;
-- Add comments to the columns
comment on column DATA_IMPORT_TASK.REIMPORT_TIME
  is '重新导入时间';


update data_export_task set reexport_time = export_time;
update data_import_task set reimport_time = import_time;
commit;
