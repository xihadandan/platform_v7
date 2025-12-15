-- Add/modify columns 
alter table DYFORM_FILE_LIST_BUTTON_CONFIG add btn_type varchar2(64 char);
alter table DYFORM_FILE_LIST_BUTTON_CONFIG add btn_show_type varchar2(64 char);
-- Add comments to the columns 
comment on column DYFORM_FILE_LIST_BUTTON_CONFIG.btn_type
  is '内置按钮';
comment on column dyform_file_list_button_config.btn_show_type
  is '编辑类操作';