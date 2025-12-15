define(['jquery', 'commons', 'constant', 'server', 'ViewDevelopmentBase', 'appModal', 'lodash'], function (
  $,
  commons,
  constant,
  server,
  ViewDevelopmentBase,
  appModal,
  _
) {
  var AppEmailFolderDevelopment = function () {
    ViewDevelopmentBase.apply(this, arguments);
  };

  commons.inherit(AppEmailFolderDevelopment, ViewDevelopmentBase, {
    init: function () {
      var _self = this;
    },

    getUserId: function () {
      if (this.userId) {
        return this.userId;
      }
      this.userId = server.SpringSecurityUtils.getCurrentUserId();
      return this.userId;
    },

    beforeRender: function (options, configuration) {
      // 归属用户ID
      this.widget.addParam('userId', this.getUserId());
    },

    afterRender: function (options, configuration) {},

    onLoadSuccess: function () {
      this.showUseCapacityInfo();
    },

    showUseCapacityInfo: function () {
      var _self = this;
      server.JDS.call({
        service: 'wmMailUserService.getInnerMailUser',
        data: [_self.getUserId()],
        version: '',
        success: function (result) {
          if (result.data && result.data.limitCapacity != null) {
            _self.widget.element.find('.mail_capacity_used').remove();
            var total = result.data.limitCapacity * 1024 * 1024; //转为byte
            var used = result.data.usedCapacity;
            var remain = total - used;
            remain = remain < 0 ? 0 : remain;
            var totalReadable = _self.capacityNumberFormate(total);
            var usedReadable = _self.capacityNumberFormate(used);
            var remainReadable = _self.capacityNumberFormate(remain);
            /*if (usedReadable !== '0B' && totalReadable === remainReadable) {//总值与剩余值无差别的情况下，取值x.99，表示有使用
                            remainReadable = (parseFloat(remainReadable) - 0.01) + (remainReadable.substr(remainReadable.length - 1, 1));
                        }*/
            var $div = $('<div>', {
              class: 'bs-bars pull-right mail_capacity_used'
            }).html(
              '总空间 ' + totalReadable + '：已用 ' + usedReadable + ' ，剩余 <span style="color: #009933;">' + remainReadable + '</span>'
            );
            var $toobar = _self.widget.element.find('.fixed-table-toolbar');
            if ($toobar.find('.search').length != 0) {
              $div.insertAfter($toobar);
            } else {
              $toobar.append($div);
            }
          }
        },
        error: function (jqXHR) {},
        async: true
      });
    },

    capacityNumberFormate: function (num, mode) {
      if (num < 1024) {
        return num + 'B';
      }
      var formateFixed = function (n, pow) {
        var base = Math.pow(1024, pow);
        var leftRange = 0.995 * base;
        var rightRange = 0.99999999 * base;
        var d = n / base;
        var m = n % base;
        if (m >= leftRange && m <= rightRange) {
          //出现x.995~x.999的情况下四舍五入会归整，避免归整。
          return parseInt(d) + '.99';
        }
        return d.toFixed(2);
      };
      if (num >= 1024 && num < 1024 * 1024) {
        return formateFixed(num, 1) + 'K';
      }
      if (num >= 1024 * 1024 && num < 1024 * 1024 * 1024) {
        return formateFixed(num, 2) + 'M';
      }
      if (num >= 1024 * 1024 * 1024) {
        return formateFixed(num, 3) + 'G';
      }
    },

    //格式化行级按钮
    lineEnderButtonHtmlFormat: function (format, row, index) {
      var $html = $(format.before);
      $html.find(row.type_code != 5 ? '.btn_class_renameFolder,.btn_class_deleteFolder' : '').remove();
      format.after = $html[0].outerHTML;
    },

    /**
     * 新建文件夹
     */
    addFolder: function () {
      var _self = this;
      _self.popDialog();
    },

    /**
     * 弹窗展示
     */
    popDialog: function (folder, dialogOpt, successCallback) {
      var _self = this;
      var options = {
        title: dialogOpt && dialogOpt.title ? dialogOpt.title : folder ? '改名文件夹' : '新建文件夹',
        message: _self.folderDialogHtml(folder ? folder.folderName : null),
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function (result) {
              var commitResult = false;
              var folderName = $.trim($('#folderName').val());
              if (folderName.length > 20) {
                appModal.alert('文件夹名称不要超过20个中文字符');
                return false;
              }
              //新增时候，文件名必填
              if (!folder && folderName.length == 0) {
                appModal.alert('请输入文件夹名称');
                return false;
              }

              //修改时候，如果文件名没填，则默认使用旧文件名
              if (folder && folderName.length == 0) {
                folderName = folder.folderName;
              }

              server.JDS.call({
                service: 'wmMailFolderFacadeService.' + (folder ? 'updateFolderName' : 'addFolder'),
                data: folder ? [folder.folderUuid, folderName] : [{ folderName: folderName }],
                success: function (result) {
                  commitResult = true;
                  if ($.isFunction(successCallback)) {
                    successCallback(result.data);
                  } else {
                    appModal.success(folder ? '改名成功' : '新建成功');
                    _self.refresh();
                    _self.refreshLeftSideMenus({ expand: true });
                  }
                },
                error: function (jqXHR) {
                  var response = JSON.parse(jqXHR.responseText);
                  _self.showExceptionMsg({
                    msg: response.msg,
                    folderName: folder ? folder.folderName : $.trim($('#folderName').val())
                  });
                },
                async: false
              });
              return commitResult;
            }
          },

          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function (result) {}
          }
        },
        size: 'middle'
      };
      appModal.dialog(options);
    },

    /**
     * 重命名文件夹
     */
    renameFolder: function (event, options, rowData) {
      this.popDialog({ folderName: rowData.name, folderUuid: rowData.uuid });
    },

    /**
     * 刷新左侧导航栏关于标签的导航项
     */
    refreshLeftSideMenus: function () {
      var $tagMenu = $("ul[loadintf='com.wellsoft.pt.webmail.support.WmMailFolderTreeDataProvider']");
      if ($tagMenu) {
        $tagMenu.trigger('reloadMenuItem', arguments);
      }
    },

    /**
     * 删除文件夹
     */
    deleteFolder: function (event, options, rowData) {
      var _self = this;
      var deleteTipHtml = function () {
        var $table = $('<table>', {
          class: 'table js-deleteConfirmTable'
        });
        var $labelTr = $('<tr>').append($('<td>').html('是否删除文件夹[' + rowData.name + ']'));
        var $labelTr2 = $('<tr>').append($('<td>', { class: 'js-deleteConfirmTip' }).html('您的邮件将移动到收件箱'));
        var $inputTr = $('<tr>').append(
          $('<td>').append(
            $('<input>', { type: 'checkbox', id: 'deleteCheckbox' }),
            $('<label>', { for: 'deleteCheckbox' }).text('同时删除邮件')
          )
        );
        $table.append($labelTr, $labelTr2, $inputTr);
        return $table[0].outerHTML;
      };
      appModal.confirm(deleteTipHtml(), function (result) {
        if (result) {
          server.JDS.call({
            service: 'wmMailFolderFacadeService.deleteFolder',
            data: [rowData.uuid, $('#deleteCheckbox').prop('checked')],
            success: function (result) {
              appModal.success('删除成功');
              _self.refresh();
              _self.refreshLeftSideMenus({ expand: true });
            },
            error: function (jqXHR) {}
          });
        }
      });

      $('#deleteCheckbox').on('click', function () {
        $('.js-deleteConfirmTip').text($(this).prop('checked') ? '您的邮件将移动到回收站' : '您的邮件将移动到收件箱');
      });
    },

    folderDialogHtml: function (folderName) {
      var $table = $('<table>', {
        class: 'table table-hover table-striped'
      });
      var $labelTr = $('<tr>').append($('<td>').html('请输入文件夹名称'));
      var $inputTr = $('<tr>').append(
        $('<td>').append(
          $('<input>', {
            type: 'text',
            id: 'folderName',
            maxlength: 64,
            style: 'width:100%;',
            placeholder: folderName ? folderName : ''
          })
        )
      );
      $table.append($labelTr, $inputTr);
      return $table[0].outerHTML;
    },

    showExceptionMsg: function (args) {
      if (args.msg.indexOf('SYS_UNQ00167755') != -1) {
        //文件夹名称唯一性（用户数据内）
        args.msg = '当前用户已经存在同名文件夹[' + args.folderName + ']';
      }
      appModal.error(args.msg);
    }
  });

  return AppEmailFolderDevelopment;
});
