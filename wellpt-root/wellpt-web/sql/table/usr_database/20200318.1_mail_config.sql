-- Add/modify columns
alter table WM_MAIL_CONFIG add allow_org_options varchar2(1000);
-- Add comments to the columns
comment on column WM_MAIL_CONFIG.allow_org_options
  is '组织选项';
