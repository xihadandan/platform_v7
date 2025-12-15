(function(factory) {
	"use strict";
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as an anonymous module.
		define([ 'jquery', 'commons', 'server', 'constant', "ztree" ], factory);
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function($, commons, server, constant) {
	"use strict";
	var panelTemplate = '<div class="panel panel-default">';
	panelTemplate += '<div class="panel-heading" style="display:none;"></div>';
	panelTemplate += '<div class="panel-body"></div></div>';
	var StringUtils = commons.StringUtils;
	var StringBuilder = commons.StringBuilder;
	$.widget("ui.wUnitTree", $.ui.wWidget, {
		options : {
			// 组件定义
			widgetDefinition : {},
			// 上级容器定义
			containerDefinition : {},
			callback : {}
		},
		_createView : function() {
			this._renderView();
			this._bindEvents();
		},
		_renderView : function() {
			var _self = this;
			var configuration = _self.getConfiguration();
			$(_self.element).html(panelTemplate);
			if (configuration.showTitleToolbar) {
				$(".panel-heading", _self.element).show().append(configuration.name);
			}
			if (StringUtils.isNotBlank(configuration.width)) {
				$(".panel", _self.element).css("width", configuration.width);
			}
			if (StringUtils.isNotBlank(configuration.height)) {
				$(".panel", _self.element).css("height", configuration.height);
				// 滚动条
				// appContext.require(["slimScroll"], function(){
				// $(".panel", _self.element).slimScroll({height:
				// configuration.height});
				// });
			}
			var treeId = _self._getDivId();
			var $panelBody = $(".panel-body", _self.element);
			var content = new StringBuilder();
			var hiddenClass = (configuration.hasSearch ? "" : "hidden");
			content.appendFormat('<div class="div_search_container {0}"> ', hiddenClass);
			content.appendFormat('<div class="form-group form-inline">');
			content.appendFormat('	<input type="search" class="form-control" placeholder="搜索">');
			content.appendFormat('	<i class="iconfont icon-ptkj-sousuochaxun"></i>');
			content.appendFormat('</div>');
			content.appendFormat('</div>');
			if(configuration.buttons && configuration.buttons.length > 0) {
			    content.appendFormat('<div class="fixed-tree-toolbar"></div>');
            }
			content.appendFormat('<div class="div_content_container" style="overflow: auto;">');
			content.appendFormat('	<ul class="div_tree_container ztree" id="{0}">', treeId);
			content.appendFormat('	</ul>');
			content.appendFormat('</div>');
			$panelBody.append(content.toString());
			_self.treeObj = $.fn.zTree.init($("#" + treeId), _self._getZTreeSetting(), _self._loadInitData());
			var treeNodes = _self.treeObj.getNodes();
			// 自动展示根结点
			if(treeNodes.length == 1 && treeNodes[0].children.length > 0) {
			    _self.treeObj.expandNode(treeNodes[0]);
			}
			$(".div_search_container i", $panelBody).on('click', function() {
				_self._onSearch();
			});
			$(".div_search_container input", $panelBody).on('keyup', function() {
				if (event.keyCode == 13) {
					_self._onSearch();
				}
			});
			// 生成操作按钮
			_self._renderButtons();
		},
		// 生成操作按钮
		_renderButtons : function() {
		    var _self = this;
            var configuration = _self.getConfiguration();
            if(!configuration.buttons || configuration.buttons.length == 0) {
                return;
            }
            var buttonHtml = new StringBuilder();
            buttonHtml.append("<div class='btn-group'>");
            $.each(configuration.buttons, function(i, button) {
                var btnId = button.uuid;
                var btnColor = button.btnLib.btnColor;
                var btnCls = button.btnLib.btnInfo['class'];
                var btnSize = button.btnLib.btnSize;
                var btnCode = button.code;
                var btnIcon = button.btnLib.btnInfo.icon || (button.btnLib.iconInfo && button.btnLib.iconInfo.fileIDs);
                var btnText = button.text;
                if (button.btnLib.btnInfo) {
                    buttonHtml.appendFormat('<button type="button" btnId="{6}" class="well-btn {0} {1} {2} btn_class_{3}"><i class="{4}"></i>{5}</button>', btnColor, btnCls, btnSize, btnCode, btnIcon, btnText, btnId);
                } else {
                    buttonHtml.appendFormat('<button type="button" btnId="{5}" class="well-btn {0} {1} {2} btn_class_{3}">{4}</button>', btnColor, btnCls, btnSize, btnCode, btnText, btnId);
                }
            });
            buttonHtml.append("</div>");
            $(".fixed-tree-toolbar", _self.element).append(buttonHtml.toString());
		},
		_onSearch : function() {
			var _self = this;
			var configuration = _self.getConfiguration();
			var searchText = $(".div_search_container input", $(_self.element)).val();
			// 重复搜索，直接返回
			if (_self.lastestSearchText === searchText) {
				return;
			}

			if (StringUtils.isNotBlank(searchText)) {
				var treeObejct = _self._getTreeObject();
				treeObejct.cancelSelectedNode();
				treeObejct.expandAll(false);
				var rootnode = treeObejct.getNodes();
				treeObejct.expandNode(rootnode[0], true, false, false);
				var nodes = treeObejct.getNodesByParamFuzzy("name", searchText, null);
				for (var i = 0; i < nodes.length; i++) {
					treeObejct.selectNode(nodes[i], true);
					var parentnode = nodes[i].getParentNode();
					while (parentnode != null) {
						treeObejct.expandNode(parentnode, true, false, false);
						parentnode = parentnode.getParentNode();
					}
				}
			} else {
				var treeObejct = _self._getTreeObject();
				treeObejct.cancelSelectedNode();
			}
			// 异步加载，刷新树
			if (_self._isAsync()) {
				_self._refreshTree(searchText);
			}
			// 记录最新搜索的文本
			_self.lastestSearchText = searchText;
		},
		_getTreeObject : function() {
			return this.treeObj;
		},
		_refreshTree : function(searchText) {
			var _self = this;
			var treeId = _self._getDivId();
			var treeSetting = _self._getZTreeSetting(searchText);
			_self.treeObj = $.fn.zTree.init($("#" + treeId), treeSetting, _self._loadInitData(searchText));
			var treeNodes = _self.treeObj.getNodes();
			// 自动展示根结点
            if(treeNodes.length == 1 && treeNodes[0].children.length > 0) {
                _self.treeObj.expandNode(treeNodes[0]);
            }
		},
		_loadInitData : function(searchText) {
			var _self = this;
			var configuration = _self.getConfiguration();
			if(configuration.dataSource == "2") {
			    var initData = [];
			    $.ajax({
                    type : "POST",
                    url : ctx + "/basicdata/treecomponent/loadDataStoreTree",
                    data : JSON.stringify(_self._getDataStoreParams(searchText)),
                    async : false,
                    dataType : "json",
					contentType: 'application/json',
                    success : function(data) {
                        initData = data;
                        // 自定义根结点
                        if(configuration.treeRootName) {
                            var rootNode = {
                                    id : "",
                                    name : configuration.treeRootName,
                                    nodeLevel : 0,
                                    isParent : true,
                                    children : initData
                            }
                            initData = [rootNode];
                        }
                    }
                });
			    return initData;
			} else {
			    if (_self._isAsync()) {
	                return [];
	            }
	            var result = [];
	            $.ajax({
	                type : "POST",
	                url : ctx + "/basicdata/treecomponent/loadTree",
	                data : _self._getParams(),
	                async : false,
	                dataType : "json",
	                success : function(data) {
	                    result = data;
	                }
	            });
	            return result;
			}
		},
		_isAsync : function() {
			var _self = this;
			if (!_self.isAsync) {
				var configuration = this.getConfiguration();
				if(configuration.dataSource == "2") {
				    return configuration.isAsync;
				} else {
				    server.JDS.call({
	                    service : "treeComponentService.isAsync",
	                    data : [ configuration.dataProvider ],
	                    async : false,
	                    success : function(result) {
	                        if (result.msg == 'success') {
	                            _self.isAsync = result.data;
	                        }
	                    }
	                });
				}
			}
			return _self.isAsync;
		},
		_getParams : function(searchText) {
			var _self = this;
			var configuration = _self.getConfiguration();

			var otherParam = {};
			otherParam.nodeTypeInfo = JSON.stringify(configuration.nodeTypeInfo);
			otherParam.dataProvider = configuration.dataProvider;
			otherParam.dataFilter = configuration.dataFilter;
			if (StringUtils.isNotBlank(searchText)) {
				otherParam.searchText = searchText;
			}
			return otherParam;
		},
		_getDataStoreParams : function(searchText) {
		    var _self = this;
            var configuration = _self.getConfiguration();
            
            var dataStoreParam = {};
            dataStoreParam.dataStoreId = configuration.treeDataStoreId;
            dataStoreParam.uniqueColumn = configuration.treeUniqueColumn;
            dataStoreParam.parentColumn = configuration.treeParentColumn;
            dataStoreParam.displayColumn = configuration.treeDisplayColumn;
            dataStoreParam.async = configuration.isAsync;
            if(configuration.dataFilter) {
				configuration.dataFilter = appContext.resolveParamsNoConflics({sql: configuration.dataFilter}, {location: location}).sql;
			}
            dataStoreParam.defaultCondition = configuration.dataFilter;
            if (StringUtils.isNotBlank(searchText)) {
                dataStoreParam.searchText = searchText;
            }
            return dataStoreParam;
		},
		_ztreeCallback : function(callbackName, params) {
			var _self = this;
			var wType = _self.getWtype();
			if (callbackName === "onClick") {
				// 点击的节点
				var treeNode = params[2];
				var eventData = {
					selectedItem : treeNode
				};
				// 自定义的条件查询
				if (treeNode.data) {
					eventData.selectedItem.customCriterion = treeNode.data.customCriterion;
				}
				eventData.selectedItem.value = eventData.selectedItem.id;
				_self.trigger(constant.WIDGET_EVENT.ItemClick, eventData);
			}
			var callbackFn = _self.options.callback;
			if ((callbackName in callbackFn) && $.isFunction(callbackFn[callbackName])) {
				return callbackFn[callbackName].apply(_self, params);
			}
		},
		registerZtreeCallback : function(name, callback) {
			this.options.callback[name] = callback;
		},
		_getZtreeSettingCallback : function(configuration) {
			var _self = this;
			var disableTypes = _self._getDisableCheckedNodeTypeArray(configuration);
			var callback = {
				onNodeCreated : function(event, treeId, treeNode){
					var nodeType = treeNode.type;
					var ztree = $.fn.zTree.getZTreeObj( treeId );
					//不支持选择的类型，设置 checkbox或radio 隐藏
					if( $.inArray( nodeType, disableTypes ) > -1 ){
						treeNode.nocheck = true;
						ztree.updateNode( treeNode );
					}
					
				}	
			};
			var callbackKeys = [ 'beforeAsync', 'beforeCheck', 'beforeClick', 'beforeCollapse', 'beforeDblClick',
					'beforeDrag', 'beforeDragOpen', 'beforeDrop', 'beforeEditName', 'beforeExpand', 'beforeMouseDown',
					'beforeMouseUp', 'beforeRemove', 'beforeRename', 'beforeRightClick', 'onAsyncError',
					'onAsyncSuccess', 'onCheck', 'onClick', 'onCollapse', 'onDblClick', 'onDrag', 'onDragMove',
					'onDrop', 'onExpand', 'onMouseDown', 'onMouseUp', 'onRemove', 'onRename',
					'onRightClick' ]; //onNodeCreated方法有内部逻辑，不能被覆盖，所以剔除掉了
			$.each(callbackKeys, function(i, key) {
				callback[key] = function() {
					return _self._ztreeCallback(key, arguments);
				}
			});
			
			return callback;
		},
		//返回被禁用选择的节点类型
		_getDisableCheckedNodeTypeArray : function(configuration){
		    if (configuration.dataSource == "2"){
		        return [];
            } else {
                var a = [];
                for( var i = 0; i< configuration.nodeTypeInfo.length; i++  ){
                    var node = configuration.nodeTypeInfo[i];
                    if( node.disableChecked == "1" ){
                        a.push( node.type );
                    }
                }
                return a;
            }
		},
		_getZTreeSetting : function(searchText) {
			var _self = this;
			var configuration = _self.getConfiguration();
			// zTree树配置
			var setting = {
				check : {
					enable : configuration.checkEnable,
					chkStyle : configuration.chkStyle,
					chkboxType : {},
					radioType : configuration.radioType
				},
				view : {
					showLine : true,
					selectedMulti : true
				},
				callback : _self._getZtreeSettingCallback(configuration)
				
			};
			// 单选/复选联动
			var chkboxType = {
				"Y" : "ps",
				"N" : "ps"
			};
			chkboxType.Y = configuration.checkedNodeType.replace(";", "")
			chkboxType.N = configuration.uncheckNodeType.replace(";", "")
			setting.check.chkboxType = chkboxType;
			// 自定义配置
			if (configuration.customSetting) {
				var zTreeSetting = JSON.parse(configuration.zTreeSetting);
				for ( var key in zTreeSetting) {
					setting[key] = $.extend(setting[key] || {}, zTreeSetting[key])
				}
			}
			// 是否异步
			if (_self._isAsync()) {
			    // 数据来源数据仓库
			    if(configuration.dataSource == "2") {
			        setting.async = {
	                        enable : true,
	                        dataType : "json",
							contentType:'application/json',
	                        autoParam : [ "id=parentColumnValue", "type=parentType" ],
	                        otherParam : _self._getDataStoreParams(searchText),
	                        url : ctx + "/basicdata/treecomponent/loadDataStoreTree"
	                    };
			    } else {
	                setting.async = {
	                    enable : true,
	                    dataType : "json",
						contentType:'application/json',
	                    autoParam : [ "id=parentId", "type=parentType" ],
	                    otherParam : _self._getParams(searchText),
	                    url : ctx + "/basicdata/treecomponent/loadTree"
	                };
			    }
			}
			return setting;
		},
		_getDivId : function() {
			var _self = this;
			if (StringUtils.isBlank(_self.divId)) {
				_self.divId = "tree_container_" + commons.UUID.createUUID();
			}
			return _self.divId;
		},
		_bindEvents : function() {
		    var _self = this;
		    _self._bindButtonEvents();
		    _self._bindDataStoreTreeEvents();
		},
		_bindButtonEvents : function() {
		    var _self = this;
		    $(".fixed-tree-toolbar button", _self.element).on("click", function(event) {
		        var btnId = $(this).attr("btnId");
		        var button = _self._getButtonInfoById(btnId);
		        var eventManger = button.eventManger || {};
                var target = button.target || {};
                if (!$.isEmptyObject(eventManger.eventHandler) && StringUtils.isNotBlank(eventManger.eventHandler.id)) {
                    var eventHandler = eventManger.eventHandler;
                    var eventParams = eventManger.eventParams || {};
                    var opt = {
                        target: target.position,
                        targetWidgetId: target.widgetId,
                        refreshIfExists: target.refreshIfExists,
                        eventTarget: target,
                        event: event,
                        appId: eventHandler.id,
                        appType: eventHandler.type,
                        appPath: eventHandler.path,
                        params: $.extend({}, eventParams.params, appContext.parseEventHashParams(eventHandler, "menuid")),
                        button: button,
                        ui: _self
                    };
                    _self.startApp(opt);
                } else {
                    var eventParams = eventManger.eventParams || {};
                    var opt = {
                        ui: _self
                    };
                    opt.params = appContext.resolveParams(eventParams.params, opt);
                    var args = [event, opt];
                    _self.invokeDevelopmentMethod(button.code, args);
                }
		    });
		},
		_getButtonInfoById : function(btnId) {
		    var _self = this;
		    var configuration = _self.getConfiguration();
		    var buttons = configuration.buttons;
		    for(var i = 0; i < buttons.length; i++) {
		        if(buttons[i].uuid == btnId) {
		            return $.extend(true, {}, buttons[i]);
		        }
		    }
		    return null;
		},
		// 绑定数据仓库树结点事件处理
		_bindDataStoreTreeEvents : function() {
		    var _self = this;
		    var configuration = _self.getConfiguration();
		    if(configuration.dataSource != "2") {
		        return;
		    }
            var treeDisplayColumn = configuration.treeDisplayColumn;
            var treeParentColumn = configuration.treeParentColumn;
		    // 树结点数据更新
		    _self.getPageContainer().on("treeNodeDataUpdated", function(e, ui) {
		        // 触发结点数据更新二开回调
                _self.invokeDevelopmentMethod("onTreeNodeDataUpdated", [e, ui]);
                
		        if(e.detail == null) {
		            return;
		        }
		        var treeId = e.detail.uuid || e.detail.dataUuid;
		        if(StringUtils.isBlank(treeId)) {
		            _self._refreshTree();
                    return;
                }
		        var dsNode = _self.loadDataStoreTreeNodeById(treeId);
		        if(dsNode != null && dsNode.data != null) {
		            var parentId = dsNode.data[treeParentColumn];
		            // 存在树结点，更新结点名称
		            if(_self.existsTreeNodeById(treeId)) {
		                var treeNode = _self.getTreeNodeById(treeId);
		                treeNode.name = dsNode.data[treeDisplayColumn];
		                _self.treeObj.updateNode(treeNode);
		                // 父结点结点变更
		                var parentNode = treeNode.getParentNode();
		                if(parentNode != null) {
		                    if(parentNode.id != parentId) {
		                        var parentNode = _self.getTreeNodeById(parentId);
		                        if(parentNode != null) {
		                            _self.treeObj.removeNode(treeNode);
		                            _self.treeObj.addNodes(parentNode, [treeNode]);
		                        }
		                    }
		                } else if(StringUtils.isNotBlank(parentId)) {
		                    var parentNode = _self.getTreeNodeById(parentId);
		                    _self.treeObj.removeNode(treeNode);
		                    _self.treeObj.reAsyncChildNodes(parentNode, "refresh");
		                }
		            } else if(_self.existsTreeNodeById(parentId)) {
		                var parentNode = _self.getTreeNodeById(parentId);
		                // 不存在树结点，但存在父结点，同步情况下添加，异步情况下刷新
		                if(_self._isAsync()) {
		                    parentNode.isParent = true;
		                    _self.treeObj.updateNode(parentNode);
		                    _self.treeObj.reAsyncChildNodes(parentNode, "refresh");
		                } else {
		                    _self.treeObj.addNodes(parentNode, [dsNode]);
		                }
		            } else {
		                _self._refreshTree();
		            }
		        }
		    });
		    // 树结点数据删除
            _self.getPageContainer().on("treeNodeDataDeleted", function(e, ui) {
                // 触发结点数据删除二开回调
                _self.invokeDevelopmentMethod("onTreeNodeDataDeleted", [e, ui]);
                if(e.detail == null) {
                    return;
                }
                if(e.detail == null) {
                    return;
                }
                var treeIdString = e.detail.uuid || e.detail.dataUuid;
                if(StringUtils.isBlank(treeIdString)) {
                    _self._refreshTree();
                    return;
                }
                var treeIds = treeIdString.split(";");
                $.each(treeIds, function(i, treeId) {
                    var treeNode = _self.getTreeNodeById(treeId);
                    if(treeNode != null) {
                        _self.treeObj.removeNode(treeNode);
                    }
                });
            });
		},
		existsTreeNodeById : function(treeId) {
		    var _self = this;
		    var nodes = _self.treeObj.getNodesByParam("id", treeId, null);
            return nodes.length == 1;
		},
		getTreeNodeById : function(treeId) {
		    var _self = this;
            var nodes = _self.treeObj.getNodesByParam("id", treeId, null);
            if(nodes.length == 1) {
                return nodes[0];
            }
            return null;
		},
		loadDataStoreTreeNodeById : function(treeId) {
		    var _self = this;
		    var treeNode = null;
		    $.ajax({
                type : "POST",
                url : ctx + "/basicdata/treecomponent/loadDataStoreTreeNode",
                data : $.extend(_self._getDataStoreParams(), {treeId: treeId, async: false}),
                async : false,
                dataType : "json",
                success : function(data) {
                    treeNode = data;
                }
            });
		    return treeNode;
		}
	});
}));