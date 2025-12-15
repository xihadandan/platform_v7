select count(*) from ${tableName} t
where
<#list pkNames as pkName>
    t.${pkName} = :${pkName}<#if pkName_has_next> and </#if>
</#list>