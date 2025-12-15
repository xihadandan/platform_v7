(function ($) {
  /*
   * VIEWDISPLAY CLASS DEFINITION ======================
   */
  var ViewDisplay = function ($placeHolder, options) {
    this.init('wviewDisplay', $placeHolder, options);
  };

  ViewDisplay.prototype = {
    constructor: ViewDisplay,
    init: function (type, element, options) {
      this.type = type;
      this.$element = $(element);
      this.options = this.getOptions(options);
      // 参数初始化
      this.initparams(this.options);
    },
    // 默认参数初始化
    initparams: function (options) {
      // $.ControlUtil.setCommonCtrAttr(this.$element,this.options);
      this.$element.attr('relationdatatext', options.relationdatatext); // 关联数据源显示值
      this.$element.attr('relationdatavalue', options.relationdatavalue); // 关联数据源隐藏值
      this.$element.attr('relationdatasql', options.relationdatasql); // 关联数据约束条件

      var path = '/basicdata/view/view_show?viewUuid=' + this.options.relationDataValue + '&currentPage=1&openBy=dytable';
      var parmArray = new Array();
      this.options.relationdatasql = " & o.status = '1' ";
      var relationDataSql2 = this.options.relationdatasql;
      if (relationDataSql2 != undefined) {
        while (relationDataSql2.indexOf('${') > -1) {
          var s1 = relationDataSql2.match('\\${.*?\\}') + '';
          parmArray.push(s1.replace('${', '').replace('}', ''));
          relationDataSql2 = relationDataSql2.replace(s1, '');
        }
      }
      path += this.options.relationdatasql;
      /*
       * for(var jt=0;jt<parmArray.length;jt++){
       * if(eval('formData.'+parmArray[jt])!=undefined&&eval('formData.'+parmArray[jt])!=""&&eval('formData.'+parmArray[jt])!="undefined"){
       * path =
       * path.replace("${"+parmArray[jt]+"}",eval('formData.'+parmArray[jt])) ; } }
       */
      if (options.columnProperty.showType == dyshowType.hide) {
        $.ControlUtil.setVisible(this.$element, false);
      } else {
        var _element = this.$element;
        if (path.indexOf('${') > -1) {
          _element.after('<span>没有相应记录</span>');
          _element.hide();
        } else {
          $.ajax({
            async: false,
            url: ctx + path,
            success: function (data) {
              _element.after(data);
              _element.hide();
            }
          });
        }
      }
      // this.addMustMark();
    },

    setValue: function (value) {},

    // 设置hide属性
    setVisible: function (isVisible) {
      $.ControlUtil.setVisible(this.$element, isVisible);
      this.options.isHide = !isVisible;
    },

    isVisible: function () {
      return this.options.isHide;
    },

    getOptions: function (options) {
      options = $.extend({}, $.fn[this.type].defaults, options, this.$element.data());
      return options;
    },

    setDataUuid: function (datauuid) {
      this.options.columnProperty.dataUuid = datauuid;
    },

    getDataUuid: function () {
      return this.options.columnProperty.dataUuid;
    },

    getFormDefinition: function () {
      return this.options.columnProperty.formDefinition;
    },

    isValueMap: function () {
      return false;
    },

    validate: function () {
      return true;
    },

    getValue: function () {
      return '';
    },

    getAllOptions: function () {
      return this.options;
    },

    getRule: function () {
      return $.ControlUtil.getCheckRules(this.options);
    },

    getMessage: function () {
      return $.ControlUtil.getCheckMsg(this.options);
    },

    setPos: function (pos) {
      this.options.columnProperty.pos = pos;
    },

    getPos: function (pos) {
      return this.options.columnProperty.pos;
    },

    /**
     * 添加url超连接事件
     */
    addUrlClickEvent: function (urlClickEvent) {},

    // bind函数，桥接
    bind: function (eventname, event) {
      this.$element.bind(eventname, event);
      return this;
    },

    // unbind函数，桥接
    unbind: function (eventname) {
      this.$element.unbind(eventname);
      return this;
    },
    setDisplayAsLabel: function () {},
    getFieldName: function () {
      return this.options.columnProperty.columnName;
    }
    // 一些其他method ---------------------
  };

  /*
   * VIEWDISPLAY PLUGIN DEFINITION =========================
   */
  $.fn.wviewDisplay = function (option) {
    var method = false;
    var args = null;
    if (arguments.length == 2) {
      method = true;
      args = arguments[1];
    }

    if (typeof option == 'string') {
      if (option === 'getObject') {
        // 通过getObject来获取实例
        var $this = $(this),
          data = $this.data('wviewDisplay');
        if (data) {
          return data; // 返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    var $this = $(this),
      data = $this.data('wviewDisplay'),
      options = typeof option == 'object' && option;
    if (!data) {
      $this.data('wviewDisplay', (data = new ViewDisplay($(this), options)));
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

  $.fn.wviewDisplay.Constructor = ViewDisplay;

  $.fn.wviewDisplay.defaults = {
    isHide: false, // 是否隐藏

    columnProperty: {}, // 字段属性
    commonProperty: {}, // 公共属性
    relationDataText: '',
    relationDataValue: '',
    relationDataSql: ''
  };
})(jQuery);
