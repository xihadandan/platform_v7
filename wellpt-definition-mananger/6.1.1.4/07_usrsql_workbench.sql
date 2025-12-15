-- 工作台新需求相关脚本
-- 添加系统生成标识字段
alter table AUDIT_PRIVILEGE add system_def NUMBER(1,0) default 0 not null ;
alter table AUDIT_ROLE add system_def NUMBER(1,0) default 0 not null ;
-- Add comments to the columns 
comment on column AUDIT_PRIVILEGE.system_def is '系统默认生成（0：否，1：是）';
comment on column AUDIT_ROLE.system_def is '系统默认生成（0：否，1：是）';
