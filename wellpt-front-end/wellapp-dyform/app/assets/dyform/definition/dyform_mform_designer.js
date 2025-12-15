$(function ($) {
  var showTabLayout = true;
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
          var editStr = "<button class='mui-btn' id='upBtn_" + treeNode.id + "' title='上移' onfocus='this.blur();'>&uarr;</button>";
          editStr += "<button class='mui-btn' id='downBtn_" + treeNode.id + "' title='下移' onfocus='this.blur();'>&darr;</button>";

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
    showConverTabs2BlockCheckbox(defintionObj, mobileConfiguration);
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

  function showConverTabs2BlockCheckbox(defintionObj, mobileConfiguration) {
    if (!defintionObj.layouts || $.isEmptyObject(defintionObj.layouts)) {
      return;
    }
    var $tbody = $('#pformUuid').closest('tbody');
    if ($('#showTabLayout', $tbody).length > 0) {
      return;
    }

    var checked = !$.isEmptyObject(mobileConfiguration.layouts);
    showTabLayout = checked;
    var checkboxHtml = `<tr>
      <td style="width: 10%">显示存储单据页签布局</td>
      <td style="width: 40%">
        <input type="checkbox" name="showTabLayout" id="showTabLayout" ${checked ? 'checked' : ''}/>
        <label class="checkbox-inline label-formbuilder" for="showTabLayout">&nbsp;</label>
      </td>
    </tr>`;
    $tbody.append(checkboxHtml);
    $('#showTabLayout', $tbody).on('change', function () {
      showTabLayout = !showTabLayout;
      delete defintionObj.mobileConfiguration;
      $('#pformUuid', $tbody).trigger('change');
    });
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
      if (nodeType == 'field' || nodeType == 'label' || nodeType == 'subform' || nodeType == 'tableView') {
        if (targetType == 'dyform') {
          var msg = nodeType == 'subform' ? '从表' : '字段';
          alert(msg + '只能在区块内移动');
          return false;
        } else if (targetType == 'block' || (targetType == 'layout' && (nodeType == 'subform' || nodeType == 'tableView'))) {
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
    if (data.type == 'dyform') {
      buildInput($container, '', '名称', treeNode.name, true);
      buildInput($container, '', 'ID', treeNode.id, true);
      var blockLayoutItems = [
        {
          id: '1',
          text: '默认布局'
        }
      ];
      // 页签布局不支持TabBar或者侧滑菜单栏，不支持嵌套
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
      buildIcon($container, 'blockIcon', '图标', data.blockIcon || '');
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
        inputMode == dyFormInputMode.ckedit ||
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
    if (data.type == 'tableView') {
      buildInput($container, 'tableViewName', '名称', data.text, true);
      buildInput($container, 'tableViewId', 'ID', data.id, true);
      buildInput($container, 'tableViewDisplayName', '显示名称', data.displayName || data.text);
      buildSelect2($container, 'tableViewWidgetUuid', '视图设置', data.tableViewWidgetUuid, {
        serviceName: 'appWidgetDefinitionMgr',
        labelField: 'tableViewWidgetName',
        valueField: 'tableViewWidgetUuid',
        params: {
          wtype: 'wMobileListView'
        },
        width: '100%',
        height: 250
      });
      buildIcon($container, 'tableViewIcon', '图标', data.icon || '');
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
      parentNode.data.subforms = {};
      parentNode.data.tableView = {};
    } else if (parentNode.data.type == 'block') {
      parentNode.data.fields = {};
      parentNode.data.subforms = {};
      parentNode.data.blocks = {};
      parentNode.data.labels = {};
      parentNode.data.tableView = {};
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
        } else if (nodeData.type == 'tableView') {
          configuration.tableView[node.id] = nodeData;
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

  function convertTableView2Ztree(root, tableView) {
    $.each(tableView || {}, function (index, view) {
      var tableViewNode = {
        id: view.id,
        name: view.text,
        children: [],
        data: $.extend(
          {
            type: 'tableView'
          },
          view
        )
      };
      root.children.push(tableViewNode);
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
      // 页签下的从表
      convertSubforms2Ztree(layoutNode, layout.subforms || {});
      // 页签下的视图展示
      convertTableView2Ztree(layoutNode, layout.tableView || {});
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
      block.blockTitle = block.blockTitle || $block.text() || block.blockTitle;
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
    var addedTableView = {};
    var blocks = defintionObj.blocks || {};
    var fields = defintionObj.fields || {};
    var layouts = defintionObj.layouts || {};
    var subforms = defintionObj.subforms || {};
    var tableView = defintionObj.tableView || {};
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
        tableView: {},
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
            displayStyle: 2, // 默认双行显示
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
      $block
        .closest('table')
        .find('table[tableviewid]')
        .each(function (i) {
          var $tableView = $(this);
          var tableViewId = $tableView.attr('tableviewid');
          var tableViewDef = tableView[tableViewId];
          if (tableViewId in addedTableView) {
            return;
          }
          // 如果没有表单定义也认为是个无效表单
          if (tableViewDef != null && $(this).find('th').size() > 0) {
            var tableViewNode = $.extend(true, tableViewDef, {
              id: tableViewId,
              text: tableViewDef.relaTableViewText,
              order: 1000 + i,
              blockCode: 'other'
            });
            blockNode.tableView[tableViewId] = tableViewNode;
            addedTableView[tableViewId] = tableViewNode;
          }
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
        tableView: {},
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
    $html.find('table[tableviewid]').each(function (i) {
      var $tableView = $(this);
      var tableViewId = $tableView.attr('tableviewid');
      var tableViewDef = tableView[tableViewId];
      if (tableViewId in addedTableView) {
        return;
      }
      // 如果没有表单定义也认为是个无效表单
      if (tableViewDef != null && $(this).find('th').size() > 0) {
        var tableViewNode = $.extend(true, tableViewDef, {
          id: tableViewId,
          text: tableViewDef.relaTableViewText,
          order: 1000 + i,
          blockCode: 'other'
        });
        blockNode.tableView[tableViewId] = tableViewNode;
      }
    });
    if (
      !$.isEmptyObject(blockNode.fields) ||
      !$.isEmptyObject(blockNode.subforms) ||
      !$.isEmptyObject(blockNode.labels) ||
      !$.isEmptyObject(blockNode.tableView)
    ) {
      mobileConfiguration.blocks[blockNode.blockCode] = blockNode;
    }
    // 页签布局
    var isTabLayout = false;
    if (showTabLayout) {
      $html.find('.layout[name]').each(function (layoutIndex) {
        mobileConfiguration.layouts = mobileConfiguration.layouts || {};
        var $layout = $(this);
        var layoutCode = $layout.attr('container') || $layout.attr('name');
        var layoutDef = layouts[layoutCode] || {};
        var subtabs = layoutDef.subtabs || {};
        var $tabContent = $layout.find('.subtab-design[name]');
        $tabContent = $tabContent.length > 0 ? $tabContent : $layout;
        $tabContent.each(function (tabIdx) {
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

          // 页签下的从表
          $tab.find('table[formuuid]').each(function (i) {
            var $subform = $(this);
            var formUuid = $subform.attr('formuuid');
            var otherBlock = mobileConfiguration.blocks.other || {};
            if (otherBlock.subforms && otherBlock.subforms[formUuid]) {
              tabNode.subforms = tabNode.subforms || {};
              tabNode.subforms[formUuid] = otherBlock.subforms[formUuid];
              delete otherBlock.subforms[formUuid];
            }
          });

          // 页签下的视图展示
          $tab.find('table[tableviewid]').each(function (i) {
            var $tableView = $(this);
            var tableViewId = $tableView.attr('tableviewid');
            var otherBlock = mobileConfiguration.blocks.other || {};
            if (otherBlock.tableView && otherBlock.tableView[tableViewId]) {
              tabNode.tableView = tabNode.tableView || {};
              tabNode.tableView[tableViewId] = otherBlock.tableView[tableViewId];
              delete otherBlock.tableView[tableViewId];
            }
          });

          mobileConfiguration.layouts[layoutId] = tabNode;
          isTabLayout = true;
        });
      });
      if (isTabLayout) {
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
        // 其他区块、页签为空，删除掉
        let otherLayout = mobileConfiguration.layouts.other || {};
        let otherBlocks = otherLayout.blocks || {};
        let otherBlock = otherBlocks.other;
        if (
          otherBlock &&
          $.isEmptyObject(otherBlock.fields) &&
          $.isEmptyObject(otherBlock.subforms) &&
          $.isEmptyObject(otherBlock.labels)
        ) {
          delete otherBlocks.other;
        }
        if (
          otherLayout &&
          $.isEmptyObject(otherLayout.blocks) &&
          $.isEmptyObject(otherLayout.fields) &&
          $.isEmptyObject(otherLayout.subforms) &&
          $.isEmptyObject(otherLayout.labels)
        ) {
          delete mobileConfiguration.layouts.other;
        }
      }
    }
    return mobileConfiguration;
  }

  function buildInput($container, name, label, value, readOnly) {
    // var readOnly = readOnly ? " readonly='readonly' " : '';
    // var html = '';
    // html += '<div  class="row">';
    // html += '	<label for="' + name + '" class="control-label">' + label + '</label>';
    // html += '	<div class="form-control">';
    // html += '		<input type="text" style="height:34px;width:300px" ' + readOnly + ' id="' + name + '" value="' + value + '" />';
    // html += '	</div>';
    // html += '</div>';
    // $container.append(html);
    formBuilder.buildInput({
      container: $container,
      label: label,
      name: name,
      value: value,
      readOnly: readOnly,
      labelColSpan: '3',
      controlColSpan: '9'
    });
  }

  function buildTextArea($container, name, label, value, readOnly) {
    // var readOnly = readOnly ? " readonly='readonly' " : '';
    // var html = '';
    // html += '<div  class="row">';
    // html += '	<label for="' + name + '" class="control-label">' + label + '</label>';
    // html += '	<div class="form-control">';
    // html += '		<textarea type="text" rows="4" style="width:300px" ' + readOnly + ' id="' + name + '">' + value + '</textarea>';
    // html += '	</div>';
    // html += '</div>';
    // $container.append(html);
    formBuilder.buildTextArea({
      container: $container,
      label: label,
      name: name,
      value: value,
      readOnly: readOnly,
      rows: '4',
      labelColSpan: '3',
      controlColSpan: '9'
    });
  }

  function buildCheckbox($container, name, label, value, readOnly) {
    // var html = '';
    var checked = value ? 'checked' : '';
    var readOnly = readOnly ? " readonly='readonly' " : '';
    // html += '<div class="row">';
    // html += '	<label for="' + name + '" class="control-label">' + label + '</label>';
    // html += '	<div class="form-control">';
    // html += '	<input type="checkbox" ' + checked + '  id="' + name + '" value="' + value + '" ' + readOnly + '/>';
    // html += '	</div>';
    // html += '</div>';
    // $container.append(html);
    var html = `<div class="form-group formbuilder clear form-group">
      <label class="col-xs-3 control-label  label-formbuilder" for="${name}">${label}</label>
      <div class="col-xs-9 controls">
        <input value="${value}" class="" name="${name}" type="checkbox" id="${name}" ${checked} ${readOnly ? 'disabled' : ''}>
          <label class="checkbox-inline label-formbuilder" for="${name}">&nbsp;</label>
      </div>
    </div>`;
    $container.append(html);
  }

  function buildSelect($container, name, label, value, options) {
    // var html = '';
    // html += '<div  class="row">';
    // html += '	<label for="' + name + '" class="control-label">' + label + '</label>';
    // html += '	<div class="form-control">';
    // html += '	<select height="34px"  style="width:310px"  id="' + name + '">';
    // $.each(options, function (index, item) {
    //   var checked = item.id == value ? 'selected' : '';
    //   html += '<option value="' + item.id + '" ' + checked + '>' + item.text + '</opton>';
    // });
    // html += '	</select>';
    // html += '</div>';
    // html += '</div>';
    // $container.append(html);

    formBuilder.buildSelect({
      container: $container,
      label: label,
      name: name,
      value: value,
      items: options,
      inputClass: 'select-full-width',
      labelColSpan: '3',
      controlColSpan: '9'
    });
    $('#' + name, $container).css({ width: '100%' });
  }

  function buildSelect2($container, name, label, value, select2Options) {
    formBuilder.buildSelect2({
      container: $container,
      label: label,
      name: name,
      value: value,
      select2: select2Options,
      inputClass: 'select-full-width',
      labelColSpan: '3',
      controlColSpan: '9'
    });
    $('#' + name, $container).trigger('change');
    // $('#' + name, $container).css({ width: '100%' });
  }

  function buildIcon($container, name, label, value, options) {
    // var readOnly = readOnly ? " readonly='readonly' " : '';
    // var html = '';
    // html += '<div  class="row" style="margin-bottom: 30px;">';
    // html += '	<label for="' + name + '" class="control-label">' + label + '</label>';
    // html += '	<div class="form-control" style="display: flex;">';
    // html += '		<input type="text" style="height:34px;width:300px" ' + readOnly + ' id="' + name + '" value="' + value + '" />';
    // html += '		<span id="span_' + name + '" class="' + value + '"></span>';
    // html += '	</div>';
    // html += '</div>';
    // $container.append(html);

    formBuilder.buildInput({
      container: $container,
      label: label,
      name: name,
      value: value,
      labelColSpan: '3',
      controlColSpan: '9'
    });
    $('#' + name, $container)
      .parent()
      .css({ display: 'flex' })
      .append($(`<span id="span_${name}" class="${value}" style="margin: 6px 0 0 3px;"></span>`));

    $('#' + name, $container).on('click', function () {
      $.WCommonPictureLib.show({
        value: value,
        selectTypes: ['3'],
        confirm: function (data) {
          var icon = data.filePaths;
          $('#span_' + name, $container).removeClass($('#' + name, $container).val());
          $('#' + name, $container).val(icon);
          $('#span_' + name, $container).addClass(icon);
        }
      });
    });
  }

  DesignerUtils.initButtonEvent = function () {
    // 全展
    $('#btn_expand_all').on('click', function () {
      var treeObj = $.fn.zTree.getZTreeObj('div_field_tree_container');
      if (treeObj) {
        treeObj.expandAll(true);
      }
    });
    // 全缩
    $('#btn_collapse_all').on('click', function (event) {
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
        oAlert2('请先进行存储表单设计，才能重置字段');
      } else if (confirm('确定要重置字段')) {
        var pFormUuid = formDefinition.pFormUuid;
        formDefinition.pFormUuid = null;
        DesignerUtils.initMformTreeInDesinger(pFormUuid);

        $('.div_property_defintion').hide();
        // treeObj = $.fn.zTree.init($("#div_field_tree_container"), mobileDyformDefintionZetting, [ ztreeJson ]);
        // mobileConfigurationChange();
        oAlert2('重置完成！');
      }
    });
    // 合并字段
    $('#btn_merge_mobile_definition').on('click', function (event) {
      // 检查标签是否全部产生了uuid
      var html = getFormHtml();
      if (isFormEmpty(html)) {
        oAlert2('请先进行存储表单设计，才能合并字段');
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
        oAlert2('合并完成！');
      }
    });
    $('#btn_del_mobile_block').on('click', function () {
      var treeObj = $.fn.zTree.getZTreeObj('div_field_tree_container');
      if (!treeObj) {
        return;
      }
      var nodes = treeObj.getSelectedNodes();
      if (nodes == null || nodes.length == 0) {
        oAlert2('请选择区块节点');
        return;
      }
      var node = nodes[0];
      if (node.data.type != 'block') {
        oAlert2('选择的节点不是区块节点');
        return;
      }
      if (node.children.length > 0) {
        oAlert2('只允许删除没有子节点的区块');
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
        oAlert2('请选择添加区块的节点');
        return;
      }
      var node = nodes[0];
      var rootNode = treeObj.getNodes()[0];
      var notLayout = rootNode.data.layouts == null || typeof rootNode.data.layouts === 'undefined';
      if (!notLayout && node.data.type != 'layout') {
        oAlert2('只能在表单页签下添加区块');
        return;
      } else if (notLayout && node.data.type != 'dyform') {
        oAlert2('只能在表单根节点下添加区块');
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
              oAlert2('请输入区块编码');
              return;
            }
            var regu = '^[a-zA-Z_][0-9a-zA-Z_]*$';
            var re = new RegExp(regu);
            if (!re.test(blockCode)) {
              oAlert2('编码只允许数字、字母、下划线且不以数字开头!');
              return;
            }
            if (!blockTitle) {
              oAlert2('请输入区块标题');
              return;
            }
            if ($.inArray(blockCode, allMobileBlockCode) != -1) {
              oAlert2('区块编码重复！');
              return;
            }
            if (formDefinition.getField(blockCode)) {
              oAlert2('区块编码与字段[' + formDefinition.getField(blockCode).displayName + ']的编码重复！');
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
      if (type == 'layout') {
        var hideBlockTitle = $('#hideBlockTitle', $container).is(':checked');
        node.data.hideBlockTitle = hideBlockTitle;
      }
      if (type == 'block') {
        var blockTitle = $('#mBlockTitle', $container).val();
        var blockCode = $('#mBlockCode', $container).val();
        var isShow = !$('#blockIsShow', $container).is(':checked');
        var blockLayout = $('#blockLayout', $container).val();
        var blockIcon = $('#blockIcon', $container).val();
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
        node.data.blockIcon = blockIcon;
      }
      if (type == 'dyform') {
        var blockLayout = $('#blockLayout', $container).val();
        node.data.blockLayout = blockLayout;
      }
      if (type == 'field') {
        var displayName = $('#fieldDisplayName', $container).val();
        var tagName = $('#fieldTagName', $container).val();
        var name = $('#fieldId', $container).val();
        var fieldIsShow = !$('#fieldIsShow', $container).is(':checked');
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
        var subformIsShow = !$('#subformIsShow', $container).is(':checked');
        var rowTitle = $('#rowTitle', $container).val();
        var expandAllWhileReadonly = $('#expandAllWhileReadonly', $container).is(':checked');
        var displayAsTable = $('#displayAsTable', $container).is(':checked');
        var displayStyle = $('#subformDisplayStyle', $container).val();
        node.id = id;
        node.name = node.data.displayName = displayName;
        node.data.rowTitle = rowTitle;
        node.data.expandAllWhileReadonly = expandAllWhileReadonly;
        node.data.displayAsTable = displayAsTable;
        node.data.isShow = subformIsShow;
        node.data.displayStyle = displayStyle;
      }
      if (type == 'tableView') {
        var displayName = $('#tableViewDisplayName', $container).val();
        var tableViewWidgetUuid = $('#tableViewWidgetUuid', $container).val();
        node.data.displayName = displayName;
        node.data.tableViewWidgetUuid = tableViewWidgetUuid;
        JDS.call({
          service: 'appWidgetDefinitionMgr.getBean',
          data: [tableViewWidgetUuid],
          async: false,
          success: function (result) {
            node.data.tableViewWidgetId = result.data.id;
          }
        });
        var tableViewIcon = $('#tableViewIcon', $container).val();
        node.data.icon = tableViewIcon;
      }
      if (type == 'label') {
        var labelIsShow = !$('#labelIsShow', $container).is(':checked');
        node.data.isShow = labelIsShow;
      }
      treeObj.updateNode(node);
      mobileConfigurationChange();
      oAlert('OK');
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
    var titleType = $("input[name='titleType']:checked").val() == '2' && $('#titleContent2').val() != '' ? '2' : '1';
    var titleContent = $('#titleContent' + titleType).val();

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
    formDefinition.titleType = titleType;
    formDefinition.titleContent = titleContent;
    formDefinition.html = htmlBodyContent;
    if (typeof formDefinition.events == 'object') {
      formDefinition.events = JSON.stringify(formDefinition.events);
    }
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
    $('#pformUuid')
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
      })
      .trigger('change');
  };
  DesignerUtils.initModuleTree = function (defintionObj) {
    // 所属业务模块
    $('#moduleId')
      .wSelect2({
        labelField: 'moduleName',
        valueField: 'moduleId',
        remoteSearch: false,
        defaultBlank: true,
        serviceName: 'appModuleMgr',
        queryMethod: 'loadSelectData',
        params: {
          includeSuperAdmin: true, //是否包含超管的模块
          //excludeIds:$("#moduleId").val(),
          systemUnitId: server.SpringSecurityUtils.getCurrentUserUnitId()
        }
      })
      .trigger('change');
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
  DesignerUtils.init = function () {
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

    var uuid = $('#formUuid').val();
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
    if ($.trim($('#moduleId').val()).length <= 0) {
      $('#moduleId').val(formDefinition.moduleId);
    }
    $('#moduleId')
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
      })
      .trigger('change');
    if ($('#moduleId').val()) {
      //指定了所属模块，则不允许变更
      $('#moduleId').prop('readonly', true);
    }

    //表单标题
    var titleType = formDefinition.titleType || '1';
    if (titleType == '2') {
      $('#titleContent2').val(formDefinition.titleContent || '');
    }
    $('#titleContent1').val('${表单名称}_${年}-${月}-${日}');

    $("input[name='titleType']")
      .off()
      .on('change', function () {
        var val = $("input[name='titleType']:checked").val();
        $('#title_set')[val == '2' ? 'show' : 'hide']();
        $('.title_content').hide();
        $('.title_content' + val).show();
      });
    $("input[name='titleType'][value='" + titleType + "']")
      .attr('checked', true)
      .trigger('change');

    $('#title_set')
      .off()
      .on('click', function () {
        var _html = get_title_expression();
        var $dialog = appModal.dialog({
          title: '表单标题设置',
          message: _html,
          size: 'large',
          shown: function () {
            $('textarea', $dialog).val($('#titleContent2').val());
            setTitle_expressionSelect($dialog, 'defaultField', true, [
              '表单名称',
              '表单ID',
              '表单编号',
              '当前登录用户姓名',
              '当前登录用户所在部门名称',
              '当前登录用户所在部门名称全路径',
              '年',
              '简年',
              '月',
              '日',
              '时',
              '分',
              '秒'
            ]);

            var formFieldData = [];
            var fields = {};
            var formTree = formDefinition.getFormTree();

            function getFormFilds(tree, id, name) {
              $.each(tree, function (index, item) {
                if (!fields[id]) {
                  fields[id] = {
                    label: name,
                    labelID: id,
                    optgroup: []
                  };
                }
                if (item.nodeType == 'field') {
                  fields[id].optgroup.push({
                    value: item.fieldName,
                    name: item.name
                  });
                }
                if ((item.nodeType == 'block' || item.nodeType == 'tab' || item.nodeType == 'subTab') && item.children.length > 0) {
                  getFormFilds(item.children, id, name);
                }
                if (item.nodeType == 'template' && item.children.length > 0) {
                  getFormFilds(item.children, item.templateUuid, '子表单-' + item.name);
                }
              });
            }
            getFormFilds(formTree, formDefinition.id, formDefinition.name);

            for (var i in fields) {
              formFieldData.push(fields[i]);
            }
            $(top.document).on('click', function (e) {
              if ($('.choose-item', $dialog)[0] === $(e.target).parents('.well-select')[0]) return;
              $('.choose-item', $dialog).removeClass('well-select-visible');
            });
            setTitle_expressionSelect($dialog, 'formField', true, formFieldData);

            $.ajax({
              type: 'get',
              url: ctx + '/api/basicdata/system/param/query',
              data: {
                keyword: ''
              },
              async: false,
              dataType: 'json',
              success: function (result) {
                var sysArgs = [];
                $.each(result.data, function (index, item) {
                  sysArgs.push({
                    name: item.name,
                    value: item.key
                  });
                });
                setTitle_expressionSelect($dialog, 'systemArgs', true, sysArgs);
              }
            });

            $('.choose-item', $dialog)
              .off()
              .on('click', function (e) {
                var $this = $(this);
                e.stopPropagation();
                $this.toggleClass('well-select-visible');
                $('.choose-item', $dialog).each(function () {
                  var _$this = $(this);
                  if (!_$this.is($this)) {
                    _$this.removeClass('well-select-visible');
                  }
                });
              });

            $('.well-select-input', $dialog)
              .off()
              .on('input propertychange', function () {
                var $that = $(this);
                var keyword = $.trim($that.val()).toUpperCase();
                var $wellSelect = $that.closest('.well-select');
                var $wellSelectItem = $wellSelect.find('.well-select-item');
                var $wellSelectNotFound = $wellSelect.find('.well-select-not-found');
                var showNum = 0;
                $wellSelectItem.each(function () {
                  var $this = $(this);
                  var value = $this.data('value').toString();
                  var name = $this.data('name').toString();
                  if (value.toUpperCase().indexOf(keyword) > -1 || name.toUpperCase().indexOf(keyword) > -1) {
                    $this.show();
                    showNum++;
                  } else {
                    $this.hide();
                  }
                });
                if (showNum) {
                  $wellSelectNotFound.hide();
                } else {
                  $wellSelectNotFound.show();
                }
              });

            $('.well-select-dropdown', $dialog)
              .off()
              .on('click', function (e) {
                e.stopPropagation();
              });
            $('.well-select-item', $dialog)
              .off()
              .on('click', function (e) {
                var $titleControl = $('#titleControl', $dialog)[0];
                var value = $titleControl.value;
                var start = $titleControl.selectionStart;
                var value1 = value.substr(0, start);
                var value2 = value.substr(start);
                var finalValue = value1 + '${' + $(this).attr('data-value') + '}' + value2;
                $('textarea', $dialog).val(finalValue);
                $('.choose-item', $dialog).removeClass('well-select-visible');
              });
          },
          buttons: {
            ok: {
              label: '保存',
              className: 'well-btn w-btn-primary',
              callback: function () {
                $('#titleContent2').val($('textarea', $dialog).val());
              }
            },
            cancel: {
              label: '关闭',
              className: 'btn-default'
            }
          }
        });
      });

    function setTitle_expressionSelect($dialog, id, showSearch, data) {
      $dialog
        .find('#' + id)
        .append(
          '<div class="well-select-dropdown" x-placement="bottom-start"' +
            '    style="position: absolute; left: 0; top: 34px; width: 300px;">' +
            '    <div class="well-select-search" style="display: ' +
            (showSearch ? 'block' : 'none') +
            ';"><input class="well-select-input" placeholder="搜索">' +
            '        <div class="search-icon"><i class="iconfont icon-ptkj-sousuochaxun"></i></div>' +
            '    </div>' +
            '    <ul class="well-select-not-found" style="display: none;">' +
            '        <li>无匹配数据</li>' +
            '    </ul>' +
            '    <ul class="well-select-dropdown-list"></ul>' +
            '</div>'
        );

      if (id == 'defaultField') {
        $.each(data, function (i, item) {
          $dialog
            .find('#' + id + ' .well-select-dropdown-list')
            .append('<li class="well-select-item" data-name="' + item + '" data-value="' + item + '"><span>' + item + '</span></li>');
        });
      } else if (id == 'systemArgs') {
        $.each(data, function (i, item) {
          $dialog
            .find('#' + id + ' .well-select-dropdown-list')
            .append(
              '<li class="well-select-item" data-name="' +
                item.name +
                '" data-value="' +
                item.value +
                '"><span>' +
                item.name +
                '</span></li>'
            );
        });
      } else if (id == 'formField') {
        $.each(data, function (i, item) {
          var html = '';
          $.each(item.optgroup, function (index, iitem) {
            html +=
              '<li class="well-select-item" data-name="' +
              iitem.name +
              '" data-value="' +
              iitem.value +
              '"><span title="' +
              iitem.value +
              '">' +
              iitem.name +
              '</span></li>';
          });
          $dialog
            .find('#formField .well-select-dropdown-list')
            .append(
              $(
                '<li class="well-select-group"' + '"><div class="well-select-group-label"><span>' + item.label + '</span></div></li>'
              ).append($('<ul class="well-select-group-list"></ul>').html(html))
            );
        });
      }
    }

    function get_title_expression() {
      return (
        '<div class="title_expression_wrap">' +
        '<div class="tip">' +
        '<span>' +
        '<i class="iconfont icon-ptkj-xinxiwenxintishi"></i>' +
        '在下方编辑表单标题表达式，可插入表单内置变量、表单字段、系统参数和文本。' +
        '</span>' +
        '</div>' +
        '<div class="content">' +
        '<div class="choose-btns clear">' +
        '<div id="defaultField" class="choose-item well-select"><span>插入表单内置变量</span><i class="iconfont icon-ptkj-xianmiaojiantou-xia well-select-arrow"></i></div>' +
        '<div id="formField" class="choose-item well-select"><span>插入表单字段</span><div class="title-tooltip"><i class="iconfont icon-ptkj-tishishuoming"></i><div class="title-tooltip-content">如果单据设计中字段被删除，则字段列表和标题中都会删除该字段</div></div><i class="iconfont icon-ptkj-xianmiaojiantou-xia well-select-arrow"></i></div>' +
        '<div id="systemArgs" class="choose-item well-select"><span>插入系统参数</span><i class="iconfont icon-ptkj-xianmiaojiantou-xia well-select-arrow"></i></div></div>' +
        '<textarea id="titleControl" class="form-control" rows="10"></textarea>' +
        '</div>' +
        '<div class="bottom-tip">样例：${表单名称}_${当前登录用户姓名}(${年}${月}${日})</div>' +
        '</div>'
      );
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
  DesignerUtils.init();
  DesignerUtils.initMformInfo(formDefinition);
  DesignerUtils.initButtonEvent(formDefinition);
  DesignerUtils.initMformTreeInDesinger(DesignerUtils.getPformUuid());
});
