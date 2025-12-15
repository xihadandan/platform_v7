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
  var DmsDocExchangerExtraSendView = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };

  commons.inherit(DmsDocExchangerExtraSendView, ListViewWidgetDevelopment, {
    $container: null,
    options: null,

    beforeRender: function (options) {
      this.$container = options.$container;
      this.options = options;
    },

    init: function (options) {
      this.beforeRender(options);
      this.createTableLayout();
      this.afterRender();
    },

    loadData: function () {
      var data = [];
      server.JDS.call({
        service: 'docExchangerFacadeService.listDocExcExtraSendDetail',
        data: [this.options.docExchangeRecordUuid],
        success: function (res) {
          data = res.data;
        },
        error: function (jqXHR) {},
        async: false
      });
      return data;
    },

    createTableLayout: function () {
      formBuilder.bootstrapTable.build({
        container: this.$container,
        name: 'docExchangeExtraSendTable',
        toolbar: false,
        table: $.extend(
          {
            classes: 'table table-hover docExchangeTable disable-pointer-cursor',
            columns: [
              {
                field: 'toUserNames',
                title: '收文人'
              },
              {
                field: 'signTimeLimit',
                title: '签收时限'
              },
              {
                field: 'feedbackTimeLimit',
                title: '反馈时限'
              },
              {
                field: 'notifyWayName',
                title: '提醒方式',
                formatter: function (v, r, i) {
                  var html = '';
                  if (r.isSmsNotify) {
                    html += '短信 ';
                  }
                  if (r.isMailNotify) {
                    html += '邮件 ';
                  }
                  if (r.isImNotify) {
                    html += '在线消息';
                  }
                  r.notifyWayName = html;
                  return html;
                }
              },
              {
                field: 'createTime',
                title: '发送时间'
              }
            ]
          },
          tableHelper.createDataStoreTableOptions({
            data: this.loadData(),
            container: this.$container,
            indexes: ['toUserNames']
          })
        )
      });
    },

    createTableHeader: function () {
      var html = '';
      html += '<div class="table-summary-row">';
      html += ' <i class="iconfont icon-ptkj-shixinjiantou-xia doc-table-toggle-btn"></i>';
      html += '  <span class="title">补发记录</span>';
      html += '</div>';
      html += tableHelper.createQueryToolbarHtml();

      $('.div-bootstraptable-formbuilder', this.$container).prepend(html);
      tableHelper.buildQueryFields({
        queryFields: [
          {
            label: '收文人',
            name: 'toUserNames',
            operator: 'like',
            queryOptions: {
              queryType: 'text',
              queryTypeLabel: '文本框'
            }
          },
          {
            label: '签收时限',
            name: 'signTimeLimit',
            operator: 'between',
            queryOptions: {
              format: 'YYYY-MM-DD',
              queryType: 'date',
              queryTypeLabel: '日期框'
            }
          },
          {
            label: '反馈时限',
            name: 'feedbackTimeLimit',
            operator: 'between',
            queryOptions: {
              format: 'YYYY-MM-DD',
              queryType: 'date',
              queryTypeLabel: '日期框'
            }
          },
          {
            label: '提醒方式',
            name: 'notifyWayName',
            operator: 'in',
            queryOptions: {
              queryType: 'checkbox',
              queryTypeLabel: '复选框',
              optionType: '1',
              optionValue: [
                {
                  id: '在线消息',
                  text: '在线消息'
                },
                {
                  id: '短信',
                  text: '短信'
                },
                {
                  id: '邮件',
                  text: '邮件'
                }
              ]
            }
          },
          {
            label: '发送时间',
            name: 'createTime',
            operator: 'between',
            queryOptions: {
              format: 'YYYY-MM-DD',
              queryType: 'date',
              queryTypeLabel: '日期框'
            }
          }
        ],
        container: this.$container
      });
      tableHelper.bindQueryToolbarEvent(this.$container.find('#table_docExchangeExtraSendTable_info'), this.$container);
    },

    afterRender: function () {
      this.createTableHeader();
      this.bindEvent();
    },

    bindEvent: function () {
      this.$container
        .find('.table-summary-row')
        .off('click', '.doc-table-toggle-btn')
        .on('click', '.doc-table-toggle-btn', function () {
          var $this = $(this);
          var open = !$this.hasClass('closed');
          $this.toggleClass('closed');
          if (open) {
            $this.closest('.table-summary-row').next('.bootstrap-table').slideUp();
          } else {
            $this.closest('.table-summary-row').next('.bootstrap-table').slideDown();
          }
          $this.closest('.table-summary-row').next('.table-toolbar-container').slideToggle().next('.bootstrap-table').slideToggle();
        });
    }
  });

  return DmsDocExchangerExtraSendView;
});
