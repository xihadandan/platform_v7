import WidgetTableDevelopment from '@develop/WidgetTableDevelopment';

class DataModelTableDataListDevelopment extends WidgetTableDevelopment {

  resetColumn(callback) {
    let $widget = this.$widget;
    // 获取模型的列定义json
    let columnJson = this.widgetForm.form.columnJson,
      _this = this;
    let columns = [];
    for (let c of columnJson) {
      columns.push({
        title: c.title,
        dataIndex: c.column,
        primaryKey: c.column === 'UUID'
      });
    }

    columns.push({
      title: '类型',
      titleHidden: true,
      dataIndex: '_markedType',
      renderFunction: {
        options: {
          template: `<template>
        <a-tag v-if="row._markedType=='FIXED'">固化数据</a-tag>
        <a-tag v-if="row._markedType=='DEMO'" color="blue">样例数据</a-tag>
        </template>`
        },
        type: 'vueTemplateDataRender'
      }
    });

    $widget.setColumns(columns);
    callback();
  }

  mounted() {
    this.widgetForm = this.getVueWidgetById('rAvssJpqlIKsXtalMgZMeNGMTCANLwLZ');
  }

  dataMarkType() {
    let keys = this.$widget.getSelectedRowKeys();
    if (keys.length) {
    }
  }

  deleteRowData({ meta, $evtWidget }) {
    let deleteKeys = meta.selectedRowKeys || [meta.UUID];
    let form = this.widgetForm.form;
    let _this = this;
    $axios
      .get(`/proxy/api/dm/deleteByUuid/uf_${form.id}?uuid=${deleteKeys.join('&uuid=')}`, {
        params: {}
      })
      .then(({ data }) => {
        _this.$widget.refetch();
        // let cols = data.data;
        // for (let i = 0, len = cols.length; i < len; i++) {
        //   if (!dataIndex.includes(cols[i].columnIndex)) {
        //     $widget.addColumn({
        //       title: cols[i].title, dataIndex: cols[i].columnIndex, dynamic: true
        //     })
        //   }
        // }
      });
  }

  editTableDataDrawer({ meta }) {
    let currentTableData = this.getPageState().currentTableData;
    for (let k in meta) {
      this.$widget.$set(currentTableData, k, meta[k]);
    }
    this.getPageContext().emitEvent('IlXonjpBzXxDrXFzYGOkDfBVmANnFQND:showDrawer');

    // this.$widget.$nextTick(()=>{
    //   // 获取浮层内的表单，并设置表单数据
    //  })
  }

  fetchTableData({ tableParams, callback }) {
    let params = {
      pagingInfo: {
        pageSize: tableParams.pagination.pageSize,
        currentPage: tableParams.pagination.currentPage,
        autoCount: true
      }
    };
    let form = this.widgetForm.form;

    this.resetColumn(() => {
      $axios.post(`/proxy/api/dm/queryTableData/uf_${form.id}`, params).then(({ data }) => {
        // 异步加载标记数据
        let rows = data.data.data,
          dataUuids = [],
          rowDataMap = {};
        for (let i = 0, len = rows.length; i < len; i++) {
          rows[i]._markedType = undefined;
          dataUuids.push(rows[i].UUID);
          rowDataMap[rows[i].UUID] = rows[i];
        }
        callback(data.data, data.data.pagination.totalCount);
        if (dataUuids.length) {
          $axios.get(`/proxy/api/dm/getDataTypes?dataUuid=${dataUuids.join('&dataUuid=')}`, {}).then(({ data }) => {
            let map = data.data;
            if (map) {
              for (let k in map) {
                rowDataMap[k]._markedType = map[k];
              }
            }
          });
        }
      });
    });
  }

  get META() {
    return {
      name: '数据模型_存储对象_数据视图二开',
      hook: {
        dataMarkType: '设置数据标记',
        deleteRowData: '删除表数据',
        editTableDataDrawer: '编辑存储行数据',
        fetchTableData: '获取表数据'
      }
    }
  }
}




export default DataModelTableDataListDevelopment;
