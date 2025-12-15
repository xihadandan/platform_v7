(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery'], factory);
  } else {
    // Browser globals
    factory(jQuery);
  }
})(function ($) {
  'use strict';

  var wellBtnLib = {};
  wellBtnLib.btnSize = [
    {
      type: '',
      id: 'ordinarySize',
      type_name: '普通尺寸',
      checked: true
    },
    {
      type: 'lg',
      id: 'lgSize',
      type_name: '较大尺寸',
      checked: false
    },
    {
      type: 'sm',
      id: 'smSize',
      type_name: '较小尺寸',
      checked: false
    }
  ];
  wellBtnLib.hasIcon = [
    {
      type: 'hasIcon',
      id: 'hasIcon',
      type_name: '带图标',
      checked: true
    },
    {
      type: 'noIcon',
      id: 'noIcon',
      type_name: '不带图标',
      checked: false
    }
  ];

  wellBtnLib.color = ['w-btn-primary', 'w-btn-success', 'w-btn-warning', 'w-btn-danger', 'w-btn-dark', 'w-btn-light'];
  wellBtnLib.colorIds = ['color_primary', 'color_success', 'color_warning', 'color_danger', 'color_dark', 'color_light'];

  wellBtnLib.btns = [
    {
      type: 'wellBtnMain',
      type_name: '可配置按钮',
      btns: [
        {
          type: 'primary',
          id: 'btn_primary',
          type_name: '主操作按钮',
          class: '',
          status: [
            {
              class: '',
              text: '普通状态'
            },
            {
              class: 'hover',
              text: '鼠标移入状态'
            },
            {
              class: 'active',
              text: '点击状态'
            },
            {
              class: 'w-disable-btn',
              text: '禁用状态'
            }
          ]
        },
        {
          type: 'minor',
          id: 'btn_minor',
          type_name: '次操作按钮',
          class: 'w-btn-minor',
          status: [
            {
              class: '',
              text: '普通状态'
            },
            {
              class: 'hover',
              text: '鼠标移入状态'
            },
            {
              class: 'active',
              text: '点击状态'
            },
            {
              class: 'w-disable-btn',
              text: '禁用状态'
            }
          ]
        },
        {
          type: 'line',
          id: 'btn_line',
          type_name: '线框按钮',
          class: 'w-line-btn',
          status: [
            {
              class: '',
              text: '普通状态'
            },
            {
              class: 'hover',
              text: '鼠标移入状态'
            },
            {
              class: 'active',
              text: '点击状态'
            },
            {
              class: 'w-disable-btn',
              text: '禁用状态'
            }
          ]
        },
        {
          type: 'noLine',
          id: 'btn_online',
          type_name: '无框按钮',
          class: 'w-noLine-btn',
          status: [
            {
              class: '',
              text: '普通状态'
            },
            {
              class: 'hover',
              text: '鼠标移入状态'
            },
            {
              class: 'active',
              text: '点击状态'
            },
            {
              class: 'w-disable-btn',
              text: '禁用状态'
            }
          ]
        }
      ]
    },
    {
      type: 'wellBtnOther',
      type_name: '其他按钮(固定类型、图标、样式,可改尺寸大小不能修改图标)',
      btns: [
        {
          class: 'w-btn-primary',
          icon: 'iconfont icon-ptkj-jiahao',
          text: '新增'
        },
        {
          class: 'w-btn-primary',
          icon: 'iconfont icon-ptkj-yinyong',
          text: '引用'
        },
        {
          class: 'w-btn-primary',
          icon: 'iconfont icon-ptkj-daoru',
          text: '导入'
        },
        {
          class: 'w-btn-primary',
          icon: 'iconfont icon-ptkj-daochu',
          text: '导出'
        },
        {
          class: 'w-btn-primary',
          icon: 'iconfont icon-ptkj-xiazai',
          text: '一键下载'
        },
        {
          class: 'w-btn-danger',
          icon: 'iconfont icon-ptkj-shanchu',
          text: '删除'
        },
        {
          class: 'w-btn-primary w-btn-minor',
          icon: 'iconfont icon-ptkj-shanchu',
          text: '批量删除'
        },
        {
          class: 'w-btn-primary w-btn-minor',
          icon: '',
          text: '跳转'
        },
        {
          class: 'w-btn-primary w-line-btn',
          icon: 'iconfont icon-ptkj-shangchuan',
          text: '上传'
        },
        {
          class: 'w-btn-primary w-line-btn',
          icon: 'iconfont icon-',
          text: '在线填单'
        },
        {
          class: 'w-btn-primary w-line-btn',
          icon: 'iconfont icon-',
          text: '查看表单'
        },
        {
          class: 'w-btn-primary w-line-btn',
          icon: '',
          text: '编辑'
        },
        {
          class: 'w-btn-primary w-line-btn',
          icon: '',
          text: '删除'
        },
        {
          class: 'w-btn-primary w-noLine-btn',
          icon: 'iconfont icon-ptkj-zhankai',
          text: '展开'
        },
        {
          class: 'w-btn-primary w-noLine-btn',
          icon: 'iconfont icon-ptkj-zhedie',
          text: '折叠'
        },
        {
          class: 'w-btn-primary w-noLine-btn',
          icon: 'iconfont icon-ptkj-shangyi',
          text: '上移'
        },
        {
          class: 'w-btn-primary w-noLine-btn',
          icon: 'iconfont icon-ptkj-xiayi',
          text: '下移'
        },
        {
          class: 'w-btn-primary w-noLine-btn',
          icon: 'iconfont icon-ptkj-yulan',
          text: '预览'
        },
        {
          class: 'w-btn-primary w-noLine-btn',
          icon: 'iconfont icon-ptkj-xiazai',
          text: '下载'
        },
        {
          class: 'w-btn-primary w-noLine-btn',
          icon: 'iconfont icon-ptkj-bianji',
          text: '编辑'
        },
        {
          class: 'w-btn-primary w-noLine-btn',
          icon: 'iconfont icon-ptkj-zhongmingming',
          text: '重命名'
        },
        {
          class: 'w-btn-primary w-noLine-btn',
          icon: 'iconfont icon-ptkj-shanchu',
          text: '删除'
        },
        {
          class: 'w-btn-primary w-noLine-btn',
          icon: 'iconfont icon-ptkj-lishi',
          text: '历史版本'
        }
      ]
    }
  ];

  $.WCommonBtnLib = {
    version: '6.1',
    defaultOptions: {
      btnSize: wellBtnLib.btnSize,
      hasIcon: wellBtnLib.hasIcon,
      btns: wellBtnLib.btns,
      colors: wellBtnLib.color,
      colorIds: wellBtnLib.colorIds,
      defaultIcon: 'iconfont icon-ptkj-morentouxiang'
    },
    show: function (opts) {
      var options = $.extend({}, this.defaultOptions, opts);
      this._render(options);
    },
    hide: function () {},
    _render: function (options) {
      var $btnLib = this._renderBtnDialog(options);
      $btnLib.addClass('wCommonBtnLib');
    },
    _renderBtnDialog: function (options) {
      var _self = this;

      var $btnSizeRadioHtml = '';
      var $btnIconRadioHtml = '';
      var $navTabsHtml = '';
      var $tabContentHtml = '';
      var iconLib =
        '<div class="chooseBtnIcon only-main"><div class="icon-wrap" style="display: inline-block;margin-right: 10px"><i class="' +
        options.defaultIcon +
        '"></i></div><button type="button" class="well-btn btn-primary">选择图标</button></div>';
      $.each(options.btnSize, function (i, v) {
        if (v.checked) {
          $btnSizeRadioHtml +=
            '<label class="radio-inline"><input type="radio" name="btnSize" value="' +
            v.type +
            '" checked' +
            ' id="' +
            v.id +
            '">' +
            '<label for="' +
            v.id +
            '">' +
            v.type_name +
            '</label>' +
            '</label>';
        } else {
          $btnSizeRadioHtml +=
            '<label class="radio-inline"><input type="radio" name="btnSize" value="' +
            v.type +
            '"' +
            ' id="' +
            v.id +
            '">' +
            '<label for="' +
            v.id +
            '">' +
            v.type_name +
            '</label>' +
            '</label>';
        }
      });
      $.each(options.hasIcon, function (i, v) {
        if (v.checked) {
          $btnIconRadioHtml +=
            '<label class="radio-inline"><input type="radio" name="btnIcon" value="' +
            v.type +
            '" checked' +
            ' id="' +
            v.id +
            '">' +
            '<label for="' +
            v.id +
            '">' +
            v.type_name +
            '</label>' +
            '</label>';
        } else {
          $btnIconRadioHtml +=
            '<label class="radio-inline"><input type="radio" name="btnIcon" value="' +
            v.type +
            '"' +
            ' id="' +
            v.id +
            '">' +
            '<label for="' +
            v.id +
            '">' +
            v.type_name +
            '</label>' +
            '</label>';
        }
      });

      $.each(options.btns, function (i, v) {
        $navTabsHtml +=
          '<li role="presentation"><a href="#' +
          v.type +
          '" aria-controls="' +
          v.type +
          '" role="tab" data-toggle="tab">' +
          v.type_name +
          '</a></li>';
        var btnsHtml = '';
        if (v.type === 'wellBtnMain') {
          var btnLi = '';
          $.each(v.btns, function (b_i, b_v) {
            var btnsHtml = '';
            $.each(b_v.status, function (b_v_i, b_v_v) {
              btnsHtml +=
                '<div class="col-xs-3 text-center">' +
                '<a class="well-btn ' +
                options.colors[0] +
                ' ' +
                b_v.class +
                ' ' +
                b_v_v.class +
                '">' +
                '<i class="' +
                options.defaultIcon +
                '"></i>按钮' +
                '</a><br>' +
                '<span>' +
                b_v_v.text +
                '</span>' +
                '</div>';
            });
            btnLi +=
              '<li class="btn-data" data-id="' +
              i +
              '-' +
              b_i +
              '"><label class="well-btn-radio"><input type="radio" name="wellBtnType" value="' +
              b_v.type +
              '"' +
              ' id="' +
              b_v.id +
              '">' +
              '<label for="' +
              b_v.id +
              '">' +
              b_v.type_name +
              '</label>' +
              '</label><div class="row">' +
              btnsHtml +
              '</div></li>';
          });
          $tabContentHtml +=
            '<div role="tabpanel" class="tab-pane row" id="' +
            v.type +
            '"><ul class="well-btn-list well-btn-main-list">' +
            btnLi +
            '</ul></div>';
        } else {
          var btnLi = '';
          $.each(v.btns, function (b_i, b_v) {
            btnLi +=
              '<li class="btn-data col-xs-3" data-id="' +
              i +
              '-' +
              b_i +
              '">' +
              '<label class="well-btn-radio">' +
              '<input type="radio" name="wellBtnType" value="' +
              b_i +
              '"' +
              ' id="' +
              b_i +
              '">' +
              '<label for="' +
              b_i +
              '">' +
              '</label>' +
              '</label>' +
              '<a class="well-btn ' +
              b_v.class +
              '">';
            if (b_v.icon) {
              btnLi += '<i class="' + b_v.icon + '"></i>';
            }
            btnLi += b_v.text + '</a></li>';
          });
          $tabContentHtml +=
            '<div role="tabpanel" class="tab-pane row" id="' +
            v.type +
            '"><ul class="well-btn-list well-btn-other-list">' +
            btnLi +
            '</ul></div>';
        }
      });

      var $colorHtml = '';
      $.each(options.colors, function (i, v) {
        if (i === 0) {
          $colorHtml +=
            '<label class="radio-inline"><input type="radio" name="btnColor" value="' +
            v +
            '" checked' +
            ' id="' +
            options.colorIds[i] +
            '">' +
            '<label for="' +
            options.colorIds[i] +
            '"></label>' +
            '<button class="well-btn ' +
            v +
            '"></button></label>';
        } else {
          $colorHtml +=
            '<label class="radio-inline"><input type="radio" name="btnColor" value="' +
            v +
            '"' +
            ' id="' +
            options.colorIds[i] +
            '">' +
            '<label for="' +
            options.colorIds[i] +
            '"></label>' +
            '<button class="well-btn ' +
            v +
            '"></button></label>';
        }
      });

      var $btnSizeRadioWrap = '<div class="btn-size-radio">' + $btnSizeRadioHtml + '</div>';
      var $btnIconRadioWrap = '<div class="btn-icon-radio only-main">' + $btnIconRadioHtml + '</div>';
      var $navTabsWrap = '<ul class="nav nav-tabs" role="tablist">' + $navTabsHtml + '</ul>';
      var $tabContentWrap = '<div class="tab-content">' + $tabContentHtml + '</div>';
      var $colorsWrap = '<div class="btn-color-wrap only-main"><label style="margin-right: 10px">按钮皮肤</label>' + $colorHtml + '</div>';
      var content =
        '<div class="wellBtnLib">' +
        $btnSizeRadioWrap +
        $btnIconRadioWrap +
        $colorsWrap +
        iconLib +
        $navTabsWrap +
        $tabContentWrap +
        '</div>';

      var dialogOpts = {
        title: '按钮库',
        size: 'large',
        message: content,
        shown: function () {
          var value = options.value;
          $(document).on('change', 'input:radio[name="btnSize"]', function () {
            var $btnLibMainList = $('.well-btn-list');
            var _val = $(this).val();
            var btn = $btnLibMainList.find('.well-btn');
            btn.removeClass('well-btn-lg well-btn-sm');
            if (_val) {
              btn.addClass('well-btn-' + _val);
            }
          });
          $(document).on('change', 'input:radio[name="btnIcon"]', function () {
            var $btnLibContainer = $('.wellBtnLib');
            var _val = $(this).val();
            var btn = $btnLibContainer.find('#wellBtnMain .well-btn');
            if (_val === 'hasIcon') {
              btn.find('i').show();
            } else {
              btn.find('i').hide();
            }
          });

          $(document).on('change', 'input:radio[name="btnColor"]', function () {
            var $btnLibMainList = $('.well-btn-main-list');
            var _val = $(this).val();
            var btn = $btnLibMainList.find('.well-btn');
            btn.removeClass(options.colors.join(' ')).addClass(_val);
          });

          if (value && value.btnSize) {
            var btnSize = value.btnSize.indexOf('well-btn-') > -1 ? value.btnSize.split('well-btn-')[1] : '';
            $("input[name='btnSize'][value='" + btnSize + "']", '.wellBtnLib')
              .attr('checked', true)
              .trigger('change');
          }
          if (value && value.iconInfo && value.iconInfo.fileIDs) {
            $("input[name='btnIcon'][value='hasIcon']").attr('checked', true).trigger('change');
            $('.icon-wrap', '.wellBtnLib').find('i').removeAttr('class').attr('class', value.iconInfo.fileIDs);
            $('.icon-wrap', '.wellBtnLib').data('info', value.iconInfo);
          } else if (value && value.iconInfo === null) {
            $("input[name='btnIcon'][value='noIcon']").attr('checked', true).trigger('change');
            $('.icon-wrap', '.wellBtnLib').find('i').removeAttr('class').attr('class', 'iconfont icon-ptkj-morentouxiang');
          }
          if (value && value.btnColor) {
            $("input[name='btnColor'][value='" + value.btnColor + "']", '.wellBtnLib')
              .attr('checked', true)
              .trigger('change');
          }
          if (value && value.btnInfo) {
            if (value.btnInfo.status) {
              $("a[aria-controls='wellBtnMain']", '.wellBtnLib').trigger('click');
              $("input[name='wellBtnType'][value='" + value.btnInfo.type + "']", '.wellBtnLib')
                .attr('checked', true)
                .trigger('change');
            } else {
              $("a[aria-controls='wellBtnOther']", '.wellBtnLib').trigger('click');
              var lis = $('.well-btn-other-list li', '.wellBtnLib');
              $.each(lis, function (index, item) {
                if (
                  $(item).find('a').hasClass(value.btnInfo.class) &&
                  $(item).find('a i').hasClass(value.btnInfo.icon) &&
                  $(item).find('a').text() == value.btnInfo.text
                ) {
                  $(item).find('input:radio').attr('checked', true).trigger('change');
                }
              });
            }
          }

          $('.chooseBtnIcon .well-btn').on('click', function (e) {
            var $this = $(this);
            $.WCommonPictureLib.show({
              selectTypes: '3',
              value: $('.icon-wrap').find('i').attr('class'),
              confirm: function (data) {
                var $icon = $('#wellBtnMain .well-btn i');
                var $iconWrap = $this.siblings('.icon-wrap');
                var $i = $iconWrap.find('i');
                $iconWrap.data('info', data);
                $i.removeClass($i.attr('class')).addClass(data.fileIDs);
                $icon.removeClass($icon.attr('class')).addClass(data.fileIDs);
              }
            });
          });

          $('.modal-dialog').find('div').css({ 'box-sizing': 'border-box' });
        },
        buttons: {
          confirm: {
            label: '确定',
            className: 'btn-primary'
          },
          cancel: {
            label: '关闭',
            className: 'btn-default'
          }
        }
      };

      // dialogOpts.buttons.cancel.callback = function() {
      //     return options.cancel.call(this);
      // };

      dialogOpts.buttons.confirm.callback = function () {
        var $wellBtnLib = $('.wellBtnLib');
        var btnSize = $wellBtnLib.find('input:radio[name="btnSize"]:checked').val();
        var btnIcon = $wellBtnLib.find('input:radio[name="btnIcon"]:checked').val();
        var btnColor = $wellBtnLib.find('input:radio[name="btnColor"]:checked').val();
        var chooseBtnID = $wellBtnLib.find('input:radio[name="wellBtnType"]:checked').parents('.btn-data').data('id').split('-');
        var btnInfo = options.btns[parseInt(chooseBtnID[0])].btns[parseInt(chooseBtnID[1])];
        var iconInfo = null;
        if (btnIcon === 'hasIcon') {
          iconInfo = $('.wellBtnLib .icon-wrap').data('info') || {
            fileIDs: 'iconfont icon-ptkj-morentouxiang',
            filePaths: 'iconfont icon-ptkj-morentouxiang',
            fileType: 3
          };
        }
        return options.confirm.call(this, {
          btnSize: btnSize ? 'well-btn-' + btnSize : '',
          iconInfo: iconInfo,
          btnInfo: btnInfo,
          btnColor: btnColor
        });
      };

      var $btnLib = $.WCommonDialog(dialogOpts);
      $('.wellBtnLib .nav-tabs a').on('click', function (e) {
        e.preventDefault();
        var $this = $(this);
        $this.tab('show');
        if ($this.attr('href') === '#wellBtnMain') {
          $('.wellBtnLib .only-main').show();
        } else {
          $('.wellBtnLib .only-main').hide();
        }
      });

      $('.wellBtnLib .nav-tabs a:first').attr('aria-expanded', 'true').parent().addClass('active');
      $('#wellBtnMain').addClass('active');
      return $btnLib;
    }
  };
});
