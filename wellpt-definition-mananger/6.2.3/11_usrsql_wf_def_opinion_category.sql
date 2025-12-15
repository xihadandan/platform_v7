-- Add/modify columns 
alter table WF_DEF_OPINION_CATEGORY add business_flag varchar2(64 char);
-- Add comments to the columns 
comment on column WF_DEF_OPINION_CATEGORY.business_flag
  is '行业应用标记';
