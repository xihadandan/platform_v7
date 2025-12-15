alter table SN_SERIAL_NUMBER_CATEGORY
    add SYSTEM varchar2(64);
alter table SN_SERIAL_NUMBER_CATEGORY
    add TENANT varchar2(64);

comment
    on column SN_SERIAL_NUMBER_CATEGORY.system is
    '归属系统';
comment
    on column SN_SERIAL_NUMBER_CATEGORY.tenant is
    '归属租户';

update SN_SERIAL_NUMBER_CATEGORY t1
set t1.tenant = 'T001'
where t1.tenant is null;
commit;


alter table SN_SERIAL_NUMBER_DEFINITION
    add SYSTEM varchar2(64);
alter table SN_SERIAL_NUMBER_DEFINITION
    add TENANT varchar2(64);

comment
    on column SN_SERIAL_NUMBER_DEFINITION.system is
    '归属系统';
comment
    on column SN_SERIAL_NUMBER_DEFINITION.tenant is
    '归属租户';

update SN_SERIAL_NUMBER_DEFINITION t1
set t1.tenant = 'T001'
where t1.tenant is null;
commit;