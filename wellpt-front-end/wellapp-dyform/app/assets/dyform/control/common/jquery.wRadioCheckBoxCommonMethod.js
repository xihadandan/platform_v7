(function ($) {
  $.wRadioCheckBoxCommonMethod = {
    nums: 0,
    // 显示为可编辑框
    setDisplayAsCtl: function (isLablElem) {
      var options = this.options;
      options.isShowAsLabel = false;
      if (this.$editableElem == null) {
        this.createEditableElem();
        this.initInputEvents();
      }

      this.$editableElem.show();
      this.hideLabelElem();
      this.setValue2EditableElem();
    },

    setDisplayAsLabel: function () {
      var self = this;
      var options = self.options;
      var readStyle = options.columnProperty.readStyle;
      if (readStyle === dyshowType.readonly) {
        // this.options.isShowAsLabel = true;
        return self.setReadOnly(true);
      }
      if (this.get$LabelElem().size() == 0) {
        // 创建标签元素
        // 添加url点击事件
        this.markPlaceHolderAsLabelElem();
      }

      this.get$LabelElem().show();

      options.isShowAsLabel = true;
      this.hideEditableElem && this.hideEditableElem();
      this.setValue2LabelElem && this.setValue2LabelElem();
      if (this.setCtlField) {
        this.setCtlField();
      }
      // add by wujx 20161014 begin
      if (!this.isInSubform()) {
        this.addUrlClickEvent(urlClickEvent);
      }
      // add by wujx 20161014 end
      // add by wujx 20161101 begin
      this.removeValidate();
      // add by wujx 20161101 end
      this.statusChange(1);
    },

    getValueMapInOptionSet: function (value) {
      if (typeof value == 'undefined' || value.length == 0) {
        return '';
      }
      var options = this.options;
      var value1 = null;
      if (value.indexOf(',') != -1) {
        // 值用,隔开
        value1 = value.split(',');
      } else {
        // 值用;隔开
        value1 = value.split(';');
      }
      var valueMap = {};
      for (var i = 0; i < value1.length; i++) {
        if (this.isSingleCheck()) {
          var selectobj = eval('(' + options.singleCheckContent + ')');
          var checked = false;
          for (attrbute in selectobj) {
            if (attrbute == value1[i]) {
              valueMap[attrbute] = selectobj[attrbute];
              checked = true;
              break;
            }
          }
          if (!checked) {
            var selectobj = eval('(' + options.singleUnCheckContent + ')');
            for (attrbute in selectobj) {
              if (attrbute == value1[i]) {
                valueMap[attrbute] = selectobj[attrbute];
                break;
              }
            }
          }
        } else {
          var optionSet = {};
          optionSet = this.getOptionSet(this.options);
          for (attrbute in optionSet) {
            if (attrbute == value1[i]) {
              valueMap[attrbute] = optionSet[attrbute];
            }
          }
        }
      }
      if (typeof valueMap == 'string') {
        return valueMap;
      } else {
        return JSON.cStringify(valueMap);
      }
    },

    getOptionSet: function (options) {
      var optionSet = {};
      var selectobj = options.optionSet;
      if (typeof selectobj == 'undefined' || selectobj == null || (typeof selectobj == 'string' && $.trim(selectobj).length == 0)) {
        console.error('a json parameter is null , used to initialize checkbox options ');
        return;
      }
      if (options.optionDataSource == dyDataSourceType.dataDictionary) {
        // 来自字典,这时optionSet为数组
        if (selectobj.length == 0) {
          return;
        } else {
          for (var j = 0; j < selectobj.length; j++) {
            var obj = selectobj[j];
            var value = obj.value || obj.code;
            obj.value = value;
          }
          optionSet = selectobj;
        }
      } else if (options.optionDataSource == dyDataSourceType.dataSource) {
        if ($.isArray(selectobj)) {
          var fieldDef = this.getFieldParams();
          for (var k = 0; k < selectobj.length; k++) {
            var obj = selectobj[k];
            var key = obj[fieldDef.dataSourceFieldName] || (obj.data && obj.data[fieldDef.dataSourceFieldName]);
            optionSet[key] = obj[fieldDef.dataSourceDisplayName] || (obj.data && obj.data[fieldDef.dataSourceDisplayName]);
          }
        } else {
          optionSet = selectobj;
        }
      } else {
        optionSet = selectobj;
      }

      if (typeof optionSet == 'object') {
        // 为了兼容IE8,先通过JSON.cStringify进行排序后，再转换成对象
        optionSet = eval('(' + JSON.cStringify(optionSet) + ')');
      } else if (typeof optionSet == 'string') {
        try {
          optionSet = eval('(' + optionSet + ')');
        } catch (e) {
          console.error(optionSet + ' -->not json format ');
          return;
        }
      }
      return optionSet;
    },
    /**
     * 隐藏选项
     *
     * @param values
     *            要隐藏的option对应value组成的数组
     */
    hideOptions: function (values) {
      var self = this;
      self.options.hiddenValues = $.unique($.merge(self.options.hiddenValues || [], values));
      self.reRender();
    },
    /**
     * 隐藏选项
     *
     * @param value
     *            要隐藏的option对应value
     */
    hideOption: function (value) {
      var values = [];
      values.push(value);
      this.hideOptions(values);
    },
    /**
     * 设置选项
     */
    setOptionSet: function (optionSet) {
      if (optionSet instanceof Array) {
        for (var key = 0; key < optionSet.length; key++) {
          var value = optionSet[key];
          if (typeof key != 'number' || !(value instanceof Object)) {
            console.error(
              this.getCtlName + ':非法的自定义选项(选项须为数组)--typeof key == ' + typeof key + ' ----typeof value == ' + typeof value
            );
            return false;
          }
        }
      }

      this.options.optionSet = optionSet;
      // this.options.optionDataSource = dyDataSourceType.custom; // 用户自定义
      return true;
    },
    /**
     * 重新渲染
     */
    reRender: function () {
      var inputType = this.getInputType();
      var opt = new Array();
      var optionSet = this.getOptionSet(this.options);
      if (optionSet == undefined) {
        optionSet = [];
      }
      opt = this.getOptionElemHtml(optionSet, inputType); // 生成各选项html
      this.drawOptionElem(opt); // 将选项渲染出来
      if (this.isShowAsLabel()) {
        this.setEditable(this.isEditable());
      }
      var options = this.options;
      var showType = options.columnProperty.showType;
      if (showType != '1') {
        return this.setReadOnly(true);
      }
    },

    getInputType: function () {
      var inputType = '';
      if (this.isCheckBoxCtl()) {
        inputType = 'checkbox';
      } else {
        inputType = 'radio';
      }
      return inputType;
    },

    getOptionElemHtml: function (optionSet, inputType, labelstyle, otherinputstyle) {
      var opt = new Array();
      var ctlName = this.getCtlName();
      var optionArray = new Array();
      //console.log( $.ComboBox.isValueNameFormater( optionSet ));
      //如果是map 格式，则转成 array  modify by zyguo 2017-03-15
      if (!this.isValueNameFormater(optionSet)) {
        for (var key in optionSet) {
          var item = { name: optionSet[key], value: key };
          optionArray.push(item);
        }
      } else {
        optionArray = optionSet;
      }
      var s = "<div class='input-box clearfix'>";

      if (this.options.selectDirection == '1') {
        // 是否是竖排
        var result = [];
        for (var i = 0; i < optionArray.length; i += 4) {
          result.push(optionArray.slice(i, i + 4));
        }
        for (var i = 0; i < result.length; i++) {
          s += "<div class='select-vertical'>";
          for (var j = 0; j < result[i].length; j++) {
            // prettier-ignore
            s += "<div><input type='" + inputType + "' name='" + ctlName + "' id='" + ctlName + '_' + result[i][j].value + "' value='" + result[i][j].value + "' />" +
                  "<label class='iconfont' for='" + ctlName + '_' + result[i][j].value + "'>" + result[i][j].name + '</label></div>';
          }
          s += '</div>';
        }
      } else {
        var textAlign = this.options.commonProperty['textAlign'];
        var btnClass = this.isBtnShape() ? 'btn-radio' : 'row';
        var labelClass = this.isBtnShape() ? '' : this.options.selectAlign == '0' ? ' col-md-3 col-sm-3 col-xs-4' : ' fix-margin';

        if (this.isLabelElem()) {
          btnClass = '';
          labelClass = 'inline-block';
        }

        if (textAlign === 'center') {
          btnClass += ' text-center';
        } else if (textAlign === 'left') {
          btnClass += ' text-left';
        } else if (textAlign === 'right') {
          btnClass += ' text-right';
        }

        s += "<div class='" + btnClass + "'>";
        for (var i = 0; i < optionArray.length; i++) {
          // prettier-ignore
          s += "<div class='" + labelClass + "'><input type='" + inputType + "' name='" + ctlName + "' id='" + ctlName + '_' + optionArray[i].value + "' value='" + optionArray[i].value + "' />" +
                  "<label class='iconfont' for='" + ctlName + '_' + optionArray[i].value + "'>" + optionArray[i].name + '</label></div>';
        }
        s += '</div>';
      }

      if (this.isSelectAll()) {
        // 是否全选
        s += '<div class="checkbox-all"><label class="iconfont selectAll">全选</label></div>';
      }
      s += '</div>';
      return s;
    },
    elementEvent: function () {
      var self = this;
      if (this.options.radioCancel == '1') {
        // radio: 取消选中 再次点击取消选中 先取消绑定事件，再次绑定
        $("input[type='radio']", this.$editableElem[0])
          .off('click')
          .on('click', function () {
            var $radio = $(this);
            if ($radio.data('waschecked') == true) {
              $radio.prop('checked', false);
              $radio.data('waschecked', false);
              $radio.parent().siblings().find("input[type='radio']").data('waschecked', false);
            } else {
              $radio.prop('checked', true);
              $radio.data('waschecked', true);
              $radio.parent().siblings().find("input[type='radio']").data('waschecked', false);
            }
          });
      }

      if (this.isSelectAll()) {
        // checkbox: 全选/取消全选
        $('.selectAll', self.$editableElem[0]).live('click', function () {
          if ($(this).attr('disabled') == 'disabled') {
            return false;
          }
          $(this).data('value') == 'all' ? $(this).data('value', '') : $(this).data('value', 'all');
          var $checkbox = $(self.$editableElem[0]).find('input');
          if ($(this).data('value') == 'all') {
            $(this).addClass('selected');
            for (var i = 0; i < $checkbox.length; i++) {
              $checkbox[i].checked = true;
            }
          } else {
            $(this).removeClass('selected');
            for (var i = 0; i < $checkbox.length; i++) {
              $checkbox[i].checked = false;
            }
          }
          self.collectValue();
        });
      }

      $("input[type='checkbox']", this.$editableElem[0]).live('click', function () {
        if ($(this).checked) {
          $(this).checked = false;
        } else {
          $(this).checked = true;
        }

        if (self.isSelectAll()) {
          var allData = $('.selectAll', self.$editableElem[0]).data('value');
          if (!$(this).checked && allData == 'all') {
            $('.selectAll', self.$editableElem[0]).data('value', '').removeClass('selected');
          }
        }
      });
    },
    getCheckedNum: function () {
      if (this.options.selectMode == '2' && this.options.checkboxMax != '') {
        // checkbox: 最大值
        var self = this;
        this.num = $("input[type='checkbox']:checked", this.$editableElem[0]).size();
        var checkbox = $("input[type='checkbox']", this.$editableElem[0]);
        var max = parseInt(this.options.checkboxMax);
        var showType = this.options.columnProperty.showType;

        if (max - checkbox.length < 0 && self.isSelectAll()) {
          $('.selectAll', self.$editableElem[0]).attr('disabled', 'disabled');
        }

        if (this.num - max >= 0) {
          //超出最大值禁选并且未设为readOnly
          checkbox.each(function (i) {
            if (checkbox[i].checked != true) {
              $(this).attr('disabled', 'disabled');
            } else {
              $(this).removeAttr('disabled');
            }
          });
        } else if (showType != dyshowType.readonly && showType != dyshowType.disabled) {
          checkbox.each(function (i) {
            $(this).removeAttr('disabled');
          });
        }
      }
    },
    drawOptionElem: function (opt) {
      // this.$editableElem.html(opt.join(''));
      this.$editableElem.html(opt);
      this.elementEvent();
      var _this = this;
      $('input[id^=other_input]', this.$editableElem[0]).hide();
      $('input[id^=other_input]', this.$editableElem[0]).change(function () {
        _this.collectValue();
      });
    },

    createEditableElem: function () {
      if (this.$editableElem != null) {
        // 创建可编辑框
        return;
      }
      var _this = this;
      var options = this.options;
      var inputType = this.getInputType();

      var ctlName = this.getCtlName();

      var editableElem = document.createElement('span');
      editableElem.setAttribute('class', this.editableClass);

      editableElem.setAttribute('name', ctlName);
      $(editableElem).css($.extend({ display: 'block' }, this.getTextInputCss()));
      if (this.isLabelElem()) {
        $(editableElem).addClass('label-elem');
      }
      this.$placeHolder.after($(editableElem));
      this.$editableElem = this.$placeHolder.next('.' + this.editableClass);

      var opt = new Array();
      var labelstyle = $.ControlUtil.getStyleHtmlByParams(
        options.commonProperty.textAlign,
        options.commonProperty.fontSize,
        options.commonProperty.fontColor,
        options.commonProperty.ctlWidth,
        options.commonProperty.ctlHight,
        ''
      );
      var otherinputstyle = $.ControlUtil.getStyleHtmlByParams(
        options.commonProperty.textAlign,
        options.commonProperty.fontSize,
        options.commonProperty.fontColor,
        '120',
        ''
      );
      // 单选模式
      var opt = '';
      if (this.isSingleCheck()) {
        var selectobj = eval('(' + options.singleCheckContent + ')');
        for (attrbute in selectobj) {
          // prettier-ignore
          opt += "<input type='" + inputType + "' name='" + ctlName + "' id='" + ctlName + '_' + attrbute + "' value='" + attrbute + "'/>" +
                  "<label class='iconfont' " + labelstyle + " for='" + ctlName + '_' + attrbute + "'>" + selectobj[attrbute] + '</label>';
        }
        this.setCtlField();
        _this.collectValue();
      }
      // 多选模式
      else {
        var optionSet = this.getOptionSet(this.options);
        if (optionSet == undefined) {
          optionSet = [];
        }
        opt = this.getOptionElemHtml(optionSet, inputType, labelstyle, otherinputstyle); // 生成各选项html
      }

      this.drawOptionElem(opt); // 将选项渲染出来

      this.get$InputElem().live('click', function () {
        if ($(this).is('.other_input')) {
          // 其他
          var $otherinput = $(this).next().next();
          if ($(this).prop('checked') == true) {
            $otherinput.show();
          } else {
            $otherinput.hide();
          }
        } else {
          if (_this.isRadioCtl()) {
            $('input[id^=other_input]', _this.$editableElem[0]).hide();
          }
        }
        _this.collectValue();
        if (_this.isSingleCheck()) {
          _this.setCtlField($(this));
        }
      });

      if (options.optionDataAutoSet) {
        var $dyform = DyformFacade.get$dyform();
        $dyform.bind2Dyform2('beforeSetData', function () {
          _this.getDynamicOptionSet();
        });
      }

      if (options.lazyLoading) {
        _this.lazySetDictOptions(function (optionSet) {
          _this.setOptionSet(optionSet);
          _this.reRender();
          _this.setValue(_this.value);
        }, true);
      }
    },

    //获取动态备选项
    getDynamicOptionSet: function () {
      var options = this.options;
      if (options.optionDataSource == '1') {
        this.reloadDataOptions(options);
      } else if (options.optionDataSource == '2') {
        this.reloadDictOptions(options);
      } else if (options.optionDataSource == '4') {
        this.initDataStore();
        this.reloadSourceOptions(options);
      }
    },
    // 重新加载常量的备选项
    reloadDataOptions: function (options) {
      var self = this;
      var relateField = DyformFacade.get$dyform().getControl(options.relateField);
      if (relateField != undefined) {
        relateField.proxiedAfterInvoke('setValue', function (value) {
          var optionSet = [];
          if ($.trim(value).length > 0) {
            optionSet = self.getConstantOptionSet(options.dataOptsList, value, []);
          }
          self.setOptionSet(self.arrayRepeat(optionSet));
          self.reRender();
          self.setValue(self.getValue());
        });
      }
    },

    // 重新加载数据字典的备选项
    reloadDictOptions: function (options) {
      var self = this;
      var relateField = DyformFacade.get$dyform().getControl(options.relateField);
      if (relateField != undefined) {
        relateField.proxiedAfterInvoke('setValue', function (value) {
          if (value == null) {
            value = '';
          } else if ($.isArray(value)) {
            value = value.join(';');
          }
          var dictType = '';
          var optionSet = [];
          var data = [];
          if (relateField.options.dictCode && relateField.options.dictCode != '') {
            dictType = relateField.options.dictCode.split(':')[0]; // 类型为关联字段的字典
          }
          if (value != '' && value.split(';').length == 1) {
            data = self.getNewOptionSet(dictType, optionSet, value); //  获取备选项的值
          }

          self.setOptionSet(data);
          self.reRender();
          self.setValue(self.getValue());
        });
      }
    },

    // 重新加载数据仓库的备选项
    reloadSourceOptions: function (options) {
      var self = this;
      var relateFieldList = options.relateField.split(';');
      var dataList = {};
      for (var i = 0; i < relateFieldList.length; i++) {
        (function (i) {
          var relateField = DyformFacade.get$dyform().getControl(relateFieldList[i]);
          if (relateField != undefined) {
            dataList[relateFieldList[i]] = relateField.getValue();
            relateField.proxiedAfterInvoke('setValue', function (value) {
              value = value || '';
              dataList[relateFieldList[i]] = value;
              self.Datastore.clearParams();
              for (var k in dataList) {
                self.Datastore.addParam(k, dataList[k]);
              }
              self.Datastore.load();
            });
          }
        })(i);
      }
    },

    // 重新设置是否动态设置备选项和关联字段,二开使用
    reSetRelatedOptions: function (options) {
      if (options == undefined) {
        return;
      }
      var ctlOptions = this.options;
      ctlOptions.optionDataAutoSet = options.optionDataAutoSet == undefined ? options.optionDataAutoSet : ctlOptions.optionDataAutoSet;
      if (options.optionDataAutoSet) {
        ctlOptions.relateField = options.relateField;
        if (ctlOptions.optionDataSource == '1') {
          ctlOptions.dataOptsList = options.dataOptsList;
        }
        this.getDynamicOptionSet();
      }
    },

    initDataStore: function () {
      var _self = this;
      var fieldDef = _self.getFieldParams();
      var DataStore = require('dataStoreBase');
      _self.Datastore = new DataStore({
        dataStoreId: fieldDef.dataSourceId,
        receiver: _self,
        pageSize: 1000,
        async: false,
        onDataChange: function (data, totalCount, params, getDefinitionJson) {
          _self.setOptionSet(data);
          _self.reRender();
          _self.setValue(_self.getValue());
        },
        error: function (jqXHR) {
          jqXHR.responseJSON && console.log(jqXHR.responseJSON.msg);
        }
      });
    },

    /**
     *
     * @param {String} eventname
     * @param {Function} event
     * @param {Boolean} custom
     */
    bind: function (eventname, event, custom) {
      if (custom) {
        $.wControlInterface.bind.call(this, eventname, event, custom);
      } else {
        if (eventname == 'change' && (!!window.ActiveXObject || 'ActiveXObject' in window)) {
          this.$editableElem.find("input[name='" + this.getCtlName() + "']").on(eventname, function () {
            var _self = this;
            var args = arguments;
            setTimeout(function () {
              event.apply(_self, args);
            }, 1);
          });
        } else {
          this.$editableElem.find("input[name='" + this.getCtlName() + "']").on(eventname, event);
        }
      }
    },

    /**
     * 单选时,被选中时，将ctlField中的字段展示出来
     */
    setCtlField: function ($this) {
      if (this.options.ctrlField != '' && this.options.ctrlField != null && this.options.ctrlField != ',') {
        var ctlField = this.options.ctrlField.split(',')[0];
        var fields = ctlField.split(';');
        for (var i = 0; i < fields.length; i++) {
          var field = fields[i];
          if (typeof $this == 'undefined') {
            // 不是通过click调用进来的,初始化时调用进来的
            $(".value[name='" + field + "']")
              .parents('.field')
              .hide();
          } else {
            if ($.ControlManager) {
              var ctl = $.ControlManager.getCtl(field);
              if (typeof ctl == 'undefined' || ctl == null) {
                console.log('无法找到控件' + field);
                continue;
              }

              if ($this.prop('checked') == true) {
                ctl.setVisible(true);
              } else {
                ctl.setVisible(false);
              }
            }
          }
        }
      }
    },
    /* 从页面元素上收集map值 */
    collectValue: function () {
      var value = [];
      var _this = this;
      var empty = true;
      $("input[name='" + this.getCtlName() + "']:checked", this.$editableElem[0]).each(function () {
        var $this = $(this);
        if ($this.is('.other_input')) {
          value.push($this.next().next().val());
        } else {
          value.push($this.val());
        }
        empty = false;
      });
      if (_this.isSingleCheck() && empty) {
        value = Object.keys(_this.options.singleUnCheckContent)[0];
      }
      _this.setValue(value);
    },

    /* 设值到标签中 */
    setValue2LabelElem: function () {
      var self = this;
      if (self.$labelElem == null) {
        return;
      }
      self.$labelElem.html(self.getDisplayValue());
      self.$labelElem.attr('title', self.getDisplayValue());
      if (self.options.lazyLoading && self.options.optionSet.length == 0) {
        self.lazySetDictOptions(function (optionSet) {
          // self.setOptionSet(optionSet);
          self.options.optionSet = optionSet;
          self.setValue(self.value);
        }, true);
      }
    },

    /* 设置到可编辑元素中 */
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
        valueObj = [valueObj];
      }
      self.get$InputElem().each(function () {
        var checked = false;
        var $this = $(this);
        for (var i = 0; i < valueObj.length; i++) {
          if ($this.val() == valueObj[i]) {
            $this.checked = true;
            if ($this.prop('checked') != true) {
              $this.prop('checked', true);
            }
            checked = true;
            break;
          }
        }
        if (!checked) {
          if ($this.is('.other_input')) {
            $this.prop('checked', true);
            $this.next().next().show();
            $this.next().next().val(valueObj[i]);
          } else {
            $this.prop('checked', false);
            $this.checked = false;
          }
        }
      });
      self.getCheckedNum();
    },

    isSingleCheck: function () {
      return this.options.selectMode == '1';
    },
    isLabelElem: function () {
      return this.options.columnProperty.showType == '2' && this.options.columnProperty.readStyle == '2';
    },

    isBtnShape: function () {
      return this.options.radioShape == '1';
    },
    isSelectAll: function () {
      return this.options.checkboxAll == '1';
    },
    getDisplayValue: function () {
      var self = this;
      var value = self.value;
      if ($.trim(value).length == 0) {
        if (self.isSingleCheck()) {
          var selectobj = eval('(' + self.options.singleUnCheckContent + ')');
          for (var i in selectobj) {
            return selectobj[i];
          }
        }
        return '';
      }
      return $.wControlInterface.getDisplayValue.apply(self, arguments);
    },
    /**
     * 获取输入框元素
     */
    get$InputElem: function () {
      var self = this;
      if (self.$editableElem == null) {
        return $([]); // 还没生成输入框时，先返回一个jquery对象
      } else {
        return $("input[name='" + self.getCtlName() + "']", self.$editableElem[0]);
      }
    },

    isValueMap: function () {
      return true;
    },
    setReadOnly: function (isReadOnly) {
      //if (this.options.isShowAsLabel) {
      // return;
      //}
      this.render();

      if (isReadOnly) {
        this.options.columnProperty.showType = dyshowType.readonly;
        this.get$InputElem().attr('disabled', 'disabled');
        this.get$InputElem().parents('.input-box').find('.selectAll').attr('disabled', 'disabled');
        $('#other_input' + this.getCtlName(), this.$editableElem[0]).attr('readonly', 'readonly');
      } else {
        this.options.columnProperty.showType = dyshowType.edit;
        this.get$InputElem().removeAttr('disabled');
        // this.get$InputElem().parents(".input-box").find(".selectAll").removeAttr("disabled");
        $('#other_input' + this.getCtlName(), this.$editableElem[0]).removeAttr('readonly');
      }
    },

    // add by wujx 20161103
    setEnable: function (isEnable) {
      this.setReadOnly(!isEnable);
    },
    getRule: function () {
      var rule = $.ControlUtil.getCheckRuleAndMsg(this.options)['rule'];
      var checkboxRule = this.getCheckboxRule();
      if (rule == undefined) {
        rule = checkboxRule;
      } else {
        $.extend(rule, checkboxRule);
      }

      return JSON.cStringify(rule);
    },

    getCheckboxRule: function () {
      var rule = {};

      if (this.options.checkboxMin) {
        rule.checkboxMin = parseFloat(this.options.checkboxMin);
      }
      if (this.options.checkboxMax) {
        rule.checkboxMax = parseFloat(this.options.checkboxMax);
      }
      return rule;
    },
    getMessage: function () {
      var msg = $.ControlUtil.getCheckRuleAndMsg(this.options)['msg'];
      var checkboxMsg = this.getCheckboxMessage();
      if (msg == undefined) {
        msg = checkboxMsg;
      } else {
        $.extend(msg, checkboxMsg);
      }
      return JSON.cStringify(msg);
    },

    getCheckboxMessage: function () {
      var msg = {};

      if (this.options.checkboxMin) {
        msg.checkboxMin = '选中数不能小于最小值' + this.options.checkboxMin;
      }
      if (this.options.checkboxMax) {
        msg.checkboxMax = '选中数不能大于最大值' + this.options.checkboxMax;
      }
      return msg;
    }
  };
})(jQuery);
