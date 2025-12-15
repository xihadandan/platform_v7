-- 1、邮箱账号表
create table wm_mail_user
(
  uuid               varchar2(255) not null,
  create_time        timestamp,
  creator            varchar2(255),
  modify_time        timestamp,
  modifier           varchar2(255),
  rec_ver            number(10) not null,
  user_id            varchar2(255),
  user_name			 varchar2(255),
  mail_user_name	 varchar2(255),
  mail_address		 varchar2(255),
  mail_password		 varchar2(255),
  mail_password_hash_algorithm	varchar2(255),
  reply_mail_address varchar2(255),
  pop3_server		 varchar2(255),
  pop3_port			 number(10),
  smpt_server 		 varchar2(255),
  smpt_port			 number(10),
  imap_server		 varchar2(255),
  imap_port			 number(10),
  is_default		 number(1), 
  PRIMARY KEY ("UUID")
)
;
-- Add comments to the columns 
comment on column wm_mail_user.uuid
  is 'uuid，系统字段';
comment on column wm_mail_user.create_time
  is '创建时间';
comment on column wm_mail_user.creator
  is '创建人';
comment on column wm_mail_user.modify_time
  is '修改时间';
comment on column wm_mail_user.modifier
  is '修改人';
comment on column wm_mail_user.rec_ver
  is '版本号';
comment on column wm_mail_user.user_id
  is '用户ID';
comment on column wm_mail_user.user_name
  is '用户名称';
comment on column wm_mail_user.mail_user_name
  is '邮箱用户名，用于验证邮箱密码的账号，默认为邮箱地址';
comment on column wm_mail_user.mail_address
  is '邮箱地址';
comment on column wm_mail_user.mail_password
  is '邮箱密码';
comment on column wm_mail_user.mail_password_hash_algorithm
  is '邮箱密码加密算法';
comment on column wm_mail_user.reply_mail_address
  is '回复邮箱地址，默认为空';
comment on column wm_mail_user.pop3_server
  is 'POP3服务器';
comment on column wm_mail_user.pop3_port
  is 'POP3服务器端口110';
comment on column wm_mail_user.smpt_server
  is '发送服务器';
comment on column wm_mail_user.smpt_port
  is '发送服务器端口25';
comment on column wm_mail_user.imap_server
  is 'IMAP服务器';
comment on column wm_mail_user.imap_port
  is 'IMAP服务器端口143';
comment on column wm_mail_user.is_default
  is '默认的邮件发送地址，用于有多个邮箱账号的默认邮箱';



-- 2、邮箱数据表

-- 2、邮箱数据表
create table wm_mailbox
(
  uuid               varchar2(255) not null,
  create_time        timestamp,
  creator            varchar2(255),
  modify_time        timestamp,
  modifier           varchar2(255),
  rec_ver            number(10) not null,
  user_id            varchar2(255),
  user_name       	 varchar2(255),
  subject       	 varchar2(255),
  send_time       	 timestamp,
  mail_size     	 number(19,0),
  from_user_name     varchar2(255),
  from_mail_address  varchar2(255),
  to_user_name       clob,
  to_mail_address    clob,
  cc_user_name       clob,
  cc_mail_address    clob,
  bcc_user_name      clob,
  bcc_mail_address   clob,
  content		     clob,
  repo_file_names    varchar2(2000),
  repo_file_uuids    varchar2(2000),
  mid         		 varchar2(255),
  mailbox_name       varchar2(255),
  is_read            varchar2(255),
  status             number(10), 
  PRIMARY KEY ("UUID")
)
;
-- Add comments to the columns 
comment on column wm_mailbox.uuid
  is 'uuid，系统字段';
comment on column wm_mailbox.create_time
  is '创建时间';
comment on column wm_mailbox.creator
  is '创建人';
comment on column wm_mailbox.modify_time
  is '修改时间';
comment on column wm_mailbox.modifier
  is '修改人';
comment on column wm_mailbox.rec_ver
  is '版本号';
comment on column wm_mailbox.user_id
  is '用户ID';
comment on column wm_mailbox.user_name
  is '用户名称';
comment on column wm_mailbox.subject
  is '主题';
comment on column wm_mailbox.send_time
  is '发送时间';
comment on column wm_mailbox.mail_size
  is '邮件大小';
comment on column wm_mailbox.from_user_name
  is '发送人名称';
comment on column wm_mailbox.from_mail_address
  is '邮箱地址';
comment on column wm_mailbox.to_user_name
  is '接收人名称，多个以分号隔开';
comment on column wm_mailbox.to_mail_address
  is '接收人邮箱地址/部门、职位、用户、群组id';
comment on column wm_mailbox.cc_user_name
  is '抄送人名称，多个以分号隔开';
comment on column wm_mailbox.cc_mail_address
  is '抄送人邮箱地址/部门、职位、用户、群组id';
comment on column wm_mailbox.bcc_user_name
  is '密送人名称，多个以分号隔开';
comment on column wm_mailbox.bcc_mail_address
  is '密送人邮箱地址/部门、职位、用户、群组id';
comment on column wm_mailbox.content
  is '邮件文本内容';
comment on column wm_mailbox.repo_file_names
  is 'mogodb附件名称，多个以分隔开';
comment on column wm_mailbox.repo_file_uuids
  is 'mogodb附件UUID，多个以分隔开';
comment on column wm_mailbox.mid
  is '邮件Message-ID，从邮件服务器上取到的邮件唯一标识';
comment on column wm_mailbox.mailbox_name
  is 'INBOX收件箱、OUTBOX发件箱';
comment on column wm_mailbox.is_read
  is '是否已阅';
comment on column wm_mailbox.status
  is '0草稿  1发送成功  2接收成功  -1删除  -2彻底删除';



-- 3、邮箱用户联系人
create table wm_mail_user_contact
(
  uuid               varchar2(255) not null,
  create_time        timestamp,
  creator            varchar2(255),
  modify_time        timestamp,
  modifier           varchar2(255),
  rec_ver            number(10) not null,
  user_id            varchar2(255),
  contact_user_name	 varchar2(255),
  contact_mail_address	varchar2(255), 
  PRIMARY KEY ("UUID")
)
;
-- Add comments to the columns 
comment on column wm_mail_user_contact.uuid
  is 'uuid，系统字段';
comment on column wm_mail_user_contact.create_time
  is '创建时间';
comment on column wm_mail_user_contact.creator
  is '创建人';
comment on column wm_mail_user_contact.modify_time
  is '修改时间';
comment on column wm_mail_user_contact.modifier
  is '修改人';
comment on column wm_mail_user_contact.rec_ver
  is '版本号';
comment on column wm_mail_user_contact.user_id
  is '用户ID';
comment on column wm_mail_user_contact.contact_user_name
  is '用户联系人名称';
comment on column wm_mail_user_contact.contact_mail_address
  is '用户联系人邮箱地址/部门、职位、用户、群组id';



-- 4、邮箱配置表
create table wm_mail_config
(
  uuid               varchar2(255) not null,
  create_time        timestamp,
  creator            varchar2(255),
  modify_time        timestamp,
  modifier           varchar2(255),
  rec_ver            number(10) not null,
  domain	 		 varchar2(255),
  pop3_server		 varchar2(255),
  pop3_port			 number(10),
  smpt_server 		 varchar2(255),
  smpt_port			 number(10),
  imap_server		 varchar2(255),
  imap_port			 number(10),
  keep_on_server	 number(1), 
  PRIMARY KEY ("UUID")
)
;
-- Add comments to the columns 
comment on column wm_mail_config.uuid
  is 'uuid，系统字段';
comment on column wm_mail_config.create_time
  is '创建时间';
comment on column wm_mail_config.creator
  is '创建人';
comment on column wm_mail_config.modify_time
  is '修改时间';
comment on column wm_mail_config.modifier
  is '修改人';
comment on column wm_mail_config.rec_ver
  is '版本号';
comment on column wm_mail_config.domain
  is '域名';
comment on column wm_mail_config.pop3_server
  is 'POP3服务器';
comment on column wm_mail_config.pop3_port
  is 'POP3服务器端口110';
comment on column wm_mail_config.smpt_server
  is '发送服务器';
comment on column wm_mail_config.smpt_port
  is '发送服务器端口25';
comment on column wm_mail_config.imap_server
  is 'IMAP服务器';
comment on column wm_mail_config.imap_port
  is 'IMAP服务器端口143';
comment on column wm_mail_config.keep_on_server
  is '在服务器保留备份';


