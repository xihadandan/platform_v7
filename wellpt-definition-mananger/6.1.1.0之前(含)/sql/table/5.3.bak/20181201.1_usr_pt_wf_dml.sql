-- Add/modify columns 
alter table WF_TASK_INSTANCE add assignee_name VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column WF_TASK_INSTANCE.assignee_name
  is '前办理人名称';


update wf_task_instance t
   set t.assignee_name =
       (select u.user_name
          from multi_org_user_account u
         where u.id = t.assignee
           and rownum <= 1)
 where t.assignee_name is null;
commit;