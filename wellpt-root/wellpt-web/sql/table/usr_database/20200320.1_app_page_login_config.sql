-- Add/modify columns 
alter table APP_LOGIN_PAGE_CONFIG add login_box_key CHAR(1);
alter table APP_LOGIN_PAGE_CONFIG add login_box_key_description VARCHAR2(2000);
-- Add comments to the columns 
comment on column APP_LOGIN_PAGE_CONFIG.login_box_key
  is '是否启用电子钥匙盘登陆';
comment on column APP_LOGIN_PAGE_CONFIG.login_box_key_description
  is '电子钥匙盘登陆描述';
  
-- Add/modify columns 
alter table MULTI_ORG_USER_INFO add CERTIFICATE_SUBJECT VARCHAR2(2000 CHAR);
-- Add comments to the columns 
comment on column MULTI_ORG_USER_INFO.CERTIFICATE_SUBJECT
  is '证书主体';