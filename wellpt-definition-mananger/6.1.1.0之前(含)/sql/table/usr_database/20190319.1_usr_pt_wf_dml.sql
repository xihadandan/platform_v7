-- Add/modify columns 
alter table WF_TASK_TIMER add over_directions VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column WF_TASK_TIMER.over_directions
  is '计时结束流向';
