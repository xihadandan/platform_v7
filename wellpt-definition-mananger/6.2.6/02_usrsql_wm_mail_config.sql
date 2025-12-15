-- Add/modify columns
alter table WM_MAIL_CONFIG add attachment_size_limit VARCHAR2(20 CHAR);
comment on column WM_MAIL_CONFIG.attachment_size_limit is '附件大小限制(单位MB)';