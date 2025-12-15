--  Add/modify columns
ALTER TABLE wm_mailbox_info_user ADD COLUMN pid varchar(255) COMMENT '邮件PID，从邮件服务器上取到的邮件唯一标识';
