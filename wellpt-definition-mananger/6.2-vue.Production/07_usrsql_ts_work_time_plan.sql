alter table TS_WORK_TIME_PLAN
    add SYSTEM varchar2(64);
alter table TS_WORK_TIME_PLAN
    add TENANT varchar2(64);

comment
    on column TS_WORK_TIME_PLAN.system is
    '归属系统';
comment
    on column TS_WORK_TIME_PLAN.tenant is
    '归属租户';

update TS_WORK_TIME_PLAN t1
set t1.tenant = 'T001'
where t1.tenant is null;
commit;


alter table TS_WORK_TIME_PLAN_HIS
    add SYSTEM varchar2(64);
alter table TS_WORK_TIME_PLAN_HIS
    add TENANT varchar2(64);

comment
    on column TS_WORK_TIME_PLAN_HIS.system is
    '归属系统';
comment
    on column TS_WORK_TIME_PLAN_HIS.tenant is
    '归属租户';

update TS_WORK_TIME_PLAN_HIS t1
set t1.tenant = 'T001'
where t1.tenant is null;
commit;


alter table TS_TIMER_CATEGORY
    add SYSTEM varchar2(64);
alter table TS_TIMER_CATEGORY
    add TENANT varchar2(64);

comment
    on column TS_TIMER_CATEGORY.system is
    '归属系统';
comment
    on column TS_TIMER_CATEGORY.tenant is
    '归属租户';

update TS_TIMER_CATEGORY t1
set t1.tenant = 'T001'
where t1.tenant is null;
commit;


alter table TS_TIMER_CONFIG
    add SYSTEM varchar2(64);
alter table TS_TIMER_CONFIG
    add TENANT varchar2(64);

comment
    on column TS_TIMER_CONFIG.system is
    '归属系统';
comment
    on column TS_TIMER_CONFIG.tenant is
    '归属租户';

update TS_TIMER_CONFIG t1
set t1.tenant = 'T001'
where t1.tenant is null;
commit;


alter table TS_HOLIDAY
    add SYSTEM varchar2(64);
alter table TS_HOLIDAY
    add TENANT varchar2(64);

comment
    on column TS_HOLIDAY.system is
    '归属系统';
comment
    on column TS_HOLIDAY.tenant is
    '归属租户';

update TS_HOLIDAY t1
set t1.tenant = 'T001'
where t1.tenant is null;
commit;


alter table TS_HOLIDAY_SCHEDULE
    add SYSTEM varchar2(64);
alter table TS_HOLIDAY_SCHEDULE
    add TENANT varchar2(64);

comment
    on column TS_HOLIDAY_SCHEDULE.system is
    '归属系统';
comment
    on column TS_HOLIDAY_SCHEDULE.tenant is
    '归属租户';

update TS_HOLIDAY_SCHEDULE t1
set t1.tenant = 'T001'
where t1.tenant is null;
commit;
