select *
from (
-- 用户
select t.id as org_id
from org_user t
where t.id = :unit_in_expression_org_id
union all
-- 部门下的用户
select u.id as org_id
from org_department_user_job j, org_user u
where j.user_uuid = u.uuid
and exists (select t1.uuid
from org_department t1
where j.department_uuid = t1.uuid
start with t1.id = :unit_in_expression_org_id
connect by prior t1.uuid = t1.parent_uuid)
union all
-- 部门及部门下的部门
select t1.id as org_id
from org_department t1
start with t1.id = :unit_in_expression_org_id
connect by prior t1.uuid = t1.parent_uuid)
