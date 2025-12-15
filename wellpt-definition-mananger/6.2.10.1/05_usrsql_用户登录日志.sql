-- Add/modify columns
alter table org_user_login_log add login_source NUMBER;
-- Add comments to the columns
comment on column org_user_login_log.login_source is '登录来源';


