<template>
  <div class="module-full-search-container">
    <a-card :bordered="false" class="preview-card">
      <template slot="title">
        <span class="title">选择分类数据源</span>
      </template>
      <a-table
        :rowKey="rowKey"
        :showHeader="false"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="pagination"
        :loading="tableLoading"
        :rowSelection="rowSelection"
        @change="handleTableChange"
        class="data-model-table"
      >
        <template slot="nameSlot" slot-scope="text, record">
          {{ `${record.name}（${record.id}）` }}
        </template>
        <template slot="subformMainSlot" slot-scope="text, record">
          <div class="_subform-main" v-if="record.showSubformMain">
            <div class="_subform-main-tag">
              主从关系：
              <span v-for="(item, index) in subformMainMap[record.id]" :key="index">
                <span v-if="index > 0">,</span>
                {{ item }}的从表
              </span>
            </div>
            <a-tooltip placement="top" :arrowPointAtCenter="true">
              <div slot="title">如果仅作为从表，可不选择为数据源</div>
              <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
            </a-tooltip>
          </div>
        </template>
        <template slot="operationSlot" slot-scope="text, record, index">
          <a-button
            type="link"
            :disabled="categoryUuid && allModelMap[record[rowKey]] && !categoryModelMap[record[rowKey]]"
            @click="handleDataModelRow(record)"
          >
            <Icon type="pticon iconfont icon-ptkj-shezhi" />
            更多设置
          </a-button>
        </template>
      </a-table>
    </a-card>
    <drawer v-model="visible" title="更多设置" :width="500" :container="getContainer" :mask="true" wrapClassName="search-model-drawer">
      <template slot="content">
        <model-info v-if="visible" :dataModel="currentDataModel" :formData="currentSearchModel" />
      </template>
      <template slot="footer">
        <a-button type="primary" @click="saveSearchModel" :loading="saveLoading">保存</a-button>
        <a-button @click="closeDrawer">取消</a-button>
      </template>
    </drawer>
  </div>
</template>

<script>
import { fetchDataModelsByType, fetchModelListByModuleId, fetchModelListByCategoryUuid, fetchSaveModel, fetchDeleteModel } from './api';
import { createModel } from './FullSearch';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import ModelInfo from './model-info.vue';

export default {
  name: 'ModuleFullSearchContent',
  inject: ['currentModule', 'pageContext'],
  props: {
    metadata: {
      type: Object
    },
    subformMainMap: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    Drawer,
    ModelInfo
  },
  data() {
    const moduleId = this.currentModule.id;
    const categoryUuid = (this.metadata && this.metadata.data && this.metadata.data.uuid) || (this.metadata && this.metadata.id);
    return {
      moduleId,
      categoryUuid,
      columns: [
        { title: '名称', dataIndex: 'name', scopedSlots: { customRender: 'nameSlot' } },
        { title: '', dataIndex: 'subformMain', width: 600, scopedSlots: { customRender: 'subformMainSlot' } },
        { title: '操作', dataIndex: 'operation', width: 170, scopedSlots: { customRender: 'operationSlot' }, align: 'center' }
      ],
      dataModels: [], // 模块下的所有存储对象
      dataSource: [],
      rowKey: 'uuid',
      selectedRowKeys: [],
      selectedRows: [],
      pagination: {
        showQuickJumper: true,
        showSizeChanger: true,
        current: 1,
        total: 0,
        pageSize: 10,
        showTotal: function (total, range) {
          const totalPages = total % this.pageSize === 0 ? parseInt(total / this.pageSize) : parseInt(total / this.pageSize + 1);
          return `共 ${totalPages} 页/ ${total} 条记录`;
        }
      },
      currentDataModel: null, // 当前存储对象
      visible: false,
      currentSearchModel: null, // 当前全文检索数据模型
      categoryModelList: [], // 分类下的全文检索数据模型
      categoryModelMap: {},
      allModelList: [], // 模块下所有的全文检索数据模型
      allModelMap: {},
      tableLoading: false,
      saveLoading: false
    };
  },
  computed: {
    rowSelection() {
      return {
        type: 'checkbox',
        selectedRowKeys: this.selectedRowKeys,
        onChange: this.changeSelectRow,
        getCheckboxProps: this.getCheckboxProps,
        columnWidth: 37
      };
    }
  },
  watch: {
    subformMainMap: {
      deep: true,
      immediate: true,
      handler(value) {
        this.setShowSubformMain(value);
      }
    },
    dataSource: {
      handler(value) {
        this.setShowSubformMain(this.subformMainMap);
      }
    }
  },
  beforeMount() {
    this.init();
  },
  mounted() {
    this.pageContext.handleEvent('deleteModelByCategoryUuid', this.batchDeleteModelByCategoryUuid);
  },
  beforeDestroy() {
    this.pageContext.offEvent('deleteModelByCategoryUuid', this.batchDeleteModelByCategoryUuid);
  },
  methods: {
    init() {
      this.tableLoading = true;
      let allFetch = [this.getDataModelsByType(), this.getModelListByModuleId()];
      if (this.categoryUuid) {
        allFetch.push(this.getModelListByCategoryUuid());
      }
      Promise.all(allFetch).then(res => {
        this.allModelList = res[1];
        let allModelMap = {};
        this.allModelList.forEach(item => {
          allModelMap[item.dataModelUuid] = item;
        });
        this.allModelMap = allModelMap;
        if (this.categoryUuid) {
          this.categoryModelList = res[2];
          let categoryModelMap = {};
          this.categoryModelList.forEach(item => {
            categoryModelMap[item.dataModelUuid] = item;
          });
          this.categoryModelMap = categoryModelMap;
        }

        this.dataModels = res[0];
        this.dataSource = this.getPaginatedData();
        this.pagination.total = this.dataModels.length;
        this.tableLoading = false;
      });
    },
    // 设置显示 “主从关系”
    setShowSubformMain(value) {
      this.dataSource.map(item => {
        if (value[item.id]) {
          this.$set(item, 'showSubformMain', true);
        }
      });
    },
    // 获取存储对象
    getDataModelsByType() {
      let req = {};
      req.type = ['TABLE'];
      req.module = [this.moduleId];
      // fetchDataModelsByType(req).then(res => {
      //   this.dataModels = res;
      //   this.dataSource = this.getPaginatedData();
      //   this.pagination.total = this.dataModels.length;
      // });
      return fetchDataModelsByType(req);
    },
    // 获取模型列表（通过模块id）
    getModelListByModuleId() {
      // fetchModelListByModuleId({
      //   moduleId: this.moduleId
      // }).then(res => {
      //   this.allModelList = res;
      //   let allModelMap = {}
      //   this.allModelList.forEach(item => {
      //     allModelMap[item.dataModelUuid] = item;
      //   });
      //   this.allModelMap = allModelMap;
      // });
      return fetchModelListByModuleId({
        moduleId: this.moduleId
      });
    },
    // 获取模型列表(通过分类uuid)
    getModelListByCategoryUuid(categoryUuid = this.categoryUuid) {
      // fetchModelListByCategoryUuid({
      //   categoryUuid: this.categoryUuid
      // }).then(res => {
      //   this.categoryModelList = res;
      //   let categoryModelMap = {}
      //   this.categoryModelList.forEach(item => {
      //     categoryModelMap[item.dataModelUuid] = item;
      //   });
      //   this.categoryModelMap = categoryModelMap;
      // });
      return fetchModelListByCategoryUuid({
        categoryUuid
      });
    },
    // 批量删除搜索模型(通过分类uuid)
    async batchDeleteModelByCategoryUuid(categoryUuid) {
      const models = await this.getModelListByCategoryUuid(categoryUuid);

      for (let index = 0; index < models.length; index++) {
        const item = models[index];
        await this.deleteSearchModelSync(item.uuid, item.dataModelUuid);
      }
    },
    // 同步删除搜索模型
    async deleteSearchModelSync(searchUuid, dataModelUuid) {
      return this.deleteSearchModel(searchUuid, dataModelUuid);
    },
    handleTableChange(pagination, filters, sorter, { currentDataSource }) {
      this.pagination.pageSize = pagination.pageSize;
      this.pagination.current = pagination.current;
      this.dataSource = this.getPaginatedData();
    },
    changeSelectRow(selectedRowKeys, selectedRows) {
      // this.selectedRowKeys = selectedRowKeys;
      // this.selectedRows = selectedRows;
    },
    getCheckboxProps(row) {
      let checked = false,
        disabled = false;
      if (this.categoryUuid) {
        if (this.allModelMap[row[this.rowKey]]) {
          checked = true;
          if (!this.selectedRowKeys.includes(row[this.rowKey])) {
            this.selectedRowKeys.push(row[this.rowKey]);
          }
        }

        if (checked && !this.categoryModelMap[row[this.rowKey]]) {
          disabled = true;
        }
      }
      return {
        props: {
          checked,
          disabled
        },
        on: {
          click: event => {
            const { valid } = this.checkCanAddModel();
            if (!valid) {
              return;
            }
            const { target } = event;
            const checked = target.checked;
            const uuid = target.closest('.ant-table-row').getAttribute('data-row-key');
            if (checked) {
              this.currentDataModel = this.dataSource.find(f => f.uuid === uuid);
              this.addSearchModel();
              this.saveSearchModel();
            } else {
              this.deleteSearchModel(this.allModelMap[uuid]['uuid'], uuid);
            }
          },
          change: event => {
            // console.log(event.target.checked);
          }
        }
      };
    },
    getPaginatedData(data = this.dataModels, pageSize = this.pagination.pageSize, currentPage = this.pagination.current) {
      const startIndex = (currentPage - 1) * pageSize;
      const endIndex = startIndex + pageSize;
      return data.slice(startIndex, endIndex);
    },
    // 删除搜索模型
    deleteSearchModel(searchUuid, dataModelUuid) {
      return new Promise((resolve, reject) => {
        fetchDeleteModel(searchUuid).then(() => {
          const findIndex = this.selectedRowKeys.findIndex(f => f === dataModelUuid);
          if (findIndex > -1) {
            this.selectedRowKeys.splice(findIndex, 1);
          }
          delete this.allModelMap[dataModelUuid];
          delete this.categoryModelMap[dataModelUuid];
          resolve();
        });
      });
    },
    // 校验是否能添加搜索模型
    checkCanAddModel() {
      let valid = true;
      if (!this.categoryUuid) {
        valid = false;
        this.$message.error('请选择分类');
      }
      if (valid && this.metadata && this.metadata.children && this.metadata.children.length) {
        valid = false;
        this.$message.error('仅末级分类可配置数据源');
      }
      return { valid };
    },
    // 点击存储对象行数据
    handleDataModelRow(row) {
      const { valid } = this.checkCanAddModel();
      if (!valid) {
        return;
      }
      this.currentDataModel = row;
      if (!this.allModelMap[row[this.rowKey]]) {
        this.addSearchModel();
        this.visible = true;
      }
      if (this.categoryModelMap[row[this.rowKey]]) {
        this.setSearchModel(row);
      }
    },
    // 添加搜索模型
    addSearchModel() {
      this.currentSearchModel = createModel({
        categoryUuid: this.categoryUuid,
        dataModelUuid: this.currentDataModel.uuid
      });
    },
    // 保存搜索模型
    saveSearchModel() {
      this.saveLoading = true;
      if (this.currentSearchModel.matchJson) {
        this.currentSearchModel.matchJson = JSON.stringify(this.currentSearchModel.matchJson);
      }
      fetchSaveModel(this.currentSearchModel).then(res => {
        this.currentSearchModel.uuid = res;
        this.allModelMap[this.currentSearchModel.dataModelUuid] = this.currentSearchModel;
        this.categoryModelMap[this.currentSearchModel.dataModelUuid] = this.currentSearchModel;
        if (!this.selectedRowKeys.includes(this.currentDataModel.uuid)) {
          this.selectedRowKeys.push(this.currentDataModel.uuid);
        }
        this.currentDataModel = null;
        this.currentSearchModel = null;
        this.visible = false;
        this.saveLoading = false;
      });
    },
    // 设置搜索模型
    setSearchModel(row) {
      this.currentSearchModel = this.categoryModelMap[row[this.rowKey]];
      this.visible = true;
    },
    getContainer() {
      return document.body;
    },
    closeDrawer() {
      this.visible = false;
    }
  }
};
</script>

<style lang="less">
.module-full-search-container {
  .ant-card-body {
    height: e('calc(100vh - 101px)');
    padding-bottom: 12px !important;
    > .data-model-table {
      padding: 20px;
      height: 100%;
      background-color: var(--w-color-white);
      overflow-y: auto;
      .ant-checkbox-disabled.ant-checkbox-checked .ant-checkbox-inner::after {
        display: none;
      }
      td {
        ._subform-main {
          display: flex;
          align-items: center;
        }
        ._subform-main-tag {
          color: #1890ff;
          background: #e6f7ff;
          border: solid 1px #91d5ff;
          padding: 0 7px;
          line-height: 20px;
          border-radius: 4px;
          font-size: 12px;
          margin-right: 4px;
        }
      }
    }
  }
}
.search-model-drawer {
  .ant-drawer-mask {
    background-color: transparent;
  }
}
</style>
