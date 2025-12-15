-- Create table
create table MULTI_ORG_DING_USER_INFO
(
  uuid               VARCHAR2(255) not null,
  create_time        TIMESTAMP(6),
  creator            VARCHAR2(255),
  modifier           VARCHAR2(255),
  modify_time        TIMESTAMP(6),
  rec_ver            NUMBER(10,2),
  user_id            VARCHAR2(255),
  ding_user_id       VARCHAR2(255),
  union_id           VARCHAR2(255),
  pt_is_active       NUMBER(1),
  order_in_depts     VARCHAR2(255),
  open_id            VARCHAR2(255),
  roles              VARCHAR2(1024),
  mobile             VARCHAR2(255),
  active             NUMBER(1),
  avatar             VARCHAR2(255),
  is_admin           NUMBER(1),
  tags               VARCHAR2(255),
  is_hide            NUMBER(1),
  is_leader_in_depts VARCHAR2(255),
  is_boss            NUMBER(1),
  is_senior          NUMBER(1),
  name               VARCHAR2(255),
  state_code         VARCHAR2(255),
  department         VARCHAR2(1024),
  email              VARCHAR2(255),
  position           VARCHAR2(255 CHAR),
  pt_origin_pwd      VARCHAR2(255)
);
-- Add comments to the table 
comment on table MULTI_ORG_DING_USER_INFO
  is '钉钉用户信息表';
-- Add comments to the columns 
comment on column MULTI_ORG_DING_USER_INFO.uuid
  is '唯一主键';
comment on column MULTI_ORG_DING_USER_INFO.create_time
  is '创建时间';
comment on column MULTI_ORG_DING_USER_INFO.creator
  is '创建人';
comment on column MULTI_ORG_DING_USER_INFO.modifier
  is '更新人';
comment on column MULTI_ORG_DING_USER_INFO.modify_time
  is '更新时间';
comment on column MULTI_ORG_DING_USER_INFO.rec_ver
  is '数据版本';
comment on column MULTI_ORG_DING_USER_INFO.user_id
  is '对应的USER_ID';
comment on column MULTI_ORG_DING_USER_INFO.ding_user_id
  is '钉钉用户id';
comment on column MULTI_ORG_DING_USER_INFO.union_id
  is '钉钉用户唯一标识';
comment on column MULTI_ORG_DING_USER_INFO.active
  is 'true表示已激活，false表示未激活';
comment on column MULTI_ORG_DING_USER_INFO.is_admin
  is 'true表示是，false表示不是';
comment on column MULTI_ORG_DING_USER_INFO.is_hide
  is 'true表示隐藏，false表示不隐藏';
comment on column MULTI_ORG_DING_USER_INFO.is_leader_in_depts
  is 'Map结构的json字符串，key是部门的id，value是人员在这个部门中是否为主管，true表示是，false表示不是';
comment on column MULTI_ORG_DING_USER_INFO.is_boss
  is 'true表示是，false表示不是';
comment on column MULTI_ORG_DING_USER_INFO.position
  is '职位信息';
comment on column MULTI_ORG_DING_USER_INFO.pt_origin_pwd
  is '平台原始登录密码';
-- Create/Recreate primary, unique and foreign key constraints 
alter table MULTI_ORG_DING_USER_INFO
  add constraint PK_MULTI_ORG_DING_USER_INFO primary key (UUID)
  using index ;
