-- Create/Recreate indexes 
create index WM_MAILBOX_IDX_01 on WM_MAILBOX (from_mail_address, mailbox_name, user_id, subject);

create index WM_MAIL_USER_IDX_01 on WM_MAIL_USER (mail_address, user_id);