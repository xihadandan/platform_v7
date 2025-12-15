<template>
  <div>
    <template v-if="viewMode === viewModeOption[2]">
      <w-tree-select
        v-model="selectedValue"
        :formDataFieldName="formDataFieldName"
        :formData="formData"
        :treeData="flowTreeData"
        :labelInValue="labelInValue"
        @change="changeTreeFlowId"
      />
    </template>
    <template v-else-if="viewMode === viewModeOption[0]">
      <span @mouseenter="onmouseenter" @mouseleave="onmouseleave">
        <a-input v-model="selectedName" @click="modalVisible = true" :readOnly="true">
          <template slot="suffix">
            <a-icon type="close-circle" theme="filled" v-if="hoverInput && selectedValue != undefined" @click="onClickRemoveSelect" />
            <a-icon :type="loading ? 'loading' : modalVisible ? 'folder-open' : 'folder'" v-else @click="modalVisible = true" />
          </template>
        </a-input>
        <a-modal :maskClosable="false" :z-index="10000" title="请选择" v-model="modalVisible" :footer="null" width="calc(100% - 600px)">
          <a-table
            :columns="columns"
            :dataSource="pagination ? dataSource : vFilterFormRows"
            :rowKey="valuePropKey"
            :pagination="pagination"
            @change="handleTableChange"
            size="small"
            :scroll="{ y: 300 }"
          >
            <template slot="title">
              <div style="display: flex; align-items: center; justify-content: space-between">
                <div style="align-self: center; line-height: 30px">
                  <template v-if="rowSelection.selectedRows.length > 0">
                    已选:
                    <a-tag class="primary-color" :closable="true" @close="onClickRemoveSelect">
                      {{ rowSelection.selectedRows[0][namePropKey] }}
                    </a-tag>
                  </template>
                </div>
                <a-input
                  style="width: 200px"
                  v-model.trim="searchText"
                  placeholder="搜索名称/ID"
                  :allowClear="true"
                  @change="onChangeSearch"
                  @pressEnter="handleSearch"
                >
                  <Icon slot="suffix" type="iconfont icon-ptkj-sousuochaxun" @click.stop="handleSearch" />
                </a-input>
              </div>
            </template>
            <template slot="nameSlot" slot-scope="text, record">
              {{ text }}
            </template>
          </a-table>
        </a-modal>
      </span>
    </template>
  </div>
</template>

<script>
const viewModeOption = ['modal', 'select', 'treeSelect'];
import WTreeSelect from '../components/w-tree-select';
import { fetchAllFlowAsCategoryTree } from '../api/index';

export default {
  name: 'FlowSelectMultipleViews',
  props: {
    value: {
      type: [String, Array]
    },
    viewMode: {
      type: String,
      default: 'modal',
      validator: mode => {
        return viewModeOption.includes(mode);
      }
    },
    formData: {
      type: Object,
      default: () => {
        return {};
      }
    },
    formDataFieldName: {
      type: String,
      default: ''
    },
    labelInValue: {
      type: Boolean,
      default: false
    },
    separator: {
      type: String,
      default: ';'
    },
    treeCheckable: {
      type: Boolean,
      default: false // 显示checkbox, true时multiple为true表示多选
    },
    // true时父子节点选中状态不再关联
    treeCheckStrictly: {
      type: Boolean,
      default: false
    },
    valuePropKey: {
      type: String,
      default: 'id'
    },
    namePropKey: {
      type: String,
      default: 'name'
    },
    needPagination: {
      type: Boolean,
      default: false
    }
  },
  data() {
    let pagination;
    if (this.needPagination) {
      pagination = {
        showQuickJumper: true,
        current: 1,
        total: 0,
        pageSize: 10,
        showTotal: function (total, range) {
          const totalPages = total % this.pageSize === 0 ? parseInt(total / this.pageSize) : parseInt(total / this.pageSize + 1);
          return `共 ${totalPages} 页/ ${total} 条记录`;
        }
      };
    } else {
      pagination = false;
    }
    return {
      viewModeOption,
      selectedValue: this.value,
      selectedName: undefined,
      flowOptions: [],
      flowIdMap: {},
      flowTreeData: [],
      loading: false,
      modalVisible: false,
      hoverInput: false,
      columns: [
        {
          title: '名称',
          dataIndex: this.namePropKey,
          scopedSlots: { customRender: 'nameSlot' },
          customCell: this.customCell
        },
        {
          title: 'ID',
          dataIndex: 'id',
          customCell: this.customCell
        }
      ],
      searchText: undefined,
      rowSelection: { selectedRows: [], selectedRowKeys: [], type: 'radio', onChange: this.onChangeRowSelect },
      dataSource: [],
      pagination
    };
  },
  components: {
    WTreeSelect
  },
  computed: {
    vFilterFormRows() {
      let rows = [];
      const addRow = data => {
        if (
          this.searchText == undefined ||
          data.id.toLowerCase().indexOf(this.searchText.toLowerCase()) != -1 ||
          data[this.namePropKey].toLowerCase().indexOf(this.searchText.toLowerCase()) != -1
        ) {
          rows.push(data);
        }
      };

      this.flowOptions.forEach(data => {
        addRow(data);
      });
      return rows;
    }
  },
  created() {
    this.getFlowTreeDataSync();
  },
  methods: {
    // 同步方式获取流程树
    getFlowTreeDataSync() {
      this.$tempStorage.getCache(
        '_FlowTreeDataSync',
        () => {
          return new Promise((resolve, reject) => {
            fetchAllFlowAsCategoryTree().then(res => {
              resolve(res);
            });
          });
        },
        res => {
          this.flowOptions = [];
          this.flowIdMap = {};
          this.rowSelection.selectedRowKeys = [];
          this.rowSelection.selectedRows = [];
          const formatTreeData = arr => {
            if (!arr) {
              return [];
            }
            return arr.map(item => {
              const children = item['children'];

              let node = {
                title: item.name,
                key: item.id,
                label: item.name,
                value: item.id,
                isLeaf: children && children.length ? false : true,
                selectable: children && children.length ? false : true,
                sourceData: item
              };

              if (this.viewMode !== this.viewModeOption[2]) {
                let option = {
                  title: item.name,
                  key: item.id,
                  label: item.name,
                  value: item.id,
                  name: item.name,
                  id: item.id
                };
                this.flowIdMap[item.id] = option;
                this.flowOptions.push(option);
                let key = option[this.valuePropKey];
                if (key == this.selectedValue) {
                  this.selectedName = option[this.namePropKey];
                  this.rowSelection.selectedRowKeys.push(option[this.valuePropKey]);
                  this.rowSelection.selectedRows.push(option);
                }
              }

              if (children) {
                node['children'] = formatTreeData(children);
                return node;
              }
              return node;
            });
          };
          this.flowTreeData = formatTreeData(res);
          if (this.viewMode === this.viewModeOption[0] && this.pagination) {
            this.dataSource = this.getPaginatedData();
          }
        }
      );
    },
    handleTableChange(pagination, filters, sorter, { currentDataSource }) {
      this.pagination.pageSize = pagination.pageSize;
      this.pagination.current = pagination.current;
      this.dataSource = this.getPaginatedData();
    },
    // 获取分页数据
    getPaginatedData(data = this.vFilterFormRows, pageSize = this.pagination.pageSize, currentPage = this.pagination.current) {
      const startIndex = (currentPage - 1) * pageSize;
      const endIndex = startIndex + pageSize;
      this.pagination.total = this.vFilterFormRows.length;
      return data.slice(startIndex, endIndex);
    },
    onChangeSearch() {
      this.$nextTick(() => {
        if (!this.searchText) {
          this.handleSearch();
        }
      });
    },
    handleSearch() {
      if (this.pagination) {
        this.pagination.current = 1;
        this.dataSource = this.getPaginatedData();
      }
    },
    // 自定义单元格
    customCell(record, rowIndex) {
      return {
        on: {
          click: event => {
            if (event.target.nodeName !== 'A') {
              let lastedSelected = undefined;
              if (this.rowSelection.selectedRowKeys.length) {
                lastedSelected = this.rowSelection.selectedRowKeys[0];
              }
              this.rowSelection.selectedRowKeys.splice(0, this.rowSelection.selectedRowKeys.length);
              this.rowSelection.selectedRows.splice(0, this.rowSelection.selectedRows.length);
              if (lastedSelected != record[this.valuePropKey]) {
                this.rowSelection.selectedRowKeys.push(record[this.valuePropKey]);
                this.rowSelection.selectedRows.push(record);
              }
              this.onModalOkSelectDone(false);
            }
          }
        }
      };
    },
    onmouseenter() {
      this.hoverInput = true;
    },
    onmouseleave() {
      this.hoverInput = false;
    },
    // 弹窗中选择完成
    onModalOkSelectDone(closeModal = true) {
      this.selectedValue = '';
      this.selectedName = '';
      if (this.rowSelection.selectedRows && this.rowSelection.selectedRows.length > 0) {
        this.selectedValue = this.rowSelection.selectedRows[0][this.valuePropKey];
        this.selectedName = this.rowSelection.selectedRows[0][this.namePropKey];
      }

      if (Object.keys(this.formData).length && this.formDataFieldName) {
        if (this.selectedName) {
          this.formData[this.formDataFieldName] = this.selectedName;
        } else {
          this.formData[this.formDataFieldName] = '';
        }
      }

      this.$emit('input', this.selectedValue);
      this.$emit('change', this.selectedValue, this.selectedValue ? this.flowIdMap[this.selectedValue] : {});
      if (closeModal) {
        this.modalVisible = false;
      }
    },
    // 监听表格行选择
    onChangeRowSelect(selectedRowKeys, selectedRows) {
      this.rowSelection.selectedRows = selectedRows;
      this.rowSelection.selectedRowKeys = selectedRowKeys;
    },
    // 移除选择
    onClickRemoveSelect() {
      this.onCloseSelectTag();
      this.selectedValue = '';
      this.selectedName = '';
      this.$emit('input', this.selectedValue);
      this.$emit('change', this.selectedValue, {});
    },
    onCloseSelectTag() {
      this.rowSelection.selectedRowKeys.splice(0, this.rowSelection.selectedRowKeys.length);
      this.rowSelection.selectedRows.splice(0, this.rowSelection.selectedRows.length);
    },
    changeTreeFlowId(flowDefId, label, extra) {
      this.$emit('input', flowDefId);
      this.$emit('change', ...arguments);
    }
  }
};
</script>

<style lang="less">
.ant-table-pagination {
  &.mini {
    .ant-pagination-item-container {
      height: auto !important;
    }
  }
}
</style>
