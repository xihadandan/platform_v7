select distinct * from(
<#list syncInfos as syncInfo>
    select uuid as data_uuid, rec_ver as data_rec_ver, '${syncInfo.dataType}' as data_type
    from ${syncInfo.tableName}
    where uuid not in (select data_uuid
    from cd_data_dependency
    where data_type = '${syncInfo.dataType}')
    <#if syncInfo_has_next>union all</#if>
</#list>
)
