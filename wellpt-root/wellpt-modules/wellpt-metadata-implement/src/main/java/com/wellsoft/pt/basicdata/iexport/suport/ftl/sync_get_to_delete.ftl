select distinct * from(
<#list syncInfos as syncInfo>
    select t.uuid as uuid, t.data_type as data_type
    from cd_data_dependency t
    where t.data_type = '${syncInfo.dataType}'
    and t.data_uuid not in (select uuid from ${syncInfo.tableName})
    <#if syncInfo_has_next>union all</#if>
</#list>
)