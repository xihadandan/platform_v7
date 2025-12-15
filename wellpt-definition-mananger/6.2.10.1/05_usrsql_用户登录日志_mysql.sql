-- mysql
ALTER TABLE org_user_login_log ADD COLUMN login_source INT DEFAULT NULL COMMENT '登录来源';

