select *
from (
-- 用户ID
select t.id as org_id
from org_user t
where t.id = :unit_in_expression_org_id
union all
-- 用户部门及所有上级部门
select t1.id as org_id
from org_department t1
start with t1.uuid in
(select duj.department_uuid
from org_department_user_job duj
where duj.user_uuid =
(select u.uuid
from org_user u
where u.id = :unit_in_expression_org_id))
connect by prior t1.parent_uuid = t1.uuid
union all
-- 用户群组及所有上级群组
select t1.id as org_id
from org_group t1
start with t1.uuid in
(select gu.group_uuid
from org_group_user gu
where gu.user_uuid =
(select u.uuid
from org_user u
where u.id = :unit_in_expression_org_id))
connect by prior t1.parent_uuid = t1.uuid
union all
-- 用户职位
select t1.id as org_id
from org_job t1
where t1.uuid in (select uj.job_uuid
from org_user_job uj
where uj.user_uuid =
(select u.uuid
from org_user u
where u.id = :unit_in_expression_org_id))
union all
-- 用户职务
select t.id as org_id
from org_duty t
where t.uuid in
(select t1.duty_uuid
from org_job t1
where t1.uuid in
(select uj.job_uuid
from org_user_job uj
where uj.user_uuid =
(select u.uuid
from org_user u
where u.id = :unit_in_expression_org_id))))
