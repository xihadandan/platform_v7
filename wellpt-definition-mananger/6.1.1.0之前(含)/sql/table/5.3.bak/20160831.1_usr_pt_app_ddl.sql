-- Add/modify columns 
alter table APP_APPLICATION add correlative_function VARCHAR2(255);
-- Add comments to the columns 
comment on column APP_APPLICATION.correlative_function
  is '关联的功能ID';