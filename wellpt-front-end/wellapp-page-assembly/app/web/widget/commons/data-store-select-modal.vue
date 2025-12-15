<template>
  <div>
    <template v-if="!displayModal">
      <a-select
        v-model="selectedValue"
        :showSearch="true"
        style="width: 100%"
        :filterOption="filterSelectOption"
        :getPopupContainer="getPopupContainerNearest()"
        @change="onChangeOption"
      >
        <template slot="suffixIcon" v-if="loading">
          <a-icon type="loading" />
        </template>
        <a-select-option v-for="d in dataSourceOptions" :value="d.id" :key="d.id">
          {{ d.text }}
        </a-select-option>
      </a-select>
    </template>
    <template v-else>
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
            :data-source="vFilterFormRows"
            rowKey="id"
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
                      {{ rowSelection.selectedRows[0].text }}
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
  name: 'DataStoreSelectModal',
  props: {
    value: String,
    valuePropKey: {
      type: String,
      default: 'id'
    },
    displayModal: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    vFilterFormRows() {
      let rows = [];
      this.dataSourceOptions.forEach(data => {
        if (
          this.searchText == undefined ||
          data.id.toLowerCase().indexOf(this.searchText.toLowerCase()) != -1 ||
          data.text.toLowerCase().indexOf(this.searchText.toLowerCase()) != -1
        ) {
          rows.push(data);
        }
      });
      return rows;
    }
  },
  data() {
    return {
      dataSourceOptions: [],
      optionMap: {},
      selectedValue: this.value,
      loading: false,
      selectedName: undefined,
      modalVisible: false,
      hoverInput: false,
      columns: [
        {
          title: '名称',
          dataIndex: 'text',
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
  created() {
    this.getDataSourceOptions();
  },
  methods: {
    // 获取数据仓库
    getDataSourceOptions(value) {
      this.loading = true;
      this.optionMap = {};
      this.rowSelection.selectedRowKeys.splice(0, this.rowSelection.selectedRowKeys.length);
      this.rowSelection.selectedRows.splice(0, this.rowSelection.selectedRows.length);
      this.$tempStorage.getCache(
        `viewComponentService.loadSelectData`,
        () => {
          return new Promise((resolve, reject) => {
            this.$axios
              .post('/common/select2/query', {
                serviceName: 'viewComponentService',
                queryMethod: 'loadSelectData'
              })
              .then(({ data }) => {
                resolve(data.results);
              });
          });
        },
        results => {
          this.loading = false;
          if (this.value != undefined) {
            this.selectedValue = this.value;
          }
          this.dataSourceOptions = results;
          const list = results;
          for (let i = 0, len = list.length; i < len; i++) {
            let key = list[i][this.valuePropKey];
            this.optionMap[key] = list[i];
            if (key == this.selectedValue) {
              this.selectedName = list[i].text;
              this.rowSelection.selectedRowKeys.push(list[i].id);
              this.rowSelection.selectedRows.push(list[i]);
            }
          }
        }
      );

      // this.$axios
      //   .post('/common/select2/query', {
      //     serviceName: 'viewComponentService',
      //     queryMethod: 'loadSelectData'
      //   })
      //   .then(({ data }) => {
      //     this.loading = false;
      //     if (this.value != undefined) {
      //       this.selectedValue = this.value;
      //     }
      //     if (data.results) {
      //       this.dataSourceOptions = data.results;
      //       const list = data.results;
      //       for (let i = 0, len = list.length; i < len; i++) {
      //         let key = list[i][this.valuePropKey];
      //         this.optionMap[key] = list[i];
      //         if (key == this.selectedValue) {
      //           this.selectedName = list[i].text;
      //           this.rowSelection.selectedRowKeys.push(list[i].id);
      //           this.rowSelection.selectedRows.push(list[i]);
      //         }
      //       }
      //     }
      //   });
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
              if (lastedSelected != record.id) {
                this.rowSelection.selectedRowKeys.push(record.id);
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
        this.selectedName = this.rowSelection.selectedRows[0].text;
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
      this.selectedName = undefined;
      this.selectedValue = undefined;
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
      this.$emit('change', this.selectedValue, this.selectedValue ? this.optionMap[this.selectedValue] : {}, option);
    },
    filterSelectOption,
    getPopupContainerNearest(triggerNode) {
      return triggerNode => {
        return triggerNode.parentNode;
      };
    }
  }
};
</script>
