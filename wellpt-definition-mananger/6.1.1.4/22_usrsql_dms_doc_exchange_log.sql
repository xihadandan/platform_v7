update DMS_DOC_EXCHANGE_LOG l set l.operator = (
   select u.user_name from multi_org_user_account u where u.id = l.operator
) where exists (
  select 1 from multi_org_user_account e where e.id = l.operator
)