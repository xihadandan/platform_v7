-- Add/modify columns 
alter table MSG_HIS_MESSAGE_QUEUE add exec_ip VARCHAR2(64);
-- Add comments to the columns 
comment on column MSG_HIS_MESSAGE_QUEUE.exec_ip
  is '执行IP';