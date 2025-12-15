-- Add/modify columns 
alter table WF_TASK_INSTANCE_TOPPING modify is_topping default  null;
-- Add/modify columns 
alter table WF_TASK_INSTANCE_TOPPING add low_priority NUMBER(10);
-- Add comments to the columns 
comment on column WF_TASK_INSTANCE_TOPPING.low_priority
  is '低优先级字段，值越高代表优先级越低，默认值为0，每处理一次稍后处理值加1';
