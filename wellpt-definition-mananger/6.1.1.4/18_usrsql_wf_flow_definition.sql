update wf_flow_definition d  
set d.category =   (
    select c.uuid from wf_def_category c, (
           select max(modify_time) as mtime from wf_def_category group by  code
    ) v where c.code = d.category and c.modify_time =v.mtime
    
) where d.category is not null and exists (
  select 1 from wf_def_category a where a.code = d.category
)
