-- Add/modify columns 
alter table WF_FLOW_INSTANCE add system_unit_id VARCHAR2(64 CHAR);

update wf_flow_instance t
   set t.system_unit_id =
       (select u.system_unit_id
          from multi_org_user_account u
         where u.id = t.start_user_id)
 where t.system_unit_id is null;
commit;