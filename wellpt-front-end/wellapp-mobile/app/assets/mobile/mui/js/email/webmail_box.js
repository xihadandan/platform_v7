define(['mui', 'commons', 'server', 'appContext', 'appModal', 'formBuilder', 'mui-fileupload', 'multiOrg', 'mui-filepreview'], function (
  $,
  commons,
  server,
  appContext,
  appModal,
  formBuilder,
  muiFileupload,
  unit,
  FileViewer
) {
  var JDS = {
    call: function (options) {
      options.version = '';
      server.JDS.call.apply(this, arguments);
    }
  };
  var StringUtils = commons.StringUtils;
  var SecurityUtils = server.SpringSecurityUtils;
  var FileUpLoad = $.MuiFileUpload.extend({
    init: function (holder, options) {
      var self = this;
      self._super.apply(this, arguments);
      self.on('afterSetValue', function () {
        var fileNum = self.files ? self.files.length : 0;
        var mailWrapper = $(self.holder).closest('.mail-wrapper');
        if (fileNum !== 0) {
          $('.file-num', mailWrapper)[0].innerHTML = fileNum;
        } else {
          $('.file-num', mailWrapper)[0].innerHTML = '';
        }
        // 触发输入
        $.trigger($('#subject', mailWrapper)[0], 'input');
      });
    },
    renderItems: function (files, append) {
      var self = this;
      var mailWrapper = $(self.holder).closest('.mail-wrapper');
      if (!append) {
        $('.file-list', mailWrapper)[0].innerHTML = '';
      }
      var fileListWrapper = $('.file-list', mailWrapper)[0];
      files.forEach(function (n) {
        var fileType = n.fileName.substring(n.fileName.indexOf('.') + 1);
        var iconSpan;
        switch (fileType) {
          case 'doc':
          case 'docx':
            iconSpan = 'file-word-o';
            break;
          case 'xls':
          case 'xlsx':
            iconSpan = 'file-excel-o';
            break;
          case 'ppt':
          case 'pptx':
            iconSpan = 'file-powerpoint-o';
            break;
          case 'rar':
          case 'zip':
            iconSpan = 'file-archive-o';
            break;
          case 'jpg':
          case 'png':
          case 'git':
            iconSpan = 'file-image-o';
            break;
          default:
            iconSpan = 'file-text-o';
        }
        var li = document.createElement('li');
        li.setAttribute('data-fileID', n.fileID);
        li.className = 'mui-table-view-cell';
        var span = document.createElement('span');
        span.className = 'fa fa-' + iconSpan;
        var fileName = document.createElement('span');
        fileName.className = 'mui-ellipsis';
        fileName.innerHTML = n.fileName;
        li.appendChild(span);
        li.appendChild(fileName);
        fileListWrapper.appendChild(li);
      });
      //初始化弹框
      var popoverBox = $('#popup', mailWrapper)[0];
      if (!popoverBox) {
        var popup = document.createElement('div');
        popup.id = 'popup';
        mailWrapper.appendChild(popup);
        formBuilder.buildActionSheet({
          container: popup,
          data: [{
              id: 'delete',
              name: 'delete',
              text: '删除'
            },
            {
              id: 'preview',
              name: 'preview',
              text: '预览'
            }
          ]
        });
      }
      //点击事件
      if ($('.file-list', mailWrapper)[0].innerHTML !== '') {
        var fileId;
        $('.file-list', mailWrapper).on('tap', 'li', function () {
          var _this = this;
          $('#popup #action_sheet_', mailWrapper).popover('show');
          fileId = $(_this)[0].getAttribute('data-fileid');
        });
        $('#popup #action_sheet_', mailWrapper)
          .off('tap', 'li')
          .on('tap', 'li', function () {
            if (this.id == 'delete') {
              self.deleteItem(fileId);
              // self.renderAfterDel();
              $('#popup #action_sheet_', mailWrapper).popover('hide');
            } else if (this.id == 'preview') {
              self.previewItem(fileId);
              $('#popup #action_sheet_', mailWrapper).popover('hide');
            }
          });
      }
    },
    renderAfterDel: function () {
      var self = this;
      var mailWrapper = $(self.holder).closest('.mail-wrapper');
      $('.file-list', mailWrapper)[0].innerHTML = '';
      self.renderItems(self.files);
    },
    deleteItem: function (fileId) {
      var self = this;
      var delItem = self.getFile(fileId);
      // console.log(self.files);
      self.deleteFileItem(delItem);
      self.setItems(self.files, false); // 刷新界面
    },
    previewItem: function (fileId) {
      var self = this;
      var previewItem = self.getFile(fileId);
      var previewObj = self.getPreviewObj(previewItem);
      FileViewer.preview(previewObj);
    }
  });

  (function ($) {
    $.fn.json2form = function (bean) {
      for (var property in bean) {
        var value = bean[property];
        if (typeof value === 'undefined' || value == null) {
          value = value || '';
        }
        var element = $("input[name='" + property + "']", this[0])[0];
        if (typeof element === 'undefined' || element == null) {
          element = $("select[name='" + property + "']", this[0])[0];
        }
        if (typeof element === 'undefined' || element == null) {
          element = $("textarea[name='" + property + "']", this[0])[0];
        }
        if (element && element.tagName.toLowerCase() === 'input') {
          element.value = value;
        } else if (element && element.tagName.toLowerCase() === 'textarea') {
          element.innerHTML = value;
        } else if (element && element.tagName.toLowerCase() === 'select') {
          $.each(element.options, function (i, option) {
            if (StringUtils.contains(value, option.value)) {
              option.setAttribute('selected', 'selected');
            }
          });
        }
      }
    };
    $.fn.form2json = function (bean) {
      for (var property in bean) {
        var element = $("input[name='" + property + "']", this[0])[0];
        if (typeof element === 'undefined' || element == null) {
          element = $("select[name='" + property + "']", this[0])[0];
        }
        if (typeof element === 'undefined' || element == null) {
          element = $("textarea[name='" + property + "']", this[0])[0];
        }
        if (element && (element.tagName.toLowerCase() === 'input' || element.tagName.toLowerCase() === 'select')) {
          bean[property] = element.value;
        } else if (element && element.tagName.toLowerCase() === 'textarea') {
          bean[property] = element.innerHTML;
        }
      }
    };
  })($);

  // 保存
  var btn_save = 'B009002001';
  // 发送
  var btn_send = 'B009002002';
  // 转发
  var btn_transfer = 'B009002003';
  // 回复
  var btn_reply = 'B009002004';
  // 回复全部
  var btn_reply_all = 'B009002005';
  // 删除
  var btn_delete = 'B009002006';
  // 业务VO类
  var bean = {
    mailboxUuid: null, // 邮箱数据UUID
    fromMailAddress: null, // 发送邮件地址
    toUserName: null, // 收件人名称
    toMailAddress: null, // 收件人地址
    ccUserName: null, // 抄送人名称
    ccMailAddress: null, // 抄送人地址
    bccUserName: null, // 密送人名称
    bccMailAddress: null, // 密送人地址
    subject: null, // 主题
    content: null, // 内容
    repoFileNames: null, // MONGODB附件名称
    repoFileUuids: null, // MONGODB附件UUID
    replyToUserName: null, // 设置回复人名称
    replyToMailAddress: null
    // 设置回复人地址
  };
  var getService = '';
  var formSelector = '#webmail_form';
  // 操作服务
  var getMailService = 'wmWebmailOutboxService.get';
  var getForTransferService = 'wmWebmailOutboxService.getForTransfer';
  var getForReplyService = 'wmWebmailOutboxService.getForReply';
  var getForReplyAllferService = 'wmWebmailOutboxService.getForReplyAll';
  var saveDraftService = 'wmWebmailOutboxService.saveDraft';
  var sendMailService = 'wmWebmailService.send';
  var deleteMailService = 'wmWebmailOutboxService.delete';
  var getMailPaperService = 'wmMailPaperFacadeService.queryCurrentUserDefaultPaper';
  var getMailSignatures = 'wmMailSignatureFacadeService.queryCurrentUserMailSignatures';
  var getMailSignatureDetail = 'wmMailSignatureFacadeService.getSignatureDetail';
  var getMailDefaultTemplate = 'wmMailTemplateFacadeService.getCurrentUserDefaultTemplate';
  var deleteMailRecentContact = 'wmMailRecentContactFacadeService.deleteCurrentUserRecentContact';
  var getMailRecentContact = 'wmMailRecentContactFacadeService.queryCurrentUserRecentContacts';

  // 展示邮件
  var showEmail = function (option) {
    option = option || {};
    if (typeof option.dataProvider !== 'undefined' || option.dataProvider == null) {
      option.dataList = option.dataProvider.getData() || [];
      option.dataProvider = option.dataProvider.cloneDataStore({
        async: false,
        onDataChange: function (data) {
          option.dataList = option.dataList.concat(data);
        }
      });
    }
    $.ajax({
      url: ctx + '/html/mobile/mui/email/show_mail.html',
      async: false,
      success: function (content) {
        function fillMailData(mailboxUuid) {
          // 获取数据
          appModal.showMask('加载中...');
          JDS.call({
            async: false,
            service: getService,
            data: [mailboxUuid],
            success: function (result) {
              appModal.hideMask();
              // 获取数据后处理
              bean = result.data;
              // 初使化邮件数据
              $('#show_mail ' + formSelector).json2form(bean);
              $('#show_mail #content')[0].innerHTML = bean.content || '';
              $('#show_mail .subject')[0].innerHTML = bean.subject || '';
              $('#show_mail .fromMailAddress')[0].innerHTML = bean.fromUserName || '';
              var repoFiles = bean.repoFiles || [];
              $('#show_mail .mui-icon-paperclip')[0].innerHTML = repoFiles.length;
              fileUpload.addFiles(repoFiles, false);
            },
            error: function () {
              appModal.hideMask();
            }
          });
        }
        var fillFromMailAddresses = function (currentUserId, mailboxUuid) {
          JDS.call({
            service: 'wmWebmailService.getWmWebmailBean',
            data: [currentUserId, mailboxUuid],
            success: function (result) {
              bean = result.data;
              $('#show_mail ' + formSelector).json2form(bean);
              var fromMailAddresses = bean.fromMailAddresses || [];
              var optionsHTML = '';
              for (var i = 0; i < fromMailAddresses.length; i++) {
                optionsHTML += "<option value='";
                optionsHTML += fromMailAddresses[i]['mailAddress'];
                optionsHTML += i === 0 ? "' selected='selected'>" : "'>";
                optionsHTML += fromMailAddresses[i]['userName'];
                optionsHTML += '</option>';
              }
              $('#show_mail #fromMailAddress')[0].innerHTML = optionsHTML;
              if (bean.isRead == 0 || bean.isRead == '0') {
                markReaded([mailboxUuid], function () {
                  return false;
                });
              }
            },
            async: false
          });
        };
        var getEmailUuids = function () {
          var emailUuids = [];
          emailUuids.push(option.mailboxUuid);
          return emailUuids;
        };
        formBuilder.showPanel({
          title: '',
          actionBack: {
            showNavTitle: true
          },
          optButton: option.dataProvider ? {
            label: '<i class="mui-icon mui-icon-arrowup" style="padding: 0px 12px;"></i><i class="mui-icon mui-icon-arrowdown" style="padding: 0px 12px;"></i>',
            callback: function (event, panel) {
              var target = event.target;
              var nextRow = null;
              var nextRowNum = option.currentRowNum || 0;
              if (target && target.classList.contains('mui-icon-arrowup')) {
                --nextRowNum;
                nextRow = option.dataList[nextRowNum];
              } else if (target && target.classList.contains('mui-icon-arrowdown')) {
                ++nextRowNum;
                if (!(nextRow = option.dataList[nextRowNum]) && option.dataProvider.hasNext()) {
                  option.dataProvider.nextPage(false);
                  nextRow = option.dataList[nextRowNum];
                }
              }
              if (nextRow && nextRow['UUID']) {
                fillMailData(nextRow['UUID']);
                option.currentRowNum = nextRowNum;
              } else {
                $.toast('没有更多数据项');
              }
              return false;
            }
          } : null,
          moreNum: 5,
          actions: [{
              id: 'replyEmail',
              name: '回复',
              callback: function (event, panel) {
                var options = {
                  mailboxUuid: option.mailboxUuid
                };
                (options.reply = true), editEmail.apply(this, [options]);
              }
            },
            {
              id: 'forwardEmail',
              name: '转发',
              callback: function (event, panel) {
                var options = {
                  mailboxUuid: option.mailboxUuid
                };
                (options.transfer = true), editEmail.apply(this, [options]);
              }
            },
            {
              id: 'markEmail',
              name: '标记',
              callback: function (event, panel) {
                markEmail.apply(this, [getEmailUuids()]);
              }
            },
            {
              id: 'moveEmail',
              name: '移动',
              callback: function (event, panel) {
                popSelectFolderDialog.apply(this, [getEmailUuids()]);
              }
            },
            {
              id: 'deleteEmail',
              name: '删除',
              cssClass: 'btn-danger',
              callback: function (event, panel) {
                logicDelEmail.apply(this, [getEmailUuids()]);
              }
            }
          ],
          content: content,
          container: '#show_mail'
        });

        var fileUpload = new FileUpLoad($('#show_mail #upload-input')[0], {
          autoUpload: true,
          multiple: true,
          lableText: ''
        });
        $('#show_mail .fileUpload-btn')[0].style.display = 'none';
        $('#show_mail .to-user-detail')[0].addEventListener('tap', function (event) {
          $('#show_mail .user-detail').each(function (i, element) {
            var valSelector = element.getAttribute('value-name');
            var valueElement = $(valSelector, element)[0];
            var toggle = element.classList.contains('mui-hidden');
            if (valueElement && valueElement.value && toggle) {
              element.classList.remove('mui-hidden');
            } else {
              element.classList.add('mui-hidden');
            }
          });
        });
        fillFromMailAddresses(SecurityUtils.getCurrentUserId(), option.mailboxUuid);

        var isTransfer = $('#show_mail #transfer')[0].value == 'true' || option.transfer;
        var isReply = $('#show_mail #reply')[0].value == 'true' || option.reply;
        var isReplyAll = $('#show_mail #replyAll')[0].value == 'true' || option.replyAll;
        var isReplyOrTransfer = isReply || isReplyAll || isTransfer;
        if (StringUtils.isNotBlank(option.mailboxUuid)) {
          var getService = getMailService;
          if (isTransfer) {
            getService = getForTransferService;
          }
          if (isReply) {
            getService = getForReplyService;
          }
          if (isReplyAll) {
            getService = getForReplyAllferService;
          }
          fillMailData(option.mailboxUuid);
        }

        function toastUserName(event) {
          $.toast(this.value);
        }

        $('#show_mail #toUserName')[0].addEventListener('tap', toastUserName);
        $('#show_mail #ccUserName')[0].addEventListener('tap', toastUserName);
        $('#show_mail #bccUserName')[0].addEventListener('tap', toastUserName);
      }
    });
  };

  // 编辑邮件
  var editEmail = function (option) {
    option = option || {};
    option.toMailAddress = option.toMailAddress || '';
    option.ccMailAddress = option.ccMailAddress || '';
    option.bccMailAddress = option.bccMailAddress || '';
    option.toUserName = option.toUserName || option.toMailAddress;
    option.ccUserName = option.ccUserName || option.ccMailAddress;
    option.bccUserName = option.bccUserName || option.bccMailAddress;
    $.ajax({
      url: ctx + '/html/mobile/mui/email/edit_mail.html',
      async: false,
      success: function (content) {
        formBuilder.showPanel({
          buttonText: '发送',
          title: '写邮件',
          actionBack: {
            label: '返回',
            callback: function (event, panel) {
              // 未编辑
              var buttonElement = this;
              if (buttonElement.innerText === '返回') {
                return;
              }
              // 获取邮件数据
              collectMailData();
              var actionMarkId = 'wui-mail-edit-actionsheet-id';
              var actionSheetSelector = 'wui-edit-mark-actionsheet';
              // 更多操作按钮
              var placeholder = document.getElementById('edit_mail');
              var actionSheetWrapper = placeholder.querySelector('.' + actionSheetSelector);
              if (actionSheetWrapper != null) {
                actionSheetWrapper.parentNode.removeChild(actionSheetWrapper);
              }
              actionSheetWrapper = document.createElement('div');
              actionSheetWrapper.classList.add(actionSheetSelector);
              placeholder.appendChild(actionSheetWrapper);
              formBuilder.buildActionSheet({
                sheetId: actionMarkId,
                data: [{
                    id: 'deleteDraft',
                    name: 'deleteDraft',
                    cssClass: 'btn-danger',
                    text: '删除草稿'
                  },
                  {
                    id: 'saveDraft',
                    name: 'saveDraft',
                    text: '保存草稿'
                  }
                ],
                container: actionSheetWrapper
              });
              // 绑定ActionSheet事件
              $(actionSheetWrapper).on('tap', '.mui-table-view-cell', function (event) {
                var _self = this;
                var name = _self.getAttribute('name');
                $('#action_sheet_' + actionMarkId).popover('toggle');
                var isDraft = bean.status === '0' || bean.status === 0;
                if (name === 'deleteDraft' && bean.mailboxUuid && isDraft) {
                  appModal.showMask('邮件删除中...');
                  JDS.call({
                    service: deleteMailService,
                    data: [
                      [bean.mailboxUuid]
                    ],
                    success: function (result) {
                      if (option && $.isFunction(option.afterDeleteSuccess)) {
                        option.afterDeleteSuccess(result);
                      }
                      // 删除成功
                      appModal.hideMask();
                      appModal.success('删除成功!', function () {
                        appContext.getWindowManager().refresh();
                      });
                    },
                    error: function (jqXHR) {
                      // 删除失败
                      appModal.alert('删除失败!');
                    }
                  });
                } else if (name === 'saveDraft') {
                  appModal.showMask('邮件保存中...');
                  // 获取邮件数据
                  JDS.call({
                    service: saveDraftService,
                    data: [bean],
                    success: function (result) {
                      appModal.hideMask();
                      if (option && $.isFunction(option.afterSaveSuccess)) {
                        option.afterSaveSuccess(result);
                      }
                      appModal.success('保存成功！', function () {
                        appContext.getWindowManager().refresh();
                      });
                    },
                    error: function (jqXHR) {
                      appModal.hideMask();
                      appModal.alert('保存失败!');
                    }
                  });
                } else {
                  $.back();
                }
              });
              // 展示actionSheet
              $('#action_sheet_' + actionMarkId).popover('toggle');
              return false;
            }
          },
          optButton: {
            label: '发送',
            callback: function (event, panel) {
              // 获取邮件数据
              collectMailData();
              // 发送人地址不能为空
              if (StringUtils.isBlank(bean.fromMailAddress)) {
                $.toast('发送人邮箱地址不能为空，请先生成邮箱账号!');
                return false;
              } else if (StringUtils.isBlank(bean.toMailAddress)) {
                $.toast('收件人不能为空!');
                return false;
              } else if (bean.subject && bean.subject.length > 20) {
                $.toast('邮件主题限制在20个字符');
                return false;
              }
              var callService = function (bean, quickSendMailFrame) {
                appModal.showMask('邮件发送中...');
                JDS.call({
                  service: sendMailService,
                  data: [bean],
                  success: function (result) {
                    appModal.hideMask();
                    if (option && $.isFunction(option.afterSendSuccess)) {
                      option.afterSendSuccess(result);
                    }
                    appModal.success('发送成功', function () {
                      appContext.getWindowManager().refresh();
                    });
                  },
                  error: function (jqXHR) {
                    appModal.hideMask();
                    var response = JSON.parse(jqXHR.responseText);
                    if (response.msg.indexOf('账号不存在') != -1) {
                      response.msg = response.msg.replace(/</g, '&lt;');
                      response.msg = response.msg.replace(/>/g, '&gt;');
                    }
                    // 提交失败
                    appModal.alert('发送失败!</br>' + response.msg);
                  }
                });
              };

              if (StringUtils.isBlank(bean.subject)) {
                appModal.confirm('邮件没有填写主题，确定继续发送？', function (r) {
                  if (r) {
                    bean.subject = '(无主题)';
                    callService(bean);
                  } else {
                    appModal.hideMask();
                  }
                });
              } else {
                callService(bean);
              }
              return false;
            }
          },
          content: content,
          container: '#edit_mail'
        });
        var fileUpload = new FileUpLoad($('#edit_mail #upload-input')[0], {
          autoUpload: true,
          multiple: true,
          lableText: ''
        });
        var fillFromMailAddresses = function (currentUserId, mailboxUuid) {
          JDS.call({
            service: 'wmWebmailService.getWmWebmailBean',
            data: [currentUserId, mailboxUuid],
            success: function (result) {
              bean = result.data;
              $('#edit_mail ' + formSelector).json2form(bean);
              var fromMailAddresses = bean.fromMailAddresses || [];
              var optionsHTML = '';
              for (var i = 0; i < fromMailAddresses.length; i++) {
                optionsHTML += "<option value='";
                optionsHTML += fromMailAddresses[i]['mailAddress'];
                optionsHTML += i === 0 ? "' selected='selected'>" : "'>";
                optionsHTML += fromMailAddresses[i]['userName'];
                optionsHTML += '</option>';
              }
              $('#edit_mail #fromMailAddress')[0].innerHTML = optionsHTML;
              //							markReaded([mailboxUuid], function(){
              //								return false;
              //							});
            },
            async: false
          });
        };
        fillFromMailAddresses(SecurityUtils.getCurrentUserId(), option.mailboxUuid);

        function collectMailData() {
          $('#edit_mail ' + formSelector).form2json(bean);
          bean.repoFiles = fileUpload.getItems();
          if (bean.repoFiles != null) {
            var fileNames = [];
            var fileUuids = [];
            $.each(bean.repoFiles, function () {
              fileNames.push(this.fileName);
              fileUuids.push(this.fileID);
            });
            bean.repoFileNames = fileNames.join(';');
            bean.repoFileUuids = fileUuids.join(';');
          }
          bean.content = $('#edit_mail #content')[0].innerHTML;
        }

        var isTransfer = $('#edit_mail #transfer')[0].value == 'true' || option.transfer;
        var isReply = $('#edit_mail #reply')[0].value == 'true' || option.reply;
        var isReplyAll = $('#edit_mail #replyAll')[0].value == 'true' || option.replyAll;
        var isReplyOrTransfer = isReply || isReplyAll || isTransfer;
        if (StringUtils.isNotBlank(option.mailboxUuid)) {
          var getService = getMailService;
          if (isTransfer) {
            getService = getForTransferService;
          }
          if (isReply) {
            getService = getForReplyService;
          }
          if (isReplyAll) {
            getService = getForReplyAllferService;
          }
          // 获取数据
          JDS.call({
            async: false,
            service: getService,
            data: [option.mailboxUuid],
            success: function (result) {
              // 获取数据后处理
              bean = result.data;
              // 初使化邮件数据
              $('#edit_mail ' + formSelector).json2form(bean);
              if (StringUtils.isNotBlank(bean.subject)) {
                $.ui.setTitle(bean.subject); // 设置邮件标题
              }
              $('#edit_mail #content')[0].innerHTML = bean.content || ''; //正文内容由设置信纸、模板步骤进行添加
              fileUpload.addFiles(bean.repoFiles || []);
              if ((isReply || isReplyAll) && bean.fromMailAddress && bean.fromUserName) {
                // option.toUserName = bean.fromUserName;
                // option.toMailAddress = bean.fromMailAddress;
              }
            }
          });
        }
        if (option.toUserName && option.toMailAddress) {
          $('#edit_mail #toUserName')[0].value = option.toUserName;
          $('#edit_mail #toMailAddress')[0].value = option.toMailAddress;
        }
        $('#edit_mail #toUserName')[0].addEventListener('tap', function (event) {
          var self = this;
          unit.open({
            selectType: '1',
            labelField: '#edit_mail #toUserName',
            valueField: '#edit_mail #toMailAddress',
            title: self.getAttribute('title'),
            afterSelect: function () {
              $.trigger(self, 'input');
            }
          });
        });
        if (option.ccUserName && option.ccMailAddress) {
          $('#edit_mail #ccUserName')[0].value = option.ccUserName;
          $('#edit_mail #ccMailAddress')[0].value = option.ccMailAddress;
        }
        $('#edit_mail #ccUserName')[0].addEventListener('tap', function (event) {
          var self = this;
          unit.open({
            selectType: '1',
            labelField: '#edit_mail #ccUserName',
            valueField: '#edit_mail #ccMailAddress',
            title: self.getAttribute('title'),
            afterSelect: function () {
              $.trigger(self, 'input');
            }
          });
        });
        if (option.bccUserName && option.bccMailAddress) {
          $('#edit_mail #bccUserName')[0].value = option.bccUserName;
          $('#edit_mail #bccMailAddress')[0].value = option.bccMailAddress;
        }
        $('#edit_mail #bccUserName')[0].addEventListener('tap', function (event) {
          var self = this;
          unit.open({
            selectType: '1',
            labelField: '#edit_mail #bccUserName',
            valueField: '#edit_mail #bccMailAddress',
            title: self.getAttribute('title'),
            afterSelect: function () {
              $.trigger(self, 'input');
            }
          });
        });

        function setCancelLabel(label) {
          var btn = $('#edit_mail>header>.mui-pull-left')[0];
          if (btn == null || typeof btn === 'undefined') {
            return false;
          }
          // btn.innerHTML = "取消";
          $.ui.setLeftNavTitle('取消');
        }

        $('#edit_mail #content')[0].addEventListener('keyup', setCancelLabel);
        $('#edit_mail ' + formSelector).on('input', 'input,select', setCancelLabel);
        $('#edit_mail ' + formSelector).on('change', 'input,select', setCancelLabel);

        $(formSelector + ' #subject')[0].addEventListener('input', function () {
          if (this.value && this.value.length > 20) {
            $.toast('邮件主题限制在20个字符');
            // this.value = this.value.substring(0,20);
          }
        });
      }
    });
  };

  // 获取标签列表
  function _getTagContent() {
    var content = "<ul class='mui-input-group wui-mark-list'>";
    server.JDS.call({
      service: 'wmMailTagFacadeService.queryUserMailTags',
      data: [],
      async: false,
      success: function (result) {
        if (result.data) {
          for (var i = 0, len = result.data.length; i < len; i++) {
            var tagData = result.data[i];
            content += "<li class='mui-input-row'>";
            content += "<div class='mui-radio'>";
            content += "<label for='" + tagData.uuid + "' style='color:" + tagData.tagColor + "';>";
            content += tagData.tagName;
            content += "</label><input name='itemCheckbox' type='radio' id='" + tagData.uuid + "'></input></div>";
            content += '</li>';
          }
        }
      },
      error: function (jqXHR) {}
    });
    content += '</ul>';
    return content;
  }
  // 标记邮件
  function markMailByTag(emailUuids, tagUuid, successCallback) {
    server.JDS.call({
      service: 'wmMailTagFacadeService.addMailRelaTag',
      data: [tagUuid, emailUuids],
      async: false,
      success: function (result) {
        var self = this;
        appModal.success('标记成功', function () {
          if ($.isFunction(successCallback) && successCallback.apply(self, arguments) === false) {
            return;
          }
          appContext.getWindowManager().refresh();
        });
      },
      error: function (jqXHR) {
        appModal.alert('标记失败!');
      }
    });
  }

  function _popNewTagDialog(tag, dialogOpt, successCallback) {
    formBuilder.showPanel({
      title: '标签名称',
      content: "<div class='mui-input-group'><div class='mui-input-row'><input type='input' id='tagName' name='tagName'></div></div>",
      optButton: {
        label: '完成',
        callback: function (event, panel) {
          var tagName = $('input[name=tagName]', panel)[0].value;
          if (StringUtils.isBlank(tagName)) {
            $.toast('请输入标签名称');
            return false;
          } else if (tagName.length > 20) {
            $.toast('标签名称不要超过20个中文字符');
            return false;
          }
          var commitResult = false;
          server.JDS.call({
            async: false,
            data: [{
              uuid: tag ? tag.tagUuid : null,
              tagName: tagName,
              tagColor: 'black'
            }],
            service: 'wmMailTagFacadeService.addMailTag',
            success: function (result) {
              commitResult = true;
              if ($.isFunction(successCallback) && successCallback.call(this, result.data) === false) {
                return;
              }
              $.toast('新建成功');
            },
            error: function (jqXHR) {}
          });
          return commitResult;
        }
      },
      actionBack: {
        label: '取消'
      },
      container: '#mail-movein-view-newtag'
    });
  }
  // 新建标签并进行邮件标记
  function addTagAndMark(emailUuids, successCallback) {
    var content = _getTagContent();
    formBuilder.showPanel({
      title: '标签',
      content: content,
      actions: [{
        id: 'newFloder',
        name: '新建标签',
        callback: function (event, panel) {
          _popNewTagDialog(null, null, function (folderCode) {
            var moveinUl = $('.wui-mark-list', panel)[0];
            var newMoveinUl = $.dom(_getTagContent())[0];
            moveinUl.parentNode.replaceChild(newMoveinUl, moveinUl);
            var folderItem = null;
            if ((folderItem = $("input[id='" + folderCode + "']", panel)[0])) {
              folderItem.checked = true;
            }
          });
        }
      }],
      optButton: {
        label: '完成',
        callback: function (event, panel) {
          var checkeds = $('input[name=itemCheckbox]:checked', panel);
          if (checkeds.length < 0) {
            $.toast('必须选择一个标签');
            return false;
          }
          var folder = checkeds[0].id;
          markMailByTag(emailUuids, folder, successCallback);
        }
      },
      actionBack: {
        label: '取消'
      },
      container: '#mail-mark-view'
    });
  }
  // 已读邮件
  function markReaded(emailUuids, successCallback) {
    server.JDS.call({
      service: 'wmWebmailService.updateMailReadStatus',
      data: [emailUuids, '1'],
      async: false,
      success: function (result) {
        var self = this;
        appModal.success('标记已读成功', function () {
          if ($.isFunction(successCallback) && successCallback.apply(self, arguments) === false) {
            return;
          }
          appContext.getWindowManager().refresh();
        });
      },
      error: function (jqXHR) {
        appModal.toast('标记已读失败!');
      }
    });
  }
  // 未读邮件
  function markUnReaded(emailUuids, successCallback) {
    server.JDS.call({
      service: 'wmWebmailService.updateMailReadStatus',
      data: [emailUuids, '0'],
      async: false,
      success: function (result) {
        var self = this;
        appModal.success('标记未读成功', function () {
          if ($.isFunction(successCallback) && successCallback.apply(self, arguments) === false) {
            return;
          }
          appContext.getWindowManager().refresh();
        });
      },
      error: function (jqXHR) {
        appModal.toast('标记未读失败!');
      }
    });
  }
  // 标记邮件
  function markEmail(emailUuids, successCallback) {
    var actionMarkId = 'wui-mail-mark-actionsheet-id';
    var actionSheetSelector = 'wui-mail-mark-actionsheet';
    // 更多操作按钮
    var pageContainer = appContext.getPageContainer();
    var placeholder = pageContainer.getRenderPlaceholder()[0];
    var actionSheetWrapper = placeholder.querySelector('.' + actionSheetSelector);
    if (actionSheetWrapper != null) {
      actionSheetWrapper.parentNode.removeChild(actionSheetWrapper);
    }
    actionSheetWrapper = document.createElement('div');
    actionSheetWrapper.classList.add(actionSheetSelector);
    placeholder.appendChild(actionSheetWrapper);
    formBuilder.buildActionSheet({
      sheetId: actionMarkId,
      data: [{
          id: 'markMail',
          name: 'markMail',
          text: '标记'
        },
        {
          id: 'markUnreaded',
          name: 'markUnreaded',
          text: '标记未读'
        },
        {
          id: 'markReaded',
          name: 'markReaded',
          text: '标记已读'
        }
      ],
      container: actionSheetWrapper
    });
    // 绑定ActionSheet事件
    $(actionSheetWrapper).on('tap', '.mui-table-view-cell', function (event) {
      var _self = this;
      var name = _self.getAttribute('name');
      $('#action_sheet_' + actionMarkId).popover('toggle');
      if (name === 'markMail') {
        addTagAndMark(emailUuids, successCallback);
      } else if (name === 'markUnreaded') {
        // 标记未读
        markUnReaded(emailUuids, successCallback);
      } else if (name === 'markReaded') {
        // 标记已读
        markReaded(emailUuids, successCallback);
      }
    });
    // 展示actionSheet
    $('#action_sheet_' + actionMarkId).popover('toggle');
  }

  // 移动邮件到指定文件夹
  function moveEmailToFolder(folder, emailUuids, successCallback) {
    server.JDS.call({
      service: 'wmWebmailService.updateMailBoxName',
      data: [folder, emailUuids],
      success: function (result) {
        var self = this;
        appModal.success('移动邮件到文件夹成功', function () {
          if ($.isFunction(successCallback) && successCallback.apply(self, arguments) === false) {
            return;
          }
          appContext.getWindowManager().refresh();
        });
      },
      error: function (jqXHR) {}
    });
  }
  // 获得文件夹内容
  function _getFolderContent() {
    var content = "<ul class='mui-input-group wui-movein-list'>";
    content += "<li class='mui-input-row'>";
    content += "<div class='mui-radio'><label for='INBOX'>收件箱</label><input name='itemCheckbox' type='radio' id='INBOX'></input></div>";
    content += '</li>';
    server.JDS.call({
      service: 'wmMailFolderFacadeService.queryUserFolders',
      data: [],
      async: false,
      success: function (result) {
        if (result.data) {
          for (var i = 0, len = result.data.length; i < len; i++) {
            var folderData = result.data[i];
            content += "<li class='mui-input-row'>";
            content += "<div class='mui-radio'>";
            content += "<label for='" + folderData.folderCode + "'>";
            content += folderData.folderName;
            content += "</label><input name='itemCheckbox' type='radio' id='" + folderData.folderCode + "'></input>";
            content += '</div>';
            content += '</li>';
          }
        }
      },
      error: function (jqXHR) {}
    });
    content += '</ul>';
    return content;
  }
  // 弹出新建文件夹对话框
  function _popNewFolderDialog(folder, dialogOpt, successCallback) {
    formBuilder.showPanel({
      title: '文件夹名称',
      content: "<div class='mui-input-group'><div class='mui-input-row'><input type='input' id='folderName' name='folderName'></div></div>",
      optButton: {
        label: '完成',
        callback: function (event, panel) {
          var folderName = $('input[name=folderName]', panel)[0].value;
          if (StringUtils.isBlank(folderName)) {
            $.toast('请输入文件夹名称');
            return false;
          } else if (folderName.length > 20) {
            $.toast('文件夹名称不要超过20个中文字符');
            return false;
          }
          var commitResult = false;
          server.JDS.call({
            async: false,
            data: [{
              folderName: folderName
            }],
            service: 'wmMailFolderFacadeService.addFolder',
            success: function (result) {
              commitResult = true;
              if ($.isFunction(successCallback)) {
                successCallback(result.data);
              } else {
                $.toast('新建成功');
              }
            },
            error: function (jqXHR) {}
          });
          return commitResult;
        }
      },
      actionBack: {
        label: '取消'
      },
      container: '#mail-movein-view-newfolder'
    });
  }
  // 弹出选择文件夹对话框
  function popSelectFolderDialog(emailUuids, successCallback) {
    var self = this;
    var content = _getFolderContent();
    formBuilder.showPanel({
      title: '移动邮件',
      content: content,
      actions: [{
        id: 'newFloder',
        name: '新建文件夹',
        callback: function (event, panel) {
          _popNewFolderDialog(null, null, function (folderCode) {
            var moveinUl = $('.wui-movein-list', panel)[0];
            var newMoveinUl = $.dom(_getFolderContent())[0];
            moveinUl.parentNode.replaceChild(newMoveinUl, moveinUl);
            var folderItem = null;
            if ((folderItem = $("input[id='" + folderCode + "']", panel)[0])) {
              folderItem.checked = true;
            }
          });
        }
      }],
      optButton: {
        label: '完成',
        callback: function (event, panel) {
          var checkeds = $('input[name=itemCheckbox]:checked', panel);
          if (checkeds.length <= 0) {
            $.toast('必须选择一个夹');
            return false;
          }
          var folder = checkeds[0].id;
          moveEmailToFolder(folder, emailUuids, successCallback);
        }
      },
      actionBack: {
        label: '取消'
      },
      container: '#mail-movein-view'
    });
  }

  function logicDelEmail(mailboxUuids, successCallback) {
    if (mailboxUuids.length <= 0) {
      appModal.toast('请选择记录！');
    } else {
      var _self = this;
      appModal.confirm('确认要删除吗?', function (result) {
        if (result) {
          JDS.call({
            service: 'wmWebmailService.delete',
            data: [mailboxUuids],
            async: false,
            success: function (result) {
              appModal.success('删除成功', function () {
                if ($.isFunction(successCallback) && successCallback.apply(self, arguments) === false) {
                  return;
                }
                appContext.getWindowManager().refresh();
              });
            },
            error: function (jqXHR) {
              appModal.alert('删除失败!');
            }
          });
        }
      });
    }
  }

  return {
    editEmail: editEmail,
    showEmail: showEmail,
    markEmail: markEmail,
    markReaded: markReaded,
    markUnReaded: markUnReaded,
    logicDelEmail: logicDelEmail,
    moveEmailToFolder: moveEmailToFolder,
    popSelectFolderDialog: popSelectFolderDialog
  };
});
