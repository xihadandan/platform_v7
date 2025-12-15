define([
  'jquery',
  'commons',
  'constant',
  'server',
  'appContext',
  'ListViewWidgetDevelopment',
  'appModal',
  'formBuilder',
  'mui-webmailBox'
], function ($, commons, constant, server, appContext, ListViewWidgetDevelopment, appModal, formBuilder, webmailBox) {
  var AppEmailListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  var StringUtils = commons.StringUtils;
  var Browser = commons.Browser;
  var menuitem = {
    navType: Browser.getQueryString('navType'),
    uuid: Browser.getQueryString('tagUuid'),
    tagName: Browser.getQueryString('tagName'),
    tagColor: Browser.getQueryString('tagColor'),
    folderCode: Browser.getQueryString('folderCode'),
    folderName: Browser.getQueryString('folderName')
  };
  commons.inherit(AppEmailListDevelopment, ListViewWidgetDevelopment, {
    beforeRender: function (options, configuration) {
      var _self = this;
      if (menuitem && menuitem.navType == 3) {
        // 自定义文件夹的邮件列表加载
        var otherConditions = [],
          paneName;
        if (menuitem.folderCode) {
          var folderCode = menuitem.folderCode;
          paneName = menuitem.folderName;
          otherConditions.push({
            columnIndex: 'MAILBOX_NAME',
            value: folderCode,
            type: 'eq'
          });
        } else if (menuitem.tagColor) {
          paneName = menuitem.tagName;
          otherConditions.push({
            sql: " exists (select 1 from wm_mail_rela_tag tag where tag.mail_uuid=this_.uuid and tag.tag_uuid='" + menuitem.uuid + "')"
          });
        }
        _self.addOtherConditions(otherConditions);
        $.ui.setTitle(paneName);
      }
    },

    getCurrentMenu: function () {
      if (menuitem && menuitem.navType == 3) {
        if (menuitem.folderCode) {
          return '我的文件夹';
        } else if (menuitem.tagColor) {
          return '我的标签';
        }
      }
      return null;
    },

    afterRender: function (options, configuration) {
      var _self = this;
      // _self.loadMoveFolderButtonGroup();
      // _self.loadTagMailButtonGroup();
      _self.bindEvent();
    },

    bindEvent: function () {
      var _self = this;
      var $pageElement = _self.getWidget().pageContainer.element;

      $pageElement.on('AppEmailList.Refresh', function () {
        _self.getWidget().refresh();
      });

      var hideTimeout = null;
      $pageElement.on('hover', '.li_class_tags', function (e) {
        if (e.type == 'mouseenter') {
          window.clearTimeout(hideTimeout);
          $('.jsTagButtonsDiv').show();
        } else {
          hideTimeout = window.setTimeout(function () {
            $('.jsTagButtonsDiv').hide();
          }, 300);
        }
      });
    },

    // 加载每一行表格的邮件所标记的标签
    loadTableMailTags: function (refreshEmailUuids) {},

    getAdjustFontColor: function (color) {
      if (!color) {
        return;
      }
      color = color.slice(1);
      if (parseInt(color, 16) > 14408667) {
        return 'black';
      }
      return '';
    },

    refreshBadgeNum: function () {
      //
    },

    // 行点击事件
    onClickRow: function (rowNum, row, $element, event) {
      var self = this;
      var mailboxUuid = row['UUID'];
      var status = row['STATUS'];
      var options = $.extend(
        {
          status: status,
          currentRowNum: rowNum,
          mailboxUuid: mailboxUuid
        },
        row
      );
      if (status === '0' || status === 0) {
        webmailBox.editEmail(options);
      } else {
        options.dataProvider = self.getDataProvider();
        webmailBox.showEmail(options);
      }
      self.refreshBadgeNum();
    },

    // 加载用于“移动到”按钮组内的文件夹按钮
    loadMoveFolderButtonGroup: function (refresh) {},

    // 加载用于“标记为”按钮组内的标签按钮
    loadTagMailButtonGroup: function (refresh) {},

    // 新建文件夹并移动选中的邮件
    addFolderAndMoveIn: function () {
      var _self = this;
      _self.moveIn.apply(this, arguments);
    },

    // 逻辑删除
    logicDelEmail: function (event, options) {
      var self = this;
      var mailboxUuids = self.getSelectedRowIds();
      webmailBox.logicDelEmail.apply(self, [
        mailboxUuids,
        function () {
          self.refresh();
          self.refreshBadgeNum();
          return false;
        }
      ]);
    },
    /*
     * truncateMsg : function(event, options){ $.alert("truncateMsg"); },
     */
    // 彻底删除
    physicsDelEmail: function () {
      var _self = this;
      var mailboxUuids = _self.getSelectedRowIds();
      if (mailboxUuids.length == 0) {
        appModal.alert('请选择记录！');
      } else {
        // console.log(mailboxUuids);
        appModal.confirm('确认要彻底删除吗?', function (result) {
          if (result) {
            server.JDS.call({
              service: 'wmWebmailService.deletePhysical',
              data: [mailboxUuids],
              async: false,
              success: function (result) {
                appModal.success('彻底删除成功');
                _self.refresh();
                _self.refreshBadgeNum();
              },
              error: function (jqXHR) {
                appModal.alert('彻底删除失败!');
              }
            });
          }
        });
      }
    },

    // 转发
    forwardEmail: function () {
      var mailboxUuids = this.getSelectedRowIds();
      if (mailboxUuids.length == 0) {
        appModal.alert('请选择记录！');
      } else if (mailboxUuids.length != 1) {
        appModal.alert('一次只能选择一条记录！');
      } else {
        var transferUrl = ctx + '/webmail/transfer?mailboxUuid=' + mailboxUuids[0];
        window.open(transferUrl);
      }
    },

    // 回复
    replyEmail: function () {
      var mailboxUuids = this.getSelectedRowIds();
      if (mailboxUuids.length == 0) {
        appModal.alert('请选择记录！');
      } else if (mailboxUuids.length != 1) {
        appModal.alert('一次只能选择一条记录！');
      } else {
        var replyUrl = ctx + '/webmail/reply?mailboxUuid=' + mailboxUuids[0];
        window.open(replyUrl);
      }
    },

    // 回复全部
    replayAllEmail: function () {
      var mailboxUuids = this.getSelectedRowIds();
      if (mailboxUuids.length == 0) {
        appModal.alert('请选择记录！');
      } else if (mailboxUuids.length != 1) {
        appModal.alert('一次只能选择一条记录！');
      } else {
        var replyAllUrl = ctx + '/webmail/reply/all?mailboxUuid=' + mailboxUuids[0];
        window.open(replyAllUrl);
      }
    },

    // 移除邮件的相关标签
    removeMailTags: function (emailUuids, tagUuid, successCallback) {
      var _self = this;
      server.JDS.call({
        service: 'wmMailTagFacadeService.deleteEmailRelaTag',
        data: [emailUuids, tagUuid],
        async: false,
        success: function (result) {
          if ($.isFunction(successCallback)) {
            successCallback();
            return;
          }
          if (_self.getCurrentMenu() == '我的标签') {
            _self.refresh();
          } else {
            _self.loadTableMailTags(emailUuids);
          }
          appModal.info('移除标签成功');
        },
        error: function (jqXHR) {
          appModal.alert('移除标签失败!');
        }
      });
    },

    // 已读邮件
    markReaded: function () {
      var self = this;
      var emailUuids = self.getSelectedRowIds();
      if (emailUuids.length == 0) {
        appModal.alert('请先勾选需要标记为已读的邮件');
        return;
      }
      webmailBox.markReaded.apply(self, [
        emailUuids,
        function () {
          self.refresh();
          self.refreshBadgeNum();
          return false;
        }
      ]);
    },
    // 行未读邮件
    markRowUnreaded: function (event, options, row) {
      var self = this;
      var mailboxUuids = [];
      mailboxUuids.push(row['UUID']);
      webmailBox.markUnReaded.apply(self, [
        mailboxUuids,
        function () {
          self.refresh();
          self.refreshBadgeNum();
          return false;
        }
      ]);
    },
    // 未读邮件
    markUnReaded: function () {
      var self = this;
      var emailUuids = self.getSelectedRowIds();
      if (emailUuids.length == 0) {
        appModal.alert('请先勾选需要标记为未读的邮件');
        return;
      }
      webmailBox.markUnReaded.apply(self, [
        emailUuids,
        function () {
          self.refresh();
          self.refreshBadgeNum();
          return false;
        }
      ]);
    },

    // 移除选中的邮件的所有标签
    removeSelectedMailTags: function () {
      var emailUuids = this.getSelectedRowIds();
      if (emailUuids.length == 0) {
        appModal.alert('请先勾选需要移除标签的邮件');
        return;
      }
      this.removeMailTags(emailUuids);
    },

    // 获取选中的行数据uuid
    getSelectedRowIds: function () {
      var selections = this.getSelections();
      var mailboxUuids = [];
      if (selections != null && selections.length > 0) {
        for (var i = 0; i < selections.length; i++) {
          mailboxUuids.push(selections[i]['UUID']);
        }
      }
      return mailboxUuids;
    },

    logicDelRowEmail: function (event, options, row) {
      var self = this;
      var mailboxUuids = [];
      mailboxUuids.push(row['UUID']);
      webmailBox.logicDelEmail.apply(self, [
        mailboxUuids,
        function () {
          self.refresh();
          self.refreshBadgeNum();
          return false;
        }
      ]);
    },
    // 标记邮件,见markEmail
    markMsg: function () {
      var self = this;
      console.error('markMsg is @Deprecated,markEmail replace with markEmail');
      self.markEmail.apply(self, arguments);
    },
    // 标记邮件
    markEmail: function () {
      var self = this;
      var emailUuids = self.getSelectedRowIds();
      if (emailUuids.length <= 0) {
        return $.toast('必须选择一条数据');
      }
      webmailBox.markEmail.apply(self, [
        emailUuids,
        function () {
          self.refresh();
          self.refreshBadgeNum();
          return false;
        }
      ]);
    },
    // 移动邮件,见moveEmail
    moveIn: function () {
      var self = this;
      console.error('moveIn is @Deprecated,moveIn replace with moveEmail');
      self.moveEmail.apply(self, arguments);
    },
    // 移动邮件
    moveEmail: function () {
      var self = this;
      var emailUuids = self.getSelectedRowIds();
      if (emailUuids.length <= 0) {
        return $.toast('必须选择一条数据');
      }
      webmailBox.popSelectFolderDialog.apply(self, [
        emailUuids,
        function () {
          self.refresh();
          self.refreshBadgeNum();
          return false;
        }
      ]);
    }
  });

  return AppEmailListDevelopment;
});
