/**
 * 关联选择控件,用户通过选择搜索到的结果，然后将结果中的列设置到表单中配置的列去
 */
(function ($) {
  var columnProperty = {
    //控件字段属性
    applyTo: null, //应用于
    columnName: null, //字段定义  fieldname
    displayName: null, //描述名称  descname
    dbDataType: '', //字段类型  datatype type
    indexed: null, //是否索引
    showed: null, //是否界面表格显示
    sorted: null, //是否排序
    sysType: null, //系统定义类型，包括三种（0：系统默认，1：管理员常量定义，2：表单添加后自定义）
    length: null, //长度
    showType: '1', //显示类型 1,2,3,4 datashow
    defaultValue: null, //默认值
    valueCreateMethod: '1', //默认值创建方式 1用户输入
    onlyreadUrl: null //只读状态下设置跳转的url
  };

  //控件公共属性
  var commonProperty = {
    inputMode: null, //输入样式 控件类型 inputDataType
    fieldCheckRules: null,
    fontSize: null, //字段的大小
    fontColor: null, //字段的颜色
    ctlWidth: null, //宽度
    ctlHight: null, //高度
    textAlign: null //对齐方式
  };

  /*
   * DIALOG CLASS DEFINITION ======================
   */
  var Dialog = function (element, options) {
    this.$element = $(element);
    this.options = $.extend({}, $.fn['wdialog'].defaults, options, this.$element.data());
  };

  Dialog.prototype = {
    constructor: Dialog,
    initSelf: function () {
      var elment = this.$element;
      var options = this.options;
      elment.attr('id', elment.attr('name'));
      elment.attr('relationdatatwosql', options.relationdatatwosql); //关联数据类型
      elment.attr('relationdatatext', options.relationdatatext); //关联数据源显示值
      elment.attr('relationdatavalue', options.relationdatavalue); //关联数据源隐藏值
      elment.attr('relationdatasql', options.relationdatasql); //关联数据约束条件
      elment.attr('relationdatashowmethod', options.relationdatashowmethod); //关联数据的展示方式
      elment.attr('relationdatashowtype', options.relationdatashowtype); //关联数据显示方式
      elment.attr('relationDataDefiantion', options.relationDataDefiantion); //关联数据显示方式
      elment.addClass('input-search'); //css in wellnewoa.css

      //根据show类型展示
      this.displayByShowType();
      //设置文本的css样式
      this.setTextInputCss();
      //设置默认值
      this.setDefaultValue(this.options.columnProperty.defaultValue);
      var issubformdialog = false;
      if (this.getCtlName().indexOf('____' + this.options.columnProperty.columnName) > 0) {
        issubformdialog = true;
      } else if (this.getCtlName().indexOf('___' + this.options.columnProperty.columnName) > 0) {
        issubformdialog = false;
      }

      var _relationDataDefiantion = this.options.relationDataDefiantion;
      var _relationDataValueTwo = this.options.relationDataValueTwo;
      var _relationDataTwoSql = this.options.relationDataTwoSql;
      var tempArray = _relationDataDefiantion.split('|');
      var _this = this;
      this.$element.unbind('click');
      this.$element.click(function () {
        var paramsId = $(this).attr('id');
        //获得要查询的字段
        var str = '';
        for (var j = 0; j < tempArray.length; j++) {
          if (tempArray[j] != '') {
            var tempObj = JSON.parse(tempArray[j]);
            str += ',' + tempObj.sqlField;
          }
        }
        strArray = tempArray;
        var path = '';
        if (_relationDataValueTwo != undefined) {
          path =
            '/basicdata/dyview/view_show?viewUuid=' + _relationDataValueTwo + '&currentPage=1&openBy=dytable&relationDataDefiantion=' + str;
        }
        var parmArray = new Array();
        var relationDataTwoSql2 = _relationDataTwoSql;
        if (relationDataTwoSql2 != '' && relationDataTwoSql2 != null && relationDataTwoSql2 != undefined) {
          while (relationDataTwoSql2.indexOf('${') > -1) {
            var s1 = relationDataTwoSql2.match('\\${.*?\\}') + '';
            parmArray.push(s1.replace('${', '').replace('}', ''));
            relationDataTwoSql2 = relationDataTwoSql2.replace(s1, '');
          }
        }
        if (_relationDataTwoSql != undefined && _relationDataTwoSql != '') {
          path += '&' + _relationDataTwoSql;
        }

        var title = '选择' + _this.options.columnProperty.displayName;
        if (path.indexOf('${') > -1) {
          var json = new Object();
          json.content = '没有相应条件的数据';
          json.title = title;
          json.height = 600;
          json.width = 800;
          showdialog(json);
        } else {
          $.ajax({
            async: false,
            cache: false,
            url: ctx + path,
            success: function (data) {
              var json = new Object();
              json.content = "<div class='dnrw' style='width:99%;'>" + data + '</div>';
              json.title = title;
              json.height = 600;
              json.width = 800;
              showDialog(json);

              $('.dataTr').unbind('dblclick');
              $('.dataTr').live('dblclick', function () {
                var paramsObj = new Object();
                //alert($(this).attr("jsonstr"));
                var jsonstr = $(this).attr('jsonstr');
                var valStr = $(this).attr('jsonstr').replace('{', '').replace('}', '').split(',');
                for (var ai1 = 0; ai1 < valStr.length; ai1++) {
                  for (var j = 0; j < tempArray.length; j++) {
                    if (tempArray[j].length == 0) {
                      alert('请配置弹出框的映射字段');
                      break;
                    }
                    var tempObj = JSON.parse(tempArray[j]);
                    if (tempObj.sqlField.replace('_', '').toUpperCase() == valStr[ai1].split('=')[0].toUpperCase().replace(' ', '')) {
                      var control = {};
                      if (_this.getPos() == dyControlPos.subForm) {
                        var datauuid = _this.getDataUuid();
                        if (issubformdialog) {
                          control = $.ControlManager.getControl(datauuid + '____' + tempObj.formField);
                          paramsObj[datauuid + '____' + tempObj.formField] = valStr[ai1].split('=')[1];
                        } else {
                          control = $.ControlManager.getControl(datauuid + '___' + tempObj.formField);
                          paramsObj[datauuid + '___' + tempObj.formField] = valStr[ai1].split('=')[1];
                        }
                        control.setValue(valStr[ai1].split('=')[1]);
                      } else {
                        control = $.ControlManager.getControl(tempObj.formField);

                        if (typeof control == 'undefined' || typeof control.setValue == 'undefined') {
                          continue;
                        }

                        control.setValue(valStr[ai1].split('=')[1]);

                        paramsObj[tempObj.formField] = valStr[ai1].split('=')[1];
                      }
                    }
                  }
                }

                $('#dialogModule').dialog('close');
                if (_this.options.afterDialogSelect) {
                  _this.options.afterDialogSelect.call(this, paramsId, paramsObj, jsonstr);
                }
              });
            }
          });
        }
      });
      this.addMustMark();
    },
    bind: function (event, callback) {
      this.options[event] = callback;
    }
  };

  /*
   * 下拉Div
   */
  var DropDiv = function (element, options) {
    this.$element = $(element);
    this.options = $.extend({}, $.fn['wdialog'].defaults, options, this.$element.data());
  };

  DropDiv.prototype = {
    constructor: DropDiv,
    initSelf: function () {
      var elment = this.$element;
      var options = this.options;
      elment.attr('id', elment.attr('name'));
      elment.attr('relationdatatwosql', options.relationdatatwosql); //关联数据类型
      elment.attr('relationdatatext', options.relationdatatext); //关联数据源显示值
      elment.attr('relationdatavalue', options.relationdatavalue); //关联数据源隐藏值
      elment.attr('relationdatasql', options.relationdatasql); //关联数据约束条件
      elment.attr('relationdatashowmethod', options.relationdatashowmethod); //关联数据的展示方式
      elment.attr('relationdatashowtype', options.relationdatashowtype); //关联数据显示方式
      elment.attr('relationDataDefiantion', options.relationDataDefiantion); //关联数据显示方式
      elment.addClass('input-search'); //css in wellnewoa.css

      //根据show类型展示
      this.displayByShowType();
      //设置文本的css样式
      this.setTextInputCss();
      //设置默认值
      this.setDefaultValue(this.options.columnProperty.defaultValue);
      var issubformdialog = this.isInSubform();

      var _relationDataDefiantion = this.options.relationDataDefiantion;
      var _relationDataValueTwo = this.options.relationDataValueTwo;
      var _relationDataTwoSql = this.options.relationDataTwoSql;
      var tempArray = _relationDataDefiantion.split('|');
      var _this = this;
      this.$element.unbind('change');
      this.$element.change(function () {
        var paramsId = $(this).attr('id');
        //获得要查询的字段
        var str = '';
        for (var j = 0; j < tempArray.length; j++) {
          if (tempArray[j] != '') {
            var tempObj = JSON.parse(tempArray[j]);
            str += ',' + tempObj.sqlField;
          }
        }
        strArray = tempArray;
        var path = '';
        if (_relationDataValueTwo != undefined) {
          path =
            '/basicdata/dyview/view_show?viewUuid=' + _relationDataValueTwo + '&currentPage=1&openBy=dytable&relationDataDefiantion=' + str;
        }
        var parmArray = new Array();
        var relationDataTwoSql2 = _relationDataTwoSql;
        if (relationDataTwoSql2 != '' && relationDataTwoSql2 != null && relationDataTwoSql2 != undefined) {
          while (relationDataTwoSql2.indexOf('${') > -1) {
            var s1 = relationDataTwoSql2.match('\\${.*?\\}') + '';
            parmArray.push(s1.replace('${', '').replace('}', ''));
            relationDataTwoSql2 = relationDataTwoSql2.replace(s1, '');
          }
        }
        if (_relationDataTwoSql != undefined && _relationDataTwoSql != '') {
          path += '&' + _relationDataTwoSql;
        }

        var title = '选择' + _this.options.columnProperty.displayName;
        if (path.indexOf('${') > -1) {
          var json = new Object();
          json.content = '没有相应条件的数据';
          json.title = title;
          json.height = 600;
          json.width = 800;
          // showdialog(json);
        } else {
          $.ajax({
            async: false,
            cache: false,
            url: ctx + path,
            success: function (data) {
              var json = new Object();
              json.content = "<div class='dnrw' style='width:99%;'>" + data + '</div>';
              json.title = title;
              json.height = 600;
              json.width = 800;
              //showDialog(json);
              console.log(data);
            }
          });
        }
      });
      this.addMustMark();
    },
    bind: function (event, callback) {
      this.options[event] = callback;
    }
  };

  /*
   * 关联控件 PLUGIN DEFINITION =========================
   */
  $.fn.wrelation = function (option) {
    var method = false;
    var args = null;
    if (arguments.length == 2) {
      method = true;
      args = arguments[1];
    }

    if (typeof option == 'string') {
      if (option === 'getObject') {
        //通过getObject来获取实例
        var $this = $(this);
        data = $this.data('wrelation');
        if (data) {
          return data; //返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    return this.each(function () {
      var $this = $(this),
        data = $this.data('wrelation'),
        options = typeof option == 'object' && option;
      if (!data) {
        if (opition.relationDataShowMethod && opition.relationDataShowMethod == relationShowType.dropdiv) {
          data = new DropDiv(this, options);
        } else {
          //默认为弹出框
          data = new Dialog(this, options);
        }
        var datacopy = {};
        var data1 = $.extend(datacopy, data);
        var extenddata = $.extend(data, $.wControlInterface);
        var data2 = $.extend(extenddata, data1);
        var data3 = $.extend(data2, $.wTextCommonMethod);
        data3.init();
        $this.data('wrelation', data3);
      }
      if (typeof option == 'string') {
        if (method == true && args != null) {
          return data[option](args);
        } else {
          return data[option]();
        }
      }
    });
  };

  $.fn.wrelation.Constructor = Dialog;

  $.fn.wrelation.defaults = {
    columnProperty: columnProperty, //字段属性
    commonProperty: commonProperty, //公共属性
    readOnly: false,
    isShowAsLabel: false,
    disabled: false,
    isHide: false, //是否隐藏
    relationDataTextTwo: '',
    relationDataValueTwo: '',
    relationDataTwoSql: '',
    relationDataDefiantion: '',
    relationDataShowMethod: '',
    relationDataShowType: '',
    pos: '',
    afterDialogSelect: function () {}
  };
})(jQuery);
