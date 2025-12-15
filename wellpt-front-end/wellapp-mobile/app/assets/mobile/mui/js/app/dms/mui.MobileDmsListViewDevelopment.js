define([ "mui", "commons", "constant", "server", "formBuilder", "MobileListDevelopmentBase", "mui-DyformCommons" , "DyformExplain", "formBuilder" ], function($, commons, constant, server, formBuilder,
		MobileListDevelopmentBase, DyformCommons, DyformExplain, formBuilder) {
	var MobileDmsListViewDevelopment = function() {
		MobileListDevelopmentBase.apply(this, arguments);
	};

	commons.inherit(MobileDmsListViewDevelopment, MobileListDevelopmentBase, {
		getDyformData : function(dataUuid){
			var self = this;
			var configuration = self.getViewConfiguration();
			if(!configuration || !configuration.dmsWidgetDefinition){
				return "未配置数据管理器";
			}
			var dmsWidgetDefinition = configuration.dmsWidgetDefinition;
			var store = dmsWidgetDefinition.configuration.store;
			if(!store || !store.formUuid){
				return "未配置数据管理器单据";
			}
			return DyformCommons.loadFormData(store.formUuid, dataUuid);
		},
		onClickRow : function(index, data, $element, event) {
			var self = this;
			var dyFormData = self.getDyformData(data.uuid || data.UUID);
			if(dyFormData == null || typeof dyFormData === "undefined"){
				return $.alert("获取详情失败");
			} else if(typeof dyFormData === "string"){
				return $.alert(dyFormData);
			}
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
	return MobileDmsListViewDevelopment;
});