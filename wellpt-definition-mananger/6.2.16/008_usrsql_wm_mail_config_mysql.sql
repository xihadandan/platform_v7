--  Add/modify columns
ALTER TABLE wm_mail_config ADD COLUMN send_receipt int(1) COMMENT '是否自动发送回执 0：否，1：是';
