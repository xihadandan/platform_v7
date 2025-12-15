-- Add/modify columns 
alter table TS_TIMER_CONFIG modify id VARCHAR2(150 CHAR);
alter table TS_TIMER_CONFIG add category_uuid VARCHAR2(50 CHAR);
alter table TS_TIMER_CONFIG add time_limit_unit VARCHAR2(50 CHAR);
alter table TS_TIMER add work_time_plan_uuid VARCHAR2(50 CHAR);
alter table TS_TIMER add timer_data CLOB;
alter table WF_TASK_TIMER add timer_config_uuid VARCHAR2(50 CHAR);
alter table WF_TASK_TIMER add work_time_plan_uuid VARCHAR2(50 CHAR);
alter table WF_TASK_TIMER drop column TIMER_ID;
-- Add comments to the columns 
comment on column TS_TIMER_CONFIG.category_uuid
  is '计时分类UUID';
comment on column TS_TIMER_CONFIG.time_limit_unit
  is '时限单位1天、2小时、3分钟';
comment on column TS_TIMER.work_time_plan_uuid
  is '工作时间方案UUID';
comment on column TS_TIMER.timer_data
  is '计时器相关数据';
comment on column WF_TASK_TIMER.timer_config_uuid
  is '计时配置UUID';
comment on column WF_TASK_TIMER.work_time_plan_uuid
  is '工作时间方案UUID';
  
  
-- Create table
create table TS_TIMER_CATEGORY
(
  uuid                     VARCHAR2(255 CHAR) not null,
  create_time              TIMESTAMP(6),
  creator                  VARCHAR2(255 CHAR),
  modifier                 VARCHAR2(255 CHAR),
  modify_time              TIMESTAMP(6),
  rec_ver                  NUMBER(10),
  system_unit_id           VARCHAR2(20 CHAR),
  name                     VARCHAR2(150 CHAR),
  id                       VARCHAR2(50 CHAR),
  code                     VARCHAR2(50 CHAR),
  icon             	 	   VARCHAR2(50 CHAR),
  icon_color          	   VARCHAR2(50 CHAR),
  remark          		   VARCHAR2(200 CHAR)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table ts_timer_category
  add constraint ts_timer_category_uuid primary key (UUID);
-- Add comments to the table 
comment on table TS_TIMER_CATEGORY
  is '计时分类';
-- Add comments to the columns 
comment on column TS_TIMER_CATEGORY.uuid
  is 'UUID，系统字段';
comment on column TS_TIMER_CATEGORY.create_time
  is '创建时间';
comment on column TS_TIMER_CATEGORY.creator
  is '创建人';
comment on column TS_TIMER_CATEGORY.modifier
  is '修改人';
comment on column TS_TIMER_CATEGORY.modify_time
  is '修改时间';
comment on column TS_TIMER_CATEGORY.rec_ver
  is '版本号';
comment on column TS_TIMER_CATEGORY.system_unit_id
  is '系统单位ID';
comment on column TS_TIMER_CATEGORY.name
  is '名称';
comment on column TS_TIMER_CATEGORY.id
  is 'ID';
comment on column TS_TIMER_CATEGORY.code
  is '编号';
comment on column TS_TIMER_CATEGORY.icon
  is '图标';
comment on column TS_TIMER_CATEGORY.icon_color
  is '图标颜色';
comment on column TS_TIMER_CATEGORY.remark
  is '备注';
 
 
-- Create table
create table TS_HOLIDAY
(
  uuid                     VARCHAR2(255 CHAR) not null,
  create_time              TIMESTAMP(6),
  creator                  VARCHAR2(255 CHAR),
  modifier                 VARCHAR2(255 CHAR),
  modify_time              TIMESTAMP(6),
  rec_ver                  NUMBER(10),
  system_unit_id           VARCHAR2(20 CHAR),
  name             		   VARCHAR2(150 CHAR),
  id                	   VARCHAR2(50 CHAR),
  calendar_type            VARCHAR2(6 CHAR),
  holiday_date 			   VARCHAR2(20 CHAR),
  holiday_date_name 	   VARCHAR2(50 CHAR),
  tag			   		   VARCHAR2(2000 CHAR),
  remark                   VARCHAR2(200 CHAR)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table ts_holiday
  add constraint ts_holiday_uuid primary key (UUID);
-- Add comments to the table 
comment on table TS_HOLIDAY
  is '节假日';
-- Add comments to the columns 
comment on column TS_HOLIDAY.uuid
  is 'UUID，系统字段';
comment on column TS_HOLIDAY.create_time
  is '创建时间';
comment on column TS_HOLIDAY.creator
  is '创建人';
comment on column TS_HOLIDAY.modifier
  is '修改人';
comment on column TS_HOLIDAY.modify_time
  is '修改时间';
comment on column TS_HOLIDAY.rec_ver
  is '版本号';
comment on column TS_HOLIDAY.system_unit_id
  is '系统单位ID';
comment on column TS_HOLIDAY.name
  is '名称';
comment on column TS_HOLIDAY.id
  is 'ID';
comment on column TS_HOLIDAY.calendar_type
  is '历法类型 1：阳历2：阴历';
comment on column TS_HOLIDAY.holiday_date
  is '节假日，真实值存储对应的数字，用符号-连接，如阳历5月20日为5-20，阴历五月二十为5-20';
comment on column TS_HOLIDAY.holiday_date_name
  is '节假日日期名称';
comment on column TS_HOLIDAY.tag
  is '标签，取枚举类EnumHolidayTag，多个以分号隔开';
comment on column TS_HOLIDAY.remark
  is '备注';


-- Create table
create table TS_HOLIDAY_INSTANCE
(
  uuid                     VARCHAR2(255 CHAR) not null,
  create_time              TIMESTAMP(6),
  creator                  VARCHAR2(255 CHAR),
  modifier                 VARCHAR2(255 CHAR),
  modify_time              TIMESTAMP(6),
  rec_ver                  NUMBER(10),
  system_unit_id           VARCHAR2(20 CHAR),
  holiday_uuid             VARCHAR2(50 CHAR),
  name             		   VARCHAR2(150 CHAR),
  id                	   VARCHAR2(50 CHAR),
  calendar_type            VARCHAR2(6 CHAR),
  holiday_date 			   VARCHAR2(20 CHAR),
  year 			  		   VARCHAR2(6 CHAR),
  instance_date			   VARCHAR2(10 CHAR)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table ts_holiday_instance
  add constraint ts_holiday_instance_uuid primary key (UUID);
-- Add comments to the table 
comment on table TS_HOLIDAY_INSTANCE
  is '节假日历史实例';
-- Add comments to the columns 
comment on column TS_HOLIDAY_INSTANCE.uuid
  is 'UUID，系统字段';
comment on column TS_HOLIDAY_INSTANCE.create_time
  is '创建时间';
comment on column TS_HOLIDAY_INSTANCE.creator
  is '创建人';
comment on column TS_HOLIDAY_INSTANCE.modifier
  is '修改人';
comment on column TS_HOLIDAY_INSTANCE.modify_time
  is '修改时间';
comment on column TS_HOLIDAY_INSTANCE.rec_ver
  is '版本号';
comment on column TS_HOLIDAY_INSTANCE.system_unit_id
  is '系统单位ID';
comment on column TS_HOLIDAY_INSTANCE.holiday_uuid
  is '节假日UUID';
comment on column TS_HOLIDAY_INSTANCE.name
  is '名称';
comment on column TS_HOLIDAY_INSTANCE.id
  is 'ID';
comment on column TS_HOLIDAY_INSTANCE.calendar_type
  is '历法类型 1：阳历2：阴历';
comment on column TS_HOLIDAY_INSTANCE.holiday_date
  is '节假日，真实值存储对应的数字，用符号-连接，如阳历5月20日为5-20，阴历五月二十为5-20';
comment on column TS_HOLIDAY_INSTANCE.year
  is '年份';
comment on column TS_HOLIDAY_INSTANCE.instance_date
  is '具体实例日期';


-- Create table
create table TS_HOLIDAY_SCHEDULE
(
  uuid                     VARCHAR2(255 CHAR) not null,
  create_time              TIMESTAMP(6),
  creator                  VARCHAR2(255 CHAR),
  modifier                 VARCHAR2(255 CHAR),
  modify_time              TIMESTAMP(6),
  rec_ver                  NUMBER(10),
  system_unit_id           VARCHAR2(20 CHAR),
  holiday_uuid             VARCHAR2(50 CHAR),
  year             		   VARCHAR2(6 CHAR),
  from_date                VARCHAR2(10 CHAR),
  to_date            	   VARCHAR2(10 CHAR),
  makeup_date 			   VARCHAR2(2000 CHAR),
  remark			   	   VARCHAR2(200 CHAR)
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table ts_holiday_schedule
  add constraint ts_holiday_schedule_uuid primary key (UUID);
-- Add comments to the table 
comment on table TS_HOLIDAY_SCHEDULE
  is '节假日安排';
-- Add comments to the columns 
comment on column TS_HOLIDAY_SCHEDULE.uuid
  is 'UUID，系统字段';
comment on column TS_HOLIDAY_SCHEDULE.create_time
  is '创建时间';
comment on column TS_HOLIDAY_SCHEDULE.creator
  is '创建人';
comment on column TS_HOLIDAY_SCHEDULE.modifier
  is '修改人';
comment on column TS_HOLIDAY_SCHEDULE.modify_time
  is '修改时间';
comment on column TS_HOLIDAY_SCHEDULE.rec_ver
  is '版本号';
comment on column TS_HOLIDAY_SCHEDULE.system_unit_id
  is '系统单位ID';
comment on column TS_HOLIDAY_SCHEDULE.holiday_uuid
  is '引用的节假日UUID';
comment on column TS_HOLIDAY_SCHEDULE.year
  is '年份';
comment on column TS_HOLIDAY_SCHEDULE.from_date
  is '开始日期';
comment on column TS_HOLIDAY_SCHEDULE.to_date
  is '结束日期';
comment on column TS_HOLIDAY_SCHEDULE.makeup_date
  is '补班日期，多个以分号隔开';
comment on column TS_HOLIDAY_SCHEDULE.remark
  is '备注';


-- Create table
create table TS_WORK_TIME_PLAN
(
  uuid                     VARCHAR2(255 CHAR) not null,
  create_time              TIMESTAMP(6),
  creator                  VARCHAR2(255 CHAR),
  modifier                 VARCHAR2(255 CHAR),
  modify_time              TIMESTAMP(6),
  rec_ver                  NUMBER(10),
  system_unit_id           VARCHAR2(20 CHAR),
  name             		   VARCHAR2(150 CHAR),
  id             		   VARCHAR2(50 CHAR),
  code             		   VARCHAR2(50 CHAR),
  version                  VARCHAR2(10 CHAR),
  is_default               NUMBER(1),
  status                   NUMBER(10),
  active_time 			   TIMESTAMP(6),
  deactive_time			   TIMESTAMP(6),
  work_time_schedule	   CLOB,
  holiday_schedule		   CLOB
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table ts_work_time_plan
  add constraint ts_work_time_plan_uuid primary key (UUID);
-- Add comments to the table 
comment on table TS_WORK_TIME_PLAN
  is '工作时间方案';
-- Add comments to the columns 
comment on column TS_WORK_TIME_PLAN.uuid
  is 'UUID，系统字段';
comment on column TS_WORK_TIME_PLAN.create_time
  is '创建时间';
comment on column TS_WORK_TIME_PLAN.creator
  is '创建人';
comment on column TS_WORK_TIME_PLAN.modifier
  is '修改人';
comment on column TS_WORK_TIME_PLAN.modify_time
  is '修改时间';
comment on column TS_WORK_TIME_PLAN.rec_ver
  is '版本号';
comment on column TS_WORK_TIME_PLAN.system_unit_id
  is '系统单位ID';
comment on column TS_WORK_TIME_PLAN.name
  is '名称';
comment on column TS_WORK_TIME_PLAN.id
  is 'ID，自动生成';
comment on column TS_WORK_TIME_PLAN.code
  is '编号';
comment on column TS_WORK_TIME_PLAN.version
  is '版本号';
comment on column TS_WORK_TIME_PLAN.is_default
  is '是否默认工作时间方案，1是0否';
comment on column TS_WORK_TIME_PLAN.status
  is '状态0未生效、1已生效、2已失效';
comment on column TS_WORK_TIME_PLAN.active_time
  is '生效时间';
comment on column TS_WORK_TIME_PLAN.deactive_time
  is '失效时间';
comment on column TS_WORK_TIME_PLAN.work_time_schedule
  is '工作时间安排，json数组对象[{工作时间安排1}, {工作时间安排2}...]';
comment on column TS_WORK_TIME_PLAN.holiday_schedule
  is '节假日安排，json数组对象[{节假日安排1}, {节假日安排2}...]';


-- Create table
create table TS_WORK_TIME_PLAN_HIS
(
  uuid                     VARCHAR2(255 CHAR) not null,
  create_time              TIMESTAMP(6),
  creator                  VARCHAR2(255 CHAR),
  modifier                 VARCHAR2(255 CHAR),
  modify_time              TIMESTAMP(6),
  rec_ver                  NUMBER(10),
  system_unit_id           VARCHAR2(20 CHAR),
  work_time_plan_uuid      VARCHAR2(50 CHAR),
  name             		   VARCHAR2(150 CHAR),
  id             		   VARCHAR2(50 CHAR),
  code             		   VARCHAR2(50 CHAR),
  version                  VARCHAR2(10 CHAR),
  is_default               NUMBER(1),
  status                   NUMBER(10),
  active_time 			   TIMESTAMP(6),
  deactive_time			   TIMESTAMP(6),
  work_time_schedule	   CLOB,
  holiday_schedule		   CLOB
);
-- Create/Recreate primary, unique and foreign key constraints 
alter table ts_work_time_plan_his
  add constraint ts_work_time_plan_his_uuid primary key (UUID);
-- Add comments to the table 
comment on table TS_WORK_TIME_PLAN_HIS
  is '工作时间方案历史';
-- Add comments to the columns 
comment on column TS_WORK_TIME_PLAN_HIS.uuid
  is 'UUID，系统字段';
comment on column TS_WORK_TIME_PLAN_HIS.create_time
  is '创建时间';
comment on column TS_WORK_TIME_PLAN_HIS.creator
  is '创建人';
comment on column TS_WORK_TIME_PLAN_HIS.modifier
  is '修改人';
comment on column TS_WORK_TIME_PLAN_HIS.modify_time
  is '修改时间';
comment on column TS_WORK_TIME_PLAN_HIS.rec_ver
  is '版本号';
comment on column TS_WORK_TIME_PLAN_HIS.system_unit_id
  is '系统单位ID';
comment on column TS_WORK_TIME_PLAN_HIS.work_time_plan_uuid
  is '工作时间方案UUID';
comment on column TS_WORK_TIME_PLAN_HIS.name
  is '名称';
comment on column TS_WORK_TIME_PLAN_HIS.id
  is 'ID，自动生成';
comment on column TS_WORK_TIME_PLAN_HIS.code
  is '编号';
comment on column TS_WORK_TIME_PLAN_HIS.version
  is '版本号';
comment on column TS_WORK_TIME_PLAN_HIS.is_default
  is '是否默认工作时间方案，1是0否';
comment on column TS_WORK_TIME_PLAN_HIS.status
  is '状态0未生效、1已生效、2已失效';
comment on column TS_WORK_TIME_PLAN_HIS.active_time
  is '生效时间';
comment on column TS_WORK_TIME_PLAN_HIS.deactive_time
  is '失效时间';
comment on column TS_WORK_TIME_PLAN_HIS.work_time_schedule
  is '工作时间安排，json数组对象[{工作时间安排1}, {工作时间安排2}...]';
comment on column TS_WORK_TIME_PLAN_HIS.holiday_schedule
  is '节假日安排，json数组对象[{节假日安排1}, {节假日安排2}...]';

