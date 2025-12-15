-- Add/modify columns 
alter table ORG_USER add not_Allowed_Tenant_Id NUMBER(1);
alter table ORG_USER add only_Logon_Width_Certificate NUMBER(1);
-- Add comments to the columns 
comment on column ORG_USER.not_Allowed_Tenant_Id
  is '不允许登陆此系统';
comment on column ORG_USER.only_Logon_Width_Certificate
  is '只能以证书登录';
