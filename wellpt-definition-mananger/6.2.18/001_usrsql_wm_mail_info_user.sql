--  Add/modify columns
ALTER TABLE WM_MAILBOX_INFO_USER ADD READ_TIME  TIMESTAMP(6);
-- Add comments to the columns
comment on column WM_MAILBOX_INFO_USER.READ_TIME is '阅读时间';
