-- create table
create table wm_mailbox_info (
  uuid varchar(255) not null comment '主键uuid',
  create_time datetime comment '创建时间',
  creator varchar(255) comment '创建人',
  modifier varchar(255)  comment '更新人',
  modify_time datetime  comment '更新时间',
  rec_ver int  comment '数据版本',
  from_user_name varchar(255)  comment '发送人名称',
  from_mail_address varchar(255)  comment '发送人邮箱地址',
  to_user_name longtext comment '接收人名称，多个以分号隔开',
  to_mail_address longtext comment '接收人邮箱地址/部门、职位、用户、群组id',
  cc_user_name longtext comment '抄送人名称，多个以分号隔开',
  cc_mail_address longtext comment '抄送人邮箱地址/部门、职位、用户、群组id',
  bcc_user_name longtext comment '密送人名称，多个以分号隔开',
  bcc_mail_address longtext comment '密送人邮箱地址/部门、职位、用户、群组id',
  actual_to_mail_address longtext comment '真实接收人',
  actual_cc_mail_address longtext comment '真实抄送人',
  actual_bcc_mail_address longtext comment '真实密送人',
  subject text comment '主题',
  content longtext comment '邮件文本内容',
  repo_file_names text comment 'mogodb附件名称，多个以分隔开',
  repo_file_uuids text comment 'mogodb附件uuid，多个以分隔开',
  mail_size int comment '邮件大小',
  read_receipt_status int default 0 not null comment '阅读回执状态（0：不需要 默认值，1:需要，2，已发送回执，3：取消发送回执）',
  priority int default 3 not null comment '优先级（1：最高，2：高，3: 正常 默认值，4：低，5：最低）',
  send_time datetime comment '发送时间',
  is_public_email int(1) comment '是否公网邮箱 0：否，1：是',
  primary key (uuid)
) comment='邮件信息表';

create table wm_mailbox_info_user (
  uuid varchar(255) not null comment '主键uuid',
  create_time datetime  comment '创建时间',
  creator varchar(255)  comment '创建人',
  modifier varchar(255)  comment '更新人',
  modify_time datetime  comment '更新时间',
  rec_ver int  comment '数据版本',
  user_id varchar(255)  comment '用户id',
  user_name varchar(255)  comment '用户名称',
  mail_address varchar(255)  comment '邮件地址',
  read_receipt_status int default 0 not null comment '阅读回执状态（0：不需要 默认值，1:需要，2，已发送回执，3：取消发送回执）',
  send_status int  comment '发送状态（0：待发送，1：已发送，2：发送失败）',
  send_count int default 0 not null comment '发送次数',
  fail_msg varchar(2000)  comment '失败原因',
  next_send_time datetime  comment '下次执行发送时间',
  mid int  comment '邮件message-id，从邮件服务器上取到的邮件唯一标识',
  mailbox_name varchar(255)  comment '邮件文件夹 系统文件夹：（inbox：收件箱，outbox：发件箱，draft：草稿箱，recycle：回收站 ），其他值代表 其他文件夹',
  is_read int  comment '是否已读 0=未读 1=已读',
  status int  comment '邮件状态 0草稿 1发送成功 2接收成功 3接收失败（空间不足） -1删除 -2彻底删除',
  revoke_status int  comment '撤回状态：0 撤回失败 1 撤回成功 2 部分撤回成功',
  system_unit_id varchar(64)  comment '系统单位id',
  mail_info_uuid varchar(255)  comment '邮件信息uuid',
  primary key (uuid),
  key index_w_m_i_u_user_id (user_id),
  key index_w_m_i_u_mid (mid),
  key index_w_m_i_u_mail_address (mail_address),
  key index_w_m_i_u_mailbox_name (mailbox_name),
  key index_w_m_i_u_mail_info_uuid (mail_info_uuid)
) comment='邮件信息关联用户表';






