-- Create table
create table APP_LOGIN_PAGE_CONFIG
(
  page_title                     VARCHAR2(255),
  footer_content                 VARCHAR2(2000),
  login_box_account_code         CHAR(1),
  login_box_account_sms          CHAR(1),
  login_box_account_remember_use CHAR(1),
  login_box_account_remember_pas CHAR(1),
  login_box_account_forget_passw CHAR(1),
  login_box_cas                  CHAR(1),
  login_box_hardware             CHAR(1),
  login_box_hardware_description VARCHAR2(2000),
  login_box_code                 CHAR(1),
  uuid                           VARCHAR2(255) not null,
  create_time                    TIMESTAMP(6),
  creator                        VARCHAR2(255),
  modifier                       VARCHAR2(255),
  modify_time                    TIMESTAMP(6),
  rec_ver                        NUMBER(10),
  page_background_image          VARCHAR2(255),
  page_logo                      VARCHAR2(255),
  page_style                     VARCHAR2(10),
  login_box_account_code_timeout NUMBER(10),
  login_box_account_sms_timeout  NUMBER(10),
  login_box_cas_url              VARCHAR2(255),
  login_box_cas_app_url          VARCHAR2(255)
);
-- Add comments to the table 
comment on table APP_LOGIN_PAGE_CONFIG
  is '登录页配置';
-- Add comments to the columns 
comment on column APP_LOGIN_PAGE_CONFIG.page_title
  is '标题';
comment on column APP_LOGIN_PAGE_CONFIG.footer_content
  is '页脚内容';
comment on column APP_LOGIN_PAGE_CONFIG.login_box_account_code
  is '是否启用验证码';
comment on column APP_LOGIN_PAGE_CONFIG.login_box_account_sms
  is '是否启用短信';
comment on column APP_LOGIN_PAGE_CONFIG.login_box_account_remember_use
  is '是否启用记住用户名';
comment on column APP_LOGIN_PAGE_CONFIG.login_box_account_remember_pas
  is '是否启用记住密码';
comment on column APP_LOGIN_PAGE_CONFIG.login_box_account_forget_passw
  is '是否启用忘记密码';
comment on column APP_LOGIN_PAGE_CONFIG.login_box_cas
  is '是否启用单点登陆';
comment on column APP_LOGIN_PAGE_CONFIG.login_box_hardware
  is '是否启用硬件登陆';
comment on column APP_LOGIN_PAGE_CONFIG.login_box_hardware_description
  is '硬件登陆描述';
comment on column APP_LOGIN_PAGE_CONFIG.login_box_code
  is '是否启用二维码登陆';
comment on column APP_LOGIN_PAGE_CONFIG.page_background_image
  is '背景图';
comment on column APP_LOGIN_PAGE_CONFIG.page_logo
  is 'logo图片';
comment on column APP_LOGIN_PAGE_CONFIG.page_style
  is '风格';
comment on column APP_LOGIN_PAGE_CONFIG.login_box_account_code_timeout
  is '验证吗超时时间(单位秒)';
comment on column APP_LOGIN_PAGE_CONFIG.login_box_account_sms_timeout
  is '短信验证码超时时间(单位秒)';
comment on column APP_LOGIN_PAGE_CONFIG.login_box_cas_url
  is '单点地址';
comment on column APP_LOGIN_PAGE_CONFIG.login_box_cas_app_url
  is '单点应用地址';
alter table APP_LOGIN_PAGE_CONFIG
  add constraint PK_APP_LOGIN_PAGE_CONFIG primary key (UUID);
