-- Add/modify columns 
alter table MSG_MESSAGE_TEMPLATE add system_unit_id varchar2(64);


-- Add/modify columns 
alter table MSG_WEBSERVICE_PARM add system_unit_id varchar2(64);



alter table MSG_MESSAGE_OUTBOX add system_unit_id varchar2(64);
alter table msg_message_inbox add system_unit_id varchar2(64);
alter table msg_message_queue add system_unit_id varchar2(64);

alter table MSG_HIS_MESSAGE_QUEUE add system_unit_id varchar2(64);
alter table msg_short_message add system_unit_id varchar2(64);
