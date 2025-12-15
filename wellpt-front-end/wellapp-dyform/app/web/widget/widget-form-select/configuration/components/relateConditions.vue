<template>
  <!-- 联动条件配置 -->
  <a-form-model class="form-relate-conditions" layout="vertical" v-bind="{}">
    <!-- 改成表单形式 -->
    <a-form-model-item label="联动项来源">
      <a-select
        :options="sourceOptions"
        :style="{ width: '100%' }"
        v-model="conditionCfg.source"
        :getPopupContainer="getPopupContainerByPs()"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="联动项"><SelectRelateField :widget="widget" /></a-form-model-item>
    <a-form-model-item label="联动条件">
      <a-select
        placeholder="联动条件"
        :options="conditionOptions"
        :style="{ width: '100%' }"
        v-model="conditionCfg.condition"
        @change="changeCondition"
        :getPopupContainer="getPopupContainerByPs()"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="联动条件值">
      <a-input placeholder="请输入条件值" v-model="conditionCfg.relateVal" />
    </a-form-model-item>
    <template v-if="widget.configuration.type === 'select-tree' || widget.wtype == 'WidgetFormCascader'">
      <a-form-model-item label="备选项">
        <a-tree-select
          v-model="conditionCfg.options"
          style="width: 100%"
          :tree-data="widget.configuration.treeData"
          :replaceFields="{ title: 'display', key: 'id', value: 'id' }"
          tree-checkable
          showCheckedStrategy="SHOW_ALL"
          placeholder="备选项（可多选）"
          :getPopupContainer="getPopupContainerByPs()"
          :dropdownClassName="getDropdownClassName()"
        >
          <span slot="title">1</span>
        </a-tree-select>
      </a-form-model-item>
    </template>
    <template v-else>
      <a-form-model-item label="备选项">
        <a-select
          mode="multiple"
          placeholder="备选项（可多选）"
          :options="options.defineOptions"
          :style="{ width: '100%' }"
          v-model="conditionCfg.options"
          @change="changeOptions"
          :getPopupContainer="getPopupContainerByPs()"
          :dropdownClassName="getDropdownClassName()"
        />
      </a-form-model-item>
    </template>
  </a-form-model>
  <!-- 先注释表格形式，后期要做成的多条件“与或” -->
  <!-- <div style="padding-bottom: 10px">
      <a-button type="primary" size="small" @click="addColumn">添加</a-button>
      <a-button type="primary" size="small" @click="delColumn" style="margin-left: 10px">删除</a-button>
    </div>
    <a-table
      size="small"
      :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
      :pagination="false"
      :columns="columnDefine"
      :data-source="relateCfgList"
      rowKey="id"
      bordered
    >
      <template slot="sourceSlot" slot-scope="text, record">
        <a-select :options="sourceOptions" :style="{ width: '100%' }" v-model="record.source"></a-select>
      </template>
      <template slot="relateKeySlot" slot-scope="text, record">
        <a-select
          placeholder="请选择"
          :options="inputFieldOptions"
          :style="{ width: '100%' }"
          size="small"
          v-model="record.relateKey"
        ></a-select>
      </template>
      <template slot="conditionSlot" slot-scope="text, record">
        <a-select
          placeholder="联动条件"
          :options="conditionOptions"
          :style="{ width: '100%' }"
          size="small"
          v-model="record.condition"
        ></a-select>
      </template>
      <template slot="relateValSlot" slot-scope="text, record">
        <a-input placeholder="请输入条件值" v-model="record.relateVal" />
      </template>
    </a-table> -->
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import { sourceOptions, conditionOptions } from '../../../commons/constant';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
export default {
  name: 'RelateConditions',
  props: {
    designer: Object,
    widget: Object,
    options: Object,
    conditionCfg: Object
  },
  data() {
    return {
      sourceOptions,
      conditionOptions,
      columnDefine: [
        { title: '联动项来源', dataIndex: 'source', width: 110, scopedSlots: { customRender: 'sourceSlot' } },
        { title: '联动项', dataIndex: 'relateKey', width: 120, scopedSlots: { customRender: 'relateKeySlot' } },
        { title: '联动条件', dataIndex: 'condition', width: 110, scopedSlots: { customRender: 'conditionSlot' } },
        { title: '联动条件值', dataIndex: 'relateVal', scopedSlots: { customRender: 'relateValSlot' } }
      ],
      relateCfgList: [] // 联动条件配置列表
    };
  },
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    // 添加
    addColumn() {
      this.relateCfgList.push({
        id: generateId(),
        source: this.sourceOptions[0].value
      });
    },
    // 删除
    delColumn() {},
    // 改变联动条件
    changeCondition(val) {
      const condition = this.conditionOptions.find(item => {
        return item.value === val;
      });
      this.conditionCfg.conditionLabel = condition.label;
    },
    // 改变备选项
    changeOptions(val) {
      this.conditionCfg.optionsArr = this.options.defineOptions.filter(item => {
        return val.includes(item.value);
      });
    }
  }
};
</script>

<style lang="less">
#design-main .widget-design-drawer .ant-drawer-content-wrapper .ant-drawer-body .form-relate-conditions .ant-form-item-label {
  width: auto;
  float: none;
}
#design-main .widget-design-drawer .ant-drawer-content-wrapper .ant-drawer-body .form-relate-conditions .ant-form-item-control-wrapper {
  width: auto;
  float: none;
}
</style>
