-- Add/modify columns
alter table APP_LOGIN_PAGE_CONFIG add column UNIT_LOGIN_PAGE_URI VARCHAR(255) comment '单位登录页地址';
alter table APP_LOGIN_PAGE_CONFIG add column UNIT_LOGIN_PAGE_SWITCH char(1) default 0 comment '单位登录页配置，0 默认登录页 1 单位自定义登录页';
