<template>
  <!-- 树形下拉框-编辑模式属性 -->
  <div>
    <!-- 只能映射到单行文本框 -->
    <a-form-model-item prop="displayValueField" ref="displayValueField" :label-col="{}" :wrapper-col="{}" class="display-b">
      <template #label>
        <span>显示值字段</span>
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <template #title>
            <div>当选项选择后，显示值将一并更新至该字段。可配置隐藏字段，用于数据提交、存储。</div>
          </template>
          <a-icon type="exclamation-circle" />
        </a-tooltip>
      </template>
      <a-select
        placeholder="请选择"
        v-model="widget.configuration.displayValueField"
        :allowClear="true"
        :options="inputFieldOptions"
        :style="{ width: '100%' }"
        :getPopupContainer="getPopupContainerByPs()"
        :dropdownClassName="getDropdownClassName()"
      />
    </a-form-model-item>
    <a-form-model-item label="显示值取值逻辑" :label-col="{ span: 9 }" :wrapper-col="{ span: 14, style: { textAlign: 'right' } }">
      <a-radio-group size="small" v-model="editMode.allPath">
        <a-radio-button :value="false">仅节点名称</a-radio-button>
        <a-radio-button :value="true">全路径名称</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="在文本中显示真实值" class="display-b" :label-col="{}" :wrapper-col="{}">
      <a-select
        :allowClear="true"
        placeholder="请选择"
        :options="textWidgetOptions"
        :style="{ width: '100%' }"
        v-model="widget.configuration.textWidgetId"
        :getPopupContainer="getPopupContainerByPs()"
        :dropdownClassName="getDropdownClassName()"
      />
    </a-form-model-item>
    <a-form-model-item label="真实值取值逻辑" :label-col="{ span: 9 }" :wrapper-col="{ span: 14, style: { textAlign: 'right' } }">
      <a-radio-group size="small" v-model="editMode.valueAllPath">
        <a-radio-button :value="false">仅节点名称</a-radio-button>
        <a-radio-button :value="true">全路径名称</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="文本分隔符" :label-col="{ span: 9 }" :wrapper-col="{ span: 14, style: { textAlign: 'right' } }">
      <a-input v-model="widget.configuration.tokenSeparators" placeholder="请输入" />
    </a-form-model-item>
    <FieldSwitch :options="editModeOptions" :widget="widget" @handelRelay="handelRelay" />
    <a-form-model-item label="次级菜单展开方式" v-if="designer.terminalType == 'mobile'">
      <a-radio-group size="small" v-model="editMode.expandTrigger">
        <a-radio-button value="hover">悬停</a-radio-button>
        <a-radio-button value="click">点击</a-radio-button>
      </a-radio-group>
    </a-form-model-item>
  </div>
</template>

<script>
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
export default {
  name: 'CascaderEditMode',
  props: {
    designer: Object,
    widget: Object,
    rules: Object,
    inputFieldOptions: Array,
    textWidgetOptions: Array
  },
  data() {
    return {
      editModeOptions: [
        { label: '父级可选', key: 'changeOnSelect', disabled: false },
        { label: '显示清空按钮', key: 'allowClear', disabled: false },
        { label: '显示搜索框', key: 'showSearch', disabled: false }
      ],
      displayValueField: { required: true, message: <a-icon type="close-circle" theme="filled" />, trigger: ['blur'], whitespace: true }
    };
  },
  computed: {
    editMode() {
      return this.widget.configuration.editMode;
    },
    cfgOptions() {
      return this.widget.configuration.options;
    }
  },
  mounted() {},
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    handelRelay({ val, eventName }) {
      this[eventName](val);
    }
  }
};
</script>

<style lang="less" scoped>
.widget-cfg-item {
  padding-bottom: 10px;
}
.select-tree-sl {
  padding: 10px 8px 10px 16px;
}
</style>
