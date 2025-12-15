define(['constant', 'commons', 'server', 'appContext', 'appModal', 'formBuilder', 'HtmlWidgetDevelopment', 'js-base64'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  formBuilder,
  HtmlWidgetDevelopment,
  base64
) {
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var JDS = server.JDS;

  // 平台管理_系统单位_组织选择项组件二开
  var AppOrgUserAccountSetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppOrgUserAccountSetDevelopment, HtmlWidgetDevelopment, {
    // 准备创建回调
    prepare: function () {
    },
    // 创建后回调
    create: function () {
    },
    // 初始化回调
    init: function () {
      var _self = this;
      var _uuid = GetRequestParam().uuid;

      var form_selector = '#org_user_form';
      var formBean = {
        uuid: null,
        id: null,
        loginName: null,
        password: null,
        type: 1,
        code: null,
        remark: null,
        isForbidden: null,
        isLocked: null,
        systemUnitId: null,
        employeeNumber: null, // 员工编号
        userName: null, // 姓名
        englishName: null,
        sex: null,
        fax: null,
        idNumber: null,
        mobilePhone: null,
        officePhone: null,
        homePhone: null,
        mainEmail: null,
        roleUuids: null
      };

      $(form_selector).json2form(formBean);

      var validator = $.common.validation.validate(form_selector, 'orgUserVo');
      initFormLayout();
      if (_uuid) {
        showOrgOptionInfo();
      }

      // 根据组织UUID获取信息
      function showOrgOptionInfo() {
        $.ajax({
          url: ctx + '/api/org/multi/getUser',
          type: 'get',
          data: {
            uuid: _uuid
          },
          success: function (result) {
            var bean = result.data;
            // 不要显示密码
            bean.password = null;
            $(form_selector).json2form(bean);
            // 设置登录名只读状态,登录名不能修改,归属单位不能修改
            $('#loginName').attr('readonly', 'readonly');
            if (bean.systemUnitId) {
              $('#org_user_systemUnitId').wellSelect('val', bean.systemUnitId).wellSelect('readonly', 'readonly');
            } else {
              $('#systemUnitId').removeAttr('readonly');
            }

            // 显示角色信息
            setTimeout(function () {
              var $zTree = $.fn.zTree.getZTreeObj('role_tree');
              // 需要先取消所有的勾选
              $zTree.checkAllNodes(false);
              $('#roleUuids').empty();
              if (bean.roleUuids) {
                var roles = bean.roleUuids.split(';');
                for (var i = 0; i < roles.length; i++) {
                  var nodes = $zTree.getNodesByParam('id', roles[i]);
                  if (nodes.length > 0) {
                    $zTree.checkNode(nodes[0], true, false, true);
                  }
                }
              } else {
                $('#roleUuids').empty();
              }
            }, 500);
            showPrivilegeResultTree(_uuid);
          }
        });
      }

      // 定义保存按钮的事件
      $('#btn_save')
        .off()
        .on('click', function () {
          if (!validator.form()) {
            return false;
          }
          $('#org_user_form').form2json(formBean);
          formBean.isForbidden = formBean.isForbidden ? '1' : '0';
          formBean.isLocked = formBean.isLocked ? '1' : '0';
          formBean.password = base64.encode(urlencode(formBean.password));
          // 获取角色的值
          var roleUuids = [];
          $('#roleUuids li').each(function () {
            roleUuids.push($(this).data('value'));
          });
          formBean.roleUuids = roleUuids.join(';');

          var url = '/api/org/multi/' + (formBean.uuid ? 'modifyUnitAdmin' : 'addUnitAdmin');
          $.ajax({
            type: 'POST',
            url: url,
            dataType: 'json',
            data: formBean,
            success: function (data) {
              if (data.code === 0) {
                appModal.success({
                  message: '保存成功！',
                  callback: function () {
                    appContext.getNavTabWidget().closeTab();
                  }
                });
              } else {
                appModal.error(data.msg);
              }
            }
          });
        });
    },
    refresh: function () {
      var _self = this;
      _self.init();
    }
  });

  function systemUnitInit(includePt) {
    $('#org_user_systemUnitId').wSelect2({
      serviceName: 'multiOrgSystemUnitService',
      queryMethod: 'queryUnitListForSelect2',
      labelField: 'org_user_systemUnitName',
      valueField: 'org_user_systemUnitId',
      placeholder: '请选择',
      params: {
        includePt: true == includePt ? 1 : 0
      },
      multiple: false,
      remoteSearch: false
    });
  }

  function initRoleTree(treeEleId, selectEleId) {
    var setting = {
      check: {
        enable: true
      },
      callback: {
        onCheck: function (event, treeId, treeNode) {
          if (treeNode) {
            var id = treeNode.id;
            var name = treeNode.name;
            if (treeNode.checked) {
              var li = "<li data-value='" + id + "'>" + name + '</li>';
              $('#' + selectEleId).append(li);
            } else {
              $('#' + selectEleId + " li[data-value='" + id + "']").remove();
            }
          }
        },
        onNodeCreated: function (event, treeId, treeNode) {
          if ('R' === treeNode.type) {
            // 角色的节点,才显示勾选
            treeNode.nocheck = false;
          } else {
            treeNode.nocheck = true;
          }
          var ztree = $.fn.zTree.getZTreeObj(treeId);
          ztree.updateNode(treeNode);
        }
      },
      view: {
        fontCss: function (treeId, treeNode) {
          if (treeNode.type === 'R') {
            return treeNode.data !== 'S0000000000'
              ? {
                color: 'red'
              }
              : {};
          }
        }
      }
    };
    $.ajax({
      url: ctx + '/api/security/role/getRoleTree',
      type: 'get',
      data: {},
      success: function (result) {
        var zTree = $.fn.zTree.init($('#' + treeEleId), setting, result.data);
        var nodes = zTree.getNodes();
        // 默认展开第一个节点
        if (nodes.length > 0) {
          var node = nodes[0];
          zTree.expandNode(node, true, false, false, true);
        }
      }
    });
  }

  function showPrivilegeResultTree(userUuid) {
    var setting = {};
    $.ajax({
      url: ctx + '/api/org/multi/getUserPrivilegeResultTree',
      type: 'get',
      data: {
        uuid: userUuid
      },
      success: function (result) {
        var zTree = $.fn.zTree.init($('#privilege_result_tree'), setting, result.data);
        var nodes = zTree.getNodes();
        // 默认展开第一个节点
        if (nodes.length > 0) {
          var node = nodes[0];
          zTree.expandNode(node, true, false, false, true);
        }
      }
    });
  }

  function initFormLayout() {
    // 设置默认类型为普通账号
    $('#type').val('0');
    // 初始化归属单位
    systemUnitInit();

    // 初始化角色树
    initRoleTree('role_tree', 'roleUuids');
  }

  return AppOrgUserAccountSetDevelopment;
});
