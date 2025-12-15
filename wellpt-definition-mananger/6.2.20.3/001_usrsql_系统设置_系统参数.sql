-- 附件控件预览，永中预览的打印按钮显示配置
insert into SYS_PARAM_ITEM (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('64ac8504-51e5-448e-8b35-29459512a44v', to_timestamp('14-06-2022 10:06:13.736000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'U0000000059', 'U0000000059', to_timestamp('14-06-2022 10:06:13.736000', 'dd-mm-yyyy hh24:mi:ss.ff'), 0, 'app.filepreview.print', '1', '附件控件预览，永中预览是否显示打印按钮，默认1，显示打印按钮，0不显示', 0, 'app.filepreview.print', '001');
commit;
