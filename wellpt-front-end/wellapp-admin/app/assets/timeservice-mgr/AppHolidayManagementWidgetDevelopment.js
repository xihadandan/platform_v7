define(['constant', 'commons', 'server', 'appContext', 'appModal', 'jquery-chained', 'HtmlWidgetDevelopment'], function (
  constant,
  commons,
  server,
  appContext,
  appModal,
  chained,
  HtmlWidgetDevelopment
) {
  var JDS = server.JDS;

  // 平台应用_基础数据管理_节假日管理_详情二开
  var AppHolidayManagementWidgetDevelopment = function () {
    HtmlWidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(AppHolidayManagementWidgetDevelopment, HtmlWidgetDevelopment, {
    // 初始化回调
    init: function () {
      this.bean = {
        uuid: null,
        name: null,
        id: null,
        calendarType: null,
        remark: null,
        tag: null,
        holidayDate: null,
        holidayDateName: null
      };
      var _this = this;
      var uuid = GetRequestParam().uuid;
      this.initCkeditorPlugin();
      _this.$element = this.widget.element;
      var dateData = getDateData();
      renderTagOptionSet();

      if (uuid) {
        getHolidayManagement(uuid);
      } else {
        renderTag(_this, '法定节假日');
      }

      $('#holidayDateName', _this.$element).cascader({
        triggerType: 'click',
        navTitls: ['a', 'b'],
        nodes: dateData['yang'].data,
        separator: ''
      });

      $("input[name='calendarType']")
        .off()
        .on('change', function () {
          $('#holidayDateName', _this.$element).val('');
          var type = $(this).val() == '2' ? 'ying' : 'yang';
          $('#holidayDateName', _this.$element).cascader('destroy');
          $('#holidayDateName', _this.$element).cascader({
            triggerType: 'click',
            navTitls: ['a', 'b'],
            nodes: dateData[type].data,
            separator: ''
          });
        });

      function renderTagOptionSet() {
        JDS.call({
          service: 'dataDictionaryService.getDataDictionariesByTypeCode',
          data: ['computer_rank', ''],
          version: '',
          async: false,
          success: function (result) {
            var data = result.data;
            if (data.length > 0) {
              console.log(data);
              var html = '';
              $.each(data, function (index, item) {
                html +=
                  '<li class="tag-options-item" data-index="' +
                  item.name +
                  '">' +
                  '<i class="tag-check iconfont icon-ptkj-duoxuan-weixuan"></i>' +
                  item.name +
                  '</li>';
              });
              $('.more-tags-data', _this.$element).append(html);
            }
          }
        });
      }

      $('.add-tags', _this.$element).live('click', function (e) {
        // 备选项选择点击显示弹窗
        e.stopPropagation();
        if ($('.more-tags-box:visible').size() > 0) {
          $('.more-tags-box:visible').hide();
        }
        if ($('.options-open').size() > 0) {
          $('.options-open').removeClass('options-open').next().hide();
        }
        var left = $(this).offset().left >= 600 ? '-400' : '0';
        $(this)
          .find('.more-tags-box')
          .css({
            bottom: '-' + $(this).find('.more-tags-box').height() - 5 + 'px',
            left: left + 'px'
          })
          .show();
      });

      $('.tag-options-item', _this.$element).live('click', function (e) {
        // 选择标签/单个选择
        e.stopPropagation();
        if ($(this).hasClass('tag-disabled')) {
          return false;
        }
        _this.hasSelectTag = true;
        _this.selectTags($(this), 'tag-selected', '');

        if (!$(this).hasClass('tag-selected')) {
          var allElem = $(_this.$element).find('.option-select-all');
          _this.removeTagClass(allElem, 'select-all', 'cancle');
        }
      });

      $('.option-select-all', _this.$element).live('click', function (e) {
        // 选择标签/全选
        e.stopPropagation();
        if ($(this).hasClass('tag-disabled')) {
          return false;
        }
        _this.hasSelectTag = true;
        _this.selectTags($(this), 'select-all', '');
      });

      $('.tag-operate-close', _this.$element).live('click', function (e) {
        // 取消
        e.stopPropagation();
        if (_this.hasSelectTag) {
          _this.cancleSelectedTag();
        }
        _this.hasSelectTag = false;

        $(this).parents('.more-tags-box').hide();
      });

      $('.tag-operate-sure', _this.$element).live('click', function (e) {
        // 确定
        e.stopPropagation();
        var tagSelected = $(_this.$element).find('.tag-selected');
        var liDom = '';
        tagSelected.each(function (index) {
          liDom +=
            "<li class='tag-group-item' data-index='" +
            $(tagSelected[index]).data('index') +
            "'>" +
            '<span>' +
            $(tagSelected[index]).text() +
            "<i class='tag-delete  iconfont icon-ptkj-dacha-xiao'></i></span>" +
            '</li>';
        });

        $(this).parents('.add-tags').prev('.tag-group').html(liDom);

        var lis = $(_this.$element).find('.tag-group li');
        lis.each(function (index) {
          $(lis[index]).find('span,.three-angle').css({
            color: '#666',
            'border-color': '#ddd',
            'background-color': '#f6f6f6'
          });
        });
        _this.hasSelectTag = false;

        $(this).parents('.more-tags-box').hide();
      });

      $('.tag-delete', _this.$element).live('click', function () {
        // 删除已选标签
        var tagItem = $(this).parents('.tag-group-item');
        tagItem.remove();
        var optionsItem = $(_this.$element).find('.tag-options-item');
        optionsItem.each(function (index) {
          if ($(optionsItem[index]).data('index') == tagItem.data('index')) {
            _this.selectTags($(optionsItem[index]), 'tag-selected');
          }
        });
        if ($(_this.$element).find('.option-select-all').hasClass('select-all')) {
          _this.selectTags($(_this.$element).find('.option-select-all'), 'select-all', 'cancle');
        }
      });

      $('#btn_save_holiday_management', _this.$element)
        .off()
        .on('click', function () {
          $('#holiday_management_form', _this.$element).form2json(_this.bean);
          var emptyStr = [];

          if (_this.bean.name == '') {
            emptyStr.push('名称');
          }
          if (_this.bean.id == '') {
            emptyStr.push('ID');
          }

          if (_this.bean.holidayDateName == '') {
            emptyStr.push('日期');
          }

          var holidayDate = $('#holidayDateName', _this.$element).data('value').toString();
          _this.bean.holidayDate = holidayDate.substr(0, 2) + '-' + holidayDate.substr(2, 2);

          if (emptyStr.length > 0) {
            appModal.error(emptyStr.join('、') + '不能为空！');
            return false;
          }

          _this.bean.calendarType = $("input[name='calendarType']:checked", _this.$element).val();
          _this.bean.remark = CKEDITOR.instances.remark.getData();
          var tag = [];
          var tagItem = $('.tag-group-item', _this.$element);
          $.each(tagItem, function (index, item) {
            tag.push($(item).data('index'));
          });
          _this.bean.tag = tag.join(';');
          $.ajax({
            type: 'post',
            url: ctx + '/api/ts/holiday/save',
            data: _this.bean,
            dataType: 'json',
            success: function (res) {
              if (res.code == 0) {
                appModal.success('保存成功！', function () {
                  appContext.getNavTabWidget().closeTab();
                });
              } else {
                appModal.error(res.msg);
              }
            }
          });
        });

      function getHolidayManagement(uuid) {
        $.ajax({
          type: 'get',
          url: ctx + '/api/ts/holiday/get',
          data: { uuid: uuid },
          success: function (res) {
            if (res.code == 0) {
              _this.bean = res.data;
              $('#holiday_management_form', _this.$element).json2form(_this.bean);
              $('#id', _this.$element).attr('readonly', true);
              $("input[name='calendarType'][value='" + _this.bean.calendarType + "']", _this.$element)
                .prop('checked', true)
                .trigger('change');

              $('#holidayDateName', _this.$element).cascader('setValue', _this.bean.holidayDate.split('-'));
              renderTag(_this, _this.bean.tag);
            }
          }
        });
      }

      function renderTag(_this, tagStr) {
        var liDom = '';
        var tag = tagStr ? tagStr.split(';') : [];
        var liDom = '';
        for (var i = 0; i < tag.length; i++) {
          liDom +=
            "<li class='tag-group-item' data-index='" +
            tag[i] +
            "'>" +
            '<span >' +
            tag[i] +
            "</span><i class='tag-delete iconfont icon-ptkj-dacha-xiao'></i>" +
            '</li>';
        }
        _this.$element.find('.tag-group').html(liDom);
        _this.mapSelectItems(tag);
      }

      function getDateData() {
        var lunarInfo = [
          0x0ab50, //1899
          0x04bd8,
          0x04ae0,
          0x0a570,
          0x054d5,
          0x0d260,
          0x0d950,
          0x16554,
          0x056a0,
          0x09ad0,
          0x055d2, //1900-1909
          0x04ae0,
          0x0a5b6,
          0x0a4d0,
          0x0d250,
          0x1d255,
          0x0b540,
          0x0d6a0,
          0x0ada2,
          0x095b0,
          0x14977, //1910-1919
          0x04970,
          0x0a4b0,
          0x0b4b5,
          0x06a50,
          0x06d40,
          0x1ab54,
          0x02b60,
          0x09570,
          0x052f2,
          0x04970, //1920-1929
          0x06566,
          0x0d4a0,
          0x0ea50,
          0x06e95,
          0x05ad0,
          0x02b60,
          0x186e3,
          0x092e0,
          0x1c8d7,
          0x0c950, //1930-1939
          0x0d4a0,
          0x1d8a6,
          0x0b550,
          0x056a0,
          0x1a5b4,
          0x025d0,
          0x092d0,
          0x0d2b2,
          0x0a950,
          0x0b557, //1940-1949
          0x06ca0,
          0x0b550,
          0x15355,
          0x04da0,
          0x0a5b0,
          0x14573,
          0x052b0,
          0x0a9a8,
          0x0e950,
          0x06aa0, //1950-1959
          0x0aea6,
          0x0ab50,
          0x04b60,
          0x0aae4,
          0x0a570,
          0x05260,
          0x0f263,
          0x0d950,
          0x05b57,
          0x056a0, //1960-1969
          0x096d0,
          0x04dd5,
          0x04ad0,
          0x0a4d0,
          0x0d4d4,
          0x0d250,
          0x0d558,
          0x0b540,
          0x0b6a0,
          0x195a6, //1970-1979
          0x095b0,
          0x049b0,
          0x0a974,
          0x0a4b0,
          0x0b27a,
          0x06a50,
          0x06d40,
          0x0af46,
          0x0ab60,
          0x09570, //1980-1989
          0x04af5,
          0x04970,
          0x064b0,
          0x074a3,
          0x0ea50,
          0x06b58,
          0x055c0,
          0x0ab60,
          0x096d5,
          0x092e0, //1990-1999
          0x0c960,
          0x0d954,
          0x0d4a0,
          0x0da50,
          0x07552,
          0x056a0,
          0x0abb7,
          0x025d0,
          0x092d0,
          0x0cab5, //2000-2009
          0x0a950,
          0x0b4a0,
          0x0baa4,
          0x0ad50,
          0x055d9,
          0x04ba0,
          0x0a5b0,
          0x15176,
          0x052b0,
          0x0a930, //2010-2019
          0x07954,
          0x06aa0,
          0x0ad50,
          0x05b52,
          0x04b60,
          0x0a6e6,
          0x0a4e0,
          0x0d260,
          0x0ea65,
          0x0d530, //2020-2029
          0x05aa0,
          0x076a3,
          0x096d0,
          0x04bd7,
          0x04ad0,
          0x0a4d0,
          0x1d0b6,
          0x0d250,
          0x0d520,
          0x0dd45, //2030-2039
          0x0b5a0,
          0x056d0,
          0x055b2,
          0x049b0,
          0x0a577,
          0x0a4b0,
          0x0aa50,
          0x1b255,
          0x06d20,
          0x0ada0, //2040-2049
          0x14b63,
          0x09370,
          0x049f8,
          0x04970,
          0x064b0,
          0x168a6,
          0x0ea50,
          0x06b20,
          0x1a6c4,
          0x0aae0, //2050-2059
          0x0a2e0,
          0x0d2e3,
          0x0c960,
          0x0d557,
          0x0d4a0,
          0x0da50,
          0x05d55,
          0x056a0,
          0x0a6d0,
          0x055d4, //2060-2069
          0x052d0,
          0x0a9b8,
          0x0a950,
          0x0b4a0,
          0x0b6a6,
          0x0ad50,
          0x055a0,
          0x0aba4,
          0x0a5b0,
          0x052b0, //2070-2079
          0x0b273,
          0x06930,
          0x07337,
          0x06aa0,
          0x0ad50,
          0x14b55,
          0x04b60,
          0x0a570,
          0x054e4,
          0x0d160, //2080-2089
          0x0e968,
          0x0d520,
          0x0daa0,
          0x16aa6,
          0x056d0,
          0x04ae0,
          0x0a9d4,
          0x0a2d0,
          0x0d150,
          0x0f252, //2090-2099
          0x0d520
        ]; //2100
        var year = new Date().getFullYear();
        var dateData = {
          // 日期数据
          ying: {
            mon: ['正月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
            num: monthDays(year, lunarInfo),
            day: [
              '初一',
              '初二',
              '初三',
              '初四',
              '初五',
              '初六',
              '初七',
              '初八',
              '初九',
              '初十',
              '十一',
              '十二',
              '十三',
              '十四',
              '十五',
              '十六',
              '十七',
              '十八',
              '十九',
              '二十',
              '廿一',
              '廿二',
              '廿三',
              '廿四',
              '廿五',
              '廿六',
              '廿七',
              '廿八',
              '廿九',
              '三十'
            ],
            data: []
          },
          yang: {
            num: [31, isLeap(year) ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31],
            data: []
          }
        };

        var obj = {
          checked: false,
          children: [],
          data: null,
          iconSkin: null,
          iconStyle: null,
          id: '',
          isParent: false,
          name: '',
          nocheck: false,
          nodeLevel: 0,
          open: false,
          path: '',
          total: 0,
          type: null,
          url: null
        };

        for (var i in dateData) {
          dateData[i] = getData(dateData[i], obj, 12, i);
        }

        return dateData;
      }

      function getData(data, obj, num, type, date, parent, day) {
        for (var i = 0; i < num; i++) {
          var newObj = _.cloneDeep(obj);
          newObj.id = setVal(i - 0 + 1);
          newObj.name = date ? (type == 'ying' ? day[i] : i - 0 + 1 + '日') : type == 'ying' ? data.mon[i] : i - 0 + 1 + '月';
          newObj.path = parent ? parent.path + '/' + newObj.name : newObj.name;
          if (data.num && data.num.length > 0) {
            getData(newObj.children, obj, data.num[i], type, true, newObj, data.day);
          }
          if (data.data) {
            data.data.push(newObj);
          } else {
            data.push(newObj);
          }
        }
        return data;
      }

      function setVal(val) {
        return val > 9 ? val : '0' + val;
      }

      // 获取农历指定月份的天数
      function monthDays(y, lunarInfo) {
        var num = [];
        for (var i = 1; i < 13; i++) {
          num.push(lunarInfo[y - 1899] & (0x10000 >> i) ? 30 : 29);
        }
        return num;
      }
      // 判断是闰年还是平年
      function isLeap(y) {
        return (y % 4 == 0 && y % 100 != 0) || y % 400 == 0;
      }
    },
    initCkeditorPlugin: function () {
      var _this = this;
      var customCkeditorPath = staticPrefix + '/dyform/explain/ckeditor'; // 自定义ckeditor相关配置的路径
      CKEDITOR.plugins.basePath = customCkeditorPath + '/plugins/'; // 自定义ckeditor的插件路径
      CKEDITOR.replace('remark', {
        allowedContent: true,
        enterMode: CKEDITOR.ENTER_P,
        font_names:
          '宋体/宋体;黑体/黑体;仿宋/仿宋_GB2312;楷体/楷体_GB2312;隶书/隶书;幼圆/幼圆;微软雅黑/微软雅黑;' + CKEDITOR.config.font_names,
        toolbarStartupExpanded: true,
        toolbarCanCollapse: true,
        customConfig: customCkeditorPath + '/dyform_config.js',
        height: '100%',
        toolbar: [
          [
            'Bold',
            'Italic',
            'Underline',
            'NumberedList',
            'BulletedList',
            '-',
            'Outdent',
            'Indent',
            'JustifyLeft',
            'JustifyCenter',
            'JustifyRight',
            'JustifyBlock',
            'Undo',
            'Redo',
            'changeMode',
            'Maximize'
          ],
          ['Font', 'FontSize', 'TextColor', 'BGColor', 'Blockquote', 'Link', 'Image', 'Table', 'Smiley', 'Source']
        ],

        pasteFromWordRemoveStyles: true,
        forcePasteAsPlainText: false,
        pasteFromWordKeepsStructure: false,
        pasteFromWordRemoveFontStyles: false,
        pasteFromWordPromptCleanup: true, // 是否提示保留word样式
        pasteFromWordNumberedHeadingToList: true,

        on: {
          paste: function (evt) {
            console.log('AppHolidayManagementWidgetDevelopment.js evt', evt);
            handleCkeditorPaste(evt);
          },
          instanceReady: function (ev) {
            // Output paragraphs as <p>Text</p>.
            this.dataProcessor.writer.setRules('p', {
              indent: true,
              breakBeforeOpen: false,
              breakAfterOpen: false,
              breakBeforeClose: false,
              breakAfterClose: false
            });

            CKEDITOR.instances.remark.setData(_this.bean.remark);

            var _path = CKEDITOR.plugins.registered.changeMode.path;
            var _name = 'content';
            var iconDown = _path + 'images/iconDown.png';
            var cke_toolbar_lastChild = $('#cke_' + _name + ' .cke_toolbar:last-child');
            var cke_button__changemode_icon = $('#cke_' + _name + ' .cke_button__changemode_icon');
            cke_toolbar_lastChild.hide();
            cke_button__changemode_icon.css('backgroundImage', 'url(' + iconDown + ')');
          },
          loaded: function (ev) {
            //根据浏览器高度，自适应调整正文框的高度
            // var height = $(window).height() - $('#content').parent().offset().top - 180;
            // $('.cke_contents').css('height', height);
            // _this.ckeditorLoadFinished = true;
            // _this.recalculateActiveRightNavHeight($('.mail-right-nav-bar-content:eq(0)'));
          }
        }
      });
    },
    selectTags: function (ele, selector, status) {
      if (ele.hasClass(selector)) {
        this.removeTagClass(ele, selector, status);
      } else {
        this.addTagClass(ele, selector, status);
      }
    },
    removeTagClass: function (ele, selector, status) {
      var xz = 'icon-ptkj-duoxuan-xuanzhong',
        wxz = 'icon-ptkj-duoxuan-weixuan';
      ele.removeClass(selector).find('i').removeClass(xz).addClass(wxz);
      if (selector == 'select-all' && status != 'cancle') {
        ele.siblings('li').removeClass('tag-selected').find('i').removeClass(xz).addClass(wxz);
      }
    },
    addTagClass: function (ele, selector, status) {
      var xz = 'icon-ptkj-duoxuan-xuanzhong',
        wxz = 'icon-ptkj-duoxuan-weixuan';
      ele.addClass(selector).removeClass('tag-disabled').find('i').removeClass(wxz).addClass(xz);
      if (selector == 'select-all' && status != 'cancle') {
        ele.siblings('li').addClass('tag-selected').removeClass('tag-disabled').find('i').removeClass(wxz).addClass(xz);
      }
    },
    cancleSelectedTag: function () {
      var _this = this;
      var optionItem = $(_this.$element).find('.tag-options-item');
      var tagTtem = $(_this.$element).find('.tag-group-item');
      var allElem = $(_this.$element).find('.option-select-all');
      var length = $('.tag-options-item', _this.$element).length;
      if ((!allElem.hasClass('select-all') && tagTtem.length == length) || (allElem.hasClass('select-all') && tagTtem.length == length)) {
        _this.addTagClass(allElem, 'select-all', 'cancle');
      } else {
        _this.removeTagClass(allElem, 'select-all', 'cancle');
      }
      optionItem.each(function (index) {
        if (tagTtem.length == 0) {
          _this.removeTagClass($(optionItem[index]), 'tag-selected', '');
        } else {
          for (var i = 0; i < tagTtem.length; i++) {
            if ($(optionItem[index]).data('index') == $(tagTtem[i]).data('index')) {
              _this.addTagClass($(optionItem[index]), 'tag-selected', '');
              break;
            } else {
              _this.removeTagClass($(optionItem[index]), 'tag-selected', '');
            }
          }
        }
      });
    },
    mapSelectItems: function (valueObj) {
      var self = this;
      var optionItem = $(this.$element).find('.tag-options-item');

      optionItem.each(function (index) {
        if (valueObj.length > 0) {
          for (var i = 0; i < valueObj.length; i++) {
            if ($(optionItem[index]).data('index') == valueObj[i]) {
              self.addTagClass($(optionItem[index]), 'tag-selected', '');
              break;
            } else {
              self.removeTagClass($(optionItem[index]), 'tag-selected', '');
            }
          }
        } else {
          self.removeTagClass($(optionItem[index]), 'tag-selected', '');
        }
      });
    }
  });
  return AppHolidayManagementWidgetDevelopment;
});
