-- Add/modify columns 
alter table APP_PAGE_DEFINITION add app_pi_uuid VARCHAR2(255 CHAR);
-- Add comments to the columns 
comment on column APP_PAGE_DEFINITION.app_pi_uuid
  is '归属产品集成UUID';

update APP_PAGE_DEFINITION t1
   set t1.app_pi_uuid =
       (select t2.uuid
          from app_product_integration t2
         where t1.uuid = t2.app_page_uuid
           and t2.app_page_reference = 0)
 where t1.app_pi_uuid is null;
 commit;






-- 更新模块归属到超级管理单位下
update app_module am
   set system_unit_id = 'S0000000000'
 where system_unit_id <> 'S0000000000'
   and exists (select 1
          from app_product_integration api, app_product ap
         where ap.system_unit_id = 'S0000000000'
           and api.app_product_uuid = ap.uuid
           and am.uuid = api.data_uuid
           and api.data_type = '2');
 commit;

-- 更新应用归属到超级管理单位下
update app_application am
   set system_unit_id = 'S0000000000'
 where system_unit_id <> 'S0000000000'
   and exists (select 1
          from app_product_integration api, app_product ap
         where ap.system_unit_id = 'S0000000000'
           and api.app_product_uuid = ap.uuid
           and am.uuid = api.data_uuid
           and api.data_type = '3');

 commit;