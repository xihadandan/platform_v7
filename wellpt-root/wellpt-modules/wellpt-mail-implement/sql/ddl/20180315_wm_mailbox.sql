
-- Create table
create table WM_MAIL_FOLDER
(
  uuid           VARCHAR2(64) not null,
  create_time    TIMESTAMP(6) not null,
  creator        VARCHAR2(64) not null,
  modify_time    TIMESTAMP(6),
  modifier       VARCHAR2(64),
  rec_ver        NUMBER(10),
  user_id        VARCHAR2(64) not null,
  system_unit_id VARCHAR2(64) not null,
  folder_name    VARCHAR2(64) not null,
  seq            NUMBER(3) default 1 not null,
  folder_code    VARCHAR2(64)
)
tablespace OA_DATA
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
comment on table WM_MAIL_FOLDER
  is '邮件文件夹';
-- Add comments to the columns 
comment on column WM_MAIL_FOLDER.create_time
  is '创建时间';
comment on column WM_MAIL_FOLDER.creator
  is '创建人';
comment on column WM_MAIL_FOLDER.modify_time
  is '修改时间';
comment on column WM_MAIL_FOLDER.modifier
  is '修改人';
comment on column WM_MAIL_FOLDER.rec_ver
  is '版本号';
comment on column WM_MAIL_FOLDER.user_id
  is '用户id';
comment on column WM_MAIL_FOLDER.system_unit_id
  is '系统组织id';
comment on column WM_MAIL_FOLDER.folder_name
  is '文件夹名称';
comment on column WM_MAIL_FOLDER.seq
  is '排序号';
comment on column WM_MAIL_FOLDER.folder_code
  is '文件编码';
-- Create/Recreate primary, unique and foreign key constraints 
alter table WM_MAIL_FOLDER
  add primary key (UUID)
  using index 
  tablespace OA_DATA
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
alter table WM_MAIL_FOLDER
  add constraint SYS_UNQ00167755 unique (USER_ID, FOLDER_NAME)
  using index 
  tablespace OA_DATA
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
  
  
  
  
  
  
  
  -- Create table
create table WM_MAIL_PAPER
(
  uuid                VARCHAR2(64) not null,
  create_time         TIMESTAMP(6) not null,
  creator             VARCHAR2(64) not null,
  modify_time         TIMESTAMP(6),
  modifier            VARCHAR2(64),
  is_default          NUMBER(1) default 0 not null,
  background_img_url  VARCHAR2(512) not null,
  background_color    VARCHAR2(12),
  background_position VARCHAR2(16),
  background_repeat   VARCHAR2(12),
  rec_ver             NUMBER(10),
  user_id             VARCHAR2(64),
  system_unit_id      VARCHAR2(64)
)
tablespace OA_DATA
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
comment on table WM_MAIL_PAPER
  is '信纸表';
-- Add comments to the columns 
comment on column WM_MAIL_PAPER.background_img_url
  is '背景图片地址';
comment on column WM_MAIL_PAPER.background_color
  is '背景颜色';
comment on column WM_MAIL_PAPER.background_position
  is '背景位置';
comment on column WM_MAIL_PAPER.background_repeat
  is '背景重复方向';
-- Create/Recreate primary, unique and foreign key constraints 
alter table WM_MAIL_PAPER
  add primary key (UUID)
  using index 
  tablespace OA_DATA
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

  
  insert into WM_MAIL_PAPER (UUID, CREATE_TIME, CREATOR, MODIFY_TIME, MODIFIER, IS_DEFAULT, BACKGROUND_IMG_URL, BACKGROUND_COLOR, BACKGROUND_POSITION, BACKGROUND_REPEAT, REC_VER, USER_ID, SYSTEM_UNIT_ID)
values ('d20a078d-9833-4808-99ed-e6a91c82d67a', '08-3月 -18 05.43.51.424000 下午', 'U0010000001', '08-3月 -18 05.43.51.424000 下午', 'U0010000001', 0, '/resources/pt/images/images/mail_paper/1.jpg', '#cdede2', 'TOP_CENTER', 'REPEAT_X', 0, '', '' );

insert into WM_MAIL_PAPER (UUID, CREATE_TIME, CREATOR, MODIFY_TIME, MODIFIER, IS_DEFAULT, BACKGROUND_IMG_URL, BACKGROUND_COLOR, BACKGROUND_POSITION, BACKGROUND_REPEAT, REC_VER, USER_ID, SYSTEM_UNIT_ID)
values ('5836bc15-49a6-483c-b985-f1b81c3bd821', '08-3月 -18 05.43.53.343000 下午', 'U0010000001', '08-3月 -18 05.43.53.343000 下午', 'U0010000001', 0, '/resources/pt/images/images/mail_paper/2.jpg', '#f6ffec', 'TOP_LEFT', 'NO_REPEAT', 0, '', '' );

insert into WM_MAIL_PAPER (UUID, CREATE_TIME, CREATOR, MODIFY_TIME, MODIFIER, IS_DEFAULT, BACKGROUND_IMG_URL, BACKGROUND_COLOR, BACKGROUND_POSITION, BACKGROUND_REPEAT, REC_VER, USER_ID, SYSTEM_UNIT_ID)
values ('ba414679-dfd7-4ac0-a34b-6003d1ad8755', '08-3月 -18 05.43.55.318000 下午', 'U0010000001', '08-3月 -18 05.43.55.318000 下午', 'U0010000001', 0, '/resources/pt/images/images/mail_paper/3.jpg', '#e4ebf5', 'BOTTOM_LEFT', 'REPEAT_X', 0, '', '' );

insert into WM_MAIL_PAPER (UUID, CREATE_TIME, CREATOR, MODIFY_TIME, MODIFIER, IS_DEFAULT, BACKGROUND_IMG_URL, BACKGROUND_COLOR, BACKGROUND_POSITION, BACKGROUND_REPEAT, REC_VER, USER_ID, SYSTEM_UNIT_ID)
values ('6af2b465-4033-4ac5-aeb2-272ec441f083', '08-3月 -18 05.41.44.622000 下午', 'U0010000001', '08-3月 -18 05.41.44.622000 下午', 'U0010000001', 0, '/resources/pt/images/images/mail_paper/5.jpg', '#e4ebf5', 'TOP_LEFT', 'REPEAT_X', 0, '', '' );

insert into WM_MAIL_PAPER (UUID, CREATE_TIME, CREATOR, MODIFY_TIME, MODIFIER, IS_DEFAULT, BACKGROUND_IMG_URL, BACKGROUND_COLOR, BACKGROUND_POSITION, BACKGROUND_REPEAT, REC_VER, USER_ID, SYSTEM_UNIT_ID)
values ('2e7e2794-6393-4c6b-a480-66eccf478033', '08-3月 -18 05.43.57.150000 下午', 'U0010000001', '08-3月 -18 05.43.57.150000 下午', 'U0010000001', 0, '/resources/pt/images/images/mail_paper/4.jpg', '#232019', 'TOP_LEFT', 'REPEAT_X', 0, '', '' );

insert into WM_MAIL_PAPER (UUID, CREATE_TIME, CREATOR, MODIFY_TIME, MODIFIER, IS_DEFAULT, BACKGROUND_IMG_URL, BACKGROUND_COLOR, BACKGROUND_POSITION, BACKGROUND_REPEAT, REC_VER, USER_ID, SYSTEM_UNIT_ID)
values ('e6be296c-2241-4c17-8234-bee01d07123a', '08-3月 -18 05.44.00.446000 下午', 'U0010000001', '08-3月 -18 05.44.00.446000 下午', 'U0010000001', 0, '/resources/pt/images/images/mail_paper/6.jpg', '#fefefe', '', '', 0, '', '' );

insert into WM_MAIL_PAPER (UUID, CREATE_TIME, CREATOR, MODIFY_TIME, MODIFIER, IS_DEFAULT, BACKGROUND_IMG_URL, BACKGROUND_COLOR, BACKGROUND_POSITION, BACKGROUND_REPEAT, REC_VER, USER_ID, SYSTEM_UNIT_ID)
values ('a622a750-c5fd-44f8-8033-f912f90826a5', '08-3月 -18 05.44.01.989000 下午', 'U0010000001', '08-3月 -18 05.44.01.989000 下午', 'U0010000001', 0, '/resources/pt/images/images/mail_paper/7.jpg', '#eaf4fd', '', '', 0, '', '' );

insert into WM_MAIL_PAPER (UUID, CREATE_TIME, CREATOR, MODIFY_TIME, MODIFIER, IS_DEFAULT, BACKGROUND_IMG_URL, BACKGROUND_COLOR, BACKGROUND_POSITION, BACKGROUND_REPEAT, REC_VER, USER_ID, SYSTEM_UNIT_ID)
values ('811ddcad-5be9-459c-9670-8a52734f82e7', '08-3月 -18 05.44.03.752000 下午', 'U0010000001', '08-3月 -18 05.44.03.752000 下午', 'U0010000001', 0, '/resources/pt/images/images/mail_paper/8.jpg', '#fffaf6', 'TOP_LEFT', 'NO_REPEAT', 0, '', '' );

insert into WM_MAIL_PAPER (UUID, CREATE_TIME, CREATOR, MODIFY_TIME, MODIFIER, IS_DEFAULT, BACKGROUND_IMG_URL, BACKGROUND_COLOR, BACKGROUND_POSITION, BACKGROUND_REPEAT, REC_VER, USER_ID, SYSTEM_UNIT_ID)
values ('c0ae0bc9-bb5f-449c-9aa1-aa78173d6839', '08-3月 -18 05.44.05.324000 下午', 'U0010000001', '08-3月 -18 05.44.05.324000 下午', 'U0010000001', 0, '/resources/pt/images/images/mail_paper/9.jpg', '#fbf7f4', 'TOP_LEFT', 'NO_REPEAT', 0, '', '' );

insert into WM_MAIL_PAPER (UUID, CREATE_TIME, CREATOR, MODIFY_TIME, MODIFIER, IS_DEFAULT, BACKGROUND_IMG_URL, BACKGROUND_COLOR, BACKGROUND_POSITION, BACKGROUND_REPEAT, REC_VER, USER_ID, SYSTEM_UNIT_ID)
values ('2111d62f-7835-43fb-aedb-668af334edf3', '08-3月 -18 05.44.06.908000 下午', 'U0010000001', '08-3月 -18 05.44.06.908000 下午', 'U0010000001', 0, '/resources/pt/images/images/mail_paper/10.jpg', '#a9dcd3', 'TOP_LEFT', 'NO_REPEAT', 0, '', '' );

insert into WM_MAIL_PAPER (UUID, CREATE_TIME, CREATOR, MODIFY_TIME, MODIFIER, IS_DEFAULT, BACKGROUND_IMG_URL, BACKGROUND_COLOR, BACKGROUND_POSITION, BACKGROUND_REPEAT, REC_VER, USER_ID, SYSTEM_UNIT_ID)
values ('445c9d9d-dc2f-4955-93c6-1c0bac58393b', '08-3月 -18 05.44.08.480000 下午', 'U0010000001', '08-3月 -18 05.44.08.480000 下午', 'U0010000001', 0, '/resources/pt/images/images/mail_paper/11.jpg', '#f2f2ea', 'CENTER_LEFT', 'NO_REPEAT', 0, '', '' );

insert into WM_MAIL_PAPER (UUID, CREATE_TIME, CREATOR, MODIFY_TIME, MODIFIER, IS_DEFAULT, BACKGROUND_IMG_URL, BACKGROUND_COLOR, BACKGROUND_POSITION, BACKGROUND_REPEAT, REC_VER, USER_ID, SYSTEM_UNIT_ID)
values ('f2ac0541-92a7-4ce9-a1c4-81147c726572', '08-3月 -18 05.44.10.759000 下午', 'U0010000001', '08-3月 -18 05.44.10.759000 下午', 'U0010000001', 0, '/resources/pt/images/images/mail_paper/12.jpg', '#0e9dbb', 'TOP_LEFT', 'REPEAT_X', 0, '', '' );

insert into WM_MAIL_PAPER (UUID, CREATE_TIME, CREATOR, MODIFY_TIME, MODIFIER, IS_DEFAULT, BACKGROUND_IMG_URL, BACKGROUND_COLOR, BACKGROUND_POSITION, BACKGROUND_REPEAT, REC_VER, USER_ID, SYSTEM_UNIT_ID)
values ('53e449db-ca0a-4775-86b9-76f7056d1912', '08-3月 -18 05.44.12.565000 下午', 'U0010000001', '08-3月 -18 05.44.12.565000 下午', 'U0010000001', 0, '/resources/pt/images/images/mail_paper/13.jpg', '#bfdfec', 'TOP_LEFT', 'REPEAT_X', 0, '', '' );

insert into WM_MAIL_PAPER (UUID, CREATE_TIME, CREATOR, MODIFY_TIME, MODIFIER, IS_DEFAULT, BACKGROUND_IMG_URL, BACKGROUND_COLOR, BACKGROUND_POSITION, BACKGROUND_REPEAT, REC_VER, USER_ID, SYSTEM_UNIT_ID)
values ('3e845d9a-4ade-4a13-a8e3-154289d0b852', '08-3月 -18 05.44.14.447000 下午', 'U0010000001', '08-3月 -18 05.44.14.447000 下午', 'U0010000001', 0, '/resources/pt/images/images/mail_paper/14.jpg', '#fff2d7', 'CENTER_LEFT', 'NO_REPEAT', 0, '', '' );





-- Create table
create table WM_MAIL_RECENT_CONTACT
(
  uuid                   VARCHAR2(64) not null,
  user_id                VARCHAR2(64) not null,
  system_unit_id         VARCHAR2(64) not null,
  contacter_name         VARCHAR2(64),
  contacter_mail_address VARCHAR2(64),
  last_contact_time      TIMESTAMP(6) not null,
  create_time            TIMESTAMP(6) not null,
  creator                VARCHAR2(64) not null,
  modify_time            TIMESTAMP(6),
  modifier               VARCHAR2(64),
  rec_ver                NUMBER(10)
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
comment on table WM_MAIL_RECENT_CONTACT
  is '邮件最近联系人';
-- Add comments to the columns 
comment on column WM_MAIL_RECENT_CONTACT.contacter_mail_address
  is '内部邮件保存的是用户/组织/部门/群组的ID';
comment on column WM_MAIL_RECENT_CONTACT.last_contact_time
  is '最近一次联系时间';
-- Create/Recreate primary, unique and foreign key constraints 
alter table WM_MAIL_RECENT_CONTACT
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

  
  
  
  -- Create table
create table WM_MAIL_RELA_TAG
(
  uuid           VARCHAR2(64) not null,
  tag_uuid       VARCHAR2(64) not null,
  mail_uuid      VARCHAR2(64) not null,
  system_unit_id VARCHAR2(64) not null,
  seq            NUMBER(3) default 1,
  create_time    TIMESTAMP(6) not null,
  creator        VARCHAR2(64) not null,
  modify_time    TIMESTAMP(6),
  modifier       VARCHAR2(64),
  rec_ver        NUMBER(10)
)
tablespace OA_DATA
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
comment on table WM_MAIL_RELA_TAG
  is '邮件关联的标签';
-- Add comments to the columns 
comment on column WM_MAIL_RELA_TAG.tag_uuid
  is '标签uui';
comment on column WM_MAIL_RELA_TAG.mail_uuid
  is '邮件uuid';
comment on column WM_MAIL_RELA_TAG.system_unit_id
  is '系统组织id';
-- Create/Recreate primary, unique and foreign key constraints 
alter table WM_MAIL_RELA_TAG
  add primary key (UUID)
  using index 
  tablespace OA_DATA
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
alter table WM_MAIL_RELA_TAG
  add constraint SYS_UNIQ00168133 unique (TAG_UUID, MAIL_UUID)
  using index 
  tablespace OA_DATA
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

  
  
  
  -- Create table
create table WM_MAIL_REVOCATION
(
  uuid              VARCHAR2(64) not null,
  from_mail_address VARCHAR2(64) not null,
  to_mail_address   VARCHAR2(64) not null,
  subject           VARCHAR2(64) not null,
  send_time         TIMESTAMP(6) not null,
  is_revoke_success NUMBER(1) not null,
  system_unit_id    VARCHAR2(64) not null,
  create_time       TIMESTAMP(6) not null,
  creator           VARCHAR2(64) not null,
  modify_time       TIMESTAMP(6),
  modifier          VARCHAR2(64),
  rec_ver           NUMBER(10)
)
tablespace OA_DATA
  pctfree 10
  initrans 1
  maxtrans 255;
-- Add comments to the table 
comment on table WM_MAIL_REVOCATION
  is '邮件撤回记录';
-- Add comments to the columns 
comment on column WM_MAIL_REVOCATION.from_mail_address
  is '发件人地址';
comment on column WM_MAIL_REVOCATION.to_mail_address
  is '收件人地址';
comment on column WM_MAIL_REVOCATION.subject
  is '主题';
comment on column WM_MAIL_REVOCATION.send_time
  is '邮件发送时间';
comment on column WM_MAIL_REVOCATION.is_revoke_success
  is '是否撤回成功：0失败 1成功';
-- Create/Recreate primary, unique and foreign key constraints 
alter table WM_MAIL_REVOCATION
  add primary key (UUID)
  using index 
  tablespace OA_DATA
  pctfree 10
  initrans 2
  maxtrans 255;
alter table WM_MAIL_REVOCATION
  add constraint SYS_UNIQ00172374 unique (FROM_MAIL_ADDRESS, TO_MAIL_ADDRESS, SUBJECT, SEND_TIME)
  using index 
  tablespace OA_DATA
  pctfree 10
  initrans 2
  maxtrans 255;

  
  
  
  -- Create table
create table WM_MAIL_SIGNATURE
(
  uuid              VARCHAR2(64) not null,
  signature_name    VARCHAR2(120) not null,
  signature_content CLOB,
  create_time       TIMESTAMP(6) not null,
  creator           VARCHAR2(64) not null,
  modify_time       TIMESTAMP(6),
  modifier          VARCHAR2(64),
  is_default        NUMBER(1) default 0 not null,
  rec_ver           NUMBER(10),
  user_id           VARCHAR2(64),
  system_unit_id    VARCHAR2(64)
)
tablespace OA_DATA
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
comment on table WM_MAIL_SIGNATURE
  is '邮件签名表';
-- Add comments to the columns 
comment on column WM_MAIL_SIGNATURE.signature_name
  is '签名标题';
comment on column WM_MAIL_SIGNATURE.signature_content
  is '签名内容';
-- Create/Recreate primary, unique and foreign key constraints 
alter table WM_MAIL_SIGNATURE
  add primary key (UUID)
  using index 
  tablespace OA_DATA
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
alter table WM_MAIL_SIGNATURE
  add constraint SYS_UNIQ00171224 unique (SIGNATURE_NAME, USER_ID)
  using index 
  tablespace OA_DATA
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

  
  
  
  
 -- Create table
create table WM_MAIL_TAG
(
  uuid           VARCHAR2(64) not null,
  tag_name       VARCHAR2(64),
  tag_color      VARCHAR2(12) not null,
  user_id        VARCHAR2(64) not null,
  system_unit_id VARCHAR2(64) not null,
  seq            NUMBER(3) default 1,
  create_time    TIMESTAMP(6) not null,
  creator        VARCHAR2(64) not null,
  modify_time    TIMESTAMP(6),
  modifier       VARCHAR2(64),
  rec_ver        NUMBER(10)
)
tablespace OA_DATA
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
-- Add comments to the columns 
comment on column WM_MAIL_TAG.tag_name
  is '标记名称';
comment on column WM_MAIL_TAG.tag_color
  is '标记颜色';
comment on column WM_MAIL_TAG.user_id
  is '用户id';
comment on column WM_MAIL_TAG.system_unit_id
  is '系统组织id';
comment on column WM_MAIL_TAG.seq
  is '排序';
-- Create/Recreate primary, unique and foreign key constraints 
alter table WM_MAIL_TAG
  add primary key (UUID)
  using index 
  tablespace OA_DATA
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

  
  
  
  
  
  
  
  
  -- Create table
create table WM_MAIL_TEMPLATE
(
  uuid             VARCHAR2(64) not null,
  template_name    VARCHAR2(120) not null,
  template_content CLOB,
  create_time      TIMESTAMP(6) not null,
  creator          VARCHAR2(64) not null,
  modify_time      TIMESTAMP(6),
  modifier         VARCHAR2(64),
  is_default       NUMBER(1) default 0 not null,
  rec_ver          NUMBER(10),
  user_id          VARCHAR2(64) not null,
  system_unit_id   VARCHAR2(64) not null
)
tablespace OA_DATA
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
comment on table WM_MAIL_TEMPLATE
  is '邮件写信模板表';
-- Add comments to the columns 
comment on column WM_MAIL_TEMPLATE.template_name
  is '模板标题';
comment on column WM_MAIL_TEMPLATE.template_content
  is '模板内容';
-- Create/Recreate primary, unique and foreign key constraints 
alter table WM_MAIL_TEMPLATE
  add primary key (UUID)
  using index 
  tablespace OA_DATA
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
alter table WM_MAIL_TEMPLATE
  add constraint SYS_UNIQ00171402 unique (TEMPLATE_NAME, USER_ID)
  using index 
  tablespace OA_DATA
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



  
  

-- 脚本编写人：陈琼
-- 脚本编写时间：2018/03/15
-- 增加邮件的撤回状态字段，支持邮件撤回功能

alter table WM_MAILBOX add  REVOKE_STATUS number(1);
comment on column WM_MAILBOX.REVOKE_STATUS
  is '撤回状态';
  
  
-- 脚本编写人：陈琼
-- 脚本编写时间：2018/03/15
-- 增加邮件服务器的同步记录表唯一索引，防止多次并发同步情况
-- 1.增加唯一索引前，删除重复的数据
delete from james_mail_async a
 where a.uuid in (select r.uuid
                    from JAMES_MAIL_ASYNC r,
                         (select max(uuid) as uuid, mailbox_id, mail_uid
                            from JAMES_MAIL_ASYNC t
                           group by mailbox_id, mail_uid
                          having count(1) > 1) v
                   where v.mailbox_id = r.mailbox_id
                     and v.mail_uid = r.mail_uid
                     and r.uuid <> v.uuid);
                  
alter table JAMES_MAIL_ASYNC
  add constraint SYS_UNIQ00172225 unique (MAILBOX_ID, MAIL_UID);

  
-- 脚本编写人：陈琼
-- 脚本编写时间：2018/03/15
-- 增加邮件主题字段长度
alter table WM_MAILBOX modify subject VARCHAR2(512);



-- 脚本编写人：陈琼
-- 脚本编写时间：2018/03/15
-- 增加各个主表的系统组织id
alter table WM_MAILBOX add system_unit_id varchar2(64);

