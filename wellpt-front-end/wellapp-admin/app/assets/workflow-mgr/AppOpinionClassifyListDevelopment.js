define(['constant', 'commons', 'server', 'appContext', 'appModal', 'ListViewWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  ListViewWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppOpinionClassifyListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppOpinionClassifyListDevelopment, ListViewWidgetDevelopment, {
    onLoadSuccess: function () {
      // 获取行业id
      if ($('.opinion_classify_table').attr('data-businessFlag')) {
        this.businessFlag = $('.opinion_classify_table').attr('data-businessFlag');
        this.businessFlagName = $('.opinion_classify_table').attr('data-businessFlagName');
      } else {
        this.businessFlag = '';
        this.businessFlagName = '';
      }
    },
    btn_add: function () {
      var bean = {
        code: '',
        name: '',
        businessFlag: this.businessFlag || '',
        businessFlagName: this.businessFlagName || ''
      };
      this.showDialog('新建分类', bean);
    },
    onClickRow: function (rowNum, row, $element, field) {
      var _self = this;
      JDS.restfulGet({
		url: ctx + '/api/workflow/opinion/category/get',
        data: {uuid: row.uuid},
		contentType: 'application/x-www-form-urlencoded', 
        success: function (result) {
          result.data.businessFlagName = _self.businessFlagName;
          _self.showDialog('分类', result.data);
        }
      });
    },
    btn_edit: function (e) {
      var _self = this;
      var index = $(e.target).parents('tr').data('index');
      var uuids = this.getData()[index].uuid;
      JDS.restfulGet({
		url: ctx + '/api/workflow/opinion/category/get',
        data: {uuid: uuids},
		contentType: 'application/x-www-form-urlencoded', 
        success: function (result) {
          result.data.businessFlagName = _self.businessFlagName;
          _self.showDialog('分类', result.data);
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
      var opinions = [];

      JDS.restfulGet({
		url: ctx + '/api/workflow/opinion/getByOpinionCategory',
        data: {categoryUuid: uuids[0]},
		contentType: 'application/x-www-form-urlencoded', 
        success: function (res) {
          opinions = res.data;
          var title = opinions.length <= 0 ? '确认删除？' : '分类下存在常用意见，将同时被删除，确认删除？';
          var url =
            opinions.length <= 0 ? '/api/workflow/opinion/category/delete' : '/api/workflow/opinion/categoryAndOpinion/delete';
          var data = opinions.length <= 0 ? {categoryUuids:uuids} : {categoryUuid:uuids[0]};
          appModal.confirm(title, function (res) {
            if (res) {
              JDS.restfulPost({
                url: ctx + url,
                data: data,
				contentType: 'application/x-www-form-urlencoded',
                success: function (result) {
                  appModal.success('删除成功!');
                  _self.refresh(true);
                }
              });
            }
          });
        }
      });
    },
    getDialogHtml: function () {
      var html =
        '<form action="" id="opinion_classify_form" class="dyform" style="margin-top: 15px;">' +
        '<input type="hidden" id="uuid" name="uuid" />' +
        '<table class="well-form form-horizontal">';
      html +=
        '<tr class="field">' +
        '<td class="label-td" style="width:20%;"><font style="color:#f00;">*</font>名称</td>' +
        '<td><input name="name" type="text" class="form-control"/></td>' +
        '</tr>' +
        '<tr class="field">' +
        '<td class="label-td" style="width:20%;">编号</td>' +
        '<td><input type="text" name="code" class="form-control"/></td>' +
        '</tr>' +
        '<tr class="field">' +
        '<td class="label-td" style="width:20%;"><font style="color:#f00;">*</font>所属行业</td>' +
        '<td>' +
        '<input type="text" name="businessFlagName" id="businessFlagName" class="form-control"/>' +
        '<input type="hidden" name="businessFlag" id="businessFlag" class="form-control"/>' +
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
              $('#opinion_classify_form').form2json(bean);
              if (bean.name == '') {
                appModal.error('名称不能为空！');
                return false;
              }
              if (bean.businessFlag == '') {
                appModal.error('所属行业不能为空！');
                return false;
              }
              JDS.restfulPost({
                url: ctx + '/api/workflow/opinion/category/save',
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
          JDS.restfulGet({
	   		url: ctx + '/api/workflow/opinion/getFlowOpinionCategoryTreeByBusinessAppDataDic',
            data: {fetchOpinionCategory:false},
			contentType: 'application/x-www-form-urlencoded', 
            async: false,
            success: function (result) {
              var data = result.data;
              function getNodeName(data) {
                if (data.id == bean.businessFlag) {
                  bean.businessFlagName = data.name;
                  return;
                } else if (data.children.length > 0) {
                  $.each(data.children, function (i, item) {
                    getNodeName(item);
                  });
                }
              }
              getNodeName(data);
              $('#opinion_classify_form').json2form(bean);
              var setting = {
                check: {
                  chkStyle: 'radio'
                },
                async: {
                  otherParam: {
                    serviceName: 'flowOpinionCategoryService',
                    methodName: 'getFlowOpinionCategoryTreeByBusinessAppDataDic',
                    data: [null]
                  }
                }
              };

              $('#businessFlagName').comboTree({
                labelField: 'businessFlagName',
                valueField: 'businessFlag',
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
    }
  });
  return AppOpinionClassifyListDevelopment;
});
