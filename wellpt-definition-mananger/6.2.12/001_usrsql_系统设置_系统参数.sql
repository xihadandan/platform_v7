-- 徽章数量显示位数
insert into SYS_PARAM_ITEM (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('dbdeeab4-e0bb-4081-9f04-428d1675c0f4', to_timestamp('15-12-2021 15:05:44.251000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'U0000000059', 'U0000000059', to_timestamp('15-12-2021 15:05:44.251000', 'dd-mm-yyyy hh24:mi:ss.ff'), 0, 'badge.number.display.bit.default', '2', '徽章数量显示位数默认值（全局）', 0, 'badge.number.display.bit.default', '001');
insert into SYS_PARAM_ITEM (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('88fddbe7-e408-4d5e-af0e-e5f939e3157f', to_timestamp('15-12-2021 15:06:13.736000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'U0000000059', 'U0000000059', to_timestamp('15-12-2021 15:06:13.736000', 'dd-mm-yyyy hh24:mi:ss.ff'), 0, 'badge.number.display.bit.switch', '1', '徽章数量显示位数开关（全局）', 0, 'badge.number.display.bit.switch', '001');
insert into SYS_PARAM_ITEM (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('ddbe7f01-e2a8-3d2w-af0e-s5b021z9265g', to_timestamp('15-12-2021 15:06:13.736000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'U0000000059', 'U0000000059', to_timestamp('15-12-2021 15:06:13.736000', 'dd-mm-yyyy hh24:mi:ss.ff'), 0, 'badge.number.zero.show.switch', '0', '徽章数量为0的显示开关（全局），默认不显示', 0, 'badge.number.zero.show.switch', '001');
commit;
