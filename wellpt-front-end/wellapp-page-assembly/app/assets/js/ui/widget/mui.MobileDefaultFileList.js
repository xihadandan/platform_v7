define([ "mui", "commons", "constant", "server", "formBuilder", "MobileListDevelopmentBase", "appContext", "DmsFileServices", "DmsListViewActionBase", "DmsDataServices", "appModal", "multiOrg"], function
		($, commons, constant, server, formBuilder, MobileListDevelopmentBase, appContext, DmsFileServices, DmsListViewActionBase, DmsDataServices, appModal, unit) {
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	var MobileDefaultFileList = function() {
		MobileListDevelopmentBase.apply(this, arguments);
	};

	commons.inherit(MobileDefaultFileList, MobileListDevelopmentBase, {

		allowActionMap : null,

		currentModuleId : null, //选择器查询范围使用

		currentFolderUuid : null,

		folderUuids : [],

		folderUuid : null,

		beforeRender : function() {
			var _self = this;
			$.FileViewer = $.extend($.FileViewer, {  //
				dispose : function(){
					var num = 1;
					if(($('#' + _self.currentModuleId + ' > .wui-file-actionsheet').length) > 0){  //
						num = num + 1;
					}

					if($('#' + _self.currentModuleId + ' > div').length > num){
						return false;
					}else if((_self.folderUuids.length == 0 && !_self.currentFolderUuid)){
						return false;
					}
					return true;
				}
			});

			_self.dmsDataServices = new DmsDataServices();
			_self.dmsFileServices = new DmsFileServices();
			var parentWidget = _self.getWidget().getParent();
			_self.allowActionMap = parentWidget.allowActionMap;

			var folderUuid = parentWidget.getConfiguration().belongToFolderUuid;
			folderUuid == null ? "" : folderUuid;

			_self.folderUuid = folderUuid;

			_self.currentFolderUuid = folderUuid;

			_self.getDataProvider().addParam('folderUuid', folderUuid);
			_self.getDataProvider().addParam('listFileMode', parentWidget.getListFileMode() == null ? "" : parentWidget.getListFileMode());

			var getCurrentModuleId = function(widget){
				var parent = widget.getParent();
				if(parent && parent.element.length > 0){
					getCurrentModuleId(parent);
				}else{
					_self.currentModuleId = widget.element[0].id
				}
			};
			getCurrentModuleId(_self.getWidget());
		},

		afterRender : function(options, configuration){
			var _self = this;
			_self.hiddenButtons();
			$('.mui-bar-nav > a.mui-action-back', $('#' + this.currentModuleId)[0])[0].addEventListener("tap", function(event){
				if(_self.folderUuids.length > 0){

					_self.currentFolderUuid = _self.folderUuids.pop();

					_self.getDataProvider().addParam('folderUuid', _self.currentFolderUuid);

					_self.getWidget().load(true);

					// event.stopPropagation();
				}else{
					_self.currentFolderUuid = null;
				}
			}, false);
			// 没用?
			_self.getWidget().element.on("tap", ".mui-file-operate", function(event) {
				var actionMarkId = "wui-file-actionsheet-id";
				var actionSheetSelector = "wui-file-actionsheet";
				// 更多操作按钮
				var pageContainer = appContext.getPageContainer();
				var placeholder = pageContainer.getRenderPlaceholder()[0];
				var actionSheetWrapper = placeholder.querySelector("." + actionSheetSelector);
				if (actionSheetWrapper != null) {
					actionSheetWrapper.parentNode.removeChild(actionSheetWrapper);
				}
				actionSheetWrapper = document.createElement("div");
				actionSheetWrapper.classList.add(actionSheetSelector);
				placeholder.appendChild(actionSheetWrapper);

				var index = this.offsetParent.offsetParent.offsetParent.getAttribute('data-index');
				var data = _self.getWidget().options.data[index];

				var fileItemActionMap = _self.getFileItemActionMap([data]);

				var actionSheets =  [];
				for(var action in _self.allowActionMap){
					if(fileItemActionMap[action]){
						actionSheets.push({
							id : action + "_end_line",
							name : action + "_end_line",
							text : _self.allowActionMap[action].name
						});
					}
				}

				formBuilder.buildActionSheet({
					sheetId : actionMarkId,
					data : actionSheets,
					container : actionSheetWrapper
				});

				// 绑定ActionSheet事件
				$(actionSheetWrapper).on("tap", ".mui-table-view-cell", function(event) {
					$("#action_sheet_" + actionMarkId).popover("toggle");
					var name = this.getAttribute("name");
					if(name){
						var method = name.split("_")[0];
						if(typeof _self[method] == "function"){
							_self[method].call(_self, {data : data});
						}
					}
				});

				$("#action_sheet_" + actionMarkId).popover("toggle");
	            event.stopPropagation();
			});
		},

		hiddenButtons : function(){
			var isShowListMore = false;
			$("#action_sheet_list-more ul li").each(function(){
				var name = this.getAttribute('name');
				if(!name){
					return;
				}

				if(this.getAttribute('id').indexOf('_mobile_file_button') == -1){
					isShowListMore = true;
				}else{
					this.classList.add("mui-hidden");
				}
			});

			$('#list_tab_bar_mobile_view nav a', $('#' + this.currentModuleId)[0]).each(function(){
				if(this.getAttribute('id').indexOf('_mobile_file_button') != -1){
					this.classList.add("mui-hidden");
				}
				if(this.getAttribute('id') == 'list-more' && !isShowListMore){
					this.classList.add("mui-hidden");
				}
			});
		},

		getFileItemActionMap : function(datas){
			var _self = this;
			var fileItemActions = _self.dmsFileServices.getIntersectionActions(datas);
			var fileItemActionMap = {};
			for(var i = 0; i < fileItemActions.length; i++){
				fileItemActionMap[fileItemActions[i].id] = true;
			}
			return fileItemActionMap;
		},

		onAddLiElement : function(index, data, $element) {
			var _self = this;
			$element.addEventListener("tap", function(event){
				var widget = _self.getWidget();
				if(widget && widget.editable){
					var checkedSelector = ".table-view-content input.input-check-item:checked";
					var datas = _self.getSelections();
					// click时间会延迟200毫秒执行，所以此处checkbox选择，需要手动设置
					var data = _self.getWidget().options.data[$element.getAttribute('data-index')];
					if($(checkedSelector, $element).length == 0){
						datas.push(data);
					}else{
						for(var i = 0; i < datas.length; i++){
							if(datas[i].uuid == data.uuid){
								datas.splice(i, 1);
								break;
							}
						}
					}

					var fileItemActionMap = _self.getFileItemActionMap(datas);

					var isShowListMore = false;
					$("#action_sheet_list-more ul li").each(function(){
						var name = this.getAttribute('name');
						if(!name){
							return;
						}
						if(this.getAttribute('id').indexOf('_mobile_file_button') == -1 || fileItemActionMap[name]){
							isShowListMore = true;
							this.classList.remove("mui-hidden");
						}else{
							this.classList.add("mui-hidden");
						}
					});


					$('#list_tab_bar_mobile_view nav a').each(function(){
						var name = this.getAttribute('name');
						if(this.getAttribute('id').indexOf('_mobile_file_button') == -1 || fileItemActionMap[name]){
							this.classList.remove("mui-hidden");
						}else{
							this.classList.add("mui-hidden");
						}

						if(name == 'list-more' && isShowListMore){
							this.classList.remove("mui-hidden");
						}

						if(name == 'list-more' && !isShowListMore){
							this.classList.add("mui-hidden");
						}
					});
				}
			},false);
		},

		getCtx : function() {
			var self = this;
			if (self.getWidget().options.baseurl) {
				return self.options.baseurl;
			} else if (window.ctx) {
				return window.ctx;
			}
			return "";
		},

		getDmsListViewActionBase : function(){
			var _self = this;
			var options = {
					ui : _self.getWidget()
				};
			return new DmsListViewActionBase(options);
		},

		showDyform : function(row){
			var _self = this;

			var dmsListViewActionBase = _self.getDmsListViewActionBase();

			var widget = _self.getWidget();
			var paramOptions = {
				appFunction : {
					id : "open_view"
				},
				rowdata : row
			};

			//若该夹有配置手机单据，则打开手机单据，否则提醒用户去配置
			var folderUuid = row.folderUuid;//文件库夹UUID
			var formUuid = row.formUuid;
			server.JDS.call({
				service : "dmsFolderService.get",
				data : [folderUuid],
				async : true,
				version : "", //绕过门面服务权限验证
				success : function(result) {
					if(!result.data){
						$.toast("该文件库所使用的夹不存在");
						console.error("该文件库所使用的夹不存在, 夹UUID=" + folderUuid);
						return;
					}else{
						var folderName = result.data.name;
						server.JDS.call({
							service : "dmsFolderConfigurationService.getByFolderUuid",
							data : [folderUuid],
							async : true,
							version : "", //绕过门面服务权限验证
							success : function(result) {
								if(!result.data){
									$.toast("请事先对该文件库所使用的文件夹("+folderName+")进行配置!!");
									console.error("请事先对该文件库所使用的文件夹("+folderName+")进行配置, 夹UUID=" + folderUuid);
									return;
								}else{
									var configuration = result.data.configuration;
									if(configuration){
										configuration = eval("(" + configuration+");")
										if(configuration.displayMFormUuid){
											var urlParams = dmsListViewActionBase.getUrlParams(paramOptions);
											$.extend(urlParams, {
												idKey : "UUID",
												idValue : row.dataUuid,
												dms_id : urlParams.dms_id,
												ac_id : "btn_file_manager_dyform_get",
												fd_id : row.folderUuid,
												doc_id : row.uuid,
												fileUuid : row.uuid,
												folderUuid : row.folderUuid,
												doc_def_uuid : formUuid,
												extraParams : {
													"ep_ac_get" : "btn_file_manager_dyform_get"
												}
											});
											appContext.getWidgetById(urlParams.dms_id).getConfiguration().store.formUuid = formUuid;

											_self.dmsDataServices.openWindow({
												urlParams : urlParams,
												ui : widget
											});
										}else{
											$.toast("请事先配置文件库所使用的文件夹("+folderName+")的手机单据属性!!");
											console.error("请事先配置文件库所使用的文件夹("+folderName+")的手机单据属性, 夹UUID=" + folderUuid);

										}
									}else{
										$.toast("请事先对该文件库所使用的文件夹("+folderName+")进行配置");
										console.error("请事先对该文件库所使用的文件夹("+folderName+")进行配置");
									}
								}
							}
						});

					}
				}
			});
		},

		showFile: function(row){
			var _self = this;
			$.trigger(document.body, "file.show", {"fileObj" : {
				"image" : false, //先填false
				"fileId" : row["uuid"],
				"fileExt" : "",
				"fileSize" : row["fileSize"] || 0,
				"fileName" : row["name"],
				"fileUrl" : _self.getCtx() + "/repository/file/mongo/download?fileID=" + row["uuid"]
			}});

		},

		showList : function(row){
			var _self = this;
			//_self.getDataProvider().options.params['folderUuid'] = row['uuid'];

			_self.getDataProvider().addParam('folderUuid', row['uuid']);
			_self.folderUuids.push(new String(_self.currentFolderUuid));
			_self.currentFolderUuid = row['uuid'];
			_self.getWidget().load(true);
		},

		onClickRow : function(index, row, $element, event) {
			var _self = this;
			if (_self.dmsFileServices.isDyform(row)) {
				_self.showDyform(row);
			} else if (_self.dmsFileServices.isFile(row)) {
				_self.showFile(row);
			} else {
				_self.showList(row);
			}
		},


		getFileManagerWidget : function() {
			var _self = this;
			var fileManagerWidget = _self.fileManagerWidget;
			if (fileManagerWidget != null) {
				return fileManagerWidget;
			}
			var configuration = _self.getViewConfiguration();
			if (configuration.dmsWidgetDefinition) {
				fileManagerWidget = appContext.getWidgetById(configuration.dmsWidgetDefinition.id);
				_self.fileManagerWidget = fileManagerWidget;
			}
			return fileManagerWidget;
		},

		download : function(){

			var _self = this;
			var rows = [];
			if(arguments.length == 1 && arguments[0].data){
				rows.push(arguments[0].data);
			}else{
				rows = _self.getSelections();
				if(rows.length != 1) {
					return $.toast("必须且只能选择一条数据");
				}
			}
			var row = rows[0];

			if (_self.dmsFileServices.isDyform(row)) {
				_self.showDyform(row);
			} else if (_self.dmsFileServices.isFile(row)) {
				_self.showFile(row);
			} else {
				$.toast("文件夹不能下载！")
			}

		},

		share : function(){
			var _self = this;
			var rows = [];
			if(arguments.length == 1 && arguments[0].data){
				rows.push(arguments[0].data);
			}else{
				rows = _self.getSelections();
				if(rows.length == 0) {
					return $.toast("必须选择一条数据");
				}
			}

			var options = {
					multiple: true,// 是否多选
					selectTypes: "all", //可以选择的类型，all:全部，M:组织，O：单位，D:部门，J：职位，U:账号，G：群组，如果有多个则以分号多分割
					title : "组织选择",
					type : "MyUnit;MyDept;MyLeader;MyUnderling;MyCompany;DutyGroup;MyPersonalGroup",
					typeList : ["MyUnit", "MyDept", "MyLeader", "MyUnderling", "MyCompany", "DutyGroup", "MyPersonalGroup"],
					valueFormat: "justId", //all 代表完整格式( V0000000001/U000000001 ), justId : 代表仅组织ID( U0000000001 )
					callback : function(){

						var orgIds = arguments[0];
						var orgNames = arguments[1];
						if(orgIds.length == 0){
							return $.toast("分享失败，没有选择用户或组织");
						}

						var shareOrgId = "";
						var shareOrgName = "";
						for(var i = 0; i < orgIds.length; i++){
							shareOrgId = shareOrgId + orgIds[i] + ";";
							shareOrgName = shareOrgName + orgNames[i] + ";";
						}
						shareOrgId = shareOrgId.substr(0, shareOrgId.length - 1);
						shareOrgName = shareOrgName.substr(0, shareOrgName.length - 1);

	        			server.JDS.call({
	        				service : "dmsFileManagerService.shareFile",
	        				data : [rows, shareOrgId, shareOrgName],
	        				async : false,
	        				version : "", //绕过门面服务权限验证
	        				success : function(result) {
	        					$.toast("分享成功！");
	        				}
	        			});
					}
			};


			unit.open(options);
		},

		cancelShare : function(e) {
			var _self = this;
			var rows = [];
			if(arguments.length == 1 && arguments[0].data){
				rows.push(arguments[0].data);
			}else{
				rows = _self.getSelections();
				if(rows.length == 0) {
					return $.toast("必须选择一条数据");
				}
			}

			appModal.confirm("确认取消分享？", function (result) {
                if (result === true || result === 1) {
        			server.JDS.call({
        				service : "dmsFileManagerService.cancelShareFile",
        				data : [rows],
        				async : false,
        				version : "", //绕过门面服务权限验证
        				success : function(result) {
        					_self.getWidget().load(true);
        				}
        			});
                }
            });
		},

		generateTree : function(html, data){

			var _self = this;

			var hasChildren = data.isParent;
			html.appendFormat("<ul class=\"mui-table-view\" style=\"margin:0px;font-size: 12px;\">");
			html.appendFormat("<li class=\"mui-table-view-cell {0}\" style=\"padding:0px;margin:0px;\">", hasChildren ? "mui-collapse" : "");
			html.appendFormat("<a class=\"mui-navigate-right\" id=\"{1}\" href=\"#\" style=\"padding: 5px 30px 5px 0px;margin:0px;\">{0}</a>", data.name, data.id);
			(hasChildren && html.appendFormat("<div class=\"mui-collapse-content\" style=\"margin: 0px 0px 0px 15px;padding:0px;\">"));

			$.each(data.children, function(idx, data) {
				_self.generateTree(html, data);
			})

			(hasChildren && html.appendFormat("</div>"));

			html.appendFormat("</li>");
			html.appendFormat("</ul>");
		},

		operateId : null,

		loadTreeData : function(param, target){

			if($('.mui-collapse-content > ul', target.offsetParent()).length > 0){
				return;
			}

			var _self = this;

			var str = "";
			for(var i in param){
				str = str + i + '=' + param[i] + '&';
			}


			str = str.substr(0, str.length - 1);

			var url = _self.getCtx() + "/dms/file/manager/component/load/folder/tree?" + str;

			$.ajax({
                url: url,
                async: false,
                type: "GET",
                dataType: "json",
                success: function (data) {
					var html = new StringBuilder();
					for(var i = 0; i < data.length; i++){
						_self.generateTree(html, data[i]);
					}
					var targetId = target[0].getAttribute('id');
					if(targetId){
						$('.mui-collapse > .mui-collapse-content', target.offsetParent()).html(html.toString());
					}else{
						target.html(html.toString());
					}

					var $as = $('.mui-collapse > .mui-navigate-right', target.offsetParent());

					for(var i = 0; i < $as.length; i++){
						var $a = $as[i];
						var id = $a.getAttribute('id');
						if(!targetId || id != targetId){
							$a.addEventListener("tap", function(event){
								_self.loadTreeData({
									checkIsParent : 'true',
									belongToFolderUuid : _self.folderUuid,
									parentFolderUuid : this.getAttribute('id')},  $(this));
							});
						}
					}

					$as = $('.mui-navigate-right', target.offsetParent());

					for(var i = 0; i < $as.length; i++){
						var $a = $as[i];
						var id = $a.getAttribute('id');
						if(!targetId || id != targetId){
							$a.addEventListener("tap", function(event){

								if(_self.operateId){
									$('#' + _self.operateId)[0].style["background-color"] = '';
								}
								_self.operateId = this.getAttribute('id');

								this.style["background-color"] = '#aae6fd';
							});
						}
					}

                }
            });

		},

		move : function(){

			var _self = this;
			var rows = [];
			if(arguments.length == 1 && arguments[0].data){
				rows.push(arguments[0].data);
			}else{
				rows = _self.getSelections();
				if(rows.length == 0) {
					return $.toast("必须选择一条数据");
				}
			}

			var btnArray = ['取消', '确定'];
			$.prompt('', '', '移动到', btnArray, function(e) {
				if (e.index == 1) {
					if(!_self.operateId){
						return $.toast("必须选择一个文件夹");
					}

					server.JDS.call({
						service : "dmsFileManagerService.moveFile",
						data : [rows, _self.operateId],
						async : false,
						version : "", //绕过门面服务权限验证
						success : function(result) {
							_self.getWidget().load(true);
						}
					});
				}
			})
			_self.operateId = null;
			_self.loadTreeData({checkIsParent : 'true', belongToFolderUuid : _self.folderUuid}, $('.mui-popup-input'));
		},

		copy : function(){
			var _self = this;
			var rows = [];
			if(arguments.length == 1 && arguments[0].data){
				rows.push(arguments[0].data);
			}else{
				rows = _self.getSelections();
				if(rows.length == 0) {
					return $.toast("必须选择一条数据");
				}
			}

			var btnArray = ['取消', '确定'];
			$.prompt('', '', '移动到', btnArray, function(e) {
				if (e.index == 1) {
					if(!_self.operateId){
						return $.toast("必须选择一个文件夹");
					}

					server.JDS.call({
						service : "dmsFileManagerService.copyFile",
						data : [rows, _self.operateId],
						async : false,
						version : "", //绕过门面服务权限验证
						success : function(result) {
							_self.getWidget().load(true);
						}
					});
				}
			})
			_self.operateId = null;
			_self.loadTreeData({checkIsParent : 'true', belongToFolderUuid : _self.folderUuid}, $('.mui-popup-input'));
		},

		rename : function(){
			var _self = this;
			var rows = [];
			if(arguments.length == 1 && arguments[0].data){
				rows.push(arguments[0].data);
			}else{
				rows = _self.getSelections();
				if(rows.length != 1) {
					return $.toast("必须且只能选择一条数据");
				}
			}
			var row = rows[0];
			var btnArray = ['取消', '确定'];
			$.prompt('', '', '重命名', btnArray, function(e) {
				if (e.index == 1) {

					var service = "dmsFileManagerService.renameFolder";

					if (_self.dmsFileServices.isDyform(row) || _self.dmsFileServices.isFile(row)) {
						service = "dmsFileManagerService.renameFile";
					}

        			server.JDS.call({
        				service : service,
        				data : [row.uuid, document.querySelector('.mui-popup-input input').value],
        				async : false,
        				version : "", //绕过门面服务权限验证
        				success : function(result) {
        					_self.getWidget().load(true);
        				}
        			});
				}
			})
			document.querySelector('.mui-popup-input input').focus();
			document.querySelector('.mui-popup-input input').value = row.name;



		},

		createFolder : function(){

			var _self = this;

			var btnArray = ['取消', '确定'];

			$.prompt('', '', '新建文件夹', btnArray, function(e) {
				if (e.index == 1) {

					var service = "dmsFileManagerService.checkTheSameNameForCreateFolder";
					var checkResult = null;
					var name = document.querySelector('.mui-popup-input input').value;
        			server.JDS.call({
        				service : service,
        				data : [name, _self.currentFolderUuid],
        				async : false,
        				version : "", //绕过门面服务权限验证
        				success : function(result) {
        					checkResult = result;
        				}
        			});

        			if(checkResult.data){
        				return $.toast('创建失败，文件夹已经存在！');
        			}

        			service = "dmsFileManagerService.createFolder";
        			server.JDS.call({
        				service : service,
        				data : [null, name, _self.currentFolderUuid],
        				async : false,
        				version : "", //绕过门面服务权限验证
        				success : function(result) {
        					_self.getWidget().load(true);
        				}
        			});

				}
			})

		},

		viewAttributes : function(){

			var _self = this;
			var rows = [];
			if(arguments.length == 1 && arguments[0].data){
				rows.push(arguments[0].data);
			}else{
				rows = _self.getSelections();
				if(rows.length != 1) {
					return $.toast("必须且只能选择一条数据");
				}
			}

			var btnArray = ['关闭'];
			$.prompt('', '', '属性信息', btnArray, function(e) {
			})


			server.JDS.call({
				service : "dmsFileManagerService.getAttributes",
				data : rows,
				async : false,
				version : "", //绕过门面服务权限验证
				success : function(result) {

					var data = result.data;
					var html = new StringBuilder();

					_self.createViewAttributesHtml(html, '名称' , data.name);
					_self.createViewAttributesHtml(html, '类型' , data.contentTypeName);
					_self.createViewAttributesHtml(html, '位置' , data.location);
					_self.createViewAttributesHtml(html, '创建时间' , data.createTime);
					_self.createViewAttributesHtml(html, '修改时间' , data.modifyTime);


					$('.mui-popup-input').html(html.toString());

				}
			});

		},

		createViewAttributesHtml : function(html, name, value){
			html.appendFormat("<ul class=\"mui-table-view\" style=\"margin:0px;font-size: 12px;\">");
			html.appendFormat("<li class=\"mui-table-view-cell\" style=\"padding:0px;margin:0px;\">");
			html.appendFormat("<a class=\"mui-navigate-right\" href=\"#\" style=\"padding: 5px 30px 5px 0px;margin:0px;\">{0}：{1}</a>", name, value);
			html.appendFormat("</li>");
			html.appendFormat("</ul>");
		},

		createDocument : function(){
			var _self = this;

			var form = null;
			server.JDS.call({
				service : "dmsFileManagerService.getFolderDyformDefinitionByFolderUuid",
				data : _self.folderUuid,
				async : false,
				version : "", //绕过门面服务权限验证
				success : function(result) {
					if(result.success){
						form = result.data;
					}
				}
			});

			if(!form){
				return $.toast("操作失败，未找到表单配置！");
			}

			var mUuid = null;
			server.JDS.call({
				service : "formDefinitionService.findVFormAndMForm",
				data : {
					pFormUuid : form.formUuid,
					formType : 'M'
				},
				async : false,
				version : "", //绕过门面服务权限验证
				success : function(result) {
					var data = result.data;
					if(data.length > 0){
						mUuid = data[0];
					}
				}
			});


			if(!mUuid){
				return $.toast("操作失败，未配置手机表单！");
			}

			var dmsListViewActionBase = _self.getDmsListViewActionBase();

			urlParams = {
				folderUuid: _self.currentFolderUuid,
				dms_id : $(dmsListViewActionBase.ui.element).data("dms_id"),
				ac_id : "btn_file_manager_dyform_get",
				extraParams : {
					"ep_ac_get" : "btn_file_manager_dyform_get"
				}
			};

			appContext.getWidgetById(urlParams.dms_id).getConfiguration().store.formUuid = mUuid;

			var widget = _self.getWidget();

			_self.dmsDataServices.openWindow({
				urlParams : urlParams,
				ui : widget
			});
		},

		'delete' :  function(){

			var _self = this;
			var rows = [];
			if(arguments.length == 1 && arguments[0].data){
				rows.push(arguments[0].data);
			}else{
				rows = _self.getSelections();
				if(rows.length == 0) {
					return $.toast("必须选择一条数据");
				}
			}

            appModal.confirm("确认删除吗？", function (result) {
                if (result === true || result === 1) {
        			server.JDS.call({
        				service : "dmsFileManagerService.deleteFile",
        				data : [rows],
        				async : false,
        				version : "", //绕过门面服务权限验证
        				success : function(result) {
        					_self.getWidget().load(true);
        				}
        			});
                }
            });
		}
	});
	return MobileDefaultFileList;
});
