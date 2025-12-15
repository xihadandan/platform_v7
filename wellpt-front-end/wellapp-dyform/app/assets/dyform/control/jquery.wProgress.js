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
    showType: '1', // 显示类型 1,2,3,4 datashow
    defaultValue: null, // 默认值
    valueCreateMethod: '1', // 默认值创建方式 1用户输入
    onlyreadUrl: null // 只读状态下设置跳转的url
  };

  // 控件公共属性
  var commonProperty = {
    inputMode: null, // 输入样式 控件类型 inputDataType
    fieldCheckRules: null,
    fontSize: null, // 字段的大小
    fontColor: null, // 字段的颜色
    ctlWidth: null, // 宽度
    ctlHight: null, // 高度
    textAlign: null // 对齐方式
  };

  var Progress = function ($placeHolder /* 占位符 */, options) {
    this.options = $.extend({}, $.fn['wprogress'].defaults, options);
    this.value = '';
    this.$editableElem = null;
    this.$labelElem = null;
    this.$placeHolder = $placeHolder;
  };

  Progress.prototype = {
    constructor: Progress
  };

  $.WProgress = {
    setDisplayAsCtl: function () {
      // 显示为可编辑框
      var self = this;
      var options = self.options;
      options.isShowAsLabel = false;
      if (self.$editableElem == null) {
        self.createEditableElem();
        self.initInputEvents();
      }
      self.$editableElem.show();
      self.$editableElem.removeAttr('disabled');
      self.$placeHolder && self.$placeHolder.hide();
    },
    setDisplayAsLabel: function () {
      var self = this;
      self.setValue2LabelElem();
      $.wControlInterface.setDisplayAsLabel.apply(self, arguments);
      self.$editableElem && self.$editableElem.attr('disabled', 'disabled');
    },
    setValue2LabelElem: function () {
      var self = this;
      self.value = $.trim(self.value).length ? self.value : self.options.defaultProgress;
      var progressUnit = self.options.progressUnit ? self.options.progressUnit : '%';
      self.$placeHolder && self.$placeHolder.html($.trim(self.value).length ? self.value + progressUnit : '0' + progressUnit);
    },
    createEditableElem: function () {
      if (this.$editableElem != null) {
        // 创建可编辑框
        return;
      }

      var ctlName = this.getCtlName();
      var editableElem = document.createElement('span');
      editableElem.setAttribute('class', this.editableClass);
      editableElem.setAttribute('name', ctlName);

      this.$placeHolder.after($(editableElem));
      this.$editableElem = this.$placeHolder.next('.' + this.editableClass);
      this.drawOptionElem(this.options);
    },

    drawOptionElem: function (options) {
      var self = this;
      var colors = options.progressColor || '#488CEE';
      var pStyle = options.columnProperty.showType == '1' ? '' : 'position:relative;';

      var pHeight = parseInt(options.progressHeight) <= 6 ? 6 : parseInt(options.progressHeight);
      var btnHeight = pHeight + 8;
      var rangeBar = 'border-radius:' + pHeight / 2 + 'px;height:' + pHeight + 'px;';
      var rangeBtn = 'height:' + btnHeight + 'px;width:' + btnHeight + 'px;';

      var html =
        '<div class="progress-wrap">' +
        '<span class="progress-min">' +
        options.progressMin +
        options.progressUnit +
        '</span>' +
        '<div class="progress-content" id="progress-bar" style="' +
        rangeBar +
        '">' +
        '<div id="progress-range" style="background-color:' +
        colors +
        ';' +
        pStyle +
        rangeBar +
        '">';
      if (options.columnProperty.showType == '2') {
        html +=
          '<span class="progress-value" id="progress-title" data-value="' +
          options.defaultProgress +
          '" style="color:' +
          colors +
          ';right:-20px;">' +
          options.defaultProgress +
          '</span>';
      }
      html += '</div>';
      if (options.columnProperty.showType == '1') {
        html +=
          '<div id="progress-btn" class="progress-btn" style="border-color:' +
          colors +
          ';' +
          rangeBtn +
          '">' +
          '<span class="progress-value" id="progress-title" data-value="' +
          options.defaultProgress +
          '" style="color:' +
          colors +
          ';left:0">' +
          options.defaultProgress +
          '</span>' +
          '</div>';
      }
      // else{
      // 	 html += '<div class="progress-edit" id="progress-edit" style="border-color:'+colors+';color:'+colors+';">'+
      // 			 '<span id="progress-title" class="progress-title" contenteditable="true"  data-value="' + options.defaultProgress + '"></span>'+
      // 		 	 '<span>'+ options.progressUnit + '</span>' +
      // 			 '</div>'
      // }
      html += '</div>' + ' <span  class="progress-max">' + options.progressMax + options.progressUnit + '</span>' + '</div>';
      this.$editableElem.html(html);
      this.setValue($.trim($(this.$editableElem[0]).find('#progress-title').data('value')));
      $(window).resize(function () {
        self.elementEvent();
      });
    },

    elementEvent: function () {
      var self = this;
      var valite = false;
      var options = self.options;
      var progressBar = $('#progress-bar', this.$editableElem[0]);
      var progressRange = $('#progress-range', this.$editableElem[0]);
      var progressTitle = $('#progress-title', this.$editableElem[0]);
      var value = parseInt(progressTitle.data('value'));
      var maxValue = parseInt(options.progressMax);
      var minValue = parseInt(options.progressMin);
      var progressUnit = options.progressUnit;
      var radius = options.progressHeight / 2;

      if (value - maxValue > 0) {
        alert('给定当前值大于了最大值');
        return;
      }
      if (0 === progressBar.width()) {
        // 元素不可见 debugger;
        progressBar.css('width', '100px');
      }
      var curr = Math.round(progressBar.width() * ((value - minValue) / (maxValue - minValue)));
      progressRange.css('width', curr + 'px');
      progressTitle.text(value + progressUnit);
      var editProgress = options.columnProperty.showType == '1';
      if (editProgress) {
        var currentValue = curr;
        var progressBtn = $(this.$editableElem[0]).find('#progress-btn');
        progressBtn.css('margin-left', curr - radius + 'px');
        var changeX = progressBtn.offset().left - curr;
        progressBtn.mousedown(function () {
          if (options.columnProperty.showType == '1') {
            valite = true;
          }
        });
        $(document.body).mousemove(function (event) {
          if (valite == false) return;
          currentValue = event.clientX - changeX;

          progressBtn.css('margin-left', currentValue - radius + 'px');
          progressRange.css('width', currentValue + 'px');

          if (currentValue >= progressBar.width()) {
            progressBtn.css('margin-left', progressBar.width() - 5 + 'px');
            progressRange.css('width', progressBar.width() + 'px');
            value = maxValue;
          } else if (currentValue <= 0) {
            progressBtn.css('margin-left', '0px');
            progressRange.css('width', '0px');
            value = minValue;
          } else {
            value = Math.round((currentValue / progressBar.width()) * (maxValue - minValue)) + minValue;
          }
          progressTitle.text(value + progressUnit).data('value', value);
        });
        $(document.body).mouseup(function () {
          if (valite == false) return;
          self.setValue(progressTitle.data('value').toString());
          valite = false;
        });
      }
      // else{
      // 	var progressEdit = $("#progress-edit",this.$editableElem[0]);
      //    progressEdit.css("margin-left", curr + "px");
      //    var oldVal = progressTitle.text()
      //    progressTitle.live('keyup',function(event){
      //        	event.stopPropagation()
      //        	event.preventDefault()
      //        if(event.keyCode  === 13){
      // 			console.log(1111)
      //            progressTitle.blur()
      // 		}
      //    });
      //    progressTitle.live('blur',function(event) {
      //    	console.log(111)
      //        event.stopPropagation()
      //        event.preventDefault()
      //    	var reg = /^\d+(\.\d+)?$/;
      //    	if(!reg.test($(this).text()) || parseInt($(this).text()) - minValue < 0  || parseInt($(this).text()) - maxValue > 0){
      //    		alert("输入值必须是数字，并且在最大值和最小值之间");
      // 			$(this).text(oldVal);
      // 			return false;
      // 		}else{
      //            progressTitle.data("value",$(this).text());
      //            curr = Math.round(progressBar.width() * ((parseInt($(this).text())-minValue) /( maxValue-minValue)));
      //            progressRange.css("width", curr + "px");
      //            progressTitle.text($(this).text());
      //            progressEdit.css("margin-left", curr + "px");
      //            self.setValue($(this).text().toString());
      //            oldVal = progressTitle.text()
      // 		}
      //    })
      // }
    },

    isValueMap: function () {
      return true;
    },
    setValue2EditableElem: function () {
      var self = this;
      if (self.$editableElem == null) {
        return;
      }
      var valueObj = self.value;
      if ($.isArray(valueObj)) {
      } else if (typeof valueObj === 'string') {
        valueObj = valueObj.split(self.getSeparator());
      } else {
        valueObj = '';
      }
      if (valueObj.length > 0) {
        var unit = self.options.progressUnit;
        $(this.$editableElem[0])
          .find('#progress-title')
          .text(valueObj[0] + unit)
          .data('value', valueObj[0]);
        this.elementEvent();
      }
    },
    get$InputElem: function () {
      var self = this;
      if (self.$editableElem == null) {
        return $([]); // 还没生成输入框时，先返回一个jquery对象
      } else {
        return $("span[name='" + self.getCtlName() + "']", self.$editableElem[0]);
      }
    }
  };

  $.fn.wprogress = function (option) {
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
        data = $this.data('wprogress');
        if (data) {
          return data; // 返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    var $this = $(this),
      data = $this.data('wprogress'),
      options = typeof option == 'object' && option;
    if (!data) {
      data = new Progress($(this), options);
      $.extend(data, $.wControlInterface);
      $.extend(data, $.WProgress);
      data.init();
      $this.data('wprogress', data);
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

  $.fn.wprogress.Constructor = Progress;

  $.fn.wprogress.defaults = {
    columnProperty: columnProperty, // 字段属性
    commonProperty: commonProperty, // 公共属性

    isShowAsLabel: false,
    // 私有属性
    isHide: false, // 是否隐藏
    checked: false,
    disabled: false,
    ctrlField: null,
    defaultProgress: '0', //默认值
    progressMin: '', //最小值
    progressMax: '', //最大值
    progressColor: '', //自定义颜色
    progressUnit: '',
    // editProgress: '0',
    progressHeight: '0'
  };
})(jQuery);
