declare
  delobj varchar2(128);
  cnt     number;
begin
  delobj := '${multi.tenancy.tenant.jdbcUsername}';
  select COUNT(*) into cnt from dba_users t where UPPER(t.username) = UPPER(delobj);
  IF cnt > 0 then
    EXECUTE immediate 'DROP USER ' || delobj || ' CASCADE';
  END IF;
end;
/

-- Create the user 
create user ${multi.tenancy.tenant.jdbcUsername}
  default tablespace OA_DATA IDENTIFIED BY ${multi.tenancy.tenant.jdbcPassword}
  temporary tablespace TEMP
  profile DEFAULT
  quota unlimited on users;
-- Grant/Revoke object privileges 
grant select on DBA_2PC_PENDING to ${multi.tenancy.tenant.jdbcUsername};
grant select on DBA_PENDING_TRANSACTIONS to ${multi.tenancy.tenant.jdbcUsername};
grant execute on DBMS_SYSTEM to ${multi.tenancy.tenant.jdbcUsername};
grant select on PENDING_TRANS$ to ${multi.tenancy.tenant.jdbcUsername};
-- Grant/Revoke role privileges 
grant connect to ${multi.tenancy.tenant.jdbcUsername};
grant exp_full_database to ${multi.tenancy.tenant.jdbcUsername};
grant imp_full_database to ${multi.tenancy.tenant.jdbcUsername};
grant resource to ${multi.tenancy.tenant.jdbcUsername};
-- Grant/Revoke system privileges 
grant debug connect session to ${multi.tenancy.tenant.jdbcUsername};
grant unlimited tablespace to ${multi.tenancy.tenant.jdbcUsername};
alter user ${multi.tenancy.tenant.jdbcUsername} quota unlimited on OA_DATA;
grant read,write on directory BACKUP to ${multi.tenancy.tenant.jdbcUsername};