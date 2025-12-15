CREATE index user_id_suspension_state_idx on wf_task_identity(user_id,suspension_state);
CREATE index version_id_idx on DYFORM_FORM_DEFINITION(version,id);
 CREATE index id_version_idx on wf_flow_definition(id,version);
CREATE index category_idx on wf_flow_definition(category);
 CREATE index forbidden_system_unit_id_type_idx on multi_org_user_account(is_forbidden,system_unit_id,type);

CREATE index uuid_class_idx on acl_class(uuid,class);

CREATE INDEX uuid_sid_idx ON acl_sid (uuid,sid);

CREATE INDEX user_id_idx ON wf_task_instance_topping (user_id);
 CREATE index flow_inst_uuid_task_inst_uuid_idx on wf_task_activity(flow_inst_uuid,task_inst_uuid);
CREATE index object_id_identity_granting_mask_idx on acl_entry(object_id_identity,granting,mask);
CREATE index trustee_completion_state_idx on wf_task_delegation(trustee,completion_state);

