function addDyformTypeScript(formUuid, formType) {
  switch (formType) {
    case 'P':
    case 'MST':
    case 'C':
      if (window.parent.initDyform && parent.initDyform.call(window, DesignerUtils) === false) {
        return;
      }
      if (formUuid) {
        DesignerUtils.init(formUuid);
      } else {
        DesignerUtils.init();
      }

      DesignerUtils.getPFormDefinition = function (formDefinition) {
        if (this.pFormDefinition) {
          return this.pFormDefinition;
        } else {
          return formDefinition.formType === 'C' ? loadFormDefinition(Browser.getQueryString('uuid')) : formDefinition;
        }
      };

      DesignerUtils.initProductNav = function (formDefinition) {
        var productFormDefinition = this.getPFormDefinition(formDefinition);
        this.pFormDefinition = productFormDefinition;
        formDefinition.product_fields = _.cloneDeep(productFormDefinition.fields);
        $('#product-nav a').text(productFormDefinition.name).attr('title', productFormDefinition.name);
        this.renderProductFields(productFormDefinition);
        this.renderProductSubForms(productFormDefinition);
        this.renderProductTempletes(productFormDefinition);
        this.setDiffProductCtlStatus();
      };

      //获取产品字段
      DesignerUtils.getProductFields = function (formDefinition) {
        return _.cloneDeep(formDefinition.fields);
      };

      //获取有更新的产品字段控件
      DesignerUtils.setDiffProductCtlStatus = function () {
        var _diffField = [];
        JDS.call({
          service: 'formDefinitionService.getDiffFieldNamesByExtFormUuid',
          data: [Browser.getQueryString('cFormUuid') || Browser.getQueryString('uuid'), true],
          async: true,
          success: function (result) {
            _diffField = result.data;
            if (_diffField.length) {
              $('#product-nav').append('<span class="new-tip">new</span>');
            }

            $('.product-nav .field-btn').each(function () {
              var $this = $(this);
              var fieldName = $this.attr('data-fieldname');
              var fieldId = $this.attr('data-id');
              if (_diffField.indexOf(fieldName) > -1 || _diffField.indexOf(fieldId) > -1) {
                $this.addClass('field-new');
              }
            });
          },
          error: function (jqXHR) {
            console.log('更新失败');
          }
        });
      };

      //渲染产品字段
      DesignerUtils.renderProductFields = function (formDefinition) {
        var initFieldsList = $('#js-tab-content-control3 .init-fields-list');
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
            $filedBtn
              .addClass('field-necessary')
              .append('<div class="icon-necessary"><i class="iconfont icon-ptkj-bitianjiaobiao"></i></div>');
          }
          $filedBtn.append(
            '<div class="text"><i class="icon-pluginCode icon-' + pluginCode + '"></i><span>' + field.displayName + '</span></div>'
          );
          $li.append($filedBtn);

          $('#js-tab-content-control3 .init-fields-list ul').append($li);
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
            $filedBtn
              .addClass('field-necessary')
              .append('<div class="icon-necessary"><i class="iconfont icon-ptkj-bitianjiaobiao"></i></div>');
          }
          if (diffProductSubForms.indexOf(subform.name) > -1) {
            $filedBtn.addClass('field-new');
          }
          $filedBtn.append(
            '<div class="text"><i class="icon-pluginCode icon-dysubform"></i><span>' + subform.displayName + '</span></div>'
          );
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
            $filedBtn
              .addClass('field-necessary')
              .append('<div class="icon-necessary"><i class="iconfont icon-ptkj-bitianjiaobiao"></i></div>');
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
            if (fieldName.indexOf(keyword) > -1) {
              $this.parent().show();
            } else {
              $this.parent().hide();
            }
          });
      };
      break;
    case 'V':
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
              console.error('未获取到控件类型：' + fields[fieldName].inputMode);
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
              class: 'js-tab-content-control clear'
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
      DesignerUtils.init(formUuid);
      DesignerUtils.initRefFormInfo(formDefinition);
      DesignerUtils.initPformTreeInDesinger(DesignerUtils.getPformUuid());

      setTimeout(function () {
        // 展示单据隐藏模板按钮
        $('a.cke_button__templatecontainer').hide();
      }, 1000);
      break;
  }
}
