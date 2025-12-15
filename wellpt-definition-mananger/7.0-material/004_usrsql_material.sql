alter table CD_MATERIAL_DEFINITION
    add SYSTEM varchar2(64);
alter table CD_MATERIAL_DEFINITION
    add TENANT varchar2(64);

comment
    on column CD_MATERIAL_DEFINITION.system is
    '归属系统';
comment
    on column CD_MATERIAL_DEFINITION.tenant is
    '归属租户';

update CD_MATERIAL_DEFINITION t1
set t1.tenant = 'T001'
where t1.tenant is null;
commit;


alter table CD_MATERIAL_DEFINITION_HIS
    add SYSTEM varchar2(64);
alter table CD_MATERIAL_DEFINITION_HIS
    add TENANT varchar2(64);

comment
    on column CD_MATERIAL_DEFINITION_HIS.system is
    '归属系统';
comment
    on column CD_MATERIAL_DEFINITION_HIS.tenant is
    '归属租户';

update CD_MATERIAL_DEFINITION_HIS t1
set t1.tenant = 'T001'
where t1.tenant is null;
commit;