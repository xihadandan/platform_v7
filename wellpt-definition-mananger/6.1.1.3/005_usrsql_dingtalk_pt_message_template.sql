
-- Add/modify columns 
alter table MSG_MESSAGE_TEMPLATE add dt_message_type VARCHAR2(16 CHAR);
alter table MSG_MESSAGE_TEMPLATE add dt_jump_type VARCHAR2(16 CHAR);
alter table MSG_MESSAGE_TEMPLATE add dt_title VARCHAR2(1024 CHAR);
alter table MSG_MESSAGE_TEMPLATE add dt_body VARCHAR2(2048 CHAR);
alter table MSG_MESSAGE_TEMPLATE add dt_uri_title VARCHAR2(1024 CHAR);
alter table MSG_MESSAGE_TEMPLATE add dt_uri VARCHAR2(1024 CHAR);
alter table MSG_MESSAGE_TEMPLATE add dt_btn_orientation VARCHAR2(1024 CHAR);
alter table MSG_MESSAGE_TEMPLATE add dt_btn_json_list clob;
-- Add comments to the columns 
comment on column MSG_MESSAGE_TEMPLATE.dt_message_type
  is '消息类型:ActionCard';
comment on column MSG_MESSAGE_TEMPLATE.dt_jump_type
  is '跳转方式:（整体跳转）single、（独立跳转）multi';
comment on column MSG_MESSAGE_TEMPLATE.dt_title
  is '标题';
comment on column MSG_MESSAGE_TEMPLATE.dt_body
  is '内容';
comment on column MSG_MESSAGE_TEMPLATE.dt_uri_title
  is '源标题';
comment on column MSG_MESSAGE_TEMPLATE.dt_uri
  is '源地址';
comment on column MSG_MESSAGE_TEMPLATE.dt_btn_orientation
  is '按钮排列方式:竖直排列(0)，横向排列(1)';
comment on column MSG_MESSAGE_TEMPLATE.dt_btn_json_list
  is ' 附加链接JSONArray[{"title":"","url":""}]';
