alter table dyform_form_definition
    add system varchar2(64);
alter table dyform_form_definition
    add tenant varchar2(64);

comment
on column dyform_form_definition.system is
'归属系统';
comment
on column dyform_form_definition.tenant is
'归属租户';

update dyform_form_definition t1
set t1.tenant = 'T001'
where t1.tenant is null;
commit;