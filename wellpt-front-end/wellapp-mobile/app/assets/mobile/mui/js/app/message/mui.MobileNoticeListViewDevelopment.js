define([ "mui", "commons", "constant", "server", "formBuilder", "MobileListDevelopmentBase", "DyformExplain", "formBuilder" ], function($, commons, constant, server, formBuilder,
		MobileListDevelopmentBase, DyformExplain, formBuilder) {
	var WorkFlowMobileListViewDevelopmentBase = function() {
		MobileListDevelopmentBase.apply(this, arguments);
	};

	commons.inherit(WorkFlowMobileListViewDevelopmentBase, MobileListDevelopmentBase, {
		openNoticeMessage : function(fileId){
			var data = {};
			server.JDS.call({
				service : "fileManagerService.getFileData",
				data : [fileId, null, null],
				async : false,
				success : function(result) {
					data = result.data;
				}
			});
			return data;
		},
		onClickRow : function(index, data, $element, event) {
			var self = this;
			var fileObj = self.openNoticeMessage(data.uuid);
			if(fileObj == null || typeof fileObj === "undefined"){
				return $.alert("获取公告详情失败");
			}
			if(fileObj.status == "0" || fileObj.status == 0) {
				return $.confirm("公告内容已删除.", function(data) {
					window.location.reload();
				});
			}
			var dyFormData = fileObj.dyFormData;
			if(typeof dyFormData.formDefinition == "string"){
				dyFormData.formDefinition = $.parseJSON(dyFormData.formDefinition);
			}
			var wrapper = document.createElement("div");
			wrapper.id = "dyform-view";
			var pageContainer = appContext.getPageContainer();
			var renderPlaceholder = pageContainer.getRenderPlaceholder();
			renderPlaceholder[0].appendChild(wrapper);
			formBuilder.buildPanel({
				title : fileObj.title,
				container : "#dyform-view"
			});
			$.ui.loadContent("#dyform-view");
			var wrapper = document.createElement("div");
			wrapper.classList.add("mui-dyform");
			wrapper.classList.add("dyform-no-bottom");
			$(renderPlaceholder)[0].querySelector("#dyform-view>.mui-content").appendChild(wrapper);
			var dyformExplain = new DyformExplain($(wrapper), {
				renderTo : ".mui-dyform",
				displayAsLabel : true,
				formData : dyFormData,
				complete : function() {
				},
				success : function() {
					
					$(".dyform-field-label img[style*=width]").each(function() {
						this.style.width = "100%";
						this.style.height = "100%";
					});
					$(".dyform-field-label img").each(function() {
						var self = this;
						self.classList.add("mui-image");
						var imgsrc = self.getAttribute("src") || "";
						self.setAttribute("src", imgsrc.replace("lcp.leedarson.com", "m.leedarson.com:8888"));
					});
					
					// 配置requirejs
					requirejs.config({
						paths : {
							imageViewer : ctx + "/mobile/mui/js/mui.imageViewer",
							html2canvas : ctx + "/mobile/mui/js/html2canvas.min"
						}
					});
					require(["imageViewer"], function() {
						if($.defaultImageViewer && $.defaultImageViewer.findAllImage) {
							$.defaultImageViewer.findAllImage();
						}
					});
					
					$(".dyform-field-label table").each(function(){
						var self = this;
						//debugger
						require(["html2canvas"], function(html2canvas) {
							html2canvas(self, {
								scale : 1,
								width : self.clientWidth,
								heigth : self.clientHeight
							}).then(function(canvas) {
								//debugger
								var imgURL=canvas.toDataURL("image/png");  
								var img = document.createElement("img");
								img.setAttribute("src", imgURL);
								img.style.width = "100%";
								img.style.height = "100%";
								img.classList.add("mui-image");
								self.style.display = "none";
								self.parentNode.insertBefore(img, self);
								require(["imageViewer"], function() {
									if($.defaultImageViewer && $.defaultImageViewer.findAllImage) {
										$.defaultImageViewer.findAllImage();
									}
								});
							});
						})
					})
				}
			});
		}
	});
	return WorkFlowMobileListViewDevelopmentBase;
});