define([ "constant", "commons", "appContext", "appModal", "DyformExplain", "DyformFunction", "DyformDevelopment" ],
		function(constant, commons, appContext, appModal, DyformExplain, DyformFunction, DyformDevelopment) {
			var time1 = (new Date()).getTime();
			var dataUuid = $("#dataUuid").val();
			var formuuid = $("#formUuid").val();
			var dyformData = DyformFunction.loadFormDefinitionData(formuuid, dataUuid);
			var time2 = (new Date()).getTime();
			console.log("loadFormDefinitionData:" + (time2 - time1) / 1000.0 + "s");
			if (typeof dyformData == "string") {
				dyformData = (eval("(" + dyformData + ")"));
			}
			var titleElem = document.getElementsByTagName('title').item(0);
			// "(解析)";
			var time3 = (new Date()).getTime();
			console.log("获取数据:" + (time3 - time1) / 1000.0 + "s");

			console.log(DyformExplain, DyformFunction);
			var isEdit = $("#isEdit").val() == "true" ? true : false;
            var isSubForm = $("#isSubForm").val() == "true" ? true : false;
            if(parent.editableSubFormData && parent.editableSubFormData[dataUuid]){
            	var mainFormDatas = parent.editableSubFormData[dataUuid], formDatas;
            	$.extend(dyformData.formDatas[formuuid][0], mainFormDatas);
            	if(formDatas = (mainFormDatas.nestformDatas && JSON.parse(mainFormDatas.nestformDatas).formDatas)) {
            		$.extend(dyformData.formDatas, formDatas);
            		delete dyformData.formDatas[formuuid][0].nestformDatas;
            	}
            }
			var dyformExplain = new DyformExplain("#dyform", {
				renderTo : "#dyform",
				formData : dyformData,
				displayAsLabel : !isEdit,// 显示为文本
				async : false,// false为同步,true为异步,默认为false
				optional : {
					isFirst : true
				},
				displayAsFormModel : false,
				success : function() {
					top.appModal.hideMask();
                    appModal.hideMask();
					console.log("表单解析完毕");
					if( isEdit ){
						$("div.widget-footer").show();	
					}
					if (isSubForm) {
                        $("div.widget-footer").hide();
                    }
				},
				error : function(msg) {
					if (window.parent) {
						window.parent.appModal.error(msg);
					} else {
						appModal.error(msg);
					}
				}
			});

			$("#btn-save").click(function() {
				if( !dyformExplain.validateForm() ){
					console.log('验证失败');
					return false;
				}
				var formData = null;
				dyformExplain.collectFormData(function(formDatas) {
					formData = formDatas;
					if (typeof formDatas != "undefined") {
						console.log("demo:\n" + JSON.cStringify(formDatas));
					}
				}, function(errorInfo) {
					console.log(errorInfo);
				});
				if( !formData ){
					appModal.error("数据收集失败");
					return false;
				}
				
				//防止重复点击和提交
				$("#btn-save").attr("disabled", "disabled");
				var url = ctx + "/pt/dyform/data/saveFormData?publishSecurityEvent=true";
				// console.log( JSON.cStringify(formData));
				$.ajax({
					url : url,
					type : "POST",
					data : JSON.cStringify(formData),
					dataType : 'json',
					contentType : 'application/json',
					success : function(result) {
						if (result.success == "true" || result.success == true) {
							console.log("数据保存成功dataUuid=" + result.data);
							appModal.success("保存成功",function(){
                                appContext.getWindowManager().refresh();
							});
							//不要刷新界面，重新填充表单数据
							// var dataUuid = result.data;
							// var dyformData = DyformFunction.loadFormDefinitionData(formuuid, dataUuid);
						//	dyformExplain.fillFormData( dyformData ); 该行代码报错，注释。有需要放开 zhongwd
//							$("#dataUuid").val( dataUuid );
//							var formUuid = $("#formUuid").val();
//							var url = ctx + "/web/dyform/data/viewer?formUuid=" + formUuid + "&dataUuid=" + dataUuid;
//							window.location.href = url;
						} else {
							appModal.alert("数据保存失败");
						}
						$("#btn-save").removeAttr("disabled");
					},
					error : function(data) {
						$("#btn-save").removeAttr("disabled");
						appModal.alert("数据保存失败");
					}
				});				
			});

		});
