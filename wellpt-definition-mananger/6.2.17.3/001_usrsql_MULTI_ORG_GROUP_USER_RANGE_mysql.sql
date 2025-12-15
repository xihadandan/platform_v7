create table MULTI_ORG_GROUP_USER_RANGE 
(
   UUID                 varchar(100)   not null comment '唯一主键',
   CREATE_TIME          datetime comment '创建时间(操作时间)',
   CREATOR              varchar(100) comment '创建人',
   MODIFIER             varchar(100) comment '更新人',
   MODIFY_TIME          datetime comment '更新时间',
   REC_VER              int comment '数据版本',
   GROUP_ID             varchar(100) comment '群组id',
   USER_RANGE_DISPLAY   varchar(255) comment '添加使用范围人员显示值字段',
   USER_RANGE_REAL      varchar(100)  comment '添加使用范围人员真实值字段',
    primary key (UUID)
);