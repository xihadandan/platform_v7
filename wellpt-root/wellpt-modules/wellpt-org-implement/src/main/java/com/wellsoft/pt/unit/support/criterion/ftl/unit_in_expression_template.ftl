select *
from (
-- 部门
select t.id as org_id
from org_department t
where t.id = :unit_in_expression_org_id
union all
-- 职位
select t.id as org_id
from org_job t
where t.id = :unit_in_expression_org_id
union all
-- 用户
select t.id as org_id
from org_user t
where t.id = :unit_in_expression_org_id
union all
-- 部门下的职位、用户
select t.org_id as org_id
from org_dept_principal t
where t.department_uuid in
(select t1.uuid
from org_department t1
start with uuid = (select uuid
from org_department t2
where t2.id = :unit_in_expression_org_id)
connect by prior t1.uuid = t1.parent_uuid)
union all
-- 部门下的职位用户
select t.id as org_id
from org_user t
where exists (select distinct t1.user_uuid
from org_user_job t1
where exists (select t2.uuid
from org_job t2
where t2.id = :unit_in_expression_org_id
and t2.uuid = t1.job_uuid)
and t.uuid = t1.user_uuid))
