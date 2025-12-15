(function ($) {
  var columnProperty = {
    // 控件字段属性
    applyTo: null, // 应用于
    columnName: null, // 字段定义 fieldname
    displayName: null, // 描述名称 descname
    dbDataType: '', // 字段类型 datatype type
    indexed: null, // 是否索引
    showed: null, // 是否界面表格显示
    sorted: null, // 是否排序
    sysType: null, // 系统定义类型，包括三种（0：系统默认，1：管理员常量定义，2：表单添加后自定义）
    length: null, // 长度
    placeholder: '', //提示语
    showType: '1', // 显示类型 1,2,3,4 datashow
    defaultValue: null, // 默认值
    valueCreateMethod: '1', // 默认值创建方式 1用户输入
    onlyreadUrl: null, // 只读状态下设置跳转的url
    toLinkFieldData: '' // 是否关联本表数据
  };

  // 控件公共属性
  var commonProperty = {
    inputMode: null, // 输入样式 控件类型 inputDataType
    fieldCheckRules: null,
    customizedRegularText: null,
    fontSize: null, // 字段的大小
    fontColor: null, // 字段的颜色
    ctlWidth: null, // 宽度
    ctlHight: null, // 高度
    textAlign: null // 对齐方式
  };

  /*
   * TEXTINPUT CLASS DEFINITION ======================
   */
  var TextInput = function ($placeHolder, options) {
    this.options = $.extend({}, $.fn['wtextInput'].defaults, options);
    this.value = '';
    this.$editableElem = null;
    this.$labelElem = null;
    this.$placeHolder = $placeHolder;
  };

  TextInput.prototype = {
    constructor: TextInput
  };

  $.TextInput = {
    createEditableElem: function () {
      var _self = this;
      if (_self.$editableElem != null) {
        // 创建可编辑框
        return;
      }
      var options = _self.options;
      var ctlName = _self.getCtlName();
      var _inputCss = _self.getTextInputCss();
      if (_inputCss.width === '') {
        _inputCss.width = '100%';
      }
      // _inputCss['font-size'] = '14px';

      var resetLeftBorderRadius = {
        'border-top-left-radius': '3px',
        'border-bottom-left-radius': '3px'
      };
      var resetRightBorderRadius = {
        'border-top-right-radius': '3px',
        'border-bottom-right-radius': '3px'
      };
      var newInput = '';
      if (
        options.columnProperty.isPasswdInput === 'false' &&
        options.columnProperty.addonValue &&
        (options.columnProperty.addonValue.indexOf('addonFront') > -1 ||
          options.columnProperty.addonValue.indexOf('addonEnd') > -1 ||
          options.columnProperty.addonValue.indexOf('addonIcon') > -1)
      ) {
        var addonFrontValue = [];
        if (options.columnProperty.addonValue.indexOf('addonFront') > -1 && options.columnProperty.addonFrontValue) {
          addonFrontValue = options.columnProperty.addonFrontValue.split(',');
        }
        var _frontHtml = '';
        if (addonFrontValue.length > 0) {
          if (addonFrontValue.length === 1) {
            _frontHtml = '<span class="input-group-addon">' + addonFrontValue[0] + '</span>';
          } else {
            var frontDropdownMenu = '';
            $.each(addonFrontValue, function (i, v) {
              frontDropdownMenu += '<li>' + v + '</li>';
            });
            _frontHtml =
              '<div class="well-input-group-addon front-group input-group-btn">' +
              '<div class="dropdown-toggle"><span class="addon-value">' +
              addonFrontValue[0] +
              '</span><i class="iconfont icon-ptkj-xianmiaojiantou-xia"></i></div>' +
              '<ul class="dropdown-menu">' +
              frontDropdownMenu +
              '</ul></div>';
          }
        }

        var addonEndValue = [];
        if (options.columnProperty.addonValue.indexOf('addonEnd') > -1 && options.columnProperty.addonEndValue) {
          addonEndValue = options.columnProperty.addonEndValue.split(',');
        }
        var _endHtml = '';
        if (addonEndValue.length > 0) {
          if (addonEndValue.length === 1) {
            _endHtml = '<span class="input-group-addon">' + addonEndValue[0] + '</span>';
          } else {
            var endDropdownMenu = '';
            $.each(addonEndValue, function (i, v) {
              endDropdownMenu += '<li>' + v + '</li>';
            });
            _endHtml =
              '<div class="well-input-group-addon end-group input-group-btn">' +
              '<div class="dropdown-toggle"><span class="addon-value">' +
              addonEndValue[0] +
              '</span><i class="iconfont icon-ptkj-xianmiaojiantou-xia"></i></div>' +
              '<ul class="dropdown-menu">' +
              endDropdownMenu +
              '</ul></div>';
          }
        }

        var iconHtml = '';
        var iconClass = '';
        if (options.columnProperty.addonValue.indexOf('addonIcon') > -1 && options.columnProperty.chooseAddonIcon) {
          iconHtml =
            '<div class="well-input-icon"><i class="' +
            options.columnProperty.addonIcon +
            ' ' +
            options.columnProperty.chooseAddonIcon +
            '"></i></div>';
          if (options.columnProperty.addonIcon === 'front') {
            iconClass = 'front-icon';
          } else {
            iconClass = 'end-icon';
          }
        }

        newInput =
          '<div class="input-group ' +
          _self.editableClass +
          '" name="' +
          ctlName +
          '">' +
          _frontHtml +
          '<div class="well-input clearfix ' +
          iconClass +
          '"><input type="text" class="form-control" autocomplete="off" placeholder="' +
          options.columnProperty.placeholder +
          '" maxlength="' +
          options.columnProperty.length +
          '">' +
          iconHtml +
          '</div>' +
          _endHtml +
          '</div>';
        _self.$placeHolder.after(newInput);
        _self.$placeHolder.next().css('width', _inputCss.width);
        if (!_frontHtml) {
          _self.$placeHolder.next().find('.form-control').css(resetLeftBorderRadius);
        }
        if (!_endHtml) {
          _self.$placeHolder.next().find('.form-control').css(resetRightBorderRadius);
        }

        var frontMenu = _self.$placeHolder.next().find('.front-group .dropdown-menu');
        var frontMenuWidth = frontMenu.width();
        frontMenu.css('width', frontMenuWidth + 24 + 'px');
        _self.$placeHolder
          .next()
          .find('.front-group .dropdown-toggle')
          .css('width', frontMenuWidth + 'px');
        var endMenu = _self.$placeHolder.next().find('.end-group .dropdown-menu');
        var endMenuWidth = endMenu.width();
        endMenu.css('width', endMenuWidth + 24 + 'px');
        _self.$placeHolder
          .next()
          .find('.end-group .dropdown-toggle')
          .css('width', endMenuWidth + 'px');
      } else {
        var editableElem = document.createElement('input');
        editableElem.setAttribute('type', 'text');
        editableElem.setAttribute('maxlength', options.columnProperty.length);
        editableElem.setAttribute('placeholder', options.columnProperty.placeholder || '');
        if (options.columnProperty.isPasswdInput === 'true') {
          editableElem.setAttribute('type', 'password');
          var passwordInput = document.createElement('div');
          passwordInput.setAttribute('class', 'well-password-input ' + _self.editableClass);
          passwordInput.setAttribute('name', ctlName);
          passwordInput.appendChild(editableElem);
          $(passwordInput).css('width', _inputCss.width);
          _self.$placeHolder.after($(passwordInput));
          $(editableElem).after('<div class="password-eye-btn close"><span class="iconfont icon-wsbs-xianshi"></span></div>');
        } else {
          editableElem.setAttribute('class', _self.editableClass);
          editableElem.setAttribute('name', ctlName);
          _self.$placeHolder.after($(editableElem));
        }
      }

      _self.$editableElem = _self.$placeHolder.next('.' + _self.editableClass);
      _self.$input = _self.$editableElem[0].nodeName === 'INPUT' ? _self.$editableElem : _self.$editableElem.find('input');
      _self.$input.css(_inputCss);
      _self.$placeHolder.siblings('.input-group').find('.well-input-group-addon').css('height', _self.$input.css('height'));

      if (options.columnProperty.showPasswordEye) {
        _self.$textEye = $(
          '<div class="text-password-eye-btn close" style="display: none;"><span class="iconfont icon-wsbs-xianshi"></span></div>'
        );
        _self.$placeHolder.after(_self.$textEye);
        _self.$textEye.parent().css('position', 'relative');
      }
      $('.text-password-eye-btn').on('click', function (e) {
        e.stopPropagation();
        var $this = $(this);
        var $label = $this.siblings('.labelclass');
        var _value = $label.data('value');
        var $span = $this.find('span');
        var _iconClass = $span.attr('class');
        if ($this.hasClass('close')) {
          $label.text(_value).attr('title', _value);
          $this.removeClass('close').addClass('open');
          $span.removeClass(_iconClass).addClass('iconfont icon-wsbs-yincang');
        } else {
          $label.text('******').attr('title', '******');
          $this.removeClass('open').addClass('close');
          $span.removeClass(_iconClass).addClass('iconfont icon-wsbs-xianshi');
        }
      });

      if (options.columnProperty.checkOnBlur) {
        _self.$input.blur(function (event) {
          _self.setValue(_self.getFinallyValue(), false);
        });
      } else {
        var debounceSetValueFn = _.debounce(_self.setValue, 500, {
          leading: false,
          trailing: true
        });
        _self.$input.keyup(function (event) {
          // debounceSetValueFn.call(_self, _self.getFinallyValue(), false);
          _self.setValue(_self.getFinallyValue(), false);
        });
      }

      _self.$input.bind('paste', function (event) {
        window.setTimeout(function () {
          _self.setValue(_self.getFinallyValue(), false); // 设置,再不对元素再进行渲染
        }, 100);
      });
      _self.$input.bind('change', function () {
        _self.setValue(_self.getFinallyValue(), false); // 设置,再不对元素再进行渲染
      });

      $('.well-input-group-addon', _self.$editableElem).on('click', function () {
        $(this).addClass('open').find('.dropdown-menu').slideDown();
      });

      $('.dropdown-menu li', _self.$editableElem).on('click', function (e) {
        e.stopPropagation();
        $(this).parent().slideUp().parent().removeClass('open').find('.addon-value').text($(this).text());
        _self.setValue(_self.getFinallyValue(), false);
      });

      $('.password-eye-btn', _self.$editableElem)
        .off('click')
        .on('click', function (e) {
          e.stopPropagation();
          var $this = $(this);
          var $span = $this.find('span');
          var _iconClass = $span.attr('class');
          var $input = $this.siblings('input');
          if ($this.hasClass('close')) {
            $input.attr('type', 'text');
            $this.removeClass('close').addClass('open');
            $span.removeClass(_iconClass).addClass('iconfont icon-wsbs-yincang');
          } else {
            $input.attr('type', 'password');
            $this.removeClass('open').addClass('close');
            $span.removeClass(_iconClass).addClass('iconfont icon-wsbs-xianshi');
          }
        });

      if (_self.options.toLinkFieldData == 1) {
        _self.create$SearchElement();
      }
      //var overlimit = false;
      // this.$editableElem.on('keyup', function (e) {
      //     var v = $(this).val();
      //     overlimit = v.length == options.columnProperty.length ? (function () {
      //         if (overlimit) {
      //             //超过数量限制，进行提示
      //             //alert('超过数字的最大数量限制了');
      //         }
      //         return true;
      //     })() : false;
      //
      //
      // });
    },

    getFinallyValue: function () {
      var _self = this;
      var _addonValue = _self.options.columnProperty.addonValue;
      var _addonFrontValue_len =
        _self.options.columnProperty.addonFrontValue && _self.options.columnProperty.addonFrontValue.split(',').length;
      var _addonEndValue_len = _self.options.columnProperty.addonEndValue && _self.options.columnProperty.addonEndValue.split(',').length;
      var _addonFrontValue = '',
        _addonEndValue = '';
      if (_addonValue && _addonValue.indexOf('addonFront') > -1 && _addonFrontValue_len > 0) {
        if (_addonFrontValue_len > 1) {
          _addonFrontValue = _self.$input.parent().prev().find('.addon-value').text();
        } else {
          _addonFrontValue = _self.$input.parent().prev().text();
        }
      }
      if (_addonValue && _addonValue.indexOf('addonEnd') > -1 && _addonEndValue_len > 0) {
        if (_addonEndValue_len > 1) {
          _addonEndValue = _self.$input.parent().next().find('.addon-value').text();
        } else {
          _addonEndValue = _self.$input.parent().next().text();
        }
      }
      if (_addonFrontValue || _addonEndValue) {
        var _value = 'front:' + _addonFrontValue + '-' + 'end:' + _addonEndValue;
        _self.setToAddonDisplayColumn(_value);
      }
      return _addonFrontValue + _self.$input.val() + _addonEndValue;
    },

    create$SearchElement: function () {
      var _this = this;
      isInAutoShowInputDiv = false;
      var $searchInput = this.$input;
      // var $inputdroplist;
      // 关闭浏览器提供给输入框的自动完成
      $searchInput.attr('inputdroplist', 'off');

      // 创建自动完成的下拉列表，用于显示服务器返回的数据,插入在搜索按钮的后面，等显示的时候再调整位置
      if (typeof $inputdroplist == 'undefined') {
        $inputdroplist = $('<div class="autocomplete"></div>').hide().appendTo($('body'));

        $inputdroplist.mouseover(function (evt) {
          isInAutoShowInputDiv = true;
        });
        $inputdroplist.mouseout(function (evt) {
          var __this = evt.currentTarget;
          var rect = __this.getBoundingClientRect();

          isInAutoShowInputDiv = false;
          var inDiv = evt.clientX >= rect.left && evt.clientX <= rect.right;
          inDiv = inDiv && evt.clientY >= rect.top && evt.clientY <= rect.bottom;
          if (!inDiv) {
            setTimeout(clear, 350);
          }
        });
      }

      this.$inputdroplist = $inputdroplist;

      // 清空下拉列表的内容并且隐藏下拉列表区
      var clear = function () {
        $inputdroplist.empty().hide();
      };

      // 注册事件，当输入框失去焦点的时候清空下拉列表并隐藏
      $searchInput.blur(function (evt) {
        if (isInAutoShowInputDiv) {
        } else {
          setTimeout(clear, 350);
        }
        isInAutoShowInputDiv = false;
        _this.clearAllSession(); // 先清空所有的请求会话
      });

      // 设置下拉项的高亮背景
      var setCheckedItem = function (_this) {
        // $(_this).parent().removeClass('highlight');
        clearCheckedItem(_this);
        $(_this).addClass('highlight');
      };

      // 设置下拉项的高亮背景
      var getCheckedItem = function () {
        return $inputdroplist.find('.highlight');
      };

      var moveCheckedItem2Next = function () {
        if ($inputdroplist.find('.highlight')[0] == $inputdroplist.find('tr:last')[0]) {
          return;
        }

        var $dom = getCheckedItem();
        if ($dom.size() == 0) {
          setCheckedItem($inputdroplist.find('tr').first());
          return;
        }
        var dom = $dom[0];
        clearCheckedItem($(dom));

        setCheckedItem($(dom).next());
      };
      var moveCheckedItem2Prev = function () {
        if ($inputdroplist.find('.highlight')[0] == $inputdroplist.find('tr:first')[0]) {
          return;
        }
        var dom = $inputdroplist.find('.highlight')[0];
        clearCheckedItem($(dom));

        setCheckedItem($(dom).prev());
      };

      // 清空下拉项的高亮背景
      var clearCheckedItem = function (_this) {
        $(_this).siblings().removeClass('highlight');
        // $(_this).addClass('highlight');
      };

      // 设置从表的值
      var setSubFormValue = function (columnName, columnValue) {
        var datauuid = _this.getDataUuid();
        var cellId = $.dyform.getCellId(datauuid, columnName);
        var control = $.ControlManager.getCtl(cellId);
        if (typeof control == 'undefined' || control == null) {
          return;
        }
        control.setValue(columnValue);
      };

      var ajax_request = function (evt) {
        _this.clearAllSession(); // 先清空所有的请求会话
        var currentSessionId = _this.createSession(); // 创建新的会话
        $searchInput.addClass('input-waiting');

        var fieldValue = $searchInput.val();
        var formUuid = _this.options.columnProperty.formDefinition.uuid;
        var columnName = _this.options.columnProperty.columnName;
        var projection = ['distinct ' + columnName];
        var selection = columnName + " like '%" + fieldValue + "%'";
        $.ajax({
          url: contextPath + '/pt/dyform/data/dynamicQuery',
          data: {
            formUuid: formUuid,
            projection: projection,
            selection: selection,
            selectionArgs: [''],
            groupBy: '',
            having: '',
            orderBy: '',
            firstResult: 0,
            maxResults: 20
          },
          type: 'post',
          dataType: 'json',
          success: function (data) {
            var columnNameArray = columnName.split('_');
            var retColumnName = '';
            for (var i = 1; i < columnNameArray.length; i++) {
              retColumnName = retColumnName + columnNameArray[i].substr(0, 1).toUpperCase() + columnNameArray[i].substr(1).toLowerCase();
            }
            retColumnName = columnNameArray[0] + retColumnName;
            //						var retColumnName = columnName.substr(0,1).toLowerCase()+columnName.substr(1).toUpperCase();
            $searchInput.removeClass('input-waiting');
            if (_this.isSessionTimeout(currentSessionId)) {
              // 会话已被取消
              return;
            }
            clear();
            _this.invoke('beforeAutoCompleteShow', data);
            if (data.length) {
              $inputdroplist.append('<table></table>');
              // 遍历data，添加到自动完成区
              $.each(data, function (index, term) {
                // 创建li标签,添加到下拉列表中
                var termText = '';
                for (var fieldName in term) {
                  termText = term[fieldName];
                  break;
                }
                $('<tr></tr>')
                  .html('<td>' + termText + '</td>')
                  .appendTo($inputdroplist.find('table'))
                  .addClass('clickable')
                  .hover(
                    function () {
                      setCheckedItem(this); // 设置高亮
                    },
                    function () {
                      // setCheckedItem(this);
                      // clearCheckedItem(this);//清空高亮
                    }
                  )
                  .click(function () {
                    if (_this.getPos() == dyControlPos.mainForm) {
                      // $searchInput.val(term[retColumnName]);
                      _this.setValue(termText);
                    } else {
                      setSubFormValue(columnName, termText);
                    }
                    clear(); // 清空下拉项
                  });
              }); // 事件注册完毕
              // 设置下拉列表的位置，然后显示下拉列表

              var inputRect = $searchInput[0].getBoundingClientRect();
              var scroll_top = 0;
              if (document.documentElement && document.documentElement.scrollTop) {
                scroll_top = document.documentElement.scrollTop;
                scrollWidth = document.documentElement.scrollWidth;
              } else {
                scroll_top = document.body.scrollTop;
                /* 某些情况下Chrome不认document.documentElement.scrollTop则对于Chrome的处理。 */
                scrollWidth = document.body.scrollWidth;
              }

              var y = scrollWidth - inputRect.left;
              var z = $inputdroplist.width();
              var left = 0;

              if (y < z) {
                left = inputRect.left - (z - y) - (scrollWidth - formWidth) / 2;
              } else {
                left = inputRect.left;
              }

              $inputdroplist.css({
                position: 'absolute',
                left: left + 'px',
                top: inputRect.top + scroll_top + $searchInput.height() + 10 + 'px',
                'z-index': 9999,
                'overflow-x': 'auto',
                'overflow-y': 'auto',
                height: 300 + 'px',
                border: '1px solid #c5dbec',
                'background-color': '#fff',
                'text-align': 'left'
              });

              if (!_this.isInSubform()) {
                $inputdroplist.css('width', $searchInput.width());
              }

              $inputdroplist.show();
            }

            if (_this.options.afterAutoCompleteShow) {
              _this.options.afterAutoCompleteShow.call(_this, evt);
            }
          },
          error: function () {}
        });
      };

      // timeout的ID
      var timeoutid = null;
      // 点击事件
      $searchInput.click(function (event) {
        if (_this.isReadOnly()) {
          // 只读时不可搜索
          return;
        }
        clearTimeout(timeoutid);
        timeoutid = setTimeout(function () {
          ajax_request.call(this, event);
        }, 500);
      });

      // 改变事件
      $searchInput.change(function () {});

      // 对输入框进行事件注册
      $searchInput
        .keyup(function (event) {
          if (_this.isReadOnly()) {
            // 只读时不可搜索
            return;
          }
          // 字母数字，退格，空格
          if (event.keyCode > 40 || event.keyCode == 8 || event.keyCode == 32) {
            // 首先删除下拉列表中的信息
            clear(); // 清空下拉项
            clearTimeout(timeoutid);
            timeoutid = setTimeout(function () {
              ajax_request.call(this, event);
            }, 500);
          } else if (event.keyCode == 38) {
            // 上
            // CheckedItem = -1 代表鼠标离开
            moveCheckedItem2Prev();
            event.preventDefault();
          } else if (event.keyCode == 40) {
            // 下
            moveCheckedItem2Next();
            event.preventDefault();
          }
        })
        .keypress(function (event) {
          // enter键
          if (event.keyCode == 13) {
            // selectItem(relationFieldMapping);
            // clear();
            // event.preventDefault();
            getCheckedItem().trigger('click');
          }
        })
        .keydown(function (event) {
          // esc键
          if (event.keyCode == 27) {
            clear();
            event.preventDefault();
          }
        });
      // 注册窗口大小改变的事件，重新调整下拉列表的位置
      $(window).resize(function () {
        var ypos = $searchInput.position().top;
        var xpos = $searchInput.position().left;
        $inputdroplist.css('width', $searchInput.css('width'));
        $inputdroplist.css({
          position: 'absolute',
          left: xpos + 'px',
          top: ypos + 'px'
        });
      });
    },
    isSessionTimeout: function (sessionId) {
      if (typeof jqueryWDialogSession[sessionId] == 'undefined') {
        return true;
      } else {
        return false;
      }
    },
    createSession: function () {
      var sessionId = new UUID().id.toLowerCase();
      jqueryWDialogSession[sessionId] = sessionId;
      return sessionId;
    },
    clearAllSession: function () {
      jqueryWDialogSession = {};
    }
  };

  /*
   * TEXTINPUT PLUGIN DEFINITION =========================
   */
  $.fn.wtextInput = function (option) {
    var method = false;
    var args = null;
    if (arguments.length == 2) {
      method = true;
      args = arguments[1];
    }

    if (typeof option == 'string') {
      if (option === 'getObject') {
        // 通过getObject来获取实例

        var $this = $(this);

        var data = $this.data('wtextInput');

        if (data) {
          return data; // 返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    var $this = $(this),
      data = $this.data('wtextInput'),
      options = typeof option == 'object' && option;
    if (!data) {
      data = new TextInput($(this), options);
      $.extend(data, $.wControlInterface);
      $.extend(data, $.wTextCommonMethod);
      $.extend(data, $.TextInput);
      $this.data('wtextInput', data);
      if (options.columnProperty.uninit) {
        return data;
      }
      data.init();
    }
    if (typeof option == 'string') {
      if (method == true && args != null) {
        return data[option](args);
      } else {
        return data[option]();
      }
    } else {
      return data;
    }
  };

  $.fn.wtextInput.Constructor = TextInput;

  $.fn.wtextInput.defaults = {
    columnProperty: columnProperty, // 字段属性
    commonProperty: commonProperty, // 公共属性
    readOnly: false,
    disabled: false,
    isHide: false, // 是否隐藏
    isShowAsLabel: false,
    formulas: {}
  };
})(jQuery);
var jqueryWDialogSession = {};
