<template>
  <!-- 备选项来源 常量 -->
  <div>
    <!-- 分组下拉框 常量 特殊处理 -->
    <div class="wcfg-select-item" v-if="isSelectGroup">
      <a-button-group class="table-header-operation" size="small">
        <a-button @click="addGroupOption" icon="plus">新增</a-button>
        <a-button @click="delGroupOption" icon="delete">删除</a-button>
        <a-button @click="moveGroupOption('forward')" icon="arrow-up">上移</a-button>
        <a-button @click="moveGroupOption" icon="arrow-down">下移</a-button>
      </a-button-group>
      <a-table
        size="small"
        rowKey="id"
        :row-selection="{ columnWidth: 65, selectedRowKeys: selectedRowKeys, onChange: selectChange }"
        :pagination="false"
        bordered
        :columns="groupColumns"
        :data-source="options.groupOptions"
      >
        <template slot="valueSlot" slot-scope="text, record">
          <a-input addon-before="分组名" v-model="record.value" size="small">
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :designer="designer" :target="record" :code="record.id" v-model="record.value" />
            </template>
          </a-input>
        </template>
      </a-table>
    </div>
    <div class="wcfg-select-item">
      <a-button-group class="table-header-operation" size="small">
        <a-button @click="addDefineOption" icon="plus">新增</a-button>
        <a-button @click="delDefineOption" icon="delete">删除</a-button>
        <a-button @click="moveDefineOption('forward')" icon="arrow-up">上移</a-button>
        <a-button @click="moveDefineOption" icon="arrow-down">下移</a-button>
      </a-button-group>
      <a-table
        size="small"
        rowKey="id"
        :row-selection="{ columnWidth: 65, selectedRowKeys: selectedOptionRowKeys, onChange: selectConstantChange }"
        :pagination="false"
        bordered
        :columns="selfDefineColumns"
        :data-source="options.defineOptions"
      >
        <template slot="valueSlot" slot-scope="text, record, index">
          <a-input addon-before="展示文本" v-model="record.label" size="small">
            <template slot="addonAfter">
              <WI18nInput :widget="widget" :designer="designer" :target="record" :code="record.id" v-model="record.label" />
            </template>
          </a-input>
          <a-input addon-before="值" v-model="record.value" size="small" />
          <span class="select-group-name" v-if="isSelectGroup">
            <span class="ant-input-group-addon">选择分组</span>
            <span>
              <a-select size="small" :allowClear="true" v-model="record.group" :getPopupContainer="getPopupContainerByPs()">
                <a-select-option v-for="item in options.groupOptions" :key="item.id" :value="item.value" :title="item.value">
                  {{ item.value }}
                </a-select-option>
              </a-select>
            </span>
          </span>
        </template>
      </a-table>
    </div>
    <!-- 联动设置 -->
    <a-form-model-item label="联动设置" :wrapper-col="{ style: { textAlign: 'right' } }">
      <a-switch v-model="widget.configuration.optionDataAutoSet" />
    </a-form-model-item>
    <template v-if="widget.configuration.optionDataAutoSet">
      <RelateFieldConfiguration :widget="widget" :designer="designer" :options="widget.configuration.options" />
    </template>
  </div>
</template>

<script>
import { generateId, swapArrayElements } from '@framework/vue/utils/util';
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
export default {
  name: 'OptionSourceSelfDefine',
  props: {
    designer: Object,
    widget: Object,
    options: Object
  },
  data() {
    return {
      selectedOptionRowKeys: [], // 选中备选项key
      selectedOptionRows: [], // 选中的备选项
      selfDefineColumns: [
        {
          title: '选项',
          dataIndex: 'value',
          scopedSlots: { customRender: 'valueSlot' }
        }
      ], // 表格列设置
      selectedRowKeys: [],
      selectedRows: [],
      groupColumns: [
        {
          title: '分组定义',
          dataIndex: 'value',
          scopedSlots: { customRender: 'valueSlot' }
        }
      ]
    };
  },
  computed: {
    isSelectGroup() {
      return this.widget.wtype == 'WidgetFormSelect' && this.widget.configuration.type == 'select-group';
    }
  },
  created() {
    if (this.isSelectGroup) {
      if (!this.options.hasOwnProperty('groupOptions')) {
        this.$set(this.options, 'groupOptions', []);
      }
    }
  },
  methods: {
    getPopupContainerByPs,
    // 选中备选项
    selectConstantChange(selectedRowKeys, selectedRows) {
      this.selectedOptionRowKeys = selectedRowKeys;
      this.selectedOptionRows = selectedRows;
    },
    // 新增
    addDefineOption() {
      const item = {
        id: generateId(),
        label: '',
        value: ''
      };
      if (this.isSelectGroup) {
        item.group = '';
      }
      this.options.defineOptions.push(item);
    },
    // 删除
    delDefineOption() {
      for (let i = 0, len = this.selectedOptionRowKeys.length; i < len; i++) {
        for (let j = 0, jlen = this.options.defineOptions.length; j < jlen; j++) {
          if (this.options.defineOptions[j].id == this.selectedOptionRowKeys[i]) {
            this.options.defineOptions.splice(j, 1);
            break;
          }
        }
      }
    },
    // 上移/下移
    moveDefineOption(direction) {
      let ids = [];
      for (let i = 0, len = this.selectedOptionRows.length; i < len; i++) {
        ids.push(this.selectedOptionRows[i].id);
      }
      swapArrayElements(
        ids,
        this.options.defineOptions,
        function (a, b) {
          return a == b.id;
        },
        direction
      );
    },
    selectChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys;
      this.selectedRows = selectedRows;
    },
    addGroupOption() {
      const id = generateId();
      this.options.groupOptions.push({
        id,
        value: ''
      });
    },
    delGroupOption() {
      for (let i = 0, len = this.selectedRowKeys.length; i < len; i++) {
        for (let j = 0, jlen = this.options.groupOptions.length; j < jlen; j++) {
          if (this.options.groupOptions[j].id == this.selectedRowKeys[i]) {
            this.options.groupOptions.splice(j, 1);
            break;
          }
        }
      }
    },
    moveGroupOption(direction) {
      let ids = [];
      for (let i = 0, len = this.selectedRows.length; i < len; i++) {
        ids.push(this.selectedRows[i].id);
      }
      swapArrayElements(
        ids,
        this.options.groupOptions,
        function (a, b) {
          return a == b.id;
        },
        direction
      );
    }
  }
};
</script>

<style lang="less">
.select-group-name {
  .ant-select .ant-select-selection {
    border-top-left-radius: 0;
    border-bottom-left-radius: 0;
  }
  > span {
    &:last-child {
      display: table-cell;
      width: 100%;
    }
  }
}
</style>
