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
  var DmsDocExchangerFeedbackView = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };

  var StringUtils = commons.StringUtils;

  commons.inherit(DmsDocExchangerFeedbackView, ListViewWidgetDevelopment, {
    $container: null,
    options: null,
    $tableElement: null,

    beforeRender: function (options) {
      this.$container = options.$container;
      this.options = options;
    },

    init: function (options) {
      this.beforeRender(options);
      this.createTableView();
    },

    refresh: function () {
      this.createTableView();
    },

    createTableView: function () {
      var _this = this;

      server.JDS.call({
        async: true,
        service: 'docExchangerFacadeService.listDocExcFeedbackOrgDetail',
        data: [this.options.docExchangeRecordUuid],
        success: function (res) {
          data = res.data;

          _this.$container.empty();

          // 渲染表格
          _this.createTable(data);

          // 渲染反馈情况汇总行
          _this.createStatusSummaryRowHtml(data);

          // 合并表格单元格
          _this.adjustTable(data);
          _this.bindEvent(data);
        }
      });
    },

    createTable: function (data) {
      var self = this;
      var columns = [
        {
          title: '序号',
          align: 'center',
          width: 50,
          formatter: function (v, row, index) {
            return index + 1;
          }
        },
        {
          field: 'fromUserUnitName',
          title: '收件单位',
          width: 100,
          formatter: function (v, row, i) {
            return row.unitName;
          }
        },
        {
          field: 'feedback',
          title: '反馈情况',
          width: 230,
          formatter: function (v, row, i) {
            if (row.feedback) {
              row.feedbackInfo = '已反馈';
              return '<span>已反馈</span>';
            } else if (row.feedbackTimeLimit) {
              row.feedbackInfo = '待反馈';
              var feedbackTimeLimit = row.feedbackTimeLimit.slice(0, -3);

              return StringUtils.format('<span>待反馈 (<span class="text-red">${feedbackTimeLimit}</span> 到期)</span>', {
                feedbackTimeLimit: feedbackTimeLimit
              });
            } else {
              row.feedbackInfo = '待反馈';
              return '<span class="text-light">待反馈</span>';
            }
          }
        },
        {
          field: 'fromUserName',
          title: '反馈人',
          width: 100
        },
        {
          field: 'feedbackTime',
          title: '反馈时间',
          width: 100,
          formatter: function (v, row, i) {
            if (!row.feedbackTime) {
              return '';
            }

            var feedbackTime = row.feedbackTime.slice(0, -3);

            return StringUtils.format('<span>${feedbackTime} ${overdue}</span>', {
              feedbackTime: feedbackTime,
              overdue: row.overdue ? '<span class="badge badge-danger">逾期</span>' : ''
            });
          }
        },
        {
          field: 'content',
          title: '反馈内容',
          width: 150
        },
        {
          field: 'fileNames',
          title: '附件',
          width: 150,
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
          field: 'answerContent',
          title: '回执',
          width: 150
        }
      ];

      if (this.options.recordStatus == 'SENDED') {
        columns.push({
          width: 300,
          field: 'operation',
          title: '操作',
          titleTooltip: '',
          formatter: function (v, r, i) {
            function createPrimaryLineBtnHtml(text, uuid, type) {
              return StringUtils.format(
                '<button uuid="${uuid}" class="well-btn w-btn-primary w-line-btn" data-type="${type}">${text}</button>',
                {
                  text: text,
                  uuid: uuid,
                  type: type
                }
              );
            }

            var uuid = r.uuid;
            var detailBtn = '';
            if (self.options.configDto.processView == 1) {
              detailBtn = createPrimaryLineBtnHtml('查看详情', uuid, 'show-detail');
            }

            var receiptBtn = createPrimaryLineBtnHtml('回执', uuid, 'request-receipt');
            var answerBtn = createPrimaryLineBtnHtml('要求再次反馈', uuid, 'request-answer');

            return detailBtn + receiptBtn + answerBtn;
          }
        });
      }

      formBuilder.bootstrapTable.build({
        container: this.$container,
        name: 'docExchangeReceiverFeedbackTable',
        toolbar: false,
        table: $.extend(
          {
            columns: columns,
            classes: 'table table-hover docExchangeTable disable-pointer-cursor',
            onPostBody: function (data) {
              if (self.$tableElement) {
                self.adjustTable(data, false);
              }
            }
          },
          tableHelper.createDataStoreTableOptions({
            data: data,
            container: this.$container,
            indexes: ['unitName', 'feedbackInfo', 'fromUserName', 'content', 'answerContent']
          })
        )
      });

      $('#div_docExchangeReceiverFeedbackTable_info').find('.fixed-table-body').css({
        overflow: 'hidden'
      });

      this.$tableElement = this.$container.find('#table_docExchangeReceiverFeedbackTable_info');
    },

    createStatusSummaryRowHtml: function (data) {
      var alreadyFeedbackCount = 0;
      var notFeedbackCount = 0;
      for (var i = 0; i < data.length; i++) {
        data[i].feedback ? alreadyFeedbackCount++ : notFeedbackCount++;
      }

      var html = '';
      html += '<div class="table-summary-row">';
      html += ' <i class="iconfont icon-ptkj-shixinjiantou-xia doc-table-toggle-btn"></i>';
      html += '  <span class="title">反馈情况</span>';
      html += '  <span class="summary">';
      html += '    (';
      html += '    <span class="not-feedback">待反馈 <span class="count">' + notFeedbackCount + '</span></span>';
      html += '    <span class="already-feedback">已反馈 <span class="count">' + alreadyFeedbackCount + '</span></span>';
      html += '    )';
      html += '  </span>';
      html += '</div>';
      html += tableHelper.createQueryToolbarHtml();

      $('.div-bootstraptable-formbuilder', this.$container).prepend(html);
      tableHelper.buildQueryFields({
        queryFields: [
          {
            label: '收件单位',
            name: 'unitName',
            operator: 'like',
            queryOptions: {
              queryType: 'text',
              queryTypeLabel: '文本框'
            }
          },
          {
            label: '反馈情况',
            name: 'feedbackInfo',
            operator: 'in',
            queryOptions: {
              queryType: 'checkbox',
              queryTypeLabel: '复选框',
              optionType: '1',
              optionValue: [
                {
                  id: '待反馈',
                  text: '待反馈'
                },
                {
                  id: '已反馈',
                  text: '已反馈'
                }
              ]
            }
          },
          {
            label: '反馈人',
            name: 'fromUserName',
            operator: 'like',
            queryOptions: {
              queryType: 'text',
              queryTypeLabel: '文本框'
            }
          },
          {
            label: '反馈时间',
            name: 'feedbackTime',
            operator: 'between',
            queryOptions: {
              format: 'YYYY-MM-DD',
              queryType: 'date',
              queryTypeLabel: '日期框'
            }
          },
          {
            label: '反馈内容',
            name: 'content',
            operator: 'like',
            queryOptions: {
              queryType: 'text',
              queryTypeLabel: '文本框'
            }
          },
          {
            label: '回执',
            name: 'answerContent',
            operator: 'like',
            queryOptions: {
              queryType: 'text',
              queryTypeLabel: '文本框'
            }
          }
        ],
        container: this.$container
      });
      tableHelper.bindQueryToolbarEvent(this.$container.find('#table_docExchangeReceiverFeedbackTable_info'), this.$container);
    },

    adjustTable: function (data, updateSummarryCount) {
      var $tbody = this.$tableElement.find('tbody');

      var $currentUnitCell = null;
      var currentUnitName = '';
      var rowSpanCount = 0;

      function setRowSpan() {
        $currentUnitCell.attr('rowSpan', rowSpanCount).css('vertical-align', 'middle');
        $currentUnitCell.next().attr('rowSpan', rowSpanCount).css('vertical-align', 'middle');
        $currentUnitCell.prev().attr('rowSpan', rowSpanCount).css('vertical-align', 'middle');

        $currentUnitCell = null;
        currentUnitName = '';
        rowSpanCount = 0;
      }

      function resetSummaryCount() {
        $('#tab_feedback_info .table-summary-row .not-feedback .count').text(notFeedbackCount);
        $('#tab_feedback_info .table-summary-row .already-feedback .count').text(feedbackCount);
      }

      var currentRowNo = 1;
      var notFeedbackCount = 0;
      var feedbackCount = 0;

      for (var i = 0; i < data.length; i++) {
        var item = data[i];

        if (!item.feedback) {
          // 未反馈时禁用“回执”和“再次要求反馈”按钮
          var $btns = $tbody.find(
            StringUtils.format('tr[data-index="${idx}"] [data-field=operation]', {
              idx: i
            })
          );
          $btns.find('[data-type=request-receipt], [data-type=request-answer], [data-type=show-detail] ').addClass('w-disable-btn');
        }

        if (!item.feedback && rowSpanCount > 1) {
          // 当前条为未反馈，且上一单位的行数大于1，则设置单位单元格的rowspan
          setRowSpan();
        }

        if (item.unitName !== currentUnitName && rowSpanCount > 0) {
          // 当前条已反馈，且与上一条不同单位，则设置单位单元格的rowspan
          setRowSpan();
        }

        if (rowSpanCount === 0) {
          $currentUnitCell = $tbody.find(
            StringUtils.format('tr[data-index="${idx}"] td[data-field=fromUserUnitName]', {
              idx: i
            })
          );
          currentUnitName = item.unitName;
          rowSpanCount++;

          $currentUnitCell.prev().text(currentRowNo);
          currentRowNo++;

          if (item.feedback) {
            feedbackCount++;
          } else {
            notFeedbackCount++;
          }
        } else if (item.unitName === currentUnitName) {
          rowSpanCount++;

          var $cell = $tbody.find(
            StringUtils.format('tr[data-index="${idx}"] td[data-field=fromUserUnitName]', {
              idx: i
            })
          );
          $cell.next().next().css('border-left', '1px solid #e8e8e8');
          $cell.next().remove();
          $cell.prev().remove();
          $cell.remove();

          $tbody
            .find(
              StringUtils.format('tr[data-index="${idx}"] td[data-field=operation]', {
                idx: i
              })
            )
            .empty();
        } else if (currentRowNo !== i + 1) {
          var $cell = $tbody.find(
            StringUtils.format('tr[data-index="${idx}"] td[data-field=fromUserUnitName]', {
              idx: i
            })
          );
          $cell.prev().text(currentRowNo);
          currentRowNo++;
          if (item.feedback) {
            feedbackCount++;
          } else {
            notFeedbackCount++;
          }
        }
      }

      // 遍历结束时，如果当前单元格未设置rowSpan，则需要设置
      if (rowSpanCount > 1) {
        setRowSpan($currentUnitCell);
      }
      if (updateSummarryCount !== false) {
        resetSummaryCount();
      }
    },

    bindEvent: function () {
      var _self = this;

      this.$container.on('click', 'td[data-field="operation"] button', function () {
        var $btn = $(this);
        if ($btn.hasClass('w-disable-btn')) {
          // 禁用按钮
          return;
        }

        var btnType = $btn.attr('data-type');

        var uuid = $btn.attr('uuid');

        switch (btnType) {
          case 'show-detail':
            // 查看详情
            var index = $btn.closest('tr').attr('data-index');
            var data = _self.$tableElement.bootstrapTable('getData')[index];
            _self.showDetailDialog(data);
            break;

          case 'request-receipt':
          case 'request-answer':
            var dialogTitle = $btn.text();
            _self.showRequestReceiptOrFeedbackDialog(dialogTitle, uuid);
            break;

          default:
            break;
        }
      });

      this.$container
        .find('.table-summary-row')
        .off('click', '.doc-table-toggle-btn')
        .on('click', '.doc-table-toggle-btn', function () {
          var $this = $(this);
          $this.toggleClass('closed');
          // $this.closest('.table-summary-row').next('.bootstrap-table').slideToggle();
          $this.closest('.table-summary-row').next('.table-toolbar-container').slideToggle().next('.bootstrap-table').slideToggle();
        });
    },

    showDetailDialog: function (data) {
      var _self = this;

      var title = data.fromUserUnitName || data.unitName;

      var detailResult = _self.getDetailData(data);
      var content = _self.getDetailDialogHtml(detailResult, title);
      var buttons = {
        cancel: {
          label: '关闭',
          className: 'btn-default'
        }
      };

      _self.popDialog(content, title, buttons);
    },

    getDetailData: function (data) {
      var result;

      server.JDS.call({
        service: 'docExchangerFacadeService.listRelatedDoc',
        async: false,
        data: [data.recordDetailUuid],
        success: function (res) {
          result = res.data;
        }
      });

      return result;
    },

    getDetailDialogHtml: function (detailResult, unitName) {
      var content = '';

      if (detailResult.length === 0) {
        // 无数据
        var noDataHtml = '';
        noDataHtml += '<div class="well-table-no-data" style="background-position: center 38  px;"></div>';
        noDataHtml += '<div style="text-align: center; color: #666;">相关文档不可见，' + unitName + '已关闭查看权限</div>';
        return noDataHtml;
      }

      // 标题
      content += '<h3 class="doc-detail-dialog-header">';
      content += '  相关文档';
      content += '  <div class="w-tooltip">';
      content += '    <i class="iconfont icon-ptkj-tishishuoming"></i>';
      content += '    <div class="w-tooltip-content">';
      content += '      收件单位对本文档进行内部处理的相关文档';
      content += '    <div>';
      content += '  <div>';
      content += '</h3>';

      // 表格
      content += '<table class="doc-detail-dialog-table">';
      content += '  <tbody>';

      for (var i = 0; i < detailResult.length; i++) {
        var item = detailResult[i];
        content += '    <tr>';
        content += '      <td>' + item.processingMethod + '</td>';
        content += '      <td>';
        content += StringUtils.format('<a target="_blank" href="${href}">${docTitle}</a>', {
          href: item.docLink,
          docTitle: item.docTitle
        });
        content += '        <span class="text-light">(' + item.createTime + ')</span>';
        content += '        </a>';
        content += '      </td>';
        content += '    </tr>';
      }

      content += '  </tbody>';
      content += '</table>';

      return content;
    },

    createAnswerHtml: function (isRequestFeedbackAgain) {
      var $form = $('<form>', {
        id: 'answerContainer',
        class: 'dyform'
      });
      $form.append(
        $('<label>').text(isRequestFeedbackAgain ? '要求再次反馈原因' : '回执内容'),
        $('<br>'),
        $('<textarea>', {
          id: 'answerContent',
          style: 'resize:vertical;height:100px;width:100%;',
          maxlength: 150,
          class: 'editableClass',
          placeholder: isRequestFeedbackAgain ? '请输入要求再次反馈原因' : '请输入回执内容'
        })
      );
      return $form[0].outerHTML;
    },

    popDialog: function (html, title, buttons, shownCallback) {
      return appModal.dialog({
        title: title,
        message: html,
        size: 'middle',
        buttons: buttons,
        shown: function () {
          if ($.isFunction(shownCallback)) {
            shownCallback();
          }
        }
      });
    },

    showRequestReceiptOrFeedbackDialog: function (title, uuid) {
      var _self = this;

      var isReqFeedbackAgain = title == '要求再次反馈';

      var $dialog = _self.popDialog(
        _self.createAnswerHtml(isReqFeedbackAgain),
        title,
        {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              appModal.showMask('处理中，请稍后...');
              server.JDS.call({
                service: 'docExchangerFacadeService.answerFeedbackDetail',
                data: [uuid, $('#answerContent', $dialog).val(), isReqFeedbackAgain ? 'SENDER_REQUEST_FEEDBACK_AGAIN' : 'SENDER_ANSWER'],
                success: function (res) {
                  appModal.hideMask();
                  if (res.data) {
                    $dialog.find('.bootbox-close-button').trigger('click');
                    _self.refresh();
                  } else {
                    appModal.info(title + '异常');
                  }
                },
                error: function (jqXHR) {
                  appModal.hideMask();
                },
                async: false
              });
              return false;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function () {}
          }
        },
        function () {}
      );
    }
  });

  return DmsDocExchangerFeedbackView;
});
