alter table DMS_FOLDER
    add TYPE number(1);

comment
    on column DMS_FOLDER.TYPE is
    '夹类型，0文件库，1文件夹';

update DMS_FOLDER t1
set t1.TYPE = 0
where t1.PARENT_UUID is null;
update DMS_FOLDER t1
set t1.TYPE = 1
where t1.PARENT_UUID is not null;
commit;


alter table DMS_FOLDER
    add SYSTEM varchar2(64);
alter table DMS_FOLDER
    add TENANT varchar2(64);

comment
    on column DMS_FOLDER.system is
    '归属系统';
comment
    on column DMS_FOLDER.tenant is
    '归属租户';

update DMS_FOLDER t1
set t1.tenant = 'T001'
where t1.tenant is null;
commit;
