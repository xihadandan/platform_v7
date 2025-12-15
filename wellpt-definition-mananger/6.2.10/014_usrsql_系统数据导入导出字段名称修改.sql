-- Add/modify columns
alter table DATA_IMPORT_RECORD rename column SYSTEM_UNIT_ID to IMPORT_UNIT_ID;
alter table DATA_IMPORT_RECORD rename column SYSTEM_UNIT_NAME to IMPORT_UNIT_NAME;

-- Add/modify columns
alter table DATA_IMPORT_TASK rename column SYSTEM_UNIT_ID to IMPORT_UNIT_ID;
alter table DATA_IMPORT_TASK rename column SYSTEM_UNIT_NAME to IMPORT_UNIT_NAME;
