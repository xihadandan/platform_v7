(function ($) {
  /**
   * 检查文字长度是否溢出，如果是则将溢出部分省略
   *
   *   注意：
   *   文字本身容器需设置line-height
   *   父物体容器需设置height和overflow-wrap: break-word（目前兼容性比较好的溢出换行样式）
   */
  $.fn.checkOverflow = function (parentClass) {
    $(this).each(function (id, el) {
      var $this = $(el);
      var $parent = $this.closest(parentClass);
      if ($this.height() > $parent.height()) {
        var parentHeight = $parent[0].offsetHeight;
        var thisText = $this.text();
        for (var i = 0; i <= thisText.length; i++) {
          $this.text(thisText.slice(0, i));
          if (parentHeight < $this[0].scrollHeight) {
            var str_all_cn = true;
            // 判断省略号取代的三个字符是否全为中文字符
            thisText
              .slice(i - 2)
              .split('')
              .forEach(function (c, id) {
                if (thisText.slice(i - 2).charCodeAt(id) <= 255) {
                  str_all_cn = false;
                }
              });
            if (str_all_cn) {
              $this.text(thisText.slice(0, i - 2) + '...');
            } else {
              $this.text(thisText.slice(0, i - 3) + '...');
            }
            break;
          }
        }
      }
    });
  };
})(jQuery);

(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'AppOutboxMsgViewDevelopment', 'AppInboxMsgViewDevelopment', 'DefaultMsgEvent', 'UserMsgEvent'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($, AppOutboxMsgViewDevelopment, AppInboxMsgViewDevelopment, DefaultMsgEvent, UserMsgEvent) {
  var HeaderDefaultEvent = {
    myWorkBench: function () {
      var _self = this;
      if (!this.hasGetData) {
        $("li[menuid='workbench']").find('.wHeaderNav-workbench').remove();
        $("li[menuid='workbench']").append("<ul class='dropdown-ul wHeaderNav-workbench'></ul>");
        this.hasGetData = true;
        JDS.call({
          service: 'appPageDefinitionMgr.listFacade',
          version: '',
          success: function (result) {
            var workbenchList = result.data;
            var sb = '';
            $.each(workbenchList, function (i, menu) {
              if (menu.wtype != 'wMobilePage') {
                var title = menu.name.length > 4 ? menu.name : '';
                var icon = menu.userDef ? 'iconfont icon-ptkj-dagou' : '';
                sb +=
                  '<li class="nav-menu-item" menuId="' +
                  menu.id +
                  '" data-url="' +
                  menu.url +
                  '" data-uuid="' +
                  menu.uuid +
                  '"><i class="' +
                  icon +
                  '"></i><span class="nav-text" title="' +
                  title +
                  '">' +
                  menu.name +
                  '</span></li>';
              }
            });
            var btnLi =
              '<li class="nav-scroll-btn-li">' +
              '<button class="move-up disabled"><span class="icon iconfont icon-ptkj-shixinjiantou-shang"></span></button>' +
              '<button class="move-down"><span class="icon iconfont icon-ptkj-shixinjiantou-xia"></span></button>' +
              '</li>';
            sb += btnLi;

            $("li[menuid='workbench']").addClass('dropdown-wrap').find('.wHeaderNav-workbench').append(sb);
            if ($('.wHeaderNav-workbench').find('.icon-ptkj-dagou').size() == 0) {
              $('.wHeaderNav-workbench li:eq(0)').find('i').addClass('iconfont icon-ptkj-dagou');
            }
          }
        });
      }
    },

    personInfoEvent: function () {
      var $personInfoList = $('<ul class="person-info-list">');
      var personInfoList = [
        {
          text: '用户资料',
          icon: 'iconfont icon-ptkj-yonghuziliao',
          type: 'info'
        },
        {
          text: '修改密码',
          icon: 'iconfont icon-ptkj-xiugaimima',
          type: 'changePassword'
        },
        {
          text: '主题设置',
          icon: 'iconfont icon-ptkj-zhutipifu',
          type: 'changeTheme'
        }
      ];
      // 系统参数是否启用账号绑定
      var isBindAccount = SystemParams.getValue('system.account.relation.enable');
      if (isBindAccount == 'enable') {
        personInfoList.push({
          text: '账号绑定',
          icon: 'iconfont icon-oa-banliguocheng',
          type: 'bindAccount'
        });
      }
      $.each(personInfoList, function (i, v) {
        $personInfoList.append('<li data-type="' + v.type + '"><i class="' + v.icon + '"></i>' + v.text + '</li>');
      });

      $("li[menuid='personInfo']").append($personInfoList);
    },

    switchAccountEvent: function ($switchAccountLi) {
      var _self = this;
      // 系统参数是否启用账号绑定
      var isBindAccount = SystemParams.getValue('system.account.relation.enable');
      if (isBindAccount != 'enable') {
        $switchAccountLi.hide();
        return;
      }
      $switchAccountLi.children('a').find('.all-accout-offline-msg-count').remove();
      $switchAccountLi.children('.switchAccountWrap').remove();
      var $switchAccountWrap = $("<div class='switchAccountWrap'></div>");
      $switchAccountLi.children('a').append('<sup class="badge all-accout-offline-msg-count" style="display:none;"></sup>');
      $switchAccountLi.append($switchAccountWrap);
      var $accountInfoList = $('<ul class="account-info-list">');
      var tpl =
        '<div class="clearfix" style="padding: 10px 20px;position: relative;"><div class="pull-left" style="width: 50px;"><i class="ui-wIcon iconfont icon-ptkj-morentouxiang" style="font-size:32px;display: none;"></i>' +
        '  <img src="/proxy/org/user/view/photo/user/${account.userId}" class="nav-avatar" onerror="javascript:this.style.display=&quot;none&quot;;this.previousElementSibling.style.display=&quot;inline-block&quot;;">' +
        '  </div>' +
        '  <div class="account-info">' +
        '    <div>${account.accountName}</div>' +
        '    <div>${account.systemUnitName}</div>' +
        '     <i class="iconfont icon-ptkj-weizhi current-user-icon" style="display:inline-block;position:absolute;top:20px;right:10px;"></i>' +
        '     <sup class="badge accout-offline-msg-count" style="display:none;position:absolute;top:20px;right:10px;background:#e33033;"></sup>' +
        '  </div>' +
        '</div>';
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var currentUserId = getCookie('cookie.current.userId');
      _self._listBindAccount(function (dataList) {
        if (dataList && dataList.length > 0) {
          $.each(dataList, function (i, v) {
            var $li = $('<li style="width:200px;cursor:pointer;"><i class=""></i></li>');
            $li.data('account', v);
            $li.html(templateEngine.render(tpl, { account: v }));
            if (v.userId == currentUserId) {
              $li.find('.accout-offline-msg-count').remove();
            } else {
              $li.find('.current-user-icon').remove();
            }
            $accountInfoList.append($li);
          });
          $switchAccountWrap.append($accountInfoList);
          $switchAccountLi.show();
        } else {
          $switchAccountLi.hide();
        }
        // 页面加载获取数量
        _self._loadCountOffLine(updateCountOffLine);
      });

      var updateCountOffLine = function (countMap) {
        if (countMap) {
          var total = 0;
          $switchAccountLi.find('ul li').each(function (i) {
            var account = $(this).data('account');
            var msgCount = countMap[account.userId];
            if (msgCount != null && msgCount > 0) {
              total += msgCount;
              $(this).find('.accout-offline-msg-count').show().text(msgCount);
            } else {
              $(this).find('.accout-offline-msg-count').hide().text('0');
            }
          });
          if (total) {
            $switchAccountLi.find('.all-accout-offline-msg-count').show().text(total);
          } else {
            $switchAccountLi.find('.all-accout-offline-msg-count').hide().text('');
          }
        }
      };

      // 收到离线消息数量
      $(document).on('countOffLine', function (e, countMap) {
        updateCountOffLine(countMap);
      });
    },
    _loadCountOffLine: function (callback) {
      $.ajax({
        url: ctx + '/proxy/message/inbox/countOffLine/' + getCookie('cookie.current.userId'),
        type: 'get',
        success: function (result) {
          if (result.code == 0) {
            callback(result.data);
          } else {
            callback({});
          }
        }
      });
    },
    defaultEvent: function () {
      var _self = this;
      $(_self.element)
        .off('click')
        .on('click', 'li.nav-menu-item', function () {
          var $this = $(this);
          var menuId = $this.attr('menuId');
          if ($.inArray(menuId, ['personInfo', 'myMsg', 'logOut', 'workbench'] > -1)) {
            switch (menuId) {
              case 'personInfo':
                break;
              case 'myMsg':
                if (
                  _self.menuItemMap[menuId].eventHandler &&
                  _self.menuItemMap[menuId].eventHandler.id == '246174de446c31abe74fb2285e1ffcd9'
                ) {
                  UserMsgEvent.myMsgEvent();
                } else {
                  DefaultMsgEvent.myMsgEvent();
                }
                $this.find('.myMsgWrap').show();
                break;
              case 'switchAccount':
                break;
              case 'logOut':
                window.open(ctx + '/logout', '_self');
                break;
              case 'workbench':
                HeaderDefaultEvent.myWorkBench();
                break;
            }
          }
        });

      $(_self.element)
        .on('mouseenter', 'li[menuid="workbench"]', function () {
          var $this = $(this);
          HeaderDefaultEvent.myWorkBench();
          $this.find('.wHeaderNav-workbench').show();
        })
        .on('mouseleave', 'li[menuid="workbench"]', function () {
          $('.wHeaderNav-workbench').hide();
        });

      $(_self.element).on('click', '.wHeaderNav-workbench', function (e) {
        e.stopPropagation();
      });
      $(_self.element).on('click', '.wHeaderNav-workbench .nav-menu-item', function () {
        var uuid = $(this).data('uuid');
        var url = $(this).data('url');
        $.ajax({
          url: ctx + '/api/user/preferences/save',
          type: 'POST',
          data: {
            dataKey: 'WORKBENCH',
            dataValue: uuid,
            moduleId: 'WORKBENCH',
            remark: '系统工作台记住上一次选择'
          },
          dataType: 'json',
          success: function (data) {
            var previewUrl = ctx + url;
            window.open(previewUrl, '_self');
          },
          error: function () {
            appModal.dialogError(err.responseJSON.msg);
          }
        });
        // JDS.call({
        //   service: 'multiOrgUserWorkInfoService.updateDefWorkbench',
        //   data: [uuid],
        //   version: '',
        //   success: function (result) {
        //     var previewUrl = ctx + url;
        //     window.open(previewUrl, '_self');
        //   },
        //   error: function (err) {
        //     appModal.dialogError(err.responseJSON.msg);
        //   }
        // });
      });

      $(_self.element)
        .on('mouseenter', "li.nav-menu-item[menuid='personInfo']", function () {
          $(this).find('.person-info-list').show();
        })
        .on('mouseleave', "li.nav-menu-item[menuid='personInfo']", function () {
          $(this).find('.person-info-list').hide();
        });

      $(_self.element).on('click', '.person-info-list li', function () {
        var $this = $(this);
        var type = $this.data('type');

        switch (type) {
          case 'info':
            var bean = {
              uuid: null,
              fax: null,
              idNumber: null,
              mobilePhone: null,
              officePhone: null,
              photoUuid: null,
              sex: null,
              userName: null,
              mainEmail: null,
              mainJobName: null,
              mainDepartmentName: null,
              code: null,
              loginName: null,
              type: 0
            };

            HeaderDefaultEvent._getUserInfoHtml(bean);
            break;
          case 'changePassword':
            return $.get(ctx + '/personalinfo/modify/password', function (modifyPwdHtml) {
              var url = ctx + '/web/app/page/preview/6aa256abaec09dfe2ef8e61326a14361?pageUuid=4f4dddab-2349-4eda-bd45-e16f14379790';
              window.open(url, '_blank');
            });
            break;

          case 'changeTheme':
            HeaderDefaultEvent._openThemeConfigModal();
            break;

          case 'bindAccount':
            HeaderDefaultEvent._showBindAccountDialog();
            break;
        }
      });

      // 切换账号
      $(_self.element)
        .on('mouseenter', "li.nav-menu-item[menuid='switchAccount']", function () {
          $(this).find('.switchAccountWrap').show();
        })
        .on('mouseleave', "li.nav-menu-item[menuid='switchAccount']", function () {
          $(this).find('.switchAccountWrap').hide();
        });
      $(_self.element).on('click', '.switchAccountWrap>ul>li', function (e) {
        var account = $(this).data('account');
        if (account && account.accountName) {
          // 当前账号不用切换
          if (account.userId == getCookie('cookie.current.userId')) {
            return;
          }
          HeaderDefaultEvent._switchAccount(account);
        }
      });
    },
    _pwdErrorMsg: function (name, text) {
      $("#personalInfoModifyPwd input[name='" + name + "']")
        .next('.error')
        .text(text)
        .addClass('icon-ptkj-wentiziduantishi');
    },

    _openThemeConfigModal: function () {
      console.log('打开主题设置');
      var _self = this;

      var allThemes;
      var chooseTheme;
      $.ajax({
        type: 'GET',
        url: '/api/getAllThemes',
        async: false,
        dataType: 'json',
        success: function (result) {
          allThemes = result;
          console.log(allThemes);
          // 更改主题字体显示文本
          $.each(allThemes, function (i, theme) {
            if (theme.custom && theme.custom.fontSize) {
              $.each(theme.custom.fontSize, function (j, fontSize) {
                if (fontSize.name == 'sm') {
                  fontSize.value = '小号字体(' + fontSize.value + ')';
                } else if (fontSize.name == 'default') {
                  fontSize.value = '中号字体(' + fontSize.value + ')';
                } else if (fontSize.name == 'lg') {
                  fontSize.value = '大号字体(' + fontSize.value + ')';
                } else if (fontSize.name == 'xl') {
                  fontSize.value = '特大号字体(' + fontSize.value + ')';
                }
              });
            }
          });
        }
      });

      $.ajax({
        type: 'GET',
        url: '/api/getUserTheme',
        async: false,
        dataType: 'json',
        success: function (result) {
          chooseTheme = result;
          chooseTheme.name = allThemes.find(function (theme) {
            return theme.theme === result.theme;
          }).name;
        }
      });

      var html = this._getThemeConfigModalHtml(allThemes, chooseTheme);
      appModal.dialog({
        title: '主题设置',
        message: html,
        size: 'medium',
        buttons: {
          confirm: {
            label: '保存',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var config = _self._getThemeConfig(allThemes);

              appModal.confirm({
                message: '确认要保存主题配置吗？保存主题将会刷新页面！',
                callback: function (result) {
                  if (result) {
                    _self._saveThemeConfig(config);
                  }
                }
              });
            }
          },
          cancel: {
            label: '取消',
            className: 'btn btn-default'
          }
        },

        shown: function (data) {
          if (chooseTheme.theme != 'wellpt-dark') {
            _self._initThemeModal(allThemes[0].custom, chooseTheme);
          }

          // Event
          $('#theme-selector')
            .off('change')
            .on('change', function () {
              var selectedThemeName = $(this).val();
              var selectedTheme = allThemes.find(function (theme) {
                return theme.name === selectedThemeName;
              });

              _self._initThemeModal(selectedTheme.custom, chooseTheme.name === selectedThemeName ? chooseTheme : null);
            });
        }
      });
    },

    _showBindAccountDialog: function () {
      var _self = this;
      var $dialog = appModal.dialog({
        title: '账号绑定',
        message: '<div class="dlg_bind_account_list"></div>',
        size: 'medium',
        width: '750px',
        shown: function (data) {
          _self._refreshBindAccountList($dialog.find('.dlg_bind_account_list'));
        },
        buttons: {
          confirm: {
            label: '关闭',
            className: 'well-btn w-btn-default'
          }
        }
      });

      // 绑定事件
      $($dialog).on('click', '.add-bind-account', function () {
        _self._showAddBindAccountDialog({
          callback: function () {
            _self._refreshBindAccountList($dialog.find('.dlg_bind_account_list'));
            // 重新绑定账号切换
            _self.switchAccountEvent($("li[menuid='switchAccount']"));
          }
        });
      });
      // 复制访问地址
      $($dialog).on('click', '.copy-addr', function () {
        var copyContent = $(this).parent().find('.login-url').text();
        $($dialog).append("<textarea id='accountHomrUrlContent' style='opacity: 0;'>");
        var inputElement = document.getElementById('accountHomrUrlContent');
        inputElement.value = copyContent;
        inputElement.select();
        document.execCommand('Copy');
        appModal.success('复制成功！');
        $('#accountHomrUrlContent').remove();
      });
      // 设置为主账号
      $($dialog).on('click', '.set-master-account', function () {
        var account = $(this).closest('td').find('.account-name').text();
        _self._setMasterAccount(account, function () {
          _self._refreshBindAccountList($dialog.find('.dlg_bind_account_list'));
        });
      });
      // 解绑
      $($dialog).on('click', '.unbind-account', function () {
        var account = $(this).closest('td').find('.account-name').text();
        _self._unbindAccount(account, function () {
          _self._refreshBindAccountList($dialog.find('.dlg_bind_account_list'));
          // 重新绑定账号切换
          _self.switchAccountEvent($("li[menuid='switchAccount']"));
        });
      });
    },
    _setMasterAccount: function (account, callback) {
      $.ajax({
        url: ctx + '/proxy/api/org/user/account/setMasterAccount/' + account,
        type: 'get',
        success: function (result) {
          if (result.code == 0) {
            appModal.success('设置成功！您可以通过主账号访问地址登录系统。');
            callback.call(this);
          } else {
            appModal.success('设置失败！');
          }
        }
      });
    },
    _unbindAccount: function (account, callback) {
      appModal.confirm('是否确认解除绑定？', function (confirmResult) {
        if (confirmResult) {
          $.ajax({
            url: ctx + '/proxy/api/org/user/account/unbound/' + account,
            type: 'get',
            success: function (result) {
              if (result.code == 0) {
                appModal.success('已解绑！');
                callback.call(this);
              } else {
                appModal.success('解绑失败！');
              }
            }
          });
        }
      });
    },
    _switchAccount: function (account) {
      var accountName = account.accountName;
      var masterAccount = account.masterAccount;
      var userId = account.userId;
      appModal.showMask('正在切换账号...');
      $.ajax({
        url:
          ctx +
          '/api/org/user/account/changingOver/' +
          accountName +
          '?masterAccount=' +
          masterAccount +
          '&accountName=' +
          accountName +
          '&userId=' +
          userId,
        type: 'post',
        data: {
          username: accountName,
          password: accountName
        },
        success: function (result) {
          appModal.hideMask();
          window.location.href = '/';
        },
        error: function () {
          appModal.error('切换账号失败！');
          appModal.hideMask();
        }
      });
    },
    _refreshBindAccountList: function ($container) {
      var _self = this;
      $container.html('');
      var unbindMsg =
        "&nbsp;&nbsp;&nbsp;&nbsp;如果您在系统中有多个账号，可以通过本功能，将账号进行绑定关联。关联后只需要登录设置好的主账号，即可切换访问全部关联账号。<a class='add-bind-account'><i class='iconfont icon-oa-banliguocheng'></i>现在绑定</a><div class='account-binding-demo'></div>";
      _self._listBindAccount(function (dataList) {
        if (dataList && dataList.length > 0) {
          _self._buildBindAccountList(dataList, $container);
        } else {
          $container.append(unbindMsg);
        }
      });
    },
    _buildBindAccountList: function (dataList, $container) {
      var tpl =
        "<table class='table table-hover table-striped disable-pointer-cursor' style='width: 98%'>" +
        " <thead style='display:none;'><tr><th></th></tr></thead>" +
        ' <tbody>' +
        ' {@each accountList as account,index}' +
        '   <tr><td><div class="accout-row">' +
        "     <div class='pull-left'>" +
        '     <div>' +
        '       <i class="ui-wIcon iconfont icon-ptkj-morentouxiang" style="font-size: 23px;display: none;"></i>' +
        '       <img src="/proxy/org/user/view/photo/user/${account.userId}" class="nav-avatar" onerror="javascript:this.style.display=&quot;none&quot;;this.previousElementSibling.style.display=&quot;inline-block&quot;;">' +
        "       <div class='account-name-container'><span class='account-name'>${account.accountName}</span> (${account.systemUnitName})" +
        "       {@if index == 0}<span class='well-btn w-btn-primary w-line-btn  btn_class_btnBatchMsg master-account-flag'>主账号</span>{@/if}" +
        '       </div>' +
        '     </div>' +
        "     <div class='login-url-container'>" +
        '       访问地址 ' +
        "       <span class='login-url'>${account.loginUrl}</span>" +
        "       <span class='iconfont icon-ptkj-fuzhi copy-addr' aria-hidden='true' title='复制访问地址'></span>" +
        '     </div>' +
        '     </div>' +
        "     <div class='pull-right' style='line-height: 5em;'>{@if index != 0}" +
        "       <a class='set-master-account'><i class='iconfont icon-oa-woguanzhude'></i>设为主账号</a>" +
        "       <a class='unbind-account'><i class='iconfont icon-ptkj-jiebang'></i>解绑</a>" +
        '     {@/if}</div>' +
        '     </div>' +
        '   </td></tr>' +
        ' {@/each}' +
        '   <tr><td class="normal-padding">' +
        "       <div><button type='button' class='btn btn-link btn-lg btn-block add-bind-account'><i class='iconfont icon-ptkj-jiahao'></i>添加账号绑定</button></div>" +
        '   </td></tr>' +
        ' </tbody>' +
        '</table>';
      var templateEngine = appContext.getJavaScriptTemplateEngine();
      var html = templateEngine.render(tpl, { accountList: dataList });
      console.log(html);
      $container.append(html);
    },
    _listBindAccount: function (callback) {
      // 请求后端获取绑定的账号列表
      $.ajax({
        url: ctx + '/api/org/user/account/getRelationAccounts',
        type: 'get',
        data: {},
        success: function (result) {
          var accounts = [];
          if ($.isArray(result.data)) {
            $.each(result.data, function (index, data) {
              var loginUrl = window.location.origin + (data.loginAddr || '');
              var account = {
                accountName: data.relationAccount,
                systemUnitName: data.relationSystemUnitName,
                loginUrl: loginUrl,
                masterAccount: data.masterAccount,
                userId: data.relationAccountId
              };
              if (data.masterAccount == data.relationAccount) {
                accounts = [account].concat(accounts);
              } else {
                accounts.push(account);
              }
            });
          }
          // 解绑后只有主账号
          if (accounts.length == 1 && accounts[0].masterAccount == accounts[0].relationAccount) {
            accounts = [];
          }
          callback.call(this, accounts);
        }
      });
    },
    _showAddBindAccountDialog: function (options) {
      var _self = this;
      var $dialog = appModal.dialog({
        title: '添加绑定账号',
        message: '<div class="dlg_add_bind_account"></div>',
        size: 'medium',
        shown: function (data) {
          var $container = $('.dlg_add_bind_account', $dialog);
          $container.append(
            '<div class="tip"><span><i class="iconfont icon-ptkj-xinxiwenxintishi"></i>您可以输入需要绑定的账号，身份验证通过后完成账号绑定！<i class="iconfont icon-ptkj-dacha pull-right"></></span></div>'
          );
          formBuilder.buildSelect2({
            container: $container,
            labelColSpan: '3',
            controlColSpan: '9',
            isRequired: true,
            label: '所属单位',
            name: 'belong_system_unit_id',
            select2: {
              data: _self._getBindSystemUnitData()
            },
            events: {
              change: function () {
                var systemUnitId = 'T001'; // $('#belong_system_unit_id', $dialog).val();
                _self._bindAccountSystemUnitChange(systemUnitId, $dialog);
              }
            }
          });
          $('#wellSelect_belong_system_unit_id', $dialog).css({ width: '392px' });
          // formBuilder.buildInput({
          //   container: $container,
          //   labelColSpan: '3',
          //   controlColSpan: '9',
          //   isRequired: true,
          //   label: '账号名',
          //   name: 'account_name'
          // });
          // formBuilder.buildInput({
          //   container: $container,
          //   labelColSpan: '3',
          //   controlColSpan: '9',
          //   isRequired: true,
          //   label: '密码',
          //   name: 'password',
          //   type: 'password'
          // });
          $container.append(_self._getValidateHtml());
        },
        buttons: {
          confirm: {
            label: '验证并绑定',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var result = false;
              var systemUnitId = $('#belong_system_unit_id', $dialog).val();
              var accountName = $('#account_name', $dialog).val();
              var password = $('#password', $dialog).val();
              var smsVerifyCode = $('#sms-verify', $dialog).val();
              var imageVerifyCode = $('#image-verify', $dialog).val();
              if (StringUtils.isBlank(systemUnitId)) {
                appModal.error('请选择所属单位！');
                return result;
              }
              if (StringUtils.isBlank(accountName)) {
                appModal.error('账号名不能为空！');
                return result;
              }
              if (StringUtils.isBlank(password)) {
                appModal.error('密码不能为空！');
                return result;
              }
              var loginPageConfig = $dialog.data('loginPageConfig');
              if (loginPageConfig != null) {
                if (loginPageConfig.loginBoxAccountSms == 0 && StringUtils.isBlank(smsVerifyCode)) {
                  appModal.error('短信验证码不能为空！');
                  return result;
                }
                if (loginPageConfig.loginBoxAccountCode == 0) {
                  if (StringUtils.isBlank(imageVerifyCode)) {
                    appModal.error('图形验证码不能为空！');
                    return result;
                  } else if (!_self._validateImageVerifyCode(imageVerifyCode, loginPageConfig.accountCodeIgnoreCase)) {
                    return result;
                  }
                }
              }

              // 保存账号绑定
              result = _self._saveBindAccount({
                systemUnitId: systemUnitId,
                accountName: accountName,
                password: password,
                smsVerifyCode: smsVerifyCode,
                imageVerifyCode: imageVerifyCode
              });

              // 成功提示信息
              if (result) {
                appModal.success('绑定成功！');
                if (options.callback && $.isFunction(options.callback)) {
                  options.callback.call(this);
                }
              }

              return result;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn btn-default'
          }
        }
      });

      // 关闭提示
      $($dialog).on('click', 'i.icon-ptkj-dacha', function (e) {
        $(this).closest('.tip').remove();
      });

      // 获取短信验证码
      var index = 60;
      var interval = null;
      var userDetails = SpringSecurityUtils.getUserDetails();
      var intervalCode = function () {
        if (index > 0) {
          index = index - 1;
          $('.send-sms', $dialog)
            .addClass('spanA')
            .html(index + 's后重发');
        } else {
          if (interval) {
            window.clearInterval(interval);
          }
          $('.send-sms', $dialog).removeClass('spanA').html('获取验证码');
        }
      };
      $($dialog).on('click', '.send-sms', function (e) {
        $.ajax({
          type: 'POST',
          url: ctx + '/proxy/security/aid/sendSms',
          dataType: 'json',
          data: {
            username: userDetails.loginName
          },
          success: function (data) {
            for (var i in data) {
              if (i == 1) {
                appModal.error(data[i]);
              } else {
                index = 60;
                $('.send-sms', $dialog)
                  .addClass('spanA')
                  .html(index + 's后重发');
                interval = window.setInterval(intervalCode, 1000);
              }
            }
          }
        });
      });
      // 变更图形验证码
      $($dialog).on('click', '.change-image-verify', function () {
        var _this = $(this);
        $.get('/captcha', {}, function (res) {
          _this.empty();
          _this.html(res);
        });
      });
    },
    _validateImageVerifyCode: function (verifyCode, ignoreCase) {
      var verifyResult = false;
      $.ajax({
        type: 'GET',
        url: ctx + '/captcha?verify=' + verifyCode + '&ignorecase=' + ignoreCase,
        async: false,
        dataType: 'json',
        success: function (data) {
          verifyResult = data.success;
          if (data.msg) {
            appModal.error(data.msg);
          }
        }
      });
      return verifyResult;
    },
    _getValidateHtml: function () {
      return (
        '<div class="account">' +
        '<ul>' +
        '<li class="username-li">' +
        '  <label>账号名称<font color="red">*</font></label>' +
        '  <input type="text" placeholder="请输入账号" id="account_name" autofocus="autofocus">' +
        '</li>' +
        '<li class="password-li">' +
        '  <label>密码<font color="red">*</font></label>' +
        '  <input type="password" id="password" placeholder="请输入密码">' +
        // '  <a href="javascript:void(0);"></a>' +
        '</li>' +
        '<li class="sms-verify-li select" style="display:none;">' +
        '<label>短信验证码<font color="red">*</font></label>' +
        '<input type="text" id="sms-verify" placeholder="请输入验证码">' +
        '<span class="send-sms">获取验证码</span>' +
        '</li>' +
        '<li class="image-verify-li" style="display:none">' +
        '<label>图形验证码<font color="red">*</font></label>' +
        '<input type="text" id="image-verify" placeholder="请输入验证码" ignore-case="1">' +
        '<span class="change-image-verify" title="点击图片更换验证码">' +
        '</span>' +
        '</li>' +
        '</ul>' +
        '</div>'
      );
    },
    _bindAccountSystemUnitChange: function (systemUnitId, $dialog) {
      if (StringUtils.isBlank(systemUnitId)) {
        $('.sms-verify-li', $dialog).hide();
        $('.image-verify-li', $dialog).hide();
        $dialog.data('loginPageConfig', null);
      } else {
        $.ajax({
          url: ctx + '/proxy/web/login/config/getLoginPageConfig?systemUnitId=' + systemUnitId,
          type: 'get',
          data: {},
          async: false,
          success: function (result) {
            if (result.code === 0) {
              // 短信验证码
              if (result.data.loginBoxAccountSms == 0) {
                $('.sms-verify', $dialog).val('');
                $('.sms-verify-li', $dialog).show();
              } else {
                $('.sms-verify-li', $dialog).hide();
              }
              // 图形验证码
              if (result.data.loginBoxAccountCode == 0) {
                $('.image-verify', $dialog).val('');
                $('.image-verify-li', $dialog).show();
                $('.change-image-verify', $dialog).trigger('click');
              } else {
                $('.image-verify-li', $dialog).hide();
              }
              $dialog.data('loginPageConfig', result.data);
            } else {
              $('.sms-verify-li', $dialog).hide();
              $('.image-verify-li', $dialog).hide();
              $dialog.data('loginPageConfig', null);
            }
          },
          error: function () {
            $('.sms-verify-li', $dialog).hide();
            $('.image-verify-li', $dialog).hide();
            $dialog.data('loginPageConfig', null);
          }
        });
      }
    },
    _saveBindAccount: function (data) {
      var JDS = require('server').JDS;
      var bindResult = false;
      JDS.restfulPost({
        url: ctx + '/api/org/user/account/saveRelationAccount',
        data: {
          systemUnitId: data.systemUnitId,
          relationAccount: data.accountName,
          relationPassword: data.password,
          smsValidCode: data.smsVerifyCode,
          validCode: data.imageVerifyCode
        },
        async: false,
        success: function (result) {
          if (result.code != 0) {
            appModal.error(result.msg);
          } else {
            bindResult = true;
          }
        }
      });
      return bindResult;
    },

    _getBindSystemUnitData: function () {
      var systenUnitData = [];
      $.ajax({
        url: ctx + '/api/org/multi/queryAllSystemUnitList',
        type: 'get',
        data: {},
        async: false,
        success: function (result) {
          if ($.isArray(result.data)) {
            $.each(result.data, function (index, data) {
              systenUnitData.push({
                id: data.id,
                text: data.name
              });
            });
          }
        }
      });
      return systenUnitData;
    },

    _getThemeConfig: function (allThemes) {
      var themeName = $('#theme-selector').val();
      var themeId = allThemes.find(function (theme) {
        return theme.name === themeName;
      }).theme;

      var color = $('#theme-color-selector .color-item-wrapper.selected').attr('title');
      var colorValue = $('#theme-color-selector .color-item-wrapper.selected').attr('data-color');
      var fontSize = $('#theme-font-size-selector option:selected').attr('title');

      var config = {
        theme: themeId
      };

      if (color || fontSize) {
        config.color = color || 'default';
        config.fontSize = fontSize || 'default';
        config.order = ['color', 'fontSize'];
        config.colorValue = colorValue || '';
      }

      return config;
    },

    _saveThemeConfig: function (config) {
      $.ajax({
        type: 'POST',
        url: ctx + '/api/saveTheme',
        dataType: 'json',
        data: config,
        async: true,
        success: function (result) {
          location.reload();
        }
      });
    },

    _getThemeConfigModalHtml: function (allThemes, chooseTheme) {
      var html = '';
      html = [];

      html.push('<div class="well-form form-horizontal" style="width: 90%;">');

      // 主题下拉框
      html.push('<div class="form-group">');
      html.push('<label class="well-form-label control-label">主题</label>');
      html.push('<div class="well-form-control"><select id="theme-selector">');
      for (var i = 0; i < allThemes.length; i++) {
        var isSelected = chooseTheme.theme === allThemes[i].theme;
        var themeId = allThemes[i].theme;
        html.push('<option theme="' + themeId + '"' + (isSelected ? 'selected' : '') + '>' + allThemes[i].name + '</option>');
      }
      html.push('</select></div>');
      html.push('</div>');

      // 颜色选择器
      html.push('<div class="form-group" id="theme-color-selector"></div>');

      //字体选择器
      html.push('<div class="form-group" id="theme-font-size-selector"></div>');

      html.push('</div>');

      return html.join('');
    },

    _initThemeModal: function (customConfig, chooseTheme) {
      // 颜色选择器
      var $themeColorSelector = $('#theme-color-selector');
      $themeColorSelector.empty().hide();
      if (customConfig && customConfig.colors) {
        $themeColorSelector.append('<label class="well-form-label control-label">颜色</label>');

        var colorsHtml = '';
        for (var i = 0; i < customConfig.colors.length; i++) {
          var color = customConfig.colors[i];

          var isSelected = chooseTheme && chooseTheme.color === color.name;
          var colorHtml =
            '<div class="color-item-wrapper ' +
            (isSelected ? 'selected' : '') +
            '" title="' +
            color.name +
            '" data-color="' +
            color.value +
            '">';
          colorHtml += '<div class="color-item-inner" style="background: ' + color.value + ';" >';
          colorHtml += '</div>';
          colorHtml += '</div>';
          colorsHtml += colorHtml;
        }

        $themeColorSelector.append('<div class="well-form-control">' + colorsHtml + '</div>');
        $themeColorSelector.show();

        $themeColorSelector
          .find('.color-item-wrapper')
          .off('click')
          .on('click', function () {
            $themeColorSelector.find('.color-item-wrapper').removeClass('selected');
            var $this = $(this);
            $this.addClass('selected');
          });
      }

      // 字体选择器
      var $themeFontSizeSelector = $('#theme-font-size-selector');
      $themeFontSizeSelector.empty().hide();

      if (customConfig && customConfig.fontSize) {
        $themeFontSizeSelector.append('<label class="well-form-label control-label">字体</label>');

        var fontSizeHtml = '';
        for (var i = 0; i < customConfig.fontSize.length; i++) {
          var fontSize = customConfig.fontSize[i];

          var isSelected = chooseTheme && chooseTheme.fontSize === fontSize.name;

          fontSizeHtml += '<option ' + (isSelected ? 'selected' : '') + ' title="' + fontSize.name + '">' + fontSize.value + '</option>';
        }

        $themeFontSizeSelector.append('<div class="well-form-control"><select>' + fontSizeHtml + '</select></div>');
        $themeFontSizeSelector.show();
      }
    },

    _initUserInfoDialog: function (html, bean) {
      var _self = this;
      appModal.dialog({
        title: '用户资料',
        message: html,
        size: 'large',
        buttons: {
          confirm: {
            label: '编辑',
            className: 'well-btn w-btn-primary',
            callback: function () {
              var flag = false;
              if ($('#user-info-form').hasClass('has-edited-box')) {
                _self._initUploadMethod();
                _self._uploadAvatar();
                $('#user-info-form').json2form(bean);
                if (bean.officePhone) {
                  $('#officePhone1').val(bean.officePhone.split('-')[0]);
                  $('#officePhone2').val(bean.officePhone.split('-')[1]);
                }
                $("input[name='sex'][value='" + bean.sex + "']").attr('checked', true);
                $('#user-info-form').removeClass('has-edited-box').addClass('is-edittind-box');
                $(this).find('.w-btn-primary').text('保存');
              } else {
                $('#user-info-form').form2json(bean);
                bean.type = bean.type || 0;
                bean.code = bean.code || '0000000000';
                bean.isForbidden = bean.isForbidden ? '1' : '0';
                bean.isLocked = bean.isLocked ? '1' : '0';
                bean.officePhone = $('#officePhone1').val() + '-' + $('#officePhone2').val();
                var url = '/api/org/multi/' + (bean.uuid ? 'modifyUser' : 'addUser');

                $.ajax({
                  type: 'POST',
                  url: url,
                  dataType: 'json',
                  data: bean,
                  async: false,
                  success: function (result) {
                    if (result.code === 0) {
                      if ($("li[menuid='personInfo'] .nav-avatar").length > 0) {
                        var imgSrc = ctx + '/org/user/view/photo/' + result.data.photoUuid;
                        $("li[menuid='personInfo'] .nav-avatar").attr('src', imgSrc);
                      } else {
                        var imgSrc = ctx + '/org/user/view/photo/' + result.data.photoUuid;
                        $("li[menuid='personInfo'] .ui-wIcon", _self.element).hide();
                        $("li[menuid='personInfo'] .ui-wIcon", _self.element).after("<img src='" + imgSrc + "' class='nav-avatar'/>");
                      }
                      $("li[menuid='personInfo'] .nav-text").text(result.data.userName);
                      appModal.success('保存成功！');
                      flag = true;
                    } else if (result.code === 1) {
                      result.data.forEach(name => {
                        appModal.error(name + '已被使用！');
                      });
                    } else {
                      appModal.error(result.msg);
                    }
                  }
                });
              }
              return flag;
            }
          },
          cancel: {
            label: '取消',
            className: 'btn btn-default'
          }
        }
      });
    },
    _getUserInfoHtml: function (bean) {
      var img = '';
      var html = '';
      $.ajax({
        dataType: 'json',
        type: 'POST',
        url: ctx + '/personalinfo/show/infoBody',
        async: false,
        success: function (res) {
          bean = res;
          img = bean.photoUuid ? ctx + '/org/user/view/photo/' + bean.photoUuid : '';
        }
      });

      html +=
        '<div id="user-info-form" class="form-widget form-user-info has-edited-box">' +
        '<input id="loginName" name="loginName" type="hidden"/>' +
        '<input name="code" type="hidden"/>' +
        '<input type="hidden" id="type" name="type" value="0"/>' +
        '<input type="hidden" id="systemUnitId" name="systemUnitId"/>' +
        '<input type="hidden" id="photoUuid" name="photoUuid"/>' +
        '<div class="well-form form-horizontal">' +
        '<div class="form-group hasEdited">' +
        '<div class="user-info-avatar">';
      if (img) {
        html += '<img src="' + img + '" alt="">';
      } else {
        html += '<div class="default-avatar"><i class="iconfont icon-wsbs-dengluyonghumingzhanghao"></i></div>';
      }

      html +=
        '</div>' + '<span class="user-info-name">' + (bean.userName || '') + '</span>' + '</div>' + '<div class="form-group isEditting">';
      var imgClass = img ? 'hasImg' : 'noImg';
      html += '<div class="user-info-avatar ' + imgClass + '">';
      html += '<img src="' + img + '" alt="" id="newAvatar">';
      html += '<div class="default-avatar"><i class="iconfont icon-wsbs-dengluyonghumingzhanghao"></i></div>';

      html +=
        '<div class="user-info-shadow"><i id="upload_del" class="iconfont icon-ptkj-shanchu"></i></div></div>' +
        '<button type="button" class="user-info-btn" id="uploadAvatar"><i class="iconfont icon-ptkj-shangchuan" style="font-size: 14px;margin-right: 6px;"></i>上传头像</button><input type="file" accept=".jpeg,.jpg,.png" id="upload"/>' +
        '<span class="user-info-tip">支持格式：JPG、PNG；大小限制：不超过50M；建议尺寸：128px*128px</span>' +
        '</div>' +
        '<div class="form-group isEditting">' +
        '<label for="name" class="well-form-label control-label no-suffix">姓名</label>' +
        '<div class="well-form-control">' +
        '<input type="text" class="form-control" id="userName" name="userName" placeholder="请输入姓名" autocomplete="off">' +
        '</div>' +
        '</div>' +
        '<div class="form-group">' +
        '<label for="name" class="well-form-label control-label no-suffix">部门</label>' +
        '<div class="well-form-control">' +
        '<input type="text" class="form-control isEditting" id="mainDepartmentName" name="mainDepartmentName" autocomplete="off" readonly>' +
        '<div class="user-info-edited hasEdited">' +
        (bean.mainDepartmentName || '') +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="form-group">' +
        '<label for="name" class="well-form-label control-label no-suffix">职位</label>' +
        '<div class="well-form-control">' +
        '<input type="text" class="form-control isEditting" id="mainJobName" name="mainJobName" autocomplete="off" readonly>' +
        '<div class="user-info-edited hasEdited">' +
        (bean.mainJobName || '') +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="form-group">' +
        '<label for="name" class="well-form-label control-label no-suffix">性别</label>' +
        '<div class="well-form-control">' +
        '<div class="isEditting">' +
        '<input type="radio" class="form-control" id="sex0" name="sex" value="1"/><label for="sex0">男</label>' +
        '<input type="radio" class="form-control" id="sex1" name="sex" value="0"/><label for="sex1">女</label>' +
        '</div>' +
        '<div class="user-info-edited hasEdited">' +
        (bean.sex == '0' ? '女' : '男' || '') +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="form-group">' +
        '<label for="name" class="well-form-label control-label no-suffix">身份证号</label>' +
        '<div class="well-form-control">' +
        '<input type="text" class="form-control isEditting" id="idNumber" name="idNumber" placeholder="请输入身份证号" autocomplete="off">' +
        '<div class="user-info-edited hasEdited">' +
        (bean.idNumber || '') +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="form-group">' +
        '<label for="name" class="well-form-label control-label no-suffix">手机号码</label>' +
        '<div class="well-form-control">' +
        '<input type="text" class="form-control isEditting" id="mobilePhone" name="mobilePhone" placeholder="请输入手机号码" autocomplete="off">' +
        '<div class="user-info-edited hasEdited">' +
        (bean.mobilePhone || '') +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="form-group">' +
        '<label for="name" class="well-form-label control-label no-suffix">办公电话</label>' +
        '<div class="well-form-control">' +
        '<div class="isEditting">' +
        '<input type="text" class="form-control" id="officePhone1" name="officePhone1" autocomplete="off" style="display: inline-block;width:38%;">-' +
        '<input type="text" class="form-control" id="officePhone2" name="officePhone2" autocomplete="off" style="display: inline-block;width:60%;">' +
        '</div>' +
        '<div class="user-info-edited hasEdited">' +
        (bean.officePhone || '') +
        '</div>' +
        '</div>' +
        '</div>' +
        '<div class="form-group">' +
        '<label for="name" class="well-form-label control-label no-suffix">邮箱地址</label>' +
        '<div class="well-form-control">' +
        '<input type="text" class="form-control isEditting" id="mainEmail" name="mainEmail" placeholder="请输入邮箱地址" autocomplete="off">' +
        '<div class="user-info-edited hasEdited">' +
        (bean.mainEmail || '') +
        '</div>' +
        '</div>' +
        '</div>' +
        // '<div class="form-group">' +
        // '<label for="name" class="well-form-label control-label no-suffix">传真号码</label>' +
        // '<div class="well-form-control">' +
        // '<input type="text" class="form-control isEditting" id="fax" name="fax" placeholder="请输入传真号码" autocomplete="off">' +
        // '<div class="user-info-edited hasEdited">'+ (bean.fax || '') +'</div>' +
        // '</div>' +
        // '</div>' +
        '</div>' +
        '</div>';
      this._initUserInfoDialog(html, bean);
    },
    _uploadAvatar: function () {
      $('#upload_del').click(function () {
        $('#photoUuid').val('');
        $('#newAvatar').attr('src', '');
        $('#upload').val('');
        $(this).parents('.user-info-avatar').removeClass('hasImg').addClass('noImg');
      });

      $('#upload').bind('change', function () {
        var file = this.value;
        if (file == '') {
          appModal.error('请选择.jpeg,.jpg,.png文件');
          return;
        }
        if (file.indexOf('.') < 0) {
          return;
        }
      });

      $('#uploadAvatar').click(function () {
        $('#upload').trigger('click');
      });
    },
    _initUploadMethod: function () {
      var url = ctx + fileServiceURL.saveFiles; // 上传文件的地址
      var $fileElement = $('#upload');
      $fileElement
        .fileupload({
          url: url,
          iframe: $.browser.msie && $.browser.version < 10,
          dataType: 'json',
          autoUpload: true,
          formData: {
            signUploadFile: ''
          },
          maxFileSize: 50000000 // 2000 MB
        })
        .on('fileuploadsubmit', function (e, data) {})
        .on('fileuploaddone', function (e, data) {
          var photoCode = data._response.result.data[0].fileID;
          $('#photoUuid').val(photoCode);
          $('#newAvatar').attr('src', ctx + '/org/user/view/photo/' + photoCode);
          $('.isEditting .user-info-avatar').removeClass('noImg').addClass('hasImg');
          appModal.toast('头像上传成功');
        })
        .on('fileuploadfail', function (e, data) {
          appModal.error(data);
        });
    }
  };
  window.HeaderDefaultEvent = HeaderDefaultEvent;
  return HeaderDefaultEvent;
});
