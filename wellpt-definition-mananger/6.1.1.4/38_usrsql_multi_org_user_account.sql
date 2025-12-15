-- Add/modify columns 
alter table MULTI_ORG_USER_ACCOUNT add USER_NAME_JP VARCHAR2(255);
-- Add comments to the columns 
comment on column MULTI_ORG_USER_ACCOUNT.USER_NAME_JP is 'userName 转简拼';