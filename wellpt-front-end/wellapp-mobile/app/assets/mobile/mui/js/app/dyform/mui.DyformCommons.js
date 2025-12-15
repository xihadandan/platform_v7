define([ "mui", "constant", "commons", "server", "mui-DyformConstant" ], function($, constant, commons, server,
		DyformConstant) {
	// 表单公共方法
	var exports = {};
	var UUID = commons.UUID;
	var StringUtils = commons.StringUtils;

	// 1、方法执行前自动调用
	exports.proxiedBeforeInvoke = function(method, beforeMethod) {
		return (function(object) {
			var _method = object[method];
			var _proxied = function() {
				object[beforeMethod].apply(object, arguments);
				var retVal = _method.apply(object, arguments);
				return retVal;
			};
			object[method] = _proxied;
		})(this);
	}

	// 2、方法执行后自动调用
	exports.proxiedAfterInvoke = function(method, afterMethod, async) {
		return (function(object) {
			var _method = object[method];
			var _proxied = null;
			if(async === true) {
				_proxied = function(){
					var args = arguments;
					var retVal = _method.apply(object, args);
					object._proxiedLocked && clearTimeout(object._proxiedLocked);
					object._proxiedLocked = setTimeout(function(){
						object[afterMethod].apply(object, args);
						object._proxiedLocked = null;
					}, 0);
					return retVal;
				}
			}else {
				_proxied = function() {
					var retVal = _method.apply(object, arguments);
					if (object._proxiedLocked != true) {
						object._proxiedLocked = true;
						object[afterMethod].apply(object, arguments);
						object._proxiedLocked = false;
					}
					return retVal;
				};
			}
			object[method] = _proxied;
		})(this);
	}

	// 3、添加样式
	exports.addClass = function($element, cls) {
		$.each($element, function() {
			this.classList.add(cls);
		});
	}

	// 4、删除样式
	exports.removeClass = function($element, cls) {
		$.each($element, function() {
			this.classList.remove(cls);
		});
	}

	// 5、创建 DOM
	exports.dom = $.dom = function(str) {
		if (typeof (str) !== 'string') {
			if ((str instanceof Array) || (str[0] && str.length)) {
				return [].slice.call(str);
			} else {
				return [ str ];
			}
		}
		if (!$.__create_dom_div__) {
			$.__create_dom_div__ = document.createElement('div');
		}
		$.__create_dom_div__.innerHTML = str;
		return [].slice.call($.__create_dom_div__.childNodes);
	};

	// 6、获取要保存的数据，统一转成字符串
	exports.getFieldValueForSave = function(field) {
		var fieldValue = field.getValue();
		if (field.isValueMap() && $.isArray(fieldValue)) {
			return fieldValue.join(";");
		}
		return fieldValue;
	}

	// 7、获取真实值
	exports.getRealValue = function(value) {
		if (value == null || (typeof value === "string" && !value.trim())) {
			return "";
		}
		var valueObj = value;
		if ($.isPlainObject(valueObj) == false) {
			valueObj = commons.JSON.parse(valueObj);// $.parseJSON(valueObj);
		}
		var displayValue = [];
		for ( var i in valueObj) {
			displayValue.push(i);
		}
		return displayValue.join(";");
	};

	// 8、获取显示值
	exports.getDisplayValue = function(value) {
		if (value == null || (typeof value === "string" && !value.trim())) {
			return "";
		}
		var valueObj = value;
		if ($.isPlainObject(valueObj) == false) {
			valueObj = commons.JSON.parse(valueObj);// $.parseJSON(valueObj);
		}
		var displayValue = [];
		for ( var i in valueObj) {
			displayValue.push(valueObj[i]);
		}
		return displayValue.join(";");
	};
	exports.getDisplayValue2 = function(value, optionSet) {
		var displayValue = [];
		value = $.isArray(value) ? value : (typeof value === "string" ? value.split(";") : [value]);
		for(var i =0;i<value.length;i++){
			for(var j=0;j<optionSet.length;j++){
				if(optionSet[j].value === value[i]){
					displayValue.push(optionSet[j].text);
				}
			}
		}
		return displayValue.join(";");
	};

	// 9、根据真实值、可选项数据，获取radio、checkbox、select的可选数据的ValueMap对象
	exports.getValueMap = function(realValue, dataArray) {
		var valueMap = {};
		if (StringUtils.isBlank(realValue)) {
			return valueMap;
		}
		var dataMap = {};
		if (dataArray != null) {
			$.each(dataArray, function(i, data) {
				dataMap[data.value] = data;
			});
		}

		var realValues = realValue.split(";");
		for (var i = 0; i < realValues.length; i++) {
			var value = realValues[i];
			if (dataMap[value] != null) {
				valueMap[value] = dataMap[value].text;
			} else if(value === "空"){
				// 正常
			} else {
				console.error("real value [" + realValue + "] is not found");
			}
		}
		return valueMap;
	}

	// 10、获取radio、checkbox、select的可选数据
	// 批次ID
	var batchId = 0;
	exports.getOptionData = function(field) {
		var dataArray = [];
		var options = field.options.fieldDefinition;
		var optionSet = getOptionSet(options);
		// 批次ID
		var groupId = batchId;
		batchId++;
		// 数组集合
		var hiddenValues = field.hiddenValues || {};
		if ($.isArray(optionSet)) {
			$.each(optionSet, function() {
				var self = this;
				var item = {
					id : UUID.createUUID(),
					groupId : groupId,
					value : self.value || self.code,
					text : self.name,
					name : field.getName()
				};
				if(hiddenValues[item.value] === true){
					return;//continue;
				}
				dataArray.push(item);
			});
		} else {
			// 对象集合
			for ( var p in optionSet) {
				var item = {
					id : UUID.createUUID(),
					groupId : groupId,
					value : p,
					text : optionSet[p],
					name : field.getName()
				};
				if(hiddenValues[item.value] === true){
					continue;//return;
				}
				dataArray.push(item);
			}
		}
		return dataArray;
	}
	function getOptionSet(options) {
		var optionSet = {};
		var selectobj = options.optionSet;
		if (selectobj == null || (typeof selectobj == "string" && !selectobj.trim())) {
			console.error("a json parameter is null , used to initialize checkbox options ");
			return;
		}
		if (options.optionDataSource == DyformConstant.dyDataSourceType.dataDictionary) {// 来自字典,这时optionSet为数组
			if (selectobj.length == 0) {
				return;
			} else {
				for (var j = 0; j < selectobj.length; j++) {
					var obj = selectobj[j];
					if (obj.hasOwnProperty("value")) {
						optionSet[selectobj[j].value] = selectobj[j].name;
					} else {
						optionSet[selectobj[j].code] = selectobj[j].name;
					}
				}
			}
		} else {
			optionSet = selectobj;
		}
		if (typeof optionSet === "string") {
			try {
				optionSet = $.parseJSON(optionSet);
			} catch (e) {
				console.error(optionSet + " -->not json format ");
				return;
			}
		}
		return optionSet;
	}

	// 10、打开表单信息
	exports.print = function(dyform) {
		var fields = dyform.getFields();
		$.each(fields, function() {
			var field = this;
			console.log(field);
			console.log("fieldId: " + field.getId() + ", fieldName: " + field.getName());
			console.log("isValueMap: " + field.isValueMap());
			console.log("fieldValue: ");
			console.log(field.getValue());
			console.log("fieldValueType: ");
			console.log(typeof field.getValue());
		});
	},
	/**
	 * 根据指定的dataUuid从指定的formUuid表单中获取表单数据
	 * */
	exports.loadFormDefinitionData = function(formUuid, dataUuid){
		if(!formUuid){//未初始化
			throw new Error("formUuid or dataUuid is not initialized");
		}
		var formDatas  = null;
		$.ajax({
			url : ctx + "/pt/dyform/data/getFormDefinitionData",
			cache : false,
			async : false,//同步完成
			type : "POST",
			data : {formUuid: formUuid, dataUuid: dataUuid} ,
			dataType : "json",
			success : function(result) { 
				if(result.success == "true" || result.success == true){ 
					formDatas =  result.data;  
	  		   	}else{
	  		   		$.alert("数据获取失败");
	  		   	}
			}, 
			error: function(re){//加载定义失败
				 $.alert("数据获取失败"); 
			}
		});
		return formDatas;
	};
	/**
	 * 根据指定的的formUuid表单中获取表单定义
	 * */
	exports.loadFormDefinition = function(uuid) {
		if(!uuid){//未初始化
			throw new Error("formUuid is not initialized");
		}		
		var definitionObj = null;
		var time1 = (new Date()).getTime();
		$.ajax({
			url : ctx + "/pt/dyform/definition/getFormDefinition",
			cache : false,
			async : false,// 同步完成
			type : "POST",
			data : {
				formUuid : uuid
			},
			dataType : "json",
			success : function(data) {
				definitionObj = data;
			},
			error : function() {// 加载定义失败
			}
		});
		var time2 = (new Date()).getTime();
		console.log("加载定义所用时间:" + (time2 - time1) / 1000.0 + "s");
		return definitionObj;
	};
	return exports;
});