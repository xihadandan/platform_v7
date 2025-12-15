alter table BOT_RULE_CONF
    add SYSTEM varchar2(64);
alter table BOT_RULE_CONF
    add TENANT varchar2(64);

comment
    on column BOT_RULE_CONF.system is
    '归属系统';
comment
    on column BOT_RULE_CONF.tenant is
    '归属租户';

update BOT_RULE_CONF t1
set t1.tenant = 'T001'
where t1.tenant is null;
commit;
