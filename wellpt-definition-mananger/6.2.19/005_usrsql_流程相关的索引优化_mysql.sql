CREATE index opinion_category_id_idx on wf_def_opinion_category(id);

DROP INDEX ACL_ENTRY_IDX_OBJECT on acl_entry;
DROP INDEX ACL_ENTRY_BATCH_IDX_OBJECT on acl_entry;
DROP INDEX ACL_ENTRY_IDX_MASK on acl_entry;
DROP INDEX ACL_ENTRY_BATCH_IDX on acl_entry;
DROP INDEX object_id_identity_granting_mask_idx on acl_entry;
CREATE index ACL_ENTRY_BATCH_IDX_OBJECT on acl_entry(OBJECT_ID_IDENTITY, SID, MASK,GRANTING);

DROP INDEX WF_DO_BATCH_IDX on wf_def_opinion;
CREATE index WF_DO_BATCH_IDX on wf_def_opinion(CREATOR,FLOW_DEF_ID,TASK_ID,content(100),MODIFY_TIME);