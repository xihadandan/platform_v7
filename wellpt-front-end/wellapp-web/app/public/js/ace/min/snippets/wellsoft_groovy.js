ace.define("ace/snippets/wellsoft_groovy", ["require", "exports", "module"], function (require, exports, module) {
    "use strict";

    exports.snippetText = "# Prototype\n\
snippet getBean : 获取服务实例\n\
	def ${1:变量名} = ApplicationContextHolder.getBean(${2:java编译类字符串});\n\
snippet saveFile : 芒果服务保存文件\n\
	ApplicationContextHolder.getBean(MongoFileService.class).saveFile(${1:文件名} , ${2:文件输入流});\n\
snippet saveFile : 芒果服务保存文件\n\
	ApplicationContextHolder.getBean(MongoFileService.class).saveFile(${1:文件夹ID} , ${1:文件名} , ${2:文件输入流});\n\
snippet getFile : 芒果服务获取文件\n\
	def ${1:变量名}=ApplicationContextHolder.getBean(MongoFileService.class).getFile(${2:文件ID});\n\
snippet copyFile : 芒果服务拷贝文件\n\
	def ${1:新的文件ID}=ontextHolder.getBean(MongoFileService.class).copyFile(${2:源文件ID}).getFileID();\n\
snippet isWorkHour : 是否工作时间\n\
	def ${1:变量名}=ApplicationContextHolder.getBean(BasicDataApiFacade.class).isWorkHogetWorkPeriodur(${2:日期});\n\
snippet getDataDictionaries : 根据类型和编码获取字典列表\n\
	List ${1:变量名}=ApplicationContextHolder.getBean(BasicDataApiFacade.class).getDataDictionaries(${2:类型} , ${3:编码});\n\
snippet getWorkingHour : 获取工作日的信息\n\
	def ${1:变量名}=ApplicationContextHolder.getBean(BasicDataApiFacade.class).getWorkingHour(${2:日期});\n\
snippet getEffectiveWorkingHour : 获取当前或者最近工作日的信息\n\
	def ${1:变量名}=ApplicationContextHolder.getBean(BasicDataApiFacade.class).getEffectiveWorkingHour(${2:日期});\n\
snippet isWorkDay : 是否工作日\n\
	def ${1:变量名}=ApplicationContextHolder.getBean(BasicDataApiFacade.class).isWorkDay(${2:日期});\n\
snippet getWorkDate : 返回有效的工作日时间\n\
	def ${1:变量名}=ApplicationContextHolder.getBean(BasicDataApiFacade.class).getWorkDate(${2:起始日期} , ${3:间隔数} ,${4:间隔日期类型});\n\
snippet getByType : 根据类型返回字典\n\
	def ${1:变量名}=ApplicationContextHolder.getBean(DataDictionaryService.class).getByType(${2:字典类型});\n\
snippet executeSQL : 可执行SQL配置执行\n\
	def ${1:变量名}=ApplicationContextHolder.getBean(ExecSqlDefinitionFacadeService.class).execute(${2:SQL配置ID} , ${3:自定义Map参数});\n\
snippet getFormDataOfMainform : 获取主表单的数据\n\
	def ${1:变量名}=ApplicationContextHolder.getBean(DyFormFacade.class).getFormDataOfMainform(${2:表单UUID} , ${3:数据UUID});\n\
snippet getFormData : 获取主表单以及从表单的数据\n\
	def ${1:变量名}=ApplicationContextHolder.getBean(DyFormFacade.class).getFormData(${2:表单UUID} , ${3:数据UUID});\n\
snippet getFormDefinition : 根据表单UUID，获取表单的定义数据\n\
	def ${1:变量名}=ApplicationContextHolder.getBean(DyFormFacade.class).getFormDefinition(${2:表单UUID});\n\
snippet getFormDefinitionById : 根据表单ID，获取表单定义数据\n\
	def ${1:变量名}=ApplicationContextHolder.getBean(DyFormFacade.class).getFormDefinition(${2:表单ID});\n\
snippet queryFormDataExists : 指定的字段的值是否存在于指定的表中\n\
	boolean ${1:变量名}=ApplicationContextHolder.getBean(DyFormFacade.class).queryFormDataExists(${2:表名} , ${3:字段名} , ${4:字段值});\n\
snippet saveFormData : 保存表单数据\n\
	import com.wellsoft.pt.dyform.implement.combiner.dto.impl.DyFormDataImpl;\n\
	DyFormDataImpl formData=new DyFormDataImpl();\n\
	//formData.setDataUuid(\"\");//对数据进行更新时候设置数据UUID\n\
	formData.setFormUuid(\"${1:表单UUID}\");\n\
	formData.setFieldValue(\"${2:字段名}\",${3:字段值});\n\
	def ${4:变量名(返回数据UUID)}=ApplicationContextHolder.getBean(DyFormFacade.class).saveFormData(formData);\n\
snippet delFullFormData : 删除表单数据包括关联的从表单数据\n\
	ApplicationContextHolder.getBean(DyFormFacade.class).delFullFormData(${1:表单UUID} , ${2:数据UUID});\n\
snippet delFormData : 删除单条数据\n\
	ApplicationContextHolder.getBean(DyFormFacade.class).delFormData(${1:表名} , ${2:数据UUID});\n\
snippet delFullSubFormData : 删除主表单数据关联的所有从表数据\n\
	ApplicationContextHolder.getBean(DyFormFacade.class).delFormData(${1:主表单UUID} , ${2:主表单数据UUID});\n\
snippet startBot : 单据转换\n\
	import com.wellsoft.pt.bot.support.BotResult;\n\
	import com.wellsoft.pt.bot.support.BotParam;\n\
	BotParam param=new BotParam(\n\
	    \"${1:单据转换配置ID}\" ,\n\
	     Sets.newHashSet(\n\
	        new BotParam.BotFromParam(\"${2:数据UUID}\",\"${3表单ID}\")\n\
	        //TODO: , new BotParam(..)\n\
	     )\n\
	);\n\
	def ${4:变量名}=ApplicationContextHolder.getBean(BotFacadeService.class).startBot(param);\n\
snippet isGranted : 当前用户是否有相关权限\n\
	boolean ${1:变量名}=ApplicationContextHolder.getBean(SecurityApiFacade.class).isGranted(${2: 权限编码});\n\
snippet hasRole : 指定用户是否有相关角色\n\
	boolean ${1:变量名}=ApplicationContextHolder.getBean(SecurityApiFacade.class).hasRole(${2: 用户ID} , ${3:角色编码});\n\
snippet getDataDictionaryJsonKvByType : 根据字典类型获取字典的key-value的字符串\n\
	ApplicationContextHolder.getBean(DataDictionaryService.class).getDataDictionaryJsonKvByType(${1:字典类型});\n\
	";
    exports.scope = "wellsoft_groovy";

});
(function () {
    ace.require(["ace/snippets/wellsoft_groovy"], function (m) {
        if (typeof module == "object" && typeof exports == "object" && module) {
            module.exports = m;
        }
    });
})();
