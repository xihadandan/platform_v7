define(['ui_component', 'constant', 'commons', 'server', 'appContext', 'design_commons', 'appModal', 'formBuilder'], function (
  ui_component,
  constant,
  commons,
  server,
  appContext,
  designCommons,
  appModal,
  formBuilder
) {
  var component = $.ui.component.BaseComponent();

  var StringUtils = commons.StringUtils;
  var UUID = commons.UUID;
  var NAV_TREE_OP_TYPE = {
    // 导航树操作类型
    ADD: 1, // 增加同级
    ADD_CHILD: 2, // 增加子级
    EDIT: 3
    // 修改
  };

  var NAV_TYPE = {
    // 导航类型
    SYSTEM_INTEGRATION: 1, // 系统集成
    DIY: 2,
    INTERFACE: 3
    // 自定义
  };

  component.prototype.create = function () {};
  // 使用属性配置器
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  // 返回属性配置器
  component.prototype.getPropertyConfigurer = function () {
    var collectClass = 'w-configurer-option';
    var navTreeCollectClass = 'w-nav-tree-option';
    var configurer = $.ui.component.BaseComponentConfigurer();
    configurer.prototype.onLoad = function ($container, options) {
      var _self = this.component;
      // 初始化页签项
      $('#widget_left_sidebar_tabs ul a', $container).on('click', function (e) {
        e.preventDefault();
        $(this).tab('show');
      });
      var configuration = $.extend(true, {}, options.configuration);
      this.initConfiguration(configuration, $container);
    };
    // 初始化配置信息
    configurer.prototype.initConfiguration = function (configuration, $container) {
      // 基本信息
      this.initBaseInfo(configuration, $container);
      // 导航信息
      this.initNavInfo(configuration, $container);
    };
    configurer.prototype.initBaseInfo = function (configuration, $container) {
      // 设置值
      designCommons.setElementValue(configuration, $container);

      // 二开JS模块
      $('#jsModule', $container).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        params: {
          dependencyFilter: 'LeftSidebarWidgetDevelopment'
        },
        labelField: 'jsModuleName',
        valueField: 'jsModule',
        remoteSearch: false,
        multiple: true
      });
    };
    configurer.prototype.initNavInfo = function (configuration, $container) {
      var _self = this;
      // 设置值
      designCommons.setElementValue(configuration, $container);

      var appPageUuid = _self.component.pageDesigner.getPageUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;

      var $nav = $('#widget_left_sidebar_tabs_nav_info');
      var navTreeId = 'widget_left_sidebar_nav_tree';
      var $navTree = $('#' + navTreeId);
      var $navTreeNodeProp = $('#widget_left_sidebar_nav_tree_node_prop');

      // 绑定导航类型值变化事件
      $("input[name='navType']", $container)
        .on('change', function () {
          var navType = $("input[name='navType']:checked", $container).val();
          if (navType == NAV_TYPE.SYSTEM_INTEGRATION) {
            $('.navResourceRow', $container).show();
            $('.navDataRow', $container).hide();
            $('.navInterfaceRow', $container).hide();
          } else if (navType == NAV_TYPE.DIY) {
            $('.navResourceRow', $container).hide();
            $('.navDataRow', $container).show();
            $('.navInterfaceRow', $container).hide();
          } else if (navType == NAV_TYPE.INTERFACE) {
            $('.navResourceRow', $container).hide();
            $('.navDataRow', $container).hide();
            $('.navInterfaceRow', $container).show();
          }
        })
        .trigger('change');

      // 初始化系统集成导航下拉树
      // $("input[name='navResource']", $nav).wCommonComboTree({
      // 	service : "appProductIntegrationMgr.getTreeByProductUuidAndDataType",
      // 	serviceParams : [ productUuid, [ 1, 2, 3 ] ],
      // 	width : "690px",
      // 	multiSelect : false, // 是否多选
      // 	parentSelect : true
      // // 父节点选择有效，默认无效
      // });

      $("input[name='navResourceName']", $nav).AppEvent({
        ztree: {
          serviceName: 'appProductIntegrationMgr',
          methodName: 'getTreeByProductUuidAndDataType',
          params: [productUuid, [1, 2, 3]]
        },
        okCallback: function ($el, data) {
          if (data) {
            $("input[name='navResource']", $nav).val(data.id);
          }
        }
      });
      $("input[name='navResourceName']", $nav).AppEvent('setValue', configuration.navResource);

      // 初始化自定义导航数据
      var zNodes = [];
      if (configuration && configuration.nav) {
        zNodes = configuration.nav;
      }
      // 选择图标
      $('#navTreeIconSelectBtn', $container).on('click', function () {
        var _thiz = this;
        $.WCommonPictureLib.show({
          selectTypes: [3],
          confirm: function (data) {
            var fileIDs = data.fileIDs;
            $('#navTreeIcon').val(fileIDs);
            $('#navTreeIconSnap', $container).attr('iconClass', fileIDs);
            $('#navTreeIconSnap', $container).addClass(fileIDs);
          }
        });
      });
      // 显示标签数量的视图组件选择控制
      $('#navTreeShowBadgeCount', $container)
        .on('change', function () {
          if (this.checked === true) {
            $('.show-badge-count', $container).show();
          } else {
            $('.show-badge-count', $container).hide();
          }
        })
        .trigger('change');
      // 获取数量的视图组件
      $('#navTreeGetBadgeCountListViewId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        labelField: 'navTreeGetBadgeCountListViewName',
        valueField: 'navTreeGetBadgeCountListViewId',
        params: {
          wtype: 'wMobileListView',
          uniqueKey: 'id',
          includeWidgetRef: 'true'
        },
        defaultBlank: true,
        remoteSearch: false
      });

      // 绑定导航树目标位置值变化事件
      $("input[name='navTreeTargetPosition'],input[name='targetPosition']", $nav).on('change', function () {
        var navTreeTargetPosition = $("input[name='" + this.name + "']:checked", $nav).val();
        if (navTreeTargetPosition == constant.TARGET_POSITION.TARGET_WIDGET) {
          $('.navTreeTargetWidgetIdRow,.intfNavTreeTargetWidgetIdRow', $nav).show();
        } else {
          $('.navTreeTargetWidgetIdRow,.intfNavTreeTargetWidgetIdRow', $nav).hide();
        }
      });

      // 目标组件ID
      $('#navTreeTargetWidgetId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        labelField: 'navTreeTargetWidgetName',
        valueField: 'navTreeTargetWidgetId',
        params: {
          appPageUuid: appPageUuid,
          uniqueKey: 'id',
          includeWidgetRef: 'true'
        },
        remoteSearch: false
      });

      $('#targetWidgetId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        labelField: 'targetWidgetName',
        valueField: 'targetWidgetId',
        params: {
          appPageUuid: appPageUuid,
          uniqueKey: 'id',
          includeWidgetRef: 'true'
        },
        remoteSearch: false
      });

      // 事件处理
      $('#navTreeEventHanlderName,#eventHanlderName', $container).each(function () {
        $(this).AppEvent({
          ztree: { params: [productUuid] },
          okCallback: function ($el, data) {
            if (data) {
              if ($el.attr('id') === 'eventHanlderName') {
                $('#eventHanlderId', $container).val(data.id);
                $('#eventHanlderType', $container).val(data.data.type);
                $('#eventHanlderPath', $container).val(data.data.path);
                $('#widgetViewName').trigger('refresh');
                // 锚点设置
                $("input[name='eventHashType']", $container).removeAttr('checked');
                $("input[name='eventHashType']", $container).trigger('change');
                if (designCommons.isSupportsAppHashByAppPath(data.data.path)) {
                  $("input[name='eventHashType']", $container).removeAttr('disabled');
                } else {
                  $("input[name='eventHashType']", $container).attr('disabled', 'disabled');
                }
                $("input[name='eventHash']", $container).val('');
                $('#eventHashTree', $container).wCommonComboTree({ value: '' });
                return;
              }
              $('#navTreeEventHanlderId', $container).val(data.id);
              $('#navTreeEventHanlderType', $container).val(data.data.type);
              $('#navTreeEventHanlderPath', $container).val(data.data.path);
              // 锚点设置
              $("input[name='navTreeEventHashType']", $container).removeAttr('checked');
              $("input[name='navTreeEventHashType']", $container).trigger('change');
              if (designCommons.isSupportsAppHashByAppPath(data.data.path)) {
                $("input[name='navTreeEventHashType']", $container).removeAttr('disabled');
              } else {
                $("input[name='navTreeEventHashType']", $container).attr('disabled', 'disabled');
              }
              $("input[name='navTreeEventHash']", $container).val('');
              $('#navTreeEventHashTree', $container).wCommonComboTree({ value: '' });
            }
          },
          clearCallback: function ($el) {
            if ($el.attr('id') === 'eventHanlderName') {
              $('#eventHanlderId,#eventHanlderType,#eventHanlderPath', $container).val('');
              $('#widgetViewName').trigger('refresh');
              // 锚点设置
              $("input[name='eventHashType']", $container).removeAttr('checked');
              $("input[name='eventHashType']", $container).trigger('change');
              $("input[name='eventHashType']", $container).attr('disabled', 'disabled');
              $("input[name='eventHash']", $container).val('');
              $('#eventHashTree', $container).wCommonComboTree({ value: '' });
              return;
            }
            $('#navTreeEventHanlderId,#navTreeEventHanlderType,#navTreeEventHanlderPath', $container).val('');
            // 锚点设置
            $("input[name='navTreeEventHashType']", $container).removeAttr('checked');
            $("input[name='navTreeEventHashType']", $container).trigger('change');
            $("input[name='navTreeEventHashType']", $container).attr('disabled', 'disabled');
            $("input[name='navTreeEventHash']", $container).val('');
            $('#navTreeEventHashTree', $container).wCommonComboTree({ value: '' });
          }
        });
      });
			// $("#navTreeEventHanlderId,#eventHanlderId", $container).wCommonComboTree({
			// 	service : "appProductIntegrationMgr.getTree",
			// 	serviceParams : [ productUuid ],
			// 	width : "330px",
			// 	height : "280px",
			// 	multiSelect : false, // 是否多选
			// 	parentSelect : true, // 父节点选择有效，默认无效
			// 	onAfterSetValue : function(event, self, value) {
			// 		var valueNodes = self.options.valueNodes;
			// 		var appName = [];
			// 		var appType = [];
			// 		var appPath = [];
			// 		if (valueNodes && valueNodes.length > 0) {
			// 			appName.push(valueNodes[0].name);
			// 			appType.push(valueNodes[0].data.type);
			// 			appPath.push(valueNodes[0].data.path);
			// 		}
			// 		if(this.id==='eventHanlderId'){
			// 			$("#eventHanlderName", $container).val(appName.join(","));
			// 			$("#eventHanlderType", $container).val(appType.join(","));
			// 			$("#eventHanlderPath", $container).val(appPath.join(","));
			// 			return;
			// 		}
			// 		$("#navTreeEventHanlderName", $container).val(appName.join(","));
			// 		$("#navTreeEventHanlderType", $container).val(appType.join(","));
			// 		$("#navTreeEventHanlderPath", $container).val(appPath.join(","));
			// 	}
			// });
			// 事件参数
			var $element = $(".event-params", $container);
			builEventParamsBootstrapTable($element, "navTreeEventParams", []);

      // 事件参数
      var $element = $('.event-params', $container);
      builEventParamsBootstrapTable($element, 'navTreeEventParams', []);

      //初始化页面时候，主动触发事件实现数据填充
      if ($("input[name='navType']:checked", $container).val() == NAV_TYPE.INTERFACE) {
        $("input[name='targetPosition']", $nav).trigger('change');
        $("input[name='targetWidgetId']", $nav).trigger('change');
        $('#eventHanlderId', $nav).wCommonComboTree({
          value: configuration.eventHanlderId
        });
      }
      // 导航事件参数
      var $intfEventElement = $('.intf-event-params', $container);
      var eventParameters = configuration.eventParameters || [];
      builEventParamsBootstrapTable($intfEventElement, 'intfEventParams', eventParameters);

      var bean2TreeNodeData = function (bean) {
        var data = {};
        for (var p in bean) {
          if (typeof p === 'string' && StringUtils.contains(p, 'navTree')) {
            data[StringUtils.uncapitalise(p.substring('navTree'.length))] = bean[p];
          } else {
            data[p] = bean[p];
          }
        }
        return data;
      };
      var treeNodeData2Bean = function (data) {
        var bean = {};
        for (var p in data) {
          if (typeof p === 'string') {
            bean['navTree' + StringUtils.capitalise(p)] = data[p];
          } else {
            bean[p] = data[p];
          }
        }
        return bean;
      };
      var treeSetting = {
        edit: {
          drag: {
            autoExpandTrigger: true,
            isCopy: false,
            isMove: true,
            prev: true,
            inner: true,
            next: true
          },
          enable: true,
          showRemoveBtn: false,
          showRenameBtn: false
        },
        view: {
          dblClickExpand: false,
          selectedMulti: false
        },
        data: {
          simpleData: {
            enable: false
          }
        },
        callback: {
          onClick: function (event, treeId, treeNode) {
            clearNavTree(); // 清理配置
            navTreeOpType = NAV_TREE_OP_TYPE.EDIT;
            var bean = treeNodeData2Bean(treeNode.data);
            designCommons.setElementValue(bean, $container);
            var iconClass = $('#navTreeIconSnap', $nav).attr('iconClass');
            $('#navTreeIconSnap', $nav).removeClass(iconClass);
            $('#navTreeIconSnap', $nav).attr('iconClass', bean.navTreeIcon);
            $('#navTreeIconSnap', $nav).addClass(bean.navTreeIcon);
            $("input[name='navTreeShowBadgeCount']", $nav).trigger('change');
            $("input[name='navTreeGetBadgeCountListViewId']", $nav).trigger('change');
            $("input[name='navTreeTargetPosition']", $nav).trigger('change');
            $("input[name='navTreeTargetWidgetId']", $nav).trigger('change');
            $('#navTreeEventHanlderId', $nav).wCommonComboTree({
              value: bean.navTreeEventHanlderId
            });
            // 事件参数
            var $element = $('.event-params', $nav);
            var eventParameters = bean.navTreeEventParameters || [];
            builEventParamsBootstrapTable($element, 'navTreeEventParams', eventParameters);
          }
        }
      };
      // 清空导航树属性设置
      var clearNavTree = function ($container) {
        var data = {
          uuid: '',
          name: '',
          icon: '',
          hidden: false,
          active: false,
          isParent: false,
          showBadgeCount: false,
          getBadgeCountListViewName: '',
          getBadgeCountListViewId: '',
          eventType: '',
          targetPosition: '_self',
          targetWidgetName: '',
          targetWidgetId: '',
          refreshIfExists: false,
          eventHanlderName: '',
          eventHanlderId: '',
          eventHanlderPath: '',
          eventHanlderType: '',
          eventParameters: [],
          eventParams: {}
        };
        var bean = treeNodeData2Bean(data);
        designCommons.setElementValue(bean, $container);
        // 清空图标显示
        var iconClass = $('#navTreeIconSnap', $container).attr('iconClass');
        $('#navTreeIconSnap', $container).removeClass(iconClass);
        // 清空目标窗口
        $("input[name='navTreeTargetPosition']", $container).trigger('change');
      };
      var zTree = $.fn.zTree.init($navTree, treeSetting, zNodes);
      _self.navTree = zTree;
      // 导航界面当前编辑状态
      var navTreeOpType = 0;
      // 检测选择的结点
      var checkSelectedNode = function () {
        var nodes = zTree.getNodes();
        if (nodes.length == 0) {
          return true;
        }
        var selectedNodes = zTree.getSelectedNodes();
        if (selectedNodes.length == 0) {
          appModal.warning('请先选择节点!');
          return false;
        }
        if (selectedNodes.length > 1) {
          appModal.warning('只能选择一个节点!');
          return false;
        }
        return true;
      };
      // 获取选择的结点
      var getSelectedNode = function () {
        var selectedNodes = zTree.getSelectedNodes();
        if (selectedNodes.length == 0) {
          return null;
        }
        return selectedNodes[0];
      };

      // 设置事件
      var zTree = $.fn.zTree.getZTreeObj(navTreeId);
      // 添加同级
      $('#widget_left_sidebar_nav_tree_add').on('click', function () {
        if (!checkSelectedNode()) {
          return;
        }
        clearNavTree();
        var uuid = UUID.createUUID();
        $('#navTreeUuid', $container).val(uuid);
        navTreeOpType = NAV_TREE_OP_TYPE.ADD;
      });
      // 添加子节点
      $('#widget_left_sidebar_nav_tree_add_child').on('click', function () {
        if (!checkSelectedNode()) {
          return;
        }
        clearNavTree();
        var uuid = UUID.createUUID();
        $('#navTreeUuid', $container).val(uuid);
        navTreeOpType = NAV_TREE_OP_TYPE.ADD_CHILD;
      });
      // 删除
      $('#widget_left_sidebar_nav_tree_remove').on('click', function () {
        if (!checkSelectedNode()) {
          return;
        }
        appModal.confirm('确认要删除吗?', function (result) {
          if (result) {
            clearNavTree();
            var selectedNode = getSelectedNode();
            zTree.removeNode(selectedNode);
          }
        });
      });
      // 保存
      $('#widget_left_sidebar_nav_tree_save').on('click', function () {
        if (navTreeOpType == 0) {
          var nodes = zTree.getNodes();
          if (nodes.length != 0) {
            return;
          } else {
            navTreeOpType = NAV_TREE_OP_TYPE.ADD;
          }
        }
        var selectedNode = getSelectedNode();
        if (selectedNode == null && zTree.getNodes().length != -0) {
          return;
        }
        var navTreeParentNode = selectedNode;
        if (navTreeOpType == NAV_TREE_OP_TYPE.ADD) {
          if (selectedNode) {
            navTreeParentNode = selectedNode.getParentNode();
          }
        }

        var navTreeName = $("input[name='navTreeName']", $navTreeNodeProp).val();
        var navTreeUuid = $("input[name='navTreeUuid']", $navTreeNodeProp).val();
        if (StringUtils.isBlank(navTreeName)) {
          appModal.warning('名称不能为空！');
          return;
        }
        var node = {
          id: navTreeUuid,
          name: navTreeName,
          data: {}
        };

        var bean = designCommons.collectConfigurerData($navTreeNodeProp, navTreeCollectClass);
        node.data = bean2TreeNodeData(bean);
        node.data.hidden = Boolean(node.data.hidden);
        node.data.active = Boolean(node.data.active);
        node.data.isParent = Boolean(node.data.isParent);
        node.data.showBadgeCount = Boolean(node.data.showBadgeCount);
        node.data.refreshIfExists = Boolean(node.data.refreshIfExists);

        // 事件参数
        var eventParameters = $navTreeNodeProp.find('#table_navTreeEventParams_info').bootstrapTable('getData');
        node.data.eventParameters = eventParameters || [];
        var eventParams = {};
        $.map(node.data.eventParameters, function (option) {
          eventParams[option.name] = option.value;
        });
        node.data.eventParams = eventParams;

        // 添加/更新节点
        if (navTreeOpType == NAV_TREE_OP_TYPE.ADD || navTreeOpType == NAV_TREE_OP_TYPE.ADD_CHILD) {
          selectedNodes = zTree.addNodes(navTreeParentNode, node);
          zTree.selectNode(selectedNodes[0]);
          navTreeOpType = NAV_TREE_OP_TYPE.EDIT;
        } else if (navTreeOpType == NAV_TREE_OP_TYPE.EDIT) {
          $.extend(selectedNode, node, true);
          zTree.updateNode(selectedNode);
        }

        $.WCommonAlert('保存成功！');
      });

      // 自定义接口
      var $tabelInfo = $('#table_node_info');
      $('#navInterface')
        .wSelect2({
          serviceName: 'treeComponentService',
          queryMethod: 'loadTreeComponent',
          remoteSearch: false
        })
        .on('change', function () {
          var dataProviderClz = $(this).val();
          $tabelInfo.bootstrapTable('removeAll');
          server.JDS.call({
            service: 'treeComponentService.getTreeTypes',
            data: [dataProviderClz],
            async: false,
            success: function (result) {
              if (result.msg == 'success') {
                var rowData = $.map(result.data, function (data) {
                  return {
                    type: data.type,
                    name: data.name,
                    icon: {},
                    disableChecked: '0'
                  };
                });
                $tabelInfo.bootstrapTable('load', rowData);
              }
            }
          });
        });
      var onEditHidden = function (field, row, $el, reason) {
        $el.closest('table').bootstrapTable('resetView');
      };
      $tabelInfo.bootstrapTable('destroy').bootstrapTable({
        data: configuration.nodeTypeInfo,
        idField: 'type',
        striped: true,
        width: 500,
        onEditableHidden: onEditHidden,
        columns: [
          {
            field: 'type',
            title: '类型',
            visible: false
          },
          {
            field: 'name',
            title: '类型名称'
          },
          {
            field: 'icon',
            title: '图标',
            visible: false,
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'bottom',
              savenochange: true,
              value2input: designCommons.bootstrapTable.icon.value2input,
              input2value: designCommons.bootstrapTable.icon.input2value,
              value2display: designCommons.bootstrapTable.icon.value2display,
              value2html: designCommons.bootstrapTable.icon.value2html
            }
          },
          {
            field: 'disableChecked',
            title: '禁止选择',
            editable: {
              type: 'select',
              mode: 'inline',
              showbuttons: false,
              source: [
                {
                  value: '1',
                  text: '是'
                },
                {
                  value: '0',
                  text: '否'
                }
              ]
            }
          }
        ]
      });
    };
    configurer.prototype.onOk = function ($container) {
      this.component.options.configuration = this.collectConfiguration($container);
    };
    // 收集配置信息
    configurer.prototype.collectConfiguration = function ($container) {
      var configuration = {};
      // 基本信息
      this.collectBaseInfo(configuration, $container);
      // 导航信息
      this.collectNavInfo(configuration, $container);
      return $.extend({}, configuration);
    };
    configurer.prototype.collectBaseInfo = function (configuration, $container) {
      var $form = $('#widget_left_sidebar_tabs_base_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.isShowRoot = Boolean(opt.isShowRoot);
      opt.autoToggle = Boolean(opt.autoToggle);
      opt.syncMenu = Boolean(opt.syncMenu);
      $.extend(configuration, opt);
    };
    configurer.prototype.collectNavInfo = function (configuration, $container) {
      var _self = this;
      var $form = $('#widget_left_sidebar_tabs_nav_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      configuration.nav = _self.navTree.getNodes();
      opt.nodeTypeInfo = $('#table_node_info').bootstrapTable('getData');
      // 导航事件参数
      var eventParameters = $('#table_intfEventParams_info', $container).bootstrapTable('getData');
      configuration.eventParameters = eventParameters;
      var eventParams = {};
      $.map(eventParameters, function (option) {
        eventParams[option.name] = option.value;
      });
      configuration.eventParams = eventParams;
      $.extend(configuration, opt);
    };
    return configurer;
  };

  var builEventParamsBootstrapTable = function ($element, name, data) {
    $element.bootstrapTable('destroy');
    $($element).html('');
    formBuilder.bootstrapTable.build({
      container: $element,
      name: name,
      ediableNest: true,
      table: {
        data: data,
        striped: true,
        idField: 'uuid',
        columns: [
          {
            field: 'checked',
            formatter: designCommons.checkedFormat,
            checkbox: true
          },
          {
            field: 'uuid',
            title: 'UUID',
            visible: false,
            editable: {
              type: 'text',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline'
            }
          },
          {
            field: 'text',
            title: '参数名称',
            editable: {
              type: 'text',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline'
            }
          },
          {
            field: 'name',
            title: '参数名',
            editable: {
              type: 'text',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline'
            }
          },
          {
            field: 'value',
            title: '参数值',
            editable: {
              type: 'text',
              showbuttons: false,
              onblur: 'submit',
              mode: 'inline'
            }
          }
        ]
      }
    });
  };

  // 返回组件定义
  component.prototype.getDefinitionJson = function () {
    var options = this.options;
    var id = this.getId();
    options.id = id;
    return options;
  };
  return component;
});
