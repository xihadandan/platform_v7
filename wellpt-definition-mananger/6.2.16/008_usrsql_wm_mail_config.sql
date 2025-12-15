--  Add/modify columns
ALTER TABLE WM_MAIL_CONFIG ADD SEND_RECEIPT number(1);
-- Add comments to the columns
comment on column WM_MAIL_CONFIG.SEND_RECEIPT is '是否自动发送回执 0：否，1：是';
