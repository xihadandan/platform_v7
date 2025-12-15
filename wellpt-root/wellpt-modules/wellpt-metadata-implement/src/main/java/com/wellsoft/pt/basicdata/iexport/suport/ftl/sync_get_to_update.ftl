select distinct * from(
<#list syncInfos as syncInfo>
    select t1.uuid as data_uuid, t2.data_type as data_type
    from ${syncInfo.tableName} t1, cd_data_dependency t2
    where t1.uuid = t2.data_uuid and t2.data_type = '${syncInfo.dataType}'
    and t1.rec_ver <> t2.data_rec_ver
    <#if syncInfo_has_next>union all</#if>
</#list>
)