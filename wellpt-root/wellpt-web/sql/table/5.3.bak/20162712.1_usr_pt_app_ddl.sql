-- Alter table 
alter table APP_FUNCTION
  storage
  (
    next 8
  )
;
-- Add/modify columns 
alter table APP_FUNCTION modify definition_json VARCHAR2(4000 CHAR);
