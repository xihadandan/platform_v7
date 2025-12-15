/***
* 如何描述该JS
*
* @author ${author}
* @date ${createDate}
*/
var View = View || {};
View.${tableName} = View.${tableName} || {};
${r'$'}(function() {
// 保存服务
var save${entity}Service = "${entityLowFisrt}ViewMaintain.saveBean";
// 获取服务
var get${entity}Service = "${entityLowFisrt}ViewMaintain.getBean";
// 删除服务
var delete${entity}Service = "${entityLowFisrt}ViewMaintain.deleteAll";
// 表单选择器
var form_selector = "#form_${tableName}";
// 弹出框选择器
var dlg_selector = "#dlg_${tableName}";
// 视图操作元素
var ${r'$'}element = null;
// ${entity}的VO类
var bean = {
uuid : null, // UUID
recVer : null, // 版本号
<#list propertyWithoutDefaults as property>
    ${property.name} : null<#if property_has_next>,</#if> // ${property.remark}
</#list>
};

// 获取选择的行数据UUID
function getSelectedRowIds(${r'$'}element, rowId, multiple) {
var rowIds = [];
if (isTopBtn(${r'$'}element)) {
${r'$'}("input[class=checkeds]:checked").each(function() {
rowIds.push(${r'$'}(this).val());
});
if (rowIds.length == 0) {
oAlert2("请选择要编辑的${entity}！");
return;
}
} else {
rowIds.push(rowId);
}
if (multiple === false && rowIds.length > 1) {
oAlert2("不支持批量编辑！");
return;
}
return rowIds;
}

/** ******************************** 新增开始 ******************************** */
// 新增${entity}
View.${tableName}.new${entity} = function() {
${r'$'}element = ${r'$'}(this);
${r'$'}.get(ctx + "/${moduleRequestPath!}${classRequestMappingPath}/new", function(data) {
showNew${entity}Dialog(data, {});
});
};
// 显示新增${entity}弹出框
function showNew${entity}Dialog(data, option) {
// 创建DIV元素
${r'$'}.common.html.createDiv(dlg_selector);

// 设置弹出框内容
${r'$'}(dlg_selector).html(data);

// 初始化下一流程选择框
var options = {
title : "新增${entity}",
autoOpen : true,
width : 420,
height : 430,
resizable : false,
modal : true,
open : function() {
// 弹出框显示后处理
},
buttons : {
"确定" : function(e) {
// 保存${entity}
save${entity}();
}
},
close : function() {
${r'$'}(dlg_selector).html("");
}
};
options = ${r'$'}.extend(true, options, option);

// 显示弹出框
${r'$'}(dlg_selector).oDialog(options);
}
// 保存${entity}
function save${entity}() {
// 清空bean对象的属性
$.common.json.clearJson(bean);
// 收集表单数据到bean
${r'$'}(form_selector).form2json(bean);
JDS.call({
service : save${entity}Service,
data : [ bean ],
success : function(result) {
oAlert("保存成功！", function() {
${r'$'}(dlg_selector).oDialog("close");
// 刷新视图列表
refreshWindow(${r'$'}element);
});
}
});
}
/** ******************************** 新增结束 ******************************** */

/** ******************************** 编辑开始 ******************************** */
// 新增${entity}
View.${tableName}.edit${entity} = function(uuid) {
${r'$'}element = ${r'$'}(this);
var uuids = getSelectedRowIds(${r'$'}element, uuid, false);
if (!uuids) {
return;
}
${r'$'}.get(ctx + "/${moduleRequestPath!}${classRequestMappingPath}/edit", function(data) {
showEdit${entity}Dialog(data, {}, uuids[0]);
});
};
// 显示新增${entity}弹出框
function showEdit${entity}Dialog(data, option, uuid) {
// 创建DIV元素
${r'$'}.common.html.createDiv(dlg_selector);

// 设置弹出框内容
${r'$'}(dlg_selector).html(data);

// 初始化下一流程选择框
var options = {
title : "编辑${entity}",
autoOpen : true,
width : 420,
height : 430,
resizable : false,
modal : true,
open : function() {
// 弹出框显示后处理
get${entity}(uuid);
},
buttons : {
"确定" : function(e) {
// 保存${entity}
save${entity}();
}
},
close : function() {
${r'$'}(dlg_selector).html("");
}
};
options = ${r'$'}.extend(true, options, option);

// 显示弹出框
${r'$'}(dlg_selector).oDialog(options);
}
// 通过UUID获取${entity}信息
function get${entity}(uuid) {
JDS.call({
service : get${entity}Service,
data : [ uuid ],
success : function(result) {
bean = result.data;
// bean数据填充到表单
${r'$'}(form_selector).json2form(bean);
}
});
}
/** ******************************** 编辑结束 ******************************** */

/** ******************************** 删除开始 ******************************** */
// 删除
View.${tableName}.deleteAll = function(uuid) {
${r'$'}element = ${r'$'}(this);
var uuids = getSelectedRowIds(${r'$'}element, uuid);
if (!uuids) {
return;
}
// 删除处理
JDS.call({
service : delete${entity}Service,
data : [ uuids ],
success : function(result) {
oAlert("删除成功！", function() {
// 刷新视图列表
refreshWindow(${r'$'}element);
});
}
});
};
/** ******************************** 删除结束 ******************************** */

});
