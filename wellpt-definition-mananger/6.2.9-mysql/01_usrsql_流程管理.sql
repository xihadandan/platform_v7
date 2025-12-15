alter table wf_task_instance add primary key(uuid);
alter table wf_flow_instance add primary key(uuid);
alter table wf_flow_definition add primary key(uuid);
alter table wf_flow_management add primary key(uuid);
alter table acl_sid add primary key(uuid);
create index wf_fm_batch_idx on wf_flow_management (FLOW_DEF_UUID, ORG_ID, TYPE);
create index acl_oi_oii_idx on acl_object_identity (OBJECT_ID_IDENTITY);
