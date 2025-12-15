-- Alter table 
alter table CD_PRINT_TEMPLATE
  storage
  (
    next 1
  )
;
-- Add/modify columns 
alter table CD_PRINT_TEMPLATE modify key_words VARCHAR2(4000 CHAR);