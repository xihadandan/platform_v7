/**
 * 组织同步日志详情List 二开
 */
define(['constant', 'commons', 'server', 'ListViewWidgetDevelopment'], function (constant, commons, server, ListViewWidgetDevelopment) {
  var AppDingtalkOrgSyncInfoOrgDevelpment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  var tableData = {};
  var detailInfo;
  commons.inherit(AppDingtalkOrgSyncInfoOrgDevelpment, ListViewWidgetDevelopment, {
    beforeRender: function (options, configuration) {
      var params = this.getWidgetParams();
      // 组织同步日志和业务事件同步日志共用二开， 一次需要判断类型
      this.p_uuid = params.apiType == 'org' ? $('#p_uuid').val() : $('#p_business_uuid').val();

      this.addOtherConditions([
        {
          columnIndex: 'LOG_ID',
          value: this.p_uuid,
          type: 'eq'
        }
      ]);
    },
    afterRender: function () {
      var that = this;
      var $container = that.widget.element;
      // 异常数据重新同步按钮 先隐藏 等待组织同步设置信息回来之后再确定是否显示
      $('.btn_class_btn_review', $container).closest('.fixed-table-toolbar').hide();

      var tabText = $container.closest('.ui-wBootstrapTabs').find('li.active>a').html();
      var tabAhref = $container.closest('.ui-wBootstrapTabs').find('li.active>a').attr('href');

      if (tabText == '钉钉人员同步') {
        var $thName = $(tabAhref).find('.bootstrap-table table th[data-field="NAME"] .th-inner');
        $thName.append('<i class="iconfont icon-ptkj-xinxiwenxintishi name-popover"></i>');

        $('.name-popover', $container).popover({
          html: true,
          placement: 'bottom',
          container: 'body',
          trigger: 'hover',
          template:
            '<div class="popover" role="tooltip"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>',
          content: function () {
            return '<span>姓名右侧显示标签【多部门】：表示该人员同步时存在多部门，人员和职位的关系暂未同步，需要人工审核人员的职位</span>';
          }
        });
      }

      var lis = $container.closest('.ui-wBootstrapTabs').find('li[role="presentation"]');

      this.getDetail($container);
    },
    // 获取组织配置详情
    getOrgSyncSetInfo: function (lis, $container) {
      var that = this;
      var i = 0;
      $.ajax({
        url: ctx + '/pt/dingtalk/getConfig',
        type: 'GET',
        async: false,
        data: {},
        dataType: 'json',
        success: function (result) {
          if (result.success) {
            $.each(lis, function (index, item) {
              if ($(item).hasClass('active')) {
                i = index;
                return false;
              }
            });
            if (i == 0) {
              if (result.data['org.sync.dept'] == '1' && detailInfo.deptStatus == 0) {
                $('.btn_class_btn_review', $container).show().closest('.fixed-table-toolbar').show();
              }
            }
            if (i == 1 && detailInfo.userStatus == 0) {
              $('.btn_class_btn_review', $container).show().closest('.fixed-table-toolbar').show();
            }
            if (i == 2) {
              if (result.data['org.sync.workinfo'] == '1' && detailInfo.userWorkStatus == 0) {
                $('.btn_class_btn_review', $container).show().closest('.fixed-table-toolbar').show();
              }
            }
          }
        },
        error: function () {}
      });
    },
    // 日志详情
    getDetail: function ($container) {
      var that = this;
      var api = this.getWidgetParams().apiType == 'org' ? '/pt/dingtalk/getOrgSyncLogDetail' : '/pt/dingtalk/getEventCallBackDetail';
      $.ajax({
        url: ctx + api,
        type: 'POST',
        data: {
          uuid: this.p_uuid
        },
        async: true,
        success: function (result) {
          detailInfo = result.data;
          // 判断同步内容，展示tab
          var lis = $container.closest('.ui-wBootstrapTabs').find('li[role="presentation"]');
          var parentElement = $container.closest('.ui-wBootstrapTabs');

          // if (detailInfo.syncStatus == 1 || detailInfo.status == 1) {
          //   $('.btn_class_btn_review', $container).hide().closest('.fixed-table-toolbar').hide();
          // } else if (detailInfo.syncStatus == 2 || detailInfo.status == 2) {
          //   // 同步异常
          //   that.getOrgSyncSetInfo(lis, $container);
          // }
          that.getOrgSyncSetInfo(lis, $container);
        }
      });
    },
    // 数据仓库数据加载完成之后 回调
    onLoadSuccess: function (data) {
      var $container = this.widget.element;
      tableData = data;
      if (tableData && tableData.total == 0) {
        $('.btn_class_btn_review', $container).closest('.fixed-table-toolbar').hide();
      }
    },
    // 同步状态
    syncStatusFn: function () {
      const that = this;
      appModal.showMask('正在同步中...', null, 10 * 60 * 1000);
      // 2是同步中，0是同步异常，1是同步成功
      var timer = setInterval(function () {
        $.ajax({
          url: ctx + '/pt/dingtalk/syncOrgStatue',
          type: 'GET',
          dataType: 'json',
          async: true,
          success: function (result) {
            var data = JSON.parse(result.data);
            if (data.code == 1) {
              appModal.hideMask();
              appModal.success('同步成功！');
              clearInterval(timer);
              appContext.refreshWidgetById('page_20200604161753', true);
            } else if (data.code == 2) {
              clearInterval(timer);

              that.syncStatusFn();
            } else if (data.code == 0) {
              appModal.hideMask();
              appModal.error('同步异常！');
              clearInterval(timer);
            }
          }
        });
      }, 5000);
    },
    // 全部重新同步
    btn_review: function () {
      var that = this;
      appModal.confirm(
        '所有异常数据，将重新同步至系统，是否确定？</br>' +
          '确定前，请保障异常原因中描述的问题已解决，</br>' +
          '否则还是会同步异常！确定后，无法取消同步！',
        function (result) {
          if (result) {
            // apiType== org 组织同步日志    apiType== business 业务事件同步日志
            if (that.getWidgetParams().apiType == 'org') {
              $.ajax({
                url: ctx + '/pt/dingtalk/syncOrg',
                type: 'POST',
                dataType: 'json',
                data: {},
                timeout: 10 * 60 * 1000,
                success: function (result) {
                  // appModal.hideMask();
                  // if (result.success) {
                  //   appModal.success('同步成功！');
                  // } else {
                  //   appModal.error(result.msg);
                  // }
                },
                error: function (err) {
                  // appModal.error('同步异常');
                },
                complete: function () {
                  // appModal.hideMask();
                  // // 确定同步，刷新日志列表页
                  // appContext.refreshWidgetById('page_20200604161753', true);
                }
              });
              that.syncStatusFn();
            } else {
              $.ajax({
                url: ctx + '/pt/dingtalk/syncEventData',
                type: 'POST',
                dataType: 'json',
                data: {
                  logId: that.p_uuid
                },
                success: function (result) {},
                error: function (err) {}
              });
              that.syncStatusFn();
            }
          }
        }
      );
    },
    // 重新同步
    btn_line_review: function (e) {
      var that = this;
      var $container = this.widget.element;
      var lis = $container.closest('.ui-wBootstrapTabs').find('li[role="presentation"]');
      var li_active_index = -1;
      var type = null;

      lis.each(function (index, item) {
        if ($(this).hasClass('active')) {
          li_active_index = index;
          return false;
        }
      });
      // li_active_index 当前tab索引   0-部门  1-人员  2-人员和职位关系
      if (li_active_index == -1) {
        return;
      }
      type = li_active_index;

      var trIndex = $(e.target).parents('tr').index();
      var rowData = tableData.rows[trIndex];

      var formData = {
        logId: rowData.UUID,
        type: type
      };
      appModal.confirm(
        '将重新同步至系统，是否确定？<br>' +
          '确定前，请保障异常原因中描述的问题已解决，</br>' +
          '否则还是会同步异常！确定后，无法取消同步！',
        function (result) {
          if (result) {
            appModal.showMask('正在同步中...', null);

            if (that.getWidgetParams().apiType == 'org') {
              $.ajax({
                url: ctx + '/pt/dingtalk/syncOneData',
                type: 'POST',
                data: formData,
                timeout: 1 * 60 * 1000,
                success: function (returnHelper) {
                  var data = returnHelper.data;
                  if (data.code == 1) {
                    appModal.hideMask();
                    appModal.success('同步成功！');
                    appContext.refreshWidgetById('page_20200604161753', true);
                  } else if (data.code == 0) {
                    appModal.hideMask();
                    appModal.error('同步异常！');
                  }
                },
                error: function (err) {}
              });
            } else {
              $.ajax({
                url: ctx + '/pt/dingtalk/syncOneData',
                type: 'POST',
                data: formData,
                success: function (returnHelper) {
                  var data = returnHelper.data;
                  if (data.code == 1) {
                    appModal.hideMask();
                    appModal.success('同步成功！');
                    appContext.refreshWidgetById('page_20200604161753', true);
                  } else if (data.code == 0) {
                    appModal.hideMask();
                    appModal.error('同步异常！');
                  }
                },
                error: function (err) {}
              });
            }
          }
        }
      );
    }
  });

  return AppDingtalkOrgSyncInfoOrgDevelpment;
});
