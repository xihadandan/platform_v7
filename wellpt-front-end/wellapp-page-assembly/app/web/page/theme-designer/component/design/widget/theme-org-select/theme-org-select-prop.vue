<template>
<!-- 基础样式 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-org-border-color"
              v-model="item.wOrgBorderColor"
            />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-org-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wOrgBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-org-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wOrgBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角尺寸"
              codeLabel="--w-org-border-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wOrgBorderRadius"
            />
           </a-collapse-panel>
          <a-collapse-panel key="4" header="图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-org-icon-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wOrgIconSize"
            />
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-org-icon-color"
              v-model="item.wOrgIconColor"
            />
          </a-collapse-panel>
          <a-collapse-panel key="5" header="背景">
            <StyleColorTreeSelectFormItem
              label="背景颜色"
              codeLabel="--w-org-bg-color"
              v-model="item.wOrgBgColor"
            />
          </a-collapse-panel>
          <a-collapse-panel key="6" header="边距">
            <StyleVarSelectFormItem
              label="左右内边距"
              codeLabel="--w-org-lr-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wOrgLrPadding"
            />
          </a-collapse-panel>
          <a-collapse-panel key="7" header="高度">
            <StyleVarSelectFormItem
              label="高度"
              codeLabel="--w-org-select-height"
              :options="getStyleDeriveOptions(packJson.heightConfig)"
              v-model="item.wOrgSelectHeight"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-org-border-color-hover"
              v-model="item.wOrgBorderColorHover"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-org-icon-size-hover"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wOrgIconSizeHover"
            />
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-org-icon-color-hover"
              v-model="item.wOrgIconColorHover"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>

  </a-tabs>
</template>
<script type="text/babel">
import themePropMixin from '@framework/vue/mixin/themePropMixin';

export default {
  name: 'ThemeOrgSelectProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
