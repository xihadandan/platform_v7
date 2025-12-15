<template>
  <!-- 下拉框-编辑模式属性 -->
  <div>
    <FieldSwitch :options="editModeOptions" :widget="widget" @handelRelay="handelRelay" />
    <template v-if="widget.configuration.options.type === 'dataDictionary'">
      <FieldSwitch :options="showMoreOptions" :widget="widget" />
      <FieldSwitch v-if="widget.configuration.editMode.showMore" :options="editDictOptions" :widget="widget" />
    </template>
    <template v-if="widget.configuration.options.type === 'dataSource'">
      <FieldSwitch :options="showMoreOptions" :widget="widget" />

      <template v-if="widget.configuration.editMode.showMore">
        <a-form-model-item label="新增备选项">
          <a-switch v-model="widget.configuration.editMode.optionAdd" />
        </a-form-model-item>
        <a-form-model-item label="新增请求地址" v-show="widget.configuration.editMode.optionAdd">
          <a-input v-model="widget.configuration.editMode.optionAddFetchUrl" />
        </a-form-model-item>
        <a-form-model-item label="备选项排序">
          <a-switch v-model="widget.configuration.editMode.optionSort" />
        </a-form-model-item>
        <a-form-model-item label="排序请求地址" v-show="widget.configuration.editMode.optionSort">
          <a-input v-model="widget.configuration.editMode.optionSortFetchUrl" />
        </a-form-model-item>
        <a-form-model-item label="删除备选项">
          <a-switch v-model="widget.configuration.editMode.optionDel" />
        </a-form-model-item>
        <a-form-model-item label="删除请求地址" v-show="widget.configuration.editMode.optionDel">
          <a-input v-model="widget.configuration.editMode.optionDelFetchUrl" />
        </a-form-model-item>
      </template>
    </template>

    <SelectedCountConfiguration v-if="this.widget.configuration.editMode.selectMultiple" :widget="widget" />
    <a-form-model-item class="display-b" :label-col="{}" :wrapper-col="{}">
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
        :allowClear="true"
        placeholder="请选择"
        :options="inputFieldOptions"
        :style="{ width: '100%' }"
        v-model="widget.configuration.displayValueField"
        :getPopupContainer="getPopupContainerByPs()"
        :dropdownClassName="getDropdownClassName()"
      />
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
  </div>
</template>

<script>
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
export default {
  name: 'SelectEditMode',
  inject: ['designer'],
  props: {
    widget: Object,
    inputFieldOptions: Array,
    textWidgetOptions: Array
  },
  data() {
    return {
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
      editModeOptions: [
        {
          label: '允许多选',
          key: 'selectMultiple',
          eventType: 'change',
          eventName: 'changeSelectMode'
        },
        {
          label: '允许全选',
          key: 'selectCheckAll',
          eventType: 'change',
          eventName: 'changeCheckAll'
        },
        {
          label: '显示清空按钮',
          key: 'allowClear',
          labelCol: { span: 10 },
          wrapperCol: { span: 13, style: { textAlign: 'right' } }
        },
        { label: '显示搜索框', key: 'showSearch' }
      ]
    };
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
    }
  }
};
</script>
