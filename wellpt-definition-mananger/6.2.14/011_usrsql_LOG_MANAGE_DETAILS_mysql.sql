create table LOG_MANAGE_DETAILS 
(
   UUID                 varchar(255)   not null,
   CREATE_TIME          datetime comment '操作时间',
   CREATOR              varchar(255),
   MODIFIER             varchar(255),
   MODIFY_TIME          datetime,
   REC_VER              int,
   LOG_ID               varchar(255) comment '明细日志id 对应管理操作日志的uuid',
   ATTR_ID              varchar(255) comment '属性id',
   ATTR_NAME            varchar(1024) comment '属性名称',
   ATTR_TYPE            varchar(255) comment '属性类型',
   DATA_SHOW_TYPE       varchar(10) comment '前端数据展示类型：eg:文本，图片',
   BEFORE_VALUE         varchar(255) comment '操作前的数据值',
   AFTER_VALUE          varchar(255) comment '操作后的数据值',
   constraint PK_LOG_MANAGE_DETAILS primary key (UUID)
)comment ='管理操作日志详情';
