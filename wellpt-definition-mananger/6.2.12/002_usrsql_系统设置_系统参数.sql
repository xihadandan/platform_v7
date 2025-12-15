-- 附件控件预览的打开方式
insert into SYS_PARAM_ITEM (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('64ac8504-51e5-448e-8b35-29459512a44a', to_timestamp('23-12-2021 15:06:13.736000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'U0000000059', 'U0000000059', to_timestamp('23-12-2021 15:06:13.736000', 'dd-mm-yyyy hh24:mi:ss.ff'), 0, 'pt.preview.open.mode', 'tab', '附件控件预览的打开方式，window为新窗口打开，tab或者其它值为新页签打开，默认tab', 0, 'pt.preview.open.mode', '001');
commit;
