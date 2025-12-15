-- Add/modify columns 
alter table MULTI_ORG_DING_DEPT add system_unit_id VARCHAR2(32);
-- Add comments to the columns 
comment on column MULTI_ORG_DING_DEPT.system_unit_id
  is '系统单位ID';
  
  
-- Add/modify columns 
alter table MULTI_ORG_DING_USER_INFO add system_unit_id VARCHAR2(32);
-- Add comments to the columns 
comment on column MULTI_ORG_DING_USER_INFO.system_unit_id
  is '系统单位ID';
  
  
update MULTI_ORG_DING_DEPT t set t.system_unit_id = (select tt.system_unit_id from multi_org_element tt where tt.id = t.ele_id) where t.system_unit_id is null;


update MULTI_ORG_DING_USER_INFO t set t.system_unit_id = (select tt.system_unit_id from multi_org_user_account tt where tt.id = t.user_id) where t.system_unit_id is null;
