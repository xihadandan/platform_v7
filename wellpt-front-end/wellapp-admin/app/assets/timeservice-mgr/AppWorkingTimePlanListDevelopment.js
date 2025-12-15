define(['constant', 'commons', 'server', 'ListViewWidgetDevelopment'], function (constant, commons, server, ListViewWidgetDevelopment) {
  // 平台应用_基础数据管理_工作时间方案_列表二开
  var AppWorkingTimePlanListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppWorkingTimePlanListDevelopment, ListViewWidgetDevelopment, {
    lineEnderButtonHtmlFormat: function (format, row, index) {
      var $html = $(format.before);
      $html.find(row.isDefault === 1 ? '.btn_class_btn_set_default' : '').remove();
      $html.find(row.isDefault === 1 ? '.btn_class_btn_del' : '').remove();
      format.after = $html[0].outerHTML;
    },
    onLoadSuccess: function (data) {
      var uuids = [];
      var self = this;
      var $elelment = this.widget.element;
      var id = $elelment.attr('id');
      $.each(data.rows, function (index, item) {
        if (item.isDefault) {
          $('#' + id + '_table', $elelment)
            .find('tbody tr:eq(' + index + ')')
            .find('td[data-field="name"]')
            .css({
              position: 'relative'
            })
            .prepend("<i class='workTime-default-icon iconfont icon-ptkj-moren'></i>");
        }
        uuids.push(item.uuid);
      });
      $.ajax({
        type: 'get',
        url: ctx + '/api/ts/work/time/plan/listNewVersionTipByUuids',
        dataType: 'json',
        traditional: true,
        contentType: 'application/json',
        data: { uuids: uuids },
        success: function (res) {
          $.each(data.rows, function (index, item) {
            var uuid = item.uuid;
            if (res.data[uuid]) {
              $('#' + id + '_table', $elelment)
                .find('tbody tr:eq(' + index + ')')
                .find('td[data-field="name"]')
                .css({
                  position: 'relative'
                })
                .append("<i class=' iconfont icon-ptkj-lishi' title='" + res.data[uuid] + "' style='color:#488cee;'></i>");
            }
          });
        }
      });
    },
    onClickRow: function (rowNum, row, $element, field) {
      var url =
        ctx +
        '/web/app/pt-mgr/pt-basicdata-mgr/app_20210531101501.html?pageUuid=8b45b90c-d728-4144-b922-f4bb8319eb5a&uuid=' +
        row.uuid +
        '&tableBtnEditTag=' +
        row.uuid;

      window.open(url, '_blank');
    },

    btn_set_default: function (e) {
      // 设为默认
      var self = this;
      var index = $(e.target).parents('tr').first().data('index');
      var datas = this.getData();
      var uuid = datas[index].uuid;
      $.ajax({
        type: 'get',
        url: ctx + '/api/ts/work/time/plan/setAsDefault',
        data: { uuid: uuid },
        success: function (res) {
          if (res.code == 0) {
            appModal.success('设置成功！');
            self.refresh();
          } else {
            appModal.error('设置失败！');
          }
        }
      });
    },

    btn_del: function (e) {
      // 删除
      var index = $(e.target).parents('tr').first().data('index');
      var datas = this.getData();
      var uuid = datas[index].uuid;
      this.deleteWorkTimePlan(uuid);
    },

    deleteWorkTimePlan: function (uuid) {
      var self = this;
      $.ajax({
        // 先判断是否有被引用
        url: ctx + '/api/ts/work/time/plan/isUsed',
        type: 'post',
        data: { uuids: [uuid] },
        success: function (res) {
          if (res.data) {
            appModal.error('工作时间方案正被使用，无法删除！');
          } else {
            appModal.confirm('确定删除工作时间方案？', function (result) {
              if (result) {
                $.ajax({
                  // 删除计时服务
                  url: ctx + '/api/ts/work/time/plan/deleteAll',
                  type: 'post',
                  data: { uuids: [uuid] },
                  success: function (res) {
                    if (res.code == 0) {
                      appModal.success('删除成功！');
                      self.refresh();
                    } else {
                      appModal.error(res.msg);
                    }
                  }
                });
              }
            });
          }
        }
      });
    }
  });

  return AppWorkingTimePlanListDevelopment;
});
