select *
from (
-- 职位
select t.id as org_id
from org_job t
where t.id = :unit_in_expression_org_id
union all
-- 部门下的职位
select j.id as org_id
from org_job j, org_duty d
where j.duty_uuid = d.uuid
and exists (select t1.uuid
from org_department t1
where j.department_uuid = t1.uuid
start with t1.id = :unit_in_expression_org_id
connect by prior t1.uuid = t1.parent_uuid)
union all
-- 用户
select u.id as org_id
from org_user u
where u.id = :unit_in_expression_org_id
union all
-- 职位下的用户
select t.id as org_id
from org_user t
where exists (select distinct t1.user_uuid
from org_user_job t1
where exists (select t2.uuid
from org_job t2
where t2.id = :unit_in_expression_org_id
and t2.uuid = t1.job_uuid)
and t.uuid = t1.user_uuid)
union all
-- 部门下的职位下的用户
select t.id as org_id
from org_user t
where exists
(select distinct t1.user_uuid
from org_user_job t1
where exists
(select t2.uuid
from org_job t2
where exists
(select j.id
from org_job j
where exists
(select dept.uuid
from org_department dept
where j.department_uuid = dept.uuid
start with dept.id = :unit_in_expression_org_id
connect by prior
dept.uuid = dept.parent_uuid)
and t2.id = j.id)
and t2.uuid = t1.job_uuid)
and t.uuid = t1.user_uuid)
union all
-- 部门及部门下的部门
select t1.id as org_id
from org_department t1
start with t1.id = :unit_in_expression_org_id
connect by prior t1.uuid = t1.parent_uuid)
