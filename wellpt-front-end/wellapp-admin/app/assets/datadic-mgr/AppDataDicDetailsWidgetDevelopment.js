define([
  'constant',
  'commons',
  'server',
  'appModal',
  'AppPtMgrDetailsWidgetDevelopment',
  'AppPtMgrCommons',
  'dataStoreBase',
  'formBuilder'
], function (constant, commons, server, appModal, AppPtMgrDetailsWidgetDevelopment, AppPtMgrCommons, DataStore, formBuilder) {
  var validator;
  var listView;
  // 平台管理_产品集成_数据字典详情_HTML组件二开
  var AppDataDicDetailsWidgetDevelopment = function () {
    AppPtMgrDetailsWidgetDevelopment.apply(this, arguments);
  };

  // 接口方法
  commons.inherit(AppDataDicDetailsWidgetDevelopment, AppPtMgrDetailsWidgetDevelopment, {
    // 组件初始化
    init: function () {
      var _self = this;
      // 验证器
      validator = server.Validation.validate({
        beanName: 'dataDictionary',
        container: this.widget.element,
        wrapperForm: true
      });

      _self._initDataDicChildTable();
      _self._initDataDicAttrTable();

      // 绑定事件
      _self._bindEvents();
    },

    _initDataDicChildTable: function () {
      var _self = this;
      var $container = $(this.widget.element);
      var $dataDicChildTable = $('#table_dic_child', $container);
      // 定义新增，删除按钮事件
      formBuilder.bootstrapTable.initTableTopButtonToolbar(
        'table_dic_child',
        'child',
        $container,
        {
          name: '',
          type: '',
          code: ''
        },
        'GUUID'
      );

      // 列定义
      $dataDicChildTable.bootstrapTable('destroy').bootstrapTable({
        data: [],
        idField: 'GUUID',
        striped: false,
        showColumns: false,
        toolbar: $('#div_dic_child_toolbar', $container),
        width: 500,
        columns: [
          {
            field: 'checked',
            formatter: function (value, row, index) {
              if (value) {
                return true;
              }
              return false;
            },
            checkbox: true
          },
          {
            field: 'GUUID',
            title: 'GUUID',
            visible: false
          },
          {
            field: 'uuid',
            title: 'UUID',
            visible: false
          },
          {
            field: 'name',
            title: '名称',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true,
              validate: function (value) {
                if (value == '') {
                  return '请输入名称!';
                }
              }
            }
          },
          {
            field: 'code',
            title: '代码',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true,
              validate: function (value) {
                if (value == '') {
                  return '请输入代码!';
                }
              }
            }
          },
          {
            field: 'type',
            title: '类型',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true,
              validate: function (value) {
                if (value == '') {
                  return '请输入类型!';
                }
              }
            }
          }
        ]
      });
    },

    _initDataDicAttrTable: function () {
      var _self = this;
      var $container = $(this.widget.element);
      var $dataDicAttrTable = $('#table_dic_attr', $container);
      // 定义新增，删除按钮事件
      formBuilder.bootstrapTable.initTableTopButtonToolbar(
        'table_dic_attr',
        'attr',
        $container,
        {
          name: '',
          value: ''
        },
        'GUUID'
      );

      // 列定义
      $dataDicAttrTable.bootstrapTable('destroy').bootstrapTable({
        data: [],
        idField: 'GUUID',
        striped: false,
        showColumns: false,
        toolbar: $('#div_dic_attr_toolbar', $container),
        width: 500,
        columns: [
          {
            field: 'checked',
            formatter: function (value, row, index) {
              if (value) {
                return true;
              }
              return false;
            },
            checkbox: true
          },
          {
            field: 'GUUID',
            title: 'GUUID',
            visible: false
          },
          {
            field: 'uuid',
            title: 'UUID',
            visible: false
          },
          {
            field: 'name',
            title: '名称',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true,
              validate: function (value) {
                if (value == '') {
                  return '请输入名称!';
                }
              }
            }
          },
          {
            field: 'value',
            title: '值',
            editable: {
              type: 'text',
              mode: 'inline',
              showbuttons: false,
              onblur: 'submit',
              savenochange: true
            }
          }
        ]
      });
    },

    _refreshDataDicChildTable: function (rowData) {
      if (rowData) {
        for (var i = 0, len = rowData.length; i < len; i++) {
          if (!rowData[i].GUUID) {
            rowData[i].GUUID = rowData[i].uuid || commons.UUID.createUUID();
          }
        }
      }
      $('#table_dic_child', this.widget.element).bootstrapTable('load', rowData);
      $('#table_dic_child', this.widget.element).bootstrapTable('resetView');
    },

    _refreshDataDicAttrdTable: function (rowData) {
      if (rowData) {
        for (var i = 0, len = rowData.length; i < len; i++) {
          if (!rowData[i].GUUID) {
            rowData[i].GUUID = rowData[i].uuid || commons.UUID.createUUID();
          }
        }
      }
      $('#table_dic_attr', this.widget.element).bootstrapTable('load', rowData);
      $('#table_dic_attr', this.widget.element).bootstrapTable('resetView');
    },

    _bindEvents: function () {
      var _self = this;
      var widget = _self.getWidget();
      var $container = $(widget.element);
      var pageContainer = _self.getPageContainer();

      pageContainer.off('AppDataDicTreeView.editTreeNode');
      pageContainer.on('AppDataDicTreeView.editTreeNode', function (e, ui) {
        var param = e.detail;
        // 清空表单
        AppPtMgrCommons.clearForm({
          container: $container,
          includeHidden: true
        });

        _self._refreshDataDicChildTable([]);
        // 显示第一个tab内容
        $('.nav-tabs>li>a:first', $container).tab('show');

        //设置父节点UUID
        var treeObj = $.fn.zTree.getZTreeObj('datadict_tree');
        if (treeObj) {
          var selected = treeObj.getSelectedNodes();
          if (selected.length == 1 && selected[0].id != -1) {
            $('#parentUuid', $container).val(selected[0].id);
          }
        }

        _self._uneditableForm(false);

        if (param.uuid) {
          //加载数据字典详情
          _self._loadDataDicDetails(param.uuid, param.isRef);
        }
      });

      $('#btn_save_dic', $container).on('click', function () {
        _self._saveDic();
        return false;
      });

      $('a[data-toggle="tab"]', $container).on('shown.bs.tab', function (e) {
        if ($(e.target).attr('aria-controls') == 'dic_list_info') {
          $('#table_dic_child', _self.widget.element).bootstrapTable('resetView');
        }
        if ($(e.target).attr('aria-controls') == 'dic_attr_info') {
          $('#table_dic_attr', _self.widget.element).bootstrapTable('resetView');
        }
        return true;
      });
    },

    _uneditableForm: function (uneditable) {
      uneditable = uneditable == undefined ? true : Boolean(uneditable);
      AppPtMgrCommons.uneditableForm(
        {
          container: this.widget.element
        },
        uneditable
      );

      //表格不可编辑
      $('#div_dic_child_toolbar,#div_dic_attr_toolbar', this.widget.element).show();
      if (uneditable) {
        $('#div_dic_child_toolbar,#div_dic_attr_toolbar', this.widget.element).hide();
      }

      $('#table_dic_attr,#table_dic_child').each(function () {
        $(this).bootstrapTable('refreshOptions', {
          editable: !uneditable
        });
      });

      //保存按钮不可用
      $('#btn_save_dic', this.widget.element).prop('disabled', uneditable);
    },

    _saveDic: function () {
      var _self = this;
      var bean = this._bean();
      AppPtMgrCommons.form2json({
        json: bean,
        container: this.widget.element
      });

      //收集子字典数据
      var children = $('#table_dic_child', this.widget.element).bootstrapTable('getData');
      var existsChildren = [];
      for (var i = 0, len = children.length; i < len; i++) {
        children[i].seq = i + 1; //序号
        children[i].moduleId = bean.moduleId; //模块ID
        if (children[i].uuid) {
          existsChildren.push(children[i].uuid);
        }
      }
      bean.changedChildren = children;
      var treeObj = $.fn.zTree.getZTreeObj('datadict_tree');
      var parentNode = treeObj.getNodeByParam('id', bean.parentUuid || -1, null);
      if (parentNode) {
        bean.seq = parentNode.children.length + 1; //设置序号
      }

      /*
                //FIXME:异步刷新节点数据
                if (bean.uuid) {
                    //同步子节点的名称

                    var selected = treeObj.getSelectedNodes();
                    if (selected.length == 1) {
                        for (var i = 0, len = children.length; i < len; i++) {
                            if (children[i].uuid) {
                                var node = treeObj.getNodeByParam("id", children[i].uuid, selected[0]);
                                node.name = children[i].name;
                                treeObj.updateNode(node);
                            } else {
                                // treeObj.addNodes(selected[0], -1, [{
                                //     id: children[i].GUUID,
                                //     name: children[i].name
                                // }])
                            }
                        }
                    }
                }*/

      //收集字典属性数据
      var attrList = $('#table_dic_attr', this.widget.element).bootstrapTable('getData');
      var existsAttrs = [];
      bean.changedAttributes = attrList;
      for (var i = 0; i < attrList.length; i++) {
        if (attrList[i].uuid) {
          existsAttrs.push(attrList[i].uuid);
        }
      }

      var keys = {
        children: 'deletedChildren',
        attributes: 'deletedAttributes'
      };
      var exists = {
        children: existsChildren,
        attributes: existsAttrs
      };
      // 删除数据
      if (bean.uuid && _self.tempData && _self.tempData[bean.uuid]) {
        for (var k in keys) {
          if (_self.tempData[bean.uuid][k]) {
            for (var i = 0; i < _self.tempData[bean.uuid][k].length; i++) {
              if (exists[k].indexOf(_self.tempData[bean.uuid][k][i].uuid) == -1) {
                //不存在，则表示被删除了
                bean[keys[k]].push(_self.tempData[bean.uuid][k][i]);
              }
            }
          }
        }
      }

      if (!bean.parentUuid && !bean.uuid) {
        //没有选择父节点的情况下，默认是根节点
        treeObj.selectNode(treeObj.getNodes()[0]);
      }
      if (!validator.form()) {
        return false;
      }
      server.JDS.call({
        service: 'dataDictionaryService.saveBean',
        data: [bean],
        version: '',
        validate: true,
        success: function (result) {
          appModal.success('保存成功！');
          // 保存成功刷新树
          var saveUuid = result.data;
          _self.widget.trigger('AppDataDicTreeView.refreshDataDicZtree', {
            action: 'save',
            deleteChildren: bean.deletedChildren,
            saveUuid: saveUuid,
            uuid: bean.uuid,
            name: bean.name
          });
        }
      });
    },

    _loadDataDicDetails: function (uuid, isRef) {
      var _self = this;
      var $container = this.widget.element;
      server.JDS.call({
        service: 'dataDictionaryService.getBean',
        data: [uuid],
        version: '',
        success: function (result) {
          var bean = _self._bean();
          $.extend(bean, result.data);
          AppPtMgrCommons.json2form({
            json: bean,
            container: $container
          });

          validator.form();
          if (isRef) {
            _self._uneditableForm();
          }
          if (!_self.tempData) {
            _self.tempData = {};
          }
          _self.tempData[uuid] = $.extend(true, {}, bean); //临时数据
          _self._refreshDataDicChildTable(bean.children);
          _self._refreshDataDicAttrdTable(bean.attributes);
        }
      });
    },
    refresh: function () {
      //刷新树
      this._uneditableForm(false);
      AppPtMgrCommons.clearForm({
        container: this.widget.element
      });
      this._refreshDataDicChildTable([]);
      this._refreshDataDicAttrdTable([]);
    },
    _bean: function () {
      return {
        uuid: null,
        name: null,
        code: null,
        type: null,
        parent: null,
        owners: [],
        id: null,
        parentName: null,
        parentUuid: null,
        ownerNames: null,
        ownerIds: null,
        children: [],
        deletedChildren: [],
        changedChildren: [],
        attributes: [],
        changedAttributes: [],
        deletedAttributes: [],
        sourceUuid: null,
        sourceType: null,
        editable: true,
        deletable: true,
        childEditable: true,
        moduleId: this._moduleId()
      };
    },
    _moduleId: function () {
      return this.getWidgetParams().moduleId;
    }
  });
  return AppDataDicDetailsWidgetDevelopment;
});
