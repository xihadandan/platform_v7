-- Drop primary, unique and foreign key constraints 
alter table ORG_OPTION
  drop constraint UK_33447WNQNPXXGRY9GGA1FNRSY cascade;
-- Create/Recreate primary, unique and foreign key constraints 
alter table ORG_OPTION
  add constraint UK_33447WNQNPXXGRY9GGA1FNRSY unique (NAME, TENANT_ID);
