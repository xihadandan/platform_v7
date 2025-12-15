--机构管理
insert into audit_resource (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, APPLY_TO, CODE, DYNAMIC, ENABLED, IS_DEFAULT, ISSYS, NAME, REMARK, TARGET, TYPE, URL, PARENT_UUID)
values ('35322da0-f420-46ab-9dbd-d5dc45b9f648', '25-3月 -16 09.25.06.530000 上午', 'U8360000001', 'U8360000001', '25-3月 -16 09.25.06.530000 上午', 11, '', '001012', null, null, null, null, '机构管理', '机构管理', '', 'MENU', '/org/corporation/list', '5177a70f-a53f-48c9-9b18-39ccdb705459');

insert into audit_privilege_resource (PRIVILEGE_UUID, RESOURCE_UUID)
values ('b4ad98dd-7d4d-410d-8cbc-28b02023f008', '35322da0-f420-46ab-9dbd-d5dc45b9f648');


--扩展字段管理
insert into audit_resource (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, APPLY_TO, CODE, DYNAMIC, ENABLED, IS_DEFAULT, ISSYS, NAME, REMARK, TARGET, TYPE, URL, PARENT_UUID)
values ('c491d7c5-7890-4cdd-b66b-94d62074c1c6', '25-3月 -16 09.51.27.432000 上午', 'U8360000001', 'U8360000001', '25-3月 -16 09.51.27.432000 上午', 5, '', '003012', null, null, null, null, '扩展字段管理', '扩展字段管理', '', 'MENU', '/cd/field/ext/definition/list', 'f4783b4f-3f1b-45c8-ac02-81a7b10deb0f');

insert into audit_privilege_resource (PRIVILEGE_UUID, RESOURCE_UUID)
values ('9315e8ac-6d6f-4fbc-bfee-72760d00b73c', 'c491d7c5-7890-4cdd-b66b-94d62074c1c6');

