define(['constant', 'commons', 'server', 'appContext', 'appModal', 'ListViewWidgetDevelopment', 'js-base64'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  ListViewWidgetDevelopment,
  base64
) {
  var StringUtils = commons.StringUtils;
  var JDS = server.JDS;

  var AppOrgUserListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppOrgUserListDevelopment, ListViewWidgetDevelopment, {
    afterRender: function (options, configuration) {
      var $element = $(this.widget.element);
      var forbiddenHtml =
        '<div class="workbench-route"><i class="iconfont icon-ptkj-tishishuoming"></i>' +
        '<div class="workbench-route-tips">管理员可禁用账号，禁用后的限制： <br>• 账号无法登录系统 <br>  • 账号不再产生任何用户数据</div>' +
        '</div>';

      var forzonHtml =
        '<div class="workbench-route"><i class="iconfont icon-ptkj-tishishuoming"></i>' +
        '<div class="workbench-route-tips">管理员可冻结账号，冻结后的限制：<br>• 账号无法登录系统</div>' +
        '</div>';

      var lockHtml =
        '<div class="workbench-route"><i class="iconfont icon-ptkj-tishishuoming"></i>' +
        '<div class="workbench-route-tips">用户密码输入错误时，会触发系统锁定账号，到达锁定时间，将自动解锁<br>锁定后的限制：<br>• 账号无法登录系统</div>' +
        '</div>';

      $("th[data-field='isForbidden']", $element)
        .find('.th-inner')
        .html('是否禁用' + forbiddenHtml);

      $("th[data-field='isLocked']", $element)
        .find('.th-inner')
        .html('是否冻结' + forzonHtml);

      $("th[data-field='pwdErrorLock']", $element)
        .find('.th-inner')
        .html('是否锁定' + lockHtml);
      $('th,.th-inner', $element).css({
        overflow: 'visible'
      });

      $element.find('.fixed-table-body table').css({
        'table-layout': 'initial'
      });
    },
    lineEnderButtonHtmlFormat: function (format, row, index) {
      var $html = $(format.before);
      $html.find(row.isForbidden === 1 ? '.btn_class_btn_forbid' : '.btn_class_btn_unforbid').remove();
      $html.find(row.isLocked === 1 ? '.btn_class_btn_forzon' : '.btn_class_btn_unforzon').remove();
      $html.find(row.pwdErrorLock === 1 ? '' : '.btn_class_btn_unlock').remove();
      format.after = $html[0].outerHTML;
    },
    getTableOptions: function (bootstrapTableOptions) {
      this.addOtherConditions([
        {
          columnIndex: 'systemUnitId',
          value: SpringSecurityUtils.getCurrentUserUnitId(),
          type: 'eq'
        }
      ]);
      this.getPwdRules();
    },
    btn_forbid: function (e) {
      var index = $(e.target).parents('tr').data('index');
      if (index === undefined) {
        index = this.getSelectionIndexes()[0];
      }
      this.updateStatus('forbid', index, '禁用');
    },
    btn_unforbid: function (e) {
      var index = $(e.target).parents('tr').data('index');
      this.updateStatus('unforbid', index, '启用');
    },
    btn_unforzon: function (e) {
      var index = $(e.target).parents('tr').data('index');
      this.updateStatus('unlock', index, '解冻');
    },
    btn_forzon: function (e) {
      var index = $(e.target).parents('tr').data('index');
      this.updateStatus('lock', index, '冻结');
    },
    btn_unlock: function (e) {
      var index = $(e.target).parents('tr').data('index');
      this.updateStatus('pwdUnlock', index, '解锁');
    },
    btn_imp: function () {
      var self = this;
      var message = this.getHtml();
      var $impDialog = appModal.dialog({
        title: '导入用户',
        message: message,
        size: 'middle',
        width: 750,
        shown: function () {
          $('#import_dialog').attr('isUpload', false);
          $('#importOrgVerName').wSelect2({
            serviceName: 'multiOrgVersionFacade',
            queryMethod: 'loadSelectData',
            labelField: 'importOrgVerName',
            valueField: 'importOrgVerId',
            placeholder: '请选择',
            params: {
              unitId: SpringSecurityUtils.getCurrentUserUnitId(),
              status: '1'
            },
            multiple: false,
            remoteSearch: false,
            width: '300',
            height: 250
          });
          if (self.rules.adminSetPwdType == 'ASPT02') {
            $('.setPwdBody').show();
            self.checkPwd('userDefinedPwd', 'confirmUserDefinedPwd', $impDialog);
          }
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary',
            callback: function (event) {
              var verId = $('#importOrgVerId').val();
              if (StringUtils.isBlank(verId)) {
                appModal.error('请选择要导入的组织版本');
                return false;
              }
              var isUpload = $('#import_dialog').attr('isUpload');
              if (isUpload == 'true') {
                appModal.error('正在上传导入中，请不要重复操作');
                return false;
              }

              if (self.rules.adminSetPwdType == 'ASPT02') {
                var userDefinedPwd = $('#userDefinedPwd', $impDialog).val();
                var confirmUserDefinedPwd = $('#confirmUserDefinedPwd', $impDialog).val();
                var isSave = true;
                var len = $('#import_dialog', $impDialog).find('.error:visible').length;
                if (userDefinedPwd == '') {
                  $('#userDefinedPwd', $impDialog).next('.error').html(self.placeholder).show();
                  isSave = false;
                }
                if (confirmUserDefinedPwd == '') {
                  $('#confirmUserDefinedPwd', $impDialog).next('.error').html('请再次输入登录密码').show();
                  isSave = false;
                }
                if (userDefinedPwd != confirmUserDefinedPwd) {
                  $('#confirmUserDefinedPwd', $impDialog).next('.error').html('两次密码输入不一致，请再次输入登录密码').show();
                  isSave = false;
                }
                if (!isSave || len > 0) {
                  return false;
                }
              }

              var file = $('#uploadfile').val();
              if (file == '') {
                alert('请选择xls文件');
                return false;
              }
              if (file.indexOf('.') < 0) {
                return false;
              }
              var fileType = file.substring(file.lastIndexOf('.'), file.length);
              if (fileType == '.xls' || fileType == '.xlsx') {
                // 暂时屏蔽所有按钮，防止重复点击
                $('#import_dialog').attr('isUpload', true);
                appModal.showMask('正在上传导入中');

                if (self.rules.adminSetPwdType == 'ASPT02') {
                  // 自定义密码
                  var pwd = $('#userDefinedPwd', $impDialog).val();
                  var url = getBackendUrl() + '/multi/org/user/import_user_defined_pwd?verId=' + verId;
                  url += '&userDefinedPwd=' + base64.encode(urlencode(pwd));
                } else {
                  var url = getBackendUrl() + '/multi/org/user/import?verId=' + verId;
                }
                var _auth = getCookie('_auth');
                if (_auth) {
                  url += '&' + _auth + '=' + getCookie(_auth);
                }

                $.ajaxFileUpload({
                  url: url, // 链接到服务器的地址
                  secureuri: false,
                  method: 'post',
                  fileElementId: 'uploadfile', // 文件选择框的ID属性
                  dataType: 'text', // 服务器返回的数据格式
                  success: function (data, status) {
                    appModal.hideMask();

                    $('#import_dialog').attr('isUpload', false);
                    // QQ浏览器会多返回莫名奇妙的额外数据，所以套个text()方法来过滤脏数据
                    var data = data == '' ? {} : eval('(' + data + ')');

                    if (data.success) {
                      var message = '导入用户成功！';
                      if (self.rules.adminSetPwdType != 'ASPT02' && data.message != '') {
                        message += '<br>已为您导出新增用户的初始登录密码信息，请注意查收';
                        window.open(ctx + '/repository/file/mongo/download?fileID=' + data.message);
                      }

                      appModal.dialog({
                        message: message,
                        title: '导入用户',
                        // className:"alingCenter"
                        buttons: {
                          ok: {
                            label: '确定',
                            className: 'well-btn w-btn-primary'
                          }
                        }
                      });
                      // 刷新页面
                      self.refresh();
                    } else {
                      appModal.alert(data.message);
                      return false;
                    }
                    $('#import_dialog').dialog('close');
                  },
                  error: function (data) {
                    appModal.hideMask();
                    $('#import_dialog').attr('isUpload', false);
                  }
                });
              } else {
                appModal.error('请选择xls文件');
                return false;
              }
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default',
            callback: function () {
              var isUpload = $('#import_dialog').attr('isUpload');
              if (isUpload == 'true') {
                appModal.info('正在上传导入中，暂时不能取消');
                return false;
              } else {
                $('#import_dialog').attr('isUpload', false);
              }
            }
          }
        }
      });
    },
    btn_exp: function () {
      appModal.confirm('确定要导出全部用户吗？', function (confirmed) {
        if (!confirmed) {
          return;
        }
        var url = getBackendUrl() + '/multi/org/user/export';
        var _auth = getCookie('_auth');
        if (_auth) {
          url += '?' + _auth + '=' + getCookie(_auth);
        }
        openWindowByPost(url, null, '_blank');
      });
    },
    btn_del_user: function () {
      var self = this;
      var selectionDatas = this.getSelections();
      if (selectionDatas.length > 0) {
        var rowId = [];
        for (var i = 0; i < selectionDatas.length; i++) {
          rowId.push(selectionDatas[i].id);
        }
        appModal.confirm('确认删除该用户？', function (confirmed) {
          if (!confirmed) {
            return;
          }

          $.ajax({
            url: ctx + '/api/org/multi/deleteUsers',
            type: 'POST',
            data: {
              userIds: rowId
            },
            dataType: 'json',
            success: function (result) {
              if (result.code === 0) {
                appModal.success('删除成功!', function () {
                  self.refresh();
                });
              } else {
                appModal.error(result.msg);
              }
            }
          });
        });
      } else {
        appModal.error('请选择记录！');
      }
    },
    btn_clear_user: function () {
      var self = this;
      appModal.confirm('确定要删除全部用户吗？<br>请谨慎操作，请在删除前备份所有“MULTI_ORG_” 开头的表！', function (confirmed) {
        if (!confirmed) {
          return;
        }
        $.ajax({
          url: ctx + '/api/org/multi/clearAllUserOfUnit',
          type: 'POST',
          data: {},
          dataType: 'json',
          success: function (result) {
            if (result.code === 0) {
              appModal.success('删除成功!', function () {
                self.refresh();
              });
            } else {
              appModal.error(result.msg);
            }
          }
        });
      });
    },
    btn_reset_pwd: function () {
      var self = this;
      var uuids = this.getSelectionUuids();
      if (uuids.length > 0) {
        if (self.rules.adminSetPwdType == 'ASPT02') {
          // 自定义重置密码
          var url = '/api/org/user/account/resetUserDefinedPwd';
          self.getResetPwdDialog(url, uuids);
        } else {
          // 随机重置密码
          appModal.confirm('确定要重置用户的登录密码吗？<br>重置后，已锁定的账号将会自动解锁！', function (confirmed) {
            if (!confirmed) {
              return;
            }
            $.ajax({
              url: ctx + '/api/org/user/account/resetUserPwd',
              type: 'POST',
              data: {
                userUuids: uuids
              },
              dataType: 'json',
              success: function (result) {
                if (result.code == 0) {
                  var $pwdDialog = top.appModal.dialog({
                    message: '重置成功！重置后，用户的登录密码为 <span id="initialPwd"></span><br>关闭弹窗后，将无法查看密码！',
                    title: '重置密码',
                    shown: function () {
                      $('#initialPwd', $pwdDialog).text(result.data);
                    },
                    buttons: {
                      copy: {
                        label: '复制密码',
                        className: 'well-btn w-btn-primary',
                        callback: function () {
                          $('.bootbox-body', $pwdDialog).append("<textarea id='copyPwdContent' style='opacity: 0;'>");
                          var copyContent = $('#initialPwd', $pwdDialog).text();
                          var inputElement = document.getElementById('copyPwdContent');
                          inputElement.value = copyContent;
                          inputElement.select();
                          document.execCommand('Copy');
                          appModal.success('复制成功！');
                          $('#copyPwdContent').remove();
                        }
                      },
                      cancel: {
                        label: '关闭',
                        className: 'btn-default'
                      }
                    }
                  });
                } else {
                  appModal.error(result.msg);
                }
              }
            });
          });
        }
      } else {
        appModal.error('请选择记录！');
      }
    },
    btn_reset_all_pwd: function () {
      var self = this;
      if (self.rules.adminSetPwdType == 'ASPT02') {
        // 自定义重置全部用户密码
        var url = '/api/org/user/account/resetAllUserDefinedPwd';
        self.getResetPwdDialog(url);
      } else {
        // 随机重置全部用户密码
        appModal.confirm('确定要重置全部用户的登录密码吗？<br>重置后，已锁定的账号将会自动解锁！', function (confirmed) {
          if (!confirmed) {
            return;
          }

          $.ajax({
            url: ctx + '/api/org/user/account/resetAllUserPwd',
            type: 'POST',
            dataType: 'json',
            data: { well: '1' },
            success: function (result) {
              if (result.code == 0) {
                window.open('/repository/file/mongo/download?fileID=' + result.data);
                var $pwdDialog = top.appModal.dialog({
                  message: '重置成功！<br>已为您导出重置后用户的登录密码信息，请注意查看！',
                  title: '重置全部用户密码',
                  className: 'textAlgn',
                  buttons: {
                    ok: {
                      label: '确定',
                      className: 'well-btn w-btn-primary'
                    }
                  }
                });
              } else {
                appModal.error(result.msg);
              }
            }
          });
        });
      }
    },
    updateStatus: function (type, index, msg) {
      var self = this;
      var uuids = this.getData()[index].uuid;
      var method = type + 'Account';
      appModal.confirm('确定要' + msg + '吗?', function (confirmed) {
        if (!confirmed) {
          return;
        }
        $.ajax({
          url: ctx + '/api/org/user/account/' + method,
          type: 'POST',
          data: {
            uuid: uuids
          },
          dataType: 'json',
          success: function (result) {
            if (result.code == 0) {
              appModal.success(msg + '成功！', function () {
                self.refresh();
              });
            } else {
              appModal.success(msg + '失败！');
            }
          }
        });
      });
    },

    getResetPwdDialog: function (url, uuids) {
      var self = this;
      var html = self.getResetPwdHtml();
      var $dialog = appModal.dialog({
        message: html,
        title: '重置密码',
        width: 540,
        shown: function () {
          self.checkPwd('newPwd', 'confirmPwd', $dialog);
        },
        buttons: {
          ok: {
            label: '确定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var isSave = true;
              var len = $('#userResetPwdForm', $dialog).find('.error:visible').length;
              var newPwd = $('#newPwd', $dialog).val();
              var confirmPwd = $('#confirmPwd', $dialog).val();
              if (newPwd == '') {
                $('#newPwd', $dialog).next('.error').html(self.placeholder).show();
                isSave = false;
              }
              if (confirmPwd == '') {
                $('#confirmPwd', $dialog).next('.error').html('请再次输入登录密码').show();
                isSave = false;
              }
              if (newPwd != confirmPwd) {
                $('#confirmPwd', $dialog).next('.error').html('两次密码输入不一致，请再次输入登录密码').show();
                isSave = false;
              }
              if (!isSave || len > 0) {
                return false;
              }
              var data = {};
              if (uuids != undefined) {
                data.userDefinedPwd = base64.encode(urlencode(newPwd));
                data.userUuids = uuids;
              } else {
                data.userDefinedPwd = base64.encode(urlencode(newPwd));
              }
              $.ajax({
                type: 'POST',
                url: ctx + url,
                dataType: 'json',
                data: data,
                success: function (result) {
                  if (result.code == 0) {
                    appModal.success({
                      message: '重置密码成功！'
                    });
                  } else {
                    appModal.error('重置密码失败！');
                    return false;
                  }
                }
              });
            }
          },
          cancel: {
            label: '取消',
            className: 'btn-default'
          }
        }
      });
    },
    getHtml: function () {
      var html = '';
      html +=
        "<div id='import_dialog' class='form-widget'>" +
        "<div class='well-form form-horizontal'>" +
        "<div class='form-group'>" +
        "<label for='loginName' class='well-form-label control-label'><font style='color:red;'>*</font>选择组织版本</label>" +
        "<div class='well-form-control'>" +
        "<input type='text' class='form-control' id='importOrgVerName' name='importOrgVerName'>" +
        "<input type='hidden' class='form-control' id='importOrgVerId' name='importOrgVerId'>" +
        '</div>' +
        '</div>' +
        '<div class="setPwdBody" style="display:none;">' +
        "<div class='form-group'>" +
        "<label for='loginName' class='well-form-label control-label'><font style='color:red;'>*</font>设置登录密码</label>" +
        "<div class='well-form-control'>" +
        "<input type='password' class='form-control' id='userDefinedPwd' name='userDefinedPwd' placeholder='" +
        this.placeholder +
        "'>" +
        '<label class="error iconfont icon-ptkj-wentiziduantishi" style="text-align: left;display:none;"></label>' +
        '</div>' +
        '</div>' +
        "<div class='form-group'>" +
        "<label for='loginName' class='well-form-label control-label'><font style='color:red;'>*</font>确认密码</label>" +
        "<div class='well-form-control'>" +
        "<input type='password' class='form-control' id='confirmUserDefinedPwd' name='confirmUserDefinedPwd' placeholder='请再次输入登录密码'>" +
        '<label class="error iconfont icon-ptkj-wentiziduantishi" style="text-align: left;display:none;"></label>' +
        '</div>' +
        '</div>' +
        '</div>' +
        "<div class='form-group'>" +
        "<label for='loginName' class='well-form-label control-label'><font style='color:red;'>*</font>选择XLS文件</label>" +
        "<div class='well-form-control'>" +
        "<input type='file' class='form-control' id='uploadfile' name='upload'>" +
        "<div class='content' style='margin-top: 15px;'><a href='" +
        ctx +
        "/static/resfacade/share/用户数据导入模板_6.0.xls'>用户数据导入模板</a></div>" +
        '</div>' +
        '</div>' +
        '</div>' +
        '</div>';
      return html;
    },
    getResetPwdHtml: function () {
      var html = '';
      html +=
        '<form class="form-vertical " role="form" id="userResetPwdForm" style="padding-top:15px;">' +
        '<div class="form-group">' +
        '<label class="col-sm-3 control-label"><font style="color:red;margin-right:3px;">*</font>请设置重置后，用户的登录密码：</label>' +
        '<div class="col-sm-8">' +
        '<input type="password" class="form-control" name="newPwd" id="newPwd" placeholder="' +
        this.placeholder +
        '">' +
        '<label class="error iconfont icon-ptkj-wentiziduantishi" style="text-align: left;"></label>' +
        '</div>' +
        '</div>' +
        '<div class="form-group">' +
        '<label class="col-sm-3 control-label"><font style="color:red;margin-right:3px;">*</font>确认密码：</label>' +
        '<div class="col-sm-8">' +
        '<input type="password" class="form-control" name="confirmPwd" id="confirmPwd" placeholder="请再次输入登录密码">' +
        '<label class="error iconfont icon-ptkj-wentiziduantishi" style="text-align: left;"></label>' +
        '</div>' +
        '</div>' +
        '<p class="reset-pwd-tips">重置后，已锁定的账号将会自动解锁！</p>' +
        '</form>';
      return html;
    },
    getPwdRules: function () {
      var self = this;
      $.ajax({
        type: 'GET',
        url: ctx + '/api/pwd/setting/getMultiOrgPwdSetting',
        dataType: 'json',
        success: function (result) {
          self.rules = result.data;
          var latter = self.rules.letterAsk == 'LA02' ? '至少包含2种' : self.rules.letterAsk == 'LA01' ? '至少包含1种' : '包含3种';
          var minLength = self.rules.minLength || 4;
          var maxLength = self.rules.maxLength || 20;
          var letterLimited = self.rules.letterLimited == 'LL01' ? '(必须要有大写、小写)' : '';
          self.placeholder = '字母' + letterLimited + '、数字、特殊字符中' + latter + '，' + minLength + '~' + maxLength + '位';
        }
      });
    },
    checkPwd: function (newPwd, comfirmPwd, $dialog) {
      var self = this;
      $('#' + newPwd, $dialog)
        .off('blur')
        .on('blur', function () {
          var val = $(this).val();
          if (val == '') {
            $(this).next('.error').html('登录密码不能为空').show();
          } else {
            var latter = self.rules.letterAsk;
            var minLength = self.rules.minLength || 4;
            var maxLength = self.rules.maxLength || 20;
            var isHide = true;
            var latterRegLower = /[a-z]+/;
            var latterRegupper = /[A-Z]+/;
            var latters = /[a-zA-Z]+/;
            var numReg = /[0-9]+/;
            var others = /[`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]+/im;

            if (minLength > val.length || maxLength < val.length) {
              isHide = false;
            } else if (/[\u4E00-\u9FA5]+/.test(val)) {
              isHide = false;
            } else {
              var i = 0;
              if (latterRegLower.test(val) || latterRegupper.test(val)) {
                i++;
              }

              if (numReg.test(val)) {
                i++;
              }
              if (others.test(val)) {
                i++;
              }
              if (i - (latter == 'LA02' ? '2' : latter == 'LA01' ? '1' : '3') < 0) {
                isHide = false;
              } else if (
                self.rules.letterLimited == 'LL01' &&
                latters.test(val) &&
                (!latterRegLower.test(val) || !latterRegupper.test(val))
              ) {
                isHide = false;
              }
            }
            if (!isHide) {
              $(this)
                .next('.error')
                .html('不符合密码格式：' + self.placeholder)
                .show();
              return false;
            } else {
              $(this).next('.error').html('').hide();
            }
          }
        });

      $('#' + comfirmPwd, $dialog)
        .off('blur')
        .on('blur', function () {
          if ($(this).val() == '') {
            $(this).next('.error').html('请再次输入登录密码').show();
          } else if ($(this).val() != $('#' + newPwd, $dialog).val()) {
            $(this).next('.error').html('两次密码输入不一致，请再次输入登录密码').show();
          } else {
            $(this).next('.error').html('').hide();
          }
        });
    }
  });
  return AppOrgUserListDevelopment;
});
