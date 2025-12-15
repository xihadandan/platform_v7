-- create table
create table multi_group
(
  uuid              varchar(255) not null comment '唯一主键',
  create_time       datetime  comment '创建时间',
  creator           varchar(255) comment '创建人',
  modifier          varchar(255) comment '更新人',
  modify_time       datetime comment '更新时间',
  rec_ver           int comment  '版本号',
  name         varchar(255) comment '集团名称',
  code    varchar(255) comment '编号',
  is_enable   int comment '启用(0:否1:是)',
  note  varchar(255) comment '描述',
  id       varchar(50) comment '唯一id值',
  primary key (uuid)
)comment ='集团信息表';

-- create table
create table multi_group_tree_node
(
  uuid              varchar(255) not null comment '唯一主键',
  create_time       datetime  comment '创建时间',
  creator           varchar(255) comment '创建人',
  modifier          varchar(255) comment '更新人',
  modify_time       datetime comment '更新时间',
  rec_ver           int comment  '版本号',
  name         varchar(255) comment '名称',
  short_name varchar(255) comment '简称',
  id       varchar(50) comment '唯一id值',
  type    int comment '类型（1，集团，2，单位，3，分类）',
  parent_id varchar(50) comment '父级id',
  ele_id varchar(50) comment '集团关联节点id',
  group_uuid varchar(255) comment '集团uuid',
  seq int comment '排序',
  primary key (uuid)
)comment ='集团组织架构节点表';

