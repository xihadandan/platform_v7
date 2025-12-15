-- Add/modify columns 
alter table CD_SCRIPT_DEFINITION add variables_definition clob;
-- Add comments to the columns 
comment on column CD_SCRIPT_DEFINITION.variables_definition
  is '变量定义';