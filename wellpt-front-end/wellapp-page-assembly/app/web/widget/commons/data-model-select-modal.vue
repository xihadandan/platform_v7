<template>
  <div>
    <template v-if="!displayModal">
      <a-select
        v-model="selectedValue"
        :style="{ width: width }"
        :size="size"
        :allowClear="true"
        :showSearch="true"
        :mode="multiSelect ? 'multiple' : 'default'"
        :filterOption="filterSelectOption"
        :getPopupContainer="getPopupContainerNearest()"
        @change="onChangeOption"
      >
        <template slot="suffixIcon" v-if="loading">
          <a-icon type="loading" />
        </template>
        <template v-if="moreType">
          <!-- 分组展示 -->
          <template v-for="(grp, g) in dataOptions">
            <a-select-opt-group :label="grp.label" v-if="grp.children.length > 0" :key="'dt_grp_' + g">
              <a-select-option v-for="(opt, i) in grp.children" :value="opt.value" :key="opt.value" :title="opt.label">
                {{ opt.label }}
              </a-select-option>
            </a-select-opt-group>
          </template>
        </template>
        <template v-else>
          <a-select-option v-for="(opt, i) in dataOptions" :value="opt.value" :key="opt.value" :title="opt.label">
            {{ opt.label }}
          </a-select-option>
        </template>
      </a-select>
    </template>
    <template v-else>
      <span @mouseenter="onmouseenter" @mouseleave="onmouseleave">
        <a-input v-model="selectedName" @click="handleVisibleModal" :readOnly="true">
          <template slot="suffix">
            <a-icon type="close-circle" theme="filled" v-if="hoverInput && selectedValue != undefined" @click="onClickRemoveSelect" />
            <a-icon :type="loading ? 'loading' : modalVisible ? 'folder-open' : 'folder'" v-else @click="handleVisibleModal" />
          </template>
        </a-input>
        <a-modal :maskClosable="false" :z-index="10000" title="请选择" v-model="modalVisible" :footer="null" width="calc(100% - 600px)">
          <a-table
            :columns="columns"
            :data-source="vFilterFormRows"
            :rowKey="valuePropKey"
            :pagination="false"
            :customRow="customRow"
            size="small"
            :scroll="{ y: 300 }"
            @change="onTableChange"
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
                <a-input style="width: 200px" v-model.trim="searchText" placeholder="搜索名称/ID" :allowClear="true" />
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
<style lang="less"></style>
<script type="text/babel">
import { filterSelectOption } from '@framework/vue/utils/function';
export default {
  name: 'DataModelSelectModal',
  inject: ['appId', 'subAppIds'],
  props: {
    value: {
      type: [String, Array]
    },
    valuePropKey: {
      type: String,
      default: 'uuid'
    },
    namePropKey: {
      type: String,
      default: 'name'
    },
    displayModal: {
      type: Boolean,
      default: false
    },
    width: {
      type: String,
      default: '100%'
    },
    size: {
      type: String,
      default: 'default'
    },
    multiSelect: {
      type: Boolean,
      default: false
    },
    dtype: {
      type: [String, Array],
      default: 'TABLE' // TABLE \ VIEW
    }
  },
  data() {
    return {
      loading: false,
      dataOptions: [],
      optionMap: {},
      selectedValue: this.value,
      selectedName: undefined,
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
      rowSelection: { selectedRows: [], selectedRowKeys: [], type: 'radio', onChange: this.onChangeRowSelect }
    };
  },
  computed: {
    moreType() {
      return Array.isArray(this.dtype) && this.dtype.length > 0;
    },
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

      this.dataOptions.forEach(data => {
        if (this.moreType) {
          data.children.forEach(opt => {
            addRow(opt);
          });
        } else {
          addRow(data);
        }
      });
      return rows;
    }
  },
  created() {
    this.getDataOptions(this.dtype);
  },
  methods: {
    handleVisibleModal() {
      this.searchText = '';
      this.modalVisible = true;
    },
    fetchDataModelColumns(uuid) {
      return new Promise((resolve, reject) => {
        this.$tempStorage.getCache(
          `dm.getDetails:${uuid}`,
          () => {
            return new Promise((resolve, reject) => {
              $axios.get(`/proxy/api/dm/getDetails`, { params: { uuid } }).then(({ data, headers }) => {
                if (data.code == 0) {
                  let detail = data.data,
                    columns = JSON.parse(detail.columnJson),
                    columnIndexOptions = [];
                  for (let col of columns) {
                    if (detail.type == 'TABLE' || (detail.type == 'VIEW' && col.hidden !== true && col.return === true)) {
                      columnIndexOptions.push({
                        value: col.alias || col.column,
                        label: col.title,
                        isSysDefault: col.isSysDefault
                      });
                    }
                  }
                  resolve(columnIndexOptions);
                }
              });
            });
          },
          data => {
            resolve(data);
          }
        );
      });
    },
    reset(type) {
      this.getDataOptions(type);
      this.selectedValue = undefined;
      this.selectedName = undefined;
    },
    // 获取数据选项
    getDataOptions(type) {
      this.loading = true;
      this.optionMap = {};
      this.rowSelection.selectedRowKeys.splice(0, this.rowSelection.selectedRowKeys.length);
      this.rowSelection.selectedRows.splice(0, this.rowSelection.selectedRows.length);
      let module = [];
      if (this.appId) {
        module.push(this.appId);
      }
      if (this.subAppIds) {
        module.push(...this.subAppIds);
      }

      this.$tempStorage.getCache(
        { url: '/proxy/api/dm/getDataModelsByType', type: typeof type == 'string' ? [type] : type, module },
        () => {
          return new Promise((resolve, reject) => {
            $axios.post(`/proxy/api/dm/getDataModelsByType`, { type: typeof type == 'string' ? [type] : type, module }).then(({ data }) => {
              if (data.code == 0) {
                resolve(data.data);
              }
            });
          });
        },
        models => {
          this.dataOptions.splice(0, this.dataOptions.length);
          this.loading = false;
          if (this.moreType) {
            // 分组
            this.dataOptions.push(
              {
                label: '存储对象',
                value: 'table',
                children: []
              },
              {
                label: '视图对象',
                value: 'view',
                children: []
              }
            );
          }
          if (models) {
            for (let item of models) {
              let key = item[this.valuePropKey];
              this.optionMap[key] = item;
              if (key == this.selectedValue) {
                this.selectedName = item[this.namePropKey];
                this.rowSelection.selectedRowKeys.push(item[this.valuePropKey]);
                this.rowSelection.selectedRows.push(item);
              }
              if (this.moreType) {
                let tar = this.dataOptions[item.type == 'TABLE' || item.type == 'RELATION' ? 0 : 1];
                tar.children.push({
                  label: item[this.namePropKey],
                  value: item[this.valuePropKey],
                  ...item
                });
              } else {
                this.dataOptions.push({
                  label: item[this.namePropKey],
                  value: item[this.valuePropKey],
                  ...item
                });
              }
            }
          }
          this.$emit('optionsReady', this.optionMap);
        }
      );
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
    // 自定义行
    customRow(row, index) {
      return {
        on: {
          // 事件
          click: event => {
            // this.rowSelection.selectedRows = [row];
            // this.rowSelection.selectedRowKeys = [row.uuid];
          }
        }
      };
    },
    // 分页
    onTableChange(pagination, filters) {},
    onmouseenter() {
      this.hoverInput = true;
    },
    onmouseleave() {
      this.hoverInput = false;
    },
    // 弹窗中选择完成
    onModalOkSelectDone(closeModal = true) {
      this.selectedValue = undefined;
      this.selectedName = undefined;
      if (this.rowSelection.selectedRows && this.rowSelection.selectedRows.length > 0) {
        this.selectedValue = this.rowSelection.selectedRows[0][this.valuePropKey];
        this.selectedName = this.rowSelection.selectedRows[0][this.namePropKey];
      }
      this.$emit('input', this.selectedValue);
      this.$emit('change', this.selectedValue, this.selectedValue ? this.optionMap[this.selectedValue] : {});
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
      this.selectedValue = undefined;
      this.selectedName = undefined;
      this.$emit('input', undefined);
      this.$emit('change', undefined, {});
    },
    onCloseSelectTag() {
      this.rowSelection.selectedRowKeys.splice(0, this.rowSelection.selectedRowKeys.length);
      this.rowSelection.selectedRows.splice(0, this.rowSelection.selectedRows.length);
    },
    // 更改选项
    onChangeOption(value, option) {
      this.selectedValue = value;
      this.$emit('input', value);
      this.$emit('change', value, this.selectedValue ? this.optionMap[this.selectedValue] : {});
    },
    filterSelectOption,
    getPopupContainerNearest(triggerNode) {
      return triggerNode => {
        if (triggerNode.closest('.ps')) {
          return triggerNode.closest('.ps');
        }
        return triggerNode.parentNode;
      };
    }
  }
};
</script>
