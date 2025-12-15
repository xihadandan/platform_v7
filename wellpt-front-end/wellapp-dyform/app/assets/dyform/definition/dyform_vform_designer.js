$(function ($) {
  // 重写和拓展基础方法
  DesignerUtils.setPageAndDialogTile = function (uuid) {
    if (StringUtils.isBlank(uuid)) {
      $('#title').html('新建展现单据');
      $('#title_h2').html('新建展现单据');
    } else {
      $('#title').html('(' + formDefinition.name + ')编辑----展现单据');
      $('#title_h2').html('(' + formDefinition.name + ')编辑表单定义----展现单据');
    }
  };
  DesignerUtils.getPformUuid = function () {
    return $('#pformUuid').val();
  };
  DesignerUtils.getPformName = function () {
    return $('#pformName').val();
  };
  // 收集用户配置信息
  DesignerUtils.collectFormDatas = function () {
    var uuid = $('#formUuid').val();
    var mainTableEnName = $('#mainTableEnName').val();
    var mainTableCnName = $('#mainTableCnName').val();
    var formSign = $('input[name=formSign]:checked').val();
    var tableNum = $('#tableNum').val();
    var tableId = $('#tableId').val();
    var customJsModule = $('#customJsModule').val();
    var customJsModuleName = $('#customJsModuleName').val();
    var moduleId = $('#moduleId').val();
    var moduleName = $('#moduleName').val();
    var titleType = $("input[name='titleType']:checked").val();
    var titleContent = $('#titleContent' + titleType).val();
    var editForm = $('#editForm').prop('checked');
    var version = $('#version').val();
    var formType = DesignerUtils.getFormType();

    var htmlBodyContent = editor.getData();
    DesignerUtils.cleanUselessDefinition(htmlBodyContent);
    // formDefinition是一个全局变量,用于保存定义
    formDefinition.id = tableId;
    formDefinition.uuid = uuid;
    formDefinition.name = mainTableCnName;
    formDefinition.code = tableNum;
    formDefinition.version = version;
    formDefinition.formType = formType;
    formDefinition.html = htmlBodyContent;
    formDefinition.events = JSON.stringify(this.events);
    formDefinition.systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
    formDefinition.customJsModule = customJsModule;
    formDefinition.customJsModuleName = customJsModuleName;
    formDefinition.moduleId = moduleId;
    formDefinition.moduleName = moduleName;
    formDefinition.titleType = titleType;
    formDefinition.titleContent = titleContent;
    formDefinition.editForm = editForm;
    // formDefinition.customJsModuleName = customJsModuleName;
    formDefinition.pFormUuid = DesignerUtils.getPformUuid();
    return true;
  };
  DesignerUtils.initPformInfo = function (formDefinition) {
    var _this = this;
    $('#pformUuid').val(formDefinition.pFormUuid);
    $('#pformName').val(formDefinition.pFormUuid);
    $('#refFormUuid').val(formDefinition.pFormUuid);
    $('#pformName')
      .wSelect2({
        serviceName: 'dyFormFacade',
        queryMethod: 'queryAllPforms',
        selectionMethod: 'getSelectedFormDefinition',
        labelField: 'pformName',
        valueField: 'pformUuid',
        defaultBlank: true,
        width: '100%',
        height: 250
      })
      .on('change', function (event) {
        var pformName = _this.getPformName();
        var pformUuid = _this.getPformUuid();
        console.log(pformUuid);
        var refFormUuid = $('#refFormUuid').val();
        if (pformUuid !== refFormUuid) {
          editor.setData(''); // 清空布局
        }
        $('#refFormUuid').val(pformUuid);
        $('#refFormName').val(pformName);
        _this.initPformTreeInDesinger(pformUuid);
        // _this.initSubformTreeInDesinger(pformUuid);
        // }).on("select2-selecting", function(data) {
        // 	if (data.val == _this.getPformUuid()) {// 值没有变更，不处理
        // 		return true;
        // 	}
        // 	$('#pformUuid').val(data.val);
        // 	if (!_this.isDesignerHtmlContentIsBlank()) {
        // 		if (confirm("存储单据发生变更，设计器中已有的设计将被重置")) {
        // 			// 展现单据: 存储单据发生变更，设计器中已有的设计将被重置
        // 			// alert("重置设计器");
        // 			editor.setData(""); // 清空布局
        // 		} else {
        // 			// alert("不重置设计器");
        // 		}
        // 	} else {
        // 		// 设计器还没内容，这时直接以存储单据的内容作为设计器的内容
        // 	}
      });
  };
  DesignerUtils.initRefFormInfo = function (formDefinition) {
    // 初始化参照单据，默认为存储单据(一旦存储单据变更，参照单据会设置为存储单据,
    // 但用户可选择自己期望的参照单据)
    var _this = this;
    $('#refFormName').wSelect2({
      serviceName: 'dyFormFacade',
      queryMethod: 'queryAllPforms',
      selectionMethod: 'getSelectedFormDefinition',
      labelField: 'refFormName',
      valueField: 'refFormUuid',
      defaultBlank: true,
      width: '100%',
      height: 250
    });
  };
  /**
   * 初始化存储单据对应的字段数据
   */
  DesignerUtils.initPformTreeInDesinger = function (pformUuid) {
    if (StringUtils.isBlank(pformUuid)) {
      return;
    }
    var pFormDefinition = FormUtils.loadFormDefinition(pformUuid);
    editor.pform = {
      formDefinition: pFormDefinition
    };
    formDefinition.tableName = pFormDefinition.tableName;
    var fieldNodes = {
      id: 'root_zNdoes_field',
      children: [],
      name: '字段信息',
      open: true,
      chkDisabled: true,
      isParent: true,
      nocheck: true
    };
    var subformNodes = {
      id: 'root_zNdoes_subform',
      children: [],
      name: '从表信息',
      open: true,
      chkDisabled: true,
      isParent: true,
      nocheck: true
    };
    var templateNodes = {
      id: 'root_zNdoes_templates',
      children: [],
      name: '子表单信息',
      open: true,
      chkDisabled: true,
      isParent: true,
      nocheck: true
    };
    var fields = pFormDefinition.fields;
    var $html = $('<div>').html(pFormDefinition.html);
    $html.find('.value[name]').each(function (idx, element) {
      var fieldName = $(this).attr('name');
      if (fields.hasOwnProperty(fieldName)) {
        var pluginCode = getPlubinCodeByInputMode(fields[fieldName].inputMode);
        if (pluginCode) {
          var field = pFormDefinition.getField(fieldName);
          if (field.master === true) {
            // 子表单字段只能通过子表单添加
            return; // continue;
          }
          fieldNodes.children.push({
            id: fieldName,
            type: pluginCode,
            url: fieldName,
            iconSkin: field.displayName,
            pId: 'root_zNdoes_field',
            data: JSON.stringify(fields[fieldName]),
            name: field.displayName
          });
        } else if (console.error) {
          console.error('为获取到控件类型：' + fields[fieldName].inputMode);
        }
      }
    });

    var subforms = pFormDefinition.subforms;
    $html.find('table[formUuid]').each(function (idx, element) {
      var formUuid = $(this).attr('formUuid');
      if (subforms.hasOwnProperty(formUuid)) {
        var subform = pFormDefinition.getSubform(formUuid);
        subformNodes.children.push({
          id: formUuid,
          type: 'dysubform',
          url: subform.name,
          iconSkin: subform.name,
          pId: 'root_zNdoes_subform',
          data: JSON.stringify(subform),
          name: subform.displayName
        });
      }
    });

    var templates = pFormDefinition.templates;
    $html.find('.template-wrapper[templateUuid]').each(function (idx, element) {
      var templateId = $(this).attr('templateUuid');
      if (templates.hasOwnProperty(templateId)) {
        var template = templates[templateId];
        if (template && template.templateFlag === true) {
          return; // continue;
        }
        templateNodes.children.push({
          id: templateId,
          type: 'templatecontainer',
          url: templateId,
          iconSkin: template.templateName,
          pId: 'root_zNdoes_templates',
          data: JSON.stringify(template),
          name: template.templateName
        });
      }
    });
    var jsTemplates = $('#js-tab-content-control2>ul.field-list');
    function renderFieldsTree(treeNodes) {
      jsTemplates.empty();
      for (var i = 0; i < treeNodes.length; i++) {
        var category = treeNodes[i];
        var categoryItem = $('<li>', {
          class: 'field-category category-' + (i === 0 ? 'open' : 'close')
        });
        var header = $('<span>', {
          class: 'header',
          'data-id': category.id
        })
          .append(
            $('<i>', {
              class: i === 0 ? 'icon-chevron-down' : 'icon-chevron-right'
            })
          )
          .append(category.name); // appendText
        categoryItem.append(header);
        var nodeItems = $('<ul>', {
          style: 'list-style-type: none;',
          class: 'js-tab-content-control'
        });
        var trees = category.children;
        for (var j = 0; j < trees.length; j++) {
          var treeNode = trees[j],
            needGrid = 'true';
          if (treeNode.type === 'dysubform' || treeNode.type === 'templatecontainer') {
            needGrid = 'false';
          }
          var nodeItem = $('<li>', {
            vform: 'true',
            'need-grid': needGrid,
            title: treeNode.url + (treeNode.path ? '\n备注：\n' + treeNode.path : ''),
            'data-id': treeNode.id,
            'data-code': treeNode.url,
            'data-json': treeNode.data,
            'plugin-code': treeNode.type,
            class: 'jsCanDraggable ui-draggable icon-' + treeNode.type
          }).text(treeNode.name);
          nodeItem.data('data-field', treeNode);
          nodeItems.append(nodeItem);
        }
        categoryItem.append(nodeItems);
        jsTemplates.append(categoryItem);
      }
      jsTemplates.append(
        $('<li>', {
          class: 'no-data'
        }).text('未搜索到相关字段')
      );
      jsTemplates.trigger('renderFieldsTree');
      // 默认显示字段页签
      $('#field-tabs li:eq(1)>a[data-toggle]').trigger('click');
    }
    var zNodes = [fieldNodes, subformNodes, templateNodes];
    renderFieldsTree(zNodes);
  };
  // 初始化
  if (window.parent.initDyform && parent.initDyform.call(window, DesignerUtils) === false) {
    return;
  }
  DesignerUtils.init();
  DesignerUtils.initRefFormInfo(formDefinition);
  DesignerUtils.initPformTreeInDesinger(DesignerUtils.getPformUuid());

  setTimeout(function () {
    // 展示单据隐藏模板按钮
    $('a.cke_button__templatecontainer').hide();
  }, 1000);
});
