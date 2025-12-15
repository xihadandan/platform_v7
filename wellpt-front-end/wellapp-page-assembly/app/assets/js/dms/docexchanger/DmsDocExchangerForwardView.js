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
  var DmsDocExchangerForwardView = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };

  commons.inherit(DmsDocExchangerForwardView, ListViewWidgetDevelopment, {
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
        service: 'docExchangerFacadeService.listDocExchangeForwardDetail',
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
        name: 'docExchangeForwardTable',
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
                field: 'fromUserName',
                title: '转发人'
              },
              {
                field: 'fromUserUnitName',
                title: '转发单位'
              },
              {
                field: 'toUserNames',
                title: '转发对象'
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
                field: 'createTime',
                title: '转发时间'
              },
              {
                field: 'remark',
                title: '转发意见'
              },
              {
                field: 'fileNames',
                title: '转发附件',
                formatter: function (v, row, i) {
                  var html = '';
                  if (v) {
                    var fileNameArr = v.split('/');
                    var fileUuidArr = row['fileUuids'].split('/');

                    for (var j = 0; j < fileNameArr.length; j++) {
                      var $a = $('<a>', {
                        href: ctx + fileServiceURL.downFile + fileUuidArr[j],
                        style: 'margin-right:10px;'
                      }).text(fileNameArr[j]);
                      html += $a[0].outerHTML;
                      if (j != fileNameArr.length - 1) {
                        html += '<br>';
                      }
                    }
                  }
                  return html;
                }
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
              }
            ]
          },
          tableHelper.createDataStoreTableOptions({
            data: this.loadData(),
            container: this.$container,
            indexes: ['fromUserName', 'fromUserUnitName', 'toUserNames', 'remark']
          })
        )
      });
    },

    createTableHeader: function () {
      var html = '';
      html += '<div class="table-summary-row">';
      html += ' <i class="iconfont icon-ptkj-shixinjiantou-xia doc-table-toggle-btn"></i>';
      html += '  <span class="title">转发记录</span>';
      html += '</div>';
      html += tableHelper.createQueryToolbarHtml();

      $('.div-bootstraptable-formbuilder', this.$container).prepend(html);
      tableHelper.buildQueryFields({
        queryFields: [
          {
            label: '转发人',
            name: 'fromUserName',
            operator: 'like',
            queryOptions: {
              queryType: 'text',
              queryTypeLabel: '文本框'
            }
          },
          {
            label: '转发单位',
            name: 'fromUserUnitName',
            operator: 'like',
            queryOptions: {
              queryType: 'text',
              queryTypeLabel: '文本框'
            }
          },
          {
            label: '转发对象',
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
            label: '转发时间',
            name: 'createTime',
            operator: 'between',
            queryOptions: {
              format: 'YYYY-MM-DD',
              queryType: 'date',
              queryTypeLabel: '日期框'
            }
          },
          {
            label: '转发意见',
            name: 'remark',
            operator: 'like',
            queryOptions: {
              queryType: 'text',
              queryTypeLabel: '文本框'
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
          }
        ],
        container: this.$container
      });
      tableHelper.bindQueryToolbarEvent(this.$container.find('#table_docExchangeForwardTable_info'), this.$container);
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
          $this.toggleClass('closed');
          // $this.closest('.table-summary-row').next('.bootstrap-table').slideToggle();
          $this.closest('.table-summary-row').next('.table-toolbar-container').slideToggle().next('.bootstrap-table').slideToggle();
        });
    }
  });

  return DmsDocExchangerForwardView;
});
