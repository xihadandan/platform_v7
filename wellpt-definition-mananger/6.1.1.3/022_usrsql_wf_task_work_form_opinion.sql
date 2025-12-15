
-- 流程意见表字段变更，varchar2类型改为clob类型，解决意见超过4000的情况


-- 备份表

create table WF_TASK_FORM_OPINION_0623 as select * from WF_TASK_FORM_OPINION;

-- 数据类型变更

alter table WF_TASK_FORM_OPINION add content_clob_temp clob;

update wf_task_form_opinion set content_clob_temp = to_clob(content);

alter table WF_TASK_FORM_OPINION drop column content;

alter table WF_TASK_FORM_OPINION rename column content_clob_temp to CONTENT;





-- 备份表

create table wf_task_form_opinion_log_0623 as select * from wf_task_form_opinion_log;

-- 数据类型变更

alter table wf_task_form_opinion_log add content_clob_temp clob;

update wf_task_form_opinion_log set content_clob_temp = to_clob(content);

alter table wf_task_form_opinion_log drop column content;

alter table wf_task_form_opinion_log rename column content_clob_temp to content;
