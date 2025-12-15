define(['ui_component', 'design_commons', 'constant', 'commons', 'server', 'tinymce', 'wSelect2'], function (
  ui_component,
  designCommons,
  constant,
  commons,
  server,
  tinymce
) {
  var component = $.ui.component.BaseComponent();
  component.prototype.create = function () {
    $(this.element).css('overflow', 'auto');
  };
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  component.prototype.getPropertyConfigurer = function () {
    var collectClass = 'w-configurer-option';
    var configurer = $.ui.component.BaseComponentConfigurer();
    configurer.prototype.onLoad = function ($container, options) {
      // 初始化页签项
      $('#widget_html_tabs ul a', $container).on('click', function (e) {
        e.preventDefault();
        $(this).tab('show');
      });
      var configuration = $.extend(true, {}, options.configuration);
      this.initConfiguration(configuration, $container);
    };
    // 初始化配置信息
    configurer.prototype.initConfiguration = function (configuration, $container) {
      // 基本信息
      this.initBaseInfo(configuration, $container);
      // 内容信息
      this.initContentInfo(configuration, $container);
    };
    // 初始化配置信息
    configurer.prototype.initBaseInfo = function (configuration, $container) {
      // 设置值
      designCommons.setElementValue(configuration, $container);

      // 加载的JS模块
      $('#jsModule', $container).wSelect2({
        serviceName: 'appJavaScriptModuleMgr',
        params: {
          dependencyFilter: 'HtmlWidgetDevelopment'
        },
        labelField: 'jsModuleName',
        valueField: 'jsModule',
        remoteSearch: false,
        multiple: true
      });
    };
    // 初始化内容信息
    configurer.prototype.initContentInfo = function (configuration, $container) {
      var component = this.component;
      var options = component.options || {};
      var content = configuration.content || options.content;
      $('textarea#wHtml_content').val(content);
      $('#htmlSourceType', $container).val(configuration.htmlSourceType || 'html_from_editor');
      $('#htmlSourceType', $container).wSelect2({
        valueField: 'htmlSourceType',
        remoteSearch: false,
        data: [
          {
            id: 'html_from_editor',
            text: '编辑器自定义HTML'
          },
          { id: 'html_from_project_code_page', text: '项目页面文件' }
        ]
      });

      $('#htmlSourceType', $container)
        .on('change', function () {
          $('.html_source_type').hide();
          if ($(this).val()) {
            $('.' + $(this).val()).show();
          }
        })
        .trigger('change');

      $('#selectPage', $container).val(configuration.selectPage);
      $('#selectPage', $container).wSelect2({
        valueField: 'selectPage',
        remoteSearch: false,
        data: (function () {
          var selections = [];
          $.ajax({
            type: 'GET',
            url: '/pagerender/viewTemplateQuery',
            async: false,
            success: function (result) {
              for (var i = 0, len = result.length; i < len; i++) {
                selections.push({
                  id: result[i],
                  text: result[i]
                });
              }
            }
          });

          return selections;
        })()
      });

      tinymce.remove('textarea#wHtml_content');
      tinymce.init({
        selector: 'textarea#wHtml_content', // change this value according to your HTML
        language: 'zh_CN',
        base_url: '/static/js/tinymce',
        height: 300,
        menubar: false,
        plugins: [
          'advlist autolink lists link image  anchor',
          'searchreplace visualblocks',
          'insertdatetime media table paste code fullscreen table'
        ],
        toolbar:
          ' code fullscreen | undo redo | ' +
          ' bold italic backcolor fontselect fontsizeselect forecolor | alignleft aligncenter ' +
          ' alignright alignjustify | bullist numlist outdent indent | link image table  '
      });
      //文本编辑器上的弹出框表单元素无法输入问题
      $.fn.modal.Constructor.prototype.enforceFocus = function () {};
      tinymce.activeEditor.setContent(content);
    };

    configurer.prototype.onOk = function ($container) {
      this.component.options.configuration = this.collectConfiguration($container);
    };
    // 收集配置信息
    configurer.prototype.collectConfiguration = function ($container) {
      var configuration = {};
      // 基本信息
      this.collectBaseInfo(configuration, $container);
      // 内容信息
      this.collectContentInfo(configuration, $container);
      return $.extend({}, configuration);
    };
    configurer.prototype.collectBaseInfo = function (configuration, $container) {
      var $form = $('#widget_html_tabs_base_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      $.extend(configuration, opt);
    };
    configurer.prototype.collectContentInfo = function (configuration, $container) {
      var $form = $('#widget_html_tabs_content_info', $container);
      var opt = designCommons.collectConfigurerData($form, collectClass);
      opt.htmlSourceType = $('#htmlSourceType', $container).val();
      opt.content = '&nbsp;';
      if (opt.htmlSourceType === 'html_from_project_code_page') {
        opt.selectPage = $('#selectPage', $container).val();
      } else if (opt.htmlSourceType === 'html_from_editor') {
        opt.content = tinymce.activeEditor.getContent() || '';
      }

      $.extend(configuration, opt);
    };
    return configurer;
  };
  component.prototype.toHtml = function () {
    var _self = this;
    var options = _self.options;
    var configuration = options.configuration || {};
    var content = configuration.content || options.content || '';
    var id = _self.getId();
    var html = '<div id="' + id + '" class="ui-wHtml">' + content + '</div>';
    return html;
  };
  component.prototype.getDefinitionJson = function () {
    var _self = this;
    var options = _self.options;
    var id = _self.getId();
    options.id = id;
    return options;
  };
  return component;
});
