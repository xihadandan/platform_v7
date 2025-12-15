//加载全局国际化资源
//I18nLoader.load("/resources/pt/js/global");
//加载动态表单定义模块国际化资源
//I18nLoader.load("/resources/pt/js/dyform/dyform");

var cacheType = {
  formUuid: 1, // 主表的formuuid
  options: 2, // 表单的选项参数
  formDefinition: 3, // 主表、从表的定义
  dataUuid: 4, // 主表的datauuid,
  deletedFormDataOfSubform: 'deletedFormDataOfSubform', // 被删除的从表的数据uuid
  control: 'control',
  formula: 'formula', // 运算公式
  initDisplay: 'initDisplay' //初始化关联显示值控件
};

/**
 * 表单插件
 */
$(function () {
  $.dyform = $.dyform || {};
  $.dyform.customScriptDir = $.dyform.customScriptDir || {}; // 自定义JS目录
  $.extend($.dyform, {
    getAccessor: function (obj, expr) {
      var ret,
        p,
        prm = [],
        i;
      if (typeof expr === 'function') {
        return expr(obj);
      }
      ret = obj[expr];
      if (ret === undefined) {
        try {
          if (typeof expr === 'string') {
            prm = expr.split('.');
          }
          i = prm.length;
          if (i) {
            ret = obj;
            while (ret && i--) {
              p = prm.shift();
              ret = ret[p];
            }
          }
        } catch (e) {}
      }
      return ret;
    },
    extend: function (options) {
      $.extend($.fn.dyform, options);
      if (!this.no_legacy_api) {
        $.fn.extend(options);
      }
    },

    /**
     * 置于jqgrid中，从表的各cell的id
     */
    getCellId: function (rowId, fieldName) {
      return rowId + '___' + fieldName;
      if ($.trim(rowId).length) {
        return rowId + '___' + fieldName;
      }
      return fieldName;
    },
    setValue: function (control, cellValue, dataUuid, formData) {
      if (!control) {
        return;
      }
      control.setDataUuid(dataUuid);
      control.setValue(cellValue, true, formData);
    },
    getValue: function (control) {
      if (control == null) {
        console.log('control is null');
        return;
      }
      var value = '';
      if (control.isValueMap()) {
        // 这些控件类型有显示值，需要将显示值及真实值一起保存至数据库中
        var valueMap = control.getValue();
        if (typeof valueMap == 'string') {
          value = valueMap;
        } else {
          value = JSON.cStringify(control.getValue());
        }
      } else if (control.isAttachCtl && control.isAttachCtl()) {
        control.getValue(function (values) {
          value = values;
        });
      } else {
        value = control.getValue();
      }
      return value;
    },
    getDisplayValue: function (control) {
      if (control == null) {
        console.log('control is null');
        return;
      }
      var value = '';
      if (control.isValueMap()) {
        // 这些控件类型有显示值，需要将显示值及真实值一起保存至数据库中
        value = control.getDisplayValue();
      } else if (control.isAttachCtl && control.isAttachCtl()) {
        control.getValue(function (values) {
          value = values;
        });
      } else {
        value = control.getValue();
      }
      return value;
    },
    getRealValue: function (control) {
      if (control == null) {
        console.log('control is null');
        return '';
      }
      var value = control.getValue(); // 返回string
      if (control.isValueMap()) {
        if (typeof value == 'undefined' || value.trim().length == 0) {
          value = '';
        } else {
          var valueMap = jQuery.parseJSON(value);
          var values = '';
          for (var key in valueMap) {
            values += key + ';';
          }
          if (values.indexof(';') < 0) {
            value = values; // valueMap ->- {} ->- ""
          } else {
            value = values.substring(0, values.length - 1);
          }
        }
      }
      return value;
    },
    modelReg: /\${([^}]*)}/i, // 显示单据占位符
    formulaReg: /\${([^}]*)}/g, // 运算符中字段占位符
    getValidateIgnoreKey: function (formId, fieldName) {
      return $.trim(formId) + ':' + $.trim(fieldName);
    },
    isValidateIgnore: function (fieldName, formId) {
      if (typeof $.dyform['validateIgnoreField'] == 'undefined') {
        return false;
      }
      var key = $.dyform.getValidateIgnoreKey(formId, fieldName);
      if (key in $.dyform.validateIgnoreField) {
        return true;
      } else {
        return false;
      }
      return false;
    },
    // 设置字段验证或者不验证
    // fields为{"formId":"",
    // "fieldName":""}数组,数组成员由formId和字段名称组成，中间用冒号连接。如:{"formId":"uf_xzsp_bjsq",
    // "fieldName":"sqr"}
    // enable为true表示需要验证,enable为false表示不验证
    enableFieldValidate: function (fields, enable) {
      if (typeof $.dyform['validateIgnoreField'] == 'undefined') {
        $.dyform.validateIgnoreField = {};
      }
      for (var i = 0; i < fields.length; i++) {
        var field = fields[i];
        var key = $.dyform.getValidateIgnoreKey(field.formId, field.fieldName);
        if (enable) {
          // 需要验证
          delete $.dyform.validateIgnoreField[key];
        } else {
          // 不需要验证
          $.dyform.validateIgnoreField[key] = true; // 不需要校验的字段
        }
      }
    },
    objectLength: function (obj) {
      var count = 0;
      for (var i in obj) {
        count++;
      }
      return count;
    },
    loadScript: function (url) {
      var time1 = new Date().getTime();
      var getFrontCache = window.getFrontCache;
      if (getFrontCache) {
        if (url.indexOf('?') > 0) {
          url += '&t=' + getFrontCache('dyform');
        } else {
          url += '?t=' + getFrontCache('dyform');
        }
      }
      var script = '';
      $.ajax({
        url: ctx + url,
        // cache: false,
        async: false, // 同步完成
        type: 'GET',
        dataType: 'text',
        contentType: 'text/html;charset=utf-8;',
        success: function (result) {
          script = result;
        },
        error: function (jqXHR) {}
      });
      var time2 = new Date().getTime();
      console.log('LOAD script(' + url + ') spent:' + (time2 - time1) / 1000 + 'S');
      return script;
    },
    nss: function (ns, selector) {
      // namespaceSelector
      if ($.trim(ns).length) {
        return selector + '[data-ns="' + ns + '"]';
      }
      return selector + ':not([data-ns])';
    },
    wns: function (ns, html) {
      // wrapNamespace
      // 添加命名空间
      var $wrap = $('<div>').html(html);
      $wrap.find('.value[name]').each(function (idx, element) {
        var name = $(element).attr('name');
        $(element).attr('data-ns', ns);
        $(element).attr('fieldName', name);
        $(element).attr('name', $.trim(ns).length > 0 ? $.dyform.getCellId(ns, name) : name);
      });
      $wrap.find('.tab-design[name]').each(function (idx, element) {
        var name = $(element).attr('name');
        $(element).attr('data-ns', ns);
        $(element).attr('layoutName', name);
        $(element).attr('name', $.trim(ns).length > 0 ? $.dyform.getCellId(ns, name) : name);
      });
      $wrap.find('table[formUuid]').each(function (idx, element) {
        var id = $(element).attr('formUuid') || $(element).attr('id');
        $(element).attr('data-ns', ns);
        $(element).attr('formUuid', id);
        $(element).attr('id', $.trim(ns).length > 0 ? $.dyform.getCellId(ns, id) : id);
      });
      $wrap.find('.template-wrapper[templateUuid]').each(function (idx, element) {
        var id = $(element).attr('templateUuid') || $(element).attr('id');
        $(element).attr('data-ns', ns);
        $(element).attr('templateUuid', id);
        $(element).attr('id', $.trim(ns).length > 0 ? $.dyform.getCellId(ns, id) : id);
      });

      $wrap.find('table[filelibraryid], table[tableviewid]').each(function (idx, element) {
        $(element).attr('data-ns', ns);
      });

      return $wrap.html();
    },
    parseRequireModules: function (formDefinition) {
      console.log('开始加载表单JS模块');
      var _this = this;
      var time1 = new Date().getTime();
      if (typeof formDefinition == 'string') {
        // 字段串类型
        formDefinition = eval('(' + formDefinition + ')');
      }
      var requireModules = [],
        formDefinitions = [formDefinition];
      var subformDefinitions = formDefinition['subformDefinitions'];
      if ($.isPlainObject(formDefinition.layouts) || $.isPlainObject(formDefinition.blocks)) {
        requireModules.push('wContainerManager');
      }
      if ($.isArray(subformDefinitions) && subformDefinitions.length > 0) {
        requireModules.push('wSubForm');
        requireModules.push('wSubForm4Group');
        for (var i = 0; i < subformDefinitions.length; i++) {
          formDefinitions.push(eval('(' + subformDefinitions[i] + ')'));
        }
      }
      for (var i = 0; i < formDefinitions.length; i++) {
        var fields = formDefinitions[i].fields;
        for (var fieldName in fields) {
          var field = fields[fieldName];
          var requireModules2 = field && dyformFieldModule[field.inputMode];
          if ($.trim(requireModules2).length <= 0) {
            continue;
          }
          $.each(requireModules2.split(','), function (idx, requireModule) {
            if ($.inArray(requireModule, requireModules) < 0) {
              requireModules.push(requireModule);
            }
          });
        }
      }
      console.log('结束加载表单JS模块' + (new Date().getTime() - time1) / 1000.0 + 's');
      return requireModules;
    },
    warn: (function (win) {
      var warn = win.console && console.warn;
      return false === $.isFunction(warn)
        ? $.noop
        : function (msg) {
            warn.apply(win, arguments);
          };
    })(window)
  });

  $.fn.dyform = function (options) {
    if (this.size() == 0) {
      throw new Error('zero elements found by selector[' + this.selector + ']');
    }
    if (this.size() > 1) {
      throw new Error('more than one element found by selector[' + this.selector + ']');
    } else if (this[0].nodeName != 'FORM') {
      // throw new Error("dataform container must be a form html element");
    }

    this.attr('class', 'dyform');

    if (typeof options == 'string') {
      // 字段串类型
      var fn = $.dyform.getAccessor($.fn.dyform, options);
      if (!fn) {
        throw 'jqGrid - No such method: ' + options;
      }
      var args = $.makeArray(arguments).slice(1);
      return fn.apply(this, args);
    } else {
      // 如果只传定义,默认为定义解析接口
      // this.dyform("parseFormDefinition", arguments);

      var fn = $.dyform.getAccessor($.fn.dyform, 'parseForm');
      return fn.apply(this, arguments);
    }
  };

  /**
   * 对外接口
   */
  $.dyform.extend(
    /**
     * @author Administrator
     *
     */
    {
      $: function (selector) {
        return $(selector, this[0]);
      },
      getNamespace: function () {
        var self = this;
        return self.getOption().namespace;
      },
      getCellId: function (fieldName) {
        var self = this;
        var ns = self.getNamespace();
        return $.trim(ns).length > 0 ? $.dyform.getCellId(ns, fieldName) : fieldName;
      },
      addCAControlObject: function () {
        if (this.isEnableSignature() && $.browser && $.browser.msie) {
        }
      },
      /**
       * 判断block是否设置两个以上的锚点，是：增加左侧导航
       */
      hasBlockAnchor: function (formDefinition) {
        var blocks = $.extend({}, formDefinition.blocks);
        var anchors = [];

        for (var templateUuid in formDefinition.templates) {
          if (!templateUuid) {
            continue;
          }

          var templateDefinition;
          if (typeof definitions == 'undefined' || typeof definitions[templateUuid] == 'undefined') {
            templateDefinition = loadFormDefinition(templateUuid);
          } else {
            templateDefinition = definitions[templateUuid];
          }
          $.extend(blocks, templateDefinition.blocks);
        }

        for (var i in blocks) {
          if (blocks[i].blockAnchor && !blocks[i].hide) {
            anchors.push({
              blockAnchor: blocks[i].blockAnchor,
              blockTitle: blocks[i].blockTitle,
              blockCode: blocks[i].blockCode,
              blockHide: blocks[i].hide
            });
          }
        }
        var self = this;
        var ns = self.getOption().namespace;
        if (anchors.length >= 2) {
          var isExtend = Browser.getQueryString('isExtend');
          var _html;
          if (isExtend == '1') {
            _html = formDefinition.product_html;
          } else {
            _html = formDefinition.html;
          }
          this.isAnchor = true;
          var html = '<div class="wellpt_form_container">' + _html + '</div>', // 获取表单标签，替换blockcode为id
            $formContainer = $('<div id="left" ></div>'), // 表单主容器
            $ulHtml = '<ul class="wellpt_form_anchor">'; // 左侧导航栏

          $.each($(_html).find('.title[blockCode]'), function (index, item) {
            var code = $(item).attr('blockCode');
            if (!blocks[code].hide && blocks[code].blockAnchor) {
              $ulHtml += '<li data-code="' + code + '">' + blocks[code].blockTitle + '</li>';
            }
          });

          $ulHtml += '</ul>';

          var toTop = '<div class="backToTop">回到顶部</div>';

          $formContainer.append($ulHtml, html, toTop);
          $formContainer.find('li[data-code]:eq(0)').addClass('anchor_active');

          if (isExtend == '1') {
            formDefinition.product_html = $($formContainer[0]).html();
          } else {
            formDefinition.html = $($formContainer[0]).html();
          }
        }
      },
      /**
       * 将dyform的jquery对象存储到全局变量$.dyform中，提供给外部使用,例如二开文件DyformFacade使用
       */
      cache$Dyform: function (formId, namespace) {
        $.dyform['$dyform'] = $.dyform['$dyform'] || {};
        $.dyform['$dyform'][namespace || formId] = this;
      },
      parseForm: function (options) {
        console.log('开始解析表单定义');
        var _this = this;
        var time1 = new Date().getTime();
        var formDefinition = options.formData.formDefinition;

        this.cache$Dyform(formDefinition.id, options.namespace);

        if (typeof formDefinition == 'string') {
          // 字段串类型
          formDefinition = eval('(' + formDefinition + ')');
        }
        var ns = options.namespace; // 定义命名空间

        if ($.trim(ns).length) {
          _this.attr('data-ns', ns);
        } else {
          _this.removeAttr('data-ns', ns);
        }
        var data = options.formData.formDatas;
        var defaults = {
          displayAsLabel: false, // 是否以标签的形式展示
          displayAsFormModel: true, // 是否用单据展示,在displayAsLabel为true的前提下，
          // 该参数才有效,true时表示使用显示单据,false表示不使用显示单据，直接使用表单的模板
          success: function () {},
          async: false,
          error: function () {}
        };

        $.extend(defaults, options);

        // 将当前容器的对应的表单定义相关数据缓存起来,当需要获取缓存中的数据时，就需要通过key去获取
        // key是什么，需要到缓存$.dyform.cache代码去看,方便统一管理缓存
        this.cache.put.call(this, cacheType.formUuid, formDefinition.uuid);
        $.extend(formDefinition, formDefinitionMethod);
        // 更新树形
        formDefinition.updateReverseFormTree(ns);

        this.cache.put.call(this, cacheType.formDefinition, formDefinition);
        this.cache.put.call(this, cacheType.options, defaults);
        this.setDataUuid(options.formData.dataUuid);
        var definitionObjs = formDefinition['subformDefinitions'];
        var definitionMap = {};

        if (typeof definitionObjs != 'undefined' && definitionObjs != null && definitionObjs.length > 0) {
          for (var i = 0; i < definitionObjs.length; i++) {
            var definitionObj = eval('(' + definitionObjs[i] + ')');
            $.extend(definitionObj, formDefinitionMethod);
            definitionMap[definitionObj.uuid] = definitionObj;
            this.cache.put.call(this, cacheType.formDefinition, definitionObj);
          }
        }

        this.addCAControlObject();

        this.validSignatureUSB();

        this.hasBlockAnchor(formDefinition);

        var displayFormModelId = formDefinition.displayFormModelId;

        if (this.isDisplayAsModel()) {
          // 判断是否用显示单据
          var model = loadDisplayModelDefintionByModelId(displayFormModelId);
          if (model == null || model == undefined) {
            // 无法找到对应的单据
            defaults.error.call(this, dymsg.modelIdNotFound);
            return;
          }
          $(this).html($.dyform.wns(ns, model.html));
          this.formatModel(formDefinition); // 将单据信息格式化成表单解析的格式
        } else {
          // 去掉占位符中的src属性，改由后端处理
          var html = "<span id='_htmldyform_'>" + formDefinition.html + '</span>';

          var placeHolderImgPattern1 = /<img[^>]+class=[\"value\"|\'value\'][^>]+name=[\'|\"]([a-zA-Z_][a-zA-Z0-9_]*)[\'|\"][^>]+>/i;
          var placeHolderImgPattern2 = /<img[^>]+name=[\'|\"]([a-zA-Z_][a-zA-Z0-9_]*)[\'|\"][^>]+class=[\"value\"|\'value\'][^>]+>/i;

          // modify by wujx 20160729 begin
          while (placeHolderImgPattern1.test(html)) {
            var r = html.match(placeHolderImgPattern1);
            if (r != null) {
              html = html.replace(r[0], "<span class='value' name='" + r[1] + "'></span>");
            }
          }

          while (placeHolderImgPattern2.test(html)) {
            var r = html.match(placeHolderImgPattern2);
            if (r != null) {
              html = html.replace(r[0], "<span class='value' name='" + r[1] + "'></span>");
            }
          }
          // modify by wujx 20160729 end

          $(this).html($.dyform.wns(ns, $(html).html()));
          var isScroll = false;
          var isClickScroll = false;

          $('li', '.wellpt_form_anchor').on('click', function () {
            if (!isClickScroll) {
              isClickScroll = true;
            }
            var $this = $(this);
            $this.parent().children('li').removeClass('anchor_active');
            $this.addClass('anchor_active');

            var blockCode = $this.data('code');
            var $blockCode =
              $('.wellpt_form_container td[blockCode=' + blockCode + ']').length > 0
                ? $('.wellpt_form_container td[blockCode=' + blockCode + ']')
                : $('.wellpt_form_container th[blockCode=' + blockCode + ']');
            var $block = $blockCode.closest('table');

            var offset = $block.offset();
            var offsetTop = offset.top;

            var bodyPaddingTop = parseInt($('body').css('padding-top'));
            var widgetPaddingTop = parseInt($('.widget-body').css('padding-top')) || 0;
            var paddingTop = bodyPaddingTop + widgetPaddingTop;

            var $parent = $block.parent();
            var parentScrollable = false;

            while (!parentScrollable && $parent.get(0)) {
              if ($parent[0].scrollHeight > $parent[0].clientHeight) {
                parentScrollable = true;
              } else {
                $parent = $parent.parent();
              }
            }

            if ($parent) {
              var $scroll = $parent[0];
              if (scrollWidgetIsBody) {
                window.scrollTo({
                  top: offsetTop,
                  left: 0,
                  behavior: 'smooth'
                });
              } else {
                // console.log(offsetTop - paddingTop)
                $scroll.scrollBy({
                  top: offsetTop - paddingTop,
                  left: 0,
                  behavior: 'smooth'
                });
              }
            }
          });

          var $container = $('.wellpt_form_container');
          var $anchors = $('.wellpt_form_anchor');

          var $scrollWidget = $('.widget-main');
          var $widgetFooter;
          var scrollWidgetIsBody = false;

          if ($scrollWidget.length) {
            $widgetFooter = $('.widget-box .footer');
          } else {
            $('.dyform').addClass('clearfix');
            $scrollWidget = $container.parent();
            var parentScrollable = false;

            while (!parentScrollable && $scrollWidget.get(0)) {
              if ($scrollWidget[0].scrollHeight > $scrollWidget[0].clientHeight && $scrollWidget[0].clientHeight > 0) {
                parentScrollable = true;
              } else {
                $scrollWidget = $scrollWidget.parent();
              }
            }

            if (!$scrollWidget.length) {
              $scrollWidget = $('body');
            }

            if ($scrollWidget.is('body')) {
              scrollWidgetIsBody = true;
            }
          }

          var bodyPaddingTop = parseInt($('body').css('padding-top'));
          var widgetPaddingTop = parseInt($scrollWidget.css('padding-top'));
          var containerTop = bodyPaddingTop + widgetPaddingTop;

          var containerBottom =
            $widgetFooter && $widgetFooter.length ? $widgetFooter.offset().top : $scrollWidget.offset().top + $scrollWidget.height();
          var currentScrollTop = $scrollWidget.scrollTop();

          var getBlockPositionOfViewport = function (blockCode) {
            var $block =
              $container.find('td[blockAnchor=true][blockCode=' + blockCode + ']').length > 0
                ? $container.find('td[blockAnchor=true][blockCode=' + blockCode + ']')
                : $container.find('th[blockAnchor=true][blockCode=' + blockCode + ']');
            var blockHeight = $block.height();
            var blockTop = $block.offset().top;
            var blockBottom = blockTop + blockHeight;

            if (scrollWidgetIsBody) {
              containerTop = document.documentElement.scrollTop || window.pageYOffset || document.body.scrollTop;
              containerBottom = containerTop + document.body.clientHeight;
            }

            if (blockTop >= containerTop && blockBottom <= containerBottom) {
              return 'inner';
            } else if (blockTop < containerTop) {
              return 'above';
            } else {
              return 'below';
            }
          };

          function scrollEvent(e) {
            if (isScroll) return;

            // 获得滚动方向
            if (scrollWidgetIsBody) {
              scrollTop = document.documentElement.scrollTop || window.pageYOffset || document.body.scrollTop;
            } else {
              scrollTop = $scrollWidget.scrollTop();
            }

            var direction = scrollTop > currentScrollTop ? 'down' : 'up';
            currentScrollTop = scrollTop;

            // 调整 `回到顶部` 按钮
            if (scrollTop > 800) {
              $('.backToTop').slideDown(200);
            } else {
              $('.backToTop').slideUp(200);
            }

            // 调整锚点
            if (direction === 'down') {
              var $activeAnchor = $anchors.find('.anchor_active');
              var $nextAnchor = $activeAnchor.next('li');
              if (!$nextAnchor.length) {
                return;
              }

              // 往下滚动的时候激活下一个锚点的条件：
              // 当前锚点内容(区块顶部)不在视口 +下个锚点的内容(区块顶部)在视口
              var activeAnchorPosition = getBlockPositionOfViewport($activeAnchor.attr('data-code'));
              var nextAnchorPosition = getBlockPositionOfViewport($nextAnchor.attr('data-code'));
              if (activeAnchorPosition === 'above' && nextAnchorPosition === 'inner') {
                // debugger;
                $activeAnchor.removeClass('anchor_active');
                $nextAnchor.addClass('anchor_active');
              }
            } else {
              var $activeAnchor = $anchors.find('.anchor_active');
              var $prevAnchor = $activeAnchor.prev('li');
              if (!$prevAnchor.length) {
                return;
              }

              // 往上滚动的时候切激活上一个锚点的条件：
              // 当前锚点内容(区块顶部)不在视口
              if (!isClickScroll) {
                var activeAnchorPosition = getBlockPositionOfViewport($activeAnchor.attr('data-code'));
                if (activeAnchorPosition === 'below') {
                  $activeAnchor.removeClass('anchor_active');
                  $prevAnchor.addClass('anchor_active');
                }
              }
              var $block =
                $container.find('td[blockAnchor=true][blockCode=' + $activeAnchor.attr('data-code') + ']').length > 0
                  ? $container.find('td[blockAnchor=true][blockCode=' + $activeAnchor.attr('data-code') + ']')
                  : $container.find('th[blockAnchor=true][blockCode=' + $activeAnchor.attr('data-code') + ']');
              var blockTop = $block[0].offsetTop;

              // console.log(scrollTop, blockTop, isClickScroll)

              if (blockTop > scrollTop - 7) {
                setTimeout(function () {
                  isClickScroll = false;
                }, 0);
              }
            }
          }

          if (scrollWidgetIsBody) {
            window.onscroll = _.throttle(scrollEvent, 20);
          } else {
            $scrollWidget.on('scroll', _.throttle(scrollEvent, 20));
          }

          $('.backToTop').on('click', function () {
            if (scrollWidgetIsBody) {
              window.scrollTo({
                top: 0,
                left: 0,
                behavior: 'smooth'
              });
            } else {
              $scrollWidget[0].scrollTo({
                top: 0,
                left: 0,
                behavior: 'smooth'
              });
            }
          });

          if (formDefinition.hasTemplate()) {
            this.parseTemplate(formDefinition.templates);
          }
        }

        $('.label').removeClass('label');

        // 移出子表单占位符
        this.$($.dyform.nss(ns, '.template-wrapper[templateUuid]')).each(function () {
          var $this = $(this);
          $this.replaceWith($this.find('>.inner-box').children());
        });

        var time1 = new Date().getTime();

        var formData = formDefinition['defaultFormData'];

        var time2 = new Date().getTime();
        console.log('加载默认值及从表定义所用时间:' + (time2 - time1) / 1000.0 + 's');

        // 加载自定义JS
        this.loadCustomJS(formDefinition.id);

        /**
         * ************用户通过前台页面写的自定义JS 2015/07/09 by hunt begin
         * ***************
         */
        if (formDefinition.customJsNew && $.trim(formDefinition.customJsNew).length > 0) {
          try {
            eval(formDefinition.customJsNew);
          } catch (e) {
            console.error(e);
          }
        }
        /**
         * ************用户通过前台页面写的自定义JS 2015/07/09 by hunt end
         * ***************
         */

        /** **2017/05/09 add by hunt begin*** */
        // 初始化时带入的默认事件参数
        var defaultEventNames = ['beforeParseForm', 'beforeSetData', 'afterSetData'];
        for (var i = 0; i < defaultEventNames.length; i++) {
          if (!this.isEventExist(defaultEventNames[i])) {
            // 二开文件中绑定的事件优先级更高
            if (typeof this.getOption()[defaultEventNames[i]] == 'function') {
              // 初始化时，有初始化该事件
              // console.debug("dyform invoke
              // event['"+defaultEventNames[i]+"'] when
              // init");
              this.bind2Dyform(defaultEventNames[i], this.getOption()[defaultEventNames[i]]);
            }
          } else {
            // console.debug("dyform
            // event['"+defaultEventNames[i]+"'] is customed ");
          }
        }

        /** **2017/05/09 add by hunt end*** */

        this.invoke('beforeParseForm', this);

        this.parseMainForm(formDefinition, formData); // 解析主表
        this.parseBlock(formDefinition);
        this.parseTips(formDefinition);

        this.parseSubform(formDefinition, definitionMap); // 解析从表

        // 解析视图列表
        this.parseTableView(
          formDefinition,
          (function () {
            return !$.isEmptyObject(options.formData.formDatas) ? options.formData.formDatas[options.formData.formUuid][0] : null;
          })()
        );

        // 解析文件库列表
        this.parseFileLibrary(
          formDefinition,
          (function () {
            return !$.isEmptyObject(options.formData.formDatas) ? options.formData.formDatas[options.formData.formUuid][0] : null;
          })()
        );

        var time2 = new Date().getTime();
        console.log('表单解析所花时间:' + (time2 - time1) / 1000.0 + 's');
        this.trigger('DyformCreationComplete');
        $(this).addClass('clearfix');

        function fillFormData() {
          _this.fillFormData(data, {
            async: defaults.async,
            callback: function () {
              var time3 = new Date().getTime();
              console.log('表单填充数据所花时间:' + (time3 - time2) / 1000.0 + 's');
              var timex = new Date().getTime();
              if (_this.isDisplayAsLabel()) {
                // 显示为标签
                _this.showAsLabel();
              }
              var timey = new Date().getTime();
              console.log('显示为标签所用时间:' + (timey - timex) / 1000.0 + 's');

              $(window).resize(function () {
                if (null == _this.context) {
                  // TODO处理内存泄漏
                  return;
                }
                _this.autoWidth();
              });

              var callSuccessCallback = function () {
                var timey = new Date();

                try {
                  if (typeof _this[formDefinition.id] == 'function') {
                    _this[formDefinition.id](); // 初始化扩展函数,在dyform_custom.js文件中
                  } else {
                  }
                } catch (e) {
                  console.log(e);
                }

                var timez = new Date();
                console.log('自定义js所花时间:' + (timez - timey) / 1000.0 + 's');

                if (_this.isCreate()) {
                  _this.addRowEmptyDatasByDefaultRowCount(); // 给从表添加默认行
                  var timed = new Date();
                  console.log('添加默认行数:' + (timed - timez) / 1000.0 + 's');
                }
                var timez = new Date();
                console.log('表单共花时间:' + (timez - time1) / 1000.0 + 's');

                //数据管理查看器直接查看表单数据的情况下
                if (appContext.getWindowManager().triggerParentWidgetEvent) {
                  appContext.getWindowManager().triggerParentWidgetEvent(Browser.getQueryString('dms_id'), 'dyformViewLoadSuccess');
                }
              };

              if (!defaults.async) {
                // 同步
                var _waitForSubforms = appContext._waitForSubforms || {};
                var syncCallback = function () {
                  if (!$.isEmptyObject(_waitForSubforms)) {
                    setTimeout(function () {
                      syncCallback.call(_this);
                    }, 200);
                  } else {
                    _this.callSuccess(callSuccessCallback);
                  }
                };
                syncCallback.call(_this);
                // _this.callSuccess(callSuccessCallback);
              } else {
                // 异步
                window.setTimeout(function () {
                  _this.callSuccess(callSuccessCallback);
                }, 40);
              }
            }
          });
        }

        // 插入数据之前的脚本
        this.invoke('beforeSetData', this);

        var dyformEvents;

        if (formDefinition.events && formDefinition.events != '') {
          dyformEvents = eval('(' + formDefinition.events + ')');
          if (dyformEvents['beforeSetData_event']) {
            appContext.eval(dyformEvents['beforeSetData_event'], _this, {
              $this: _this
            });
          }
        }

        // 插入数据
        try {
          fillFormData();
        } catch (e) {
          console.error(e.stack);
        }

        // 插入数据之后的脚本
        if (this.find('.table-form-item .table-form-body').length) {
          console.log('从表未渲染完......');
        } else {
          console.log('表单渲染完毕......');
          var parentDyform = _this.parents('.dyform');
          if (parentDyform.length && this.isSubformComplete()) {
            parentDyform.invoke('afterSetData', parentDyform);
          } else {
            this.invoke('afterSetData', this);
          }
        }

        // 绑定F2手机预览事件
        this.bindPreviewMobileView(options.formData);
        this.parseTab(formDefinition);

        if ($.dyform.noInitValidate !== true) {
          // 防止二次填充数据出发校验
          for (var ctlName in $.ControlManager) {
            var ctrlObj = $.ControlManager[ctlName];
            var control = ctrlObj && ctrlObj.control;
            if (control && control.initValidate) {
              control.initValidate(true);
            }
          }
        }

        this.afterParseForm(formDefinition);
        _this.getOption().complete = true;
        appModal.hideMask();
        if (_this.getOption().btnEventOptions) {
          this.setBtnEventToFileCtl();
        }

        localStorage.setItem('afterParseForm', true);

        return this;
      },

      isSubformComplete: function () {
        var _this = this;
        var parentDyform = _this.parents('.dyform');
        if (parentDyform.length) {
          if (parentDyform.find('.table-form-item .table-form-body').length) {
            return false;
          }
        }
        return true;
      },

      afterParseForm: function (formDefinition) {
        var _this = this;
        var ns = _this.getOption().namespace;

        _this.$($.dyform.nss(ns, '.value[fieldName]')).each(function () {
          var fieldName = $(this).attr('fieldName');
          var control = _this.getControl(fieldName);
          if (typeof control == 'undefined' || control == null) {
            return true; // continue;
          }
          control.initControlOver && control.initControlOver(this, fieldName);
        });
        if ($('#wf_form').length > 0 && $('#wf_form').parent('.widget-main').length > 0) {
          //表单滚动条，侧边tab打开状态并且不分屏,滚动条不显示，其他情况都显示
          var scollHeight = $(window).height();
          $('#wf_form')
            .parent('.widget-main')
            .height(scollHeight - 90)
            .niceScroll({
              oneaxismousemode: false,
              cursorcolor: '#ccc',
              cursorwidth: '10px',
              scrollCLass: 'formScrollClass'
            });

          $('#wf_form')
            .parent('.widget-main')
            .on('scroll', function () {
              _this.removePopup();
            });

          var tabActive = $('.open-tab').hasClass('open-tab-active');
          var stackActive = $('.stick').hasClass('stick-active');
          var zIndex = -1;
          if (stackActive || !tabActive) {
            zIndex = 80;
          }
          $('body').find('.formScrollClass').css({
            zIndex: zIndex
          });
          setTimeout(function () {
            $('#wf_form').parent('.widget-main').getNiceScroll().resize();
          }, 1000);
          $(document)
            .on('filechange', function (e) {
              setTimeout(function () {
                $('#wf_form').parent('.widget-main').getNiceScroll().resize();
              }, 100);
            })
            .on('fileViewChange', function (e) {
              setTimeout(function () {
                $('#wf_form').parent('.widget-main').getNiceScroll().resize();
              }, 100);
            })
            .on('shown.bs.collapse', function (e) {
              setTimeout(function () {
                $('#wf_form').parent('.widget-main').getNiceScroll().resize();
              }, 100);
            })
            .on('hidden.bs.collapse', function (e) {
              setTimeout(function () {
                $('#wf_form').parent('.widget-main').getNiceScroll().resize();
              }, 100);
            })
            .on('pageSizeOnChange', function (e) {
              console.log('pageSizeOnChange');
              setTimeout(function () {
                $('#wf_form').parent('.widget-main').getNiceScroll().resize();
              }, 1000);
            });
        }

        this.setPlaceholdersVisibility(formDefinition.placeholderCtr);
      },

      /**
       * 绑定F2手机预览事件
       */
      bindPreviewMobileView: function (formData) {
        var _this = this;
        window.setTimeout(function () {
          // 添加事件F2快捷键功能
          $(_this.context).bind('keydown', function (event) {
            // F2
            if (113 == event.keyCode) {
              $.ajax({
                type: 'POST',
                url: ctx + '/dyform/isDefMobile.action',
                data: {
                  formUuid: _this.getFormUuid()
                },
                dataType: 'json',
                success: function (data) {
                  if (true == data) {
                    openWindowByPost(
                      ctx + '/pt/dyform/data/previewMobileView.action',
                      {
                        previewMobileDyformJsonStr: JSON.stringify({
                          formUuid: _this.getFormUuid(),
                          dataUuid: _this.getDataUuid(),
                          dUtils: formData.formDefinition,
                          formDatas: _this.getFormDatas()
                        })
                      },
                      _this.getFormUuid() + _this.getDataUuid()
                    );
                  } else {
                    appModal.error('该表单没有设置手机定义');
                  }
                },
                error: function (result) {
                  var responseText = result.responseText;
                  responseText = eval('(' + responseText + ')');
                  appModal.error(responseText.data);
                }
              });
            }
          });
        }, 10);
      },

      resetSubformWidth: function () {
        var _this = this;
        var formUuid = _this.getFormUuid();
        var formDefinition = _this.cache.get.call(_this, cacheType.formDefinition, formUuid);

        var subforms = formDefinition.subforms;
        for (var i in subforms) {
          var subform = subforms[i];
          var subformctl = _this.getSubformControl(subform.formUuid);
          // subformctl.setGridWidth(formWidth);
          if (subformctl) subformctl.resetGridWidth();
        }
      },

      // 加载自定义JS
      loadCustomJS: function (formId) {
        var customJSDir = $.dyform.customScriptDir;
        if (!customJSDir) {
          $.dyform.customScriptDir = {};
          customJSDir = $.dyform.customScriptDir;
        }

        var customScriptURL = customJSDir[formId];
        if (customScriptURL) {
          var scriptUrls = [];
          if (!(customScriptURL instanceof Array)) {
            scriptUrls = [customScriptURL];
          } else {
            scriptUrls = customScriptURL;
          }
          for (var i in scriptUrls) {
            var customScript = $.dyform.loadScript(scriptUrls[i]);
            if ($.trim(customScript).length > 0) {
              try {
                eval(customScript);
              } catch (e) {
                console.error(e);
              }
            }
          }
        }
      },
      callSuccess: function (callback) {
        var timex = new Date();
        this.getOption().success.apply(this);
        var timey = new Date();
        console.log('回调所花时间:' + (timey - timex) / 1000.0 + 's');
        if (!this.getOption().async) {
          // 同步
          callback();
        } else {
          window.setTimeout(function () {
            callback();
          }, 40);
        }
      },

      // 获取可选参数
      getOptional: function () {
        var options = this.getOption();
        return options && options.optional;
      },

      getOption: function () {
        var options = this.cache.get.call(this, cacheType.options);
        return options;
      },

      // 是否新建
      isCreate: function () {
        if (this.getOptional() && this.getOptional().isFirst) {
          return true;
        } else {
          return false;
        }
      },
      getDyformTitle: function (success, error, formUuid, dataUuid) {
        var self = this;
        var optional = self.getOptional() || {};
        if ($.trim(optional.title)) {
          return success.call(self, optional.title);
        }
        formUuid = formUuid || '';
        dataUuid = dataUuid || '';
        self.collectFormData(function (formDatas) {
          $.ajax({
            url: ctx + '/pt/dyform/data/getDyformTitle?formUuid=' + formUuid + '&dataUuid=' + dataUuid,
            type: 'POST',
            data: JSON.cStringify(formDatas),
            dataType: 'json',
            async: false,
            contentType: 'application/json',
            success: function (result) {
              if (result.success) {
                success.call(self, result.data);
              } else if (error) {
                error.apply(self, arguments);
              }
            },
            error: error
          });
        }, error);
      },

      formatModel: function (formDefinition) {
        var _this = this;
        var ns = _this.getOption().namespace;
        $($.dyform.nss(ns, '.model_field[fieldName]')).each(function () {
          var $this = this;
          var name = $this.attr('fieldName');
          if (name == undefined && $.trim(name).length == 0) {
            return true;
          }

          var fieldDefinition = formDefinition.getField(name);
          if (typeof fieldDefinition == 'undefined') {
            return true; // continue;
          }

          fieldDefinition.showType = dyshowType.showAsLabel; // 显示为标签
          // 将占位符转换成表单解析的格式
          var titleHtml = $this.html();
          titleHtml = titleHtml.replace($.dyform.modelReg, "<span class='value' name='" + name + "'></span>");
          $this.html(titleHtml);
        });

        // var _this = this;
        // 含有formUuid属性的table即为从表,解析各从表
        $($.dyform.nss(ns, 'table[formUuid]')).each(function () {
          $.ControlManager.createSubFormControl($(this), _this, formDefinition);
        });

        $($.dyform.nss(ns, '.model_subform[fieldName]')).each(function () {
          var $this = $(this);
          var name = $this.attr('fieldName');
          if (name == undefined && $.trim(name).length == 0) {
            return true;
          }
          var subDefinitions = formDefinition.subforms;
          if (typeof subDefinitions == 'undefined' || subDefinitions.length == 0) {
            return true; // continue;
          }
          var formUuid = undefined;
          for (var i in subDefinitions) {
            var subDefinition = subDefinitions[i];
            if (subDefinition.id == name) {
              formUuid = subDefinition.formUuid;
              break;
            }
          }
          if (!formUuid) {
            return true;
          }

          var titleHtml = $this.html();
          titleHtml = titleHtml.replace($.dyform.modelReg, "<table formUuid='" + formUuid + "'></table>");
          $this.html(titleHtml);
        });
      },

      setPlaceholdersVisibility: function (placeholders) {
        var ns = this.getNamespace();
        for (var placeholder in placeholders) {
          if (placeholders[placeholder].showType === '5') {
            var $placeholder = $($.dyform.nss(ns, '[fieldname=' + placeholder + ']'));
            $placeholder.closest('td').hide();
            $placeholder.closest('td').prev('td').hide();
          }
        }
      },

      isDisplayAsModel: function () {
        var displayFormModelId = this.getFormDefinition(this.getFormUuid()).displayFormModelId;
        var options = this.getOption();
        if (
          options.displayAsFormModel == true &&
          options.displayAsLabel == true &&
          displayFormModelId != undefined &&
          $.trim(displayFormModelId).length > 0
        ) {
          return true;
        } else {
          return false;
        }
        // return options.displayAsFormModel == true;
      },

      isDisplayAsLabel: function () {
        var options = this.getOption();
        return options.displayAsLabel;
        // return options.displayAsFormModel == true;
      },

      /**
       * 给主表设置Uuid
       *
       * @param dataUuid
       */
      setDataUuid: function (dataUuid) {
        if (dataUuid) {
          this.cache.put.call(this, cacheType.dataUuid, dataUuid);
        }
      },
      /**
       * 获取主表Uuid
       *
       * @param dataUuid
       */
      getDataUuid: function () {
        return this.cache.get.call(this, cacheType.dataUuid);
      },
      /**
       * 获取主表的定义id
       */
      getFormUuid: function (formId) {
        if (formId) {
          // 从表
          var formDefinitions = this.getFormDefinition(); // 所有的表单定义
          for (var i in formDefinitions) {
            if (formDefinitions[i].id == formId) {
              return i;
            }
          }
        } else {
          // 主表
          return this.cache.get.call(this, cacheType.formUuid);
        }
      },
      /**
       * 获取主表的定义id
       */
      getFormId: function (formUuid) {
        if (formUuid) {
          // 从表
          var formDefinitions = this.getFormDefinition(); // 所有的表单定义
          for (var i in formDefinitions) {
            if (formDefinitions[i].uuid == formUuid) {
              return formDefinitions[i].id;
            }
          }
        } else {
          // 主表
          return this.getFormId(this.getFormUuid()); // 迭代获取FormId
        }
      },
      getFormDefinition: function (formUuid) {
        var formDefinition = this.cache.get.call(this, cacheType.formDefinition, formUuid);
        return formDefinition;
      },

      getMainformValueKeySetMap: function () {
        var keyMap = {};
        var fields = this.getFormDefinition(this.getFormUuid()).fields;
        for (var field in fields) {
          // radio表单元素
          // valuemap,树形下拉框valuemap,checkbox表单元素valuemap,下拉单选框valuemap
          var inputMode = fields[field].inputMode;
          if (
            inputMode == dyFormInputMode.treeSelect ||
            inputMode == dyFormInputMode.radio ||
            inputMode == dyFormInputMode.checkbox ||
            inputMode == dyFormInputMode.selectMutilFase
          ) {
            var optionSet = fields[field].optionSet;
            if (typeof optionSet == 'undefined' || optionSet == null || optionSet.length == 0) {
              console.log('field[' + field + '] optionSet undefined');
              continue;
            }
            var obj = {};
            for (var key in optionSet) {
              var value = optionSet[key]; // 键值对换
              obj[value] = key;
            }
            keyMap[field] = obj;
          }
        }
        return keyMap;
      },

      getMainformValueFormatSetMap: function () {
        var keyMap = {};
        var fields = this.getFormDefinition(this.getFormUuid()).fields;
        for (var field in fields) {
          var inputMode = fields[field].inputMode;
          if (inputMode == dyFormInputMode.date) {
            var fmt = fields[field].contentFormat;
            if (typeof fmt == 'undefined' || fmt == null) {
              console.log('field[' + field + '] contentFormat undefined');
              continue;
            }
            var format = '';
            if (fmt == dyDateFmt.yearMonthDate) {
              format = 'yyyy-MM-dd';
            } else if (fmt == dyDateFmt.dateTimeHour) {
              format = 'yyyy-MM-dd HH';
            } else if (fmt == dyDateFmt.dateTimeMin) {
              format = 'yyyy-MM-dd HH:mm';
            } else if (fmt == dyDateFmt.dateTimeSec) {
              format = 'yyyy-MM-dd HH:mm:ss';
            } else if (fmt == dyDateFmt.timeHour) {
              format = 'HH';
            } else if (fmt == dyDateFmt.timeMin) {
              format = 'HH:mm';
            } else if (fmt == dyDateFmt.timeSec) {
              format = 'HH:mm:ss';
            } else if (fmt == dyDateFmt.yearMonthDateCn) {
              format = 'yyyy年MM月dd日';
            } else if (fmt == dyDateFmt.yearCn) {
              format = 'yyyy年';
            } else if (fmt == dyDateFmt.yearMonthCn) {
              format = 'yyyy年MM月';
            } else if (fmt == dyDateFmt.monthDateCn) {
              format = 'MM月dd日';
            } else if (fmt == dyDateFmt.year) {
              format = 'yyyy';
            }
            keyMap[field] = format;
          }
        }
        return keyMap;
      },
      /**
       * 收集表单数据,包括控件数据、签名、被删除的行数据、正文上传 注意:
       * 表单数据收集应用了jquery的Promise进行异步操作
       *
       * @param success
       *            收集成功回调
       * @param error
       *            收集失败回调
       */
      collectFormData: function (success, error) {
        if (typeof success != 'function') {
          throw new Error('参数错误: 第一个参数须为成功收集的回调函数');
        }
        if (typeof error != 'function') {
          throw new Error('参数错误: 第二个参数须为成功收集的回调函数');
        }
        console.log('开始收集数据-----' + new Date());
        var time1 = new Date().getTime();
        var _this = this;
        var option = _this.getOption();
        if (option == undefined) {
          return;
        }
        var formUuid = _this.getFormUuid();
        if (!option.complete) {
          // 为解析完成，返回初始化的formData对象
          var formData = option.formData;
          formData.formDatas = formData.formDatas || {};
          formData.formDatas[formUuid] = formData.formDatas[formUuid] || {};
          return success.call(_this, {
            formId: formData['formId'],
            formUuid: formData['formUuid'],
            dataUuid: formData['dataUuid'],
            formDatas: formData['formDatas'],
            addedFormDatas: formData['addedFormDatas'],
            updatedFormDatas: formData['updatedFormDatas']
          });
        }
        var formData = {};
        var errorInfo = {};
        var deletedFormDatas = this.getDeletedRowIds();

        this.getFormDatas(
          function (formDatas) {
            // 收集成功回调
            if (_this.isEnableSignature()) {
              // 创建签名
              var signature = _this.createSignature(formData);
              formData.signature = signature;
            }
            formData['formDatas'] = formDatas;
            formData['formUuid'] = _this.getFormUuid();
            formData['dyformDataOptions'] = option.dyformDataOptions;
            formData['deletedFormDatas'] = deletedFormDatas;

            formData['addedFormDatas'] = _this.getOption().formData.addedFormDatas;

            formData['updatedFormDatas'] = _this.getOption().formData.updatedFormDatas;
            var time2 = new Date().getTime();
            console.log('表单收集所花时间:' + (time2 - time1) / 1000.0 + 's');

            if (typeof success == 'function') {
              success.call(_this, formData);
            }
          },
          function (errorInfo) {
            // 收集失败回调
            if (typeof error == 'function') {
              error.call(_this, errorInfo);
            }
          }
        );
      },
      /**
       * 收集表单显示数据,包括控件数据、签名、被删除的行数据、正文上传
       *
       * @param formUuid
       */
      collectFormDisplayData: function () {
        console.log('开始收集数据-----' + new Date());
        var time1 = new Date().getTime();

        var formData = {};
        var deletedFormDatas = this.getDeletedRowIds();
        var formDatas = this.getFormDisplayDatas();

        if (this.isEnableSignature()) {
          // 创建签名
          var signature = this.createSignature(formData);
          formData.signature = signature;
        }

        formData['formDatas'] = formDatas;
        formData['deletedFormDatas'] = deletedFormDatas;
        formData['formUuid'] = this.getFormUuid();
        var time2 = new Date().getTime();
        console.log('表单收集所花时间:' + (time2 - time1) / 1000.0 + 's');
        console.log('收集结束-----' + new Date());
        return formData;
      },

      /**
       * 需要签名返回true
       */
      isEnableSignature: function () {
        var formUuidOfMainform = this.getFormUuid();
        var formDefinitionOfMainform = this.getFormDefinition(formUuidOfMainform);
        var enableSignature = formDefinitionOfMainform.enableSignature;

        if (enableSignature == signature.enable) {
          return true;
        } else {
          return false;
        }
      },

      createSignature: function (signedData) {
        var jsonString = JSON.cStringify(signedData);
        var signature = {};
        $.ajax({
          url: ctx + '/pt/dyform/data/getDigestValue',
          cache: false,
          async: false, // 同步完成
          type: 'POST',
          data: jsonString,
          dataType: 'json',
          contentType: 'application/json',
          success: function (result) {
            var dataSignature = result.data;
            var b = fjcaWs.OpenFJCAUSBKey();
            if (!b) {
              signature.status = -1;
              signature.remark = 'lose to open FJCAUSBKey';
            } else {
              fjcaWs.ReadCertFromKey();
              var cert = fjcaWs.GetCertData();
              fjcaWs.SignDataWithKey(dataSignature.digestValue);
              var signData = fjcaWs.GetSignData();
              fjcaWs.CloseUSBKey();
              signature.signedData = jsonString;
              signature.digestValue = dataSignature.digestValue;
              signature.certificate = cert;
              signature.signatureValue = signData;
              signature.status = 1;
              signature.digestAlgorithm = dataSignature.digestAlgorithm;
            }
          },
          error: function (jqXHR) {
            // 数字签名失败

            console.log('lose to get digestValue');
            var faultData = JSON.parse(jqXHR.responseText);
            signature.status = -1;
            signature.remark = faultData.msg;
          }
        });
        return signature;
      },

      /**
       * 验证表单数据
       *
       * @param formUuid
       */
      validateForm: function (isBubble) {
        var time1 = new Date().getTime();

        if (!this.validSignatureUSB()) {
          return false;
        }
        var _this = this;
        var validator = $(_this).data('wValidator');
        if (validator) {
          validator.invalid = {}; // 重置invalid，从表删除行时，无法清理invalid
        }

        var formUuid = this.getFormUuid();
        var dataUuid = this.getDataUuid(); // 先从缓存中获取
        var time3 = new Date().getTime();

        var ns = this.getNamespace() || this.getFormUuid();
        var validationInfo = _this.collectShouldValidatedInfo(formUuid)[ns];
        console.log(validationInfo);

        // var valid1 = this.validateMainform(formUuid);
        var valid1 = this.validateMainform(validationInfo.shouldValidatedFields);

        var time4 = new Date().getTime();
        console.log('主表验证所用时间:' + (time4 - time3) / 1000.0 + 's');
        // var valid2 = this.validateSubform(formUuid);
        var valid2 = this.validateSubform(validationInfo.shouldValidatedSubForms);

        var time5 = new Date().getTime();
        console.log('从表验证所用时间:' + (time5 - time4) / 1000.0 + 's');

        if (valid1 && valid2) {
          if (typeof bubble != 'undefined' && bubble != null) {
            bubble.close();
          }
        }
        // 气泡管理
        if (isBubble != false && !(valid1 && valid2)) {
          var validator = $(this).data('wValidator');
          var count = this.objectLength(validator.invalid);
          if (count > 0) {
            if (typeof bubble == 'undefined' || bubble == null) {
              bubble = new Bubble();
            }
            bubble.clear();
            var invalidSubformCtrl = {};
            for (var i in validator.invalid) {
              var ctl = this.getControlByControlId(i);

              // add by wujx 20160928 begin
              // 过滤从表重复列控件
              var subFormUuid = ctl.getFormDefinition().uuid;
              if (!invalidSubformCtrl[subFormUuid]) {
                invalidSubformCtrl[subFormUuid] = [];
              }
              var fieldName = ctl.getFieldName();
              if ($.inArray(fieldName, invalidSubformCtrl[subFormUuid]) == -1) {
                invalidSubformCtrl[subFormUuid].push(fieldName);
              } else {
                continue;
              }

              // add by wujx 20160928 end

              (function (ctl) {
                _this.showValidateMessage(ctl);
                var errorMap = validator.errorMap || {};
                var id = ctl.getCtlName();
                var fieldName = ctl.getFieldName();
                var displayName = ctl.getDisplayName();

                var invisible = ~$.inArray(fieldName, validationInfo.invisibleFields);

                bubble.addErrorItem(
                  {
                    id: id,
                    displayName: displayName,
                    title: errorMap[id] || displayName,
                    invisible: invisible
                  },
                  function (data) {
                    _this.showValidateMessage(ctl);
                  }
                );
              })(ctl);
            }

            bubble.show();
            bubble.$html.find('#bubble_tooltip_content').children('li').first().find('a').trigger('click');
          }
          // add by wujx 20160822 begin
          // 处理从表校验气泡
          var formDefinition = this.cache.get.call(this, cacheType.formDefinition, formUuid);
          var subforms = formDefinition.subforms;
          for (var i in subforms) {
            // 遍历各从表
            var subformUuid = subforms[i].formUuid;
            var subformcontrol = _this.getSubformControl(subforms[i].formUuid);
            if (!subformcontrol) {
              continue;
            }
            var valid = subformcontrol.validateBlankRow();
            if (!valid) {
              if (count <= 0) {
                if (typeof bubble == 'undefined' || bubble == null) {
                  bubble = new Bubble();
                }
                bubble.clear();
                bubble.show();
              }
              var subformConfig = subformcontrol.getSubformConfig();
              var invisible = ~$.inArray(subformUuid, validationInfo.invisibleSubforms);
              bubble.addErrorItem({
                id: subformConfig.formUuid + '-blankrow',
                title: '从表[' + subformConfig.displayName + ']有空行！',
                displayName: '从表[' + subformConfig.displayName + ']有空行！',
                invisible: invisible
              });
            }
          }
          // add by wujx 20160822 end
        }
        var time2 = new Date().getTime();
        console.log('表单验证所用时间:' + (time2 - time1) / 1000.0 + 's');
        return valid1 && valid2;
        // return false;
      },

      // 显示验证不通过的控件的提示信息
      showValidateMessage: function (control) {
        var editableElem = control.get$InputElem();
        // 确认输入框是否被隐藏,如果被隐藏,说明所在的布局处于非激活状态，那么就激活所在的布局
        if (editableElem != null && editableElem.size() > 0) {
          if ($(editableElem).is(':hidden')) {
            this.activeLayoutByControl(control);
          }
        }
        if (control.getPos() == dyControlPos.subForm) {
          // 在从表中
          control.setEditable();
        }
        var editableElem = control.get$InputElem();
        if (editableElem != null && editableElem.size() > 0) {
          var editableElem2 = editableElem.closest('div.control-container').find('.editableClass:visible');
          if (editableElem2 && editableElem2.length > 0) {
            editableElem = editableElem2;
          }

          var $wrapElement;
          switch (control.getInputMode()) {
            case dyFormInputMode.number:
              $wrapElement = editableElem.parent();
              break;
            case dyFormInputMode.treeSelect:
              $wrapElement = control.$editableElem.next();
              break;
            case dyFormInputMode.ckedit:
              $wrapElement = control.$editableElem.next();
              break;
            case dyFormInputMode.radio:
            case dyFormInputMode.checkbox:
              $wrapElement = editableElem.parents('.editableClass');
              break;
            case dyFormInputMode.select:
            case dyFormInputMode.comboSelect:
              $wrapElement = editableElem.prev();
              break;
            case dyFormInputMode.orgSelect:
            case dyFormInputMode.orgSelectStaff:
            case dyFormInputMode.orgSelectDepartment:
            case dyFormInputMode.orgSelectStaDep:
            case dyFormInputMode.orgSelectAddress:
            case dyFormInputMode.orgSelect2:
              $wrapElement = control.$editableElem.next();
              break;
            default:
              $wrapElement = editableElem;
              break;
          }

          this._scrollIntoView($wrapElement);
          // this._focusElement($wrapElement);
        }
        if (control.validateElem) {
          control.validateElem(); // 验证输入框
        }
      },

      _scrollIntoView: function ($element) {
        if ($element && $element.get(0)) $element.get(0).scrollIntoView();
        window.scrollBy(0, -100);
      },

      // 激活控件所在的布局
      activeLayoutByControl: function (control) {
        var $layouts = control.$placeHolder.parents('.tab-design,div.container');
        if ($layouts.size() == 0) {
          // 没有放在布局里面
          return;
        }

        for (var i = $layouts.size(); i >= 0; i--) {
          var $layoutElem = $($layouts[i]);
          if ($layoutElem.is('.tab')) {
            // 页签布局里面
            // $layoutElem.find(".subtab");
            var layoutName = $layoutElem.attr('name');
            var layout = $.ContainerManager.getContainer(layoutName);
            if(layout !=null && layout.getType().indexOf('tab')>-1){
              if (layout) layout.activeSubTab(control); // 激活控件所在的子页签
            }
          }
        }
      },

      objectLength: function (obj) {
        return $.dyform.objectLength(obj);
      },
      /**
       * 校验签名key
       */
      validSignatureUSB: function () {
        if (this.isEnableSignature()) {
          if (Browser.isIE()) {
          } else {
            appModal.alert('当前浏览器无法对表单数据进行签名，请使用IE浏览器编辑表单!');
          }
          return checkCAKey();
        }
        return true;
      },

      /**
       * 填充数据,数据key为formUuid
       *
       * @param formDatas
       * @param callback
       */
      fillFormData: function (formDatas, callback) {
        if (formDatas == null || formDatas == undefined) {
          return;
        }
        var formUuid = this.getFormUuid(); // 获取当前表单的定义uuid
        var time1 = new Date().getTime();

        var subformFormUuid = [];
        for (var i in formDatas) {
          var formData = formDatas[i];
          if (formUuid == i) {
            // 填充主表
            this.fillFormDataOfMainform(formData[0]);
          } else {
            subformFormUuid.push(i);
          }
        }

        var time2 = new Date().getTime();

        console.log('填充主表数据所用时间:' + (time2 - time1) / 1000.0 + 's');
        if (subformFormUuid.length > 0) {
          // var time3 =( new Date()).getTime();
          // 表单从表排序，按dom结构中的先后顺序解析
          var formDefinitionOfMainform = this.getFormDefinition(formUuid);
          if (formDefinitionOfMainform && $.isArray(formDefinitionOfMainform.formTree)) {
            var subformOrders = {};
            $.each(formDefinitionOfMainform.formTree, function (i, node) {
              if (node.nodeType == 'subform') {
                subformOrders[node.subformUuid] = i;
              }
            });
            subformFormUuid.sort(function (subformUuid1, subformUuid2) {
              return (subformOrders[subformUuid1] || 0) - (subformOrders[subformUuid2] || 0);
            });
          }
          this.fillSubformData(formDatas, subformFormUuid, 0, callback);
          // var time4 =( new Date()).getTime();
          // console.log("填充从表数据所用时间:" + (time4 - time3)/1000.0 +
          // "s");
        } else {
          if (!callback) {
            return;
          }
          if (typeof callback == 'function') {
            callback.apply(this);
          } else {
            callback.callback.apply(this);
          }
        }
      },

      /**
       * 填充数据,数据key为formUuid
       *
       * @param newData
       * @param callback
       */
      reFillFormData: function (newData, callback) {
        var self = this;
        self.collectFormData(
          function (formData) {
            var formDatas = formData.formDatas;
            var mainFormUuid = self.getFormUuid(); // 获取当前表单的定义uuid
            for (var formUuid in formDatas) {
              var formData = formDatas[formUuid] || [];
              for (var i = 0; i < formData.length; i++) {
                formData[i].uuid = newData == true ? self.createUuid() : formData[i].uuid;
              }
            }
            self.fillFormData(formDatas, callback);
            self.clearDeleteRows();
          },
          function (error) {
            console.log(error);
          }
        );
      },

      /**
       * 填充数据,数据key为formUuid(NO COVER)
       *
       * @param formDatas
       * @param callback
       */
      fillFormDataNC: function (formDatas, callback) {
        var _this = this;
        var ns = _this.getOption().namespace;
        var formUuid = this.getFormUuid(); // 获取当前表单的定义uuid
        var time1 = new Date().getTime();

        for (var i in formDatas) {
          var formData = formDatas[i];
          if (formUuid == i) {
            // 填充主表
            this.fillFormDataOfMainformNC(formData[0]);
          }
        }

        var time2 = new Date().getTime();
        console.log('填充主表数据(fillFormDataNC)所用时间:' + (time2 - time1) / 1000.0 + 's');
        var time3 = new Date().getTime();
        for (var i in formDatas) {
          var formData = formDatas[i];
          if (formUuid == i) {
          } else {
            // 填充从表
            var subformctl = _this.getSubformControl(i);
            if (!subformctl) {
              continue;
            }

            subformctl.setMainformDataUuid(this.getDataUuid());
            subformctl.fillFormDataNC(formData);
          }
        }

        var time4 = new Date().getTime();
        console.log('填充从表(fillFormDataNC)数据所用时间:' + (time4 - time3) / 1000.0 + 's');
        if (callback) {
          callback.apply(this);
        }
      },

      /**
       * 填充数据,数据key为formUuid,值为显示值
       *
       * @param formDatas
       * @param formUuid
       *            数据的定义uuid,没有的话就主表id(应为excel表空间不能有formId)
       * @param callback
       */
      fillFormDisplayData: function (formDatas, formUuid, callback) {
        var _this = this;
        var ns = _this.getOption().namespace;
        // var formUuid = mainFormUuid;
        if (typeof formUuid == 'undefined' || formUuid == null) {
          formUuid = this.getFormUuid(); // 获取当前表单的定义uuid
        }
        if (this.invoke('beforeImportData', formDatas, formUuid) === false) {
          return false;
        }
        var mainDataUuid = this.getDataUuid();
        formDatas[formUuid] = formDatas[formUuid] || [];
        formDatas[formUuid][0] = $.extend(formDatas[formUuid][0] || {}, {
          uuid: mainDataUuid
        });
        var time1 = new Date().getTime();
        for (var fi in formDatas) {
          if (formUuid != fi) {
            continue;
          }
          var cacheVK = this.getMainformValueKeySetMap();
          var cacheVF = this.getMainformValueFormatSetMap();
          // 主表值键转换
          var formData = formDatas[fi];
          var mainFormData = formData[0];
          for (var key in mainFormData) {
            if (typeof cacheVF[key] != 'undefined' && cacheVF[key] != '') {
              // 日期格式处理
              var disValue = mainFormData[key];
              var fmt = cacheVF[key];
              try {
                var dateVar = new Date(Date.parse(disValue.replace(/-/g, '/')));
                if (!isNaN(dateVar.getTime())) {
                  // 有效日期
                  mainFormData[key] = dateVar.format(fmt);
                }
              } catch (e) {
                console.log(e);
              }
              continue; // 日期控件
            }
            var optionSet = cacheVK[key];
            if (typeof optionSet == 'undefined' || optionSet == null) {
              // 不再需要isValueMap判断了
              continue;
            }
            var disValue = mainFormData[key];
            var value = optionSet[disValue];
            if (typeof value == 'undefined' || value == null) {
              continue;
            }
            mainFormData[key] = value;
          }
          cacheVK = null;
        }

        var time2 = new Date().getTime();
        console.log('主表(值->键)解析所用时间:' + (time2 - time1) / 1000.0 + 's');
        try {
          // 从表导入
          var time3 = new Date().getTime();
          for (var di in formDatas) {
            if (formUuid == di) {
            } else {
              // 填充从表
              var formData = formDatas[di]; // 从表数据
              if (formData.length <= 0) {
                continue;
              }
              var subformctl = _this.getSubformControl(di);
              if (typeof subformctl == 'undefined' || subformctl == null) {
                continue;
              }
              var cacheVK = undefined; // 从表级缓存
              var cacheVF = undefined; // 从表级缓存

              if (typeof cacheVK == 'undefined' || cacheVK == null) {
                // 缓存从表VK定义
                cacheVK = subformctl.getSubformValueKeySetMap();
              }

              if (typeof cacheVF == 'undefined' || cacheVF == null) {
                // 缓存从表VF定义
                cacheVF = subformctl.getSubformValueFormatSetMap();
              }

              for (var fi = 0; fi < formData.length; fi++) {
                var subFormData = formData[fi];

                for (var key in subFormData) {
                  if (typeof cacheVF[key] != 'undefined' && cacheVF[key] != '') {
                    // 日期格式处理
                    var disValue = subFormData[key];
                    var fmt = cacheVF[key];
                    try {
                      var dateVar = new Date(Date.parse(disValue.replace(/-/g, '/')));
                      if (!isNaN(dateVar.getTime())) {
                        subFormData[key] = dateVar.format(fmt);
                      }
                    } catch (e) {
                      console.log(e);
                    }
                    continue; // 日期控件
                  }
                  var optionSet = cacheVK[key];
                  if (typeof optionSet == 'undefined' || optionSet == null) {
                    // 没有控件缓存则isValueMap为false
                    continue;
                  } else {
                    var disValue = subFormData[key];
                    var disValues = disValue.split(';');
                    var valuesObj = {};
                    for (var i = 0; i < disValues.length; i++) {
                      var value = optionSet[disValues[i]];
                      valuesObj[value] = disValues[i];
                    }

                    if (this.objectLength(valuesObj) == 0) {
                      continue;
                    }
                    subFormData[key] = JSON.stringify(valuesObj);
                  }
                }
              }
              cacheVK = null;
              cacheVF = null;
            }
          }
        } catch (e) {
          console.error(e);
        }

        var time4 = new Date().getTime();
        console.log('从表主表(值->键)解析所用时间:' + (time4 - time3) / 1000.0 + 's');
        var _this = this,
          dlg_selector = 'excel_dlg_confirm';
        var fillCallback = function (cover) {
          if (_this.invoke('beforeFillImportData', formDatas, formUuid) === false) {
            return false;
          }
          cover ? _this.fillFormData(formDatas, callback) : _this.fillFormDataNC(formDatas, callback);
        };
        var json = {
          title: '追加还是覆盖?',
          resizable: false,
          width: 400,
          height: 180,
          content:
            '<div id="popup_message_div" style="height: 70px;"><div id="popup_message_icon"></div><div id="popup_message" style="width: 310px; height: 45px;overflow: auto;">追加&nbsp;:&nbsp;导入时,保留之前的单据。<br>覆盖&nbsp;:&nbsp;清空之前的单据,然后导入。</div></div>',
          dialogId: dlg_selector,
          buttons: {
            追加: function () {
              fillCallback(false);
              closeDialog(dlg_selector);
            },
            覆盖: function () {
              fillCallback(true);
              closeDialog(dlg_selector);
            },
            取消: function () {
              closeDialog(dlg_selector);
            }
          },
          defaultBtnName: '取消'
        };
        $('#' + dlg_selector).remove();
        showDialog(json);
        // var cover = confirm("是否覆盖表单数据?");
        // cover ? this.fillFormData(formDatas,callback) :
        // this.fillFormDataNC(formDatas,callback);
      },
      /**
       * 填充数据,数据key为formID
       *
       * @param formDatas
       * @param callback
       */
      fillFormDatas: function (formDatas, callback) {
        var formDatasKeyUuid = {};
        for (var i in formDatas) {
          var formData = formDatas[i];
          var uuid = this.getFormUuid(i);
          formDatasKeyUuid[uuid] = formData;
        }
        this.fillFormData(formDatasKeyUuid, callback);
      },
      /**
       * 填充数据,数据key为formID,value为显示值
       *
       * @param formDatas
       * @param mainFormId为主表数据ID
       * @param callback
       */
      fillFormDisplayDatas: function (formDatas, mainFormId, callback) {
        if (typeof mainFormId == 'undefined' || mainFormId == null) {
          mainFormId = this.getFormId(); // 获取主表ID,不是UUID
        }
        var formDatasKeyUuid = {};
        for (var i in formDatas) {
          if (typeof i == 'undefined' || i == null) {
            continue;
          }
          var formData = formDatas[i];
          var uuid = '';
          if (mainFormId == i) {
            uuid = this.getFormUuid(); // 主表formUuid为当前表单Uuid
          } else {
            uuid = this.getFormUuid(i);
          }
          formDatasKeyUuid[uuid] = formData;
        }
        this.fillFormDisplayData(formDatasKeyUuid, callback);
      },
      /**
       * 为主表填充数据
       *
       * @param formData
       */
      fillFormDataOfMainform: function (formData) {
        if (!formData) {
          return;
        }
        this.setDataUuid(formData.uuid); // 将dataUuid缓存
        var _this = this;
        var ns = _this.getOption().namespace;
        _this.$($.dyform.nss(ns, '.value[fieldName]')).each(function () {
          var fieldName = $(this).attr('fieldName');
          var value = formData[fieldName] == undefined ? formData[fieldName.toLowerCase()] : formData[fieldName];
          if (typeof value == 'undefined') {
            return true;
          }
          _this.setFieldValueByFieldName(fieldName, value, null, formData);
        });
      },

      /**
       * 为主表填充数据(不覆盖)NO COVER
       *
       * @param formData
       */
      fillFormDataOfMainformNC: function (formData) {
        this.setDataUuid(formData.uuid); // 将dataUuid缓存
        var _this = this;
        var ns = _this.getOption().namespace;
        _this.$($.dyform.nss(ns, '.value[fieldName]')).each(function () {
          var fieldName = $(this).attr('fieldName');
          var value = formData[fieldName.toLowerCase()];
          if (typeof value == 'undefined') {
            return true;
          }
          var oldValue = _this.getFieldValueByFieldName(fieldName);
          if (typeof oldValue == 'undefined' || oldValue == null || oldValue == '') {
            _this.setFieldValueByFieldName(fieldName, value);
          }
        });
      },

      createUuid: function () {
        return new UUID().id.toLowerCase();
      },
      /**
       * 整个表单设置为只读
       */
      setReadOnly: function () {
        var _this = this;
        var ns = _this.getOption().namespace;
        _this.$($.dyform.nss(ns, '.value[fieldName]')).each(function () {
          var fieldName = $(this).attr('fieldName');
          var control = _this.getControl(fieldName);
          if (typeof control == 'undefined' || control == null) {
            return true; // continue;
          }
          control.setReadOnly(true);
        });
        _this.$($.dyform.nss(ns, 'table[formUuid]')).each(function () {
          var subformctl = _this.getSubformControl($(this).attr('formUuid'));
          subformctl.setReadOnly();
        });
        _this.invoke('afterSetReadOnly');
      },
      /**
       * 设置上传文本文件时同时生成swf副本
       */
      setTextFile2SWF: function (enable) {
        var _this = this;
        var ns = _this.getOption().namespace;
        var formUuidOfMainform = this.getFormUuid();
        var formDefinitionOfMainform = this.getFormDefinition(formUuidOfMainform);
        this.$($.dyform.nss(ns, '.value[fieldName]')).each(function () {
          var fieldName = $(this).attr('fieldName');
          var control = _this.getControl(fieldName);
          if (typeof control == 'undefined' || control == null) {
            return true; // continue;
          }

          if (formDefinitionOfMainform.isInputModeAsAttach(fieldName)) {
            control.setTextFile2SWF(enable);
          }
        });

        this.$($.dyform.nss(ns, 'table[formUuid]')).each(function () {
          var subformctl = _this.getSubformControl($(this).attr('formUuid'));
          subformctl.setTextFile2SWF(enable);
        });
      },
      setEditable: function () {
        var _this = this;
        var ns = _this.getOption().namespace;
        this.$($.dyform.nss(ns, '.value[fieldName]')).each(function () {
          var fieldName = $(this).attr('fieldName');
          var control = _this.getControl(fieldName);
          if (typeof control == 'undefined' || control == null || control.getPos() == dyControlPos.subForm) {
            return true; // continue;
          }
          control.setEditable();
        });

        this.$($.dyform.nss(ns, 'table[formUuid]')).each(function () {
          var subformctl = _this.getSubformControl($(this).attr('formUuid'));
          subformctl.setEditable();
        });
        this.invoke('afterSetEditable');
      },

      setAllowDownload: function () {
        var _this = this;
        var ns = _this.getOption().namespace;
        this.$($.dyform.nss(ns, '.value[fieldName]')).each(function () {
          var fieldName = $(this).attr('fieldName');
          var control = _this.getControl(fieldName);
          if (typeof control == 'undefined' || control == null || control.getPos() == dyControlPos.subForm) {
            return true; // continue;
          }
          if (control.setAllowDownload) {
            control.setAllowDownload(true);
          }
        });
      },
      setNoAllowDownload: function () {
        var _this = this;
        var ns = _this.getOption().namespace;
        this.$($.dyform.nss(ns, '.value[fieldName]')).each(function () {
          var fieldName = $(this).attr('fieldName');
          var control = _this.getControl(fieldName);
          if (typeof control == 'undefined' || control == null || control.getPos() == dyControlPos.subForm) {
            return true; // continue;
          }
          if (control.setAllowDownload) {
            control.setAllowDownload(false);
          }
        });
      },

      showAsLabel: function () {
        var _this = this;
        var ns = _this.getOption().namespace;
        this.$($.dyform.nss(ns, '.value[fieldName]')).each(function () {
          var fieldName = $(this).attr('fieldName');
          var control = _this.getControl(fieldName);
          if (typeof control == 'undefined' || control == null) {
            return true; // continue;
          }
          control.setDisplayAsLabel();
        });
        this.$($.dyform.nss(ns, 'table[formUuid]')).each(function () {
          var subformctl = _this.getSubformControl($(this).attr('formUuid'));
          subformctl.setDisplayAsLabel();
        });

        this.invoke('afterShowAsLabel');
      },
      handleException: function (exceptionData, options) {
        var subcode = exceptionData.subcode;
        if (subcode === 'SerialNumberOccupy') {
          var dyformDataOptions = options.dyformDataOptions();
          appModal.confirm(exceptionData.title, function (result) {
            if (result) {
              dyformDataOptions.putOptions('serialNumberConfirmed-' + exceptionData.fieldName, true);
              $.isFunction(options.callback) && options.callback.call();
            }
          });
        } else {
          console.error('表单数据保存失败: ', exceptionData);
          try {
            if (exceptionData.msg) {
              appModal.error(exceptionData.msg);
            }
          } catch (error) {
            appModal.error('表单数据保存失败！');
          }
        }
      },
      /**
       * 处理异常
       */
      handleError: function (exceptionData, options) {
        var self = this;

        function errorFun(message) {
          appModal.error(message || '表单数据保存失败！');
        }

        try {
          var msg = JSON.parse(exceptionData);
          if (msg.errorCode == 'SQLGRAM') {
            appModal.error('保存时,后台语法错误!!!有可能是人为去修改了表单后台数据库表字段,更详细的信息如下:\n' + msg.msg);
          } else if (msg.errorCode == 'DATA_OUT_OF_DATE') {
            appModal.error('请重新加载并修改数据:\n' + msg.msg);
          } else if (msg.errorCode == 'SaveData') {
            self.handleException(msg.data, options);
          } else {
            if (msg.msg) {
              errorFun(msg.msg);
            } else {
              errorFun();
            }
          }
        } catch (e) {
          errorFun();
        }
      },
      enableSignature: function (enable) {
        var _this = this;
        var ns = _this.getOption().namespace;
        var formUuidOfMainform = this.getFormUuid();
        var formDefinitionOfMainform = this.getFormDefinition(formUuidOfMainform);

        if (enable) {
          formDefinitionOfMainform.enableSignature = signature.enable;
        } else {
          formDefinitionOfMainform.enableSignature = signature.disable;
        }

        this.$($.dyform.nss(ns, '.value[fieldName]')).each(function () {
          var fieldName = $(this).attr('fieldName');
          var control = _this.getControl(fieldName);
          if (typeof control == 'undefined' || control == null) {
            return true; // continue;
          }

          if (formDefinitionOfMainform.isInputModeAsAttach(fieldName)) {
            control.enableSignature(enable);
          }
        });

        this.$($.dyform.nss(ns, 'table[formUuid]')).each(function () {
          var subformctl = _this.getSubformControl($(this).attr('formUuid'));
          subformctl.enableSignature(enable);
        });
      },

      fixedSubformsHeader: function (scrollContainerSelector, topContainerSelector, subforms) {
        var self = this;
        var ns = self.getOption().namespace;
        self.$($.dyform.nss(ns, 'table[formUuid]')).each(function () {
          var formUuid = $(this).attr('formUuid');
          if (subforms && subforms.indexOf && subforms.indexOf(formUuid) < 0) {
            // continue;
            return;
          }
          var subformctl = self.getSubformControl(formUuid);
          if (subformctl && subformctl.fixedHeader) {
            subformctl.fixedHeader(scrollContainerSelector, topContainerSelector);
          }
        });
      },
      dyformDataOptions: function () {
        var self = this;
        var option = self.getOption();
        option.dyformDataOptions = option.dyformDataOptions || {};
        return {
          getOptions: function (key) {
            return option.dyformDataOptions[key];
          },
          putOptions: function (key, value) {
            if (null == value) {
              delete option.dyformDataOptions[key];
            } else {
              option.dyformDataOptions[key] = value;
            }
          }
        };
      },
      setBtnEventToFileCtl: function () {
        var _this = this;
        var ns = _this.getOption().namespace;
        var btnEventOptions = _this.getOption().btnEventOptions;
        this.$($.dyform.nss(ns, '.value[fieldName]')).each(function () {
          var fieldName = $(this).attr('fieldName');
          var control = _this.getControl(fieldName);
          if (typeof control == 'undefined' || control == null) {
            return true; // continue;
          }

          var ctlInputMode = control.options.commonProperty.inputMode;
          if (btnEventOptions && (ctlInputMode == '4' || ctlInputMode == '6')) {
            var ctlIndex = _.findIndex(btnEventOptions, function (o) {
              return o.inputMode == ctlInputMode;
            });

            if (ctlIndex > -1) {
              control.clickFileNameEvent(btnEventOptions[ctlIndex].haveEditAuth, btnEventOptions[ctlIndex].notHaveEditAuth);
            }
          }
        });
      },
      setFileCtlBtnEventOptions: function (options) {
        if ($.isArray(options)) {
          var formOptions = this.getOption();
          formOptions.btnEventOptions = options;
        }
      }
    }
  );

  /**
   * 设置字段属性值接口
   */
  $.dyform.extend({
    /** 记录映射字段到 `formDefinition` */
    setMappingFields: (function () {
      var called = {};

      return function (formUuid) {
        if (called[formUuid]) {
          return;
        }

        called[formUuid] = true;

        var formDefinition = this.getFormDefinition(formUuid);
        if (!formDefinition.mappingFields) {
          formDefinition.mappingFields = [];
        }

        var fields = formDefinition.fields;
        for (var fieldName in fields) {
          var field = fields[fieldName];
          if (field.applyTo && field.applyTo.length) {
            formDefinition.mappingFields.push(fieldName);
          }
        }
      };
    })(),

    getFieldNameByApplyTo: function (applyTo, formUuid) {
      if (typeof applyTo == 'object' && typeof applyTo['fieldMappingName'] != 'undefined') {
        applyTo = applyTo['fieldMappingName']; // 这里为了兼容旧系统
      }

      if (typeof formUuid == 'undefined' || formUuid == null) {
        formUuid == this.getFormUuid();
      }

      var formDefinition = this.getFormDefinition(formUuid);

      var applyTo2FieldName = formDefinition['applyTo2FieldName'];
      if (typeof applyTo2FieldName == 'undefined') {
        applyTo2FieldName = {};
        var fields = formDefinition['fields'];

        for (var fieldName in fields) {
          var field = fields[fieldName];
          var applyTotmp = field.applyTo;

          applyTo2FieldName[fieldName] = fieldName;
          if (typeof applyTotmp != 'undefined' && applyTotmp.length > 0) {
            var tos = applyTotmp.split(';');
            for (var i = 0; i < tos.length; i++) {
              var to = tos[i];
              applyTo2FieldName[to] = fieldName;
            }
          }
        }
        formDefinition['applyTo2FieldName'] = applyTo2FieldName;
      }
      var fieldName = applyTo2FieldName[applyTo];
      if (typeof fieldName == 'undefined') {
        console.log("cann't get the fieldName for mappingName[" + JSON.cStringify(applyTo) + '][formID:' + formDefinition.id + ']');
        // throw new Error("cann't get the fieldName for
        // mappingName[" + applyTo + "][formID:" +
        // formDefinition.id + "]");
      }
      return fieldName;
    },

    setFieldValue: function (mappingName, data, dataUuid) {
      var formUuid = this.getFormUuidByRowId(dataUuid);

      var fieldName = this.getFieldNameByApplyTo(mappingName, formUuid);
      if (!fieldName) {
        console.log("cann't find field for mappingName[" + mappingName + ']');
        return;
      }
      if (data == null || !data.value) {
        this.setFieldValueByFieldName(fieldName, data, dataUuid);
      } else {
        this.setFieldValueByFieldName(fieldName, data.value, dataUuid);
      }
    },

    /**
     * 设置主表的某字段的值
     *
     * @param fieldName
     *            字段名
     * @param data
     *            各字段是的值 格式根据不同的inputMode也会不一样，与getFieldValue一致
     */

    setFieldValueByFieldName: function (fieldName, data, dataUuid, formData) {
      // var time1 = (new Date()).getTime();

      var control = this.getControl(fieldName, dataUuid);
      if (!control) {
        console.log("cann't find field[" + fieldName + ']');
        return;
      }

      var placeholderCtr = this.data().options.formData.formDefinition.placeholderCtr;
      if (placeholderCtr && placeholderCtr[fieldName]) {
        return;
      }

      $.dyform.setValue(control, data, this.getDataUuid(), formData);
      // var time2 = (new Date()).getTime();
      // console.log("set value for control [" + fieldName + "]"+
      // (time2 - time1 )/1000.0 + "s" );
    },

    /**
     * 获取主表某字段的值
     *
     * @param fieldName
     */
    getFieldValueByFieldName: function (fieldName, dataUuid) {
      var control = this.getControl(fieldName, dataUuid);
      if (control == null) {
        console.log("control cann't find ,fieldName=" + fieldName + ' dataUuid=' + dataUuid);
        return;
      }
      return $.dyform.getValue(control);
    },

    /**
     * 获取主表某字段的值
     *
     * @param fieldName
     */
    getFieldValue: function (mappingName, dataUuid) {
      var formUuid = this.getFormUuidByRowId(dataUuid);
      var fieldName = this.getFieldNameByApplyTo(mappingName, formUuid);
      if (typeof fieldName == 'undefined') {
        return null;
      }
      return this.getFieldValueByFieldName(fieldName, dataUuid);
    },

    setFieldReadOnlyByFieldName: function (fieldName, dataUuid) {
      var control = this.getControl(fieldName, dataUuid);
      control.setReadOnly(true);
    },

    setFieldReadOnly: function (mappingName, dataUuid) {
      var formUuid = this.getFormUuidByRowId(dataUuid);
      var fieldName = this.getFieldNameByApplyTo(mappingName, formUuid);
      this.setFieldReadOnlyByFieldName(fieldName, dataUuid);
    },

    setFieldEditableByFieldName: function (fieldName, dataUuid) {
      var control = this.getControl(fieldName, dataUuid);
      control.setEditable(true);
    },

    setFieldEditable: function (mappingName, dataUuid) {
      var formUuid = this.getFormUuidByRowId(dataUuid);
      var fieldName = this.getFieldNameByApplyTo(mappingName, formUuid);
      this.setFieldEditableByFieldName(fieldName, dataUuid);
    },

    setFieldAsLabelByFieldName: function (fieldName, dataUuid) {
      var control = this.getControl(fieldName, dataUuid);
      control.setDisplayAsLabel(true);
    },

    setFieldAsLabel: function (mappingName, dataUuid) {
      var formUuid = this.getFormUuidByRowId(dataUuid);
      var fieldName = this.getFieldNameByApplyTo(mappingName, formUuid);
      this.setFieldAsLabelByFieldName(fieldName, dataUuid);
    },

    setFieldAsHiddenByFieldName: function (fieldName, dataUuid) {
      var control = this.getControl(fieldName, dataUuid);
      control.setVisible(false);
    },
    /**
     * 隐藏行
     * @param fieldName
     */
    setControlRowHide: function (fieldName) {
      var control = this.getControl(fieldName);
      control.setRowVisible(false);
    },
    /**
     * 显示行
     * @param fieldName
     */
    setControlRowShow: function (fieldName) {
      var control = this.getControl(fieldName);
      control.setRowVisible(true);
    },
    /**
     * 隐藏指定控件的列
     *
     * @param {String}
     *            fileName
     */

    setControlCellHide: function (fileName) {
      var control = this.getControl(fileName);
      control.setCellVisible(false);
    },
    /**
     * 显示指定控件的列
     *
     * @param {String}
     *            fileName
     */

    setControlCellShow: function (fileName) {
      var control = this.getControl(fileName);
      control.setCellVisible(true);
    },
    // modified by huangwy 2015-03-12 设置表单字段必填(分主表和从表)
    setFieldRequiredByFormId: function (fieldName, isRequired, formId) {
      // 如果formId没有填写，这是主表
      if (!formId || formId == this.getFormId()) {
        var control = this.getControl(fieldName);
        control.setRequired(isRequired);
        // 否则是从表
      } else {
        var forminfo = {};
        forminfo.id = formId;
        // 搜集从表的数据
        var allRowData = this.getAllRowData(forminfo); // 获取所有的数据
        if (allRowData) {
          for (var i = 0; i < allRowData.length; i++) {
            this.getControl(fieldName, allRowData[i].id).setRequired(isRequired);
          }
        }
      }
    },
    // modified by huangwy 2015-03-12 设置表单字段必填(分主表和从表)
    setFieldRequiredByFormUuid: function (fieldName, isRequired, formUuid) {
      var formId = this.getFormId(formUuid);
      this.setFieldRequiredByFormId(fieldName, isRequired, formId);
    },

    setFieldAsHide: function (mappingName, dataUuid) {
      var formUuid = this.getFormUuidByRowId(dataUuid);
      var fieldName = this.getFieldNameByApplyTo(mappingName, formUuid);
      this.setFieldAsHiddenByFieldName(fieldName, dataUuid);
    },

    setFieldAsShowByFieldName: function (fieldName, dataUuid) {
      var control = this.getControl(fieldName, dataUuid);
      control.setVisible(true);
    },

    setFieldAsShow: function (mappingName, dataUuid) {
      var formUuid = this.getFormUuidByRowId(dataUuid);
      var fieldName = this.getFieldNameByApplyTo(mappingName, formUuid);
      this.setFieldAsShowByFieldName(fieldName, dataUuid);
    },
    getControl: function (fieldName, dataUuidOfSubform) {
      var self = this;
      var controlId = fieldName,
        options;
      var ns = dataUuidOfSubform || self.attr('data-ns');
      if ($.trim(ns).length > 0) {
        controlId = $.dyform.getCellId(ns, fieldName);
      }
      var ctl = self.getControlByControlId(controlId);
      if (ctl == null && (options = self.getOption())) {
        var placeholderCtr = options.formData.formDefinition.placeholderCtr;
        if (placeholderCtr && placeholderCtr[fieldName]) {
          return;
        }
        console.error('字段:【' + fieldName + '】不存在, 查看是不是在新版本中。是否是流程中表单没有升级到最新版本.');
      }
      return ctl; //$.ControlManager.getCtl(controlId);
    },
    getControlByControlId: function (controlId) {
      var self = this;
      var ctl =
        self.cache.get.call(self, cacheType.control, controlId) || ($.ControlManager[controlId] && $.ControlManager[controlId].control);
      return ctl;
    },
    getControlByPlaceHolder: function (placeHolderName) {
      var self = this;
      return self.getControl(placeHolderName);
    },
    getRealValue: function (jsonValue) {
      if (jsonValue == null || jsonValue.trim().length == 0) {
        return '';
      }
      var json = {};
      try {
        json = jQuery.parseJSON(value);
      } catch (ex) {
        json = {};
      }
    },
    // 控件不存在 返回false,否则 true
    isControl: function (fieldName, dataUuidOfSubform) {
      return this.getControl(fieldName, dataUuidOfSubform) == null ? false : true;
    },
    autoWidth: function () {
      // $(window).trigger("resize")
      var _this = this;

      //触发表单中的控件的resize方法
      _this.resizeTid && clearTimeout(_this.resizeTid);
      _this.resizeTid = setTimeout(function () {
        var ns = _this.getOption().namespace;
        _this.$($.dyform.nss(ns, ".value[fieldName][pos!='" + dyControlPos.subForm + "']")).each(function () {
          // 遍历主表的占位符
          var fieldName = $(this).attr('fieldName');
          var control = _this.getControl(fieldName);
          if (typeof control == 'undefined' || control == null) {
            return true;
          }
          if (control.resize) {
            control.resize();
          }
        });
        _this.resetSubformWidth();
      }, 1000 / 24);
    },

    /**
     * 绑定事件至控件中
     *
     * @param option
     */
    bind2Control: function (option) {
      var defaults = {
        type: 'click',
        mappingName: null,
        dataUuid: this.getDataUuid(),
        callback: function () {}
      };

      $.extend(defaults, option);

      var formUuid = this.getFormUuidByRowId(defaults.dataUuid);

      var fieldName = this.getFieldNameByApplyTo(defaults.mappingName, formUuid);

      var control = this.getControl(fieldName, defaults.dataUuid);
      if (!control) {
        console.log("bind2Control cann't find control :" + JSON.cStringify(option));
        return;
      }
      control.bind(defaults.type, defaults.callback);
    },
    // bind函数，桥接
    bind2Dyform: function (eventname, event) {
      var options = this.cache.get.call(this, cacheType.options);
      var events = options.events;
      if (typeof events == 'undefined') {
        options.events = {};
        events = options.events;
      }
      events[eventname] = event;
    },

    bind2Dyform2: function (eventname, event) {
      var self = this;
      var options = this.cache.get.call(this, cacheType.options);
      var fn = options.events[eventname];
      if (fn) {
        options.events[eventname] = function () {
          fn.apply(this, arguments);
          event && event.apply(this, arguments);
        };
        return self;
      }
      return self.bind2Dyform.apply(self, arguments);
    },

    invoke: function (method) {
      var options = this.cache.get.call(this, cacheType.options);
      var events = options.events;
      if (events && events[method]) {
        try {
          return events[method].apply(this, $.makeArray(arguments).slice(1));
        } catch (e) {
          console.error(e);
        }
      } else {
        console.log("cann't invoke method[" + method + '],method not find.');
      }
    },
    isEventExist: function (method) {
      var options = this.cache.get.call(this, cacheType.options);
      var events = options.events;
      if (events && events[method]) {
        return true;
      } else {
        return false;
      }
    },
    // 取得布局
    getLayout: function (name) {
      return $.ContainerManager.getContainer(name) || $.ContainerManager.getLayout(name);
    },
    // 移除弹出的窗口
    removePopup: function () {
      $('.layui-laydate').remove(); //时间控件弹出窗

      // 页面滚动时,将input等历史记录下拉框去掉
      var activeElement = document.activeElement;
      if ($(activeElement)[0] && ['input', 'textarea', 'select'].indexOf($(activeElement)[0].localName) > -1) {
        $(activeElement).blur();
      }
    }
  });
  /**
   * 从表对外接口
   */
  $.dyform.extend({
    getSubformControl: function (formUuidOfSubform) {
      var _this = this;
      var ns = _this.getOption().namespace;
      var cellId = _this.getCellId(formUuidOfSubform);
      var control = $.ControlManager.getSubFormControl(cellId, ns);
      return control;
    },
    /**
     * 隐藏从表
     *
     * @param formUuid
     */
    hideSubFormByFormUuid: function (formUuid) {
      var subformctl = this.getSubformControl(formUuid);
      if (subformctl) {
        subformctl.hide();
      } else {
        console.log("cann't find subformctl for formUuid[" + formUuid + ']');
      }
    },

    /**
     * 隐藏从表
     *
     * @param formId
     */
    hideSubForm: function (formId) {
      var formUuid = this.getFormUuid(formId);
      if (formUuid) {
        this.hideSubFormByFormUuid(formUuid);
      } else {
        console.log("cann't find formUuid for formId[" + formId + ']');
      }
    },
    /**
     * 显示从表
     *
     * @param formUuid
     */
    showSubFormByFormUuid: function (formUuid) {
      var subformctl = this.getSubformControl(formUuid);
      if (subformctl) {
        subformctl.show();
      } else {
        console.log("cann't find subformctl for formUuid[" + formUuid + ']');
      }
    },

    /**
     * 显示从表
     *
     * @param formId
     */
    showSubForm: function (formId) {
      var formUuid = this.getFormUuid(formId);
      if (formUuid) {
        this.showSubFormByFormUuid(formUuid);
      } else {
        console.log("cann't find formUuid for formId[" + formId + ']');
      }
    },
    addRowDataByFormUuid: function (formUuid, data) {
      var subformCtl = this.getSubformControl(formUuid);
      subformCtl.addRowData(formUuid, data, true, false);
    },

    copyRowDataByFormUuid: function (formUuid, data) {
      var subformCtl = this.getSubformControl(formUuid);
      subformCtl.copyRowData(formUuid, data, true, false);
    },
    /**
     * 添加行
     */
    addRowData: function (formId, data) {
      var formUuid = this.getFormUuid(formId);
      return this.addRowDataByFormUuid(formUuid, data);
    },

    updateRowDataByFormUuid: function (formUuid, data) {
      var subformCtl = this.getSubformControl(formUuid);
      if (subformCtl == undefined) {
        console.log("cann't find subformctl for formUuid[" + formUuid + ']');
        return;
      }
      subformCtl.updateRowData(data);
    },

    updateRowData: function (formId, data) {
      var formUuid = this.getFormUuid(formId);
      if (formUuid == undefined) {
        console.log("cann't find formUuid for formId[" + formId + ']');
        return;
      }
      this.updateRowDataByFormUuid(formUuid, data);
    },
    /**
     * 刪除行
     */
    deleteRowDataByFormUuid: function (formUuid, rowId) {
      if (formUuid == undefined) {
        console.log('parameters formUuid[' + formUuid + '] is undefined.');
        return;
      }
      var self = this;
      var subformCtl = self.getSubformControl(formUuid);
      subformCtl.delRowData(rowId);
      setTimeout(function () {
        // self.validateForm();// bug#39166
      }, 0);
    },
    /**
     * 刪除行
     */
    deleteRowData: function (formId, rowId) {
      var formUuid = this.getFormUuid(formId);
      if (formUuid) {
        this.deleteRowDataByFormUuid(formUuid, rowId);
      } else {
        console.log("cann't find formUuid for formId[" + formId + ']');
      }
    },

    deleteAllRowData: function (formId) {
      var formUuid = this.getFormUuid(formId);
      if (formUuid) {
        var subformCtl = this.getSubformControl(formUuid);
        subformCtl.clearSubform();
      } else {
        console.log("cann't find formUuid for formId[" + formId + ']');
      }
    },

    getRowDataByFormUuid: function (formUuid, rowId, fnSuccess, fnError) {
      var subformCtl = this.getSubformControl(formUuid);
      return subformCtl.getRowData(formUuid, rowId, fnSuccess, fnError);
    },

    getRowData: function (formId, rowId, fnSuccess, fnError) {
      var formUuid = this.getFormUuid(formId);
      if (formUuid) {
        return this.getRowDataByFormUuid(formUuid, rowId, fnSuccess, fnError);
      } else {
        console.log("cann't find formUuid for formId[" + formId + ']');
      }
    },

    /**
     * 获取选中的行
     * @param formUuid
     * @returns {Array}
     */
    getSelectedRowDataByFormUuid: function (formUuid, fnSuccess, fnError) {
      var subformCtl = this.getSubformControl(formUuid);
      if (typeof subformCtl.selectedRowId == 'undefined') {
        return [];
      }
      return subformCtl.getRowData(formUuid, subformCtl.selectedRowId, fnSuccess, fnError);
    },
    /* 获取选中的行 */
    getSelectedRowData: function (formId, fnSuccess, fnError) {
      var formUuid = this.getFormUuid(formId);
      if (formUuid) {
        return this.getSelectedRowDataByFormUuid(formUuid, fnSuccess, fnError);
      } else {
        console.log("cann't find formUuid for formId[" + formId + ']');
      }
    },

    collectSubformDataByFormUuid: function (formUuid, fnSuccessCallback, fnFailCallback) {
      var subformCtl = this.getSubformControl(formUuid);
      if (subformCtl == null || subformCtl == undefined) {
        var msg = 'formUuid=' + formUuid + '的从表不存在';
        console.error(msg);
        fnFailCallback.call(this, msg);
      }
      return subformCtl.collectSubformData(fnSuccessCallback, fnFailCallback);
    },
    group: function (formId) {
      var formUuid = this.getFormUuid(formId);
      if (formUuid) {
        var subformCtl = this.getSubformControl(formUuid);
        subformCtl.group();
      } else {
        console.log("cann't find formUuid for formId[" + formId + ']');
      }
    },

    /**
     * 获取从表的所有行数据
     *
     * @param formInfo
     *            {id:""}
     */
    getAllRowData: function (formInfo) {
      return this.collectSubformData(formInfo.id, formInfo.fnSuccessCallback, formInfo.fnFailCallback);
    },

    /**
     * 设置从表整列只读
     *
     * @param formId
     * @param mappingName
     */
    setColumnReadOnly: function (formId, mappingName) {
      this.setColumnAsLabel(formId, mappingName);
    },
    /**
     *
     * 设置整个从表只读
     *
     * @param formUuid
     */
    setSubformReadOnlyByFormUuid: function (formUuid) {
      var subformCtl = this.getSubformControl(formUuid);
      subformCtl.setReadOnly();
    },

    /**
     * 设置整个从表只读
     *
     * @param formId
     */
    setSubformReadOnly: function (formId) {
      var formUuid = this.getFormUuid(formId);
      this.setSubformReadOnlyByFormUuid(formUuid);
    },

    /**
     *
     * 设置整个从表只读
     *
     * @param formUuid
     */
    setSubformLabelByFormUuid: function (formUuid) {
      var subformCtl = this.getSubformControl(formUuid);
      subformCtl.setDisplayAsLabel();
    },

    /**
     * 设置整个从表只读
     *
     * @param formId
     */
    setSubformLabel: function (formId) {
      var formUuid = this.getFormUuid(formId);
      this.setSubformLabelByFormUuid(formUuid);
    },

    /**
     * 设置从表整列为标签
     *
     * @param formId
     * @param mappingName
     */
    setColumnAsLabel: function (formId, mappingName) {
      var formUuid = this.getFormUuid(formId);
      if (formUuid) {
        var fieldName = this.getFieldNameByApplyTo(mappingName, formUuid);
        var subformCtl = this.getSubformControl(formUuid);

        subformCtl.setColumnAsLabel(fieldName);
      } else {
        console.log("cann't find formUuid for formId[" + formId + ']');
      }
    },

    /**
     * 设置从表整列可编辑
     *
     * @param formId
     * @param mappingName
     */
    setColumnEditable: function (formId, mappingName) {
      var formUuid = this.getFormUuid(formId);
      if (formUuid) {
        var fieldName = this.getFieldNameByApplyTo(mappingName, formUuid);
        var subformCtl = this.getSubformControl(formUuid);
        subformCtl.setColumnEditable(fieldName);
      } else {
        console.log("cann't find formUuid for formId[" + formId + ']');
      }
    },

    /**
     *
     * @param formId
     * @param mappingName
     * @param controlable
     *            不传该参数或者为null默认为true,true:在从表中光标移出移出时没控件的标签/控件的切换效果,false:反之，有切换效果
     */
    setColumnCtl: function (formId, mappingName, controlable) {
      var formUuid = this.getFormUuid(formId);
      if (formUuid) {
        var fieldName = this.getFieldNameByApplyTo(mappingName, formUuid);
        var subformCtl = this.getSubformControl(formUuid);
        subformCtl.setColumnCtl(fieldName, controlable);
      } else {
        console.log("cann't find formUuid for formId[" + formId + ']');
      }
    },

    /**
     * 隐藏列(根据字段名)
     *
     * @param formId
     * @param mappingName
     */
    hideColumnByFieldName: function (formId, fieldName) {
      var formUuid = this.getFormUuid(formId);
      if (formUuid) {
        var subformctl = this.getSubformControl(formUuid);
        subformctl.hideColumn(fieldName);
        subformctl.resetGridWidth();
      } else {
        console.log("cann't find formUuid for formId[" + formId + ']');
      }
    },

    /**
     * 隐藏列
     *
     * @param formId
     * @param mappingName
     */
    hideColumn: function (formId, mappingName) {
      var formUuid = this.getFormUuid(formId);
      var fieldName = this.getFieldNameByApplyTo(mappingName, formUuid);
      if (fieldName) {
        this.hideColumnByFieldName(formId, fieldName);
      } else {
        console.log("cann't find fieldName for mappingName[" + mappingName + ']');
      }
    },

    /**
     * 显示列
     *
     * @param formId
     * @param mappingName
     */
    showColumn: function (formId, mappingName) {
      var formUuid = this.getFormUuid(formId);
      if (formUuid) {
        var fieldName = this.getFieldNameByApplyTo(mappingName, formUuid);

        var subformctl = this.getSubformControl(formUuid);
        subformctl.showColumn(fieldName);
        subformctl.resetGridWidth();
      } else {
        console.log("cann't find formUuid for formId[" + formId + ']');
      }
    },

    /**
     * 显示列
     *
     * @param formId
     * @param fieldName
     */
    showColumnByFieldName: function (formId, fieldName) {
      var formUuid = this.getFormUuid(formId);
      if (formUuid) {
        var subformctl = this.getSubformControl(formUuid);
        subformctl.showColumn(fieldName);
        subformctl.resetGridWidth();
      } else {
        console.log("cann't find formUuid for formId[" + formId + ']');
      }
    },

    /**
     * 通过从表的行ID获取对应的从表formUuid
     *
     * @param rowId
     * @returns
     */
    getFormUuidByRowId: function (rowId) {
      var self = this;
      if (typeof rowId == 'undefined' || rowId == null) {
        return self.getFormUuid();
      }
      var formUuid = null;
      self.getFormDatas(
        function (formDatas) {
          for (var i in formDatas) {
            var records = formDatas[i];
            for (var index = 0; index < records.length; index++) {
              if (records[index]['uuid'] == rowId) {
                return (formUuid = i); // fast break;
              }
            }
          }
        },
        function (errorInfo) {}
      );
      return formUuid;
    },
    getRowIds: function (formUuid) {
      var subformCtl = this.getSubformControl(formUuid);
      return subformCtl.getRowIds();
    },
    /*
     * addCustomBtn:function(options){ var formUuid =
     * this.getFormUuid(options.formId); var subformCtl =
     * this.getSubformControl(formUuid); subformCtl.addCustomBtn(options);
     * ///this.addCustomBtnByFormUuid(formUuid, options); }
     */

    /*
     * addCustomBtnByFormUuid:function(options){ var subformCtl =
     * this.getSubformControl(options.formUuid);
     * subformCtl.addCustomBtn(options); }
     */

    /**
     * 隐藏从表的操作按钮
     */
    hideOperateBtn: function (formId) {
      var formUuid = this.getFormUuid(formId);
      if (formUuid) {
        this.hideOperateBtnByFormUuid(formUuid);
      } else {
        console.log("cann't find formUuid for formId[" + formId + ']');
      }
    },
    hideOperateBtnByFormUuid: function (formUuid) {
      var subformctl = this.getSubformControl(formUuid);
      subformctl.hideOperateBtn();
    },

    /**
     * 显示从表的操作按钮
     */
    showOperateBtn: function (formId) {
      var formUuid = this.getFormUuid(formId);
      if (formUuid) {
        this.showOperateBtnByFormUuid(formUuid);
      } else {
        console.log("cann't find formUuid for formId[" + formId + ']');
      }
    },

    /**
     * 显示从表的操作按钮
     */
    showOperateBtnByFormUuid: function (formUuid) {
      var subformctl = this.getSubformControl(formUuid);
      subformctl.showOperateBtn();
    },
    /***********************************************************************
     * 根据默认行数添加从表空行
     */
    addRowEmptyDataByDefaultRowCount: function (formId) {
      var formUuid = this.getFormUuid(formId);
      var subformctl = this.getSubformControl(formUuid);
      if (typeof subformctl == 'undefined') {
        return;
      }
      var subformconfig = subformctl.options.formDefinition.subforms[formUuid];
      if (typeof subformconfig.defaultRowCount == 'undefined' || subformconfig.defaultRowCount == 0) {
        return;
      } else {
        var count = parseInt(subformconfig.defaultRowCount, 10);
        if (count == undefined || count == '' || count == 0) {
          return;
        }

        var formDatasOfOneForm = subformctl.getRowIds();
        if (formDatasOfOneForm.length > 0) {
          return;
        }
        for (var index = 0; index < count; index++) {
          var id = this.createUuid();
          this.addRowDataByFormUuid(formUuid, {
            id: id,
            uuid: id
          });
        }
      }
    },
    /**
     * 给所有的从表添加默认空行
     */
    addRowEmptyDatasByDefaultRowCount: function () {
      var formUuid = this.getFormUuid();
      var formDefinition = this.cache.get.call(this, cacheType.formDefinition, formUuid);
      var subforms = formDefinition.subforms;
      for (var i in subforms) {
        var subform = subforms[i];
        this.addRowEmptyDataByDefaultRowCount(subform.id || subform.outerId);
      }
    },
    /**
     * 上移
     */
    upSubformRowData: function (formUuid, selectRowId) {
      var subFormCtl = this.getSubformControl(formUuid);
      subFormCtl.upSubformRowDataEvent(formUuid, selectRowId);
    },
    /**
     * 下移
     */
    downSubformRowData: function (formUuid, selectRowId) {
      var subFormCtl = this.getSubformControl(formUuid);
      subFormCtl.downSubformRowDataEvent(formUuid, selectRowId);
    },
    /**
     * 清空從表的所有空行
     */
    clearSubformBlankRow: function (formUuid) {
      var subFormCtl = this.getSubformControl(formUuid);
      subFormCtl.clearSubformBlankRow();
    },
    /**
     * 从表导入
     */
    excelImp4MainForm: function (formEl) {
      window.excelImp4MainForm(formEl);
    },
    /**
     * 从表导出
     */
    excelExp4MainFormEx: function (outerId, formEl) {
      window.excelExp4MainFormEx(formEl, outerId);
    },
    /**
     * 获取选中行
     */
    getSelectedRowId: function (formUuid) {
      var subFormCtl = this.getSubformControl(formUuid);
      return subFormCtl.getSelectedRowId();
    },
    /**
     * 获取选中行(Array)
     */
    getSelectedRowIds: function (formUuid) {
      var subFormCtl = this.getSubformControl(formUuid);
      return subFormCtl.getSelectedRowIds();
    },
    /**
     * 添加子行
     */
    addSubformRowDataInJqGrid: function (formUuid, selectedRowId) {
      var subFormCtl = this.getSubformControl(formUuid);
      // var rowData = subFormCtl.getRowData(formUuid, selectedRowId);
      subFormCtl.getRowData(formUuid, selectedRowId, function (rowData) {
        subFormCtl.addSubformRowDataInJqGrid(formUuid, rowData);
      });
    },
    /**
     * 添加打开弹框
     */
    editSubformRowDataInDialog: function (formUuid, subform, data) {
      var subFormCtl = this.getSubformControl(formUuid);
      subFormCtl.editSubformRowDataInDialog(subform, data);
    }
  });

  $.dyform.extend({
    /**
     * 解析区块
     */
    parseBlock: function (formDefinition) {
      var _this = this;
      var blocks = formDefinition.blocks;
      var ns = _this.getOption().namespace;
      if (typeof blocks == 'undefined') {
        return;
      }

      for (var blockCode in blocks) {
        var block = blocks[blockCode];
        var hide = block['hide'];
        if (typeof hide == 'undefined') {
          // 没有hide属性，不处理
          continue;
        }

        var $block = _this.$('[blockcode~=' + blockCode + ']').closest('table');
        $block.addClass('container block-design').attr('name', blockCode);
        $.ContainerManager.createContainer($block, blockCode, formDefinition, {
          namespace: ns
        });

        if (hide == true) {
          this.hideBlock(blockCode);
        } else {
          this.showBlock(blockCode);
        }
      }
    },
    /**
     * 隐藏区块add by wujx 20160708
     */
    hideBlock: function (blockCode) {
      var container = $.ContainerManager.getContainer(blockCode);
      container.hide();
    },
    /**
     * 显示区块add by wujx 20160708
     */
    showBlock: function (blockCode) {
      var layout = $.ContainerManager.getContainer(blockCode);
      layout.show();
    },

    /**
     * 解析子表单
     */
    parseTemplate: function (templates) {
      var _this = this;
      for (var i in templates) {
        var _formData = loadFormDefinition(i);
        _this.parseBlock(_formData);
      }
    },
    /*
     * 解析tips
     * */
    parseTips: function (formDefinition) {
      var _this = this;
      var ns = _this.getOption().namespace;
      var tips = formDefinition.tips;
      if (typeof tips == 'undefined') {
        return;
      }
      var position = {
        top: 1,
        right: 2,
        bottom: 3,
        left: 4
      };
      for (var i in tips) {
        (function (i) {
          var tip = tips[i],
            type = tip['tipsType'];

          if (type == '1') {
            var pos = tip['tipsPosition'],
              content = tip['tipsContent'],
              tipsWidth = tip['tipsWidth'];
            var tips_index = 0;
            $('#' + i)
              .on('mouseover', '.tool-content', function (e) {
                e.stopPropagation();
                e.preventDefault();
                if (tips_index) {
                  return false;
                }
                tips_index = layer.tips(content, '#' + i, {
                  tips: position[pos],
                  offset: ['150px', '15px'],
                  skin: 'ctr-tips-content',
                  area: tipsWidth + 'px',
                  time: 900000,
                  end: function () {
                    tips_index = 0;
                  }
                });
              })
              .on('mouseout', '.tool-content', function (e) {
                e.stopPropagation();
                e.preventDefault();
                if (tips_index) {
                  layer.close(tips_index);
                }
              });
          }
        })(i);
      }
    },
    /**
     * 解析页签
     */
    parseTab: function (formDefinition) {
      var tabs = formDefinition.layouts;
      if (typeof tabs == 'undefined') {
        return;
      }
      for (var i in tabs) {
        var tab = tabs[i];
        var subtabs = tab['subtabs'];
        if (typeof subtabs == 'undefined') {
          // 没有subtabs属性，不处理
          continue;
        }
        for (var i in subtabs) {
          var subtab = subtabs[i];
          var hide = subtab['hide'];
          if (typeof hide == 'undefined') {
            // 没有hide属性，不处理
            continue;
          }

          if (hide == true) {
            this.hideTab(i);
          } else {
            this.showTab(i);
          }
        }
      }
    },
    /**
     * 隐藏页签add by zhongwd 20180910
     */
    hideTab: function (subTabName) {
      // var tabName = $("div[name='" + subTabName + "']")
      //   .closest('.tab')
      //   .attr('layoutname');
      var layout = $.ContainerManager.getLayout(subTabName);
      layout.hide();
    },
    /**
     * 显示页签add by zhongwd 20180910
     */
    showTab: function (subTabName) {
      // var tabName = $("div[name='" + subTabName + "']")
      //   .closest('.tab')
      //   .attr('layoutname');
      var layout = $.ContainerManager.getLayout(subTabName);
      layout.show();
    },
    /**
     * 主表解析
     *
     * @param formDefinition
     */
    parseMainForm: function (formDefinition, formData) {
      var _this = this;
      var ns = _this.getOption().namespace;
      // 解析布局
      _this.$($.dyform.nss(ns, '.tab-design[layoutName]')).each(function () {
        var name = $(this).attr('layoutName');
        if (!(name in formDefinition.layouts)) {
          return true; // continue;
        }

        $.ContainerManager.createContainer($(this), name, formDefinition, {
          namespace: ns
        });
      });

      // 解析控件
      _this.$($.dyform.nss(ns, '.value[fieldName]')).each(function () {
        var t1 = new Date().getTime();
        var $this = $(this);
        var name = $this.attr('name');
        var fieldName = $this.attr('fieldName');

        var fieldDefinition = formDefinition.getField(fieldName);

        if (typeof fieldDefinition == 'undefined') {
          return true; // continue;
        }

        var control;
        try {
          control = $.ControlManager.createCtl({
            $placeHolder: $this,
            fieldDefinition: fieldDefinition,
            formDefinition: formDefinition,
            $currentForm: _this
          });
          _this.cache.put.call(_this, cacheType.control, $.ControlManager[name]);

          //判断是否要初始化显示值控件的值
          var displayCtlName = fieldDefinition.realDisplay.display;
          if (displayCtlName && fieldDefinition.defaultValue) {
            _this.cache.put.call(_this, cacheType.initDisplay, {
              srcCtlName: name,
              displayCtlName: displayCtlName
            });
          }
          var initDisplay = _this.data('initDisplay') || {};
          for (var i in initDisplay) {
            if (name === initDisplay[i]) {
              $.ControlManager[i].control.setToRealDisplayColumn();
            }
          }
        } catch (e) {
          console && console.log(e);
          return true; // continue
        }

        if (typeof formData != 'undefined') {
          var value = formData[fieldName];
          if (typeof value != 'undefined' && value != null) {
            $.dyform.setValue(control, value, null);
          }
        }

        if (control && control.setPos) {
          control.setPos(dyControlPos.mainForm);
        }
        console.log('创建控件耗时：' + ((new Date().getTime() - t1) / 1000.0 + 's'), fieldDefinition);
      });

      // 自定义JS,formDefinition.customJs已废弃使用。
      /**
       * ************这段代码不要去掉了，已废弃 2015/07/09 by hunt begin***************
       */
      if (formDefinition.customJs && $.trim(formDefinition.customJs).length > 0) {
        try {
          eval(formDefinition.customJs);
        } catch (e) {
          console.error(e);
        }
      }
      /**
       * ************这段代码不要去掉了，已废弃 2015/07/09 by hunt end***************
       */
    },

    /**
     * 从表解析
     *
     * @param formDefinition
     */
    parseSubform: function (formDefinition, definitions) {
      var _this = this;
      var ns = _this.getOption().namespace;
      this.$($.dyform.nss(ns, 'table[formUuid]')).each(function () {
        var formUuid = $(this).attr('formUuid');
        var subformDefinition = null;
        if (typeof definitions == 'undefined' || typeof definitions[formUuid] == 'undefined') {
          subformDefinition = loadFormDefinition(formUuid);
        } else {
          subformDefinition = definitions[formUuid];
        }

        if (typeof subformDefinition == 'undefined' || subformDefinition == null) {
          return true;
        }
        $.ControlManager.createSubFormControl($(this), _this, formDefinition, subformDefinition);
        // 自定义JS
        if (subformDefinition.customJs && $.trim(subformDefinition.customJs).length > 0) {
          try {
            eval(subformDefinition.customJs);
          } catch (e) {
            console.error(e);
          }
        }
      });
    },

    /**
     * 解析视图列表组件
     * @param formDefinition
     * @param formData
     */
    parseTableView: function (formDefinition, formData) {
      var _this = this;
      var ns = _this.getOption().namespace;
      this.$($.dyform.nss(ns, 'table[tableviewid]')).each(function () {
        var tableViewId = $(this).attr('tableviewid');
        var tableViewDefinition = formDefinition.tableView[tableViewId];
        if (typeof tableViewDefinition == 'undefined' || tableViewDefinition == null) {
          return true;
        }
        $.ControlManager.createTableViewControl($(this), _this, formDefinition, tableViewDefinition, formData);
      });
    },
    parseFileLibrary: function (formDefinition, formData) {
      var _this = this;
      var ns = _this.getOption().namespace;
      this.$($.dyform.nss(ns, 'table[filelibraryid]')).each(function () {
        var fileLibraryId = $(this).attr('filelibraryid');
        var fileLibraryDefinition = formDefinition.fileLibrary[fileLibraryId];
        if (typeof fileLibraryDefinition == 'undefined' || fileLibraryDefinition == null) {
          return true;
        }
        $.ControlManager.createFileLibraryControl($(this), _this, formDefinition, fileLibraryDefinition, formData);
      });
    },
    /**
     * 缓存在这里集中管理,数据从这里保存和获取,统一入口和出口
     */
    cache: {
      put: function (type, data) {
        switch (type) {
          case cacheType.formUuid: // 表单定义的uuid
            $(this).data('formUuid', data);
            break;
          case cacheType.options: // 表单的一些设置
            $(this).data('options', data);
            break;
          case cacheType.formDefinition:
            var formDefinitions = $(this).data('formDefinition');
            if (typeof formDefinitions == 'undefined' || formDefinitions == null) {
              formDefinitions = {};
            }
            formDefinitions[data.uuid] = data;
            $(this).data('formDefinition', formDefinitions);
            break;
          case cacheType.dataUuid:
            $(this).data('dataUuid', data);
            break;
          case cacheType.control:
            var $this = $(this);
            var control = $this.data('control') || {};
            control[data.$placeHolder.attr('name')] = data;
            $this.data('control', control);
            break;
          // 运算公式缓存处理
          case cacheType.formula:
            break;
          case cacheType.initDisplay:
            var initDisplay = $(this).data('initDisplay') || {};
            initDisplay[data.srcCtlName] = data.displayCtlName;
            $(this).data('initDisplay', initDisplay);
            break;
          default:
            throw new Error('unknown cacheType when put data into cache');
        }
      },
      get: function (type, key) {
        var data = null;
        switch (type) {
          case cacheType.formUuid: // 表单定义的uuid
            data = $(this).data('formUuid');
            break;
          case cacheType.options: // 表单的一些设置
            data = $(this).data('options');

            break;
          case cacheType.formDefinition:
            // 这里需要知道欲获取哪个表单的定义，所以需要通过key将表单定义的uuid传进来
            if (typeof key == 'undefined' || key == null) {
              // throw new Error("parameter[key] should be
              // passed");

              data = $(this).data('formDefinition');
            } else {
              var formDefinitions = $(this).data('formDefinition');
              if (typeof formDefinitions == 'undefined' || formDefinitions == null) {
                throw new Error('there is no form definition in the cache');
              }

              data = formDefinitions[key];
            }

            break;
          case cacheType.dataUuid:
            data = $(this).data('dataUuid');
            break;
          case cacheType.control:
            var control = $(this).data('control');
            if (control == null || key == null) {
              data = control;
            } else {
              data = control[key] && control[key].control;
            }
            break;
          case cacheType.formula:
            data = $(this).data(cacheType.formula);
            if (typeof data == 'undefined' || data == null) {
              data = {};
            }
            break;
          case cacheType.initDisplay:
            data = $(this).data('initDisplay');
            break;
          default:
            throw new Error('unknown cachetype when get data from cache');
        }
        return data;
      }
    },

    collectShouldValidatedInfo: function (formUuid) {
      var _this = this;
      var formDefinition = this.cache.get.call(this, cacheType.formDefinition, formUuid);
      var ns = this.getNamespace() || formUuid;

      if (!formDefinition.formTree || !window.reverseSubformTree) {
        formDefinition.updateFormTree();
        formDefinition.updateReverseFormTree(ns);
      }

      var shouldValidatedFields = [];
      var invisibleFields = [];
      var shouldValidatedSubForms = [];
      var invisibleSubforms = [];
      var formValidationInfo = {};

      formValidationInfo[ns] = {
        shouldValidatedFields: shouldValidatedFields,
        invisibleFields: invisibleFields,
        shouldValidatedSubForms: shouldValidatedSubForms,
        invisibleSubforms: invisibleSubforms
      };

      // TODO: to be deleted! only for test
      // _this.hideTab('subtbl_3TDUYWID3L3GF2QV');
      // _this.hideSubFormByFormUuid('6dbc2248-54c3-4b99-9237-ec64aa67d4b4');
      // _this.hideBlock('block1');
      // ------------------------------------------

      // 获取主表需要校验的字段
      for (var fieldName in window.reverseFieldTree[ns]) {
        var control = _this.getControl(fieldName);

        if (control && control.isValidationNeeded()) {
          shouldValidatedFields.push(fieldName);

          var isHide = control.isHide();
          if (isHide) {
            invisibleFields.push(fieldName);
          }
        }
      }

      // 获取需要校验的从表
      for (var subformUuid in window.reverseSubformTree[ns]) {
        var subformDef = window.reverseSubformTree[ns][subformUuid];
        var subformControl = _this.getSubformControl(subformUuid);

        if (subformControl.isValidationNeeded()) {
          shouldValidatedSubForms.push(subformUuid);
          if (subformControl.isHide()) {
            invisibleSubforms.push(subformUuid);
            for (var i = 0; i < subformDef.children.length; i++) {
              var field = subformDef.children[i];
              invisibleFields.push(field.name);
            }
          }
        }
      }

      return formValidationInfo;
    },

    validateMainform: function (shouldValidatedFields) {
      var _this = this;
      var valid = true;

      $.each(shouldValidatedFields, function (idx, fieldName) {
        var control = _this.getControl(fieldName);
        var isControlValid = control.validate();
        valid = valid && isControlValid;
      });

      return valid;
    },

    validateSubform: function (shouldValidatedSubForms) {
      var _this = this;
      var valid = true;

      $.each(shouldValidatedSubForms, function (idx, subformUuid) {
        var subformControl = _this.getSubformControl(subformUuid);
        var isFieldsValid = subformControl.validate();
        var hasBlankRows = subformControl.validateSubform();
        valid = valid && isFieldsValid && hasBlankRows;
      });

      return valid;
    },

    validateMainform_old: function (formUuid) {
      var _this = this;
      var ns = _this.getOption().namespace;
      var valid = true;
      var formId = this.getFormId();
      var _this = this;

      this.$($.dyform.nss(ns, '.value[fieldName]')).each(function () {
        var fieldName = $(this).attr('fieldName');
        if ($.dyform.isValidateIgnore(formId, fieldName)) {
          // 不需要验证的字段
          return true;
        } else {
          var control = _this.getControl(fieldName);
          if (typeof control == 'undefined' || control == null || control.getPos() == dyControlPos.subForm) {
            // 从表不需要验证
            return true;
          }
        }

        var time3 = new Date().getTime();

        var v = true;
        v = control.validate();
        var time4 = new Date().getTime();
        // console.log(fieldName + "[" +
        // (control.getDisplayName == undefined?
        // "" : control.getDisplayName()) +
        // (control.getInputMode == undefined?
        // "" : control.getInputMode()) +
        // "]验证所用时间:" + (time4 - time3) / 1000.0
        // + "s");
        valid = valid && v;
      });
      return valid;
    },

    validateSubform_old: function (formUuid) {
      var self = this;
      // var rulesObj = {rules:{}, messages:{}};
      var formDefinition = this.cache.get.call(this, cacheType.formDefinition, formUuid);
      var valid = true;
      var subforms = formDefinition.subforms;
      for (var i in subforms) {
        // 遍历各从表
        try {
          var subformcontrol = self.getSubformControl(subforms[i].formUuid);
          if (!subformcontrol) {
            continue;
          }
          var v = subformcontrol.validate();
          var v2 = subformcontrol.validateSubform();
          valid = valid && v && v2;
        } catch (e) {
          console.log(e);
        }
      }
      return valid;
    },

    /**
     * 获取主表数据
     *
     * @param formUuid
     */
    collectMainformData: function (success, error) {
      var formData = {};
      var _this = this;

      var promiseFuns = [];
      var ns = _this.getOption().namespace;
      this.$($.dyform.nss(ns, ".value[fieldName][pos!='" + dyControlPos.subForm + "']")).each(function () {
        // 遍历主表的占位符
        var fieldName = $(this).attr('fieldName');
        var control = _this.getControl(fieldName);
        if (typeof control == 'undefined' || control == null) {
          return true;
        }

        var promiseFun = function () {
          var def = $.Deferred();
          if (control.isGetValueByAsyn && control.isGetValueByAsyn()) {
            // 需要异步取值
            control.getValue(
              function (value) {
                def.resolve(fieldName, value);
              },
              function (errorInfo) {
                def.reject(fieldName, errorInfo);
              }
            );
          } else {
            def.resolve(fieldName, $.dyform.getValue(control));
          }

          return def.promise();
        };
        promiseFuns.push(promiseFun);
      });

      var fnThen = function (index) {
        promiseFuns[index]().then(
          function (fieldName, value) {
            formData[fieldName] = value;
            if (index == promiseFuns.length - 1) {
              // 最后一个元素
              // console.log("数据收集完成");
              // console.log(formData);
              var dataUuid = _this.getDataUuid(); // 先从缓存中获取
              if (typeof dataUuid == 'undefined') {
                dataUuid = _this.createUuid();
                _this.setDataUuid(dataUuid);
              }
              formData['uuid'] = dataUuid; // 主表的uuid
              // console.log("成功回调");
              success.call(_this, formData);
            } else {
              // 执行下个控件的取值
              fnThen(index + 1);
            }
          },
          function (fieldName, errorInfo) {
            // 附件上传失败
            console.log(fieldName + '字段取值， 失败回调');
            error.call(_this, errorInfo);
          }
        );
      };
      if (promiseFuns.length == 0) {
        // 主表没有控件
        var dataUuid = _this.getDataUuid(); // 先从缓存中获取
        if (typeof dataUuid == 'undefined') {
          dataUuid = _this.createUuid();
          _this.setDataUuid(dataUuid);
        }
        formData['uuid'] = dataUuid; // 主表的uuid
        console.log('成功回调');
        success.call(_this, formData);
      } else {
        fnThen(0);
      }
    },

    /**
     * 获取主表显示数据
     *
     * @param formUuid
     */
    collectMainformDisplayData: function (formUuid) {
      var _this = this;
      var ns = _this.getOption().namespace;
      var formData = {};
      this.$($.dyform.nss(ns, '.value[fieldName]')).each(function () {
        // 遍历主表的占位符
        var fieldName = $(this).attr('fieldName');
        var control = _this.getControl(fieldName);
        if (typeof control == 'undefined' || control == null) {
          return true;
        }
        formData[fieldName] = $.dyform.getDisplayValue(control);
      });

      var dataUuid = this.getDataUuid(); // 先从缓存中获取
      if (typeof dataUuid == 'undefined') {
        dataUuid = this.createUuid();
        this.setDataUuid(dataUuid);
      }
      formData['uuid'] = dataUuid; // 主表的uuid

      return formData;
    },
    loadDefaultFormData: function (formUuid) {
      var formData = {};
      var url = ctx + '/pt/dyform/data/getDefaultFormData';
      $.ajax({
        url: url,
        cache: false,
        async: false, // 同步完成
        type: 'POST',
        data: {
          formUuid: formUuid
        },
        dataType: 'json',
        success: function (result) {
          if (result.success == 'true' || result.success == true) {
            formData = result.data;
          } else {
          }
        },
        error: function (data) {
          console.log(JSON.cStringify(data));
        }
      });
      return formData;
    },

    /**
     * 收集表单各控件的数据
     */
    getFormDatas: function (success, error) {
      var _this = this;
      var formDatas = {};
      var formUuid = this.getFormUuid();
      // console.log("为formUuid[" + formUuid + "]收集数据");
      var formDefinition = this.cache.get.call(this, cacheType.formDefinition, formUuid);

      // 收集从表数据
      var subforms = formDefinition.subforms;
      var promiseFuns = [];
      for (var i in subforms) {
        (function (j) {
          var subform = subforms[j];
          var subformctl = _this.getSubformControl(subform.formUuid);
          if (!subformctl) {
            return;
          }
          promiseFuns.push(function () {
            var def = $.Deferred();
            subformctl.collectSubformData(
              function (formData) {
                def.resolve(subform.formUuid, formData);
              },
              function (errorInfo) {
                def.reject(errorInfo);
              }
            );
            return def.promise();
          });
        })(i);
      }

      promiseFuns.push(function () {
        // 收集主表数据
        var def = $.Deferred();
        var mainformData = _this.collectMainformData(
          function (mainformData) {
            // 收集成功
            var mainformDatas = [];
            mainformDatas.push(mainformData);
            def.resolve(formUuid, mainformDatas);
          },
          function (errorInfo) {
            // 收集失败
            def.reject(errorInfo);
          }
        );
        return def.promise();
      });

      var fnThen = function (index) {
        promiseFuns[index]().then(
          function (formUuid, formData) {
            for (var i in formData) {
              var formDataOfOneForm = formData[i];
              formDataOfOneForm['form_uuid'] = formUuid;
              formDataOfOneForm['sort_order'] = formDataOfOneForm['seqNO'];
              _this.compareNewAndOldData(_this.getOldFormData(formUuid, formDataOfOneForm.uuid), formDataOfOneForm); // 新旧数据对比
            }

            formDatas[formUuid] = formData;
            if (index == promiseFuns.length - 1) {
              // 最后一个
              success.call(_this, formDatas);
            } else {
              fnThen(index + 1);
            }
          },
          function (errorInfo) {
            // 附件上传失败
            error.call(_this, errorInfo);
          }
        );
      };
      fnThen(0);
    },

    /**
     * 获取表单初始化时传入的数据
     */
    getOldFormData: function (formUuid, dataUuid) {
      var oldFormDatas = this.getOption().formData.formDatas;
      if (oldFormDatas == undefined || oldFormDatas == null || this.objectLength(oldFormDatas) == 0) {
        return {};
      }

      var oldFormDatasOfOneForm = oldFormDatas[formUuid];
      if (oldFormDatasOfOneForm == undefined || oldFormDatasOfOneForm == null || this.objectLength(oldFormDatasOfOneForm) == 0) {
        return {};
      }

      for (var i in oldFormDatasOfOneForm) {
        var oldFormData = oldFormDatasOfOneForm[i];
        if (oldFormData.uuid == dataUuid) {
          return oldFormData;
        }
      }
      return {};
      // return oldFormDatasOfOneForm[dataUuid];
    },

    /**
     * 新旧数据对比
     */
    compareNewAndOldData: function (oldFormData, formData) {
      var dataUuid = formData['uuid'];
      var formUuid = formData['form_uuid'];
      if (
        oldFormData == undefined ||
        this.objectLength(oldFormData) == 0 ||
        (GetRequestParam().ac_id == 'btn_list_view_copy' && GetRequestParam().idValue != '' && GetRequestParam().idValue != formData.uuid)
      ) {
        //复制源文 dataUuid 要用新的，避免保存不了
        dataUuid = this.createUuid();
        formData.uuid = dataUuid;
        // 新数据,标记为新增
        this.markAsAddedFormData(formUuid, dataUuid);
      } else {
        // 旧数据
        if (this.isMarkedAsNewFormData(formUuid, dataUuid)) {
          // 已被标记为新增
          return;
        } else {
          formData['modify_time'] = oldFormData['modify_time']; // 上一次的修改时间
          var updatedFields = [];
          // 字段比较忽略大小写
          var _formData = {};
          var _oldFormData = {};
          // 字段转小写后比较
          for (var _f in formData) {
            _formData[_f.toLowerCase()] = formData[_f];
          }
          for (var _f in oldFormData) {
            _oldFormData[_f.toLowerCase()] = oldFormData[_f];
          }
          for (var field in _formData) {
            var newV = _formData[field];
            var oldV = _oldFormData[field];
            if (typeof newV == 'object' && typeof oldV == 'object' && newV instanceof Array) {
              // 附件
              if (newV.length == 0 && oldV == null) {
                continue;
              }
              if (oldV instanceof Array && this.objectLength(newV) != this.objectLength(oldV)) {
                updatedFields.push(field);
              } else {
                var equal = WellFileUpload.isEqual(newV, oldV);
                if (!equal) {
                  // 前后的值不一致
                  updatedFields.push(field);
                }
              }
            } else if (newV !== oldV) {
              if (!(newV === '' && (oldV === null || oldV === undefined || oldV === ''))) {
                updatedFields.push(field);
              }
            }
          }
          if (updatedFields.length > 0) {
            this.markAsUpdatedFields(formUuid, dataUuid, updatedFields);
          } else {
            //对比无字段更新的情况
            var updatedFormDatas = this.getOption().formData.updatedFormDatas;
            updatedFormDatas[formUuid] = updatedFormDatas[formUuid] || {};
            updatedFormDatas[formUuid][dataUuid] = [];
          }
        }
      }
    },

    /**
     * 判断是否已被标记为新增数据
     */
    isMarkedAsNewFormData: function (formUuid, dataUuid) {
      var addedFormDatas = this.getOption().formData.addedFormDatas;
      if (addedFormDatas == null || addedFormDatas == undefined) {
        return false;
      }

      var addedFormDatasOfOneForm = addedFormDatas[formUuid];
      if (addedFormDatasOfOneForm == null || addedFormDatasOfOneForm == undefined || addedFormDatasOfOneForm.length == 0) {
        return false;
      }

      for (var du in addedFormDatasOfOneForm) {
        if (du == dataUuid) {
          return true;
        }
      }
      return false;
    },

    /**
     * 标记为新数据
     */
    markAsAddedFormData: function (formUuid, dataUuid) {
      var addedFormDatas = this.getOption().formData.addedFormDatas;
      var addedFormDatasOfOneForm = addedFormDatas[formUuid];
      if (addedFormDatasOfOneForm == null || addedFormDatasOfOneForm == undefined) {
        addedFormDatas[formUuid] = [];
      }

      if (dataUuid) {
        addedFormDatas[formUuid].push(dataUuid);
      }
    },

    /**
     * 标记为编辑字段
     */
    markAsUpdatedFields: function (formUuid, dataUuid, updatedFields) {
      var updatedFormDatas = this.getOption().formData.updatedFormDatas;
      var updatedFormDatasOfOneForm = updatedFormDatas[formUuid];
      if (updatedFormDatasOfOneForm == null || updatedFormDatasOfOneForm == undefined) {
        updatedFormDatas[formUuid] = {};
        updatedFormDatas[formUuid][dataUuid] = [];
      }
      if (updatedFormDatas[formUuid][dataUuid] == null || updatedFormDatas[formUuid][dataUuid] == undefined) {
        updatedFormDatas[formUuid][dataUuid] = [];
      }

      for (var j in updatedFields) {
        var field = updatedFields[j];
        var hasExist = false;
        for (var i in updatedFormDatas[formUuid][dataUuid]) {
          if (updatedFormDatas[formUuid][dataUuid][i] == field) {
            hasExist = true;
          }
        }
        if (!hasExist) {
          updatedFormDatas[formUuid][dataUuid].push(field);
        }
      }
    },
    /**
     * 标记为编辑字段
     */
    markAsUpdatedField: function (formUuid, dataUuid, updatedField) {
      var array = [];
      array.push(updatedField);
      this.markAsUpdatedFields(formUuid, dataUuid, array);
    },

    /**
     * 判断是否被编辑过
     */
    hasBeenModified: function (callback) {
      var _self = this;
      var _old = this.getOption().formData.formDatas[this.getFormUuid()][0];
      this.getFormDatas(function (data) {
        _self.compareNewAndOldData(_old, data[_self.getFormUuid()][0]);
        var formData = _self.getOption().formData;
        var formUuid = formData.formUuid;
        var dataUuid = formData.dataUuid;
        callback(
          !$.isEmptyObject(formData.deletedFormDatas) ||
            !$.isEmptyObject(formData.addedFormDatas) ||
            formData.updatedFormDatas[formUuid][dataUuid].length
        );
      });
    },

    /**
     * 收集表单各控件的显示数据
     */
    getFormDisplayDatas: function () {
      var _this = this;
      var formDatas = {};
      var formUuid = this.getFormUuid();
      // console.log("为formUuid[" + formUuid + "]收集数据");
      var formDefinition = this.cache.get.call(this, cacheType.formDefinition, formUuid);

      // 收集从表数据

      var subforms = formDefinition.subforms;
      for (var i in subforms) {
        var subform = subforms[i];
        try {
          var subformctl = _this.getSubformControl(subform.formUuid);
          if (!subformctl) {
            continue;
          }
          formDatas[subform.formUuid] = subformctl.collectSubformDisplayData();
        } catch (e) {
          console.log(e);
        }
      }
      // formData.subformData = subformData;
      // 收集主表数据

      formDatas[formUuid] = [];
      formDatas[formUuid].push(this.collectMainformDisplayData(formUuid));
      return formDatas;
    },
    /**
     * 收集被删除的行数据
     */
    getDeletedRowIds: function () {
      var formUuid = this.getFormUuid();
      var formDefinition = this.cache.get.call(this, cacheType.formDefinition, formUuid);
      var deletedFormDatas = {};

      var subforms = formDefinition.subforms;
      for (var i in subforms) {
        var subform = subforms[i];
        try {
          var subformctl = this.getSubformControl(subform.formUuid);
          if (!subformctl) {
            continue;
          }
          deletedFormDatas[subform.formUuid] = subformctl.getDeleteRows();
        } catch (e) {
          console.log(e);
        }
      }
      return deletedFormDatas;
    },

    /**
     * 收集被删除的行数据
     */
    clearDeleteRows: function () {
      var formUuid = this.getFormUuid();
      var formDefinition = this.cache.get.call(this, cacheType.formDefinition, formUuid);
      var subforms = formDefinition.subforms;
      for (var i in subforms) {
        var subform = subforms[i];
        try {
          var subformctl = this.getSubformControl(subform.formUuid);
          if (!subformctl) {
            continue;
          }
          subformctl.clearDeleteRows();
        } catch (e) {
          console.log(e);
        }
      }
    },

    /**
     * 获取从表的所有行数据
     *
     * @param formId
     * @returns
     */
    collectSubformData: function (formId, fnSuccessCallback, fnFailCallback) {
      var formUuid = this.getFormUuid(formId);
      return this.collectSubformDataByFormUuid(formUuid, fnSuccessCallback, fnFailCallback);
    },
    /**
     * 获取从表的所有行显示数据
     *
     * @param formId
     * @returns
     */
    collectSubformDisplayData: function (formId, fnSuccessCallback, fnFailCallback) {
      var formUuid = this.getFormUuid(formId);
      return this.collectSubformDisplayDataByFormUuid(formUuid, fnSuccessCallback, fnFailCallback);
    },
    // 更新整个表单的数据
    refreshFormDatas: function (dyformData, callback) {
      var formUuid = this.getFormUuid();
      for (var i in dyformData.formDatas) {
        var formDatasOfOneForm = dyformData.formDatas[i];
        if (i == formUuid) {
          this.fillFormDataOfMainform(formDatasOfOneForm[0]);
        } else {
          var subformControl = this.getSubformControl(i);
          subformControl.refreshFormDatas(formDatasOfOneForm, function () {});
        }
      }
      // getSubformControl
      // var oldFormDatas = this.getOption().formData.formDatas;
      this.getOption().formData = dyformData;
      if (callback) {
        callback();
      }
    }
  });

  /**
   * 私有方法，请勿调用
   */
  $.dyform.extend({
    /**
     * 异步填充从表数据
     */
    fillSubformData: function (formDatas, subformUuids, index, optional) {
      var _this = this;
      var i = subformUuids[index];
      var formData = formDatas[i];
      var subformctl = _this.getSubformControl(i);
      var timex = new Date().getTime();
      subformctl.setMainformDataUuid(this.getDataUuid());

      var optionalType = typeof optional;
      if (optionalType == 'function') {
        var callback = optional;
        optional = {
          async: this.getOption().async,
          callback: callback
        };
      } else if (optionalType == 'undefined') {
        optional = {
          async: this.getOption().async,
          callback: function () {}
        };
      } else {
        if (optional.async == undefined) {
          optional.async = this.getOption().async;
        }
      }

      if (index == subformUuids.length - 1) {
        subformctl.fillFormData(formData, {
          async: optional.async,
          callback: function () {
            var timey = new Date().getTime();
            console.log(
              '填充从表[' + subformctl.getDisplayName() + '][' + formData.length + ']的数据所用时间:' + (timey - timex) / 1000.0 + 's'
            );
            optional.callback();
          }
        });
      } else {
        subformctl.fillFormData(formData, {
          async: optional.async,
          asyncFillData: index > 4,
          callback: function () {
            var timey = new Date().getTime();
            console.log(
              '填充从表[' + subformctl.getDisplayName() + '][' + formData.length + ']的数据所用时间:' + (timey - timex) / 1000.0 + 's'
            );
            _this.fillSubformData(formDatas, subformUuids, index + 1, optional);
          }
        });
      }
    }
  });
});
