define([
  'ui_component',
  'constant',
  'commons',
  'server',
  'appContext',
  'design_commons',
  'appModal',
  'formBuilder',
  'ace_code_editor',
  'ace_ext_language_tools'
], function (ui_component, constant, commons, server, appContext, designCommons, appModal, formBuilder) {
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
    INTERFACE: 3, // 自定义
    DATASTORE: 4 //数据仓库
  };

  // 徽章数量获取方式
  var BADGE_WAY = [
    {
      id: 'tableWidgetCount',
      text: '通过表格视图获取数量',
      $id: 'navTreeGetBadgeCountListViewId',
      $valiTxt: '请选择统计数量的表格视图'
    },
    {
      id: 'countJs',
      text: '通过数据统计JS脚本',
      $id: 'navTreeGetBadgeCountJs',
      $valiTxt: '请选择统计数量的JS脚本'
    },
    {
      id: 'dataStoreCount',
      text: '通过数据仓库获取数量',
      $id: 'navTreeGetBadgeCountDataStore',
      $valiTxt: '请选择统计数量的数据仓库'
    }
  ];

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
      //导航脚本事件信息
      this.initNavJsEventCodeInfo(configuration, $container);
    };
    configurer.prototype.initBaseInfo = function (configuration, $container) {
      // 设置值
      designCommons.setElementValue(configuration, $container);
      // 设置导航为伸缩式导航
      $("input[name='isTelescopicNav']", $container)
        .on('change', function () {
          if ($(this).is(':checked')) {
            $(this).parent().next().show();
          } else {
            $(this).parent().next().hide();
            $(this).parent().next().find('input').prop('checked', false);
          }
        })
        .trigger('change');
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
            $('.navDataStoreRowConfig', $container).hide();
            $('.navInterfaceRowConfig', $container).show();
          } else if (navType == NAV_TYPE.DATASTORE) {
            $('.navResourceRow', $container).hide();
            $('.navDataRow', $container).hide();
            $('.navInterfaceRow', $container).show();
            $('.navInterfaceRowConfig', $container).hide();
            $('.navDataStoreRowConfig', $container).show();
          }
        })
        .trigger('change');

      // 初始化系统集成导航下拉树
      $("input[name='navResource']", $nav).wCommonComboTree({
        service: 'appProductIntegrationMgr.getTreeByProductUuidAndDataType',
        serviceParams: [productUuid, [1, 2, 3]],
        width: '690px',
        multiSelect: false, // 是否多选
        parentSelect: true
        // 父节点选择有效，默认无效
      });

      // 初始化自定义导航数据
      var zNodes = [];
      if (configuration && configuration.nav) {
        zNodes = configuration.nav;
      }
      // 选择图标
      $('#navTreeIconSelectBtn', $container).on('click', function () {
        $.WCommonPictureLib.show({
          selectTypes: [3],
          selectIconclass: $('#navTreeIconSnap').attr('iconClass'),
          confirm: function (data) {
            var fileIDs = data.fileIDs;
            $('#navTreeIcon').val(fileIDs);
            $('#navTreeIconSnap', $container).attr('iconClass', fileIDs);
            $('#navTreeIconSnap', $container).attr('class', fileIDs);
          }
        });
      });

      // 选择图标
      $('#navTreeIconDelBtn', $container).on('click', function () {
        $('#navTreeIcon').val('');
        $('#navTreeIconSnap', $container).attr('iconClass', '');
        $('#navTreeIconSnap', $container).attr('class', '');
      });

      // 绑定导航树目标位置值变化事件
      $("input[name='navTreeTargetPosition'],input[name='targetPosition']", $nav)
        .on('change', function () {
          var navTreeTargetPosition = $("input[name='" + this.name + "']:checked", $nav).val();
          if (navTreeTargetPosition == constant.TARGET_POSITION.TARGET_WIDGET) {
            if (this.name === 'navTreeTargetPosition') {
              $('.navTreeTargetWidget', $nav).show();
              $('#navTreeTargetWidgetSelectorType', $nav).trigger('change');
            } else {
              $('.targetWidget', $nav).show();
              $('#targetWidgetSelectorType', $nav).trigger('change');
            }
          } else {
            if (this.name === 'navTreeTargetPosition') {
              $('.navTreeTargetWidget', $nav).hide();
            } else {
              $('.targetWidget', $nav).hide();
            }
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

      $('#navTreeTargetWidgetSelectorType', $container)
        .wSelect2({
          data: [
            {
              id: '1',
              text: '#id选择器'
            },
            {
              id: '2',
              text: '.class选择器'
            }
          ],
          labelField: 'navTreeTargetWidgetSelectorTypeName',
          valueField: 'navTreeTargetWidgetSelectorType',
          defaultBlank: true,
          remoteSearch: false
        })
        .on('change', function () {
          var _type = $(this).val();
          if (_type === '1') {
            $('.navTreeTargetWidgetIdRow', $nav).show();
            $('.navTreeTargetWidgetCssClassRow', $nav).hide();
          } else if (_type === '2') {
            $('.navTreeTargetWidgetIdRow', $nav).hide();
            $('.navTreeTargetWidgetCssClassRow', $nav).show();
          } else {
            $('.navTreeTargetWidgetIdRow', $nav).hide();
            $('.navTreeTargetWidgetCssClassRow', $nav).hide();
          }
        });

      $('#targetWidgetSelectorType', $container)
        .wSelect2({
          data: [
            {
              id: '1',
              text: '#id选择器'
            },
            {
              id: '2',
              text: '.class选择器'
            }
          ],
          labelField: 'targetWidgetSelectorTypeName',
          valueField: 'targetWidgetSelectorType',
          defaultBlank: true,
          remoteSearch: false
        })
        .on('change', function () {
          var _type = $(this).val();
          if (_type === '1') {
            $('.intfNavTreeTargetWidgetIdRow', $nav).show();
            $('.intfNavTreeTargetWidgetCssClassRow', $nav).hide();
          } else if (_type === '2') {
            $('.intfNavTreeTargetWidgetIdRow', $nav).hide();
            $('.intfNavTreeTargetWidgetCssClassRow', $nav).show();
          } else {
            $('.intfNavTreeTargetWidgetIdRow', $nav).hide();
            $('.intfNavTreeTargetWidgetCssClassRow', $nav).hide();
          }
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

      $('#navTreeEventHanlderName,#eventHanlderName', $container).each(function () {
        $(this).AppEvent({
          ztree: {
            params: [productUuid]
          },
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
                $('#eventHashTree', $container).wCommonComboTree({
                  value: ''
                });
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
              $('#navTreeEventHashTree', $container).wCommonComboTree({
                value: ''
              });
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
              $('#eventHashTree', $container).wCommonComboTree({
                value: ''
              });
              return;
            }
            $('#navTreeEventHanlderId,#navTreeEventHanlderType,#navTreeEventHanlderPath', $container).val('');
            // 锚点设置
            $("input[name='navTreeEventHashType']", $container).removeAttr('checked');
            $("input[name='navTreeEventHashType']", $container).trigger('change');
            $("input[name='navTreeEventHashType']", $container).attr('disabled', 'disabled');
            $("input[name='navTreeEventHash']", $container).val('');
            $('#navTreeEventHashTree', $container).wCommonComboTree({
              value: ''
            });
          }
        });
      });
      // 锚点设置单选框取消选中
      $("input[name='navTreeEventHashType'], input[name='eventHashType']", $nav)
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
      // 锚点设置值变化事件
      $("input[name='eventHashType']", $nav)
        .on('change', function () {
          var eventHashType = $("input[name='" + this.name + "']:checked", $nav).val();
          $('.eventHashTypeRow', $nav).hide();
          if (eventHashType == '1') {
            $('.eventHashType' + eventHashType, $nav).show();
            // 事件处理
            $('#eventHashTree', $nav).wCommonComboTree({
              service: 'pageDefinitionService.getAppHashTreeByAppPath',
              serviceParams: [$('#eventHanlderPath', $nav).val()],
              multiSelect: false, // 是否多选
              parentSelect: false, // 父节点选择有效，默认无效
              onAfterSetValue: function (event, self, value) {
                $('#eventHash', $nav).val(value);
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
          } else if (eventHashType == '2') {
            $('.eventHashType' + eventHashType, $nav).show();
          }
        })
        .trigger('change');

      // 事件处理
      /*$("#navTreeEventHanlderId,#eventHanlderId", $container).wCommonComboTree({
                service: "appProductIntegrationMgr.getTreeWithPtProduct",
                serviceParams: [productUuid],
                width: "330px",
                height: "280px",
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
                        $("#eventHanlderName", $container).val(appName.join(","));
                        $("#eventHanlderType", $container).val(appType.join(","));
                        $("#eventHanlderPath", $container).val(appPath.join(","));
                        $("#widgetViewName").trigger('refresh');
                        return;
                    }
                    $("#navTreeEventHanlderName", $container).val(appName.join(","));
                    $("#navTreeEventHanlderType", $container).val(appType.join(","));
                    $("#navTreeEventHanlderPath", $container).val(appPath.join(","));
                }
            });*/

      // 事件参数
      var $element = $('.event-params', $container);
      builEventParamsBootstrapTable($element, 'navTreeEventParams', []);

      //初始化页面时候，主动触发事件实现数据填充
      if ($("input[name='navType']:checked", $container).val() == NAV_TYPE.INTERFACE) {
        $("input[name='targetPosition']", $nav).trigger('change');
        $("input[name='targetWidgetId']", $nav).trigger('change');
        $('#eventHanlderName', $nav).val(configuration.eventHanlderName);
        $('#eventHanlderId', $nav).val(configuration.eventHanlderId);
      }

      // 导航事件参数
      var $intfEventElement = $('.intf-event-params', $container);
      var eventParameters = configuration.eventParameters || [];
      builEventParamsBootstrapTable($intfEventElement, 'intfEventParams', eventParameters);

      // 接口参数
      builEventParamsBootstrapTable($('.src-server-params', $container), 'srcServerParams', configuration.srcServerParameters || []);

      $('.src-server-params table', $container).on('initSrcServerParameters', function (e) {
        var $table = $('.src-server-params table', $container);
        $table.bootstrapTable('removeAll');
      });
      if (!configuration.srcServerParameters || configuration.srcServerParameters.length == 0) {
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
            navTreeOpType = NAV_TREE_OP_TYPE.EDIT;
            var bean = treeNodeData2Bean(treeNode.data);
            //兼容旧代码没有徽章数量方式
            if (!bean.getBadgeCountWay && bean.getBadgeCountListViewId) {
              bean.getBadgeCountWay = 'tableWidgetCount';
            }
            if (!bean.navTreeGetBadgeCountWay) {
              bean.navTreeGetBadgeCountWay = 'tableWidgetCount';
            }
            if (bean.navTreeTargetWidgetId && !bean.navTreeTargetWidgetSelectorType) {
              bean.navTreeTargetWidgetSelectorType = '1';
              bean.navTreeTargetWidgetSelectorTypeName = '#id选择器';
            }
            designCommons.setElementValue(bean, $container);
            var iconClass = $('#navTreeIconSnap', $nav).attr('iconClass');
            $('#navTreeIconSnap', $nav).removeClass(iconClass);
            $('#navTreeIconSnap', $nav).attr('iconClass', bean.navTreeIcon);
            $('#navTreeIconSnap', $nav).addClass(bean.navTreeIcon);
            $("input[name='navTreeShowBadgeCount']", $nav).trigger('change');
            $("input[name='navTreeGetBadgeCountListViewId']", $nav).trigger('change');
            if (bean.navTreeGetBadgeDigit == 'defined') {
              //位数自定义
              if (bean.navTreeGetBadgeDigitNumber) {
                $('li[data-digit=' + bean.navTreeGetBadgeDigitNumber + ']', '.youshangjiao-xuanzhong-ul', $nav).trigger('click');
              }
            } else {
              // 初始化时，位数使用系统默认时，清除自定义选中
              $('#navTreeGetBadgeDigit_default', $container).trigger('click');
              $('#navTreeGetBadgeDigitNumber', $nav).val('');
              $('li', '.youshangjiao-xuanzhong-ul', $container).removeClass('active');
            }
            $("input[name='navTreeTargetPosition']", $nav).trigger('change');
            $("input[name='navTreeTargetWidgetSelectorType']", $nav).trigger('change');
            $("input[name='navTreeTargetWidgetId']", $nav).trigger('change');
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
            // 事件参数
            var $element = $('.event-params', $nav);
            var eventParameters = bean.navTreeEventParameters || [];
            builEventParamsBootstrapTable($element, 'navTreeEventParams', eventParameters);
          },
          onDragMove: function (treeId, treeNode) {
            $('.zTreeDragUL').appendTo($('.bootbox'));
            $('.tmpzTreeMove_arrow').appendTo($('.bootbox'));
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
          getBadgeCountJs: '',
          getBadgeCountJsTable: '',
          getBadgeCountListViewName: '',
          getBadgeCountListViewId: '',
          getBadgeCountDataStore: '',
          getBadgeCountWay: '',
          getBadgeDigit: 'default',
          getBadgeDigitNumber: '',
          getBadgeZero: 'default',
          eventType: '',
          targetPosition: '_self',
          targetWidgetName: '',
          targetWidgetId: '',
          targetWidgetSelectorTypeName: '',
          targetWidgetSelectorType: '',
          targetWidgetCssClass: '',
          refreshIfExists: false,
          eventHanlderName: '',
          eventHanlderId: '',
          eventHanlderPath: '',
          eventHanlderType: '',
          eventHashType: '',
          eventHash: '',
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
        $('#navTreeEventHanlderId,#navTreeEventHanlderName', $container).val('');
        $("input[name='navTreeShowBadgeCount']", $nav).trigger('change');
        $('li', '.youshangjiao-xuanzhong-ul', $container).removeClass('active');
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
          appModal.warning('请先选择节点！');
          return false;
        }
        if (selectedNodes.length > 1) {
          appModal.warning('只能选择一个节点！');
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
        var selectedNode = getSelectedNode();
        if (navTreeOpType == 0) {
          var nodes = zTree.getNodes();

          if (nodes.length != 0) {
            if (selectedNode == null) {
              appModal.warning('请添加或选择节点');
              return;
            }
          } else {
            navTreeOpType = NAV_TREE_OP_TYPE.ADD;
          }
        }

        if (selectedNode == null && zTree.getNodes().length != 0) {
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
        if (!navTreeUuid) {
          navTreeUuid = UUID.createUUID();
          $("input[name='navTreeUuid']", $navTreeNodeProp).val(navTreeUuid);
        }
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
        var validateBadgeInput = _self.validateBadgeInput(bean);
        if (!validateBadgeInput) {
          //验证失败
          return false;
        }
        node.data = bean2TreeNodeData(bean);
        node.data.hidden = Boolean(node.data.hidden);
        node.data.active = Boolean(node.data.active);
        node.data.isParent = Boolean(node.data.isParent);
        node.data.showBadgeCount = node.data.showBadgeCount.length == 1 ? Boolean(node.data.showBadgeCount[0]) : false;
        node.data.refreshIfExists = Boolean(node.data.refreshIfExists);
        // 锚点设置验证
        if (StringUtils.isNotBlank(node.data.eventHashType) && StringUtils.isBlank(node.data.eventHash)) {
          appModal.warning('锚点设置不能为空！');
          return;
        }
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
      $('#navInterface')
        .wSelect2({
          serviceName: 'treeComponentService',
          queryMethod: 'loadTreeComponent',
          remoteSearch: false
        })
        .on('change', function () {
          var dataProviderClz = $(this).val();

          $('.src-server-params table', $container).trigger('initSrcServerParameters');
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
                //FIXME:
                //$tabelInfo.bootstrapTable("load", rowData);
              }
            }
          });
        });

      $('#navDataStore', $container).wSelect2({
        serviceName: 'viewComponentService',
        queryMethod: 'loadSelectData',
        labelField: 'navDataStore',
        valueField: 'navDataStoreId',
        remoteSearch: false,
        params: {
          piUuid: _self.component.pageDesigner.getPiUuid()
        }
      });

      var editor = ace.edit('navDsDefaultCondition');
      editor.setTheme('ace/theme/clouds');
      editor.session.setMode('ace/mode/sql');
      //启用提示菜单
      ace.require('ace/ext/language_tools');
      editor.setOptions({
        enableBasicAutocompletion: true,
        enableSnippets: true,
        enableLiveAutocompletion: true,
        showPrintMargin: false,
        enableVarSnippets: {
          value: 'wComponentDataStoreCondition',
          showSnippetsTabs: ['内置变量', '常用代码逻辑'],
          scope: ['sql']
        },
        enableCodeHis: {
          relaBusizUuid: this.component.options.id,
          codeType: 'wLeftSidebar.navDsDefaultCondition',
          enable: true
        }
      });
      if (configuration.navDsDefaultCondition) {
        editor.setValue(configuration.navDsDefaultCondition);
      }
      $('#navDsDefaultCondition').data('codeEditor', editor);

      $('.navDsColumnSelect', $container).each(function (i) {
        var columnDatas = function () {
          var colDatas = [];
          server.JDS.call({
            service: 'viewComponentService.getColumnsById',
            data: [$('#navDataStoreId', $container).val()],
            async: false,
            success: function (result) {
              if (result.msg == 'success') {
                colDatas = $.map(result.data, function (data) {
                  return {
                    value: data.columnIndex,
                    text: data.columnIndex,
                    title: data.title,
                    id: data.columnIndex
                  };
                });
              }
            }
          });
          return colDatas;
        };
        $(this)
          .on('reloadColumn', function () {
            //展示列
            if ($(this).data('wellSelect')) {
              $(this).wellSelect('reRenderOption', {
                data: columnDatas()
              });
            } else {
              $(this).wSelect2({
                data: columnDatas(),
                defaultBlank: true,
                remoteSearch: false
              });
            }
          })
          .trigger('reloadColumn');
      });

      $('#navDataStore', $container).on('change', function () {
        $('.navDsColumnSelect', $container).trigger('reloadColumn');
      });

      var widgetViewDefineMap = {};
      var getWidgetData = function () {
        var allItems = [];
        var eventHandlerId = $('#eventHanlderId', $container).val();
        if (eventHandlerId) {
          server.JDS.call({
            service: 'appWidgetDefinitionMgr.getByPiUuid',
            data: [eventHandlerId],
            async: false,
            version: '',
            success: function (result) {
              if (result.success && result.data) {
                var cascadeItems = function (itemDatas) {
                  for (var i = 0, len = itemDatas.length; i < len; i++) {
                    if (itemDatas[i].wtype == 'wBootstrapTable') {
                      allItems.push({
                        text: itemDatas[i].title,
                        id: itemDatas[i].id
                      });
                      widgetViewDefineMap[itemDatas[i].id] = itemDatas[i];
                    }
                    if (itemDatas[i].items && itemDatas[i].items.length > 0) {
                      cascadeItems(itemDatas[i].items);
                    }
                  }
                };
                cascadeItems([JSON.parse(result.data.definitionJson)]);
              }
            }
          });
        }
        return allItems;
      };
      var widgetSelectOpt = {
        labelField: 'widgetViewName',
        valueField: 'widgetViewId',
        remoteSearch: false,
        data: getWidgetData()
      };
      $('#widgetViewName', $container).wSelect2(widgetSelectOpt);
      $('#widgetViewName', $container).data('options', widgetSelectOpt);
      $('#widgetViewName', $container).on('refresh', function () {
        widgetSelectOpt = $(this).data('options');
        widgetSelectOpt.data = getWidgetData();
        $('#widgetViewName,#widgetViewId', $container).val('');
        $(this).wSelect2(widgetSelectOpt);
        $('#table_relationshipQuery_info', $container).bootstrapTable('removeAll');
      });

      // 操作常量
      var operatorSource = (function () {
        var operatorSource = [];
        server.JDS.call({
          service: 'viewComponentService.getQueryOperators',
          async: false,
          success: function (result) {
            if (result.msg == 'success') {
              operatorSource = result.data;
            }
          }
        });
        return operatorSource;
      })();
      var optionValue = configuration.relationshipQuery || [];
      formBuilder.bootstrapTable.build({
        container: $('.relationship-query-info', $container),
        name: 'relationshipQuery',
        ediableNest: true,
        table: {
          data: optionValue,
          striped: true,
          idField: 'uuid',
          columns: [
            {
              field: 'checked',
              formatter: false,
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
              field: 'columnIndex',
              title: '字段名',
              editable: {
                type: 'select',
                mode: 'inline',
                showbuttons: false,
                onblur: 'submit',
                emptytext: '请选择',
                source: function () {
                  var widgetViewId = $('#widgetViewId', $container).val();
                  if (!widgetViewId) {
                    return [];
                  }
                  var columns = widgetViewDefineMap[widgetViewId].configuration.columns;
                  return $.map(columns, function (column) {
                    return {
                      value: column.name,
                      text: column.header
                    };
                  });
                }
              }
            },
            {
              field: 'operator',
              title: '操作符',
              editable: {
                type: 'select',
                mode: 'inline',
                onblur: 'submit',
                showbuttons: false,
                source: operatorSource
              }
            },
            {
              field: 'ignoreEmptyValue',
              title: '忽略真实值为空的查询条件',
              editable: {
                type: 'select',
                mode: 'inline',
                onblur: 'submit',
                showbuttons: false,
                source: [
                  {
                    value: '0',
                    text: '不忽略'
                  },
                  {
                    value: '1',
                    text: '忽略'
                  }
                ]
              }
            }
          ]
        }
      });

      // 显示标签数量的视图组件选择控制
      $('#navTreeShowBadgeCount', $container)
        .on('change', function () {
          if (this.checked === true) {
            $('.show-badge-count', $container).show();
            $('#navTreeGetBadgeCountWay', $container).trigger('change');
            $("input[name='navTreeGetBadgeDigit']", $container).trigger('change');
            $('#navTreeGetBadgeCountDataStore', $container).trigger('change');
            $("input[name='navTreeGetBadgeZero']", $container).trigger('change');
          } else {
            $('.show-badge-count', $container).hide();
            $('.show-badge-count-digit', $container).hide();
          }
        })
        .trigger('change');

      // 徽章获取方式
      $('#navTreeGetBadgeCountWay', $container).wSelect2({
        valueField: 'navTreeGetBadgeCountWay',
        lavelField: 'navTreeGetBadgeCountWayName',
        remoteSearch: false,
        data: BADGE_WAY
      });

      //徽章获取数量变化
      $('#navTreeGetBadgeCountWay', $container)
        .on('change', function () {
          $('.badge-count-way', $container).hide();
          if ($(this).val()) {
            $('.' + $(this).val(), $container).show();
            if ($(this).val() == 'countJs') {
              $('#navTreeGetBadgeCountJs', $container).trigger('change');
            }
          }
        })
        .trigger('change');

      // 统计数量的数据仓库
      $('#navTreeGetBadgeCountDataStore', $container).wSelect2({
        serviceName: 'viewComponentService',
        queryMethod: 'loadSelectData',
        valueField: 'navTreeGetBadgeCountDataStore',
        labelField: 'navTreeGetBadgeCountDataStoreName',
        remoteSearch: false,
        params: {
          piUuid: _self.component.pageDesigner.getPiUuid()
        }
      });

      //统计数量的js脚本
      $('#navTreeGetBadgeCountJs', $container).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        valueField: 'navTreeGetBadgeCountJs',
        labelField: 'navTreeGetBadgeCountJsName',
        params: {
          dependencyFilter: 'GetCountBase'
        },
        queryCallBack: function () {
          // 去除tab获取徽章的js的方法
          var hasIndex = _.findIndex(this.data, {
            id: 'GetModuleMgrTabBadgeCountDevelopment'
          });
          if (hasIndex > -1) {
            this.data.splice(hasIndex, 1);
          }
        },
        remoteSearch: false
      });

      //统计数量的js脚本对应的表格视图
      $('#navTreeGetBadgeCountJsTable', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        valueField: 'navTreeGetBadgeCountJsTable',
        params: {
          wtype: 'wBootstrapTable',
          uniqueKey: 'id',
          includeWidgetRef: 'true'
        },
        remoteSearch: false
      });

      $('#navTreeGetBadgeCountJs', $container).on('change', function () {
        $('.countJsTable[js]', $container).hide();
        if ($(this).val() == 'BootstrapTableViewGetCount') {
          $(".countJsTable[js='BootstrapTableViewGetCount']", $container).show();
        }
      });

      // 获取数量的视图组件
      $('#navTreeGetBadgeCountListViewId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        labelField: 'navTreeGetBadgeCountListViewName',
        valueField: 'navTreeGetBadgeCountListViewId',
        params: {
          wtype: 'wBootstrapTable',
          uniqueKey: 'id',
          includeWidgetRef: 'true'
        },
        defaultBlank: true,
        remoteSearch: false
      });

      // 绑定徽章数值显示位数radio值变化事件
      $("input[name='navTreeGetBadgeDigit']", $container)
        .on('change', function () {
          var navTreeGetBadgeDigit = $("input[name='" + this.name + "']:checked", $container).val();
          defaultBadgeVal = _self.getBadgeNumberdisplayBit($container);
          if (navTreeGetBadgeDigit == 'default') {
            $('.show-badge-count-digit', $container).hide();
            var badgeTip = '徽章将按照全局设置显示数值的最大位数，当前最大显示为：';
            if (defaultBadgeVal == 'off') {
              $('.navTreeGetBadgeDigitTip').html(badgeTip + '不控制显示位数');
            } else {
              var badgeNo = '';
              for (var i = 0; i < parseInt(defaultBadgeVal); i++) {
                badgeNo += '9';
              }
              $('.navTreeGetBadgeDigitTip').html(badgeTip + badgeNo + '+');
            }
          } else {
            $('.show-badge-count-digit', $container).show();
            $('.navTreeGetBadgeDigitTip').html('请选择自徽章数值显示位数，自定义设置后将不受全局的徽章设置影响。');
            if ($('li.active', '.youshangjiao-xuanzhong-ul', $container).length == 0) {
              //当前未选中具体值,设置默认选中
              if (defaultBadgeVal < '5' || defaultBadgeVal == 'off') {
                $('li[data-digit=' + defaultBadgeVal + ']', '.youshangjiao-xuanzhong-ul', $container).trigger('click');
              } else {
                // $('li[data-digit="off"]', '.youshangjiao-xuanzhong-ul', $container).addClass('active').trigger('click');
              }
            }
          }
        })
        .trigger('change');

      //绑定徽章数值显示位数自定义值变化事件
      $('li', '.youshangjiao-xuanzhong-ul', $container)
        .on('click', function () {
          $('li', '.youshangjiao-xuanzhong-ul', $container).removeClass('active');
          $(this).addClass('active');
          $('#navTreeGetBadgeDigitNumber', $container).val($(this).attr('data-digit'));
        })
        .trigger('change');

      // 绑定徽章数值 是否显示0
      $("input[name='navTreeGetBadgeZero']", $container)
        .on('change', function () {
          var navTreeGetBadgeZero = $("input[name='" + this.name + "']:checked", $container).val();
          //全局设置 徽章数量为0的显示开关，为0不显示，1显示，默认0
          var defaultVal = SystemParams.getValue('badge.number.zero.show.switch');
          if (navTreeGetBadgeZero == 'default') {
            var badgeTip = '徽章数值为0时，将按照全局设置显示。当前徽章数值为0时';
            if (defaultVal == '0') {
              $('.navTreeGetBadgeZeroTip').html(badgeTip + '不显示徽章');
            } else {
              $('.navTreeGetBadgeZeroTip').html(badgeTip + '显示徽章');
            }
          } else if (navTreeGetBadgeZero == '1') {
            $('.navTreeGetBadgeZeroTip').html('徽章数值为0时，将显示徽章（自定义设置后将不受全局的徽章设置影响）');
          } else if (navTreeGetBadgeZero == '0') {
            $('.navTreeGetBadgeZeroTip').html('徽章数值为0时，将不显示徽章（自定义设置后将不受全局的徽章设置影响）');
          }
        })
        .trigger('change');
    };

    configurer.prototype.initNavJsEventCodeInfo = function (configuration, $container) {
      var _this = this;
      $('#widget_left_sidebar_tabs_nav_js_event_info', $container)
        .find('pre')
        .each(function () {
          var editor = ace.edit($(this).attr('id'));
          editor.setTheme('ace/theme/clouds');
          editor.session.setMode('ace/mode/javascript');
          //启用提示菜单
          ace.require('ace/ext/language_tools');
          editor.setOptions({
            enableBasicAutocompletion: true,
            enableSnippets: true,
            enableLiveAutocompletion: true,
            enableVarSnippets: 'wLeftSidebar.' + $(this).attr('id'),
            enableCodeHis: {
              relaBusizUuid: _this.component.options.id,
              codeType: 'wLeftSidebar.' + $(this).attr('id'),
              enable: true
            }
          });
          if (configuration.defineEventJs && configuration.defineEventJs[$(this).attr('id')]) {
            editor.setValue(configuration.defineEventJs[$(this).attr('id')]);
          }

          $(this).data('codeEditor', editor);
        });
    };

    //获取徽章数量显示的系统参数，并处理
    configurer.prototype.getBadgeNumberdisplayBit = function ($container) {
      var _self = this;
      var defaultBadgeVal = 'off';
      //徽章数量显示位数默认值（全局）：2
      var defaultVal = SystemParams.getValue('badge.number.display.bit.default');
      if (defaultVal && parseInt(defaultVal)) {
        var defaultVal1 = parseInt(defaultVal);
        if (defaultVal1 < 1) {
          defaultVal = 'off';
        }
      } else {
        defaultVal = 'off';
      }
      //徽章数量显示位数开关（全局）：0 为关，表示不做处理；1 为开，表示使用默认值。
      var switchVal = SystemParams.getValue('badge.number.display.bit.switch');
      if (switchVal == '1') {
        defaultBadgeVal = defaultVal;
      }
      return defaultBadgeVal;
    };

    //验证徽章逻辑必填项
    configurer.prototype.validateBadgeInput = function (bean, $container) {
      var _self = this;
      var ispass = true;
      if (bean.navTreeShowBadgeCount) {
        //显示徽章
        if (bean.navTreeGetBadgeCountWay) {
          //选择获取数量的方式
          var wayItem = _.find(BADGE_WAY, {
            id: bean.navTreeGetBadgeCountWay
          }); //获取方式对应的下拉
          if (!bean[wayItem.$id]) {
            appModal.warning(wayItem.$valiTxt + '！');
            ispass = false;
          }
          if (bean[wayItem.$id] == 'BootstrapTableViewGetCount') {
            if (!bean.navTreeGetBadgeCountJsTable) {
              appModal.warning('请选择表格视图！');
              ispass = false;
            }
          }
        } else {
          appModal.warning('请选择获取数量的方式！');
          ispass = false;
        }
        if (bean.navTreeGetBadgeDigit == 'defined') {
          //位数自定义
          if (!bean.navTreeGetBadgeDigitNumber) {
            appModal.warning('请选择徽章自定义显示方式！');
            ispass = false;
          }
        }
      }
      return ispass;
    };

    configurer.prototype.onOk = function ($container) {
      var _self = this;
      var configuration = _self.collectConfiguration($container);
      // 锚点设置验证
      if (StringUtils.isNotBlank(configuration.eventHashType) && StringUtils.isBlank(configuration.eventHash)) {
        appModal.warning('锚点设置不能为空！');
        return false;
      }
      _self.component.options.configuration = configuration;
    };
    // 收集配置信息
    configurer.prototype.collectConfiguration = function ($container) {
      var configuration = {};
      // 基本信息
      this.collectBaseInfo(configuration, $container);
      // 导航信息
      this.collectNavInfo(configuration, $container);
      this.collectNavJsEventInfo(configuration, $container);
      return $.extend({}, configuration);
    };
    configurer.prototype.collectBaseInfo = function (configuration, $container) {
      var $form = $('#widget_left_sidebar_tabs_base_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.isShowRoot = Boolean(opt.isShowRoot);
      opt.autoToggle = Boolean(opt.autoToggle);
      opt.initExpend = Boolean(opt.initExpend);
      opt.firstNavView = Boolean(opt.firstNavView);
      opt.isTelescopicNav = Boolean(opt.isTelescopicNav);
      if (opt.isTelescopicNav) {
        opt.thumbnailNav = Boolean(opt.thumbnailNav);
      }
      $.extend(configuration, opt);
    };
    configurer.prototype.collectNavInfo = function (configuration, $container) {
      var _self = this;
      var $form = $('#widget_left_sidebar_tabs_nav_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      configuration.nav = _self.navTree.getNodes();
      // 导航事件参数
      var eventParameters = $('#table_intfEventParams_info', $container).bootstrapTable('getData');
      configuration.eventParameters = eventParameters;
      var eventParams = {};
      $.map(eventParameters, function (option) {
        eventParams[option.name] = option.value;
      });
      configuration.eventParams = eventParams;
      // 接口服务参数
      var srcServerParameters = $('#table_srcServerParams_info', $container).bootstrapTable('getData');
      configuration.srcServerParameters = srcServerParameters;
      var srcServerParams = {};
      $.map(srcServerParameters, function (option) {
        srcServerParams[option.name] = option.value;
      });
      configuration.srcServerParams = srcServerParams;
      configuration.relationshipQuery = $container.find('#table_relationshipQuery_info').bootstrapTable('getData');
      configuration.navDsDefaultCondition = $('#navDsDefaultCondition', $container).data('codeEditor').getValue();
      $.extend(configuration, opt);
    };
    configurer.prototype.collectNavJsEventInfo = function (configuration, $container) {
      //自定义js事件代码
      var opt = {
        defineEventJs: {}
      };
      $('#widget_left_sidebar_tabs_nav_js_event_info pre', $container).each(function () {
        opt.defineEventJs[$(this).attr('id')] = $(this).data('codeEditor').getValue();
      });
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
