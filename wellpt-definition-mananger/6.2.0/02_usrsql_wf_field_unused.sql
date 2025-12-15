alter table wf_task_operation drop column action_status;
alter table wf_task_instance drop column is_topping;
alter table wf_task_delegation drop column duty_agent_uuid;
alter table wf_task_form_attachment_log drop column is_syn_back;