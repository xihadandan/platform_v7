<template>
  <div class="branch-table">
    <div style="pading: 8px 0">
      <a-button size="small" type="primary" @click="add">增加</a-button>
      <a-button size="small" type="primary" @click="edit">编辑</a-button>
      <a-button size="small" @click="del">删除</a-button>
      <a-button size="small" @click="up">上移</a-button>
      <a-button size="small" @click="down">下移</a-button>
    </div>
    <a-table
      :data-source="value"
      :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onChange }"
      :pagination="false"
      :row-key="(record, index) => index"
      bordered
    >
      <a-table-column key="name" data-index="name" title="列名" :width="160" class="label-item"></a-table-column>
      <a-table-column key="typeName" data-index="typeName" title="类型" :width="160" class="label-item"></a-table-column>
    </a-table>
    <a-modal
      title="办理进度列定义"
      :visible="modalVisible"
      @ok="save"
      @cancel="modalVisible = false"
      :width="700"
      :bodyStyle="{ height: '500px', 'overflow-y': 'auto', padding: '12px 20px' }"
      :getContainer="getContainer"
      :destroyOnClose="true"
    >
      <branch-columns-info
        :subFormData="subFormData"
        :columns="value"
        :formData="currentInfo"
        :index="currentIndex"
        ref="infoRef"
        :type="type"
      ></branch-columns-info>
    </a-modal>
  </div>
</template>

<script>
import { generateId, deepClone } from '@framework/vue/utils/util';
import { btnSource } from '../designer/constant';
import WSelect from '../components/w-select';
import { filter, map, each, findIndex, sortBy } from 'lodash';
import BranchColumnsInfo from './branch-columns-info.vue';
export default {
  name: 'BranchColumnsTable',
  props: {
    value: {
      type: Array,
      default: () => []
    },
    type: {
      type: String,
      default: ''
    },
    subFormData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    WSelect,
    BranchColumnsInfo
  },
  data() {
    each(this.value, item => {
      if (!item.uuid) {
        item.uuid = generateId();
      }
    });
    return {
      selectedRowKeys: [],
      selectedRows: [],
      modalVisible: false,
      currentInfo: '',
      currentIndex: -1
    };
  },
  watch: {},
  computed: {
    btnSourceOptions() {
      let options = [];
      if (this.type == 'subflow') {
        each(btnSource, item => {
          if (['closeSubView', 'allowSubView'].indexOf(item.id) == -1) {
            options.push(item);
          }
        });
      } else {
        options = deepClone(btnSource);
      }
      return options;
    }
  },
  created() {},
  methods: {
    getContainer() {
      return document.querySelector('.edit-widget-property-container');
    },
    onChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys; //选中的keys
      this.selectedRows = selectedRows; //选中的行
    },
    add() {
      this.currentInfo = {
        type: '1',
        typeName: '固定',
        index: '',
        name: '',
        sources: '',
        searchFlag: '0',
        extraColumn: null,
        uuid: generateId(),
        sortable: {
          enable: false,
          alogrithmType: 'orderByChar',
          datePattern: undefined,
          script: null
        },
        renderFunction: { type: undefined, options: {} },
        exportFunction: { type: undefined, options: {} },
        hidden: false,
        ellipsis: true,
        showTip: false,
        tipContent: undefined,
        titleAlign: 'left',
        contentAlign: 'left',
        titleHidden: false,
        defaultContentIfNull: '',
        clickEvent: {
          enable: false,
          eventHandler: {}
        }
      };
      this.currentIndex = -1;
      this.modalVisible = true;
    },
    edit() {
      if (this.selectedRows.length != 1) {
        this.$message.error('请选择编辑项');
        return false;
      }
      this.currentIndex = this.selectedRowKeys[0];
      let currentInfo = deepClone(this.selectedRows[0]);
      if (!currentInfo.hasOwnProperty('renderFunction')) {
        Object.assign(currentInfo, {
          sortable: {
            enable: false,
            alogrithmType: 'orderByChar',
            datePattern: undefined,
            script: null
          },
          renderFunction: { type: undefined, options: {} },
          exportFunction: { type: undefined, options: {} },
          hidden: false,
          ellipsis: true,
          showTip: false,
          tipContent: undefined,
          titleAlign: 'left',
          contentAlign: 'left',
          titleHidden: false,
          defaultContentIfNull: '',
          clickEvent: {
            enable: false,
            eventHandler: {}
          }
        });
      }
      if (typeof currentInfo.sortable == 'boolean') {
        currentInfo.sortable = {
          enable: false,
          alogrithmType: 'orderByChar',
          datePattern: undefined,
          script: null
        };
      }
      this.currentInfo = currentInfo;
      this.modalVisible = true;
    },
    save() {
      this.$refs.infoRef.save(({ valid, error, data }) => {
        if (valid) {
          if (this.currentIndex == -1) {
            this.value.push(data);
          } else {
            this.value.splice(this.currentIndex, 1, data);
            this.selectedRows[0] = data;
          }
          this.modalVisible = false;
        }
      });
    },
    up() {
      if (this.selectedRows.length == 0) {
        this.$message.error('请选择上移的项');
        return false;
      }
      each(sortBy(this.selectedRowKeys), (item, index) => {
        if (item > 0) {
          const currentItem = this.value[item];
          this.value.splice(item, 1);
          this.value.splice(item - 1, 0, currentItem);
        }
      });
      this.resetTableSelectKey();
    },
    down() {
      if (this.selectedRows.length == 0) {
        this.$message.error('请选择下移的项');
        return false;
      }
      each(sortBy(this.selectedRowKeys).reverse(), (item, index) => {
        if (item < this.value.length - 1) {
          const currentItem = this.value[item];
          this.value.splice(item + 2, 0, currentItem);
          this.value.splice(item, 1);
        }
      });
      this.resetTableSelectKey();
    },
    del() {
      if (this.selectedRows.length == 0) {
        this.$message.error('请选择删除的项');
        return false;
      }
      const newData = filter(this.value, (item, index) => {
        return findIndex(this.selectedRows, { uuid: item.uuid }) == -1;
      });
      this.emitChange(newData);
      this.selectedRowKeys = [];
      this.selectedRows = [];
    },
    resetTableSelectKey() {
      let selectedRowKeys = [];
      each(this.selectedRows, (item, index) => {
        const hasIndex = findIndex(this.value, { uuid: item.uuid });
        if (hasIndex > -1) {
          selectedRowKeys.push(hasIndex);
        }
      });
      this.selectedRowKeys = selectedRowKeys;
    },
    emitChange(data) {
      this.$emit('input', data);
      console.log(data);
    }
  }
};
</script>
