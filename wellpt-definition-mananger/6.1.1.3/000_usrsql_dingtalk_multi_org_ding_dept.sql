-- Create table
create table MULTI_ORG_DING_DEPT
(
  create_dept_group NUMBER(1),
  uuid              VARCHAR2(255 CHAR) not null,
  ele_id            VARCHAR2(255 CHAR),
  id                VARCHAR2(255 CHAR),
  name              VARCHAR2(255 CHAR),
  parent_id         VARCHAR2(255 CHAR),
  auto_add_user     NUMBER(1),
  ext               VARCHAR2(255 CHAR),
  create_time       TIMESTAMP(6),
  creator           VARCHAR2(255),
  modifier          VARCHAR2(255),
  modify_time       TIMESTAMP(6),
  rec_ver           NUMBER(10,2)
);
-- Add comments to the table 
comment on table MULTI_ORG_DING_DEPT
  is '钉钉部门信息表';
-- Add comments to the columns 
comment on column MULTI_ORG_DING_DEPT.create_dept_group
  is '是否同步创建一个关联此部门的企业群，true表示是，false表示不是';
comment on column MULTI_ORG_DING_DEPT.ele_id
  is '部门节点id';
comment on column MULTI_ORG_DING_DEPT.id
  is '部门id';
comment on column MULTI_ORG_DING_DEPT.name
  is '部门名称';
comment on column MULTI_ORG_DING_DEPT.parent_id
  is '父部门id，根部门为1';
comment on column MULTI_ORG_DING_DEPT.auto_add_user
  is '当群已经创建后，是否有新人加入部门会自动加入该群，true表示是，false表示不是';
comment on column MULTI_ORG_DING_DEPT.create_time
  is '创建时间';
comment on column MULTI_ORG_DING_DEPT.creator
  is '创建人';
comment on column MULTI_ORG_DING_DEPT.modifier
  is '更新人';
comment on column MULTI_ORG_DING_DEPT.modify_time
  is '更新时间';
comment on column MULTI_ORG_DING_DEPT.rec_ver
  is '数据版本';
-- Create/Recreate primary, unique and foreign key constraints 
alter table MULTI_ORG_DING_DEPT
  add constraint PK_MULTI_ORG_DING_DEPT primary key (UUID)
  using index;
