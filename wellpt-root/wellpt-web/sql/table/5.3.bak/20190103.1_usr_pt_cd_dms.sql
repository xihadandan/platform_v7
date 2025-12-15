-- Add/modify columns 
alter table CD_DATA_STORE_DEFINITION add view_name VARCHAR2(255);
-- Add comments to the columns 
comment on column CD_DATA_STORE_DEFINITION.view_name
  is '数据库视图名';


insert into cd_data_dict (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, NAME, TYPE, PARENT_UUID, SOURCE_UUID, SOURCE_TYPE, EDITABLE, DELETABLE, CHILD_EDITABLE)
values ('b248d33e-0115-4be4-b1a3-233f1b21b9a6', '03-JAN-19 10.11.06.275000 AM', 'U0000000059', 'U0000000059', '03-JAN-19 10.11.06.275000 AM', 4, 'DATA_STORE_TYPE_VIEW', '数据库视图', 'APP_PT_DATA_STORE_TYPE_VIEW', 'c210c10e-4f0a-43a0-9844-56bedbc83585', null, null, 0, 0, 0);
commit;