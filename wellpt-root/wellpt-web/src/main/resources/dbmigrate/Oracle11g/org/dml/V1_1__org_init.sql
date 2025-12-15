declare
  delobj varchar2(128);
  cnt     number;
begin
  delobj := 'U' || replace('@{multi.tenancy.tenant.id}','T','') || '0000001';
  select COUNT(*) into cnt from org_user t where t.ID = delobj;
  IF cnt < 1 then
insert into ORG_ORGANIZATION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, IS_DEFAULT, NAME, TYPE_CODE, TYPE_NAME)
values ('80eb5660-e2cb-4560-ac08-35665e5dc26f', '12-10月-15 10.53.12.000000 上午', 'U0010000015', 'U0010000015', '12-10月-15 11.31.18.626000 上午', 1, '001', 'O001001', 1, '区政府', '000101', '管理组织');



insert into org_user (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, ACCOUNT_NON_EXPIRED, ACCOUNT_NON_LOCKED, CODE, CREDENTIALS_NON_EXPIRED, DEPARTMENT_NAME, DEPUTY_NAMES, EMPLOYEE_NUMBER, ENABLED, FAX, GROUP_NAMES, ID, ID_NUMBER, IS_ALLOWED_BACK, ISSYS, JOB_NAME, LAST_LOGIN_TIME, LEADER_NAMES, LOGIN_NAME, MOBILE_PHONE, OFFICE_PHONE, PASSWORD, PHOTO_UUID, REMARK, ROLE_NAMES, SEX, TENANT_ID, TRACE, USER_NAME, HOME_PHONE, PERSONNEL_AREA, MAJOR_JOB_NAME, OTHER_JOB_NAMES, PRINCIPAL_COMPANY, EXTERNAL_ID, BOUSER, BOPWD, ENGLISH_NAME, OTHER_MOBILE_PHONE, MAIN_EMAIL, OTHER_EMAIL, SMALL_PHOTO_UUID, TRACE_DATE, LOGIN_NAME_BAK)
values ('@{multi.tenancy.tenant.id}' || 'a10b-6ce9-492d-aee9-1dea9181ba17', '06-12月-13 11.34.48.000000 上午', 'U0010000001', 'U0010000001', '29-10月-15 07.45.32.984000 下午', 607, 1, 1, '111', null, '区政府/信息管理中心/系统应用部', '', '11111111', 1, '11111111', '单位资料编辑人员;共享资料管理人员;单位资料管理人员', 'U' || replace('@{multi.tenancy.tenant.id}','T','') || '0000001', '', 0, 1, '', '05-12月-14 10.57.24.000000 上午', '', 'adm_pt', '13237085777', '56587711', '8a0be58a40aa0e6fedd2577ed8ebff42', '', '是的', '', '1', '@{multi.tenancy.tenant.id}', '出差', '系统管理员', '', '', '区政府/信息管理中心/系统应用部/实施工程师', '', '', '', 'ldxit', 'ldx2013', '', '12345678901', 'lcptest@leedarson.com', 's11211', 'a030a2fb1e0240978725f1bc23002962', '29-10月-15 07.45.32.982000 下午', 'ldx');


insert into org_user_role (USER_UUID, ROLE_UUID, TENANT_ID)
values ('@{multi.tenancy.tenant.id}' || 'a10b-6ce9-492d-aee9-1dea9181ba17', 'e96683fc-5008-41f6-a719-be85035ff14a', '@{multi.tenancy.tenant.id}');



insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('2c13a319-2717-4e1d-bd12-172bd040d498', '27-10月-14 04.16.40.662000 下午', 'U0010000001', 'U0010000001', '27-10月-14 04.16.40.662000 下午', 4, '003', 'MyLeader', '我的所有领导', '', '', '我的领导(所有)', 1, '@{multi.tenancy.tenant.id}');

insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('8813f0c6-f8ac-409e-a9fd-d8e2b8678654', '07-12月-13 11.48.59.740000 上午', 'U0010000001', 'U0010000001', '07-12月-13 11.48.59.740000 上午', 2, '004', 'MyUnderling', '我的下属', '', '', '我的下属', 1, '@{multi.tenancy.tenant.id}');

insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('04ed6627-9e52-40d6-b3ef-dae1ebb2a75f', '07-12月-13 11.49.03.930000 上午', 'U0010000001', 'U0010000001', '07-12月-13 11.49.03.930000 上午', 2, '005', 'PublicGroup', '公共群组', '', '', '公共群组', 1, '@{multi.tenancy.tenant.id}');

insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('679f8d4e-53bf-445e-8735-67b3edb91beb', '20-11月-14 03.43.32.679000 下午', 'U0010000001', 'U0010000001', '20-11月-14 03.43.32.679000 下午', 5, '006', 'PrivateGroup', '个人群组', '', '', '个人群组', 1, '@{multi.tenancy.tenant.id}');

insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('9ed4c06d-815b-45fc-9b07-33292f14884a', '07-12月-13 11.49.13.505000 上午', 'U0010000001', 'U0010000001', '07-12月-13 11.49.13.505000 上午', 2, '007', 'MyParentDept', '上级部门', '', '', '上级部门', 1, '@{multi.tenancy.tenant.id}');

insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('f44b82fc-df31-42de-a794-9a86d2cd972b', '07-12月-13 11.49.17.728000 上午', 'U0010000001', 'U0010000001', '07-12月-13 11.49.17.728000 上午', 2, '008', 'OnlineUser', '在线人员', '', '', '在线人员', 1, '@{multi.tenancy.tenant.id}');

insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('6e6191d2-2a3f-45ad-98f0-f8eae9e0bd9f', '07-12月-13 11.49.22.814000 上午', 'U0010000001', 'U0010000001', '07-12月-13 11.49.22.814000 上午', 2, '009', 'Dept', '部门', '', '', '部门', 1, '@{multi.tenancy.tenant.id}');

insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('e4a20cfb-bdde-41b2-b094-53e571660168', '27-11月-15 12.44.13.087000 下午', 'U1610000001', 'U1610000001', '27-11月-15 12.44.13.087000 下午', 13, '010', 'Unit', '单位通讯录', '', '', '单位通讯录', 0, '@{multi.tenancy.tenant.id}');

insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('2bd64199-561a-44b3-b035-9896563d771f', '12-8月 -15 08.32.16.320000 下午', 'U0010000001', 'U0010000001', '12-8月 -15 08.32.16.320000 下午', 28, '011', 'UnitUser', '集团通讯录', '', '', '集团通讯录', 1, '@{multi.tenancy.tenant.id}');

insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('8afec188-565a-4b49-9dda-e1fa562b82a0', '07-8月 -15 05.33.22.498000 下午', 'U0010000001', 'U0010000001', '07-8月 -15 05.33.22.498000 下午', 28, '012', 'DOCUMENT', '业务单位通讯录', '', '', '业务单位通讯录', 0, '@{multi.tenancy.tenant.id}');

insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('09433bd8-f527-4c96-9607-8902b370e38d', '01-4月 -14 04.34.59.882000 下午', 'U0010000001', 'U0010000001', '01-4月 -14 04.34.59.882000 下午', 9, '013', 'BizUser', '业务人员通讯录', '', '', '', 0, '@{multi.tenancy.tenant.id}');

insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('55fcf84e-378b-4486-b5d4-cc27b16a9598', '18-8月 -14 06.44.57.420000 下午', 'U0010000001', 'U0010000001', '18-8月 -14 06.44.57.420000 下午', 0, '014', 'Job', '职位', '', '', '', 1, '@{multi.tenancy.tenant.id}');

insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('87164041-8508-433e-a4d1-4e3e91fdbbf1', '10-9月 -14 11.37.30.919000 上午', 'U0010000001', 'U0010000001', '10-9月 -14 11.37.30.919000 上午', 0, '015', 'Duty', '职务', '', '', '', 1, '@{multi.tenancy.tenant.id}');

insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('204bbf2e-bcf6-40ac-aaf0-b315f2f233df', '18-9月 -14 10.14.10.219000 下午', 'U0010000001', 'U0010000001', '18-9月 -14 10.14.10.219000 下午', 0, '016', 'JobDuty', '职位（职务）', '', '', '', 1, '@{multi.tenancy.tenant.id}');

insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('f100b8cd-5706-40b4-beca-158cccd23b42', '05-10月-14 06.31.26.833000 下午', 'U0010000001', 'U0010000001', '05-10月-14 06.31.26.833000 下午', 0, '017', 'MyUnit1', '我的单位(职位)', '', '', '', 0, '@{multi.tenancy.tenant.id}');

insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('6e7f9461-be21-4f03-9d38-8adf9dab322e', '29-10月-14 04.57.07.022000 下午', 'U0010000001', 'U0010000001', '29-10月-14 04.57.07.022000 下午', 2, '018', 'MyDirectLeader', '我的直属领导', '', '', '', 1, '@{multi.tenancy.tenant.id}');

insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('9cb2d38e-1e52-41ac-b5f3-980427ac9bfd', '09-12月-14 05.10.59.073000 下午', 'U0010000001', 'U0010000001', '09-12月-14 05.10.59.073000 下午', 0, '001', 'MyUnit', '我的单位', '', '', '我的单位', 1, '@{multi.tenancy.tenant.id}');

insert into ORG_OPTION (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, CODE, ID, NAME, OWNER, OWNER_NAME, REMARK, SHOW, TENANT_ID)
values ('530c173c-e295-4327-80b5-7e78784bb825', '09-12月-14 05.11.22.358000 下午', 'U0010000001', 'U0010000001', '09-12月-14 05.11.22.358000 下午', 1, '002', 'MyDept', '我的部门', '', '', '我的部门', 1, '@{multi.tenancy.tenant.id}');
  END IF;
end;
/
