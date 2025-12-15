-- Add/modify columns 
alter table WF_TASK_TIMER_LOG add remark VARCHAR2(2000 CHAR);
-- Add comments to the columns 
comment on column WF_TASK_TIMER_LOG.remark
  is '备注';

  
-- Add/modify columns 
alter table WF_TASK_TIMER add timing_state NUMBER(10) default 0;
-- Add comments to the columns 
comment on column WF_TASK_TIMER.timing_state
  is '计时状态(0正常、1预警、2到期、3逾期)';


-- Add/modify columns 
alter table WF_FLOW_INSTANCE add timing_state NUMBER(10) default 0;
-- Add comments to the columns 
comment on column WF_FLOW_INSTANCE.timing_state
  is '计时状态(0正常、1预警、2到期、3逾期)';


