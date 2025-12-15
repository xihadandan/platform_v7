define([
  'jquery',
  'commons',
  'constant',
  'server',
  'ListViewWidgetDevelopment',
  'appModal',
  'minicolors',
  'AppEmailTagDevelopment',
  'AppEmailFolderDevelopment',
  'dataStoreBase',
  'formBuilder'
], function (
  $,
  commons,
  constant,
  server,
  ListViewWidgetDevelopment,
  appModal,
  minicolors,
  AppEmailTagDevelopment,
  AppEmailFolderDevelopment,
  DataStore,
  formBuilder
) {
  var JDS = server.JDS;
  var AppEmailListDevelopment = function () {
    ListViewWidgetDevelopment.apply(this, arguments);
  };

  commons.inherit(AppEmailListDevelopment, ListViewWidgetDevelopment, {
    beforeRender: function (options, configuration) {
      var _self = this;
      if (options.widgetDefinition.params) {
        var otherConditions = [],
          paneName;
        var menuName = options.widgetDefinition.params.menuName;
        var pannelTitle = options.widgetDefinition.params.title;
        $('#mailListPanelTitle').text(pannelTitle);
        if (menuName == '其他邮箱账号') {
          //其他邮箱账号的菜单点击进来的
          var mailAddress = options.widgetDefinition.params.mailAddress;
          otherConditions.push({
            columnIndex: 'MAILBOX_NAME',
            value: mailAddress,
            type: 'eq'
          });
          _self.mailAddress = mailAddress;
          _self.addOtherConditions(otherConditions);
          return;
        }

        if (menuName == '我的文件夹邮件') {
          var folderCode = options.widgetDefinition.params.folderCode;
          otherConditions.push({
            columnIndex: 'MAILBOX_NAME',
            value: folderCode,
            type: 'eq'
          });
          otherConditions.push({
            columnIndex: 'STATUS',
            value: -1,
            type: 'ne'
          });
          otherConditions.push({
            columnIndex: 'STATUS',
            value: -2,
            type: 'ne'
          });
          _self.addOtherConditions(otherConditions);
          return;
        }

        if (menuName == '我的标签邮件') {
          var tagUuid = options.widgetDefinition.params.tagUuid;
          otherConditions.push({
            sql: " exists (select 1 from wm_mail_rela_tag tag where tag.mail_uuid=this_.uuid and tag.tag_uuid='" + tagUuid + "')"
          });
          _self.addOtherConditions(otherConditions);
          return;
        }
      }
    },

    getCurrentMenu: function () {
      var menuitem = $('.nav-menu-active').data('menuitem');
      if (menuitem && menuitem.navType == 3) {
        if (menuitem.folderCode) {
          return '我的文件夹';
        } else if (menuitem.tagColor) {
          return '我的标签';
        }
      }
      return null;
    },

    loadCssLink: function () {
      // $('head').append(
      //   $('<link>', {
      //     href: staticPrefix + '/js/minicolors/css/jquery.minicolors.css',
      //     rel: 'stylesheet'
      //   }),
      //   $('<link>', {
      //     href: staticPrefix + '/js/pt/css/webmail/wm_webmail_v2.css',
      //     rel: 'stylesheet'
      //   })
      // );
    },

    afterRender: function (options, configuration) {
      var _self = this;
      $("th[data-field='IS_READ']", this.widget.element).css({
        'min-width': '70px'
      });
      _self.loadCssLink();
      _self.loadMoveFolderButtonGroup();
      _self.loadTagMailButtonGroup();
      _self.appendUnreadMailNumsButton(configuration);
      _self.bindEvent();
      _self.widget.element.find('.fixed-table-body').css({
        overflow: 'initial'
      });
    },

    appendUnreadMailNumsButton: function (configuration) {
      if (configuration.name == '收件箱的邮件表格') {
        var _self = this;
        var $button = $('<button>', {
          class: 'btn btn-default btn-bg-color btn-filterUnreadMail'
        }).append(
          $('<span>').text('筛选未读邮件 '),
          $('<span>', {
            class: 'badge'
          })
        );
        var conditions = [
          {
            sql: configuration.defaultCondition
          },
          {
            columnIndex: 'IS_READ',
            value: 0,
            type: 'eq'
          }
        ];
        this.widget.element.find('.div_header_toolbar').prepend($button);
        _self.getUnreadMailCount(configuration.dataStoreId, conditions);
        $button.on('click', function () {
          if ($(this).hasClass('active')) {
            $(this).removeClass('active');
            _self.clearOtherConditions({
              columnIndex: 'IS_READ',
              value: 0,
              type: 'eq'
            });
            $(this).find('span:eq(0)').text('筛选未读邮件 ');
          } else {
            _self.addOtherConditions([
              {
                columnIndex: 'IS_READ',
                value: 0,
                type: 'eq'
              }
            ]);
            $(this).addClass('active');
            $(this).find('span:eq(0)').text('取消筛选未读邮件 ');
          }
          _self.widget.refresh();
        });
      }
    },

    getUnreadMailCount: function (dataStoreId, defaultCondition) {
      var _self = this;
      var unreadDataStore = new DataStore({
        dataStoreId: dataStoreId,
        defaultCriterions: defaultCondition,
        onDataChange: function (data, count) {
          if (count != 0) {
            _self.widget.element.find('.btn-filterUnreadMail .badge').text(count);
          }
        }
      });
      unreadDataStore.getCount(true);
    },

    bindEvent: function () {
      var _self = this;
      var $pageElement = _self.widget.pageContainer.element;

      $pageElement.on('AppEmailList.Refresh', function () {
        _self.getWidget().refresh();
      });

      //移动邮件到指定文件夹点击事件
      _self.widget.element
        .off('click', '.js-moveToOwnFolder,.li_class_moveToInBox')
        .on('click', '.js-moveToOwnFolder,.li_class_moveToInBox', function () {
          var selections = _self.getSelections();
          if (selections.length == 0) {
            appModal.alert('请先勾选需要移动的邮件');
            return;
          }
          var folder;
          if ($(this).is('.li_class_moveToInBox')) {
            folder = 'INBOX';
          } else {
            folder = $(this).attr('folderCode');
          }
          var emailUuids = [];
          for (var select in selections) {
            emailUuids.push(selections[select].UUID);
          }
          _self.moveEmailToFolder(folder, emailUuids);
        });

      //标记邮件
      _self.widget.element.off('click', '.js-markMailTag').on('click', '.js-markMailTag', function () {
        var selections = _self.getSelections();
        if (selections.length == 0) {
          appModal.alert('请先勾选需要标记的邮件');
          return;
        }
        _self.markMailByTag(_self.getSelectedRowIds(), $(this).attr('tagUuid'));
      });

      var $tableElement = _self.widget.$tableElement;
      //表格渲染数据完毕后的事件处理
      $tableElement.on('post-body.bs.table', function (event, data) {
        //1.表格行增加uuid属性
        var datas = _self.getData();
        if (datas && datas.length > 0) {
          for (var i in datas) {
            _self.widget.$tableElement.find('tr[data-index=' + i + ']').attr('uuid', datas[i].UUID);
          }
        }
        //2.加载邮件的标签
        _self.loadTableMailTags();
      });

      $tableElement.on('hover', '.emailTrTagDiv', function (e) {
        if (e.type == 'mouseenter') {
          $(this).addClass('emailTrTagHover');
        } else {
          $(this).removeClass('emailTrTagHover');
        }
      });

      $tableElement.on('click', '.emailTrTagClose', function (e) {
        var $parentDiv = $(this).parents('.emailTrTagDiv');
        _self.removeMailTags([$parentDiv.attr('emailUuid')], $parentDiv.attr('tagUuid'));
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

      _self.widget.element.off('click', '.li_class_addTagAndMark').on('click', '.li_class_addTagAndMark', function (e) {
        _self.addTagAndMark();
      });
    },

    //加载每一行表格的邮件所标记的标签
    loadTableMailTags: function (refreshEmailUuids) {
      var _self = this;

      if ($('th[data-field="SUBJECT"]').length == 0) {
        return;
      }
      var emailUuids = [];
      if (refreshEmailUuids && refreshEmailUuids.length > 0) {
        //局部刷新邮件列表
        emailUuids = refreshEmailUuids;
      } else {
        //全局刷新邮件列表
        var emailDatas = _self.getData();
        for (var i = 0, len = emailDatas.length; i < len; i++) {
          emailUuids.push(emailDatas[i].UUID);
        }
      }

      var index = $('th[data-field="SUBJECT"]')[0].cellIndex;
      if (emailUuids.length > 0) {
        server.JDS.call({
          service: 'wmMailTagFacadeService.queryMailRelaTag',
          data: [emailUuids],
          success: function (result) {
            for (var i = 0, len = emailUuids.length; i < len; i++) {
              var $tr = _self.widget.$tableElement.find('tr[uuid=' + emailUuids[i] + ']');
              var $td = $tr.find('td:eq(' + index + ')');
              // $td.css('white-space', 'pre-wrap');//标签超出显示
              $td.parent().off('mouseenter');
              $td.parent().off('mouseleave');
              $td.find('.emailTrTagDiv').remove();
              if (result.data[emailUuids[i]]) {
                $td.css('height', 60);
                var $tagContainer = $td.find('.emailTrTagDivContainer');
                var direction = 'left2right';
                if ($tagContainer.length == 0) {
                  var marginTop = 0;
                  if ($td.text().trim() == '') {
                    marginTop = 9;
                  }
                  $td.append(
                    $('<div>', {
                      class: 'emailTrTagDivContainer'
                    })
                  );
                  $tagContainer = $td.find('.emailTrTagDivContainer');
                  $tagContainer.css('margin-top', marginTop);
                  $td.parent().find('td').css('padding-top', '1px');
                }

                //计算标签容器的样式
                var $tds = $td.parent().find('td:visible');
                var leftWidth = 0,
                  rightWidth = 0,
                  tagTdWidth = 0;
                for (var t = 0, tlen = $tds.length; t < tlen; t++) {
                  if (t < index) {
                    leftWidth += $tds[t].offsetWidth;
                  } else if (t > index) {
                    rightWidth += $tds[t].offsetWidth;
                  } else {
                    tagTdWidth = $tds[t].offsetWidth;
                  }
                }
                if (rightWidth + tagTdWidth < $td.parent()[0].clientWidth / 2) {
                  //标签从右向左扩展
                  $tagContainer.css({
                    right: rightWidth,
                    width: leftWidth + tagTdWidth,
                    'overflow-x': 'hidden',
                    'text-align': 'right'
                  });
                  direction = 'right2left';
                } else {
                  //标签从左向右扩展
                  $tagContainer.css({
                    width: rightWidth + tagTdWidth,
                    'overflow-x': 'hidden'
                  });
                }

                var tags = result.data[emailUuids[i]];
                if (direction == 'right2left') {
                  tags.reverse();
                }
                for (var j in tags) {
                  var $textSpan = $('<span>', {
                    class: 'emailTrTagTextSpan',
                    style: 'background:' + tags[j].tagColor + ''
                  }).text(tags[j].tagName);
                  var $closeSpan = $('<span>', {
                    class: 'emailTrTagClose jsTagDelete',
                    style: 'background:' + tags[j].tagColor + ';'
                  }).append(
                    $('<b>', {
                      class: 'emailTrTagCloseIcon jsTagDelete'
                    }).text('x')
                  );
                  var color = _self.getAdjustFontColor(tags[j].tagColor);
                  if (color) {
                    $textSpan.css('color', color);
                    $closeSpan.css('color', color);
                  }
                  var $div = $('<div>', {
                    class: 'emailTrTagDiv',
                    tagUuid: tags[j].uuid,
                    emailUuid: emailUuids[i]
                  }).append($textSpan, $closeSpan);

                  $tagContainer.append($div);
                }

                //标签超出部分跑马灯效果
                var scrollWidth = $tagContainer[0].scrollWidth;
                var clientWidth = $tagContainer[0].clientWidth;
                if (scrollWidth > clientWidth) {
                  $td.parent().on('mouseenter', function () {
                    var _this = $(this);
                    var $tagContainer = $(this).find('.emailTrTagDivContainer');
                    var maxLeft = $tagContainer[0].scrollWidth - $tagContainer[0].clientWidth + 40,
                      left = 0;
                    var timeInterval = window.setInterval(function () {
                      left += 10;
                      if (left < maxLeft) {
                        _this.find('.emailTrTagDiv').animate(
                          {
                            left: -left
                          },
                          90
                        );
                      }
                    }, 100);
                    $(this).data('timeInterval', timeInterval);
                  });
                  $td.parent().on('mouseleave', function () {
                    var _this = $(this);
                    _this.find('.emailTrTagDiv').animate({
                      left: 0
                    });
                    window.clearInterval($(this).data('timeInterval'));
                  });

                  $td.find('.emailTrTagDiv').on('mouseenter', function () {
                    window.clearInterval($(this).parents('tr').data('timeInterval'));
                  });
                }
              }
            }
          },
          error: function (jqXHR) {}
        });
      }
    },

    getAdjustFontColor: function (color) {
      color = color.slice(1);
      if (parseInt(color, 16) > 14408667) {
        return 'black';
      }
      return '';

      /*color=color.slice(1);
                var c16,c10,max16=15,b=[];
                for(var i=0;i<color.length;i++){
                    c16=parseInt(color.charAt(i),16);//  to 16进制
                    c10=parseInt(max16-c16,10);// 10进制计算
                    b.push(c10.toString(16)); // to 16进制
                }
                return '#'+b.join('');*/
    },

    //触发AppEmail.Change事件刷新左导航上的数量值更新（AppEmail.Change事件绑定在AppEmailLeftSidebarWidgetDevelopment）
    refreshBadgeNum: function () {
      this.getPageContainer().trigger('AppEmail.Change');
    },

    //行点击事件
    onClickRow: function (rowNum, row, $element, field) {
      //判断是否是标签的点击触发的
      var _self = this;
      if ($(window.event.target).is('.jsTagDelete')) {
        return;
      }

      var mailboxUuid = row['UUID'];
      var status = row['STATUS'];

      var url =
        ctx +
        '/web/app/pt-app/pt-webmail/pt-webmail-openmail.html?pageUuid=9a6037a5-52fe-4d82-86c0-1d068a6c0b51&mailboxUuid=' +
        mailboxUuid;
      //草稿状态，则打开继续发送界面
      if (status == '0') {
        if (row['SEND_TIME'] == null && row['SEND_STATUS'] == null) {
          url = ctx + '/web/app/pt-app/pt-webmail/pt-webmail-write.html?boxname=DRAFT&mailboxUuid=' + mailboxUuid;
        }
      }

      //刷新头部徽章数量
      window.open(url);
      this.refreshBadgeNum();

      //未读图标触发修改
      var $parentTr = $(window.event.target).parents('tr');
      if ($parentTr.length > 0) {
        var $notReadIcon = $parentTr.find('.mail_not_read');
        if ($notReadIcon.length > 0) {
          $notReadIcon.removeClass('mail_not_read').addClass('mail_readed');
          var count = parseInt(_self.widget.element.find('.btn-filterUnreadMail .badge').text());
          _self.widget.element.find('.btn-filterUnreadMail .badge').text(--count);
        }
      }
    },

    //加载用于“移动到”按钮组内的文件夹按钮
    loadMoveFolderButtonGroup: function (refresh) {
      server.JDS.call({
        service: 'wmMailFolderFacadeService.queryUserFolders',
        data: [],
        mask: false,
        success: function (result) {
          var $targetLi = $('.li_class_addFolderAndMoveIn');
          if ($targetLi.length == 0) {
            return;
          }
          if (refresh) {
            $('.js-moveToOwnFolder').remove();
          }

          $targetLi.parent().css('overflow-y', 'scroll');

          $targetLi.addClass('split_li');
          if (result.data) {
            for (var i = 0, len = result.data.length; i < len; i++) {
              var folderData = result.data[i];
              var $li = $('<li>', {
                class: 'js-moveToOwnFolder',
                folderCode: folderData.folderCode
              }).append(
                $('<a>', {
                  href: '#'
                }).text(folderData.folderName)
              );
              $li.insertBefore($targetLi);
            }
          }
        },
        error: function (jqXHR) {}
      });
    },

    //加载用于“标记为”按钮组内的标签按钮
    loadTagMailButtonGroup: function (refresh) {
      server.JDS.call({
        service: 'wmMailTagFacadeService.queryUserMailTags',
        data: [],
        mask: false,
        success: function (result) {
          var $targetLi = $('.li_class_tags');
          if ($targetLi.length == 0) {
            return;
          }
          if (refresh) {
            //已加载过按钮组，重新刷新
            $('.jsTagButtonsDiv').remove();
          } else {
            $targetLi.find('a').append(
              $('<span>', {
                class: 'iconfont icon-ptkj-xianmiaojiantou-you',
                style: 'float:right;'
              })
            );
          }

          var $div = $('<div>', {
            class: 'jsTagButtonsDiv tagButtonDiv'
          });
          var $buttonUl = $('<ul>', {
            class: 'dropdown-menu show',
            role: 'menu'
          });
          $div.append($buttonUl);
          $targetLi.append($div);
          if (result.data) {
            for (var i = 0, len = result.data.length; i < len; i++) {
              var tagData = result.data[i];
              var $li = $('<li>', {
                class: 'js-markMailTag',
                tagUuid: tagData.uuid
              }).append(
                $('<a>', {
                  href: '#'
                }).html('<span class="glyphicon glyphicon-stop" style="color:' + tagData.tagColor + ';"></span>' + tagData.tagName)
              );
              $buttonUl.append($li);
              // $li.insertBefore($targetLi);
            }
            $buttonUl.append(
              $('<li>', {
                class: 'li_class_addTagAndMark split_li'
              }).append(
                $('<a>', {
                  href: '#'
                }).text('新建标签并标记')
              )
            );
          }
          //$(".dropdown-menu").css('overflow','initial');
        },
        error: function (jqXHR) {}
      });
    },

    //新建文件夹并移动选中的邮件
    addFolderAndMoveIn: function () {
      var _self = this;
      var selections = _self.getSelections();
      if (selections.length == 0) {
        appModal.alert('请先勾选需要移动的邮件');
        return;
      }
      var emailUuids = [];
      for (var select in selections) {
        emailUuids.push(selections[select].UUID);
      }
      this.popAddFolderDialog(emailUuids);
    },

    //移动邮件到指定文件夹
    moveEmailToFolder: function (folder, emailUuids, successCallback) {
      var _self = this;
      server.JDS.call({
        service: 'wmWebmailService.updateMailBoxName',
        data: [folder, emailUuids],
        success: function (result) {
          if ($.isFunction(successCallback)) {
            successCallback();
            return;
          }
          appModal.info('移动邮件到文件夹成功');
          _self.refresh();
          _self.refreshBadgeNum();
        }
      });
    },

    //弹出新建文件夹的弹窗
    popAddFolderDialog: function (emailUuids) {
      var _self = this;

      var folderDev = new AppEmailFolderDevelopment();
      folderDev.popDialog(
        null,
        {
          title: '新建文件夹并移动'
        },
        function (folderCode) {
          _self.moveEmailToFolder(folderCode, emailUuids);
          folderDev.refreshLeftSideMenus({
            expand: false
          });
          _self.loadMoveFolderButtonGroup(true);
        }
      );
    },

    //逻辑删除
    logicDelEmail: function () {
      var mailboxUuids = this.getSelectedRowIds();
      if (mailboxUuids.length == 0) {
        appModal.alert('请选择记录！');
      } else {
        var _self = this;
        appModal.confirm('确认要删除吗?', function (result) {
          if (result) {
            server.JDS.call({
              service: 'wmWebmailService.delete',
              data: [mailboxUuids],
              async: false,
              success: function (result) {
                appModal.success('删除成功');
                _self.refresh();
                _self.refreshBadgeNum();
              },
              error: function (jqXHR) {
                appModal.alert('删除失败!');
              }
            });
          }
        });
      }
    },

    //彻底删除
    physicsDelEmail: function () {
      var mailboxUuids = this.getSelectedRowIds();
      if (mailboxUuids.length == 0) {
        appModal.alert('请选择记录！');
      } else {
        var _self = this;
        console.log(mailboxUuids);
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

    //转发
    forwardEmail: function () {
      var mailboxUuids = this.getSelectedRowIds();
      if (mailboxUuids.length == 0) {
        appModal.alert('请选择记录！');
      } else if (mailboxUuids.length != 1) {
        appModal.alert('一次只能选择一条记录！');
      } else {
        var transferUrl = ctx + '/web/app/pt-app/pt-webmail/pt-webmail-write.html?transfer=true&mailboxUuid=' + mailboxUuids[0];
        window.open(transferUrl);
      }
    },

    //回复
    replyEmail: function () {
      var mailboxUuids = this.getSelectedRowIds();
      if (mailboxUuids.length == 0) {
        appModal.alert('请选择记录！');
      } else if (mailboxUuids.length != 1) {
        appModal.alert('一次只能选择一条记录！');
      } else {
        var replyUrl = ctx + '/web/app/pt-app/pt-webmail/pt-webmail-write.html?reply=true&mailboxUuid=' + mailboxUuids[0];
        window.open(replyUrl);
      }
    },

    //回复全部
    replayAllEmail: function () {
      var mailboxUuids = this.getSelectedRowIds();
      if (mailboxUuids.length == 0) {
        appModal.alert('请选择记录！');
      } else if (mailboxUuids.length != 1) {
        appModal.alert('一次只能选择一条记录！');
      } else {
        var replyAllUrl = ctx + '/web/app/pt-app/pt-webmail/pt-webmail-write.html?replyAll=true&mailboxUuid=' + mailboxUuids[0];
        window.open(replyAllUrl);
      }
    },

    //标记邮件
    markMailByTag: function (emailUuids, tagUuid, successCallback) {
      var _self = this;
      server.JDS.call({
        service: 'wmMailTagFacadeService.addMailRelaTag',
        data: [tagUuid, emailUuids],
        async: false,
        success: function (result) {
          if ($.isFunction(successCallback)) {
            successCallback();
            return;
          }
          _self.loadTableMailTags(emailUuids);
          appModal.info('标记成功');
        },
        error: function (jqXHR) {
          appModal.alert('标记失败!');
        }
      });
    },

    //移除邮件的相关标签
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

    //已读邮件
    markReaded: function () {
      var _self = this;
      var emailUuids = this.getSelectedRowIds();
      if (emailUuids.length == 0) {
        appModal.alert('请先勾选需要标记为已读的邮件');
        return;
      }
      JDS.restfulPost({
        url: ctx + '/proxy/api/mail/manager/updateMailReadStatus',
        data: {
          mailboxUuids: emailUuids.join(','),
          readStatus: '1'
        },
        async: false,
        contentType: 'application/x-www-form-urlencoded',
        success: function (result) {
          if (result.code == 0) {
            appModal.info('标记已读成功');
            _self.refresh();
          } else {
            appModal.alert('标记已读失败!');
          }
        }
      });
    },

    //未读邮件
    markUnReaded: function () {
      var _self = this;
      var emailUuids = this.getSelectedRowIds();
      if (emailUuids.length == 0) {
        appModal.alert('请先勾选需要标记为未读的邮件');
        return;
      }
      JDS.restfulPost({
        url: ctx + '/proxy/api/mail/manager/updateMailReadStatus',
        data: {
          mailboxUuids: emailUuids.join(','),
          readStatus: '0'
        },
        async: false,
        contentType: 'application/x-www-form-urlencoded',
        success: function (result) {
          if (result.code == 0) {
            appModal.info('标记未读成功');
            _self.refresh();
          } else {
            appModal.alert('标记未读失败!');
          }
        }
      });
    },

    //移除选中的邮件的所有标签
    removeSelectedMailTags: function () {
      var emailUuids = this.getSelectedRowIds();
      if (emailUuids.length == 0) {
        appModal.alert('请先勾选需要移除标签的邮件');
        return;
      }
      this.removeMailTags(emailUuids);
    },

    // 新建标签并进行邮件标记
    addTagAndMark: function () {
      var _self = this;
      var emailUuids = this.getSelectedRowIds();
      if (emailUuids.length == 0) {
        appModal.alert('请先勾选需要标记的邮件');
        return;
      }
      var tagDev = new AppEmailTagDevelopment();
      tagDev.popDialog(
        null,
        {
          title: '新建标签并标记'
        },
        function (tagUuid) {
          _self.markMailByTag(emailUuids, tagUuid);
          tagDev.refreshLeftSideMenus({
            expand: false
          });
          _self.loadTagMailButtonGroup(true);
        }
      );
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
     * 收取外部邮箱邮件
     * @param event
     * @param opt
     */
    fetchOuterMail: function (event, opt) {
      var _self = this;
      /* $.get(ctx + "/webmail/syncOtherMailAcountMessages",
                     {
                         type: 0, mailAddress: _self.mailAddress
                     },
                     function (res, msg, object) {
                         debugger;
                         if (object.status == 200) {
                             _self.refresh();
                         }
                     }
                 );*/
      $.ajax({
        type: 'GET',
        url: ctx + '/proxy/webmail/syncOtherMailAcountMessages',
        data: {
          type: 0,
          mailAddress: _self.mailAddress
        },
        dataType: 'json',
        contentType: 'application/json',
        success: function (res) {
          if (res > 0) {
            _self.refresh();
            appModal.info('您有' + res + '封新邮件');
          }
        },
        async: true
      });

      appModal.info('后端服务同步中');
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
    }
  });

  return AppEmailListDevelopment;
});
