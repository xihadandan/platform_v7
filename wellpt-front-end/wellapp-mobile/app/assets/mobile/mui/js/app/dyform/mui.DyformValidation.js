define(['mui', 'constant', 'commons', 'server', 'mui-DyformCommons'], function ($, constant, commons, server, DyformCommons) {
  $.sliderTab = function (element) {
    var MUI_ACTIVE = 'mui-active';
    if (!element || element.classList.contains(MUI_ACTIVE)) {
      return;
    }
    var target = element.getAttribute('href');
    if (!target) {
      return;
    }
    var href = target.replace('#', '');
    var targetTab = $('.mui-control-content[id=' + href + ']')[0];
    if (!targetTab) {
      return;
    }
    var tabNumber = -1;
    $('.mui-control-item', element.parentNode).each(function (idx, elem) {
      if (elem === element) {
        tabNumber = idx;
      }
      elem.classList.remove(MUI_ACTIVE);
    });
    $('.mui-control-content', targetTab.parentNode).each(function (idx, elem) {
      elem.classList.remove(MUI_ACTIVE);
    });
    var slider = $('.layout-slider').slider();
    if (slider && slider.gotoItem) {
      // TODO soft code .layout-slider
      slider.gotoItem(tabNumber, undefined);
    } else {
      element.classList.add(MUI_ACTIVE);
      targetTab.classList.add(MUI_ACTIVE);
      $.trigger(targetTab, $.eventName('shown', name), {
        tabNumber: tabNumber
      });
    }
  };
  var StringUtils = commons.StringUtils;
  // 约束条件
  var fieldCheckRule = {
    notBlank: '1', // "非空"
    unique: '5', // "唯一"
    common: '10', // 文本样式，校验规则
    url: '11', // "url",
    email: '12', // "email",
    idCardNumber: '13', // "身份证",
    telephone: '14', // "固定电话",
    mobilePhone: '15' // "手机"
  };

  var errorHintBubble = '<div class="bubble-tip mui-hidden" style="z-index: 999;">';
  errorHintBubble += '<div class="bubble-header mui-hidden">非法参数列表</div>';
  errorHintBubble += '<div class="bubble-content"><ul class="mui-table-view bubble-view"></ul></div>';
  errorHintBubble += '<div class="bubble-footer"></div>';
  errorHintBubble += '</div>'; // mui-popover
  var errorTouchMask = '<div class="mui-backdrop bubble-mask mui-hidden" style="z-index: 998;">';
  var errorTouchBubble = '<div class="bubble-touch mui-hidden" draggable="true" style="z-index: 1000;">';
  errorTouchBubble += '<div class="bubble-touch-inner"></div></div>';
  var Bubble = function (panel) {
    var self = this;
    var $bubbleTip = $('div.bubble-tip');
    var $bubbleMask = $('div.bubble-mask');
    var $bubbleTouch = $('div.bubble-touch');
    if ($bubbleTip.length <= 0 || $bubbleMask.length <= 0 || $bubbleTouch.length <= 0) {
      $bubbleTip = DyformCommons.dom(errorHintBubble);
      $bubbleMask = DyformCommons.dom(errorTouchMask);
      $bubbleTouch = DyformCommons.dom(errorTouchBubble);
      if (!panel) {
        panel = $('body')[0];
      }
      panel.appendChild($bubbleTip[0]);
      panel.appendChild($bubbleMask[0]);
      panel.appendChild($bubbleTouch[0]);
    }
    var tip = (self.tip = $bubbleTip[0]);
    var mask = (self.mask = $bubbleMask[0]);
    var touch = (self.touch = $bubbleTouch[0]);
    // 防止移动穿透
    tip.addEventListener($.EVENT_MOVE, $.preventDefault);
    // 蒙版,防止点击穿透和任意点击隐藏
    mask.addEventListener($.EVENT_MOVE, $.preventDefault);
    mask.addEventListener('tap', function (event) {
      this._hide();
    });
    mask._show = function (event) {
      mask._shown = true;
      mask.classList.remove('mui-hidden');
      tip.classList.remove('mui-hidden');
    };
    mask._hide = function (event) {
      mask._shown = false;
      tip.classList.add('mui-hidden');
      mask.classList.add('mui-hidden');
    };
    // 圆点移动
    touch.addEventListener(
      'touchstart',
      function (event) {
        // console.log(event);
        touch._drap = false; // 触摸,非拖动
        // 修改拖动元素的透明度
        touch.classList.toggle('active');
      },
      false
    );
    // move
    var boxTop = 44 + 10;
    var boxWidth = 10 + 24;
    var boxButtom = 50 + 4 + 48;
    var cWidth = window.screen.width;
    var cHeight = window.screen.height;
    touch.addEventListener(
      'touchmove',
      function (event) {
        // console.log(event);
        touch._drap = true; // 拖动
        event.preventDefault();
        event.stopPropagation();
        if (event.targetTouches.length <= 0) {
          return false;
        }
        var eventTouch = event.targetTouches[0];
        var x = eventTouch.clientX - 24;
        var y = eventTouch.clientY - 24;
        touch.style.top = y + 'px';
        touch.style.left = x + 'px';
      },
      false
    );
    touch.addEventListener(
      'touchend',
      function (event) {
        // console.log(event);
        touch.classList.remove('active');
        if (touch._drap === false) {
          return;
        }
        event.preventDefault();
        event.stopPropagation();
        var x = touch.style.left;
        if (x === 'auto') {
          x = cWidth - touch.style.right;
        }
        x = parseInt(x, 10);
        var y = parseInt(touch.style.top, 10);
        var leftOrRight = boxTop < y && y < cHeight - boxButtom;
        if ((leftOrRight && cWidth / 2 < x) || x > cWidth - boxWidth) {
          touch.style.left = 'auto';
          touch.style.right = '10px';
        } else if (leftOrRight || x < boxWidth) {
          touch.style.right = 'auto';
          touch.style.left = '10px';
        }
        if (y < boxTop) {
          touch.style.top = boxTop + 'px';
        } else if (y > cHeight - boxButtom) {
          touch.style.top = cHeight - boxButtom + 'px';
        }
      },
      false
    );
    // 圆点触摸
    touch.addEventListener('tap', function (event) {
      event.preventDefault();
      event.stopPropagation();
      if (mask._shown) {
        mask._hide();
      } else {
        mask._show();
      }
    });
    self.list = tip.querySelector('.bubble-view');
  };

  $.extend(Bubble.prototype, {
    constructor: Bubble,
    show: function (css) {
      var self = this;
      self.mask._show();
      self.touch.classList.remove('mui-hidden');
    },
    // 隐藏
    hide: function (event) {
      var self = this;
      self.mask._hide();
      self.touch.classList.add('mui-hidden');
    },
    // 关闭
    close: function () {
      var self = this;
      self.clear();
      self.hide();
    },
    // 添加错误条目data={"id":"", title:"", displayName:""}
    addErrorItem: function (data, callback) {
      var self = this;
      self.removeErrorItemIfNeed(data.fieldName); // 如果有的话，移出
      // + "&nbsp;&nbsp;|&nbsp;&nbsp;" +data.message
      var html = DyformCommons.dom(
        "<li class='mui-table-view-cell'><a id='" +
          data.fieldName +
          "' title='" +
          data.message +
          "' class='tip-item'>" +
          data.displayName +
          "<span class='bubble-error'>" +
          data.message +
          '</span></a></li>'
      )[0];
      var fn = function (event) {
        event.preventDefault();
        event.stopPropagation();
        self.mask._hide();
        self.touch.classList.remove('mui-hidden');
        if ($.isFunction(callback)) {
          callback.call(this, data, event);
        }
      };
      var tipItem = html.querySelector('a.tip-item');
      // tipItem.addEventListener("tap", fn);
      tipItem.addEventListener('tap', fn);
      self.list.appendChild(html);
    },
    // 删除错误条目
    removeErrorItemIfNeed: function (id) {
      var self = this,
        item = self.list.querySelector("a[id='" + id + "']");
      item && self.list.removeChild(item.parentNode);
    },
    // 删除错误条目
    removeErrorItem: function (id) {
      var self = this;
      self.removeErrorItemIfNeed(id);
      if (self.itemCount() == 0) {
        // 如果条目被删除完毕则自动关闭
        self.close();
      }
    },
    clear: function () {
      this.list.innerHTML = '';
    },
    itemCount: function () {
      var self = this;
      return $('a.tip-item', self.list).length;
    }
  });

  // 验证结果
  var ValidationResult = function (dyform, errors) {
    this.dyform = dyform;
    this.errors = errors;
  };
  $.extend(ValidationResult.prototype, {
    // 是否存在错误
    hasErrors: function () {
      return this.errors != null && this.errors.length != 0;
    },
    // 返回错误信息列表
    getErrors: function () {
      return this.errors;
    }
  });

  // 检验器
  var Validator = function (rule, fn) {
    this.rule = rule;
    this.fn = fn;
  };
  $.extend(Validator.prototype, {
    // 验证字段
    validate: function (field) {
      var result = this.fn.call(this, field.getValue(), field);
      result.checkedRule = this.rule;
      return result;
    },
    // 判断是否必填规则
    isRequired: function () {
      return this.rule === dyCheckRule.notNull;
    }
  });

  var regUrl =
    /^(https?|s?ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i;
  var regMail =
    /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i;
  var regMobilePhone = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1})|(147)|(170)|(176)|(177)|(178))+\d{8})$/;
  // 表单验证
  var DyformValidation = function (dyform) {
    var self = this;
    self.bubble = new Bubble($(dyform.element)[0]);
    self.dyform = dyform;
    self.methods = {};
    self.init();
  };
  $.extend(DyformValidation.prototype, {
    // 初始化
    init: function () {
      // 注册内置验证器
      // 1、非空
      this.methods[dyCheckRule.notNull] = function (value, field) {
        var notBlankTip = {
          valid: false, // false
          // 表示校验不通过，true表示校验通过
          tipMsg: '非空' // 当校验不通过时，提示的信息
        };
        if (field.isValueMap() && $.isPlainObject(value) && JSON.stringify(value) === '{}') {
          return notBlankTip;
        }
        if (($.isArray(value) && value.length <= 0) || StringUtils.isBlank(value) || value == '{}') {
          return notBlankTip;
        }
        return {
          valid: true
        };
      };
      // 5、唯一
      this.methods[dyCheckRule.unique] = function (value, field) {
        var self = this;
        var fieldName = field.getName();
        var result = {
          valid: true
        };
        var blocks = field.dyform.formData.blocks;
        $.each(field.dyform.formFields, function (i, ofield) {
          var blockHide = false;
          $.each(blocks, function (idx, obj) {
            if (obj.blockCode === ofield.getBlockCode) {
              blockHide = obj.hide;
            }
          });
          // 从表数据
          if (ofield.isSubform()) {
          } else if (fieldName !== ofield.getName() && value === ofield.getValue() && !blockHide) {
            // 主表数据
            result = {
              valid: false, // false
              // 表示校验不通过，true表示校验通过
              tipMsg: '唯一' // 当校验不通过时，提示的信息
            };
            return false;
          }
        });
        return result;
      };
      // 10、文本样式，校验规则, 检查不要超过最大字段长度的要求
      this.methods[dyCheckRule.common] = function (value, field) {
        // 大文本、三种附件不检查字段长度
        var inputMode = field.definition.inputMode;
        var dbDataType = field.definition.dbDataType;
        if (
          (inputMode == dyFormInputMode.textArea && dbDataType == dyFormDataType.clob) ||
          inputMode == dyFormInputMode.accessoryImg ||
          inputMode == dyFormInputMode.accessory3 ||
          inputMode == dyFormInputMode.accessory1
        ) {
          return {
            valid: true
          };
        }
        var maxLength = parseInt(field.definition.length);
        // 有定义最大字段要求则检查
        if (maxLength > 0 && value) {
          var v = value;
          if (typeof value == 'object') {
            v = JSON.stringify(value);
          }
          if (v.length > maxLength) {
            return {
              valid: false,
              tipMsg: '长度不能大于' + maxLength + '个字符'
            };
          }
        }
        // 其他情况均返回成功
        return {
          valid: true
        };
      };
      // 11、url
      this.methods[dyCheckRule.url] = function (value, field) {
        if (StringUtils.isBlank(value) || regUrl.test(value)) {
          return {
            valid: true
          };
        }
        return {
          valid: false, // false
          // 表示校验不通过，true表示校验通过
          tipMsg: '非有效的URL地址' // 当校验不通过时，提示的信息
        };
      };
      // 12、email
      this.methods[dyCheckRule.email] = function (value, field) {
        if (StringUtils.isBlank(value) || regMail.test(value)) {
          return {
            valid: true
          };
        }
        return {
          valid: false, // false
          // 表示校验不通过，true表示校验通过
          tipMsg: '非有效的email地址' // 当校验不通过时，提示的信息
        };
      };
      // 13、身份证
      this.methods[dyCheckRule.idCard] = function (value, field) {
        // 身份证号码的验证规则
        function isIdCardNo(num) {
          // if (isNaN(num))
          // {alert("输入的不是数字！"); return
          // false;}
          var len = num.length,
            re;
          if (len == 15) re = new RegExp(/^(\d{6})()?(\d{2})(\d{2})(\d{2})(\d{2})(\w)$/);
          else if (len == 18) re = new RegExp(/^(\d{6})()?(\d{4})(\d{2})(\d{2})(\d{3})(\w)$/);
          else {
            // alert("输入的数字位数不对。");
            return false;
          }
          var a = num.match(re);
          if (a != null) {
            if (len == 15) {
              var D = new Date('19' + a[3] + '/' + a[4] + '/' + a[5]);
              var B = D.getYear() == a[3] && D.getMonth() + 1 == a[4] && D.getDate() == a[5];
            } else {
              var D = new Date(a[3] + '/' + a[4] + '/' + a[5]);
              var B = D.getFullYear() == a[3] && D.getMonth() + 1 == a[4] && D.getDate() == a[5];
            }
            if (!B) {
              // alert("输入的身份证号 "+ a[0] +"
              // 里出生日期不对。");
              return false;
            }
          }
          if (!re.test(num)) {
            // alert("身份证最后一位只能是数字和字母。");
            return false;
          }
          return true;
        }
        if (StringUtils.isBlank(value) || isIdCardNo(value)) {
          return {
            valid: true
          };
        }
        return {
          valid: false, // false
          // 表示校验不通过，true表示校验通过
          tipMsg: '非有效的身份证' // 当校验不通过时，提示的信息
        };
      };
      // 14、固定电话
      this.methods[dyCheckRule.tel] = function (value, field) {
        var tel = /^(\d{3,4}-?)?\d{7,9}$/g;
        if (StringUtils.isBlank(value) || tel.test(value)) {
          return {
            valid: true
          };
        }
        return {
          valid: false, // false
          // 表示校验不通过，true表示校验通过
          tipMsg: '非有效的电话号' // 当校验不通过时，提示的信息
        };
      };
      // 15、手机
      this.methods[dyCheckRule.mobilePhone] = function (value, field) {
        var length = value.length;
        if (StringUtils.isBlank(value) || (length == 11 && regMobilePhone.test(value))) {
          return {
            valid: true
          };
        }
        return {
          valid: false, // false
          // 表示校验不通过，true表示校验通过
          tipMsg: '非有效的手机号' // 当校验不通过时，提示的信息
        };
      };
      // 16、整数
      this.methods[dyCheckRule.num_int] = function (value, field) {
        value = value + '';
        var n13 = /^[-\+]?\d+$/;
        if (StringUtils.isBlank(value) || (n13.test(value) && value.length <= 9)) {
          return {
            valid: true
          };
        }
        return {
          valid: false, // false
          tipMsg: '整数' // 当校验不通过时，提示的信息
        };
      };
      // 17、正整数
      this.methods[dyCheckRule.num_int_positive] = function (value, field) {
        value = value + '';
        var n13 = /^[-\+]?\d+$/;
        if (StringUtils.isBlank(value) || (n13.test(value) && parseInt(value) >= 0)) {
          return {
            valid: true
          };
        }
        return {
          valid: false, // false
          tipMsg: '正整数' // 当校验不通过时，提示的信息
        };
      };
      // 18、负整数
      this.methods[dyCheckRule.num_int_negtive] = function (value, field) {
        value = value + '';
        var n13 = /^[-\+]?\d+$/;
        if (StringUtils.isBlank(value) || (n13.test(value) && parseInt(value) <= 0)) {
          return {
            valid: true
          };
        }
        return {
          valid: false, // false
          tipMsg: '负整数' // 当校验不通过时，提示的信息
        };
      };
      // 19、长整数
      this.methods[dyCheckRule.num_long] = function (value, field) {
        value = value + '';
        var n13 = /^[-\+]?\d+$/;
        if (StringUtils.isBlank(value) || (n13.test(value) && value.length <= 16)) {
          return {
            valid: true
          };
        }
        return {
          valid: false, // false
          tipMsg: '长整数' // 当校验不通过时，提示的信息
        };
      };
      // 20、浮点数
      this.methods[dyCheckRule.num_float] = function (value, field) {
        value = value + '';
        var n15 = /^[-\+]?\d+(\.\d+)?$/;
        if (StringUtils.isBlank(value) || (n15.test(value) && value.length <= 12)) {
          return {
            valid: true
          };
        }
        return {
          valid: false, // false
          tipMsg: '浮点数' // 当校验不通过时，提示的信息
        };
      };
      // 21、双精度浮点数
      this.methods[dyCheckRule.num_double] = function (value, field) {
        value = value + '';
        var n15 = /^[-\+]?\d+(\.\d+)?$/;
        if (StringUtils.isBlank(value) || (n15.test(value) && value.length <= 18)) {
          return {
            valid: true
          };
        }
        return {
          valid: false, // false
          tipMsg: '双精度浮点数' // 当校验不通过时，提示的信息
        };
      };
    },
    // 根据字段检验规则创建验证器
    createRequiredValidator: function (checkRule) {
      return new Validator(1, this.methods[checkRule.value]);
    },
    // 根据字段检验规则创建验证器
    createValidator: function (checkRule) {
      return new Validator(checkRule.value, this.methods[checkRule.value]);
    },
    // 创建自定义的验证器
    createCustomValidator: function (fn) {
      return new Validator('custom', fn);
    },
    // 验证处理
    validate: function (bubble) {
      console.log('validate==============');
      var _self = this;
      var errors = [];
      var formFields = _self.dyform.getFields();
      $.each(formFields, function (i, formField) {
        if (formField.isSubform()) {
          var subformFieldErrors = _self._validateSubform(formField);
          errors = errors.concat(subformFieldErrors);
        } else {
          var validResults = formField.validate();
          errors = errors.concat(validResults);
        }
      });
      var validationResult = new ValidationResult(this.dyform, errors);
      if (bubble && validationResult.hasErrors()) {
        _self.bubble.clear(); // 先清空
        var dErrors = validationResult.getErrors();
        $.each(dErrors, function (i, data) {
          _self.bubble.addErrorItem(data, function (data, event) {
            var code = data.field.getBlockCode();
            var activeTab = $('a.mui-control-item[code=' + code + ']')[0];
            $.sliderTab(activeTab);
            var focusElement = null;
            if (data.field.$editableElem) {
              focusElement = data.field.$editableElem[0];
            } else {
              focusElement = data.field.$labelElem[0];
            }
            if (focusElement) {
              // focusElement.scrollIntoView(true); // alignToTop
              // focusElement.select();
              $.ui.scrollIntoView(focusElement);
              focusElement.focus();
            }
          });
        });
        _self.bubble.show();
      } else {
        _self.bubble.close();
      }
      return validationResult;
    },
    // 验证从表
    _validateSubform: function (subform) {
      var errors = [];
      var formFields = subform.getFields();
      $.each(formFields, function (i, formField) {
        var validResults = formField.validate();
        errors = errors.concat(validResults);
      });
      return errors;
    }
  });

  return DyformValidation;
});
