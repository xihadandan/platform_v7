
-- 组织同步设置，人员证件号码同步开关，默认 1 开启
insert into SYS_PARAM_ITEM
values ('183ce481-cb14-451b-8555-874db420c817', STR_TO_DATE('2021-12-15 16:48:06', '%Y-%m-%d %H:%i:%s'), 'U0000000059', 'U0000000059', STR_TO_DATE('2021-12-15 16:48:06', '%Y-%m-%d %H:%i:%s'), 0, 'pt.app.dingtalk.org.sync.user.idnumber', '1', 'pt.app.dingtalk.org.sync.user.idnumber', 2, 'pt.app.dingtalk.org.sync.user.idnumber', '009');


ALTER TABLE `multi_org_ding_user_info`
ADD COLUMN `ID_NUMBER`  varchar(255) NULL COMMENT '证件号码' AFTER `REMARK`;


