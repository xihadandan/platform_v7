alter table DMS_FOLDER
    add LIBRARY_UUID varchar(50);

comment
on column DMS_FOLDER.LIBRARY_UUID is '夹所在库UUID';

alter table DMS_FILE
    add LIBRARY_UUID varchar(50);

comment
on column DMS_FILE.LIBRARY_UUID is '文件所在库UUID';

update DMS_FOLDER t1
set t1.LIBRARY_UUID = t1.UUID
where t1.PARENT_UUID is null
  and t1.TYPE = 0;
commit;