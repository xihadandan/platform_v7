-------------------------------------------------
-- Export file for user USR_PT_6_0_DEV@123_DB1 --
-- Created by Leo on 2018/6/21, 16:34:24 --------
-------------------------------------------------


create table DMS_DATA_ATTENTION_MARK
(
  uuid           VARCHAR2(64) not null,
  data_uuid      VARCHAR2(64) not null,
  user_id        VARCHAR2(64) not null,
  creator        VARCHAR2(255 CHAR),
  create_time    TIMESTAMP(6),
  modifier       VARCHAR2(255 CHAR),
  modify_time    TIMESTAMP(6),
  rec_ver        NUMBER(10),
  system_unit_id VARCHAR2(64) not null
)
;
comment on table DMS_DATA_ATTENTION_MARK
  is '数据标记-关注';
comment on column DMS_DATA_ATTENTION_MARK.data_uuid
  is '关注数据的UUID';
comment on column DMS_DATA_ATTENTION_MARK.user_id
  is '用户ID';
create index SYS_C00202268 on DMS_DATA_ATTENTION_MARK (DATA_UUID, USER_ID);
alter table DMS_DATA_ATTENTION_MARK
  add primary key (UUID);


create table DMS_DATA_COLLECT_MARK
(
  uuid           VARCHAR2(64) not null,
  data_uuid      VARCHAR2(64) not null,
  user_id        VARCHAR2(64) not null,
  creator        VARCHAR2(255 CHAR),
  create_time    TIMESTAMP(6),
  modifier       VARCHAR2(255 CHAR),
  modify_time    TIMESTAMP(6),
  rec_ver        NUMBER(10),
  system_unit_id VARCHAR2(64) not null
)
;
comment on table DMS_DATA_COLLECT_MARK
  is '数据标记-收藏';
comment on column DMS_DATA_COLLECT_MARK.data_uuid
  is '收藏数据的UUID';
comment on column DMS_DATA_COLLECT_MARK.user_id
  is '用户ID';
create index SYS_C00202269 on DMS_DATA_COLLECT_MARK (DATA_UUID, USER_ID);
alter table DMS_DATA_COLLECT_MARK
  add primary key (UUID);




create table DMS_DATA_READ_MARK
(
  uuid           VARCHAR2(64) not null,
  data_uuid      VARCHAR2(64) not null,
  user_id        VARCHAR2(64) not null,
  creator        VARCHAR2(255 CHAR),
  create_time    TIMESTAMP(6),
  modifier       VARCHAR2(255 CHAR),
  modify_time    TIMESTAMP(6),
  rec_ver        NUMBER(10),
  system_unit_id VARCHAR2(64) not null
)
;
comment on table DMS_DATA_READ_MARK
  is '数据标记-已读';
comment on column DMS_DATA_READ_MARK.data_uuid
  is '已读数据的UUID';
comment on column DMS_DATA_READ_MARK.user_id
  is '用户ID';
create index SYS_C00202267 on DMS_DATA_READ_MARK (DATA_UUID, USER_ID);
alter table DMS_DATA_READ_MARK
  add primary key (UUID);


