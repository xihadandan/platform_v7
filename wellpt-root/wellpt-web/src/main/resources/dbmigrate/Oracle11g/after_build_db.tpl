declare
	delobj varchar2(128);
	objstu varchar2(128);
	cnt     number;
begin
	objstu := '${multi.tenancy.tenant.status}';
	delobj := 'U' || replace('${multi.tenancy.tenant.id}','T','') || '0000001';
	select COUNT(*) into cnt from ${multi.tenancy.org.username}.org_user t where t.ID = delobj;
	-- 启用或待审核的租户才添加租户管理员
	IF (objstu = '1' or objstu = '2') and cnt < 1 then
insert into ${multi.tenancy.org.username}.org_user (UUID, CREATE_TIME, CREATOR, MODIFIER, MODIFY_TIME, REC_VER, ACCOUNT_NON_EXPIRED, ACCOUNT_NON_LOCKED, CODE, CREDENTIALS_NON_EXPIRED, DEPARTMENT_NAME, DEPUTY_NAMES, EMPLOYEE_NUMBER, ENABLED, FAX, GROUP_NAMES, ID, ID_NUMBER, IS_ALLOWED_BACK, ISSYS, JOB_NAME, LAST_LOGIN_TIME, LEADER_NAMES, LOGIN_NAME, MOBILE_PHONE, OFFICE_PHONE, PASSWORD, PHOTO_UUID, REMARK, ROLE_NAMES, SEX, TENANT_ID, TRACE, USER_NAME, HOME_PHONE, PERSONNEL_AREA, MAJOR_JOB_NAME, OTHER_JOB_NAMES, PRINCIPAL_COMPANY, EXTERNAL_ID, BOUSER, BOPWD, ENGLISH_NAME, OTHER_MOBILE_PHONE, MAIN_EMAIL, OTHER_EMAIL, SMALL_PHOTO_UUID, TRACE_DATE, LOGIN_NAME_BAK)
values ('${multi.tenancy.tenant.id}' || 'a10b-6ce9-492d-aee9-1dea9181ba17', '06-12月-13 11.34.48.000000 上午', 'U0010000001', 'U0010000001', '29-10月-15 07.45.32.984000 下午', 607, 1, 1, '111', null, '区政府/信息管理中心/系统应用部', '', '11111111', 1, '11111111', '单位资料编辑人员;共享资料管理人员;单位资料管理人员', 'U' || replace('${multi.tenancy.tenant.id}','T','') || '0000001', '', 0, 1, '', '05-12月-14 10.57.24.000000 上午', '', 'adm_pt', '13237085777', '56587711', '8a0be58a40aa0e6fedd2577ed8ebff42', '', '是的', '', '1', '${multi.tenancy.tenant.id}', '出差', '系统管理员', '', '', '区政府/信息管理中心/系统应用部/实施工程师', '', '', '', 'ldxit', 'ldx2013', '', '12345678901', 'lcptest@leedarson.com', 's11211', 'a030a2fb1e0240978725f1bc23002962', '29-10月-15 07.45.32.982000 下午', 'ldx');

insert into ${multi.tenancy.org.username}.org_user_role (USER_UUID, ROLE_UUID)
values ('${multi.tenancy.tenant.id}' || 'a10b-6ce9-492d-aee9-1dea9181ba17', 'e96683fc-5008-41f6-a719-be85035ff14a');
	END IF;
end;
/