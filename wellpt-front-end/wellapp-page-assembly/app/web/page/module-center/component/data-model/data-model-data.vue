<template>
  <div>
    <a-space style="margin-bottom: 12px">
      <Drawer
        title="新增数据"
        ref="addDataDrawer"
        :width="500"
        :destroyOnClose="true"
        :mask="true"
        :maskClosable="true"
        drawerClass="pt-drawer"
      >
        <a-button type="primary">
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          新增
        </a-button>
        <template slot="content">
          <DataModelDataEdit :data-model="dataModel" ref="newData" :key="newDataKey" />
        </template>
        <template slot="footer">
          <a-button type="primary" @click="saveAndNewNextData">保存并添加下一个</a-button>
          <a-button
            type="default"
            @click="
              () => {
                this.$refs.addDataDrawer.hide();
              }
            "
          >
            取消
          </a-button>
          <a-button type="primary" @click="saveEditData(null, 'newData')">保存</a-button>
        </template>
      </Drawer>
      <a-button @click="e => removeRows(selectedRowKeys)">
        <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        删除
      </a-button>
      <a-switch v-model="showSysDefaultColumn" checked-children="显示内置属性" un-checked-children="显示内置属性"></a-switch>
      <Modal title="设置内置数据" :ok="e => confirmMarkData(e)" :width="760" :maxHeight="400">
        <a-button @click="onClickMarkDataBtn">
          <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
          设置内置数据
        </a-button>
        <template slot="content">
          <div>
            <a-alert :message="'已选择数据 ' + selectedRowKeys.length + ' 将数据设置为: '" type="info" style="margin-bottom: 10px" />
            <div>
              <a-page-header
                v-for="(opt, i) in markDataOptions"
                :key="'markOptDesc_' + i"
                style="border: 1px solid rgb(235, 237, 240); margin-bottom: 10px; cursor: pointer"
                :sub-title="opt.description"
                @click.native.stop="selectMarkDataType = opt.type"
              >
                <template slot="title">
                  <a-checkbox :checked="selectMarkDataType == opt.type">
                    {{ opt.title }}
                  </a-checkbox>
                </template>
              </a-page-header>
            </div>
          </div>
        </template>
      </Modal>
    </a-space>
    <a-table
      class="pt-table"
      rowKey="UUID"
      :columns="modelColumns"
      :data-source="rows"
      :pagination="pagination"
      @change="onTableChange"
      :row-selection="rowSelection"
      :loading="loading"
      :scroll="{ x: rows.length > 0 ? scrollX : undefined, y: rows.length > 0 ? 530 : undefined }"
    >
      <template slot="uuidSlot" slot-scope="text, record">
        {{ text }}
        <!-- <a-tag v-if="record._markedType == 'FIXED'">固化数据</a-tag>
        <a-tag v-else-if="record._markedType == 'DEMO'" color="blue">样例数据</a-tag> -->
      </template>
      <template slot="operationSlot" slot-scope="text, record">
        <a-tag :visible="record._markedType == 'FIXED'" closable @close="removeDataMark(record)">固化数据</a-tag>
        <a-tag :visible="record._markedType == 'DEMO'" color="blue" closable @close="removeDataMark(record)">样例数据</a-tag>
        <Drawer
          title="编辑数据"
          ref="drawer"
          :width="500"
          okText="保存"
          :ok="e => saveEditData(e, 'editData_' + record.UUID)"
          :destroyOnClose="true"
          :mask="true"
          :maskClosable="true"
          drawerClass="pt-drawer"
        >
          <a-button type="link" size="small">
            <Icon type="pticon iconfont icon-ptkj-bianji"></Icon>
            编辑
          </a-button>
          <template slot="content">
            <DataModelDataEdit
              :data-model="dataModel"
              :row-data="record"
              :showSysDefaultColumn="showSysDefaultColumn"
              :ref="'editData_' + record.UUID"
            />
          </template>
        </Drawer>
        <a-button type="link" size="small" @click="e => removeRows([record.UUID])">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
          删除
        </a-button>
      </template>
    </a-table>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import DataModelDataEdit from './data-model-data-edit.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';

export default {
  name: 'DataModelData',
  props: { dataModel: Object },
  components: { Drawer, DataModelDataEdit, Modal },
  computed: {
    modelColumns() {
      let cols = [];
      if (this.dataModel != undefined && this.dataModel.columnJson != undefined) {
        let columnJson = JSON.parse(this.dataModel.columnJson);
        this.scrollX = 0;
        for (let c of columnJson) {
          if (!this.showSysDefaultColumn && c.isSysDefault) {
            continue;
          }
          let col = {
            title: c.title,
            dataIndex: c.column,
            // width: 100,
            primaryKey: c.column === 'UUID'
          };
          if (c.column === 'UUID') {
            col.scopedSlots = { customRender: 'uuidSlot' };
          }
          this.scrollX += 100;
          cols.push(col);
        }

        cols.push({
          title: '操作',
          dataIndex: '__$OPERATION',
          width: 210,
          align: 'center',
          fixed: 'right',
          scopedSlots: { customRender: 'operationSlot' }
        });
        this.scrollX += 210;
      }
      return cols;
    }
  },
  data() {
    return {
      scrollX: 0,
      loading: true,
      rows: [],
      pagination: { pageSize: 10, current: 1, totalPages: 0 },
      selectedRowKeys: [],
      selectedRows: [],
      rowSelection: {
        onChange: (selectedRowKeys, selectedRows) => {
          this.selectedRowKeys = selectedRowKeys;
          this.selectedRows = selectedRows;
        }
      },
      selectMarkDataType: undefined,
      markDataOptions: [
        {
          title: '固化数据',
          description: '数据将作为模块的出厂固化数据，授权和部署后自动内置到模块中，用户无法修改和删除',
          icon: 'plus',
          type: 'FIXED'
        },
        {
          title: '样例数据',
          description: '数据将作为模块的数据demo，授权和部署后自动内置到模块中，用户可修改和删除',
          icon: 'plus',
          type: 'DEMO'
        },
        {
          title: '普通数据',
          description: '取消固化数据、样例数据标识，不再是内置数据',
          icon: 'plus',
          type: 'NORMAL'
        }
      ],
      showSysDefaultColumn: false,
      newDataKey: Date.now()
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchDataModelRows();
  },
  mounted() {},
  methods: {
    refresh() {
      this.fetchDataModelRows();
    },
    confirmMarkData(e) {
      $axios
        .get(`/proxy/api/dm/markDataType/uf_${this.dataModel.id}?${'uuid=' + this.selectedRowKeys.join('&uuid=')}`, {
          params: { type: this.selectMarkDataType === 'NORMAL' ? null : this.selectMarkDataType }
        })
        .then(({ data }) => {
          if (data.code == 0) {
            let rows = this.selectedRows;
            for (let i = 0, len = rows.length; i < len; i++) {
              this.$set(rows[i], '_markedType', this.selectMarkDataType === 'NORMAL' ? undefined : this.selectMarkDataType);
            }
            this.$message.success('设置数据成功');
            e(true);
          }
        })
        .catch(() => {});
    },
    removeDataMark(row) {
      $axios
        .get(`/proxy/api/dm/markDataType/uf_${this.dataModel.id}?${'uuid=' + row.UUID}`, {
          params: { type: null }
        })
        .then(({ data }) => {
          if (data.code == 0) {
            row._markedType = undefined;
          }
        })
        .catch(() => {});
    },
    onClickMarkDataBtn(e) {
      this.selectMarkDataType = undefined;
      if (this.selectedRowKeys.length == 0) {
        e.stopPropagation();
        this.$message.info('请选择行');
      }
    },
    removeRows(keys) {
      const _this = this;
      this.$confirm({
        title: '提示',
        content: '确定要删除吗?',
        okText: '确定',
        cancelText: '取消',
        onOk() {
          $axios
            .get(`/proxy/api/dm/deleteByUuid/uf_${_this.dataModel.id}?uuid=${keys.join('&uuid=')}`, {
              params: {}
            })
            .then(({ data }) => {
              _this.pagination.current = 1;
              _this.fetchDataModelRows();
            });
        },
        onCancel() {}
      });
    },
    saveEditData(e, item, callback) {
      let _this = this;
      this.$refs[item].submit(() => {
        if (e && typeof e === 'function') {
          e(true);
        }
        if (callback && typeof callback === 'function') {
          callback();
        }
        if (callback === undefined && e === null) {
          this.$refs.addDataDrawer.hide();
        }
        _this.fetchDataModelRows();
      });
    },
    // 保存并添加下一个
    saveAndNewNextData() {
      this.saveEditData(null, 'newData', () => {
        this.newDataKey = Date.now();
      });
    },
    onTableChange(pagination) {
      if (typeof this.pagination !== 'boolean') {
        this.pagination.current = pagination.current;
        this.pagination.pageSize = pagination.pageSize;
      }
      this.fetchDataModelRows();
    },
    fetchDataModelRows() {
      this.loading = true;
      $axios
        .post(`/proxy/api/dm/queryTableData/uf_${this.dataModel.id}`, {
          pagingInfo: {
            pageSize: this.pagination.pageSize,
            currentPage: this.pagination.current,
            autoCount: true
          },
          orders: [
            {
              sortName: 'CREATE_TIME',
              sortOrder: 'desc'
            }
          ]
        })
        .then(({ data }) => {
          this.loading = false;
          // 异步加载标记数据
          let rows = data.data.data,
            dataUuids = [],
            rowDataMap = {};
          for (let i = 0, len = rows.length; i < len; i++) {
            rows[i]._markedType = undefined;
            dataUuids.push(rows[i].UUID);
            rowDataMap[rows[i].UUID] = rows[i];
          }
          this.pagination.total = data.data.pagination.totalCount;
          this.pagination.current = data.data.pagination.currentPage;
          this.pagination.totalPages = data.data.pagination.totalPages;
          this.rows = rows;
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
    },
    getDrawerContainer() {
      return document.querySelector('#data-model-detail-body').querySelector('.ant-card-body');
    }
  }
};
</script>
