var requirejsPaths={
<#list modules as module>
    "${module.exports}" : "${ctx!}${module.confusePath}"<#if module_has_next>,</#if>
</#list>
};


// ie浏览器的脚本兼容性问题
var _ua = navigator.userAgent.toLowerCase();
var _isIE = _ua.indexOf("msie") > -1;
var _safariVersion;
//ie8以下的脚本兼容性:
//1.loadash 脚本兼容性
if(requirejsPaths.lodash){
if (_isIE) {
_safariVersion = _ua.match(/msie ([\d.]+)/)[1];
}
if (_safariVersion <= 8.0) {
requirejsPaths.lodash='${ctx!}/resources/lodash/3.10.1/lodash.custom.min';
}
}

var WebApp = window.WebApp || {};

requirejs.onError = function(error) {
// alert("JavaScript module loaded failure: " + error.message);// 不提示前端
if(window.console && console.log){
console.log(error);
}
var errorRequireModules = WebApp.errorRequireModules || [];
WebApp.errorRequireModules = errorRequireModules.concat(error.requireModules);
throw error;
}

requirejs.config({
baseUrl : "${ctx!}/resources",
urlArgs : 'v=${version}&t=${jsTimestamp}',
waitSeconds: 0,
paths : requirejsPaths,
shim : {
<#list modules as module>
    "${module.exports}" : {
    deps : [<#list module.dependencies as dependency>"${dependency}"<#if dependency_has_next>,</#if></#list>],
    exports : "${module.exports}"
    }<#if module_has_next>,</#if>
</#list>
},
map: {
'*': {
'css': 'requirejs/css.min'
}
}
});

WebApp.configJsModules = WebApp.configJsModules || {};
WebApp.errorRequireModules = WebApp.errorRequireModules || [];
<#list modules as module>WebApp.configJsModules["${module.exports}"]="${module.exports}";</#list>

