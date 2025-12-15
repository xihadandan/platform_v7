delete from ${tableName} t
where
<#if pkNames?? && (pkNames?size > 0)>
    <#list pkNames as pkName>
        t.${pkName} = :${pkName}<#if pkName_has_next> and </#if>
    </#list>
<#else>
    1 = 2
</#if>