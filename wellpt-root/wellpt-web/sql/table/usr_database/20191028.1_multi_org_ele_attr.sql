-- Create table
create table MULTI_ORG_ELEMENT_ATTR
(
  uuid         VARCHAR2(255 CHAR) primary key ,
  create_time  TIMESTAMP(6),
  creator      VARCHAR2(255 CHAR),
  modifier     VARCHAR2(255 CHAR),
  modify_time  TIMESTAMP(6),
  rec_ver      NUMBER(10),
  seq          NUMBER(1) default 1,
  code         VARCHAR2(255 CHAR) not null,
  name         VARCHAR2(255 CHAR),
  attr_value   VARCHAR2(4000 CHAR),
  element_uuid VARCHAR2(255 CHAR) not null,
  attr_type    VARCHAR2(32),
  attr_display VARCHAR2(4000 CHAR)
) ;
-- Add comments to the table 
comment on table MULTI_ORG_ELEMENT_ATTR
  is '组织节点自定义扩展属性';
-- Add comments to the columns 
comment on column MULTI_ORG_ELEMENT_ATTR.seq
  is '排序号';
comment on column MULTI_ORG_ELEMENT_ATTR.code
  is '属性';
comment on column MULTI_ORG_ELEMENT_ATTR.name
  is '属性名';
comment on column MULTI_ORG_ELEMENT_ATTR.attr_value
  is '属性值';
comment on column MULTI_ORG_ELEMENT_ATTR.element_uuid
  is '组织节点UUID';