var CkPlugin = $.extend(
  {
    SUBFORM: 'dysubform',
    DATECTL: 'control4date',
    LABEL: 'control4label',
    FORM: 'dyform',
    PREVIEW: 'dyformpreview',
    TREESELECTCTL: 'control4treeselect',
    NUMBERCTL: 'control4number',
    RADIOCTL: 'control4radio',
    COMBOBOXCTL: 'control4combobox',
    COMBOSELECTCTL: 'control4comboselect',
    CHECKBOXCTL: 'control4checkbox',
    CKEDITORCTL: 'control4ckeditor',
    TEXTCTL: 'control4text',
    TEXTAREACTL: 'control4textarea',
    CKEDITORCTL: 'control4ckeditor',
    CHECKBOXCTL: 'control4checkbox',
    SERIALNUMBERCTL: 'control4serialnumber',
    DIALOGCTL: 'control4dialog',
    MULTIORG: 'control4multiOrg',
    UNITCTL2: 'control4unit2',
    FILEUPLOADCTL: 'control4fileupload',
    FILEUPLOAD4ICONCTL: 'control4fileupload4icon',
    FILEUPLOAD4IMAGECTL: 'control4fileupload4image',
    // VIEWDISPLAYCTL : "control4viewdisplay",
    PROPERTIESDIALOGS: 'propertiesDialog',
    BLOCK: 'block',
    BUTTON: 'control4btn',
    PROPERTIESDIALOGS4MODEL: 'propertiesDialog4model',
    MODELFIELD: 'modelField',
    MODELSUBFORM: 'modelSubform',
    COPYFORM: 'copyForm',
    // CONTROL4TIMEEMPLOY : "control4timeEmploy",
    EMBEDDED: 'control4embedded',
    JOBS: 'control4jobs',
    RATY: 'control4raty',
    TAGS: 'control4tag',
    TIPS: 'control4tip',
    LAYOUT: 'layout',
    TABS: 'layout4tab', // 页签
    TEMPLATECONTAINER: 'templatecontainer',
    IMAGE2: 'image2',
    UPLOADIMAGE: 'uploadimage',
    UPLOADWIDGET: 'uploadwidget',
    FILETOOLS: 'filetools',
    NOTIFICATIONAGGREGATOR: 'notificationaggregator',
    WIDGET: 'widget',
    LINEUTILS: 'lineutils',
    CLIPBOARD: 'clipboard',
    NOTIFICATION: 'notification',
    TABLEVIEWCTL: 'control4tableview',
    CHAINED: 'control4chained',
    TAGGROUP: 'control4taggroup',
    TIPS: 'control4tips',
    COLORS: 'control4color',
    SWITCHS: 'control4switch',
    PROGRESS: 'control4progress',
    SELECTCTL: 'control4select',
    FILELIBRARYCTL: 'control4filelibrary'
  },
  window.CkPlugin
);

var getPluginNameByInputMode = function (inputMode) {};

var ckUtils = {
  removeHintInDesigner: function () {
    var _hintelement = this.document.find('#user__hint');
    if (_hintelement.count() > 0) {
      _hintelement.getItem(0).remove();
    }
  },
  /**
   * 获取指定元素对应的table容器
   *
   * @param focusedDomElement
   * @returns
   */
  getTableContainer: function (focusedDomElement) {
    var pluginContainerDomElement = null;
    if (focusedDomElement.is('table') || focusedDomElement.hasAscendant('table')) {
      if (focusedDomElement.is('table')) {
        pluginContainerDomElement = focusedDomElement;
        return pluginContainerDomElement;
      } else {
        pluginContainerDomElement = this.getTableContainer(focusedDomElement.getParent());
      }
    }
    return pluginContainerDomElement;
  },
  jump2PropertyDialog: function (focusedDom, pluginName, evt) {
    // console.log(pluginName + "===>" + JSON.cStringify(evt));
    this.focusedDom = focusedDom;
    evt.data.dialog = pluginName;
  },

  getPluginName: function (inputMode) {
    if (inputMode == undefined) {
      return null;
    }

    if (inputMode == dyFormInputMode.date) {
      return CkPlugin.DATECTL;
    }

    if (inputMode == dyFormInputMode.treeSelect) {
      return CkPlugin.TREESELECTCTL;
    }

    if (inputMode == dyFormInputMode.number) {
      return CkPlugin.NUMBERCTL;
    }

    if (inputMode == dyFormInputMode.radio) {
      return CkPlugin.RADIOCTL;
    }

    if (inputMode == dyFormInputMode.checkbox) {
      return CkPlugin.CHECKBOXCTL;
    }

    if (inputMode == dyFormInputMode.selectMutilFase) {
      return CkPlugin.COMBOBOXCTL;
    }

    if (inputMode == dyFormInputMode.comboSelect) {
      return CkPlugin.COMBOSELECTCTL;
    }

    if (inputMode == dyFormInputMode.text) {
      return CkPlugin.TEXTCTL;
    }

    if (inputMode == dyFormInputMode.orgSelect2) {
      return CkPlugin.MULTIORG;
    }

    if (inputMode == dyFormInputMode.orgSelect2) {
      return CkPlugin.UNITCTL2;
    }

    if (inputMode == dyFormInputMode.accessory3) {
      return CkPlugin.FILEUPLOADCTL;
    }

    if (inputMode == dyFormInputMode.accessory1) {
      return CkPlugin.FILEUPLOAD4ICONCTL;
    }

    if (inputMode == dyFormInputMode.accessoryImg) {
      return CkPlugin.FILEUPLOAD4IMAGECTL;
    }

    if (inputMode == dyFormInputMode.dialog) {
      return CkPlugin.DIALOGCTL;
    }

    if (inputMode == dyFormInputMode.ckedit) {
      return CkPlugin.CKEDITORCTL;
    }

    if (inputMode == dyFormInputMode.serialNumber || inputMode == dyFormInputMode.unEditSerialNumber) {
      return CkPlugin.SERIALNUMBERCTL;
    }

    if (inputMode == dyFormInputMode.textArea) {
      return CkPlugin.TEXTAREACTL;
    }

    // if (inputMode == dyFormInputMode.timeEmploy) {
    // return CkPlugin.CONTROL4TIMEEMPLOY;
    // }

    if (inputMode == dyFormInputMode.embedded) {
      return CkPlugin.EMBEDDED;
    }

    if (inputMode == dyFormInputMode.job) {
      return CkPlugin.JOBS;
    }

    if (inputMode == dyFormInputMode.chained) {
      return CkPlugin.CHAINED;
    }
    if (inputMode == dyFormInputMode.taggroup) {
      return CkPlugin.TAGGROUP;
    }

    if (inputMode == dyFormInputMode.select) {
      return CkPlugin.SELECTCTL;
    }
    if (inputMode == dyFormInputMode.colors) {
      return CkPlugin.COLORS;
    }
    if (inputMode == dyFormInputMode.switchs) {
      return CkPlugin.SWITCHS;
    }
    if (inputMode == dyFormInputMode.progress) {
      return CkPlugin.PROGRESS;
    }
    if (inputMode == dyFormInputMode.placeholder) {
      return CkPlugin.PLACEHOLDER;
    }
  },

  createPlugin: function (pluginName, pluginTitle, needDialog) {
    // 创建一个插件对象
    var currentPath = this.plugins[pluginName].path;
    this.ui.addButton(pluginName, {
      label: pluginTitle, // 调用dialog时显示的名称
      icon: currentPath + '/images/anchor.jpg', // 在toolbar中的图标
      command: pluginName
    });

    if (needDialog) {
      this.plugins[pluginName].htmlUrl = currentPath + 'dialogs/' + pluginName + '.html';
      CKEDITOR.dialog.add(pluginName, currentPath + 'dialogs/' + pluginName + '.js');
      this.addCommand(pluginName, new CKEDITOR.dialogCommand(pluginName));
    }
  },
  allDoubleClick: function (evt, pluginName, a) {
    var element = evt.data.element; // element是CKEDITOR.dom.node类的对象
    var $element = $(element.$);
    var $pTemplate = $element.parents('.template-wrapper');
    if ($element.hasClass('template-wrapper') === false && $pTemplate.length > 0 && $pTemplate.attr('contenteditable') == 'false') {
      return;
    } else {
      var pluginContainerDomElement = CKEDITOR.getPluginContainerDomElement(pluginName, element);
      if (pluginContainerDomElement != null) {
        a.focusedDom = pluginContainerDomElement;
        evt.data.dialog = pluginName;
      }
    }
  }
};

var dialogMethod = {
  onOk: function () {
    // this.fillCkeditor();
    // this.exitDialog();
    var _editor = this.getParentEditor();
    alert(_editor);
  },
  onCancel: function () {
    // 退出窗口时清空属性窗口的缓存
    // if( (typeof custombtn.exitDialog) != "undefined"){
    // this.exitDialog();
    // /}
    var _editor = this.getParentEditor();
    alert(_editor);
  },

  onShow: function () {
    var _editor = this.getParentEditor();

    // var focusedDomPropertyUrl = editor.focusedDomPropertyUrl4Btn;
    // $("#" + containerID).load(focusedDomPropertyUrl, function(){
    // custombtn.initPropertyDialog(editor);//初始化属性窗口
    // });
  }
};

CKEDITOR.getPluginContainerDomElement = function (plugin, focusedDomElement) {
  var pluginContainerDomElement = null;
  if (plugin == CkPlugin.SUBFORM) {
    // 从表插件
    if (focusedDomElement.is('table') || focusedDomElement.hasAscendant('table')) {
      if (focusedDomElement.is('table')) {
        pluginContainerDomElement = focusedDomElement;
      } else {
        pluginContainerDomElement = CKEDITOR.getPluginContainerDomElement(plugin, focusedDomElement.getParent());
      }
      var formUuid = $(pluginContainerDomElement).attr('formUuid');
      if (formUuid != '' && typeof formUuid != 'undefined') {
        // 只有从表才会设置formUuid的属性
        return pluginContainerDomElement;
      } else {
        return null;
      }
    }
  } else if (
    plugin == CkPlugin.DATECTL || // 日期控件插件
    plugin == CkPlugin.TREESELECTCTL ||
    plugin == CkPlugin.NUMBERCTL ||
    plugin == CkPlugin.RADIOCTL ||
    plugin == CkPlugin.CHECKBOXCTL ||
    plugin == CkPlugin.COMBOBOXCTL ||
    plugin == CkPlugin.COMBOSELECTCTL ||
    plugin == CkPlugin.TEXTCTL ||
    plugin == CkPlugin.MULTIORG ||
    plugin == CkPlugin.UNITCTL2 ||
    plugin == CkPlugin.FILEUPLOADCTL ||
    plugin == CkPlugin.FILEUPLOAD4ICONCTL ||
    plugin == CkPlugin.FILEUPLOAD4IMAGECTL ||
    plugin == CkPlugin.DIALOGCTL ||
    plugin == CkPlugin.CKEDITORCTL ||
    plugin == CkPlugin.SERIALNUMBERCTL ||
    plugin == CkPlugin.TEXTAREACTL ||
    plugin == CkPlugin.EMBEDDED ||
    plugin == CkPlugin.JOBS ||
    plugin == CkPlugin.CHAINED ||
    plugin == CkPlugin.TAGGROUP ||
    plugin == CkPlugin.COLORS ||
    plugin == CkPlugin.SWITCHS ||
    plugin == CkPlugin.PROGRESS ||
    plugin == CkPlugin.SELECTCTL ||
    plugin == CkPlugin.PLACEHOLDER
  ) {
    if (focusedDomElement.is('img')) {
      var imputmode = $(focusedDomElement).attr('inputmode');
      if (isDyFormControl(plugin, imputmode) && $(focusedDomElement).attr('class') == 'value') {
        pluginContainerDomElement = focusedDomElement;
        var fieldName = $(focusedDomElement).attr('name');
        var field = formDefinition.getField(fieldName);
        if (typeof field == 'undefined') {
          field = new MainFormFieldClass();
          field.name = fieldName;
          field.isFirst = true; // 第一次创建
          formDefinition.fields[fieldName] = field;
        }
      }
    }
  } else if (plugin == CkPlugin.LABEL) {
    // label控件插件
    if (focusedDomElement.is('label')) {
      // var parentDomElement = focusedDomElement.getParent();
      if ($(focusedDomElement).attr('class') == 'label') {
        pluginContainerDomElement = focusedDomElement;
      }
    }
  } else if (plugin == CkPlugin.TABLEVIEWCTL) {
    // 视图列表插件
    if (focusedDomElement.is('table') || focusedDomElement.hasAscendant('table')) {
      if (focusedDomElement.is('table')) {
        pluginContainerDomElement = focusedDomElement;
      } else {
        pluginContainerDomElement = CKEDITOR.getPluginContainerDomElement(plugin, focusedDomElement.getParent());
      }
      var tableviewid = $(pluginContainerDomElement).attr('tableviewid');
      if (tableviewid != '' && typeof tableviewid != 'undefined') {
        // 只有视图列表组件才会设置tableviewid的属性
        return pluginContainerDomElement;
      } else {
        return null;
      }
    }
    // if ($(focusedDomElement.$).parents('table').is('table[tableviewid]'))
    // {
    // pluginContainerDomElement = $(focusedDomElement.$).parents('table');
    //
    // }
  } else if (plugin == CkPlugin.BLOCK) {
    // 区块布局插件
    if (focusedDomElement.is('td') || focusedDomElement.is('th')) {
      // var parentDomElement = focusedDomElement.getParent();
      if ($(focusedDomElement).attr('class') == 'title') {
        pluginContainerDomElement = focusedDomElement;
      }
    }
  } else if (plugin == CkPlugin.TABS) {
    // 页签布局插件
    var clazz = $(focusedDomElement).attr('class');

    if (focusedDomElement.is('div') && /\btab-design\b/.test(clazz)) {
      // 页签布局
      pluginContainerDomElement = focusedDomElement;
    }
  } else if (plugin == CkPlugin.BUTTON) {
    // 按钮控件插件
    if (focusedDomElement.is('input')) {
      if ($(focusedDomElement).attr('type') == 'button') {
        pluginContainerDomElement = focusedDomElement;
      }
    }
  } else if (plugin == CkPlugin.MODELFIELD) {
    // 按钮控件插件
    if (focusedDomElement.is('span')) {
      if ($(focusedDomElement).attr('class') == 'model_field') {
        // 显示单据字段占位符
        pluginContainerDomElement = focusedDomElement;
      }
    }
  } else if (plugin == CkPlugin.MODELSUBFORM) {
    // 按钮控件插件
    if (focusedDomElement.is('span')) {
      if ($(focusedDomElement).attr('class') == 'model_subform') {
        // 显示单据字段占位符
        pluginContainerDomElement = focusedDomElement;
      }
    }
  } else if (plugin == CkPlugin.TEMPLATECONTAINER) {
    if (focusedDomElement.is('div')) {
      var clazz = $(focusedDomElement).attr('class');
      if (typeof clazz === 'string' && clazz.indexOf('template-wrapper') > -1) {
        // 显示单据字段占位符
        pluginContainerDomElement = focusedDomElement;
      }
    }
  } else if (plugin == CkPlugin.TIPS) {
    // 区块布局插件
    if (focusedDomElement.is('div')) {
      var parentDomElement = focusedDomElement.getParent();
      var clazz = $(parentDomElement).attr('class');
      if (typeof clazz === 'string' && clazz.indexOf('tips-wrap') > -1) {
        pluginContainerDomElement = parentDomElement;
      }
    }
  } else if (plugin == CkPlugin.FILELIBRARYCTL) {
    // 文件库列表插件
    if (focusedDomElement.is('table') || focusedDomElement.hasAscendant('table')) {
      if (focusedDomElement.is('table')) {
        pluginContainerDomElement = focusedDomElement;
      } else {
        pluginContainerDomElement = CKEDITOR.getPluginContainerDomElement(plugin, focusedDomElement.getParent());
      }
      var filelibraryid = $(pluginContainerDomElement).attr('filelibraryid');
      if (filelibraryid != '' && typeof filelibraryid != 'undefined') {
        // 只有视图列表组件才会设置filelibraryid的属性
        return pluginContainerDomElement;
      } else {
        return null;
      }
    }
  }
  return pluginContainerDomElement;
};

function getWidthAndHeight() {
  if (window.innerWidth) winWidth = window.innerWidth;
  else if (document.body && document.body.clientWidth) winWidth = document.body.clientWidth;
  // 获取窗口高度
  if (window.innerHeight) winHeight = window.innerHeight;
  else if (document.body && document.body.clientHeight) winHeight = document.body.clientHeight;
  // 通过深入 Document 内部对 body 进行检测，获取窗口大小
  if (document.documentElement && document.documentElement.clientHeight && document.documentElement.clientWidth) {
    winHeight = document.documentElement.clientHeight;
    winWidth = document.documentElement.clientWidth;
  }
  return {
    height: winHeight,
    width: winWidth
  };
}

/**
 * 判断是否是表单控件
 *
 * @param plugin
 * @param inputmode
 * @returns {Boolean}
 */
function isDyFormControl(plugin, inputmode) {
  if (ckUtils.getPluginName(inputmode) == plugin) {
    return true;
  } else {
    return false;
  }
}

function addPlugin(pluginName, pluginToolHint, dialogTitle, controlConfig) {
  CKEDITOR.plugins.add(pluginName, {
    requires: ['dialog'],
    init: function (a) {
      // 定义插件对话框
      CKEDITOR.dialog.add(pluginName, getDialog(pluginName, pluginToolHint, dialogTitle, controlConfig));

      // 定义命令，用于打开对话框
      a.addCommand(pluginName, new CKEDITOR.dialogCommand(pluginName));

      // 定义一个按钮,用于触发打开"设置从表"对话框的命令
      a.ui.addButton(pluginName, {
        label: pluginToolHint, // 调用dialog时显示的名称
        command: pluginName,
        icon: CKEDITOR.plugins.basePath + pluginName + '/images/anchor.png' // 在toolbar中的图标
      });

      // 定义双击事件
      a.on('doubleclick', function (evt) {
        ckUtils.allDoubleClick(evt, pluginName, this);
      });
    }
  });
}

function getContainerId(pluginName) {
  var containerID = 'container_' + pluginName;
  return containerID;
}

/**
 * 弹出框信息
 */
function getDialog(pluginName, pluginToolHint, dialogTitle, controlConfig) {
  var fun = function (editor) {
    var containerID = getContainerId(pluginName);
    return {
      title: dialogTitle,
      minHeight: 450,
      minWidth: 900,
      contents: [
        {
          id: 'setcontrolConfig',
          label: 'label',
          title: 'title',
          expand: true,
          padding: 0,
          elements: [
            {
              id: 'table_html',
              type: 'html',
              style: 'width: 100%;',
              html: "<div id='" + containerID + "'>" + dialogTitle + '</div>',
              onLoad: function () {}
            }
          ]
        }
      ],
      onOk: function () {
        if (pluginName === CkPlugin.SUBFORM) {
          var checkpass = controlConfig.collectFormAndFillCkeditor();
          if (!checkpass) {
            return false;
          }
          controlConfig.exitDialog();
        } else {
          var checkpass = $.ControlConfigUtil.ctlEditDialogOnOk(controlConfig, containerID);
          if (!checkpass) {
            return false;
          }
          $('#' + getContainerId()).empty(); // 防止缓存
          // ，出现多个不同的属性弹出框由于缓存，出现重复元素
        }
        if (window.formDefinition) {
          window.formDefinition.updateFormTree();
        }
      },
      onCancel: function () {
        // 退出窗口时清空属性窗口的缓存
        $.ControlConfigUtil.ctlEditDialogOnCancel(controlConfig, containerID);
        $('#' + getContainerId()).empty(); // 防止缓存
        // ，出现多个不同的属性弹出框由于缓存，出现重复元素
      },
      onShow: function (event) {
        var date = new Date();
        if (!CKEDITOR.customCkeditorPath) {
          CKEDITOR.customCkeditorPath = staticPrefix + '/dyform/definition/ckeditor'; // 自定义ckeditor相关配置的路径
        }
        if (pluginName === CkPlugin.SUBFORM) {
          var basePropertyHtmlFile = CKEDITOR.plugins.basePath + pluginName + '/dialogs/property.html?' + date.getTime(); // CKEDITOR.customCkeditorPath在设计器中定义
          $('#' + containerID).load(basePropertyHtmlFile, function (data) {
            controlConfig.initPropertyDialog(editor); // 初始化属性窗口
            $.ControlConfigUtil.enableFocus(editor, CKEDITOR.dialog._.currentTop, containerID);
            if ($.isFunction(event.callback)) {
              event.callback.apply(this, arguments);
            }
            if (editor.focusedDom != null && typeof editor.focusedDom != 'undefined') {
              // 通过双击ckeditor中的从表元素,表示修改从表属性,则不需要做焦点位置判断
              return;
            } else if (editor && editor.getSelection) {
              // 判断光标焦点位置是不是在从表中，若在其他从表中则不允许插入新的从表
              var selection = editor.getSelection();
              var selected_ranges = selection.getRanges(); // getting
              // ranges
              var node = selected_ranges[0].startContainer; // selecting
              // the
              // starting
              // node
              var parents = node.getParents(true);
              var parentsLength = parents.length;
              if (parentsLength > 3) {
                var outestParent = parents[parentsLength - 3]; // 最外层的容器
                if (
                  outestParent.getName() == 'table' &&
                  $(outestParent).attr('formUuid') != '' &&
                  $(outestParent).attr('formUuid') != undefined
                ) {
                  // 如果光标的位置在从表中，这时需要提示用户位置错误，不能在从表的位置插入从表.
                  this.hide();
                  alert('不能在从表的位置插入从表，请将光标移至正确的位置!!');
                }
              }
            }
          });
        } else {
          var basePropertyHtmlFile = CKEDITOR.customCkeditorPath + '/pluginUtils/fields/tab_base_property.html?' + date.getTime(); // CKEDITOR.customCkeditorPath在设计器中定义
          editor.placeHolderImage = CKEDITOR.plugins.basePath + pluginName + '/images/placeHolder.jpg'; // 占位符
          var checkRuleHtmlFile = CKEDITOR.plugins.basePath + pluginName + '/dialogs/tab_check_rule.html?' + date.getTime(); // "检验规则"页签
          var styleHtmlFile = CKEDITOR.plugins.basePath + pluginName + '/dialogs/tab_style.html?' + date.getTime(); // "样式"页签
          var otherHtmlFile = CKEDITOR.plugins.basePath + pluginName + '/dialogs/tab_other.html?' + date.getTime(); // "其他"页签

          $('#' + containerID).load(basePropertyHtmlFile, function () {
            $.when(
              $.get(checkRuleHtmlFile, function (data) {
                var $checkRule = $('#check_rule');
                $checkRule.html(data);

                /<tr/.test(data) ? $('a[href=#check_rule]').closest('li').show() : $('a[href=#check_rule]').closest('li').hide();
              }),
              $.get(styleHtmlFile, function (data) {
                var $style = $('#style');
                $style.html(data);

                /<tr/.test(data) ? $('a[href=#style]').closest('li').show() : $('a[href=#style]').closest('li').hide();
              }),
              $.get(otherHtmlFile, function (data) {
                var $other = $('#other');
                $other.html(data);

                /<tr/.test(data) ? $('a[href=#other]').closest('li').show() : $('a[href=#other]').closest('li').hide();
              })
            ).done(function () {
              controlConfig.initPropertyDialog(editor); // 初始化属性窗口
              $.ControlConfigUtil.enableFocus(editor, CKEDITOR.dialog._.currentTop, containerID);
              if ($.isFunction(event.callback)) {
                event.callback.apply(this, arguments);
              }
            });
          });
        }
        $.ControlConfigUtil.ctlEditDialogonShow(editor, this, pluginToolHint, pluginName);
      }
    };
  };

  return fun;
}
