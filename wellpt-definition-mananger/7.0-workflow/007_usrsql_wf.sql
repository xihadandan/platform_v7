create index ACL_SID_IDX on acl_sid (SID);
create index WF_TD_BATCH_IDX on wf_task_delegation (TASK_INST_UUID, TRUSTEE, COMPLETION_STATE);
create index WF_TIT_USER_ID_IDX on wf_task_instance_topping (USER_ID)