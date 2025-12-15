define(['ui_component', 'tinymce'], function (ui_component, tinymce) {
  var component = $.ui.component.BaseComponent();
  component.prototype.create = function () {
    $(this.element).css('overflow', 'auto');
  };
  component.prototype.usePropertyConfigurer = function () {
    return true;
  };
  component.prototype.getPropertyConfigurer = function () {
    var configurer = $.ui.component.BaseComponentConfigurer();
    configurer.prototype.getTemplate = function () {
      return "<textarea id='wFooter_content' name='wFooter_content' cols='50' rows='10' />";
    };
    configurer.prototype.onLoad = function ($container, options) {
      tinymce.remove('textarea#wFooter_content');
      tinymce.init({
        selector: 'textarea#wFooter_content', // change this value according to your HTML
        language: 'zh_CN',
        base_url: staticPrefix + '/js/tinymce',
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
          ' alignright alignjustify | bullist numlist outdent indent | link image table ',
        init_instance_callback: function (editor) {
          tinymce.activeEditor.setContent(options.content);
        }
      });
      //文本编辑器上的弹出框表单元素无法输入问题
      $.fn.modal.Constructor.prototype.enforceFocus = function () {};
    };
    configurer.prototype.onOk = function () {
      var _self = this.component;
      var content = tinymce.activeEditor.getContent() || '';
      _self.setOption('content', content);
    };
    return configurer;
  };
  component.prototype.toHtml = function () {
    var options = this.options;
    var id = this.getId();
    options.content = options.content || '';
    var html = '<div id="' + id + '" class="ui-wFooter">' + options.content + '</div>';
    return html;
  };
  component.prototype.getDefinitionJson = function () {
    var options = this.options;
    var id = this.getId();
    options.id = id;
    return options;
  };
  return component;
});
