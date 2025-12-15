-- 数据库名称替换为要更新的数据库
-- 1、查询丢失的主键，并执行输出的sql，不存在UUID列的表忽略掉
select 'alter table '|| table_name || ' add constraint ' ||table_name|| '_uuid primary key(uuid);'   
from (  select * from dba_tables t WHERE t.owner = '数据库名称' and t.TABLE_NAME NOT  like '%TEMP%') a
 where not exists (select *
          from user_constraints b
         where b.constraint_type = 'P'
           and a.table_name = b.table_name ) ;

-- 2、所有表单生成的表uf_%对字段mainform_data_uuid添加索引，执行输出的sql，不存在mainform_data_uuid的错误忽略掉
select 'create index ' || t.table_name || '_MDU_IDX on ' || t.table_name || '(mainform_data_uuid);'
from dba_tables t where t.owner = '数据库名称' AND t.table_name LIKE 'UF_%'; 