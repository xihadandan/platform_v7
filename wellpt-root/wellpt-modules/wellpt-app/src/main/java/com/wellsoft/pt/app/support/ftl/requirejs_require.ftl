require([
<#list modules as module>
    "${module.exports}"<#if module_has_next>,</#if>
</#list>
], function() {
${callbackScript}
});