define(['ui_component', 'constant', 'commons', 'formBuilder', 'appContext', 'design_commons'], function (
  ui_component,
  constant,
  commons,
  formBuilder,
  appContext,
  designCommons
) {
  var StringBuilder = commons.StringBuilder;
  var StringUtils = commons.StringUtils;
  var component = $.ui.component.BaseComponent();
  var UUID = commons.UUID;
  component.prototype.create = function () {
    var _self = this;
    var options = _self.options;
    var $element = $(_self.element);
    var $panelContainer = $element.find('.ui-sortable');
    _self.pageDesigner.sortable(_self, $panelContainer, $panelContainer);

    // 初始化容器结点
    if (options.items != null) {
      $.each(options.items, function (j, item) {
        var $draggable = _self.pageDesigner.createDraggableByDefinitionJson(item);
        _self.pageDesigner.drop(_self, $panelContainer, $draggable, item);
      });
    }
  };
  // 返回定义的HTML
  component.prototype.toHtml = function () {
    var _self = this;
    var options = _self.options;
    var children = _self.getChildren();
    var id = _self.getId();
    var html = new StringBuilder();
    var configuration = options.configuration || {};
    var cssClass = configuration.customClass;
    html.appendFormat("<div id='{0}' class='panel panel-default {1}'>", id, cssClass);
    // 头部信息
    if (configuration.hideHeader !== true && configuration.header) {
      html.appendFormat('<div class="panel-heading">');
      var headerTpl = '<h3 class="panel-title"><span class="panel-icon {0}"></span><span class="title-text">{1}</span>';
      if (configuration.header.showBadgeCount) {
        headerTpl += '<span class="badge"></span>';
      }
      headerTpl += '</h3>';
      html.appendFormat(headerTpl, configuration.header.titleIcon || 'iconfont icon-ptkj-qukuaibiaotitubiao', configuration.header.title);
      html.appendFormat('</div>');
    }
    // 内容信息
    html.appendFormat("<div class='panel-body'>");
    if (children != null) {
      $.each(children, function (i) {
        var child = this;
        var childHtml = child.toHtml.call(child);
        html.append(childHtml);
      });
    }
    html.appendFormat('</div>');
    // 底部信息
    if (configuration.hideFooter !== true && configuration.footer) {
      html.append('<div class="panel-footer">面板脚注</div>');
    }
    html.appendFormat('</div>');
    return html;
  };
  // 使用属性配置器
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  var clearChecked = function (row) {
    row.checked = false;
    return row;
  };

  var buildEventParamsBootstrapTable = function ($element, name, data, productUuid) {
    $element.bootstrapTable('destroy');
    $($element).html('');
    $.each(data, function (i, item) {
      //兼容旧版
      if (!item.eventManager) {
        item.eventManager = {
          eventHandler: item.handler,
          eventParams: item.params ? item.params : null,
          parameters: item.params ? item.params.parameters : null,
          params: item.params ? item.params.params : null
        };
      }
    });
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
            field: 'source',
            title: '事件元素',
            editable: {
              type: 'select',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              emptytext: '请选择',
              source: function () {
                return [
                  {
                    value: '.panel-view-more',
                    text: '查看更多'
                  }
                ];
              }
            }
          },
          {
            field: 'type',
            title: '事件类型',
            editable: {
              type: 'select',
              mode: 'inline',
              onblur: 'submit',
              showbuttons: false,
              source: function () {
                var eventTypes = constant.EVENT_TYPE.concat({
                  key: 'DOM_LOADED',
                  value: 'domloaded',
                  name: 'DOM初始化'
                });
                return $.map(eventTypes, function (eventType) {
                  return {
                    value: eventType.value,
                    text: eventType.name
                  };
                });
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
            field: 'eventManager',
            title: '事件管理',
            width: 150,
            editable: {
              mode: 'modal',
              onblur: 'ignore',
              type: 'wCustomForm',
              placement: 'left',
              savenochange: true,
              renderParams: {
                defineJs: false
              },
              value2input: designCommons.bootstrapTable.eventManager.value2input,
              input2value: designCommons.bootstrapTable.eventManager.input2value,
              validate: designCommons.bootstrapTable.eventManager.validate,
              value2display: designCommons.bootstrapTable.eventManager.value2display
            }
          }
        ]
      }
    });
  };

  // 返回属性配置器
  component.prototype.getPropertyConfigurer = function () {
    var collectClass = 'w-configurer-option';

    var configurerPrototype = {};
    configurerPrototype.getTemplateUrl = function () {
      // 调用父类提交方法
      var wtype = this._superApply(arguments);
      wtype = wtype.replace('widget', 'container');
      return wtype;
    };
    configurerPrototype.onLoad = function ($container, options) {
      // 初始化页签项
      $('#container_panel_tabs ul a', $container).on('click', function (e) {
        e.preventDefault();
        $(this).tab('show');
      });
      var configuration = $.extend(true, {}, options.configuration);
      this.initConfiguration(configuration, $container);
    };
    configurerPrototype.onOk = function ($container) {
      this.component.options.configuration = this.collectConfiguration($container);
    };
    // 初始化配置信息
    configurerPrototype.initConfiguration = function (configuration, $container) {
      // 基本信息
      this.initBaseInfo(configuration, $container);
      // 头部信息
      this.initHeaderInfo(configuration, $container);
      // 内容信息
      this.initBodyInfo(configuration, $container);
      // 底部信息
      this.initFooterInfo(configuration, $container);
    };
    configurerPrototype.initBaseInfo = function (configuration, $container) {
      // 设置值
      designCommons.setElementValue(configuration, $container);

      // 分类
      // $("#categoryCode", $container).wSelect2({
      //     serviceName: "dataDictionaryService",
      //     params: {
      //         type: "MODULE_CATEGORY"
      //     },
      //     labelField: "categoryName",
      //     valueField: "categoryCode",
      //     remoteSearch: false
      // });
      // 加载的JS模块
      $('#jsModule', $container).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        params: {
          dependencyFilter: 'PanelWidgetDevelopment'
        },
        labelField: 'jsModuleName',
        valueField: 'jsModule',
        remoteSearch: false,
        multiple: true
      });
      //设置panel定义为显示容器
      $('#showAsContainer').on('change', function () {
        if ($(this).is(':checked')) {
          $('#hideHeader').prop('checked', true);
        } else {
          $('#hideHeader').prop('checked', false);
        }
      });
    };
    configurerPrototype.initHeaderInfo = function (configuration, $container) {
      var _self = this;
      var header = configuration.header || {};
      // 设置值
      designCommons.setElementValue(header, $container);

      var appPageUuid = _self.component.pageDesigner.getPageUuid();
      var piUuid = _self.component.pageDesigner.getPiUuid();
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;
      if (StringUtils.isNotBlank(system.piUuid)) {
        piUuid = system.piUuid;
      }

      // 标题图标
      $('#titleIconSelectBtn', $container).on('click', function () {
        $.WCommonPictureLib.show({
          selectTypes: [3],
          confirm: function (data) {
            var fileType = data.fileType;
            var pictureFilePath = data.filePaths;
            if (StringUtils.isBlank(pictureFilePath)) {
              return;
            }
            $('#titleIcon', $container).val(pictureFilePath);
            var $titleParent = $('#titleIcon', $container).parent();
            $titleParent.addClass(pictureFilePath);
          }
        });
      });
      $('#titleIconDeleteBtn', $container).on('click', function () {
        $('#titleIcon', $container).val('');
        var $titleParent = $('#titleIcon', $container).parent();
        $titleParent.attr('class', 'col-sm-10');
      });
      if (header && header.titleIcon) {
        var $titleParent = $('#titleIcon', $container).parent();
        $titleParent.addClass(header.titleIcon);
      }

      // 显示标签数量的视图组件选择控制
      $('#showBadgeCount', $container)
        .on('change', function () {
          if (this.checked === true) {
            $('.show-badge-count', $container).show();
            $('#getBadgeCountWay', $container).trigger('change');
          } else {
            $('.show-badge-count', $container).hide();
          }
          //
        })
        .trigger('change');
      // 获取数量的视图组件
      $('#getBadgeCountListViewId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        labelField: 'getBadgeCountListViewName',
        valueField: 'getBadgeCountListViewId',
        params: {
          wtype: 'wBootstrapTable',
          uniqueKey: 'id',
          includeWidgetRef: 'true'
        },
        remoteSearch: false
      });

      // 启用查询的视图组件选择控制
      $('#enableQuery', $container)
        .on('change', function () {
          if (this.checked === true) {
            $('.enable-query', $container).show();
          } else {
            $('.enable-query', $container).hide();
          }
        })
        .trigger('change');
      // 查询关联的视图组件
      $('#queryCorrelativeListViewId', $container).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        labelField: 'queryCorrelativeListViewName',
        valueField: 'queryCorrelativeListViewId',
        params: {
          wtype: 'wBootstrapTable',
          appPageUuid: appPageUuid,
          uniqueKey: 'id',
          includeWidgetRef: 'true'
        },
        remoteSearch: false
      });

      // 头部事件
      var $element = $('.header-events', $container);
      var headerEvents = header.events || [];
      $.each(headerEvents, function (i, item) {
        //兼容旧版
        if (!item.eventManager) {
          item.eventManager = {
            eventHandler: item.handler,
            eventParams: item.params ? item.params : null,
            parameters: item.params ? item.params.parameters : null,
            params: item.params ? item.params.params : null
          };
        }
      });
      formBuilder.bootstrapTable.build({
        container: $element,
        name: 'headerEvents',
        ediableNest: true,
        table: {
          data: headerEvents,
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
              field: 'source',
              title: '事件元素',
              editable: {
                type: 'select',
                mode: 'inline',
                showbuttons: false,
                onblur: 'submit',
                emptytext: '请选择',
                source: function () {
                  var sources = [];
                  sources.push({
                    value: '.panel-title',
                    text: '面板标题'
                  });
                  sources.push({
                    value: '.panel-view-more',
                    text: '面板查看更多'
                  });
                  sources.push({
                    value: '.panel-body',
                    text: '面板主体'
                  });
                  return sources;
                }
              }
            },
            {
              field: 'type',
              title: '事件类型',
              editable: {
                type: 'select',
                mode: 'inline',
                onblur: 'submit',
                showbuttons: false,
                source: function () {
                  var eventTypes = constant.EVENT_TYPE.concat({
                    key: 'DOM_LOADED',
                    value: 'domloaded',
                    name: 'DOM初始化'
                  });
                  return $.map(eventTypes, function (eventType) {
                    return {
                      value: eventType.value,
                      text: eventType.name
                    };
                  });
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
              field: 'eventManager',
              title: '事件管理',
              width: 150,
              editable: {
                mode: 'modal',
                onblur: 'ignore',
                type: 'wCustomForm',
                placement: 'left',
                savenochange: true,
                renderParams: {
                  defineJs: false
                },
                value2input: designCommons.bootstrapTable.eventManager.value2input,
                input2value: designCommons.bootstrapTable.eventManager.input2value,
                validate: designCommons.bootstrapTable.eventManager.validate,
                value2display: designCommons.bootstrapTable.eventManager.value2display
              }
            }
          ]
        }
      });

      $('#getBadgeCountWay', $container).wSelect2({
        valueField: 'getBadgeCountWay',
        remoteSearch: false,
        data: (function () {
          return [
            { id: 'tableWidgetCount', text: '通过页面表格视图获取数量' },
            { id: 'dataStoreCount', text: '通过数据仓库获取数量' }
          ];
        })()
      });
      //兼容旧代码：配置了视图数量统计的情况下
      if (!header.getBadgeCountWay && header.getBadgeCountListViewId) {
        $('#getBadgeCountWay', $container).val('tableWidgetCount');
        $('.tableWidgetCount', $container).show();
      }

      $('#getBadgeCountWay', $container)
        .on('change', function () {
          $('.badge-count-way', $container).hide();
          if ($(this).val()) {
            $('.' + $(this).val(), $container).show();
          }
        })
        .trigger('change');

      // 统计数量的数据仓库
      $('#getBadgeCountDataStore', $container).wSelect2({
        serviceName: 'viewComponentService',
        queryMethod: 'loadSelectData',
        valueField: 'getBadgeCountDataStore',
        remoteSearch: false,
        params: {
          piUuid: _self.component.pageDesigner.getPiUuid()
        }
      });
    };
    configurerPrototype.initBodyInfo = function (configuration, $container) {
      console.log(configuration);
      var $container = $('#container_panel_tabs_content_info', $container);
      var _self = this;
      var body = configuration.body || {};
      var $contentType = $('input[name="contentType"]', $container);
      // 设置值

      designCommons.setElementValue(body, $container);

      // todo
      $contentType.on('click', function () {
        var $this = $(this);
        var _value = $this.val();
        $('#' + _value, $container).show();
        if (_value === 'contentUrl') {
          $('#contentTab', $container).hide();
        } else {
          $('#contentUrl', $container).hide();
        }
      });

      $('#navTree_uuid', $container).val(UUID.createUUID());

      if (body.contentType) {
        var _contentType = body.contentType;
        $('#' + _contentType, $container).show();
        if (_contentType === 'contentUrl') {
          $('#contentTab', $container).hide();
        } else {
          $('#contentUrl', $container).hide();
        }
      }
      if (body.contentTabHeight) {
        $('input[name="contentTabHeight"]', $container).val(body.contentTabHeight);
      }
      var tabs = body.tabs ? body.tabs : [];
      var system = appContext.getCurrentUserAppData().getSystem();
      var productUuid = system.productUuid;
      var $tabsForm = $('#widget_bootstap_tabs_form');
      var navTreeId = 'widget_bootstrap_tabs_nav_tree';
      var $navTree = $('#' + navTreeId);
      var treeSetting = {
        edit: {
          drag: {
            autoExpandTrigger: true,
            isCopy: false,
            isMove: true,
            prev: true,
            inner: false,
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
            var data = treeNode.data;
            clearNavTree();

            // 兼容旧数据
            if (!data.hasOwnProperty('clickTabToRefresh')) {
              data.clickTabToRefresh = '0';
            }

            for (var i in data) {
              var value = data[i];

              if (typeof value === 'object') {
                for (var j in value) {
                  $('#navTree_' + i + '_' + j, $tabsForm).val(value[j]);
                }
              } else {
                $('#navTree_' + i, $tabsForm).val(value);
              }
            }

            var iconClass = $('#navTreeIconSnap', $tabsForm).attr('iconClass');
            $('#navTreeIconSnap', $tabsForm).removeClass(iconClass);
            $('#navTreeIconSnap', $tabsForm).attr('iconClass', $('#navTree_icon_className').val());
            $('#navTreeIconSnap', $tabsForm).addClass($('#navTree_icon_className').val());
            $('#navTree_viewId', $tabsForm).trigger('change');
            $(
              '#navTree_BadgeType,#navTree_BadgeTypeCountJs,' + '#navTree_BadgeTypeCountDs,#navTree_BadgeTypeCountJsWBootstrapTable',
              $tabsForm
            ).trigger('change');

            $('#navTree_eventHandler_name', $tabsForm).wCommonComboTree({
              value: data.eventHandler.id
            });
            $('#navTree_eventHandler_name', $tabsForm).AppEvent('setValue', data.eventHandler.id);
            if ($('#navTree_isShowBadge').val() == '1') {
              $('#navTree_isShowBadge').prop('checked', true).trigger('change');
            } else {
              $('#navTree_isShowBadge').prop('checked', false).trigger('change');
            }
            $('#navTree_clickTabToRefresh').prop('checked', $('#navTree_clickTabToRefresh').val() == '1');

            var $tabMoreEvents = $('#contentTab .tab-more-events', $container);
            var tabMoreEvents = data.moreEvents || [];
            buildEventParamsBootstrapTable($tabMoreEvents, 'tabMoreEvents', tabMoreEvents, productUuid);
          }
        }
      };

      function clearNavTree() {
        $('input[type="text"]', $tabsForm).each(function () {
          $(this).val('');
          $(this).trigger('change');
        });
        $('input[type="hidden"]', $tabsForm).each(function () {
          $(this).val('');
          $(this).trigger('change');
        });

        $('input[type="checkbox"]', $tabsForm).each(function () {
          $(this).prop('checked', false);
          $(this).trigger('change');
        });

        var iconClass = $('#navTreeIconSnap', $tabsForm).attr('iconClass');
        $('#navTreeIconSnap', $tabsForm).removeClass(iconClass);
        $('#navTreeIconSnap', $tabsForm).attr('iconClass', '');
      }

      var zNodes = [];
      for (var i = 0; i < tabs.length; i++) {
        var zNode = {
          id: tabs[i].uuid,
          name: tabs[i].name,
          data: tabs[i]
        };

        zNodes.push(zNode);
      }

      var zTree = $.fn.zTree.init($navTree, treeSetting, zNodes);
      _self.navTree = zTree;
      // 添加同级
      $('#widget_bootstrap_tabs_nav_add').on('click', function () {
        clearNavTree();
        var uuid = UUID.createUUID();
        $('#navTree_uuid', $tabsForm).val(uuid);
        $('#navTree_viewId', $tabsForm).trigger('change');
        $('#navTree_isShowBadge', $tabsForm).trigger('change');
        var $tabMoreEvents = $('#contentTab .tab-more-events', $container);
        buildEventParamsBootstrapTable($tabMoreEvents, 'tabMoreEvents', [], productUuid);
      });

      // 删除
      $('#widget_bootstrap_tabs_nav_remove').on('click', function () {
        appModal.confirm('确认要删除吗?', function (result) {
          if (result) {
            clearNavTree();
            var selectedNode = getSelectedNode();
            var data = selectedNode.data;
            zTree.removeNode(selectedNode);

            for (var i = 0; i < tabs.length; i++) {
              if (tabs[i].uuid === data.uuid) {
                tabs.splice(i, 1);
                return;
              }
            }
            $('#navTree_uuid', $tabsForm).val(UUID.createUUID());
          }
        });
      });

      var getSelectedNode = function () {
        var selectedNodes = zTree.getSelectedNodes();
        if (selectedNodes.length === 0) {
          return null;
        }
        return selectedNodes[0];
      };

      // 选择图标
      $('#navTreeIconSelectBtn', $tabsForm).on('click', function () {
        var _self = this;
        $.WCommonPictureLib.show({
          selectTypes: [3],
          confirm: function (data) {
            var fileIDs = data.fileIDs;
            $('#navTree_icon_className', $tabsForm).val(fileIDs);
            $('#navTreeIconSnap', $tabsForm).attr('iconClass', fileIDs);
            $('#navTreeIconSnap', $tabsForm).addClass(fileIDs);
          }
        });
      });
      $('#navTree_eventHandler_name', $tabsForm).AppEvent({
        ztree: { params: [productUuid] },
        okCallback: function ($el, data) {
          if (data) {
            $('#navTree_eventHandler_id', $tabsForm).val(data.id);
            $('#navTree_eventHandler_type', $tabsForm).val(data.data.type);
            $('#navTree_eventHandler_path', $tabsForm).val(data.data.path);
          }
        }
      });

      // 显示标签数量的视图组件选择控制
      $('#navTree_isShowBadge', $container).on('change', function () {
        if (this.checked === true) {
          $('.navTree_BadgeType_select', $tabsForm).show();
          $('#navTree_BadgeType', $tabsForm).trigger('change');
        } else {
          $('.showBadge', $tabsForm).hide();
        }
      });

      $('#navTree_BadgeType', $tabsForm).wSelect2({
        valueField: 'navTree_BadgeType',
        remoteSearch: false,
        data: [
          { id: 'countJs', text: '通过数量统计JS脚本' },
          { id: 'datastore', text: '通过数据仓库获取数量' }
        ]
      });

      $('#navTree_BadgeType', $tabsForm).on('change', function () {
        $('.navTree_BadgeType', $tabsForm).hide();
        var typeValue = $(this).val();
        $('.navTree_BadgeType' + '_' + typeValue).show();
      });

      $('#navTree_BadgeTypeCountJsWBootstrapTable', $tabsForm).wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        valueField: 'navTree_BadgeTypeCountJsWBootstrapTable',
        params: {
          wtype: 'wBootstrapTable',
          uniqueKey: 'id',
          includeWidgetRef: 'true'
        },
        remoteSearch: false
      });

      $('#navTree_BadgeTypeCountJs', $tabsForm).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        params: {
          dependencyFilter: 'GetCountBase'
        },
        valueField: 'navTree_BadgeTypeCountJs',
        defaultBlank: true,
        remoteSearch: false
      });

      $('#navTree_BadgeTypeCountJs', $tabsForm).on('change', function () {
        $('.navTree_BadgeType_countJs[js]', $tabsForm).hide();
        if ($(this).val()) {
          $(".navTree_BadgeType_countJs[js='BootstrapTableViewGetCount']", $tabsForm).show();
        }
      });

      $('#navTree_BadgeTypeCountDs', $tabsForm).wSelect2({
        serviceName: 'viewComponentService',
        queryMethod: 'loadSelectData',
        valueField: 'navTree_BadgeTypeCountDs',
        defaultBlank: true,
        remoteSearch: false,
        params: {
          piUuid: _self.component.pageDesigner.getPiUuid()
        }
      });

      $('#navTree_save', $tabsForm).on('click', function () {
        var name = $('#navTree_name', $tabsForm).val();
        if (StringUtils.isBlank(name)) {
          appModal.warning('名称不能为空！');
          return;
        }
        var data = {};
        $('input[type="text"]', $tabsForm).each(function () {
          var id = $(this).attr('id');
          var fields = id.split('_');
          if (fields.length === 2) {
            data[fields[1]] = $(this).val();
          } else if (fields.length === 3) {
            if (!data[fields[1]]) {
              data[fields[1]] = {};
            }
            data[fields[1]][fields[2]] = $(this).val();
          }
        });
        $('input[type="hidden"]', $tabsForm).each(function () {
          var id = $(this).attr('id');
          var fields = id.split('_');
          if (fields.length === 2) {
            data[fields[1]] = $(this).val();
          } else if (fields.length === 3) {
            if (!data[fields[1]]) {
              data[fields[1]] = {};
            }
            data[fields[1]][fields[2]] = $(this).val();
          }
        });
        if ($('#navTree_isShowBadge').is(':checked')) {
          data['isShowBadge'] = '1';
        } else {
          data['isShowBadge'] = '0';
        }
        data['clickTabToRefresh'] = $('#navTree_clickTabToRefresh').prop('checked') ? '1' : '0';

        // 事件参数
        var eventParameters = $tabsForm.find('#table_tabMoreEvents_info').bootstrapTable('getData');
        data.moreEvents = eventParameters || [];

        for (var i = 0; i < tabs.length; i++) {
          if (tabs[i].uuid === $('#navTree_uuid').val()) {
            tabs[i] = data;
            var node = zTree.getNodesByParam('id', data.uuid, null)[0];
            node.name = data.name;
            data.items = node.data.items ? node.data.items : [];
            data.level = node.data.level || _self.component.options.level + 1;
            node.data = data;
            zTree.updateNode(node);
            $.WCommonAlert('保存成功！');
            return;
          }
        }

        data.items = [];
        data.level = _self.component.options.level + 1;
        var zNode = {
          id: data.uuid,
          name: data.name,
          data: data
        };
        tabs.push(data);
        zTree.addNodes(null, zNode);
        zTree.selectNode(zTree.getNodesByParam('id', data.uuid, null)[0]);
        $.WCommonAlert('保存成功！');
      });
    };
    configurerPrototype.initFooterInfo = function (configuration, $container) {};
    // 收集配置信息
    configurerPrototype.collectConfiguration = function ($container) {
      var configuration = {};
      // 基本信息
      this.collectBaseInfo(configuration, $container);
      // 数据源
      this.collectHeaderInfo(configuration, $container);
      // 权限操作
      this.collectBodyInfo(configuration, $container);
      // 可视化
      this.collectFooterInfo(configuration, $container);
      return $.extend({}, configuration);
    };
    configurerPrototype.collectBaseInfo = function (configuration, $container) {
      var $form = $('#container_panel_tabs_base_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.hideHeader = Boolean(opt.hideHeader);
      opt.hideFooter = Boolean(opt.hideFooter);
      opt.showAsContainer = Boolean(opt.showAsContainer);
      opt.noPadding = Boolean(opt.noPadding);
      opt.bgTransparent = Boolean(opt.bgTransparent);
      opt.noMarginBottom = Boolean(opt.noMarginBottom);
      $.extend(configuration, opt);
    };
    configurerPrototype.collectHeaderInfo = function (configuration, $container) {
      var $form = $('#container_panel_tabs_header_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.showBadgeCount = Boolean(opt.showBadgeCount);
      opt.enableViewMore = Boolean(opt.enableViewMore);
      opt.enableQuery = Boolean(opt.enableQuery);
      opt.inputQueryShow = Boolean(opt.inputQueryShow);
      // 头部事件
      var $tableheaderEventsInfo = $('#table_headerEvents_info', $container);
      var headerEvents = $tableheaderEventsInfo.bootstrapTable('getData');
      opt.events = headerEvents;
      configuration.header = configuration.header || {};
      $.extend(configuration.header, opt);
    };
    configurerPrototype.collectBodyInfo = function (configuration, $container) {
      var _self = this;
      var $form = $('#container_panel_tabs_content_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      configuration.body = configuration.body || {};

      opt.contentType = $('input[name="contentType"]:checked').val();

      if (opt.contentType === 'contentTab') {
        opt.contentTabHeight = $('input[name="contentTabHeight"]').val();
        var nodes = _self.navTree.getNodes();
        opt.tabs = [];
        for (var i = 0; i < nodes.length; i++) {
          opt.tabs.push(nodes[i].data);
        }
        opt.pageUrl = '';
      } else {
        opt.tabs = [];
      }
      if (!configuration.activeTabUuid && opt.tabs.length) {
        configuration.activeTabUuid = opt.tabs[0].uuid;
      }
      $.extend(configuration.body, opt);
    };
    configurerPrototype.collectFooterInfo = function (configuration, $container) {};
    var configurer = $.ui.component.BaseComponentConfigurer(configurerPrototype);
    return configurer;
  };

  // 选择其他组件定义时，修改tab项uuid，防止存在引用多个同样组件时导致冲突
  component.prototype.changeWidgetUuidBeforeFromOtherDefintionLoad = function (definitionJson) {
    var tabs = definitionJson.configuration.body.tabs;
    if (tabs && tabs.length) {
      for (var i = 0; i < tabs.length; i++) {
        tabs[i].uuid = UUID.createUUID();
      }
    }
    return definitionJson;
  };

  // 返回组件定义
  component.prototype.getDefinitionJson = function ($element) {
    var _self = this;
    var definitionJson = _self.options;
    definitionJson.id = _self.getId();
    definitionJson.items = [];
    var children = _self.getChildren();
    $.each(children, function (i) {
      var child = this;
      definitionJson.items.push(child.getDefinitionJson());
    });
    return definitionJson;
  };
  return component;
});
