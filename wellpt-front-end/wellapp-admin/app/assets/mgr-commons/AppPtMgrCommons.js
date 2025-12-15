/**
 * 平台管理_公共功能
 */
define(['jquery', 'server', 'commons', 'constant', 'jquery-form'], function ($, server, commons, constant) {
  var JDS = server.JDS;
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  var AppPtMgrCommons = function () {};

  AppPtMgrCommons.json2form = function (options) {
    var object = options.json;
    var checkboxesAsString = options.checkboxesAsString;
    var $container = options.container;
    // 如果对象不存在属性直接忽略
    for (var property in object) {
      $(':input[name=' + property + ']', $container).each(function () {
        var type = this.type;
        var name = this.name;
        var data = object[name];
        switch (type) {
          case undefined:
          case 'text':
          case 'password':
          case 'hidden':
          case 'button':
          case 'reset':
          case 'textarea':
          case 'submit': {
            if ($(this).parent().hasClass('date')) {
              $(this).next().val(data);
            }
            $(this).val(data);
            break;
          }
          case 'checkbox':
          case 'radio': {
            this.checked = false;
            if ($.isArray(data) === true) {
              // checkbox
              // multiple value is Array
              for (var el in data) {
                if (data[el] == $(this).val()) {
                  this.checked = true;
                }
              }
            } else {
              // radio is a string single value
              if (type == 'radio') {
                if (data == $(this).val()) {
                  this.checked = true;
                }
              } else {
                if (checkboxesAsString && checkboxesAsString === true) {
                  data = data == null ? '' : data;
                  var a = data.split(',');
                  for (var el in a) {
                    if (a[el] == $(this).val()) {
                      this.checked = true;
                    }
                  }
                } else {
                  // 当个checkbox只有true、false
                  this.checked = data == true || data == 'true';
                }
              }
            }
            break;
          }
          case 'select':
          case 'select-one':
          case 'select-multiple': {
            $(this).find('option:selected').attr('selected', false);
            if ($.isArray(data) === true) {
              for (var el in data) {
                $(this)
                  .find("option[value='" + data[el] + "']")
                  .attr('selected', true);
              }
            } else {
              $(this)
                .find("option[value='" + data + "']")
                .attr('selected', true);
            }
            break;
          }
        }
      });
    }
  };

  AppPtMgrCommons.form2json = function (options) {
    var object = options.json;
    var checkboxesAsString = options.checkboxesAsString;
    var $container = options.container;
    // 如果表单元素不存在直接忽略
    for (var property in object) {
      if ($.isArray(options.ignorePropertys) && $.inArray(property, options.ignorePropertys) != -1) {
        //忽略的属性取值
        continue;
      }
      var elements = $(':input[name=' + property + ']', $container);
      if (elements.length == 0) {
        continue;
      }
      // 单个元素
      if (elements.length == 1) {
        var element = elements[0];
        var v = fieldValue(element);
        if (v && v.constructor == Array) {
          var a = [];
          for (var i = 0, max = v.length; i < max; i++) {
            a.push(v[i]);
          }
          object[property] = a;
        } else {
          if (v !== null && typeof v != 'undefined') {
            object[property] = v;
          }
          // checkbox只有一个值(true、false)
          if (element.type == 'checkbox') {
            if (element.checked === true) {
              object[property] = true;
            } else {
              object[property] = false;
            }
          }
        }
      }
      // 多个元素
      if (elements.length > 1) {
        var a = [];
        for (var i = 0; i < elements.length; i++) {
          var element = elements[i];
          var v = fieldValue(element);
          if (v && v.constructor == Array) {
            for (var i = 0, max = v.length; i < max; i++) {
              a.push(v[i]);
            }
          } else {
            if (v !== null && typeof v != 'undefined') {
              a.push(v);
            }
          }
        }
        // 单选框只有一个值
        if (elements[0].type == 'radio') {
          object[property] = a.join();
        } else {
          if (elements[0].type == 'checkbox' && checkboxesAsString && checkboxesAsString === true) {
            object[property] = a.join();
          } else {
            object[property] = a;
          }
        }
      }
    }
  };

  /**
   * Returns the value of the field element.
   */
  function fieldValue(el, successful) {
    var n = el.name,
      t = el.type,
      tag = el.tagName.toLowerCase();
    if (successful === undefined) {
      successful = true;
    }

    if (
      successful &&
      (!n ||
        el.disabled ||
        t == 'reset' ||
        t == 'button' ||
        ((t == 'checkbox' || t == 'radio') && !el.checked) ||
        ((t == 'submit' || t == 'image') && el.form && el.form.clk != el) ||
        (tag == 'select' && el.selectedIndex == -1))
    ) {
      return null;
    }

    if (tag == 'select') {
      var index = el.selectedIndex;
      if (index < 0) {
        return null;
      }
      var a = [],
        ops = el.options;
      var one = t == 'select-one' || t == 'text';
      var max = one ? index + 1 : ops.length;
      for (var i = one ? index : 0; i < max; i++) {
        var op = ops[i];
        if (op.selected) {
          var v = op.value;
          if (!v) {
            // extra pain for IE...
            v = op.attributes && op.attributes['value'] && !op.attributes['value'].specified ? op.text : op.value;
          }
          if (one) {
            return v;
          }
          a.push(v);
        }
      }
      return a;
    }
    return $(el).val();
  }

  // 清空表单
  AppPtMgrCommons.clearForm = function (options) {
    var container = options.container || document;
    $(container).clearForm(options.includeHidden);
    if ($.isFunction(options.afterClear)) {
      options.afterClear();
    }
  };

  //表单不可编辑
  AppPtMgrCommons.uneditableForm = function (options, editable) {
    var container = options.container || document;
    $(container)
      .find(':input')
      .prop('readonly', editable == undefined ? true : Boolean(editable));
  };

  // ID生成器
  var idGenerator = {
    generate: function (selector, prefix) {
      var id = prefix;
      JDS.call({
        service: 'idGeneratorService.getBySysDate',
        async: false,
        version: '',
        success: function (result) {
          prefix += result.data;
        }
      });
      if (selector) {
        $(selector).val(prefix);
        $(selector).focus();
        $(selector).trigger('focusin');
      }
      return prefix;
    }
  };
  AppPtMgrCommons.idGenerator = idGenerator;

  // html
  var html = {
    // 创建DIV元素
    createDiv: function (selector) {
      var id = selector;
      if (selector.indexOf('#') == 0) {
        id = selector.substring(1);
      }
      // 放置
      if ($(selector).length == 0) {
        $('body').append("<div id='" + id + "' />");
      }
    }
  };
  AppPtMgrCommons.html = html;

  return AppPtMgrCommons;
});
