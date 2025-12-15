create table WH_WORK_SETTINGS 
(
   UUID                 varchar(255)   not null comment '唯一主键',
   CREATE_TIME          datetime comment '创建时间(操作时间)',
   CREATOR              varchar(255) comment '创建人',
   MODIFIER             varchar(255) comment '更新人',
   MODIFY_TIME          datetime comment '更新时间',
   REC_VER              int comment '数据版本',
   SYSTEM_UNIT_ID       varchar(255) comment '系统单位id',
   WORK_TIME            varchar(5) comment '格式 ：01:00',
    primary key (UUID)
);