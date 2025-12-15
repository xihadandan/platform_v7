update dyform_file_list_button_config t set t.btn_type = '1', t.btn_show_type = 'edit';
update dyform_file_list_button_config t set t.btn_type = '1', t.btn_show_type = 'show' where t.code in('preview_btn', 'download_btn', 'copy_name_btn');
commit;