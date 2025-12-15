select *
from (
-- 部门及部门下的部门
select t1.id as org_id
from org_department t1
start with t1.id = :unit_in_expression_org_id
connect by prior t1.uuid = t1.parent_uuid)