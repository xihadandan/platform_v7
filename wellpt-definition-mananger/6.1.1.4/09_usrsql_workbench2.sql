-- 工作台新需求相关脚本
alter table MULTI_ORG_USER_WORK_INFO add def_page_uuid VARCHAR2(255);
-- Add comments to the columns 
comment on column MULTI_ORG_USER_WORK_INFO.def_page_uuid is '用户默认工作台uuid';
