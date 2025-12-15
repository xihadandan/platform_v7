(function () {
  if (typeof CkPlugin == 'undefined') {
    return;
  }

  var pluginName = CkPlugin.FILELIBRARYCTL;
  var $columnTable;

  CKEDITOR.dialog.add(pluginName, function (editor) {
    var containerID = 'container_' + pluginName;
    return {
      title: '设置文件夹目录维护组件',
      minHeight: 400,
      maxHeight: 600,
      minWidth: 800,
      contents: [
        {
          id: 'fileLibraryProperty',
          label: 'label',
          title: 'title',
          expand: true,
          padding: 0,
          elements: [
            {
              id: 'file_library_html',
              type: 'html',
              style: 'width: 100%;',
              html: "<div id='" + containerID + "'>设置文件夹目录维护组件</div>",
              onLoad: function () {}
            }
          ]
        }
      ],
      onOk: function () {
        return collectFormAndFillCkeditor(editor);
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
    $('#file-library-tabs').tabs();
    this.editor = editor;
    $columnTable = $('#file_library_columns');
    initFileLibraryComponentSelect(editor);
  }

  function initFileLibraryComponentSelect(editor) {
    var focusedDom = editor.focusedDom;
    var initFileLibraryData;
    if (focusedDom) {
      var fileLibraryId = focusedDom.getAttribute('filelibraryid');
      initFileLibraryData = formDefinition.fileLibrary[fileLibraryId];
    }

    initFileLibraryColumnTable(initFileLibraryData ? initFileLibraryData.columns : []);
    if (initFileLibraryData) {
      $('#relaFileLibraryText').val(initFileLibraryData.relaFileLibraryText);
      $('#relaFileLibraryId').val(initFileLibraryData.relaFileLibraryId);
      $('#relaFileLibraryWidgetId').val(initFileLibraryData.relaFileLibraryWidgetId);
      $('#defaultCondition').val(initFileLibraryData.defaultCondition);
      $('#id').val(initFileLibraryData.id);
      $('#id').prop('readonly', true);
      var showType = initFileLibraryData.showType == '5' ? true : false;
      this.$('.file-library-config #showType').prop('checked', showType).data('waschecked', showType);
    }
    $('#relaFileLibraryText')
      .wSelect2({
        serviceName: 'appWidgetDefinitionMgr',
        labelField: 'relaFileLibraryText',
        valueField: 'relaFileLibraryId',
        params: {
          wtype: 'wFileLibrary',
          appPageUuid: null,
          uniqueKey: 'uuid',
          includeWidgetRef: 'false'
        },
        width: '100%',
        remoteSearch: false
      })
      .change(function (event) {
        var widgetUuid = $('#relaFileLibraryId').val();
        $columnTable.bootstrapTable('load', ajaxLoadFileLibraryColumnData(widgetUuid));
      });

    this.$('.file-library-config #showType')
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
  function ajaxLoadFileLibraryColumnData(widgetUuid) {
    var data = [];
    if (widgetUuid) {
      JDS.call({
        service: 'appWidgetDefinitionMgr.getBean',
        data: [widgetUuid],
        async: false,
        success: function (result) {
          var definitionJson = $.parseJSON(result.data.definitionJson);
          data = definitionJson.configuration.columns;
          $('#relaFileLibraryWidgetId').val(result.data.id);
        }
      });
    }
    return data;
  }

  //
  function initFileLibraryColumnTable(columnData) {
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
    var fileLibrary = new WFileLibraryClass();
    fileLibrary.relaFileLibraryId = $('#relaFileLibraryId').val();
    // tableView.id = $("#id").val();
    fileLibrary.id = fileLibrary.relaFileLibraryId;
    fileLibrary.relaFileLibraryText = $('#relaFileLibraryText').val();
    fileLibrary.defaultCondition = $('#defaultCondition').val();
    fileLibrary.relaFileLibraryWidgetId = $('#relaFileLibraryWidgetId').val();
    if (fileLibrary.id == undefined || fileLibrary.id == null || $.trim(fileLibrary.id).length == 0) {
      alert('文件夹目录ID不为空');
      return false;
    }
    if (!fileLibrary.relaFileLibraryText) {
      alert('文件夹目录设置不为空');
      return false;
    }
    fileLibrary.columns = $columnTable.bootstrapTable('getData');
    fileLibrary.showType = this.$('.file-library-config #showType').prop('checked') ? '5' : '-1';
    formDefinition.fileLibrary = formDefinition.fileLibrary || {};
    var imgSrc = CKEDITOR.plugins.basePath + pluginName + '/images/placeHolder.jpg';
    //创建视图控件的显示图片
    // var ctlHtml = "<img inputmode='tableview' name='" + tableView.id + "' class='value'  src='" + imgSrc + "'/>";
    var ctlHtml = tableHtml(fileLibrary);
    var element = CKEDITOR.dom.element.createFromHtml(ctlHtml);
    if (this.editor.focusedDom == null) {
      this.editor.focusedDom = this.editor.document.findOne("img[name='" + fileLibrary.relaFileLibraryId + "']"); // /查找占位符
    }
    if (this.editor.focusedDom != null && this.editor.focusedDom != undefined) {
      element.insertBefore(this.editor.focusedDom);
      this.editor.focusedDom.remove();
    } else {
      this.editor.insertElement(element);
    }

    formDefinition.fileLibrary[fileLibrary.id] = fileLibrary;
    return true;
  }

  function tableHtml(fileLibrary) {
    var $table = $('<table>', {
      filelibraryid: fileLibrary.id,
      class: 'table table-bordered',
      style: 'cursor:pointer;'
    });
    var $tr = $('<tr>', { class: 'active' });
    for (var i = 0, len = fileLibrary.columns.length; i < len; i++) {
      if (fileLibrary.columns[i].hidden == 1) {
        continue;
      }
      $tr.append($('<th>').text(fileLibrary.columns[i].header));
    }
    $table.append($tr);
    return $table[0].outerHTML;
  }
})();
