-- ADD/MODIFY COLUMNS 
ALTER TABLE WM_MAIL_USER ADD LIMIT_CAPACITY NUMBER(12);
-- ADD COMMENTS TO THE COLUMNS 
COMMENT ON COLUMN WM_MAIL_USER.LIMIT_CAPACITY
  IS '容量限制';
ALTER TABLE WM_MAIL_USER ADD USED_CAPACITY NUMBER(12);
-- ADD COMMENTS TO THE COLUMNS 
COMMENT ON COLUMN WM_MAIL_USER.USED_CAPACITY
  IS '已使用容量';
  
  
  
-- Create table
create table WM_MAIL_USE_CAPACITY
(
  uuid           VARCHAR2(255) not null,
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(255),
  modify_time    TIMESTAMP(6),
  modifier       VARCHAR2(255),
  rec_ver        NUMBER(10) not null,
  user_id        VARCHAR2(255),
  mailbox        VARCHAR2(255),
  capacity_used  NUMBER(12) default 0,
  system_unit_id VARCHAR2(255)
);
-- Add comments to the table 
comment on table WM_MAIL_USE_CAPACITY
  is '邮件容量使用空间情况';
-- Add comments to the columns 
comment on column WM_MAIL_USE_CAPACITY.mailbox
  is '邮件文件夹';
comment on column WM_MAIL_USE_CAPACITY.capacity_used
  is '已用空间容量';


  
  
-- Add/modify columns 
alter table WM_MAIL_CONFIG add deadline_capacity number(12);
alter table WM_MAIL_CONFIG add default_capacity number(12);
-- Add comments to the columns 
comment on column WM_MAIL_CONFIG.deadline_capacity
  is '剩余容量红线值';
comment on column WM_MAIL_CONFIG.default_capacity
  is '用户默认容量';
