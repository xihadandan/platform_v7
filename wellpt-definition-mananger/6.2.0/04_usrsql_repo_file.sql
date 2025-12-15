-- Add/modify columns 
alter table REPO_FILE add orig_uuid VARCHAR2(255);
-- Add comments to the columns 
comment on column REPO_FILE.orig_uuid
  is '原始附件UUID';