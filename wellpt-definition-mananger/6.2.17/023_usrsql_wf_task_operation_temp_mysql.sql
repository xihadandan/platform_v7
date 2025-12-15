-- create table
create table wf_task_operation_temp (
  uuid varchar(36) not null comment '主键uuid',
  create_time datetime comment '创建时间',
  creator varchar(255) comment '创建人',
  modifier varchar(255)  comment '更新人',
  modify_time datetime  comment '更新时间',
  rec_ver int  comment '数据版本',
  opinion_text varchar(4000)  comment '办理意见内容',
  assignee varchar(36)  comment '操作人ID',
  assignee_name varchar(255) comment '操作人名称',
  flow_inst_uuid varchar(36) comment '所在流程实例',
  task_inst_uuid varchar(36) comment '所在任务实例',
  task_identity_uuid varchar(36) comment '所在待办实体UUID',
  task_id varchar(36) comment '所在任务ID',
  task_name varchar(255) comment '所在任务名称',
  primary key (uuid)
) comment='环节保存操作意见';