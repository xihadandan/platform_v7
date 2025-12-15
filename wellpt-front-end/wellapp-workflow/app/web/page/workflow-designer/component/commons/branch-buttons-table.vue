<template>
  <div class="branch-table">
    <div style="padding: 8px 0">
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
      <a-table-column key="id" data-index="id" title="按钮">
        <template slot-scope="text, record, index">
          <div class="ant-form-item">
            <w-select
              :options="btnSourceOptions"
              v-model="record.id"
              style="width: 140px"
              @change="(value, option) => changeBtn(option, record)"
            />
          </div>
        </template>
      </a-table-column>
      <a-table-column key="newName" data-index="newName" title="新名称">
        <template slot-scope="text, record">
          <div class="ant-form-item">
            <a-input v-model="record.newName" allow-clear style="width: 140px" @change="e => changeNewName(e, record)">
              <template slot="addonAfter">
                <w-i18n-input :target="record" :code="record.uuid" v-model="record.newName" />
              </template>
            </a-input>
          </div>
        </template>
      </a-table-column>
    </a-table>
  </div>
</template>

<script>
import { generateId, deepClone } from '@framework/vue/utils/util';
import { btnSource } from '../designer/constant';
import WSelect from '../components/w-select';
import { filter, map, each, findIndex, sortBy } from 'lodash';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'BranchButtonsTable',
  props: {
    value: {
      type: Array,
      default: () => []
    },
    type: {
      type: String,
      default: ''
    }
  },
  components: {
    WSelect,
    WI18nInput
  },
  data() {
    each(this.value, item => {
      if (!item.uuid) {
        item.uuid = generateId();
      }
    });
    return {
      selectedRowKeys: [],
      selectedRows: []
    };
  },

  computed: {
    btnSourceOptions() {
      let options = [];
      if (this.type != 'subflow') {
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
  created() {
    if (this.type === 'subflow' || this.type === 'task') {
      this.value.forEach(item => {
        if (!item.newName) {
          const btnSource = this.btnSourceOptions.find(b => b.id === item.id);
          if (btnSource) {
            item.newName = btnSource.text;
          }
        }
      });
    }
  },
  methods: {
    changeBtn(option, item) {
      if (this.type === 'subflow' || this.type === 'task') {
        item.newName = option.data.props.title;
      }
    },
    changeNewName(event, item) {
      const value = event.target.value;
      if (this.type === 'subflow' || this.type === 'task') {
        if (!value) {
          this.$message.error('新名称为空时设置默认名称');
          const btnSource = this.btnSourceOptions.find(b => b.id === item.id);
          if (btnSource) {
            item.newName = btnSource.text;
          }
        }
      }
    },
    onChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys; //选中的keys
      this.selectedRows = selectedRows; //选中的行
    },
    add() {
      this.value.push({
        uuid: generateId(),
        id: '',
        newName: ''
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
    }
  }
};
</script>
