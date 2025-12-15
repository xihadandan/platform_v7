
alter table MULTI_ORG_USER_TREE_NODE modify ele_id VARCHAR2(32);
ALTER TABLE  MULTI_ORG_USER_TREE_NODE  ADD PRIMARY KEY ("UUID");
 
alter table MULTI_ORG_TREE_NODE modify ele_id VARCHAR2(32);
alter table MULTI_ORG_TREE_NODE modify ele_id_path VARCHAR2(255);


ALTER TABLE  MULTI_ORG_DUTY MODIFY ( UUID  VARCHAR2(64 CHAR));
ALTER TABLE  MULTI_ORG_DUTY  ADD PRIMARY KEY ("UUID");
ALTER TABLE  multi_org_element MODIFY ( UUID  VARCHAR2(64 CHAR));
ALTER TABLE  multi_org_element  ADD PRIMARY KEY ("UUID");

ALTER TABLE  multi_org_element_role MODIFY ( UUID  VARCHAR2(64 CHAR));
ALTER TABLE  multi_org_element_role  ADD PRIMARY KEY ("UUID");


ALTER TABLE  multi_org_group MODIFY ( UUID  VARCHAR2(64 CHAR));
ALTER TABLE  multi_org_group  ADD PRIMARY KEY ("UUID");

ALTER TABLE  multi_org_element_leader MODIFY ( UUID  VARCHAR2(64 CHAR));
ALTER TABLE  multi_org_element_leader  ADD PRIMARY KEY ("UUID");


ALTER TABLE  multi_org_group_member MODIFY ( UUID  VARCHAR2(64 CHAR));
ALTER TABLE  multi_org_group_member  ADD PRIMARY KEY ("UUID");

ALTER TABLE  multi_org_group_role MODIFY ( UUID  VARCHAR2(64 CHAR));
ALTER TABLE  multi_org_group_role  ADD PRIMARY KEY ("UUID");

ALTER TABLE  multi_org_job_duty MODIFY ( UUID  VARCHAR2(64 CHAR));
ALTER TABLE  multi_org_job_duty  ADD PRIMARY KEY ("UUID");


ALTER TABLE  multi_org_job_rank MODIFY ( UUID  VARCHAR2(64 CHAR));
ALTER TABLE  multi_org_job_rank  ADD PRIMARY KEY ("UUID");

ALTER TABLE  multi_org_pwd_setting MODIFY ( UUID  VARCHAR2(64 CHAR));
ALTER TABLE  multi_org_pwd_setting  ADD PRIMARY KEY ("UUID");

ALTER TABLE  multi_org_system_unit MODIFY ( UUID  VARCHAR2(64 CHAR));
ALTER TABLE  multi_org_system_unit  ADD PRIMARY KEY ("UUID");

ALTER TABLE  multi_org_system_unit_member MODIFY ( UUID  VARCHAR2(64 CHAR));
ALTER TABLE  multi_org_system_unit_member  ADD PRIMARY KEY ("UUID");

ALTER TABLE  multi_org_tree_node MODIFY ( UUID  VARCHAR2(64 CHAR));
ALTER TABLE  multi_org_tree_node  ADD PRIMARY KEY ("UUID");

ALTER TABLE  multi_org_user_account MODIFY ( UUID  VARCHAR2(64 CHAR));
ALTER TABLE  multi_org_user_account  ADD PRIMARY KEY ("UUID");

ALTER TABLE  multi_org_user_info MODIFY ( UUID  VARCHAR2(64 CHAR));
ALTER TABLE  multi_org_user_info  ADD PRIMARY KEY ("UUID");

ALTER TABLE  multi_org_user_role MODIFY ( UUID  VARCHAR2(64 CHAR));
ALTER TABLE  multi_org_user_role  ADD PRIMARY KEY ("UUID");

ALTER TABLE  multi_org_version MODIFY ( UUID  VARCHAR2(64 CHAR));
ALTER TABLE  multi_org_version  ADD PRIMARY KEY ("UUID");


CREATE INDEX  idx_moua_sys_u_id  ON  MULTI_ORG_USER_ACCOUNT  ("SYSTEM_UNIT_ID");
CREATE INDEX  idx_mouw_user_id  ON  MULTI_ORG_USER_WORK_INFO ("USER_ID");


CREATE INDEX  idx_apr_page_uuid 
  ON  APP_PAGE_RESOURCE ("APP_PAGE_UUID");
  
CREATE INDEX  IDX_AWD_PAGE_UUID
  ON  APP_WIDGET_DEFINITION ("APP_PAGE_UUID");
  
  
alter table APP_LOGIN_PAGE_CONFIG add account_code_ignore_case char(1) default 0;
comment on column APP_LOGIN_PAGE_CONFIG.account_code_ignore_case
  is '验证码是否区分大小写';
  
  
  
-- Add/modify columns 
alter table MSG_MESSAGE_TEMPLATE modify uuid VARCHAR2(64 CHAR);
alter table MSG_MESSAGE_TEMPLATE modify creator VARCHAR2(64 CHAR);
alter table MSG_MESSAGE_TEMPLATE modify modifier VARCHAR2(64 CHAR);
alter table MSG_MESSAGE_TEMPLATE modify category VARCHAR2(64 CHAR);
alter table MSG_MESSAGE_TEMPLATE modify code VARCHAR2(64 CHAR);
alter table MSG_MESSAGE_TEMPLATE modify id VARCHAR2(64 CHAR);
alter table MSG_MESSAGE_TEMPLATE modify name VARCHAR2(64 CHAR);
alter table MSG_MESSAGE_TEMPLATE modify send_time VARCHAR2(64 CHAR);
alter table MSG_MESSAGE_TEMPLATE modify send_way VARCHAR2(64 CHAR);
alter table MSG_MESSAGE_TEMPLATE modify type VARCHAR2(64 CHAR);
alter table MSG_MESSAGE_TEMPLATE modify is_online_popup VARCHAR2(64 CHAR);

 
update ACL_SID set creator=null;
alter table ACL_SID modify uuid VARCHAR2(64);
alter table ACL_SID modify creator VARCHAR2(64);
alter table ACL_SID modify modifier VARCHAR2(64);
update ACL_SID set creator=modifier;
commit;


update ACL_OBJECT_IDENTITY set creator =null;
alter table ACL_OBJECT_IDENTITY modify uuid varchar2(64);
alter table ACL_OBJECT_IDENTITY modify creator varchar2(64);
alter table ACL_OBJECT_IDENTITY modify modifier varchar2(64);
update ACL_OBJECT_IDENTITY set creator =modifier;
commit;

alter table MSG_MESSAGE_QUEUE
  drop constraint MSG_MESSAGE_QUEUE_UUID cascade;
drop index MSG_MESSAGE_QUEUE_UUID;
ALTER TABLE  MSG_MESSAGE_QUEUE ADD PRIMARY KEY ("UUID");
alter table MSG_MESSAGE_QUEUE modify uuid VARCHAR2(64);
 


alter table WF_TASK_INSTANCE modify uuid VARCHAR2(64);
alter table WF_TASK_INSTANCE modify flow_def_uuid VARCHAR2(64);
alter table WF_TASK_INSTANCE modify flow_inst_uuid VARCHAR2(64);
alter index WF_TIN_FLOW_DEF_UUID_IDX rename to IDX_C00194813;
alter index WF_TIN_FLOW_INST_UUID_IDX rename to IDX_C00194814;
alter index MSG_MESSAGE_TIME rename to IDX_MMQ_TIME;



