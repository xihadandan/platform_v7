--  Add/modify columns
ALTER TABLE WM_MAILBOX_INFO_USER ADD PID VARCHAR2(255);
-- Add comments to the columns
comment on column WM_MAILBOX_INFO_USER.PID is '邮件PID，从邮件服务器上取到的邮件唯一标识';
