define([
  'jquery',
  'commons',
  'constant',
  'server',
  'ListViewWidgetDevelopment',
  'appModal',
  'formBuilder',
  'DmsDocExchangerDetailsTableHelper'
], function ($, commons, constant, server, ListViewWidgetDevelopment, appModal, formBuilder, tableHelper) {
  var DmsDocExchangerSignView = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };

  var stringUtils = commons.StringUtils;

  commons.inherit(DmsDocExchangerSignView, ListViewWidgetDevelopment, {
    $container: null,
    options: null,

    beforeRender: function (options) {
      this.$container = options.$container;
      this.$container.append('<div class="receiver-area"></div>');
      this.$container.append('<div class="forward-area"></div>');

      this.options = options;
    },

    init: function (options) {
      this.beforeRender(options);
      this.createView();
      this.afterRender();
    },

    createView: function () {
      this.createReceiverView();

      if (window.isSender) {
        this.createForwardView();
      }
    },

    createReceiverView: function () {
      var that = this;

      server.JDS.call({
        async: true,
        service: 'docExchangerFacadeService.listDocExchangeReceiverDetail',
        data: [this.options.docExchangeRecordUuid],
        success: function (res) {
          var $receiverTable = that.$container.find('.receiver-area');
          var listData = res.data;
          var statusSummary = that.classifyStatus(listData);

          that.createTableLayout(listData, $receiverTable, 'receiver-table');
          that.createStatusSummaryRowHtml($receiverTable, statusSummary);
        }
      });
    },

    createForwardView: function () {
      var that = this;

      server.JDS.call({
        async: true,
        service: 'docExchangerFacadeService.listDmsDocExchangeForwardOrgDto',
        data: [this.options.docExchangeRecordUuid],
        success: function (res) {
          var $receiverTable = that.$container.find('.forward-area');

          for (var idx = 0; idx < res.data.length; idx++) {
            var item = res.data[idx];
            var tableName = 'forward-table-' + idx;
            var $container = $('<div>', {
              id: tableName
            });
            $receiverTable.append($container);

            var summaryTitle = item.forwardUnitName + '单位转发';
            var summarySubtitle = item.forwardDate;
            var listData = item.forwardList;
            var statusSummary = that.classifyStatus(listData);

            that.createTableLayout(listData, $container, tableName);
            that.createStatusSummaryRowHtml($container, statusSummary, summaryTitle, summarySubtitle, tableName);
          }
        }
      });
    },

    classifyStatus: function (data) {
      var statusSummary = {
        SIGNED: 0,
        WAIT_SIGN: 0,
        RETURNED: 0
      };

      if (Object.prototype.toString.call(data) !== '[object Array]') {
        return statusSummary;
      }

      for (var i = 0; i < data.length; i++) {
        statusSummary[data[i].signStatus]++;
      }

      return statusSummary;
    },

    createTableLayout: function (data, $container, tableName) {
      formBuilder.bootstrapTable.build({
        container: $container,
        name: tableName,
        toolbar: false,
        table: $.extend(
          {
            classes: 'table table-hover docExchangeTable disable-pointer-cursor',
            columns: [
              {
                title: '序号',
                align: 'center',
                width: 50,
                formatter: function (v, row, index) {
                  return index + 1;
                }
              },
              {
                field: 'toUserName',
                title: '收件单位'
              },
              {
                field: 'signStatusName',
                title: '签收情况',
                formatter: function (v, row, i) {
                  if (v == '已退回') {
                    var reason = row.returnReason ? '<br>[ ' + row.returnReason + ' ]' : '';
                    return '<span style="color:#E99F00">' + v + '</span>' + reason;
                  }

                  if (v == '待签收' && row.signNearDeadline) {
                    return v + '(<span class="text-danger">' + row.signTimeLimit + ' 到期</span>)';
                  }

                  if (v == '待签收') {
                    return v;
                  }

                  if (v == '待反馈' || v == '已反馈' || v == '已签收') {
                    var overdue = '<span class="badge badge-danger">逾期</span>';
                    return '已签收 ' + (row.overdue ? overdue : '');
                  }
                }
              },
              {
                field: 'signUserName',
                title: '签收人'
              },
              {
                field: 'signTime',
                title: '操作时间'
              }
            ]
          },
          tableHelper.createDataStoreTableOptions({
            data: data,
            container: $container,
            indexes: ['toUserName', 'signStatusName', 'signUserName']
          })
        )
      });
    },

    createStatusSummaryRowHtml: function ($container, statusSummary, title, subtitle, tableName) {
      var html = '';
      html += '<div class="table-summary-row">';
      html += ' <i class="iconfont icon-ptkj-shixinjiantou-xia doc-table-toggle-btn"></i>';
      html += '  <span class="title">' + (title || '签收情况') + '</span>';
      if (subtitle) {
        html += '  <span class="subtitle"> - ' + subtitle + '</span>';
      }
      html += '  <span class="summary">';
      html += '    (';
      html += '    <span class="wait-sign">待签收 <span class="count">' + statusSummary.WAIT_SIGN + '</span></span>';
      html += '    <span class="signed">已签收 <span class="count">' + statusSummary.SIGNED + '</span></span>';
      html += '    <span class="returned">退回 <span class="count">' + statusSummary.RETURNED + '</span></span>';
      html += '    )';
      html += '  </span>';
      html += '</div>';
      html += tableHelper.createQueryToolbarHtml();

      $('.div-bootstraptable-formbuilder', $container).prepend(html);
      tableHelper.buildQueryFields({
        queryFields: [
          {
            label: '收件单位',
            name: 'toUserName',
            operator: 'like',
            queryOptions: {
              queryType: 'text',
              queryTypeLabel: '文本框'
            }
          },
          {
            label: '签收情况',
            name: 'signStatusName',
            operator: 'in',
            queryOptions: {
              queryType: 'checkbox',
              queryTypeLabel: '复选框',
              optionType: '1',
              optionValue: [
                {
                  id: '待签收',
                  text: '待签收'
                },
                {
                  id: '已签收',
                  text: '已签收'
                },
                {
                  id: '已退回',
                  text: '退回'
                }
              ]
            }
          },
          {
            label: '签收人',
            name: 'signUserName',
            operator: 'like',
            queryOptions: {
              queryType: 'text',
              queryTypeLabel: '文本框'
            }
          },
          {
            label: '操作时间',
            name: 'signTime',
            operator: 'between',
            queryOptions: {
              format: 'YYYY-MM-DD',
              queryType: 'date',
              queryTypeLabel: '日期框'
            }
          }
        ],
        container: $container
      });
      if (tableName) {
        tableHelper.bindQueryToolbarEvent($container.find('#table_' + tableName + '_info'), $container);
      } else {
        tableHelper.bindQueryToolbarEvent($container.find('#table_receiver-table_info'), $container);
      }
    },

    afterRender: function () {
      this.bindEvent();
    },

    bindEvent: function () {
      $(this.$container)
        .off('click', '.doc-table-toggle-btn')
        .on('click', '.doc-table-toggle-btn', function () {
          var $this = $(this);
          $this.toggleClass('closed');
          $this.closest('.table-summary-row').next('.bootstrap-table').slideToggle();
          $this.closest('.table-summary-row').next('.table-toolbar-container').slideToggle().next('.bootstrap-table').slideToggle();
        });
    }
  });

  return DmsDocExchangerSignView;
});
