-- 抄送人过多，字段长度不够，进行扩充
-- Add/modify columns
alter table WF_TASK_OPERATION modify COPY_USER_ID VARCHAR2(4000 CHAR);
