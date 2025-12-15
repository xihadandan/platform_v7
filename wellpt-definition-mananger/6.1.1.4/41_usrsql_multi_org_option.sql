-- Add/modify columns 
alter table MULTI_ORG_OPTION add is_enable NUMBER(1);
update MULTI_ORG_OPTION t set t.is_enable = 1 where 1 = 1;