--  Add/modify columns
ALTER TABLE APP_LOGIN_PAGE_CONFIG ADD LOGIN_BOX_ACCOUNT_CODE_TYPE  VARCHAR2(20);

-- Add comments to the columns
comment on column APP_LOGIN_PAGE_CONFIG.LOGIN_BOX_ACCOUNT_CODE_TYPE is '验证码类型';