-- Create table APP_MY_CALENDAR_EVENT
create table APP_MY_CALENDAR_EVENT
(
  uuid                     VARCHAR2(255 CHAR) not null,
  creator                  VARCHAR2(255 CHAR),
  create_time              TIMESTAMP(6),
  modifier                 VARCHAR2(255 CHAR),
  modify_time              TIMESTAMP(6),
  rec_ver                  NUMBER(10),
  title                    VARCHAR2(255 CHAR),
  start_time               TIMESTAMP(6),
  end_time                 TIMESTAMP(6),
  event_content            CLOB,
  belong_obj_id            VARCHAR2(255),
  system_unit_id           VARCHAR2(12),
  address                  VARCHAR2(255),
  remark                   VARCHAR2(255),
  file_uuids               CLOB,
  join_users               VARCHAR2(255),
  public_range             VARCHAR2(255),
  notice_types             VARCHAR2(255),
  notice_objs              VARCHAR2(255),
  repeat_mark_id           VARCHAR2(255),
  is_all                   NUMBER(1) default 0,
  is_remind                NUMBER(1) default 0,
  is_repeat                NUMBER(1) default 0,
  repeat_conf              VARCHAR2(255),
  remind_conf              VARCHAR2(255),
  is_finish                NUMBER(1) default 0,
  repeat_period_start_time TIMESTAMP(0),
  repeat_period_end_time   TIMESTAMP(0),
  calendar_creator         VARCHAR2(12),
  remind_time              TIMESTAMP(0),
  remind_status            NUMBER(1) default 0,
  part_users               VARCHAR2(255),
  calendar_creator_name    VARCHAR2(255)
);
-- Add comments to the columns 
comment on column APP_MY_CALENDAR_EVENT.title
  is '标题';
comment on column APP_MY_CALENDAR_EVENT.start_time
  is '开始时间';
comment on column APP_MY_CALENDAR_EVENT.end_time
  is '结束时间';
comment on column APP_MY_CALENDAR_EVENT.event_content
  is '事项内容';
comment on column APP_MY_CALENDAR_EVENT.belong_obj_id
  is '事项归属对象ID';
comment on column APP_MY_CALENDAR_EVENT.address
  is '地址';
comment on column APP_MY_CALENDAR_EVENT.remark
  is '备注说明';
comment on column APP_MY_CALENDAR_EVENT.file_uuids
  is '附件UUID';
comment on column APP_MY_CALENDAR_EVENT.join_users
  is '参与人ID';
comment on column APP_MY_CALENDAR_EVENT.public_range
  is '公开范围';
comment on column APP_MY_CALENDAR_EVENT.notice_types
  is '通知方式';
comment on column APP_MY_CALENDAR_EVENT.notice_objs
  is '通知对象';
comment on column APP_MY_CALENDAR_EVENT.repeat_mark_id
  is '重复对象的UUID';
comment on column APP_MY_CALENDAR_EVENT.is_all
  is '是否全天';
comment on column APP_MY_CALENDAR_EVENT.is_remind
  is '是否提醒';
comment on column APP_MY_CALENDAR_EVENT.is_repeat
  is '是否重复';
comment on column APP_MY_CALENDAR_EVENT.repeat_conf
  is '重复的配置';
comment on column APP_MY_CALENDAR_EVENT.remind_conf
  is '提醒的配置选项';
comment on column APP_MY_CALENDAR_EVENT.is_finish
  is '是否完成';
comment on column APP_MY_CALENDAR_EVENT.repeat_period_start_time
  is '周期开始时间';
comment on column APP_MY_CALENDAR_EVENT.repeat_period_end_time
  is '周期结束时间';
comment on column APP_MY_CALENDAR_EVENT.calendar_creator
  is '日历本归属用户';
comment on column APP_MY_CALENDAR_EVENT.remind_time
  is '提醒时间';
comment on column APP_MY_CALENDAR_EVENT.remind_status
  is '提醒状态，0：还没提醒，1：已提醒';
comment on column APP_MY_CALENDAR_EVENT.part_users
  is '部分用户可见';
comment on column APP_MY_CALENDAR_EVENT.calendar_creator_name
  is '日历本创建者用户名';
-- Create/Recreate indexes 
create index IDX_APP_REPEATMARKID on APP_MY_CALENDAR_EVENT (REPEAT_MARK_ID);
-- Create/Recreate primary, unique and foreign key constraints 
alter table APP_MY_CALENDAR_EVENT
  add primary key (UUID);




-- Create table APP_MY_CALENDAR_GROUP
create table APP_MY_CALENDAR_GROUP
(
  uuid               VARCHAR2(255 CHAR) not null,
  creator            VARCHAR2(255 CHAR),
  create_time        TIMESTAMP(6),
  modifier           VARCHAR2(255 CHAR),
  modify_time        TIMESTAMP(6),
  rec_ver            NUMBER(10),
  group_name         VARCHAR2(255 CHAR),
  group_members      CLOB,
  group_members_name CLOB
);
-- Add comments to the columns 
comment on column APP_MY_CALENDAR_GROUP.group_name
  is '组名';
comment on column APP_MY_CALENDAR_GROUP.group_members
  is '组成员列表';
comment on column APP_MY_CALENDAR_GROUP.group_members_name
  is '组成员名称';
-- Create/Recreate indexes 
create unique index IDX_APP_CALENDAR_GOUP on APP_MY_CALENDAR_GROUP (CREATOR, GROUP_NAME);
-- Create/Recreate primary, unique and foreign key constraints 
alter table APP_MY_CALENDAR_GROUP
  add primary key (UUID);



-- Create table APP_MY_ATTENTION
create table APP_MY_ATTENTION
(
  uuid               VARCHAR2(255 CHAR) not null,
  creator            VARCHAR2(255 CHAR),
  create_time        TIMESTAMP(6),
  modifier           VARCHAR2(255 CHAR),
  modify_time        TIMESTAMP(6),
  rec_ver            NUMBER(10),
  attention_obj_id   VARCHAR2(255 CHAR),
  attention_obj_type VARCHAR2(3)
);
-- Add comments to the columns 
comment on column APP_MY_ATTENTION.attention_obj_id
  is '被关注的对象ID';
comment on column APP_MY_ATTENTION.attention_obj_type
  is '被关注的对象类型, U:用户，G：群组';
-- Create/Recreate indexes 
create index IDX_APP_ATTENTION_CREATOR on APP_MY_ATTENTION (CREATOR, ATTENTION_OBJ_TYPE);
-- Create/Recreate primary, unique and foreign key constraints 
alter table APP_MY_ATTENTION
  add primary key (UUID);
