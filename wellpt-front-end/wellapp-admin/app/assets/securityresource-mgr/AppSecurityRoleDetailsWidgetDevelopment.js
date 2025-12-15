define([
  'constant',
  'commons',
  'server',
  'appModal',
  'AppPtMgrDetailsWidgetDevelopment',
  'AppPtMgrCommons',
  'dataStoreBase',
  'multiOrg'
], function (constant, commons, server, appModal, AppPtMgrDetailsWidgetDevelopment, AppPtMgrCommons, DataStore, multiOrg) {
  var validator;
  var listView;
  var orgUserTypes = {
    U: '人员',
    B: '单位',
    D: '部门',
    J: '职位',
    G: '群组',
    O: '组织节点',
    DU: '职务'
  };

  var iconUrl = {
    role: '../../../../static/js/pt/img/role.png',
    right: '../../../../static/js/pt/img/right.png'
  };

  // 平台管理_产品集成_角色详情_HTML组件二开
  var AppSecurityRoleDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppSecurityRoleDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;
      // 验证器
      validator = server.Validation.validate({
        beanName: 'role',
        container: this.widget.element,
        wrapperForm: true
      });

      _self.rolePrivilegeTreeId = 'role_privilege_tree' + commons.UUID.createUUID();
      $('.role_privilege_tree', this.widget.element).attr('id', _self.rolePrivilegeTreeId);

      _self.privilegeRstTreeId = 'privilege_result_tree' + commons.UUID.createUUID();
      $('.privilege_result_tree', this.widget.element).attr('id', _self.privilegeRstTreeId);

      _self._formInputRender();

      _self._loadRolePrivilegeTreeNodes();

      _self._initRoleRelaUserTable();

      // 绑定事件
      _self._bindEvents();
    },

    _initRoleRelaUserTable: function () {
      var $container = this.widget.element;
      var _self = this;

      var $userTable = $('#table_role_used_info', $container);
      $userTable.bootstrapTable('destroy').bootstrapTable({
        data: [],
        idField: 'GUUID',
        striped: false,
        showColumns: false,
        width: 500,
        height: 600,
        columns: [
          {
            field: 'GUUID',
            title: 'GUUID',
            visible: false
          },
          {
            field: 'name',
            title: '名称'
          },
          {
            field: 'type',
            title: '类型',
            formatter: function (v, r, i) {
              return orgUserTypes[v] || '';
            }
          }
        ]
      });
    },

    _refreshRoleRelaUserTable: function (memberIds, memberNames) {
      if (memberIds && memberNames) {
        var ids = memberIds.split(';');
        var names = memberNames.split(';');
        var rows = [];
        for (var i = 0, len = ids.length; i < len; i++) {
          rows.push({
            GUUID: ids[i],
            name: names[i],
            type: ids[i].substr(0, 1)
          });
        }
        $('#table_role_used_info', this.widget.element).bootstrapTable('load', rows);
        $('#table_role_used_info', this.widget.element).bootstrapTable('resetView');
      }
    },

    _loadRolePrivilegeTreeNodes: function (uuid) {
      var _self = this;
      server.JDS.restfulGet({
        url: `/proxy/api/security/role/getRolePrivilegeTree?uuid=${uuid == undefined ? '' : uuid}&appId=${
          _self._appId() == undefined ? '' : _self._appId()
        }`,
        success: function (result) {
          if (result.code == 0) {
            var nodes = result.data.children;
            //按角色、权限分组
            var nodeGroups = _.groupBy(nodes, function (n) {
              return n.id.substr(0, 1);
            });

            nodeGroups.R &&
              nodeGroups.R.forEach(function (item) {
                item.icon = iconUrl.role;
              });
            nodeGroups.R &&
              nodeGroups.P.forEach(function (item) {
                item.icon = iconUrl.right;
              });

            var treeData = [];
            treeData.push({
              id: '1',
              name: '角色',
              nocheck: true,
              children: nodeGroups.R
            });
            treeData.push({
              id: '2',
              name: '权限',
              nocheck: true,
              children: nodeGroups.P
            });

            _self.$rolePrivilegeTree = $.fn.zTree.init($('#' + _self.rolePrivilegeTreeId), _self._rolePrivilegeTreeSetting(), treeData);

            var isRoleSelected =
              nodeGroups.R &&
              nodeGroups.R.some(function (node) {
                return !!node.checked;
              });
            var isRightSelected =
              nodeGroups.R &&
              nodeGroups.P.some(function (node) {
                return !!node.checked;
              });

            var nodes = _self.$rolePrivilegeTree.getNodes();
            if (!isRoleSelected && !isRightSelected) {
              // 默认角色夹收起，权限夹展开
              _self.$rolePrivilegeTree.expandNode(nodes[1], true, true, false, true);
            } else {
              // 存在已选数据后，默认展开已选数据所在夹
              if (isRoleSelected) {
                _self.$rolePrivilegeTree.expandNode(nodes[0], true, true, false, true);
              }
              if (isRightSelected) {
                _self.$rolePrivilegeTree.expandNode(nodes[1], true, true, false, true);
              }
            }

            _self.refreshMultiSelectedRolePrivilege();
          }
        }
      });
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
      } else {
        serviceName = 'appSystemMgr';
      }
      if (serviceName) {
        $('#appId', $container).prop('readonly', $('#appId', $container).val() != '');
        $('#appId', $container).wSelect2({
          valueField: 'appId',
          remoteSearch: false,
          serviceName: serviceName,
          queryMethod: 'loadSelectData',
          params: {
            //systemUnitId:server.SpringSecurityUtils.getCurrentUserUnitId()
          }
        });
      }

      $('#memberNames').click(function () {
        $.unit2.open({
          valueField: 'memberIds',
          labelField: 'memberNames',
          title: '选择成员',
          type: 'MyUnit;PublicGroup',
          unitId:
            server.SpringSecurityUtils.getCurrentUserUnitId() != 'S0000000000' ? server.SpringSecurityUtils.getCurrentUserUnitId() : null, //超管页面下可以配置选择所有单位
          multiple: true,
          selectTypes: 'all',
          valueFormat: 'justId',
          callback: function () {
            validator.element($('#memberNames'));
            _self._refreshRoleRelaUserTable($('#memberIds').val(), $('#memberNames').val());
          }
        });
      });
    },

    _appId: function () {
      var widgetParams = this.getWidgetParams();
      var piTypeProperty = {
        模块: 'moduleId',
        应用: 'appId',
        系统: 'systemId'
      };
      return widgetParams[piTypeProperty[widgetParams.appProdIntegrateType]];
    },

    _appType: function () {
      return this.getWidgetParams().appProdIntegrateType;
    },

    _bean: function () {
      return {
        uuid: null,
        name: null,
        remark: null,
        issys: null,
        nestedRoles: [],
        privileges: [],
        sourceUuid: null,
        categoryUuid: null,
        categoryName: null,
        memberIds: null,
        id: null,
        recVer: null,
        code: null,
        appId: this._appId()
      };
    },

    _bindEvents: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var pageContainer = _self.getPageContainer();

      // 新增
      pageContainer.off('AppSecurityRoleListView.editRow');
      pageContainer.on('AppSecurityRoleListView.editRow', function (e) {
        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });
        $('#appId', $container).val(_self._appId()).trigger('change');
        _self.$rolePrivilegeTree.checkAllNodes(false);
        if (e.detail.rowData) {
          //编辑
          server.JDS.restfulGet({
            url: `/proxy/api/security/role/getBean/${e.detail.rowData.uuid}`,
            success: function (result) {
              if (result.code == 0) {
                var bean = _self._bean();
                $.extend(bean, result.data);
                bean = result.data;
                //初始化已选树节点
                _self._loadRolePrivilegeTreeNodes(bean.uuid);
                _self._refreshRoleRelaUserTable(bean.memberIds, bean.memberNames);
                _self._loadRolePrivilegeResultTree(bean.uuid);

                AppPtMgrCommons.json2form({
                  json: bean,
                  container: $container
                });
                $('#memberNames', $container).val(bean.memberSmartNames);
                $('#id', $container).attr('readonly', true);
                $('#appId', $container).trigger('change');
                validator.form();
              }
            }
          });
        } else {
          AppPtMgrCommons.idGenerator.generate($container.find('#id'), 'ROLE_');
          $('#code', $container).val($('#id', $container).val().replace('ROLE_', ''));
          _self.refreshMultiSelectedRolePrivilege();
          $('#id', $container).removeAttr('readonly');
        }

        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');
        listView = e.detail.ui;
      });

      $('#btn_save_role', $container).on('click', function () {
        _self.saveRole();
        return false;
      });

      $('a[data-toggle="tab"]', $container).on('shown.bs.tab', function (e) {
        if ($(e.target).attr('aria-controls') == 'role_used_info') {
          $('#table_role_used_info', _self.widget.element).bootstrapTable('resetView');
        }
        return true;
      });

      $('#searchRolePrivilegeTreeBtn', $container).on('click', function () {
        var nodeName = $('#searchRolePrivilegeTree', $container).val();
        if (nodeName != '') {
          searchZtreeNode(0, _self.rolePrivilegeTreeId, nodeName, function (uuid) {});
        }
        return false;
      });

      $('#selectedRolePrivilege', $container).on('click', 'li .delete-btn', function () {
        var $node = $(this).closest('li');
        var uuid = $node.attr('uuid');
        var treeNode = _self.$rolePrivilegeTree.getNodeByParam('id', uuid);
        _self.$rolePrivilegeTree.checkNode(treeNode, false);
        $node.remove();
      });
    },

    refreshMultiSelectedRolePrivilege: function () {
      var $select = $('#selectedRolePrivilege', this.widget.element);
      $select.empty();
      var checkNodes = this.$rolePrivilegeTree.getCheckedNodes(true);
      for (var i = 0, len = checkNodes.length; i < len; i++) {
        var node = checkNodes[i];
        var type = node.id.substr(0, 1);
        var icon =
          type === 'P'
            ? '<i style="margin-right: 4px; color: #8CA4C6;" class="iconfont icon-ptkj-quanxian"></i>'
            : '<i style="margin-right: 4px; color: #F19E55;" class="iconfont icon-ptkj-jiaose"></i>';

        var $li = $('<li></li>');
        $li.append('<span class="name">' + icon + node.name + '</span>');
        $li.append('<span class="delete-btn">X</span>');
        $li.attr('uuid', node.id).attr('title', node.name);
        $select.append($li);
      }
    },

    _loadRolePrivilegeResultTree: function (uuid) {
      var _self = this;
      server.JDS.restfulGet({
        url: `/proxy/api/security/role/queryPrivilegeResultAsTree/${uuid}`,
        success: function (result) {
          function setChildrenIcon(children) {
            if (!children || !children.length) {
              return;
            }
            for (var i = 0; i < children.length; i++) {
              var item = children[i];
              item.icon = item.id.substr(0, 1) === 'R' ? iconUrl.role : iconUrl.right;
              setChildrenIcon(item.children);
            }
          }

          var data = result.data;
          data.icon = iconUrl.role;
          setChildrenIcon(data.children);

          var zTree = $.fn.zTree.init($('#' + _self.privilegeRstTreeId), {}, result.data);
          var nodes = zTree.getNodes();
          // 默认展开第一个节点
          if (nodes.length > 0) {
            var node = nodes[0];
            zTree.expandNode(node, true, false, false, true);
          }
          // 角色权限结果同步到可选的角色资源树
          if (result.data && result.data.children) {
            var rolePrivilegeTree = $.fn.zTree.getZTreeObj(_self.rolePrivilegeTreeId);
            var index = 0;
            $.each(result.data.children, function (i, nodeData) {
              var rpNode = rolePrivilegeTree.getNodeByParam('id', nodeData.id, null);
              if (rpNode == null) {
                var parentNode = rolePrivilegeTree.getNodeByParam('id', '1');
                if (nodeData.id.substr(0, 1) == 'P') {
                  parentNode = rolePrivilegeTree.getNodeByParam('id', '2');
                }
                var newNode = $.extend(true, {}, nodeData);
                newNode.children = [];
                newNode.checked = true;
                delete newNode.nodeLevel;
                rolePrivilegeTree.addNodes(parentNode, index++, [newNode]);
              }
            });
          }
        }
      });
    },

    _rolePrivilegeTreeSetting: function () {
      var _self = this;
      return {
        check: {
          enable: true,
          chkboxType: {
            Y: 's',
            N: 'ps'
          }
        },
        callback: {
          onCheck: function (e, treeId, treeNode) {
            _self.refreshMultiSelectedRolePrivilege();
          },
          onClick: function (e, treeId, treeNode, checked) {
            _self.$rolePrivilegeTree.checkNode(treeNode, checked);
            _self.refreshMultiSelectedRolePrivilege();
          }
        }
      };
    },

    saveRole: function () {
      var _self = this;
      var $container = $(this.widget.element);
      var bean = _self._bean();
      AppPtMgrCommons.form2json({
        json: bean,
        container: $container
      });

      bean.systemUnitId = server.SpringSecurityUtils.getCurrentUserUnitId();
      var checkNodes = _self.$rolePrivilegeTree.getCheckedNodes(true);
      bean['nestedRoles'] = [];
      bean['privileges'] = [];
      $.each(checkNodes, function (index) {
        if (this.id.substring(0, 1) == 'R') {
          var nestedRole = {};
          nestedRole.roleUuid = this.id.substring(1, this.id.length);
          bean['nestedRoles'].push(nestedRole);
        } else if (this.id.substring(0, 1) == 'P') {
          var privilege = {};
          privilege.uuid = this.id.substring(1, this.id.length);
          bean['privileges'].push(privilege);
        }
      });

      if (!validator.form()) {
        return false;
      }

      server.JDS.restfulPost({
        url: '/proxy/api/security/role/saveBean',
        data: bean,
        success: function (result) {
          if (result.code == 0) {
            appModal.success('保存成功！', function () {
              // 保存成功刷新列表
              listView.trigger('AppSecurityRoleListView.refresh');
              // 清空表单
              AppPtMgrCommons.clearForm({
                container: $container,
                includeHidden: true
              });
              $.ajax({
                url: ctx + '/api/security/role/publishRoleUpdatedEvent',
                type: 'POST',
                data: {
                  uuid: bean.uuid
                },
                dataType: 'json',
                success: function (result) {}
              });
            });
          }
        }
      });
    }
  });
  return AppSecurityRoleDetailsWidgetDevelopment;
});
