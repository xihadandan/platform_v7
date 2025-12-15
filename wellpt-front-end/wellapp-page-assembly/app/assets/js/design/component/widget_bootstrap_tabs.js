define(['ui_component', 'constant', 'commons', 'server', 'formBuilder', 'appContext', 'design_commons', 'wSelect2'], function (
  ui_component,
  constant,
  commons,
  server,
  formBuilder,
  appContext,
  designCommons
) {
  var StringBuilder = commons.StringBuilder;
  var UUID = commons.UUID;
  var collectClass = 'w-configurer-option';
  var StringUtils = commons.StringUtils;
  var component = $.ui.component.BaseComponent();

  var checkRequire = function (propertyNames, options, $container) {
    for (var i = 0; i < propertyNames.length; i++) {
      var propertyName = propertyNames[i];
      if (StringUtils.isBlank(options[propertyName])) {
        var title = $("label[for='" + propertyName + "']", $container).text();
        appModal.error(title.replace('*', '') + '不允许为空！');
        return false;
      }
    }
    return true;
  };

  var configurer = $.ui.component.BaseComponentConfigurer();
  component.prototype.create = function () {
    // $(this.element).find(".widget-body").html(this.options.content);

    var _self = this;
    var options = _self.options;
    var $element = $(_self.element);
    var $tabsContainer = $element.find('.ui-sortable');
    _self.pageDesigner.sortable(_self, $tabsContainer, $tabsContainer);

    // 初始化容器结点
    if (options.items != null) {
      $.each(options.items, function (j, item) {
        var $draggable = _self.pageDesigner.createDraggableByDefinitionJson(item);
        _self.pageDesigner.drop(_self, $tabsContainer, $draggable, item);
      });
    }
  };
  component.prototype.onPreivew = function (container) {
    var _self = this;
    var $widget = $(_self.element);
    // 有放置组件不进行预览
    var $panelBody = $widget.children('.widget-body').find('.ui-sortable');
    if ($panelBody.children().length > 0 && _self.isPreivew != true) {
      return;
    }
    if (_self.isPreivew == true) {
      $panelBody.html('');
    }
    var html = _self.toPreviewHtml();
    $panelBody.html(html);
    _self.isPreivew = true;
  };
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  configurer.prototype.initBaseInfo = function (configuration, $container) {
    // 二开JS模块
    $('#jsModule', $container).wSelect2({
      serviceName: 'appJavaScriptModuleMgr',
      params: {
        dependencyFilter: 'BootstrapTabsWidgetDevelopment'
      },
      labelField: 'jsModuleName',
      valueField: 'jsModule',
      remoteSearch: false,
      multiple: true
    });
    // 页面风格
    $('#tabStyle', $container)
      .on('change', function () {
        var tabStyle = $(this).val();
        $('.tab-style', $container).hide();
        $('.tab-style-' + tabStyle, $container).show();
      })
      .trigger('change');
  };

  configurer.prototype.initTabsInfo = function (configuration, $container) {
    var _self = this;

    $('#navTree_uuid', $tabsForm).val(UUID.createUUID());

    var tabs = configuration.tabs ? configuration.tabs : [];
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
          if (data.items && data.items.length) {
            $('.not-drag-component', $tabsForm).hide();
          } else {
            $('.not-drag-component', $tabsForm).show();
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
          $("input[name='eventHashType']", $tabsForm).each(function () {
            if (this.value == data.eventHandler.hashType) {
              this.checked = true;
            }
          });
          $("input[name='eventHashType']", $tabsForm).trigger('change');
          if (designCommons.isSupportsAppHashByAppPath(data.eventHandler.path)) {
            $("input[name='eventHashType']", $tabsForm).removeAttr('disabled');
          } else {
            $("input[name='eventHashType']", $tabsForm).attr('disabled', 'disabled');
          }
          $('#eventHashTree', $tabsForm).wCommonComboTree({
            value: data.eventHandler.hash
          });

          $('#navTree_isShowBadge')
            .prop('checked', $('#navTree_isShowBadge').val() == '1')
            .trigger('change');
          $('#navTree_clickTabToRefresh').prop('checked', $('#navTree_clickTabToRefresh').val() == '1');

          if (data.icon && data.icon.className) {
            $('#navTreeIconRemoveBtn', $container).show();
          } else {
            $('#navTreeIconRemoveBtn', $container).hide();
          }
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
      $('eventHashType', $tabsForm).val('');
      $('eventHash', $tabsForm).val('');
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
    });

    // 删除
    $('#widget_bootstrap_tabs_nav_remove').on('click', function () {
      appModal.confirm('确认要删除吗？', function (result) {
        if (result) {
          clearNavTree();
          var selectedNode = getSelectedNode();
          var data = selectedNode.data;
          zTree.removeNode(selectedNode);

          for (var i = 0; i < tabs.length; i++) {
            if (tabs[i].uuid == data.uuid) {
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
      if (selectedNodes.length == 0) {
        return null;
      }
      return selectedNodes[0];
    };

    // 选择图标
    $('#navTreeIconSelectBtn', $tabsForm).on('click', function () {
      var _thiz = this;
      $.WCommonPictureLib.show({
        selectTypes: [3],
        confirm: function (data) {
          var fileIDs = data.fileIDs;
          $('#navTree_icon_className', $tabsForm).val(fileIDs);
          $('#navTreeIconSnap', $tabsForm).attr('iconClass', fileIDs);
          $('#navTreeIconSnap', $tabsForm).attr('class', fileIDs);
          if (data) $('#navTreeIconRemoveBtn', $container).show();
        }
      });
    });
    // 移除图标
    $('#navTreeIconRemoveBtn', $container).on('click', function () {
      $('#navTree_icon_className').val('');
      $('#navTreeIconSnap', $container).attr('iconClass', '');
      $('#navTreeIconSnap', $container).attr('class', '');
      $('#navTreeIconRemoveBtn', $container).hide();
    });
    $('#navTree_eventHandler_name', $tabsForm).AppEvent({
      ztree: {
        params: [productUuid]
      },
      okCallback: function ($el, data) {
        if (data) {
          $('#navTree_eventHandler_id', $tabsForm).val(data.id);
          $('#navTree_eventHandler_type', $tabsForm).val(data.data.type);
          $('#navTree_eventHandler_path', $tabsForm).val(data.data.path);
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
        }
      },
      clearCallback: function ($el) {
        $('#navTree_eventHandler_id,#navTree_eventHandler_type,#navTree_eventHandler_path', $container).val('');
        // 锚点设置
        $("input[name='eventHashType']", $container).removeAttr('checked');
        $("input[name='eventHashType']", $container).trigger('change');
        $("input[name='eventHashType']", $container).attr('disabled', 'disabled');
        $("input[name='eventHash']", $container).val('');
        $('#eventHashTree', $container).wCommonComboTree({
          value: ''
        });
      }
    });
    // 锚点设置单选框取消选中
    $("input[name='eventHashType']", $container)
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
    $("input[name='eventHashType']", $container)
      .on('change', function () {
        var eventHashType = $("input[name='" + this.name + "']:checked", $container).val();
        $('.eventHashTypeRow', $container).hide();
        if (eventHashType == '1') {
          $('.eventHashType' + eventHashType, $container).show();
          // 事件处理
          $('#eventHashTree', $container).wCommonComboTree({
            service: 'pageDefinitionService.getAppHashTreeByAppPath',
            serviceParams: [$('#navTree_eventHandler_path', $container).val()],
            multiSelect: false, // 是否多选
            parentSelect: false, // 父节点选择有效，默认无效
            onAfterSetValue: function (event, self, value) {
              $('#eventHash', $container).val(value);
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
          $('.eventHashType' + eventHashType, $container).show();
        }
      })
      .trigger('change');

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
        {
          id: 'countJs',
          text: '通过数量统计JS脚本'
        },
        {
          id: 'datastore',
          text: '通过数据仓库获取数量'
        }
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
        if (fields.length == 2) {
          data[fields[1]] = $(this).val();
        } else if (fields.length == 3) {
          if (!data[fields[1]]) {
            data[fields[1]] = {};
          }
          data[fields[1]][fields[2]] = $(this).val();
        }
      });
      $('input[type="hidden"]', $tabsForm).each(function () {
        var id = $(this).attr('id');
        var fields = id.split('_');
        if (fields.length == 2) {
          data[fields[1]] = $(this).val();
        } else if (fields.length == 3) {
          if (!data[fields[1]]) {
            data[fields[1]] = {};
          }
          data[fields[1]][fields[2]] = $(this).val();
        }
      });

      data['isShowBadge'] = $('#navTree_isShowBadge').prop('checked') ? '1' : '0';
      data['clickTabToRefresh'] = $('#navTree_clickTabToRefresh').prop('checked') ? '1' : '0';

      // 锚点设置收集及验证
      if (data.eventHandler) {
        data.eventHandler.hashType = $("input[name='eventHashType']:checked", $tabsForm).val();
        data.eventHandler.hash = $('#eventHash', $tabsForm).val();
        if (StringUtils.isNotBlank(data.eventHandler.hashType) && StringUtils.isBlank(data.eventHandler.hash)) {
          appModal.warning('锚点设置不能为空！');
          return;
        }
      }

      for (var i = 0; i < tabs.length; i++) {
        if (tabs[i].uuid == $('#navTree_uuid').val()) {
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
  configurer.prototype.onLoad = function ($container, options) {
    var configuration = $.extend(true, {}, options.configuration);
    $('#widget_tree_tabs ul a', $container).on('click', function (e) {
      e.preventDefault();
      $(this).tab('show');
    });
    designCommons.setElementValue(configuration, $container, 'tabs');
    // 初始化基础信息tab
    this.initBaseInfo(configuration, $container);
    // 初始化页签tab
    this.initTabsInfo(configuration, $container);
  };

  configurer.prototype.onOk = function ($container) {
    if (this.component.isReferenceWidget()) {
      return;
    }
    var _self = this;
    var opt = designCommons.collectConfigurerData($('#widget_tree_tabs_base_info', $container), collectClass);
    var nodes = _self.navTree.getNodes();
    opt.tabs = [];
    for (var i = 0; i < nodes.length; i++) {
      opt.tabs.push(nodes[i].data);
    }
    var requeryFields = ['name'];
    if (!checkRequire(requeryFields, opt, $container)) {
      return false;
    }
    this.component.options.configuration = $.extend({}, opt);
  };

  component.prototype.getPropertyConfigurer = function () {
    return configurer;
  };
  // 返回门户配置器
  component.prototype.getPortalPropertyConfigurer = function () {
    return {
      onLoad: function ($container, options) {
        var _self = this;
        var $portalTabs = $('<div class="portalTabs"></div>');
        var name = 'portalTabs';
        $container.append($portalTabs);
        var tabs = options.configuration.tabs || [];
        $.each(tabs, function (i, tab) {
          tab.text = tab.name;
          tab.appId = tab.uuid;
        });
        var apps = _self.getPortalApps();
        $.each(apps, function (i, app) {
          app.name = app.text;
        });
        apps = tabs.concat(apps);
        _self.apps = apps;
        formBuilder.bootstrapTable.build({
          container: $portalTabs,
          name: name,
          ediableNest: true,
          table: {
            data: tabs,
            striped: true,
            idField: 'uuid',
            onEditableSave: function (field, row, oldValue, $el) {
              // 选择应用时，名称为空，设置名称为选择的应用名称
              if (field == 'appId' && StringUtils.isBlank(row.text)) {
                $.each(apps, function (index, app) {
                  if (app.uuid == row.appId) {
                    row.text = app.text;
                  }
                });
                var $tablePortalTabsInfo = $('#table_portalTabs_info', $container);
                var dataList = $tablePortalTabsInfo.bootstrapTable('getData');
                $.each(dataList, function (index, rowData) {
                  if (row == rowData) {
                    $tablePortalTabsInfo.bootstrapTable('updateRow', index, rowData);
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
                title: '页签名称',
                width: '50%',
                editable: {
                  type: 'text',
                  showbuttons: false,
                  onblur: 'submit',
                  mode: 'inline',
                  validate: function (value) {
                    if (StringUtils.isBlank(value)) {
                      return '请输入名称!';
                    }
                  }
                }
              },
              {
                field: 'appId',
                title: '页签内容',
                width: '50%',
                editable: {
                  type: 'select',
                  mode: 'inline',
                  onblur: 'submit',
                  showbuttons: false,
                  savenochange: true,
                  source: $.map(apps, function (app) {
                    return {
                      text: app.text,
                      value: app.uuid
                    };
                  }),
                  validate: function (value) {
                    if (StringUtils.isBlank(value)) {
                      return '请选择应用!';
                    }
                  }
                }
              }
            ]
          }
        });
      },
      onOk: function ($container) {
        var _self = this;
        var apps = _self.apps || [];
        var appMap = {};
        $.each(apps, function (i, app) {
          appMap[app.uuid] = app;
        });

        var $tablePortalTabsInfo = $('#table_portalTabs_info', $container);
        var portalTabs = $tablePortalTabsInfo.bootstrapTable('getData');
        var newTabs = [];
        for (var i = 0; i < portalTabs.length; i++) {
          var portalTab = portalTabs[i];
          // 检查数据有效性
          if (StringUtils.isBlank(portalTab.text)) {
            appModal.error('页签名称不能为空！');
            return false;
          }
          // 放入新的tabs数据
          if (appMap[portalTab.appId]) {
            var newTab = $.extend(true, {}, appMap[portalTab.appId]);
            newTab.name = portalTab.text;
            delete newTab.text;
            newTabs.push(newTab);
          }
        }
        _self.component.options.configuration.tabs = newTabs;
      }
    };
  };
  component.prototype.toHtml = function () {
    var _self = this;
    var options = _self.options;
    var children = _self.getChildren();
    var id = _self.getId();
    var configuration = options.configuration || {};
    var tabStyle = configuration.tabStyle;
    var tabColspan = configuration.tabColspan || 3;
    var html = new StringBuilder();
    html.appendFormat("<div id='{0}' class='ui-wBootstrapTabs'>", id);
    // 左侧tabs
    if (tabStyle == 'left') {
      html.appendFormat("<div class='row row-tabs row-tabs-left'>");
      // Nav tabs
      html.appendFormat("<div class='col-xs-{0}'>", tabColspan);
      html.appendFormat("<ul class='nav nav-tabs tabs-left js-nav-tabs' role='tablist'></ul>");
      html.appendFormat('</div>');
      // Tab panes
      html.appendFormat("<div class='col-xs-{0}'>", 12 - parseInt(tabColspan));
      html.appendFormat("<div class='tab-content js-tab-content'></div>");
      html.appendFormat('</div>');
      html.appendFormat("<div class='clearfix'></div>");
      html.appendFormat('</div>');
    } else if (tabStyle == 'right') {
      // 右侧tabs
      html.appendFormat("<div class='row row-tabs row-tabs-right'>");
      // Tab panes
      html.appendFormat("<div class='col-xs-{0}'>", 12 - parseInt(tabColspan));
      html.appendFormat("<div class='tab-content js-tab-content'></div>");
      html.appendFormat('</div>');
      // Nav tabs
      html.appendFormat("<div class='col-xs-{0}'>", tabColspan);
      html.appendFormat("<ul class='nav nav-tabs tabs-right js-nav-tabs' role='tablist'></ul>");
      html.appendFormat('</div>');
      html.appendFormat("<div class='clearfix'></div>");
      html.appendFormat('</div>');
    } else {
      html.appendFormat('<div class="bootstrap-tabs-container"><div class="nav-tabs-box">');
      // Nav tabs
      html.appendFormat("<ul class='nav nav-tabs js-nav-tabs' role='tablist'></ul>");
      html.appendFormat('</div></div>');
      // Tab panes
      html.appendFormat("<div class='tab-content js-tab-content'></div>");
    }
    if (children != null) {
      $.each(children, function (i) {
        var child = this;
        var childHtml = child.toHtml.call(child);
        html.append(childHtml);
      });
    }
    html.append('</div>');
    return html.toString();
  };
  component.prototype.toPreviewHtml = function () {
    var _self = this;
    var options = _self.options;
    var children = _self.getChildren();
    var id = _self.getId();
    var configuration = options.configuration || {};
    var tabStyle = configuration.tabStyle;
    var tabColspan = configuration.tabColspan || 3;
    var lis = new StringBuilder();
    var contents = new StringBuilder();
    $.each(configuration.tabs, function (i, tab) {
      lis.appendFormat('<li role="presentation" id="{0}" class="{1}">', tab.uuid, i == 0 ? 'active' : '');
      lis.appendFormat('<a href="#{0}" role="tab" data-toggle="tab" >{1}</a>', tab.uuid + '_' + i, tab.name);
      lis.appendFormat('</li>');
      contents.appendFormat('<div role="tabpanel" class="tab-pane {1}" id="{0}">', tab.uuid + '_' + i, i == 0 ? 'active' : '');
      contents.appendFormat('</div>');
    });
    var html = new StringBuilder();
    html.appendFormat("<div id='{0}' class='ui-wBootstrapTabs'>", id);
    // 左侧tabs
    if (tabStyle == 'left') {
      html.appendFormat("<div class='row row-tabs row-tabs-left'>");
      // Nav tabs
      html.appendFormat("<div class='col-xs-{0}'>", tabColspan);
      html.appendFormat("<ul class='nav nav-tabs tabs-left js-nav-tabs' role='tablist'></ul>");
      html.appendFormat('</div>');
      // Tab panes
      html.appendFormat("<div class='col-xs-{0}'>", 12 - parseInt(tabColspan));
      html.appendFormat("<div class='tab-content js-tab-content'></div>");
      html.appendFormat('</div>');
      html.appendFormat("<div class='clearfix'></div>");
      html.appendFormat('</div>');
    } else if (tabStyle == 'right') {
      // 右侧tabs
      html.appendFormat("<div class='row row-tabs row-tabs-right'>");
      // Tab panes
      html.appendFormat("<div class='col-xs-{0}'>", 12 - parseInt(tabColspan));
      html.appendFormat("<div class='tab-content js-tab-content'></div>");
      html.appendFormat('</div>');
      // Nav tabs
      html.appendFormat("<div class='col-xs-{0}'>", tabColspan);
      html.appendFormat("<ul class='nav nav-tabs tabs-right js-nav-tabs' role='tablist'></ul>");
      html.appendFormat('</div>');
      html.appendFormat("<div class='clearfix'></div>");
      html.appendFormat('</div>');
    } else {
      // Nav tabs
      html.appendFormat('<div class="bootstrap-tabs-container"><div class="nav-tabs-box">');
      html.appendFormat("<ul class='nav nav-tabs js-nav-tabs' role='tablist'>");
      html.appendFormat(lis.toString());
      html.appendFormat('</ul></div></div>');
      // Tab panes
      html.appendFormat("<div class='tab-content js-tab-content'>");
      html.appendFormat(contents.toString());
      html.appendFormat('</div>');
    }
    if (children != null) {
      $.each(children, function (i) {
        var child = this;
        var childHtml = child.toHtml.call(child);
        html.append(childHtml);
      });
    }
    html.append('</div>');
    return html.toString();
  };
  component.prototype.getDefinitionJson = function () {
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
