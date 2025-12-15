--  Add/modify columns
ALTER TABLE WM_MAIL_CONFIG ADD IS_PUBLIC_EMAIL number(1);
-- Add comments to the columns
comment on column WM_MAIL_CONFIG.IS_PUBLIC_EMAIL is '是否公网邮箱 0：否，1：是';
