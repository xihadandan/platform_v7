-- Add/modify columns 
alter table APP_LOGIN_PAGE_CONFIG add ding_style VARCHAR2(1024);
alter table APP_LOGIN_PAGE_CONFIG add ding_width VARCHAR2(32);
alter table APP_LOGIN_PAGE_CONFIG add ding_height VARCHAR2(32);
-- Add comments to the columns 
comment on column APP_LOGIN_PAGE_CONFIG.ding_style
  is '钉钉扫码样式';
comment on column APP_LOGIN_PAGE_CONFIG.ding_width
  is '钉钉扫码宽度';
comment on column APP_LOGIN_PAGE_CONFIG.ding_height
  is '钉钉扫码高度';
  
  
  
-- Add/modify columns 
alter table APP_LOGIN_PAGE_CONFIG add login_box_ding VARCHAR2(8);
-- Add comments to the columns 
comment on column APP_LOGIN_PAGE_CONFIG.login_box_ding
  is '钉钉扫码登录：0启用、1不启用';