(function ($) {
  $.wTagGroupMethod = {
    hasSelectTag: false,
    hasEditted: false,
    setDisplayAsCtl: function () {
      // 显示为可编辑框
      var options = this.options;
      options.isShowAsLabel = false;
      if (this.$editableElem == null) {
        this.createEditableElem();
        this.initInputEvents();
      }
      this.$editableElem.show();
    },
    setValue2LabelElem: function () {
      var self = this;
      if (self.$labelElem == null) {
        return;
      }
      var value = self.value;
      if (self.isValueMap()) {
        value = self.getDisplayValue();
      }
      if (typeof value == 'string' && value.indexOf('\n') != -1 && !self.isCkeditor()) {
        // 对于富文本则不处理
        value = value.replaceAll('\n', '<br/>');
      }
      self.hasEditted = true;
      self.$labelElem.attr('title', value);
      self.$labelElem.attr('data-status', 'edited');
      self.$labelElem.hide();
      if (self.$editableElem) {
        self.$editableElem.show();
      } else {
        self.createEditableElem();
      }
    },
    getOptionSet: function (options) {
      var optionSet = {};
      var selectobj = options.optionSet;
      if (typeof selectobj == 'undefined' || selectobj == null || (typeof selectobj == 'string' && $.trim(selectobj).length == 0)) {
        console.error('a json parameter is null , used to initialize taggroup options ');
        return;
      }
      if (options.optionDataSource == dyDataSourceType.dataDictionary) {
        // 来自字典,这时optionSet为数组
        if (selectobj.length == 0) {
          return;
        } else {
          for (var j = 0; j < selectobj.length; j++) {
            var obj = selectobj[j];
            var key = obj.value || obj.code;
            optionSet[key] = obj.name;
          }
        }
      } else if (options.optionDataSource == dyDataSourceType.dataConstant) {
        if ($.isArray(selectobj)) {
          for (var k = 0; k < selectobj.length; k++) {
            var obj = selectobj[k];
            var key = typeof obj == 'string' ? obj : obj['value'] ? obj['value'] : Object.keys(obj)[0];
            optionSet[key] = typeof obj == 'string' ? obj : obj['name'] ? obj['name'] : obj[Object.keys(obj)[0]];
          }
        } else {
          optionSet = selectobj;
        }
      } else {
        if ($.isArray(selectobj)) {
          var fieldDef = this.getFieldParams();
          for (var k = 0; k < selectobj.length; k++) {
            var obj = selectobj[k];
            var key = obj[fieldDef.dataSourceFieldName];
            optionSet[key] = obj[fieldDef.dataSourceDisplayName];
          }
        } else {
          optionSet = selectobj;
        }
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
    setOptionSet: function (optionSet) {
      for (var key = 0; key < optionSet.length; key++) {
        var value = optionSet[key];
        if (typeof key != 'string' || typeof value != 'string') {
          console.error(
            this.getCtlName + ':非法的自定义选项(选项须为字符串)--typeof key == ' + typeof key + ' ----typeof value == ' + typeof value
          );
          return false;
        }
      }
      this.options.optionSet = optionSet;
      // this.options.optionDataSource = dyDataSourceType.custom; // 用户自定义
      return true;
    },
    createEditableElem: function () {
      // 创建可编辑框
      var _this = this;
      if (this.$editableElem != null) {
        return;
      }

      var options = this.options;
      var ctlName = this.getCtlName();

      var editableElem = document.createElement('span');
      editableElem.setAttribute('class', this.editableClass);
      editableElem.setAttribute('name', ctlName);
      $(editableElem).css(
        $.extend(
          {
            display: 'block',
            'min-height': '24px'
          },
          this.getTextInputCss()
        )
      );
      this.hasEditted = false;
      this.$placeHolder.after($(editableElem));
      this.$editableElem = this.$placeHolder.next('.' + this.editableClass);
      this.drawOptionElem(options);
      this.elementEvent(options);
      var val = this.getValue();
      if (val.length > 0) {
        this.setValue2EditableElem(value);
      }

      if (options.optionDataAutoSet) {
        var $dyform = DyformFacade.get$dyform();
        $dyform.bind2Dyform2('beforeSetData', function () {
          _this.getDynamicOptionSet();
        });
      }

      if (options.lazyLoading) {
        _this.lazySetDictOptions(function (optionSet) {
          _this.setOptionSet(optionSet);
          _this.drawOptionElem(options);
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
    initDataStore: function () {
      var _self = this;
      var fieldDef = _self.getFieldParams();
      var DataStore = require('dataStoreBase');
      _self.Datastore = new DataStore({
        dataStoreId: fieldDef.dataSourceId,
        receiver: _self,
        pageSize: 1000,
        onDataChange: function (data, totalCount, params, getDefinitionJson) {
          _self.options.optionSet = data;
          _self.drawOptionElem(_self.options);
          _self.setValue(_self.getValue());
        },
        error: function (jqXHR) {
          jqXHR.responseJSON && console.log(jqXHR.responseJSON.msg);
        }
      });
    },
    // 重新加载常量的备选项
    reloadDataOptions: function (options) {
      var self = this;
      var relateField = DyformFacade.get$dyform().getControl(options.relateField);
      if (relateField != undefined) {
        relateField.proxiedAfterInvoke(
          'setValue',
          _.debounce(
            function (value) {
              var optionSet = [];
              if ($.trim(value).length > 0) {
                optionSet = self.getConstantOptionSet(options.dataOptsList, value, []);
              }
              options.optionSet = self.arrayRepeat(optionSet);
              self.drawOptionElem(options);
              self.setValue(self.getValue());
            },
            500,
            {
              leading: false,
              trailing: true
            }
          )
        );
      }
    },

    // 重新加载数据字典的备选项
    reloadDictOptions: function (options) {
      var self = this;
      var relateField = DyformFacade.get$dyform().getControl(options.relateField);
      if (relateField != undefined) {
        relateField.proxiedAfterInvoke(
          'setValue',
          _.debounce(
            function (value) {
              if (value == null) {
                value = '';
              } else if ($.isArray(value)) {
                value = value.join(';');
              }
              var dictType = '';
              var optionSet = [];
              if (relateField.options.dictCode && relateField.options.dictCode != '') {
                dictType = relateField.options.dictCode.split(':')[0]; // 类型为关联字段的字典
              }
              if (value != '' && value.split(';').length == 1) {
                options.optionSet = self.getNewOptionSet(dictType, optionSet, value); //  获取备选项的值
              } else {
                options.optionSet = optionSet;
              }

              self.drawOptionElem(options);
              self.setValue(self.getValue());
            },
            500,
            {
              leading: false,
              trailing: true
            }
          )
        );
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
            relateField.proxiedAfterInvoke(
              'setValue',
              _.debounce(
                function (value) {
                  value = value || '';
                  dataList[relateFieldList[i]] = value;
                  self.Datastore.clearParams();
                  for (var k in dataList) {
                    self.Datastore.addParam(k.toLocaleLowerCase(), dataList[k]);
                  }

                  self.Datastore.load();
                },
                500,
                {
                  leading: false,
                  trailing: true
                }
              )
            );
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

    drawOptionElem: function (options) {
      var optionSet = this.getOptionSet(options);
      var html = "<ul class='tag-group'></ul>";

      if (options.tagEditable == '1') {
        // 备选项添加
        html += "<span class='add-tags'><i class='iconfont icon-ptkj-jiahao'></i>";
        html +=
          "<div class='more-tags-box'><ul class='more-tags-data'>" +
          "<li class='option-select-all'><i class='iconfont icon-ptkj-duoxuan-weixuan'></i>全选</li>";

        for (var key in optionSet) {
          html +=
            "<li class='tag-options-item' data-index='" +
            key +
            "'><i class='tag-check iconfont icon-ptkj-duoxuan-weixuan'></i>" +
            optionSet[key] +
            '</li>';
        }

        html += "</ul><div class='tag-operate'>";
        html +=
          "<button class='tag-operate-btn tag-operate-sure' type='button'>确定</button>" +
          "<button class='tag-operate-btn tag-operate-close' type='button'>取消</button>";

        html += '</div></div></span>';
      } else {
        // 直接编辑添加
        html +=
          "<span class='edit-tags'>" +
          "<input id='addAndEditTag' class='edit-tags-input' type='text' placeholder='标签名' />" +
          "<i class=' iconfont icon-ptkj-jiahao'></i>" +
          '</span>';
      }

      this.$editableElem.html(html);
    },
    elementEvent: function (options) {
      var _this = this;

      $(document).bind('click', function (e) {
        // 点击关闭标签组选择框
        e.stopPropagation();
        var tagsBox = $('.more-tags-box', _this.$editableElem[0]);
        if (!$(e.target).hasClass('more-tags-box') && $('.more-tags-box:visible').size() > 0 && tagsBox.is(':visible')) {
          $('.tag-operate-close', _this.$editableElem[0]).trigger('click');
        }
      });

      $('.edit-tags', this.$editableElem[0]).live('click', function () {
        // 直接添加点击事件
        $(this).addClass('tag-focus').find('#addAndEditTag').show().focus();
      });

      $('#addAndEditTag', this.$editableElem[0]).blur(function () {
        // 直接添加失去焦点事件

        if ($(this).val() != '') {
          var shapeClass = options.tagShape == '1' ? '' : 'tag-shapes';

          var liDom =
            "<li class='tag-group-item " +
            shapeClass +
            "' data-index='" +
            $(this).val() +
            "'>" +
            "<i class='three-angle'></i>" +
            '<span>' +
            $(this).val() +
            "</span><i class='tag-delete iconfont icon-ptkj-dacha-xiao'></i>" +
            '</li>';

          $(this).parent().prev('.tag-group').append(liDom);
          var index = $(_this.$editableElem[0]).find('.tag-group').find('li').length;

          if (options.tagColor == '2') {
            $(_this.$editableElem[0])
              .find('.tag-group li')
              .last()
              .find('span,.three-angle')
              .css({
                color: options.tagFontColor[(index - 1) % options.tagFontColor.length] || '#666',
                'border-color': options.tagBorderColor[(index - 1) % options.tagBorderColor.length] || '#ddd',
                'background-color': options.tagBgColor[(index - 1) % options.tagBgColor.length] || '#f6f6f6'
              });
          }
          $(this).val('');
          _this.collectValue();
        }
        $(this).parent().removeClass('tag-focus');
        $(this).hide();
      });

      $('.add-tags', this.$editableElem[0]).live('click', function (e) {
        // 备选项选择点击显示弹窗
        e.stopPropagation();
        if ($('.more-tags-box:visible').size() > 0) {
          _this.cancleSelectedTag(options);
          $('.more-tags-box:visible').hide();
        }
        if ($('.options-open').size() > 0) {
          $('.options-open').removeClass('options-open').next().hide();
        }
        var left = $(this).offset().left >= 600 ? '-400' : '0';

        $(this)
          .find('.more-tags-box')
          .css({
            bottom: '-' + $(this).find('.more-tags-box').height() - 5 + 'px',
            left: left + 'px'
          })
          .show();

        _this.setElemDisable(options);
      });

      $('.tag-options-item', this.$editableElem[0]).live('click', function (e) {
        // 选择标签/单个选择
        e.stopPropagation();
        if ($(this).hasClass('tag-disabled')) {
          return false;
        }
        _this.hasSelectTag = true;
        _this.selectTags($(this), 'tag-selected', '');
        _this.setElemDisable(options);

        if (!$(this).hasClass('tag-selected')) {
          var allElem = $(_this.$editableElem[0]).find('.option-select-all');
          _this.removeTagClass(allElem, 'select-all', 'cancle');
        }
      });

      $('.option-select-all', this.$editableElem[0]).live('click', function (e) {
        // 选择标签/全选
        e.stopPropagation();
        if ($(this).hasClass('tag-disabled')) {
          return false;
        }
        _this.hasSelectTag = true;
        _this.selectTags($(this), 'select-all', '');
      });

      $('.tag-operate-close', this.$editableElem[0]).live('click', function (e) {
        // 取消
        e.stopPropagation();
        if (options.tagEditable == '1' && _this.hasSelectTag) {
          _this.cancleSelectedTag(options);
        }
        _this.hasSelectTag = false;

        $(this).parents('.more-tags-box').hide();
      });

      $('.tag-operate-sure', this.$editableElem[0]).live('click', function (e) {
        // 确定
        e.stopPropagation();
        var tagSelected = $(_this.$editableElem[0]).find('.tag-selected');
        var liDom = '';
        tagSelected.each(function (index) {
          var shapesClass = options.tagShape == '1' ? '' : 'tag-shapes';
          liDom +=
            "<li class='tag-group-item " +
            shapesClass +
            "' data-index='" +
            $(tagSelected[index]).data('index') +
            "'>" +
            "<i class='three-angle'></i>" +
            '<span>' +
            $(tagSelected[index]).text() +
            "<i class='tag-delete  iconfont icon-ptkj-dacha-xiao'></i></span>" +
            '</li>';
        });

        $(this).parents('.add-tags').prev('.tag-group').html(liDom);

        var lis = $(_this.$editableElem[0]).find('.tag-group li');
        if (options.tagColor == '2' && lis.length > 0) {
          lis.each(function (index) {
            $(lis[index])
              .find('span,.three-angle')
              .css({
                color: options.tagFontColor[index % options.tagFontColor.length] || '#666',
                'border-color': options.tagBorderColor[index % options.tagBorderColor.length] || '#ddd',
                'background-color': options.tagBgColor[index % options.tagBgColor.length] || '#f6f6f6'
              });
          });
        }
        _this.hasSelectTag = false;

        $(this).parents('.more-tags-box').hide();
        _this.collectValue();
      });

      $('.tag-delete', this.$editableElem[0]).live('click', function () {
        // 删除已选标签
        var tagItem = $(this).parents('.tag-group-item');
        tagItem.remove();
        if (options.tagEditable == '1') {
          var optionsItem = $(_this.$editableElem[0]).find('.tag-options-item');
          optionsItem.each(function (index) {
            if ($(optionsItem[index]).data('index') == tagItem.data('index')) {
              _this.selectTags($(optionsItem[index]), 'tag-selected');
            }
          });
          if ($(_this.$editableElem[0]).find('.option-select-all').hasClass('select-all')) {
            _this.selectTags($(_this.$editableElem[0]).find('.option-select-all'), 'select-all', 'cancle');
          }
        }
        _this.collectValue();
      });

      $('#addAndEditTag', this.$editableElem[0]).blur(function () {});
    },
    cancleSelectedTag: function (options) {
      var _this = this;
      var optionItem = $(this.$editableElem[0]).find('.tag-options-item');
      var tagTtem = $(this.$editableElem[0]).find('.tag-group-item');
      var allElem = $(this.$editableElem[0]).find('.option-select-all');
      var length = Object.keys(options.optionSet).length;
      if ((!allElem.hasClass('select-all') && tagTtem.length == length) || (allElem.hasClass('select-all') && tagTtem.length == length)) {
        _this.addTagClass(allElem, 'select-all', 'cancle');
      } else {
        _this.removeTagClass(allElem, 'select-all', 'cancle');
      }
      optionItem.each(function (index) {
        if (tagTtem.length == 0) {
          _this.removeTagClass($(optionItem[index]), 'tag-selected', '');
        } else {
          for (var i = 0; i < tagTtem.length; i++) {
            if ($(optionItem[index]).data('index') == $(tagTtem[i]).data('index')) {
              _this.addTagClass($(optionItem[index]), 'tag-selected', '');
              break;
            } else {
              _this.removeTagClass($(optionItem[index]), 'tag-selected', '');
            }
          }
        }
      });
    },
    selectTags: function (ele, selector, status) {
      if (ele.hasClass(selector)) {
        this.removeTagClass(ele, selector, status);
      } else {
        this.addTagClass(ele, selector, status);
      }
    },
    removeTagClass: function (ele, selector, status) {
      var xz = 'icon-ptkj-duoxuan-xuanzhong',
        wxz = 'icon-ptkj-duoxuan-weixuan';
      ele.removeClass(selector).find('i').removeClass(xz).addClass(wxz);
      if (selector == 'select-all' && status != 'cancle') {
        ele.siblings('li').removeClass('tag-selected').find('i').removeClass(xz).addClass(wxz);
      }
    },
    addTagClass: function (ele, selector, status) {
      var xz = 'icon-ptkj-duoxuan-xuanzhong',
        wxz = 'icon-ptkj-duoxuan-weixuan';
      ele.addClass(selector).removeClass('tag-disabled').find('i').removeClass(wxz).addClass(xz);
      if (selector == 'select-all' && status != 'cancle') {
        ele.siblings('li').addClass('tag-selected').removeClass('tag-disabled').find('i').removeClass(wxz).addClass(xz);
      }
    },
    setElemDisable: function (options) {
      if (options.selectMode == '2' && options.selectMaxContent != '') {
        if (parseInt(options.selectMaxContent) - Object.keys(options.optionSet).length < 0) {
          $(this.$editableElem[0]).find('.option-select-all').addClass('tag-disabled');
        }

        var tagSelected = $(this.$editableElem[0]).find('.tag-selected'); // 被选中的数据
        var notSelected = $(this.$editableElem[0]).find(".tag-options-item:not('.tag-selected')"); // 没有被选中的数据

        if (tagSelected.length < parseInt(options.selectMaxContent)) {
          notSelected.removeClass('tag-disabled');
        } else {
          notSelected.addClass('tag-disabled');
        }
      }
    },
    collectValue: function () {
      /* 从页面元素上收集map值 */
      var value = [];
      var _this = this;
      $('li.tag-group-item', this.$editableElem[0]).each(function () {
        value.push($(this).data('index'));
      });
      _this.setValue(value);
    },
    isValueMap: function () {
      return true;
    },
    /* 设值到可编辑元素中 */
    getDisplayValue: function () {
      var self = this;
      var value = self.value;
      if ($.trim(value).length == 0) {
        return '';
      }
      return $.wControlInterface.getDisplayValue.apply(self, arguments);
    },
    setValue: function (value) {
      var self = this;

      if ($.isArray(value)) {
        value = value.join(',');
      } else if (self.isValueMap()) {
        if ($.isPlainObject(value) || (typeof value === 'string' && value.indexOf('{') === 0)) {
          self.setValueByMap(value);
        }
      }
      self.value = value;
      if (self.isValueMap()) {
        self.setValue2LabelElem && self.setValue2LabelElem();
        self.setValue2EditableElem && self.setValue2EditableElem();
      } else {
        self.get$InputElem().attr('data-value', value);
      }
      if (self.culateByFormula) {
        self.culateByFormula(); // 根据运算公式计算
      }
      if (self.setValueLock) {
        return self.setValueLock;
      }
      self.setValueLock = true;
      try {
        if (self.isValueMap()) {
          self.setToRealDisplayColumn();
        }
        self.invoke('afterSetValue', self.value);
        var uuid = self.getDataUuid();
        if (uuid && self.hasEditted && self.options.displayAsLabel) {
          var formuuid = self.getFormDefinition().uuid;
          var real = self.getFieldName();
          var fieldValueMap = {};
          fieldValueMap[real] = self.getValue();
          if (self.options.columnProperty.realDisplay && self.options.columnProperty.realDisplay.display) {
            var display = self.options.columnProperty.realDisplay.display;
            var displayValue = self.getDisplayValue();
            fieldValueMap[display] = displayValue;
          }
          var url = ctx + '/pt/dyform/data/updateFieldValue';
          var postData = {
            uuid: uuid,
            formUuid: formuuid,
            fieldValueMap: fieldValueMap
          };
          $.ajax({
            url: url,
            type: 'POST',
            data: JSON.cStringify(postData),
            dataType: 'json',
            contentType: 'application/json',
            success: function (result) {
              if (result.success == 'true' || result.success == true) {
                if (
                  self.options.columnProperty.events &&
                  self.options.columnProperty.events.controlEvents &&
                  self.options.columnProperty.events.controlEvents.afterSetValue
                ) {
                  var controlEvents = self.options.columnProperty.events.controlEvents;
                  appContext.eval(controlEvents['afterSetValue'], self, {
                    $this: self, //当前控件对象
                    columnProperty: self.options.columnProperty, //控件属性
                    $form: DyformFacade.get$dyform(), //当前表单
                    event: 'refreshTable'
                  });
                }
              } else {
                appModal.error('数据保存失败');
              }
            },
            error: function (data) {
              appModal.error('数据保存失败');
            }
          });
        }
      } finally {
        self.setValueLock = false;
      }
    },
    mapSelectItems: function (valueObj) {
      var self = this;
      var optionItem = $(this.$editableElem[0]).find('.tag-options-item');

      optionItem.each(function (index) {
        if (valueObj.length > 0) {
          for (var i = 0; i < valueObj.length; i++) {
            if ($(optionItem[index]).data('index') == valueObj[i]) {
              self.addTagClass($(optionItem[index]), 'tag-selected', '');
              break;
            } else {
              self.removeTagClass($(optionItem[index]), 'tag-selected', '');
            }
          }
        } else {
          self.removeTagClass($(optionItem[index]), 'tag-selected', '');
        }
      });
    },
    getDisplayLabel: function (realValue) {
      var self = this,
        separator;
      if (typeof realValue === 'string' && realValue.indexOf((separator = ',')) > 0) {
        realValue = realValue.split(separator);
      } else if (false === $.isArray(realValue)) {
        realValue = [realValue];
      }
      var displayValue = null;
      var showDisplay = false; //用于options无对应数据但有配置显示值字段时直接取显示值字段的值
      // getOptionSet树形控件和组织选择没有getOptionSet,需要重写getDisplayLabel
      if ($.isFunction(self.getOptionSet)) {
        displayValue = [];
        var optionSet = self.getOptionSet(self.options) || {};
        for (var i = 0; i < realValue.length; i++) {
          var value,
            key = realValue[i];
          if ($.isArray(optionSet)) {
            for (var j = 0; j < optionSet.length; j++) {
              if (key == optionSet[j].value || key == optionSet[j].id) {
                value = optionSet[j].name || optionSet[j].text;
                break;
              }
            }
          } else {
            value = optionSet[key];
          }
          // other_input
          if (value === undefined) {
            showDisplay = true;
            displayValue.push('undefined');
          } else {
            displayValue.push(value);
          }
        }
      } else {
        displayValue = realValue;
      }

      if (self.options.tagEditable != '1' && showDisplay) {
        displayValue = realValue;
      }
      return typeof displayValue === 'string' ? displayValue : displayValue.join(self.options.separator);
    },
    setValue2EditableElem: function () {
      var self = this;
      var option = self.options;

      if (self.$editableElem == null) {
        return;
      }

      var valueObj = self.value;
      if (self.options.columnProperty.realDisplay && self.options.columnProperty.realDisplay.display) {
        var displayVal = self.getDisplayValue();
        // console.log(self.getFormData())
        //                 var display = self.options.columnProperty.realDisplay.display;
        //                 var displayVal = $("#"+display).attr("title")
        // var displayVal =self.getControl(display).getValue()
      } else {
        var displayVal = self.getDisplayLabel(valueObj);
      }

      if ($.isArray(valueObj)) {
      } else if (typeof valueObj === 'string' && valueObj != '') {
        valueObj = valueObj.split(',');
      } else {
        valueObj = '';
      }

      if ($.isArray(displayVal)) {
      } else if (typeof displayVal === 'string' && displayVal != '') {
        displayVal = displayVal.split(',');
      } else {
        displayVal = '';
      }

      if (valueObj.length > 0) {
        var shapesClass = option.tagShape == '1' ? '' : 'tag-shapes';
        var liDom = '';
        for (var i = 0; i < valueObj.length; i++) {
          if (valueObj[i] != '' && displayVal[i] !== 'undefined') {
            var styles =
              option.tagColor == '1'
                ? ''
                : 'background:' +
                  option.tagBgColor[i % option.tagBgColor.length] +
                  ';color:' +
                  option.tagFontColor[i % option.tagFontColor.length] +
                  ';border-color:' +
                  option.tagBorderColor[i % option.tagBorderColor.length] +
                  ';';
            liDom +=
              "<li class='tag-group-item " +
              shapesClass +
              "' data-index='" +
              valueObj[i] +
              "'>" +
              "<i class='three-angle' style='" +
              styles +
              "'></i>" +
              "<span style='" +
              styles +
              "'>" +
              displayVal[i] +
              "</span><i class='tag-delete  iconfont icon-ptkj-dacha-xiao'></i>" +
              '</li>';
          }
        }
        $(self.$editableElem[0]).find('.tag-group').html(liDom);
        if (option.tagEditable == '1') {
          self.mapSelectItems(valueObj);
        }
      }
    },
    get$InputElem: function () {
      var self = this;
      if (self.$editableElem == null) {
        return $([]); // 还没生成输入框时，先返回一个jquery对象
      } else {
        return self.$editableElem; // $("span[name='" + self.getCtlName() + "']", self.$editableElem[0]);
      }
    },
    getRule: function () {
      var rule = $.ControlUtil.getCheckRuleAndMsg(this.options)['rule'];
      var taggroupRule = this.getTaggroupRule();
      if (rule == undefined) {
        rule = taggroupRule;
      } else {
        $.extend(rule, taggroupRule);
      }

      return JSON.cStringify(rule);
    },

    getTaggroupRule: function () {
      var rule = {};

      if (this.options.selectMinContent) {
        rule.selectMinContent = parseFloat(this.options.selectMinContent);
      }
      if (this.options.selectMaxContent) {
        rule.selectMaxContent = parseFloat(this.options.selectMaxContent);
      }
      return rule;
    },
    getMessage: function () {
      var msg = $.ControlUtil.getCheckRuleAndMsg(this.options)['msg'];
      var taggroupMsg = this.getTaggroupMessage();
      if (msg == undefined) {
        msg = taggroupMsg;
      } else {
        $.extend(msg, taggroupMsg);
      }
      return JSON.cStringify(msg);
    },

    getTaggroupMessage: function () {
      var msg = {};

      if (this.options.selectMinContent) {
        msg.selectMinContent = '选中数不能小于最小值' + this.options.selectMinContent;
      }
      if (this.options.selectMaxContent) {
        msg.selectMaxContent = '选中数不能大于最大值' + this.options.selectMaxContent;
      }
      return msg;
    }
  };
})(jQuery);
