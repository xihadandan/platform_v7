alter table task_job_details add   STARTER  VARCHAR2(255 CHAR);
comment on column TASK_JOB_DETAILS.STARTER is '发起人';