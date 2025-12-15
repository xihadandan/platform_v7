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

  /*
   * COMBOTREE CLASS DEFINITION ======================
   */
  var ComboTree = function ($placeHolder, options) {
    this.options = $.extend({}, $.fn['wcomboTree'].defaults, options);
    this.value = '';
    this.$editableElem = null;
    this.$labelElem = null;
    this.$placeHolder = $placeHolder;
  };

  ComboTree.prototype = {
    constructor: ComboTree
  };

  $.ComboTree = {
    ztreeOtherParams: function () {
      var self = this;
      var fieldDef = this.getFieldParams();
      //数据服务的参数
      if (fieldDef.optionDataSource == '1') {
        if (this.options.newServiceName && !this.options.serviceName) {
          return {
            serviceName: this.options.newServiceName,
            methodName: 'getNodes',
            data: ['-1', false, {}],
            version: ''
          };
        }
        var ctlName = this.getCtlName();
        var serviceName = this.options.serviceName.split('.')[0]; // 服务名
        var method = this.options.serviceName.split('.')[1].split('(')[0]; // 方法名
        var datas = this.options.serviceName.split('.')[1].split('(')[1].replace(')', ''); // 数据
        var data = datas.split(',')[0];
        data = data.replace("'", '').replace("'", '');

        var data1 = data.replace(/\|+/g, '');
        this.$editableElem.after("<input type='hidden' id='" + data1 + "'  value='" + data.replace(/\|+/g, ',') + "'>");

        var dataArrays = new Array();
        if ($('#' + data1).val()) {
          if (
            $('#' + data1)
              .val()
              .split(',').length > 1
          ) {
            var dataArray = $('#' + data1)
              .val()
              .split(',');
            for (var da = 0; da < dataArray.length; da++) {
              dataArrays.push(dataArray[da]);
            }
          } else if (
            $('#' + data1)
              .val()
              .indexOf('${data}') > 0
          ) {
            dataArrays.push($('#data').val());
          } else {
            dataArrays.push($('#' + data1).val());
          }
        }
        return {
          serviceName: serviceName,
          methodName: method,
          data: dataArrays,
          version: ''
        };
      }
      //数据仓库的参数
      if (fieldDef.optionDataSource == '2') {
        // self.initDataStore();
        return {
          serviceName: 'cdDataStoreService',
          methodName: 'loadTreeNodes',
          data: [
            {
              dataStoreId: fieldDef.dataSourceId,
              uniqueColumn: fieldDef.uniqueColumn,
              parentColumn: fieldDef.parentColumn,
              displayColumn: fieldDef.displayColumn,
              valueColumn: fieldDef.valueColumn,
              defaultCondition: fieldDef.defaultCondition,
              async: false
            }
          ],
          version: ''
        };
      }

      if (fieldDef.optionDataSource == '3') {
        if (fieldDef.dictUuid) {
          return {
            serviceName: 'dataDictionaryService',
            methodName: 'getAllDataDicAsTree',
            data: [fieldDef.dictUuid],
            version: ''
          };
        }
      }
      return null;
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
          _self.refresh();

          // _self.options.optionSet = _self.buildOptionSet(data);
          // _self.reRender('reRenderOption');
          // if (params && params.afterReRenderOptionCb && $.isFunction(params.afterReRenderOptionCb)) {
          //   params.afterReRenderOptionCb();
          // }
        },
        error: function (jqXHR) {
          jqXHR.responseJSON && console.log(jqXHR.responseJSON.msg);
        }
      });
    },

    setDefaultCondition: function (criterion) {
      var fieldDef = this.getFieldParams();
      fieldDef.defaultCondition = criterion;
    },

    //清除数据仓库条件
    clearDsConditions: function () {
      var fieldDef = this.getFieldParams();
      fieldDef.defaultCondition = '';
    },

    createEditableElem: function () {
      if (this.$editableElem != null) {
        // 创建可编辑框
        return;
      }

      var options = this.options;
      var ctlName = this.getCtlName();
      var editableElem = document.createElement('input');
      editableElem.setAttribute('class', this.editableClass);
      editableElem.setAttribute('name', ctlName);
      editableElem.setAttribute('id', ctlName);
      editableElem.setAttribute('type', 'text');

      $(editableElem).css(this.getTextInputCss());

      this.$placeHolder.after($(editableElem));
      this.$editableElem = this.$placeHolder.next('.' + this.editableClass);
      this.$editableElem.addClass('input-tier'); // css in wellnewoa.css

      this.$_editableElem = $("<input type='hidden' id='_" + ctlName + "' name='_" + ctlName + "' value=''>");
      this.$editableElem.after(this.$_editableElem);

      var clearAll = options.clearAll == true || options.clearAll == undefined ? true : false;
      if (!options.mutiSelect) {
        var fieldCheckRules = options.commonProperty.fieldCheckRules;

        var rIndex = _.findIndex(fieldCheckRules, function (o) {
          return o.value == '1';
        });
        if (rIndex > -1) {
          clearAll = false;
        }
      }

      // 树形下拉框
      var val = '';

      var _this = this;
      var setting = this.treeSetting();

      // 初始化树的值
      this.$editableElem.val(options.columnProperty.defaultValue);
      this.$editableElem.comboTree({
        labelField: '_' + ctlName,
        valueField: ctlName,
        width: options.treeWidth,
        height: options.treeHeight,
        selectParent: options.selectParent,
        treeSetting: setting,
        initService: options.initService,
        initServiceParam: options.initServiceParam,
        mutiSelect: options.mutiSelect,
        placeholder: options.placeholder,
        showCheckAll: options.showCheckAll,
        expandAndCollapse: options.expandAndCollapse,
        allPath: options.allPath ? (options.allPath === 'false' ? false : true) : true,
        clearAll: clearAll,
        dictValueColumn: options.dictValueColumn || 'uuid'
      });
      $('#' + ctlName + '_ztree').data('options', options);

      if (options.optionDataAutoSet) {
        var $dyform = DyformFacade.get$dyform();
        $dyform.bind2Dyform2('beforeSetData', function () {
          _this.getDynamicOptionSet();
        });
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
        this.getDynamicOptionSet();
      }
    },

    //获取动态备选项
    getDynamicOptionSet: function () {
      var options = this.options;
      var fieldDef = this.getFieldParams();
      if (fieldDef.optionDataSource == '3') {
        this.reloadDictOptions(options);
      } else if (fieldDef.optionDataSource == '2') {
        this.reloadSourceOptions(options);
      }
    },

    // 重新加载数据字典的备选项
    reloadDictOptions: function (options) {
      var self = this;
      var relateField = DyformFacade.get$dyform().getControl(options.relateField);
      var fieldDef = this.getFieldParams();
      if (relateField != undefined) {
        relateField.proxiedAfterInvoke('setValue', function (value) {
          if (value == null) {
            value = '';
          } else if ($.isArray(value)) {
            value = value.join(';');
          }
          var display = self.options.columnProperty.realDisplay.display;
          if (display != '') {
            DyformFacade.get$dyform().getControl(display).setValue('');
          }

          if (value != '' && value.split(';').length == 1) {
            $.ajax({
              async: false,
              type: 'get',
              url: ctx + '/basicdata/datadict/type/' + value,
              dataType: 'json',
              success: function (result) {
                if (result.data && result.data.uuid) {
                  fieldDef.dictUuid = value;
                } else {
                  fieldDef.dictUuid = '';
                }
                self.refresh();
              }
            });
          } else {
            fieldDef.dictUuid = '';
            self.refresh();
          }

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
              var param = {};
              for (var k in dataList) {
                param[k.toLocaleLowerCase()] = dataList[k];
              }
              var treeId = self.$editableElem.attr('id') + '_' + 'ztree';
              $.fn.zTree.destroy(treeId);
              self.$editableElem.val('');
              self.$_editableElem.val('');
              var setting = self.treeSetting();
              $.extend(setting.async.otherParam.data[0], {
                params: param
              });
              self.$editableElem.data('comboTree').reInit(setting); //重新初始化
              self.setValue(self.getValue());
            });
          }
        })(i);
      }
    },

    showEditableElem: function () {
      var self = this;
      $.wControlInterface.showEditableElem.apply(self, arguments);
      self.$editableElem.hide();
    },

    treeSetting: function () {
      var _this = this;
      var fieldDef = _this.getFieldParams();
      var otherParam = this.ztreeOtherParams();
      var options = this.options;
      var setting = {
        mutiSelect: options.mutiSelect,
        async: {
          otherParam: otherParam
        },
        view: {
          showLine: true
        },
        check: {
          // 复选框的选择做成可配置项
          enable: options.mutiSelect ? true : false,
          chkStyle: options.mutiSelect ? 'checkbox' : 'radio',
          radioType: 'all'
        },
        src: 'control',
        callback: {
          onAsyncSuccess: function (e, treeId, treeNode) {
            _this.get$InputElem().trigger('onTreeAsyncSuccess', _this, treeId, treeNode);
          },
          onCheck: function (event, treeId, treeNode) {
            /*	if (!_this.options.mutiSelect) {
                            return;
                        }*/
            _this.setValue2Object(); // 将值分别从labelfield和valuefield中取出放到内存中的value对象中
            // _this.setToRealDisplayColumn();
          }
        }
      };
      if (fieldDef.optionDataSource == '3' && options.showRightBtn) {
        //数据字典时右侧显示操作按钮
        setting.view.addDiyDom = _this.addDiyDom;
      }
      return setting;
    },

    //添加自定义按钮
    addDiyDom: function (treeId, treeNode) {
      if (treeNode.parentNode && treeNode.parentNode.id !== 1) return;
      var aObj = $('#' + treeNode.tId + '_a');
      if ($('#diyBtn_' + treeNode.id).length > 0) return;
      var controlStr =
        "<div class='ztree-diy-control iconfont icon-ptkj-gengduocaozuo' id='diyControl_" +
        treeNode.id +
        "' title='" +
        treeNode.name +
        "'></div>";
      aObj.after(controlStr);
      var btn = $('#diyControl_' + treeNode.id);
      if (!btn) return;

      btn.on('click', function () {
        $.ComboTree.buildSelectControlElement(btn, treeId);
      });
    },

    hasEventAdd: function (treeId, type) {
      var _options = $('#' + treeId).data('options');
      if (_options.dicCodeAddBtn) {
        if (type === 'btn') {
          return (
            '<div class="event-item event-add pull-left add-bro active left-border-radius">加同级</div>' +
            '<div class="event-item event-add pull-left add-child right-border-radius">加子级</div>'
          );
        } else {
          return (
            '<div class="event-field">' +
            '<input class="form-control" name="nodeNames" placeholder="选项名称">' +
            '<div class="add-node">添加</div></div>'
          );
        }
      } else {
        return '';
      }
    },
    hasEventDel: function (treeId) {
      var _options = $('#' + treeId).data('options');
      if (_options.dicCodeDelBtn) {
        return '<div class="event-item event-del pull-right">' + '<span class="iconfont del-node icon-ptkj-shanchu"></span>' + '</div>';
      } else {
        return '';
      }
    },
    hasEventSort: function (treeId) {
      var _options = $('#' + treeId).data('options');
      var moveUpIcon = '';
      var moveDownIcon = '';
      if (_options.dicCodeMoveUp) {
        moveUpIcon = '<span class="iconfont move-up icon-ptkj-shangyi"></span>';
      }
      if (_options.dicCodeMoveDown) {
        moveDownIcon = '<span class="iconfont move-down icon-ptkj-xiayi"></span>';
      }
      if (!moveUpIcon && !moveDownIcon) return '';
      return '<div class="event-item event-sort pull-right">' + moveUpIcon + moveDownIcon + '</div>';
    },
    buildSelectControlElement: function (mount, treeId) {
      var _self = this;
      var controlElement =
        '<div class="well-select-control-element"><div class="control-area"></div>' +
        '        <div class="well-select-control-element-event clearfix">' +
        _self.hasEventAdd(treeId, 'btn') +
        _self.hasEventDel(treeId) +
        _self.hasEventSort(treeId) +
        '        </div>' +
        _self.hasEventAdd(treeId, 'field') +
        '    </div>';

      var mountWidth = mount.outerWidth();
      var mountHeight = mount.outerHeight();
      var mountOffsetTop = mount.offset().top - $(window).scrollTop();
      var mountOffsetLeft = mount.offset().left - $(window).scrollLeft();
      mount.append(controlElement);
      var $controlElement = mount.find('.well-select-control-element');
      $controlElement.slideDown('fast');
      $controlElement.css({
        top: mountOffsetTop + mountHeight + 'px',
        left: mountOffsetLeft + mountWidth - $controlElement.outerWidth() + 'px'
      });

      $controlElement.on('click', function (e) {
        e.stopPropagation();
      });
      mount.mouseleave(function () {
        // $controlElement.remove();
      });
      $controlElement.mouseleave(function (e) {
        // if (!e.relatedTarget.isSameNode($(this).parents('.well-select-item')[0])) {
        //   //判断移出时鼠标是否还在当前选项上
        //   $(this).remove();
        // }
      });
      var $eventField = $controlElement.find('.event-field');
      $controlElement.find('.event-add').on('click', function (e) {
        e.stopPropagation();
        $(this).addClass('active').siblings('.event-add').removeClass('active');
        $eventField.show();
      });
      $eventField.find('.add-node').on('click', function () {
        var $this = $(this);
        var _parentLi = mount.closest('li');
        var addNodeValue = $.trim($this.prev().val());
        if (!addNodeValue) return;
        var addType = $controlElement.find('.event-add.active').hasClass('add-bro') ? 'bro' : 'child';
        _self.addNewOption(_parentLi, addNodeValue, treeId, addType);
      });
      $controlElement.find('.move-up').on('click', function () {
        var _parentLi = mount.closest('li');
        _self.sortOption(_parentLi, treeId, 'up');
      });
      $controlElement.find('.move-down').on('click', function () {
        var _parentLi = mount.closest('li');
        _self.sortOption(_parentLi, treeId, 'down');
      });
      $controlElement.find('.del-node').on('click', function () {
        var _parentLi = mount.closest('li');
        _self.delOption(_parentLi, treeId);
      });
    },

    addNewOption: function (currElement, val, treeId, addType) {
      var treeObj = $.fn.zTree.getZTreeObj(treeId); //获取到树
      var nodesSys = treeObj.getNodes(); //可以获取所有的父节点
      var nodesSysAll = treeObj.transformToArray(nodesSys); //获取树所有节点
      var curr_uuid = $(currElement).find('.ztree-diy-control').eq(0).attr('id').split('diyControl_')[1];

      var currNode = null;
      $.each(nodesSysAll, function (i, v) {
        if (v.id === curr_uuid) {
          currNode = v;
          return false;
        }
      });

      var parentNode = currNode.getParentNode();

      var data;
      if (addType === 'bro') {
        data = [val, curr_uuid, (parentNode && parentNode.id) || treeId];
      } else {
        data = [val, null, curr_uuid];
      }

      JDS.call({
        service: 'dataDictionaryService.quickAddDataDic',
        data: data,
        success: function (result) {
          var newNode = {
            id: result.data.uuid,
            name: result.data.name,
            data: result.data
          };
          if (addType === 'bro') {
            treeObj.copyNode(currNode, newNode, 'next');
          } else {
            treeObj.copyNode(currNode, newNode, 'inner');
          }
        }
      });
    },

    delOption: function (currElement, treeId) {
      var treeObj = $.fn.zTree.getZTreeObj(treeId); //获取到树
      var nodesSys = treeObj.getNodes(); //可以获取所有的父节点
      var nodesSysAll = treeObj.transformToArray(nodesSys); //获取树所有节点
      var curr_uuid = $(currElement).find('.ztree-diy-control').eq(0).attr('id').split('diyControl_')[1];

      var currNode = null;
      $.each(nodesSysAll, function (i, v) {
        if (v.id === curr_uuid) {
          currNode = v;
          return false;
        }
      });
      $.ajax({
        type: 'delete',
        url: ctx + '/dict/' + curr_uuid,
        success: function (result) {
          currElement.remove();
        }
      });
    },

    sortOption: function (currElement, treeId, type) {
      var treeObj = $.fn.zTree.getZTreeObj(treeId); //获取到树
      var nodesSys = treeObj.getNodes(); //可以获取所有的父节点
      var nodesSysAll = treeObj.transformToArray(nodesSys); //获取树所有节点
      var curr_uuid = $(currElement).find('.ztree-diy-control').eq(0).attr('id').split('diyControl_')[1];
      var currNode = null;
      var targetNode = null;
      var $targetNode = null;
      var targetNodeUuid = null;
      var JDS_data = [];
      $.each(nodesSysAll, function (i, v) {
        if (v.id === curr_uuid) {
          currNode = v;
          return false;
        }
      });

      if (type === 'up') {
        $targetNode = $(currElement).prev();
        if ($targetNode.length === 0) return;
        targetNodeUuid = $targetNode.find('.ztree-diy-control').eq(0).attr('id').split('diyControl_')[1];
        JDS_data = [targetNodeUuid, curr_uuid];
      } else {
        $targetNode = $(currElement).next();
        if ($targetNode.length === 0) return;
        targetNodeUuid = $targetNode.find('.ztree-diy-control').eq(0).attr('id').split('diyControl_')[1];
        JDS_data = [curr_uuid, targetNodeUuid];
      }

      $.each(nodesSysAll, function (i, v) {
        if (v.id === targetNodeUuid) {
          targetNode = v;
          return false;
        }
      });

      JDS.call({
        service: 'dataDictionaryService.moveDataDicAfterOther',
        data: JDS_data,
        success: function (result) {
          if (type === 'up') {
            treeObj.moveNode(targetNode, currNode, 'prev');
          } else {
            treeObj.moveNode(targetNode, currNode, 'next');
          }
        }
      });
    },

    // 暂未开放删除节点接口
    // cancelCheckNode: function (currId,treeId) {
    //     var self = this;
    //     var checkValue = [];
    //     var checkName = [];
    //     if(self.$editableElem.next().css('display') === 'none') {
    //         self.$editableElem.find('.well-tag').each(function () {
    //             var $this = $(this);
    //             checkValue.push($this.data('value'));
    //             checkName.push($this.data('name'));
    //         });
    //     } else {
    //         var treeObj = $.fn.zTree.getZTreeObj(treeId);//获取到树
    //         var nodes = treeObj.getCheckedNodes();
    //         var cancelNode = null;
    //         $.each(nodes,function (i,v) {
    //             if(v.id === currId) {
    //                 cancelNode = v;
    //             } else {
    //                 checkValue.push(v.id);
    //                 checkName.push(v.name);
    //             }
    //         });
    //         treeObj.checkNode(cancelNode,false,false);
    //     }
    //     if(checkValue.length) {
    //         self.$_editableElem.val(checkValue.join(';'));
    //     } else {
    //         self.$_editableElem.val('');
    //         self.$editableElem.find('.well-select-placeholder').show();
    //     }
    //     self.setValue(self.$_editableElem.val());
    // },

    resetDicOptionset: function (dictUuid) {
      var fieldDef = this.getFieldParams();
      if (fieldDef.optionDataSource == '3') {
        fieldDef.dictUuid = dictUuid;
      }
    },

    //刷新树节点数据
    refresh: function (cb) {
      var treeId = this.$editableElem.attr('id') + '_' + 'ztree';
      $.fn.zTree.destroy(treeId);
      this.$editableElem.val('');
      this.$_editableElem.val('');
      this.$editableElem.data('comboTree').reInit(this.treeSetting()); //重新初始化
      if (cb && $.isFunction(cb)) {
        cb();
      }
    },

    /* 设值到标签中 */
    setValue2LabelElem: function () {
      var self = this;
      if (self.$labelElem == null) {
        return;
      }
      if (!self.$editableElem) {
        self.createEditableElem();
      }

      self.$labelElem.html(self.getDisplayValue());
      self.$labelElem.attr('title', self.getDisplayValue());
      setTimeout(function () {
        self.displayByShowType();
      }, 16);
    },

    /* 设置到可编辑元素中 */
    setValue2EditableElem: function () {
      var self = this;
      if (self.$editableElem == null || self.$_editableElem == null) {
        return;
      }
      var value = self.getValue();
      self.$editableElem.val(value);
      self.$editableElem.comboTree('initValue', value);
    },

    isValueMap: function () {
      return true;
    },

    // 设值,值为真实值
    setValue: function (rawValue) {
      var self = this;
      var value = (rawValue || '') + '';
      var options = self.options;
      if ($.trim(value).length > 0) {
        var value1 = null;
        if (value.indexOf(',') != -1) {
          // 值用,隔开
          value1 = value.split(',');
        } else {
          // 值用;隔开
          value1 = value.split(';');
        }
        var valueMap = [];
        for (var i = 0; i < value1.length; i++) {
          valueMap.push(value1[i]);
        }
        $.wControlInterface.setValue.call(self, valueMap);
        var display = self.options.columnProperty.realDisplay.display;
        var displayValue = self.getDisplayValue();
        if (typeof display === 'string' && display.length > 0) {
          var control = $.ControlManager.getCtl(self.getContronId(display));
          if (typeof control == 'undefined' || control == null) {
            return;
          }
          if (control.value && !displayValue) {
            displayValue = control.value;
          }
        }
        if (displayValue) {
          self.$editableElem &&
            self.$editableElem.comboTree('initValue', {
              value: valueMap.join(self.getSeparator()),
              displayValue: displayValue
            });
        } else {
          self.$editableElem && self.$editableElem.comboTree('initValue', valueMap.join(self.getSeparator()));
        }
      } else {
        // bug#49286: 【项目注册】模块，采购明细从表的采购项目名称未填写，直接校验必填
        $.wControlInterface.setValue.call(self, '');

        self.$editableElem && self.$editableElem.comboTree('initValue', valueMap);
      }
    },
    // 将值分别从labelfield和valuefield中取出放到内存中的value对象中
    setValue2Object: function () {
      var self = this;
      var realValue = self.$editableElem.val();
      if ($.trim(realValue).length == 0) {
        return self.setValue('');
      }

      var displayValue = self.$_editableElem.val();

      var values = realValue.split(self.getSeparator());
      var displayvalue = displayValue.split(self.getSeparator());
      if (values.length !== displayvalue.length) {
        throw new Error('隐藏值和显示值长度不一致!');
      }
      var v = [];
      for (var i = 0; i < values.length; i++) {
        v.push(values[i]);
      }
      // self.value = v;
      // $.wControlInterface.setValue.call(self, v.join(self.getSeparator()));
      self.setValue(v.join(self.getSeparator()));
    },
    getDisplayLabel: function (realValue) {
      var self = this,
        separator;
      if (typeof realValue === 'string' && realValue.indexOf((separator = self.getSeparator())) > 0) {
        realValue = realValue.split(separator);
      } else if (false === $.isArray(realValue)) {
        realValue = [realValue];
      }
      var displayValue = null;
      // getOptionSet树形控件和组织选择没有getOptionSet,需要重写getDisplayLabel
      displayValue = [];
      if (self.$editableElem) {
        return self.$editableElem.data('comboTree').initValue(realValue);
      }
    },
    setReadOnly: function (isReadOnly) {
      if (isReadOnly) {
        this.options.columnProperty.showType = dyshowType.readonly;
        if (this.$_editableElem) {
          this.$editableElem.attr('readonly', 'readonly');
          this.$editableElem.comboTree('readonly', true);
        }
        this.removeValidate();
      } else {
        this.options.columnProperty.showType = dyshowType.edit;
        this.$editableElem.removeAttr('readonly');
        this.$editableElem.comboTree('readonly', false);
      }
    }
  };

  /*
   * COMBOTREE PLUGIN DEFINITION =========================
   */
  $.fn.wcomboTree = function (option) {
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
        data = $this.data('wcomboTree');
        if (data) {
          return data; // 返回实例对象
        } else {
          throw new Error('This object is not available');
        }
      }
    }

    var $this = $(this),
      data = $this.data('wcomboTree'),
      options = typeof option == 'object' && option;
    if (!data) {
      data = new ComboTree($(this), options);

      $.extend(data, $.wControlInterface);
      $.extend(data, $.wTextCommonMethod);
      $.extend(data, $.wSelectCommonMethod);
      $.extend(data, $.ComboTree);

      data.init();
      $this.data('wcomboTree', data);
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

  $.fn.wcomboTree.Constructor = ComboTree;

  $.fn.wcomboTree.defaults = {
    columnProperty: columnProperty, // 字段属性
    commonProperty: commonProperty, // 公共属性

    // 控件私有属性
    disabled: false,
    readOnly: false,
    isShowAsLabel: false,
    isHide: false, // 是否隐藏
    serviceName: null,
    //treeWidth: 220,
    treeHeight: 220,
    mutiSelect: true,
    realDisplay: {},

    // 获得根据真实值获得初始值。新版需要解析value，将真实值解析为真实值和显示值，分别填充到对应元素上.
    initService: 'dataDictionaryService.getKeyValuePair',
    initServiceParam: ['DY_FORM_FIELD_MAPPING'],
    // 是否允许多选
    isinitTreeById: false,
    fieldType: 'ztree'
  };
})(jQuery);
