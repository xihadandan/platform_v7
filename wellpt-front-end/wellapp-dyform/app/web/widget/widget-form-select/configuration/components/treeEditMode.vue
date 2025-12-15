<template>
  <!-- 树形下拉框-编辑模式属性 -->
  <div>
    <template v-if="false && cfgOptions.type !== 'selfDefine'">
      <a-form-model-item label="节点加载方式">
        <a-radio-group size="small" v-model="editMode.loadAsync" @change="changeLoadAsync">
          <a-radio-button :value="false">同步加载</a-radio-button>
          <a-radio-button :value="true">异步加载</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
    </template>
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
    <FieldSwitch :options="editModeOptions" :widget="widget" @handelRelay="handelRelay" />
    <selected-count-configuration :widget="widget" />
    <!-- <FieldSwitch v-if="widget.configuration.options.type === 'dataDictionary'" :options="showMoreOptions" :widget="widget" />
    <FieldSwitch v-if="widget.configuration.editMode.showMore" :options="editDictOptions" :widget="widget" /> -->
  </div>
</template>

<script>
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
export default {
  name: 'SelectTreeEditMode',
  props: {
    widget: Object,
    rules: Object,
    inputFieldOptions: Array
  },
  data() {
    return {
      editModeOptions: [
        { label: '允许多选', key: 'selectMultiple', eventType: 'change', eventName: 'changeSelectMode', disabled: false },
        { label: '允许全选', key: 'selectCheckAll', eventType: 'change', eventName: 'changeCheckAll', disabled: false },
        { label: '父级可选', key: 'selectParent', disabled: false },
        { label: '显示清空按钮', key: 'allowClear', disabled: false },
        { label: '显示搜索框', key: 'showSearch', disabled: false },
        {
          label: '允许全部展开/折叠',
          key: 'allCollapse',
          disabled: false,
          labelCol: { span: 12 },
          wrapperCol: { span: 12, style: { textAlign: 'right' } }
        }
      ],
      editDictOptions: [
        { label: '新增备选项', key: 'optionAdd' },
        { label: '备选项排序', key: 'optionSort' },
        { label: '删除备选项', key: 'optionDel' }
      ],
      showMoreOptions: [
        {
          label: '显示选项更多操作',
          key: 'showMore',
          labelCol: { span: 10 },
          wrapperCol: { span: 13, style: { textAlign: 'right' } }
        }
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
  mounted() {
    // 暂无实现异步节点加载方式，先隐藏该配置
    if (false && this.cfgOptions.type !== 'selfDefine') {
      this.displayValueFieldValidate();
    }
  },
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    handelRelay({ val, eventName }) {
      this[eventName](val);
    },
    // 是否全选
    changeCheckAll(value) {
      if (value) {
        this.widget.configuration.editMode.selectMode = 'multiple';
        this.widget.configuration.editMode.selectMultiple = true;
      }
    },
    // change是否多选
    changeSelectMode(value) {
      this.widget.configuration.editMode.selectMode = 'default';
      if (value) {
        this.widget.configuration.editMode.selectMode = 'multiple';
      } else {
        this.widget.configuration.editMode.selectCheckAll = false;
      }
    },
    // 节点加载方式
    changeLoadAsync(e) {
      const isAsync = e.target.value;
      if (isAsync) {
        this.widget.configuration.editMode.selectCheckAll = false;
        this.editModeOptions[1].disabled = true;
      } else {
        this.editModeOptions[1].disabled = false;
      }
      this.displayValueFieldValidate();
    },
    displayValueFieldValidate() {
      if (this.widget.configuration.editMode.loadAsync) {
        // this.$set(this.widget.configuration, 'displayValueField', '');
        this.$set(this.rules, 'displayValueField', this.displayValueField);
        this.$refs.displayValueField.onFieldBlur();
      } else {
        // this.$delete(this.widget.configuration, 'displayValueField');
        this.$delete(this.rules, 'displayValueField');
        this.$refs.displayValueField.clearValidate();
      }
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
