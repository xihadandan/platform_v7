define(['constant', 'commons', 'server', 'appContext', 'appModal', 'ListViewWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  ListViewWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppGroupOrgListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppGroupOrgListDevelopment, ListViewWidgetDevelopment, {
    onLoadSuccess: function (data) {
      var _self = this;
      var widget = _self.getWidget();
      var $element = $(widget.element);
      $.each(data.rows, function (i, item) {
        if (item.isEnable != 1) {
          $element
            .find("tr[data-index='" + i + "']")
            .find('.switch-wrap')
            .removeClass('active');
        }
      });

      $('.switch-wrap')
        .off()
        .on('click', function (e) {
          var index = $(e.target).parents('tr').data('index');
          var isActive = $(this).hasClass('active');
          $(this)[isActive ? 'removeClass' : 'addClass']('active');
          var uuid = data.rows[index].uuid;

          $.ajax({
            url: ctx + '/proxy/api/multi/group/isEnable',
            type: 'POST',
            data: {
              isEnable: isActive ? 0 : 1,
              uuid: uuid
            },
            dataType: 'json',
            success: function (result) {
              if (result.code === 0) {
              } else {
                appModal.error(result.msg);
              }
            }
          });
        });
    },
    btn_add: function () {
      this.openDetailDialog();
    },
    btn_edit: function (e) {
      var index = $(e.target).parents('tr').data('index');
      var data = this.getData();
      this.openDetailDialog(data[index].uuid);
    },
    btn_del: function (e) {
      var index = $(e.target).parents('tr').data('index');
      var data = this.getData();
      this.del([data[index].uuid]);
    },
    btn_delAll: function () {
      var data = this.getSelection();
      if (data.length == 0) {
        appModal.error('请选择记录！');
        return;
      }
      var ids = [];
      for (var i = 0; i < data.length; i++) {
        ids.push(data[i].id);
      }
      this.del(ids);
    },
    del: function (uuids) {
      var self = this;
      appModal.confirm('确定删除集团组织定义？', function (res) {
        if (res) {
          $.ajax({
            url: ctx + '/proxy/api/multi/group/del',
            type: 'POST',
            data: {
              uuids: uuids.join(',')
            },
            dataType: 'json',
            success: function (result) {
              if (result.code === 0) {
                appModal.success('删除成功！', function () {
                  self.refresh();
                });
              } else {
                appModal.error(result.msg);
              }
            }
          });
        }
      });
    },
    openDetailDialog: function (uuid) {
      var self = this;
      var tree = null;
      var html = this.getDetailHtml();
      var bean = {
        uuid: null,
        name: null,
        code: null,
        note: null,
        treeNode: null
      };
      var groupTreeNode = {
        name: null,
        type: null,
        shortName: null,
        eleId: null,
        id: null,
        nameId: null
      };
      var $groupDialog = appModal.dialog({
        title: '集团组织',
        message: html,
        size: 'large',
        height: 600,
        shown: function () {
          // 添加成员单位
          $('#btn_add_group', $groupDialog)
            .off()
            .on('click', function () {
              initTreeContent('1');
              $('#children', $groupDialog).val('1');
              changeOptionSet(tree, $groupDialog, self.data);
            });

          // 添加子级成员
          $('#btn_add_group_child', $groupDialog)
            .off()
            .on('click', function () {
              initTreeContent('2');
              $('#children', $groupDialog).val('2');
              changeOptionSet(tree, $groupDialog, self.data);
            });

          // 添加分类节点
          $('#btn_add_group_type', $groupDialog)
            .off()
            .on('click', function () {
              initTreeContent('3');
              $('#children', $groupDialog).val('1');
            });

          // 删除
          $('#btn_delete_group', $groupDialog)
            .off()
            .on('click', function () {
              var treeObj1 = $.fn.zTree.getZTreeObj('group_org_tree');
              var selectedNode = treeObj1.getSelectedNodes();
              if (selectedNode.length == 0) {
                appModal.error('请先选择需要删除的节点！');
                return false;
              }
              if (selectedNode[0].children && selectedNode[0].children.length > 0) {
                appModal.error('存在子级节点，请先移出子级节点！');
                return false;
              }
              appModal.confirm('确定删除集团组织架构节点？', function (res) {
                if (res) {
                  if (selectedNode[0].parentTId) {
                    treeObj1.removeNode(selectedNode[0], false);
                  } else {
                    tree = $.fn.zTree.init($('#group_org_tree'), setting, null);
                    $('#btn_add_group', $groupDialog).removeAttr('disabled');
                    $('#btn_add_group_type', $groupDialog).removeAttr('disabled');
                    $('#btn_add_group', $groupDialog).trigger('click');
                  }
                  appModal.success('删除成功！');
                  clearForm($groupDialog);
                }
              });
            });

          //保存
          $('#btn_tree_save', $groupDialog)
            .off()
            .on('click', function () {
              var val = $('#tree_type', $groupDialog).val();
              var treeObj = $.fn.zTree.getZTreeObj('group_org_tree');
              var selectNode = treeObj.getSelectedNodes();
              var nodes = treeObj.getNodes();
              if ((val == '2' || val == '3') && selectNode.length == 0) {
                appModal.error('请先选中节点！');
                return false;
              } else if (!val) {
                val = 1;
              }
              if (val == '1' && nodes.length > 0 && $('#treeId', $groupDialog).val() == '') {
                appModal.error('根节点只能有一个！');
                return false;
              }
              if (val == '3' && $('#tree_typeName', $groupDialog).val() == '') {
                appModal.error('分类名称不能为空！');
                return false;
              } else if (val != '3' && $('#tree_name', $groupDialog).val() == '') {
                appModal.error('成员单位不能为空！');
                return false;
              }

              if ($('#treeId', $groupDialog).val()) {
                var node = treeObj.getNodesByParam('tId', $('#treeId', $groupDialog).val(), null)[0];
                node.name =
                  val == '3'
                    ? $('#tree_typeName', $groupDialog).val()
                    : $('#tree_shortName', $groupDialog).val() || $('#tree_name', $groupDialog).val();
                node.type = val;
                node.shortName = val == '3' ? '' : $('#tree_shortName', $groupDialog).val();
                node.eleId = val == '1' ? $('#id', $groupDialog).val() : '';
                node.id = val == '3' ? '' : $('#id', $groupDialog).val();
                node.iconSkin = val == '3' ? 'icon-folder' : 'icon-doc';
                treeObj.updateNode(node);
              } else {
                groupTreeNode.name =
                  val == '3'
                    ? $('#tree_typeName', $groupDialog).val()
                    : $('#tree_shortName', $groupDialog).val() || $('#tree_name', $groupDialog).val();
                groupTreeNode.type = val;
                groupTreeNode.shortName = val == '3' ? '' : $('#tree_shortName', $groupDialog).val();
                groupTreeNode.eleId = val == '1' ? $('#id', $groupDialog).val() : '';
                groupTreeNode.id = val == '3' ? '' : $('#id', $groupDialog).val();
                groupTreeNode.iconSkin = val == '3' ? 'icon-folder' : 'icon-doc';
                var children = $('#children', $groupDialog).val();
                var parentNode =
                  val == '1'
                    ? null
                    : children == '2'
                    ? treeObj.getNodesByParam('tId', selectNode[0].tId, null)[0]
                    : treeObj.getNodesByParam('tId', selectNode[0].parentTId, null)[0];
                treeObj.addNodes(parentNode, groupTreeNode);
              }
              appModal.success('添加成功！');
              if (val != '3') {
                changeOptionSet(treeObj, $groupDialog, self.data);
              }
              clearForm($groupDialog);
            });

          $('#tree_name', $groupDialog).wellSelect({
            data: [],
            labelField: 'tree_name',
            valueField: 'id',
            showEmpty: false
          });

          var setting = {
            edit: {
              drag: {
                autoExpandTrigger: false,
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
            callback: {
              beforeClick: function (treeId, treeNode) {
                $('#treeId', $groupDialog).val(treeNode.tId);
                $('#tree_type', $groupDialog).val(treeNode.type);
                if (treeNode.type == '3') {
                  $('.group_tree2', $groupDialog).show();
                  $('.group_tree1', $groupDialog).hide();
                  $('#tree_typeName', $groupDialog).val(treeNode.name);
                } else {
                  var id = treeNode.type == '1' ? treeNode.eleId : treeNode.id;
                  var data = {
                    id: id,
                    text: 1
                  };
                  changeOptionSet(tree, $groupDialog, self.data, data);
                  $('.group_tree2', $groupDialog).hide();
                  $('.group_tree1', $groupDialog).show();
                  // $('#tree_name', $groupDialog).val(treeNode.name);
                  $('#tree_name', $groupDialog).wellSelect('val', id).trigger('change');
                  $('#tree_shortName', $groupDialog).val(treeNode.shortName);
                }
                if (!treeNode.parentTId) {
                  $('#btn_add_group', $groupDialog).attr('disabled', true);
                  $('#btn_add_group_type', $groupDialog).attr('disabled', true);
                } else {
                  $('#btn_add_group', $groupDialog).removeAttr('disabled');
                  $('#btn_add_group_type', $groupDialog).removeAttr('disabled');
                }
              },
              beforeDrop: function (treeId, treeNodes, targetNode, moveType) {
                var moveNode = treeNodes[0];
                switch (moveType) {
                  case 'prev':
                  case 'next':
                    if (targetNode.level == 0) {
                      return false;
                    } else {
                      return true;
                    }

                    break;
                  case 'inner':
                    if (moveNode.level != 0 && targetNode.level != 0) {
                      return true;
                    }
                    break;
                }
                return false;
              }
            }
          };

          function initTreeContent(type) {
            var treeObj1 = $.fn.zTree.getZTreeObj('group_org_tree');
            var nodes = treeObj1.getNodes();
            var selectNode = treeObj1.getSelectedNodes();
            if (nodes.length != 0 && type == '1') {
              type = '2';
            }
            if (nodes.length == 0 && selectNode == 0) {
              if (type == '2') {
                appModal.error('请选择具体节点后再添加子级成员单位！');
                return false;
              } else if (type == '3') {
                appModal.error('请选择具体节点后再添加分类节点！');
                return false;
              }
            }

            $('#tree_type', $groupDialog).val(type);

            if (type === '3') {
              $('.group_tree2', $groupDialog).show();
              $('.group_tree1', $groupDialog).hide();
            } else {
              $('.group_tree2', $groupDialog).hide();
              $('.group_tree1', $groupDialog).show();
            }
            clearForm($groupDialog);
          }

          function changeOptionSet(tree, $groupDialog, data, obj) {
            var nodes = tree.getNodes();
            var newData = _.cloneDeep(data);
            var data1 = getData(nodes, newData, obj);
            $('#tree_name', $groupDialog).wellSelect('reRenderOption', {
              data: data1
            });
          }

          function getData(nodes, data, obj) {
            if (nodes.length > 0) {
              for (var i = 0; i < nodes.length; i++) {
                var index = _.findIndex(data, function (o) {
                  return nodes[i].parentTId ? o.id == nodes[i].id : o.id == nodes[i].eleId;
                });

                if (!((obj && data[index] && obj.id == data[index].id) || index == -1)) {
                  data.splice(index, 1);
                }

                if (nodes[i].children && nodes[i].children.length > 0) {
                  getData(nodes[i].children, data, obj);
                }
              }
              return data;
            }
            return data;
          }

          function clearForm($groupDialog) {
            $('#treeId', $groupDialog).val('');
            $('#tree_uuid', $groupDialog).val('');
            $('#tree_name', $groupDialog).wellSelect('val', '').trigger('change');
            $('#tree_shortName', $groupDialog).val('');
            $('#tree_typeName', $groupDialog).val('');
          }

          function setTreeIcon(treeNode) {
            for (var i = 0; i < treeNode.length; i++) {
              var subTree = treeNode[i].children;
              treeNode[i].iconSkin = treeNode[i].type == '3' ? 'icon-folder' : 'icon-doc';
              if (subTree && subTree.length > 0) {
                setTreeIcon(subTree);
              }
            }
            return treeNode;
          }

          if (uuid) {
            $.ajax({
              url: ctx + '/api/multi/group/get',
              type: 'get',
              data: {
                uuid: uuid
              },
              dataType: 'json',
              success: function (result) {
                if (result.code === 0) {
                  bean = result.data;
                  $('#group_org_form', $groupDialog).json2form(bean);
                  var treeNode = [];
                  if (bean.treeNode) {
                    treeNode = setTreeIcon([bean.treeNode]);
                  }
                  tree = $.fn.zTree.init($('#group_org_tree'), setting, treeNode);
                } else {
                  appModal.error(result.msg);
                }
              }
            });
          } else {
            tree = $.fn.zTree.init($('#group_org_tree'), setting, null);
          }

          $.ajax({
            url: ctx + '/api/org/multi/queryAllSystemUnitList',
            type: 'get',
            dataType: 'json',
            success: function (result) {
              if (result.code === 0) {
                var data = [];
                $.each(result.data, function (index, item) {
                  data.push({
                    id: item.id,
                    text: item.name
                  });
                });

                self.data = data;

                $('#tree_name', $groupDialog).wellSelect('reRenderOption', {
                  data: data
                });
              }
            }
          });
        },
        buttons: {
          save: {
            label: '保存',
            className: 'well-btn w-btn-primary',
            callback: function () {
              $('#group_org_form', $groupDialog).form2json(bean);
              if (bean.name == '') {
                appModal.error('名称不能为空！');
                return false;
              }
              bean.treeNode = tree.getNodes()[0];
              $.ajax({
                url: ctx + '/api/multi/group/addOrUpdate',
                type: 'POST',
                data: JSON.stringify(bean),
                dataType: 'json',
                contentType: 'application/json',
                success: function (result) {
                  if (result.code === 0) {
                    self.refresh();
                  } else {
                    appModal.error(result.msg);
                  }
                }
              });
            }
          },
          close: {
            label: '关闭',
            className: 'btn btn-default'
          }
        }
      });
    },
    getDetailHtml: function () {
      var html = '';
      html +=
        '<form id="group_org_form" class="group_org_form" style="margin-top: 15px">' +
        '<ul class="nav nav-tabs" role="tablist">' +
        '<li role="presentation" class="active">' +
        '<a href="#group_org_basic_info" aria-controls="group_org_basic_info" role="tab" data-toggle="tab" aria-expanded="true">基本信息</a>' +
        '</li>' +
        '<li role="presentation">' +
        '<a href="#group_org_framework" aria-controls="group_org_framework" role="tab" data-toggle="tab" aria-expanded="true">自定义属性</a>' +
        '</li>' +
        '</ul>' +
        '<input type="hidden" id="uuid" name="uuid" />' +
        '<div class="tab-content">' +
        '<div role="tabpanel" class="tab-pane active" id="group_org_basic_info" style="padding-top: 10px">' +
        '<div class="well-form form-horizontal">' +
        '<div class="form-group">' +
        '<label for="name" class="well-form-label control-label required">名称</label>' +
        '<div class="well-form-control">' +
        '<input type="text" class="form-control" id="name" name="name" />' +
        '</div>' +
        '</div>' +
        '<div class="form-group">' +
        '<label for="code" class="well-form-label control-label">编号</label>' +
        '<div class="well-form-control">' +
        '<input type="text" class="form-control" id="code" name="code" />' +
        '</div>' +
        '</div>' +
        '<div class="form-group">' +
        '<label for="note" class="well-form-label control-label">备注</label>' +
        '<div class="well-form-control">' +
        '<textarea class="form-control" id="note" name="note"></textarea>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div role="tabpanel" class="tab-pane" id="group_org_framework" style="padding-top: 10px">' +
        '<div class="row">' +
        '<div class="col-sm-5 form-horizontal">' +
        '<div class="btn-group">' +
        '<button id="btn_add_group" type="button" class="well-btn w-btn-primary">添加成员单位</button>' +
        '<button id="btn_add_group_child" type="button" class="well-btn w-btn-primary">添加子级成员单位</button>' +
        '<button id="btn_add_group_type" type="button" class="well-btn w-btn-primary" style="margin-top:10px;">添加分类节点</button>' +
        '<button id="btn_delete_group" type="button" class="well-btn w-btn-primary" style="margin-top:10px;">删除</button>' +
        '</div>' +
        '<ul id="group_org_tree" class="ztree"></ul>' +
        '</div>' +
        '<div class="col-sm-7 well-form form-horizontal" style="width:58.3%;margin-top:0;">' +
        '<input name="treeId" id="treeId" hidden/>' +
        '<input name="children" id="children" hidden/>' +
        '<input name="tree_type" id="tree_type" hidden/>' +
        '<div class="form-group group_tree1">' +
        '<label for="tree_name" class="well-form-label control-label required">成员单位</label>' +
        '<div class="well-form-control">' +
        '<input type="text" class="form-control" id="tree_name" name="tree_name" />' +
        '<input type="hidden" class="form-control" id="id" name="id" />' +
        '</div>' +
        '</div>' +
        '<div class="form-group group_tree1">' +
        '<label for="tree_shortName" class="well-form-label control-label">单位简称</label>' +
        '<div class="well-form-control">' +
        '<input type="text" class="form-control" id="tree_shortName" name="tree_shortName" />' +
        '</div>' +
        '</div>' +
        '<div class="form-group group_tree2" style="display:none;">' +
        '<label for="tree_typeName" class="well-form-label control-label required">分类名称</label>' +
        '<div class="well-form-control">' +
        '<input type="text" class="form-control" id="tree_typeName" name="tree_typeName" />' +
        '</div>' +
        '</div>' +
        '<div class="row">' +
        '<div class="col-sm-12 text-center">' +
        '<button type="button" class="well-btn w-btn-primary" id="btn_tree_save">添加</button>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>' +
        '</form>';
      return html;
    }
  });
  return AppGroupOrgListDevelopment;
});
