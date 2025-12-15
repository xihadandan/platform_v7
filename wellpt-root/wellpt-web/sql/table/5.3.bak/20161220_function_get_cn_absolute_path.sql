-- 获取数据字典全路径,uid:数据字典uuid,level表示往上追溯几层,-1表示全路径
create or replace function get_cn_absolute_path(uid in varchar2, 
                                             lev in number) 
											 return varchar2 as
  PATH varchar2(2000);
begin
  if lev = -1 then 
    begin
      select NAME_PATH
        into PATH
        from (select SUBSTR(SYS_CONNECT_BY_PATH(name, '/'), 2) NAME_PATH
                from (select name, rownum ro
                        from cd_data_dict
                       start with uuid = uid
                      connect by prior parent_uuid = uuid) newtab
               start with newtab.ro =
                          (select count(1)
                             from cd_data_dict
                            start with uuid = uid
                           connect by prior parent_uuid = uuid)
              connect by prior newtab.ro - 1 = newtab.ro
               order by level desc)
       where rownum = 2;
    end;
    return PATH;
  else 
    begin
      select NAME_PATH
        into PATH
        from (select SUBSTR(SYS_CONNECT_BY_PATH(name, '/'), 2) NAME_PATH
                from (select name, rownum ro
                        from cd_data_dict
                       start with uuid = uid
                      connect by prior parent_uuid = uuid) newtab
               start with newtab.ro = lev + 1
              connect by prior newtab.ro - 1 = newtab.ro
               order by level desc)
       where rownum = 1;
    end;
    return PATH;
  end if;
end get_cn_absolute_path;
