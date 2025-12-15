define(['commons', 'server', 'HtmlWidgetDevelopment'], function (commons, server, HtmlWidgetDevelopment) {
  var AppPictureLibEditViewDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppPictureLibEditViewDevelopment, HtmlWidgetDevelopment, {
    init: function () {
      var _this = this;
      var uuid = GetRequestParam().uuid;
      var newItem = false;

      if (uuid) {
        $.ajax({
          type: 'GET',
          url: '/basicdata/img/category/' + uuid,
          dataType: 'json',
          async: false,
          success: function (result) {
            if (result.code === 0) {
              _this.initForm(result.data);
            }
          }
        });
      } else {
        newItem = true;
      }

      var items = this.initBgColor();
      $('.bg-choose-box').append(items);

      this.bindEvent();

      if (newItem) {
        $('#useIcon').prop('checked', 'checked').trigger('change');
      }
    },

    initForm: function (data) {
      data.useIcon = !!data.icon;
      $('#picture_lib_form').json2form(data);

      if (data.useIcon) {
        $('#msgIconShow')
          .addClass(data.icon)
          .css('background', data.color || '');
        if (data.color) {
          $('.icon-bg-wrap #selectionColor div').css('background', data.color).data('color', data.color);
        }
      }
    },

    bindEvent: function () {
      var _this = this;

      $('#useIcon')
        .off()
        .on('change', function () {
          var $this = $(this);
          if ($this.prop('checked')) {
            $('#icon-selector').show();
            if (!$('#icon').val()) {
              var defaultColor = '#64B3EA';
              $('#icon').val('iconfont icon-ptkj-tubiaoshitu');
              $('#color').val(defaultColor);
              $('.icon-bg-wrap #selectionColor div').css('background-color', defaultColor).data('color', defaultColor);
              $('#msgIconShow').attr('class', 'msgIconShow iconfont icon-ptkj-tubiaoshitu').css('background', defaultColor);
            }
          } else {
            $('#icon-selector').hide();
          }
        })
        .trigger('change');

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
              $('#msgIconShow').attr('class', 'msgIconShow ' + fileIDs);
            }
          });
        });

      // 色块选择
      $('.bg-choose-item', '.icon-bg-wrap')
        .off()
        .on('click', function () {
          var color = $(this).data('color');
          $(this).addClass('hasChoose').siblings().removeClass('hasChoose');
          $('#color').val(color);
          $('#selectionColor').find('div').data('color', color).css({ background: color });
          $('#msgIconShow').css({ background: color });
        });

      // 关闭色块
      $(document).on('click', function (e) {
        if (!$(e.target).hasClass('icon-bg-wrap') && !$(e.target).parents().hasClass('icon-bg-wrap')) {
          $('.bg-choose-list').hide();
          $('.minicolors').hide();
        }
      });

      // 显示色块
      $('#selectionColor')
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
            $('#color').minicolors({
              control: 'hue',
              format: 'hex',
              color: '#0070C0',
              letterCase: 'lowercase',
              opacity: opacity,
              position: 'bottom left',
              theme: 'bootstrap',
              change: function (value, opacity) {
                $('#color').focus();
                $('#selectionColor').find('div').data('color', value).css('background', value);
                $('#msgIconShow').css({ background: value });
              },
              hide: function () {
                $('#color').hide();
                $('.icon-bg-wrap').find('.minicolors').hide();
              },
              show: function () {
                $('#color').focus();
              }
            });
            $('#color').focus();
            if (!opacity) {
              $('.minicolors-input-swatch').hide();
            }
          }
        });

      // 保存
      $('#pic_save_btn').click(function () {
        // 收集数据
        var bean = {
          uuid: uuid || '',
          name: '',
          icon: '',
          color: '',
          code: '',
          description: '',
          systemUnitId: ''
        };

        $('#picture_lib_form').form2json(bean);

        if (bean.code == '') {
          appModal.error('编号不能为空！');
          return false;
        }

        if (bean.name == '') {
          appModal.error('分类名称不能为空！');
          return false;
        }

        if (_this.isCategoryAlreadyExists(bean.name, bean.uuid)) {
          appModal.error('分类名称已存在，无法保存！');
          return false;
        }

        if (!$('#useIcon').prop('checked')) {
          bean.icon = '';
          bean.color = '';
        }

        $.ajax({
          type: 'POST',
          url: '/basicdata/img/category/save',
          dataType: 'json',
          data: bean,
          success: function (result) {
            if (result.code === 0) {
              appModal.success('保存成功！');
              appContext.getNavTabWidget().closeTab();
            } else {
              appModal.alert(result.data);
            }
          }
        });
      });
    },

    isCategoryAlreadyExists: function (name, uuid) {
      var exists = false;
      $.ajax({
        type: 'GET',
        url: '/basicdata/img/category/queryAllCategory',
        dataType: 'json',
        async: false,
        success: function (result) {
          var data = result.data;
          for (var i = 0; i < data.length; i++) {
            var item = data[i];
            if (item.name.trim().toLowerCase() === name.trim().toLowerCase() && item.uuid !== uuid) {
              exists = true;
              return;
            }
          }
        }
      });
      return exists;
    },

    initBgColor: function () {
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
        var icon = '<i class="iconfont icon-ptkj-dagou"></i>';
        // prettier-ignore
        items += '<li class="bg-choose-item" data-color="' + item + '" style="background: ' + item + '">' + icon + '</li>';
      });

      return items;
    },

    refresh: function () {
      this.init();
    }
  });
  return AppPictureLibEditViewDevelopment;
});
