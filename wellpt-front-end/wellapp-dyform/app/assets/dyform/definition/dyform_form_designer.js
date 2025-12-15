// 加载全局国际化资源
I18nLoader.load('/js/pt/js/global');
// 加载动态表单定义模块国际化资源
I18nLoader.load('/dyform/commons/dyform');
+(function ($) {
  'use strict';

  var formBean = {
    uuid: null,
    createTime: null,
    creator: null,
    modifyTime: null,
    modifier: null,
    recVer: null,
    id: null,
    code: null,
    isUp: null,
    name: null,
    remark: null,
    version: null,
    tableName: null,
    relationTbl: null,
    formType: null,
    definitionJson: null,
    enableSignature: null,
    events: null,
    pFormUuid: null,
    systemUnitId: null,
    customJsModule: null,
    refDesignerFormUuid: null,
    moduleId: null
  };
  window.formDefinition = null;
  var DesignerUtils = window.DesignerUtils || {};
  DesignerUtils.getFormType = function () {
    return $('#formType').val();
  };
  DesignerUtils.isFormTypeAsVform = function () {
    return FormUtils.isFormTypeAsVform(DesignerUtils.getFormType());
  };
  DesignerUtils.isFormTypeAsMform = function () {
    return FormUtils.isFormTypeAsMform(DesignerUtils.getFormType());
  };
  DesignerUtils.isFormTypeAsPform = function () {
    return FormUtils.isFormTypeAsPform(DesignerUtils.getFormType());
  };
  DesignerUtils.isFormTypeAsCform = function () {
    return FormUtils.isFormTypeAsCform(DesignerUtils.getFormType());
  };
  DesignerUtils.isFormTypeAsMstform = function () {
    return FormUtils.isFormTypeAsMstform(DesignerUtils.getFormType());
  };
  // 获取默认的HINT
  DesignerUtils.getDesingerDefaultHtml = function () {
    var html = '';
    html +=
      "<div id='user__hint' style='color:gray;height:30px;line-height:30px;text-align:center'>请在白色背景区域编辑,以免造成样式混乱</div>";
    // html += "<p>&nbsp;</p>";
    return html;
  };
  /**
   * 判断设计器中是否已有值, 没值返回true, 有值返回false;
   *
   * @returns {Boolean}
   */
  DesignerUtils.isDesignerHtmlContentIsBlank = function () {
    var data = editor.getData();
    return StringUtils.isBlank(data) || $(data).attr('id') == 'user__hint';
  };

  // 收集用户配置信息
  DesignerUtils.collectFormDatas = function () {
    var uuid = $('#formUuid').val();
    var version = $('#version').val();
    var formType = $('#formType').val();
    var tableId = $('#tableId').val();
    var tableNum = $('#tableNum').val();
    var customJsModule = $('#customJsModule').val();
    var customJsModuleName = $('#customJsModuleName').val();
    var titleType = $("input[name='titleType']:checked").val() == '2' && $('#titleContent2').val() != '' ? '2' : '1';
    var titleContent = $('#titleContent' + titleType).val();
    var mainTableEnName = $('#mainTableEnName').val();
    var mainTableCnName = $('#mainTableCnName').val();
    var formSign = $('input[name=formSign]:checked').val();
    var defaultVFormUuid = $('#defaultVFormUuid').val();
    var defaultMFormUuid = $('#defaultMFormUuid').val();
    var moduleId = $('#moduleId').val();
    var moduleName = $('#moduleName').val();
    var repositoryMode = $('#repositoryMode').val();
    var userTableName = $('#userTableName').val();
    var serviceUrl = $('#serviceUrl').val();
    var serviceToken = $('#serviceToken').val();
    var customInterface = $('#customInterface').val();
    var customInterfaceName = $('#customInterfaceName').val();
    var editForm = $('#editForm').prop('checked');

    var htmlBodyContent = editor.getData();
    DesignerUtils.cleanUselessDefinition(htmlBodyContent);
    formDefinition.uuid = uuid;
    formDefinition.version = version;
    formDefinition.formType = formType;
    if (typeof uuid === 'undefined' || uuid == null || uuid == '' || uuid == 'null') {
      formDefinition.id = tableId.toLowerCase(); // 转成小写;
      formDefinition.tableName = 'uf_' + tableId.toLowerCase(); // 转成小写
    } else {
      formDefinition.id = tableId;
      formDefinition.tableName = mainTableEnName.toLowerCase(); // 转成小写
      // 兼容导入的旧表单
      formDefinition.id = formDefinition.id || formDefinition.name;
      formDefinition.tableName = formDefinition.tableName || formDefinition.outerId;
    }
    formDefinition.code = tableNum;
    formDefinition.name = mainTableCnName;
    formDefinition.html = htmlBodyContent;
    formDefinition.enableSignature = formSign;
    formDefinition.events = JSON.stringify(this.events);
    formDefinition.systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
    formDefinition.customJsModule = customJsModule;
    formDefinition.customJsModuleName = customJsModuleName;
    formDefinition.titleType = titleType;
    formDefinition.titleContent = titleContent;
    formDefinition.defaultVFormUuid = defaultVFormUuid;
    formDefinition.defaultMFormUuid = defaultMFormUuid;
    formDefinition.moduleId = moduleId;
    formDefinition.moduleName = moduleName;
    formDefinition.editForm = editForm;
    // 表单存储
    var repository = {
      mode: repositoryMode,
      userTableName: userTableName,
      serviceUrl: serviceUrl,
      serviceToken: serviceToken,
      customInterface: customInterface,
      customInterfaceName: customInterfaceName
    };
    formDefinition.repository = repository;
    return formDefinition;
  };
  // 清除无效的字段定义
  window.cleanUselessDefinition = DesignerUtils.cleanUselessDefinition = function (srcHtml, type) {
    if (type === 'noEditor') {
      var editor = null;
    }
    srcHtml = srcHtml || (editor && editor.getData());
    var $html = $('<span>').html(srcHtml);
    var fields = formDefinition.fields;
    for (var fieldName in fields) {
      if (fields[fieldName].master === true) {
        continue;
      } else if ($html.find(".value[name='" + fieldName + "']").size() == 0) {
        // 在模板中没有找到该字段,则删除该字段的定义
        console.log(fieldName + ' 对应的占位符已被删除，故删除对应的字段定义');
        DesignerUtils.deleteTitleField(fieldName, fields[fieldName].displayName);
        formDefinition.deleteField(fieldName);
      } else if (fields[fieldName].unPersistentName) {
        delete fields[fieldName].unPersistentName;
      } else if ($html.find(".value[name='" + fieldName + "']").attr('inputmode') === '130') {
        //排除真实值占位符
        delete fields[fieldName];
        delete formDefinition.databaseFields[fieldName];
      }
    }

    var placeholderCtr = formDefinition.placeholderCtr;
    for (var placeholder in placeholderCtr) {
      if ($html.find(".value[name='" + placeholder + "']").size() == 0) {
        delete placeholderCtr[placeholder];
      }
    }

    var subforms = formDefinition.subforms;
    for (var formUuid in subforms) {
      if ($html.find("table[formUuid='" + formUuid + "']").size() == 0) {
        // 在模板中没有找到该字段,则删除该从表
        // 的定义
        console.log(formUuid + ' 对应的占位符已被删除，故删除对应的从表定义');
        delete subforms[formUuid];
      }
    }
    var tableViews = formDefinition.tableView;
    for (var tableViewId in tableViews) {
      if ($html.find('table[tableviewid="' + tableViewId + '"]').length == 0) {
        // 模板中没有找到视图列表，则删除视图列表定义
        delete tableViews[tableViewId];
      }
    }

    var layouts = formDefinition.layouts;
    for (var name in layouts) {
      if ($html.find(".tab-design[name='" + name + "']").size() == 0) {
        // 在模板中没有找到该字段,则删除该从表
        // 的定义
        console.log(name + ' 对应的占位符已被删除，故删除对应的布局定义');
        delete layouts[name];
      }
    }

    var blocks = formDefinition.blocks;
    if (typeof blocks != 'undefined') {
      for (var blockCode in blocks) {
        if ($html.find("[blockCode='" + blockCode + "']").size() == 0) {
          // 在模板中没有找到该字段,则删除该区块的定义
          console.log(blockCode + ' 对应的占位符已被删除，故删除对应的区块');
          delete blocks[blockCode];
        }
      }
    }
    var templates = formDefinition.templates;
    if (typeof templates != 'undefined') {
      for (var blockCode in templates) {
        if ($html.find(".template-wrapper[templateuuid='" + blockCode + "']").size() == 0) {
          // 在模板中没有找到该字段,则删除该子表单的定义
          console.log(blockCode + ' 对应的占位符已被删除，故删除对应的子表单');
          delete templates[blockCode];
        }
      }
    }
    if (formDefinition.defaultFormData) {
      delete formDefinition.defaultFormData;
    }
    if (formDefinition.subformDefinitions) {
      delete formDefinition.subformDefinitions;
    }
  };

  DesignerUtils.deleteTitleField = function (fieldName, name) {
    if ($("input[name='titleType']:checked").val() == '2') {
      var data = $('#titleContent2').val();

      while (data.indexOf('${' + name + '(dyform.' + fieldName + ')}') > -1) {
        data = data.split('${' + name + '(dyform.' + fieldName + ')}').join('');
      }
      $('#titleContent2').val(data);
    }
  };

  // 校验保存
  DesignerUtils.validateAndSaveForm = function (evenSource) {
    formDefinition.updateFormTree();
    $('.nav-tabs li:eq(0) a').trigger('click');
    setTimeout(function () {
      var tableId = $('#tableId').val();
      var isValid = validator.form();
      if (!isValid) {
        // 验证不通过
        return;
      } else if ((DesignerUtils.isFormTypeAsMstform() || DesignerUtils.isFormTypeAsPform()) && !/^[a-zA-Z0-9_]+$/.test(tableId)) {
        // 存储单据才做这个检验
        alert(' ID只能包含英文字母、数字以及下划线_ ，例如：user_info');
        $('#tableId').focus();
        return;
      }
      DesignerUtils.saveForm(evenSource);
    }, 30);
  };
  // getDesignerHtmlContent
  DesignerUtils.getDesignerHtmlContent = function () {
    return editor.getData();
  };
  // fillFormDesigner
  DesignerUtils.fillFormDesigner = function (html) {
    CKEDITOR.instances.moduleText.setData(html);
  };
  // fillBasicPropertyTab
  DesignerUtils.fillBasicPropertyTab = function (defintionObj) {
    var copyFormUuid = $('#formCopy').val();
    if (StringUtils.isNotBlank(copyFormUuid)) {
      defintionObj.id = '';
      defintionObj.version = '';
      defintionObj.formUuid = '';
      defintionObj.tableName = '';
      // 隐藏另存为按钮
      $('.btn-save-as-new-version').hide();
      $('#formUuid').val(defintionObj.formUuid);
    } else {
      $('#tableId').attr('readonly', true); // 编辑时,表名只读
      $('#mainTableEnName').attr('readonly', true); // 编辑时,表名只读
    }
    $('#tableId').val(defintionObj.id); // 该表对外暴露出去的id
    $('#mainTableEnName').val(defintionObj.tableName);
    $('#mainTableCnName').val(defintionObj.name);
    document.title = defintionObj.name || '单据设计器';
    $('input[name=formSign]').attr('checked', defintionObj.enableSignature ? true : false);
    $('#tableNum').val(defintionObj.code);
    var version = StringUtils.isBlank(defintionObj.version) ? '1.0' : defintionObj.version;
    $('#version').val(version);
    $('#dyformVersion').html(version);
    if (null == defintionObj.editForm) {
      defintionObj.editForm = true; // 默认可编辑
    }
    $('#editForm').prop('checked', defintionObj.editForm);
    DesignerUtils.doBindSystemUnitAs(defintionObj.systemUnitId);
  };
  DesignerUtils.initModuleTree = function (defintionObj) {
    var tid = null;
    var jsTemplates = $('#js-tab-content-control2>ul.field-list');

    function renderFieldsTree(treeNodes) {
      // console.log("------renderFieldsTree");
      // 全局在最上面
      treeNodes.sort(function (a, b) {
        return a.id === 'Global' ? -1 : 1;
      });
      jsTemplates.empty();
      for (var i = 0; i < treeNodes.length; i++) {
        var category = treeNodes[i];
        var categoryItem = $('<li>', {
          class: 'field-category category-close' // + (i === 0 ? "open" : "close")
        });
        var header = $('<span>', {
          class: 'header',
          'data-id': category.id
        })
          .append(
            $('<i>', {
              class: 'iconfont icon-ptkj-xianmiaojiantou-you' // + (i === 0 ? "icon-ptkj-xianmiaojiantou-xia" : "icon-ptkj-xianmiaojiantou-you")
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
            'need-grid': needGrid,
            title: treeNode.name + '[' + treeNode.url + ']' + (treeNode.path ? ':\n' + treeNode.path : ''),
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
    }

    if (DesignerUtils.isFormTypeAsMstform() || DesignerUtils.isFormTypeAsPform()) {
      $('#moduleId').on('change', function (event) {
        var moduleId = $(this).val();
        tid && clearTimeout(tid);
        tid = setTimeout(function (t) {
          JDS.call({
            service: 'formCommonFieldDefinitionService.queryFieldsTreeByModuleId',
            data: [moduleId, ''],
            success: function (result) {
              if (result.success) {
                renderFieldsTree(result.data);
              }
            }
          });
          tid = null;
        }, 0);
      });
      $('#field-tabs>.nav>li:eq(1)').on('click', function (event) {
        var moduleId = $('#moduleId').val();
        if ($.trim(moduleId).length <= 0) {
          return alert('请先设置“基本属性”-“所属模块”');
        }
      });
    }
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
    if ($('#moduleId').val()) {
      // 指定了所属模块，则不允许变更
      //	            $("#moduleId").prop('readonly',true);
    }
    // 所属模块
    //			$("#moduleName").wCommonComboTree({
    //				service : "appProductIntegrationMgr.getTreeByDataType2",
    //				serviceParams : [ [ "1", "2", "3" ], "全局" ],
    //				height : "280px",
    //				multiSelect : false, // 是否多选
    //				parentSelect : true, // 父节点选择有效，默认无效
    //				onAfterSetValue : function(event, self, value) {
    //					var valueNodes = self.options.valueNodes;
    //					var appName = [], appPath = [];
    //					if (valueNodes && valueNodes.length > 0) {
    //						appPath.push(valueNodes[0].id);
    //						appName.push(valueNodes[0].name);
    //					}
    //					$("#moduleName").val(appName.join(","));
    //					$("#moduleId").val(appPath.join(",")).trigger("change");
    //				}
    //			}).wCommonComboTree("setValue", defintionObj ? defintionObj.moduleId : "");
  };
  // fillEventTab
  DesignerUtils.fillEventTab = function (defintionObj) {
    var dyformEvents;
    if (typeof defintionObj.events === 'string') {
      dyformEvents = $.parseJSON(defintionObj.events);
    } else {
      dyformEvents = defintionObj.events;
    }
    if (dyformEvents) {
      this.events = dyformEvents;
    }
  };

  /**
   * 绑定"所属系统单位"的值为当前登录用户会话中的系统单位
   *
   * @returns
   */
  DesignerUtils.doBindSystemUnitFromCurrentSession = function () {
    var currentUserUnitId = SpringSecurityUtils.getCurrentUserUnitId();
    var currentUserUnitName = SpringSecurityUtils.getCurrentUserUnitName();
    formDefinition.systemUnitId = currentUserUnitId;
    DesignerUtils.showSystemUnitInfo(currentUserUnitId, currentUserUnitName);
  };

  DesignerUtils.doBindSystemUnitAs = function (systemUnitId) {
    var systemUnitName = SystemUnit.getNameById(systemUnitId);
    DesignerUtils.showSystemUnitInfo(systemUnitId, systemUnitName);
  };

  DesignerUtils.showSystemUnitInfo = function (systemUnitId, systemUnitName) {
    $('.system-unit').html(systemUnitName + '(' + systemUnitId + ')');
  };

  DesignerUtils.setPageAndDialogTile = function (uuid) {
    if (StringUtils.isBlank(uuid)) {
      $('#title').html('新建');
      $('#title_h2').html('新建');
    } else {
      $('#title').html('(' + formDefinition.name + ')编辑----');
      $('#title_h2').html('(' + formDefinition.name + ')编辑----');
    }
  };
  // 移出表单设计器占位符
  DesignerUtils.removeHintInDesigner = function () {
    var _hintelement = editor.document.find('#user__hint');
    if (_hintelement.count() > 0) {
      _hintelement.getItem(0).remove();
    }
  };

  DesignerUtils.initLog = function () {
    $('#list').jqGrid(
      $.extend($.common.jqGrid.settings, {
        url: ctx + '/common/jqgrid/query?queryType=dyFormChangeLog',
        postData: {
          queryPrefix: 'query',
          queryOr: true,
          query_LIKES_formUuid: $('#formUuid').val()
        },
        datatype: 'json',
        mtype: 'POST',
        colNames: ['uuid', '登录名', '姓名', '部门', '操作时间', '旧表单内容', '新表单内容', '登录IP'],
        colModel: [
          {
            name: 'uuid',
            index: 'uuid',
            hidden: true,
            key: true
          },
          {
            name: 'loginName',
            index: 'loginName',
            width: '100'
          },
          {
            name: 'userName',
            index: 'userName',
            width: '100'
          },
          {
            name: 'departmentJob',
            index: 'departmentJob',
            width: '150'
          },
          {
            name: 'createTime',
            index: 'createTime',
            width: '100'
          },
          {
            name: 'oldContentJson',
            index: 'oldContentJson',
            width: '200',
            formatter: function (cellvalue, options, rowObject) {
              return $('<div/>').text(cellvalue).html();
            }
          },
          {
            name: 'newContentJson',
            index: 'newContentJson',
            width: '200',
            formatter: function (cellvalue, options, rowObject) {
              return $('<div/>').text(cellvalue).html();
            }
          },
          {
            name: 'loginIp',
            index: 'loginIp',
            width: '100'
          }
        ],
        rowNum: 20,
        shrinkToFit: true,
        rownumbers: true,
        rowList: [10, 20, 50, 100, 200],
        rowId: 'uuid',
        pager: '#pager',
        sortname: 'createTime',
        recordpos: 'right',
        viewrecords: true,
        sortable: true,
        sortorder: 'desc',
        multiselect: false,
        autowidth: false,
        height: 450,
        scrollOffset: 0,
        jsonReader: {
          root: 'dataList',
          total: 'totalPages',
          page: 'currentPage',
          records: 'totalRows',
          repeatitems: false
        },
        loadComplete: function (data) { },
        ondblClickRow: function (id) {
          var rowData = $('#list').jqGrid('getRowData', id);
          DesignerUtils.showCompareDialog(rowData);
        }
      })
    );
  };
  // added by linxr,表单事件
  DesignerUtils.initEvent = function () {
    // 事件框初始化
    var _this = this;
    if (!this.events) {
      _this.events = {};
    }

    var setting = {
      check: {
        enable: true
      },
      data: {
        simpleData: {
          enable: true
        }
      },
      callback: {
        onClick: zTreeOnDblClick
      }
    };

    // 如果事件框有内容，往checkbox打钩
    var checkEventNode = function (eventNodeId, flag) {
      var treeNode = _this.eventTreeObj.getNodeByParam('id', eventNodeId, null);
      if (treeNode == null) {
        return false;
      }
      _this.eventTreeObj.setChkDisabled(treeNode, false);
      _this.eventTreeObj.checkNode(treeNode, flag, false, false);
      _this.eventTreeObj.setChkDisabled(treeNode, true);
    };

    // 事件框事件
    var eventCallback = function (param) {
      var script = param.value;
      var nodes = treeObj.getSelectedNodes();
      for (var i = 0; i < nodes.length; i++) {
        var treeNode = nodes[i];
        if ($.trim(script).length > 0) {
          _this.events[treeNode.id] = script;
          checkEventNode(treeNode.id, true);
        } else {
          delete _this.events[treeNode.id];
          checkEventNode(treeNode.id, false);
        }
      }
    };

    var zRootNodes = [
      {
        id: 'beforeInit_event',
        chkDisabled: true,
        pId: 0,
        name: '表单初始化前事件',
        open: true
      },
      {
        id: 'afterInit_event',
        chkDisabled: true,
        pId: 0,
        name: '表单初始化后事件',
        open: true
      },
      {
        id: 'beforeSetData_event',
        chkDisabled: true,
        pId: 0,
        name: '表单设值前事件',
        open: true
      },
      {
        id: 'beforeSave_event',
        chkDisabled: true,
        pId: 0,
        name: '表单保存前事件',
        open: true
      },
      {
        id: 'afterSave_event',
        chkDisabled: true,
        pId: 0,
        name: '表单保存后事件',
        open: true
      }
    ];

    var aceCoderPlugin = {};
    for (var i = 0, len = zRootNodes.length; i < len; i++) {
      $('#dyformScriptContent').append(
        $('<div>', {
          id: 'codeContainer_' + zRootNodes[i].id
        })
      );
      aceCoderPlugin[zRootNodes[i].id] = $.fn.aceBinder({
        container: '#codeContainer_' + zRootNodes[i].id,
        id: zRootNodes[i].id,
        iframeId: 'dyformScriptContentIframe_' + zRootNodes[i].id,
        value: _this.events[zRootNodes[i].id],
        varSnippets: 'dyform.' + zRootNodes[i].id,
        codeHis: {
          enable: true,
          relaBusizUuid: $('#tableId').val(), //表单ID
          codeType: 'dyform.' + zRootNodes[i].id
        },
        valueChange: function (v) {
          eventCallback.call(_this, {
            value: v,
            $ace: this
          });
        }
      });
      if (i != 0) aceCoderPlugin[zRootNodes[i].id].getContainer().hide();
    }

    function zTreeOnDblClick(event, treeId, treeNode) {
      if (!treeNode) {
        return;
      }
      var id = treeNode.id;
      $("div[id^='codeContainer_']").hide();
      aceCoderPlugin[id].getContainer().show();
      if (_this.events[id]) {
        aceCoderPlugin[id].setValue(_this.events[id]);
      } else {
        aceCoderPlugin[id].setValue('');
      }
    }

    function initEventTreeNode(events) {
      for (var i in events) {
        if (events.hasOwnProperty(i)) {
          if (events[i].length > 0) {
            checkEventNode(i, true);
          } else {
            checkEventNode(i, false);
          }
        }
      }
    }

    // 初始化树
    var treeObj = (_this.eventTreeObj = $.fn.zTree.init($('#dyFormEventTree'), setting, zRootNodes));
    initEventTreeNode(this.events);
  };
  DesignerUtils.showCompareDialog = function (rowData) {
    var html = '<div id="compareDialog" style="display:none;text-align:left;">';
    html += "<div>旧表单<br/><textarea style='height:200px' >" + rowData.oldContentJson + '</textarea></div>';
    html += "<div>新表单<br/><textarea style='height:200px' >" + rowData.newContentJson + '</textarea></div>';
    html += '</div>';
    $('body').append(html);
    $('#compareDialog').dialog({
      autoOpen: true,
      height: 600,
      width: 1000,
      modal: true,
      title: '比较',
      beforeClose: function () {
        $(this).remove();
      },
      buttons: {
        确定: function () {
          $(this).dialog('close');
        },
        取消: function () {
          $(this).dialog('close');
        }
      }
    });
  };
  DesignerUtils.saveRefs = function (formDefinition) {
    var refIds = [];
    var formUuid = formDefinition.uuid;
    var arr = [formDefinition.fields, formDefinition.subforms];
    for (var i = 0; i < arr.length; i++) {
      var fields = arr[i];
      for (var fieldName in fields) {
        var field = fields[fieldName];
        if ($.trim(field.refId).length > 0) {
          refIds.push(field.refId);
        }
      }
    }
    JDS.call({
      service: 'formCommonFieldRefService.saveRefs',
      data: [formUuid, refIds],
      success: function (result) { }
    });
  };
  DesignerUtils.getFormDefinition = function (evenSource) {
    var formDefinitionClone = window.formDefinitionClone || $.extend(true, {}, formDefinition);
    formDefinition.isUp = evenSource; // 是否表单版本升级1:为升级;0为保存或更新

    DesignerUtils.collectFormDatas();

    // 存储服务检验
    var repository = formDefinition.repository;
    if (repository && repository.mode) {
      var repositoryMode = repository.mode;
      if (repositoryMode == '2' && StringUtils.isBlank(repository.userTableName)) {
        alert('表名不能为空！');
        return;
      }
      if (repositoryMode == '3' || repositoryMode == '4') {
        if (StringUtils.isBlank(repository.serviceUrl)) {
          alert('服务地址不能为空！');
          return;
        }
        if (StringUtils.isBlank(repository.serviceToken)) {
          alert('Access Token不能为空！');
          return;
        }
      }
      if (repositoryMode == '5' && StringUtils.isBlank(repository.customInterface)) {
        alert('存储接口不能为空！');
        return;
      }
    }
    if (!formDefinition) {
      throw new Error('unknown formDefinition');
    }

    var formDefinitionCopy = {};
    $.extend(formDefinitionCopy, formDefinition);

    var deletedFieldNames = [];
    if (formDefinitionCopy) {
      if (typeof formDefinitionCopy.deletedFieldNames != 'undefined') {
        deletedFieldNames = formDefinitionCopy.deletedFieldNames;
        delete formDefinitionCopy.deletedFieldNames;
      }
    }

    formDefinitionCopy.definitionJson = JSON.cStringify(formDefinitionCopy);
    if (deletedFieldNames && deletedFieldNames.length > 0) {
      // 删除提醒
      var deletedFieldDisplayNames = [];
      for (var i = 0; i < deletedFieldNames.length; i++) {
        var deletedFieldName = deletedFieldNames[i];
        var deletedField = formDefinitionClone.getField(deletedFieldName);
        if (deletedField) {
          deletedFieldDisplayNames.push(deletedField.displayName);
        } else {
          deletedFieldDisplayNames.push(deletedFieldName);
        }
      }
      if (!confirm('确定删除以下字段吗?\n' + deletedFieldDisplayNames.join(','))) {
        return false;
      }
    }

    if (formDefinitionCopy) {
      if (formDefinitionCopy.fields) delete formDefinitionCopy.fields;
      if (formDefinitionCopy.subforms) delete formDefinitionCopy.subforms;
      if (formDefinitionCopy.html) delete formDefinitionCopy.html;

      if (formDefinitionCopy.layouts) {
        delete formDefinitionCopy.layouts;
      }
      if (formDefinitionCopy.blocks) {
        delete formDefinitionCopy.blocks;
      }
      if (formDefinitionCopy.templates) {
        delete formDefinitionCopy.templates;
      }
      if (formDefinitionCopy.tableView) {
        delete formDefinitionCopy.tableView;
      }
      if (formDefinitionCopy.mobileConfiguration) {
        delete formDefinitionCopy.mobileConfiguration;
      }
      for (var npKey in formDefinitionCopy) {
        // 删除非持久化的字段
        if (formBean && formBean.hasOwnProperty(npKey) === false) {
          delete formDefinitionCopy[npKey];
        }
      }
    }
    return {
      formDefinition: formDefinitionCopy,
      deletedFieldNames: deletedFieldNames
    };
  };
  // 保存
  DesignerUtils.saveForm = function (evenSource) {
    var formDefinitionData = DesignerUtils.getFormDefinition(evenSource);
    var uuid = $('#formUuid').val();
    var url = contextPath + '/pt/dyform/definition/update';
    if (uuid == '' || uuid == null || typeof uuid == 'undefined') {
      url = contextPath + '/pt/dyform/definition/save';
    } else if (evenSource == '0' && !confirm('确定要更新表单定义吗?')) {
      return false;
    } else if (evenSource == '1' && !confirm('确定要升级表单定义版本吗?')) {
      return false;
    } else if (!formDefinitionData || !formDefinitionData.formDefinition) {
      return false;
    }
    appModal.showMask();
    $.ajax({
      url: url,
      type: 'POST',
      data: {
        formDefinition: JSON.cStringify(formDefinitionData.formDefinition),
        deletedFieldNames: JSON.cStringify(formDefinitionData.deletedFieldNames)
      },
      dataType: 'json',
      async: true,
      timeout: 120000,
      contentType: 'application/x-www-form-urlencoded;charset=utf-8',
      success: function (result) {
        appModal.hideMask();
        if (result.success == 'true' || result.success == true) {
          $('body').trigger('ace_$saveCodeHis'); //触发代码变更历史保存
          appModal.info('保存成功！');
          //刷新模块管理的表单表格
          if (window.opener && window.opener.appContext) {
            var $tableWidget = window.opener.appContext.getWidgetByCssSelector("[w-name='平台管理_产品集成_表单视图表格']");
            if ($tableWidget) {
              $tableWidget.trigger('RefreshDyformDefinitionTable');
            }
          }
          var formUuid = result.data;
          if (DesignerUtils.isFormTypeAsPform()) {
            window.location.href = contextPath + '/pt/dyform/definition/pform-designer?uuid=' + formUuid;
          } else if (DesignerUtils.isFormTypeAsCform()) {
            window.location.href = contextPath + '/pt/dyform/definition/cform-designer?uuid=' + formUuid;
          } else if (DesignerUtils.isFormTypeAsVform()) {
            window.location.href = contextPath + '/pt/dyform/definition/vform-designer?uuid=' + formUuid;
          } else if (DesignerUtils.isFormTypeAsMform()) {
            window.location.href = contextPath + '/pt/dyform/definition/mform-designer?uuid=' + formUuid;
          } else if (DesignerUtils.isFormTypeAsMstform()) {
            window.location.href = contextPath + '/pt/dyform/definition/mstform-designer?uuid=' + formUuid;
          } else {
            throw new Error('未知类型');
          }
        } else {
          appModal.alert('保存失败\n' + result.data);
        }
      },
      error: function (result) {
        var responseText = result.responseText;
        try {
          var errorObj = eval('(' + responseText + ')');
          appModal.alert('保存失败\n' + errorObj.data);
        } catch (e) {
          appModal.alert(JSON.cStringify(result));
        }
      }
    });
  };
  // 设置校验规则
  DesignerUtils.setValidateOptions = function (uuid) {
    var validateRules = {};
    var validateMessages = {};
    uuid = $('#formUuid').val();
    if (typeof uuid === 'undefined' || uuid == null || uuid == '' || uuid == 'null') {
      // 新增
      validateRules = {
        tableId: {
          required: true,
          maxlength: 29,
          remote: {
            url: ctx + '/common/validate/check/exists',
            type: 'POST',
            async: false,
            data: {
              uuid: function () {
                return $('#formUuid').val();
              },
              checkType: 'formDefinition',
              fieldName: 'id',
              fieldValue: function () {
                return $('#tableId').val();
              }
            }
          }
        },
        mainTableCnName: {
          required: true
        },
        moduleName: {
          required: true
        }
      };
      validateMessages = {
        tableId: {
          required: 'ID不能为空！',
          maxlength: 'ID不得超过 {0}个字符.',
          remote: '该ID已存在!',
          regex: 'ID名须以 UF_开头'
        },
        mainTableCnName: {
          required: '表单名称不能为空!'
        },
        mainTableEnName: {
          required: '数据库名称不能为空!',
          remote: '该数据库名称已存在!',
          maxlength: $.validator.format('数据库表名不得超过 {0}个字符.'),
          regex: '数据库表名须以 UF_ 开头'
        },
        moduleName: {
          required: '所属模块必填'
        }
      };
    } else {
      // 更新或者版本升级
      validateRules = {
        mainTableCnName: {
          required: true
        },
        moduleName: {
          required: true
        }
      };
      validateMessages = {
        mainTableCnName: {
          required: '表单名称不能为空!'
        },
        moduleName: {
          required: '所属模块必填'
        }
      };
    }
    window.validator = $('#mainForm').validate({
      rules: validateRules,
      messages: validateMessages,
      ignore: ''
    });
  };

  DesignerUtils.initList = function (formUuid) {
    var postData = {
      queryPrefix: 'query',
      queryOr: true
    };
    var form_def_list_selector = '#list';
    if (DesignerUtils.isFormTypeAsMstform() && formUuid) {
      postData['query_EQS_mstFormUuid'] = formUuid;
      postData['query_EQS_pTableName'] = formDefinition.tableName || 'tableName';
    } else if (DesignerUtils.isFormTypeAsPform() && formUuid) {
      postData['query_EQS_pFormUuid'] = formUuid;
      postData['query_EQS_pTableName'] = formDefinition.tableName || 'tableName';
    } else {
      return false;
    }
    $(form_def_list_selector).jqGrid({
      treeGrid: true,
      postData: postData,
      treeGridModel: 'adjacency',
      ExpandColumn: 'name',
      url: contextPath + '/pt/dyform/definition/list',
      mtype: 'POST',
      datatype: 'json',
      colNames: ['uuid', '单据名称', '单据类型(true)', '单据类型', '编号', '表单ID', '数据库表名', '版本'],
      colModel: [
        {
          name: 'uuid',
          index: 'uuid',
          width: 150,
          hidden: true,
          key: true
        },
        {
          name: 'name',
          index: 'name',
          width: 200
        },
        {
          name: 'formType',
          index: 'formType',
          width: 25,
          hidden: true
        },
        {
          name: 'formTypeName',
          index: 'formTypeName',
          width: 25
        },
        {
          name: 'code',
          index: 'code',
          width: 25
        },
        {
          name: 'id',
          index: 'id',
          width: 50
        },
        {
          name: 'tableName',
          index: 'tableName',
          width: 200
        },
        {
          name: 'version',
          index: 'version',
          width: 50
        }
      ],
      sortname: 'code',
      rowNum: 500,
      viewrecords: true,
      pager: '#pager',
      height: 'auto',
      scrollOffset: 0,
      jsonReader: {
        root: 'dataList',
        total: 'totalPages',
        page: 'currentPage',
        records: 'totalRows'
      },
      ondblClickRow: function (id) {
        // 双击视图打开新界面
        var uuid = id;
        if (uuid && uuid.length > 0) {
          var selectedRowData = $(form_def_list_selector).jqGrid('getRowData', id);
          FormUtils.openFormDefinition(selectedRowData['formType'], uuid);
        }
      },
      loadComplete: function (data) { },
      loadBeforeSend: function (xhr) {
        xhr.setRequestHeader('x-csrf-token', getCookie('_csrfToken'));
      }
    });
    $(form_def_list_selector).jqGrid('setGridWidth', $('div.tabbable').width() - 50);
    if (DesignerUtils.isFormTypeAsPform()) {
      $('#defaultVFormUuid').val(formDefinition.defaultVFormUuid);
      $('#defaultVFormName').val(formDefinition.defaultVFormUuid);
      $('#defaultVFormName')
        .wSelect2({
          params: {
            pFormUuid: formUuid
          },
          serviceName: 'dyFormFacade',
          queryMethod: 'queryAllVforms',
          selectionMethod: 'getSelectedFormDefinition',
          labelField: 'defaultVFormName',
          valueField: 'defaultVFormUuid',
          defaultBlank: true,
          height: 250
        })
        .trigger('change');
      var copyFormUuid = $('#formCopy').val();
      if (StringUtils.isNotBlank(copyFormUuid)) {
        formDefinition.defaultMFormUuid = '';
      }
      $('#defaultMFormUuid').val(formDefinition.defaultMFormUuid);
      $('#defaultMFormName').val(formDefinition.defaultMFormUuid);
      $('#defaultMFormName')
        .wSelect2({
          params: {
            pFormUuid: formUuid
          },
          serviceName: 'dyFormFacade',
          queryMethod: 'queryAllMforms',
          selectionMethod: 'getSelectedFormDefinition',
          labelField: 'defaultMFormName',
          valueField: 'defaultMFormUuid',
          defaultBlank: true,
          width: '100%',
          height: 250
        })
        .trigger('change');
    }
  };

  //二开设置表单标题
  DesignerUtils.setTitleContent = function (content) {
    $("input[name='titleType'][value='2']").attr('checked', true).trigger('change');
    $('#titleContent2').val(content);
  };

  var referenceRes = {
    datadic: [],
    serialnumer: [],
    jsModule: [],
    datastore: [],
    slaveForm: [],
    subForm: []
  };

  //解析各字段引用的资源
  DesignerUtils.explainFormReferenceResouce = function () {
    //checkbox
    var fields = formDefinition.databaseFields;
    for (var f in fields) {
      var im = fields[f].inputMode;
      //数据字典资源
      if (
        im == dyFormInputMode.checkbox ||
        im == dyFormInputMode.selectMutilFase ||
        im == dyFormInputMode.radio ||
        im == dyFormInputMode.comboSelect
      ) {
        if (fields[f].dictCode) {
          referenceRes.datadic.push(fields[f]);
        }
      }
      //流水号资源
      if (im == dyFormInputMode.serialNumber || im == dyFormInputMode.unEditSerialNumber) {
        referenceRes.serialnumer.push(fields[f]);
      }
      //数据仓库资源
      if (
        im == dyFormInputMode.checkbox ||
        im == dyFormInputMode.selectMutilFase ||
        im == dyFormInputMode.radio ||
        im == dyFormInputMode.comboSelect ||
        im == dyFormInputMode.treeSelect
      ) {
        if (fields[f].dataSourceId) {
          referenceRes.datastore.push(fields[f]);
        }
      }
    }
    //二开脚本依赖
    if (formDefinition.customJsModule) {
      var jsArr = formDefinition.customJsModule.split(';');
      for (var i = 0; i < jsArr.length; i++) {
        referenceRes.jsModule.push({
          js: jsArr[i]
        });
      }
    }
    console.log('引用资源详情：', referenceRes);

    var totalRes = {
      total: 0,
      rows: []
    };
    DesignerUtils.initRefChildFormTable(totalRes);
    DesignerUtils.initRefSubFormTable(totalRes);
    DesignerUtils.initRefJsModuleTable(totalRes);
    DesignerUtils.initRefSerialNumberTable(totalRes);
    DesignerUtils.initRefDataDicTable(totalRes);
    DesignerUtils.initRefDatastoreTable(totalRes);
    DesignerUtils.initRefTotalResourceTable(totalRes);
  };
  //
  DesignerUtils.initRefChildFormTable = function (totalRes) {
    var $table = $('#table_ref_template_form_info');
    var data = [];
    for (var k in formDefinition.templates) {
      var t = formDefinition.templates[k];
      //查询表单详细信息
      server.JDS.call({
        service: 'formDefinitionService.findDyFormFormDefinitionByFormUuid',
        data: [k],
        version: '',
        success: function (result) {
          var formDef = result.data;
          if (formDef) {
            data.push({
              uuid: k,
              name: t.templateName,
              version: formDef.version,
              tableName: formDef.tableName,
              id: formDef.id
            });
            totalRes.total++;
            totalRes.rows.push({
              uuid: totalRes.total,
              name: t.templateName,
              type: '子表单',
              refField: ''
            });
          }
        },
        error: function (jqXHR) { },
        async: false
      });
    }

    $table.bootstrapTable('destroy').bootstrapTable({
      data: data,
      idField: 'uuid',
      striped: false,
      showColumns: false,
      width: 500,
      search: true,
      columns: [
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'name',
          title: '单据名称',
          visible: true
        },
        {
          field: 'id',
          title: '表单ID',
          visible: true
        },
        {
          field: 'tableName',
          title: '数据库表名',
          visible: true
        },
        {
          field: 'version',
          title: '版本',
          visible: true
        }
      ],
      visibleSearch: true,
      showSearchButton: true
    });

    DesignerUtils.showResCountBadge('template_form_ref_res', data.length);
  };
  DesignerUtils.showResCountBadge = function (id, cnt) {
    $("a[aria-controls='" + id + "']").append(
      $('<span>', {
        class: 'badge pull-right'
      }).text(cnt)
    );
  };
  DesignerUtils.initRefSubFormTable = function (totalRes) {
    var $table = $('#table_ref_subform_info');
    var data = [];

    for (var k in formDefinition.subforms) {
      var sf = formDefinition.subforms[k];
      //查询表单详细信息
      server.JDS.call({
        service: 'formDefinitionService.findDyFormFormDefinitionByFormUuid',
        data: [k],
        version: '',
        success: function (result) {
          var formDef = result.data;
          if (formDef) {
            data.push({
              uuid: k,
              name: formDef.name,
              version: formDef.version,
              tableName: formDef.tableName,
              id: formDef.id
            });
            totalRes.total++;
            totalRes.rows.push({
              uuid: totalRes.total,
              name: sf.displayName,
              type: '从表',
              refField: ''
            });
          }
        },
        error: function (jqXHR) { },
        async: false
      });
    }

    $table.bootstrapTable('destroy').bootstrapTable({
      data: data,
      idField: 'uuid',
      striped: false,
      showColumns: false,
      width: 500,
      search: true,
      columns: [
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'name',
          title: '单据名称',
          visible: true
        },
        {
          field: 'id',
          title: '表单ID',
          visible: true
        },
        {
          field: 'tableName',
          title: '数据库表名',
          visible: true
        },
        {
          field: 'version',
          title: '版本',
          visible: true
        }
      ],
      visibleSearch: true,
      showSearchButton: true
    });
    DesignerUtils.showResCountBadge('subform_ref_res', data.length);
  };
  DesignerUtils.initRefJsModuleTable = function (totalRes) {
    var $refJsModuleTable = $('#table_ref_js_info');
    var data = [];
    for (var i = 0; i < referenceRes.jsModule.length; i++) {
      //查询表单详细信息
      server.JDS.call({
        service: 'appJavaScriptModuleMgr.getById',
        data: [referenceRes.jsModule[i].js],
        version: '',
        success: function (result) {
          var jsModule = result.data;
          if (jsModule) {
            data.push({
              uuid: i,
              jsName: jsModule.name
            });
            totalRes.total++;
            totalRes.rows.push({
              uuid: totalRes.total,
              name: jsModule.name,
              type: 'JS模块',
              refField: ''
            });
          }
        },
        error: function (jqXHR) { },
        async: false
      });
    }
    $refJsModuleTable.bootstrapTable('destroy').bootstrapTable({
      data: data,
      idField: 'uuid',
      striped: false,
      showColumns: false,
      width: 500,
      search: true,
      columns: [
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'jsName',
          title: '名称',
          visible: true
        }
      ],
      visibleSearch: true,
      showSearchButton: true
    });
    DesignerUtils.showResCountBadge('js_ref_res', data.length);
  };

  DesignerUtils.initRefSerialNumberTable = function (totalRes) {
    var $refSntable = $('#table_ref_sn_info');
    var data = [];
    for (var i = 0; i < referenceRes.serialnumer.length; i++) {
      var sn = referenceRes.serialnumer[i];
      if (sn.designatedId != '') {
        data.push({
          uuid: i,
          id: sn.designatedId,
          name: sn.designated,
          refField: sn.displayName
        });
        totalRes.total++;
        totalRes.rows.push({
          uuid: totalRes.total,
          name: sn.designated,
          type: '流水号',
          refField: sn.displayName
        });
      }
    }
    $refSntable.bootstrapTable('destroy').bootstrapTable({
      data: data,
      idField: 'uuid',
      striped: false,
      showColumns: false,
      width: 500,
      search: true,
      columns: [
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'name',
          title: '名称',
          visible: true
        },
        {
          field: 'id',
          title: 'ID',
          visible: true
        },
        {
          field: 'refField',
          title: '引用字段',
          visible: true
        },
        {
          field: 'operation',
          title: '操作',
          formatter: function (value, row, index) {
            return '<button class="well-btn w-btn-primary">操作</button>';
          }
        }
      ],
      visibleSearch: true,
      showSearchButton: true
    });
    DesignerUtils.showResCountBadge('serialnumber_ref_res', data.length);
  };
  DesignerUtils.initRefDataDicTable = function (totalRes) {
    var $refDatadicTable = $('#table_ref_datadic_info');
    var data = [];
    for (var i = 0; i < referenceRes.datadic.length; i++) {
      var dd = referenceRes.datadic[i];
      if (dd.dictCode != '') {
        var parts = dd.dictCode.split(':');
        data.push({
          uuid: i,
          id: parts[0],
          name: parts[1],
          refField: dd.displayName
        });
        totalRes.total++;
        totalRes.rows.push({
          uuid: totalRes.total,
          name: parts[1],
          type: '数据字典',
          refField: dd.displayName
        });
      }
    }
    $refDatadicTable.bootstrapTable('destroy').bootstrapTable({
      data: data,
      idField: 'uuid',
      striped: false,
      showColumns: false,
      width: 500,
      search: true,
      columns: [
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'name',
          title: '名称',
          visible: true
        },
        {
          field: 'id',
          title: 'ID',
          visible: true
        },
        {
          field: 'refField',
          title: '引用字段',
          visible: true
        },
        {
          field: 'operation',
          title: '操作',
          formatter: function (value, row, index) {
            return '<button class="well-btn w-btn-primary">操作</button>';
          }
        }
      ],
      visibleSearch: true,
      showSearchButton: true
    });
    DesignerUtils.showResCountBadge('datadic_ref_res', data.length);
  };
  DesignerUtils.initRefDatastoreTable = function (totalRes) {
    var $refDatastoreTable = $('#table_ref_datastore_info');
    var data = [];
    for (var i = 0; i < referenceRes.datastore.length; i++) {
      var dd = referenceRes.datastore[i];
      if (dd.dataSourceId != '') {
        data.push({
          uuid: i,
          id: dd.dataSourceId,
          name: dd.dataSourceText,
          refField: dd.displayName
        });
        totalRes.total++;
        totalRes.rows.push({
          uuid: totalRes.total,
          name: dd.dataSourceText,
          type: '数据仓库',
          refField: dd.displayName
        });
      }
    }
    $refDatastoreTable.bootstrapTable('destroy').bootstrapTable({
      data: data,
      idField: 'uuid',
      striped: false,
      showColumns: false,
      width: 500,
      search: true,
      columns: [
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'name',
          title: '名称',
          visible: true
        },
        {
          field: 'id',
          title: 'ID',
          visible: true
        },
        {
          field: 'refField',
          title: '引用字段',
          visible: true
        },
        {
          field: 'operation',
          title: '操作',
          formatter: function (value, row, index) {
            return '<button class="well-btn w-btn-primary">操作</button>';
          }
        }
      ],
      visibleSearch: true,
      showSearchButton: true
    });
    DesignerUtils.showResCountBadge('datastore_ref_res', data.length);
  };

  DesignerUtils.initRefTotalResourceTable = function (totalRes) {
    var $refTotalTable = $('#table_ref_total_info');
    $refTotalTable.bootstrapTable('destroy').bootstrapTable({
      data: totalRes.rows,
      idField: 'uuid',
      striped: false,
      showColumns: false,
      width: 500,
      search: true,
      columns: [
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'name',
          title: '名称',
          visible: true
        },
        {
          field: 'type',
          title: '类型',
          visible: true
        },
        {
          field: 'refField',
          title: '引用字段',
          visible: true
        },
        {
          field: 'operation',
          title: '操作',
          formatter: function (value, row, index) {
            return row.type == 'JS模块' ? '' : '<button class="well-btn w-btn-primary">操作</button>';
          }
        }
      ],
      visibleSearch: true,
      showSearchButton: true
    });
    DesignerUtils.showResCountBadge('all_ref_res', totalRes.rows.length);
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

    // $(window).resize(autoWith).trigger("resize");
    var self = this;
    // 动态表单单据关闭
    $('.form_header .form_close').click(function (e) {
      window.close();
    });

    var uuid = $('#formUuid').val();

    // 清除编辑器
    var instance = CKEDITOR.instances['moduleText'];
    if (instance) {
      CKEDITOR.remove(instance);
    }

    var jsTemplates = $('#js-tab-content-control>.js-tab-content-control');
    var ctlTemplateHtml = jsTemplates.find('.js-template').prop('outerHTML');
    var plugins = getControlPlugins();
    for (var i = 0; i < plugins.length; i++) {
      var code = plugins[i].code;
      var name = plugins[i].name;
      var $li = $(ctlTemplateHtml);
      $li.removeClass('js-template');
      $li.attr('plugin-code', code);
      $li.addClass('icon-' + code);
      $li.html(name);
      if (code === 'dysubform' || code === 'templatecontainer') {
        $li.attr('need-grid', 'false');
      }
      jsTemplates.append($li);
    }
    var jsTemplates2 = $('#js-tab-content-control2>.field-list');
    jsTemplates2.on('click', 'span.header', function (event) {
      var $this = $(this);
      var $parent = $this.parent();
      if ($parent.hasClass('category-open')) {
        $parent.removeClass('category-open').addClass('category-close');
        $this.find('i').removeClass('icon-ptkj-xianmiaojiantou-xia').addClass('icon-ptkj-xianmiaojiantou-you');
      } else {
        $parent.removeClass('category-close').addClass('category-open');
        $this.find('i').removeClass('icon-ptkj-xianmiaojiantou-you').addClass('icon-ptkj-xianmiaojiantou-xia');
      }
    });
    var sid = null;

    function searchFieldsTree(text) {
      sid && clearTimeout(sid);
      sid = setTimeout(function (t) {
        jsTemplates2.find('ul.js-tab-content-control').each(function (idx, elem) {
          var $elem = $(elem);
          if ($.trim(text).length <= 0) {
            return $elem.removeClass('search');
          }
          $elem.addClass('search');
          $elem.find('li[plugin-code]').each(function (idx, eItem) {
            var $eItem = $(eItem);
            var treeNode = $eItem.data('data-field');
            if (treeNode.name.indexOf(text) > -1 || treeNode.url.indexOf(text) > -1 || treeNode.iconSkin.indexOf(text) > -1) {
              $eItem.addClass('match-item');
            } else {
              $eItem.removeClass('match-item');
            }
          });
        });
        if ($.trim(text).length <= 0 || jsTemplates2.find('.match-item').length > 0) {
          jsTemplates2.removeClass('no-item');
        } else {
          jsTemplates2.addClass('no-item');
        }
        sid = null;
      }, 8);
    }

    var $searchText = $('#js-tab-content-control2 #search_tree_text').on('blur keypress input', function (event) {
      if (event.type === 'blur' || event.type === 'input' || event.keyCode == 13) {
        searchFieldsTree($(this).val());
      }
    });

    $('#js-tab-content-control2 #btn_search_tree').on('click', function (event) {
      var text = $searchText.val();
      if ($.trim(text).length <= 0) {
        return alert('请先输入字段名称');
      }
      return searchFieldsTree(text);
    });

    $('#js-tab-content-control2 #btn_collapse_all').click(function (event) {
      jsTemplates2.find('.category-close').each(function (idx, elem) {
        $(elem).removeClass('category-close').addClass('category-open');
        $(elem).find('i').removeClass('icon-ptkj-xianmiaojiantou-you').addClass('icon-ptkj-xianmiaojiantou-xia');
      });
    });
    $('#js-tab-content-control2 #btn_expand_all').click(function (event) {
      jsTemplates2.find('.category-open').each(function (idx, elem) {
        $(elem).removeClass('category-open').addClass('category-close');
        $(elem).find('i').removeClass('icon-ptkj-xianmiaojiantou-xia').addClass('icon-ptkj-xianmiaojiantou-you');
      });
    });

    var customCkeditorPath = staticPrefix + '/dyform/definition/ckeditor'; // 自定义ckeditor相关配置的路径
    CKEDITOR.plugins.basePath = customCkeditorPath + '/plugins/'; // 自定义ckeditor的插件路径

    // 初始化编辑器
    window.editor = CKEDITOR.replace(
      'moduleText',
      $.extend(
        {
          allowedContent: true,
          enterMode: CKEDITOR.ENTER_P,
          toolbarStartupExpanded: true,
          bodyId: 'testest',
          bodyClass: 'dyform',
          customConfig: customCkeditorPath + '/dyform_config.js',
          // 工具栏
          toolbar: [
            // ['Undo','Redo'],
            ['Bold', 'Italic', 'Underline'],
            // ['Cut','Copy','Paste'],
            // ['NumberedList','BulletedList','-'],
            ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock'],
            ['Format', 'Font', 'FontSize'],
            ['TextColor', 'BGColor', 'Link'],
            [
              // 'Image',
              'Table'
            ],
            [
              CkPlugin.BLOCK,
              CkPlugin.TABS,
              CkPlugin.BUTTON,
              CkPlugin.TABLEVIEWCTL,
              CkPlugin.FILELIBRARYCTL,
              CkPlugin.TEMPLATECONTAINER,
              CkPlugin.TIPS
            ],
            ['dyformpreview'],
            ['propertiesDialog'], // ['copyForm'],
            ['hierarchyDialog'],
            ['Source', 'Maximize']
            // ,['titleClass','titleClass2','titleClass3']
          ],
          on: {
            instanceReady: function (ev) {
              DesignerUtils.initModuleTree(formDefinition);
              // 清除非放置落点的红色聚焦样式
              var clearFocusLocation = function () {
                var tdNodes = CKEDITOR.instances['moduleText'].document.find('.drop_focus_td');
                var count = tdNodes.count();
                for (var i = 0; i < count; i++) {
                  var node = tdNodes.getItem(i);
                  node.removeClass('drop_focus_td');
                }
              };

              // 解析拖拽的放置落点
              var explainDropLocationNode = function (ui) {
                var outOffset = $('#cke_moduleText').offset();
                var $toolBar = $('#cke_1_top'); // 工具栏

                var tdNodes = CKEDITOR.instances['moduleText'].document.find('td,p'); // 查找td和段落p的格子
                var count = tdNodes.count();
                clearFocusLocation();
                var scrollTop =
                  $('#cke_moduleText iframe')[0].contentWindow.pageYOffset ||
                  $('#cke_moduleText iframe').contents()[0].documentElement.scrollTop ||
                  $('#cke_moduleText iframe').contents()[0].body.scrollTop ||
                  0;
                var uiWidth = $(ui.helper).outerWidth();
                var uiHeight = $(ui.helper).outerHeight();
                var toolBarHeight = $toolBar.height();
                for (var i = 0; i < count; i++) {
                  var node = tdNodes.getItem(i);
                  var nodeOffset = $(node.$).offset();
                  var beginLeft = nodeOffset.left;
                  var endLeft = nodeOffset.left + node.$.offsetWidth;
                  var beginTop = nodeOffset.top;
                  var endTop = nodeOffset.top + node.$.offsetHeight;

                  // 真实左偏移量=ck编辑器内的左偏移量+控件宽度的一半
                  var inOffsetLeft = ui.offset.left - outOffset.left + uiWidth / 2;
                  // 真实顶部偏移量=ck编辑器内的顶部偏移量+控件高度的一半+竖直滚动距离
                  var inOffsetTop = ui.offset.top - outOffset.top - toolBarHeight + uiHeight / 2 + scrollTop;

                  // console.log(
                  //   'location:[%s,%s] , leftRange:[%s,%s] , topRange:[%s,%s] , scrollTop:[%s]',
                  //   inOffsetLeft,
                  //   inOffsetTop,
                  //   beginLeft,
                  //   endLeft,
                  //   beginTop,
                  //   endTop,
                  //   scrollTop
                  // );

                  if (beginLeft <= inOffsetLeft && endLeft >= inOffsetLeft && beginTop <= inOffsetTop && endTop >= inOffsetTop) {
                    // 在布局格子的范围内
                    return node;
                  }
                }
              };

              // 编辑器滚动出更多视图空间
              var scrollView = function (nodeOffset) {
                var $frameBody = $('#cke_moduleText iframe').contents().find('body');
                var nScrollTop = $frameBody[0].scrollTop;
                var seeHeight = $('#cke_moduleText iframe').contents()[0].documentElement.clientHeight; // 可视高度
                // 落点的顶部偏移量超过可视高度时候，向下滚动一段距离
                if (nodeOffset.top >= seeHeight + nScrollTop - 30) {
                  $frameBody.scrollTop(nScrollTop == 0 ? 100 : nScrollTop + 100);
                } else if (nScrollTop > 0 && nodeOffset.top <= nScrollTop) {
                  // 向上滚动一段距离
                  $frameBody.scrollTop(nScrollTop - 100);
                }
              };
              $(document.body)
                .on('renderFieldsTree', function (event) {
                  // 拖拽绑定
                  $('.jsCanDraggable').draggable({
                    helper: 'clone',
                    refreshPositions: false,
                    cursor: 'crosshair', // 拖拽的鼠标样式：十字
                    iframeFix: true,
                    drag: function (event, ui) {
                      // 拖拽过程中事件，实现红色虚框定位布局格子
                      var node = explainDropLocationNode(ui);
                      if (node) {
                        node.addClass('drop_focus_td'); // 点位到落点，展示红色虚框样式
                        scrollView($(node.$).offset());
                      }
                    }
                  });
                })
                .trigger('renderFieldsTree');

              // 触发放置事件
              $('#cke_moduleText').droppable({
                accept: '.jsCanDraggable', // 只接受放置匹配条件的控件
                drop: function (event, ui) {
                  var $editor = CKEDITOR.instances['moduleText'];
                  var node = explainDropLocationNode(ui);
                  var needGrid = $(ui.helper).attr('need-grid') == 'true';
                  if (!node && needGrid) {
                    // 找到落点格子，但是该控件是需要布局作为容器
                    return alert('在插入"' + $(ui.helper).text() + '"之前，请先添加布局表格！');
                  } else if (!node && !needGrid) {
                    // 未点位到布局格落点且不需要布局的控件，添加到ck的最后面
                    node = $editor.document.getBody().getLast();
                  }
                  var id = $(ui.helper).data('id');
                  var json = $(ui.helper).data('json');
                  var pluginCode = $(ui.helper).attr('plugin-code');
                  var vform = $(ui.helper).attr('vform') === 'true';
                  if (node) {
                    // 添加到布局落点里面
                    var brNode = node.findOne && node.findOne('br');
                    if (brNode) {
                      // 移除掉ck多余的br
                      brNode.remove();
                    }
                    clearFocusLocation();
                    var range = $editor.createRange();
                    range.moveToPosition(node, CKEDITOR.POSITION_BEFORE_END);
                    range.select();
                    if (id && json && (json.name || json.templateName)) {
                      var pformObj = {
                        refId: id,
                        refObj: json
                      };
                      var field = vform ? json : pformObj;
                      var ctlHtml = null;
                      if (pluginCode === CkPlugin.SUBFORM) {
                        formDefinition.subforms[json.formUuid] = field;
                        ctlHtml = '<img data-is-temp="true" class="value" formuuid="' + json.formUuid + '" name="' + json.name + '">';
                      } else if (pluginCode === CkPlugin.TEMPLATECONTAINER) {
                        ctlHtml =
                          '<img data-is-temp="true" class="value" templateUuid="' +
                          json.templateUuid +
                          '" templateName="' +
                          json.templateName +
                          '">';
                      } else {
                        formDefinition.fields[json.name] = field;
                        ctlHtml = '<img data-is-temp="true" class="value" inputMode="' + json.inputMode + '" name="' + json.name + '">';
                      }
                      var element = CKEDITOR.dom.element.createFromHtml(ctlHtml);
                      $editor.focusedDom = element;
                      setTimeout(function (t) {
                        var $container = $('#container_' + pluginCode);
                        $container.find('input[id=name]').prop('readonly', true);
                        $container.find('input[id=formTree]').prop('readonly', true);
                        // $container.find("input[id=displayName]").prop("readonly", true);
                        if (pluginCode === CkPlugin.TEMPLATECONTAINER) {
                          $container.find('input[id=template-flag]').closest('tr').hide();
                        }
                      }, 800); // 引用字段设置只读
                    }
                    var pluginCode = $(ui.helper).attr('plugin-code');
                    $editor.execCommand(pluginCode, id, json); // 触发控件弹窗事件
                  }
                }
              });

              $(document.body).on('field-delete', function (event, fieldName) {
                if ($.trim(fieldName).length > -1) {
                  var selector = 'li[title="' + fieldName + '"]';
                  jsTemplates2.find(selector).draggable('option', {
                    disabled: false
                  });
                }
              });

              function checkedRefs() {
                var focusedDom = editor.focusedDom;
                if ((editor.mode === 'wysiwyg' && focusedDom == null) || typeof focusedDom === 'undefined') {
                  var fields = formDefinition.fields || {};
                  var templates = formDefinition.templates || {};
                  var subforms = {}; // hash subforms
                  for (var i in formDefinition.subforms || {}) {
                    var subform = formDefinition.getSubform(i);
                    if (subform && subform.name) {
                      subforms[subform.name] = subform;
                    }
                  }
                  // 非编辑状态才做清理
                  DesignerUtils.cleanUselessDefinition(editor.getData());
                  //
                  jsTemplates2.find('li[data-code]:not(.ui-draggable-dragging)').each(function (idx, element) {
                    // 先全部设置启用，然后再对已经引用的禁用
                    var $this = $(this);
                    var code = $this.attr('data-code');
                    var disable = code in fields || code in templates || code in subforms;
                    $this.draggable('option', {
                      disabled: disable
                    });
                  });
                }
                window.setTimeout(checkedRefs, 2000);
              }

              // 设置被引用则处于选中状态
              checkedRefs();

              $(this.document.find('body').getItem(0)).attr('class', 'dyform');
              var _this = this;
              this.document.on('click', function () {
                DesignerUtils.removeHintInDesigner();
              });
              this.dataProcessor.writer.setRules('p', {
                indent: true,
                breakBeforeOpen: false,
                breakAfterOpen: false,
                breakBeforeClose: false,
                breakAfterClose: false
              });

              // 从表设置为不可编辑
              var subforms = this.document.find('table[formUuid]');
              for (var i = 0; i < subforms.count(); i++) {
                subforms.getItem(i).unselectable();
                subforms.getItem(i).setState(CKEDITOR.TRISTATE_OFF);
              }

              // 自定义表格删除事件
              var createDef = function (def) {
                return CKEDITOR.tools.extend(def || {}, {
                  contextSensitive: 1,
                  refresh: function (editor, path) {
                    this.setState(path.contains('table', 1) ? CKEDITOR.TRISTATE_OFF : CKEDITOR.TRISTATE_DISABLED);
                  }
                });
              };

              _this.addCommand(
                'tableDelete',
                createDef({
                  // 重新定义表格删除事件,主表不得删除
                  exec: function (editor) {
                    var path = editor.elementPath();
                    var table = path.contains('table', 1);

                    if (!table) return;

                    // If the table's parent has only one child remove
                    // it as
                    // well (unless it's the body or a table cell)
                    // (#5416,
                    // #6289)
                    var parent = table.getParent();
                    if (parent.getChildCount() == 1 && !parent.is('body', 'td', 'th')) table = parent;

                    var range = editor.createRange();
                    range.moveToPosition(table, CKEDITOR.POSITION_BEFORE_START);
                    table.remove();
                    range.select();
                    var formUuid = $(table).attr('formUuid');
                    if (typeof formUuid != 'undefined') {
                      // 从表
                      // 从JSON定义中删除从表的定义信息
                      console.log('delete subform ' + formUuid);
                      formDefinition.deleteSubform(formUuid);
                    }
                  }
                })
              );

              // 子表单因此模板
              if (DesignerUtils.isFormTypeAsMstform()) {
                $('.icon-dysubform').hide(); // 子表单不支持从表
                $('.cke_button__templatecontainer').hide();
              }
            },
            keyup: function (evt) {
              if (
                !(
                  evt.data.keyCode == '8' ||
                  evt.data.keyCode == '13' ||
                  evt.data.keyCode == '46' ||
                  evt.data.keyCode == 10000 ||
                  evt.data.keyCode == '1114202'
                )
              ) {
                return true;
              }
              // 元素删除事件
              if (evt.data.keyCode == 10000) {
                if (evt.data.type == 'field') {
                  console.log('delete field');
                  var elem = evt.data.element;
                  var name = elem.getAttribute('name');
                  console.log('delete field ' + name);

                  elem.remove();
                  formDefinition.deleteField(name);
                }
              }

              return true;
            },
            change: function (evt) { },
            doubleclick: function (evt) {
              DesignerUtils.removeHintInDesigner();
            },
            afterCommandExec: function (event) {
              var editor = event.editor;
              var command = event.data && event.data.command;
              if (command && command.name === 'maximize' && DesignerUtils.onMaximize) {
                var container = editor.container.$;
                if (command.state === 2) {
                  // 最小化
                  DesignerUtils.onMaximize('min');
                } else if (command.state === 1) {
                  // 最大化
                  DesignerUtils.onMaximize('max');
                }
              }
            },
            resize: function (evt) { }
          }
        },
        DesignerUtils.editorOptions
      )
    );

    $.extend(editor, ckUtils);

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 保存、更新动态表单

    $('#btn_form_save').click(function () {
      DesignerUtils.validateAndSaveForm('0'); // 用于表示用户点击的是"保存"按钮
    });

    $('.btn-save-as-new-version').click(function () {
      DesignerUtils.validateAndSaveForm('1'); // 用于表示用户点击的是"版本升级"按钮
    });

    // 初始化开始结束

    // 为已经初始化完成的各页面元素填充数据
    if (typeof uuid === 'undefined' || uuid == null || uuid == '') {
      $('.btn-save-as-new-version').remove(); // 对于"新建"页面, 去掉保存为新版本的功能
      DesignerUtils.setValidateOptions(); // 设置校验规则
      DesignerUtils.fillFormDesigner(DesignerUtils.getDesingerDefaultHtml()); // 初始化页面中的"单据设计"tab
      // 如果是"新建"，则“归属系统单位”的值为当前登录用户所属的系统单位
      DesignerUtils.doBindSystemUnitFromCurrentSession();
      var version = '1.0';
      $('#version').val(version);
      $('#dyformVersion').html(version);
    } else {
      window.formDefinition = loadFormDefinition(uuid) || window.formDefinition; // 加载表单定义
      formDefinition.databaseFields = $.extend({}, formDefinition.fields); //数据库已经保存的字段，便于判断区分本次操作的字段
      DesignerUtils.fillBasicPropertyTab(formDefinition); // 初始化页面中的"基本属性"tab
      DesignerUtils.fillEventTab(formDefinition); // 初始化页面中的"事件管理"tab
      DesignerUtils.fillFormDesigner(formDefinition.html); // 初始化页面中的"表单设计"tab
      DesignerUtils.setValidateOptions(uuid); // 设置校验规则
      DesignerUtils.saveRefs(formDefinition); // 关联引用字段(日志性的，不需要强一致性？)

      setTimeout(function () {
        window.formDefinition.updateFormTree();
        $('.nav-tabs li:eq(1) a').trigger('click');
      }, 50);
    }

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
        width: $('#customJsModuleName').parent('td').width(),
        height: 250
      })
      .val(formDefinition.customJsModule)
      .trigger('change');

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
          height: 520,
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

            getFormFilds(formTree, formDefinition.id, formDefinition.name, fields);

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
                var id = $(this).parents('.well-select').attr('id');
                if (id == 'formField') {
                  var content = '${' + $(this).attr('data-name') + '(dyform.' + $(this).attr('data-value') + ')}';
                } else if (id == 'systemArgs') {
                  var content = '${' + $(this).attr('data-name') + '(sys.' + $(this).attr('data-value') + ')}';
                } else {
                  var content = '${' + $(this).attr('data-value') + '}';
                }
                var finalValue = value1 + content + value2;
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

    function getFormFilds(tree, id, name, fields) {
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
          getFormFilds(item.children, id, name, fields);
        }
        if (item.nodeType == 'template' && item.children.length > 0) {
          getFormFilds(item.children, item.templateUuid, '子表单-' + item.name, fields);
        }
      });
    }

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
        '<i class="iconfont icon-ptkj-xinxiwenxintishi"></i>' +
        '<span>' +
        '在下方编辑标题表达式，可插入选项（表单内置变量、表单字段、系统参数）和文本，文本会根据插入选项进行解析，当文本与多个选项匹配，或无选项匹配时，则不会解析' +
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
    // 存储服务
    var repository = formDefinition.repository || {};
    $('#repositoryMode')
      .on('change', function () {
        var mode = $(this).val();
        onRepositoryModeChange(mode);
      })
      .val(repository.mode || '1')
      .trigger('change')
      .select2({
        width: $('#repositoryMode').parent('td').width()
      });

    // 存储服务选择变更处理
    function onRepositoryModeChange(mode) {
      $('.repository-mode').hide();
      $('.repository-mode-' + mode).show();
    }
    $('#userTableName')
      .wSelect2({
        serviceName: 'cdDataStoreDefinitionService',
        queryMethod: 'loadSelectDataByTable',
        defaultBlank: false,
        remoteSearch: false
      })
      .val(repository.userTableName)
      .trigger('change');
    $('#serviceUrl').val(repository.serviceUrl);
    $('#serviceToken').val(repository.serviceToken);
    $('#customInterface')
      .wSelect2({
        serviceName: 'formDefinitionService',
        queryMethod: 'queryAllCustomFormRepositories',
        labelField: 'customInterfaceName',
        valueField: 'customInterface',
        defaultBlank: true,
        remoteSearch: false,
        width: $('#customInterfaceName').parent('td').width(),
        height: 250
      })
      .val(repository.customInterface)
      .trigger('change');
    if (StringUtils.isNotBlank(repository.mode)) {
      $('#repositoryMode').select2('disable');
      $('#userTableName').select2('disable');
      $('#customInterface').select2('disable');
    }

    $('body').on('click', '.btn-pt-help', function () {
      $(this).find('.pt-help-content').clone().dialog({
        zIndex: 50000,
        minWidth: 500,
        title: '帮助'
      });
    });

    $.extend(formDefinition, formDefinitionMethod);
    try {
      DesignerUtils.setPageAndDialogTile(uuid); // 设置标题,ie8兼容性问题
    } catch (ex) { }
    if (DesignerUtils.isFormTypeAsVform() || DesignerUtils.isFormTypeAsCform()) {
      DesignerUtils.initPformInfo(formDefinition);
      DesignerUtils.initRefFormInfo(formDefinition);
      DesignerUtils.initPformTreeInDesinger(DesignerUtils.getPformUuid());
    }
    DesignerUtils.initList(uuid);
    DesignerUtils.initEvent();
    DesignerUtils.explainFormReferenceResouce();
  };
  window.DesignerUtils = DesignerUtils;
})(jQuery);
