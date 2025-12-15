-- Add/modify columns 
alter table TS_TIMER_CONFIG add timing_mode_type VARCHAR2(10 CHAR);
alter table TS_TIMER_CONFIG add timing_mode_type_name VARCHAR2(20 CHAR);
alter table TS_TIMER_CONFIG add timing_mode_unit VARCHAR2(10 CHAR);
alter table TS_TIMER_CONFIG add timing_mode_unit_name VARCHAR2(20 CHAR);
alter table TS_TIMER_CONFIG add time_limit_type_name VARCHAR2(20 CHAR);
-- Add comments to the columns 
comment on column TS_TIMER_CONFIG.timing_mode_type
  is '计时方式类型: 1工作日、2工作日(一天24小时)、3自然日';
comment on column TS_TIMER_CONFIG.timing_mode_type_name
  is '计时方式类型名称';
comment on column TS_TIMER_CONFIG.timing_mode_unit
  is '计时方式单位: 1按天、2按小时、3按分钟';
comment on column TS_TIMER_CONFIG.timing_mode_unit_name
  is '计时方式单位名称';
comment on column TS_TIMER_CONFIG.time_limit_type_name
  is '时限类型名称';