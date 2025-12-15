<template>
  <!-- 联动列表 -->
  <div class="wcfg-select-item">
    <a-table
      size="small"
      :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
      :pagination="false"
      :columns="columns"
      :data-source="relateList"
      rowKey="id"
      bordered
    >
      <template slot="relateConditionSlot" slot-scope="text, record">
        {{ widget.configuration.relateKeyLabel }}{{ record.conditionLabel }}{{ record.relateVal }}
      </template>
      <template slot="valueSlot" slot-scope="text, record">
        已选{{ record.options ? record.options.length : 0 }}项
        <WidgetDesignDrawer :id="`editorRelateCondition${record.id}`" title="联动条件配置" :designer="designer">
          <div style="color: #1890ff; cursor: pointer">设置</div>
          <template slot="content">
            <RelateConditions :widget="widget" :designer="designer" :options="options" :conditionCfg="record" />
          </template>
        </WidgetDesignDrawer>
      </template>
    </a-table>
  </div>
</template>

<script>
import { conditionOptions } from '../../../commons/constant';
export default {
  name: 'RelateLists',
  props: {
    widget: Object,
    designer: Object,
    options: Object,
    relateList: Array
  },
  data() {
    return {
      conditionOptions,
      selectedRowKeys: [],
      columns: [
        { title: '联动条件', dataIndex: 'relateCondition', scopedSlots: { customRender: 'relateConditionSlot' } },
        { title: '备选项', dataIndex: 'value', scopedSlots: { customRender: 'valueSlot' } }
      ]
    };
  },
  methods: {
    // 多选
    onSelectChange(selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys;
      this.$emit('selectedRowKeys', this.selectedRowKeys);
    },
    // 重置选择项
    resetRowKeys() {
      this.selectedRowKeys = [];
    }
  }
};
</script>
