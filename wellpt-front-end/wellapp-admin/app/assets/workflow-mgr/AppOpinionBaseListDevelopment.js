define(['constant', 'commons', 'server', 'appContext', 'appModal', 'ListViewWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  ListViewWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppOpinionBaseListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppOpinionBaseListDevelopment, ListViewWidgetDevelopment, {
    onLoadSuccess: function () {
      // 获取分类id
      this.opinionCategoryUuid = this.getParam('opinionCategoryUuids');
      this.opinionCategoryName = $('.opinion_base_table').attr('data-opinionCategoryName');
    },
    btn_add: function () {
      var bean = {
        content: '',
        code: '',
        opinionCategoryUuid: this.opinionCategoryUuid || '',
        opinionCategoryName: this.opinionCategoryName || ''
      };
      this.showDialog('新建意见', bean);
    },
    onClickRow: function (rowNum, row, $element, field) {
      var _self = this;
      JDS.restfulGet({
		url: ctx + '/api/workflow/opinion/get',
        data: {uuid: row.uuid},
		contentType: 'application/x-www-form-urlencoded', 
        success: function (result) {
          result.data.opinionCategoryName = _self.opinionCategoryName;
          _self.showDialog('常用意见', result.data);
        }
      });
    },
    btn_edit: function (e) {
      var _self = this;
      var index = $(e.target).parents('tr').data('index');
      var uuids = this.getData()[index].uuid;
      JDS.restfulGet({
		url: ctx + '/api/workflow/opinion/get',
        data: {uuid: uuids},
		contentType: 'application/x-www-form-urlencoded', 
        success: function (result) {
          result.data.opinionCategoryName = _self.opinionCategoryName;
          _self.showDialog('常用意见', result.data);
        }
      });
    },
    btn_deleteAll: function () {
      var uuids = this.getSelectionUuids();
      if (uuids.length <= 0) {
        appModal.error('请选择记录！');
        return false;
      }
      this.deleteItems(uuids);
    },
    btn_delete: function (e) {
      var index = $(e.target).parents('tr').data('index');
      var uuids = [this.getData()[index].uuid];
      this.deleteItems(uuids);
    },
    deleteItems: function (uuids) {
      var _self = this;
      appModal.confirm('确认删除？', function (res) {
        if (res) {
          JDS.restfulPost({
			url: ctx + '/api/workflow/opinion/deleteAll',
        	data: {uuids: uuids},
			contentType: 'application/x-www-form-urlencoded', 
            success: function (result) {
              appModal.success('删除成功!');
              _self.refresh(true);
            }
          });
        }
      });
    },
    getDialogHtml: function () {
      var html =
        '<form action="" id="opinion_base_form" class="dyform" style="margin-top: 15px;">' +
        '<input type="hidden" id="uuid" name="uuid" />' +
        '<table class="well-form form-horizontal">';
      html +=
        '<tr class="field">' +
        '<td class="label-td" style="width:20%;"><font style="color:#f00;">*</font>意见内容</td>' +
        '<td><textarea type="text" name="content" id="content" class="form-control"></textarea></td>' +
        '</tr>' +
        '<tr class="field">' +
        '<td class="label-td" style="width:20%;">编号</td>' +
        '<td><input type="text" name="code" id="code" class="form-control"/></td>' +
        '</tr>' +
        '<tr class="field">' +
        '<td class="label-td" style="width:20%;"><font style="color:#f00;">*</font>所属分类</td>' +
        '<td>' +
        '<input type="text" name="opinionCategoryName" id="opinionCategoryName" class="form-control"/>' +
        '<input type="hidden" name="opinionCategoryUuid" id="opinionCategoryUuid" class="form-control"/>' +
        '</td>' +
        '</tr>';

      html += '</table></form>';
      return html;
    },
    showDialog: function (title, bean) {
      var _self = this;
      var html = this.getDialogHtml();
      appModal.dialog({
        message: html,
        zIndex: '99',
        size: 'large',
        title: title,
        buttons: {
          confirm: {
            className: 'well-btn w-btn-primary',
            label: '保存',
            callback: function () {
              $('#opinion_base_form').form2json(bean);
              if (bean.content == '') {
                appModal.error('意见内容不能为空！');
                return false;
              }
              if (bean.opinionCategoryUuid == '') {
                appModal.error('所属分类不能为空！');
                return false;
              }
              JDS.restfulPost({
				url: ctx + '/api/workflow/opinion/save',
                data: bean,
                success: function (result) {
                  appModal.success({
                    message: '保存成功',
                    callback: function () {
                      _self.refresh();
                    }
                  });
                }
              });
            }
          },
          cancel: {
            className: 'btn btn-default',
            label: '取消'
          }
        },
        shown: function () {
          $('#opinion_base_form').json2form(bean);

          var setting = {
            check: {
              radioType: 'level',
              chkStyle: 'radio',
              enable: true
            },
            view: {
              autoCancelSelected: true
            },
            async: {
              otherParam: {
                serviceName: 'flowOpinionCategoryService',
                methodName: 'getFlowOpinionCategoryTreeByBusinessAppDataDic',
                data: [true]
              }
            },
            callback: {
              onNodeCreated: function (event, treeId, treeNode) {
                var ztree = $.fn.zTree.getZTreeObj(treeId);
                if (treeNode.type != '-1') {
                  treeNode.nocheck = true;
                  ztree.updateNode(treeNode);
                } else {
                  $('#' + treeNode.tId)
                    .find('span.button.ico_docu')
                    .css({
                      'background-color': 'rgba(72,140,238,.3)'
                    });
                }
              },
              onClick: null,
              beforeClick: function (treeId, treeNode) {
                if (treeNode == null || treeNode.type !== '-1') {
                  return;
                }
                return true;
              }
            }
          };
          $('#opinionCategoryName').comboTree({
            labelField: 'opinionCategoryName',
            valueField: 'opinionCategoryUuid',
            treeSetting: setting,
            width: 220,
            height: 220,
            multiple: true,
            autoInitValue: false,
            autoCheckByValue: true
          });
        }
      });
    }
  });
  return AppOpinionBaseListDevelopment;
});
