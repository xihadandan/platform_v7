define(['jquery', 'commons', 'constant', 'server', 'ListViewWidgetDevelopment', 'appModal'], function (
  $,
  commons,
  constant,
  server,
  ListViewWidgetDevelopment,
  appModal
) {
  var AppEmailAccountListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };

  commons.inherit(AppEmailAccountListDevelopment, ListViewWidgetDevelopment, {
    beforeRender: function (options, configuration) {
      var _self = this;
      this.loadCssLink();
    },

    loadCssLink: function () {
      $('head').append(
        $('<link>', {
          href: ctx + '/resources/pt/css/dyform/explain/dyform.css',
          rel: 'stylesheet'
        })
      );
    },

    afterRender: function (options, configuration) {
      var _self = this;
    },

    addAccount: function (data) {
      var _self = this;
      var $dialog = this.popDialog(
        this.createOtherMailAccountHtml(data),
        data && data.UUID ? '编辑邮箱账号' : '新增其他邮箱账号',
        {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              var commitRet = false;
              var accountData = _self.collectData();
              var validateRst = _self.validateData(accountData);
              if (!validateRst.success) {
                appModal.info(validateRst.msg);
                return false;
              }
              if (data && data.UUID) {
                accountData.uuid = data.UUID;
              }
              appModal.showMask('邮箱账号处理中...', $dialog);
              server.JDS.call({
                service: 'wmMailUserService.saveMailUser',
                data: [accountData],
                version: '',
                success: function (result) {
                  appModal.hideMask($dialog);
                  appModal.info(data && data.UUID ? '编辑成功' : '新增成功');
                  $dialog.find('.bootbox-close-button').trigger('click');
                  _self.refresh();
                  _self.refreshLeftSideMenus({
                    expand: true
                  });
                  if (!accountData.uuid) {
                    _self.syncMail(null, null, [accountData.mailAddress]);
                  }
                },
                error: function (jqXHR) {
                  appModal.hideMask($dialog);
                  var errorMsg = jqXHR.responseJSON.data ? jqXHR.responseJSON.data : '';
                  if (errorMsg.length == 0) {
                    errorMsg =
                      jqXHR.responseJSON.msg.indexOf('SYSUNQ_C00174260') != -1 ? '账号已存在，请勿重复添加' : jqXHR.responseJSON.msg;
                  }
                  appModal.alert({
                    title: data && data.UUID ? '编辑失败' : '新增失败',
                    message: errorMsg,
                    type: 'warning'
                  });
                },
                async: true
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
        function () {
          $('#mailAddress')
            .off('blur')
            .on('blur', function (e) {
              var address = $.trim($(this).val());
              if (address.length > 0) {
                var regExp = /\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*/;
                if (!regExp.test(address)) {
                  /*appModal.info("邮箱账号不正确");
                $(this).val("");*/
                  return;
                }
                var parts = address.split('@');
                server.JDS.call({
                  service: 'wmMailOpenServerFacadeService.getByDomain',
                  data: [parts[1].toLowerCase()],
                  version: '',
                  success: function (result) {
                    if (result.success && result.data) {
                      $('#pop3Server').val(result.data.popServer);
                      $('#pop3Port').val(result.data.popPort);
                      if (result.data.isPopSsl) {
                        $('#isPopSsl').prop('checked', true);
                      }

                      $('#smtpServer').val(result.data.smtpServer);
                      $('#smtpPort').val(result.data.smtpPort);
                      if (result.data.isSmtpSsl) {
                        $('#isSmtpSsl').prop('checked', true);
                      }
                    }
                  },
                  error: function (jqXHR) {},
                  async: true
                });
              }
            });

          $('#mailAddress')
            .off('keyup')
            .on('keyup', function (e) {
              if (
                $(this)
                  .val()
                  .substr($(this).val().length - 1) == '@'
              ) {
                _self.mailAddressDropdownHtml($(this));
              } else {
                $(this).next('div').remove();
              }
            });

          $('#accoutForm').on('click', '.address_li', function () {
            $('#mailAddress').val($(this).text());
            $('#mailAddress').trigger('blur');
            $('#mailAddress').next('div').remove();
          });

          $('#accoutForm').on('hover', '.address_li', function () {
            $('.address_li').css('background-color', 'white');
            $(this).css('background-color', '#efefef');
          });
        }
      );
    },

    syncMail: function (event, opt, mailAddressArr) {
      if (!mailAddressArr) {
        var selections = this.getSelections();
        if (selections.length == 0) {
          appModal.info('请选择需要同步邮件的账号');
          return;
        }
        mailAddressArr = [];
        for (var i = 0, len = selections.length; i < len; i++) {
          mailAddressArr.push(selections[i].MAIL_ADDRESS);
        }
      }

      this.popDialog(
        this.createSyncMailDialogHtml(event == null),
        '同步邮件',
        {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function () {
              for (var j = 0, len = mailAddressArr.length; j < len; j++) {
                $.get(
                  ctx + '/proxy/webmail/syncOtherMailAcountMessages',
                  {
                    type: $('#syncMailForm :checked').val(),
                    mailAddress: mailAddressArr[j]
                  },
                  function (res) {}
                );
              }
              appModal.info('后端服务同步中');

              return true;
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
    },

    createSyncMailDialogHtml: function (isAfterAddAction) {
      var $form = $('<form>', {
        class: 'dyform',
        id: 'syncMailForm'
      });
      if (isAfterAddAction) {
        $form.append($('<p>').append('新增其他邮箱账号成功，是否同步邮件？'));
      }

      $form.append(
        $('<input>', {
          type: 'radio',
          name: 'syncTypeRadio',
          id: 'beforeWeek',
          value: 1
        }),
        $('<label>', {
          for: 'beforeWeek'
        }).text('最近七天'),
        $('<input>', {
          type: 'radio',
          name: 'syncTypeRadio',
          id: 'beforeMonth',
          value: 2
        }),
        $('<label>', {
          for: 'beforeMonth'
        }).text('最近一个月'),
        $('<input>', {
          type: 'radio',
          name: 'syncTypeRadio',
          id: 'before3Month',
          value: 3
        }),
        $('<label>', {
          for: 'before3Month'
        }).text('最近三个月'),
        $('<input>', {
          type: 'radio',
          name: 'syncTypeRadio',
          id: 'beforeHalfYear',
          value: 4
        }),
        $('<label>', {
          for: 'beforeHalfYear'
        }).text('最近半年'),
        $('<input>', {
          type: 'radio',
          name: 'syncTypeRadio',
          id: 'allMail',
          value: -9
        }),
        $('<label>', {
          for: 'allMail'
        }).text('全部邮件')
      );
      return $form[0].outerHTML;
    },

    editAccount: function () {
      var selections = this.getSelections();
      if (selections.length != 1) {
        appModal.info('请选择一行进行编辑');
        return;
      }
      this.addAccount(selections[0]);
    },

    deleteAccount: function () {
      var _self = this;
      var selections = this.getSelections();
      if (selections.length == 0) {
        appModal.info('请选择需要删除的邮箱账号');
        return;
      }
      var uuids = [];
      var mailboxs = [];
      for (var i = 0, len = selections.length; i < len; i++) {
        uuids.push(selections[i].UUID);
        mailboxs.push(selections[i].MAIL_ADDRESS);
      }
      appModal.confirm('确定要删除账号吗？', function (res) {
        if (res) {
          server.JDS.call({
            service: 'wmMailUserService.deleteMailUserByUuids',
            data: [uuids],
            version: '',
            success: function (result) {
              server.JDS.call({
                service: 'wmWebmailService.deleteByMailboxAndUserId',
                data: [mailboxs, server.SpringSecurityUtils.getCurrentUserId()],
                version: '',
                async: true
              });
              appModal.info('删除成功');
              _self.refresh();
              _self.refreshLeftSideMenus({
                expand: true
              });
            }
          });
        }
      });
    },

    collectData: function () {
      var data = {};
      $('#accoutForm :input').each(function (i) {
        if ($(this).attr('type') == 'checkbox') {
          data[$(this).attr('id')] = $(this).prop('checked');
        } else {
          data[$(this).attr('id')] = $(this).val();
        }
      });
      return data;
    },

    validateData: function (data) {
      var rst = {
        success: true,
        msg: ''
      };
      if (!data.mailAddress || $.trim(data.mailAddress).length == 0) {
        rst.success = false;
        rst.msg += '邮箱账号必填';
      }

      if (!data.mailPassword || $.trim(data.mailPassword).length == 0) {
        rst.success = false;
        rst.msg += '邮箱密码必填';
      }

      return rst;
    },

    mailAddressDropdownHtml: function ($target) {
      var _self = this;
      var dropdownAddressSelect = function () {
        var existDiv = $target.next('div').length > 0;
        var $ul;
        if (!existDiv) {
          var $div = $('<div>', {
            style:
              'position: absolute;border: 1px solid #ccc;background-color: white;' +
              'border-radius: 2px;max-height: 200px;max-width: 430px;overflow-y: auto;'
          });
          $ul = $('<ul>', {
            style: 'cursor: pointer;padding: 5px;'
          });
          $div.append($ul);
          $div.insertAfter($target);
        } else {
          $ul = $target.next('div').find('ul');
          $ul.empty();
        }
        var address = $.trim($target.val());
        for (var i = 0; i < _self.allOpenServers.length; i++) {
          $ul.append(
            $('<li>', {
              class: 'address_li',
              style: 'padding:2px;'
            }).text(address + _self.allOpenServers[i])
          );
        }
      };
      if (!this.allOpenServers) {
        server.JDS.call({
          service: 'wmMailOpenServerFacadeService.listAll',
          data: [],
          version: '',
          success: function (result) {
            _self.allOpenServers = [];
            if (result.success && result.data.length > 0) {
              _self.allOpenServers = $.map(result.data, function (r, i) {
                return r.domain;
              });
              dropdownAddressSelect();
            }
          },
          error: function (jqXHR) {},
          async: true
        });
      } else {
        dropdownAddressSelect();
      }
    },

    createOtherMailAccountHtml: function (data) {
      var $table = $('<table>', {
        class: 'table'
      });
      var $actTr = $('<tr>').append(
        $('<td>', {
          class: 'label-td'
        })
          .append('邮箱账号')
          .append(
            $('<font>', {
              color: 'red',
              size: 2
            }).text('*')
          ),
        $('<td>').append(
          $('<input>', {
            type: 'text',
            id: 'mailAddress',
            maxlength: 64,
            class: 'editableClass',
            value: data ? data.MAIL_ADDRESS : ''
          })
        )
      );

      var $pwdTr = $('<tr>').append(
        $('<td>', {
          class: 'label-td'
        })
          .append('邮箱密码')
          .append(
            $('<font>', {
              color: 'red',
              size: 2
            }).text('*')
          ),
        $('<td>').append(
          $('<input>', {
            type: 'password',
            id: 'mailPassword',
            maxlength: 64,
            class: 'editableClass'
          })
        )
      );

      var $actNameTr = $('<tr>').append(
        $('<td>', {
          class: 'label-td'
        }).append('发件昵称'),
        $('<td>').append(
          $('<input>', {
            type: 'text',
            id: 'mailUserName',
            maxlength: 64,
            class: 'editableClass',
            value: data ? data.MAIL_USER_NAME : ''
          })
        )
      );

      var $popTr = $('<tr>').append(
        $('<td>', {
          class: 'label-td'
        }).append('POP服务器'),
        $('<td>').append(
          $('<input>', {
            type: 'text',
            id: 'pop3Server',
            maxlength: 64,
            class: 'editableClass',
            value: data ? data.POP3_SERVER : '',
            style: 'width: 66%; margin-right: 12px;'
          }),
          $('<input>', {
            type: 'text',
            id: 'pop3Port',
            maxlength: 12,
            class: 'editableClass',
            placeholder: '端口',
            value: data ? data.POP3_PORT : '',
            style: 'width: 16%;margin-right:5px;'
          }),
          $('<input>', {
            type: 'checkbox',
            id: 'isPopSsl',
            checked: data.IS_POP_SSL ? true : false
          }),
          $("<label for='isPopSsl' style='padding-left:0px;'>").text('SSL')
        )
      );

      var $smtpTr = $('<tr>').append(
        $('<td>', {
          class: 'label-td'
        }).append('SMTP服务器'),
        $('<td>').append(
          $('<input>', {
            type: 'text',
            id: 'smtpServer',
            maxlength: 64,
            class: 'editableClass',
            value: data ? data.SMTP_SERVER : '',
            style: 'width: 66%; margin-right: 12px;'
          }),
          $('<input>', {
            type: 'text',
            id: 'smtpPort',
            maxlength: 12,
            class: 'editableClass',
            placeholder: '端口',
            value: data ? data.SMTP_PORT : '',
            style: 'width: 16%;margin-right:5px;'
          }),
          $('<input>', {
            type: 'checkbox',
            id: 'isSmtpSsl',
            checked: data.IS_SMTP_SSL ? true : false
          }),
          $('<label>').text('SSL')
        )
      );

      $table.append($actTr, $pwdTr, $actNameTr, $popTr /*,$smtpTr*/);
      return $('<form>', {
        class: 'dyform',
        id: 'accoutForm'
      }).append($table)[0].outerHTML;
    },

    popDialog: function (html, title, buttons, shownCallback, size) {
      return appModal.dialog({
        title: title,
        message: html,
        size: size ? size : 'middle',
        buttons: buttons,
        shown: function () {
          if ($.isFunction(shownCallback)) {
            shownCallback();
          }
        }
      });
    },

    //获取选中的行数据uuid
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

    /**
     * 刷新左侧导航栏关于其他邮箱账号的导航项
     */
    refreshLeftSideMenus: function () {
      var $tagMenu = $("ul[loadintf='com.wellsoft.pt.webmail.support.WmMailOuterMailAccountTreeDataProvider']");
      if ($tagMenu) {
        $tagMenu.trigger('reloadMenuItem', arguments);
      }
    }
  });

  return AppEmailAccountListDevelopment;
});
