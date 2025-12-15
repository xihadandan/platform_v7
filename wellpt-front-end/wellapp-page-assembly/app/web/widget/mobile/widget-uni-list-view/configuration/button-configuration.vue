<template>
  <div>
    <a-form-model>
      <a-form-model-item label="名称">
        <a-input v-model="button.text" allow-clear>
          <template slot="addonAfter">
            <WI18nInput :widget="widget" :designer="designer" :code="button.uuid" :target="button" v-model="button.text" />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="编码">
        <a-input v-model="button.code" allow-clear />
      </a-form-model-item>
      <a-form-model-item label="显示">
        <a-switch v-model="button.defaultVisible" />
      </a-form-model-item>
      <a-form-model-item label="位置">
        <a-checkbox-group v-model="button.position" :options="positionOptions" />
      </a-form-model-item>
      <a-form-model-item label="图标">
        <ImageLibrary
          v-model="button.icon.className"
          width="80px"
          height="40px"
          :allowSelectType="['icon', 'commonImages', 'mongoImages']"
          :iconStyle="{
            fontSize: '24px'
          }"
        />
      </a-form-model-item>
      <template v-if="button.icon.className">
        <a-form-model-item label="不显示名称">
          <a-switch v-model="button.hiddenText" />
        </a-form-model-item>
      </template>
      <a-form-model-item label="行末样式" v-if="button.position.indexOf('3') > -1">
        <div style="border-radius: 4px; border: 1px dotted var(--w-border-color-light); padding: var(--w-padding-2xs)">
          <a-form-model-item label="背景颜色">
            <ColorPicker v-model="button.bgColor" allow-clear></ColorPicker>
          </a-form-model-item>
          <a-form-model-item label="文字颜色">
            <ColorPicker v-model="button.textColor" allow-clear></ColorPicker>
          </a-form-model-item>
        </div>
      </a-form-model-item>
      <a-form-model-item label="底部/行下样式" v-if="button.position.indexOf('2') > -1 || button.position.indexOf('5') > -1">
        <div style="border-radius: 4px; border: 1px dotted var(--w-border-color-light); padding: var(--w-padding-2xs)">
          <a-form-model-item label="类型">
            <a-select :options="typeOptions" v-model="button.type" allow-clear />
          </a-form-model-item>
          <a-form-model-item label="功能色">
            <a-select :options="statusOptions" v-model="button.status" allow-clear />
          </a-form-model-item>
          <!-- <a-form-model-item label="小按钮">
            <a-switch v-model="button.mini"></a-switch>
          </a-form-model-item> -->
          <!-- <a-form-model-item label="空心">
            <a-switch v-model="button.plain"></a-switch>
          </a-form-model-item> -->
        </div>
      </a-form-model-item>
      <a-form-model-item label="事件">
        <div style="border-radius: 4px; border: 1px dotted var(--w-border-color-light); padding: var(--w-padding-2xs)">
          <WidgetEventHandler :widget="widget" :eventModel="button.eventManger" :designer="designer" />
        </div>
      </a-form-model-item>
    </a-form-model>
  </div>
</template>
<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import ImageLibrary from '@pageAssembly/app/web/widget/commons/image-library.vue';
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';

export default {
  name: 'ButtonConfiguration',
  mixins: [],
  inject: ['pageContext'],
  props: {
    widget: Object,
    designer: Object,
    button: Object,
    columnIndexOptions: Array
  },
  data() {
    if (!this.button.uuid) {
      this.button.uuid = generateId();
    }
    return {
      positionOptions: [
        // { label: '表格头部', value: '1' },
        { label: '表格底部', value: '2' },
        { label: '行末', value: '3' },
        // { label: '悬浮行上', value: '4' },
        { label: '行下', value: '5' }
      ],
      statusOptions: [
        { label: '成功色', value: 'success' },
        { label: '错误色', value: 'error' },
        { label: '警告色', value: 'warning' }
      ],
      typeOptions: [
        { label: '主要按钮', value: 'primary' },
        { label: '默认按钮', value: 'default' },
        { label: '次要按钮', value: 'minor' },
        { label: '警告按钮', value: 'danger' },
        { label: '链接按钮', value: 'link' },
        { label: '文字按钮', value: 'text' }
      ]
    };
  },

  beforeCreate() {},
  components: { ImageLibrary, ColorPicker },
  computed: {},
  created() {},
  methods: {
    iconSelected(icon) {
      this.$set(this.button.icon, 'className', icon);
    }
  },
  beforeMount() {},
  mounted() {}
};
</script>
