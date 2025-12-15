
-- 组织同步设置，人员证件号码同步开关，默认 1 开启
insert into SYS_PARAM_ITEM (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, KEY, VALUE, NAME, SOURCETYPE, CODE, TYPE)
values ('183ce481-cb14-451b-8555-874db420c817', to_timestamp('15-12-2021 16:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 'U0000000059', 'U0000000059', to_timestamp('15-12-2021 16:48:06.332000', 'dd-mm-yyyy hh24:mi:ss.ff'), 0, 'pt.app.dingtalk.org.sync.user.idnumber', '1', 'pt.app.dingtalk.org.sync.user.idnumber', 2, 'pt.app.dingtalk.org.sync.user.idnumber', '009');
commit;


-- Add/modify columns
alter table MULTI_ORG_DING_USER_INFO add ID_NUMBER VARCHAR2(255);
-- Add comments to the columns
comment on column MULTI_ORG_DING_USER_INFO.ID_NUMBER
  is '证件号码';


