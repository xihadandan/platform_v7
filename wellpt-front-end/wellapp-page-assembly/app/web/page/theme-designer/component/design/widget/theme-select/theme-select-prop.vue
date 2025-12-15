<template>
<!-- 基础样式 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-select-border-color"
              v-model="item.wSelectBorderColor"
            />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-select-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wSelectBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-select-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wSelectBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角尺寸"
              codeLabel="--w-select-border-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wSelectBorderRadius"
            />
           </a-collapse-panel>
          <a-collapse-panel key="3" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-select-text-color"
              v-model="item.wSelectTextColor"
            />
            <StyleColorTreeSelectFormItem
              label="占位文字颜色"
              codeLabel="--w-select-placeholder-color"
              v-model="item.wSelectPlaceholderColor"
            />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-select-text-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wSelectTextSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-select-text-weight"
             :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wSelectTextWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-select-arrow-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wSelectArrowSize"
            />
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-select-arrow-color"
              v-model="item.wSelectArrowColor"
            />
          </a-collapse-panel>
          <a-collapse-panel key="5" header="背景">
            <StyleColorTreeSelectFormItem
              label="背景颜色"
              codeLabel="--w-select-selection-bg"
              v-model="item.wSelectSelectionBg"
            />
          </a-collapse-panel>
          <a-collapse-panel key="6" header="边距">
            <StyleVarSelectFormItem
              label="右外边距"
              codeLabel="--w-select-rendered-mr"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wSelectRenderedMr"
            />
          </a-collapse-panel>
          <a-collapse-panel key="7" header="高度">
            <StyleVarSelectFormItem
              label="高度"
              codeLabel="--w-select-selection-height"
              :options="getStyleDeriveOptions(packJson.heightConfig)"
              v-model="item.wSelectSelectionHeight"
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
              codeLabel="--w-select-border-color-hover"
              v-model="item.wSelectBorderColorHover"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-select-arrow-size-hover"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wSelectArrowSizeHover"
            />
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-select-arrow-color-hover"
              v-model="item.wSelectArrowColorHover"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="3" tab="焦点">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-select-border-color-focus"
              v-model="item.wSelectBorderColorFocus"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-select-arrow-size-focus"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wSelectArrowSizeFocus"
            />
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-select-arrow-color-focus"
              v-model="item.wSelectArrowColorFocus"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="4" tab="禁用">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-select-border-color-disabled"
              v-model="item.wSelectBorderColorDisabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-select-text-color-disabled"
              v-model="item.wSelectTextColorDisabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-select-arrow-size-disabled"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wSelectArrowSizeDisabled"
            />
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-select-arrow-color-disabled"
              v-model="item.wSelectArrowColorDisabled"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="背景">
            <StyleColorTreeSelectFormItem
              label="背景颜色"
              codeLabel="--w-select-selection-bg-disabled"
              v-model="item.wSelectSelectionBgDisabled"
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
  name: 'ThemeSelectProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
