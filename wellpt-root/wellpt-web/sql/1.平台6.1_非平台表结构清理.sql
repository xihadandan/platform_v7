
-------------------------------
----     非平台表单清理      ----
-------------------------------

DECLARE
  CURSOR del_form_tabale_data_cur IS
    select table_name
      from all_tables
     where owner = (select user from dual)
       and table_name like 'UF_%';
  rsd del_form_tabale_data_cur%rowtype;

  CURSOR del_form_tabale_cur IS
    select table_name
      from all_tables
     where owner = (select user from dual)
       and table_name not like 'UF_PT_%'
       and table_name like 'UF_%';
  rs del_form_tabale_cur%rowtype;

  CURSOR temp_ddcursor IS
    select table_name
      from all_tables
     where owner = (select user from dual)
       and (table_name like 'MAIL_%' or table_name like 'JAMES_MAIL%' or
           table_name like 'USERFORM_%' or table_name like 'FM_%' or
           table_name like 'DX_%' or table_name like 'PSI_%' or
           table_name like 'SCHEDULE%' or table_name like 'VIEW_%' or
           table_name like 'DYVIEW_%' or table_name like 'CMS_%' or
           table_name like 'TEST_%' or table_name like 'OA_%' or
           table_name like 'DEMO_%');
  rsdd temp_ddcursor%rowtype;

BEGIN
  for rs in del_form_tabale_cur loop
    begin
      execute immediate 'drop table ' || rs.table_name;
    end;
  end loop;

  for rsd in del_form_tabale_data_cur loop
    begin
      execute immediate 'truncate table ' || rsd.table_name;
    end;
  end loop;

  for rsdd in temp_ddcursor loop
    begin
      execute immediate 'drop table ' || rsdd.table_name;
    end;
  end loop;
END;