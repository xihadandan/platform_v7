alter table WF_FLOW_INSTANCE
    modify START_JOB_ID varchar(500);

alter table WF_TASK_IDENTITY
    add IDENTITY_ID varchar(500);
alter table WF_TASK_IDENTITY
    add IDENTITY_ID_PATH varchar(2000);
alter table WF_TASK_OPERATION
    add OPERATOR_IDENTITY_ID varchar(500);
alter table WF_TASK_OPERATION
    add OPERATOR_IDENTITY_NAME_PATH varchar(4000);
alter table ORG_GROUP_MEMBER
    add MEMBER_ID_PATH varchar(200);

comment
    on column WF_TASK_IDENTITY.IDENTITY_ID is '用户身份ID';
comment
    on column WF_TASK_IDENTITY.IDENTITY_ID_PATH is '用户身份ID路径';
comment
    on column WF_TASK_OPERATION.OPERATOR_IDENTITY_ID is '操作人身份ID';
comment
    on column WF_TASK_OPERATION.OPERATOR_IDENTITY_NAME_PATH is '操作人身份名称路径';
comment
    on column ORG_GROUP_MEMBER.MEMBER_ID_PATH is '成员ID路径';