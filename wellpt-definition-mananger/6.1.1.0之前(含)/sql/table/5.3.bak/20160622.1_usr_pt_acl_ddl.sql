-- Alter table 
alter table ACL_ENTRY
  storage
  (
    next 1
  )
;
-- Create/Recreate indexes 
create index ACL_ENTRY_IDX_MASK_SID on ACL_ENTRY (mask, sid);

drop index ACL_ENTRY_BATCH_IDX;