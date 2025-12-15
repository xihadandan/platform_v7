-- Add/modify columns 
alter table ORG_USER add login_name_hash_algorithm VARCHAR2(255);
alter table ORG_USER add login_name_hash_value VARCHAR2(255);
alter table ORG_USER add password_hash_algorithm VARCHAR2(255);
-- Add comments to the columns 
comment on column ORG_USER.login_name_hash_algorithm
  is '登录名哈希算法，支持明文(NONE)、MD5(随机盐loginName)，默认MD5';
comment on column ORG_USER.login_name_hash_value
  is '登录名哈希算法对就的值';
comment on column ORG_USER.password_hash_algorithm
  is '密码哈希算法，默认MD5(随机盐loginName)';
