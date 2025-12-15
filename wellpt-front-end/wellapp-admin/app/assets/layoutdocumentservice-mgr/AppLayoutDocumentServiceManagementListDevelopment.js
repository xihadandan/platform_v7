define(['constant', 'commons', 'server', 'ListViewWidgetDevelopment'], function (constant, commons, server, ListViewWidgetDevelopment) {
  // 平台应用_基础数据管理_签章服务配置_列表二开
  var AppSealServiceManagementListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  commons.inherit(AppSealServiceManagementListDevelopment, ListViewWidgetDevelopment, {
    // 表格数据加载完毕回调方法，子类可覆盖
    onLoadSuccess: function (data) {
      var _self = this;
      setTimeout(function () {
        _self
          .getWidget()
          .$tableElement.find('.switch-wrap')
          .off('click')
          .on('click', function () {
            var $switchWrap = $(this);
            var uuid = $switchWrap.parent().parent().find('td.bs-checkbox input').val();

            if ($switchWrap.hasClass('active')) {
              $switchWrap.removeClass('active');
              _self.changeLayoutDocumentConfigStatus(uuid, '0', function () {
                $switchWrap.removeClass('active');
              });
            } else {
              appModal.confirm(
                '系统仅允许存在一个"启用"的服务，继续保存将修改其他"启用"的服务的状态为"禁用"，是否确定保存？',
                function (result) {
                  if (result) {
                    _self.changeLayoutDocumentConfigStatus(uuid, '1', function () {
                      $switchWrap.addClass('active');
                    });
                  }
                }
              );
            }
          });
      }, 1);
    },

    btn_del_all: function (e) {
      var self = this;

      var datas = this.getSelections();
      if (datas.length == 0) {
        appModal.error('请先选择记录！');
      } else {
        var uuid = [];
        $.each(datas, function (index, item) {
          uuid.push(item.UUID);
        });

        appModal.confirm('确认删除？', function (result) {
          if (result) {
            self.deleteTimeService(uuid);
          }
        });
      }
    },
    btn_del: function (e) {
      var self = this;

      var index = $(e.target).parents('tr').first().data('index');
      var datas = this.getData();
      var uuid = datas[index].UUID;

      appModal.confirm('确认删除？', function (result) {
        if (result) {
          self.deleteTimeService([uuid]);
        }
      });
    },

    deleteTimeService: function (uuids) {
      var self = this;
      $.ajax({
        // 删除签章服务配置
        url: ctx + '/api/basicdata/layoutDocumentServiceConf/deleteByUuids',
        type: 'post',
        data: { uuids: uuids },
        success: function (res) {
          if (res.code === 0) {
            appModal.success('删除成功');
            self.refresh();
          } else {
            appModal.error(res.msg);
          }
        }
      });
    },

    changeLayoutDocumentConfigStatus: function (uuid, status, callback) {
      var self = this;
      $.ajax({
        // 删除签章服务配置
        url: ctx + '/api/basicdata/layoutDocumentServiceConf/changeLayoutDocumentConfigStatus',
        type: 'post',
        data: { uuid: uuid, status: status },
        success: function (res) {
          if (res.code === 0) {
            if (callback) {
              callback();
            }
            appModal.success('修改成功');
            self.refresh();
          } else {
            appModal.error(res.msg);
          }
        }
      });
    }
  });

  return AppSealServiceManagementListDevelopment;
});
