import VuePageDevelopment from '@develop/VuePageDevelopment';
import WorkflowSimulation from '@workflow/app/web/page/workflow-simulation/simulation/workflow-simulation.js';
// import DefDataImport from '@admin/app/web/lib/def-data-import.vue';
// import DefDataExport from '@admin/app/web/lib/def-data-export.vue';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';
import { debounce } from 'lodash';
class WfFlowDefinitionPageDevelopment extends VuePageDevelopment {
  get META() {
    return {
      name: '流程管理_流程定义页面二开',
      hook: {
        deleteFlowCategory: '删除流程分类',
        importFlowDefinition: '定义导入',
        exportFlowDefinition: '定义导出',
        exportFlowDefinitionList: '导出流程定义清单',
        copyFlowDefinition: '复制流程定义',
        logicalDeleteFlowDefinition: '逻辑删除流程定义',
        recoveryFlowDefinition: '恢复流程定义',
        physicalDeleteFlowDefinition: '物理删除流程定义',
        flowSimulation: '流程仿真',
        scheduleDeleteSetting: '定时清理设置'
      }
    };
  }

  mounted() {
    const _this = this;
    this.$widget.pageContext.handleEvent('categorySelect_workflow', (selectedKeys, selectedKeysIncludeChildren, sourceCode, e) => {
      // 表格数据过滤
      if (sourceCode == 'xYRNaUypXIUrqzNSafsNcebKyOaKWtlH') {
        let tableWidget = _this.getVueWidgetById('xYRNaUypXIUrqzNSafsNcebKyOaKWtlH');
        let dataSource = tableWidget.getDataSourceProvider();
        if (selectedKeys.length > 0) {
          dataSource.removeParam('categoryUuid');
          dataSource.addParam('categoryUuid', selectedKeys[0]);
        } else {
          dataSource.removeParam('categoryUuid');
        }
        tableWidget.refetch(true);
        let cardWidget = _this.getVueWidgetById('FZEELtxQCeEquukAlwetBUHaRaTrEFWc');
        if (cardWidget) {
          cardWidget.updateTitle(selectedKeys.length ? e.node.dataRef.name : '');
        }
      } else {
        let tableWidget = _this.getVueWidgetById('JSWYlKZVCkBeOAYIWBENYLIDDnQBRQZR');
        let dataSource = tableWidget.getDataSourceProvider();
        if (selectedKeys.length > 0) {
          dataSource.removeParam('categoryUuid');
          dataSource.addParam('categoryUuid', selectedKeys[0]);
        } else {
          dataSource.removeParam('categoryUuid');
        }
        tableWidget.refetch(true);
        let cardWidget = _this.getVueWidgetById('NOfPkCxUMIUVpTnyfrzzxbsmqRAxZzsN');
        if (cardWidget) {
          cardWidget.updateTitle(selectedKeys.length ? e.node.dataRef.name : '');
        }
      }
    });

    _this.handleEvent('omhnjCXbgLMLFfkVlnSJVXPJuAHPDTSj:treeNodeSelected', ({ selectedKeys, selected, node, selectedNodes }) => {
      // 表格数据过滤
      let tableWidget = _this.getVueWidgetById('xYRNaUypXIUrqzNSafsNcebKyOaKWtlH');
      let dataSource = tableWidget.getDataSourceProvider();
      if (node.pos != '0-0' && selectedKeys.length > 0) {
        dataSource.removeParam('categoryUuid');
        dataSource.addParam('categoryUuid', selectedKeys[0]);
      } else {
        dataSource.removeParam('categoryUuid');
      }
      tableWidget.refetch(true);
    });

    _this.handleEvent('gAIyNpFRleXGWDnXVmohGLuafyCIiwsM:treeNodeSelected', ({ selectedKeys, selected, node, selectedNodes }) => {
      // 表格数据过滤
      let tableWidget = _this.getVueWidgetById('JSWYlKZVCkBeOAYIWBENYLIDDnQBRQZR');
      let dataSource = tableWidget.getDataSourceProvider();
      if (node.pos != '0-0' && selectedKeys.length > 0) {
        dataSource.removeParam('categoryUuid');
        dataSource.addParam('categoryUuid', selectedKeys[0]);
      } else {
        dataSource.removeParam('categoryUuid');
      }
      tableWidget.refetch(true);
    });
  }

  /**
   * 删除流程分类
   *
   * @param {*} evt
   * @returns
   */
  deleteFlowCategory(evt) {
    const _this = this;
    if (!evt || !evt.meta || !evt.meta.key) {
      _this.$widget.$message.error('请选择分类');
      return;
    }
    _this.$widget.$confirm({
      title: '确认框',
      content: `确定删除分类[${evt.meta.title}]吗？`,
      okText: '确定',
      cancelText: '取消',
      onOk() {
        $axios
          .post(`/api/workflow/category/deleteWhenNotUsed`, {
            uuid: evt.meta.key
          })
          .then(({ data: result }) => {
            if (result.code == 0) {
              // 刷新分类树
              _this.$widget.pageContext.emitEvent('omhnjCXbgLMLFfkVlnSJVXPJuAHPDTSj:refetch');
              _this.$widget.pageContext.emitEvent('gAIyNpFRleXGWDnXVmohGLuafyCIiwsM:refetch');
              _this.$widget.$message.success('删除成功');
            } else {
              _this.$widget.$message.error(result.msg);
            }
          })
          .catch(error => {
            _this.$widget.$message.error('删除失败');
          });
      }
    });
  }

  /**
   * 定义导入
   *
   * @param {*} evt
   */
  importFlowDefinition(evt) {
    let DefDataImportComponent = Vue.extend(ImportDef);
    let defDataImport = new DefDataImportComponent({ propsData: { title: '定义导入', visible: true } });
    let importDiv = document.createElement('div');
    importDiv.classList.add('import-container');
    document.body.appendChild(importDiv);
    defDataImport.$mount(importDiv);
    defDataImport.show();
  }

  /**
   * 定义导出
   *
   * @param {*} evt
   */
  exportFlowDefinition(evt) {
    const _this = this;
    let selectedRows = evt.meta.selectedRows || [evt.meta];
    let uuids = selectedRows.map(row => row.uuid);
    if (uuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    let DefDataExportComponent = Vue.extend(ExportDef);
    let defDataExport = new DefDataExportComponent({
      propsData: { title: '定义导出', visible: true, uuid: uuids, type: 'flowDefinition' }
    });
    let exportDiv = document.createElement('div');
    exportDiv.classList.add('export-container');
    document.body.appendChild(exportDiv);
    defDataExport.$mount(exportDiv);
    defDataExport.show();
  }

  /**
   * 导出流程定义清单
   *
   * @param {*} evt
   */
  exportFlowDefinitionList(evt) {
    const _this = this;
    let tableWidget = _this.getVueWidgetById('xYRNaUypXIUrqzNSafsNcebKyOaKWtlH');
    let dataSource = tableWidget.getDataSourceProvider();
    let exportColumns = tableWidget.widget.configuration.columns.filter(column => !column.hidden);
    exportColumns.forEach(column => {
      if (!column.columnIndex) {
        column.columnIndex = column.dataIndex;
      }
      if (!column.renderer && column.renderFunction && column.renderFunction.type == 'freemarkerTemplateRenderer') {
        column.renderer = {
          rendererType: column.renderFunction.type,
          template: column.renderFunction.options.template
        };
      }
    });
    dataSource.exportData({
      fileName: '流程清单导出',
      type: 'excel',
      exportColumns,
      pagination: {
        pageSize: 100000,
        currentPage: 1
      }
    });
  }

  /**
   * 复制流程定义
   *
   * @param {*} evt
   */
  copyFlowDefinition(evt) {
    const _this = this;
    let selectedRows = evt.meta.selectedRows || [evt.meta];
    if (selectedRows.length != 1) {
      _this.$widget.$message.error('请选择一条记录！');
      return;
    }

    let flowDefinition = selectedRows[0];
    let Modal = Vue.extend({
      template: `<a-modal :title="title" :visible="visible" width="600px" :maskClosable="false" @ok="handleOk" @cancel="handleCancel">
        <WfFlowDefinitionCopyForm ref="form" :flowDefinition="flowDefinition"></WfFlowDefinitionCopyForm>
        <template slot="footer">
          <a-button @click="handleCancel">取消</a-button>
          <a-button type="primary" @click="handleOk($event, true)">保存并编辑</a-button>
          <a-button type="primary" @click="handleOk">保存</a-button>
        </template>
      </a-modal>`,
      components: { WfFlowDefinitionCopyForm: () => import('./template/wf-flow-definition-copy-form.vue') },
      data: function () {
        return { title: '复制流程定义', flowDefinition, visible: true };
      },
      created() { },
      methods: {
        handleCancel() {
          this.visible = false;
          this.$destroy();
        },
        handleOk: debounce(
          function (e, edit = false) {
            this.$refs.form
              .collect()
              .then(formData => {
                if (!formData) {
                  return;
                }
                _this.$widget.$loading('复制中...');
                _this.doCopyFlowDefinition(flowDefinition.uuid, formData.flowDefName, formData.flowDefId, edit).then(success => {
                  if (success) {
                    this.visible = false;
                    this.$destroy();
                  }
                  _this.$widget.$loading(false);
                });
              })
              .catch(() => {
                _this.$widget.$loading(false);
                _this.$widget.$message.error('复制失败！');
              });
          },
          500,
          {
            leading: true,
            trailing: false
          }
        )
      }
    });
    let modal = new Modal();
    modal.$mount();
  }

  /**
   * 处理流程定义复制
   *
   * @param {*} uuid
   * @param {*} newFlowDefName
   * @param {*} newFlowDefId
   * @param {*} edit
   */
  doCopyFlowDefinition(uuid, newFlowDefName, newFlowDefId, edit) {
    const _this = this;
    return $axios
      .post('/json/data/services', {
        serviceName: 'flowSchemeService',
        methodName: 'copy',
        args: JSON.stringify([uuid, newFlowDefName, newFlowDefId])
      })
      .then(({ data: result }) => {
        if (!result.success) {
          this.$widget.$message.error(result.msg);
        } else {
          // 刷新流程定义
          this.$widget.pageContext.emitEvent('xYRNaUypXIUrqzNSafsNcebKyOaKWtlH:refetch');
          this.$widget.pageContext.emitEvent('JSWYlKZVCkBeOAYIWBENYLIDDnQBRQZR:refetch');
          this.$widget.$message.success('复制成功！');
          setTimeout(() => {
            if (edit) {
              $axios.get(`/proxy/api/workflow/scheme/flow/json.action?uuid=${result.data}`).then(({ data: result }) => {
                let url = '';
                if (result.xmlDefinition) {
                  url = `/web/app/pt-mgr/pt-wf-mgr/pt-wf-designer.html?id=${result.uuid}`;
                } else {
                  url = `/workflow-designer/index?uuid=${result.uuid}`;
                }
                window.open(_this.addSystemPrefix(url));
              });
            }
          }, 1000);
        }
        return result.success;
      })
      .catch(({ response }) => {
        if (response.data && response.data.data && response.data.data.msg) {
          this.$widget.$message.error(response.data.data.msg);
        }
      });
  }

  addSystemPrefix(url) {
    const _this = this;
    if (_this.$widget._$SYSTEM_ID && url && !url.startsWith('/sys/')) {
      url = `/sys/${_this.$widget._$SYSTEM_ID}/_${url}`;
    }
    return url;
  }

  /**
   * 逻辑删除流程定义
   *
   * @param {*} evt
   * @returns
   */
  logicalDeleteFlowDefinition(evt) {
    const _this = this;
    let selectedRows = evt.meta.selectedRows || [evt.meta];
    let uuids = selectedRows.map(row => row.uuid);
    if (uuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    _this.$widget.$confirm({
      title: '确认框',
      content: `确定删除流程定义[${selectedRows.map(row => row.name)}]吗？`,
      onOk() {
        $axios
          .post(`/api/workflow/definition/logical-delete-all`, {
            uuids
          })
          .then(({ data: result }) => {
            if (result.code == 0) {
              // 刷新流程定义
              _this.$widget.pageContext.emitEvent('xYRNaUypXIUrqzNSafsNcebKyOaKWtlH:refetch');
              _this.$widget.pageContext.emitEvent('JSWYlKZVCkBeOAYIWBENYLIDDnQBRQZR:refetch');
              _this.$widget.$message.success('删除成功');
            } else {
              _this.$widget.$message.error(result.msg);
            }
          })
          .catch(error => {
            _this.$widget.$message.error('删除失败');
          });
      }
    });
  }

  /**
   * 恢复流程定义
   *
   * @param {*} evt
   */
  recoveryFlowDefinition(evt) {
    const _this = this;
    let selectedRows = evt.meta.selectedRows || [evt.meta];
    let uuids = selectedRows.map(row => row.uuid);
    if (uuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    $axios.post(`/proxy/api/workflow/definition/recoveryAll?uuids=${uuids}`).then(({ data: result }) => {
      if (result && result.code === 0) {
        // 刷新流程定义
        _this.$widget.pageContext.emitEvent('xYRNaUypXIUrqzNSafsNcebKyOaKWtlH:refetch');
        _this.$widget.pageContext.emitEvent('JSWYlKZVCkBeOAYIWBENYLIDDnQBRQZR:refetch');
        _this.$widget.$message.success('恢复成功！恢复的流程需要重新启用');
      } else {
        _this.$widget.$message.error(result.msg);
      }
    });
  }

  /**
   * 物理删除流程定义
   *
   * @param {*} evt
   */
  physicalDeleteFlowDefinition(evt) {
    const _this = this;
    let selectedRows = evt.meta.selectedRows || [evt.meta];
    let uuids = selectedRows.map(row => row.uuid);
    if (uuids.length == 0) {
      _this.$widget.$message.error('请选择记录！');
      return;
    }

    _this.$widget.$confirm({
      title: '确认框',
      content: `确定彻底删除流程定义[${selectedRows.map(row => row.name)}]吗？`,
      onOk() {
        $axios
          .post(`/api/workflow/definition/physical-delete-all`, {
            uuids
          })
          .then(({ data: result }) => {
            if (result.code == 0) {
              // 刷新流程定义
              _this.$widget.pageContext.emitEvent('xYRNaUypXIUrqzNSafsNcebKyOaKWtlH:refetch');
              _this.$widget.pageContext.emitEvent('JSWYlKZVCkBeOAYIWBENYLIDDnQBRQZR:refetch');
              _this.$widget.$message.success('删除成功');
            } else {
              _this.$widget.$message.error(result.msg);
            }
          })
          .catch(error => {
            _this.$widget.$message.error('删除失败');
          });
      }
    });
  }

  /**
   * 流程仿真
   *
   * @param {*} evt
   */
  flowSimulation(evt) {
    const _this = this;
    let selectedRows = evt.meta.selectedRows || [evt.meta];
    if (selectedRows.length <= 0) {
      _this.$widget.$message.error('请选择记录！');
    } else if (selectedRows.length == 1) {
      // 打开仿真详情
      window.open(this.addSystemPrefix(`/workflow-simulation/index?uuid=${selectedRows[0].uuid}`));
    } else {
      // 批量仿真
      let index = 0;
      let allWorkUrls = [];
      let onSuccess = (workUrls = []) => {
        allWorkUrls = [...allWorkUrls, ...workUrls];
        let flowDefinition = selectedRows[index++];
        if (flowDefinition) {
          let workflowSimulation = new WorkflowSimulation({
            $widget: evt.$evtWidget,
            flowDefinition,
            showLoading: true,
            showLoadingFlowName: true,
            onSuccess,
            onPause: onSuccess
          });
          workflowSimulation.readyAndstart(false);
        } else if (allWorkUrls.length) {
          _this.$widget.$confirm({
            title: '确认框',
            content: '流程仿真成功，是否查看流程数据！',
            okText: '确定',
            cancelText: '取消',
            onOk() {
              allWorkUrls.forEach(url => {
                window.open(_this.addSystemPrefix(url));
              });
            }
          });
        }
      };
      onSuccess();
    }
  }

  /**
   * 定时清理设置
   *
   * @param {*} evt
   */
  scheduleDeleteSetting(evt) {
    const _this = this;
    let Modal = Vue.extend({
      template: `<a-modal :title="title" :visible="visible" width="600px" :maskClosable="false" ok-text="确定" cancel-text="取消" @ok="handleOk" @cancel="handleCancel">
        <WfFlowDefinitionAutoCleanUpSetting ref="form"></WfFlowDefinitionAutoCleanUpSetting>
      </a-modal>`,
      components: { WfFlowDefinitionAutoCleanUpSetting: () => import('./template/wf-flow-definition-auto-cleanup-setting.vue') },
      data: function () {
        return { title: '定时清除设置', visible: true };
      },
      created() { },
      methods: {
        handleCancel() {
          this.visible = false;
          this.$destroy();
        },
        handleOk(e) {
          this.$refs.form.save().then(success => {
            if (success) {
              this.visible = false;
              this.$destroy();
            }
          });
        }
      }
    });
    let modal = new Modal();
    modal.$mount();
  }
}

export default WfFlowDefinitionPageDevelopment;
