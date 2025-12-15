define([ "mui", "constant", "commons", "server" ], function($, constant, commons, server) {
	// 代理一笔表单，字段通过该对象获取所需要的信息
	var DyformScope = function(dyform, formDefinition, formFieldMap, formData) {
		this.dyform = dyform;
		this.formDefinition = formDefinition;
		this.formFieldMap = formFieldMap;
		this.formData = formData;
		this.isSubformScope = this.dyform.formDefinition.uuid !== formDefinition.uuid;
	}
	$.extend(DyformScope.prototype, {
		// 获取数据UUID
		getDataUuid : function() {
			return this.formData.uuid;
		},
		setDataUuid : function(dataUuid) {
			this.formData.uuid = dataUuid;
		},
		// 获取表单ID
		getFormId : function() {
			return this.formDefinition.id;
		},
		// 获取表单定义UUID
		getFormUuid : function() {
			return this.formDefinition.uuid;
		},
		// 获取表单解析对象
		getDyform : function() {
			return this.dyform;
		},
		// 获取表单数据
		getFormData : function() {
			return this.formData;
		},
		// 创建字段ID
		createFieldId : function(fieldDefinition) {
			if (fieldDefinition.uuid != null) {
				return this.getFieldIdForSubform(fieldDefinition.id);
			}
			return this.getFieldId(fieldDefinition.name);
		},
		// 获取普通字段ID
		getFieldId : function(fieldName) {
			return this.formData.uuid + "_" + fieldName;
		},
		// 获取从表字段ID
		getFieldIdForSubform : function(fieldName) {
			return this.formData.uuid + "_subform_" + fieldName;
		},
		// 根据字段名称返回字段对象
		getField : function(fieldName) {
			var _self = this;
			var fieldId = _self.getFieldId(fieldName);
			return _self.formFieldMap[fieldId];
		},
		// 根据应用于返回字段对象
		getFieldsByMappingName : function(mappingName) {
			// TODO
		},
		// 判断是否从表数据
		isSubform : function() {
			return this.isSubformScope;
		},
		triggerFormula : function(field) {
			var _self = this;
			var allformulas = _self.dyform.formulas;
			if (allformulas == null) {
				return formulas;
			}

			var fieldName = field.getName();
			if (_self.isSubform()) {
				fieldName = _self.getFormId() + ":" + fieldName;
			}

			var formulas = allformulas[fieldName];// 获取公式
			if (typeof formulas == "undefined") {
				return;
			}

			// 运行公式
			for (var i = 0; i < formulas.length; i++) {
				try {
					formulas[i].call(field);
				} catch (e) {
					console.error(e);
				}
			}
		},
		// 判断表单是否创建完成
		isDyformCreateComplete : function() {
			return this.dyform.dyformCreateComplete;
		},
		// 表单是否显示为文本
		isDisplayAsLabel : function() {
			return this.dyform.isDisplayAsLabel();
		}
	});
	return DyformScope;
});