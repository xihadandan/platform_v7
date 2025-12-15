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
  var DmsDocExchangerLogView = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };

  commons.inherit(DmsDocExchangerLogView, ListViewWidgetDevelopment, {
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
        service: 'docExchangerFacadeService.listDocExchangecLogs',
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
      var _self = this;
      var data = _self.loadData();
      formBuilder.bootstrapTable.build({
        container: this.$container,
        name: 'docExchangeReceiverLogTable',
        toolbar: false,
        searchOnEnterKey: true,
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
                field: 'operationName',
                title: '操作名称'
              },
              {
                field: 'operator',
                title: '操作人员'
              },
              {
                field: 'createTime',
                title: '操作时间'
              },
              {
                field: 'target',
                title: '操作对象'
              },
              {
                field: 'content',
                title: '内容'
              },
              {
                field: 'fileNames',
                title: '附件',
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
              }
            ]
          },
          tableHelper.createDataStoreTableOptions({
            data: data,
            container: this.$container,
            indexes: ['operationName', 'operator', 'target', 'content']
          })
        )
      });
    },

    createTableHeader: function () {
      var html = '';
      html += '<div class="table-summary-row">';
      html += ' <i class="iconfont icon-ptkj-shixinjiantou-xia doc-table-toggle-btn"></i>';
      html += '  <span class="title">操作日志</span>';
      html += '</div>';
      html += tableHelper.createQueryToolbarHtml();

      $('.div-bootstraptable-formbuilder', this.$container).prepend(html);
      tableHelper.buildQueryFields({
        queryFields: [
          {
            label: '操作名称',
            name: 'operationName',
            operator: 'like',
            queryOptions: {
              queryType: 'text',
              queryTypeLabel: '文本框'
            }
          },
          {
            label: '操作人',
            name: 'operator',
            operator: 'like',
            queryOptions: {
              queryType: 'text',
              queryTypeLabel: '文本框'
            }
          },
          {
            label: '操作时间',
            name: 'createTime',
            operator: 'between',
            queryOptions: {
              format: 'YYYY-MM-DD',
              queryType: 'date',
              queryTypeLabel: '日期框'
            }
          },
          {
            label: '操作对象',
            name: 'target',
            operator: 'like',
            queryOptions: {
              queryType: 'text',
              queryTypeLabel: '文本框'
            }
          },
          {
            label: '内容',
            name: 'content',
            operator: 'like',
            queryOptions: {
              queryType: 'text',
              queryTypeLabel: '文本框'
            }
          }
        ],
        container: this.$container
      });
      tableHelper.bindQueryToolbarEvent(this.$container.find('#table_docExchangeReceiverLogTable_info'), this.$container);
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

  return DmsDocExchangerLogView;
});
