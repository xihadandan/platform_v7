<template>
  <div class="branch-table">
    <div style="pading: 8px 0">
      <a-button size="small" type="primary" @click="add">增加</a-button>
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
      <a-table-column key="name" data-index="name" title="排序列">
        <template slot-scope="text, record">
          <div class="ant-form-item">
            <w-select :options="fieldOptions" v-model="record.name" style="width: 140px"></w-select>
          </div>
        </template>
      </a-table-column>
      <a-table-column key="direction" data-index="direction" title="排序方式">
        <template slot-scope="text, record">
          <div class="ant-form-item">
            <w-select :options="sortSource" v-model="record.direction" style="width: 140px"></w-select>
          </div>
        </template>
      </a-table-column>
    </a-table>
  </div>
</template>

<script>
import { generateId, deepClone } from '@framework/vue/utils/util';
import { fixedField, fixedSortField, sortSource } from '../designer/constant';
import WSelect from '../components/w-select';
import { filter, map, each, findIndex, sortBy, assignIn } from 'lodash';
export default {
  name: 'BranchColumnsSortTable',
  props: {
    value: {
      type: Array,
      default: () => []
    },
    type: {
      type: String,
      default: ''
    },
    columns: {
      type: Array,
      default: () => []
    }
  },
  components: {
    WSelect
  },
  data() {
    return {
      selectedRowKeys: [],
      selectedRows: [],
      sortSource,
      nameOptions: []
    };
  },
  watch: {
    value: {
      deep: true,
      handler() {
        each(this.value, item => {
          if (!item.key) {
            item.key = generateId();
          }
        });
      }
    }
  },
  computed: {
    fieldOptions() {
      let options = deepClone(fixedSortField);
      each(this.columns, item => {
        if (item.type === '1') {
          // 固定列
          const hasIndex = options.findIndex(f => f.id === item.index);
          if (hasIndex > -1) {
            options[hasIndex]['text'] = item.name;
          }
        } else {
          // 扩展列
          options.push({
            id: item.index,
            text: item.name
          });
        }
      });
      return options.concat(this.nameOptions);
    }
  },
  created() {
    this.getNameOptions();
  },
  methods: {
    getNameOptions() {
      this.nameOptions = [];
      let options = [];
      this.$axios.get('/api/workflow/work/sortFields').then(({ data }) => {
        if (data.data) {
          data.data.forEach(item => {
            const curIndex = fixedField.findIndex(f => f.id === item.id);
            if (curIndex == -1) {
              options.push(item);
            }
          });
          this.nameOptions = options;
        }
      });
    },
    onChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys; //选中的keys
      this.selectedRows = selectedRows; //选中的行
    },
    add() {
      this.value.push({
        key: generateId(),
        name: '',
        direction: ''
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
        return findIndex(this.selectedRows, { key: item.key }) == -1;
      });
      this.emitChange(newData);
      this.selectedRowKeys = [];
      this.selectedRows = [];
    },
    resetTableSelectKey() {
      let selectedRowKeys = [];
      each(this.selectedRows, (item, index) => {
        const hasIndex = findIndex(this.value, { key: item.key });
        if (hasIndex > -1) {
          selectedRowKeys.push(hasIndex);
        }
      });
      this.selectedRowKeys = selectedRowKeys;
    },
    emitChange(data) {
      this.$emit('input', data);
    }
  }
};
</script>
