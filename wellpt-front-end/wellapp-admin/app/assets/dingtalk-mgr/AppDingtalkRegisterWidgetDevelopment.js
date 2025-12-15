define(['constant', 'commons', 'server', 'HtmlWidgetDevelopment'], function (constant, commons, server, HtmlWidgetDevelopment) {
  var AppDingtalkRegisterWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };

  commons.inherit(AppDingtalkRegisterWidgetDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var _self = this;
      var len = 0;
      var events = {
        user_add_org: ['用户新增', '用户变更'],
        user_modify_org: ['用户修改', '用户变更'],
        user_leave_org: ['用户删除', '用户变更'],
        user_active_org: ['用户账号激活', '用户变更'],
        org_admin_add: ['用户被设为管理员', '用户变更'],
        org_admin_remove: ['用户被取消设为管理员', '用户变更'],
        org_dept_create: ['部门新增', '部门变更'],
        org_dept_modify: ['部门修改', '部门变更'],
        org_dept_remove: ['部门删除', '部门变更']
        // label_user_change: ['员工角色信息发生变更', '角色变更'],
        // label_conf_add: ['增加角色或者角色组', '角色变更'],
        // label_conf_del: ['删除角色或者角色组', '角色变更'],
        // label_conf_modify: ['修改角色或者角色组', '角色变更']
      };
      initTable();
      var tempTags = [];

      $(this.widget.element).find('.bootstrap-table').addClass('ui-wBootstrapTable');

      getEventList();
      function getEventList() {
        $('.no-records-found').hide();
        $.ajax({
          type: 'GET',
          url: ctx + '/pt/dingtalk/getCallBack',
          dataType: 'json',
          success: function (result) {
            if (result.success) {
              var data = result.data;
              if (data.call_back_tag && data.call_back_tag.length > 0) {
                var callbackTag = data.call_back_tag;
                len = callbackTag.length;
                tempTags = callbackTag;
                console.log('23', callbackTag);
                for (var i = 0; i < callbackTag.length; i++) {
                  if (events[callbackTag[i]] && events[callbackTag[i]].length > 0) {
                    $('#eventList').bootstrapTable('insertRow', {
                      index: 0,
                      row: {
                        event: events[callbackTag[i]] ? events[callbackTag[i]][0] : null,
                        eventType: callbackTag[i],
                        eventCate: events[callbackTag[i]] ? events[callbackTag[i]][1] : null
                      }
                    });
                  }
                }
              } else {
                $('.no-records-found').show();
              }
            }
          }
        });
      }

      function initTable() {
        $('#eventList')
          .bootstrapTable('destroy')
          .bootstrapTable({
            data: [],
            idField: 'uuid',
            pageStyle: 'default',
            pageStyleName: '默认分页',
            pagination: true,
            pageSize: '10',
            pageList: '10,20,50,100,200',
            jumpPage: true,
            hidePageList: '',
            hideTotalPageText: '',
            waterfallPageLabel: '',
            prePageLabel: '',
            nextPageLabel: '',
            columns: [
              {
                checkbox: true
              },
              {
                field: 'event',
                title: '已注册事件'
              },
              {
                field: 'eventType',
                title: '事件类型'
              },
              {
                field: 'eventCate',
                title: '类别'
              }
            ]
          });
      }
      // 新增注册事件
      $('#btn_register')
        .off()
        .on('click', function () {
          var html =
            '<form>' +
            "<div class='form-group'>" +
            "<label class='control-label' style='font-weight: normal;color:#666;'>请选择业务事件 <span>（含已注册事件）：</span></label>" +
            "<div class='control-control'>" +
            "<input type='text' name='bussinessEvent' id='bussinessEvent' class='form-control'/>" +
            '</div>' +
            '</div>' +
            '</form>';

          appModal.dialog({
            title: '注册事件',
            message: html,
            size: 'middle',
            shown: function () {
              $('#bussinessEvent').wellSelect({
                data: [
                  { id: 'user_add_org', text: '通讯录用户增加', desc: 'user_add_org' },
                  { id: 'user_modify_org', text: '通讯录用户更改', desc: 'user_modify_org' },
                  { id: 'user_leave_org', text: '通讯录用户离职', desc: 'user_leave_org' },
                  { id: 'user_active_org', text: '加入企业后用户激活', desc: 'user_active_org' },
                  { id: 'org_admin_add', text: '通讯录用户被设为管理员', desc: 'org_admin_add' },
                  { id: 'org_admin_remove', text: '通讯录用户被取消设置管理员', desc: 'org_admin_remove' },
                  { id: 'org_dept_create', text: '通讯录企业部门创建', desc: 'org_dept_create' },
                  { id: 'org_dept_modify', text: '通讯录企业部门修改', desc: 'org_dept_modify' },
                  { id: 'org_dept_remove', text: '通讯录企业部门删除', desc: 'org_dept_remove' }
                  // { id: 'label_user_change', text: '员工角色信息发生变更', desc: 'label_user_change' },
                  // { id: 'label_conf_add', text: '增加角色或者角色组', desc: 'label_conf_add' },
                  // { id: 'label_conf_del', text: '删除角色或者角色组', desc: 'label_conf_del' },
                  // { id: 'label_conf_modify', text: '修改角色或者角色组', desc: 'label_conf_modify' }
                ],
                valueField: 'bussinessEvent',
                multiple: true,
                chooseAll: true,
                remoteSearch: false
              });
            },
            buttons: {
              canfirm: {
                label: '注册',
                className: 'well-btn w-btn-primary',
                callback: function () {
                  if ($('#bussinessEvent').val() != '') {
                    var val = $('#bussinessEvent').val();
                    var call_back_tag = JSON.stringify(val.split(';'));
                    var url = len == 0 ? '/pt/dingtalk/registerCallBack' : '/pt/dingtalk/updateCallBack';
                    $.ajax({
                      type: 'POST',
                      url: ctx + url,
                      data: { call_back_tag: call_back_tag },
                      dataType: 'json',
                      success: function (result) {
                        if (result.success) {
                          appModal.success('注册成功');
                          _self.refresh();
                        }
                      }
                    });
                  }
                }
              },
              cancel: {
                label: '取消',
                className: 'btn btn-default'
              }
            }
          });
        });

      $('#btn_deleteAll')
        .off()
        .on('click', function () {
          var datas = $('#eventList').bootstrapTable('getSelections');
          if (datas.length == 0) {
            appModal.error('请选择记录！');
            return false;
          }
          for (var i = 0; i < datas.length; i++) {
            for (var j = 0; j < tempTags.length; j++) {
              if (datas[i].eventType == tempTags[j]) {
                tempTags.splice(j, 1);
                continue;
              }
            }
          }

          appModal.confirm(
            '确定删除选择的已注册业务事件吗？<br>删除后，业务事件对应的数据，平台将不会处理，<br>从而造成钉钉和平台的数据不一致，请谨慎操作！',
            function (result) {
              if (result) {
                $.ajax({
                  type: 'GET',
                  url: ctx + '/pt/dingtalk/deleteCallBack',
                  dataType: 'json',
                  success: function (result) {
                    if (result.success) {
                      appModal.success('删除成功', function () {
                        if (tempTags.length > 0) {
                          var url = '/pt/dingtalk/registerCallBack';
                          $.ajax({
                            type: 'POST',
                            url: ctx + url,
                            data: { call_back_tag: JSON.stringify(tempTags) },
                            dataType: 'json',
                            success: function (result) {
                              if (result.success) {
                                _self.refresh();
                              }
                            }
                          });
                        } else {
                          _self.refresh();
                        }
                      });
                    }
                  }
                });
              }
            }
          );
        });
    },
    refresh: function () {
      this.init();
    }
  });
  return AppDingtalkRegisterWidgetDevelopment;
});
