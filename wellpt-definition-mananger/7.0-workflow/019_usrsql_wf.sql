alter table WF_FLOW_INSTANCE
    add overdue_time timestamp;
COMMENT
    ON COLUMN WF_FLOW_INSTANCE.overdue_time IS '逾期时间';

alter table WF_TASK_INSTANCE
    add overdue_time timestamp;
COMMENT
    ON COLUMN WF_TASK_INSTANCE.overdue_time IS '逾期时间';

alter table WF_TASK_INSTANCE
    add task_timer_uuid varchar2(50);
COMMENT
    ON COLUMN WF_TASK_INSTANCE.task_timer_uuid IS '环节计时器UUID';

alter table WF_TASK_IDENTITY
    add overdue_state number(1, 0);
COMMENT
    ON COLUMN WF_TASK_IDENTITY.overdue_state IS '逾期状态(0未逾期、1逾期中)';
create index wf_ti_os_idx on wf_task_identity (overdue_state);