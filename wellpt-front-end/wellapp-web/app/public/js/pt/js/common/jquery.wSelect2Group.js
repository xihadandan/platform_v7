/**
 */
(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'jquery' ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($) {
	"use strict";
	$.fn.wSelect2Group = function(params) {
		if (typeof (arguments[0]) === "string") {
			$(this).select2(params);
			return $(this);
		}
		// 默认参数
		var defaultOptions = {
			queryMethod : '',// 查询下拉数据时的方法，配置该参数，则对应service无需继承Select2QueryApi
			selectionMethod : '',// 根据数据IDS返回Text，配置该参数，则对应service无需继承Select2QueryApi
			params : {},// ajax向后台透传的参数
			pageSize : 20, // 当remoteSeach = true 时生效
			separator : ';',// 多选时的分割符号
			defaultBlank : true,// 默认空选项
			defaultBlankText : "",// 默认空选项的显示值
			remoteSearch : false,// 是否远程查询.远程查询时，每次打开和查询都会带条件到后台查询，非远程查询一次将所有数据拉取到前端赋值给option.data，不在访问后端。
			valueField : null,// 下拉框值回填input的ID
			labelField : null,
			theme : "classic",
			isGroup: true,
			multiple : true, // 是否多选
		// 下拉框展示值回填input的ID
		};

		// 合并参数
		var options = $.extend(defaultOptions, params);

		if (options.serviceName) {
			$.ajax({
				type : "POST",
				url : "/common/select2/group/query",
				dataType : "json",
				async : false,
				data : {
					serviceName : options.serviceName,
					queryMethod : options.queryMethod,
					pageSize : options.remoteSearch ? options.pageSize : 1000,
					pageNo : 1,
					params : options.params
				},
				success : function(res) {
					options.data = res.results;
					console.log(res.results)
				}
			});
		}

		// 覆盖select2的原生方法，参考jquery.wselect.js, 用来判断选中值
		options.initSelection = function(element, callback) {
			var ids = element.val();
			if (options.valueField) {
				ids = $("#" + options.valueField).val();
			}
			var selectData = [];
			if (ids) {
				if (options.multiple) {
					ids = ids.split(options.separator);
				} else {
					ids = [ ids ];
				}
				if (options.data) {
					for (var j = 0; j < ids.length; j++) {
						var id = ids[j];
						for (var i = 0; i < options.data.length; i++) {
							for (var k = 0; k < options.data[i].children.length; k++) {
								if (options.data[i].children[k].id == id) {
									var text = options.data[i].children[k].text;
									selectData.push({
										id : id,
										text : text
									});
									break;
								}
							}
						}
					}
				}
			}
			if (options.multiple) { // 多选是返回数组格式
				callback(selectData);
			}else{
				if( selectData.length > 0 ){
					callback(selectData[0]);	
				}
			}
		};

		$(this).select2(options);

		$(this).on("change", function() {
			// debugger;
			var selectedData = $(this).select2("data");
			if (options.multiple) { // 多选是返回数组格式
				var values = [];
				var labels = [];
				for (var i = 0; i < selectedData.length; i++) {
					values.push(selectedData[i].id);
					labels.push(selectedData[i].text);
				}
				if (options.labelField) {
					$("#" + options.labelField).val(labels.join(options.separator));
				}
				$("#" + options.valueField).val(values.join(options.separator));
			} else { // 单选是返回object
				if (options.labelField) {
					$("#" + options.labelField).val(selectedData.text);
				}
				$("#" + options.valueField).val(selectedData.id);
			}
		});

		return $(this);
	};
}));