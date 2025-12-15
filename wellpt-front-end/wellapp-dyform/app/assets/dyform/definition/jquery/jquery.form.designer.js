+(function ($) {
  var DyformDesigner = function ($element, options) {
    var self = this;
    self.options = options = $.extend({}, $.fn.dyformdesigner.defaults, options);
    // 先隐藏，初始化后显示
    self.$element = $element;
    window.initDyform = function (DesignerUtils) {
      var win = (self.editorWin = this);
      self.DesignerUtils = DesignerUtils;
      DesignerUtils.editorOptions = self.options.editorOptions;
      var $body = $(win.document.body);
      if (options.cssFiles) {
        if (typeof options.cssFiles === 'string') {
          options.cssFiles = [options.cssFiles];
        }
        $.each(options.cssFiles, function (idx, cssFile) {
          $body.append(
            $('<link>', {
              rel: 'stylesheet',
              href: ctx + cssFile
            })
          );
        });
      }
      if (options.jsFiles) {
        if (typeof options.jsFiles === 'string') {
          options.jsFiles = [options.jsFiles];
        }
        $.each(options.jsFiles, function (idx, jsFile) {
          $body.append(
            $('<script>', {
              src: ctx + jsFile,
              type: 'text/javascript'
            })
          );
        });
      }
      if (!DesignerUtils.onMaximize && options.onMaximize) {
        DesignerUtils.onMaximize = function (state) {
          var $ckeInner = $body.find('div.cke_inner');
          var $leftPanel = $body.find('#field-tabs').parent('td');
          var winWidth = $(win).width();
          if (state === 'max') {
          } else if (state === 'min') {
          }
          options.onMaximize.apply(self, arguments);
        };
      }
      if (options.defaultShowFieldList) {
        $body.find('#field-tabs>.nav-tabs>li.active').removeClass('active').hide();
        $body.find('#field-tabs>.nav-tabs>li:eq(1)').addClass('active').find('a').text('字段列表');
        $body.find('.tab-content>#js-tab-content-control').removeClass('active');
        $body.find('.tab-content>#js-tab-content-control2').addClass('active');
      }
      $body.find('.tab-content>#tab1').removeClass('active');
      $body.find('.tab-content>#tab2').addClass('active');
      $body.find('input#mainTableCnName').val('');
      $element.show();
    };
    self.initEditor(options.formUuid);
  };

  $.extend(DyformDesigner.prototype, {
    getFormDefinition: function () {
      var self = this;
      if (!self.DesignerUtils || !self.DesignerUtils.getFormDefinition) {
        throw Error('表单未初始化,稍后重试');
      }
      // 保存
      return self.DesignerUtils.getFormDefinition('0');
    },
    setFormDefinition: function (formDefinition) {
      var self = this;
      var options = self.options;
      self.initEditor(formDefinition.uuid);
      options.formDefinition = formDefinition;
    },
    setFormTitleContent: function (content) {
      var self = this;
      if (!self.DesignerUtils || !self.DesignerUtils.setTitleContent) {
        throw Error('表单设置标题二开方法不存在');
      }
      // 保存
      return self.DesignerUtils.setTitleContent(content);
    },
    copyForm: function (formUuid) {
      var self = this;
      self.$element.hide();
      var options = self.options;
      if ($.trim(formUuid).length > 0) {
        options.formUuid = formUuid;
      }
      // 默认为“自定义扩展表单 ”模块
      var url = '/pt/dyform/definition/form-copy?moduleId=' + (options.moduleId || 'mod_20191126160547');
      if ($.trim(options.formUuid).length > 0) {
        url += '&uuid=' + options.formUuid;
      }
      self.$element.removeAttr('src').attr('src', url);
    },
    initView: function (formUuid) {
      var self = this;
      self.$element.show();
      var options = self.options;
      if ($.trim(formUuid).length > 0) {
        options.formUuid = formUuid;
      }
      // 默认为“自定义扩展表单 ”模块
      var url = '/pt/dyform/definition/open?isView=true&moduleId=' + (options.moduleId || 'mod_20191126160547');
      if ($.trim(options.formUuid).length > 0) {
        url += '&formUuid=' + options.formUuid;
      }
      self.$element.removeAttr('src').attr('src', url);
    },
    initEditor: function (formUuid) {
      var self = this;
      self.$element.hide();
      var options = self.options;
      if ($.trim(formUuid).length > 0) {
        options.formUuid = formUuid;
      }
      // 默认为“自定义扩展表单 ”模块
      var url = '/pt/dyform/definition/pform-designer?moduleId=' + (options.moduleId || 'mod_20191126160547');
      if ($.trim(options.formUuid).length > 0) {
        url += '&uuid=' + options.formUuid;
      }
      self.$element.removeAttr('src').attr('src', url);
    }
  });
  /**
   *
   *
   *
   *
   */
  $.fn.dyformdesigner = function (option) {
    if (!this[0] || this[0].nodeName != 'IFRAME') {
      throw new Error('dataform container must be a form iframe element');
    }
    var $this = $(this),
      data = $this.data('dyformdesigner'),
      options = typeof option == 'object' && option;
    if (!data) {
      data = new DyformDesigner($this, options);
      $this.data('dyformdesigner', data);
    }
    if (typeof option === 'string' && $.isFunction(data[option])) {
      return data[option].apply(data, $.makeArray(arguments).slice(1));
    } else {
      return data;
    }
  };
  $.fn.dyformdesigner.defaults = {
    onMaximize: function (state) {
      if (state === 'max') {
      } else if (state === 'min') {
      }
    },
    moduleId: null,
    jsFiles: null,
    cssFiles: null,
    defaultShowFieldList: true
  };
})(jQuery);
