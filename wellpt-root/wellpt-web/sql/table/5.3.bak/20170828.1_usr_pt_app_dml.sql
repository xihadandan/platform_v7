-- Add/modify columns 
alter table APP_SYSTEM add title VARCHAR2(255);
-- Add comments to the columns 
comment on column APP_SYSTEM.title
  is '标题';

-- Add/modify columns 
alter table APP_MODULE add title VARCHAR2(255);
-- Add comments to the columns 
comment on column APP_MODULE.title
  is '标题';
  
-- Add/modify columns 
alter table APP_APPLICATION add title VARCHAR2(255);
-- Add comments to the columns 
comment on column APP_APPLICATION.title
  is '标题';