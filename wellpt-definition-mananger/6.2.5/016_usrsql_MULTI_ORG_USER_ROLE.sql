--一、删除脏数据
delete from multi_org_user_role t where not exists (
select * from (
select max(uuid) uuid, user_id, ROLE_UUID from multi_org_user_role this_ group by user_id , ROLE_UUID ) t2 where t2.uuid = t.uuid
);

--二、添加唯一组合键
ALTER TABLE MULTI_ORG_USER_ROLE ADD CONSTRAINT "UQ_USER_ID_ROLE_UUID" UNIQUE ("USER_ID", "ROLE_UUID");