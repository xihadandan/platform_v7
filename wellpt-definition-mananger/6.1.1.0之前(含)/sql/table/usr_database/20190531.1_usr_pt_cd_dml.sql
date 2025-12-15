-- Add/modify columns 
alter table CD_DATA_STORE_DEFINITION add camel_column_index NUMBER(1);
-- Add comments to the columns 
comment on column CD_DATA_STORE_DEFINITION.camel_column_index
  is '使用驼峰风格列索引，对SQL命名查询、SQL语句、数据库表、数据库视图有效';
