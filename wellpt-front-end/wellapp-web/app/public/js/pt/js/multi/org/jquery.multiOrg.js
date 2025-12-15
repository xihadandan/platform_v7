var unitDialogCount = 0;
(function (factory) {
  'use strict';
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery', 'server'], factory);
  } else {
    // Browser globals
    factory(jQuery, window.server);
  }
})(function ($, server) {
  'use strict';
  if ($.fn.modal && $.fn.modal.Constructor) {
    $.fn.modal.Constructor.prototype.enforceFocus = function () {
      $(document)
        .off('focusin.bs.modal')
        .on(
          'focusin.bs.modal',
          $.proxy(function (e) {
            if ($(e.target).parents('div.ui-dialog').length > 0) {
              return;
            }
            if (document !== e.target && this.$element[0] !== e.target && !this.$element.has(e.target).length) {
              this.$element.trigger('focus');
            }
          }, this)
        );
    };
  }
  $.unit2 = {
    // 设置参数的默认值
    defaultOptions: {
      title: '选择对象', // 组织弹出框的标题
      labelField: null, //必填参数，可以为空字符串
      valueField: null, //必填参数，可以为空字符串
      multiple: true, // 是否多选，默认为true
      // 下拉框的选择范围，all:全部，MyUnit|我的单位,具体值参考 multiUnitTree.js文件的, 或者参考组织选项里面的配置
      // UNIT_TREE_TYPE 变量的定义
      // 如果显示多个用分号分隔符
      type: null, //参考组织选项里面的配置
      separator: ';', //多选时，数据的分割符
      selectTypes: 'all', //可以选择的类型，all:全部，M:组织，O：单位，D:部门，J：职位，U:账号，G：群组，R：角色，如果有多个则以分号多分割
      callback: null, //点击确认后的回调函数， 传参为 values, labels， 均为数组格式
      excludeValues: [], //数组格式，需要排除的选项,
      unitId: null, //用户的归属单位ID, 非必填，如有指定，则显示该单位的数据，该参数只对我的单位，我的集团,职务群组有效
      orgVersionId: null, //可选，如果设置，则只展示该版本的数据，如果没有设置，则显示最新版本的数据
      isInMyUnit: true, //是否只展示本单位内的数据，不跨单位
      initValues: null, //初始化真实值，数组格式,
      initLabels: null, //初始化显示值，数组格式,与initValues配套使用
      otherParams: {}, //额外参数，主要供给二开使用
      close: null, //弹出框的关闭事件
      valueFormat: 'justId', //all 代表完整格式( V0000000001/U000000001 ), justId : 代表仅组织ID( U0000000001 )
      nameFormat: 'justName', //all 代表完整格式( XXXX/XXXX), justName : 代表仅组织名称( XXXX )
      moreOptList: [], //提供额外的自定义选项列表 [{"id":"XXXX","name":"模块通讯录"}]
      defaultType: '', //默认选中的类型
      viewStyles: null,
      showRole: false // 是否显示角色， type为all的时候做判断，false不显示，true显示，其他时候type有传role则显示
    },
    options: {},
    //检查参数
    checkParams: function (options) {
      if (options.type == null) {
        console.error(' $.unit2.open() 参数 type不能为空');
        return false;
      } else if (null == options.labelField || null == options.valueField) {
        console.error(' $.unit2.open() 参数 labelField 和  valueField 不能为空');
        return false;
      }
      return true;
    },

    isValidOrgId: function (orgId) {
      if (orgId.length != 11) {
        return false;
      }
      var type = orgId.substr(0, 1);
      if ('U' == type) {
        //用户
        return true;
      } else if ('O' == type) {
        //组织节点
        return true;
      } else if ('B' == type) {
        //业务单位
        return true;
      } else if ('D' == type) {
        //部门
        return true;
      } else if ('J' == type) {
        //职位
        return true;
      } else if ('DU' == type) {
        //职务
        return true;
      } else if ('G' == type) {
        //群组
        return true;
      } else if ('E' == type) {
        //业务通讯录
        return true;
      }
      return false;
    },

    computeInitData: function (initValues, initLabels) {
      var data = [];
      if (initValues && initLabels) {
        var values = initValues.split(';');
        var labels = initLabels.split(';');
        for (var i = 0; i < values.length; i++) {
          //检查是否标准格式
          if (values[i].substr(0, 1) == 'V' && values[i].split('/').length > 1) {
            var idAndVersion = values[i].split('/');
            var versionId = idAndVersion[0];
            var id = idAndVersion[1];
            var namePath = labels[i];
            var names = namePath.split('/');
            var node = {
              id: id,
              type: id.substr(0, 1),
              name: names[names.length - 1],
              namePath: namePath,
              orgVersionId: versionId
            };
            data.push(node);
          } else {
            //非标准格式，则采用原值，那只有ID,NAME, 主要是用于邮件功能
            var node = {
              id: values[i],
              name: labels[i],
              namePath: labels[i]
            };
            // 如果是个合法的组织ID,则计算type
            if (this.isValidOrgId(values[i])) {
              node.type = values[i].substr(0, 1);
            }
            data.push(node);
          }
        }
      }
      return data;
    },

    getSmartName: function (ids, labels) {
      var self = this;
      var res = {};
      if (ids instanceof String) {
        ids = ids.split(',');
      }
      var nameDisplayMethod = self.options.nameDisplayMethod || '1';
      server.JDS.restfulPost({
        url: '/proxy/api/org/tree/dialog/smartName',
        async: false,
        contentType: 'application/json',
        dataType: 'json',
        data: {
          nodeIds: ids,
          nodeNames: labels,
          nameDisplayMethod: nameDisplayMethod
        },
        success: function (result) {
          for (var i in result.data) {
            var _data = result.data[i];
            if (!_data || !_data.name) {
              continue;
            }
            if (i[0] === 'U') {
              res[i] = _data.name;
            } else {
              res[i] = (nameDisplayMethod === '2' ? _data.shortName : _data.smartNamePath) || _data.name;
            }
          }
        }
      });
      return res;
    },
    open: function (params) {
      var _self = this;
      // 合并参数到 this.options 中, 以options的配置为准, 供给所有方法共同使用
      this.options = $.extend({}, this.defaultOptions, params);
      //检查参数是否正确
      if (!this.checkParams(this.options)) {
        return;
      }

      var index = unitDialogCount++;
      var unitDialogId = 'unit_dialog_' + index;
      var targetWindow = params.targetWindow ? params.targetWindow : window.top.appModal ? window.top : window;
      var orgUrl = params.v == "7.0" ? "/page/71686723515449344" : "/multi/org/dialog.html";
      var unitDialogUrl = ctx + orgUrl + '?unitId=' + (this.options.unitId || '');
      var iframeHtml = "<iframe id='" + unitDialogId + "' src='" + unitDialogUrl + "' frameborder='0' height='100%' width='100%'></iframe>";
      console.log('multiOrg.js打开组织弹出框-----开始 =' + unitDialogId);

      var unitDialog = targetWindow.$(iframeHtml).dialog({
        title: $.unit2.options.title,
        bgiframe: true,
        autoOpen: true,
        resizable: true,
        stack: true,
        width: 980,
        height: 620,
        modal: true,
        draggable: false, //设置dialog不可以拖动
        overlay: {
          background: '#000',
          opacity: 0.5
        },
        buttons: {
          确定: function () {
            //将选中的值，赋值给对应labelField和valueField字段
            var unitDialogFrame = targetWindow.document.getElementById(unitDialogId);
            var treeNodes = unitDialogFrame.contentWindow.UnitTree.getSelectedData();
            var values = [];
            var labels = [];
            for (var i = 0; i < treeNodes.length; i++) {
              if (treeNodes[i]) {
                //非标准格式的话，则直接保存id
                if (treeNodes[i].orgVersionId) {
                  if ('justId' == $.unit2.options.valueFormat) {
                    values.push(treeNodes[i].id);
                  } else {
                    values.push(treeNodes[i].orgVersionId + '/' + treeNodes[i].id);
                  }
                } else {
                  values.push(treeNodes[i].id);
                }
                if ('justName' == $.unit2.options.nameFormat) {
                  var name = treeNodes[i].name;
                  if (typeof name === 'string' && name.indexOf('/')) {
                    name = name.substr(name.lastIndexOf('/') + 1);
                  }
                  labels.push(name);
                } else {
                  var label = treeNodes[i].namePath || treeNodes[i].name;
                  labels.push(label);
                }
              }
            }
            var smartList = _self.getSmartName(values, labels);
            for (var i in smartList) {
              labels[values.indexOf(i)] = smartList[i];
            }
            $('#' + $.unit2.options.labelField).val(labels.join($.unit2.options.separator));
            $('#' + $.unit2.options.valueField).val(values.join($.unit2.options.separator));
            if ($.unit2.options.callback) {
              $.unit2.options.callback.call(this, values, labels, treeNodes);
            }
            unitDialog.dialog('close');
          },
          取消: function () {
            unitDialog.dialog('close');
          }
        },
        open: function (e) {
          // 为了布局好看，重置了iframe的style属性
          $('.ui-widget-overlay').css('background', '#000');
          $('.ui-widget-overlay').css('opacity', '0.5');
          $(this).css('width', '100%');
          $(this).css('margin-bottom', '-12px');
          $(this).css('padding', '0');
          //var s = $(this).attr("style");
          var $dailog = $(this).parents('.ui-dialog');
          $dailog
            .find('.ui-dialog-buttonset')
            .css({
              'text-align': 'right'
            })
            .find('button')
            .css({
              float: 'none',
              padding: '0 15px'
            });
          if ($.unit2.options.zIndex) {
            $dailog.css('z-index', $.unit2.options.zIndex);
            $dailog.next().css('z-index', $.unit2.options.zIndex - 1);
          }
          /* 关闭按钮开始 */
          $dailog.find('.ui-dialog-titlebar-close').removeClass('ui-button-icon-only').addClass('icon iconfont icon-ptkj-dacha').empty();
          /* 关闭按钮结束 */

          //如果有设置了labelField和initLabel，以valueField的值为主
          var initLabels = $.unit2.options.initLabels;
          if ($('#' + $.unit2.options.labelField).length > 0) {
            initLabels = $('#' + $.unit2.options.labelField).val();
          }

          //如果有设置了valueField和initValues，以valueField的值为主
          var initValues = $.unit2.options.initValues;
          if ($('#' + $.unit2.options.valueField).length > 0) {
            initValues = $('#' + $.unit2.options.valueField).val();
          }
          console.log('multiOrg.js-open  打开组织弹出框-----开始，=' + unitDialogId);
          var initData = $.unit2.computeInitData(initValues, initLabels);
          if (initData.length > 0 && $.isFunction(params.computeInitData)) {
            params.computeInitData(initData);
          }
          //var unitDialogFrame = targetWindow.document.getElementById(unitDialogId);
          var $unitDialogFrame = targetWindow.$('#' + unitDialogId);
          console.log('multiOrg.js-open  unitDialogFrame=' + $unitDialogFrame);
          console.log($unitDialogFrame);
          var unitTreeOptions = {
            multiple: $.unit2.options.multiple,
            type: $.unit2.options.type,
            data: initData,
            separator: $.unit2.options.separator,
            selectTypes: $.unit2.options.selectTypes,
            unitId: $.unit2.options.unitId,
            excludeValues: $.unit2.options.excludeValues,
            orgVersionId: $.unit2.options.orgVersionId,
            isInMyUnit: $.unit2.options.isInMyUnit,
            otherParams: $.unit2.options.otherParams,
            moreOptList: $.unit2.options.moreOptList,
            defaultType: $.unit2.options.defaultType,
            valueFormat: $.unit2.options.valueFormat,
            nameFormat: $.unit2.options.nameFormat,
            viewStyles: $.unit2.options.viewStyles,
            isNeedUser: $.unit2.options.isNeedUser,
            nameDisplayMethod: $.unit2.options.nameDisplayMethod
          };
          $unitDialogFrame.on('load', function () {
            $dailog.find('.ui-dialog-buttonset>button.ui-button').each(function (idx, element) {
              var $element = $(this);
              if ($element.text() === '确定') {
                $unitDialogFrame[0].contentWindow.UnitTree.onSelectedChange = function (num) {
                  $element.html('确定 &nbsp;(' + num + '个)');
                };
                return false;
              }
            });
            $unitDialogFrame[0].contentWindow.UnitTree.setMaskStatus = function (boolean) {
              if (boolean) {
                $dailog.find('.ui-widget-header').css('position', 'relative').append('<div class="UnitTreeMask"></div>');
                $dailog.find('.ui-dialog-buttonpane').css('position', 'relative').append('<div class="UnitTreeMask"></div>');
              } else {
                $dailog.find('.ui-widget-header .UnitTreeMask').remove();
                $dailog.find('.ui-dialog-buttonpane .UnitTreeMask').remove();
              }
            };
            $unitDialogFrame[0].contentWindow.appModal.showSimpleLoading('', $($unitDialogFrame[0].contentWindow.document.body));
            window.setTimeout(function () {
              console.log('multiOrg.js-iframeOnload 打开组织弹出框-----开始，=' + unitDialogId);
              $unitDialogFrame[0].contentWindow.UnitTree.init(unitTreeOptions);
              console.log('multiOrg.js-iframeOnload 打开组织弹出框-----结束，=' + unitDialogId);
              $unitDialogFrame[0].contentWindow.appModal.hideSimpleLoading();
            }, 100);
          });
          console.log('multiOrg.js-open 打开组织弹出框-----结束');
        },
        close: function () {
          console.log('multiOrg.js-close 关闭组织弹出框-----开始');
          if ($.unit2.options.close) {
            $.unit2.options.close.call(this);
          }
          targetWindow.$('#' + unitDialogId).remove();
          console.log('multiOrg.js-close 关闭组织弹出框-----结束');
          return true;
        }
      });
      console.log('multiOrg.js打开组织弹出框-----结束，=' + unitDialogId);
      unitDialog.parent().draggable().css({ //使用draggable，支持拖动出屏幕
        "position": "fixed"
      });
      return unitDialog;
    }
  };

  var orgStyles = {
    orgStyle1: 'org-style1',
    orgStyle2: 'org-style2',
    orgStyle3: 'org-style3'
  };
  var OrgSelect = function (element, options) {
    var self = this;
    // 差异化处理
    options = options || {};
    options.icon = options.icon || 'icon iconfont icon-ptkj-zuzhixuanze';
    options.trigger = options.trigger || 'manual'; // click、focus
    var $element = $(element);
    self.valObj = {};
    self.options = options;
    self.$element = $element;
    // 拷贝class和style
    self.$container = $('<ul class="org-select"></ul>');
    self.$styleElem = $('<div class="org-select-container" tabindex="0"></div>');
    self.$styleElem.append(self.$container);
    self.$styleElem.append('<i class="icon ' + options.icon + '"></i>');
    self.$styleElem.addClass(self.$element.attr('class'));
    self.$styleElem.attr('style', self.$element.attr('style'));
    $element.after(self.$styleElem).hide();
    self.bindEvent();
    self.switchStyle(options.orgStyle); // 设置样式
    self.initValue(options.orgOptions);
  };

  $.extend(OrgSelect.prototype, {
    initValue: function (options) {
      if (!options) {
        // orgOptions
        return;
      }
      var self = this;
      var $initLabels = $('#' + options.labelField);
      if (!options.initLabels && $initLabels.length > 0) {
        options.initLabels = $initLabels.val();
      }
      var $initValues = $('#' + options.valueField);
      if (!options.initValues && $initValues.length > 0) {
        options.initValues = $initValues.val();
      }
      if (options.initValues && options.initValues.length > 0) {
        self.$element.attr('hiddenvalue', options.initValues).trigger('change.orgSelect');
      }
    },
    bindEvent: function () {
      var self = this;
      var options = self.options;
      self.$element.on(options.trigger + '.orgSelect', function (event) {
        // self.$styleElem.trigger(options.trigger + ".orgSelect", event);
      });
      self.$styleElem.on(options.trigger + '.orgSelect', function (event) {
        var $styleElem = self.$styleElem;
        if ($styleElem.attr('readonly') === 'readonly') {
          return;
        }
        var orgOptions = options.orgOptions;
        if ($.isFunction(orgOptions)) {
          orgOptions = orgOptions.apply(self, arguments);
        }
        if ($.isPlainObject(orgOptions)) {
          self.$element.attr('disabled', 'disabled');
          self.open(orgOptions);
          self.$element.removeAttr('disabled');
        }
      });
      self.$element.on('change.orgSelect', function (event) {
        var $this = $(this);
        var values = $this.attr('hiddenvalue'),
          labels = $this.val();
        if (options.orgOptions && options.orgOptions.valueField) {
          values = $('#' + options.orgOptions.valueField).val();
        }
        self.setValue(values, labels);
      });
    },
    show: function () {
      var self = this;
      self.$styleElem.show();
    },
    hide: function () {
      var self = this;
      self.$styleElem.hide();
    },
    open: function (options) {
      var self = this;
      // merge default options
      options = $.extend({}, options); // 拷贝,防止原始的callback被修改
      var callback = options.callback;
      options.callback = function (values, labels, treeNodes) {
        $.isFunction(callback) && callback.apply(this, arguments);
        self.setValue(values, labels);
      };
      return $.unit2.open(options);
    },
    switchStyle: function (style) {
      var self = this;
      if (style === self.orgStyle) {
        return false;
      }
      self.$styleElem.removeClass(self.orgStyle);
      self.$styleElem.addClass((self.orgStyle = style));
    },
    getValue: function () {
      var self = this;
      return self.valObj.values;
    },
    getObject: function () {
      var self = this;
      return self.valObj;
    },
    setValue: function (values, labels) {
      var self = this;
      var group = {};
      self.$container.empty();
      if (typeof values === 'string' && $.trim(values).length > 0) {
        values = values.split(';');
        labels = (labels || self.getDisplayValue(values)).split(';');
      }
      if ($.isArray(values) && values.length > 0) {
        labels = labels || self.getDisplayValue(values).split(';');
        for (var i = 0; i < values.length; i++) {
          var vals = values[i].split('/');
          var tag = vals[vals.length - 1].charAt(0);
          group[tag] = group[tag] || [];
          group[tag].push(labels[i]);
        }
        $.each(group, function (tag, value) {
          $.each(value, function (idx, userName) {
            if (!userName) {
              return;
            }
            var $entity = $('<li class="org-entity"></li>').addClass(tag);
            // $entity.append($("<i class=\"org-icon\">"));
            $entity.append($('<span class="org-label">').append(userName));
            self.$container.append($entity);
          });
        });
      } else {
        (values = []), (labels = []);
      }
      self.valObj.values = values;
      self.valObj.labels = labels;
    },
    getDisplayValue: function (values) {
      var self = this;
      var key = 'vcache_' + values;
      var nameDisplayMethod = self.options.nameDisplayMethod || '1';
      if (!self.options[key]) {
        $.ajax({
          type: 'POST',
          url: ctx + '/proxy/api/org/tree/dialog/smartName',
          contentType: 'application/json',
          dataType: 'json',
          async: false,
          data: JSON.stringify({
            nameDisplayMethod: nameDisplayMethod,
            nodeIds: values
          }),
          success: function (result) {
            var res = {};
            for (var i in result.data) {
              var _data = result.data[i];
              if (i[0] === 'U') {
                res[i] = _data.name;
              } else {
                res[i] = (nameDisplayMethod === '2' ? _data.shortName : _data.smartNamePath) || _data.namePath;
              }
            }
            self.options[key] = res;
          }
        });
      }
      var displayValues = [];
      if (self.options[key]) {
        var opt = self.options[key];
        for (var i = 0; i < values.length; i++) {
          displayValues.push(opt[values[i]]);
        }
      }
      return displayValues.join(';');
    },
    setReadonly: function (readonly) {
      var self = this;
      if (readonly) {
        self.$styleElem.attr('readonly', 'readonly');
      } else {
        self.$styleElem.removeAttr('readonly');
      }
    },
    destory: function () {
      var self = this;
      self.$element.off('.orgSelect').removeData('bs.orgSelect');
      self.$styleElem.off('.orgSelect').remode();
      self.$element = null;
      self.$container = null;
      self.$styleElem = null;
    }
  });
  /**
   * $.unit2.orgStyles支持三种样式。不在三种范围内，默认为全部“;”分割
   *
   *
   * $("orgSelectElement").orgSelect({
   * 		trigger : "click|foucs",
   * 		orgStyle : $.unit2.orgStyles.orgStyle1,
   * 		orgOptions : Object orgOptions | function return orgOptions
   * });
   *
   * method:open(options)|setValue(values[,labels])|getValue|show|hide|destory
   * event:change
   */
  $.fn.orgSelect = function (options) {
    var $this = $(this);
    var data = $this.data('bs.orgSelect');
    if (!data && typeof options === 'object') {
      // 默认使用样式3
      options.orgStyle = options.orgStyle || $.unit2.orgStyles.orgStyle3;
      $this.data('bs.orgSelect', (data = new OrgSelect(this, options)));
    } else if (typeof options == 'string') {
      return data[options].apply(data, $.makeArray(arguments).slice(1));
    }
  };
  $.fn.orgSelect.Constructor = OrgSelect;

  // 展现样式
  $.unit2.orgStyles = orgStyles;
  return $.unit2;
});
