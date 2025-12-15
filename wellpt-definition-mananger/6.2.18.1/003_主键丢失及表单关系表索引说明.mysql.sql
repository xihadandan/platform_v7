-- 数据库名称替换为要更新的数据库
-- 1、查询丢失的主键，并执行输出的sql，不存在UUID列的表忽略掉
SELECT
 concat_ws(' ', 'ALTER TABLE', t1.table_name, 'ADD PRIMARY KEY(UUID);') as add_pk_sql
FROM
	information_schema.TABLES t1
LEFT OUTER JOIN information_schema.TABLE_CONSTRAINTS t2 ON t1.table_schema = t2.TABLE_SCHEMA
AND t1.table_name = t2.TABLE_NAME
AND t2.CONSTRAINT_NAME IN ('PRIMARY')
WHERE
	t2.table_name IS NULL
AND t1.table_type = 'BASE TABLE'
AND t1.TABLE_SCHEMA = '数据库名称';

-- 2、所有表单生成的表uf_%对字段mainform_data_uuid添加索引，执行输出的sql，不存在mainform_data_uuid的错误忽略掉
select concat_ws(' ', 'create index', concat(t1.table_name, '_mdu_idx on'), t1.table_name, '(mainform_data_uuid);') 
from information_schema.TABLES t1 where t1.table_schema = '数据库名称' and t1.table_name like 'uf_%';