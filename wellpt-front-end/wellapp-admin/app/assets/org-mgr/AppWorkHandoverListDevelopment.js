define([
  'constant',
  'commons',
  'server',
  'appContext',
  'appModal',
  'layDate',
  'bootstrapTable',
  'bootstrapTable_editable',
  'multiOrg',
  'ListViewWidgetDevelopment'
], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  laydate,
  bootstrapTable,
  bootstrapTable_editable,
  multiOrg,
  ListViewWidgetDevelopment
) {
  var JDS = server.JDS;

  var AppWorkHandoverListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };

  commons.inherit(AppWorkHandoverListDevelopment, ListViewWidgetDevelopment, {
    // 视图组件渲染完成回调方法，子类可覆盖
    afterRender: function (data) {
      var self = this;
      var widgetRefreshEventDiv = "<div id='widgetRefreshEventDiv'></div>";
      $('.btn_class_btn_add').parent().append(widgetRefreshEventDiv);
      $('#widgetRefreshEventDiv')
        .unbind()
        .bind('click', function () {
          self.widgetRefresh();
        });
    },
    lineEnderButtonHtmlFormat: function (format, row, index) {
      var $html = $(format.before);
      $html.find(row.workHandoverStatus !== 1 ? '.btn_class_btn_del' : '').remove();
      format.after = $html[0].outerHTML;
    },

    btn_set: function () {
      var setHtml = this.getSetHtml();
      var $timeDialog = appModal.dialog({
        message: setHtml,
        title: '任务执行设置',
        size: 'large',
        height: 300,
        width: 550,
        shown: function () {
          laydate.render({
            elem: '#executionTime',
            format: 'HH:mm',
            trigger: 'click',
            type: 'time'
          });
          $('#executionTime', $timeDialog).val('01:00');

          $.ajax({
            url: ctx + '/api/wh/handover/getWorkSettings',
            type: 'get',
            dataType: 'json',
            success: function (res) {
              if (res.code == 0) {
                $('#executionTime', $timeDialog).val(res.data.workTime || '01:00');
              }
            }
          });
        },
        buttons: {
          save: {
            label: '保存',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var times = $('#executionTime', $timeDialog).val();
              if (times == '') {
                appModal.error('执行时间不能为空！');
                return false;
              }
              $.ajax({
                url: ctx + '/api/wh/handover/saveWorkSettings',
                type: 'post',
                dataType: 'json',
                data: { workTime: times },
                success: function (res) {
                  if (res.code == 0) {
                    appModal.success('保存成功！');
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

    btn_del: function (e) {
      var index = $(e.target).parents('tr').data('index');
      var uuid = this.getData()[index].uuid;
      var self = this;
      $.ajax({
        url: ctx + '/proxy/api/wh/handover/deleteWorkHandover',
        type: 'post',
        data: { handoverUuid: uuid },
        success: function (res) {
          if (res.code == 0) {
            appModal.success('删除成功！');
            self.getWidget().refresh();
          } else {
            appModal.success(res.msg);
          }
        }
      });
    },
    widgetRefresh: function () {
      var self = this;
      self.refresh();
    },
    getSetHtml: function () {
      var html = '';
      html +=
        "<div class='execution'>" +
        '<label>默认执行时间' +
        "<div class='search-tooltip'>" +
        "<i class='iconfont icon-ptkj-tishishuoming'></i>" +
        "<div class='search-tooltip-content'>默认执行时间即为系统空闲时间，选择更改后，任务序列中未执行任务执行时间将全部更改</div>" +
        '</div>' +
        '</label>' +
        "<div style='position:relative;'>" +
        "<input class='form-control' id='executionTime' name='executionTime'/>" +
        "<span class='date-icon'><i class='iconfont icon-ptkj-shizhongxuanzeshijian'></i></span>" +
        '</div>' +
        '</div>';
      return html;
    }
  });
  return AppWorkHandoverListDevelopment;
});
