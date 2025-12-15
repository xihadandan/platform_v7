-- Add/modify columns 
alter table WM_MAIL_CONFIG add mail_server_type varchar2(64) default 'JamesMail' not null;

-- Add/modify columns
alter table WM_MAIL_CONFIG add api_port number(10);
-- Add comments to the columns
comment on column WM_MAIL_CONFIG.api_port
  is 'API端口';

