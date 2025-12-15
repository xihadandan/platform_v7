
--  Add/modify columns
alter table wm_mailbox_info add column is_public_email int(1) comment '是否公网邮箱 0：否，1：是';
alter table wm_mailbox_info add column keep_on_server int(1) comment '在服务器保留备份 0：否，1：是';
alter table wm_mailbox_info add column send_receipt int(1) comment '是否自动发送回执 0：否，1：是';


-- create table
create table wm_mailbox_info_status (
  uuid varchar(255) not null comment '主键uuid',
  create_time datetime comment '创建时间',
  creator varchar(255) comment '创建人',
  modifier varchar(255)  comment '更新人',
  modify_time datetime  comment '更新时间',
  rec_ver int  comment '数据版本',
  mail_info_uuid varchar(255)  comment '邮件信息UUID',
  mail_info_user_uuid varchar(255)  comment '发件人关联信息UUID',
  recipient_type int(1) comment '收件人类型（1，收件，2：抄送，3：密送）',
  user_id varchar(255) comment '用户Id',
  mail_name varchar(255) comment '邮件用户名',
  mail_address varchar(255) comment '邮件地址',
  status int(1) comment '状态（1：已发送，2：已投递到邮箱服务，3：地址不存在，4：未开启公网邮箱，5：无效邮件地址，6：邮件服务异常）',
  system_unit_id varchar(64) comment '系统单位Id',
  primary key (uuid),
  key index_w_m_i_u_user_uuid (mail_info_user_uuid),
  key index_w_m_i_u_info_uuid (mail_info_uuid)
) comment='邮件状态信息表';
