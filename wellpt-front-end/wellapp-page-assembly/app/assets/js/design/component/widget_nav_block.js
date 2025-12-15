define(['ui_component', 'constant', 'commons', 'formBuilder', 'appContext', 'design_commons'], function (
  ui_component,
  constant,
  commons,
  formBuilder,
  appContext,
  designCommons
) {
  var component = $.ui.component.BaseComponent();

  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var UUID = commons.UUID;
  var NAV_TREE_OP_TYPE = {
    // 导航树操作类型
    ADD: 1, // 增加同级
    ADD_CHILD: 2, // 增加子级
    EDIT: 3 // 修改
  };
  component.prototype.create = function () {};
  // 使用属性配置器
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  // 返回属性配置器
  component.prototype.getPropertyConfigurer = function () {
    var collectClass = 'w-configurer-option';
    var configurer = $.ui.component.BaseComponentConfigurer();
    var navTreeCollectClass = 'w-nav-tree-option';
    configurer.prototype.onLoad = function ($container, options) {
      // 初始化页签项
      $('#widget_header_tabs ul a', $container).on('click', function (e) {
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
      //自定义导航
      this.initCustomNavbar(configuration, $container);
    };
    configurer.prototype.initBaseInfo = function (configuration, $container) {
      // 设置值
      designCommons.setElementValue(configuration, $container);
      $('#contentMaxNum', $container).attr('value', configuration.contentMaxNum);

      // 二开JS模块
      $('#jsModule', $container).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        params: {
          dependencyFilter: 'HeaderWidgetDevelopment'
        },
        labelField: 'jsModuleName',
        valueField: 'jsModule',
        remoteSearch: false,
        multiple: true
      });
    };
    configurer.prototype.initCustomNavbar = function (configuration, $container) {
      var _self = this;
      // 设置值
      designCommons.setElementValue(configuration, $container);

      var appPageUuid = _self.component.pageDesigner.getPageUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;

      var $nav = $('#widget_custom_navbar_info');
      var navTreeId = 'widget_navbar_tree';
      var $navTree = $('#' + navTreeId);
      var $navTreeNodeProp = $('#widget_navbar_tree_node_prop');
      // 初始化自定义导航数据
      var zNodes = [];
      if (configuration && configuration.nav) {
        zNodes = configuration.nav;
      }
      // 选择图标
      $('#navTreeIconSelectBtn', $nav).on('click', function () {
        $.WCommonPictureLib.show({
          selectTypes: [3],
          confirm: function (data) {
            var fileIDs = data.fileIDs;
            $('#navTreeIcon').val(fileIDs);
            $('#navTreeIconSnap', $nav).attr('iconClass', fileIDs);
            $('#navTreeIconSnap', $nav).attr('class', fileIDs);
            if (data) $('#navTreeIconRemoveBtn', $container).show();
          }
        });
      });

      // 移除图标
      $('#navTreeIconRemoveBtn', $container).on('click', function () {
        $('#navTreeIcon').val('');
        $('#navTreeIconSnap', $container).attr('iconClass', '');
        $('#navTreeIconSnap', $container).attr('class', '');
        $('#navTreeIconRemoveBtn', $container).hide();
      });
      // 显示标签数量的视图组件选择控制
      $('#navTreeIsShowBadge', $nav)
        .on('change', function () {
          if (this.checked === true) {
            $('.navTreeBadgeType_select', $nav).show();
            $('#navTreeBadgeType', $nav).trigger('change');
          } else {
            $('.showBadge', $nav).hide();
          }
        })
        .trigger('click');

      $('#navTreeBadgeType', $nav).wSelect2({
        valueField: 'navTreeBadgeType',
        remoteSearch: false,
        data: [
          { id: 'countJs', text: '通过数量统计JS脚本' },
          { id: 'datastore', text: '通过数据仓库获取数量' }
        ]
      });

      $('#navTreeBadgeType', $nav).on('change', function () {
        $('.navTreeBadgeType', $nav).hide();
        var typeValue = $(this).val();
        $('.navTreeBadgeType' + '_' + typeValue).show();
      });

      $('#navTreeBadgeTypeCountJsWBootstrapTable', $nav).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        valueField: 'navTreeBadgeTypeCountJsWBootstrapTable',
        params: {
          wtype: 'wBootstrapTable',
          uniqueKey: 'id',
          includeWidgetRef: 'true'
        },
        remoteSearch: false
      });

      $('#navTreeBadgeTypeCountJs', $nav).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        params: {
          dependencyFilter: 'GetCountBase'
        },
        valueField: 'navTreeBadgeTypeCountJs',
        defaultBlank: true,
        remoteSearch: false
      });

      $('#navTreeBadgeTypeCountJs', $nav).on('change', function () {
        $('.navTreeBadgeType_countJs[js]', $nav).hide();
        if ($(this).val() === 'BootstrapTableViewGetCount') {
          $(".navTreeBadgeType_countJs[js='BootstrapTableViewGetCount']", $nav).show();
        }
      });

      $('#navTreeBadgeTypeCountDs', $nav).wSelect2({
        serviceName: 'viewComponentService',
        queryMethod: 'loadSelectData',
        valueField: 'navTreeBadgeTypeCountDs',
        defaultBlank: true,
        remoteSearch: false,
        params: {
          piUuid: _self.component.pageDesigner.getPiUuid()
        }
      });
      // 绑定导航树目标位置值变化事件
      $("input[name='navTreeTargetPosition'],input[name='targetPosition']", $nav)
        .on('change', function () {
          var navTreeTargetPosition = $("input[name='" + this.name + "']:checked", $nav).val();
          if (navTreeTargetPosition == constant.TARGET_POSITION.TARGET_WIDGET) {
            $('.navTreeTargetWidgetIdRow,.intfNavTreeTargetWidgetIdRow', $nav).show();
          } else {
            $('.navTreeTargetWidgetIdRow,.intfNavTreeTargetWidgetIdRow', $nav).hide();
          }
        })
        .trigger('change');

      // 目标组件ID
      $('#navTreeTargetWidgetId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        queryMethod: 'loadLayoutSelectData',
        selectionMethod: 'loadLayoutSelectDataByIds',
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
        queryMethod: 'loadLayoutSelectData',
        selectionMethod: 'loadLayoutSelectDataByIds',
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
      $('#navTreeEventHanlderId,#eventHanlderId', $container).wCommonComboTree({
        service: 'appProductIntegrationMgr.getTreeWithPtProduct',
        serviceParams: [productUuid],
        width: '330px',
        height: '280px',
        multiSelect: false, // 是否多选
        parentSelect: true, // 父节点选择有效，默认无效
        onAfterSetValue: function (event, self, value) {
          var valueNodes = self.options.valueNodes;
          var appName = [];
          var appType = [];
          var appPath = [];
          if (valueNodes && valueNodes.length > 0) {
            appName.push(valueNodes[0].name);
            appType.push(valueNodes[0].data.type);
            appPath.push(valueNodes[0].data.path);
          }
          if (this.id === 'eventHanlderId') {
            $('#eventHanlderName', $container).val(appName.join(','));
            $('#eventHanlderType', $container).val(appType.join(','));
            $('#eventHanlderPath', $container).val(appPath.join(','));
            $('#widgetViewName').trigger('refresh');
            return;
          }
          $('#navTreeEventHanlderName', $container).val(appName.join(','));
          $('#navTreeEventHanlderType', $container).val(appType.join(','));
          $('#navTreeEventHanlderPath', $container).val(appPath.join(','));
        }
      });

      $('.src-server-params table', $container).on('initSrcServerParameters', function (e) {
        var $table = $('.src-server-params table', $container);
        $table.bootstrapTable('removeAll');
      });
      if (!configuration.srcServerParameters || configuration.srcServerParameters.length === 0) {
        $('.src-server-params table', $container).trigger('initSrcServerParameters');
      }

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
            if (treeNode.level) {
              $('.nav-block-prop-content', $nav).show();
            } else {
              $('.nav-block-prop-content', $nav).hide();
            }
            navTreeOpType = NAV_TREE_OP_TYPE.EDIT;
            var bean = treeNodeData2Bean(treeNode.data);
            designCommons.setElementValue(bean, $nav);
            var iconClass = $('#navTreeIconSnap', $nav).attr('iconClass');
            $('#navTreeIconSnap', $nav).removeClass(iconClass).addClass(bean.navTreeIcon).attr('iconClass', bean.navTreeIcon);
            if (bean.navTreeIcon) {
              $('#navTreeIconRemoveBtn', $container).show();
            } else {
              $('#navTreeIconRemoveBtn', $container).hide();
            }
            $("input[name='navTreeShowBadgeCount']", $nav).trigger('change');
            $("input[name='navTreeGetBadgeCountListViewId']", $nav).trigger('change');
            $("input[name='navTreeTargetPosition']", $nav).trigger('change');
            $("input[name='navTreeTargetWidgetId']", $nav).trigger('change');
            $("input[name='navTreeIsShow']", $nav).trigger('change');
            $('#navTreeEventHanlderId', $nav).val(bean.navTreeEventHanlderId);
            $('#navTreeEventHanlderName', $nav).val(bean.navTreeEventHanlderName);
            $('#navTreeEventHanlderName', $nav).AppEvent('setValue', bean.navTreeEventHanlderId);
            $("input[name='navTreeEventHashType']", $nav).trigger('change');
            if (designCommons.isSupportsAppHashByAppPath(bean.navTreeEventHanlderPath)) {
              $("input[name='navTreeEventHashType']", $nav).removeAttr('disabled');
            } else {
              $("input[name='navTreeEventHashType']", $nav).attr('disabled', 'disabled');
            }
            $('#navTreeEventHashTree', $nav).wCommonComboTree({
              value: bean.navTreeEventHash
            });

            $(
              '#navTreeIsShowBadge,#navTreeBadgeType,#navTreeBadgeTypeCountJs,' +
                '#navTreeBadgeTypeCountDs,#navTreeBadgeTypeCountJsWBootstrapTable',
              $nav
            ).trigger('change');
            // 事件参数
            var $eventParams = $('.event-params', $nav);
            var eventParameters = bean.navTreeEventParameters || [];
            builEventParamsBootstrapTable($eventParams, 'navTreeEventParams', eventParameters);
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
          eventType: 'click',
          targetPosition: '_blank',
          targetWidgetName: '',
          targetWidgetId: '',
          refreshIfExists: false,
          eventHanlderName: '',
          eventHanlderId: '',
          eventHanlderPath: '',
          eventHanlderType: '',
          eventHashType: '',
          eventHash: '',
          eventParameters: [],
          eventParams: {},
          isShowBadge: false,
          badgeType: '',
          badgeTypeCountDs: '',
          badgeTypeCountJs: '',
          badgeTypeCountJsWBootstrapTable: ''
        };
        var bean = treeNodeData2Bean(data);
        designCommons.setElementValue(bean, $container);
        // 清空图标显示
        var iconClass = $('#navTreeIconSnap', $container).attr('iconClass');
        $('#navTreeIconSnap', $container).removeClass(iconClass);
        // 清空默认显示
        $('#navTreeIsShow', $container).attr('checked', false);
        // 清空目标窗口
        $("input[name='navTreeTargetPosition']", $container).trigger('change');
        // 清空事件参数表格数据
        var $eventParams = $('.event-params', $container);
        builEventParamsBootstrapTable($eventParams, 'navTreeEventParams', []);
      };
      var zTree = $.fn.zTree.init($navTree, treeSetting, zNodes);
      _self.navTree = zTree;
      // 导航界面当前编辑状态
      var navTreeOpType = 0;
      // 检测选择的结点
      var checkSelectedNode = function (type) {
        var nodes = zTree.getNodes();
        if (nodes.length === 0 || type === 'type') {
          return true;
        }
        var selectedNodes = zTree.getSelectedNodes();
        if (selectedNodes.length === 0) {
          appModal.warning('请先选择节点!');
          return false;
        }
        if (selectedNodes.length > 1) {
          appModal.warning('只能选择一个节点!');
          return false;
        }
        if (type !== 'same') {
          if (selectedNodes[0].level) {
            appModal.warning('请选择第一级节点!');
            return false;
          }
        } else {
          if (selectedNodes[0].level) {
            $('.nav-block-prop-content', $nav).show();
          } else {
            $('.nav-block-prop-content', $nav).hide();
          }
        }

        return true;
      };
      // 获取选择的结点
      var getSelectedNode = function () {
        var selectedNodes = zTree.getSelectedNodes();
        if (selectedNodes.length === 0) {
          return null;
        }
        return selectedNodes[0];
      };

      // 设置事件
      var zTree = $.fn.zTree.getZTreeObj(navTreeId);

      $('#navTreeEventHanlderName', $nav).AppEvent({
        ztree: { params: [productUuid] },
        okCallback: function ($el, data) {
          if (data) {
            $('#navTreeEventHanlderId', $nav).val(data.id);
            $('#navTreeEventHanlderType', $nav).val(data.data.type);
            $('#navTreeEventHanlderPath', $nav).val(data.data.path);
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
          $('#navTreeEventHanlderId,#navTreeEventHanlderType,#navTreeEventHanlderPath', $container).val('');
          // 锚点设置
          $("input[name='navTreeEventHashType']", $container).removeAttr('checked');
          $("input[name='navTreeEventHashType']", $container).trigger('change');
          $("input[name='navTreeEventHashType']", $container).attr('disabled', 'disabled');
          $("input[name='navTreeEventHash']", $container).val('');
          $('#navTreeEventHashTree', $container).wCommonComboTree({ value: '' });
        }
      });
      // 锚点设置单选框取消选中
      $("input[name='navTreeEventHashType']", $nav)
        .parent()
        .on('mouseup', function () {
          var $radio = $(this).find('input');
          if ($radio.attr('checked') == 'checked') {
            setTimeout(function () {
              $radio.removeAttr('checked');
              $radio.trigger('change');
            }, 1);
          }
        });
      // 锚点设置值变化事件
      $("input[name='navTreeEventHashType']", $nav)
        .on('change', function () {
          var navTreeEventHashType = $("input[name='" + this.name + "']:checked", $nav).val();
          $('.navTreeEventHashTypeRow', $nav).hide();
          if (navTreeEventHashType == '1') {
            $('.navTreeEventHashType' + navTreeEventHashType, $nav).show();
            // 事件处理
            $('#navTreeEventHashTree', $nav).wCommonComboTree({
              service: 'pageDefinitionService.getAppHashTreeByAppPath',
              serviceParams: [$('#navTreeEventHanlderPath', $nav).val()],
              multiSelect: false, // 是否多选
              parentSelect: false, // 父节点选择有效，默认无效
              onAfterSetValue: function (event, self, value) {
                $('#navTreeEventHash', $nav).val(value);
                var valueNodes = self.options.valueNodes;
                if (valueNodes && valueNodes.length == 1) {
                  var parantNode = valueNodes[0].getParentNode();
                  if (parantNode) {
                    $(this).val('/' + parantNode.name + '/' + valueNodes[0].name);
                  } else {
                    $(this).val('/' + valueNodes[0].name);
                  }
                }
              }
            });
          } else if (navTreeEventHashType == '2') {
            $('.navTreeEventHashType' + navTreeEventHashType, $nav).show();
          }
        })
        .trigger('change');

      // 添加类型
      $('#widget_navbar_tree_add').on('click', function () {
        clearNavTree();
        var uuid = UUID.createUUID();
        $('#navTreeUuid', $container).val(uuid);
        navTreeOpType = 0;
        $('.nav-block-prop-content', $container).hide();
      });
      // 添加同级
      $('#widget_navbar_tree_add_same').on('click', function () {
        if (!checkSelectedNode('same')) {
          return;
        }
        clearNavTree();
        var uuid = UUID.createUUID();
        $('#navTreeUuid', $container).val(uuid);
        navTreeOpType = NAV_TREE_OP_TYPE.ADD;
      });
      // 添加内容
      $('#widget_navbar_tree_add_child').on('click', function () {
        var selectedNodes = zTree.getSelectedNodes();
        if (selectedNodes.length === 0) {
          var warningTip = zTree.getNodes().length ? '请先选择或者添加类型!' : '请先添加类型!';
          appModal.warning(warningTip);
          return false;
        }
        if (!checkSelectedNode()) {
          return;
        }
        clearNavTree();
        var uuid = UUID.createUUID();
        $('#navTreeUuid', $container).val(uuid);
        navTreeOpType = NAV_TREE_OP_TYPE.ADD_CHILD;
        $('.nav-block-prop-content', $container).show();
      });
      // 删除
      $('#widget_navbar_tree_remove').on('click', function () {
        var selectedNodes = zTree.getSelectedNodes();
        if (selectedNodes.length === 0) {
          appModal.warning('未选择要删除的节点！');
          return false;
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
      $('#widget_navbar_tree_save').on('click', function () {
        var selectedNode = getSelectedNode();

        if (!selectedNode && zTree.getNodes().length && navTreeOpType > 0) {
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

        // 锚点设置验证
        if (StringUtils.isNotBlank(node.data.eventHashType) && StringUtils.isBlank(node.data.eventHash)) {
          appModal.warning('锚点设置不能为空！');
          return;
        }

        // 事件参数
        var eventParameters = $navTreeNodeProp.find('#table_navTreeEventParams_info').length
          ? $navTreeNodeProp.find('#table_navTreeEventParams_info').bootstrapTable('getData')
          : [];
        node.data.eventParameters = eventParameters;
        var eventParams = {};
        $.map(node.data.eventParameters, function (option) {
          eventParams[option.name] = option.value;
        });
        node.data.eventParams = eventParams;

        // 添加/更新节点
        if (navTreeOpType === 0 || navTreeOpType == NAV_TREE_OP_TYPE.ADD || navTreeOpType == NAV_TREE_OP_TYPE.ADD_CHILD) {
          var selectedNodes = zTree.addNodes(navTreeParentNode, node);
          zTree.selectNode(selectedNodes[0]);
          navTreeOpType = NAV_TREE_OP_TYPE.EDIT;
        } else if (navTreeOpType == NAV_TREE_OP_TYPE.EDIT) {
          $.extend(selectedNode, node, true);
          zTree.updateNode(selectedNode);
        }

        appModal.success('保存成功！');
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
      //自定义导航条
      this.collectCustomNavbarInfo(configuration, $container);
      return $.extend({}, configuration);
    };
    configurer.prototype.collectBaseInfo = function (configuration, $container) {
      var $form = $('#widget_header_tabs_base_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.subNavAndToolBarHidden = Boolean(opt.subNavAndToolBarHidden);
      $.extend(configuration, opt);
    };
    configurer.prototype.collectCustomNavbarInfo = function (configuration, $container) {
      var _self = this;
      var $form = $('#widget_custom_navbar_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      configuration.nav = _self.navTree.getNodes();
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

  // 返回组件定义JSON
  component.prototype.getDefinitionJson = function () {
    var options = this.options;
    var id = this.getId();
    options.id = id;
    return options;
  };
  return component;
});
