-- Create table
create table MULTI_GROUP
(
  UUID              VARCHAR2(255) not null,
  CREATE_TIME       TIMESTAMP(6),
  CREATOR           VARCHAR2(255),
  MODIFIER          VARCHAR2(255),
  MODIFY_TIME       TIMESTAMP(6),
  REC_VER           NUMBER(10),
  NAME         VARCHAR2(255),
  CODE    VARCHAR2(255),
  IS_ENABLE   NUMBER,
  NOTE VARCHAR2(255),
  ID       VARCHAR2(50),
  constraint PK_MULTI_GROUP primary key (UUID)
);

-- Add comments to the table
comment on table MULTI_GROUP is '集团信息表';
-- Add comments to the columns
comment on column MULTI_GROUP.UUID is '主键uuid';
comment on column MULTI_GROUP.CREATE_TIME is '创建时间';
comment on column MULTI_GROUP.CREATOR is '创建人';
comment on column MULTI_GROUP.MODIFIER is '更新人';
comment on column MULTI_GROUP.MODIFY_TIME is '更新时间';
comment on column MULTI_GROUP.REC_VER is '数据版本';

comment on column MULTI_GROUP.NAME is '集团名称';
comment on column MULTI_GROUP.CODE is '编号';
comment on column MULTI_GROUP.IS_ENABLE is '启用(0:否1:是)';
comment on column MULTI_GROUP.NOTE is '描述';
comment on column MULTI_GROUP.ID is '唯一Id值';

-- Create table
create table MULTI_GROUP_TREE_NODE
(
  UUID              VARCHAR2(255) not null,
  CREATE_TIME       TIMESTAMP(6),
  CREATOR           VARCHAR2(255),
  MODIFIER          VARCHAR2(255),
  MODIFY_TIME       TIMESTAMP(6),
  REC_VER           NUMBER(10),
  NAME         VARCHAR2(255),
  SHORT_NAME VARCHAR2(255),
  ID       VARCHAR2(50),
  TYPE    NUMBER,
  PARENT_ID VARCHAR2(50),
  ELE_ID VARCHAR2(50),
  GROUP_UUID VARCHAR2(255),
  SEQ NUMBER,
  constraint PK_MULTI_GROUP_TREE_NODE primary key (UUID)
);

-- Add comments to the table
comment on table MULTI_GROUP_TREE_NODE is '集团组织架构节点表';
-- Add comments to the columns
comment on column MULTI_GROUP_TREE_NODE.UUID is '主键uuid';
comment on column MULTI_GROUP_TREE_NODE.CREATE_TIME is '创建时间';
comment on column MULTI_GROUP_TREE_NODE.CREATOR is '创建人';
comment on column MULTI_GROUP_TREE_NODE.MODIFIER is '更新人';
comment on column MULTI_GROUP_TREE_NODE.MODIFY_TIME is '更新时间';
comment on column MULTI_GROUP_TREE_NODE.REC_VER is '数据版本';

comment on column MULTI_GROUP_TREE_NODE.NAME is '名称';
comment on column MULTI_GROUP_TREE_NODE.SHORT_NAME is '简称';
comment on column MULTI_GROUP_TREE_NODE.ID is '唯一Id值';
comment on column MULTI_GROUP_TREE_NODE.TYPE is '类型（1，集团，2，单位，3，分类）';
comment on column MULTI_GROUP_TREE_NODE.PARENT_ID is '父级Id';
comment on column MULTI_GROUP_TREE_NODE.ELE_ID is '集团关联节点ID';
comment on column MULTI_GROUP_TREE_NODE.GROUP_UUID is '集团UUID';
comment on column MULTI_GROUP_TREE_NODE.SEQ is '排序';

