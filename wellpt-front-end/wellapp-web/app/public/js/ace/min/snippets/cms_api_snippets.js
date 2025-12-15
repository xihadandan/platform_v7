/**
 * cms构建应用可以使用到的api相关snippet
 */

ace.define("ace/snippets/cms_api_snippets", ["require", "exports", "module"], function (require, exports, module) {
    "use strict";

    exports.snippetText = "# \n\
# 表格组件api \n\
snippet Api.getPrimaryColumnName : 获取表格定义的主键字段名\n\
	Api.getPrimaryColumnName(${1})\n\
snippet Api.getSelectedData : 获取表格的选中行数据\n\
	Api.getSelectedData()\n\
snippet Api.getData : 获取指定下标的行数据，不填下标则返回当前页数据\n\
	Api.getData(${1:下标数})\n\
snippet Api.getSelectedIds : 获取表格的选中主键id值集合\n\
	Api.getSelectedIds()\n\
snippet Api.existOneSelRowFieldInValues : 判断是否存在选中的一行的某个字段值在指定值范围内\n\
	Api.existOneSelRowFieldInValues(${1:字段名} , ${2:[值数组]})\n\
snippet Api.isAllSelRowFieldInValues : 判断是否所有选中行的某个字段值在指定值范围内\n\
	Api.isAllSelRowFieldInValues(${1:字段名} , ${2:[值数组]})\n\
snippet Api.refresh : 刷新表格数据\n\
	Api.refresh()\n\
snippet Api.hideColumns : 隐藏表字段列\n\
	Api.hideColumns(${1:[字段名数组]})\n\
snippet Api.showColumns : 展示表字段列\n\
	Api.showColumns(${1:[字段名数组]})\n\
snippet Api.toggleButtons : 按钮状态切换\n\
	Api.toggleButtons(${1:[按钮编码数组]} , ${2:状态值:show/hide/remove})\n\
snippet Api.hideButtons : 隐藏按钮\n\
	Api.hideButtons(${1:[按钮编码数组])\n\
snippet Api.showButtons : 显示按钮\n\
	Api.showButtons(${1:[按钮编码数组]})\n\
snippet Api.removeButtons : 移除按钮\n\
	Api.removeButtons(${1:[按钮编码数组]})\n\
snippet Api.openDocumentWindow : 另开窗口模式打开单据\n\
	Api.openDocumentWindow(${1:下标数(选填，如果不填默认打开点击行的)})\n\
snippet Api.openDocumentDialog : 弹窗模式打开单据\n\
	Api.openDocumentDialog(${1:下标数(选填，如果不填默认打开点击行的)})\n\
snippet Api.openDocument : 打开单据\n\
	Api.openDocument(${1:窗口类型 _blank/_dialog} , ${2:是否编辑模式 true/false} , ${3:下标数(选填，如果不填默认打开点击行的)})\n\
snippet Api.setNoRecordTip : 设置无记录的提示信息\n\
	Api.setNoRecordTip(${1:提示信息})\n\
snippet Api.addOtherConditions : 添加额外的查询条件\n\
	Api.addOtherConditions(${1:[条件数组]})\n\
snippet Api.clearOtherConditions : 清除额外的查询条件\n\
	Api.clearOtherConditions(${1:[条件数组，选填。不填则清除全部额外条件]})\n\
snippet Api.statusMarkRow : 状态标记\n\
	Api.statusMarkRow(${1:[主键值数组]} , ${2:标记方式} , ${3:回调函数})\n\
# 公用服务API\n\
snippet Api.executeSQL : 执行SQL配置\n\
	Api.executeSQL(${1:SQL配置ID} , ${2:参数} , ${3:回调函数} , ${4:是否异步执行 true/false})\n\
snippet Api.startBot : 单据转换\n\
	Api.startBot(${1:单据转换配置ID} , ${2:参数} , ${3:回调函数} , ${4:是否异步执行 true/false})\n\
snippet Api.sendMessage : 发送消息\n\
	Api.sendMessage(${1:参数} , ${2:回调函数} , ${3:是否异步执行 true/false})\n\
# 左导航API\n\
snippet LeftSideBarRefresh : 刷新左导航\n\
	appContext.getPageContainer().trigger(constant.WIDGET_EVENT.LeftSideBarRefresh)\n\
snippet LeftSideBarRefreshDynamicItem : 刷新动态左导航\n\
	appContext.getPageContainer().trigger(constant.WIDGET_EVENT.LeftSideBarRefreshDynamicItem)\n\
snippet BadgeRefresh : 刷新左导航徽章数量\n\
	appContext.getPageContainer().trigger(constant.WIDGET_EVENT.BadgeRefresh);\n\
# 公用工具包API\n\
snippet commons.StringUtils.isBlank : 字符串判断是否为空，返回true或者false\n\
	commons.StringUtils.isBlank(${1:字符串})\n\
snippet commons.StringUtils.defaultIfBlank : 字符串如果为空，则默认一个值\n\
	commons.StringUtils.defaultIfBlank(${1:字符串} , ${2:默认值})\n\
snippet commons.StringUtils.contains : 是否包含某个个字符串\n\
	commons.StringUtils.contains(${1:搜索字符串} , ${2:包含该值} , ${3:起始下标值})\n\
snippet commons.StringUtils.capitalise : 首字母大写\n\
	commons.StringUtils.capitalise(${1:字符串})\n\
snippet commons.StringUtils.uncapitalise : 首字母小写\n\
	commons.StringUtils.uncapitalise(${1:字符串})\n\
snippet commons.StringUtils.trim : 移除字符串首尾处的空格\n\
	commons.StringUtils.trim(${1:字符串})\n\
snippet commons.StringUtils.ltrim : 移除字符串左侧的空格\n\
	commons.StringUtils.ltrim(${1:字符串})\n\
snippet commons.StringUtils.rtrim : 移除字符串右侧的空格\n\
	commons.StringUtils.rtrim(${1:字符串})\n\
snippet commons.StringUtils.isMobile : 验证是否是手机号码\n\
	commons.StringUtils.isMobile(${1:字符串})\n\
snippet commons.StringUtils.isEmail : 验证是否电子邮件地址\n\
	commons.StringUtils.isEmail(${1:字符串})\n\
snippet commons.DateUtils.format : 日期格式化为字符串\n\
	commons.DateUtils.format(${1:日期},${2:格式字符串})\n\
snippet commons.UUID.createUUID : 生成UUID值\n\
	commons.UUID.createUUID()\n\
snippet commons.Browser.getQueryString : 获取浏览器地址后的参数值\n\
	commons.Browser.getQueryString(${1:参数名},${2:默认值(可选)})\n\
# 服务工具包API\n\
snippet server.SpringSecurityUtils.getUserDetails : 获取用户详情\n\
	server.SpringSecurityUtils.getUserDetails(${1:用户ID(可选，不填则返回当前登录用户)})\n\
snippet server.SecurityUtils.hasRole : 判断用户是否有指定角色\n\
	server.SecurityUtils.hasRole(${1:角色ID})\n\
snippet server.SecurityUtils.getCurrentUserId : 获取当前登录用户的ID\n\
	server.SecurityUtils.getCurrentUserId()\n\
snippet JDS.call : JDS服务调用\n\
	server.JDS.call({\n\
	    helper : '${1:服务地址}',\n\
	    data : [${2:调用参数}],\n\
	    success:function(response){\n\
	        //TODO:成功响应处理\n\
	    },\n\
	    error:function(jqXHR, statusText, error){\n\
	        //TODO:异常响应处理\n\
	    },\n\
	    async:${3:是否异步 true/false}\n\
	)}\n\
snippet downloadMongoFile : 下载芒果存储的文件\n\
	FileDownloadUtils.downloadMongoFile({\n\
	    folderId:${1:文件夹ID},\n\
	    purpose:${2:用途},\n\
	    fileId:${3:文件ID(多个文件使用数组)},\n\
	    batchCompression:${4:多个文件是否压缩 true/false}\n\
	})\n\
# 弹窗API\n\
snippet appModal.info : 弹窗提示消息 \n\
	appModal.info(${1:消息字符串} , ${2:回调函数(可选)})\n\
snippet appModal.warning : 弹窗警告消息 \n\
	appModal.warning(${1:消息字符串} , ${2:回调函数(可选)}) \n\
snippet appModal.error : 弹窗错误消息 \n\
	appModal.error(${1:消息字符串} , ${2:回调函数(可选)}) \n\
snippet appModal.success : 弹窗成功消息 \n\
	appModal.success(${1:消息字符串} , ${2:回调函数(可选)})\n\
# 流程API\n\
snippet Api.openWorkflowDocument : 打开流程单据\n\
	Api.openWorkflowDocument(${1:自定义url参数(可选)} , ${2:行下标(可选)})\n\
	";


    exports.scope = "cms_api_snippets";


});
(function () {
    ace.require(["ace/snippets/cms_api_snippets"], function (m) {
        if (typeof module == "object" && typeof exports == "object" && module) {
            module.exports = m;
        }
    });
})();
