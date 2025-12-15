define(['constant', 'commons', 'server', 'ListViewWidgetDevelopment'], function (constant, commons, server, ListViewWidgetDevelopment) {
  // 平台应用_基础数据管理_计时服务_列表二开
  var AppTimeServiceListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppTimeServiceListDevelopment, ListViewWidgetDevelopment, {
    beforeLoadData: function (options, configuration) {
      var uuid = this.getParam('categoryUuid');
      localStorage.removeItem('ts-categoryUuid');
      localStorage.setItem('ts-categoryUuid', uuid);
      this.removeParam('categoryUuid');
      if (uuid == undefined || uuid == 'all') {
        if (uuid == 'all') {
          this.clearOtherConditions();
        }

        var id = $(this.widget.element).siblings('.ui-wHtml').attr('id');
        if (appContext.getWidgetById(id)) {
          var $wHtml = $(appContext.getWidgetById(id).element);
          var $items = $wHtml.find('#msg_category_tree .msg-category-item.hasSelectCate');
          if ($items.size() > 0) {
            uuid = $items.data('uuid');
          }
        }
      } else {
        this.clearOtherConditions();
        this.addOtherConditions([
          {
            columnIndex: 'categoryUuid',
            value: uuid,
            type: 'eq'
          }
        ]);
      }
    },
    onClickRow: function (rowNum, row, $element, field) {
      var url =
        ctx +
        '/web/app/pt-mgr/pt-basicdata-mgr/app_20210531101617.html?pageUuid=6606477d-e115-4452-83b0-b5dc655d77dd&uuid=' +
        row.uuid +
        '&tableBtnEditTag=' +
        row.uuid;
      appContext.getNavTabWidget().createTab('time_service', '计时服务-编辑', 'iframe', url);
    },
    btn_del_all: function (e) {
      var datas = this.getSelections();
      if (datas.length == 0) {
        appModal.error('请先选择记录！');
      } else {
        var uuid = [];
        $.each(datas, function (index, item) {
          uuid.push(item.uuid);
        });
        this.deleteTimeService(uuid);
      }
    },
    btn_del: function (e) {
      var index = $(e.target).parents('tr').first().data('index');
      var datas = this.getData();
      var uuid = datas[index].uuid;
      this.deleteTimeService([uuid]);
    },
    deleteTimeService: function (uuid) {
      var self = this;
      $.ajax({
        // 先判断是否有被引用
        url: ctx + '/api/ts/timer/config/isUsed',
        type: 'post',
        dataType: 'json',
        data: { uuids: uuid },
        success: function (res) {
          if (res.data) {
            appModal.error(res.msg);
          } else {
            appModal.confirm('确定删除计时服务？', function (result) {
              if (result) {
                $.ajax({
                  // 删除计时服务
                  url: ctx + '/api/ts/timer/config/deleteAll',
                  type: 'post',
                  data: { uuids: uuid },
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

  return AppTimeServiceListDevelopment;
});
