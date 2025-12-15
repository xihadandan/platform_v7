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
    EDIT: 3
    // 修改
  };
  component.prototype.create = function () {};
  // 使用属性配置器
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  function buildMenuItemBootstrapTable($element, name, menuItems, productUuid, navType) {
    formBuilder.bootstrapTable.build({
      container: $element,
      name: name,
      ediableNest: true,
      table: {
        data: menuItems,
        striped: true,
        idField: 'uuid',
        onEditableSave: function (field, row, oldValue, $el) {
          if (navType !== '2') {
            return;
          }
          var $tableSubNavMenuItemsInfo = $('#table_subNav-menuItems_info', $element);
          if (field == 'defaultSelected' && row[field] == '1') {
            var data = $tableSubNavMenuItemsInfo.bootstrapTable('getData');
            $.each(data, function (index, rowData) {
              if (row != rowData) {
                rowData.defaultSelected = 0;
                $tableSubNavMenuItemsInfo.bootstrapTable('updateRow', index, rowData);
              }
            });
          }
        },
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
            title: '名称',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit'
            }
          },
          {
            field: 'icon',
            title: '图标',
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'right',
              savenochange: true,
              iconSelectTypes: [3],
              value2input: designCommons.bootstrapTable.icon.value2input,
              input2value: designCommons.bootstrapTable.icon.input2value,
              value2display: designCommons.bootstrapTable.icon.value2display,
              value2html: designCommons.bootstrapTable.icon.value2html
            }
          },
          {
            field: 'group',
            title: '组别',
            width: 100,
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit'
            }
          },
          {
            field: 'hidden',
            title: '是否隐藏',
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: [
                {
                  value: '-1',
                  text: ''
                },
                {
                  value: '0',
                  text: '显示'
                },
                {
                  value: '1',
                  text: '隐藏'
                }
              ]
            }
          },
          {
            field: 'badge',
            title: '徽章',
            visible: navType === '1',
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'bottom',
              savenochange: true,
              value2input: designCommons.bootstrapTable.badge.value2input,
              value2display: designCommons.bootstrapTable.badge.value2display
            }
          },
          {
            field: 'defaultSelected',
            title: '默认选中',
            visible: navType === '2',
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: [
                {
                  value: '-1',
                  text: ''
                },
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
          },
          {
            field: 'eventType',
            title: '事件类型',
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: function () {
                var eventTypes = constant.EVENT_TYPE;
                var types = [
                  {
                    value: '-1',
                    text: ''
                  }
                ];
                var etes = $.map(eventTypes, function (eventType) {
                  return {
                    value: eventType.value,
                    text: eventType.name
                  };
                });
                types = types.concat(etes);
                return types;
              }
            }
          },
          {
            field: 'target',
            title: '目标位置',
            editable: {
              onblur: 'cancel',
              type: 'wCustomForm',
              placement: 'bottom',
              savenochange: true,
              value2input: designCommons.bootstrapTable.targePosition.value2input,
              value2display: designCommons.bootstrapTable.targePosition.value2display,
              inputCompleted: designCommons.bootstrapTable.targePosition.inputCompleted
            }
          },
          {
            field: 'eventHandler',
            title: '事件处理',
            width: 150,
            editable: {
              onblur: 'ignore',
              type: 'wCustomForm',
              placement: 'left',
              savenochange: true,
              value2input: designCommons.bootstrapTable.eventHandler.value2input,
              input2value: designCommons.bootstrapTable.eventHandler.input2value,
              validate: designCommons.bootstrapTable.eventHandler.validate,
              value2display: designCommons.bootstrapTable.eventHandler.value2display
            }
          },
          {
            field: 'eventParams',
            title: '事件参数',
            editable: {
              onblur: 'ignore',
              type: 'wCustomForm',
              placement: 'left',
              savenochange: true,
              value2input: designCommons.bootstrapTable.eventParams.value2input,
              input2value: designCommons.bootstrapTable.eventParams.input2value,
              value2display: designCommons.bootstrapTable.eventParams.value2display
            }
          }
        ]
      }
    });
  }
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
      // 一级导航信息
      this.initSubNavInfo(configuration, $container);
      // 工具栏信息
      this.initToolBarInfo(configuration, $container);
      //自定义导航
      this.initCustomNavbar(configuration, $container);
    };
    configurer.prototype.initBaseInfo = function (configuration, $container) {
      // 设置值
      designCommons.setElementValue(configuration, $container);

      // if($('#isShowCustomNavbar').is(':checked')) {
      //    $('.custom-navbar-tab').show();
      // }
      //
      // $('#isShowCustomNavbar').on('change',function () {
      //     var _val = $(this).is(':checked');
      //     if(_val) {
      //         $('.custom-navbar-tab').show();
      //     } else {
      //         $('.custom-navbar-tab').hide();
      //     }
      // })

      var navBarType = $('input[name="isShowCustomNavbar"]:checked').val();
      if (navBarType === 'false') {
        $('.default-navbar-tab').show();
        $('.custom-navbar-tab').hide();
      } else {
        $('.default-navbar-tab').hide();
        $('.custom-navbar-tab').show();
      }

      $('input[name="isShowCustomNavbar"]').on('change', function () {
        if ($(this).val() === 'false') {
          $('.default-navbar-tab').show();
          $('.custom-navbar-tab').hide();
        } else {
          $('.default-navbar-tab').hide();
          $('.custom-navbar-tab').show();
        }
      });

      // 初始化基本信息
      var backgroupColorOption = "<option value='themeColor'>采用主题颜色</option>";
      for (var i = 0; i < constant.WIDGET_COLOR.length; i++) {
        var color = constant.WIDGET_COLOR[i];
        var selected = color.value == configuration.backgroudColor ? 'selected' : '';
        backgroupColorOption += "<option value='" + color.value + "'" + selected + '>' + color.name + '</option>';
      }
      $('#backgroudColor', $container).append(backgroupColorOption);

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

      if (configuration.fixedTop !== '0') {
        $('#fixedTopSwitch', $container).addClass('active');
      }

      $('#fixedTopSwitch', $container).on('click', function () {
        if ($(this).hasClass('active')) {
          $(this).removeClass('active');
          $('#fixedTop', $container).val('0');
        } else {
          $(this).addClass('active');
          $('#fixedTop', $container).val('1');
        }
      });
    };
    configurer.prototype.initSubNavInfo = function (configuration, $container) {
      var _self = this;
      var subNav = configuration.subNav || {};
      // 设置值
      designCommons.setElementValue(subNav, $container);
      var appPageUuid = _self.component.pageDesigner.getPageUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;

      // 一级导航
      var $element = $('.subNav-menuItems', $container);
      var menuItems = subNav.menuItems || [];
      buildMenuItemBootstrapTable($element, 'subNav-menuItems', menuItems, productUuid, '2');
    };
    configurer.prototype.initToolBarInfo = function (configuration, $container) {
      var _self = this;
      var toolBar = configuration.toolBar || {};
      // 设置值
      designCommons.setElementValue(toolBar, $container);
      var appPageUuid = _self.component.pageDesigner.getPageUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;

      // 一级导航
      var $element = $('.toolBar-menuItems', $container);
      var menuItems = toolBar.menuItems || [];
      buildMenuItemBootstrapTable($element, 'toolBar-menuItems', menuItems, productUuid);
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
      $('#navTreeIconSelectBtn', $container).on('click', function () {
        var _thiz = this;
        $.WCommonPictureLib.show({
          selectTypes: [3],
          confirm: function (data) {
            var fileIDs = data.fileIDs;
            $('#navTreeIcon').val(fileIDs);
            $('#navTreeIconSnap', $container).attr('iconClass', fileIDs);
            $('#navTreeIconSnap', $container).attr('class', fileIDs);

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
          wtype: 'wBootstrapTable',
          uniqueKey: 'id',
          includeWidgetRef: 'true'
        },
        defaultBlank: true,
        remoteSearch: false
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

      $('#navTreeEventHanlderName', $container).each(function () {
        $(this).AppEvent({
          ztree: { params: [productUuid] },
          okCallback: function ($el, data) {
            if (data) {
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

      // 事件参数
      var $element = $('.event-params', $container);
      builEventParamsBootstrapTable($element, 'navTreeEventParams', []);

      //初始化页面时候，主动触发事件实现数据填充
      //            if ($("input[name='navType']:checked", $container).val() == NAV_TYPE.INTERFACE) {
      //                $("input[name='targetPosition']", $nav).trigger("change");
      //                $("input[name='targetWidgetId']", $nav).trigger("change");
      //                $("#eventHanlderId", $nav).wCommonComboTree({
      //                    value: configuration.eventHanlderId
      //                });
      //            }

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
            designCommons.setElementValue(bean, $container);
            var iconClass = $('#navTreeIconSnap', $nav).attr('iconClass');
            $('#navTreeIconSnap', $nav).removeClass(iconClass);
            $('#navTreeIconSnap', $nav).attr('iconClass', bean.navTreeIcon);
            $('#navTreeIconSnap', $nav).addClass(bean.navTreeIcon);
            $("input[name='navTreeShowBadgeCount']", $nav).trigger('change');
            $("input[name='navTreeGetBadgeCountListViewId']", $nav).trigger('change');
            $("input[name='navTreeTargetPosition']", $nav).trigger('change');
            $("input[name='navTreeTargetWidgetId']", $nav).trigger('change');
            $('#navTreeEventHanlderId', $nav).val(bean.navTreeEventHanlderId);
            $('#navTreeEventHanlderName', $nav).val(bean.navTreeEventHanlderName);
            $('#navTreeEventHanlderType', $nav).val(bean.navTreeEventHanlderType);
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

            if (bean.navTreeIcon) {
              $('#navTreeIconRemoveBtn', $container).show();
            } else {
              $('#navTreeIconRemoveBtn', $container).hide();
            }

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
      $('#widget_navbar_tree_add').on('click', function () {
        if (!checkSelectedNode()) {
          return;
        }
        clearNavTree();
        var uuid = UUID.createUUID();
        $('#navTreeUuid', $container).val(uuid);
        navTreeOpType = NAV_TREE_OP_TYPE.ADD;
      });
      // 添加子节点
      $('#widget_navbar_tree_add_child').on('click', function () {
        if (!checkSelectedNode()) {
          return;
        }
        clearNavTree();
        var uuid = UUID.createUUID();
        $('#navTreeUuid', $container).val(uuid);
        navTreeOpType = NAV_TREE_OP_TYPE.ADD_CHILD;
      });
      // 删除
      $('#widget_navbar_tree_remove').on('click', function () {
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
      $('#widget_navbar_tree_save').on('click', function () {
        if (navTreeOpType == 0) {
          var nodes = zTree.getNodes();
          if (nodes.length != 0) {
            appModal.warning('请选择节点！');
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
    };
    configurer.prototype.onOk = function ($container) {
      this.component.options.configuration = this.collectConfiguration($container);
    };
    // 收集配置信息
    configurer.prototype.collectConfiguration = function ($container) {
      var configuration = {};
      // 基本信息
      this.collectBaseInfo(configuration, $container);
      // 二级导航信息
      this.collectSubNavInfo(configuration, $container);
      // 工具栏信息
      this.collectToolBatInfo(configuration, $container);
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
    configurer.prototype.collectSubNavInfo = function (configuration, $container) {
      var $form = $('#widget_header_tabs_sub_nav_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      // 二级导航
      var $tableSubNavMenuItemsInfo = $('#table_subNav-menuItems_info', $container);
      var menuItems = $tableSubNavMenuItemsInfo.bootstrapTable('getData');
      opt.menuItems = menuItems;
      configuration.subNav = configuration.subNav || {};
      $.extend(configuration.subNav, opt);
    };
    configurer.prototype.collectToolBatInfo = function (configuration, $container) {
      var $form = $('#widget_header_tabs_tool_bar_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      // 工具栏
      var $tableToolBarMenuItemsInfo = $('#table_toolBar-menuItems_info', $container);
      var menuItems = $tableToolBarMenuItemsInfo.bootstrapTable('getData');
      opt.menuItems = menuItems;
      configuration.toolBar = configuration.toolBar || {};
      $.extend(configuration.toolBar, opt);
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
  // 返回组件定义HTML
  component.prototype.toHtml = function () {
    var options = this.options;
    var configuration = options.configuration || {};
    var id = this.getId();
    var subNavBarTpl = new StringBuilder();
    // 隐藏二级导航判断处理
    if (configuration.subNavAndToolBarHidden !== true) {
      var navStyle = null;
      if (configuration.subNav) {
        navStyle = configuration.subNav.navStyle;
      }
      if (StringUtils.isNotBlank(navStyle)) {
        navStyle = 'nav-' + navStyle;
      }
      subNavBarTpl.appendFormat('<div class="navbar navbar-{0} ui-navbar">');
      subNavBarTpl.append('	<div class="navbar-content clearfix">');
      subNavBarTpl.appendFormat('		<ul class="nav navbar-nav {0} ui-navbar-nav">', navStyle);
      subNavBarTpl.append('		</ul>');
      subNavBarTpl.append('		<ul class="nav navbar-nav navbar-right ui-navbar-toolBar">');
      subNavBarTpl.append('		</ul>');
      subNavBarTpl.append('	</div>');
      subNavBarTpl.append('</div>');
    }

    var sb = new StringBuilder();
    sb.appendFormat('<div id="{0}" class="ui-wNavbar">', id);
    var backgroundColor = configuration.backgroudColor;
    sb.appendFormat(subNavBarTpl.toString(), backgroundColor);
    sb.appendFormat('</div>');
    return sb.toString();
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
