-- Create table
create table MSG_MESSAGE_RECENT_CONTACT
(
  uuid              VARCHAR2(64) not null,
  user_id           VARCHAR2(64) not null,
  system_unit_id    VARCHAR2(64) not null,
  contacter_name    VARCHAR2(64),
  contact_way       VARCHAR2(128),
  last_contact_time TIMESTAMP(6) not null,
  create_time       TIMESTAMP(6) not null,
  creator           VARCHAR2(64) not null,
  modify_time       TIMESTAMP(6),
  modifier          VARCHAR2(64),
  rec_ver           NUMBER(10)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table 
comment on table MSG_MESSAGE_RECENT_CONTACT
  is '消息最近联系人';
-- Add comments to the columns 
comment on column MSG_MESSAGE_RECENT_CONTACT.last_contact_time
  is '最近一次联系时间';
-- Create/Recreate primary, unique and foreign key constraints 
alter table MSG_MESSAGE_RECENT_CONTACT
  add primary key (UUID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
