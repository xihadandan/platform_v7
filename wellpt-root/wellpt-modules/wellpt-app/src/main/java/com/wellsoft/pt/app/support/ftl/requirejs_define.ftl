define([
<#list modules as module>
    "${module.id}"<#if module_has_next>,</#if>
</#list>
], function() {
${callbackScript}
});