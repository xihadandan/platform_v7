define([ "mui", "constant", "commons", "server", "mui-DyformField", "mui-DyformCommons" ], function($, constant,
		commons, server, DyformField, dyformCommons) {
	var StringUtils = commons.StringUtils;
	var JSON = commons.JSON;
	// 可选项值控件基类
	var DyformFieldOptionBase = function($placeHolder, options) {
		DyformField.apply(this, arguments);
	};
	commons.inherit(DyformFieldOptionBase, DyformField, {
		// 返回初始值，真实值转valueMap
		getInitValue : function() {
			var _self = this;
			var initValue = _self._superApply(arguments);
			return initValue;
		},
		// 设置，传入真实值时，转化为valueMap对象
		setValue : function(value) {
			var _self = this;
			_self._superApply(arguments);
		},
		/**
		 * 设置选项
		 */
		setOptionSet:function(optionSet) {
			var self = this;
			var options = self.options.fieldDefinition;
			for(var key in optionSet){
				var value = optionSet[key];
				if(typeof key != "string" || typeof value != "string"){
					console.error(self.getName() + ":非法的自定义选项(选项须为字符串)--typeof key == " + typeof key + " ----typeof value == " +  typeof value);
					return false;
				}
			}
			options.optionSet = optionSet;
			options.optionDataSource = dyDataSourceType.custom;//用户自定义 
			self.dataProvider = dyformCommons.getOptionData(self);
			return self.dataProvider;
		},
		hideOptions : function(values){
			var self = this;
			var options = self.options.fieldDefinition;
			self.hiddenValues = self.hiddenValues || {};
			$.each(values, function(idx, value){
				self.hiddenValues[value] = true;
			});
			options.optionDataSource = dyDataSourceType.custom;//用户自定义 
			self.dataProvider = dyformCommons.getOptionData(self);
		},
		hideOption:function(value) {
			var self = this;
			var values = [];
			values.push(value);
			self.hideOptions(values);
		},
		showOptions : function(values){
			var self = this;
			var options = self.options.fieldDefinition;
			self.hiddenValues = self.hiddenValues || {};
			$.each(values, function(idx, value){
				delete self.hiddenValues[value];
			});
			options.optionDataSource = dyDataSourceType.custom;//用户自定义 
			self.dataProvider = dyformCommons.getOptionData(self);
		},
		showOption:function(value) {
			var self = this;
			var values = [];
			values.push(value);
			self.showOptions(values);
		},
		 /**
		  * 重新渲染
		  */
		 reRender:function() {
			 var self = this;
			 self.renderEditableElem(self.$editableElem);
			 self.renderLabelElem(self.$labelElem);
		 }
	});
	return DyformFieldOptionBase;
});