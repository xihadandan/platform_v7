alter table WF_OPINION_RULE
    add SYSTEM varchar2(64);
alter table WF_OPINION_RULE
    add TENANT varchar2(64);

comment
on column WF_OPINION_RULE.system is
    '归属系统';
comment
on column WF_OPINION_RULE.tenant is
    '归属租户';

update WF_OPINION_RULE t1
set t1.tenant = 'T001'
where t1.tenant is null;
commit;


alter table WF_DEF_CATEGORY
    add SYSTEM varchar2(64);
alter table WF_DEF_CATEGORY
    add TENANT varchar2(64);

comment
on column WF_DEF_CATEGORY.system is
    '归属系统';
comment
on column WF_DEF_CATEGORY.tenant is
    '归属租户';

update WF_DEF_CATEGORY t1
set t1.tenant = 'T001'
where t1.tenant is null;
commit;

