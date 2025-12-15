define(['mui', 'constant', 'commons', 'server', 'mui-DyformField', 'mui-DyformCommons', 'quill'], function (
  $,
  constant,
  commons,
  server,
  DyformField,
  dyformCommons,
  Quill
) {
  // 文本域
  var wRichEditor = function ($placeHolder, options) {
    DyformField.apply(this, arguments);
  };
  function htmlDecode(html) {
    var elem = document.createElement('span');
    elem.innerHTML = html;
    return elem.textContent || elem.innerText;
  }
  var addClass = dyformCommons.addClass;
  var removeClass = dyformCommons.removeClass;
  var placeHolderTplId_1 = 'mui-DyformField-placeHolder-richEditor';
  var placeHolderTplId_2 = 'mui-DyformField-placeHolder-richEditor-2';
  $('head').append('<link href="' + ctx + '/static/js/quill/quill.snow.css" rel="stylesheet">');
  commons.inherit(wRichEditor, DyformField, {
    init: function () {
      var self = this;
      // html解码
      // self.value = htmlDecode(self.value);
      self._superApply();
    },
    render: function () {
      var self = this;
      // var displayStyle = parseInt( self.options.fieldDefinition.displayStyle );
      // var placeHolderTplId = displayStyle == 2 ? placeHolderTplId_2 : placeHolderTplId_1;
      self._superApply([placeHolderTplId_2]);
      // $(self.$editableElem[0].parentNode.parentNode).scroll();
    },
    // 渲染编辑元素
    renderEditableElem: function ($editableElem) {
      var self = this;
      if (self.$editableElem) {
        var richEditor = self.richEditor;
        if ($.isFunction(Quill) && null == richEditor) {
          richEditor = self.richEditor = new Quill(self.$editableElem[0], {
            placeholder: '请输入',
            theme: 'snow'
          });
          self.$qlToolbar = $('.ql-toolbar', self.$element[0]);
          removeClass(self.$qlToolbar, 'mui-hidden');
          self.$qlEditor = $('.ql-editor', self.$element[0]);
          self.$qlEditor[0].innerHTML = self.getValue(); //
        } else {
          self.$editableElem[0].innerHTML = self.getValue(); //
        }
        // self.$editableElem[0].value = self.getValue();
      }
    },
    // 渲染文本元素
    renderLabelElem: function ($labelElem) {
      var self = this;
      if (self.$labelElem) {
        self.$labelElem[0].innerHTML = self.getValue();
        $('table', self.$labelElem[0]).each(function () {
          var self = this;
          if (self.width > $.screen.availWidth) {
            self.style.width = '100%';
            self.setAttribute('width', '100%');
          }
        });
        $.ui.resizeImage(self.$labelElem[0]);
      }
    },
    //收集数据
    collectValue: function (event) {
      var self = this;
      return self.$qlEditor ? self.$qlEditor[0].innerHTML : self.$editableElem[0].innerHTML;
    },
    setDisplayAsLabel: function () {
      var self = this;
      self._superApply(arguments);
      if (self.$qlToolbar && self.$qlToolbar.length) {
        addClass(self.$qlToolbar, 'mui-hidden');
      }
    },
    setEditable: function () {
      var self = this;
      self._superApply(arguments);
      if (self.$qlToolbar && self.$qlToolbar.length) {
        removeClass(self.$qlToolbar, 'mui-hidden');
      }
    }
  });
  return wRichEditor;
});
