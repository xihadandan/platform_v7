define(['constant', 'commons', 'server', 'appModal', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appModal,
  HtmlWidgetDevelopment
) {
  // 平台管理_产品集成_角色详情_HTML组件二开
  var AppUserTypeRoleDetailsWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };

  var USER_TYPE = {
    INDIVIDUAL: '个人用户',
    LEGAL_PERSION: '法人用户'
  };

  // 接口方法
  commons.inherit(AppUserTypeRoleDetailsWidgetDevelopment, HtmlWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;

      _self.renderPage();
      _self.getRoleMetadata();
      _self._bindEvents();
    },

    getRoleMetadata: function () {
      var _this = this;
      var $container = $(this.widget.element);
      server.JDS.call({
        service: 'roleService.queryRoleDtosBySystemUnitId',
        data: [server.SpringSecurityUtils.getCurrentUserUnitId()],
        success: function (result) {
          if (result.success) {
            _this.roles = result.data;
            var $ul = $('.ul_waitselect', $container);
            for (var i = 0; i < $ul.length; i++) {
              var li = [];
              var t = $($ul[i]).attr('type');
              for (var j = 0; j < _this.roles.length; j++) {
                li.push(
                  $('<li>', { roleid: _this.roles[j].id, title: _this.roles[j].name.toLowerCase() }).append(
                    $('<input>', {
                      type: 'checkbox',
                      id: t + j,
                      class: 'role_checkbox',
                      value: _this.roles[j].id
                    }),
                    $('<label>', { for: t + j }).text(_this.roles[j].name)
                  )
                );
              }
              $($ul[i]).append(li);
            }
            for (var k in USER_TYPE) {
              (function (type) {
                server.JDS.call({
                  service: 'userTypeRoleService.getUserTypeRoles',
                  data: [type],
                  success: function (res) {
                    if (res.success) {
                      var _ul = $container.find(".ul_waitselect[type='" + type + "']");
                      for (var r = 0; r < res.data.length; r++) {
                        _ul
                          .find("[roleid='" + res.data[r] + "'] :input")
                          .prop('checked', true)
                          .trigger('change');
                      }
                    }
                  }
                });
              })(k);
            }
          }
        }
      });
    },

    renderPage: function () {
      var $container = $(this.widget.element);
      var tabHeadHtml = $('#userTypeRoleTemplate', $container).find('[role=presentation]')[0].outerHTML;
      var tabPanelHtml = $('#userTypeRoleTemplate', $container).find('[role=tabpanel]')[0].outerHTML;
      var active = 'active';
      for (var k in USER_TYPE) {
        var html = new commons.StringBuilder();
        html.appendFormat(tabHeadHtml, k.toLowerCase(), USER_TYPE[k], active);
        $container.find('.nav-tabs').append(html.toString());
        html = new commons.StringBuilder();
        html.appendFormat(tabPanelHtml, k.toLowerCase(), active);
        $container.find('.tab-content').append(html.toString());
        active = '';
      }
    },

    _bindEvents: function () {
      var _self = this;
      var $container = $(this.widget.element);
      $('#btn_save_user_type_role', $container)
        .off()
        .on('click', function () {
          _self.saveUserTypeRole();
        });
      $container.on('change', '.role_checkbox', function () {
        var checked = $(this).prop('checked');
        var _value = $(this).val();
        var _label = $(this).next().text();
        var _id = $(this).attr('id');
        if (checked) {
          $(this)
            .parents('.row')
            .find('.ul_selected')
            .append(
              $('<li>', { roleid: _value }).append(
                $('<span>').text(_label).attr('title', _label),
                $('<label>', { class: 'icon iconfont icon-ptkj-dacha-xiao', for: _id })
              )
            );
        } else {
          $(this)
            .parents('.row')
            .find('.ul_selected')
            .find("[roleid='" + _value + "']")
            .remove();
        }
      });

      $container.on('click', '.role-qry-close-icon', function () {
        $(this).prev().val('');
        $(this).parents('.keyword_search_toolbar').find('.btn-query').click();
      });

      $container.on('click', '.btn-query', function () {
        var val = $(this).parents('.keyword_search_toolbar').find('.role-qry-input').val().toLowerCase();
        var $lis = $(this).parents('.tab-pane').find('.ul_waitselect').find('li');
        if (val == '') {
          $lis.show();
        } else {
          for (var i = 0; i < $lis.length; i++) {
            var title = $($lis[i]).attr('title');
            if (title.indexOf(val) == -1) {
              $($lis[i]).hide();
            }
          }
        }
      });
    },

    collectUserTypeRoles: function () {
      var $container = $(this.widget.element);
      var data = {};
      for (var k in USER_TYPE) {
        data[k] = [];
        var $lis = $('#' + k.toLowerCase() + '_ul_selected', $container).find('li');
        for (var i = 0; i < $lis.length; i++) {
          data[k].push($($lis[i]).attr('roleid'));
        }
      }
      return data;
    },

    saveUserTypeRole: function () {
      var _self = this;
      var $container = $(this.widget.element);
      var data = _self.collectUserTypeRoles();
      for (var k in data) {
        if (data[k].length == 0) {
          appModal.error(USER_TYPE[k] + ', 请选择角色! ');
          throw new Error('请选择角色');
        }
      }

      server.JDS.call({
        service: 'userTypeRoleService.saveUserTypeRole',
        data: [data],
        success: function (result) {
          if (result.success) {
            appModal.success('保存成功! ');
          }
        }
      });
    }
  });
  return AppUserTypeRoleDetailsWidgetDevelopment;
});
