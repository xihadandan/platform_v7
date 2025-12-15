define(['constant', 'commons', 'server', 'HtmlWidgetDevelopment'], function (constant, commons, server, HtmlWidgetDevelopment) {
  // 页面组件二开基础
  var AppMsgCategoryDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppMsgCategoryDevelopment, HtmlWidgetDevelopment, {
    // 组件初始化
    init: function () {
      getList();
      $('#addMsgCategory')
        .off()
        .on('click', function () {
          openDialog('新建消息分类');
        });

      $('#searchMsgCategory')
        .off()
        .on('click', function () {
          var val = $('#fullName').val();
          getList(val);
        });
      $('#fullName')
        .off()
        .on('keyup', function () {
          if ($(this).val() != '') {
            $('#deleteMsgCategory').show();
          }
        })
        .on('keypress', function (e) {
          if (e.keyCode == 13) {
            getList($(this).val());
          }
        });

      $('#deleteMsgCategory')
        .off()
        .on('click', function () {
          $('#fullName').val('');
          $(this).hide();
          getList();
        });

      $('#msg_category_content')
        .off()
        .on('click', '.hasList', function () {
          if ($(this).hasClass('icon-ptkj-shixinjiantou-you')) {
            $(this).removeClass('icon-ptkj-shixinjiantou-you').addClass('icon-ptkj-shixinjiantou-xia');
            $('#msg_category_tree').slideDown();
          } else {
            $(this).removeClass('icon-ptkj-shixinjiantou-xia').addClass('icon-ptkj-shixinjiantou-you');
            $('#msg_category_tree').slideUp();
          }
        });

      $('.msg-category-folder')
        .off()
        .on('click', function () {
          var width = $(this).parents('.msg-content-list').width();
          if ($(this).find('i').hasClass('icon-ptkj-youshouzhan')) {
            $(this)
              .parents('.msg-content-list')
              .find('.ui-wBootstrapTable')
              .css({
                width: width - 310 + 'px'
              });
            $(this)
              .parents('.msg-content-list')
              .find('.ui-wHtml')
              .animate(
                {
                  width: '230px'
                },
                300
              )
              .find('.msg-category-wrap')
              .show();
            $(this).parents('.msg-category-content').animate(
              {
                width: '220px'
              },
              0
            );
            $(this).find('i').removeClass('icon-ptkj-youshouzhan').addClass('icon-ptkj-zuoshouzhan');
          } else {
            $(this)
              .parents('.msg-content-list')
              .find('.ui-wHtml')
              .animate(
                {
                  width: '0'
                },
                300
              )
              .find('.msg-category-wrap')
              .hide();
            $(this).parents('.msg-category-content').animate(
              {
                width: '0'
              },
              300
            );
            $(this)
              .parents('.msg-content-list')
              .find('.ui-wBootstrapTable')
              .animate(
                {
                  width: width - 50 + 'px'
                },
                400
              );
            $(this).find('i').addClass('icon-ptkj-youshouzhan').removeClass('icon-ptkj-zuoshouzhan');
          }
        });

      $('#msgAllCategory')
        .off()
        .on('click', function () {
          $('.msg-category-item').removeClass('hasSelectCate');
          var msgFormateTable = $('#msg_category_content').parents('.msg-content-list').find('.ui-wBootstrapTable').attr('id');
          $('#' + msgFormateTable)
            .wBootstrapTable('removeParam', 'classifyUuid')
            .wBootstrapTable('refresh');
        });

      $('#msg_category_tree')
        .off()
        .on('click', '.msg-category-item', function (e) {
          e.stopPropagation();
          $(this).addClass('hasSelectCate').siblings().removeClass('hasSelectCate');
          var uuid = $(this).data('uuid');
          var msgFormateTable = $('#msg_category_content').parents('.msg-content-list').find('.ui-wBootstrapTable').attr('id');
          if (uuid == 'all') {
            $('#' + msgFormateTable)
              .wBootstrapTable('removeParam', 'classifyUuid')
              .wBootstrapTable('refresh');
          } else {
            $('#' + msgFormateTable)
              .wBootstrapTable('addParam', 'classifyUuid', uuid)
              .wBootstrapTable('refresh');
          }
        });

      function getList(val) {
        $.ajax({
          url: ctx + '/proxy/api/message/classify/queryList',
          type: 'get',
          data: {
            name: val
          },
          success: function (result) {
            if (result.code === 0) {
              var data = result.data;
              var lis = '';
              for (var i = 0; i < data.length; i++) {
                var icon = data[i].icon ? data[i].icon : 'iconfont icon-xmch-wodexiaoxi';
                var background = data[i].iconBg ? data[i].iconBg : '#64B3EA';
                lis +=
                  "<li class='msg-category-item' data-uuid='" +
                  data[i].uuid +
                  "' title='" +
                  (data[i].note || '') +
                  "'>" +
                  "<span class='" +
                  icon +
                  "' style='background:" +
                  background +
                  "'></span>" +
                  "<span class='msg-category-text'>" +
                  data[i].name +
                  '</span>' +
                  "<i class='iconfont icon-ptkj-gengduocaozuo icon-operate'></i>" +
                  "<ul class='category-item-operate'>" +
                  "<li class='btn-edit'><i class='iconfont icon-ptkj-bianji'></i>编辑</li>" +
                  "<li class='btn-delete'><i class='iconfont icon-ptkj-shanchu'></i>删除</li>" +
                  '</ul>' +
                  '</li>';
              }

              if (data.length > 0) {
                if ($('.icon-folders').hasClass('icon-ptkj-shixinjiantou-you')) {
                  $('.icon-folders').addClass('hasList icon-ptkj-shixinjiantou-xia').removeClass('icon-ptkj-shixinjiantou-you');
                  $('#msg_category_tree').slideDown();
                }
              }

              $('#msg_category_tree').html(lis);
            }
          }
        });
      }

      function getDetail(bean, uuid) {
        $.ajax({
          url: ctx + '/proxy/api/message/classify/getOne',
          type: 'get',
          data: {
            uuid: uuid
          },
          success: function (result) {
            if (result.code === 0) {
              bean = result.data;
              $('#messageForm').json2form(bean);
              $('#msgIconShow').addClass(bean.icon).css({
                background: bean.iconBg
              });
              $('#selectIconBg').find('div').attr('data-color', bean.iconBg).css({
                background: bean.iconBg
              });
              if (bean.isEnable == 1) {
                $('.switch-wrap').addClass('active');
              } else {
                $('.switch-wrap').removeClass('active');
              }
            }
          }
        });
      }

      function openDialog(title, uuid) {
        var bean = {
          uuid: uuid || '',
          name: '',
          icon: '',
          iconBg: '',
          code: '',
          isEnable: '',
          note: ''
        };
        var message = getDialogContent();
        var options = {
          title: title,
          message: message,
          size: 'middle',
          zIndex: 100,
          buttons: {
            ok: {
              label: '保存',
              className: 'well-btn w-btn-primary',
              callback: function () {
                $('#messageForm').form2json(bean);
                if (bean.name == '') {
                  appModal.error('分类名称不能为空！');
                  return false;
                }
                $.ajax({
                  url: ctx + '/proxy/api/message/classify/saveOrupdateClassify',
                  type: 'post',
                  data: JSON.stringify(bean),
                  contentType: 'application/json',
                  success: function (result) {
                    if (result.code === 0) {
                      appModal.success('保存成功');
                      getList();
                    } else {
                      appModal.error(result.msg);
                    }
                  },
                  error: function (err) {
                    appModal.error({
                      message: err.responseJSON.msg,
                      timer: 3000
                    });
                  }
                });
              }
            },
            cancel: {
              label: '关闭',
              className: 'well-btn btn-default'
            }
          },
          shown: function () {
            if (uuid) {
              getDetail(bean, uuid);
            }

            // 开关按钮
            $('.switch-wrap')
              .off()
              .on('click', function () {
                if ($(this).hasClass('active')) {
                  $(this).removeClass('active');
                  $('#isEnable').val(0);
                } else {
                  $(this).addClass('active');
                  $('#isEnable').val(1);
                }
              });

            // 选择图标
            $('#addIcon')
              .off()
              .on('click', function () {
                $.WCommonPictureLib.show({
                  selectTypes: [3],
                  confirm: function (data) {
                    var fileIDs = data.fileIDs;
                    $('#icon').val(fileIDs);
                    $('#msgIconShow').attr('iconClass', fileIDs);
                    $('#msgIconShow').attr('class', fileIDs);
                  }
                });
              });

            var items = initBgColor();
            $('.bg-choose-box').append(items);

            // 色块选择
            $('.bg-choose-item', '.icon-bg-wrap')
              .off()
              .on('click', function () {
                var color = $(this).data('color');
                $(this).addClass('hasChoose').siblings().removeClass('hasChoose');
                $('#iconBg').val(color);
                $('#selectIconBg').find('div').data('color', color).css('background', color);
                $('#msgIconShow').css({
                  background: color
                });
              });
            // 关闭色块
            $(document).on('click', function (e) {
              if (!$(e.target).hasClass('icon-bg-wrap') && !$(e.target).parents().hasClass('icon-bg-wrap')) {
                $('.bg-choose-list').hide();
                $('.minicolors').hide();
              }
            });
            // 显示色块
            $('#selectIconBg')
              .off()
              .on('click', function () {
                var currColor = $(this).find('div').data('color');
                $('.bg-choose-list').css('display', 'inline-block');
                var chooseItem = $('.bg-choose-list').find('.bg-choose-item');
                chooseItem.removeClass('hasChoose');
                $.each(chooseItem, function (index, item) {
                  if ($(item).data('color') == currColor) {
                    $(item).addClass('hasChoose');
                  }
                });
              });
            // 更多
            $('.bg-choose-more', '.icon-bg-wrap')
              .off()
              .on('click', function () {
                $('.bg-choose-list').hide();
                if ($('.minicolors').size() > 0) {
                  $('.minicolors').show();
                } else {
                  var opacity = false;
                  $('#iconBg').minicolors({
                    control: 'hue',
                    format: 'hex',
                    color: '#0070C0',
                    letterCase: 'lowercase',
                    opacity: opacity,
                    position: 'bottom left',
                    theme: 'bootstrap',
                    change: function (value, opacity) {
                      $('#iconBg').focus();
                      $('#selectIconBg').find('div').data('color', value).css('background', value);
                      $('#msgIconShow').css({
                        background: value
                      });
                    },
                    hide: function () {
                      $('#iconBg').hide();
                      $('.icon-bg-wrap').find('.minicolors').hide();
                    },
                    show: function () {
                      $('#iconBg').focus();
                    }
                  });
                  $('#iconBg').focus();
                  if (!opacity) {
                    $('.minicolors-input-swatch').hide();
                  }
                }
              });
          }
        };
        appModal.dialog(options);
      }

      function getDialogContent() {
        var html = '';
        html +=
          "<form id='messageForm' class='dyform'>" +
          "<table class='well-form form-horizontal'>" +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'><font style='color:#f00;'>*</font>分类名称</td>" +
          "<td><input type='text' name='name' id='name' class='form-control' placeholder='分类名称'/></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>图标</td>" +
          '<td>' +
          "<input type='hidden' name='icon' id='icon'/>" +
          "<span id='msgIconShow' style='margin-right: 5px;' style='background: transparent'></span>" +
          "<button id='addIcon' type='button' class='well-btn w-btn-primary' style='vertical-align: top;'>选择图标</button>" +
          "<div class='icon-bg-wrap'>" +
          "<div class='currBg' id='selectIconBg' data-color=''><div></div></div>" +
          "<div class='bg-choose-list'>" +
          "<ul class='bg-choose-box'></ul>" +
          "<div class='bg-choose-more'>更多<i></i></div>" +
          '</div>' +
          "<input type='hidden' name='iconBg' id='iconBg'/>" +
          '</div>' +
          '</td>' +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>编号</td>" +
          "<td><input type='text' name='code' id='code' class='form-control' placeholder='编号'/></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>启用分类</td>" +
          "<td><input type='hidden' name='isEnable' id='isEnable' value='1'/><div class='switch-wrap active'><span class='switch-radio'></span></div></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>描述</td>" +
          "<td><textarea name='note' id='note' cols='30' rows='5' class='form-control'></textarea></td>" +
          '</tr>' +
          '</table>' +
          '</form>';

        return html;
      }

      function initBgColor() {
        var colors = [
          '#64B3EA',
          '#FCB862',
          '#92D678',
          '#F38F8A',
          '#9584EE',
          '#44C8E5',
          '#E28DE9',
          '#65DEAA',
          '#FE88A7',
          '#77A8EE',
          '#D9D688',
          '#F6ABBB'
        ];
        var items = '';
        // 渲染背景颜色
        $.each(colors, function (index, item) {
          items +=
            '<li class="bg-choose-item" data-color="' +
            item +
            '" style="background: ' +
            item +
            '"><i class="iconfont icon-ptkj-dagou"></i></li>';
        });

        return items;
      }

      $('#msg_category_tree').on('click', '.btn-edit', function (e) {
        e.stopPropagation();
        var uuid = $(this).parents('.msg-category-item').data('uuid');
        openDialog('编辑消息分类', uuid);
      });

      $('#msg_category_tree').on('click', '.btn-delete', function (e) {
        e.stopPropagation();
        var uuid = $(this).parents('.msg-category-item').data('uuid');
        appModal.confirm('确定删除分类吗', function (success) {
          if (success) {
            $.ajax({
              url: ctx + '/proxy/api/message/classify/delClassifys',
              type: 'post',
              data: JSON.stringify([uuid]),
              contentType: 'application/json',
              success: function (result) {
                if (result.code === 0) {
                  appModal.success('删除成功');
                  getList();
                }
              }
            });
          }
        });
      });
    },
    refresh: function () {
      this.init();
    }
  });
  return AppMsgCategoryDevelopment;
});
