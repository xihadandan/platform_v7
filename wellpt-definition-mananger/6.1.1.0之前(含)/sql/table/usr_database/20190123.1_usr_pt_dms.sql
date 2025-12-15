-- Add/modify columns 
alter table DMS_FOLDER add code VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column DMS_FOLDER.code
  is '编号';

update cd_data_store_definition t
    set t.default_condition = 'dy.' || t.default_condition
  where t.data_interface_name =
        'com.wellsoft.pt.dms.file.store.DmsFileDataStoreQuery'
    and t.default_condition like 'system_unit_id%';
commit;
