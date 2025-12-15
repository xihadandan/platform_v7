define([ "mui", "commons", "constant", "server", "formBuilder", "DyformExplain", "mui-MobilePanelDevelopmentBase" ], function($,
		commons, constant, server, formBuilder, DyformExplain, MobilePanelDevelopmentBase) {
	var browser = commons.Browser;
	// 消息通知
	var MobileNoticeDevelopment = function() {
		MobilePanelDevelopmentBase.apply(this, arguments);
	};
	function openOnlineMessage(uuid) {
		var data;
		$.ajax({
			url : ctx + "/message/content/openMessageInbox",
			data : {
				"uuid" : uuid
			},
			async : false,
			dataType : "json",
			success : function(result) {
				data = result;
			}
		});
		return data;
	}

	function openSmsMessage(uuid) {
		var data;
		server.JDS.call({
			service : "shortMessageService.getMessage",
			data : uuid,
			async : false,
			success : function(result) {
				data = result.data;
			}
		});
		return data;
	}
	
	function openNoticeMessage(fileId){
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
		// previewDyformData(data.dyFormData, data.title);
	}
	
	commons.inherit(MobileNoticeDevelopment, MobilePanelDevelopmentBase, {
		afterRender : function(options, configuration) {
			var self = this;
			var msgId = browser.getQueryString("msgId");
			var msgType = browser.getQueryString("msgType");
			if (!msgId || !msgType) {
				return $.alert("参数不正确");
			}
			if (msgType === "online") {
				var message = openOnlineMessage(msgId);
				var pageContainer = appContext.getPageContainer();
				var renderPlaceholder = pageContainer.getRenderPlaceholder();
				if (message && message.body && message.body.indexOf("openNoticeWindow") > 0) {
					var body = message.body;
					var openNoticeScript = body.substring(body.indexOf("openNoticeWindow"), body.lastIndexOf(");")+1);
					window.openNoticeWindow = function(fileId){
						var fileObj = openNoticeMessage(fileId);
						if(fileObj == null || typeof fileObj === "undefined"){
							return $.alert("获取公告详情失败");
						}
						if(fileObj.status == "0" || fileObj.status == 0){
							return $.confirm("公告内容已删除.", function(data) {
								$.trigger(document.body, "webview.close", {});
							});
						}
						$.ui.setTitle(fileObj.title);
						var dyFormData = fileObj.dyFormData;
						if(typeof dyFormData.formDefinition == "string"){
							dyFormData.formDefinition = $.parseJSON(dyFormData.formDefinition);
						}
						var wrapper = document.createElement("div");
						wrapper.classList.add("mui-dyform");
						wrapper.classList.add("dyform-no-bottom");
						$(renderPlaceholder)[0].querySelector(".mui-content").appendChild(wrapper);
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
					window.eval(openNoticeScript);
				}else if (message) {
					var html = "<div class='mui-dyform'><div class='mui-input-group message-detail'>";
					if(message.recipientName){
						html += "<div class='mui-input-row'>";
						html += "<label class='mui-ellipsis'>收件人</label>";
						html += "<label class='dyform-field-label recipient'>"+message.recipientName+"</label>";
						html += "</div>";
					}
					if(message.senderName){
						html += "<div class='mui-input-row'>";
						html += "<label class='mui-ellipsis'>发件人</label>";
						html += "<label class='dyform-field-label sender'>"+message.senderName+"</label>";
						html += "</div>";
					}
					if(message.subject){
						html += "<div class='mui-input-row'>";
						html += "<label class='mui-ellipsis'>主题</label>";
						html += "<label class='dyform-field-label subject'>"+message.subject+"</label>";
						html += "</div>";
					}
					if(message.body){
						html += "<div class='mui-input-row'>";
						html += "<label class='mui-ellipsis'>内容</label><br>";
						html += "<label class='dyform-field-label body'>"+message.body+"</label>";
						html += "</div>";
					}
					html += "</div></div>";
					$.ui.setTitle(message.subject || message.senderName);
					$(renderPlaceholder)[0].querySelector(".mui-content").innerHTML = html;
				} else {
					$.confirm("消息内容已删除.", function(data) {
						$.trigger(document.body, "webview.close", {});
					});
				}
			} else if (msgType === "msg") {
				var message = openSmsMessage(msgId);
				if(message){
					var pageContainer = appContext.getPageContainer();
					var renderPlaceholder = pageContainer.getRenderPlaceholder();
					var html = "<div class='mui-dyform'><div class='mui-input-group message-detail'>";
					if(message.recipientMobilePhone){
						html += "<div class='mui-input-row'>";
						html += "<label class='mui-ellipsis'>收件人</label>";
						html += "<label class='dyform-field-label recipient'>"+message.recipientMobilePhone+"</label>";
						html += "</div>";
					}
					if(message.sendMobilePhone){
						html += "<div class='mui-input-row'>";
						html += "<label class='mui-ellipsis'>发件人</label>";
						html += "<label class='dyform-field-label sender'>"+message.sendMobilePhone+"</label>";
						html += "</div>";
					}
					if(message.body){
						html += "<div class='mui-input-row'>";
						html += "<label class='mui-ellipsis'>内容</label><br>";
						html += "<label class='dyform-field-label body'>"+message.body+"</label>";
						html += "</div>";
					}
					html += "</div></div>";
					var title = message.sendMobilePhone || message.senderName;
					$.ui.setTitle(title == "system" ?  "系统管理员" :title);
					$(renderPlaceholder)[0].querySelector(".mui-content").innerHTML = html;
				} else {
					$.confirm("短信内容已删除.", function(data) {
						$.trigger(document.body, "webview.close", {});
					});
				}
			}
			// console.log("afterRender");
		}
	});
	return MobileNoticeDevelopment;
});