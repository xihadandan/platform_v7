-- Create table
create table CD_IMG_CATEGORY
(
  uuid           VARCHAR2(255 CHAR) not null,
  create_time    TIMESTAMP(6),
  creator        VARCHAR2(255 CHAR),
  modifier       VARCHAR2(255 CHAR),
  modify_time    TIMESTAMP(6),
  rec_ver        NUMBER(10),
  code           VARCHAR2(255 CHAR),
  name           VARCHAR2(200 CHAR),
  icon           VARCHAR2(64 CHAR),
  color          VARCHAR2(64 CHAR),
  description    VARCHAR2(400 CHAR),
  system_unit_id VARCHAR2(12)
);
-- Add comments to the table 
comment on table CD_IMG_CATEGORY
  is '图片分类';
-- Add comments to the columns 
comment on column CD_IMG_CATEGORY.name
  is '分类名称';
comment on column CD_IMG_CATEGORY.icon
  is '图标';
comment on column CD_IMG_CATEGORY.color
  is '图标颜色';
comment on column CD_IMG_CATEGORY.description
  is '分类描述';
comment on column CD_IMG_CATEGORY.system_unit_id
  is '归属系统单位';
-- Create/Recreate primary, unique and foreign key constraints 
alter table CD_IMG_CATEGORY
  add constraint PK_CD_IMG_CATEGORY primary key (UUID);
