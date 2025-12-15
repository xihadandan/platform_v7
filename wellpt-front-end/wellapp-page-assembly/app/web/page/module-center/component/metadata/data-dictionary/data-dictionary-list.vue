<template>
  <div class="data-dictionary-list">
    <div class="flex f_nowrap" style="margin-bottom: 12px">
      <a-space class="f_g_1">
        <Drawer
          title="新增字典"
          ref="newDataDrawer"
          :width="800"
          :container="getDrawerContainer"
          :mask="true"
          :maskClosable="true"
          okText="保存"
          :zIndex="1000"
          :destroyOnClose="true"
          drawerClass="pt-drawer"
        >
          <a-button type="primary">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            新增
          </a-button>
          <template slot="content">
            <DataDictionaryDetail :moduleSource="moduleSource" :dataDictionary="formData" ref="newData" :categories="categories" />
          </template>
          <template slot="footer">
            <a-button type="primary" @click="saveAndNewNextData('newData')">保存并添加下一个</a-button>
            <a-button type="default" @click="closeDrawer('newData')">取消</a-button>
            <a-button type="primary" @click="saveDictionary(null, 'newData')">保存</a-button>
          </template>
        </Drawer>
        <Drawer
          v-if="supportsGeneric"
          title="新增通用字典"
          ref="newGenerateDataDrawer"
          :width="800"
          :container="getDrawerContainer"
          :mask="true"
          :maskClosable="true"
          okText="保存"
          :destroyOnClose="true"
          drawerClass="pt-drawer"
        >
          <a-button type="primary">
            <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
            新增通用字典
          </a-button>
          <template slot="content">
            <DataDictionaryDetail
              moduleSource="none"
              :dataDictionary="formData"
              ref="newGenerateData"
              :categories="categories"
              extAttrLeft="120px"
            />
          </template>
          <template slot="footer">
            <a-button type="primary" @click="saveAndNewNextData('newGenerateData')">保存并添加下一个</a-button>
            <a-button type="default" @click="closeDrawer('newGenerateData')">取消</a-button>
            <a-button type="primary" @click="saveDictionary(null, 'newGenerateData')">保存</a-button>
          </template>
        </Drawer>
        <a-button type="danger" @click="onDeleteAllClick">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
          删除
        </a-button>
        <Modal title="字典使用检测" :width="900" :height="600" :ok="e => e(true)" :destroyOnClose="true" dialogClass="pt-modal">
          <a-button type="default">
            <Icon type="pticon iconfont icon-daping-01"></Icon>
            字典使用检测
          </a-button>
          <template slot="content">
            <DataDictionaryUsedList :categoryType="categoryType"></DataDictionaryUsedList>
          </template>
        </Modal>
        <DataDictionaryImport></DataDictionaryImport>

        <a-button type="default" @click="onBatchTranslateClick">批量翻译</a-button>
      </a-space>
      <div class="f_s_0" style="width: 300px">
        <a-input-search
          placeholder="关键字"
          v-model="searchText"
          allow-clear
          @search="onSearch"
          style="width: calc(100% - 82px); margin-right: 12px"
        />
        <a-button type="primary" @click="onSearch">查询</a-button>
      </div>
    </div>
    <a-table
      rowKey="uuid"
      :columns="dictionaryColumns"
      :data-source="rows"
      :pagination="pagination"
      @change="onTableChange"
      :row-selection="rowSelection"
      class="pt-table data-dictionary-list-table"
    >
      <template slot="orderSlot" slot-scope="text, record, index">
        {{ getOrder(index) }}
      </template>
      <template slot="nameSlot" slot-scope="text, record, index">
        {{ record.name }}
        <a-tag class="primary-color w-ellipsis" style="max-width: 160px; vertical-align: middle" :title="record.code">
          {{ record.code }}
        </a-tag>
      </template>
      <template slot="modifierSlot" slot-scope="text, record, index">
        <div>
          <div>{{ record.modifierName }}</div>
          <div>{{ record.modifyTime }}</div>
        </div>
      </template>

      <template slot="operationSlot" slot-scope="text, record">
        <Drawer
          title="编辑字典"
          ref="drawer"
          :width="800"
          :container="getDrawerContainer"
          okText="保存"
          :mask="true"
          :maskClosable="true"
          :ok="e => saveDictionary(e, 'editData_' + record.uuid)"
          :destroyOnClose="true"
          drawerClass="pt-drawer"
        >
          <a-button type="link" size="small">
            <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
            编辑
          </a-button>
          <template slot="content">
            <DataDictionaryDetail
              :moduleSource="supportsGeneric && !record.moduleId ? 'none' : moduleSource"
              :dataDictionary="record"
              :categories="categories"
              :ref="'editData_' + record.uuid"
            />
          </template>
        </Drawer>
        <DataDictionaryExport :dataDictionary="record"></DataDictionaryExport>
        <a-button type="link" size="small" @click="onRowDataDeleteClick(record)">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
          删除
        </a-button>
      </template>
    </a-table>
    <div style="text-align: right; margin-top: 20px">
      <a-pagination v-model="pagination.current" :pageSize="pagination.pageSize" @change="onChangePagination" :total="pagination.total" />
    </div>
  </div>
</template>

<script>
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import DataDictionaryDetail from './data-dictionary-detail.vue';
import DataDictionaryUsedList from './data-dictionary-used-list.vue';
import DataDictionaryExport from './data-dictionary-export.vue';
import DataDictionaryImport from './data-dictionary-import.vue';
export default {
  props: {
    categories: Array,
    categoryUuid: [String, Number],
    categoryType: {
      String,
      default: 'module' // 字典分类类型，buildIn、module
    },
    queryParams: {
      type: Object,
      default() {
        return {};
      }
    },
    // 是否支持通用字典
    supportsGeneric: {
      type: Boolean,
      default: false
    },
    // 模块来源，'current'当前模块，'all'系统所有模块,‘none’没有模块归属
    moduleSource: {
      type: String,
      default: 'current'
    }
  },
  inject: ['currentModule', 'pageContext'],
  components: {
    Drawer,
    Modal,
    DataDictionaryDetail,
    DataDictionaryUsedList,
    DataDictionaryExport,
    DataDictionaryImport
  },
  data() {
    const _this = this;
    return {
      searchText: '',
      dictionaryColumns: [
        { title: '序号', width: 60, dataIndex: 'order', scopedSlots: { customRender: 'orderSlot' }, align: 'center' },
        { title: '字典名称 / 编码', dataIndex: 'name', scopedSlots: { customRender: 'nameSlot' } },
        // { title: '编码', dataIndex: 'code' },
        { title: '分类', width: 90, dataIndex: 'categoryName' },
        // { title: '最后修改时间', dataIndex: 'modifyTime' },
        { title: '修改人 / 最后修改时间', width: 175, dataIndex: 'modifierName', scopedSlots: { customRender: 'modifierSlot' } },
        { title: '操作', width: 225, dataIndex: '__operation', scopedSlots: { customRender: 'operationSlot' } }
      ],
      rows: [],
      pagination: {
        pageSize: 10,
        current: 1,
        totalPages: 0,
        showTotal(total, range) {
          return `共${_this.pagination.totalPages}页/${total}条记录`;
        }
      },
      selectedRowKeys: [],
      selectedRows: [],
      rowSelection: {
        onChange: (selectedRowKeys, selectedRows) => {
          this.selectedRowKeys = selectedRowKeys;
          this.selectedRows = selectedRows;
        }
      },
      formData: {
        moduleId: undefined
      },
      onlyTranslateEmpty: true,
      translatingAll: false
    };
  },
  created() {
    this.loadData();
  },
  methods: {
    closeDrawer(type) {
      if (this.$refs[`${type}Drawer`]) {
        this.$refs[`${type}Drawer`].hide();
      }
    },
    // 保存并添加下一个
    saveAndNewNextData(id) {
      this.saveDictionary(null, id, () => {
        this[`${id}Key`] = Date.now();
      });
    },
    onBatchTranslateClick() {
      if (this.selectedRows.length) {
        let _this = this;
        this.$confirm({
          title: '确定要翻译选择字典的所有字典项吗?',
          content: h => (
            <div>
              <a-checkbox checked={_this.onlyTranslateEmpty} onChange={e => (_this.onlyTranslateEmpty = e.target.checked)}>
                仅翻译未进行翻译的文字内容
              </a-checkbox>
            </div>
          ),
          onOk() {
            _this.translatingAll = true;
            _this.$loading('翻译中...');
            _this.$axios
              .post(`/proxy/api/datadict/translateAll`, { uuids: _this.selectedRowKeys, onlyTranslateEmpty: _this.onlyTranslateEmpty })
              .then(({ data }) => {
                _this.translatingAll = false;
                _this.$loading(false);
                if (data.code == 0) {
                  _this.$message.success('翻译成功');
                } else {
                  _this.$message.error('翻译异常');
                }
              })
              .catch(error => {
                _this.$message.error('翻译异常');
                _this.$loading(false);
              });
          },
          onCancel() {}
        });
      }
    },
    refresh() {
      this.loadData();
    },
    onSearch() {
      this.pagination.current = 1;
      this.loadData();
    },
    onTableChange(pagination) {
      if (pagination) {
        this.pagination = pagination;
      }
      this.loadData();
    },
    loadData() {
      let criterions = [];
      if (this.categoryUuid) {
        criterions.push({
          columnIndex: 'categoryUuid',
          value: this.categoryUuid,
          type: 'eq'
        });
      }
      if (this.categoryType == 'buildIn') {
        criterions.push({
          columnIndex: 'categoryType',
          value: 0,
          type: 'eq'
        });
      } else if (this.categoryType == 'module') {
        criterions.push({
          type: 'or',
          conditions: [
            {
              columnIndex: 'categoryType',
              value: 1,
              type: 'eq'
            },
            {
              columnIndex: 'categoryType',
              type: 'is null'
            }
          ]
        });
      }
      if (this.currentModule && this.currentModule.id) {
        criterions.push({
          columnIndex: 'moduleId',
          value: this.currentModule.id,
          type: 'eq'
        });
      }
      if (this.searchText) {
        criterions.push({
          type: 'or',
          conditions: [
            {
              columnIndex: 'name',
              value: this.searchText,
              type: 'like'
            },
            {
              columnIndex: 'code',
              value: this.searchText,
              type: 'like'
            }
          ]
        });
      }
      $axios
        .post(`/proxy/api/datastore/loadData`, {
          dataStoreId: 'CD_DS_DATA_DICTIONARY',
          pagingInfo: { currentPage: this.pagination.current, pageSize: this.pagination.pageSize },
          criterions,
          params: this.queryParams
        })
        .then(({ data }) => {
          let rows = data.data.data;
          this.pagination.total = data.data.pagination.totalCount;
          this.pagination.current = data.data.pagination.currentPage;
          this.pagination.totalPages = data.data.pagination.totalPages;
          this.rows = rows;
        });
    },
    getOrder(index) {
      if (this.pagination.current <= 0) {
        return index + 1;
      }
      return this.pagination.pageSize * (this.pagination.current - 1) + index + 1;
    },
    getDrawerContainer() {
      return document.body; //this.$el; // document.querySelector('.data-dictionary-list');
    },
    onDeleteAllClick() {
      const _this = this;
      if (_this.selectedRowKeys.length == 0) {
        _this.$message.error('请选择要删除的记录！');
        return;
      }
      _this.$confirm({
        title: '提示',
        content: `确认删除选择的字典？`,
        onOk() {
          _this.deleteDictionries(_this.selectedRowKeys);
        }
      });
    },
    onRowDataDeleteClick(rowData) {
      const _this = this;
      _this.$confirm({
        title: '提示',
        content: `确认删除字典——${rowData.name}？`,
        onOk() {
          _this.deleteDictionries([rowData.uuid]);
        }
      });
    },
    deleteDictionries(uuids) {
      const _this = this;
      $axios
        .post(`/proxy/api/datadict/deleteAll?uuids=${uuids}`)
        .then(({ data }) => {
          if (data.code == 0) {
            // 刷新表格
            _this.refresh();
            _this.$message.success('删除成功！');
            _this.pageContext.emitEvent('dataDictionary:loadCount');
          } else {
            _this.$message.error(data.msg || '删除失败！');
          }
        })
        .catch(({ response }) => {
          if (response.data && response.data.msg) {
            _this.$message.error(response.data.msg);
          } else {
            _this.$message.error('删除失败！');
          }
        });
    },
    saveDictionary(e, refCompId, callback) {
      let _this = this;
      this.$refs[refCompId].submit(() => {
        if (typeof e === 'function') {
          e(true);
        }
        if (typeof callback === 'function') {
          callback();
        }
        if (callback === undefined && e === null) {
          this.closeDrawer(refCompId);
        }
        _this.formData = {
          moduleId: undefined
        };
        _this.refresh();
        _this.pageContext.emitEvent('dataDictionary:loadCount');
      });
    },
    onChangePagination() {
      this.onTableChange();
    }
  }
};
</script>

<style lang="less">
.data-dictionary-list {
  height: e('calc(100vh - 124px)');
  background-color: #ffffff;
  padding: var(--w-padding-xs) var(--w-padding-md);
  .data-dictionary-list-table {
    height: e('calc(100% - 100px)');
    overflow: auto;
    .ant-table-pagination {
      display: none;
    }
  }
  .data-dictionary-ref-list-table {
    height: e('calc(100% - 150px)');
    overflow: auto;
    .ant-table-pagination {
      display: none;
    }
  }
}
</style>
