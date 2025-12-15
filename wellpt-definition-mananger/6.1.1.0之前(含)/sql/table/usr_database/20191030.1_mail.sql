-- Add/modify columns
alter table WM_MAILBOX add from_mail_uuid VARCHAR2(255);
-- Add comments to the columns
comment on column WM_MAILBOX.from_mail_uuid
  is '内部来源邮件UUID';



-- Add/modify columns
alter table WM_MAIL_REVOCATION add mailbox_uuid VARCHAR2(64);



-- Drop columns
alter table WM_MAIL_REVOCATION
  drop constraint SYS_UNIQ00172374 cascade;
alter table WM_MAIL_REVOCATION drop column from_mail_address;
alter table WM_MAIL_REVOCATION drop column subject;
alter table WM_MAIL_REVOCATION drop column send_time;
-- Create/Recreate indexes
create unique index SYS_UNIQ00172375 on WM_MAIL_REVOCATION (mailbox_uuid, to_mail_address);