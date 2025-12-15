--  Add/modify columns
alter table WM_MAIL_CONFIG add receive_mail_action number(1) default 0 not null;
-- Add comments to the columns
comment on column WM_MAIL_CONFIG.receive_mail_action is '点击接收邮件操作 0：显示收件箱，1：维持不变';