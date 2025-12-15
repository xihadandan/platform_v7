<template>
  <!-- 下拉选项 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem label="文字颜色" codeLabel="--w-select-item-text-color" v-model="item.wSelectItemTextColor" />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-select-item-text-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wSelectItemTextSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-select-item-text-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wSelectItemTextWeight"
            />
          </a-collapse-panel>
          <!-- <a-collapse-panel key="2" header="背景">
            <StyleColorTreeSelectFormItem label="背景颜色" codeLabel="--w-select-item-bg" v-model="item.wSelectItemBg" />
          </a-collapse-panel> -->
          <a-collapse-panel key="3" header="边距">
            <StyleVarSelectFormItem
              label="左右内边距"
              codeLabel="--w-select-item-lr-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wSelectItemLrPadding"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="高度">
            <StyleVarSelectFormItem
              label="高度"
              codeLabel="--w-select-item-height"
              :options="getStyleDeriveOptions(packJson.heightConfig)"
              v-model="item.wSelectItemHeight"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="文字">
            <StyleColorTreeSelectFormItem label="文字颜色" codeLabel="--w-select-item-color-hover" v-model="item.wSelectItemColorHover" />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-select-item-text-weight-hover"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wSelectItemTextWeightHover"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="背景">
            <StyleColorTreeSelectFormItem label="背景颜色" codeLabel="--w-select-item-bg-hover" v-model="item.wSelectItemBgHover" />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="图标">
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-select-item-color-icon-hover"
              v-model="item.wSelectItemColorIconHover"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="3" tab="选中">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="文字">
            <StyleColorTreeSelectFormItem
              label="文字颜色"
              codeLabel="--w-select-item-color-selected"
              v-model="item.wSelectItemColorSelected"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-select-item-text-weight-selected"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wSelectItemTextWeightSelected"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="背景">
            <StyleColorTreeSelectFormItem label="背景颜色" codeLabel="--w-select-item-bg-selected" v-model="item.wSelectItemBgSelected" />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="图标">
            <StyleColorTreeSelectFormItem
              label="图标颜色"
              codeLabel="--w-select-item-color-icon-selected"
              v-model="item.wSelectItemColorIconSelected"
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
  name: 'ThemeSelectDropdownMenu',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
