-- Add/modify columns 
alter table APP_LOGIN_PAGE_CONFIG add enable_oauth number(1) default 0;
alter table APP_LOGIN_PAGE_CONFIG add enable_oauth_login number(1) default 0;
alter table APP_LOGIN_PAGE_CONFIG add enable_customize_oauth_login number(1) default 0;
alter table APP_LOGIN_PAGE_CONFIG add CUSTOMIZE_OAUTH_LOGIN_URI VARCHAR2(255);
-- Add comments to the columns 
comment on column APP_LOGIN_PAGE_CONFIG.enable_oauth
  is '是否启用统一认证登录';
comment on column APP_LOGIN_PAGE_CONFIG.enable_oauth_login
  is '是否使用统一认证登录页';
comment on column APP_LOGIN_PAGE_CONFIG.enable_customize_oauth_login
  is '是否自定义平台的统一认证登录页地址';
comment on column APP_LOGIN_PAGE_CONFIG.CUSTOMIZE_OAUTH_LOGIN_URI
  is '自定义平台的统一认证登录页地址';




create table USER_INFO
(
  uuid              VARCHAR2(64) primary key,
  create_time       TIMESTAMP(6),
  creator           VARCHAR2(255 CHAR),
  modifier          VARCHAR2(255 CHAR),
  modify_time       TIMESTAMP(6),
  rec_ver           NUMBER(10),
  account_uuid      VARCHAR2(64) not null,
  user_name         VARCHAR2(255 CHAR) not null,
  type              NUMBER(1) default 0,
  login_name        VARCHAR2(64) not null,
  mail              VARCHAR2(64),
  ceil_phone_number VARCHAR2(16)
);


create table USER_ACCOUNT
(
  uuid                       VARCHAR2(64) primary key,
  create_time                TIMESTAMP(6),
  creator                    VARCHAR2(255 CHAR),
  modifier                   VARCHAR2(255 CHAR),
  modify_time                TIMESTAMP(6),
  rec_ver                    NUMBER(10),
  password                   VARCHAR2(255) not null,
  login_name                 VARCHAR2(64) not null,
  is_enabled                 NUMBER(1) default 1,
  is_account_non_expired     NUMBER(1) default 1,
  is_account_non_locked      NUMBER(1) default 1,
  is_credentials_non_expired NUMBER(1) default 1,
  ext_login_name             VARCHAR2(64),
  ext_login_type             VARCHAR2(64)
);


create table USER_TYPE_ROLE
(
  uuid        VARCHAR2(64) primary key,
  create_time TIMESTAMP(6),
  creator     VARCHAR2(255 CHAR),
  modifier    VARCHAR2(255 CHAR),
  modify_time TIMESTAMP(6),
  rec_ver     NUMBER(10),
  role        VARCHAR2(64) not null,
  type        NUMBER(1) not null
);


create table USER_CREDENTIAL
(
  uuid                       VARCHAR2(64) primary key,
  create_time                TIMESTAMP(6),
  creator                    VARCHAR2(255 CHAR),
  modifier                   VARCHAR2(255 CHAR),
  modify_time                TIMESTAMP(6),
  rec_ver                    NUMBER(10),
  login_name                 VARCHAR2(64) not null,
  type varchar2(64) not null,
  code varchar2(120) not null
)