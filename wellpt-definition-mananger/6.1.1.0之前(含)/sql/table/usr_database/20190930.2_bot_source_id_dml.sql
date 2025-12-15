-- Add/modify columns
alter table BOT_RULE_CONF add source_obj_id VARCHAR2(4000);
-- Add comments to the columns
comment on column BOT_RULE_CONF.source_obj_id
  is '源单据ID';
