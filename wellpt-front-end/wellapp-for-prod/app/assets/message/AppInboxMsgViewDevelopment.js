define(['jquery', 'commons', 'constant', 'server', 'AppOutboxMsgViewDevelopment', 'appModal', 'multiOrg', 'ckeditor'], function (
  $,
  commons,
  constant,
  server,
  AppOutboxMsgViewDevelopment,
  appModal,
  multiOrg
) {
  var JDS = server.JDS;
  var InboxMsgModule = function () {
    AppOutboxMsgViewDevelopment.apply(this, arguments);
  };

  commons.inherit(InboxMsgModule, AppOutboxMsgViewDevelopment, {
    beforeLoadData: function (options, configuration) {
      $(this.widget.element).parents('.ui-wPage').first().css({
        background: 'transparent'
      });
      var uuid = this.getParam('classifyUuid');
      this.removeParam('classifyUuid');
      this.clearOtherConditions();
      var index = $(this.widget.element).parent().data('index');
      if (index == 0) {
        if (uuid == undefined) {
          uuid = this.getClassifyUuid();
        }
        if (uuid == 'user') {
          this.addOtherConditions([
            {
              columnIndex: 'SENDER',
              value: 'system',
              type: 'ne'
            }
          ]);
        } else if (uuid != undefined && uuid != '' && uuid != 'all') {
          this.addOtherConditions([
            {
              columnIndex: 'CLASSIFY_UUID',
              value: uuid,
              type: 'eq'
            }
          ]);
        }
      }
      var _self = this;
      _self.isInternetUser = false;
      var iframes = top.$('iframe');
      var src = '';
      $.each(iframes, function (index, item) {
        if ($(item).attr('src') && $(item).attr('src').indexOf('myMsg') > -1) {
          src = $(item).attr('src');
        }
      });
      if (src.indexOf('246174de446c31abe74fb2285e1ffcd9') > -1) {
        _self.isInternetUser = true;
      }
      if (_self.isInternetUser) {
        $(_self.widget.element).find('.btn_class_btnNewMsg').hide();
      }
    },

    onLoadSuccess: function (data) {
      var _self = this;
      var $element = $(_self.widget.element);
      setTimeout(function () {
        var newData = data.rows;
        var $table = $element.find('.fixed-table-body table');
        for (var i = 0; i < newData.length; i++) {
          var item = newData[i];
          var html = '';
          var msgParam = JSON.parse(item.MESSAGE_PARM);
          _self.getIconBg(item, i);
          if (msgParam.callbackJson) {
            var callbackJson = JSON.parse(msgParam.callbackJson);
            html += _self.getBtn(callbackJson, 'message-list');
          }

          if (msgParam.relatedTitle && msgParam.relatedUrl) {
            html += "<a class='msg-relatedUrl' href='" + msgParam.relatedUrl + "' target='_blank'>" + msgParam.relatedTitle + '</a>';
          }

          $table
            .find('tr:eq(' + (i + 1) + ')')
            .find('td .underline-btn')
            .append(html);
        }

        $element.find('.btn_class_btnForwardMsg, .btn_class_btnReplyMsg, .btn_class_btnDelMsg').hide();
        if (_self.isInternetUser) {
          $element.find("tr[data-index='" + i + "'] .btnForwardMsg").hide();
          $element.find("tr[data-index='" + i + "'] .btnReplyMsg").hide();
        }

        $($table).on('click', 'tr .underline-btn btn-group', function (e) {
          e.stopPropagation();
          e.preventDefault();
        });
        $($table)
          .off('click', 'tr .underline-btn button.well-btn-callback,tr .underline-btn li span.well-btn-callback')
          .on('click', 'tr .underline-btn button.well-btn-callback,tr .underline-btn li span.well-btn-callback', function (e) {
            var $this = $(this);
            _self.currentRowNum = $(e.target).parents('tr').data('index');

            e.stopPropagation();
            e.preventDefault();
            if ($this.data('target') && $this.data('event')) {
              // 默认转发按钮事件
              if ($this.hasClass('btnForwardMsg') || $this.parent('li').hasClass('btnForwardMsg')) {
                _self.btnForwardMsg(e);
                return;
              }

              // 默认删除按钮事件
              if ($this.hasClass('btnDelMsg') || $this.parent('li').hasClass('btnDelMsg')) {
                _self.btnDelMsg(e);
                return;
              }

              // 默认回复按钮事件
              if ($this.hasClass('btnReplyMsg') || $this.parent('li').hasClass('btnReplyMsg')) {
                _self.btnReplyMsg(e);
                return;
              }

              // 自定义按钮事件
              var target = $this.data('target');
              var eventManger = $this.data('event').eventHandler;
              var eventParam = $this.data('event').params;
              if ($this.hasClass('btnExpDetail')) {
                var url = '/web/app/page/preview/6001a4908f9afe87a4b4b9a278bff001?pageUuid=' + eventManger.id.split('_')[2];
                var dataUuid = JSON.parse(JSON.parse(_self.getData()[0].MESSAGE_PARM).callbackJson).events[2].eventManger.params.uuid;
                url = url + '&uuid=' + dataUuid;
                window.open(ctx + url);
                return;
              }

              if ($this.hasClass('btnImpDetail')) {
                var url = '/web/app/page/preview/56111a4071fa57436e77120e3d55eb67?pageUuid=' + eventManger.id.split('_')[2];
                var dataUuid = JSON.parse(JSON.parse(_self.getData()[0].MESSAGE_PARM).callbackJson).events[2].eventManger.params.uuid;
                url = url + '&uuid=' + dataUuid;
                window.open(ctx + url);
                return;
              }

              var opt = {
                target: target.position,
                targetWidgetId: target.widgetId,
                refreshIfExists: target.refreshIfExists,
                eventTarget: target,
                appId: eventManger.id,
                appType: eventManger.type,
                appPath: eventManger.path,
                appData: top.appContext.getCurrentUserAppData().appData,
                params: eventParam
              };
              top.appContext.pageContainer.startApp(opt);
            }
          });
      }, 0);
    },

    currentRowNum: null,
    onClickRow: function (rowNum, row, $element, field, e) {
      this.currentRowNum = rowNum;
      e.stopPropagation();
      $('.wellpt-msg-wrapper > .panel-body > .panel-tab-header').css({
        zIndex: -1
      });
      var $dom = $(e.target);
      var $tbale = $(this.getWidget().element);
      if ($tbale.find('.onlySelectItem').size() > 0) {
        if ($element.find('.underPadding').hasClass('checked')) {
          $element.find('.underPadding').removeClass('checked');
        } else {
          $element.find('.underPadding').addClass('checked');
        }
      } else if (
        !$dom.hasClass('well-btn') &&
        !$dom.hasClass('btn-group') &&
        !$dom.parent().hasClass('well-btn') &&
        !$dom.parent().hasClass('btn-group') &&
        !$dom.hasClass('msg-relatedUrl')
      ) {
        var _self = this;
        var title = ' 来自 ' + row['SENDER_NAME'] + '(' + row['RECEIVED_TIME'].substr(0, 19) + ')';
        var outboxUuid = row['MESSAGE_OUTBOX_UUID'];
        var buttons = {
          forwardMsg: {
            label: '转发',
            className: 'well-btn w-btn-primary',
            callback: function () {
              //必须先删除旧的dialog，才能起新的DIALOG,不能会有ID冲突，导致富文本框和附件上传控件出不来的问题
              $dialog.next('.modal-backdrop').remove();
              $dialog.remove();
              _self.showForwardDialog(row);
            }
          },
          retractMsg: {
            label: '回复',
            className: 'well-btn w-btn-primary',
            callback: function () {
              //必须先删除旧的dialog，才能起新的DIALOG,不能会有ID冲突，导致富文本框和附件上传控件出不来的问题
              $dialog.remove();
              $('.modal-backdrop').remove();
              _self.showReplyDialog(row);
            }
          }
        };
        var $dialog = this.viewMsg(row, buttons, title, outboxUuid, function () {
          if (_self.isInternetUser) {
            $dialog.find("button[data-bb-handler='forwardMsg']").hide();
            $dialog.find("button[data-bb-handler='retractMsg']").hide();
          }
          if (row['ISREAD'] == 0) {
            _self.readMsgService([row['UUID']], function () {
              _self.refresh();
              _self.refreshBadgeNum();
              _self.refreshCategory();
            });
          }
        });
      }
    },

    //回复按钮对应的事件
    btnReplyMsg: function (args) {
      // var selections = this.getSelections();
      // var uuids = this.getSelectedRowIds(selections);
      args.stopPropagation();
      args.preventDefault();
      var index = this.currentRowNum;
      var data = this.currentViewRow || this.getData()[index];
      var uuids = [data.UUID];
      if (uuids.length == 0) {
        appModal.error('请选择记录！');
      } else if (uuids.length != 1) {
        appModal.error('一次只能选择一条记录！');
      } else {
        this.showReplyDialog(data);
      }
    },

    //展示回复对话框
    showReplyDialog: function (row) {
      var _self = this;
      var dialogHtml = this.getReplyDialogHtml(row);
      var outboxUuid = row['MESSAGE_OUTBOX_UUID'] || row['messageOutboxUuid'];
      var $replyDialog = this.showSendMsgDialog(dialogHtml, '回复消息', outboxUuid, function () {
        var index = _self.getSelectionIndexes()[0];
        _self.widget.$tableElement.find('tr[data-index=' + index + ']').removeClass('unread');
        if (row['ISREAD'] == 0 || row['isRead'] == 0) {
          _self.readMsgService([row['UUID' || uuid]]);
        }
      });
    },

    //转发按钮对应的事件
    btnForwardMsg: function (args) {
      // var selections = this.getSelections();
      // var uuids = this.getSelectedRowIds(selections);
      args.stopPropagation();
      args.preventDefault();
      var index = this.currentRowNum;
      var data = this.currentViewRow || this.getData()[index];
      var uuids = [data.UUID];
      if (uuids.length == 0) {
        appModal.error('请选择记录！');
      } else if (uuids.length != 1) {
        appModal.error('一次只能选择一条记录！');
      } else {
        this.showForwardDialog(data);
      }
    },

    //展示转发对话框
    showForwardDialog: function (row) {
      var _self = this;
      var dialogHtml = this.getForwardDialogHtml(row);
      var outboxUuid = row['MESSAGE_OUTBOX_UUID'];
      var $forwardDialog = this.showSendMsgDialog(dialogHtml, '转发消息', outboxUuid, function () {
        if (row['ISREAD'] == 0) {
          _self.readMsgService([row['UUID']]);
        }
        var index = _self.getSelectionIndexes()[0];
        _self.widget.$tableElement.find('tr[data-index=' + index + ']').removeClass('unread');
      });
    },

    //删除按钮对应的事件
    btnDelMsg: function (args, $dialog) {
      // var uuids = this.getSelectedRowIds(this.getSelections());
      args.stopPropagation();
      args.preventDefault();
      var index = this.currentRowNum;
      var data = this.currentViewRow || this.getData()[index];
      var uuids = [data.UUID];
      if (uuids.length == 0) {
        appModal.error('请选择记录！');
      } else {
        var _self = this;
        console.log(uuids);
        appModal.confirm({
          message: '确认要删除吗?',
          shown: function () {
            _self.getMask();
          },
          callback: function (result) {
            if (result) {
              $.ajax({
                type: 'POST',
                url: ctx + '/message/content/deleteInboxMessage',
                data: 'uuids=' + uuids,
                dataType: 'text',
                success: function () {
                  appModal.success('删除成功！');
                  _self.refresh();
                  _self.refreshBadgeNum();
                  _self.refreshCategory();

                  if ($dialog) {
                    $dialog.next('.modal-backdrop').remove();
                    $dialog.remove();
                  }
                }
              });
            }
            _self.cancelMask();
          }
        });
      }
    },

    //标识已读按钮对应的事件
    btnMarkRead: function (args) {
      var uuids = this.getSelectedRowIds(this.getSelections());
      if (uuids.length == 0) {
        appModal.error('请选择记录！');
      } else {
        var _self = this;
        appModal.confirm('确认标记为已读?', function (result) {
          if (result) {
            _self.readMsgService(uuids, function () {
              _self.refresh();
              _self.refreshBadgeNum();
            });
          }
        });
      }
    },

    /**
     * 已读服务
     * @param uuids
     * @param successCallback
     */
    readMsgService: function (uuids, successCallback) {
      $.ajax({
        type: 'POST',
        url: ctx + '/message/content/read',
        data: 'uuids=' + uuids,
        dataType: 'text',
        success: function () {
          if ($.isFunction(successCallback)) {
            successCallback();
          }
        }
      });
    },

    //标识未读按钮对应的事件
    btnMarkUnread: function (args) {
      var uuids = this.getSelectedRowIds(this.getSelections());
      if (uuids.length == 0) {
        appModal.error('请选择记录！');
      } else {
        var _self = this;
        appModal.confirm('确认标记为未读?', function (result) {
          if (result) {
            $.ajax({
              type: 'POST',
              url: ctx + '/message/content/unread',
              data: 'uuids=' + uuids,
              dataType: 'text',
              success: function () {
                _self.refresh();
                _self.refreshBadgeNum();
              }
            });
          }
        });
      }
    },

    //收件箱标星按钮对应的事件
    btnMarkStar: function (args) {
      var uuids = this.getSelectedRowIds(this.getSelections());
      if (uuids.length == 0) {
        appModal.error('请选择记录！');
      } else {
        var _self = this;
        $.ajax({
          type: 'POST',
          url: ctx + '/message/content/markInboxflag',
          data: 'uuids=' + uuids + '&markflag=1',
          dataType: 'text',
          success: function () {
            appModal.success('标星成功！');
            _self.refresh();
          }
        });
      }
    },

    //收件箱取消标星按钮对应的事件
    btnCancelMarkStar: function (args) {
      var uuids = this.getSelectedRowIds(this.getSelections());
      if (uuids.length == 0) {
        appModal.error('请选择记录！');
      } else {
        var _self = this;
        $.ajax({
          type: 'POST',
          url: ctx + '/message/content/markInboxflag',
          data: 'uuids=' + uuids + '&markflag=0',
          dataType: 'text',
          success: function () {
            appModal.success('取消标星成功！');
            _self.refresh();
          }
        });
      }
    },

    /**
     * 全部标记为已读
     */
    btnReadMsg: function () {
      var _self = this;
      var classifyUuid = _self.getClassifyUuid() == 'all' ? '' : _self.getClassifyUuid();
      $.ajax({
        type: 'put',
        async: false,
        dataType: 'text',
        contentType: 'application/json',
        url: ctx + '/message/inbox/updateToReadStateByclass?classifyUuid=' + classifyUuid,
        success: function (result) {
          appModal.success('标记成功！', function () {
            _self.refresh();
            _self.refreshCategory();
            top.appContext.getWidgetByCssSelector('.ui-wNewHeader').refreshBadge();
          });
        }
      });
    },
    /**
     * 全部标记为未读
     */
    btnNoReadMsg: function () {
      var _self = this;
      var classifyUuid = _self.getClassifyUuid() == 'all' ? '' : _self.getClassifyUuid();
      $.ajax({
        type: 'put',
        async: false,
        dataType: 'json',
        contentType: 'application/json',
        url: ctx + '/message/inbox/updateToUnReadStateByclass?classifyUuid=' + classifyUuid,
        success: function (result) {
          appModal.success('标记成功！', function () {
            _self.refresh();
            _self.refreshCategory();
            top.appContext.getWidgetByCssSelector('.ui-wNewHeader').refreshBadge();
          });
        }
      });
    },
    /**
     * 批量操作
     * @param args
     */
    btnBatchMsg: function (args) {
      var _self = this;
      var $toolbar = $(args.target).parents('.fixed-table-toolbar');
      $toolbar.find('.bs-bars').hide();
      var tabIndex = $toolbar.parents('.panel-tab-content').data('index');
      var html =
        "<div class='msg-batch-buttons'>" +
        "<button type='button' class='well-btn w-btn-primary w-btn-minor btn-delete-msg'><i class='iconfont icon-ptkj-shanchu'></i>删除</button>";
      if (tabIndex == '0') {
        html +=
          "<button type='button' class='well-btn w-btn-primary w-line-btn btn-tag-read'><i class='iconfont icon-szgy-biaojiweiyidu'></i>标记已读</button>";
      } else {
        html +=
          "<button type='button' class='well-btn w-btn-primary w-line-btn btn-tag-read'><i class='iconfont icon-szgy-biaojiweiyidu'></i>标记未读</button>";
      }
      html += "<i title='退出批量操作' class='msg-batch-close iconfont icon-ptkj-huanyuanhuifu'></i>" + '</div>';
      $toolbar.append(html);
      $toolbar.siblings('.fixed-table-container').addClass('onlySelectItem');

      $toolbar.on('click', '.msg-batch-close', function () {
        $(this).parent('.msg-batch-buttons').hide();
        $toolbar.find('.bs-bars').show();
        $toolbar.siblings('.fixed-table-container').removeClass('onlySelectItem');
        $toolbar.siblings('.fixed-table-container').find('.underPadding').removeClass('checked');
      });
      $toolbar.off('click', '.btn-delete-msg').on('click', '.btn-delete-msg', function () {
        var datas = _self.getData();
        var id = $toolbar.parents('.ui-wBootstrapTable').attr('id');
        var chooseItem = $('#' + id + '_table').find('tr .checked');
        var uuids = [];
        $.each(chooseItem, function (index, item) {
          var trIndex = $(item).parent('tr').data('index');
          uuids.push(datas[trIndex].UUID);
        });
        if (uuids.length > 0) {
          appModal.confirm({
            message: '确认要删除吗?',
            shown: function () {
              _self.getMask();
            },
            callback: function (result) {
              _self.cancelMask();
              if (result) {
                $.ajax({
                  type: 'POST',
                  url: ctx + '/message/content/deleteInboxMessage',
                  data: 'uuids=' + uuids,
                  dataType: 'text',
                  success: function () {
                    _self.refresh();
                    _self.refreshCategory();
                    top.appContext.getWidgetByCssSelector('.ui-wNewHeader').refreshBadge();
                  }
                });
              }
            }
          });
        } else {
          appModal.error({
            message: '请先选择要删除的记录',
            shown: function () {
              _self.getMask();
            },
            callback: function () {
              _self.cancelMask();
            }
          });
        }
      });

      $toolbar.off('click', '.btn-tag-read').on('click', '.btn-tag-read', function () {
        var datas = _self.getData();
        var id = $toolbar.parents('.ui-wBootstrapTable').attr('id');
        var chooseItem = $('#' + id + '_table').find('tr .checked');
        var uuids = [];
        $.each(chooseItem, function (index, item) {
          var trIndex = $(item).parent('tr').data('index');
          uuids.push(datas[trIndex].UUID);
        });

        if (uuids.length > 0) {
          var title = tabIndex == '0' ? '已读' : '未读';
          var url = tabIndex == '0' ? ctx + '/message/content/read' : ctx + '/message/content/unread';
          appModal.confirm({
            message: '确定要标记为' + title,
            shown: function () {
              _self.getMask();
            },
            callback: function () {
              _self.cancelMask();
              $.ajax({
                type: 'POST',
                url: url,
                data: 'uuids=' + [uuids],
                dataType: 'text',
                success: function () {
                  _self.refresh();
                  _self.refreshCategory();
                  top.appContext.getWidgetByCssSelector('.ui-wNewHeader').refreshBadge();
                }
              });
            }
          });
        } else {
          appModal.error({
            message: '请先选择要标记的记录',
            shown: function () {
              _self.getMask();
            },
            callback: function () {
              _self.cancelMask();
            }
          });
        }
      });
    },

    getReplyDialogHtml: function (row) {
      var senderName = row['SENDER_NAME'] || row['senderName'];
      var subject = row['SUBJECT'] || row['subject'];
      var body = row['BODY'] || row['body'];
      var senderTime = row['SENT_TIME'] || row['sendTime'];
      var newSubject = 'Re:' + subject;

      var newBody = this.getReplybody(senderName, subject, body, senderTime);
      var relatedUrl = row['RELATED_URL'] || row['relatedUrl'];
      var relatedTitle = row['RELATED_TITLE'] || row['relatedTitle'];
      var userId = row['SENDER'] || row['sender'];
      var userName = row['SENDER_NAME'] || row['senderName'];
      var dialogHtml = this.getDialogHtml(userId, userName, newSubject, newBody, '', relatedUrl, relatedTitle);
      return dialogHtml;
    },

    // 刷新分类列表
    refreshCategory: function () {
      console.log(this);
      var htmlId = $(this.widget.element).parents('.msg-receive-content').find('.ui-wHtml').attr('id');
      var tableId = $(this.widget.element).parents('.tab-pane').siblings().find('.ui-wBootstrapTable').attr('id');
      if (tableId) {
        appContext.getWidgetById(tableId).refresh();
      }
      appContext.getWidgetById(htmlId).refresh();
    },

    getIconBg: function (row, i) {
      var $table = $(this.widget.element).find('.fixed-table-body table');
      var $cateList = $(this.getWidget().element).parents('.ui-wPanel').siblings('.ui-wHtml').find('.msg-category-item');
      var cateBg = '#64B3EA';
      for (var k = 0; k < $cateList.length; k++) {
        var uuid = $($cateList[k]).data('uuid');
        var bg = $($cateList[k]).data('iconbg');
        if (row.CLASSIFY_UUID && row.CLASSIFY_UUID == uuid) {
          cateBg = bg;
          break;
        }
      }
      $table
        .find('tr:eq(' + (i + 1) + ')')
        .find('td')
        .find('.table-msg-title span.table-msg-cate')
        .css({
          background: cateBg
        });
    },

    getClassifyUuid: function () {
      var uuid = '';
      var id = $(this.widget.element).parents('.ui-wPanel').siblings('.ui-wHtml').attr('id');
      if (appContext.getWidgetById(id)) {
        var $wHtml = $(appContext.getWidgetById(id).element);
        var $items = $wHtml.find('#msg_category_tree .msg-category-item.hasSelectCate');
        if ($items.size() > 0) {
          uuid = $items.data('uuid');
        }
      }
      return uuid;
    }
  });

  return InboxMsgModule;
});
