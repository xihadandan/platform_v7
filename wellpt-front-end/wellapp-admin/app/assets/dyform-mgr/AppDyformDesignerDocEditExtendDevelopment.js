function addDyformTypeScript(formUuid, docUuid, defaultFormDefinition, oldFields) {
  if (window.parent.initDyform && parent.initDyform.call(window, DesignerUtils) === false) {
    return;
  }
  if (formUuid) {
    DesignerUtils.init(formUuid, docUuid, defaultFormDefinition);
  } else {
    DesignerUtils.init();
  }

  DesignerUtils.getPFormDefinition = function (formDefinition) {
    if (this.pFormDefinition) {
      return this.pFormDefinition;
    } else {
      return loadFormDefinition(Browser.getQueryString('formUuid'));
    }
  };

  DesignerUtils.initProductNav = function (formDefinition) {
    var productFormDefinition = this.getPFormDefinition(formDefinition);
    this.pFormDefinition = productFormDefinition;
    formDefinition.product_fields = _.cloneDeep(productFormDefinition.fields);
    this.renderProductFields(productFormDefinition, 'js-tab-content-control3');
    this.renderProductSubForms(productFormDefinition);
    this.renderProductTempletes(productFormDefinition);
    // this.setDiffProductCtlStatus();
  };

  DesignerUtils.initDefaultProductNav = function (formDefinition) {
    var defaultProductFormDefinition = this.getPFormDefinition(formDefinition);
    this.pFormDefinition = defaultProductFormDefinition;
    defaultProductFormDefinition.fields = this.getFields(defaultFormDefinition.fields);
    defaultProductFormDefinition.product_fields = _.cloneDeep(defaultFormDefinition.fields);
    this.renderProductFields(defaultProductFormDefinition, 'js-tab-content-control4');
    // formDefinition.product_fields = _.cloneDeep(defaultFormDefinition.fields);
    // if (docUuid) {
    //   this.setDiffProductCtlStatus(defaultFormDefinition.fields);
    // }
  };

  DesignerUtils.getFields = function (fields) {
    var newField = _.cloneDeep(fields);
    for (var i in newField) {
      if (i.indexOf('ex_doc_') == -1) {
        delete newField[i];
      }
    }
    return newField;
  };

  //获取产品字段
  DesignerUtils.getProductFields = function (formDefinition) {
    return _.cloneDeep(formDefinition.fields);
  };

  //获取有更新的产品字段控件
  DesignerUtils.setDiffProductCtlStatus = function (fields) {
    var _diffField = DesignerUtils.getDiffFields(fields, oldFields);

    if (_diffField.length) {
      $('#product-nav1').append('<span class="new-tip">new</span>');
    }

    $('#js-tab-content-control4 .product-nav .field-btn').each(function () {
      var $this = $(this);
      var fieldName = $this.attr('data-fieldname');
      var fieldId = $this.attr('data-id');
      if (_diffField.indexOf(fieldName) > -1 || _diffField.indexOf(fieldId) > -1) {
        $this.addClass('field-new');
      }
    });
  };
  DesignerUtils.getDiffFields = function (fields, oldFields) {
    var diff = [];
    for (var i in fields) {
      if (!oldFields[i]) {
        diff.push(i);
      }
    }
    return diff;
  };

  //渲染产品字段
  DesignerUtils.renderProductFields = function (formDefinition, id) {
    var initFieldsList = $('#' + id + ' .init-fields-list');
    var productFields = this.getProductFields(formDefinition);

    function renderProductFieldsList(field) {
      var pluginCode = ckUtils.getPluginName(field.inputMode);
      var $li = $('<li>');
      var $filedBtn = $('<div>', {
        'need-grid': 'true',
        title: field.displayName,
        'data-fieldName': field.name,
        'data-code': field.name,
        'data-json': JSON.stringify(field),
        'plugin-code': pluginCode,
        productFiled: '1',
        class: 'field-btn ui-draggable jsCanDraggable'
      });
      if (field.necessary === '1') {
        $filedBtn.addClass('field-necessary').append('<div class="icon-necessary"><i class="iconfont icon-ptkj-bitianjiaobiao"></i></div>');
      }
      $filedBtn.append(
        '<div class="text"><i class="icon-pluginCode icon-' + pluginCode + '"></i><span>' + field.displayName + '</span></div>'
      );
      $li.append($filedBtn);

      $('#' + id + ' .init-fields-list ul').append($li);
    }

    for (var i in productFields) {
      if (productFields[i]) {
        renderProductFieldsList(productFields[i]);
      }
    }
    initFieldsList.trigger('renderFieldsTree');
  };

  //获取产品从表
  DesignerUtils.getProductSubForms = function (formDefinition) {
    return _.cloneDeep(formDefinition.subforms);
  };

  //获取有更新的产品从表
  DesignerUtils.getDiffProductSubForms = function (formDefinition) {
    return [];
  };

  //设置产品表单从表使用状态
  DesignerUtils.setProductSubFormsUsedStatus = function () {
    var productFormDefinition = this.getPFormDefinition(formDefinition);
    var subforms = productFormDefinition.subforms;
    for (var i in subforms) {
      var subFormName = subforms[i].name;
      var _status = formDefinition.subforms[subforms[i].formUuid] ? true : false;
      var $dom = $('.product-nav').find('.field-btn[data-fieldname="' + subFormName + '"]');

      setTimeout(
        function (_status, $dom) {
          if (_status) {
            $dom.removeClass('unused').draggable('disable');
          } else {
            $dom.addClass('unused').draggable('enable');
          }
        },
        0,
        _status,
        $dom
      );
    }
  };

  //渲染产品表单从表
  DesignerUtils.renderProductSubForms = function (formDefinition) {
    var initSubFormsList = $('#js-tab-content-control3 .init-subforms-list');
    var productSubForms = this.getProductSubForms(formDefinition);
    var diffProductSubForms = this.getDiffProductSubForms(formDefinition);
    if (Object.keys(productSubForms).length > 0) {
      $('#js-tab-content-control3').find('.init-subforms-list').show();
    }

    if (diffProductSubForms.length) {
      $('#product-nav').append('<span class="new-tip">new</span>');
    }

    function renderProductSubFormsList(subform) {
      var $li = $('<li>');
      var $filedBtn = $('<div>', {
        'need-grid': 'false',
        title: subform.displayName,
        'data-fieldName': subform.name,
        'data-id': subform.formUuid,
        'data-code': subform.name,
        'data-json': JSON.stringify(subform),
        'plugin-code': 'dysubform',
        productFiled: '1',
        class: 'field-btn ui-draggable jsCanDraggable'
      });
      if (subform.necessary === '1') {
        $filedBtn.addClass('field-necessary').append('<div class="icon-necessary"><i class="iconfont icon-ptkj-bitianjiaobiao"></i></div>');
      }
      if (diffProductSubForms.indexOf(subform.name) > -1) {
        $filedBtn.addClass('field-new');
      }
      $filedBtn.append('<div class="text"><i class="icon-pluginCode icon-dysubform"></i><span>' + subform.displayName + '</span></div>');
      $li.append($filedBtn);

      $('#js-tab-content-control3 .init-subforms-list ul').append($li);
    }

    for (var i in productSubForms) {
      if (productSubForms[i]) {
        renderProductSubFormsList(productSubForms[i]);
      }
    }
    initSubFormsList.trigger('renderFieldsTree');
    this.setProductSubFormsUsedStatus();
  };

  DesignerUtils.renderSubFormHtml = function (formDefinition) {
    var tdHtml = '';
    for (var i in formDefinition.fields) {
      tdHtml += '<th fieldname="' + formDefinition.fields[i].displayName + '">' + formDefinition.fields[i].displayName + '</th>';
    }
    var _html =
      '<table border="1" formuuid="' +
      formDefinition.formUuid +
      '" title="' +
      formDefinition.displayName +
      '" style="border-collapse: collapse; user-select: none;" class="cke_off"><tbody><tr>' +
      tdHtml +
      '</tr></tbody></table>';
    return _html;
  };

  //获取产品子表单
  DesignerUtils.getProductTemplates = function (formDefinition) {
    return _.cloneDeep(formDefinition.templates);
  };
  //获取有更新的产品子表单
  DesignerUtils.getDiffProductTemplates = function (formDefinition) {
    return [];
  };

  //设置产品表单子表单使用状态
  DesignerUtils.setProductTempletesUsedStatus = function () {
    var productFormDefinition = this.getPFormDefinition(formDefinition);
    var templates = productFormDefinition.templates;
    for (var i in templates) {
      var templateName = templates[i].templateName;
      var _status = formDefinition.templates[templates[i].templateUuid] ? true : false;
      var $dom = $('.product-nav').find('.field-btn[data-fieldname="' + templateName + '"]');

      setTimeout(
        function (_status, $dom) {
          if (_status) {
            $dom.removeClass('unused').draggable('disable');
          } else {
            $dom.addClass('unused').draggable('enable');
          }
        },
        0,
        _status,
        $dom
      );
    }
  };

  DesignerUtils.renderProductTempletes = function (formDefinition) {
    var initTempletesList = $('#js-tab-content-control3 .init-templetes-list');
    var productTemplates = this.getProductTemplates(formDefinition);
    var diffProductTemplates = this.getDiffProductTemplates(formDefinition);
    if (Object.keys(productTemplates).length > 0) {
      $('#js-tab-content-control3').find('.init-templetes-list').show();
    }
    if (diffProductTemplates.length) {
      $('#product-nav').append('<span class="new-tip">new</span>');
    }

    function renderProductTempletesList(templete) {
      var $li = $('<li>');
      var $filedBtn = $('<div>', {
        'need-grid': 'false',
        title: templete.templateName,
        'data-fieldName': templete.templateName,
        'data-id': templete.templateUuid,
        'data-code': templete.templateName,
        'data-json': JSON.stringify(templete),
        'plugin-code': 'templatecontainer',
        productFiled: '1',
        class: 'field-btn ui-draggable jsCanDraggable'
      });
      if (templete.necessary === '1') {
        $filedBtn.addClass('field-necessary').append('<div class="icon-necessary"><i class="iconfont icon-ptkj-bitianjiaobiao"></i></div>');
      }
      if (diffProductTemplates.indexOf(templete.name) > -1) {
        $filedBtn.addClass('field-new');
      }
      $filedBtn.append(
        '<div class="text"><i class="icon-pluginCode icon-templatecontainer"></i><span>' + templete.templateName + '</span></div>'
      );
      $li.append($filedBtn);

      initTempletesList.find('ul').append($li);
    }

    for (var i in productTemplates) {
      if (productTemplates[i]) {
        renderProductTempletesList(productTemplates[i]);
      }
    }
    initTempletesList.trigger('renderFieldsTree');
    this.setProductTempletesUsedStatus();
  };

  DesignerUtils.searchProductCtl = function (keyword) {
    $('.product-nav ul')
      .show()
      .find('.field-btn')
      .each(function () {
        var $this = $(this);
        var fieldName = $this.attr('data-fieldName');
        var fieldDisplay = $this.attr('title');
        if (fieldName.indexOf(keyword) > -1 || fieldDisplay.indexOf(keyword) > -1) {
          $this.parent().show();
        } else {
          $this.parent().hide();
        }
      });
  };
}
