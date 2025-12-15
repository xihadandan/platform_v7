alter table WF_FLOW_SETTING
    add ATTR_VAL_CLOB clob;
update WF_FLOW_SETTING t1
set t1.ATTR_VAL_CLOB = t1.ATTR_VAL;
commit;
alter table WF_FLOW_SETTING rename column ATTR_VAL to ATTR_VAL_BAK;
alter table WF_FLOW_SETTING rename column ATTR_VAL_CLOB to ATTR_VAL;
