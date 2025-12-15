alter table WF_TASK_IDENTITY
    add VIEW_FORM_MODE NUMBER(10, 0);
alter table WF_TASK_IDENTITY
    add TODO_TYPE_OPERATE NUMBER(10, 0);

comment on column WF_TASK_IDENTITY.VIEW_FORM_MODE is '查看表单方式';
comment on column WF_TASK_IDENTITY.TODO_TYPE_OPERATE is '办理类型操作权限';