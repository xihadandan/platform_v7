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
  var DmsDocExchangerUrgeView = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };

  commons.inherit(DmsDocExchangerUrgeView, ListViewWidgetDevelopment, {
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
        service: 'docExchangerFacadeService.listDocExcUrgeDetail',
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
        name: 'docExchangeReceiverUrgeTable',
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
                field: 'urgeWayName',
                title: '催办方式'
              },
              {
                field: 'createTime',
                title: '催办时间'
              },
              {
                field: 'content',
                title: '催办内容'
              }
            ]
          },
          tableHelper.createDataStoreTableOptions({
            data: this.loadData(),
            container: this.$container,
            indexes: ['toUserName', 'content']
          })
        )
      });
    },

    createTableHeader: function () {
      var html = '';
      html += '<div class="table-summary-row">';
      html += ' <i class="iconfont icon-ptkj-shixinjiantou-xia doc-table-toggle-btn"></i>';
      html += '  <span class="title">催办记录</span>';
      html += '</div>';
      html += tableHelper.createQueryToolbarHtml();

      $('.div-bootstraptable-formbuilder', this.$container).prepend(html);
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
            label: '催办方式',
            name: 'urgeWayName',
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
            label: '催办内容',
            name: 'content',
            operator: 'like',
            queryOptions: {
              queryType: 'text',
              queryTypeLabel: '文本框'
            }
          },
          {
            label: '催办时间',
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
      tableHelper.bindQueryToolbarEvent(this.$container.find('#table_docExchangeReceiverUrgeTable_info'), this.$container);
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

  return DmsDocExchangerUrgeView;
});
