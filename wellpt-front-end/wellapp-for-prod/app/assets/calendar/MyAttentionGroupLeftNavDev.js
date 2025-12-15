define(['constant', 'commons', 'server', 'LeftSidebarWidgetDevelopment', 'appModal', 'multiOrg'], function (
  constant,
  commons,
  server,
  LeftSidebarWidgetDevelopment,
  appModal
) {
  var JDS = server.JDS;
  var currUser = server.SpringSecurityUtils.getUserDetails();
  // 页面组件二开基础
  var MyAttentionGroupLeftNavDev = function () {
    LeftSidebarWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(MyAttentionGroupLeftNavDev, LeftSidebarWidgetDevelopment, {
    //取消关注图标事件
    initAttentionEditIconEvent: function () {
      var _self = this;
      var menus = this.getWidget();
      //定义编辑日历本事件
      $(menus.element)
        .find('.sidebar-nav-item-icon')
        .each(function () {
          $(this).click(function () {
            var $menuItem = $(this).parents('.mainnav-menu-item');
            var menuId = $menuItem.attr('menuid');
            var g = _self.widget.menuItemMap[menuId].group;
            var $dialog = appModal.dialog({
              title: '详情',
              message: _self._getAttentinDialogHtml(),
              shown: function (_$dialog) {
                _$dialog.find('#groupName').val(g.groupName);
                _$dialog.find('#groupMembers').val(g.groupMembers);
                _$dialog.find('#groupMembersName').val(g.groupMembersName);
                _$dialog.find('#groupMembersName').click(function () {
                  $.unit2.open({
                    valueField: 'groupMembers',
                    labelField: 'groupMembersName',
                    title: '选择人员',
                    type: 'all',
                    multiple: true,
                    selectTypes: 'U',
                    valueFormat: 'justId',
                    excludeValues: [currUser.userId]
                  });
                });
              },
              buttons: {
                saveGroup: {
                  label: '保存群组',
                  className: 'btn btn-primary',
                  callback: function () {
                    var groupMembers = $dialog.find('#groupMembers').val();
                    var groupMembersName = $dialog.find('#groupMembersName').val();
                    var values = groupMembers ? groupMembers.split(';') : [];
                    var labels = groupMembersName ? groupMembersName.split(';') : [];
                    var groupName = $dialog.find('#groupName').val();
                    if (commons.StringUtils.isBlank(groupName)) {
                      _self._showErr($dialog, 'groupName', '组名不能为空');
                      return false;
                    } else if (labels.length == 0) {
                      _self._showErr($dialog, 'groupMembersName', '组成员不能为空');
                      return false;
                    } else {
                      _self._modifyGroupMembers(g.uuid, values, labels, groupName);
                    }
                  }
                },
                cancelAttention: {
                  label: '取消关注',
                  className: 'btn btn-primary',
                  callback: function () {
                    appModal.confirm('确定取消关注？', function (result) {
                      if (result) {
                        _self._cancelAttention(g.uuid);
                      }
                    });
                  }
                },
                cancel: {
                  label: '取消',
                  className: 'btn btn-default'
                }
              }
            });

            return false;
          });
        });
    },

    //变更组员信息
    _modifyGroupMembers: function (groupUuid, groupMembers, groupMembersName, groupName) {
      var _self = this;
      server.JDS.call({
        service: 'attentionFacade.modifyGroupMembers',
        data: [groupUuid, groupMembers, groupMembersName, groupName],
        success: function (result) {
          //刷新菜单
          _self.refresh();
        }
      });
    },

    _cancelAttention: function (groupUuid) {
      var _self = this;
      server.JDS.call({
        service: 'attentionFacade.cancelAttentionGroup',
        data: [groupUuid],
        success: function (result) {
          if (result && result.success) {
            appModal.success('取消成功', function () {
              _self.refresh();
            });
          }
        }
      });
    },
    // 组件初始化
    init: function () {
      var _self = this;
      var menus = this.getWidget();
      //初始化左侧菜单栏上面的添加关注按钮
      _self._initAddAttentionButton();

      //如果不存在则添加提示语
      //默认选中第一个
      /*var $firstMenu = $(menus.element).find(".mainnav-menu-item").first();
			if($firstMenu.length==0){
				$(menus.element).append("暂无关注,请添加");
			}*/
    },

    _getAttentinDialogHtml: function () {
      var $table = $("<table class='table table-border'>");
      var $nameTr = $("<tr><td>群组名称</td><td><input type='text' id='groupName' class='form-control'></td></tr>");
      var $memberTr = $('<tr><td>群组成员</td><td></td></tr>');
      $memberTr.find('td:eq(1)').append("<input type='text' id='groupMembersName' class='form-control'>");
      $memberTr.find('td:eq(1)').append("<input type='hidden' id='groupMembers' class='form-control'>");
      $table.append($nameTr, $memberTr);
      return $table;
    },

    _showErr: function ($dialog, eleId, errMsg) {
      var $td = $dialog.find('#' + eleId).parents('td');
      var $err = $td.find('.error');
      if ($err.length == 0) {
        $err = $("<div class='error' style='color:red'>");
        $err.html(errMsg);
        $td.append($err);
      } else {
        $err.html(errMsg);
      }
    },

    _initAddAttentionButton: function () {
      var _self = this;
      $('button.js-add-attention-group').click(function () {
        var $dialog = appModal.dialog({
          title: '添加群组',
          message: _self._getAttentinDialogHtml(),
          shown: function (_$dialog) {
            _$dialog.find('#groupMembersName').click(function () {
              $.unit2.open({
                valueField: 'groupMembers',
                labelField: 'groupMembersName',
                title: '选择人员',
                type: 'all',
                multiple: true,
                selectTypes: 'U',
                valueFormat: 'justId',
                excludeValues: [currUser.userId]
              });
            });
            _$dialog.find('#groupName').keyup(function () {
              var groupName = $('#groupName').val();
              if (typeof groupName == 'string') {
                if (groupName.length > 200) {
                  $('#groupName').val(groupName.substr(0, 200));
                }
              }
            });
          },
          buttons: {
            ok: {
              label: '确定',
              className: 'btn btn-primary',
              callback: function () {
                var groupName = $dialog.find('#groupName').val();
                var groupMembers = $dialog.find('#groupMembers').val();
                var groupMembersName = $dialog.find('#groupMembersName').val();
                if (commons.StringUtils.isBlank(groupName)) {
                  _self._showErr($dialog, 'groupName', '组名不能为空');
                  return false;
                } else if (groupName.length > 200) {
                  _self._showErr($dialog, 'groupName', '最多不可超过200个字');
                  return false;
                } else if (commons.StringUtils.isBlank(groupMembersName)) {
                  _self._showErr($dialog, 'groupMembersName', '组成员不能为空');
                  return false;
                } else {
                  _self._addAttentionGroup(groupName, groupMembers, groupMembersName);
                }
              }
            },
            cancel: {
              label: '取消',
              className: 'btn btn-default'
            }
          }
        });
      });
    },

    _addAttentionGroup: function (groupName, groupMembers, groupMembersName) {
      var _self = this;
      server.JDS.call({
        service: 'attentionFacade.addAttentionGroup',
        data: [groupName, groupMembers.split(';'), groupMembersName.split(';')],
        success: function (result) {
          if (result && result.success) {
            appModal.success('添加关注成功', function () {
              _self.refresh();
            });
          }
        }
      });
    },

    afterRenderView: function (event, params) {
      var _self = this;
      //初始化关注图标的事件
      _self.initAttentionEditIconEvent();
    }
  });
  return MyAttentionGroupLeftNavDev;
});
