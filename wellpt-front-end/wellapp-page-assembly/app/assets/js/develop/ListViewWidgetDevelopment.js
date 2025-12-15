define(['constant', 'commons', 'server', 'WidgetDevelopment'], function (constant, commons, server, WidgetDevelopment) {
  // 视图组件二开基础
  var ListViewWidgetDevelopment = function () {
    WidgetDevelopment.apply(this, arguments);
  };
  // 接口方法
  commons.inherit(ListViewWidgetDevelopment, WidgetDevelopment, {
    // 获取bootstrapTable的更多配置回调，配置设置到bootstrapTableOptions，子类可覆盖
    getTableOptions: function (bootstrapTableOptions) {},
    // 视图组件渲染前回调方法，子类可覆盖
    beforeRender: function (options, configuration) {},
    // 视图组件渲染完成回调方法，子类可覆盖
    afterRender: function (options, configuration) {},
    // 行数据渲染后回调方法，子类可覆盖
    onPostBody: function (data) {},
    // 刷新列表后的回调方法
    onRefresh: function (data) {},
    // 加载数据前回调方法
    beforeLoadData: function (options, configuration, request) {
      var _self = this;
      var allUserCheckbox = $('input[name="allUserCheckbox"]');
      if ($(allUserCheckbox).is('[type=checkbox]')) {
        if ($(allUserCheckbox).prop('checked')) {
          _self.removeParam('eleIdPath');
          var zTreeObj = $.fn.zTree.getZTreeObj("all_org_tree");
          zTreeObj.cancelSelectedNode();
        }
      }
    },
    // 表格数据加载完毕回调方法，子类可覆盖
    onLoadSuccess: function (data) {},
    // 行点击的时候回调方法，子类可覆盖
    onClickRow: function (rowNum, row, $element, field) {},
    // 行双击的时候回调方法，子类可覆盖
    onDblClickRow: function (rowNum, row, $element, field) {},
    // 点击排序的时候回调方法，子类可覆盖
    onSort: function (name, order) {},
    /**
     * 行级按钮的格式化输出
     * @param format：格式化对象{before:,after:}，before格式化前的行级按钮html字符串，通过设置after值返回格式化的值
     * @param row  行数据
     * @param index  当前行下标
     */
    lineEnderButtonHtmlFormat: function (format, row, index) {},
    // 清除视图参数
    clearParams: function () {
      this.getWidget().clearParams();
    },
    // 添加视图参数
    addParam: function (key, value) {
      this.getWidget().addParam(key, value);
    },
    // 移除视图参数
    removeParam: function (key) {
      return this.getWidget().removeParam(key);
    },
    // 获取视图参数
    getParam: function (key) {
      return this.getWidget().getParam(key);
    },
    // 获取选中列数据集合，同getSelections
    getSelection: function () {
      return this.getWidget().getSelections();
    },
    // 获取选中列数据集合
    getSelections: function () {
      return this.getWidget().getSelections();
    },
    // 获取选中列数据的UUID集合
    getSelectionUuids: function () {
      var selects = this.getWidget().getSelections();
      var uuids = [];
      if (selects && selects.length > 0) {
        for (var i = 0; i < selects.length; i++) {
          if (selects[i].hasOwnProperty('UUID')) {
            uuids.push(selects[i]['UUID']);
          } else if (selects[i].hasOwnProperty('uuid')) {
            uuids.push(selects[i]['uuid']);
          }
        }
      }
      return uuids;
    },
    // 获取选中列索引集合
    getSelectionIndexes: function () {
      return this.getWidget().getSelectionIndexes();
    },
    // 获取视图数据集合
    getData: function () {
      return this.getWidget().getData();
    },
    // 根据唯一键获取列数据
    getRowByUniqueId: function (id) {
      return this.getWidget().getRowByUniqueId(id);
    },
    // 刷新数据 options{ notifyChange:false,//是否触发条数变更通知
    // conditions:[{columnIndex:'',value:'',type:'like'}]//条件对象 }
    refresh: function (options) {
      return this.getWidget().refresh(options);
    },
    // 页码跳转
    selectPage: function (pageNum) {
      return this.getWidget().selectPage(pageNum);
    },
    // 重新计算（设置）视图高度
    resetHeight: function (height) {
      return this.getWidget().resetHeight(height);
    },
    // 获取视图定义信息
    getViewConfiguration: function () {
      return this.getWidget().getConfiguration();
    },
    // 获取获取数据源对象
    getDataProvider: function () {
      return this.getWidget().getDataProvider();
    },
    // 添加额外的查询条件
    addOtherConditions: function (conditions) {
      this.getWidget().addOtherConditions(conditions);
    },
    // 情况额外查询条件,condition为空是清楚全部，否则清楚等于condition的一条额外查询条件
    clearOtherConditions: function (condition) {
      this.getWidget().clearOtherConditions(condition);
    },

    /**
     * 树形列表切换
     */
    toggleTreeView: function () {
      this.getWidget().toggleTreeView();
    },

    /**
     * 展开树节点
     * @param dataIndex null或者undefined 则展开所有树节点
     */
    expandTreeView: function (dataIndex) {
      this.getWidget().expandTreeView(dataIndex);
    },
    /**
     * 折叠树节点
     * @param dataIndex null或者undefined 则折叠所有树节点
     */
    collapseTreeView: function (dataIndex) {
      this.getWidget().collapseTreeView(dataIndex);
    },

    /**
     * 获取列定义的主键列名称
     * @returns {*}
     */
    getPrimaryColumnName: function () {
      var columns = this.getWidget().getConfiguration().columns;
      for (var i = 0; i < columns.length; i++) {
        if (columns[i]['idField'] == '1') {
          return columns[i]['name'];
        }
      }
      return null;
    },

    /**
     * 获取选中的主键值
     */
    getSelectionPrimaryKeys: function () {
      var primaryColumnName = this.getPrimaryColumnName();
      if (!primaryColumnName) {
        return [];
      }
      return $.map(this.getData(), function (row, index) {
        if (row.rowCheckItem) {
          return row[primaryColumnName];
        }
      });
    },

    /**
     * 获取选中的指定字段值集合
     */
    getSelectionsFields: function (field) {
      return $.map(this.getData(), function (row, index) {
        if (row.rowCheckItem) {
          return row[field];
        }
      });
    },

    /**
     * 获取数据集合的指定列值
     */
    getDataColumnValues: function (columnName) {
      if (columnName) {
        return $.map(this.getData(), function (row, index) {
          return row[columnName];
        });
      }
      return [];
    },
    onExport: function (value) {
      this.onExportOrImoprtOrDependence('export', value);
    },
    onImport: function () {
      this.onExportOrImoprtOrDependence('import');
    },
    onDependence: function (value) {
      this.onExportOrImoprtOrDependence('dependencie', value);
    },
    onExportOrImoprtOrDependence: function (type, value) {
      var self = this;
      if (type === 'import') {
        $.iexportData[type]({
          callback: function () {
            self.refresh();
          }
        });
      } else {
        var uuids = self.getSelectionUuids();
        if (!uuids.length) {
          appModal.error('请选择记录！');
          return false;
        }
        var uuid = uuids.join(';');
        $.iexportData[type]({ uuid: uuid, type: value });
      }
    },

    //切换为高级搜索时触发二开js自定义事件
    changeToFieldSearch: function () {}
  });
  return ListViewWidgetDevelopment;
});
