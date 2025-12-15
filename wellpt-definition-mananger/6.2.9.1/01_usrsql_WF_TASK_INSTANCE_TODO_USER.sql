create table WF_TASK_INSTANCE_TODO_USER 
(
   UUID                 VARCHAR2(255 char)   not null,
   CREATE_TIME          TIMESTAMP,
   CREATOR              VARCHAR2(255 char),
   MODIFIER             VARCHAR2(255 char),
   MODIFY_TIME          TIMESTAMP,
   REC_VER              NUMBER(10),
   TASK_INST_UUID       VARCHAR2(255 char),
   TODO_USER_ID         VARCHAR2(100 char),
   TODO_USER_NAME       VARCHAR2(100 char),
   TODO_USER_JOB_PATH   VARCHAR2(1000 char),
   TASK_ID              VARCHAR2(255 char),
   FLOW_INST_UUID       VARCHAR2(255 char)   not null,
   constraint PK_WF_TASK_INSTANCE_TODO_USER primary key (UUID)
);

comment on table WF_TASK_INSTANCE_TODO_USER is
'环节实例_任务待办人员扩展表 ';

comment on column WF_TASK_INSTANCE_TODO_USER.UUID is
'UUID，系统字段';

comment on column WF_TASK_INSTANCE_TODO_USER.CREATE_TIME is
'创建时间';

comment on column WF_TASK_INSTANCE_TODO_USER.CREATOR is
'创建人';

comment on column WF_TASK_INSTANCE_TODO_USER.MODIFIER is
'修改人';

comment on column WF_TASK_INSTANCE_TODO_USER.MODIFY_TIME is
'修改时间';

comment on column WF_TASK_INSTANCE_TODO_USER.REC_VER is
'版本号';

comment on column WF_TASK_INSTANCE_TODO_USER.TASK_INST_UUID is
'所在环节实例UUID';

comment on column WF_TASK_INSTANCE_TODO_USER.TODO_USER_ID is
'任务待办人员ID';

comment on column WF_TASK_INSTANCE_TODO_USER.TODO_USER_NAME is
'任务待办人员名称';

comment on column WF_TASK_INSTANCE_TODO_USER.TODO_USER_JOB_PATH is
'任务待办人员职位路径';

comment on column WF_TASK_INSTANCE_TODO_USER.TASK_ID is
'环节ID';

comment on column WF_TASK_INSTANCE_TODO_USER.FLOW_INST_UUID is
'流程实例UUID';
