function addDyformTypeScript(formUuid, formType) {
  switch (formType) {
    case 'P':
    case 'MST':
      if (window.parent.initDyform && parent.initDyform.call(window, DesignerUtils) === false) {
        return;
      }
      if (formUuid) {
        DesignerUtils.init(formUuid);
      } else {
        DesignerUtils.init();
      }
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
            var refFormUuid = $('#refFormUuid').val();
            if (pformUuid !== refFormUuid) {
              editor.setData(''); // 清空布局
            }
            $('#refFormUuid').val(pformUuid);
            $('#refFormName').val(pformName);
            _this.initPformTreeInDesinger(pformUuid);
          });
      };

      /**
       * 根据存储单据uuid获取是否有扩展单据uuid
       */
      DesignerUtils.getMaxExtFormUuidByFormUuid = function (uuid) {
        var cFormUuid = null;
        JDS.call({
          service: 'formDefinitionService.getMaxExtFormUuidByFormUuid',
          data: [uuid, 'C'],
          async: false,
          success: function (result) {
            cFormUuid = result.data;
          },
          error: function (jqXHR) {
            alert('更新失败');
          }
        });
        return cFormUuid;
      };

      /**
       * 初始化存储单据对应的字段数据
       */
      DesignerUtils.initPformTreeInDesinger = function (pformUuid) {
        if (StringUtils.isBlank(pformUuid)) {
          return;
        }
        var pFormDefinition = null;

        var cFormUuid = this.getMaxExtFormUuidByFormUuid(pformUuid);
        if (cFormUuid) {
          pFormDefinition = FormUtils.loadFormDefinition(cFormUuid);
        } else {
          pFormDefinition = FormUtils.loadFormDefinition(pformUuid);
        }

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
      DesignerUtils.initPformInfo(formDefinition);
      DesignerUtils.initPformTreeInDesinger(DesignerUtils.getPformUuid());

      setTimeout(function () {
        // 展示单据隐藏模板按钮
        $('a.cke_button__templatecontainer').hide();
      }, 1000);
      break;
    case 'M':
      var allMobileBlockCode = [];
      var mobileDyformDefintionZetting = {
        edit: {
          enable: true,
          showRenameBtn: false,
          showRemoveBtn: false,
          drag: {
            isCopy: false,
            isMove: true,
            prev: true,
            next: true,
            inner: false
          }
        },
        view: {
          showLine: true,
          selectedMulti: false,
          dblClickExpand: function (treeId, treeNode) {
            return treeNode.level > 0;
          },
          addHoverDom: function (treeId, treeNode) {
            if (treeNode.data.type == 'block' || treeNode.data.type == 'layout') {
              var aObj = $('#' + treeNode.tId + '_a');
              // 判断是否已经存在，已经存在就不需要重复添加
              if ($('#upBtn_' + treeNode.id).length > 0) {
                return;
              }
              var editStr =
                "<div class='m-tree-btn' id='upBtn_" +
                treeNode.id +
                "' title='上移' onfocus='this.blur();'><i class='iconfont icon-ptkj-shangyi'></i></div>";
              editStr +=
                "<div class='m-tree-btn' id='downBtn_" +
                treeNode.id +
                "' title='下移' onfocus='this.blur();'><i class='iconfont icon-ptkj-xiayi'></i></div>";

              aObj.append(editStr);
              // 定义上移和下移事件
              var treeObj = $.fn.zTree.getZTreeObj(treeId);
              // console.log(treeId);
              var upBtn = $('#upBtn_' + treeNode.id);
              if (upBtn) {
                upBtn.bind('click', function () {
                  var preNode = treeNode.getPreNode();
                  if (preNode) {
                    // 移动树节点
                    treeObj.moveNode(preNode, treeNode, 'prev');
                    // order顺序交换下，更新预览里面的顺序
                    var preNodeOrder = preNode.data.order;
                    var nodeOrder = treeNode.data.order;
                    preNode.data.order = nodeOrder;
                    treeNode.data.order = preNodeOrder;
                    treeObj.updateNode(preNode);
                    treeObj.updateNode(treeNode);
                    mobileConfigurationChange();
                  }
                });
              }
              var downBtn = $('#downBtn_' + treeNode.id);
              if (downBtn) {
                downBtn.bind('click', function () {
                  var nextNode = treeNode.getNextNode();
                  if (nextNode) {
                    // 移动树节点
                    var r = treeObj.moveNode(nextNode, treeNode, 'next');
                    // order顺序交换下，更新预览里面的顺序
                    var nextNodeOrder = nextNode.data.order;
                    var nodeOrder = treeNode.data.order;
                    nextNode.data.order = nodeOrder;
                    treeNode.data.order = nextNodeOrder;
                    treeObj.updateNode(nextNodeOrder);
                    treeObj.updateNode(treeNode);
                    mobileConfigurationChange();
                  }
                });
              }
            }
          },
          removeHoverDom: function (treeId, treeNode) {
            if (treeNode.data.type == 'block' || treeNode.data.type == 'layout') {
              $('#upBtn_' + treeNode.id)
                .unbind()
                .remove();
              $('#downBtn_' + treeNode.id)
                .unbind()
                .remove();
            }
          }
        },
        callback: {
          onClick: onFieldTreeNodeClick,
          beforeDrop: beforeDrop,
          onDrop: zTreeOnDrop
        },
        data: {
          keep: {
            parent: true
          },
          simpleData: {
            enable: true,
            idKey: 'id',
            pIdKey: 'pId'
          }
        }
      };

      function isFormEmpty(html) {
        if (html == null || html == '') {
          return true;
        } else if (html.indexOf('请在白色背景区域编辑') > -1) {
          return true;
        } else if (html == '<p>&nbsp;</p>') {
          return true;
        }
        return false;
      }

      function objectLength(obj) {
        var count = 0;
        if (obj != null && typeof obj !== 'undefined') {
          for (var i in obj) {
            count++;
          }
        }
        return count;
      }

      function getFormHtml(definition) {
        return definition ? definition.html : formDefinition.html;
      }

      /**
       * 渲染"手机设置"标签页
       *
       * @param defintionObj
       */
      function renderMobileDesign(defintionObj) {
        console.log('重新渲染，renderMobileDesign=============');
        // 获取手机配置信息
        var mobileConfiguration = loadMobileConfig(defintionObj);
        var pDefintionObj = FormUtils.loadFormDefinition(defintionObj.pFormUuid);
        if (pDefintionObj && pDefintionObj.fields) {
          var fieldLength = 0,
            fieldLength2 = objectLength(pDefintionObj.fields);
          $.each(mobileConfiguration.blocks || {}, function (blockCode, block) {
            fieldLength += objectLength(block.fields);
          });
          $.each(mobileConfiguration.layouts || {}, function (blockCode, layout) {
            $.each(layout.blocks || {}, function (blockCode, block) {
              fieldLength += objectLength(block.fields);
            });
          });
          if (fieldLength2 > 0 && fieldLength !== fieldLength2) {
            setTimeout(function () {
              if (confirm('手机表单字段有变更，是否进行合并')) {
                $('#btn_merge_mobile_definition').trigger('click');
              }
            }, 2000);
          }
        }
        // 转换成树节点json格式
        var ztreeJson = convert2Ztree(mobileConfiguration);
        var treeObj = $.fn.zTree.init($('#div_field_tree_container'), mobileDyformDefintionZetting, [ztreeJson]);
        // 展示手机预览效果
        mobilePreview(defintionObj);
      }

      function loadDefinitionJsonDefaultInfo(formDefinition) {
        JDS.call({
          service: 'dyFormFacade.loadDefinitionJsonDefaultInfo',
          async: false,
          data: [
            JSON.stringify(formDefinition),
            DesignerUtils.getFormType(),
            formDefinition.name,
            formDefinition.pFormUuid || DesignerUtils.getPformUuid()
          ],
          success: function (result) {
            formDefinition = result.data;
          }
        });
        return formDefinition;
      }

      function mobilePreview(defintionObj) {
        if (window.frames['iframe_mobile_preview'].preview) {
          defintionObj = loadDefinitionJsonDefaultInfo(defintionObj);
          window.frames['iframe_mobile_preview'].preview(defintionObj);
        } else {
          setTimeout(function () {
            mobilePreview(defintionObj);
          }, 1000);
        }
      }

      function beforeDrop(treeId, treeNodes, targetNode, moveType) {
        // 不能移出到树外面
        if (targetNode == null) {
          alert('targetNode不能为空');
          return false;
        }
        for (var i = 0, l = treeNodes.length; i < l; i++) {
          var nodeType = treeNodes[i].data.type;
          var targetType = targetNode.data.type;
          // 字段，标签，从表只允许在拖动到区块下面
          if (nodeType == 'field' || nodeType == 'label' || nodeType == 'subform') {
            if (targetType == 'dyform') {
              var msg = nodeType == 'subform' ? '从表' : '字段';
              alert(msg + '只能在区块内移动');
              return false;
            } else if (targetType == 'block') {
              // 移动该节点到该区块下的第一个位置
              var treeObj = $.fn.zTree.getZTreeObj(treeId);
              // 空区块
              if (targetNode.children.length == 0) {
                treeObj.moveNode(targetNode, treeNodes[i], 'inner');
              } else {
                treeObj.moveNode(targetNode.children[0], treeNodes[i], 'prev');
              }
              // 触发onDrop更新order属性，并且刷新手机预览界面
              zTreeOnDrop(null, treeId, treeNodes, targetNode, moveType);
              return false;
            }
          }

          // 区块只能通过上下键移动
          if (nodeType == 'block') {
            return false;
          }
        }
        return true;
      }

      function zTreeOnDrop(event, treeId, treeNodes, targetNode, moveType) {
        var zObject = $.fn.zTree.getZTreeObj('div_field_tree_container');
        var parentNode = treeNodes[0].getParentNode();
        for (var i = 0, l = parentNode.children.length; i < l; i++) {
          var node = parentNode.children[i];
          node.data.order = i; // 更新顺序
          node.data.blockCode = parentNode.data.blockCode; // 因为存在跨区块移动，所以需要变更blockCode属性
          zObject.updateNode(node);
        }
        mobileConfigurationChange();
      }

      function mobileConfigurationChange() {
        // console.log("mobileConfigurationChange=========")
        var defintionObj = $.extend({}, formDefinition);
        var zObject = $.fn.zTree.getZTreeObj('div_field_tree_container');
        var mobileConfiguration = {};
        var rootNodes = zObject.getNodes();
        if (rootNodes.length != 1) {
          return mobileConfiguration;
        }
        var rootNode = rootNodes[0];
        var layouts = rootNode.data.layouts;
        mobileConfiguration.blockLayout = rootNode.data.blockLayout;
        if (layouts == null || typeof layouts === 'undefined') {
          mobileConfiguration.blocks = {};
        } else {
          mobileConfiguration.layouts = {};
        }
        // 更新左边树节点
        convertZtreeChildren(rootNode, mobileConfiguration);
        // 更新右边手机预览
        defintionObj.mobileConfiguration = mobileConfiguration;
        mobilePreview(defintionObj);
      }

      function onFieldTreeNodeClick(event, treeId, treeNode, clickFlag) {
        setDefinitionProperty(treeNode);
      }

      function setDefinitionProperty(treeNode) {
        $('.div_property_defintion').show();
        var data = treeNode.data;
        // console.log("treeNode=================");
        // console.log( treeNode );
        var $container = $('.div_property_defintion_container').empty();
        $container.append("<input type='hidden' id='treeNodeId' value ='" + treeNode.id + "'/>");
        $container.append("<input type='hidden' id='treeNodeType' value ='" + treeNode.data.type + "'/>");
        console.log(data.type);
        if (data.type == 'dyform') {
          buildInput($container, '', '名称', treeNode.name, true);
          buildInput($container, '', 'ID', treeNode.id, true);
          var blockLayoutItems = [
            {
              id: '1',
              text: '默认布局'
            }
          ];
          // 如果是页签布局，则不支持“侧滑菜单栏”
          if (data.layouts == null || typeof data.layouts === 'undefined') {
            blockLayoutItems = blockLayoutItems.concat([
              {
                id: '2',
                text: '顶部TabBar'
              },
              {
                id: '3',
                text: '底侧TabBar'
              },
              {
                id: '4',
                text: '顶部滑块'
              },
              {
                id: '5',
                text: '侧滑菜单栏'
              }
            ]);
          }
          buildSelect($container, 'blockLayout', '区块布局', data.blockLayout, blockLayoutItems);
        }
        if (data.type == 'layout') {
          buildInput($container, 'displayName', '名称', data.displayName);
          buildInput($container, 'name', '编号', data.name, true);
          buildCheckbox($container, 'isActive', '激活', data.isActive, true);
          var hideBlockTitle = data.hideBlockTitle == null ? true : data.hideBlockTitle;
          buildCheckbox($container, 'hideBlockTitle', '隐藏同名区块标题', hideBlockTitle);
        }
        if (data.type == 'block') {
          buildInput($container, 'mBlockTitle', '名称', data.blockTitle);
          buildInput($container, 'mBlockCode', '编号', data.blockCode, true);
          buildCheckbox($container, 'blockIsShow', '隐藏', !data.isShow);
          buildSelect($container, 'blockLayout', '显示模式', data.blockLayout, [
            {
              id: '1',
              text: '卡片视图'
            },
            {
              id: '2',
              text: '折叠面板'
            }
          ]);
        }
        if (data.type == 'field') {
          buildInput($container, 'fieldDisplayName', '名称', data.displayName, true);
          buildInput($container, 'fieldId', 'ID', data.name, true);
          buildInput($container, 'fieldTagName', '标签名称', data.tagName || data.displayName, false);

          buildCheckbox($container, 'fieldIsShow', '隐藏', !data.isShow);
          // console.log("field===================");
          // console.log( data );
          // console.log( dyFormInputMode.radio );
          var inputMode = parseInt(data.inputMode);
          // 默认支持单双行切换
          var selectOptions = [
            {
              id: '1',
              text: '单行'
            },
            {
              id: '2',
              text: '双行'
            }
          ];
          // radio，checkbox，视图，嵌入页面, 三种附件 只支持双行
          if (
            inputMode == dyFormInputMode.radio ||
            inputMode == dyFormInputMode.checkbox ||
            inputMode == dyFormInputMode.viewdisplay ||
            inputMode == dyFormInputMode.embedded ||
            inputMode == dyFormInputMode.accessoryImg ||
            inputMode == dyFormInputMode.accessory1 ||
            inputMode == dyFormInputMode.accessory3
          ) {
            selectOptions = [
              {
                id: '2',
                text: '双行'
              }
            ];
          }
          buildSelect($container, 'fieldDisplayStyle', '显示模式', data.displayStyle, selectOptions);
        }
        if (data.type == 'subform') {
          buildInput($container, 'subformDisplayName', '名称', data.displayName);
          buildInput($container, 'subformId', 'ID', data.formUuid, true);
          var subformDefinition = formDefinition.subforms[data.formUuid];
          var subformFields = [];
          $.each(subformDefinition.fields, function (i, field) {
            subformFields.push({
              id: field.name,
              text: field.displayName
            });
          });
          buildSelect($container, 'rowTitle', '行标题', data.rowTitle, subformFields);
          buildCheckbox($container, 'expandAllWhileReadonly', '默认展开只读数据', data.expandAllWhileReadonly);
          buildCheckbox($container, 'displayAsTable', '只读时以表格展示', data.displayAsTable);
          buildCheckbox($container, 'subformIsShow', '隐藏', !data.isShow);
          buildSelect($container, 'subformDisplayStyle', '显示模式', data.displayStyle, [
            {
              id: '1',
              text: '分组列表'
            },
            {
              id: '2',
              text: '弹框'
            }
          ]);
        }
        if (data.type == 'label') {
          buildTextArea($container, 'labelText', '标签内容', data.text, true);
          buildCheckbox($container, 'labelIsShow', '隐藏', !data.isShow);
        }
      }

      function putMobileNodes(blocks, map) {
        if (!$.isEmptyObject(blocks)) {
          $.each(blocks, function (key, block) {
            $.each(block.fields, function (key, field) {
              map.fields[key] = field;
            });
            $.each(block.subforms, function (key, subform) {
              map.subforms[key] = subform;
            });
            $.each(block.labels, function (key, label) {
              map.labels[key] = label;
            });
            map.blocks[key] = block;
            putMobileNodes(block.blocks, map);
          });
        }
      }

      // 更新数据的时候，客户端先收集mobileConfiguration这个字段值
      function collectMobileData(defintionObj, isMerge) {
        var zObject = $.fn.zTree.getZTreeObj('div_field_tree_container');
        console.log('collectMobileData================');
        // 如果没有生成树节点则采用PC端的设置生成mobileConfiguration
        var newMobileConfiguration = loadDefaultMobileConfiguration(defintionObj);
        if (!zObject) {
          return newMobileConfiguration;
        }
        var rootNodes = zObject.getNodes();
        if (rootNodes.length != 1) {
          return newMobileConfiguration;
        }
        // 已经生成树节点了，采用树节点的数据
        var mobileConfiguration = {};
        var rootNode = rootNodes[0];
        var layouts = rootNode.data.layouts;
        mobileConfiguration.blockLayout = rootNode.data.blockLayout;
        if (layouts == null || typeof layouts === 'undefined') {
          mobileConfiguration.blocks = {};
        } else {
          mobileConfiguration.layouts = {};
        }
        convertZtreeChildren(rootNode, mobileConfiguration);

        // 保存不自动合并字段了，只有点击合并字段才确认合并
        if (isMerge) {
          // TODO 页签合并
          var newMap = {
            fields: {},
            subforms: {},
            blocks: {},
            labels: {}
          };
          putMobileNodes(newMobileConfiguration.blocks, newMap);
          var map = {
            fields: {},
            subforms: {},
            blocks: {},
            labels: {}
          };
          putMobileNodes(mobileConfiguration.blocks, map);
          $.each(newMap.fields, function (key, field) {
            // 重复的field就覆盖，新增的就添加
            // 检查区块
            if (!(field.blockCode in map.blocks)) {
              mobileConfiguration.blocks[field.blockCode] = map.blocks[field.blockCode] = newMap.blocks[field.blockCode];
              map.blocks[field.blockCode].fields = {};
              map.blocks[field.blockCode].subforms = {};
              map.blocks[field.blockCode].blocks = {};
              map.blocks[field.blockCode].labels = {};
            }

            // 重复的直接覆盖，但是不能变更displayStyle和区块位置, 位置顺序
            if (key in map.fields) {
              field.displayStyle = map.fields[key].displayStyle;
              field.blockCode = map.fields[key].blockCode;
              field.tagName = map.fields[key].tagName;
              field.order = map.fields[key].order;
            }

            map.blocks[field.blockCode].fields[key] = field;

            delete map.fields[key];
          });
          $.each(newMap.subforms, function (key, subform) {
            if (!(subform.blockCode in map.blocks)) {
              mobileConfiguration.blocks[subform.blockCode] = map.blocks[subform.blockCode] = newMap.blocks[subform.blockCode];
              map.blocks[subform.blockCode].fields = {};
              map.blocks[subform.blockCode].subforms = {};
              map.blocks[subform.blockCode].blocks = {};
              map.blocks[subform.blockCode].labels = {};
            }

            // 重复的直接覆盖，但是不能变更displayStyle, 区块位置，顺序
            if (key in map.subforms) {
              subform.displayStyle = map.subforms[key].displayStyle;
              subform.blockCode = map.subforms[key].blockCode;
              subform.order = map.subforms[key].order;
              subform.displayName = map.subforms[key].displayName;
            }

            map.blocks[subform.blockCode].subforms[key] = subform;

            delete map.subforms[key];
          });

          $.each(newMap.labels, function (key, label) {
            // 重复的label就覆盖，新增的就添加
            // 检查区块
            if (!(label.blockCode in map.blocks)) {
              mobileConfiguration.blocks[label.blockCode] = map.blocks[label.blockCode] = newMap.blocks[label.blockCode];
              map.blocks[label.blockCode].fields = {};
              map.blocks[label.blockCode].subforms = {};
              map.blocks[label.blockCode].blocks = {};
              map.blocks[label.blockCode].labels = {};
            }

            // 重复的直接覆盖，但是不能变更displayStyle和区块位置
            if (key in map.labels) {
              label.blockCode = map.labels[key].blockCode;
              label.order = map.labels[key].order;
            }

            map.blocks[label.blockCode].labels[key] = label;

            delete map.labels[key];
          });

          // 剩余的map，就是被PC端删除掉的控件
          $.each(map.fields, function (key, field) {
            delete map.blocks[field.blockCode].fields[key];
          });

          $.each(map.subforms, function (key, subform) {
            delete map.blocks[subform.blockCode].subforms[key];
          });

          $.each(map.labels, function (key, label) {
            delete map.blocks[label.blockCode].labels[key];
          });
        }
        return mobileConfiguration;
      }

      function convertZtreeChildren(parentNode, configuration) {
        if (parentNode.data.type == 'dyform') {
          parentNode.data.blocks = {};
        } else if (parentNode.data.type == 'layout') {
          parentNode.data.blocks = {};
        } else if (parentNode.data.type == 'block') {
          parentNode.data.fields = {};
          parentNode.data.subforms = {};
          parentNode.data.blocks = {};
          parentNode.data.labels = {};
        }
        if (parentNode.children) {
          for (var i = 0, l = parentNode.children.length; i < l; i++) {
            var node = parentNode.children[i];
            var nodeData = node.data;
            nodeData.parentBlockCode = parentNode.data.blockCode || '';
            if (nodeData.type == 'layout') {
              configuration.layouts[node.id] = nodeData;
            } else if (nodeData.type == 'block') {
              configuration.blocks[node.id] = nodeData;
            } else if (nodeData.type == 'field') {
              configuration.fields[node.id] = nodeData;
            } else if (nodeData.type == 'subform') {
              nodeData.blockCode = parentNode.data.blockCode;
              configuration.subforms[node.id] = nodeData;
            } else if (nodeData.type == 'label') {
              configuration.labels[node.id] = nodeData;
            }
            convertZtreeChildren(node, nodeData);
          }
        }
      }

      function convertFields2Ztree(root, fields) {
        $.each(fields || {}, function (index, field) {
          var fieldNode = {
            id: field.name,
            name: field.displayName,
            data: $.extend(
              {
                type: 'field'
              },
              field
            )
          };
          root.children.push(fieldNode);
        });
      }
      function convertLabels2Ztree(root, labels) {
        $.each(labels || {}, function (index, label) {
          var labelNode = {
            id: label.id,
            name: label.text,
            data: $.extend(
              {
                type: 'label'
              },
              label
            )
          };
          root.children.push(labelNode);
        });
      }

      function convertSubforms2Ztree(root, subforms) {
        $.each(subforms || {}, function (index, subform) {
          var formUuid = subform.formUuid;
          if (!subform.formId) {
            // 兼容之前保存的数据没有formId
            var subformDef = formDefinition.subforms[formUuid];
            subform.formId = subformDef && subformDef.outerId;
          }
          var subformNode = {
            id: formUuid,
            name: subform.displayName,
            children: [],
            data: $.extend(
              {
                type: 'subform'
              },
              subform
            )
          };
          root.children.push(subformNode);
        });
      }

      function convertLayouts2Ztree(root, layouts) {
        $.each(layouts || {}, function (index, layout) {
          var layoutNode = {
            id: layout.id,
            name: layout.displayName,
            isParent: true,
            data: $.extend(
              {
                type: 'layout'
              },
              layout
            ),
            children: []
          };
          allMobileBlockCode.push(layoutNode.id);
          convertBlocks2Ztree(layoutNode, layout.blocks || {});
          layoutNode.children.sort(function (a, b) {
            return a.data.order - b.data.order;
          });
          root.children.push(layoutNode);
        });
      }

      function convertBlocks2Ztree(root, blocks) {
        // 去掉占位符中的src属性
        var html = unwrapperImg(getFormHtml());
        var $html = $('<span></span>').append(html);
        $.each(blocks || {}, function (index, block) {
          // 翻译时直接在html中编辑,标题信息未设置到JSON中,优先取html中的标题
          var $block = $html.find('.title[colspan][blockcode=' + block.blockCode + ']');
          block.blockTitle = $block.text() || block.blockTitle;
          var blockNode = {
            id: block.blockCode,
            name: block.blockTitle,
            isParent: true,
            data: $.extend(
              {
                type: 'block'
              },
              block
            ),
            children: []
          };
          allMobileBlockCode.push(block.blockCode);
          delete blockNode.data.fields;
          convertFields2Ztree(blockNode, block.fields || {});
          convertSubforms2Ztree(blockNode, block.subforms || {});
          convertBlocks2Ztree(blockNode, block.blocks || {}); // 不存在去看嵌套关系
          convertLabels2Ztree(blockNode, block.labels || {});
          blockNode.children.sort(function (a, b) {
            return a.data.order - b.data.order;
          });
          root.children.push(blockNode);
        });
      }

      // mobileConfiguration转成ztree数据
      function convert2Ztree(mobileConfiguration) {
        var name = $('#mainTableCnName').val();
        var id = $('#tableId').val();
        var root = {
          id: id,
          name: name,
          data: {
            type: 'dyform',
            layouts: mobileConfiguration.layouts,
            blockLayout: mobileConfiguration.blockLayout
          },
          open: true,
          isParent: true,
          children: []
        };
        allMobileBlockCode = [];
        // 转化blocks信息为树节点信息
        // console.log( mobileConfiguration.blocks );
        var layouts = mobileConfiguration.layouts;
        if (layouts == null && typeof layouts === 'undefined') {
          convertBlocks2Ztree(root, mobileConfiguration.blocks);
        } else {
          convertLayouts2Ztree(root, layouts);
        }
        root.children.sort(function (a, b) {
          return a.data.order - b.data.order;
        });
        return root;
      }

      function loadMobileConfig(formDefinition) {
        if (!formDefinition.mobileConfiguration) {
          formDefinition.mobileConfiguration = loadDefaultMobileConfiguration(formDefinition);
        }
        return formDefinition.mobileConfiguration;
      }

      function unwrapperImg(html) {
        html = "<span id='_htmldyform_'>" + html + '</span>';
        var srcpattern = /src="\/resources[^\"]+ckeditor[^\"]+plugins[^\"]+images[^\"]+jpg\"/;
        var srcpattern2 = /src="resources[^\"]+ckeditor[^\"]+plugins[^\"]+images[^\"]+jpg\"/;
        html = html.replace(srcpattern, '');
        html = html.replace(srcpattern2, '');
        while (srcpattern.test(html) || srcpattern2.test(html)) {
          html = html.replace(srcpattern, '');
          html = html.replace(srcpattern2, '');
        }

        var placeHolderImgPattern1 = /<img[^>]+class=[\"value\"|\'value\'][^>]+name=[\'|\"]([^\'|^\"]*)[\'|\"][^>]+>/i;
        var placeHolderImgPattern2 = /<img[^>]+name=[\'|\"](.*)[\'|\"][^>]+class=[\"value\"|\'value\'][^>]+>/i;

        while (placeHolderImgPattern1.test(html)) {
          var r = html.match(placeHolderImgPattern1);
          if (r != null) {
            html = html.replace(r[0], "<span class='value' name='" + r[1] + "'></span>");
          }
        }

        while (placeHolderImgPattern2.test(html)) {
          var r = html.match(placeHolderImgPattern2);
          if (r != null) {
            html = html.replace(r[0], "<span class='value' name='" + r[1] + "'></span>");
          }
        }
        return $(html).html();
      }

      function loadDefaultMobileConfiguration(defintionObj) {
        var mobileConfiguration = {
          blockLayout: '1',
          blocks: {}
        };
        var addedFields = {};
        var addedSubforms = {};
        var addedLabels = {};
        var blocks = defintionObj.blocks || {};
        var fields = defintionObj.fields || {};
        var layouts = defintionObj.layouts || {};
        var subforms = defintionObj.subforms || {};
        var labelIndex = 0;
        // 去掉占位符中的src属性
        var html = unwrapperImg(getFormHtml());
        var $html = $('<body></body>').append(html);
        // console.log($html);
        $html.find('.title[blockcode][colspan]').each(function (index) {
          var $block = $(this);
          var blockCode = $block.attr('blockcode');
          var blockTitle = $block.text() || blocks[blockCode].blockTitle;
          console.log('blockTitle', blockTitle);
          var isShow = true;
          if (blockCode in blocks) {
            isShow = !blocks[blockCode].hide;
          }
          var blockNode = {
            blockCode: blockCode,
            parentBlockCode: '',
            blockTitle: blockTitle,
            blockLayout: '1',
            isShow: isShow,
            order: index,
            fields: {},
            subforms: {},
            blocks: {},
            labels: {}
          };

          $block
            .closest('table')
            .find('.value[name]')
            .each(function (i) {
              var $field = $(this);
              var name = $field.attr('name');
              var displayName = fields[name] ? fields[name].displayName : $field.attr('title');
              var isShow = true;
              if (name in fields) {
                isShow = fields[name].showType != 5;
              }
              var inputMode = fields[name] ? fields[name].inputMode : '';
              // console.log( "closestTable,,inputMode=" + inputMode + ",add"
              // );
              var fieldNode = {
                name: name,
                displayName: displayName,
                tagName: displayName,
                isShow: isShow,
                order: i,
                blockCode: blockCode,
                displayStyle: 1,
                displayTemplate: '',
                inputMode: inputMode
              };
              blockNode.fields[name] = fieldNode;
              addedFields[name] = fieldNode;
            });
          $block
            .closest('table')
            .find('table[formuuid]')
            .each(function (i) {
              var $subform = $(this);
              var formUuid = $subform.attr('formuuid');
              var subformDef = subforms[formUuid];
              var formId = subformDef && subformDef['outerId'];
              var displayName = subformDef ? subformDef.displayName : $subform.attr('title');
              // 如果没有表单定义也认为是个无效表单
              if ($(this).find('th').size() > 0) {
                var subformNode = {
                  formId: formId,
                  formUuid: formUuid,
                  displayName: displayName,
                  isShow: true,
                  order: 1000 + i,
                  blockCode: blockCode,
                  displayStyle: 1
                };
                blockNode.subforms[formUuid] = subformNode;
                addedSubforms[formUuid] = subformNode;
              }
            });
          $block
            .closest('table')
            .find('label.label')
            .each(function (i) {
              var $label = $(this);
              var text = $label.text();
              var html = $label.html();
              var id = $label.attr('uuid');
              var labelNode = {
                id: id,
                text: text,
                html: html,
                isShow: true,
                blockCode: blockCode,
                order: 2000 + i
              };
              blockNode.labels[id] = labelNode;
              addedLabels[id] = labelNode;
            });
          mobileConfiguration.blocks[blockCode] = blockNode;
        });

        if ('other' in mobileConfiguration.blocks) {
          var blockNode = mobileConfiguration.blocks.other;
        } else {
          var blockNode = {
            blockCode: 'other',
            parentBlockCode: '',
            blockTitle: '其他',
            blockLayout: '1',
            order: 1000,
            isShow: true,
            fields: {},
            subforms: {},
            labels: {},
            blocks: {}
          };
        }
        // console.log( $html );
        $html.find('.value[name]').each(function (i) {
          var $field = $(this);
          var name = $field.attr('name');
          var displayName = fields[name] ? fields[name].displayName : $field.attr('title');
          if (name in addedFields) {
            return;
          }
          var isShow = true;
          if (name in fields) {
            isShow = fields[name].showType != 5;
          }
          var inputMode = fields[name] ? fields[name].inputMode : '';
          // console.log( "other,,inputMode=" + inputMode + ",add" );
          var fieldNode = {
            name: name,
            displayName: displayName,
            tagName: displayName,
            isShow: isShow,
            order: i,
            blockCode: 'other',
            displayStyle: 1,
            displayTemplate: '',
            inputMode: inputMode
          };
          blockNode.fields[name] = fieldNode;
        });
        $html.find('table[formuuid]').each(function (i) {
          var $subform = $(this);
          var formUuid = $subform.attr('formuuid');
          var subformDef = subforms[formUuid];
          var formId = subformDef && subformDef['outerId'];
          var displayName = subformDef ? subformDef.displayName : $subform.attr('title');
          if (formUuid in addedSubforms) {
            return;
          }
          // 如果没有表单定义也认为是个无效表单
          if ($(this).find('th').size() > 0) {
            var subformNode = {
              formId: formId,
              formUuid: formUuid,
              displayName: displayName,
              isShow: true,
              order: 1000 + i,
              blockCode: 'other',
              displayStyle: 1
            };
            blockNode.subforms[formUuid] = subformNode;
          }
        });
        $html.find('label.label').each(function (i) {
          var $label = $(this);
          var text = $label.text();
          var html = $label.html();
          var id = $label.attr('uuid');
          if (id in addedLabels) {
            return;
          }
          var labelNode = {
            id: id,
            text: text,
            html: html,
            isShow: true,
            blockCode: 'other',
            order: 2000 + i
          };
          blockNode.labels[id] = labelNode;
          addedLabels[id] = labelNode;
        });
        if (!$.isEmptyObject(blockNode.fields) || !$.isEmptyObject(blockNode.subforms) || !$.isEmptyObject(blockNode.labels)) {
          mobileConfiguration.blocks[blockNode.blockCode] = blockNode;
        }
        // 页签布局
        $html.find('.layout[name]').each(function (layoutIndex) {
          mobileConfiguration.layouts = mobileConfiguration.layouts || {};
          var $layout = $(this);
          var layoutCode = $layout.attr('name');
          var layoutDef = layouts[layoutCode] || {};
          var subtabs = layoutDef.subtabs || {};
          $layout.find('.subtab-design[name]').each(function (tabIdx) {
            var $tab = $(this);
            var tabCode = $tab.attr('name');
            var subtab = subtabs[tabCode];
            var tabTitle = $tab.attr('title') || subtab.displayName;
            var layoutId = layoutCode + '-' + tabCode;
            var tabNode = $.extend(
              {
                id: layoutId,
                hideBlockTitle: true,
                parentBlockCode: '',
                order: layoutIndex * 50 + tabIdx,
                blocks: {}
              },
              subtab
            );
            $tab.find('.title[blockcode][colspan]').each(function (blockIdx) {
              var $block = $(this);
              var blockCode = $block.attr('blockcode');
              var blockNode = mobileConfiguration.blocks[blockCode];
              // 删除mobileConfiguration下的block
              delete mobileConfiguration.blocks[blockCode];
              tabNode.blocks[blockCode] = blockNode;
            });
            mobileConfiguration.layouts[layoutId] = tabNode;
          });
          $.each(mobileConfiguration.blocks, function (idx, block) {
            // 还有其他非页签管理的区块，则添加到其他页签中
            mobileConfiguration.layouts.other = mobileConfiguration.layouts.other || {
              id: 'other',
              name: 'other',
              displayName: '其他',
              hideBlockTitle: true,
              parentBlockCode: '',
              order: 1000 + 100,
              isActive: false,
              blocks: {}
            };
            delete mobileConfiguration.blocks[block.blockCode];
            mobileConfiguration.layouts.other.blocks[block.blockCode] = block;
          });
        });
        return mobileConfiguration;
      }

      function buildInput($container, name, label, value, readOnly) {
        formBuilder.buildInput({
          container: $container,
          label: label,
          name: name,
          value: value,
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          readOnly: readOnly
        });
      }

      function buildTextArea($container, name, label, value, readOnly) {
        formBuilder.buildTextarea({
          container: $container,
          label: label,
          name: name,
          value: value,
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          readOnly: readOnly
        });
      }

      function buildCheckbox($container, name, label, value, readOnly) {
        formBuilder.buildCheckbox({
          container: $container,
          label: label,
          name: name,
          value: [],
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          items: [
            {
              id: value,
              text: ''
            }
          ],
          readOnly: readOnly,
          checked: value
        });
      }

      function buildSelect($container, name, label, value, options) {
        formBuilder.buildSelect2({
          container: $container,
          label: label,
          name: name,
          value: value,
          inputClass: 'w-custom-collect',
          labelColSpan: '3',
          controlColSpan: '9',
          select2: {
            data: options,
            valueField: name,
            defaultBlank: true,
            remoteSearch: false
          }
        });
      }

      DesignerUtils.initButtonEvent = function () {
        // 全展
        $('.btn_expand_all').live('click', function () {
          var treeObj = $.fn.zTree.getZTreeObj('div_field_tree_container');
          if (treeObj) {
            treeObj.expandAll(true);
          }
        });
        // 全缩
        $('.btn_collapse_all').live('click', function (event) {
          var treeObj = $.fn.zTree.getZTreeObj('div_field_tree_container');
          if (treeObj) {
            var root = treeObj.getNodes()[0];
            var nodes = root.children;
            for (var i = 0; i < nodes.length; i++) {
              treeObj.expandNode(nodes[i], false, true);
            }
          }
        });
        // 节点搜索
        var count = 0;
        var lastSearchName = '';
        $('#btn_search_tree').live('click', function () {
          var searchText = $('#search_tree_text').val();
          var treeObj = $.fn.zTree.getZTreeObj('div_field_tree_container');

          // 换搜索词了，需要重置
          if (lastSearchName != searchText) {
            count = 0;
          }
          // 通过名称模糊搜索，也可通过Id查找
          var nodes = treeObj.getNodesByParamFuzzy('name', searchText, null);
          lastSearchName = searchText;
          treeObj.cancelSelectedNode();
          // console.log( nodes );
          for (i = 0; i < nodes.length; i++) {
            if (i == count) {
              treeObj.selectNode(nodes[i]);
              // 实现自动打开
              treeObj.expandNode(nodes[i], true, false);
              count++;
              if (count >= nodes.length) {
                count = 0;
              }
              break;
            }
          }
        });

        // 重置字段
        $('#btn_reset_mobile_definition').on('click', function (event) {
          // 检查标签是否全部产生了uuid
          var html = getFormHtml();
          if (isFormEmpty(html)) {
            appModal.alert('请先进行存储表单设计，才能重置字段');
          } else if (confirm('确定要重置字段')) {
            var pFormUuid = formDefinition.pFormUuid;
            formDefinition.pFormUuid = null;
            DesignerUtils.initMformTreeInDesinger(pFormUuid);

            $('.div_property_defintion').hide();
            // treeObj = $.fn.zTree.init($("#div_field_tree_container"), mobileDyformDefintionZetting, [ ztreeJson ]);
            // mobileConfigurationChange();
            appModal.alert('重置完成！');
          }
        });
        // 合并字段
        $('#btn_merge_mobile_definition').on('click', function (event) {
          // 检查标签是否全部产生了uuid
          var html = getFormHtml();
          if (isFormEmpty(html)) {
            appModal.alert('请先进行存储表单设计，才能合并字段');
          } else if (confirm('确定要合并字段')) {
            var mobileConfiguration = collectMobileData(formDefinition, true);
            // mobileConfiguration转化成ztree数据
            var ztreeJson = convert2Ztree(mobileConfiguration);
            var treeObj = $.fn.zTree.getZTreeObj('div_field_tree_container');
            if (treeObj) {
              treeObj.destroy();
            }
            $('.div_property_defintion').hide();
            treeObj = $.fn.zTree.init($('#div_field_tree_container'), mobileDyformDefintionZetting, [ztreeJson]);
            mobileConfigurationChange();
            appModal.alert('合并完成！');
          }
        });
        $('#btn_del_mobile_block').on('click', function () {
          var treeObj = $.fn.zTree.getZTreeObj('div_field_tree_container');
          if (!treeObj) {
            return;
          }
          var nodes = treeObj.getSelectedNodes();
          if (nodes == null || nodes.length == 0) {
            appModal.alert('请选择区块节点');
            return;
          }
          var node = nodes[0];
          if (node.data.type != 'block') {
            appModal.alert('选择的节点不是区块节点');
            return;
          }
          if (node.children.length > 0) {
            appModal.alert('只允许删除没有子节点的区块');
            return;
          }
          if (confirm('确定删除该区块')) {
            var index = $.inArray(node.id, allMobileBlockCode);
            if (index != -1) {
              allMobileBlockCode.splice(index, 1);
            }
            treeObj.removeNode(node);
            mobileConfigurationChange();
          }
        });
        $('#btn_add_mobile_block').on('click', function () {
          var treeObj = $.fn.zTree.getZTreeObj('div_field_tree_container');
          if (!treeObj) {
            return;
          }
          var nodes = treeObj.getSelectedNodes();
          if (nodes.length == 0) {
            appModal.alert('请选择添加区块的节点');
            return;
          }
          var node = nodes[0];
          var rootNode = treeObj.getNodes()[0];
          var notLayout = rootNode.data.layouts == null || typeof rootNode.data.layouts === 'undefined';
          if (!notLayout && node.data.type != 'layout') {
            appModal.alert('只能在表单页签下添加区块');
            return;
          } else if (notLayout && node.data.type != 'dyform') {
            appModal.alert('只能在表单根节点下添加区块');
            return;
          }
          var $dialogDiv = $('#div_add_block_dialog');
          $dialogDiv.find('input').val('');
          $('#div_add_block_dialog').dialog({
            width: 340,
            buttons: {
              确认: function () {
                var blockCode = $(this).find('#mobileBlockCode').val();
                var blockTitle = $(this).find('#mobileBlockTitle').val();
                if (!blockCode) {
                  appModal.alert('请输入区块编码');
                  return;
                }
                var regu = '^[a-zA-Z_][0-9a-zA-Z_]*$';
                var re = new RegExp(regu);
                if (!re.test(blockCode)) {
                  appModal.alert('编码只允许数字、字母、下划线且不以数字开头!');
                  return;
                }
                if (!blockTitle) {
                  appModal.alert('请输入区块标题');
                  return;
                }
                if ($.inArray(blockCode, allMobileBlockCode) != -1) {
                  appModal.alert('区块编码重复！');
                  return;
                }
                if (formDefinition.getField(blockCode)) {
                  appModal.alert('区块编码与字段[' + formDefinition.getField(blockCode).displayName + ']的编码重复！');
                  return;
                }
                allMobileBlockCode.push(blockCode);
                treeObj.addNodes(node, [
                  {
                    id: blockCode,
                    name: blockTitle,
                    isParent: true,
                    children: [],
                    data: {
                      type: 'block',
                      blockCode: blockCode,
                      blockTitle: blockTitle,
                      isShow: true,
                      blockLayout: '1'
                    }
                  }
                ]);
                mobileConfigurationChange();
                $(this).dialog('close');
              },
              取消: function () {
                $(this).dialog('close');
              }
            }
          });
        });
        $('#btn_definition_reset').on('click', function () {
          var treeObj = $.fn.zTree.getZTreeObj('div_field_tree_container');
          var $container = $('.div_property_defintion_container');
          var type = $('#treeNodeType', $container).val();
          var id = $('#treeNodeId', $container).val();
          var node = treeObj.getNodeByParam('id', id);
          setDefinitionProperty(node);
          mobileConfigurationChange();
        });
        $('#btn_definition_ok').on('click', function () {
          var treeObj = $.fn.zTree.getZTreeObj('div_field_tree_container');
          var $container = $('.div_property_defintion_container');
          var id = $('#treeNodeId', $container).val();
          var type = $('#treeNodeType', $container).val();
          var node = treeObj.getNodeByParam('id', id);
          if (type == 'block') {
            var blockTitle = $('#mBlockTitle', $container).val();
            var blockCode = $('#mBlockCode', $container).val();
            var isShow = !$('[name="blockIsShow"]', $container).is(':checked');
            var blockLayout = $('#blockLayout', $container).val();
            var re = /^([a-zA-Z_][a-zA-Z0-9_]*)$/;
            if (!re.test(blockCode)) {
              alert(dymsg.blockCodeError);
              return false;
            }
            node.id = blockCode;
            node.name = blockTitle;
            node.data.isShow = isShow;
            node.data.blockCode = blockCode;
            node.data.blockTitle = blockTitle;
            node.data.blockLayout = blockLayout;
          }
          if (type == 'dyform') {
            var blockLayout = $('#blockLayout', $container).val();
            node.data.blockLayout = blockLayout;
          }
          if (type == 'field') {
            var displayName = $('#fieldDisplayName', $container).val();
            var tagName = $('#fieldTagName', $container).val();
            var name = $('#fieldId', $container).val();
            var fieldIsShow = !$("[name='fieldIsShow']", $container).is(':checked');
            var displayStyle = $('#fieldDisplayStyle', $container).val();
            node.id = name;
            node.name = node.data.displayName = displayName;
            node.data.tagName = tagName;
            node.data.isShow = fieldIsShow;
            node.data.displayStyle = displayStyle;
          }
          if (type == 'subform') {
            var displayName = $('#subformDisplayName', $container).val();
            var id = $('#subformId', $container).val();
            var subformIsShow = !$('[name="subformIsShow"]', $container).is(':checked');
            var rowTitle = $('#rowTitle', $container).val();
            var expandAllWhileReadonly = $('[name="expandAllWhileReadonly"]', $container).is(':checked');
            var displayAsTable = $('[name="displayAsTable"]', $container).is(':checked');
            var displayStyle = $('#subformDisplayStyle', $container).val();
            node.id = id;
            node.name = node.data.displayName = displayName;
            node.data.rowTitle = rowTitle;
            node.data.expandAllWhileReadonly = expandAllWhileReadonly;
            node.data.displayAsTable = displayAsTable;
            node.data.isShow = subformIsShow;
            node.data.displayStyle = displayStyle;
          }
          if (type == 'label') {
            var labelIsShow = !$('[name="labelIsShow"]', $container).is(':checked');
            node.data.isShow = labelIsShow;
          }

          if (type == 'layout') {
            var displayName = $('#displayName', $container).val();
            var name = $('#name', $container).val();
            var isActive = $('[name="isActive"]', $container).is(':checked');
            var hideBlockTitle = $('[name="hideBlockTitle"]', $container).is(':checked');
            node.name = node.data.displayName = displayName;
            node.data.isActive = isActive;
            node.data.hideBlockTitle = hideBlockTitle;
          }
          treeObj.updateNode(node);
          mobileConfigurationChange();
          appModal.alert('OK');
        });
      };
      // 重写和拓展基础方法
      DesignerUtils.setPageAndDialogTile = function (uuid) {
        if (StringUtils.isBlank(uuid)) {
          $('#title').html('新建手机单据');
          $('#title_h2').html('新建手机单据');
        } else {
          $('#title').html('(' + formDefinition.name + ')编辑----手机单据');
          $('#title_h2').html('(' + formDefinition.name + ')编辑表单定义----手机单据');
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

        var version = $('#version').val();
        var formType = DesignerUtils.getFormType();

        var htmlBodyContent = formDefinition.html;
        DesignerUtils.cleanUselessDefinition(htmlBodyContent, 'noEditor');
        // formDefinition是一个全局变量,用于保存定义
        formDefinition.id = tableId;
        formDefinition.uuid = uuid;
        formDefinition.name = mainTableCnName;
        formDefinition.code = tableNum;
        formDefinition.version = version;
        formDefinition.formType = formType;
        formDefinition.html = htmlBodyContent;
        formDefinition.events = JSON.stringify(formDefinition.events);
        formDefinition.systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        formDefinition.customJsModule = customJsModule;
        formDefinition.pFormUuid = DesignerUtils.getPformUuid();
        formDefinition.moduleId = $('#moduleId').val();
        formDefinition.mobileConfiguration = collectMobileData(formDefinition, false);
        return true;
      };
      DesignerUtils.initMformInfo = function (formDefinition) {
        var _this = this;
        $('#pformUuid').val(formDefinition.pFormUuid);
        $('#pformName').val(formDefinition.pFormUuid);
        $('#pformName')
          .on('change', function (event) {
            var pformName = _this.getPformName();
            var pformUuid = _this.getPformUuid();
            $('#refFormUuid').val(pformUuid);
            $('#refFormName').val(pformName);
            _this.initMformTreeInDesinger(pformUuid);
          })
          .wSelect2({
            serviceName: 'dyFormFacade',
            queryMethod: 'queryAllPforms',
            selectionMethod: 'getSelectedFormDefinition',
            labelField: 'pformName',
            valueField: 'pformUuid',
            defaultBlank: true,
            width: '100%',
            height: 250
          });
      };
      DesignerUtils.initModuleTree = function (defintionObj) {
        // 所属模块
        $('#moduleName')
          .wCommonComboTree({
            service: 'appProductIntegrationMgr.getTreeByDataType2',
            serviceParams: [['1', '2', '3'], '全局'],
            height: '280px',
            multiSelect: false, // 是否多选
            parentSelect: true, // 父节点选择有效，默认无效
            onAfterSetValue: function (event, self, value) {
              var valueNodes = self.options.valueNodes;
              var appName = [],
                appPath = [];
              if (valueNodes && valueNodes.length > 0) {
                appPath.push(valueNodes[0].id);
                appName.push(valueNodes[0].name);
              }
              $('#moduleName').val(appName.join(','));
              $('#moduleId').val(appPath.join(',')).trigger('change');
            }
          })
          .wCommonComboTree('setValue', defintionObj ? defintionObj.moduleId : '');
      };
      /**
       * 初始化存储单据对应的字段数据
       */
      DesignerUtils.initMformTreeInDesinger = function (pformUuid) {
        if (StringUtils.isBlank(pformUuid)) {
          return;
        }
        if (pformUuid === formDefinition.pFormUuid) {
          // 保存过
        } else {
          formDefinition = FormUtils.loadFormDefinition(pformUuid);
          formDefinition.uuid = '';
          formDefinition.pFormUuid = pformUuid;
        }
        renderMobileDesign(formDefinition);
      };

      // 初始化
      DesignerUtils.init = function (formUuid) {
        // 初始化开始
        window.formDefinition = new MainFormClass(); // 用于保存定义数据
        function autoWith() {
          var div_body_width = $(window).width() * 0.95;
          $('.form_header').css('width', div_body_width - 5);
          $('.div_body').css('width', div_body_width);
        }
        $(window).resize(autoWith).trigger('resize');

        // 动态表单单据关闭
        $('.form_header .form_close').click(function (event) {
          window.close();
        });

        var uuid = formUuid || '';
        // 保存、更新动态表单

        $('#btn_form_save').click(function (event) {
          if ($('#pformUuid').val() === '') {
            alert('请选择存储单据！');
            return;
          }
          DesignerUtils.validateAndSaveForm('0'); // 用于表示用户点击的是"保存"按钮
        });

        $('.btn-save-as-new-version').click(function (event) {
          DesignerUtils.validateAndSaveForm('1'); // 用于表示用户点击的是"版本升级"按钮
        });
        // 为已经初始化完成的各页面元素填充数据
        if (typeof uuid === 'undefined' || uuid == null || uuid == '') {
          $('.btn-save-as-new-version').remove(); // 对于"新建"页面, 去掉保存为新版本的功能
          DesignerUtils.setValidateOptions(); // 设置校验规则

          // 如果是"新建"，则“归属系统单位”的值为当前登录用户所属的系统单位
          DesignerUtils.doBindSystemUnitFromCurrentSession();
          var version = '1.0';
          $('#version').val(version);
          $('#dyformVersion').html(version);
        } else {
          formDefinition = loadFormDefinition(uuid); // 加载表单定义
          DesignerUtils.fillBasicPropertyTab(formDefinition); // 初始化页面中的"基本属性"tab
          DesignerUtils.fillEventTab(formDefinition); // 初始化页面中的"事件管理"tab
          DesignerUtils.setValidateOptions(uuid); // 设置校验规则
          setTimeout(function () {
            $('.nav-tabs li:eq(1) a').trigger('click');
          }, 50);
        }

        DesignerUtils.initModuleTree(formDefinition);
        // JS模块
        $('#customJsModule')
          .wSelect2({
            serviceName: 'appJavaScriptModuleMgr',
            params: {
              dependencyFilter: 'DyformDevelopment'
            },
            labelField: 'customJsModuleName',
            valueField: 'customJsModule',
            defaultBlank: true,
            remoteSearch: false,
            width: $('#customJsModule').parent('td').width(),
            height: 250
          })
          .val(formDefinition.customJsModule)
          .trigger('change');

        // 所属业务模块
        $('#moduleId')
          .val(formDefinition.moduleId)
          .wSelect2({
            valueField: 'moduleId',
            labelField: 'moduleName',
            remoteSearch: false,
            serviceName: 'appModuleMgr',
            queryMethod: 'loadSelectData',
            params: {
              //excludeIds:$("#moduleId").val(),
              //systemUnitId:server.SpringSecurityUtils.getCurrentUserUnitId()
            },
            width: $('#moduleId').parent('td').width()
          });
        if ($('#moduleId').val()) {
          //指定了所属模块，则不允许变更
          $('#moduleId').prop('readonly', true);
        }

        $.extend(formDefinition, formDefinitionMethod);
        try {
          DesignerUtils.setPageAndDialogTile(uuid); // 设置标题,ie8兼容性问题
        } catch (ex) {}

        DesignerUtils.initEvent();
      };
      // 初始化
      if (window.parent.initDyform && parent.initDyform.call(window, DesignerUtils) === false) {
        return;
      }
      DesignerUtils.init(formUuid);
      DesignerUtils.initMformInfo(formDefinition);
      DesignerUtils.initButtonEvent(formDefinition);
      DesignerUtils.initMformTreeInDesinger(DesignerUtils.getPformUuid());
      DesignerUtils.explainFormReferenceResouce();
      break;
  }
}
