-- Add/modify columns 
alter table WF_FLOW_DEFINITION add temp_version NUMBER(4,1) default 1.0;
-- Add comments to the columns 
comment on column WF_FLOW_DEFINITION.temp_version
  is '版本号';


update WF_FLOW_DEFINITION set temp_version = version;

-- Drop columns 
alter table WF_FLOW_DEFINITION drop column version;

-- Add/modify columns 
alter table WF_FLOW_DEFINITION rename column temp_version to VERSION;

