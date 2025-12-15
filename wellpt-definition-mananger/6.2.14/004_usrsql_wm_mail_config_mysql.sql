--  Add/modify columns
ALTER TABLE wm_mail_config ADD COLUMN is_public_email int(1) COMMENT '是否公网邮箱 0：否，1：是';
