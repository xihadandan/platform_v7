 create or replace view user_login_name_view as 
select  a.id,a.login_name,a.user_name,a.login_name_lower_case,a.login_name_zh,t.mobile_phone,t.ID_NUMBER,t.English_Name,t.main_email,t.EMPLOYEE_NUMBER,u.name system_unit_name 
from MULTI_ORG_USER_ACCOUNT a ,
         MULTI_ORG_USER_INFO t,multi_org_system_unit u 
         WHERE a.id = t.user_id and a.system_unit_id = u.id and is_forbidden = 0;  
---创建临时表		 
CREATE TABLE user_name_reduplicate_temp AS SELECT reduplicate FROM (
            SELECT DISTINCT reduplicate,id FROM (
                       SELECT LOWER(login_name) reduplicate , id FROM user_login_name_view) a
          ) b GROUP BY reduplicate HAVING COUNT(reduplicate)>1;