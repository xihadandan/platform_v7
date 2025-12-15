-- Add/modify columns 
alter table MULTI_ORG_DING_DEPT add org_version_id VARCHAR2(32);
-- Add comments to the columns 
comment on column MULTI_ORG_DING_DEPT.org_version_id
  is '组织版本ID';
  
update multi_org_ding_dept t set t.org_version_id = (select tt.org_version_id from multi_org_tree_node tt where tt.ele_id = t.ele_id) where t.org_version_id is null;
