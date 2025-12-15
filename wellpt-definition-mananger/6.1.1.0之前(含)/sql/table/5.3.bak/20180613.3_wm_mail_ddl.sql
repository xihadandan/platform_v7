-- Add/modify columns 
alter table WM_MAIL_CONFIG add system_unit_id VARCHAR2(32);
-- Add/modify columns 
alter table WM_MAIL_USER add system_unit_id VARCHAR2(32);
-- Add comments to the columns 
comment on column WM_MAIL_USER.system_unit_id
  is '系统组织ID';
-- Add/modify columns 
alter table WM_MAIL_USER add is_inner_user NUMBER(1) default 1;
-- Add comments to the columns 
comment on column WM_MAIL_USER.is_inner_user
  is '是否是内部邮件账号';

  
  -- Add/modify columns 
alter table WM_MAIL_USER add is_pop_ssl NUMBER(1) default 0;
alter table WM_MAIL_USER add is_smtp_ssl NUMBER(1) default 0;
-- Add comments to the columns 
comment on column WM_MAIL_USER.is_pop_ssl
  is 'pop是否使用SSL';
comment on column WM_MAIL_USER.is_smtp_ssl
  is 'smtp是否使用SSL';
alter table WM_MAIL_USER add SYNC_MESSAGE_NUMBER NUMBER(12) default 0;
-- Add comments to the columns 
comment on column WM_MAIL_USER.SYNC_MESSAGE_NUMBER is '上次同步的邮件序号(用于下一次同步的起始)';
  
  -- Add/modify columns 
alter table WM_MAIL_USER rename column smpt_server to SMTP_SERVER;
alter table WM_MAIL_USER rename column smpt_port to SMTP_PORT;

-- Add/modify columns 
alter table WM_MAIL_CONFIG rename column smpt_server to SMTP_SERVER;
alter table WM_MAIL_CONFIG rename column smpt_port to SMTP_PORT;


-- Create/Recreate primary, unique and foreign key constraints 
alter table WM_MAIL_USER
  add constraint SYSUNQ_C00174260 unique (MAIL_ADDRESS, USER_ID);
  
  
  
  
  
  
  
  
  
  
  
  -- Create table
create table WM_MAIL_OPEN_SERVER
(
  uuid              VARCHAR2(64) not null,
  domain            VARCHAR2(64) not null,
  create_time       TIMESTAMP(6) not null,
  creator           VARCHAR2(64) not null,
  modify_time       TIMESTAMP(6),
  modifier          VARCHAR2(64),
  rec_ver           NUMBER(10),
  smtp_server       VARCHAR2(64),
  smtp_port         VARCHAR2(8),
  pop_server        VARCHAR2(64),
  pop_port          VARCHAR2(8),
  imap_server       VARCHAR2(64),
  imap_port         VARCHAR2(8),
  is_pop_ssl        NUMBER(1),
  is_imap_ssl       NUMBER(1),
  is_smtp_ssl       NUMBER(1)
)
tablespace OA_DATA
  pctfree 10
  initrans 1
  maxtrans 255;
-- Add comments to the table 
comment on table WM_MAIL_OPEN_SERVER
  is '第三方邮箱开放服务配置';
-- Add comments to the columns 
comment on column WM_MAIL_OPEN_SERVER.domain
  is '域名';
comment on column WM_MAIL_OPEN_SERVER.smtp_server
  is 'smtp服务器';
comment on column WM_MAIL_OPEN_SERVER.smtp_port
  is 'smtp服务器端口';
comment on column WM_MAIL_OPEN_SERVER.pop_server
  is 'POP服务器';
comment on column WM_MAIL_OPEN_SERVER.pop_port
  is 'POP服务器端口';
comment on column WM_MAIL_OPEN_SERVER.imap_server
  is 'imap服务器';
comment on column WM_MAIL_OPEN_SERVER.imap_port
  is 'imap服务器端口';
comment on column WM_MAIL_OPEN_SERVER.is_pop_ssl
  is 'pop是否使用SSL连接';
comment on column WM_MAIL_OPEN_SERVER.is_imap_ssl
  is 'imap是否使用SSL连接';
comment on column WM_MAIL_OPEN_SERVER.is_smtp_ssl
  is 'smtp是否使用SSL连接';
-- Create/Recreate primary, unique and foreign key constraints 
alter table WM_MAIL_OPEN_SERVER
  add primary key (UUID)
  using index 
  tablespace OA_DATA
  pctfree 10
  initrans 2
  maxtrans 255;
alter table WM_MAIL_OPEN_SERVER
  add constraint SYSUNQ_C00197235 unique (DOMAIN)
  using index 
  tablespace OA_DATA
  pctfree 10
  initrans 2
  maxtrans 255;
  
  
  insert into WM_MAIL_OPEN_SERVER (uuid, domain, create_time, creator, modify_time, modifier, rec_ver, smtp_server, smtp_port, pop_server, pop_port, imap_server, imap_port, is_pop_ssl, is_imap_ssl, is_smtp_ssl)
values ('1', 'qq.com', to_timestamp('13-06-2018 15:58:58.000000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'admin', to_timestamp('13-06-2018 15:58:58.000000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'admin', 0, 'smtp.qq.com', '465', 'pop.qq.com', '995', null, null, 1, null, 1);
insert into WM_MAIL_OPEN_SERVER (uuid, domain, create_time, creator, modify_time, modifier, rec_ver, smtp_server, smtp_port, pop_server, pop_port, imap_server, imap_port, is_pop_ssl, is_imap_ssl, is_smtp_ssl)
values ('2', 'outlook.com', to_timestamp('13-06-2018 16:01:16.000000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'admin', to_timestamp('13-06-2018 16:01:16.000000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'admin', 0, 'smtp-mail.outlook.com', '465', 'pop-mail.outlook.com', '995', null, null, 1, null, 1);
insert into WM_MAIL_OPEN_SERVER (uuid, domain, create_time, creator, modify_time, modifier, rec_ver, smtp_server, smtp_port, pop_server, pop_port, imap_server, imap_port, is_pop_ssl, is_imap_ssl, is_smtp_ssl)
values ('3', '163.com', to_timestamp('13-06-2018 16:02:41.000000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'admin', to_timestamp('13-06-2018 16:02:41.000000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'admin', 0, 'smtp.163.com', '465', 'pop.163.com', '995', null, null, 1, null, 1);
insert into WM_MAIL_OPEN_SERVER (uuid, domain, create_time, creator, modify_time, modifier, rec_ver, smtp_server, smtp_port, pop_server, pop_port, imap_server, imap_port, is_pop_ssl, is_imap_ssl, is_smtp_ssl)
values ('4', 'sina.com', to_timestamp('13-06-2018 17:24:46.000000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'admin', to_timestamp('13-06-2018 17:24:46.000000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'admin', 0, 'smtp.sina.com', '465', 'pop.sina.com', '995', null, null, 1, null, 1);
insert into WM_MAIL_OPEN_SERVER (uuid, domain, create_time, creator, modify_time, modifier, rec_ver, smtp_server, smtp_port, pop_server, pop_port, imap_server, imap_port, is_pop_ssl, is_imap_ssl, is_smtp_ssl)
values ('5', '126.com', to_timestamp('13-06-2018 17:28:12.000000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'admin', to_timestamp('13-06-2018 17:28:12.000000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'admin', 0, 'smtp.126.com', '465', 'pop.126.com', '995', null, null, 1, null, 1);
insert into WM_MAIL_OPEN_SERVER (uuid, domain, create_time, creator, modify_time, modifier, rec_ver, smtp_server, smtp_port, pop_server, pop_port, imap_server, imap_port, is_pop_ssl, is_imap_ssl, is_smtp_ssl)
values ('6', 'vip.qq.com', to_timestamp('13-06-2018 17:33:53.000000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'admin', to_timestamp('13-06-2018 17:33:53.000000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'admin', 0, 'smtp.qq.com', '465', 'pop.qq.com', '995', null, null, 1, null, 1);
insert into WM_MAIL_OPEN_SERVER (uuid, domain, create_time, creator, modify_time, modifier, rec_ver, smtp_server, smtp_port, pop_server, pop_port, imap_server, imap_port, is_pop_ssl, is_imap_ssl, is_smtp_ssl)
values ('7', 'vip.163.com', to_timestamp('13-06-2018 17:39:11.000000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'admin', to_timestamp('13-06-2018 17:39:11.000000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'admin', 0, 'smtp.163.com', '465', 'pop.163.com', '995', null, null, 1, null, 1);

