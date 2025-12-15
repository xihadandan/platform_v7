-- Alter table 
alter table WF_DEF_OPINION_CATEGORY
  storage
  (
    next 8
  )
;
-- Add/modify columns 
alter table WF_DEF_OPINION_CATEGORY add id VARCHAR2(255 CHAR);
