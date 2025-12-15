(function (factory) {
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define([], factory);
  } else {
    // Browser globals
    factory();
  }
})(function () {
  var formDefinitionMethod = {
    deleteField: function (fieldName) {
      var field = this.getField(fieldName);
      if (typeof field == 'undefined') {
        // alert(fieldName + " 字段不存在");
        console.log(fieldName + ' 字段不存在');
        return;
      }
      $(document.body).trigger('field-delete', fieldName);
      if (typeof field.oldName == 'undefined') {
        // 新建的字段，不需要上传到后台进行操作
        this.deleteFieldDirectly(fieldName);
        return;
      }

      this.deleteFieldButPersistOperate(fieldName); //
    },
    deleteFieldButPersistOperate: function (fieldName) {
      if (typeof this.deletedFieldNames == 'undefined') {
        this.deletedFieldNames = [];
      }

      this.deletedFieldNames.push(fieldName);

      this.deleteFieldDirectly(fieldName);
    },

    deleteFieldDirectly: function (fieldName) {
      delete this.fields[fieldName];
    },
    getSubform: function (formUuid) {
      var self = this;
      var subform = self.subforms[formUuid];
      if (subform == null || subform.refMerged === true) {
      } else if (subform.refObj != null) {
        // 引用配置可以被覆盖
        var refObj = $.extend({}, subform.refObj, subform);
        $.extend(subform, refObj, {
          refMerged: true
        });
      }
      return subform;
    },
    addSubform: function (formUuid, subform) {
      var self = this;
      var oSubform = self.subforms[formUuid];
      var ignoreFieldsName = ['name', 'inputMode', 'displayName'];
      if (oSubform && oSubform.refObj && $.trim(oSubform.refId).length > 0) {
        var refId = oSubform.refId,
          refObj = oSubform.refObj,
          formObj = {};
        // 删除refObj中已经有的属性
        delete subform.refMerged;
        for (var name in subform) {
          var vt = subform[name];
          if ($.inArray(name, ignoreFieldsName) === -1) {
            var v = refObj[name];
            if (v == vt || JSON.stringify(v) == JSON.stringify(vt)) {
              continue; // 忽略refObj相同的属性（配置时确认）
            }
          }
          // 配置删掉的属性（重新收集没有对应的属性）设值null,这样在jquery.extend中就可以覆盖,因为null
          // === undefined为false
          formObj[name] = vt === undefined ? null : vt;
        }
        formObj.refId = refId;
        formObj.refObj = refObj;
        self.subforms[formUuid] = formObj;
      } else {
        self.subforms[formUuid] = subform;
      }
    },
    deleteSubform: function (formUuid) {
      var subform = this.subforms[formUuid];
      if (typeof subform == 'undefined') {
        console.log(formUuid + ' 从表不存在');
        return;
      }
      delete this.subforms[formUuid];
    },
    deleteTableView: function (tableViewId) {
      var tableView = this.tableView[tableViewId];
      if (typeof tableView == 'undefined') {
        console.log(tableViewId + ' 视图列表不存在');
        return;
      }
      delete this.tableView[tableViewId];
    },
    mergeRefFields: function () {
      var fields = self.fields;
      for (var fieldName in fields) {
        var field = self.getField(fieldName);
      }
      return self.fields;
    },
    getField: function (fieldName) {
      var self = this;
      var field = self.fields[fieldName];
      if (field == null || field.refMerged === true) {
      } else if (field.refObj != null) {
        // 引用配置可以被覆盖
        var refObj = $.extend({}, field.refObj, field);
        $.extend(field, refObj, {
          refMerged: true
        });
      }
      return field;
    },
    addField: function (fieldName, fieldClzzObj) {
      var self = this;
      if (typeof self.deletedFieldNames == 'undefined') {
        self.deletedFieldNames = [];
      }

      for (var i = 0; i < self.deletedFieldNames.length; i++) {
        if (self.deletedFieldNames[i] == fieldName) {
          self.deletedFieldNames.splice(0, 1);
        }
      }
      var oField = self.fields[fieldName];
      var ignoreFieldsName = ['name', 'inputMode', 'displayName'];
      if (oField && oField.refObj && $.trim(oField.refId).length > 0) {
        var refId = oField.refId,
          refObj = oField.refObj,
          fieldObj = {};
        // 删除refObj中已经有的属性
        delete fieldClzzObj.refMerged;
        for (var name in fieldClzzObj) {
          var vt = fieldClzzObj[name];
          if ($.inArray(name, ignoreFieldsName) === -1) {
            var v = refObj[name];
            if (v == vt || JSON.stringify(v) == JSON.stringify(vt)) {
              continue; // 忽略refObj相同的属性（配置时确认）
            }
          }
          // 配置删掉的属性（重新收集没有对应的属性）设值null,这样在jquery.extend中就可以覆盖,因为null
          // === undefined为false
          fieldObj[name] = vt === undefined ? null : vt;
        }
        fieldObj.refId = refId;
        fieldObj.refObj = refObj;
        self.fields[fieldName] = fieldObj;
      } else {
        if (this.formType === 'C' && !oField) {
          fieldClzzObj.product_field = 'extend';
        }
        self.fields[fieldName] = fieldClzzObj;
      }
    },
    hasField: function (fieldName) {
      var field = this.fields[fieldName];
      return !(typeof field == 'undefined' || field == null);
    },
    hasDatabaseField: function (fieldName) {
      return this.databaseFields && !(typeof this.databaseFields[fieldName] == 'undefined' || this.databaseFields[fieldName] == null);
    },
    deleteTemplate: function (templateId) {
      var templateFormDefinition = loadFormDefinition(templateId);
      var fields = templateFormDefinition.fields;
      for (var fieldName in fields) {
        var field = this.getField(fieldName);
        if (field && field.master) {
          this.deleteField(fieldName);
        }
      }
      delete this.templates[templateId];
    },
    addTemplate: function (templateId, templateObj) {
      var templateFormDefinition = loadFormDefinition(templateId);
      var templateFields = templateFormDefinition.fields;
      var notAddFields = {};
      for (var fieldName in templateFields) {
        var field = this.getField(fieldName);
        var templateField = templateFields[fieldName];
        if (!field || field.hasOwnProperty('master')) {
          // 是否脱离模板
          templateField.master = templateObj.templateFlag ? false : true;
          this.addField(fieldName, templateField);
        } else {
          notAddFields[fieldName] = templateField;
        }
      }
      this.templates[templateId] = templateObj;
      return notAddFields;
    },
    hasTemplate: function (templateId) {
      var templates = this.templates || {};
      for (var key in templates) {
        if (templates[key].templateId === templateId) {
          return true;
        }
      }
      return false;
    },

    //脱离子表单
    templateToBlock: function (templateId, templateObj) {
      var templateFormDefinition = loadFormDefinition(templateId);
      $.extend(formDefinition.blocks, templateFormDefinition.blocks);
    },

    /**
     * 判断字段是否为附件字段
     *
     * @param fieldName
     * @returns {Boolean}
     */
    isInputModeAsAttach: function (fieldName) {
      var field = this.getField(fieldName);
      if (typeof field == 'undefined') {
        return false;
      }
      var inputMode = field.inputMode;
      return this.isAttach(inputMode);
    },
    isAttach: function (inputMode) {
      if (this.isOcxAttach(inputMode)) {
        return true;
      }
      if (inputMode == dyFormInputMode.accessoryImg || inputMode == dyFormInputMode.accessory3) {
        return true;
      } else {
        return false;
      }
    },

    isOcxAttach: function (inputMode) {
      if (inputMode == dyFormInputMode.accessory1) {
        return true;
      } else {
        return false;
      }
    },
    isValueAsMap: function (fieldName) {
      if (typeof fieldName == 'undefined') {
        return false;
      }
      var inputType = this.getField(fieldName).inputMode;
      return this.isInputModeAsValueMap(inputType);
    },
    isInputModeAsValueMap: function (inputMode) {
      if (typeof inputMode == 'undefined') {
        return false;
      }

      if (
        dyFormInputMode.orgSelect == inputMode ||
        dyFormInputMode.orgSelectStaff == inputMode ||
        dyFormInputMode.orgSelectDepartment == inputMode ||
        dyFormInputMode.orgSelectStaDep == inputMode ||
        dyFormInputMode.orgSelectAddress == inputMode ||
        dyFormInputMode.orgSelectGroup == inputMode ||
        dyFormInputMode.text == inputMode ||
        dyFormInputMode.treeSelect == inputMode ||
        dyFormInputMode.radio == inputMode ||
        dyFormInputMode.checkbox == inputMode ||
        dyFormInputMode.selectMutilFase == inputMode ||
        dyFormInputMode.comboSelect == inputMode ||
        dyFormInputMode.job == inputMode ||
        dyFormInputMode.select == inputMode ||
        dyFormOrgSelectType.orgSelectJob == inputMode ||
        dyFormOrgSelectType.orgSelectPublicGroup == inputMode ||
        dyFormOrgSelectType.orgSelectPublicGroupSta == inputMode ||
        dyFormOrgSelectType.orgSelectMyDept == inputMode ||
        dyFormOrgSelectType.orgSelectMyParentDept == inputMode ||
        dyFormOrgSelectType.orgSelectMyUnit == inputMode ||
        dyFormInputMode.orgSelect2 == inputMode ||
        dyFormInputMode.chained == inputMode ||
        dyFormInputMode.taggroup == inputMode ||
        dyFormInputMode.colors == inputMode ||
        dyFormInputMode.placeholder == inputMode
      ) {
        return true;
      }
      return false;
    },

    isCustomField: function (fieldName) {
      if (typeof fieldName == 'undefined') {
        return false;
      }
      var inputType = this.getField(fieldName).sysType;
      if (inputType != dyFieldSysType.custom) {
        // 系统性字段不提供给外部使用
        return false;
      } else {
        return true;
      }
    },
    isInputModeAsText: function (inputMode) {
      if (typeof inputMode == 'undefined') {
        return false;
      }
      if (dyFormInputMode.text == inputMode) {
        return true;
      }
      return false;
    },
    isInputModeAsTextArea: function (inputMode) {
      if (typeof inputMode == 'undefined') {
        return false;
      }
      if (dyFormInputMode.textArea == inputMode) {
        return true;
      }
      return false;
    },
    isSpanPlaceholder: function (inputMode) {
      if (typeof inputMode == 'undefined') {
        return false;
      }
      if (dyFormInputMode.placeholder == inputMode) {
        return true;
      }
      return false;
    },
    isDateControl: function (inputMode) {
      if (typeof inputMode == 'undefined') {
        return false;
      }
      if (dyFormInputMode.date == inputMode) {
        return true;
      }
      return false;
    },
    /**
     *
     * @param desc
     *            true:降序, false:升序
     */
    sortFieldsOfSubform: function (p, desc) {
      if (typeof p == 'string') {
        var fields = this.subforms[p].fields;
      } else {
        var fields = p;
      }

      var fieldsArray = [];
      for (var i in fields) {
        // console.log(fields[i].order);
        fieldsArray.push(fields[i]);
      }
      var ret = sortASCBy(fieldsArray, 'order');
      var fieldsOfBeSored = {};
      for (var i = 0; i < ret.length; i++) {
        fieldsOfBeSored[ret[i].name] = ret[i];
      }
      return fieldsOfBeSored;
    },

    setFieldOptionSet: function (fieldName, optionSet) {
      if (this.isValueAsMap(fieldName)) {
        this.getField(fieldName).optionSet = optionSet;
        this.getField(fieldName).optionDataSource = dyDataSourceType.custom;
      } else {
        throw new Error('fieldName[' + fieldName + '] is not a option control ');
      }
    },
    getFormDataOfMainform: function () {
      var self = this;
      var formDatas = self.formDatas;
      var maiFormUuid = self.formUuid;
      if (maiFormUuid && formDatas[maiFormUuid]) {
        return formDatas[maiFormUuid][0];
      }
    },

    _generateTabNode: function ($el) {
      var node = {};
      node.nodeType = 'tab';
      var iconUrl = '/static/js/pt/img/ptkj-tab.png';
      node.icon = iconUrl;
      node.children = [];
      node.isContainer = true;

      // 页签
      var tabName = $el.attr('name');
      node.tabName = tabName;
      node.symbol = tabName;

      var tab = this.layouts && this.layouts[tabName];
      if (!tab) {
        // 子表中
        var templateUuid = $el.closest('div.template-wrapper[templateUuid]').attr('templateUuid');
        if (templateUuid && window._templateDefinitionCache[templateUuid]) {
          tab = window._templateDefinitionCache[templateUuid].layouts[tabName];
        }
      }
      if (!tab) {
        console.error && console.error('formTree没有找到页签[' + tabName + ']');
        return;
      }
      node.name = tab.displayName;
      var $subTabs = $el.children('.subtab-design');
      $.each($subTabs, function (idx, subTab) {
        var $subTab = $(subTab);
        var subTabName = $subTab.attr('name');
        var subTab = tab.subtabs[subTabName];
        var child = {
          name: subTab.displayName,
          subTabName: subTabName,
          symbol: subTabName,
          children: [],
          icon: iconUrl,
          nodeType: 'subTab',
          isLayout: true
        };
        node.children.push(child);
      });

      return node;
    },

    _generateTemplateNode: function ($el) {
      var node = {};
      node.nodeType = 'template';
      node.children = [];

      var templateUuid = $el.attr('templateUuid');

      var templateDefinition = loadFormDefinition(templateUuid);
      if (templateDefinition) {
        if (!window._templateDefinitionCache) {
          window._templateDefinitionCache = {};
        }
        window._templateDefinitionCache[templateUuid] = templateDefinition;
      }

      node.templateUuid = templateUuid;

      var template = this.templates && this.templates[templateUuid];
      if (template) {
        // 主表中
        node.name = template.templateName;
        if (template.fields) {
          var isCheckRuleInArray = function (rule, checkRules) {
            if (checkRules.length == 0) {
              return false;
            }
            for (var i = 0; i < checkRules.length; i++) {
              if (rule.value == checkRules[i].value) {
                return true;
              }
            }
            return false;
          };
          // 合并子表单设置的属性到表单定义中
          var templateFields = templateDefinition.fields;
          $.each(template.fields, function (fieldName) {
            var field = template.fields[fieldName];
            var templateField = templateFields[fieldName];
            if (field.showType) {
              templateField.showType = field.showType;
            }
            if (field.fieldCheckRules) {
              var templateFieldCRs = templateField.fieldCheckRules;
              $.each(field.fieldCheckRules, function (j, checkRule) {
                if (!isCheckRuleInArray(checkRule, templateFieldCRs)) {
                  templateFieldCRs.push(checkRule);
                }
              });
            }
          });
        }
      } else {
        // 子表单中
        node.name = $el.attr('templateName');
      }

      return node;
    },

    _generateSubformNode: function ($el) {
      var node = {};
      node.nodeType = 'subform';
      node.children = [];

      var subformUuid = $el.attr('formUuid');
      node.subformUuid = subformUuid;
      node.symbol = subformUuid;

      var subform = this.subforms && this.subforms[subformUuid];
      if (!subform) {
        // 子表中
        var templateUuid = $el.closest('div.template-wrapper[templateUuid]').attr('templateUuid');
        if (templateUuid && window._templateDefinitionCache[templateUuid]) {
          subform = window._templateDefinitionCache[templateUuid].subforms[subformUuid];
        }
      }
      if (!subform) {
        console.error && console.error('formTree没有找到从表[' + subformUuid + ']');
        return;
      }
      node.name = subform.displayName;

      for (var fieldName in subform.fields) {
        var field = subform.fields[fieldName];
        // 显示隐藏的列
        node.children.push({ name: field.displayName, order: field.order, fieldName: fieldName });
        // 不显示隐藏的列
        // node.children.push({ name: field.displayName, isHidden: field.hidden === '2', order: field.order });
      }
      node.children.sort(function (a, b) {
        return a.order - b.order;
      });

      return node;
    },

    _generateTableViewNode: function ($el) {
      var node = {};
      node.nodeType = 'tableView';
      node.type = 'component';
      node.children = [];

      var tableViewId = $el.attr('tableViewId');
      node.tableViewId = tableViewId;

      var tableView = this.tableView && this.tableView[tableViewId];
      if (!tableView) {
        // 子表中
        var templateUuid = $el.closest('div.template-wrapper[templateUuid]').attr('templateUuid');
        if (templateUuid && window._templateDefinitionCache[templateUuid]) {
          tableView = window._templateDefinitionCache[templateUuid].tableView[tableViewId];
        }
      }
      if (!tableView) {
        console.error && console.error('formTree没有找到视图列表[' + tableViewId + ']');
        return;
      }
      node.name = tableView.relaTableViewText;
      for (var i = 0; i < tableView.columns.length; i++) {
        var col = tableView.columns[i];
        var childNode = {};
        childNode.name = col.header;
        childNode.uuid = col.uuid;
        childNode.isHidden = col.hidden === '1';
        node.children.push(childNode);
      }

      return node;
    },

    _generateFileLibraryNode: function ($el) {
      var node = {};
      node.nodeType = 'fileLibrary';
      node.children = [];

      var fileLibraryId = $el.attr('fileLibraryId');
      node.fileLibraryId = fileLibraryId;

      var fileLibrary = this.fileLibrary && this.fileLibrary[fileLibraryId];

      if (!fileLibrary) {
        // 子表中
        var templateUuid = $el.closest('div.template-wrapper[templateUuid]').attr('templateUuid');
        if (templateUuid && window._templateDefinitionCache[templateUuid]) {
          fileLibrary = window._templateDefinitionCache[templateUuid].fileLibrary[fileLibraryId];
        }
      }
      if (!fileLibrary) {
        console.error && console.error('formTree没有找到文件夹目录维护组件[' + fileLibraryId + ']');
        return;
      }
      node.name = fileLibrary.relaFileLibraryText;
      for (var i = 0; i < fileLibrary.columns.length; i++) {
        var col = fileLibrary.columns[i];
        var childNode = {};
        childNode.name = col.header;
        childNode.uuid = col.uuid;
        childNode.isHidden = col.hidden === '1';
        node.children.push(childNode);
      }

      return node;
    },

    _getIconUrlByNodeType: function (nodeType) {
      switch (nodeType) {
        case 'block':
          return '/static/js/pt/img/ptkj-block.png';
        case 'tab':
        case 'subTab':
          return '/static/js/pt/img/ptkj-tab.png';
        case 'table':
          return '/static/js/pt/img/ptkj-table.png';
        default:
          return '';
      }
    },

    _generateContainerNode: function ($el) {
      var _this = this;
      var node = {};
      node.isContainer = true;
      node.isGeneratedByNewContainer = true;
      node.children = [];
      node.nodeType = $el.attr('type');
      node.symbol = $el.attr('symbol');
      node.name = node.symbol;

      var icon = _this._getIconUrlByNodeType(node.nodeType);
      if (icon) {
        node.icon = icon;
      }

      // 只取一个层级的 layout, 不取 layout 下面的 layout
      $el.find('> .layout, :not(.layout) .layout').map(function (idx, layout) {
        var $layout = $(layout);
        if ($layout.attr('tree-ignore')) {
          return;
        }

        var childNode = {};
        childNode.children = [];
        childNode.isLayout = true;
        childNode.nodeType = $layout.attr('type');
        childNode.symbol = $layout.attr('symbol');
        childNode.name = childNode.symbol;

        var icon = _this._getIconUrlByNodeType(childNode.nodeType);
        if (icon) {
          childNode.icon = icon;
        }

        node.children.push(childNode);
      });

      if (!node.children.length) {
        node.isLayout = true;
      }

      return node;
    },

    _generateBlockNode: function ($el) {
      var node = {};
      node.nodeType = 'block';
      node.icon = '/static/js/pt/img/ptkj-block.png';
      node.children = [];
      node.isLayout = true;
      node.isContainer = true;

      var blockName = $el.find('[blockCode]:first').attr('blockCode');
      node.blockCode = blockName;
      node.symbol = blockName;

      var block = this.blocks && this.blocks[blockName];

      if (!block) {
        // 子表中
        var templateUuid = $el.closest('div.template-wrapper[templateUuid]').attr('templateUuid');
        if (templateUuid && window._templateDefinitionCache[templateUuid]) {
          block = window._templateDefinitionCache[templateUuid].blocks[blockName];
        }
      }
      if (!block) {
        console.error && console.error('formTree没有找到区块[' + blockName + ']');
        return;
      }
      node.name = block.blockTitle;
      node.hide = !!block.hide;

      return node;
    },

    _generateTableNode: function ($el) {
      var node = {};
      node.nodeType = 'table';
      node.icon = '/static/js/pt/img/ptkj-table.png';
      node.children = [];

      node.name = $el.children('caption').text();

      return node;
    },

    _generateFieldNode: function ($el) {
      var node = {};
      node.nodeType = 'field';
      node.children = [];

      var fieldName = $el.attr('name');
      var inputMode = $el.attr('inputMode');
      node.inputMode = inputMode;

      var field = this.fields[fieldName];

      // 真实值占位符
      if (inputMode == 130) {
        field = this.placeholderCtr && this.placeholderCtr[fieldName];
      }

      node.name = field ? field.displayName : '';
      node.fieldName = fieldName;

      return node;
    },

    _generateElementNode: function ($el) {
      if ($el.hasClass('container')) {
        // 节点: 新版容器布局
        return this._generateContainerNode($el);
      }

      if ($el.is('div.tab-design')) {
        // 节点: 页签
        return this._generateTabNode($el);
      }

      if ($el.is('div.template-wrapper[templateUuid]')) {
        // 节点: 子表单
        return this._generateTemplateNode($el);
      }

      if ($el.is('table[formuuid]')) {
        // 节点: 从表
        return this._generateSubformNode($el);
      }

      if ($el.is('table[tableViewId]')) {
        // 节点: 视图列表
        return this._generateTableViewNode($el);
      }

      if ($el.is('table[fileLibraryId]')) {
        // 节点: 文件夹目录维护组件
        return this._generateFileLibraryNode($el);
      }

      if ($el.is('table')) {
        if ($el.find('> tbody > tr > td[blockcode]').length) {
          // 节点: 区块(旧版)
          return this._generateBlockNode($el);
        }

        // 节点: 表格
        return this._generateTableNode($el);
      }

      if ($el.is('[inputMode]')) {
        // 节点: 字段
        return this._generateFieldNode($el);
      }

      // 节点: 无效节点
      return null;
    },

    updateFormTree: function () {
      this.formTree = this.getFormTree();
    },

    // 表单层级结构
    getFormTree: function () {
      var _this = this;
      if (window.editor) {
        _this.html = window.editor.getData();
      }

      var $html = $(_this.html);
      var nodes = [];

      function mapDomTree($html, nodes) {
        $html.map(function (idx, item) {
          var $item = $(item);
          var node = _this._generateElementNode($item);
          if (node) {
            nodes.push(node);

            // 使用新布局组件创建生成的
            if (node.isGeneratedByNewContainer) {
              if (node.children.length > 0) {
                for (var i = 0; i < node.children.length; i++) {
                  var layout = node.children[i];
                  var symbol = layout.symbol;

                  // prettier-ignore
                  var selector=
                    '.layout[symbol=' + symbol + '][container=' + node.symbol + '].content, ' +
                    '.layout[symbol=' + symbol + '][container=' + node.symbol + '] .content[container=' + node.symbol + ']'

                  var $children = $item.find(selector).children();
                  mapDomTree($children, layout.children);
                }
              } else {
                var symbol = node.symbol;

                // prettier-ignore
                var selector=
                  '.layout[symbol=' + symbol + '][container=' + symbol + '].content, ' +
                  '.layout[symbol=' + symbol + '][container=' + symbol + '] .content[container=' + symbol + ']'

                mapDomTree($item.find(selector).children(), node.children);
              }
              return;
            }

            // 使用旧布局组件创建的区块或者
            if ($item.is('table')) {
              var $children = $item.find('> tbody > tr > td').children();
              mapDomTree($children, node.children);

              // 取消表格布局
              if (node.nodeType === 'table') {
                var tableNode = nodes.pop();
                for (var i = 0; i < tableNode.children.length; i++) {
                  nodes.push(tableNode.children[i]);
                }
              }

              return;
            }

            if (node.nodeType === 'template') {
              var $children = $item.find('> .inner-box').children();
              mapDomTree($children, node.children);
              return;
            }

            if (node.nodeType === 'tab') {
              for (var i = 0; i < node.children.length; i++) {
                var subTab = node.children[i];
                var subTabName = subTab.subTabName;
                var $children = $item.children('.subtab-design[name=' + subTabName + ']').children();
                mapDomTree($children, subTab.children);
              }
              return;
            }
          } else {
            var $children = $item.children();
            if ($children.length) {
              mapDomTree($children, nodes);
            }
          }
        });
      }

      mapDomTree($html, nodes);

      console.log('FormTree: ', nodes);
      return nodes;
    },

    updateReverseFormTree: function (ns) {
      // if (!this.formTree) {
      //   this.updateFormTree();
      // }
      this.updateFormTree();

      if (!window.reverseFieldTree) {
        window.reverseFieldTree = {};
        window.reverseSubformTree = {};
      }

      ns = ns || this.uuid;
      window.reverseFieldTree[ns] = {};
      window.reverseSubformTree[ns] = {};

      function reverse(nodes, parent) {
        nodes.map(function (node) {
          if (parent) {
            node.parent = parent;
          }
          if (node.nodeType === 'field') {
            window.reverseFieldTree[ns][node.fieldName] = node;
            return;
          } else if (node.nodeType === 'subform') {
            window.reverseSubformTree[ns][node.subformUuid] = node;
            return;
          } else if (node.children) {
            reverse(node.children, node);
          }
        });
      }

      reverse(JSON.parse(JSON.stringify(this.formTree)), null);

      console.log('reverseFieldTree: ', reverseFieldTree);
      console.log('reverseSubformTree: ', reverseSubformTree);
    }
  };
  return (window.formDefinitionMethod = formDefinitionMethod);
});
