define(['constant', 'commons', 'server', 'appModal', 'AppPtMgrDetailsWidgetDevelopment', 'AppPtMgrCommons', 'dataStoreBase'], function (
  constant,
  commons,
  server,
  appModal,
  AppPtMgrDetailsWidgetDevelopment,
  AppPtMgrCommons,
  DataStore
) {
  var validator;
  var listView;
  // 平台管理_产品集成_权限详情_HTML组件二开
  var AppSecurityPrivilegeDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppSecurityPrivilegeDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;
      this.otherResIds = [];
      // 验证器
      validator = server.Validation.validate({
        beanName: 'privilege',
        container: this.widget.element,
        wrapperForm: true
      });

      _self.piTreeId = 'prod_integrate_tree' + commons.UUID.createUUID();

      $('.prod_integrate_tree', this.widget.element).attr('id', _self.piTreeId);

      _self._formInputRender();

      if (_self.getWidgetParams().appSystemUuid) {
        _self._initProductionIntegrationTree();
      } else {
        _self.loadOtherResourceTree(null);
      }

      // 绑定事件
      _self._bindEvents();
    },

    _initProductionIntegrationTree: function () {
      var _self = this;
      var $container = this.widget.element;
      var zTree;
      var dataDataDictionarySetting = {
        check: {
          enable: true,
          chkboxType: {
            Y: 's',
            N: 'ps'
          }
        }
      };

      server.JDS.call({
        service: 'appSystemMgr.getTreeByAppSystemUuid',
        data: [_self.getWidgetParams().appSystemUuid],
        version: '',
        success: function (result) {
          if (result.success) {
            var nodes = result.data;
            for (var n = 0, nlen = nodes.length; n < nlen; n++) {
              var node = nodes[n];
              var appId = _self._appId();
              if (_self.getWidgetParams().appProdIntegrateType == '模块') {
                //模块配置下的产品集成树，只能勾选到当前模块以及其下子节点
                node.nocheck = true;
                var moduleNodes = node.children;
                //只展示该模块的树
                var matchModuleNodes = [];
                _self.extractModuleNodes(appId, moduleNodes, matchModuleNodes);
                node.children = matchModuleNodes;
                //                                    for (var i = 0; i < moduleNodes.length; i++) {
                //                                        if (moduleNodes[i].data && ((moduleNodes[i].data.id !== appId && moduleNodes[i].data.type == 2)
                //                                            || moduleNodes[i].data.type !== 2)) {//非当前模块的，并且非模块类型的节点，进行移除
                //                                            moduleNodes.splice(i, 1);
                //                                            i--;
                //                                        }
                //                                    }
              } else if (_self.getWidgetParams().appProdIntegrateType == '应用') {
                //应用配置下的产品集成树，只能勾选到当前应用以及其下子节点
                node.nocheck = true;
                var moduleNodes = node.children;

                //只展示该应用的树
                for (var i = 0; i < moduleNodes.length; i++) {
                  moduleNodes[i].nocheck = true; //模块节点不可选
                  var appModes = moduleNodes[i].children;
                  var getApp = false;
                  for (var j = 0; j < appModes.length; j++) {
                    if (appModes[j].data && appModes[j].data.id === appId) {
                      getApp = true;
                      moduleNodes[i].nocheck = true; //模块不能选择
                      continue;
                    }
                    appModes.splice(j, 1); //非当前应用的节点，进行移除
                    j--;
                  }
                  if ((!getApp && moduleNodes[i].data && moduleNodes[i].data.type == 2) || moduleNodes[i].data.type !== 2) {
                    //非当前应用的模块、或者非模块类型的节点，进行移除
                    moduleNodes.splice(i, 1);
                    i--;
                  }
                }
              } else if (_self.getWidgetParams().appProdIntegrateType == '系统') {
                var sChildren = nodes[n].children;
                if (sChildren.length > 0) {
                  for (var k = 0; k < sChildren.length; k++) {
                    var sName = sChildren[k].name;
                    if (sName.indexOf('页面') != 0) {
                      continue;
                    }
                    sChildren.splice(k, 1);
                    k--;
                  }
                }
              }
            }

            zTree = $.fn.zTree.init($('#' + _self.piTreeId, $container), dataDataDictionarySetting, result.data);
            _self.$prodIntegrateTree = zTree;
            // 默认展开第一个节点
            if (nodes.length > 0) {
              var node = zTree.getNodes()[0];
              zTree.expandNode(node, true, false, false, true);
            }
          }
        },
        async: false
      });
    },

    // 提取模块信息
    extractModuleNodes: function (appId, moduleNodes, matchModuleNodes) {
      var _self = this;
      for (var i = 0; i < moduleNodes.length; i++) {
        var moduleNode = moduleNodes[i];
        if (moduleNode.data && moduleNode.data.id == appId && moduleNode.data.type == 2) {
          matchModuleNodes.push(moduleNode);
        } else if (moduleNode.children && moduleNode.children.length > 0) {
          _self.extractModuleNodes(appId, moduleNode.children, matchModuleNodes);
        }
      }
    },

    _formInputRender: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      $('#appId', $container).val(_self._appId());
      var serviceName;
      if (this._appType() === '模块') {
        serviceName = 'appModuleMgr';
      } else if (this._appType() === '应用') {
        serviceName = 'appApplicationMgr';
      } else if (this._appType() === '系统') {
        serviceName = 'appSystemMgr';
      }
      $('#appId', $container).prop('readonly', $('#appId', $container).val() != '');
      if (serviceName) {
        $('#appId', $container).wSelect2({
          valueField: 'appId',
          remoteSearch: false,
          serviceName: serviceName,
          queryMethod: 'loadSelectData',
          params: {
            //systemUnitId:server.SpringSecurityUtils.getCurrentUserUnitId()
          }
        });
      } else {
        $('#appId', $container).wSelect2({
          valueField: 'appId',
          remoteSearch: false,
          serviceName: 'appSystemMgr',
          queryMethod: 'loadSelectData',
          params: {
            // systemUnitId: server.SpringSecurityUtils.getCurrentUserUnitId()
          }
        });
      }
    },

    _appId: function (type) {
      var widgetParams = this.getWidgetParams();
      var piTypeProperty = { 模块: 'moduleId', 应用: 'appId', 系统: 'systemId' };
      return widgetParams[piTypeProperty[type ? type : widgetParams.appProdIntegrateType]];
    },

    _appType: function () {
      return this.getWidgetParams().appProdIntegrateType;
    },
    loadOtherResourceTree: function (uuid) {
      var _self = this;
      var privilege = {};
      _self.otherResIds = [];
      privilege.uuid = uuid;
      var setting = {
        check: {
          enable: true,
          chkboxType: {
            Y: 's',
            N: 'ps'
          }
        }
      };
      server.JDS.restfulGet({
        url: `/proxy/api/security/privilege/getOtherResourceTree/${uuid}`,
        success: function (result) {
          var data = result.data[0].children;
          for (var i = 0; i < data.length; i++) {
            data[i].name = '产品: ' + data[i].name;
            data[i].nocheck = false;
            var children = data[i].children;
            if (data[i].data && data[i].data['type'] == 1) {
              if (children.length > 0) {
                for (var h = 0; h < children.length; h++) {
                  var name = children[h].name;
                  if (name.indexOf('页面') != 0) {
                    continue;
                  }
                  children.splice(h, 1);
                  h--;
                }
              }
            } else if (children.length > 0) {
              for (var j = 0; j < children.length; j++) {
                if (children[j].data && children[j].data['type'] == 1) {
                  var child = children[j].children;
                  for (var k = 0; k < child.length; k++) {
                    var name1 = child[k].name;
                    if (name1.indexOf('页面') != 0) {
                      continue;
                    }
                    child.splice(k, 1);
                    k--;
                  }
                }
              }
            }
          }
          var otherResTree = $.fn.zTree.init($('#' + _self.piTreeId), setting, data);
          _self.$prodIntegrateTree = otherResTree;
          $.each(data, function () {
            var root = this;
            _self.otherResIds.push(root.id);
          });
        }
      });
    },
    toOtherResourceTree: function (bean) {
      var _self = this;
      if (_self.otherResIds.length == 0) {
        _self.loadOtherResourceTree(bean.uuid);
      } else {
        $.each(this.otherResIds, function () {
          var otherResTree = $.fn.zTree.getZTreeObj(_self.piTreeId);
          // 取消树选择
          otherResTree.checkAllNodes(false);

          var otherReses = bean['otherResources'];
          if (otherReses) {
            for (var index = 0; index < otherReses.length; index++) {
              var node = otherResTree.getNodeByParam('id', otherReses[index].resourceUuid);
              if (node) {
                otherResTree.checkNode(node, true);
              }
            }
          }
        });
      }
    },
    _bean: function () {
      return {
        uuid: null,
        name: null,
        code: null,
        enabled: null,
        remark: null,
        roles: [],
        resources: [],
        recVer: null,
        otherResources: [],
        categoryName: null,
        categoryUuid: null,
        appId: this._appId()
      };
    },

    _bindEvents: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var pageContainer = _self.getPageContainer();

      // 新增
      pageContainer.off('AppSecurityPrivilegeListView.editRow');
      pageContainer.on('AppSecurityPrivilegeListView.editRow', function (e) {
        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });
        if (_self.getWidgetParams().appSystemUuid) {
          _self._initProductionIntegrationTree();
        } else {
          _self.toOtherResourceTree(e.detail.rowData || {});
        }

        $('#appId', $container).val(_self._appId()).trigger('change');
        if (e.detail.rowData) {
          //编辑
          server.JDS.restfulGet({
            url: `/proxy/api/security/privilege/getPrivilegeBean/${e.detail.rowData.uuid}`,
            success: function (result) {
              if (result.code == 0) {
                var bean = _self._bean();
                $.extend(bean, result.data);
                bean = result.data;
                //初始化已选树节点
                if (_self.$prodIntegrateTree) {
                  for (var index = 0; index < bean.otherResources.length; index++) {
                    var node = _self.$prodIntegrateTree.getNodeByParam('id', bean.otherResources[index].resourceUuid);
                    if (node) {
                      _self.$prodIntegrateTree.checkNode(node, true);
                    }
                  }
                } else {
                  //初始化已选树节点还未加载等待1秒处理
                  setTimeout(function () {
                    for (var index = 0; index < bean.otherResources.length; index++) {
                      var node = _self.$prodIntegrateTree.getNodeByParam('id', bean.otherResources[index].resourceUuid);
                      if (node) {
                        _self.$prodIntegrateTree.checkNode(node, true);
                      }
                    }
                  }, 1000);
                }

                AppPtMgrCommons.json2form({
                  json: bean,
                  container: $container
                });
                $('#appId', $container).trigger('change');
                validator.form();
              }
            }
          });
        } else {
          AppPtMgrCommons.idGenerator.generate($container.find('#code'), 'PRIVILEGE_');
          //$("#code", $container).val($("#id", $container).val().replace('PRIVILEGE_', ''));
        }

        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');
        listView = e.detail.ui;
      });

      $('#btn_save_privilege', $container).on('click', function () {
        _self.savePrivilege();
        return false;
      });

      $('#searchPrivilegePiTreeBtn', $container).on('click', function () {
        var nodeName = $('#searchPrivilegePiTree', $container).val();
        if (nodeName != '') {
          searchZtreeNode(0, _self.piTreeId, nodeName, function (uuid) {});
          return false;
        }
      });

      $('#searchPrivilegePiTree', $container).on('keyup', function (e) {
        if (e.keyCode === 13) {
          $('#searchPrivilegePiTreeBtn', $container).trigger('click');
        }
      });
    },

    savePrivilege: function () {
      var _self = this;
      var $container = $(this.widget.element);
      var bean = _self._bean();
      AppPtMgrCommons.form2json({
        json: bean,
        container: $container
      });

      bean.systemUnitId = server.SpringSecurityUtils.getCurrentUserUnitId();

      bean['otherResources'] = [];
      var zTree = _self.$prodIntegrateTree;
      var checkNodes = zTree.getCheckedNodes(true);
      $.each(checkNodes, function (index) {
        var othersResource = {};
        othersResource.type = 'appProductIntegration';
        othersResource.resourceUuid = this.id;
        if (this.id < 0) {
          return true;
        }
        bean['otherResources'].push(othersResource);
      });

      if (!validator.form()) {
        return false;
      }
      server.JDS.restfulPost({
        url: '/proxy/api/security/privilege/saveBean',
        data: bean,
        success: function (result) {
          if (result.code == 0) {
            appModal.success('保存成功！', function () {
              // 保存成功刷新列表
              listView.trigger('AppSecurityPrivilegeListView.refresh');
              // 清空表单
              AppPtMgrCommons.clearForm({
                container: $container,
                includeHidden: true
              });
              if (bean.uuid != '') {
                $.get('/security/privilege/publishPrivilegeUpdatedEvent', { uuid: bean.uuid }, function () {});
              }
            });
          }
        }
      });
    }
  });
  return AppSecurityPrivilegeDetailsWidgetDevelopment;
});
