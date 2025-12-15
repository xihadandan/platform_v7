-- Add/modify columns 
alter table CD_ID_GENERATOR add TENANT_ID VARCHAR2(255 CHAR);
-- Create/Recreate primary, unique and foreign key constraints 
alter table CD_ID_GENERATOR
  drop constraint SYS_C0022058 cascade;
alter table CD_ID_GENERATOR
  add primary key (ENTITY_CLASS_NAME, TENANT_ID)
  using index 
  tablespace OA_DATA
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

