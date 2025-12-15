define([ "mui", "commons", "constant", "server", "formBuilder", "appModal", "mui-MobileDefaultFileList"], function
		($, commons, constant, server, formBuilder, appModal, MobileDefaultFileList) {
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	var dmsFileServices = null;
	
	var MobileLabelFileList = function() {
		MobileDefaultFileList.apply(this, arguments);
	};

	commons.inherit(MobileLabelFileList, MobileDefaultFileList, {
		
		beforeRender : function() {
			MobileDefaultFileList.prototype.beforeRender.apply(this,arguments);  			
			//this.getDataProvider().options.params['tagUuid'] = commons.Browser.getQueryString("tagUuid");
			
			this.getDataProvider().addParam('tagUuid', commons.Browser.getQueryString("tagUuid"));
		},
		
		markId : null,
		
		createLableHtml : function(html, key, value, color){
			
			html.appendFormat("<ul class=\"mui-table-view\" id=\"{0}\" style=\"margin:0px;font-size: 12px;\">", key);
			html.appendFormat("<li class=\"mui-table-view-cell\" style=\"padding:0px;margin:0px;\">");
			html.appendFormat("<a class=\"mui-navigate-right\" href=\"#\" style=\"padding: 5px 30px 5px 0px;margin:0px;{1}\">{0}</a>", value, color);
			html.appendFormat("</li>");
			html.appendFormat("</ul>");
			
		},
		
		markLabel : function(){
			var _self = this;
			_self.markId = null;
			
			var btnArray = ['取消', '确定'];
			$.prompt('', '', '标记为', btnArray, function(e) {
				if (e.index == 1) {
					if(!_self.markId){
						return $.toast("操作失败，请选择标记！");
					}
					
					if(_self.markId == 'cancel_label'){
						var rows = _self.getSelections();
						if(rows.length == 0){
							return $.toast("操作失败，必须选择一条数据");
						}
						
						var ids = [];
						$.each(rows, function(i, data){
							ids.push(data.uuid);
						});

						server.JDS.call({
							service : "dmsTagFacadeService.untagDataAll",
							data : [ids],
							async : false,
							version : "", //绕过门面服务权限验证
							success : function(result) {
								$.toast("操作成功");
								_self.getWidget().load(true);
							}
						});

					}else if(_self.markId == 'create_label'){
						
						$.prompt('', '', '新建标签', btnArray, function(e) {
							if (e.index == 1) {
								var value = document.querySelector('.mui-popup-input input').value;
								if(value.length == 0){
									return $.toast("操作失败，请填写标签名");
								}
								server.JDS.call({
									service : "dmsTagFacadeService.createTag",
									data : [value, '#90caf9'],
									async : false,
									version : "", //绕过门面服务权限验证
									success : function(result) {
										$.toast("操作成功");
										_self.getWidget().load(true);
									}
								});								
								
							}
							
						});
						document.querySelector('.mui-popup-input input').focus();
					}else if(_self.markId == 'createAndMark_label'){
						var rows = _self.getSelections();
						if(rows.length == 0){
							return $.toast("操作失败，必须选择一条数据");
						}
						
						var ids = [];
						$.each(rows, function(i, data){
							ids.push(data.uuid);
						});
						
						$.prompt('', '', '新建标签并标记', btnArray, function(e) {
							if (e.index == 1) {
								var value = document.querySelector('.mui-popup-input input').value;
								if(value.length == 0){
									return $.toast("操作失败，请填写标签名");
								}
								server.JDS.call({
									service : "dmsTagFacadeService.createTagAndTagData",
									data : [value, '#90caf9', ids],
									async : false,
									version : "", //绕过门面服务权限验证
									success : function(result) {
										$.toast("操作成功");
										_self.getWidget().load(true);
									}
								});								
								
							}
							
						});
						document.querySelector('.mui-popup-input input').focus();
					}else{
						var rows = _self.getSelections();
						if(rows.length == 0){
							return $.toast("操作失败，必须选择一条数据");
						}
						var ids = [];
						$.each(rows, function(i, data){
							ids.push(data.uuid);
						});

						server.JDS.call({
							service : "dmsTagFacadeService.tagData",
							data : [ids, _self.markId],
							async : false,
							version : "", //绕过门面服务权限验证
							success : function(result) {
								$.toast("操作成功");
							}
						});
						
						
					}
				}
			})
			server.JDS.call({
				service : "dmsTagFacadeService.queryCurrentUserTags",
				data : [],
				async : false,
				version : "", //绕过门面服务权限验证
				success : function(result) {
					
					var html = new StringBuilder();
					
					_self.createLableHtml(html, 'cancel_label','取消标签' , 'color:red;');
					$.each(result.data, function(i, data){
						_self.createLableHtml(html, data.uuid,data.name, '');
					});
					
					_self.createLableHtml(html, 'create_label','创建标签', 'color:red;');
					
					_self.createLableHtml(html, 'createAndMark_label','创建并标记标签', 'color:red;');
					$('.mui-popup-input').html(html.toString());
					document.querySelector('.mui-popup-input').style['max-height'] = '500px';
					document.querySelector('.mui-popup-input').style['overflow-y'] = 'auto';
					
					$('.mui-popup-input .mui-table-view').each(function(){
						$(this)[0].addEventListener("tap", function(event){
							if(_self.markId){
								$('#' + _self.markId)[0].style["background-color"] = '';
							}
							this.style["background-color"] = '#eee';
										
							_self.markId = this.getAttribute('id');
						});
					});
				}
			});
			
		}
	});
	return MobileLabelFileList;
});