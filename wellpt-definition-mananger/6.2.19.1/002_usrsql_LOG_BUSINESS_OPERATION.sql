
-- Create/Recreate indexes
create index LOG_BUSINESS_OPERATION_UNITID on LOG_BUSINESS_OPERATION (system_unit_id);

create index LOG_BUSINESS_OPERATION_CTIME on LOG_BUSINESS_OPERATION (create_time);

-- Create/Recreate indexes
create index LOG_BUSINESS_LOG_ID on LOG_BUSINESS_DETAILS (log_id);
