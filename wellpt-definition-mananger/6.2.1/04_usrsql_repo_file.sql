-- Add/modify columns 
alter table REPO_FILE add source VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column REPO_FILE.source
  is '来源';
