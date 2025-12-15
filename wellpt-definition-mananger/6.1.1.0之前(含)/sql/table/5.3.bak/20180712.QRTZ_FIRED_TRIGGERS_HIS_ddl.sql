-- 创建job触发器触发历史备份表
-- Create table
create table QRTZ_FIRED_TRIGGERS_HIS
(
  entry_id          VARCHAR2(95) not null,
  trigger_name      VARCHAR2(200) not null,
  trigger_group     VARCHAR2(200) not null,
  is_volatile       VARCHAR2(1) not null,
  instance_name     VARCHAR2(200) not null,
  fired_time        NUMBER(13) not null,
  priority          NUMBER(13) not null,
  state             VARCHAR2(16) not null,
  job_name          VARCHAR2(200),
  job_group         VARCHAR2(200),
  is_stateful       VARCHAR2(1),
  requests_recovery VARCHAR2(1),
  execute_time      TIMESTAMP(6),
  finish_time       TIMESTAMP(6),
  modify_time       TIMESTAMP(6)
)
tablespace OA_DATA
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Create/Recreate indexes 
create index SYS_C00206112 on QRTZ_FIRED_TRIGGERS_HIS (JOB_NAME)
  tablespace OA_DATA
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
create index SYS_C00206113 on QRTZ_FIRED_TRIGGERS_HIS (FIRED_TIME)
  tablespace OA_DATA
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
-- Create/Recreate primary, unique and foreign key constraints 
alter table QRTZ_FIRED_TRIGGERS_HIS
  add primary key (ENTRY_ID)
  using index 
  tablespace OA_DATA
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
  
  
  
  
  
  
  
  
  -- 创建触发器
CREATE OR REPLACE TRIGGER QRTZ_FIRED_HIS_TRIGGER
  AFTER INSERT OR DELETE on  qrtz_fired_triggers   For each row
BEGIN
 case when  INSERTING then
    IF :NEW.STATE='EXECUTING' THEN -- 执行中
      UPDATE qrtz_fired_triggers_his SET STATE=:NEW.STATE,execute_time =sysdate,JOB_NAME=:NEW.JOB_NAME,JOB_GROUP=:NEW.JOB_GROUP,IS_STATEFUL=:NEW.IS_STATEFUL,REQUESTS_RECOVERY=:NEW.REQUESTS_RECOVERY,MODIFY_TIME =SYSDATE  WHERE ENTRY_ID=:NEW.ENTRY_ID;
      ELSIF :NEW.STATE='ACQUIRED' THEN -- 开始执行
        INSERT INTO qrtz_fired_triggers_his (ENTRY_ID, TRIGGER_NAME,TRIGGER_GROUP,IS_VOLATILE,INSTANCE_NAME,FIRED_TIME,PRIORITY,STATE,JOB_NAME,JOB_GROUP,IS_STATEFUL,REQUESTS_RECOVERY) VALUES (:NEW.ENTRY_ID,:NEW.TRIGGER_NAME,:NEW.TRIGGER_GROUP,:NEW.IS_VOLATILE,:NEW.INSTANCE_NAME,:NEW.FIRED_TIME,:NEW.PRIORITY,:NEW.STATE,:NEW.JOB_NAME,:NEW.JOB_GROUP,:NEW.IS_STATEFUL,:NEW.REQUESTS_RECOVERY);
        END IF;
    when DELETING then  -- 删除就表示执行结束
       UPDATE qrtz_fired_triggers_his SET STATE=(case :OLD.STATE when 'EXECUTING' then 'FINISH' else 'ERROR' end  ),FINISH_TIME=sysdate,MODIFY_TIME =SYSDATE WHERE ENTRY_ID=:OLD.ENTRY_ID;
    end case;

END;

