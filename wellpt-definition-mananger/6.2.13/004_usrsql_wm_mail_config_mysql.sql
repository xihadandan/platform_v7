--  Add/modify columns
ALTER TABLE wm_mail_config ADD COLUMN receive_mail_action int(1) NOT NULL DEFAULT 0 COMMENT '点击接收邮件操作 0：显示收件箱，1：维持不变';