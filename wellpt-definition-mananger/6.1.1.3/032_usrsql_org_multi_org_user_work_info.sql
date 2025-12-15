-- Add/modify columns
alter table MULTI_ORG_USER_WORK_INFO
add ( DIRECT_LEADER_IDS VARCHAR2(4000) NULL) ;
-- Add comments to the columns
comment on column MULTI_ORG_USER_WORK_INFO.DIRECT_LEADER_IDS is '直属上级领导';

