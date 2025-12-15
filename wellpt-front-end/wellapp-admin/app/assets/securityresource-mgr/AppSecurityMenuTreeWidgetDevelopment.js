define(['constant', 'commons', 'server', 'appModal', 'AppPtMgrDetailsWidgetDevelopment', 'AppPtMgrCommons', 'dataStoreBase'], function (
  constant,
  commons,
  server,
  appModal,
  AppPtMgrDetailsWidgetDevelopment,
  AppPtMgrCommons,
  DataStore,
  ztree
) {
  // 平台管理_产品集成_权限菜单资源树形_HTML组件二开
  var AppSecurityMenuTreeWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppSecurityMenuTreeWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;

      _self._ztreeInit();
      // 绑定事件
      _self._bindEvents();
    },

    _ztreeInit: function () {
      var _self = this;
      var zTree;
      //
      var treeSetting = {
        async: {
          enable: true,
          contentType: 'application/json',
          url: `/proxy/api/security/resource/getModuleMenuAsTree?uuid=-1&moduleId=${_self._moduleId()}`,
          type: 'POST'
        },
        callback: {
          beforeClick: function (treeId, treeNode) {
            if (treeNode.id != null && treeNode.id != -1) {
              // 查看详细
              _self.widget.trigger('AppSecurityMenuTreeView.editTreeNode', {
                uuid: treeNode.id,
                ui: _self.widget
              });
            }
            return true;
          },
          onAsyncSuccess: function (event, treeId, treeNode, msg) {
            var nodes = zTree.getNodes();
            var moduleName = $('.breadcrumb > li:last').text();
            if (moduleName) {
              nodes[0].name = moduleName; //模块名称
              zTree.updateNode(nodes[0]);
            }
            // 默认展开第一个节点
            if (nodes.length > 0) {
              var node = nodes[0];
              zTree.expandNode(node, true, false, false, true);
            }
          }
        }
      };

      zTree = $.fn.zTree.init($('#security_menu_tree'), treeSetting);

      _self.$securityMenuTree = zTree;
    },

    getSecurityMenuTree: function () {
      return this.$securityMenuTree;
    },

    _bindEvents: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var pageContainer = _self.getPageContainer();
      pageContainer.off('AppSecurityMenuTreeView.refreshDataDicZtree');
      pageContainer.on('AppSecurityMenuTreeView.refreshDataDicZtree', function (e) {
        var param = e.detail;
        refreshZtree('security_menu_tree', param.action, param.saveUuid, param.uuid, param.name);
        _self.refreshTabBage();
      });

      $('.btn_class_btn_add', $container).on('click', function () {
        _self.widget.trigger('AppSecurityMenuTreeView.editTreeNode', {
          uuid: null,
          ui: _self.widget
        });
        return false;
      });

      $('.btn_class_btn_delete', $container).on('click', function () {
        var selected = _self.getSecurityMenuTree().getSelectedNodes();
        if (selected.length == 1 && selected[0].id != -1) {
          appModal.confirm('确定要删除菜单资源[' + selected[0].name + ']吗？', function (result) {
            if (result) {
              server.JDS.restfulRequest({
                url: `/proxy/api/security/resource/remove/${selected[0].id}`,
                type: 'DELETE',
                success: function (result) {
                  appModal.success('删除成功!');
                  refreshZtree('security_menu_tree', 'delete');
                  _self.refreshTabBage();
                  _self.widget.trigger('AppSecurityMenuTreeView.editTreeNode', {
                    uuid: null,
                    ui: _self.widget
                  });
                }
              });
            }
          });
        }
        return false;
      });

      $('.li_class_definition_export', $container).on('click', function () {
        _self.definition_export();
        return false;
      });

      $('.li_class_definition_import', $container).on('click', function () {
        _self.definition_import();
        return false;
      });

      $('.btn-query', $container).on('click', function () {
        var nodeName = $("input[type='search']", $container).val();
        searchZtreeNode(0, 'security_menu_tree', nodeName, function (uuid) {
          var node = _self._getNodeByUuid(uuid, null);
          _self.widget.trigger('AppSecurityMenuTreeView.editTreeNode', {
            uuid: uuid,
            ui: _self.widget
          });
        });
        return false;
      });

      $('.btn-reset', $container).on('click', function () {
        $("input[type='search']", $container).val('');
      });
    },

    _getNodeByUuid: function (uuid, parentNode) {
      return this.getSecurityMenuTree().getNodesByParamFuzzy('uuid', uuid, parentNode);
    },

    refreshTabBage: function () {
      //刷新徽章
      var tabpanel = this.widget.element.parents('.active');
      if (tabpanel.length > 0) {
        var id = tabpanel.attr('id');
        id = id.substring(0, id.indexOf('-'));
        $('#' + id).trigger(constant.WIDGET_EVENT.BadgeRefresh, {
          targetTabName: '菜单/按钮'
        });
      }
      return true;
    },

    definition_import: function () {
      var _self = this;
      // 定义导入
      $.iexportData['import']({
        callback: function () {
          _self.getSecurityMenuTree().refresh();
        }
      });
    },

    definition_export: function () {
      // 定义导出
      var selected = this.getSecurityMenuTree().getSelectedNodes();
      if (selected.length == 1) {
        $.iexportData['export']({
          uuid: selected[0].id,
          type: 'resource'
        });
      } else {
        appModal.alert('请选择导出的菜单资源定义！');
      }
    },

    _moduleId: function () {
      return this.getWidgetParams().moduleId || '';
    }
  });
  return AppSecurityMenuTreeWidgetDevelopment;
});
