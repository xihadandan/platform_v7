(function () {
  var pluginName = CkPlugin.TABLEVIEWCTL;
  var $columnTable;

  CKEDITOR.dialog.add(pluginName, function (editor) {
    var containerID = 'container_' + pluginName;
    return {
      title: '设置视图列表',
      minHeight: 400,
      maxHeight: 600,
      minWidth: 800,
      contents: [
        {
          id: 'tableViewProperty',
          label: 'label',
          title: 'title',
          expand: true,
          padding: 0,
          elements: [
            {
              id: 'table_html',
              type: 'html',
              style: 'width: 100%;',
              html: "<div id='" + containerID + "'>设置视图列表</div>",
              //html:"<iframe  id=container_" + pluginName + " src='" + ctx +"/resources/ckeditor4.1/plugins/dysubform/index.html' />",
              onLoad: function () {}
            }
          ]
        }
      ],
      onOk: function () {
        return collectFormAndFillCkeditor(editor);
        // exitDialog(editor);
      },
      onCancel: function () {
        //退出窗口时清空属性窗口的缓存
        $.ControlConfigUtil.exitDialog(editor);
        $('#' + containerID).empty();
      },
      onShow: function () {
        //$("#attrCfgDiv").remove();//防止缓存 ，出现多个不同的属性弹出框由于缓存，出现重复元素
        var date = new Date();
        var dialogHtmlFile = CKEDITOR.plugins.basePath + pluginName + '/dialogs/' + pluginName + '.html?' + date.getTime(); //加载HTML配置页面
        //以GET方式(不得以POST方式)加载属性窗口的html
        $('#' + containerID).load(dialogHtmlFile, function () {
          initPropertyDialog(editor); //初始化属性窗口
          $.ControlConfigUtil.enableFocus(editor, CKEDITOR.dialog._.currentTop, containerID);
        });
      }
    };
  });

  /**
   * 初始化属性窗口
   */
  function initPropertyDialog(editor) {
    $('#table-view-tabs').tabs();
    this.editor = editor;
    $columnTable = $('#table_view_columns');
    initTableViewComponentSelect(editor);
  }

  function initTableViewComponentSelect(editor) {
    var focusedDom = editor.focusedDom;
    var initTableViewData;
    if (focusedDom) {
      var tableViewId = focusedDom.getAttribute('tableviewid');
      initTableViewData = formDefinition.tableView[tableViewId];
    }

    initTableViewColumnTable(initTableViewData ? initTableViewData.columns : []);
    if (initTableViewData) {
      $('#relaTableViewText').val(initTableViewData.relaTableViewText);
      $('#relaTableViewId').val(initTableViewData.relaTableViewId);
      $('#relaTableViewWidgetId').val(initTableViewData.relaTableViewWidgetId);
      $('#defaultCondition').val(initTableViewData.defaultCondition);
      $('#id').val(initTableViewData.id);
      $('#id').prop('readonly', true);
      var showType = initTableViewData.showType == '5' ? true : false;
      this.$('.table-view-config #showType').prop('checked', showType).data('waschecked', showType);
    }
    $('#relaTableViewText')
      .wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        labelField: 'relaTableViewText',
        valueField: 'relaTableViewId',
        params: {
          wtype: 'wBootstrapTable',
          appPageUuid: null,
          uniqueKey: 'uuid',
          includeWidgetRef: 'false'
        },
        width: '100%',
        remoteSearch: false
      })
      .change(function (event) {
        var widgetUuid = $('#relaTableViewId').val();
        $columnTable.bootstrapTable('load', ajaxLoadTableViewColumnData(widgetUuid));
      });

    this.$('.table-view-config #showType')
      .off()
      .on('click', function () {
        var $radio = $(this);
        if ($radio.data('waschecked') == true) {
          $radio.prop('checked', false);
          $radio.data('waschecked', false);
        } else {
          $radio.prop('checked', true);
          $radio.data('waschecked', true);
        }
      });
  }

  //请求表格组件的列定义数据
  function ajaxLoadTableViewColumnData(widgetUuid) {
    var data = [];
    if (widgetUuid) {
      JDS.call({
        service: 'appWidgetDefinitionMgr.getBean',
        data: [widgetUuid],
        async: false,
        success: function (result) {
          var definitionJson = $.parseJSON(result.data.definitionJson);
          data = definitionJson.configuration.columns;
          $('#relaTableViewWidgetId').val(result.data.id);
        }
      });
    }
    return data;
  }

  //
  function initTableViewColumnTable(columnData) {
    $columnTable.bootstrapTable('destroy').bootstrapTable({
      data: columnData,
      idField: 'uuid',
      //striped: true,
      showColumns: true,
      onEditableHidden: function (field, row, $el, reason) {
        $el.closest('table').bootstrapTable('resetView');
      },
      width: 500,
      columns: [
        {
          field: 'uuid',
          title: 'UUID',
          visible: false
        },
        {
          field: 'header',
          title: '标题'
        },
        {
          field: 'name',
          title: '字段名'
        },
        {
          field: 'hidden',
          title: '隐藏',
          editable: {
            type: 'select',
            mode: 'inline',
            showbuttons: false,
            source: [
              {
                value: '1',
                text: '是'
              },
              {
                value: '0',
                text: '否'
              }
            ]
          }
        }
      ]
    });
  }

  //收集数据，并填入ck控件
  function collectFormAndFillCkeditor(editor) {
    var tableView = new WTableViewClass();
    tableView.relaTableViewId = $('#relaTableViewId').val();
    // tableView.id = $("#id").val();
    tableView.id = tableView.relaTableViewId;
    tableView.relaTableViewText = $('#relaTableViewText').val();
    tableView.defaultCondition = $('#defaultCondition').val();
    tableView.relaTableViewWidgetId = $('#relaTableViewWidgetId').val();
    if (tableView.id == undefined || tableView.id == null || $.trim(tableView.id).length == 0) {
      alert('视图ID不为空');
      return false;
    }
    if (!tableView.relaTableViewText) {
      alert('视图设置不为空');
      return false;
    }
    tableView.columns = $columnTable.bootstrapTable('getData');
    tableView.showType = this.$('.table-view-config #showType').prop('checked') ? '5' : '-1';
    formDefinition.tableView = formDefinition.tableView || {};
    var imgSrc = CKEDITOR.plugins.basePath + pluginName + '/images/placeHolder.jpg';
    //创建视图控件的显示图片
    // var ctlHtml = "<img inputmode='tableview' name='" + tableView.id + "' class='value'  src='" + imgSrc + "'/>";
    var ctlHtml = tableHtml(tableView);
    var element = CKEDITOR.dom.element.createFromHtml(ctlHtml);
    if (this.editor.focusedDom == null) {
      this.editor.focusedDom = this.editor.document.findOne("img[name='" + tableView.relaTableViewId + "']"); // /查找占位符
    }
    if (this.editor.focusedDom != null && this.editor.focusedDom != undefined) {
      element.insertBefore(this.editor.focusedDom);
      this.editor.focusedDom.remove();
    } else {
      this.editor.insertElement(element);
    }

    formDefinition.tableView[tableView.id] = tableView;
    return true;
  }

  function tableHtml(tableView) {
    var $table = $('<table>', {
      tableviewid: tableView.id,
      class: 'table table-bordered',
      style: 'cursor:pointer;'
    });
    var $tr = $('<tr>', { class: 'active' });
    for (var i = 0, len = tableView.columns.length; i < len; i++) {
      if (tableView.columns[i].hidden == 1) {
        continue;
      }
      $tr.append($('<th>').text(tableView.columns[i].header));
    }
    $table.append($tr);
    return $table[0].outerHTML;
  }
})();
