-- Add/modify columns
alter table APP_LOGIN_PAGE_CONFIG add UNIT_LOGIN_PAGE_URI VARCHAR2(255);
alter table APP_LOGIN_PAGE_CONFIG add UNIT_LOGIN_PAGE_SWITCH char(1) default 0;
-- Add comments to the columns
comment on column APP_LOGIN_PAGE_CONFIG.UNIT_LOGIN_PAGE_URI
  is '单位登录页地址';
comment on column APP_LOGIN_PAGE_CONFIG.UNIT_LOGIN_PAGE_SWITCH
  is '单位登录页配置，0 默认登录页 1 单位自定义登录页';
