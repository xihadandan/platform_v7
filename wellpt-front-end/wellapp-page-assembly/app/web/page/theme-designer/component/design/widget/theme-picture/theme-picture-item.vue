<template>
<!-- 基础样式 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-picture-item-border-color"
              v-model="item.wPictureItemBorderColor"
            />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-picture-item-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wPictureItemBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-picture-item-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wPictureItemBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角尺寸"
              codeLabel="--w-picture-item-border-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wPictureItemBorderRadius"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="边距">
            <StyleVarSelectFormItem
              label="左右内边距"
              codeLabel="--w-picture-item-lr-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wPictureItemLrPadding"
            />
          </a-collapse-panel>
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="蒙版">
            <StyleColorTreeSelectFormItem
              label="蒙版填充颜色"
              codeLabel="--w-picture-item-mask-color"
              v-model="item.wPictureItemMaskColor"
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
  name: 'ThemePictureItem',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
