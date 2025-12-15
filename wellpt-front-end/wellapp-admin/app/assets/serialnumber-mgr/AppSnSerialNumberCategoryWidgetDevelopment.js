define(['constant', 'commons', 'server', 'HtmlWidgetDevelopment'], function (constant, commons, server, HtmlWidgetDevelopment) {
  var JDS = server.JDS;
  // 页面组件二开基础
  var AppSnSerialNumberCategoryWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppSnSerialNumberCategoryWidgetDevelopment, HtmlWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var $container = this.widget.element;
      var $parentContainer = $container.closest('.ui-wBootgrid');
      setTimeout(function () {
        // 移除!important标记的样式类
        $parentContainer
          .children('.col-idx-1')
          .removeClass('custom-column')
          .width($parentContainer.width() - 230);
        $parentContainer.children('.col-idx-0').removeClass('custom-column').width('220px');
      }, 200);
      getList();
      $('#addSerialNumberCategory', $container)
        .off()
        .on('click', function () {
          openDialog('新建流水号分类');
        });

      $('#searchSerialNumberCategory', $container)
        .off()
        .on('click', function () {
          var val = $('#categoryName', $container).val();
          getList(val);
        });
      $('#categoryName', $container)
        .off()
        .on('keyup', function () {
          if ($(this).val() != '') {
            $('#deleteSerialNumberCategory', $container).show();
          } else {
            $('#deleteSerialNumberCategory', $container).hide();
          }
        })
        .on('keypress', function (e) {
          if (e.keyCode == 13) {
            getList($(this).val());
          }
        });

      $('#deleteSerialNumberCategory', $container)
        .off()
        .on('click', function () {
          $('#categoryName', $container).val('');
          $(this).hide();
          getList();
        });

      $('#sn_category_content', $container)
        .off()
        .on('click', '.hasList', function () {
          if ($(this).hasClass('icon-ptkj-shixinjiantou-you')) {
            $(this).removeClass('icon-ptkj-shixinjiantou-you').addClass('icon-ptkj-shixinjiantou-xia');
            $('#sn_category_tree', $container).slideDown();
          } else {
            $(this).removeClass('icon-ptkj-shixinjiantou-xia').addClass('icon-ptkj-shixinjiantou-you');
            $('#sn_category_tree', $container).slideUp();
          }
        });

      $('.msg-category-folder', $container)
        .off()
        .on('click', function () {
          var width = $parentContainer.width();
          if ($(this).find('i').hasClass('icon-ptkj-youshouzhan')) {
            $container
              .animate(
                {
                  width: '230px'
                },
                300
              )
              .find('.msg-category-wrap')
              .show();
            $parentContainer.children('.col-idx-0').animate({ width: '220px' }, 300);
            $parentContainer.children('.col-idx-0').find('.msg-category-content').animate(
              {
                width: '220px'
              },
              300
            );
            $parentContainer.children('.col-idx-1').animate(
              {
                width: width - 230 + 'px'
              },
              300
            );
            $(this).find('i').removeClass('icon-ptkj-youshouzhan').addClass('icon-ptkj-zuoshouzhan');
          } else {
            $container
              .animate(
                {
                  width: '0'
                },
                300
              )
              .find('.msg-category-wrap')
              .hide();
            $parentContainer.children('.col-idx-0').animate({ width: '20px' }, 300);
            $parentContainer.children('.col-idx-0').find('.msg-category-content').animate(
              {
                width: '0'
              },
              300
            );
            $parentContainer.children('.col-idx-1').animate(
              {
                width: width - 30 + 'px'
              },
              300
            );
            $(this).find('i').addClass('icon-ptkj-youshouzhan').removeClass('icon-ptkj-zuoshouzhan');
          }
        });

      $('#snAllCategory', $container)
        .off('click')
        .on('click', function () {
          $('.msg-category-item', $container).removeClass('hasSelectCate');
          var msgFormateTable = $('#sn_category_content', $container).parents('.ui-wBootgrid').find('.ui-wBootstrapTable').attr('id');
          $('#' + msgFormateTable).wBootstrapTable('removeParam', 'categoryUuid');
          $('#' + msgFormateTable).wBootstrapTable('refresh');
        });

      $('#sn_category_tree', $container)
        .off()
        .on('click', '.msg-category-item', function (e) {
          e.stopPropagation();
          $(this).addClass('hasSelectCate').siblings().removeClass('hasSelectCate');
          var uuid = $(this).data('uuid');
          var msgFormateTable = $('#sn_category_content', $container).parents('.ui-wBootgrid').find('.ui-wBootstrapTable').attr('id');

          $('#' + msgFormateTable)
            .wBootstrapTable('addParam', 'categoryUuid', uuid)
            .wBootstrapTable('refresh');
        });

      function getList(val) {
        JDS.restfulPost({
          url: ctx + '/proxy/api/sn/serial/number/category/getAllBySystemUnitIdsLikeName',
          data: {
            name: val || ''
          },
          success: function (result) {
            if (result.success || result.code == 0) {
              var data = result.data;
              var lis = '';
              for (var i = 0; i < data.length; i++) {
                var icon = data[i].icon ? data[i].icon : 'iconfont icon-ptkj-fenlei2';
                var background = data[i].iconColor ? data[i].iconColor : '#64B3EA';
                var remark = data[i].remark || '';
                var title = '';
                if (data[i].name.length > 9 && remark != '') {
                  title = data[i].name + '\n' + remark;
                } else if (data[i].name.length <= 9 && remark != '') {
                  title = remark;
                } else if (data[i].name.length > 9 && remark == '') {
                  title = data[i].name;
                }
                lis +=
                  "<li class='msg-category-item' data-code='" +
                  data[i].code +
                  "' data-uuid='" +
                  data[i].uuid +
                  "' title='" +
                  title +
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
                if ($('.icon-folders', $container).hasClass('icon-ptkj-shixinjiantou-you')) {
                  $('.icon-folders').addClass('hasList icon-ptkj-shixinjiantou-xia').removeClass('icon-ptkj-shixinjiantou-you');
                  $('#sn_category_tree', $container).slideDown();
                }
              }

              $('#sn_category_tree', $container).html(lis);

              setTimeout(function () {
                resetSelectedItem();
              }, 300);
            }
          }
        });
      }

      function resetSelectedItem() {
        var msgFormateTable = $('#sn_category_content', $container).parents('.ui-wBootgrid').find('.ui-wBootstrapTable').attr('id');
        var $table = $('#' + msgFormateTable);
        if ($table.length) {
          var selectedCategoryUuid = $table.wBootstrapTable('getParam', 'categoryUuid');
          if (typeof selectedCategoryUuid === 'string') {
            $('#sn_category_content .msg-category-item[data-uuid="' + selectedCategoryUuid + '"]', $container).addClass('hasSelectCate');
          }
        }
      }

      function getDetail(bean, uuid, $dialog) {
        JDS.restfulGet({
          url: ctx + '/proxy/api/sn/serial/number/category/get',
          data: {
            uuid: uuid
          },
          contentType: 'application/x-www-form-urlencoded',
          success: function (result) {
            if (result.success || result.code == 0) {
              bean = result.data;
              $('#messageForm', $dialog).json2form(bean);
              $('#msgIconShow', $dialog).addClass(bean.icon).css({
                background: bean.iconColor
              });
              $('#selectIconBg', $dialog).find('div').attr('data-color', bean.iconColor).css({
                background: bean.iconColor
              });
            }
          }
        });
      }

      function openDialog(title, uuid) {
        var bean = {
          uuid: uuid || '',
          name: '',
          icon: '',
          iconColor: '',
          code: '',
          remark: ''
        };
        var $dialog = null;
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
                $('#messageForm', $dialog).form2json(bean);
                if (bean.name == '') {
                  appModal.error('分类名称不能为空！');
                  return false;
                }

                if (bean.code == '') {
                  appModal.error('编号不能为空！');
                  return false;
                }

                JDS.restfulPost({
                  url: ctx + '/proxy/api/sn/serial/number/category/save',
                  data: bean,
                  success: function (result) {
                    if (result.success || result.code == 0) {
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
              getDetail(bean, uuid, $dialog);
            }

            // 选择图标
            $('#addIcon', $dialog)
              .off()
              .on('click', function () {
                $.WCommonPictureLib.show({
                  selectTypes: [3],
                  confirm: function (data) {
                    var fileIDs = data.fileIDs;
                    $('#icon', $dialog).val(fileIDs);
                    $('#msgIconShow', $dialog).attr('iconClass', fileIDs);
                    $('#msgIconShow', $dialog).attr('class', fileIDs);
                  }
                });
              });

            var items = initBgColor();
            $('.bg-choose-box', $dialog).append(items);

            // 色块选择
            $('.bg-choose-item', $dialog.find('.icon-bg-wrap'))
              .off()
              .on('click', function () {
                var color = $(this).data('color');
                $(this).addClass('hasChoose').siblings().removeClass('hasChoose');
                $('#iconColor', $dialog).val(color);
                $('#selectIconBg', $dialog).find('div').data('color', color).css('background', color);
                $('#msgIconShow', $dialog).css({
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
            $('#selectIconBg', $dialog)
              .off()
              .on('click', function () {
                var currColor = $(this).find('div').data('color');
                $('.bg-choose-list', $dialog).css('display', 'inline-block');
                var chooseItem = $('.bg-choose-list', $dialog).find('.bg-choose-item');
                chooseItem.removeClass('hasChoose');
                $.each(chooseItem, function (index, item) {
                  if ($(item).data('color') == currColor) {
                    $(item).addClass('hasChoose');
                  }
                });
              });
            // 更多
            $('.bg-choose-more', $dialog.find('.icon-bg-wrap'))
              .off()
              .on('click', function () {
                $('.bg-choose-list', $dialog).hide();
                if ($('.minicolors', $dialog).size() > 0) {
                  $('.minicolors', $dialog).show();
                } else {
                  var opacity = false;
                  $('#iconColor', $dialog).minicolors({
                    control: 'hue',
                    format: 'hex',
                    color: '#0070C0',
                    letterCase: 'lowercase',
                    opacity: opacity,
                    position: 'bottom left',
                    theme: 'bootstrap',
                    change: function (value, opacity) {
                      $('#iconColor', $dialog).focus();
                      $('#selectIconBg', $dialog).find('div').data('color', value).css('background', value);
                      $('#msgIconShow', $dialog).css({
                        background: value
                      });
                    },
                    hide: function () {
                      $('#iconColor', $dialog).hide();
                      $('.icon-bg-wrap', $dialog).find('.minicolors').hide();
                    },
                    show: function () {}
                  });
                  $('#iconColor', $dialog).focus();
                  if (!opacity) {
                    $('.minicolors-input-swatch', $dialog).hide();
                  }
                }
              });
          }
        };
        $dialog = appModal.dialog(options);
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
          "<input type='hidden' name='iconColor' id='iconColor'/>" +
          '</div>' +
          '</td>' +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'><font style='color:#f00;'>*</font>编号</td>" +
          "<td><input type='text' name='code' id='code' class='form-control' placeholder='编号'/></td>" +
          '</tr>' +
          "<tr class='field'>" +
          "<td class='label-td' style='width:20%'>描述</td>" +
          "<td><textarea name='remark' id='remark' cols='30' rows='5' class='form-control'></textarea></td>" +
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

      $('#sn_category_tree', $container).on('click', '.btn-edit', function (e) {
        e.stopPropagation();
        var uuid = $(this).parents('.msg-category-item').data('uuid');
        openDialog('编辑流水号分类', uuid);
      });

      $('#sn_category_tree', $container).on('click', '.btn-delete', function (e) {
        e.stopPropagation();
        var $el = $(this).parents('.msg-category-item');
        var uuid = $el.data('uuid');
        appModal.confirm('确定删除分类吗', function (success) {
          if (success) {
            JDS.restfulPost({
              url: ctx + '/proxy/api/sn/serial/number/category/deleteWhenNotUsed',
              data: {
                uuid: uuid
              },
              contentType: 'application/x-www-form-urlencoded',
              success: function (result) {
                if (result.success || result.code == 0) {
                  if (result.data === 1 || result.data === 0) {
                    appModal.success('删除成功');
                    $el.remove();
                    $('#snAllCategory', $container).trigger('click');
                  } else if (result.data === -1) {
                    appModal.error('流水号分类下存在流水号定义，无法删除！');
                  }
                } else {
                  appModal.error('系统异常');
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
  return AppSnSerialNumberCategoryWidgetDevelopment;
});
