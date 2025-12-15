define(['constant', 'commons', 'server', 'appContext', 'appModal', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppOrgGroupWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppOrgGroupWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var formBean = {
        uuid: null,
        id: null,
        name: null,
        type: 0,
        code: null,
        remark: null,
        memberIdPaths: null,
        memberNames: null,
        roleUuids: null,
        userRangeReals: null,
        userRangeDisplays: null
      };
      var validator = $.common.validation.validate('#org_group_form', 'orgGroupVo');
      var uuid = GetRequestParam().uuid;

      initFormLayout(validator);

      $('#org_group_form').json2form(formBean);
      if (uuid) {
        showGroupInfo(uuid);
      } else {
        setTimeout(function () {
          var $zTree = $.fn.zTree.getZTreeObj('group_role_tree');
          $zTree.checkAllNodes(false);
          $('#group_roleUuids').empty();
        }, 500);
        $('#group_privilege_result_tree').empty();
      }

      $('#org_group_btn_save')
        .off()
        .on('click', function () {
          if (!validator.form()) {
            return false;
          }
          $('#org_group_form').form2json(formBean);
          if (formBean.name.indexOf('/') > -1) {
            appModal.error('名称不能包含/');
            return false;
          }
          // 获取角色的值
          var roleUuids = [];
          $('#group_roleUuids option').each(function () {
            roleUuids.push($(this).val());
          });
          formBean.roleUuids = roleUuids.join(';');

          var url = '/api/org/group/' + (formBean.uuid ? 'modifyGroup' : 'addGroup');
          $.ajax({
            type: 'POST',
            url: url,
            dataType: 'json',
            data: formBean,
            success: function (data) {
              appModal.success('保存成功！');
              appContext.getNavTabWidget().closeTab();
            }
          });
        });

      function initFormLayout(validator) {
        $('#memberNames').click(function () {
          $.unit2.open({
            valueField: 'memberIdPaths',
            labelField: 'memberNames',
            title: '选择成员',
            type: 'MyUnit',
            multiple: true,
            selectTypes: 'all',
            valueFormat: 'justId',
            nameDisplayMethod: '1',
            callback: function (ids, names, treeNode) {
              validator.element($('#memberNames'));
              var memberList = [];
              $.each(ids, function (i) {
                memberList.push({
                  memberObjId: treeNode[i].id,
                  memberObjName: treeNode[i].name,
                  memberObjType: treeNode[i].type
                });
              });
              showGroupMemberList(memberList);
            }
          });
        });
        $('#userRangeDisplays').click(function () {
          $.unit2.open({
            valueField: 'userRangeReals',
            labelField: 'userRangeDisplays',
            title: '使用者弹窗',
            type: 'MyUnit;MyDept;MyLeader;MyUnderling;DutyGroup;MyGroup;PublicGroup',
            multiple: true,
            selectTypes: 'all',
            valueFormat: 'justId',
            nameDisplayMethod: '1',
            //从入口群组的使用者进来时，不做过滤
            otherParams: { fromUserRange: '1' },
            callback: function (ids, names, treeNode) {}
          });
        });

        $.common.ztree.initRoleTree('group_role_tree', 'group_roleUuids');
        // 初始化成员数
        $('#group_members_list').bootstrapTable({
          data: [],
          idField: 'id',
          striped: false,
          showColumns: false,
          sortable: true,
          undefinedText: '',
          columns: [
            {
              field: 'id',
              title: 'id',
              visible: false
            },
            {
              field: 'name',
              title: '名称'
            },
            {
              field: 'type',
              title: '类型',
              formatter: function (value, row, index) {
                var name = $.common.idPrev.getName(value);
                if (!name) {
                  return value;
                }
                return name;
              }
            }
          ]
        });
      }

      function showGroupInfo(uuid) {
        $.ajax({
          url: ctx + '/api/org/group/getGroupVo',
          type: 'get',
          data: {
            uuid: uuid
          },
          success: function (result) {
            formBean = result.data;
            $('#org_group_form').json2form(formBean);
            setTimeout(function () {
              var $zTree = $.fn.zTree.getZTreeObj('group_role_tree');
              $zTree.checkAllNodes(false);

              $('#group_roleUuids').empty();
              if (formBean.roleUuids) {
                var roles = formBean.roleUuids.split(';');
                for (var i = 0; i < roles.length; i++) {
                  var nodes = $zTree.getNodesByParam('id', roles[i]);
                  if (nodes.length > 0) {
                    $zTree.checkNode(nodes[0], true, false, true);
                  }
                }
              }
            }, 500);
            showGroupMemberList(formBean.memberList);
          }
        });
        showPrivilegeResultTree(uuid);
      }

      function showGroupMemberList(memberList) {
        $('#group_members_list').bootstrapTable('removeAll');
        if (memberList) {
          for (var i = 0; i < memberList.length; i++) {
            var member = memberList[i];
            $('#group_members_list').bootstrapTable('insertRow', {
              index: 0,
              row: {
                id: member.memberObjId,
                name: member.memberObjName,
                type: member.memberObjType
              }
            });
          }
        }
      }

      function showPrivilegeResultTree(uuid) {
        var setting = {};
        $.ajax({
          url: ctx + '/api/org/group/getGroupPrivilegeResultTree',
          type: 'get',
          data: {
            uuid: uuid
          },
          success: function (result) {
            var zTree = $.fn.zTree.init($('#group_privilege_result_tree'), setting, result.data);
            var nodes = zTree.getNodes();
            if (nodes.length > 0) {
              var node = nodes[0];
              zTree.expandNode(node, true, false, false, true);
            }
          }
        });
      }
    },
    refresh: function () {
      this.init();
    }
  });
  return AppOrgGroupWidgetDevelopment;
});
